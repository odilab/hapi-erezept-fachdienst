package ca.uhn.fhir.jpa.starter.custom.operation.close;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessTokenService;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.Profession;
import ca.uhn.fhir.jpa.starter.custom.operation.AuthorizationService;
import ca.uhn.fhir.jpa.starter.custom.operation.AuditService;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Provider für die $close Operation gemäß E-Rezept-Spezifikation.
 * Diese Operation beendet den E-Rezept-Workflow und erstellt eine digitale Quittung.
 * Der Task-Status wechselt von "in-progress" zu "completed".
 */
@Component
public class CloseOperationProvider implements IResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseOperationProvider.class);
    
    // Erlaubte Rollen für die Close-Operation gemäß A_19230-01
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        "oid_oeffentliche_apotheke",
        "oid_krankenhausapotheke"
    );

    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final CloseTaskService closeTaskService;
    private final AuditService auditService;

    @Autowired
    public CloseOperationProvider(
            FhirContext fhirContext,
            DaoRegistry daoRegistry,
            AccessTokenService accessTokenService,
            AuthorizationService authorizationService,
            CloseTaskService closeTaskService,
            AuditService auditService) {
        this.fhirContext = fhirContext;
        this.daoRegistry = daoRegistry;
        this.accessTokenService = accessTokenService;
        this.authorizationService = authorizationService;
        this.closeTaskService = closeTaskService;
        this.auditService = auditService;
    }

    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }

    /**
     * Implementierung der $close Operation für E-Rezepte.
     * 
     * POST /Task/{id}/$close?secret={secret}
     * 
     * @param theId Die Task-ID (Prescription ID)
     * @param secret Das Secret als Query-Parameter (A_19231-02)
     * @param rxDispensation Optional: MedicationDispense Informationen (A_24287-01)
     * @param theRequestDetails Request-Details mit Authorization-Header
     * @return Bundle mit signiertem Receipt gemäß GEM_ERP_PR_PAR_CloseOperation_Output
     */
    @Operation(name = "$close", idempotent = false, type = Task.class)
    public Bundle closeTaskOperation(
            @IdParam IdType theId,
            @OperationParam(name = "secret", min = 0) StringType secret,
            @OperationParam(name = "rxDispensation", min = 0) Parameters rxDispensation,
            RequestDetails theRequestDetails) {
        
        LOGGER.info("$close Operation aufgerufen für Task ID: {}", theId.getIdPart());

        try {
            // 1. Extrahiere und validiere Access Token
            AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
            String telematikId = accessToken.getIdNumber();
            if (telematikId == null || telematikId.isEmpty()) {
                throw new UnprocessableEntityException("Telematik-ID not contained in JWT");
            }
            
            // 2. Lade den Task
            IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
            Task task;
            try {
                task = taskDao.read(theId, theRequestDetails);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Task not found for prescription id");
            }

            // 3. Prüfe ob Task gelöscht/cancelled ist (aus C++ ErpExpect)
            if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
                throw new ResourceGoneException("Task has already been deleted");
            }

            // 4. Prüfe Task-Status (A_19231-02)
            if (!task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
                throw new ForbiddenOperationException("Task has to be in progress, but is: " + task.getStatus().toCode());
            }

            // 5. Validiere Secret (A_19231-02)
            // Secret kann als Query-Parameter oder Operation-Parameter kommen
            if (secret == null || !secret.hasValue()) {
                // Versuche aus Query-Parameter zu lesen
                String[] secretParams = theRequestDetails.getParameters().get("secret");
                String secretParam = (secretParams != null && secretParams.length > 0) ? secretParams[0] : null;
                secret = secretParam != null ? new StringType(secretParam) : null;
            }
            validateSecret(secret, task);

            // 6. Prüfe Berechtigung (A_19230-01)
            validateProfessionOid(accessToken);

            // 7. Extrahiere KVNR aus Task
            String kvnr = extractKvnr(task);
            if (kvnr == null) {
                throw new InternalErrorException("Task has no KV number");
            }

            // 8. Extrahiere FlowType für MedicationDispense-Validierung
            String flowType = extractFlowType(task);

            // 9. Verarbeite MedicationDispense (A_24287-01, A_26002-01, A_26003-01)
            List<MedicationDispense> medicationDispenses = null;
            
            // Debug: Log Parameter Status
            LOGGER.info("rxDispensation parameter: null={}, empty={}", 
                rxDispensation == null, rxDispensation != null ? rxDispensation.isEmpty() : "n/a");
            
            if (rxDispensation != null) {
                // Debug: Log was HAPI übergibt
                LOGGER.info("rxDispensation received - isEmpty: {}, parameterCount: {}", 
                    rxDispensation.isEmpty(), rxDispensation.getParameter().size());
                for (Parameters.ParametersParameterComponent param : rxDispensation.getParameter()) {
                    LOGGER.info("  Parameter: name={}, hasPart={}, hasResource={}", 
                        param.getName(), param.hasPart(), param.hasResource());
                    if (param.hasPart()) {
                        for (Parameters.ParametersParameterComponent part : param.getPart()) {
                            LOGGER.info("    Part: name={}, hasResource={}, resourceType={}", 
                                part.getName(), part.hasResource(), 
                                part.hasResource() ? part.getResource().getClass().getSimpleName() : "null");
                        }
                    }
                }
                
                // CloseTaskService erwartet die rxDispensation-Parameter direkt
                medicationDispenses = closeTaskService.parseMedicationDispenses(rxDispensation, flowType);
                if (!medicationDispenses.isEmpty()) {
                    // Validiere MedicationDispenses
                    closeTaskService.validateMedicationDispenses(medicationDispenses, theId.getIdPart(), kvnr, telematikId);
                    // Update lastMedicationDispense timestamp
                    updateLastMedicationDispense(task);
                }
            }
            
            // Prüfe ob MedicationDispense vorhanden ist
            if (medicationDispenses == null || medicationDispenses.isEmpty()) {
                // Prüfe ob bereits eine MedicationDispense existiert (A_24287-01)
                ensureMedicationDispenseExists(theId.getIdPart());
            }

            // 10. Lade signiertes E-Rezept (Binary)
            Binary prescriptionBinary = loadPrescriptionBinary(task);
            if (prescriptionBinary == null || prescriptionBinary.getContent() == null) {
                throw new InternalErrorException("No matching prescription found");
            }

            // 11. Setze Task-Status auf completed ZUERST (A_19232)
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setLastModified(new Date());
            if (!task.hasMeta()) {
                task.setMeta(new Meta());
            }
            task.getMeta().setLastUpdated(new Date());
            
            // 12. Erstelle Receipt Bundle (A_19233-05)
            Date inProgressDate = getInProgressDate(task);
            Date completedDate = new Date();
            
            // Prüfe Zeitstempel-Konsistenz
            if (inProgressDate.after(completedDate)) {
                throw new InternalErrorException("in-progress date later than completed time");
            }

            Bundle receiptBundle = closeTaskService.createReceiptBundle(
                task, 
                prescriptionBinary, 
                telematikId, 
                inProgressDate, 
                completedDate
            );
            
            // 13. Lösche verknüpfte Communications (A_20513)
            deleteCommunicationsForTask(theId.getIdPart());

            // 14. Speichere Task und Receipt
            if (medicationDispenses != null && !medicationDispenses.isEmpty()) {
                // Speichere mit neuen MedicationDispenses
                closeTaskService.saveMedicationDispenses(medicationDispenses, task.getIdElement().getIdPart());
            }
            
            // Update Task mit Receipt-Referenz
            task.addOutput()
                .setType(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_DocumentType")
                        .setCode("3")))
                .setValue(new Reference("Bundle/" + receiptBundle.getIdElement().getIdPart()));
            
            // Explizit den Task updaten - verwende theRequestDetails wie in AcceptOperation
            LOGGER.info("Updating Task {} to status COMPLETED", task.getIdElement().getIdPart());
            ca.uhn.fhir.jpa.api.model.DaoMethodOutcome outcome = taskDao.update(task, theRequestDetails);
            LOGGER.info("Task update outcome: resource ID = {}, created = {}", 
                outcome.getId(), outcome.getCreated());

            // 15. Audit-Logging
            logCloseOperation(task, accessToken, kvnr);

            LOGGER.info("Task erfolgreich geschlossen mit ID: {}", task.getIdElement().getIdPart());
            return receiptBundle;

        } catch (Exception e) {
            LOGGER.error("Fehler bei der $close Operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validiert das Secret aus Query-Parameter gegen Task.
     */
    private void validateSecret(StringType secret, Task task) {
        if (secret == null || !secret.hasValue()) {
            throw new ForbiddenOperationException("No secret provided");
        }
        
        String providedSecret = secret.getValue();
        
        // Extrahiere Secret aus Task
        String taskSecret = task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new ForbiddenOperationException("Task has no secret"));

        if (!taskSecret.equals(providedSecret)) {
            throw new ForbiddenOperationException("Invalid secret provided for Task");
        }
    }

    /**
     * Prüft ob der Nutzer die erforderliche Rolle hat.
     */
    private void validateProfessionOid(AccessToken accessToken) {
        String professionOid = mapProfessionToOid(accessToken.getProfession());
        
        if (!ALLOWED_PROFESSION_OIDS.contains(professionOid)) {
            LOGGER.error("Unerlaubte professionOID für $close: {}", professionOid);
            throw new ForbiddenOperationException("Die Operation $close ist nur für Apotheken erlaubt");
        }
        
        LOGGER.debug("ProfessionOID {} ist für $close Operation autorisiert", professionOid);
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
     * Prüft ob bereits eine MedicationDispense für den Task existiert.
     */
    private void ensureMedicationDispenseExists(String taskId) {
        IFhirResourceDao<MedicationDispense> medicationDispenseDao = daoRegistry.getResourceDao(MedicationDispense.class);
        
        SearchParameterMap params = new SearchParameterMap();
        params.add(MedicationDispense.SP_PRESCRIPTION, new ReferenceParam("Task/" + taskId));
        IBundleProvider results = medicationDispenseDao.search(params);
        
        if (results.isEmpty()) {
            throw new ForbiddenOperationException(
                "Abschluss des Workflows konnte nicht durchgeführt werden. Dispensierinformationen wurden nicht bereitgestellt."
            );
        }
    }

    /**
     * Lädt das signierte E-Rezept (Binary) für den Task.
     */
    private Binary loadPrescriptionBinary(Task task) {
        // Extrahiere HealthCarePrescriptionUuid aus Task Extension
        String healthCarePrescriptionUuid = task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid".equals(ext.getUrl()))
            .map(ext -> ((StringType) ext.getValue()).getValue())
            .findFirst()
            .orElse(null);
        
        if (healthCarePrescriptionUuid == null) {
            return null;
        }
        
        IFhirResourceDao<Binary> binaryDao = daoRegistry.getResourceDao(Binary.class);
        try {
            return binaryDao.read(new IdType("Binary", healthCarePrescriptionUuid));
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    /**
     * Extrahiert das in-progress Datum aus dem Task.
     */
    private Date getInProgressDate(Task task) {
        // Suche nach lastStatusChange Extension oder nutze lastModified
        Date lastStatusChange = task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastStatusChange".equals(ext.getUrl()))
            .map(ext -> ((DateTimeType) ext.getValue()).getValue())
            .findFirst()
            .orElse(null);
        
        if (lastStatusChange != null) {
            return lastStatusChange;
        }
        
        // Fallback auf lastModified
        return task.getLastModified() != null ? task.getLastModified() : task.getMeta().getLastUpdated();
    }

    /**
     * Aktualisiert den lastMedicationDispense Zeitstempel.
     */
    private void updateLastMedicationDispense(Task task) {
        task.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastMedicationDispense")
            .setValue(new DateTimeType(new Date()));
    }

    /**
     * Löscht alle Communications für einen Task.
     */
    private void deleteCommunicationsForTask(String taskId) {
        IFhirResourceDao<Communication> communicationDao = daoRegistry.getResourceDao(Communication.class);
        
        SearchParameterMap params = new SearchParameterMap();
        params.add(Communication.SP_BASED_ON, new ReferenceParam("Task/" + taskId));
        IBundleProvider results = communicationDao.search(params);
        
        // Iteriere über Ergebnisse und lösche sie
        if (results != null && results.size() != null && results.size() > 0) {
            for (IBaseResource resource : results.getAllResources()) {
                Communication comm = (Communication) resource;
                communicationDao.delete(comm.getIdElement());
                LOGGER.debug("Communication {} für Task {} gelöscht", comm.getIdElement().getIdPart(), taskId);
            }
        }
        
        LOGGER.debug("Alle Communications für Task {} gelöscht", taskId);
    }

    /**
     * Erstellt einen Audit-Log-Eintrag für die Close-Operation.
     */
    private void logCloseOperation(Task task, AccessToken accessToken, String kvnr) {
        try {
            String actorName = accessToken.getOrganizationName() != null 
                ? accessToken.getOrganizationName() 
                : accessToken.getIdNumber();
            
            String description = String.format(
                "E-Rezept Task mit ID '%s' durch %s abgeschlossen",
                task.getIdElement().getIdPart(),
                actorName
            );

            AuditEvent auditEvent = auditService.createRestAuditEvent(
                AuditEvent.AuditEventAction.U, // Update
                "close", // Subtype für $close Operation
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
                auditService.addEntityDetail(auditEvent, "task-status", "completed");
                if (kvnr != null) {
                    auditService.addEntityDetail(auditEvent, "kvnr", kvnr);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents für Close-Operation: {}", e.getMessage(), e);
            // Die Hauptoperation sollte nicht fehlschlagen
        }
    }
}