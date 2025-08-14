package ca.uhn.fhir.jpa.starter.custom.operation.activate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.TslManager;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.asn1.cms.Attribute;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service für die Aktivierung von E-Rezept Tasks.
 * Validiert das signierte Bundle und aktualisiert den Task-Status.
 */
@Service
public class ActivateTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateTaskService.class);
    
    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final TslManager tslManager;
    
    @Value("${erp.anr.validation.mode:warning}")
    private String anrValidationMode;

    @Autowired
    public ActivateTaskService(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            TslManager tslManager) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.tslManager = tslManager;
    }

    /**
     * Aktiviert einen Task durch Hinzufügen des signierten Bundles.
     * 
     * @param task Der zu aktivierende Task
     * @param signedBundleBase64 Das signierte Bundle als Base64-String
     * @param accessToken Das Access Token des Aufrufers
     * @return Der aktivierte Task
     */
    public Task activateTask(Task task, String signedBundleBase64, AccessToken accessToken) {
        LOGGER.info("Aktiviere Task mit ID: {}", task.getIdElement().getIdPart());

        try {
            // 1. Dekodiere Base64
            byte[] signedData = Base64.getDecoder().decode(signedBundleBase64);
            
            // 2. Parse PKCS#7 und validiere Signatur
            CMSSignedData cmsSignedData = new CMSSignedData(signedData);
            
            // Extrahiere Signaturdatum
            Date signingTime = extractSigningTime(cmsSignedData);
            if (signingTime == null) {
                throw new UnprocessableEntityException("No signingTime in PKCS#7 file");
            }
            
            // TODO: Vollständige Signaturvalidierung mit TSL
            // Für Testzwecke wird hier nur die Struktur geprüft
            validateSignatureStructure(cmsSignedData);
            
            // 3. Extrahiere das signierte Bundle
            byte[] bundleContent = (byte[]) cmsSignedData.getSignedContent().getContent();
            String bundleXml = new String(bundleContent, StandardCharsets.UTF_8);
            
            // 4. Parse Bundle
            IParser parser = fhirContext.newXmlParser();
            Bundle kbvBundle = parser.parseResource(Bundle.class, bundleXml);
            
            // 5. Führe alle Validierungen durch
            performAllValidations(task, kbvBundle, signingTime);
            
            // 6. Extrahiere Patient-ID und setze sie im Task
            PatientIdentifierInfo patientInfo = extractPatientIdentifierFromBundle(kbvBundle);
            // Setze Patient-Referenz mit korrektem System und Identifier
            Reference patientRef = new Reference();
            patientRef.setType("Patient");
            patientRef.setIdentifier(new Identifier()
                .setSystem(patientInfo.system)
                .setValue(patientInfo.value));
            task.setFor(patientRef);
            
            // 7. Setze Status auf ready
            task.setStatus(Task.TaskStatus.READY);
            
            // 8. Setze Timestamps
            Date now = new Date();
            task.setLastModified(now);
            
            // 9. Berechne und setze ExpiryDate und AcceptDate
            setExpiryAndAcceptDates(task, signingTime);
            
            // 10. Speichere Healthcare Prescription UUID und Binary
            String healthCarePrescriptionUuid = UUID.randomUUID().toString();
            task.addExtension()
                .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid")
                .setValue(new StringType(healthCarePrescriptionUuid));
            
            // Erstelle Binary für das signierte Bundle
            Binary healthCarePrescriptionBinary = new Binary();
            healthCarePrescriptionBinary.setId(healthCarePrescriptionUuid);
            healthCarePrescriptionBinary.setContentType("application/pkcs7-mime");
            healthCarePrescriptionBinary.setData(signedBundleBase64.getBytes());
            
            // Speichere Binary mit update() statt create(), um die selbst gesetzte ID zu verwenden
            IFhirResourceDao<Binary> binaryDao = daoRegistry.getResourceDao(Binary.class);
            binaryDao.update(healthCarePrescriptionBinary, (ca.uhn.fhir.rest.api.server.RequestDetails) null);
            
            // 11. Speichere den aktualisierten Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task updatedTask = (Task) taskDao.update(task).getResource();
            
            LOGGER.info("Task erfolgreich aktiviert - Status: ready, Patient-ID: {}", patientInfo.value);
            
            return updatedTask;
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Aktivieren des Tasks: {}", e.getMessage(), e);
            if (e instanceof UnprocessableEntityException) {
                throw (UnprocessableEntityException) e;
            }
            throw new UnprocessableEntityException("Fehler beim Verarbeiten des signierten Bundles: " + e.getMessage());
        }
    }

    /**
     * Führt alle Validierungen für die Aktivierung durch.
     */
    private void performAllValidations(Task task, Bundle kbvBundle, Date signingTime) {
        // 1. PrescriptionID Validierung
        validatePrescriptionId(task, kbvBundle);
        
        // 2. AuthoredOn == Signaturdatum
        validateAuthoredOnEqualsSigningDate(kbvBundle, signingTime);
        
        // 3. Coverage Type Validierung
        validateCoverageType(task, kbvBundle);
        
        // 4. BTM/Thalidomid Prüfung
        validateNoNarcotics(kbvBundle);
        
        // 5. Patient-ID Validierung (KVNR oder PKV-ID)
        PatientIdentifierInfo patientInfo = extractPatientIdentifierFromBundle(kbvBundle);
        if (!isValidPatientIdentifier(patientInfo)) {
            throw new UnprocessableEntityException(
                "Ungültige Versichertennummer: Die übergebene Versichertennummer entspricht nicht den " +
                "Prüfziffer-Validierungsregeln."
            );
        }
        
        // 6. Optional: ANR Validierung
        validatePractitioner(kbvBundle);
    }

    /**
     * Validiert die PrescriptionID Übereinstimmung.
     */
    private void validatePrescriptionId(Task task, Bundle bundle) {
        String taskPrescriptionId = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new UnprocessableEntityException("Task has no PrescriptionID"));
        
        String bundlePrescriptionId = bundle.getIdentifier().getValue();
        
        if (!taskPrescriptionId.equals(bundlePrescriptionId)) {
            throw new UnprocessableEntityException(
                "PrescriptionID mismatch: Task has " + taskPrescriptionId + ", Bundle has " + bundlePrescriptionId
            );
        }
        
        // Prüfe Präfix
        String flowType = extractFlowType(task);
        if (!bundlePrescriptionId.startsWith(flowType + ".")) {
            throw new UnprocessableEntityException("PrescriptionID prefix does not match flowType");
        }
    }

    /**
     * Validiert dass AuthoredOn mit dem Signaturdatum übereinstimmt.
     */
    private void validateAuthoredOnEqualsSigningDate(Bundle bundle, Date signingTime) {
        // Finde MedicationRequest
        MedicationRequest medicationRequest = findMedicationRequest(bundle);
        Date authoredOn = medicationRequest.getAuthoredOn();
        
        if (authoredOn == null) {
            throw new UnprocessableEntityException("MedicationRequest has no authoredOn date");
        }
        
        // Vergleiche nur das Datum (nicht die Zeit)
        LocalDate authoredOnDate = authoredOn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate signingDate = signingTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (!authoredOnDate.equals(signingDate)) {
            throw new UnprocessableEntityException(
                "AuthoredOn (" + authoredOnDate + ") does not match signing date (" + signingDate + ")"
            );
        }
    }

    /**
     * Validiert den Coverage Type.
     */
    private void validateCoverageType(Task task, Bundle bundle) {
        String flowType = extractFlowType(task);
        
        // Finde Coverage
        List<Resource> coverages = bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof Coverage)
            .map(Bundle.BundleEntryComponent::getResource)
            .collect(Collectors.toList());
        
        if (coverages.isEmpty()) {
            return; // Coverage ist optional
        }
        
        Coverage coverage = (Coverage) coverages.get(0);
        String coverageType = coverage.getType().getCodingFirstRep().getCode();
        
        // PKV (SEL) nur bei 200er Workflows
        if ("SEL".equals(coverageType) && !flowType.equals("200")) {
            throw new UnprocessableEntityException("PKV coverage not allowed for workflow " + flowType);
        }
        
        // GKV nicht bei FlowType 200 (PKV-spezifisch)
        if ("GKV".equals(coverageType) && flowType.equals("200")) {
            throw new UnprocessableEntityException("GKV coverage not allowed for PKV workflow " + flowType);
        }
    }

    /**
     * Prüft auf BTM/Thalidomid.
     */
    private void validateNoNarcotics(Bundle bundle) {
        List<Medication> medications = bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof Medication)
            .map(e -> (Medication) e.getResource())
            .collect(Collectors.toList());
        
        for (Medication medication : medications) {
            // Prüfe auf BTM Extension
            boolean hasNarcoticsExtension = medication.getExtension().stream()
                .anyMatch(ext -> ext.getUrl().contains("BTM") || ext.getUrl().contains("Thalidomid"));
            
            if (hasNarcoticsExtension) {
                throw new UnprocessableEntityException("BTM und Thalidomid nicht zulässig");
            }
        }
    }

    /**
     * Validiert den Practitioner (ANR).
     */
    private void validatePractitioner(Bundle bundle) {
        List<Practitioner> practitioners = bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof Practitioner)
            .map(e -> (Practitioner) e.getResource())
            .collect(Collectors.toList());
        
        for (Practitioner practitioner : practitioners) {
            // Finde ANR
            Identifier anrIdentifier = practitioner.getIdentifier().stream()
                .filter(id -> id.getSystem() != null && id.getSystem().contains("ANR"))
                .findFirst()
                .orElse(null);
            
            if (anrIdentifier != null && !isValidAnr(anrIdentifier.getValue())) {
                String errorMessage = "Ungültige Arztnummer (LANR oder ZANR): Die übergebene Arztnummer entspricht " +
                                    "nicht den Prüfziffer-Validierungsregeln.";
                
                if ("error".equals(anrValidationMode)) {
                    throw new UnprocessableEntityException(errorMessage);
                }
                // Bei "warning" Mode wird die Warnung im Provider gesetzt
            }
        }
    }

    /**
     * Interne Klasse zur Rückgabe von Patient-Identifier Informationen.
     */
    private static class PatientIdentifierInfo {
        String value;
        String system;
        boolean isPkv;
        
        PatientIdentifierInfo(String value, String system, boolean isPkv) {
            this.value = value;
            this.system = system;
            this.isPkv = isPkv;
        }
    }
    
    /**
     * Extrahiert die Patient-ID (KVNR oder PKV-ID) aus dem Bundle.
     */
    private PatientIdentifierInfo extractPatientIdentifierFromBundle(Bundle bundle) {
        // Finde Patient
        Patient patient = bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof Patient)
            .map(e -> (Patient) e.getResource())
            .findFirst()
            .orElseThrow(() -> new UnprocessableEntityException("Bundle contains no Patient resource"));
        
        // Suche nach KVNR (GKV Patient)
        Identifier kvnrIdentifier = patient.getIdentifier().stream()
            .filter(id -> "http://fhir.de/sid/gkv/kvid-10".equals(id.getSystem()))
            .findFirst()
            .orElse(null);
        
        if (kvnrIdentifier != null) {
            return new PatientIdentifierInfo(kvnrIdentifier.getValue(), kvnrIdentifier.getSystem(), false);
        }
        
        // Suche nach PKV-ID (PKV Patient)
        Identifier pkvIdentifier = patient.getIdentifier().stream()
            .filter(id -> "http://fhir.de/sid/pkv/kvid-10".equals(id.getSystem()))
            .findFirst()
            .orElse(null);
        
        if (pkvIdentifier != null) {
            return new PatientIdentifierInfo(pkvIdentifier.getValue(), pkvIdentifier.getSystem(), true);
        }
        
        throw new UnprocessableEntityException("Patient has neither KVNR nor PKV-ID");
    }
    
    /**
     * Extrahiert die KVNR aus dem Bundle.
     * @deprecated Nutze extractPatientIdentifierFromBundle stattdessen
     */
    @Deprecated
    private String extractKvnrFromBundle(Bundle bundle) {
        // Finde Patient
        Patient patient = bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof Patient)
            .map(e -> (Patient) e.getResource())
            .findFirst()
            .orElseThrow(() -> new UnprocessableEntityException("Bundle contains no Patient resource"));
        
        // Finde KVNR (GKV) oder PKV-ID
        // Zuerst versuche KVNR zu finden (für GKV Patienten)
        String kvnr = patient.getIdentifier().stream()
            .filter(id -> "http://fhir.de/sid/gkv/kvid-10".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElse(null);
        
        // Falls keine KVNR gefunden, suche nach PKV-ID (für PKV Patienten)
        if (kvnr == null) {
            kvnr = patient.getIdentifier().stream()
                .filter(id -> "http://fhir.de/sid/pkv/kvid-10".equals(id.getSystem()))
                .findFirst()
                .map(Identifier::getValue)
                .orElseThrow(() -> new UnprocessableEntityException("Patient has neither KVNR nor PKV-ID"));
        }
        
        return kvnr;
    }

    /**
     * Extrahiert den FlowType aus dem Task.
     */
    private String extractFlowType(Task task) {
        return task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType".equals(ext.getUrl()))
            .findFirst()
            .map(ext -> ((Coding) ext.getValue()).getCode())
            .orElseThrow(() -> new UnprocessableEntityException("Task has no FlowType"));
    }

    /**
     * Findet die MedicationRequest im Bundle.
     */
    private MedicationRequest findMedicationRequest(Bundle bundle) {
        return bundle.getEntry().stream()
            .filter(e -> e.hasResource() && e.getResource() instanceof MedicationRequest)
            .map(e -> (MedicationRequest) e.getResource())
            .findFirst()
            .orElseThrow(() -> new UnprocessableEntityException("Bundle contains no MedicationRequest"));
    }

    /**
     * Setzt ExpiryDate und AcceptDate basierend auf dem Signaturdatum.
     */
    private void setExpiryAndAcceptDates(Task task, Date signingTime) {
        LocalDate signingDate = signingTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // ExpiryDate = Signaturdatum + 3 Monate
        LocalDate expiryDate = signingDate.plusMonths(3);
        task.getRestriction().setPeriod(new Period());
        task.getRestriction().getPeriod().setEnd(Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        // ExpiryDate auch als Extension setzen (wird von gematik-Profil erwartet)
        // DateType erwartet nur das Datum ohne Zeit im Format YYYY-MM-DD
        task.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_ExpiryDate")
            .setValue(new DateType(expiryDate.toString()));  // LocalDate.toString() gibt YYYY-MM-DD zurück
        
        // AcceptDate abhängig vom FlowType
        String flowType = extractFlowType(task);
        LocalDate acceptDate;
        
        if ("160".equals(flowType) || "169".equals(flowType)) {
            // 28 Tage für Muster 16
            acceptDate = signingDate.plusDays(28);
        } else {
            // 3 Monate für PKV und andere
            acceptDate = signingDate.plusMonths(3);
        }
        
        // AcceptDate als Extension setzen (muss DateType sein, nicht DateTimeType)
        // DateType erwartet nur das Datum ohne Zeit im Format YYYY-MM-DD
        task.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_AcceptDate")
            .setValue(new DateType(acceptDate.toString()));  // LocalDate.toString() gibt YYYY-MM-DD zurück
    }

    /**
     * Extrahiert das Signaturdatum aus der CMS Signatur.
     */
    private Date extractSigningTime(CMSSignedData cmsSignedData) {
        try {
            SignerInformationStore signerInfos = cmsSignedData.getSignerInfos();
            Collection<SignerInformation> signers = signerInfos.getSigners();
            
            for (SignerInformation signer : signers) {
                if (signer.getSignedAttributes() != null) {
                    Attribute signingTimeAttr = 
                        signer.getSignedAttributes().get(org.bouncycastle.asn1.cms.CMSAttributes.signingTime);
                    
                    if (signingTimeAttr != null) {
                        org.bouncycastle.asn1.cms.Time signingTime = 
                            org.bouncycastle.asn1.cms.Time.getInstance(signingTimeAttr.getAttrValues().getObjectAt(0));
                        return signingTime.getDate();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fehler beim Extrahieren der Signaturzeit: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Validiert die Struktur der Signatur.
     */
    private void validateSignatureStructure(CMSSignedData cmsSignedData) {
        try {
            SignerInformationStore signerInfos = cmsSignedData.getSignerInfos();
            if (signerInfos.getSigners().isEmpty()) {
                throw new UnprocessableEntityException("No signers found in PKCS#7 signature");
            }
            
            // TODO: Vollständige Signaturvalidierung mit TSL
            // - Zertifikatskette prüfen
            // - Gegen TSL validieren
            // - Profession OID prüfen
            
        } catch (Exception e) {
            throw new UnprocessableEntityException("Invalid PKCS#7 structure: " + e.getMessage());
        }
    }

    /**
     * Prüft ob eine Patient-ID gültig ist (KVNR oder PKV-ID).
     */
    private boolean isValidPatientIdentifier(PatientIdentifierInfo patientInfo) {
        if (patientInfo == null || patientInfo.value == null) {
            return false;
        }
        
        if (patientInfo.isPkv) {
            // PKV-ID Validierung
            // PKV-IDs beginnen meist mit 'P' gefolgt von Ziffern
            return patientInfo.value.matches("P[0-9]{9}");
        } else {
            // KVNR Validierung
            return isValidKvnr(patientInfo.value);
        }
    }
    
    /**
     * Prüft ob eine KVNR gültig ist (Prüfziffer).
     */
    private boolean isValidKvnr(String kvnr) {
        if (kvnr == null || kvnr.length() != 10) {
            return false;
        }
        
        // Vereinfachte Prüfung - in Produktion sollte die korrekte Prüfziffer-Berechnung implementiert werden
        return kvnr.matches("[A-Z][0-9]{9}");
    }

    /**
     * Prüft ob eine ANR gültig ist (Prüfziffer).
     */
    private boolean isValidAnr(String anr) {
        if (anr == null || anr.isEmpty()) {
            return true; // ANR ist optional
        }
        
        // Vereinfachte Prüfung - in Produktion sollte die korrekte Prüfziffer-Berechnung implementiert werden
        return anr.matches("[0-9]{9}");
    }
}