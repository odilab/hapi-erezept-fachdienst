package ca.uhn.fhir.jpa.starter.custom.operation.accept;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für die $accept Operation.
 * 
 * WICHTIG: Die Accept-Operation benötigt einen Task im Status "ready".
 * Daher muss zuerst create und dann activate aufgerufen werden!
 */
public class AcceptOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptOperationIntegrationTest.class);

    /**
     * Helper-Methode: Erstellt einen Task und aktiviert ihn.
     * @return Task im Status "ready" mit AccessCode
     * 
     * Hinweis: Seit der Änderung ist Task-ID = Prescription-ID,
     * was die Tests vereinfacht.
     */
    private TaskTestData createAndActivateTask() {
        LOGGER.info("Erstelle und aktiviere Task für Accept-Tests");
        
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
        // Seit der Änderung ist Task-ID = Prescription-ID
        String prescriptionId = createdTask.getIdElement().getIdPart();
        String accessCode = createdTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();

        // 2. Activate Task mit signiertem Bundle
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters activateParams = new Parameters();
        activateParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        // Activate gibt Parameters zurück, nicht direkt einen Task
        Parameters activateResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .withAdditionalHeader("X-AccessCode", accessCode)
            .returnResourceType(Parameters.class)
            .execute();
        
        Task activatedTask = (Task) activateResult.getParameter().get(0).getResource();

        return new TaskTestData(activatedTask, accessCode, prescriptionId);
    }

    @Test
    public void testAcceptTask_ValidRequest_ReturnsBundle() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act
        Bundle result = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$accept")
            .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();

        // Assert
        assertNotNull(result);
        assertEquals(Bundle.BundleType.COLLECTION, result.getType());
        
        // Prüfe Meta-Profile
        assertTrue(result.hasMeta());
        assertTrue(result.getMeta().hasProfile());
        assertTrue(result.getMeta().getProfile().stream()
            .anyMatch(p -> p.getValue().contains("GEM_ERP_PR_PAR_AcceptOperation_Output")));

        // Prüfe Bundle-Inhalt (Task + Binary)
        LOGGER.info("Bundle enthält {} Entries", result.getEntry().size());
        for (Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() != null) {
                LOGGER.info("Entry: {}", entry.getResource().getResourceType());
            }
        }
        assertTrue(result.getEntry().size() >= 2, "Bundle sollte mindestens Task und Binary enthalten (hat aber nur " + result.getEntry().size() + ")");
        
        // Prüfe Task
        Task acceptedTask = null;
        for (Bundle.BundleEntryComponent entry : result.getEntry()) {
            if (entry.getResource() instanceof Task) {
                acceptedTask = (Task) entry.getResource();
                break;
            }
        }
        
        assertNotNull(acceptedTask, "Bundle sollte einen Task enthalten");
        assertEquals(Task.TaskStatus.INPROGRESS, acceptedTask.getStatus());
        
        // Prüfe Secret
        boolean hasSecret = acceptedTask.getIdentifier().stream()
            .anyMatch(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()));
        assertTrue(hasSecret, "Task sollte ein Secret haben");
        
        String secret = acceptedTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElse("");
        assertEquals(64, secret.length(), "Secret sollte 64 Zeichen lang sein (256 Bit hex)");
        
        // Prüfe Owner
        assertTrue(acceptedTask.hasOwner(), "Task sollte einen Owner haben");
        assertNotNull(acceptedTask.getOwner().getDisplay(), "Owner sollte Telematik-ID enthalten");
        
        // Prüfe Binary
        boolean hasBinary = result.getEntry().stream()
            .anyMatch(entry -> entry.getResource() instanceof Binary);
        assertTrue(hasBinary, "Bundle sollte ein Binary (signiertes E-Rezept) enthalten");
    }

    @Test
    public void testAcceptTask_WithHeaderAccessCode_Success() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act - AccessCode im Header statt Query-Parameter
        Bundle result = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$accept")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .withAdditionalHeader("X-AccessCode", testData.accessCode)
            .returnResourceType(Bundle.class)
            .execute();

        // Assert
        assertNotNull(result);
        assertEquals(Bundle.BundleType.COLLECTION, result.getType());
    }

    @Test
    public void testAcceptTask_WrongAccessCode_Returns403() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        String wrongAccessCode = "0000000000000000000000000000000000000000000000000000000000000000";

        // Act & Assert
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$accept")
                .withParameter(Parameters.class, "ac", new StringType(wrongAccessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }

    @Test
    public void testAcceptTask_MissingAccessCode_Returns403() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act & Assert
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$accept")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }

    @Test
    public void testAcceptTask_TaskInDraftStatus_Returns409() {
        // Arrange - Erstelle Task aber aktiviere ihn nicht
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
        // Seit der Änderung ist Task-ID = Prescription-ID
        String prescriptionId = createdTask.getIdElement().getIdPart();
        String accessCode = createdTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();

        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Act & Assert
        BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", prescriptionId))
                .named("$accept")
                .withParameter(Parameters.class, "ac", new StringType(accessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
        
        assertEquals(409, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("invalid status draft"));
    }

    @Test
    public void testAcceptTask_TaskAlreadyInProgress_Returns409() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");

        // Erster Accept - sollte funktionieren
        Bundle firstAccept = client
            .operation()
            .onInstance(new IdType("Task", testData.prescriptionId))
            .named("$accept")
            .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();

        assertNotNull(firstAccept);

        // Zweiter Accept - sollte fehlschlagen
        BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$accept")
                .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });

        assertEquals(409, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("invalid status in-progress"));
    }

    @Test
    public void testAcceptTask_UnauthorizedRole_Returns403() {
        // Arrange
        TaskTestData testData = createAndActivateTask();
        // Verwende Arzt-Token statt Apotheken-Token
        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");

        // Act & Assert
        assertThrows(ForbiddenOperationException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", testData.prescriptionId))
                .named("$accept")
                .withParameter(Parameters.class, "ac", new StringType(testData.accessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }

    @Test
    public void testAcceptTask_NonExistentTask_Returns404() {
        // Arrange
        String nonExistentId = "160.000.000.000.000.99"; // Nicht existierende ID
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        String randomAccessCode = UUID.randomUUID().toString().replace("-", "") + 
                                  UUID.randomUUID().toString().replace("-", "");

        // Act & Assert
        assertThrows(BaseServerResponseException.class, () -> {
            client
                .operation()
                .onInstance(new IdType("Task", nonExistentId))
                .named("$accept")
                .withParameter(Parameters.class, "ac", new StringType(randomAccessCode))
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }

    @Test
    public void testAcceptTask_PKVWithConsent_ReturnsBundleWithConsent() {
        // TODO: Implementierung für PKV-Test mit Consent
        // Dies würde einen Task mit Flowtype 200 oder 209 und eine Consent-Resource benötigen
        LOGGER.info("PKV Consent Test noch nicht implementiert");
    }

    /**
     * Erstellt ein signiertes Test-Bundle.
     */
    private String createSignedBundleForTest(String prescriptionId, String kvnr) {
        try {
            // Erstelle temporäre XML-Datei mit angepasstem Bundle
            String bundleXml = loadAndAdaptKbvBundleXml(prescriptionId);
            
            // Erstelle temporäre Datei
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-bundle-", ".xml");
            java.nio.file.Files.write(tempFile, bundleXml.getBytes(StandardCharsets.UTF_8));
            
            // Signiere das Bundle mit dem Fachdiensttool
            RestTemplate restTemplate = ca.uhn.fhir.jpa.starter.custom.util.TestSslUtils.createTrustAllRestTemplate();
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            // Nutze File statt String, wie in FachdienstToolIntegrationTest
            body.add("kbvBundleFromFile", new org.springframework.core.io.FileSystemResource(tempFile.toFile()));
            body.add("kbvBundleAsString", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            String signUrl = String.format("https://localhost:%d/signDocumentWithTool",
                ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig.startErpServiceContainer().getMappedPort(3001));
            
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
        
        // Ersetze die PrescriptionID im Bundle mit der aus dem erstellten Task
        // Suche nach dem Pattern mit flexiblem Whitespace
        String oldPattern = "(<system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"\\s*/>[\\s\\n\\r]*<value value=\")[^\"]+(\")";
        String replacement = "$1" + prescriptionId + "$2";
        bundleXml = bundleXml.replaceAll(oldPattern, replacement);
        
        // Ersetze authoredOn mit heutigem Datum
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bundleXml = bundleXml.replaceAll(
            "<authoredOn value=\"[^\"]+\"\\s*/>",
            "<authoredOn value=\"" + today + "\"/>"
        );
        
        return bundleXml;
    }

    /**
     * Hilfsklasse für Test-Daten.
     */
    private static class TaskTestData {
        final Task task;
        final String accessCode;
        final String prescriptionId;

        TaskTestData(Task task, String accessCode, String prescriptionId) {
            this.task = task;
            this.accessCode = accessCode;
            this.prescriptionId = prescriptionId;
        }
    }
}