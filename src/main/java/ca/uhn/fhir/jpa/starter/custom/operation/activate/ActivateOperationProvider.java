package ca.uhn.fhir.jpa.starter.custom.operation.activate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessTokenService;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.Profession;
import ca.uhn.fhir.jpa.starter.custom.operation.AuthorizationService;
import ca.uhn.fhir.jpa.starter.custom.operation.AuditService;
import ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateTaskService;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Provider für die $activate Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation aktiviert einen Task im Status "draft" durch Hinzufügen
 * eines signierten E-Rezept-Bundles.
 */
@Component
public class ActivateOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateOperationProvider.class);
    
    // Erlaubte Rollen für die Activate-Operation gemäß Spezifikation
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        "oid_praxis_arzt",
        "oid_zahnarztpraxis",
        "oid_praxis_psychotherapeut",
        "oid_krankenhaus"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final ActivateTaskService activateTaskService;
    private final AuditService auditService;

    @Autowired
    public ActivateOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            ActivateTaskService activateTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.activateTaskService = activateTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $activate Operation für E-Rezepte.
     * 
     * POST /Task/{id}/$activate
     * 
     * @param theId Die Task-ID (Prescription ID)
     * @param ePrescription Das signierte E-Rezept als Binary
     * @param theRequestDetails Request-Details mit Authorization-Header
     * @return Parameters-Ressource mit dem aktivierten Task
     */
    @Operation(name = "$activate", idempotent = false, type = Task.class)
    public Parameters activateTaskOperation(
            @IdParam IdType theId,
            @OperationParam(name = "ePrescription", min = 1) Binary ePrescription,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$activate Operation aufgerufen für Task ID: {}", theId.getIdPart());

        try {
            // 1. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            
            // 2. Prüfe Berechtigung (nur verordnende Leistungserbringer)
            validateProfessionOid(accessToken);

            // 3. Lade den Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task task;
            try {
                task = taskDao.read(theId);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Requested Task not found in DB");
            }

            // 4. Prüfe Task-Status
            if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
                // Note: In HAPI FHIR 8.0.0, GoneException is not available
                // Using ForbiddenOperationException as a workaround for 410 status
                throw new ForbiddenOperationException("Task has already been deleted");
            }

            // 5. Prüfe AccessCode
            validateAccessCode(theRequestDetails, task);

            // 6. Prüfe dass Task im Status draft ist
            if (!task.getStatus().equals(Task.TaskStatus.DRAFT)) {
                throw new ForbiddenOperationException("Task not in status draft but in status " + task.getStatus().toCode());
            }

            // 7. Validiere Binary Parameter
            if (ePrescription == null || !ePrescription.hasData()) {
                throw new UnprocessableEntityException("ePrescription parameter is missing or has no data");
            }

            if (!"application/pkcs7-mime".equals(ePrescription.getContentType())) {
                throw new UnprocessableEntityException("ePrescription must have contentType 'application/pkcs7-mime'");
            }

            // 8. Aktiviere den Task mit dem signierten Bundle
            Task activatedTask = activateTaskService.activateTask(
                task, 
                ePrescription.getDataElement().getValueAsString(),
                accessToken
            );

            // 9. Erstelle Output-Parameters
            Parameters outputParameters = createOutputParameters(activatedTask);

            // 10. Audit-Logging
            logActivateOperation(activatedTask, accessToken);

            LOGGER.info("Task erfolgreich aktiviert mit ID: {}", activatedTask.getIdElement().getIdPart());
            return outputParameters;

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $activate Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Prüft ob der Nutzer die erforderliche Rolle hat.
     */
    private void validateProfessionOid(AccessToken accessToken) {
        String professionOid = mapProfessionToOid(accessToken.getProfession());

        if (!ALLOWED_PROFESSION_OIDS.contains(professionOid)) {
            LOGGER.error("Unerlaubte professionOID für $activate: {}", professionOid);
            throw new ForbiddenOperationException("Die Operation $activate ist nur für verordnende Leistungserbringer erlaubt");
        }

        LOGGER.debug("ProfessionOID {} ist für $activate Operation autorisiert", professionOid);
    }

    /**
     * Mappt Profession enum zu OID String.
     */
    private String mapProfessionToOid(Profession profession) {
        if (profession == null) {
            return "";
        }
        
        switch (profession) {
            case ARZT_KRANKENHAUS:
            case KRANKENHAUS:
                return "oid_krankenhaus";
            case PRAXIS_ARZT:
            case LEISTUNGSERBRINGER:
                return "oid_praxis_arzt";
            case ZAHNARZT_PRAXIS:
                return "oid_zahnarztpraxis";
            case PRAXIS_PSYCHOTHERAPEUT:
                return "oid_praxis_psychotherapeut";
            default:
                return profession.name().toLowerCase();
        }
    }

    /**
     * Validiert den AccessCode aus dem Request-Header.
     */
    private void validateAccessCode(RequestDetails requestDetails, Task task) {
        String providedAccessCode = requestDetails.getHeader("X-AccessCode");
        if (providedAccessCode == null || providedAccessCode.isEmpty()) {
            throw new ForbiddenOperationException("X-AccessCode header is missing");
        }

        // Extrahiere AccessCode aus Task
        String taskAccessCode = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new UnprocessableEntityException("Task has no AccessCode"));

        if (!taskAccessCode.equals(providedAccessCode)) {
            throw new ForbiddenOperationException("Access code does not match");
        }
    }

    /**
     * Erstellt die Output-Parameters gemäß GEM_ERP_PR_PAR_ActivateOperation_Output.
     */
    private Parameters createOutputParameters(Task task) {
        Parameters parameters = new Parameters();
        
        // Setze Meta-Profile
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_ActivateOperation_Output|1.5");
        parameters.setMeta(meta);
        
        // Füge den Task als "return" Parameter hinzu
        Parameters.ParametersParameterComponent returnParam = parameters.addParameter();
        returnParam.setName("return");
        returnParam.setResource(task);
        
        return parameters;
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Activate-Operation.
     */
    private void logActivateOperation(Task task, AccessToken accessToken) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String patientId = task.getFor() != null && task.getFor().hasIdentifier() 
                ? task.getFor().getIdentifier().getValue()
                : "Unknown";
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' durch %s aktiviert",
                task.getIdElement().getIdPart(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.U, // Update
                "activate", // Subtype für $activate Operation
                AuditEvent.AuditEventOutcome._0, // Erfolg
                new Reference(task.getIdElement().toVersionless()),
                "Task",
                task.getIdElement().toVersionless().getValue(),
                description,
                actorName,
                accessToken.getIdNumber(),
                new Reference("Patient/" + patientId) // Patient-Bezug
            );

            // Füge Details zum aktivierten Task hinzu
            if (auditEvent != null && auditEvent.hasId()) {
                auditService.addEntityDetail(auditEvent, "prescription-id", 
                    task.getIdentifierFirstRep().getValue());
                auditService.addEntityDetail(auditEvent, "task-status", task.getStatus().toCode());
                auditService.addEntityDetail(auditEvent, "patient-id", patientId);
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Activate-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen
        }
    }
}