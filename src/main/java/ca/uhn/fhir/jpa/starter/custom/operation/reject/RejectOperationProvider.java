package ca.uhn.fhir.jpa.starter.custom.operation.reject;

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
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Provider für die $reject Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation ermöglicht es Apotheken, ein akzeptiertes E-Rezept zurückzugeben.
 * Der Task-Status wechselt von "in-progress" zurück zu "ready".
 * 
 * Implementiert gemäß:
 * - A_19170-02: Rollenprüfung (nur Apotheken)
 * - A_19171-03: Secret-Validierung
 * - A_19172-01: Secret löschen und Status auf "ready" setzen
 * - A_24175: Owner löschen
 * - A_24286-02: MedicationDispense löschen
 * - A_19514: HTTP 204 No Content
 */
@Component
public class RejectOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RejectOperationProvider.class);
    
    // Erlaubte Rollen für die Reject-Operation gemäß A_19170-02
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        "oid_oeffentliche_apotheke",
        "oid_krankenhausapotheke"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final RejectTaskService rejectTaskService;
    private final AuditService auditService;

    @Autowired
    public RejectOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            RejectTaskService rejectTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.rejectTaskService = rejectTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $reject Operation für E-Rezepte.
     * 
     * POST /Task/{id}/$reject?secret={secret}
     * 
     * @param theId Die Task-ID (Prescription ID)
     * @param secret Das Secret als Operation-Parameter (optional, kann auch als Query-Parameter übergeben werden)
     * @param theRequestDetails Request-Details mit Authorization-Header und Query-Parametern
     * @return Der aktualisierte Task (obwohl die Spezifikation HTTP 204 verlangt, erwartet HAPI einen Rückgabewert)
     */
    @Operation(name = "$reject", idempotent = false, type = Task.class)
    public Task rejectTaskOperation(
            @IdParam IdType theId,
            @OperationParam(name = "secret", min = 0) StringType secret,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$reject Operation aufgerufen für Task ID: {}", theId.getIdPart());

        try {
            // 1. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            String telematikId = accessToken.getIdNumber();
            if (telematikId == null || telematikId.isEmpty()) {
                throw new ForbiddenOperationException("Missing Telematik-ID in ACCESS_TOKEN");
            }
            
            // 2. Prüfe Berechtigung (nur Apotheken) - A_19170-02
            validateProfessionOid(accessToken);
            
            // 3. Lade den Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task task;
            try {
                task = taskDao.read(theId, theRequestDetails);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Task not found for prescription id");
            }

            // 4. Prüfe ob Task gelöscht/cancelled ist
            if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
                throw new ResourceGoneException("Task has already been deleted");
            }

            // 5. Prüfe Task-Status - muss "in-progress" sein (A_19171-03)
            if (!task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
                throw new ForbiddenOperationException("Task not in status in progress, is: " + task.getStatus().toCode());
            }

            // 6. Extrahiere Secret - entweder aus Operation-Parameter oder Query-Parameter
            String secretValue = null;
            if (secret != null && secret.hasValue()) {
                secretValue = secret.getValue();
            } else {
                // Fallback: Versuche aus Query-Parameter zu lesen
                String[] secretParams = theRequestDetails.getParameters().get("secret");
                secretValue = (secretParams != null && secretParams.length > 0) ? secretParams[0] : null;
            }

            // 7. Validiere Secret (A_19171-03 + A_20703)
            validateSecret(secretValue, task);

            // 8. Führe Reject-Operation aus
            performReject(task, taskDao, theRequestDetails);

            // 9. Audit-Logging
            logRejectOperation(task, accessToken);

            LOGGER.info("Task erfolgreich zurückgewiesen mit ID: {}", task.getIdElement().getIdPart());
            
            // 10. Gebe den aktualisierten Task zurück
            // Hinweis: Die Spezifikation (A_19514) verlangt HTTP 204 No Content,
            // aber HAPI benötigt einen Rückgabewert. Der Client kann den Body ignorieren.
            return task;

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $reject Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Prüft ob der Nutzer die erforderliche Rolle hat (A_19170-02).
     */
    private void validateProfessionOid(AccessToken accessToken) {
        String professionOid = mapProfessionToOid(accessToken.getProfession());
        
        if (!ALLOWED_PROFESSION_OIDS.contains(professionOid)) {
            LOGGER.error("Unerlaubte professionOID für $reject: {}", professionOid);
            throw new ForbiddenOperationException("Die Operation $reject ist nur für Apotheken erlaubt");
        }
        
        LOGGER.debug("ProfessionOID {} ist für $reject Operation autorisiert", professionOid);
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
            case KRANKENHAUS_APOTHEKE:
                return "oid_krankenhausapotheke";
            default:
                return profession.name().toLowerCase();
        }
    }

    /**
     * Validiert das Secret (A_19171-03 + A_20703).
     */
    private void validateSecret(String providedSecret, Task task) {
        // Secret aus Parameter extrahieren
        if (providedSecret == null || providedSecret.isEmpty()) {
            // A_20703: Bei fehlendem Secret VAU-Error-Code: brute_force setzen
            // In Java/HAPI müssen wir dies über eine spezielle Exception handhaben
            throw new ForbiddenOperationException("No or invalid secret");
        }
        
        // Secret aus Task extrahieren
        String taskSecret = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElse(null);
        
        if (taskSecret == null || !taskSecret.equals(providedSecret)) {
            // A_20703: Bei falschem Secret VAU-Error-Code: brute_force setzen
            throw new ForbiddenOperationException("No or invalid secret");
        }
    }

    /**
     * Führt die Reject-Operation aus.
     * - A_19172-01: Secret löschen und Status auf "ready" setzen
     * - A_24175: Owner löschen
     * - A_24286-02: MedicationDispense löschen falls vorhanden
     */
    private void performReject(Task task, IFhirResourceDao<Task> taskDao, RequestDetails requestDetails) {
        // A_19172-01: Secret löschen
        task.getIdentifier().removeIf(id -> 
            "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()));
        
        // A_24175: Owner löschen
        task.setOwner(null);
        
        // A_19172-01: Status auf "ready" setzen
        task.setStatus(Task.TaskStatus.READY);
        
        // LastUpdate aktualisieren
        task.getMeta().setLastUpdated(new Date());
        
        // Task in Datenbank aktualisieren
        taskDao.update(task, requestDetails);
        
        // A_24286-02: MedicationDispense löschen falls vorhanden
        rejectTaskService.deleteMedicationDispenseIfExists(task, requestDetails);
    }

    /**
     * Extrahiert die KVNR aus dem Task für Audit-Logging.
     */
    private String extractKvnr(Task task) {
        if (task.hasFor() && task.getFor().hasIdentifier()) {
            return task.getFor().getIdentifier().getValue();
        }
        return null;
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Reject-Operation.
     */
    private void logRejectOperation(Task task, AccessToken accessToken) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String kvnr = extractKvnr(task);
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' durch %s zurückgewiesen",
                task.getIdElement().getIdPart(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.U, // Update
                "reject", // Subtype für $reject Operation
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
                auditService.addEntityDetail(auditEvent, "task-status", "ready");
                if (kvnr != null) {
                    auditService.addEntityDetail(auditEvent, "kvnr", kvnr);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Reject-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen wenn Audit-Logging fehlschlägt
        }
    }
}