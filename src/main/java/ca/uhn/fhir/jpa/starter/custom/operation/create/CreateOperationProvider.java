package ca.uhn.fhir.jpa.starter.custom.operation.create;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessTokenService;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.Profession;
import ca.uhn.fhir.jpa.starter.custom.operation.AuthorizationService;
import ca.uhn.fhir.jpa.starter.custom.operation.AuditService;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Provider für die $create Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation erstellt einen neuen Task mit Status "draft" und generiert
 * dabei eine Rezept-ID sowie einen AccessCode.
 */
@Component
public class CreateOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOperationProvider.class);
    
    // Erlaubte Rollen für die Create-Operation gemäß Spezifikation
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        "oid_arzt",
        "oid_zahnarzt", 
        "oid_praxis_arzt",
        "oid_zahnarztpraxis",
        "oid_praxis_psychotherapeut",
        "oid_krankenhaus"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final CreateTaskService createTaskService;
    private final AuditService auditService;

    @Autowired
    public CreateOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            CreateTaskService createTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.createTaskService = createTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $create Operation für E-Rezepte.
     * 
     * POST /Task/$create
     * 
     * @param workflowType Der Workflow-Typ (z.B. "160" für Muster 16, "200" für PKV)
     * @param theRequestDetails Request-Details mit Authorization-Header
     * @return Parameters-Ressource mit dem erstellten Task gemäß GEM_ERP_PR_PAR_CreateOperation_Output
     */
    @Operation(name = "$create", idempotent = false, type = Task.class)
    public Parameters createTaskOperation(
            @OperationParam(name = "workflowType", min = 1) Coding workflowType,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$create Operation aufgerufen");

        try {
            // 1. Validiere Eingabeparameter
            validateInputParameters(workflowType);

            // 2. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            
            // 3. Prüfe Berechtigung (nur verordnende Leistungserbringer)
            validateProfessionOid(accessToken);

            // 4. Erstelle neuen Task mit Status "draft"
            Task createdTask = createTaskService.createTask(workflowType, accessToken);

            // 5. Erstelle Output-Parameters gemäß GEM_ERP_PR_PAR_CreateOperation_Output
            Parameters outputParameters = createOutputParameters(createdTask);

            // 6. Audit-Logging
            logCreateOperation(createdTask, accessToken);

            LOGGER.info("Task erfolgreich erstellt mit ID: {}", createdTask.getIdElement().getIdPart());
            return outputParameters;

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $create Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validiert die Eingabeparameter der $create Operation.
     */
    private void validateInputParameters(Coding workflowType) {
        if (workflowType == null) {
            throw new UnprocessableEntityException("Parameter 'workflowType' ist erforderlich");
        }
        
        if (!workflowType.hasSystem() || !workflowType.hasCode()) {
            throw new UnprocessableEntityException("Parameter 'workflowType' muss System und Code enthalten");
        }

        // Prüfe ob das System korrekt ist
        if (!"https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType".equals(workflowType.getSystem())) {
            throw new UnprocessableEntityException("workflowType System muss 'https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType' sein");
        }

        // Prüfe ob der Code gültig ist (160, 169, 200, 209, 210)
        List<String> validFlowTypes = Arrays.asList("160", "169", "200", "209", "210");
        if (!validFlowTypes.contains(workflowType.getCode())) {
            throw new UnprocessableEntityException("Ungültiger workflowType Code: " + workflowType.getCode());
        }
    }

    /**
     * Prüft ob der Nutzer die erforderliche Rolle hat.
     */
    private void validateProfessionOid(AccessToken accessToken) {
        // ProfessionOID ist in Profession enum kodiert
        String professionOid = mapProfessionToOid(accessToken.getProfession());

        if (!ALLOWED_PROFESSION_OIDS.contains(professionOid)) {
            LOGGER.error("Unerlaubte professionOID für $create: {}", professionOid);
            throw new ForbiddenOperationException("Die Operation $create ist nur für verordnende Leistungserbringer erlaubt");
        }

        LOGGER.debug("ProfessionOID {} ist für $create Operation autorisiert", professionOid);
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
                return "oid_krankenhaus";
            case LEISTUNGSERBRINGER:
                // Hier müsste genauer differenziert werden zwischen verschiedenen Leistungserbringern
                return "oid_praxis_arzt"; // Default für Leistungserbringer
            default:
                return profession.name().toLowerCase();
        }
    }

    /**
     * Erstellt die Output-Parameters gemäß GEM_ERP_PR_PAR_CreateOperation_Output.
     */
    private Parameters createOutputParameters(Task task) {
        Parameters parameters = new Parameters();
        
        // Setze Meta-Profile
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_CreateOperation_Output|1.5");
        parameters.setMeta(meta);
        
        // Füge den Task als "return" Parameter hinzu
        Parameters.ParametersParameterComponent returnParam = parameters.addParameter();
        returnParam.setName("return");
        returnParam.setResource(task);
        
        return parameters;
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Create-Operation.
     */
    private void logCreateOperation(Task task, AccessToken accessToken) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' und FlowType '%s' durch %s erstellt",
                task.getIdElement().getIdPart(),
                task.getExtensionByUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType")
                    .getValue().toString(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.C, // Create
                "create", // Subtype für $create Operation
                AuditEvent.AuditEventOutcome._0, // Erfolg
                new Reference(task.getIdElement().toVersionless()),
                "Task",
                task.getIdElement().toVersionless().getValue(),
                description,
                actorName,
                accessToken.getIdNumber(),
                null // Kein Patient-Bezug bei Create
            );

            // Füge Details zum erstellten Task hinzu
            if (auditEvent != null && auditEvent.hasId()) {
                auditService.addEntityDetail(auditEvent, "prescription-id", 
                    task.getIdentifierFirstRep().getValue());
                auditService.addEntityDetail(auditEvent, "task-status", task.getStatus().toCode());
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Create-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen
        }
    }
}