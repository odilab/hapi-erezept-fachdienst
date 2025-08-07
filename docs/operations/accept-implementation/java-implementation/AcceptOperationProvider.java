package ca.uhn.fhir.jpa.starter.custom.operation.accept;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessTokenService;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.Profession;
import ca.uhn.fhir.jpa.starter.custom.operation.AuthorizationService;
import ca.uhn.fhir.jpa.starter.custom.operation.AuditService;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Provider für die $accept Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation ermöglicht es Apotheken, ein E-Rezept zur Bearbeitung zu übernehmen.
 * Der Task-Status wechselt von "ready" zu "in-progress" und es wird ein Secret generiert.
 */
@Component
public class AcceptOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptOperationProvider.class);
    
    // Erlaubte Rollen für die Accept-Operation gemäß Spezifikation
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        "oid_oeffentliche_apotheke",
        "oid_krankenhausapotheke"
    );
    
    // Erlaubte Rollen für DiGA (Flowtype 162)
    private static final List<String> ALLOWED_PROFESSION_OIDS_DIGA = Arrays.asList(
        "oid_kostentraeger"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final AcceptTaskService acceptTaskService;
    private final AuditService auditService;

    @Autowired
    public AcceptOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            AcceptTaskService acceptTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.acceptTaskService = acceptTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $accept Operation für E-Rezepte.
     * 
     * POST /Task/{id}/$accept?ac={accessCode}
     * 
     * @param theId Die Task-ID (Prescription ID)
     * @param accessCode Der AccessCode als Query-Parameter
     * @param theRequestDetails Request-Details mit Authorization-Header
     * @return Bundle mit Task und Binary (signiertes E-Rezept)
     */
    @Operation(name = "$accept", idempotent = false, type = Task.class)
    public Bundle acceptTaskOperation(
            @IdParam IdType theId,
            @OperationParam(name = "ac", min = 1) StringType accessCode,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$accept Operation aufgerufen für Task ID: {}", theId.getIdPart());

        try {
            // 1. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            String telematikId = accessToken.getIdNumber();
            if (telematikId == null || telematikId.isEmpty()) {
                throw new UnprocessableEntityException("Missing Telematik-ID in ACCESS_TOKEN");
            }
            
            // 2. Lade den Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task task;
            try {
                task = taskDao.read(theId);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Task not found for prescription id");
            }

            // 3. Prüfe ob Task gelöscht/cancelled ist (A_19149-02)
            if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
                throw new ResourceGoneException("Task meanwhile deleted for prescription id");
            }

            // 4. Validiere AccessCode (A_19167-04)
            validateAccessCode(accessCode.getValue(), theRequestDetails, task);

            // 5. Prüfe Task-Status (A_19168-01)
            validateTaskStatus(task, telematikId);

            // 6. Prüfe Berechtigung basierend auf FlowType
            String flowType = extractFlowType(task);
            validateProfessionOid(accessToken, flowType);

            // 7. Prüfe Einlösefrist (A_23539-01)
            validateExpiryDate(task);

            // 8. Lade signiertes E-Rezept (Binary)
            Binary prescriptionBinary = loadPrescriptionBinary(task);
            
            // 9. Prüfe Mehrfachverordnung wenn vorhanden (A_22635-02)
            if (prescriptionBinary != null) {
                acceptTaskService.validateMultiplePrescription(prescriptionBinary);
            }

            // 10. Generiere Secret und aktualisiere Task (A_19169-01)
            String secret = generateSecret();
            task.setStatus(Task.TaskStatus.INPROGRESS);
            task.addIdentifier()
                .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret")
                .setValue(secret);
            
            // 11. Setze Owner (A_24174)
            task.setOwner(new Reference().setDisplay(telematikId));
            
            // 12. Aktualisiere LastModified
            task.getMeta().setLastUpdated(new Date());
            
            // 13. Speichere aktualisierten Task
            taskDao.update(task);

            // 14. Erstelle Response Bundle
            Bundle responseBundle = createResponseBundle(task, prescriptionBinary, flowType);

            // 15. Prüfe und füge Consent für PKV hinzu (A_22110)
            if ("200".equals(flowType) || "209".equals(flowType)) {
                addConsentIfExists(responseBundle, task);
            }

            // 16. Audit-Logging
            logAcceptOperation(task, accessToken);

            LOGGER.info("Task erfolgreich akzeptiert mit ID: {}", task.getIdElement().getIdPart());
            return responseBundle;

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $accept Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validiert den AccessCode aus Query-Parameter oder Header.
     */
    private void validateAccessCode(String providedAccessCode, RequestDetails requestDetails, Task task) {
        // Prüfe auch Header falls Query-Parameter leer
        if (providedAccessCode == null || providedAccessCode.isEmpty()) {
            providedAccessCode = requestDetails.getHeader("X-AccessCode");
        }
        
        if (providedAccessCode == null || providedAccessCode.isEmpty()) {
            throw new ForbiddenOperationException("AccessCode missing");
        }

        // Extrahiere AccessCode aus Task
        String taskAccessCode = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new ResourceGoneException("Task has no AccessCode"));

        if (!taskAccessCode.equals(providedAccessCode)) {
            throw new ForbiddenOperationException("Access code does not match");
        }
    }

    /**
     * Validiert den Task-Status gemäß A_19168-01.
     */
    private void validateTaskStatus(Task task, String telematikId) {
        Task.TaskStatus status = task.getStatus();
        
        if (status.equals(Task.TaskStatus.INPROGRESS)) {
            if (task.hasOwner() && task.getOwner().getDisplay() != null && 
                task.getOwner().getDisplay().equals(telematikId)) {
                throw new UnprocessableEntityException("Task has invalid status in-progress. Task is processed by requesting institution");
            } else {
                throw new UnprocessableEntityException("Task has invalid status in-progress");
            }
        } else if (status.equals(Task.TaskStatus.DRAFT)) {
            throw new UnprocessableEntityException("Task has invalid status draft");
        } else if (status.equals(Task.TaskStatus.COMPLETED)) {
            throw new UnprocessableEntityException("Task has invalid status completed");
        } else if (!status.equals(Task.TaskStatus.READY)) {
            throw new UnprocessableEntityException("Task must be in status ready");
        }
    }

    /**
     * Prüft ob der Nutzer die erforderliche Rolle hat.
     */
    private void validateProfessionOid(AccessToken accessToken, String flowType) {
        String professionOid = mapProfessionToOid(accessToken.getProfession());
        
        // DiGA (Flowtype 162) hat andere erlaubte Rollen
        if ("162".equals(flowType)) {
            if (!ALLOWED_PROFESSION_OIDS_DIGA.contains(professionOid)) {
                LOGGER.error("Unerlaubte professionOID für $accept (DiGA): {}", professionOid);
                throw new ForbiddenOperationException("Die Operation $accept ist für DiGA nur für Kostenträger erlaubt");
            }
        } else {
            if (!ALLOWED_PROFESSION_OIDS.contains(professionOid)) {
                LOGGER.error("Unerlaubte professionOID für $accept: {}", professionOid);
                throw new ForbiddenOperationException("Die Operation $accept ist nur für Apotheken erlaubt");
            }
        }
        
        LOGGER.debug("ProfessionOID {} ist für $accept Operation autorisiert", professionOid);
    }

    /**
     * Mappt Profession enum zu OID String.
     */
    private String mapProfessionToOid(Profession profession) {
        if (profession == null) {
            return "";
        }
        
        switch (profession) {
            case OEFFENTLICHE_APOTHEKE:
                return "oid_oeffentliche_apotheke";
            case KRANKENHAUSAPOTHEKE:
                return "oid_krankenhausapotheke";
            case KOSTENTRAEGER:
                return "oid_kostentraeger";
            default:
                return profession.name().toLowerCase();
        }
    }

    /**
     * Prüft die Einlösefrist gemäß A_23539-01.
     */
    private void validateExpiryDate(Task task) {
        Date expiryDate = extractExpiryDate(task);
        if (expiryDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiryDate);
            cal.add(Calendar.DAY_OF_MONTH, 1); // +24h
            
            if (cal.getTime().before(new Date())) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                throw new ForbiddenOperationException("Verordnung bis " + sdf.format(expiryDate) + " einlösbar.");
            }
        }
    }

    /**
     * Extrahiert das Ablaufdatum aus dem Task.
     */
    private Date extractExpiryDate(Task task) {
        // ExpiryDate ist in Extension gespeichert
        return task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_ExpiryDate".equals(ext.getUrl()))
            .map(ext -> ((DateTimeType) ext.getValue()).getValue())
            .findFirst()
            .orElse(null);
    }

    /**
     * Extrahiert den FlowType aus dem Task.
     */
    private String extractFlowType(Task task) {
        return task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType".equals(ext.getUrl()))
            .map(ext -> ((Coding) ext.getValue()).getCode())
            .findFirst()
            .orElse("160");
    }

    /**
     * Lädt das signierte E-Rezept (Binary) für den Task.
     */
    private Binary loadPrescriptionBinary(Task task) {
        // TODO: Implementierung hängt von der Datenbankstruktur ab
        // Normalerweise ist das Binary über eine Referenz im Task verlinkt
        // oder über die Prescription ID abrufbar
        
        // Placeholder-Implementierung:
        Binary binary = new Binary();
        binary.setContentType("application/pkcs7-mime");
        // binary.setData(...); // Würde aus DB geladen
        return binary;
    }

    /**
     * Generiert ein 256-Bit Secret (64 Hex-Zeichen).
     */
    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] secretBytes = new byte[32]; // 256 Bit = 32 Bytes
        random.nextBytes(secretBytes);
        return bytesToHex(secretBytes);
    }

    /**
     * Konvertiert Bytes zu Hex-String.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Erstellt das Response Bundle.
     */
    private Bundle createResponseBundle(Task task, Binary prescriptionBinary, String flowType) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        
        // Setze Meta-Profile
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_AcceptOperation_Output|1.5");
        bundle.setMeta(meta);
        
        // Self-Link
        String prescriptionId = task.getIdElement().getIdPart();
        bundle.addLink()
            .setRelation("self")
            .setUrl("/Task/" + prescriptionId + "/$accept/");
        
        // Task hinzufügen
        bundle.addEntry()
            .setFullUrl("/Task/" + prescriptionId)
            .setResource(task);
        
        // Binary (signiertes E-Rezept) hinzufügen
        if (prescriptionBinary != null) {
            bundle.addEntry()
                .setFullUrl("urn:uuid:" + UUID.randomUUID())
                .setResource(prescriptionBinary);
        }
        
        return bundle;
    }

    /**
     * Fügt Consent zum Bundle hinzu wenn vorhanden (PKV).
     */
    private void addConsentIfExists(Bundle bundle, Task task) {
        // TODO: Consent für KVNR suchen und hinzufügen
        // Dies erfordert Zugriff auf die Consent-Repository
        
        String kvnr = extractKvnr(task);
        if (kvnr != null) {
            // IFhirResourceDao<Consent> consentDao = daoRegistry.getResourceDao(Consent.class);
            // Suche Consent mit patient.identifier = kvnr und category = "CHARGCONS"
            // Wenn gefunden, zum Bundle hinzufügen
        }
    }

    /**
     * Extrahiert die KVNR aus dem Task.
     */
    private String extractKvnr(Task task) {
        if (task.hasFor() && task.getFor().hasIdentifier()) {
            return task.getFor().getIdentifier().getValue();
        }
        return null;
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Accept-Operation.
     */
    private void logAcceptOperation(Task task, AccessToken accessToken) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String kvnr = extractKvnr(task);
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' durch %s akzeptiert",
                task.getIdElement().getIdPart(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.U, // Update
                "accept", // Subtype für $accept Operation
                AuditEvent.AuditEventOutcome._0, // Erfolg
                new Reference(task.getIdElement().toVersionless()),
                "Task",
                task.getIdElement().toVersionless().getValue(),
                description,
                actorName,
                accessToken.getIdNumber(),
                kvnr != null ? new Reference("Patient/" + kvnr) : null
            );

            // Füge Details hinzu
            if (auditEvent != null && auditEvent.hasId()) {
                auditService.addEntityDetail(auditEvent, "prescription-id", 
                    task.getIdentifierFirstRep().getValue());
                auditService.addEntityDetail(auditEvent, "task-status", "in-progress");
                if (kvnr != null) {
                    auditService.addEntityDetail(auditEvent, "kvnr", kvnr);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Accept-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen
        }
    }
}