package ca.uhn.fhir.jpa.starter.custom.operation.abort;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service für die $abort Operation.
 * Behandelt die Geschäftslogik für das Löschen von Tasks und zugehörigen Daten.
 * 
 * Implementiert gemäß A_19027-06: Löschung personenbezogener Daten.
 */
@Service
public class AbortTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTaskService.class);

    private final DaoRegistry daoRegistry;

    @Autowired
    public AbortTaskService(DaoRegistry daoRegistry) {
        this.daoRegistry = daoRegistry;
    }

    /**
     * Entfernt alle personenbezogenen Daten aus dem Task (A_19027-06).
     * 
     * Gemäß Spezifikation müssen alle personenbezogenen medizinischen Daten
     * außer der KVNR in Task.for aus dem Task entfernt werden.
     * 
     * @param task Der zu bereinigende Task
     */
    public void clearPersonalDataFromTask(Task task) {
        LOGGER.debug("Lösche personenbezogene Daten aus Task {}", task.getIdElement().getIdPart());
        
        // WICHTIG: PrescriptionId muss erhalten bleiben, da sie die primäre Task-ID ist!
        // Entferne nur AccessCode und Secret
        
        // Entferne AccessCode
        task.getIdentifier().removeIf(id -> 
            "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()));
        
        // Entferne Secret
        task.getIdentifier().removeIf(id -> 
            "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()));
        
        // Entferne Owner (Apotheken-Telematik-ID)
        if (task.hasOwner()) {
            task.setOwner(null);
        }
        
        // WICHTIG: Entferne Extensions die auf andere Ressourcen verweisen
        // Diese müssen entfernt werden, damit der Task keine ungültigen Referenzen hat
        
        // Entferne HealthCarePrescriptionUuid Extension (verweist auf Binary)
        task.getExtension().removeIf(ext -> 
            "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid".equals(ext.getUrl()));
        
        // Entferne PatientConfirmationUuid Extension
        task.getExtension().removeIf(ext -> 
            "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_PatientConfirmationUuid".equals(ext.getUrl()));
        
        // Entferne ReceiptUuid Extension (verweist auf Bundle)
        task.getExtension().removeIf(ext -> 
            "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_ReceiptUuid".equals(ext.getUrl()));
        
        // Entferne LastMedicationDispense Extension
        task.getExtension().removeIf(ext -> 
            "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastMedicationDispense".equals(ext.getUrl()));
        
        // Entferne AcceptDate Extension
        task.getExtension().removeIf(ext -> 
            "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_AcceptDate".equals(ext.getUrl()));
        
        // WICHTIG: ExpiryDate und FlowType bleiben erhalten, da sie für die Task-Struktur notwendig sind
        
        // Entferne Input und Output, da diese Referenzen auf Binaries oder andere Ressourcen enthalten könnten
        // Diese müssen entfernt werden BEVOR der Task gespeichert wird
        if (task.hasInput()) {
            // Input kann Referenzen auf Binary (HealthCarePrescription) enthalten
            task.getInput().clear();
        }
        
        if (task.hasOutput()) {
            // Output kann Referenzen auf Bundle (Receipt) enthalten
            task.getOutput().clear();
        }
        
        // WICHTIG: Task.for (KVNR) bleibt erhalten gemäß Spezifikation!
        // Die KVNR wird für Audit und Nachvollziehbarkeit benötigt
        
        LOGGER.debug("Personenbezogene Daten aus Task {} entfernt", task.getIdElement().getIdPart());
    }

    /**
     * Löscht alle Communications die mit dem Task verbunden sind (A_19027-06).
     * 
     * @param taskId Die ID des Tasks dessen Communications gelöscht werden sollen
     */
    public void deleteCommunicationsForTask(String taskId) {
        LOGGER.debug("Lösche Communications für Task {}", taskId);
        
        try {
            IFhirResourceDao<Communication> communicationDao = daoRegistry.getResourceDao(Communication.class);
            
            // Suche alle Communications mit basedOn = Task/{taskId}
            SearchParameterMap searchParams = new SearchParameterMap();
            searchParams.add(Communication.SP_BASED_ON, new ReferenceParam("Task/" + taskId));
            
            IBundleProvider searchResults = communicationDao.search(searchParams);
            List<IBaseResource> communications = searchResults.getAllResources();
            
            LOGGER.info("Gefunden: {} Communications für Task {}", communications.size(), taskId);
            
            // Lösche jede gefundene Communication
            for (IBaseResource resource : communications) {
                Communication communication = (Communication) resource;
                try {
                    communicationDao.delete(communication.getIdElement());
                    LOGGER.debug("Communication {} gelöscht", communication.getIdElement().getIdPart());
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Löschen der Communication {}: {}", 
                        communication.getIdElement().getIdPart(), e.getMessage());
                    // Fehler beim Löschen einzelner Communications sollte nicht die gesamte Operation abbrechen
                }
            }
            
            LOGGER.info("Alle {} Communications für Task {} gelöscht", communications.size(), taskId);
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Löschen der Communications für Task {}: {}", taskId, e.getMessage());
            // Fehler beim Löschen von Communications sollte nicht die Hauptoperation abbrechen
            // gemäß C++ Implementierung
        }
    }

    /**
     * Löscht das HealthCareProviderPrescription Binary wenn vorhanden.
     * 
     * @param task Der Task mit möglicher Binary-Referenz
     */
    public void deleteHealthCarePrescriptionBinary(Task task) {
        // Extrahiere die healthCarePrescriptionUuid aus Task Extension
        String healthCarePrescriptionUuid = task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid".equals(ext.getUrl()))
            .map(ext -> ((StringType) ext.getValue()).getValue())
            .findFirst()
            .orElse(null);
            
        if (healthCarePrescriptionUuid != null) {
            try {
                IFhirResourceDao<Binary> binaryDao = daoRegistry.getResourceDao(Binary.class);
                binaryDao.delete(new IdType("Binary", healthCarePrescriptionUuid));
                LOGGER.debug("HealthCarePrescription Binary {} gelöscht", healthCarePrescriptionUuid);
            } catch (Exception e) {
                LOGGER.warn("Konnte HealthCarePrescription Binary {} nicht löschen: {}", 
                    healthCarePrescriptionUuid, e.getMessage());
                // Fehler beim Löschen sollte nicht die Hauptoperation abbrechen
            }
        }
    }

    /**
     * Löscht das Receipt Bundle wenn vorhanden.
     * 
     * @param task Der Task mit möglicher Receipt-Referenz
     */
    public void deleteReceiptBundle(Task task) {
        // Extrahiere die receiptUuid aus Task Extension
        String receiptUuid = task.getExtension().stream()
            .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_ReceiptUuid".equals(ext.getUrl()))
            .map(ext -> ((StringType) ext.getValue()).getValue())
            .findFirst()
            .orElse(null);
            
        if (receiptUuid != null) {
            try {
                IFhirResourceDao<Bundle> bundleDao = daoRegistry.getResourceDao(Bundle.class);
                bundleDao.delete(new IdType("Bundle", receiptUuid));
                LOGGER.debug("Receipt Bundle {} gelöscht", receiptUuid);
            } catch (Exception e) {
                LOGGER.warn("Konnte Receipt Bundle {} nicht löschen: {}", 
                    receiptUuid, e.getMessage());
                // Fehler beim Löschen sollte nicht die Hauptoperation abbrechen
            }
        }
    }

    /**
     * Löscht alle MedicationDispense Ressourcen die mit dem Task verbunden sind.
     * 
     * @param taskId Die ID des Tasks
     */
    public void deleteMedicationDispenses(String taskId) {
        try {
            IFhirResourceDao<MedicationDispense> medicationDispenseDao = 
                daoRegistry.getResourceDao(MedicationDispense.class);
            
            // Suche alle MedicationDispenses mit authorizingPrescription = Task/{taskId}
            SearchParameterMap searchParams = new SearchParameterMap();
            searchParams.add(MedicationDispense.SP_PRESCRIPTION, new ReferenceParam("Task/" + taskId));
            
            IBundleProvider searchResults = medicationDispenseDao.search(searchParams);
            List<IBaseResource> medicationDispenses = searchResults.getAllResources();
            
            LOGGER.info("Gefunden: {} MedicationDispenses für Task {}", medicationDispenses.size(), taskId);
            
            // Lösche jede gefundene MedicationDispense
            for (IBaseResource resource : medicationDispenses) {
                MedicationDispense dispense = (MedicationDispense) resource;
                try {
                    medicationDispenseDao.delete(dispense.getIdElement());
                    LOGGER.debug("MedicationDispense {} gelöscht", dispense.getIdElement().getIdPart());
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Löschen der MedicationDispense {}: {}", 
                        dispense.getIdElement().getIdPart(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Löschen der MedicationDispenses für Task {}: {}", taskId, e.getMessage());
        }
    }

    /**
     * Führt eine vollständige Bereinigung aller Task-bezogenen Daten durch.
     * Diese Methode kombiniert alle Löschoperationen.
     * 
     * @param task Der zu bereinigende Task
     */
    public void performCompleteDataCleanup(Task task) {
        String taskId = task.getIdElement().getIdPart();
        
        LOGGER.info("Starte vollständige Datenbereinigung für Task {}", taskId);
        
        // 1. Lösche Communications
        deleteCommunicationsForTask(taskId);
        
        // 2. Lösche HealthCareProviderPrescription Binary
        deleteHealthCarePrescriptionBinary(task);
        
        // 3. Lösche Receipt Bundle
        deleteReceiptBundle(task);
        
        // 4. Lösche MedicationDispenses
        deleteMedicationDispenses(taskId);
        
        // 5. Bereinige personenbezogene Daten aus Task
        clearPersonalDataFromTask(task);
        
        LOGGER.info("Vollständige Datenbereinigung für Task {} abgeschlossen", taskId);
    }
}