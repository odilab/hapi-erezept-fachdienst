package ca.uhn.fhir.jpa.starter.custom.operation.create;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.model.DaoMethodOutcome;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service für die Erstellung von E-Rezept Tasks.
 * Generiert Rezept-IDs und AccessCodes gemäß Spezifikation.
 */
@Service
public class CreateTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTaskService.class);
    
    private static final String PRESCRIPTION_ID_SYSTEM = "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId";
    private static final String ACCESS_CODE_SYSTEM = "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode";
    private static final String FLOW_TYPE_EXTENSION_URL = "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType";
    
    // Map für FlowType zu PerformerType (gemäß Spezifikation)
    private static final Map<String, String> FLOW_TYPE_TO_PERFORMER_TYPE = new HashMap<>();
    static {
        FLOW_TYPE_TO_PERFORMER_TYPE.put("160", "urn:oid:1.2.276.0.76.4.54"); // Öffentliche Apotheke
        FLOW_TYPE_TO_PERFORMER_TYPE.put("169", "urn:oid:1.2.276.0.76.4.54"); // Öffentliche Apotheke
        FLOW_TYPE_TO_PERFORMER_TYPE.put("200", "urn:oid:1.2.276.0.76.4.54"); // Öffentliche Apotheke (PKV)
        FLOW_TYPE_TO_PERFORMER_TYPE.put("209", "urn:oid:1.2.276.0.76.4.59"); // Apotheke (DiGA)
        FLOW_TYPE_TO_PERFORMER_TYPE.put("210", "urn:oid:1.2.276.0.76.4.30"); // Kostenträger
    }

    private final DaoRegistry daoRegistry;
    private final CustomValidator customValidator;
    private final SecureRandom secureRandom;
    private long prescriptionIdCounter = 0; // In Produktion sollte dies persistent gespeichert werden

    @Autowired
    public CreateTaskService(DaoRegistry daoRegistry, CustomValidator customValidator) {
        this.daoRegistry = daoRegistry;
        this.customValidator = customValidator;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Erstellt einen neuen Task für ein E-Rezept.
     * 
     * @param workflowType Der Workflow-Typ (FlowType)
     * @param accessToken Das Access Token des aufrufenden Nutzers
     * @return Der erstellte Task mit Status "draft"
     */
    public Task createTask(Coding workflowType, AccessToken accessToken) {
        LOGGER.info("Erstelle neuen Task mit FlowType: {}", workflowType.getCode());

        Task task = new Task();
        
        // 1. Setze Basis-Eigenschaften
        task.setStatus(Task.TaskStatus.DRAFT);
        task.setIntent(Task.TaskIntent.ORDER);
        
        // 2. Setze Zeitstempel
        Date now = new Date();
        task.setAuthoredOn(now);
        task.setLastModified(now);
        
        // 3. Generiere und setze Prescription ID
        String prescriptionId = generatePrescriptionId(workflowType.getCode());
        task.addIdentifier()
            .setSystem(PRESCRIPTION_ID_SYSTEM)
            .setValue(prescriptionId)
            .setUse(Identifier.IdentifierUse.OFFICIAL);
        
        // 4. Generiere und setze Access Code (256 Bit Hex-String)
        String accessCode = generateAccessCode();
        task.addIdentifier()
            .setSystem(ACCESS_CODE_SYSTEM)
            .setValue(accessCode)
            .setUse(Identifier.IdentifierUse.OFFICIAL);
        
        // 5. Setze FlowType Extension
        Extension flowTypeExtension = new Extension(FLOW_TYPE_EXTENSION_URL);
        flowTypeExtension.setValue(workflowType);
        task.addExtension(flowTypeExtension);
        
        // 6. Setze PerformerType basierend auf FlowType
        CodeableConcept performerType = new CodeableConcept();
        String performerOid = FLOW_TYPE_TO_PERFORMER_TYPE.get(workflowType.getCode());
        if (performerOid != null) {
            performerType.addCoding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_OrganizationType")
                .setCode(performerOid)
                .setDisplay(getPerformerTypeDisplay(performerOid));
            task.addPerformerType(performerType);
        }
        
        // 7. Setze Meta-Informationen
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task|1.2");
        meta.setLastUpdated(now);
        task.setMeta(meta);
        
        // 8. Validiere den Task vor dem Speichern (CustomValidator wird automatisch beim Speichern aufgerufen)
        LOGGER.debug("Task wird validiert und gespeichert...");
        
        // 9. Speichere den Task
        IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
        DaoMethodOutcome outcome = taskDao.create(task);
        Task savedTask = (Task) outcome.getResource();
        
        LOGGER.info("Task erfolgreich erstellt - ID: {}, PrescriptionID: {}", 
            savedTask.getIdElement().getIdPart(), prescriptionId);
        
        return savedTask;
    }

    /**
     * Generiert eine Rezept-ID gemäß Spezifikation.
     * Format: [FlowType].[Laufende Nummer]
     * Beispiel: 160.000.000.000.000.01
     */
    private String generatePrescriptionId(String flowType) {
        // In Produktion sollte dies thread-safe und persistent sein
        synchronized (this) {
            prescriptionIdCounter++;
            
            // Format: XXX.XXX.XXX.XXX.XXX.XX (insgesamt 16 Stellen nach FlowType)
            String paddedCounter = String.format("%016d", prescriptionIdCounter);
            
            // Füge Punkte alle 3 Stellen ein (außer bei den letzten 2)
            StringBuilder formattedId = new StringBuilder(flowType);
            formattedId.append(".");
            formattedId.append(paddedCounter.substring(0, 3)).append(".");
            formattedId.append(paddedCounter.substring(3, 6)).append(".");
            formattedId.append(paddedCounter.substring(6, 9)).append(".");
            formattedId.append(paddedCounter.substring(9, 12)).append(".");
            formattedId.append(paddedCounter.substring(12, 14)).append(".");
            formattedId.append(paddedCounter.substring(14, 16));
            
            return formattedId.toString();
        }
    }

    /**
     * Generiert einen 256-Bit AccessCode als Hex-String.
     * Mindestentropie: 120 Bit gemäß Spezifikation.
     */
    private String generateAccessCode() {
        byte[] randomBytes = new byte[32]; // 256 Bit = 32 Byte
        secureRandom.nextBytes(randomBytes);
        
        // Konvertiere zu Hex-String
        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            hexString.append(String.format("%02x", b & 0xff));
        }
        
        return hexString.toString();
    }

    /**
     * Gibt den Display-Text für einen PerformerType zurück.
     */
    private String getPerformerTypeDisplay(String oid) {
        switch (oid) {
            case "urn:oid:1.2.276.0.76.4.54":
                return "Öffentliche Apotheke";
            case "urn:oid:1.2.276.0.76.4.59":
                return "Apotheke";
            case "urn:oid:1.2.276.0.76.4.30":
                return "Kostenträger";
            default:
                return "";
        }
    }
}