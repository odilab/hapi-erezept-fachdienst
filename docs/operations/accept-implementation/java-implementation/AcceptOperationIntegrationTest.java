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
        String prescriptionId = createdTask.getIdElement().getIdPart();
        String accessCode = createdTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();

        // 2. Activate Task (würde normalerweise ein signiertes Bundle benötigen)
        // Für Tests verwenden wir ein Mock-Bundle
        Binary mockPrescription = new Binary();
        mockPrescription.setContentType("application/pkcs7-mime");
        mockPrescription.setData(createMockSignedPrescription().getBytes());

        Parameters activateParams = new Parameters();
        activateParams.addParameter()
            .setName("ePrescription")
            .setResource(mockPrescription);

        Task activatedTask = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .withAdditionalHeader("X-AccessCode", accessCode)
            .returnResourceType(Task.class)
            .execute();

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
        assertTrue(result.getEntry().size() >= 2, "Bundle sollte mindestens Task und Binary enthalten");
        
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
     * Erstellt ein Mock signiertes E-Rezept für Tests.
     */
    private String createMockSignedPrescription() {
        // Dies würde normalerweise ein echtes signiertes PKCS7/CMS Bundle sein
        // Für Tests verwenden wir einen Platzhalter
        return "MOCK_SIGNED_PRESCRIPTION_BASE64_" + UUID.randomUUID();
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