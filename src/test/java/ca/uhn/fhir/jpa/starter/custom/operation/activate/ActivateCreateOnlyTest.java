package ca.uhn.fhir.jpa.starter.custom.operation.activate;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test nur die Create Operation ohne Aktivierung.
 */
public class ActivateCreateOnlyTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateCreateOnlyTest.class);

    @Test
    public void testCreateTaskOperation() {
        // Arrange
        Parameters params = new Parameters();
        params.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160"));

        // Act
        Task task = client
            .operation()
            .onType(Task.class)
            .named("$create")
            .withParameters(params)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
            .returnResourceType(Task.class)
            .execute();

        // Assert
        assertNotNull(task);
        assertNotNull(task.getId());
        assertEquals(Task.TaskStatus.DRAFT, task.getStatus());
        assertNotNull(task.getIdentifierFirstRep().getValue()); // prescriptionId
        assertTrue(task.getIdentifierFirstRep().getValue().startsWith("160.")); // prescriptionId should start with workflow type
        
        // Check for AccessCode in extension
        boolean hasAccessCode = task.getExtension().stream()
            .anyMatch(ext -> ext.getUrl().contains("accessCode"));
        assertTrue(hasAccessCode, "Task should have accessCode extension");
        
        LOGGER.info("Task erfolgreich erstellt - ID: {}, PrescriptionID: {}, Status: {}", 
            task.getIdElement().getIdPart(), 
            task.getIdentifierFirstRep().getValue(),
            task.getStatus());
    }

    @Test
    public void testCreateTaskWithDifferentWorkflows() {
        String[] workflowTypes = {"160", "169", "200", "209"};
        
        for (String workflow : workflowTypes) {
            // Arrange
            Parameters params = new Parameters();
            params.addParameter()
                .setName("workflowType")
                .setValue(new Coding()
                    .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                    .setCode(workflow));

            // Act
            Task task = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(params)
                .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
                .returnResourceType(Task.class)
                .execute();

            // Assert
            assertNotNull(task);
            assertEquals(Task.TaskStatus.DRAFT, task.getStatus());
            assertTrue(task.getIdentifierFirstRep().getValue().startsWith(workflow + "."));
            
            LOGGER.info("Task für Workflow {} erstellt - PrescriptionID: {}", 
                workflow, task.getIdentifierFirstRep().getValue());
        }
    }
}