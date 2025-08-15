package ca.uhn.fhir.jpa.starter.custom.operation.abort;

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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Provider für die $abort Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation löscht einen Task und alle damit verbundenen personenbezogenen Daten.
 * 
 * Die Operation kann durchgeführt werden von:
 * - Versicherten (eigenes Rezept oder als Vertreter mit AccessCode)
 * - Ärzten (nur Status ready mit AccessCode)
 * - Apotheken (nur Status in-progress mit Secret)
 * 
 * Implementiert gemäß:
 * - A_19027-06: Löschung personenbezogener Daten
 * - A_19121: Task Status auf cancelled setzen
 * - A_19145/A_19146: Status-Prüfungen
 * - A_22102_01: Flowtype 169/209 Einschränkungen
 */
@Component
public class AbortOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortOperationProvider.class);
    
    // Erlaubte Rollen für die Abort-Operation gemäß Spezifikation
    // Alle Rollen sind erlaubt, aber mit unterschiedlichen Bedingungen
    private static final List<String> ALLOWED_PROFESSION_OIDS_DOCTORS = Arrays.asList(
        "oid_arzt",
        "oid_zahnarzt", 
        "oid_praxis_arzt",
        "oid_zahnarztpraxis",
        "oid_praxis_psychotherapeut",
        "oid_krankenhaus"
    );
    
    private static final List<String> ALLOWED_PROFESSION_OIDS_PHARMACY = Arrays.asList(
        "oid_oeffentliche_apotheke",
        "oid_krankenhausapotheke"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final AbortTaskService abortTaskService;
    private final AuditService auditService;

    @Autowired
    public AbortOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            AbortTaskService abortTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.abortTaskService = abortTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $abort Operation für E-Rezepte.
     * 
     * POST /Task/{id}/$abort?ac={accessCode} (für Versicherte/Ärzte)
     * POST /Task/{id}/$abort?secret={secret} (für Apotheken)
     * 
     * @param theId Die Task-ID (Prescription ID)
     * @param accessCode Der AccessCode als Query-Parameter (optional)
     * @param secret Das Secret als Query-Parameter (optional)
     * @param theRequestDetails Request-Details mit Authorization-Header
     * @return Kein Response-Body, nur Status 204 No Content
     */
    @Operation(name = "$abort", idempotent = false, type = Task.class)
    public void abortTaskOperation(
            @IdParam IdType theId,
            @OperationParam(name = "ac", min = 0) StringType accessCode,
            @OperationParam(name = "secret", min = 0) StringType secret,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$abort Operation aufgerufen für Task ID: {}", theId.getIdPart());

        try {
            // 1. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            String professionOid = mapProfessionToOid(accessToken.getProfession());
            
            // 2. Lade den Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task task;
            try {
                task = taskDao.read(theId);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Task not found for prescription id");
            }

            // 3. Prüfe ob Task bereits gelöscht ist (cancelled)
            if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
                throw new ResourceGoneException("Task has already been deleted");
            }

            // 4. Prüfe ob Task im Status draft ist (A_19027-06)
            if (task.getStatus().equals(Task.TaskStatus.DRAFT)) {
                throw new ForbiddenOperationException("Abort not expected for newly created Task");
            }

            // 5. Flowtype-spezifische Prüfung für Versicherte (A_22102_01)
            if ("oid_versicherter".equals(professionOid)) {
                checkFlowtypeRestrictions(task);
            }

            // 6. Rollenbasierte Validierung
            String auditEventId = performRoleBasedValidation(
                professionOid, 
                task, 
                accessCode != null ? accessCode.getValue() : null,
                secret != null ? secret.getValue() : null,
                theRequestDetails,
                accessToken
            );

            // 7. Setze Task Status auf cancelled (A_19121)
            task.setStatus(Task.TaskStatus.CANCELLED);
            task.getMeta().setLastUpdated(new Date());

            // 8. Lösche personenbezogene Daten (A_19027-06)
            abortTaskService.clearPersonalDataFromTask(task);
            
            // 9. Lösche Task-bezogene Communications
            abortTaskService.deleteCommunicationsForTask(theId.getIdPart());

            // 10. Speichere aktualisierten Task
            taskDao.update(task, theRequestDetails);

            // 11. Audit-Logging
            logAbortOperation(task, accessToken, auditEventId);

            LOGGER.info("Task erfolgreich gelöscht mit ID: {}", task.getIdElement().getIdPart());
            
            // 12. Return 204 No Content (A_19514)
            // In HAPI FHIR wird dies automatisch gemacht wenn die Methode void zurückgibt

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $abort Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Führt rollenbasierte Validierung durch.
     * @return AuditEventId für das Logging
     */
    private String performRoleBasedValidation(
            String professionOid,
            Task task,
            String providedAccessCode,
            String providedSecret,
            RequestDetails requestDetails,
            AccessToken accessToken) {
        
        Task.TaskStatus status = task.getStatus();
        
        // Apotheke
        if (ALLOWED_PROFESSION_OIDS_PHARMACY.contains(professionOid)) {
            // A_19145: Task muss im Status in-progress sein
            if (!status.equals(Task.TaskStatus.INPROGRESS)) {
                throw new ForbiddenOperationException("Task must be in progress for user pharmacy, is: " + 
                    status.toCode());
            }
            
            // A_19224: Secret validieren
            validateSecret(providedSecret, task, requestDetails);
            return "POST_Task_abort_pharmacy";
        }
        
        // Andere Nutzer (Versicherte, Ärzte)
        else {
            // A_19146: Task darf NICHT im Status in-progress sein
            if (status.equals(Task.TaskStatus.INPROGRESS)) {
                throw new ForbiddenOperationException("Task must not be in progress for users other than pharmacy, but is: " + 
                    status.toCode());
            }
            
            // Versicherte
            if ("oid_versicherter".equals(professionOid)) {
                String kvnrFromToken = accessToken.getIdNumber();
                String kvnrFromTask = extractKvnr(task);
                
                if (kvnrFromTask == null) {
                    throw new UnprocessableEntityException("Task has no KV number");
                }
                
                // A_20546_03: Eigenes Rezept (KVNR stimmt überein)
                if (kvnrFromToken.equals(kvnrFromTask)) {
                    return "POST_Task_abort_insurant";
                } 
                // A_20547: Vertreter (KVNR stimmt nicht überein)
                else {
                    validateAccessCode(providedAccessCode, task, requestDetails);
                    return "POST_Task_abort_representative";
                }
            }
            
            // Ärzte
            else if (ALLOWED_PROFESSION_OIDS_DOCTORS.contains(professionOid)) {
                // A_19120_3: Task muss im Status ready sein
                if (!status.equals(Task.TaskStatus.READY)) {
                    throw new ForbiddenOperationException("Task must be ready for doctor, but is: " + 
                        status.toCode());
                }
                
                // A_19120_3: AccessCode validieren
                validateAccessCode(providedAccessCode, task, requestDetails);
                return "POST_Task_abort_doctor";
            }
            
            // Unbekannte Rolle
            else {
                throw new ForbiddenOperationException("Operation $abort not allowed for profession OID: " + professionOid);
            }
        }
    }

    /**
     * Prüft Flowtype-spezifische Einschränkungen (A_22102_01).
     */
    private void checkFlowtypeRestrictions(Task task) {
        String flowType = extractFlowType(task);
        
        // Flowtype 169 (direkteZuweisung) und 209 (direkteZuweisungPkv)
        if ("169".equals(flowType) || "209".equals(flowType)) {
            if (!task.getStatus().equals(Task.TaskStatus.COMPLETED)) {
                throw new ForbiddenOperationException(
                    "Abort for patient in workflow types 169 / 209 only allowed for completed Task");
            }
        }
    }

    /**
     * Validiert den AccessCode (A_19120_3, A_20547, A_20703).
     */
    private void validateAccessCode(String providedAccessCode, RequestDetails requestDetails, Task task) {
        // AccessCode aus Header oder Query-Parameter
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
            .orElseThrow(() -> new ForbiddenOperationException("Task has no AccessCode"));

        if (!taskAccessCode.equals(providedAccessCode)) {
            // A_20703: Bei Mismatch sollte VAU Error Code "brute_force" gesetzt werden
            // In Java können wir das nicht direkt, aber die Exception reicht
            throw new ForbiddenOperationException("AccessCode mismatch");
        }
    }

    /**
     * Validiert das Secret für Apotheken (A_19224, A_20703).
     */
    private void validateSecret(String providedSecret, Task task, RequestDetails requestDetails) {
        // Secret aus Query-Parameter (nicht aus Header!)
        if (providedSecret == null || providedSecret.isEmpty()) {
            throw new ForbiddenOperationException("No secret provided for user pharmacy");
        }

        // Extrahiere Secret aus Task
        String taskSecret = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new ForbiddenOperationException("Task has no secret"));

        if (!taskSecret.equals(providedSecret)) {
            // A_20703: Bei Mismatch sollte VAU Error Code "brute_force" gesetzt werden
            throw new ForbiddenOperationException("Invalid secret provided for user pharmacy");
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
            case ARZT_KRANKENHAUS:
                return "oid_krankenhaus";
            case LEISTUNGSERBRINGER:
                return "oid_praxis_arzt";
            case VERSICHERTER:
                return "oid_versicherter";
            default:
                return profession.name().toLowerCase();
        }
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Abort-Operation.
     */
    private void logAbortOperation(Task task, AccessToken accessToken, String auditEventId) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String kvnr = extractKvnr(task);
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' durch %s gelöscht",
                task.getIdElement().getIdPart(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.D, // Delete
                "abort", // Subtype für $abort Operation
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
                auditService.addEntityDetail(auditEvent, "task-status", "cancelled");
                auditService.addEntityDetail(auditEvent, "audit-event-id", auditEventId);
                if (kvnr != null) {
                    auditService.addEntityDetail(auditEvent, "kvnr", kvnr);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Abort-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen
        }
    }
}