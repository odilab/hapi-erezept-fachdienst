package ca.uhn.fhir.jpa.starter.custom.operation.activate;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für die $activate Operation.
 * 
 * WICHTIG: Diese Tests benötigen einen vorher erstellten Task im Status "draft".
 * Die Tests bauen aufeinander auf:
 * 1. CreateOperationIntegrationTest erstellt Task (Status: draft)
 * 2. ActivateOperationIntegrationTest aktiviert Task (Status: ready)
 */
public class ActivateOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateOperationIntegrationTest.class);

    /**
     * Hilfsmethode: Erstellt einen Task im Status draft für Tests.
     */
    private Task createTaskForTest(String flowType) {
        LOGGER.info("Erstelle Test-Task mit FlowType: {}", flowType);
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode(flowType)
                .setDisplay("Test FlowType"));

        try {
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            Parameters result = client
                .operation()
                .onType(Task.class)
                .named("$create")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .returnResourceType(Parameters.class)
                .execute();

            Task createdTask = (Task) result.getParameter().get(0).getResource();
            LOGGER.info("Task erstellt mit ID: {}", createdTask.getIdElement().getIdPart());
            return createdTask;
            
        } catch (Exception e) {
            fail("Konnte Test-Task nicht erstellen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Hilfsmethode: Erstellt ein signiertes Test-Bundle.
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
        // Die PrescriptionID ist normalerweise in der Composition.identifier
        bundleXml = bundleXml.replaceAll(
            "<identifier>\\s*<system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"\\s*/>\\s*<value value=\"[^\"]+\"\\s*/>",
            "<identifier><system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"/><value value=\"" + prescriptionId + "\"/>"
        );
        
        // Ersetze authoredOn mit heutigem Datum
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bundleXml = bundleXml.replaceAll(
            "<authoredOn value=\"[^\"]+\"\\s*/>",
            "<authoredOn value=\"" + today + "\"/>"
        );
        
        return bundleXml;
    }
    
    /**
     * Erstellt ein minimales Test KBV Bundle XML (Fallback).
     */
    private String createTestKbvBundleXml(String prescriptionId, String kvnr) {
        return String.format("""
            <Bundle xmlns="http://hl7.org/fhir">
                <identifier>
                    <system value="https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId"/>
                    <value value="%s"/>
                </identifier>
                <type value="document"/>
                <entry>
                    <resource>
                        <Composition>
                            <status value="final"/>
                            <type>
                                <coding>
                                    <system value="https://fhir.kbv.de/CodeSystem/KBV_CS_SFHIR_KBV_FORMULAR_ART"/>
                                    <code value="e16A"/>
                                </coding>
                            </type>
                            <subject>
                                <reference value="Patient/1"/>
                            </subject>
                            <date value="2025-08-05"/>
                            <author>
                                <reference value="Practitioner/1"/>
                            </author>
                        </Composition>
                    </resource>
                </entry>
                <entry>
                    <resource>
                        <Patient>
                            <id value="1"/>
                            <identifier>
                                <type>
                                    <coding>
                                        <system value="http://fhir.de/StructureDefinition/identifier-kvid-10"/>
                                        <code value="GKV"/>
                                    </coding>
                                </type>
                                <system value="http://fhir.de/sid/gkv/kvid-10"/>
                                <value value="%s"/>
                            </identifier>
                            <name>
                                <family value="Testpatient"/>
                                <given value="Max"/>
                            </name>
                        </Patient>
                    </resource>
                </entry>
                <entry>
                    <resource>
                        <Practitioner>
                            <id value="1"/>
                            <identifier>
                                <type>
                                    <coding>
                                        <system value="http://terminology.hl7.org/CodeSystem/v2-0203"/>
                                        <code value="LANR"/>
                                    </coding>
                                </type>
                                <system value="https://fhir.kbv.de/NamingSystem/KBV_NS_Base_ANR"/>
                                <value value="838382202"/>
                            </identifier>
                            <name>
                                <family value="Testarzt"/>
                                <given value="Dr."/>
                            </name>
                        </Practitioner>
                    </resource>
                </entry>
                <entry>
                    <resource>
                        <MedicationRequest>
                            <status value="active"/>
                            <intent value="order"/>
                            <medicationCodeableConcept>
                                <coding>
                                    <system value="http://fhir.de/CodeSystem/ifa/pzn"/>
                                    <code value="00000000"/>
                                    <display value="Test-Medikament"/>
                                </coding>
                            </medicationCodeableConcept>
                            <subject>
                                <reference value="Patient/1"/>
                            </subject>
                            <authoredOn value="2025-08-05"/>
                            <requester>
                                <reference value="Practitioner/1"/>
                            </requester>
                            <dosageInstruction>
                                <text value="1-0-0-0"/>
                            </dosageInstruction>
                        </MedicationRequest>
                    </resource>
                </entry>
                <entry>
                    <resource>
                        <Coverage>
                            <status value="active"/>
                            <type>
                                <coding>
                                    <system value="http://fhir.de/CodeSystem/versicherungsart-de-basis"/>
                                    <code value="GKV"/>
                                </coding>
                            </type>
                            <beneficiary>
                                <reference value="Patient/1"/>
                            </beneficiary>
                            <payor>
                                <identifier>
                                    <system value="http://fhir.de/sid/arge-ik/iknr"/>
                                    <value value="104212059"/>
                                </identifier>
                            </payor>
                        </Coverage>
                    </resource>
                </entry>
            </Bundle>
            """, prescriptionId, kvnr);
    }

    @Test
    public void testActivateTask_WithValidSignedBundle_ReturnsActivatedTask() {
        LOGGER.info("Starte Test: testActivateTask_WithValidSignedBundle_ReturnsActivatedTask");
        
        // Arrange - Erstelle Task
        Task draftTask = createTaskForTest("160");
        String prescriptionId = draftTask.getIdentifierFirstRep().getValue();
        String accessCode = draftTask.getIdentifier().stream()
            .filter(id -> id.getSystem().contains("AccessCode"))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        // Erstelle signiertes Bundle (KVNR wird aus dem Bundle gelesen)
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        
        // Erstelle Binary mit signiertem Bundle (signedBundle ist bereits Base64)
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        try {
            // Act
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            Parameters result = client
                .operation()
                .onInstance(draftTask.getIdElement())
                .named("$activate")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .withAdditionalHeader("X-AccessCode", accessCode)
                .returnResourceType(Parameters.class)
                .execute();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getParameter().size());
            assertEquals("return", result.getParameter().get(0).getName());
            
            Task activatedTask = (Task) result.getParameter().get(0).getResource();
            assertNotNull(activatedTask);
            assertEquals(Task.TaskStatus.READY, activatedTask.getStatus());
            assertEquals(prescriptionId, activatedTask.getIdentifierFirstRep().getValue());
            
            // Prüfe KVNR (als Identifier Reference)
            assertNotNull(activatedTask.getFor());
            assertNotNull(activatedTask.getFor().getIdentifier());
            assertEquals("http://fhir.de/sid/gkv/kvid-10", activatedTask.getFor().getIdentifier().getSystem());
            assertEquals("S040464113", activatedTask.getFor().getIdentifier().getValue());
            
            // Prüfe ExpiryDate und AcceptDate
            assertNotNull(activatedTask.getRestriction());
            assertNotNull(activatedTask.getRestriction().getPeriod());
            assertNotNull(activatedTask.getRestriction().getPeriod().getEnd());
            
            boolean hasAcceptDate = activatedTask.getExtension().stream()
                .anyMatch(ext -> ext.getUrl().contains("AcceptDate"));
            assertTrue(hasAcceptDate, "Task sollte AcceptDate Extension haben");
            
            LOGGER.info("Task erfolgreich aktiviert - Status: {}", activatedTask.getStatus());
            
        } catch (Exception e) {
            fail("Unerwarteter Fehler: " + e.getMessage());
        }
    }

    @Test
    public void testActivateTask_WithWrongAccessCode_ThrowsForbidden() {
        LOGGER.info("Starte Test: testActivateTask_WithWrongAccessCode_ThrowsForbidden");
        
        // Arrange
        Task draftTask = createTaskForTest("160");
        String prescriptionId = draftTask.getIdentifierFirstRep().getValue();
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        try {
            // Act & Assert
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
                client
                    .operation()
                    .onInstance(draftTask.getIdElement())
                    .named("$activate")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .withAdditionalHeader("X-AccessCode", "WRONG_ACCESS_CODE")
                    .returnResourceType(Parameters.class)
                    .execute();
            });
            
            assertEquals(403, exception.getStatusCode());
            assertTrue(exception.getMessage().contains("Access code does not match"));
            
        } catch (Exception e) {
            fail("Unerwarteter Fehler: " + e.getMessage());
        }
    }

    @Test
    public void testActivateTask_WithTaskNotInDraftStatus_ThrowsForbidden() {
        LOGGER.info("Starte Test: testActivateTask_WithTaskNotInDraftStatus_ThrowsForbidden");
        
        // Arrange - Erstelle und aktiviere Task
        Task draftTask = createTaskForTest("160");
        String prescriptionId = draftTask.getIdentifierFirstRep().getValue();
        String accessCode = draftTask.getIdentifier().stream()
            .filter(id -> id.getSystem().contains("AccessCode"))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        // Aktiviere Task einmal
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
        
        // Erste Aktivierung
        Parameters firstResult = client
            .operation()
            .onInstance(draftTask.getIdElement())
            .named("$activate")
            .withParameters(inParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessToken)
            .withAdditionalHeader("X-AccessCode", accessCode)
            .returnResourceType(Parameters.class)
            .execute();
        
        // Hole die aktivierte Task aus dem Ergebnis
        Task activatedTask = (Task) firstResult.getParameter().get(0).getResource();

        // Act & Assert - Zweite Aktivierung sollte fehlschlagen
        BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
            client
                .operation()
                .onInstance(activatedTask.getIdElement())
                .named("$activate")
                .withParameters(inParams)
                .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                .withAdditionalHeader("X-AccessCode", accessCode)
                .returnResourceType(Parameters.class)
                .execute();
        });
        
        assertEquals(403, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Task not in status draft"));
    }

    @Test
    public void testActivateTask_WithInvalidPrescriptionId_ThrowsBadRequest() {
        LOGGER.info("Starte Test: testActivateTask_WithInvalidPrescriptionId_ThrowsBadRequest");
        
        // Arrange
        Task draftTask = createTaskForTest("160");
        String prescriptionId = draftTask.getIdentifierFirstRep().getValue();
        String accessCode = draftTask.getIdentifier().stream()
            .filter(id -> id.getSystem().contains("AccessCode"))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        // Erstelle Bundle mit falscher PrescriptionID
        String wrongPrescriptionId = "160.999.999.999.999.99";
        String signedBundle = createSignedBundleForTest(wrongPrescriptionId, versichertenKvnr);
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        try {
            // Act & Assert
            String accessToken = getValidAccessToken("SMCB_KRANKENHAUS");
            
            BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
                client
                    .operation()
                    .onInstance(draftTask.getIdElement())
                    .named("$activate")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .withAdditionalHeader("X-AccessCode", accessCode)
                    .returnResourceType(Parameters.class)
                    .execute();
            });
            
            assertEquals(422, exception.getStatusCode());
            assertTrue(exception.getMessage().contains("PrescriptionID mismatch"));
            
        } catch (Exception e) {
            fail("Unerwarteter Fehler: " + e.getMessage());
        }
    }

    @Test
    public void testActivateTask_WithUnauthorizedRole_ThrowsForbidden() {
        LOGGER.info("Starte Test: testActivateTask_WithUnauthorizedRole_ThrowsForbidden");
        
        // Arrange
        Task draftTask = createTaskForTest("160");
        String prescriptionId = draftTask.getIdentifierFirstRep().getValue();
        String accessCode = draftTask.getIdentifier().stream()
            .filter(id -> id.getSystem().contains("AccessCode"))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        String signedBundle = createSignedBundleForTest(prescriptionId, "S040464113");
        
        Binary ePrescription = new Binary();
        ePrescription.setContentType("application/pkcs7-mime");
        ePrescription.setDataElement(new Base64BinaryType(signedBundle));
        
        Parameters inParams = new Parameters();
        inParams.addParameter()
            .setName("ePrescription")
            .setResource(ePrescription);

        try {
            // Act & Assert - Verwende EGK Token (Patient) statt SMCB
            String accessToken = getValidAccessToken("EGK1");
            
            BaseServerResponseException exception = assertThrows(BaseServerResponseException.class, () -> {
                client
                    .operation()
                    .onInstance(draftTask.getIdElement())
                    .named("$activate")
                    .withParameters(inParams)
                    .withAdditionalHeader("Authorization", "Bearer " + accessToken)
                    .withAdditionalHeader("X-AccessCode", accessCode)
                    .returnResourceType(Parameters.class)
                    .execute();
            });
            
            assertEquals(403, exception.getStatusCode());
            assertTrue(exception.getMessage().contains("nur für verordnende Leistungserbringer erlaubt"));
            
        } catch (Exception e) {
            fail("Unerwarteter Fehler: " + e.getMessage());
        }
    }
}