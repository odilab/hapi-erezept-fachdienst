package ca.uhn.fhir.jpa.starter.custom.operation.abort;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import ca.uhn.fhir.jpa.starter.custom.util.TestSslUtils;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
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
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für die $abort Operation.
 */
public class AbortOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortOperationIntegrationTest.class);

    /**
     * Helper-Klasse für Task-Testdaten
     */
    private static class TaskTestData {
        public final Task task;
        public final String accessCode;
        public final String prescriptionId;
        public final String secret;
        
        public TaskTestData(Task task, String accessCode, String prescriptionId, String secret) {
            this.task = task;
            this.accessCode = accessCode;
            this.prescriptionId = prescriptionId;
            this.secret = secret;
        }
    }

    // ========== ERFOLGSSZENARIEN ==========

    @Test
    public void testAbort_ByPatient_Success() {
        // Arrange: Erstelle Task und aktiviere ihn (Status: ready)
        TaskTestData testData = createActivatedTask("160");
        
        // Act: Patient löscht eigenes Rezept mit EGK1 Token
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            // Assert: Task sollte jetzt cancelled sein
            Task updatedTask = client.read()
                .resource(Task.class)
                .withId(testData.prescriptionId)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            assertEquals(Task.TaskStatus.CANCELLED, updatedTask.getStatus());
            assertPersonalDataRemoved(updatedTask);
            
        } catch (Exception e) {
            fail("Abort durch Patient sollte erfolgreich sein: " + e.getMessage());
        }
    }

    @Test
    public void testAbort_ByRepresentative_WithAccessCode_Success() {
        // Arrange: Task im Status ready
        TaskTestData testData = createActivatedTask("160");
        
        // Act: Vertreter löscht Rezept mit AccessCode (andere KVNR im Token)
        String accessTokenVertreter = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenVertreter)
                .execute();
            
            // Assert: Task sollte jetzt cancelled sein
            Task updatedTask = client.read()
                .resource(Task.class)
                .withId(testData.prescriptionId)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenVertreter)
                .execute();
            
            assertEquals(Task.TaskStatus.CANCELLED, updatedTask.getStatus());
            
        } catch (Exception e) {
            fail("Abort durch Vertreter mit AccessCode sollte erfolgreich sein: " + e.getMessage());
        }
    }

    @Test
    public void testAbort_ByDoctor_StatusReady_Success() {
        // Arrange: Task im Status ready
        TaskTestData testData = createActivatedTask("160");
        
        // Act: Arzt löscht Rezept mit AccessCode
        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .execute();
            
            // Assert: Task sollte jetzt cancelled sein
            Task updatedTask = client.read()
                .resource(Task.class)
                .withId(testData.prescriptionId)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .execute();
            
            assertEquals(Task.TaskStatus.CANCELLED, updatedTask.getStatus());
            
        } catch (Exception e) {
            fail("Abort durch Arzt sollte erfolgreich sein: " + e.getMessage());
        }
    }

    @Test
    public void testAbort_ByPharmacy_StatusInProgress_Success() {
        // Arrange: Task im Status in-progress (nach accept)
        TaskTestData testData = createAcceptedTask("160");
        
        // Act: Apotheke löscht Rezept mit Secret
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withParameter(Parameters.class, "secret", new StringType(testData.secret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
            
            // Assert: Task sollte jetzt cancelled sein
            Task updatedTask = client.read()
                .resource(Task.class)
                .withId(testData.prescriptionId)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
            
            assertEquals(Task.TaskStatus.CANCELLED, updatedTask.getStatus());
            
        } catch (Exception e) {
            fail("Abort durch Apotheke sollte erfolgreich sein: " + e.getMessage());
        }
    }

    // ========== FEHLERSZENARIEN ==========

    @Test
    public void testAbort_TaskNotFound_Returns404() {
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", "999.999.999.999.999.99"))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            fail("Sollte ResourceNotFoundException werfen");
        } catch (BaseServerResponseException e) {
            assertEquals(404, e.getStatusCode());
        }
    }

    @Test
    public void testAbort_StatusDraft_Returns403() {
        // Arrange: Task im Status draft
        TaskTestData testData = createDraftTask("160");
        
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            fail("Sollte ForbiddenOperationException werfen");
        } catch (BaseServerResponseException e) {
            assertEquals(403, e.getStatusCode());
            assertTrue(e.getMessage().contains("Abort not expected for newly created Task"));
        }
    }

    @Test
    public void testAbort_AlreadyCancelled_Returns410() {
        // Arrange: Erstelle und lösche Task
        TaskTestData testData = createActivatedTask("160");
        
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        // Erst erfolgreich löschen
        client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$abort")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
            .execute();
        
        // Dann nochmal versuchen
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            fail("Sollte ResourceGoneException werfen");
        } catch (ResourceGoneException e) {
            assertEquals(410, e.getStatusCode());
        }
    }

    @Test
    public void testAbort_PharmacyWrongSecret_Returns403() {
        // Arrange: Task im Status in-progress
        TaskTestData testData = createAcceptedTask("160");
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        String wrongSecret = "wrong-secret-12345";
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withParameter(Parameters.class, "secret", new StringType(wrongSecret))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .execute();
            
            fail("Sollte ForbiddenOperationException werfen");
        } catch (BaseServerResponseException e) {
            assertEquals(403, e.getStatusCode());
        }
    }

    @Test
    public void testAbort_PatientStatusInProgress_Returns403() {
        // Arrange: Task im Status in-progress
        TaskTestData testData = createAcceptedTask("160");
        
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            fail("Sollte ForbiddenOperationException werfen");
        } catch (BaseServerResponseException e) {
            assertEquals(403, e.getStatusCode());
            assertTrue(e.getMessage().contains("must not be in progress"));
        }
    }

    @Test
    public void testAbort_Flowtype169_NotCompleted_Returns403() {
        // Arrange: Task mit Flowtype 169 im Status ready
        TaskTestData testData = createActivatedTask("169");
        
        String accessTokenPatient = getValidAccessToken("EGK1");
        
        try {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$abort")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenPatient)
                .execute();
            
            fail("Sollte ForbiddenOperationException werfen");
        } catch (BaseServerResponseException e) {
            assertEquals(403, e.getStatusCode());
            assertTrue(e.getMessage().contains("169") || e.getMessage().contains("209"));
        }
    }

    // ========== HELPER METHODEN ==========

    /**
     * Erstellt einen Task im Status draft.
     */
    private TaskTestData createDraftTask(String flowType) {
        LOGGER.info("Erstelle Task mit FlowType {}", flowType);
        
        Parameters createParams = new Parameters();
        createParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode(flowType));
        
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
        String taskAccessCode = createdTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        LOGGER.info("Task erstellt mit ID: {}, AccessCode: {}", prescriptionId, taskAccessCode);
        
        return new TaskTestData(createdTask, taskAccessCode, prescriptionId, null);
    }

    /**
     * Erstellt einen Task und aktiviert ihn (Status: ready).
     */
    private TaskTestData createActivatedTask(String flowType) {
        LOGGER.info("Erstelle und aktiviere Task mit FlowType {}", flowType);
        
        // 1. Create Task
        TaskTestData draftData = createDraftTask(flowType);
        
        try {
            // 2. Activate Task mit korrekt angepasstem Bundle
            String signedBundle = createSignedBundleForTest(draftData.prescriptionId, versichertenKvnr);
            
            Binary ePrescription = new Binary();
            ePrescription.setContentType("application/pkcs7-mime");
            ePrescription.setDataElement(new Base64BinaryType(signedBundle));
            
            Parameters activateParams = new Parameters();
            activateParams.addParameter()
                .setName("ePrescription")
                .setResource(ePrescription);
            
            String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");
            
            Parameters activateResult = client
                .operation()
                .onInstance(new IdType("Task", draftData.prescriptionId))
                .named("$activate")
                .withParameters(activateParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .withAdditionalHeader("X-AccessCode", draftData.accessCode)
                .returnResourceType(Parameters.class)
                .execute();
            
            Task activatedTask = (Task) activateResult.getParameter().get(0).getResource();
            
            LOGGER.info("Task aktiviert mit ID: {}, Status: {}, For: {}", 
                activatedTask.getIdElement().getIdPart(), 
                activatedTask.getStatus(),
                activatedTask.hasFor() ? activatedTask.getFor().getIdentifier() : "keine KVNR");
            
            return new TaskTestData(activatedTask, draftData.accessCode, draftData.prescriptionId, null);
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Aktivieren des Tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Konnte Task nicht aktivieren", e);
        }
    }

    /**
     * Erstellt einen Task und führt ihn bis in-progress (nach accept).
     */
    private TaskTestData createAcceptedTask(String flowType) {
        LOGGER.info("Erstelle und akzeptiere Task mit FlowType {}", flowType);
        
        // 1. Create und Activate
        TaskTestData activatedData = createActivatedTask(flowType);
        
        // 2. Accept Task als Apotheke mit AccessCode als Parameter  
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Parameters acceptParams = new Parameters();
        acceptParams.addParameter()
            .setName("ac")
            .setValue(new StringType(activatedData.accessCode));
        
        Bundle acceptResult = client
            .operation()
            .onInstance(new IdType("Task", activatedData.prescriptionId))
            .named("$accept")
            .withParameters(acceptParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();
        
        // Extract Task und Secret aus Accept-Result
        Task acceptedTask = (Task) acceptResult.getEntry().get(0).getResource();
        String taskSecret = acceptedTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        LOGGER.info("Task akzeptiert mit ID: {}, Status: {}, Secret: {}", 
            acceptedTask.getIdElement().getIdPart(), acceptedTask.getStatus(), taskSecret);
        
        return new TaskTestData(acceptedTask, activatedData.accessCode, activatedData.prescriptionId, taskSecret);
    }

    /**
     * Erstellt ein signiertes Bundle für die Aktivierung.
     */
    private String createSignedBundleForTest(String prescriptionId, String kvnr) {
        try {
            String bundleXml = loadAndAdaptKbvBundleXml(prescriptionId, kvnr);
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-bundle-", ".xml");
            java.nio.file.Files.write(tempFile, bundleXml.getBytes(StandardCharsets.UTF_8));
            
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
            return response.getBody();
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des signierten Bundles: {}", e.getMessage());
            fail("Konnte signiertes Bundle nicht erstellen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lädt und passt das KBV Bundle XML an.
     */
    private String loadAndAdaptKbvBundleXml(String prescriptionId, String kvnr) throws IOException {
        ClassPathResource resource = new ClassPathResource("e-rezept-bundles/valid/Beispiel_4.xml");
        String bundleXml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        // PrescriptionID ersetzen
        String oldPattern = "(<system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"\\s*/>[\\s\\n\\r]*<value value=\")[^\"]+(\")";
        String replacement = "$1" + prescriptionId + "$2";
        bundleXml = bundleXml.replaceAll(oldPattern, replacement);
        
        // KVNR des Patienten ersetzen
        if (kvnr != null && !kvnr.isEmpty()) {
            // Pattern für KVID/KVNR in Patient Resource
            String kvnrPattern = "(<system value=\"http://fhir.de/sid/gkv/kvid-10\"\\s*/>[\\s\\n\\r]*<value value=\")[^\"]+(\")";
            String kvnrReplacement = "$1" + kvnr + "$2";
            bundleXml = bundleXml.replaceAll(kvnrPattern, kvnrReplacement);
        }
        
        // AuthoredOn auf heute setzen
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bundleXml = bundleXml.replaceAll("<authoredOn value=\"[^\"]+\"", "<authoredOn value=\"" + today + "\"");
        
        return bundleXml;
    }

    /**
     * Prüft ob personenbezogene Daten entfernt wurden.
     */
    private void assertPersonalDataRemoved(Task task) {
        // Überprüfe dass Owner entfernt wurde
        assertFalse(task.hasOwner(), "Owner sollte entfernt sein");
        
        // Überprüfe dass Input leer ist
        assertTrue(task.getInput().isEmpty(), "Input sollte leer sein");
        
        // Überprüfe dass Output leer ist
        assertTrue(task.getOutput().isEmpty(), "Output sollte leer sein");
        
        // KVNR sollte aber noch vorhanden sein (für Audit)
        assertTrue(task.hasFor(), "KVNR sollte noch vorhanden sein");
    }
}