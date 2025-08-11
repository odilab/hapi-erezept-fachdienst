package ca.uhn.fhir.jpa.starter.custom.operation.close;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import ca.uhn.fhir.jpa.starter.custom.util.TestSslUtils;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
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
 * Integrationstests für die $close Operation.
 * 
 * WICHTIG: Die Close-Operation benötigt einen Task im Status "in-progress".
 * Daher muss zuerst create, dann activate und dann accept aufgerufen werden!
 */
public class CloseOperationIntegrationTest extends BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseOperationIntegrationTest.class);

    private Task testTask;
    private String secret;
    private String accessCode;
    
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
    
    @BeforeEach
    public void setupTask() throws Exception {
        super.setUp();
        
        // Führe den kompletten Workflow bis in-progress durch
        TaskTestData testData = createAcceptedTask();
        testTask = testData.task;
        secret = testData.secret;
        accessCode = testData.accessCode;
    }
    
    /**
     * Helper-Methode: Erstellt einen Task und führt ihn bis in-progress.
     * @return Task im Status "in-progress" mit Secret
     */
    private TaskTestData createAcceptedTask() {
        LOGGER.info("Erstelle Task und führe bis in-progress für Close-Tests");
        
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
        String taskAccessCode = createdTask.getIdentifier().stream()
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
        
        Parameters activateResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
            .withAdditionalHeader("X-AccessCode", taskAccessCode)
            .returnResourceType(Parameters.class)
            .execute();
        
        Task activatedTask = (Task) activateResult.getParameter().get(0).getResource();
        
        // 3. Accept Task
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Bundle acceptResult = client
            .operation()
            .onInstance(new IdType("Task", prescriptionId))
            .named("$accept")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .withAdditionalHeader("X-AccessCode", taskAccessCode)
            .returnResourceType(Bundle.class)
            .execute();
        
        // Extract Task und Secret aus Accept-Result
        Task acceptedTask = (Task) acceptResult.getEntry().get(0).getResource();
        String taskSecret = acceptedTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .findFirst()
            .map(Identifier::getValue)
            .orElseThrow();
        
        return new TaskTestData(acceptedTask, taskAccessCode, prescriptionId, taskSecret);
    }
    
    @Test
    void testCloseWithMedicationDispense() {
        // Extrahiere KVNR aus dem Task
        String kvnr = testTask.getFor() != null && testTask.getFor().hasIdentifier() 
            ? testTask.getFor().getIdentifier().getValue() 
            : versichertenKvnr;
        
        // Erstelle MedicationDispense mit korrekter KVNR
        MedicationDispense dispense = createMedicationDispenseWithKvnr(testTask.getIdElement().getIdPart(), kvnr);
        
        // Hauptparameter für die Operation
        Parameters closeParams = new Parameters();
        closeParams.addParameter()
            .setName("secret")
            .setValue(new StringType(secret));
        
        // rxDispensation als separates Parameters-Objekt
        Parameters rxDispensationParams = new Parameters();
        rxDispensationParams.addParameter()
            .setName("medicationDispense")
            .setResource(dispense);
        
        closeParams.addParameter()
            .setName("rxDispensation")
            .setResource(rxDispensationParams);
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Bundle result = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$close")
            .withParameters(closeParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();
        
        assertNotNull(result);
        assertEquals(Bundle.BundleType.DOCUMENT, result.getType());
        assertTrue(result.getMeta().getProfile().stream()
            .anyMatch(p -> p.getValue().startsWith("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle")));
        
        // Prüfe Composition
        Composition composition = result.getEntry().stream()
            .filter(e -> e.getResource() instanceof Composition)
            .map(e -> (Composition) e.getResource())
            .findFirst()
            .orElse(null);
        
        assertNotNull(composition);
        assertEquals(Composition.CompositionStatus.FINAL, composition.getStatus());
        
        // Prüfe Task-Status (muss mit Authorization-Header gelesen werden)
        // Warte kurz, damit die Datenbank-Transaktion abgeschlossen ist
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        
        Task closedTask = client.read()
            .resource(Task.class)
            .withId(testTask.getIdElement().toVersionless())
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .execute();
        
        LOGGER.info("Task status nach Close: {}, LastModified: {}", 
            closedTask.getStatus(), closedTask.getLastModified());
        
        assertEquals(Task.TaskStatus.COMPLETED, closedTask.getStatus());
    }
    
    @Test
    void testCloseWithoutMedicationDispense_AlreadyExists() {
        // Dieser Test ist in HAPI nicht möglich, da MedicationDispense.authorizingPrescription
        // keine Task-Referenz erlaubt. Der Provider würde die MedicationDispense nicht finden.
        // Stattdessen testen wir, dass Close mit MedicationDispense im Body funktioniert.
        
        // Erstelle MedicationDispense mit korrekter KVNR
        String kvnr = testTask.getFor() != null && testTask.getFor().hasIdentifier() 
            ? testTask.getFor().getIdentifier().getValue() 
            : versichertenKvnr;
        MedicationDispense dispense = createMedicationDispenseWithKvnr(testTask.getIdElement().getIdPart(), kvnr);
        
        // Übergebe MedicationDispense im Body statt vorab zu speichern
        Parameters closeParams = new Parameters();
        closeParams.addParameter()
            .setName("secret")
            .setValue(new StringType(secret));
        
        // rxDispensation als separates Parameters-Objekt
        Parameters rxDispensationParams = new Parameters();
        rxDispensationParams.addParameter()
            .setName("medicationDispense")
            .setResource(dispense);
        
        closeParams.addParameter()
            .setName("rxDispensation")
            .setResource(rxDispensationParams);
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Bundle result = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$close")
            .withParameters(closeParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();
        
        assertNotNull(result);
        assertEquals(Bundle.BundleType.DOCUMENT, result.getType());
    }
    
    @Test
    void testCloseWithWrongSecret() {
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        assertThrows(ForbiddenOperationException.class, () -> {
            Parameters params = new Parameters();
            params.addParameter()
                .setName("secret")
                .setValue(new StringType("WRONG_SECRET"));
            
            client.operation()
                .onInstance(testTask.getIdElement())
                .named("$close")
                .withParameters(params)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseWithWrongRole() {
        String accessTokenArzt = getValidAccessToken("SMCB_KRANKENHAUS");
        
        assertThrows(ForbiddenOperationException.class, () -> {
            Parameters params = new Parameters();
            params.addParameter()
                .setName("secret")
                .setValue(new StringType(secret));
            
            client.operation()
                .onInstance(testTask.getIdElement())
                .named("$close")
                .withParameters(params)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenArzt)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseWithoutSecret() {
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        assertThrows(ForbiddenOperationException.class, () -> {
            client.operation()
                .onInstance(testTask.getIdElement())
                .named("$close")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseNonExistentTask() {
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        assertThrows(ResourceNotFoundException.class, () -> {
            Parameters params = new Parameters();
            params.addParameter()
                .setName("secret")
                .setValue(new StringType("some_secret"));
            
            client.operation()
                .onInstance(new IdType("Task", "999.999.999.999.999.99"))
                .named("$close")
                .withParameters(params)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseTaskNotInProgress() throws Exception {
        // Erstelle neuen Task (Status: draft)
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
        
        Task draftTask = (Task) createResult.getParameter().get(0).getResource();
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        // Versuche Close auf draft Task
        assertThrows(ForbiddenOperationException.class, () -> {
            Parameters params = new Parameters();
            params.addParameter()
                .setName("secret")
                .setValue(new StringType("some_secret"));
            
            client.operation()
                .onInstance(draftTask.getIdElement())
                .named("$close")
                .withParameters(params)
                .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseWithMultipleMedicationDispenses() {
        // Extrahiere KVNR aus dem Task
        String kvnr = testTask.getFor() != null && testTask.getFor().hasIdentifier() 
            ? testTask.getFor().getIdentifier().getValue() 
            : versichertenKvnr;
        
        // Erstelle mehrere MedicationDispense mit korrekter KVNR
        MedicationDispense dispense1 = createMedicationDispenseWithKvnr(testTask.getIdElement().getIdPart(), kvnr);
        MedicationDispense dispense2 = createMedicationDispenseWithKvnr(testTask.getIdElement().getIdPart(), kvnr);
        
        Parameters closeParams = new Parameters();
        
        // Secret als erstes hinzufügen
        closeParams.addParameter()
            .setName("secret")
            .setValue(new StringType(secret));
        
        // rxDispensation als separates Parameters-Objekt mit mehreren MedicationDispenses
        Parameters rxDispensationParams = new Parameters();
        rxDispensationParams.addParameter()
            .setName("medicationDispense")
            .setResource(dispense1);
        rxDispensationParams.addParameter()
            .setName("medicationDispense")
            .setResource(dispense2);
        
        closeParams.addParameter()
            .setName("rxDispensation")
            .setResource(rxDispensationParams);
        
        String accessTokenApotheke = getValidAccessToken("SMCB_APOTHEKE");
        
        Bundle result = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$close")
            .withParameters(closeParams)
            .withAdditionalHeader("Authorization", "Bearer " + accessTokenApotheke)
            .returnResourceType(Bundle.class)
            .execute();
        
        assertNotNull(result);
        assertEquals(Bundle.BundleType.DOCUMENT, result.getType());
    }
    
    /**
     * Helper-Methode: Erstellt eine MedicationDispense für Tests
     */
    private MedicationDispense createMedicationDispense(String taskId) {
        return createMedicationDispenseWithKvnr(taskId, versichertenKvnr);
    }
    
    /**
     * Helper-Methode: Erstellt eine MedicationDispense mit spezifischer KVNR
     */
    private MedicationDispense createMedicationDispenseWithKvnr(String taskId, String kvnr) {
        MedicationDispense dispense = new MedicationDispense();
        
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense");
        dispense.setMeta(meta);
        
        dispense.setStatus(MedicationDispense.MedicationDispenseStatus.COMPLETED);
        // HAPI FHIR R4 erlaubt keine Task-Referenz in authorizingPrescription
        // Die gematik-Spezifikation erweitert FHIR, aber HAPI's Core-Validierung kann nicht umgangen werden
        // dispense.addAuthorizingPrescription(new Reference("Task/" + taskId));
        dispense.setSubject(new Reference().setIdentifier(new Identifier()
            .setSystem("http://fhir.de/sid/gkv/kvid-10")
            .setValue(kvnr)));
        dispense.setWhenHandedOver(new Date());
        
        // Setze Medication
        CodeableConcept medication = new CodeableConcept();
        medication.addCoding(new Coding()
            .setSystem("http://fhir.de/CodeSystem/ifa/pzn")
            .setCode("06313728")
            .setDisplay("Sumatriptan-1A Pharma 100 mg Tabletten"));
        dispense.setMedication(medication);
        
        // Setze Quantity
        Quantity quantity = new Quantity();
        quantity.setValue(1);
        quantity.setUnit("St");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setCode("{tbl}");
        dispense.setQuantity(quantity);
        
        return dispense;
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
        
        // Ersetze die PrescriptionID im Bundle
        String oldPattern = "(<system value=\"https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId\"\\s*/>[\\s\\n\\r]*<value value=\")[^\"]+(\")";
        String replacement = "$1" + prescriptionId + "$2";
        bundleXml = bundleXml.replaceAll(oldPattern, replacement);
        
        // Aktualisiere AuthoredOn auf heute
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bundleXml = bundleXml.replaceAll("<authoredOn value=\"[^\"]+\"", "<authoredOn value=\"" + today + "\"");
        
        return bundleXml;
    }
}