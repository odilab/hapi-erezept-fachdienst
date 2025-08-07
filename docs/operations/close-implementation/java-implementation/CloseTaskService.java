package ca.uhn.fhir.jpa.starter.custom.operation.close;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Service für die Geschäftslogik der $close Operation.
 * Handhabt die Erstellung des Receipt Bundles und die Signierung.
 */
@Service
public class CloseTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseTaskService.class);

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;

    @Autowired
    public CloseTaskService(FhirContext fhirContext, DaoRegistry daoRegistry) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
    }

    /**
     * Parst MedicationDispense-Objekte aus den Parameters.
     * 
     * @param rxDispensation Die Input-Parameters
     * @param flowType Der FlowType des Tasks
     * @return Liste von MedicationDispense-Objekten
     */
    public List<MedicationDispense> parseMedicationDispenses(Parameters rxDispensation, String flowType) {
        List<MedicationDispense> medicationDispenses = new ArrayList<>();
        
        for (Parameters.ParametersParameterComponent param : rxDispensation.getParameter()) {
            if ("rxDispensation".equals(param.getName())) {
                MedicationDispense dispense = null;
                Medication medication = null;
                
                // Extrahiere MedicationDispense und optional Medication
                for (Parameters.ParametersParameterComponent part : param.getPart()) {
                    if ("medicationDispense".equals(part.getName()) && part.hasResource()) {
                        if (part.getResource() instanceof MedicationDispense) {
                            dispense = (MedicationDispense) part.getResource();
                        }
                    } else if ("medication".equals(part.getName()) && part.hasResource()) {
                        if (part.getResource() instanceof Medication) {
                            medication = (Medication) part.getResource();
                        }
                    }
                }
                
                if (dispense != null) {
                    // Setze Medication-Referenz wenn vorhanden
                    if (medication != null) {
                        dispense.setMedication(new Reference("Medication/" + medication.getIdElement().getIdPart()));
                    }
                    
                    // Validiere Profil basierend auf FlowType (A_26002-01, A_26003-01)
                    validateMedicationDispenseProfile(dispense, flowType);
                    
                    medicationDispenses.add(dispense);
                }
            }
        }
        
        return medicationDispenses;
    }

    /**
     * Validiert das MedicationDispense-Profil basierend auf dem FlowType.
     */
    private void validateMedicationDispenseProfile(MedicationDispense dispense, String flowType) {
        Meta meta = dispense.getMeta();
        
        if ("162".equals(flowType)) {
            // DiGA - A_26003-01
            if (!meta.hasProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense_DiGA")) {
                throw new UnprocessableEntityException(
                    "Unzulässige Abgabeinformationen: Für diesen Workflow sind nur Abgabeinformationen für digitale Gesundheitsanwendungen zulässig."
                );
            }
        } else if (Arrays.asList("160", "169", "200", "209").contains(flowType)) {
            // Arzneimittel - A_26002-01
            if (!meta.hasProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense")) {
                throw new UnprocessableEntityException(
                    "Unzulässige Abgabeinformationen: Für diesen Workflow sind nur Abgabeinformationen für Arzneimittel zulässig."
                );
            }
        }
    }

    /**
     * Validiert MedicationDispense-Objekte.
     */
    public void validateMedicationDispenses(List<MedicationDispense> medicationDispenses, 
                                           String prescriptionId, 
                                           String kvnr, 
                                           String telematikId) {
        for (MedicationDispense dispense : medicationDispenses) {
            // Prüfe Prescription-ID
            if (dispense.hasAuthorizingPrescription()) {
                String refId = dispense.getAuthorizingPrescriptionFirstRep().getReference();
                if (!refId.equals("Task/" + prescriptionId)) {
                    throw new UnprocessableEntityException("MedicationDispense references wrong prescription");
                }
            }
            
            // Prüfe KVNR
            if (dispense.hasSubject()) {
                String subjectKvnr = dispense.getSubject().getIdentifier().getValue();
                if (!kvnr.equals(subjectKvnr)) {
                    throw new UnprocessableEntityException("MedicationDispense has wrong KVNR");
                }
            }
            
            // Setze Performer (Telematik-ID der Apotheke)
            if (!dispense.hasPerformer()) {
                dispense.addPerformer()
                    .setActor(new Reference()
                        .setIdentifier(new Identifier()
                            .setSystem("https://gematik.de/fhir/sid/telematik-id")
                            .setValue(telematikId)));
            }
            
            // Setze WhenHandedOver wenn nicht vorhanden
            if (!dispense.hasWhenHandedOver()) {
                dispense.setWhenHandedOver(new Date());
            }
        }
    }

    /**
     * Erstellt das Receipt Bundle mit Signatur.
     */
    public Bundle createReceiptBundle(Task task, 
                                     Binary prescriptionBinary,
                                     String telematikId,
                                     Date inProgressDate,
                                     Date completedDate) {
        
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setId(UUID.randomUUID().toString());
        bundle.setTimestamp(completedDate);
        
        // Setze Meta-Profile
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle|1.5");
        bundle.setMeta(meta);
        
        // Erstelle Composition (Quittung)
        Composition composition = createComposition(
            task.getIdElement().getIdPart(),
            telematikId,
            inProgressDate,
            completedDate
        );
        
        // Erstelle Device (Signatur-Device)
        Device device = createDevice();
        
        // Extrahiere Prescription Digest
        String prescriptionDigest = extractPrescriptionDigest(prescriptionBinary);
        Binary digestBinary = createDigestBinary(prescriptionDigest);
        
        // Füge Composition als ersten Entry hinzu
        bundle.addEntry()
            .setFullUrl("urn:uuid:" + composition.getIdElement().getIdPart())
            .setResource(composition);
            
        // Füge Device hinzu
        bundle.addEntry()
            .setFullUrl("urn:uuid:" + device.getIdElement().getIdPart())
            .setResource(device);
            
        // Füge Digest Binary hinzu
        bundle.addEntry()
            .setFullUrl("urn:uuid:" + digestBinary.getIdElement().getIdPart())
            .setResource(digestBinary);
        
        // Signiere das Bundle
        signBundle(bundle, device.getIdElement().getIdPart());
        
        return bundle;
    }

    /**
     * Erstellt die Composition für das Receipt.
     */
    private Composition createComposition(String prescriptionId,
                                         String telematikId,
                                         Date inProgressDate,
                                         Date completedDate) {
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        
        // Setze Meta
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Composition|1.5");
        composition.setMeta(meta);
        
        // Status und Type
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setType(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_DocumentType")
                .setCode("3")
                .setDisplay("Receipt")));
        
        // Subject (Prescription-ID)
        composition.setSubject(new Reference("Task/" + prescriptionId));
        
        // Date
        composition.setDate(completedDate);
        
        // Author (Device-Referenz)
        composition.addAuthor(new Reference("Device/" + UUID.randomUUID().toString()));
        
        // Title
        composition.setTitle("Quittung");
        
        // Event (Period mit in-progress und completed)
        Composition.CompositionEventComponent event = composition.addEvent();
        Period period = new Period();
        period.setStart(inProgressDate);
        period.setEnd(completedDate);
        event.setPeriod(period);
        
        // Section mit Prescription Digest
        Composition.SectionComponent section = composition.addSection();
        section.setTitle("Prescription Digest");
        section.addEntry(new Reference("Binary/" + UUID.randomUUID().toString()));
        
        // Extension für Telematik-ID
        composition.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_Beneficiary")
            .setValue(new Identifier()
                .setSystem("https://gematik.de/fhir/sid/telematik-id")
                .setValue(telematikId));
        
        return composition;
    }

    /**
     * Erstellt das Device für die Signatur.
     */
    private Device createDevice() {
        Device device = new Device();
        device.setId(UUID.randomUUID().toString());
        
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Device|1.5");
        device.setMeta(meta);
        
        device.setStatus(Device.FHIRDeviceStatus.ACTIVE);
        
        // Device Name
        Device.DeviceDeviceNameComponent deviceName = device.addDeviceName();
        deviceName.setName("E-Rezept Fachdienst");
        deviceName.setType(Device.DeviceNameType.USERFRIENDLYNAME);
        
        // Serial Number (könnte aus Konfiguration kommen)
        device.setSerialNumber("FD-" + System.currentTimeMillis());
        
        // Version
        Device.DeviceVersionComponent version = device.addVersion();
        version.setValue("1.0.0");
        
        return device;
    }

    /**
     * Extrahiert den Prescription Digest aus der Binary.
     */
    private String extractPrescriptionDigest(Binary prescriptionBinary) {
        try {
            // TODO: Implementierung abhängig vom CAdES-BES Format
            // Dies würde normalerweise den Message Digest aus der Signatur extrahieren
            byte[] content = prescriptionBinary.getContent();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new UnprocessableEntityException("Failed to extract prescription digest: " + e.getMessage());
        }
    }

    /**
     * Erstellt die Binary für den Prescription Digest.
     */
    private Binary createDigestBinary(String digest) {
        Binary binary = new Binary();
        binary.setId(UUID.randomUUID().toString());
        
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Digest|1.5");
        binary.setMeta(meta);
        
        binary.setContentType("application/octet-stream");
        binary.setContent(Base64.getDecoder().decode(digest));
        
        return binary;
    }

    /**
     * Signiert das Bundle mit CAdES-BES.
     * 
     * HINWEIS: Dies ist eine vereinfachte Implementierung.
     * In Produktion muss dies mit echten Zertifikaten und HSM erfolgen.
     */
    private void signBundle(Bundle bundle, String deviceId) {
        try {
            // Serialisiere Bundle zu XML
            String bundleXml = fhirContext.newXmlParser().encodeResourceToString(bundle);
            
            // TODO: Echte CAdES-BES Signatur implementieren
            // Dies würde normalerweise:
            // 1. Bundle canonicalisieren
            // 2. Mit privatem Schlüssel signieren
            // 3. CAdES-BES Format erstellen
            // 4. Base64 encodieren
            
            // Placeholder Signatur
            String signatureData = Base64.getEncoder().encodeToString(bundleXml.getBytes());
            
            // Füge Signatur zum Bundle hinzu
            Extension signatureExtension = bundle.addExtension();
            signatureExtension.setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_Signature");
            
            Signature signature = new Signature();
            signature.setType(Collections.singletonList(new Coding()
                .setSystem("urn:iso-astm:E1762-95:2013")
                .setCode("1.2.840.113549.1.7.2")
                .setDisplay("CAdES-BES")));
            signature.setWhen(new Date());
            signature.setWho(new Reference("Device/" + deviceId));
            signature.setData(signatureData.getBytes());
            
            signatureExtension.setValue(signature);
            
        } catch (Exception e) {
            throw new UnprocessableEntityException("Failed to sign receipt bundle: " + e.getMessage());
        }
    }

    /**
     * Speichert MedicationDispense-Objekte.
     */
    public void saveMedicationDispenses(List<MedicationDispense> medicationDispenses, String taskId) {
        IFhirResourceDao<MedicationDispense> medicationDispenseDao = daoRegistry.getResourceDao(MedicationDispense.class);
        
        for (MedicationDispense dispense : medicationDispenses) {
            // Setze Task-Referenz
            dispense.addAuthorizingPrescription(new Reference("Task/" + taskId));
            
            // Speichere oder update
            if (dispense.hasId()) {
                medicationDispenseDao.update(dispense);
            } else {
                medicationDispenseDao.create(dispense);
            }
        }
    }
}