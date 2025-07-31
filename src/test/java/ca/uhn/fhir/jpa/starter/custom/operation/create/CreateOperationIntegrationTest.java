package ca.uhn.fhir.jpa.starter.custom.operation.create;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Haupttest-Klasse für die $create Operation mit echten Testcontainern.
 * 
 * Diese Tests verwenden KEINE Mocks und laufen gegen echte Services:
 * - IDP-Server (Testcontainer) für Authentifizierung
 * - ERP-Service (Testcontainer) für Access Token Generation
 * - HAPI FHIR Server mit der implementierten Create-Operation
 * 
 * Getestet werden:
 * - Task-Erstellung mit verschiedenen FlowTypes (160, 169, 200, 209, 210)
 * - Fehlerbehandlung bei ungültigen oder fehlenden Parametern
 * - Authentifizierung und Autorisierung
 * - Eindeutigkeit von Prescription IDs und Access Codes
 */
public class CreateOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOperationIntegrationTest.class);


    @Test
    public void testCreateTask_WithValidFlowType160_ReturnsTaskWithDraftStatus() {
        LOGGER.info("Starte Test: testCreateTask_WithValidFlowType160_ReturnsTaskWithDraftStatus");
        
        // Arrange
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160")
                .setDisplay("Muster 16 (Apothekenpflichtige Arzneimittel)"));

        try {
            // Hole Access Token
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            LOGGER.info("Access Token erfolgreich erhalten");

            // Act
            Task result = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Task.class)
                .execute();

            // Assert
            assertNotNull(result);
            assertNotNull(result.getIdElement().getIdPart());
            assertEquals(Task.TaskStatus.DRAFT, result.getStatus());
            assertEquals(Task.TaskIntent.ORDER, result.getIntent());
            
            // Prüfe Prescription ID
            boolean hasPrescriptionId = result.getIdentifier().stream()
                .anyMatch(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId".equals(id.getSystem()));
            assertTrue(hasPrescriptionId, "Task sollte eine Prescription ID haben");
            
            String prescriptionId = result.getIdentifier().stream()
                .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId".equals(id.getSystem()))
                .findFirst()
                .map(Identifier::getValue)
                .orElse("");
            assertTrue(prescriptionId.startsWith("160."), "Prescription ID sollte mit '160.' beginnen");
            
            // Prüfe Access Code
            boolean hasAccessCode = result.getIdentifier().stream()
                .anyMatch(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()));
            assertTrue(hasAccessCode, "Task sollte einen Access Code haben");
            
            String accessCode = result.getIdentifier().stream()
                .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
                .findFirst()
                .map(Identifier::getValue)
                .orElse("");
            assertEquals(64, accessCode.length(), "Access Code sollte 64 Zeichen lang sein (256 Bit hex)");
            
            // Prüfe FlowType Extension
            boolean hasFlowType = result.getExtension().stream()
                .anyMatch(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType".equals(ext.getUrl()));
            assertTrue(hasFlowType, "Task sollte FlowType Extension haben");
            
            // Prüfe PerformerType
            assertEquals(1, result.getPerformerType().size());
            assertTrue(result.getPerformerType().get(0).getCoding().stream()
                .anyMatch(coding -> "urn:oid:1.2.276.0.76.4.54".equals(coding.getCode())));
            
            LOGGER.info("Task erfolgreich erstellt mit ID: {}", result.getIdElement().getIdPart());
            
        } catch (Exception e) {
            LOGGER.error("Fehler im Test: ", e);
            fail("Test fehlgeschlagen: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_WithDifferentFlowTypes() {
        LOGGER.info("Teste verschiedene FlowTypes");
        
        String[] validFlowTypes = {"160", "169", "200", "209", "210"};
        
        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            for (String flowType : validFlowTypes) {
                Parameters inParams = new Parameters();
                inParams.addParameter()
                    .setName("workflowType")
                    .setValue(new Coding()
                        .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                        .setCode(flowType));
                
                Task result = client
                    .operation()
                    .onType(Task.class)
                    .named("$create")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .returnResourceType(Task.class)
                    .execute();
                
                assertNotNull(result);
                assertEquals(Task.TaskStatus.DRAFT, result.getStatus());
                
                String prescriptionId = getPrescriptionId(result);
                assertTrue(prescriptionId.startsWith(flowType + "."), 
                          "Prescription ID sollte mit '" + flowType + ".' beginnen");
                
                LOGGER.info("FlowType {} erfolgreich getestet", flowType);
            }
            
        } catch (Exception e) {
            LOGGER.error("Fehler im Test: ", e);
            fail("Test fehlgeschlagen: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_WithInvalidFlowType_ThrowsException() {
        LOGGER.info("Teste ungültigen FlowType");
        
        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            Parameters inParams = new Parameters();
            inParams.addParameter()
                .setName("workflowType")
                .setValue(new Coding()
                    .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                    .setCode("999")); // Ungültiger FlowType
            
            assertThrows(BaseServerResponseException.class, () -> {
                client
                    .operation()
                    .onType(Task.class)
                    .named("$create")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .returnResourceType(Task.class)
                    .execute();
            });
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Setup: ", e);
            fail("Test-Setup fehlgeschlagen: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_WithMissingWorkflowType_ThrowsException() {
        LOGGER.info("Starte Test: testCreateTask_WithMissingWorkflowType_ThrowsException");
        
        // Arrange - Leere Parameter
        Parameters inParams = new Parameters();

        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            // Act & Assert
            assertThrows(BaseServerResponseException.class, () -> {
                client
                    .operation()
                    .onType(Task.class)
                    .named("$create")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .returnResourceType(Task.class)
                    .execute();
            });
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Abrufen des Access Tokens: ", e);
            fail("Test-Setup fehlgeschlagen: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_WithoutAuthorization_Returns401() {
        LOGGER.info("Starte Test: testCreateTask_WithoutAuthorization_Returns401");
        
        // Arrange
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160"));

        // Act & Assert
        BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
            client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .returnResourceType(Task.class)
                .execute();
        });
        
        assertEquals(401, exception.getStatusCode(), "Sollte 401 Unauthorized zurückgeben");
    }

    @Test
    public void testCreateTask_MultipleCreations_GenerateUniqueIds() {
        LOGGER.info("Starte Test: testCreateTask_MultipleCreations_GenerateUniqueIds");
        
        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            // Arrange
            Parameters inParams = new Parameters();
            inParams.addParameter()
                .setName("workflowType")
                .setValue(new Coding()
                    .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                    .setCode("160"));

            // Act - Erstelle 3 Tasks
            Task task1 = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Task.class)
                .execute();

            Task task2 = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Task.class)
                .execute();

            Task task3 = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Task.class)
                .execute();

            // Assert - Alle IDs sollten unterschiedlich sein
            assertNotEquals(task1.getIdElement().getIdPart(), task2.getIdElement().getIdPart());
            assertNotEquals(task2.getIdElement().getIdPart(), task3.getIdElement().getIdPart());
            assertNotEquals(task1.getIdElement().getIdPart(), task3.getIdElement().getIdPart());

            // Prüfe Prescription IDs
            String prescriptionId1 = getPrescriptionId(task1);
            String prescriptionId2 = getPrescriptionId(task2);
            String prescriptionId3 = getPrescriptionId(task3);
            
            assertNotEquals(prescriptionId1, prescriptionId2);
            assertNotEquals(prescriptionId2, prescriptionId3);
            assertNotEquals(prescriptionId1, prescriptionId3);

            // Prüfe Access Codes
            String accessCode1 = getAccessCode(task1);
            String accessCode2 = getAccessCode(task2);
            String accessCode3 = getAccessCode(task3);
            
            assertNotEquals(accessCode1, accessCode2);
            assertNotEquals(accessCode2, accessCode3);
            assertNotEquals(accessCode1, accessCode3);
            
            LOGGER.info("Alle 3 Tasks erfolgreich mit eindeutigen IDs erstellt");
            
        } catch (Exception e) {
            LOGGER.error("Fehler im Test: ", e);
            fail("Test fehlgeschlagen: " + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_AndVerifyExistsInDatabase() {
        LOGGER.info("Teste Task-Erstellung und Verifizierung in der Datenbank");
        
        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            // Arrange
            Parameters inParams = new Parameters();
            inParams.addParameter()
                .setName("workflowType")
                .setValue(new Coding()
                    .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                    .setCode("160")
                    .setDisplay("Muster 16 (Apothekenpflichtige Arzneimittel)"));
            
            // Act - Task erstellen
            Task createdTask = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Task.class)
                .execute();
            
            // Assert - Task wurde erstellt
            assertNotNull(createdTask);
            assertNotNull(createdTask.getIdElement().getIdPart());
            String taskId = createdTask.getIdElement().getIdPart();
            String prescriptionId = getPrescriptionId(createdTask);
            
            LOGGER.info("Task erstellt mit ID: {} und Prescription ID: {}", taskId, prescriptionId);
            
            // Act - Task aus der Datenbank abrufen (mit read Operation)
            Task retrievedTask = client
                .read()
                .resource(Task.class)
                .withId(taskId)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .execute();
            
            // Assert - Task existiert in der Datenbank
            assertNotNull(retrievedTask, "Task sollte in der Datenbank existieren");
            assertEquals(taskId, retrievedTask.getIdElement().getIdPart(), "Task ID sollte übereinstimmen");
            assertEquals(prescriptionId, getPrescriptionId(retrievedTask), "Prescription ID sollte übereinstimmen");
            assertEquals(Task.TaskStatus.DRAFT, retrievedTask.getStatus(), "Status sollte DRAFT sein");
            
            // Weitere Validierungen
            assertEquals(createdTask.getIntent(), retrievedTask.getIntent());
            assertEquals(getAccessCode(createdTask), getAccessCode(retrievedTask), "Access Code sollte übereinstimmen");
            
            // Prüfe, dass die Task auch über Search gefunden werden kann
            Bundle searchResults = client
                .search()
                .forResource(Task.class)
                .where(Task.IDENTIFIER.exactly().systemAndCode(
                    "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId", 
                    prescriptionId))
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnBundle(Bundle.class)
                .execute();
            
            assertNotNull(searchResults);
            assertEquals(1, searchResults.getEntry().size(), "Genau ein Task sollte gefunden werden");
            
            Task searchedTask = (Task) searchResults.getEntry().get(0).getResource();
            assertEquals(taskId, searchedTask.getIdElement().getIdPart(), "Gesuchter Task sollte der gleiche sein");
            
            LOGGER.info("Task erfolgreich aus der Datenbank abgerufen und über Suche gefunden");
            
            // Optional: Prüfe Metadaten
            assertNotNull(retrievedTask.getMeta());
            assertNotNull(retrievedTask.getMeta().getVersionId(), "Task sollte eine Version haben");
            assertNotNull(retrievedTask.getMeta().getLastUpdated(), "Task sollte ein LastUpdated Datum haben");
            
            LOGGER.info("Task Metadaten - Version: {}, LastUpdated: {}", 
                retrievedTask.getMeta().getVersionId(), 
                retrievedTask.getMeta().getLastUpdated());
            
        } catch (Exception e) {
            LOGGER.error("Fehler im Test: ", e);
            fail("Test fehlgeschlagen: " + e.getMessage());
        }
    }

    // Hilfsmethoden
    private String getPrescriptionId(Task task) {
        return task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElse("");
    }

    private String getAccessCode(Task task) {
        return task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElse("");
    }
}