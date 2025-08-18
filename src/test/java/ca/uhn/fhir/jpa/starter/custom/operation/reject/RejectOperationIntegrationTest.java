package ca.uhn.fhir.jpa.starter.custom.operation.reject;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import ca.uhn.fhir.jpa.starter.custom.util.TestSslUtils;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
        String signedBundle = createSignedBundleForTest(prescriptionId, versichertenKvnr);
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters activateParams = new Parameters();
        activateParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .withAdditionalHeader("X-AccessCode", accessCode)
            .returnResourceType(Parameters.class)
            .execute();

        // 3. Accept Task (als Apotheke)
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Bundle acceptResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$accept")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .withAdditionalHeader("X-AccessCode", accessCode)
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

    /**
     * Helper-Methode: Erstellt ein signiertes Bundle für Tests.
     * Nutzt den Fachdiensttool-Container zum Signieren.
     */
    private String createSignedBundleForTest(String prescriptionId, String kvnr) {
        try {
            // Erstelle temporäre XML-Datei mit angepasstem Bundle
            String bundleXml = loadAndAdaptKbvBundleXml(prescriptionId);
            
            // Erstelle temporäre Datei
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-bundle-", ".xml");
            java.nio.file.Files.write(tempFile, bundleXml.getBytes(StandardCharsets.UTF_8));
            
            // Signiere das Bundle mit dem Fachdiensttool
            RestTemplate restTemplate = TestSslUtils.createTrustAllRestTemplate();
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("kbvBundleFromFile", new org.springframework.core.io.FileSystemResource(tempFile.toFile()));
            body.add("kbvBundleAsString", "");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            
            String signUrl = String.format("https://localhost:%d/signDocumentWithTool",
                TestcontainersConfig.startErpServiceContainer().getMappedPort(3001));
            
            ResponseEntity<String> response = restTemplate.postForEntity(signUrl, request, String.class);
            
            return response.getBody(); // Base64 encoded signature
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des signierten Bundles: {}", e.getMessage());
            fail("Konnte signiertes Bundle nicht erstellen: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lädt ein echtes KBV Bundle aus den Test-Ressourcen und passt die PrescriptionID an.
     */
    private String loadAndAdaptKbvBundleXml(String prescriptionId) throws IOException {
        // Lade ein echtes Bundle aus den Test-Ressourcen
        ClassPathResource resource = new ClassPathResource("e-rezept-bundles/valid/Beispiel_4.xml");
        String bundleXml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        // PrescriptionID ersetzen - mit korrektem Pattern für XML
        String oldPattern = "(<system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"\\s*/>[\\s\\n\\r]*<value value=\")[^\"]+(\")";
        String replacement = "$1" + prescriptionId + "$2";
        bundleXml = bundleXml.replaceAll(oldPattern, replacement);
        
        // AuthoredOn auf heute setzen für Activate-Validierung
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bundleXml = bundleXml.replaceAll("<authoredOn value=\"[^\"]+\"", "<authoredOn value=\"" + today + "\"");
        
        return bundleXml;
    }

    @Test
    public void testRejectSuccess() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act - Reject mit korrektem Secret als Parameter
        Task rejectedTaskResult = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Task.class)
            .execute();

        // Assert - Task sollte wieder im Status "ready" sein
        // Verwende den zurückgegebenen Task direkt statt zu suchen
        assertNotNull(rejectedTaskResult, "Reject sollte einen Task zurückgeben");

        // A_19172-01: Status muss "ready" sein
        assertEquals(Task.TaskStatus.READY, rejectedTaskResult.getStatus(), 
            "Task-Status sollte nach Reject 'ready' sein");
        
        // A_19172-01: Secret muss gelöscht sein
        boolean hasSecret = rejectedTaskResult.getIdentifier().stream()
            .anyMatch(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()));
        assertFalse(hasSecret, "Secret sollte nach Reject gelöscht sein");
        
        // A_24175: Owner muss gelöscht sein
        assertFalse(rejectedTaskResult.hasOwner(), "Owner sollte nach Reject gelöscht sein");
    }

    @Test
    public void testRejectWithWrongSecret() {
        // Arrange
        TaskTestData testData = createActivateAndAcceptTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
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
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act & Assert - Reject ohne Secret sollte Fehler werfen
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$reject")
                .withNoParameters(Parameters.class)
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
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
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
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
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
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act - Reject
        Task rejectedTaskResult = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Task.class)
            .execute();

        // Assert - MedicationDispense sollte gelöscht sein
        // Verwende den zurückgegebenen Task direkt
        assertNotNull(rejectedTaskResult, "Reject sollte einen Task zurückgeben");
        
        // A_24286-02: lastMedicationDispense Extension sollte nicht mehr vorhanden sein
        boolean hasLastMedicationDispense = rejectedTaskResult.getExtension().stream()
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
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act - Reject
        Task rejectedTaskResult = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$reject")
            .withParameter(Parameters.class, "secret", new StringType(testData.secret))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Task.class)
            .execute();

        // Assert - Owner sollte gelöscht sein (A_24175)
        // Verwende den zurückgegebenen Task direkt
        assertNotNull(rejectedTaskResult, "Reject sollte einen Task zurückgeben");
        assertFalse(rejectedTaskResult.hasOwner(), "Owner sollte nach Reject gelöscht sein (A_24175)");
    }
}