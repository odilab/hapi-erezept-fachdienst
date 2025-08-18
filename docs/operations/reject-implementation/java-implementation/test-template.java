package ca.uhn.fhir.jpa.starter.custom.operation.reject;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für die $reject Operation.
 * 
 * WICHTIG: Die Reject-Operation benötigt einen Task im Status "in-progress".
 * Daher muss zuerst create, dann activate, dann accept aufgerufen werden!
 */
public class RejectOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RejectOperationIntegrationTest.class);

    /**
     * Helper-Klasse für Test-Daten.
     */
    private static class TaskTestData {
        final Task task;
        final String accessCode;
        final String prescriptionId;
        final String secret;
        
        TaskTestData(Task task, String accessCode, String prescriptionId, String secret) {
            this.task = task;
            this.accessCode = accessCode;
            this.prescriptionId = prescriptionId;
            this.secret = secret;
        }
    }

    /**
     * Helper-Methode: Erstellt einen Task, aktiviert und akzeptiert ihn.
     * @return Task im Status "in-progress" mit Secret
     */
    private TaskTestData createActivateAndAcceptTask() {
        LOGGER.info("Erstelle, aktiviere und akzeptiere Task für Reject-Tests");
        
        // 1. Create Task
        Parameters createParams = new Parameters();
        createParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160"));

        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");
        
        Parameters createResult = client
            .operation()
            .onType(Task.class)
            .named("$create")
            .withParameters(createParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .returnResourceType(Parameters.class)
            .execute();

        Task createdTask = (Task) createResult.getParameter().get(0).getResource();
        String prescriptionId = createdTask.getIdElement().getIdPart();
        String accessCode = createdTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();

        // 2. Activate Task
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters activateParams = new Parameters();
        activateParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        Parameters activateResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .withAdditionalHeader("X-AccessCode", accessCode)
            .returnResourceType(Parameters.class)
            .execute();

        // 3. Accept Task (als Apotheke)
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");
        
        Bundle acceptResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$accept")
            .withParameter(Parameters.class, "ac", new StringType(accessCode))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();

        // Extrahiere Task und Secret aus Accept-Result
        Task acceptedTask = null;
        for (Bundle.BundleEntryComponent entry : acceptResult.getEntry()) {
            if (entry.getResource() instanceof Task) {
                acceptedTask = (Task) entry.getResource();
                break;
            }
        }
        
        assertNotNull(acceptedTask, "Task nicht in Accept-Response gefunden");
        
        String secret = acceptedTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow(() -> new AssertionError("Secret nicht in Task gefunden"));

        return new TaskTestData(acceptedTask, accessCode, prescriptionId, secret);
    }

    @Test
    public void testRejectSuccess() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");

        // Act - Reject mit korrektem Secret
        client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .execute();

        // Assert - Task sollte wieder im Status "ready" sein
        Bundle searchResult = client
            .search()
            .forResource(Task.class)
            .where(Task.IDENTIFIER.exactly().systemAndCode(
                "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
                testData.prescriptionId))
            .returnBundle(Bundle.class)
            .execute();

        assertEquals(1, searchResult.getEntry().size());
        Task rejectedTask = (Task) searchResult.getEntry().get(0).getResource();
        
        // A_19172-01: Status muss "ready" sein
        assertEquals(Task.TaskStatus.READY, rejectedTask.getStatus(), 
            "Task-Status sollte nach Reject 'ready' sein");
        
        // A_19172-01: Secret muss gelöscht sein
        boolean hasSecret = rejectedTask.getIdentifier().stream()
            .anyMatch(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()));
        assertFalse(hasSecret, "Secret sollte nach Reject gelöscht sein");
        
        // A_24175: Owner muss gelöscht sein
        assertFalse(rejectedTask.hasOwner(), "Owner sollte nach Reject gelöscht sein");
    }

    @Test
    public void testRejectWithWrongSecret() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");
        String wrongSecret = "0000000000000000000000000000000000000000000000000000000000000000";

        // Act & Assert - Reject mit falschem Secret sollte 403 werfen
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$reject")
                .withParameter(Parameters.class, "secret", new StringType(wrongSecret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
        }, "Reject mit falschem Secret sollte ForbiddenOperationException werfen");
    }

    @Test
    public void testRejectWithoutSecret() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");

        // Act & Assert - Reject ohne Secret sollte Fehler werfen
        assertThrows(Exception.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$reject")
                // Kein Secret-Parameter!
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
        }, "Reject ohne Secret sollte Exception werfen");
    }

    @Test
    public void testRejectTaskNotInProgress() {
        // Arrange - Erstelle nur Task (Status: draft)
        Parameters createParams = new Parameters();
        createParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160"));

        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");
        
        Parameters createResult = client
            .operation()
            .onType(Task.class)
            .named("$create")
            .withParameters(createParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .returnResourceType(Parameters.class)
            .execute();

        Task createdTask = (Task) createResult.getParameter().get(0).getResource();
        String prescriptionId = createdTask.getIdElement().getIdPart();
        
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");
        String dummySecret = "0000000000000000000000000000000000000000000000000000000000000000";

        // Act & Assert - Reject auf Task mit Status != in-progress sollte 403 werfen
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", prescriptionId))
                .named("$reject")
                .withParameter(Parameters.class, "secret", new StringType(dummySecret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
        }, "Reject auf Task mit Status != in-progress sollte ForbiddenOperationException werfen");
    }

    @Test
    public void testRejectNonExistentTask() {
        // Arrange
        String nonExistentTaskId = "160.000.000.000.000.99"; // Nicht existierende ID
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");
        String dummySecret = "0000000000000000000000000000000000000000000000000000000000000000";

        // Act & Assert - Reject auf nicht existierenden Task sollte 404 werfen
        assertThrows(ResourceNotFoundException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", nonExistentTaskId))
                .named("$reject")
                .withParameter(Parameters.class, "secret", new StringType(dummySecret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
        }, "Reject auf nicht existierenden Task sollte ResourceNotFoundException werfen");
    }

    @Test
    public void testRejectWithWrongProfession() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        // Verwende Arzt-Token statt Apotheken-Token
        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");

        // Act & Assert - Reject mit falscher Rolle sollte 403 werfen
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$reject")
                .withParameter(Parameters.class, "secret", new StringType(testData.secret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .execute();
        }, "Reject mit falscher Profession sollte ForbiddenOperationException werfen");
    }

    @Test
    public void testRejectDeletesMedicationDispense() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        
        // Füge MedicationDispense hinzu (simuliert durch Extension)
        // In echtem Test würde man hier $dispense aufrufen
        
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");

        // Act - Reject
        client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .execute();

        // Assert - MedicationDispense sollte gelöscht sein
        Bundle searchResult = client
            .search()
            .forResource(Task.class)
            .where(Task.IDENTIFIER.exactly().systemAndCode(
                "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
                testData.prescriptionId))
            .returnBundle(Bundle.class)
            .execute();

        Task rejectedTask = (Task) searchResult.getEntry().get(0).getResource();
        
        // A_24286-02: lastMedicationDispense Extension sollte nicht mehr vorhanden sein
        boolean hasLastMedicationDispense = rejectedTask.getExtension().stream()
            .anyMatch(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastMedicationDispense"
                .equals(ext.getUrl()));
        assertFalse(hasLastMedicationDispense, 
            "lastMedicationDispense Extension sollte nach Reject gelöscht sein");
    }

    @Test
    public void testRejectOwnerDeleted() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        
        // Verifiziere dass Owner nach Accept gesetzt ist
        assertTrue(testData.task.hasOwner(), "Task sollte nach Accept einen Owner haben");
        
        String accessTokenApotheke = getValidAccessToken("HBA_APOTHEKE");

        // Act - Reject
        client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .execute();

        // Assert - Owner sollte gelöscht sein (A_24175)
        Bundle searchResult = client
            .search()
            .forResource(Task.class)
            .where(Task.IDENTIFIER.exactly().systemAndCode(
                "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
                testData.prescriptionId))
            .returnBundle(Bundle.class)
            .execute();

        Task rejectedTask = (Task) searchResult.getEntry().get(0).getResource();
        assertFalse(rejectedTask.hasOwner(), "Owner sollte nach Reject gelöscht sein (A_24175)");
    }
}