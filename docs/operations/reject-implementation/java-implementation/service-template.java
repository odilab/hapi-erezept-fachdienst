package ca.uhn.fhir.jpa.starter.custom.operation.reject;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service-Klasse für die Geschäftslogik der $reject Operation.
 * 
 * Hauptaufgabe: Löschen von MedicationDispense-Ressourcen falls vorhanden (A_24286-02)
 */
@Service
public class RejectTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RejectTaskService.class);

    private final DaoRegistry daoRegistry;

    @Autowired
    public RejectTaskService(DaoRegistry daoRegistry) {
        this.daoRegistry = daoRegistry;
    }

    /**
     * Löscht MedicationDispense und zugehörige Medication falls vorhanden.
     * Implementiert A_24286-02: "Delete MedicationDispense"
     * 
     * @param task Der Task für den MedicationDispense gelöscht werden soll
     * @param requestDetails Request-Kontext
     */
    public void deleteMedicationDispenseIfExists(Task task, RequestDetails requestDetails) {
        try {
            // Prüfe ob Task eine lastMedicationDispense Extension hat
            Extension lastMedicationDispenseExt = task.getExtension().stream()
                .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastMedicationDispense"
                    .equals(ext.getUrl()))
                .findFirst()
                .orElse(null);
            
            if (lastMedicationDispenseExt == null) {
                LOGGER.debug("Task {} hat keine lastMedicationDispense Extension", 
                    task.getIdElement().getIdPart());
                return;
            }

            // Extrahiere die MedicationDispense ID aus der Extension
            if (lastMedicationDispenseExt.getValue() instanceof Reference) {
                Reference medicationDispenseRef = (Reference) lastMedicationDispenseExt.getValue();
                String medicationDispenseId = medicationDispenseRef.getReferenceElement().getIdPart();
                
                if (medicationDispenseId != null && !medicationDispenseId.isEmpty()) {
                    deleteMedicationDispense(medicationDispenseId, requestDetails);
                    
                    // Entferne die Extension aus dem Task
                    task.getExtension().remove(lastMedicationDispenseExt);
                    LOGGER.info("LastMedicationDispense Extension aus Task {} entfernt", 
                        task.getIdElement().getIdPart());
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Löschen von MedicationDispense für Task {}: {}", 
                task.getIdElement().getIdPart(), e.getMessage(), e);
            // Fehler beim Löschen von MedicationDispense sollte die Reject-Operation nicht verhindern
        }
    }

    /**
     * Löscht eine MedicationDispense und die zugehörige Medication.
     * 
     * @param medicationDispenseId ID der zu löschenden MedicationDispense
     * @param requestDetails Request-Kontext
     */
    private void deleteMedicationDispense(String medicationDispenseId, RequestDetails requestDetails) {
        IFhirResourceDao<MedicationDispense> medicationDispenseDao = 
            daoRegistry.getResourceDao(MedicationDispense.class);
        
        try {
            // Lade MedicationDispense
            IdType medicationDispenseIdType = new IdType("MedicationDispense", medicationDispenseId);
            MedicationDispense medicationDispense = medicationDispenseDao.read(
                medicationDispenseIdType, requestDetails);
            
            // Prüfe ob eine Medication referenziert wird
            if (medicationDispense.hasMedicationReference()) {
                Reference medicationRef = medicationDispense.getMedicationReference();
                String medicationId = medicationRef.getReferenceElement().getIdPart();
                
                if (medicationId != null && !medicationId.isEmpty()) {
                    deleteMedication(medicationId, requestDetails);
                }
            }
            
            // Lösche MedicationDispense
            medicationDispenseDao.delete(medicationDispenseIdType, requestDetails);
            LOGGER.info("MedicationDispense {} gelöscht", medicationDispenseId);
            
        } catch (Exception e) {
            LOGGER.warn("MedicationDispense {} konnte nicht gelöscht werden: {}", 
                medicationDispenseId, e.getMessage());
        }
    }

    /**
     * Löscht eine Medication-Ressource.
     * 
     * @param medicationId ID der zu löschenden Medication
     * @param requestDetails Request-Kontext
     */
    private void deleteMedication(String medicationId, RequestDetails requestDetails) {
        IFhirResourceDao<Medication> medicationDao = daoRegistry.getResourceDao(Medication.class);
        
        try {
            IdType medicationIdType = new IdType("Medication", medicationId);
            medicationDao.delete(medicationIdType, requestDetails);
            LOGGER.info("Medication {} gelöscht", medicationId);
        } catch (Exception e) {
            LOGGER.warn("Medication {} konnte nicht gelöscht werden: {}", 
                medicationId, e.getMessage());
        }
    }

    /**
     * Alternative Methode: Sucht und löscht alle MedicationDispense für einen Task.
     * Kann verwendet werden wenn keine lastMedicationDispense Extension vorhanden ist.
     * 
     * @param taskId Die Task-ID für die MedicationDispense gesucht werden
     * @param requestDetails Request-Kontext
     */
    public void deleteAllMedicationDispenseForTask(String taskId, RequestDetails requestDetails) {
        IFhirResourceDao<MedicationDispense> medicationDispenseDao = 
            daoRegistry.getResourceDao(MedicationDispense.class);
        
        try {
            // Suche alle MedicationDispense die diesen Task referenzieren
            SearchParameterMap searchParams = new SearchParameterMap();
            searchParams.add(MedicationDispense.SP_PRESCRIPTION, 
                new ReferenceParam("Task/" + taskId));
            
            IBundleProvider searchResults = medicationDispenseDao.search(searchParams, requestDetails);
            List<IBaseResource> resources = searchResults.getResources(0, 100);
            
            for (IBaseResource resource : resources) {
                if (resource instanceof MedicationDispense) {
                    MedicationDispense md = (MedicationDispense) resource;
                    
                    // Lösche zugehörige Medication falls vorhanden
                    if (md.hasMedicationReference()) {
                        String medicationId = md.getMedicationReference()
                            .getReferenceElement().getIdPart();
                        if (medicationId != null) {
                            deleteMedication(medicationId, requestDetails);
                        }
                    }
                    
                    // Lösche MedicationDispense
                    medicationDispenseDao.delete(md.getIdElement(), requestDetails);
                    LOGGER.info("MedicationDispense {} für Task {} gelöscht", 
                        md.getIdElement().getIdPart(), taskId);
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Suchen/Löschen von MedicationDispense für Task {}: {}", 
                taskId, e.getMessage(), e);
        }
    }

    /**
     * Prüft ob für einen Task MedicationDispense existieren.
     * 
     * @param task Der zu prüfende Task
     * @return true wenn MedicationDispense existieren
     */
    public boolean hasMedicationDispense(Task task) {
        // Prüfe Extension
        return task.getExtension().stream()
            .anyMatch(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastMedicationDispense"
                .equals(ext.getUrl()));
    }
}