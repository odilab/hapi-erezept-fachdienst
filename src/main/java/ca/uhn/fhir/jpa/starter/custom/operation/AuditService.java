package ca.uhn.fhir.jpa.starter.custom.operation;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Service für die Erstellung von AuditEvents für E-Rezept-Operationen.
 * Vereinfachte Version.
 */
@Service
public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    private final DaoRegistry daoRegistry;

    @Autowired
    public AuditService(DaoRegistry daoRegistry) {
        this.daoRegistry = daoRegistry;
    }

    /**
     * Erstellt ein AuditEvent für REST-Operationen.
     */
    public AuditEvent createRestAuditEvent(
            AuditEvent.AuditEventAction action,
            String subtype,
            AuditEvent.AuditEventOutcome outcome,
            Reference whatReference,
            String whatType,
            String whatDisplay,
            String description,
            String actorName,
            String actorId,
            Reference patientReference) {
        
        try {
            AuditEvent auditEvent = new AuditEvent();
            
            // Basis-Eigenschaften
            auditEvent.setType(new Coding()
                .setSystem("http://dicom.nema.org/resources/ontology/DCM")
                .setCode("110106")
                .setDisplay("Export"));
            
            auditEvent.setAction(action);
            auditEvent.setOutcome(outcome);
            auditEvent.setRecorded(new Date());
            
            // Subtype
            if (subtype != null) {
                auditEvent.addSubtype(new Coding()
                    .setSystem("http://hl7.org/fhir/restful-interaction")
                    .setCode(subtype));
            }
            
            // Agent (Actor)
            AuditEvent.AuditEventAgentComponent agent = auditEvent.addAgent();
            agent.setWho(new Reference()
                .setDisplay(actorName)
                .setIdentifier(new Identifier()
                    .setValue(actorId)));
            agent.setRequestor(true);
            
            // Entity (What)
            if (whatReference != null) {
                AuditEvent.AuditEventEntityComponent entity = auditEvent.addEntity();
                entity.setWhat(whatReference);
                entity.setType(new Coding()
                    .setSystem("http://hl7.org/fhir/resource-types")
                    .setCode(whatType));
                if (whatDisplay != null) {
                    entity.setName(whatDisplay);
                }
                if (description != null) {
                    entity.setDescription(description);
                }
            }
            
            // Speichere das AuditEvent
            IFhirResourceDao<AuditEvent> auditEventDao = daoRegistry.getResourceDao(AuditEvent.class);
            AuditEvent savedAuditEvent = (AuditEvent) auditEventDao.create(auditEvent).getResource();
            
            LOGGER.debug("AuditEvent erstellt mit ID: {}", savedAuditEvent.getIdElement().getIdPart());
            return savedAuditEvent;
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des AuditEvents: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Fügt einem AuditEvent ein Detail hinzu.
     */
    public void addEntityDetail(AuditEvent auditEvent, String type, String value) {
        if (auditEvent == null || !auditEvent.hasEntity()) {
            return;
        }
        
        AuditEvent.AuditEventEntityComponent entity = auditEvent.getEntityFirstRep();
        entity.addDetail()
            .setType(type)
            .setValue(new Base64BinaryType(value.getBytes()));
    }
}