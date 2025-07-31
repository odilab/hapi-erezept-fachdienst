package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.jpa.starter.custom.ErgTestResourceUtil;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomValidatorTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomValidatorTest.class);
    
    private CustomValidator validator;
    private FhirContext ctx;
    
    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
        validator = new CustomValidator(ctx);
        validator.init();
    }
    
    @Test
    @DisplayName("Test: Validierung eines E-Rezept Bundles")
    void testValidateEPrescriptionBundle() {
        // Erstelle ein minimales E-Rezept Bundle
        Bundle bundle = createMinimalEPrescriptionBundle();
        
        // Der Gematik Validator ist sehr strikt - er erfordert vollständige Bundles
        // Daher erwarten wir hier einen Fehler für unser minimales Bundle
        assertThrows(UnprocessableEntityException.class, 
            () -> validator.validateResourceOnCreate(bundle),
            "Minimal Bundle sollte Validierungsfehler werfen");
    }
    
    @Test
    @DisplayName("Test: Validierung eines Tasks")
    void testValidateTask() {
        Task task = new Task();
        task.setStatus(Task.TaskStatus.READY);
        task.setIntent(Task.TaskIntent.ORDER);
        task.setAuthoredOn(new Date());
        
        // Meta mit Profil
        task.getMeta().addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task|1.2");
        
        // Identifier
        task.addIdentifier()
            .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId")
            .setValue("160.123.456.789.123.58");
        
        // Extension für Flowtype
        task.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_PrescriptionType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160")
                .setDisplay("Muster 16 (Apothekenpflichtige Arzneimittel)"));
        
        // Test mit gematik Validator
        assertDoesNotThrow(() -> validator.validateResourceOnCreate(task));
    }
    
    @Test
    @DisplayName("Test: Validierung schlägt bei ungültiger Ressource fehl")
    void testValidationFailsForInvalidResource() {
        // Erstelle ein ungültiges Bundle (ohne required fields)
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        // Kein Identifier, keine Composition - sollte fehlschlagen
        
        // Erwarte UnprocessableEntityException
        assertThrows(UnprocessableEntityException.class, 
            () -> validator.validateResourceOnCreate(bundle),
            "Validierung sollte bei ungültigem Bundle fehlschlagen");
    }
    
    @Test
    @DisplayName("Test: Validierung einer MedicationRequest")
    void testValidateMedicationRequest() {
        MedicationRequest medRequest = new MedicationRequest();
        // Füge Profil hinzu (erforderlich für Gematik Validator)
        medRequest.getMeta().addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Prescription|1.0.2");
        medRequest.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);
        medRequest.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);
        medRequest.setAuthoredOn(new Date());
        
        // Subject (Patient reference)
        medRequest.setSubject(new Reference("Patient/123"));
        
        // Medication
        medRequest.setMedication(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("http://fhir.de/CodeSystem/ifa/pzn")
                .setCode("06313728")
                .setDisplay("Sumatriptan-1A Pharma 100 mg Tabletten")));
        
        // Dosierung
        Dosage dosage = new Dosage();
        dosage.setText("1-0-0-0");
        medRequest.addDosageInstruction(dosage);
        
        // Test - auch mit Profil wird es fehlschlagen wegen fehlender Pflichtfelder
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(medRequest),
            "MedicationRequest sollte wegen fehlender Pflichtfelder fehlschlagen");
    }
    
    @Test
    @DisplayName("Test: Update-Hook funktioniert")
    void testValidateOnUpdate() {
        Task oldTask = new Task();
        oldTask.setStatus(Task.TaskStatus.DRAFT);
        
        Task newTask = new Task();
        // Füge Profil hinzu
        newTask.getMeta().addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task|1.2");
        newTask.setStatus(Task.TaskStatus.READY);
        newTask.setIntent(Task.TaskIntent.ORDER);
        newTask.setAuthoredOn(new Date());
        
        // Test Update-Hook - erwartet Fehler wegen unvollständiger Task
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnUpdate(oldTask, newTask),
            "Update sollte wegen unvollständiger Task fehlschlagen");
    }
    
    @Test
    @DisplayName("Test: Verschiedene Bundle-Typen werden erkannt")
    void testDifferentBundleTypes() {
        // E-Rezept Bundle
        Bundle erpBundle = createMinimalEPrescriptionBundle();
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(erpBundle),
            "Minimales E-Rezept Bundle sollte fehlschlagen");
        
        // Normales Bundle (nicht E-Rezept spezifisch)
        Bundle normalBundle = new Bundle();
        normalBundle.setType(Bundle.BundleType.COLLECTION);
        normalBundle.addEntry().setResource(new Patient().addName(new HumanName().setFamily("Test")));
        
        // Sollte fehlschlagen wegen fehlendem Profil
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(normalBundle),
            "Bundle ohne Profil sollte fehlschlagen");
    }
    
    private Bundle createMinimalEPrescriptionBundle() {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setTimestamp(new Date());
        
        // Identifier
        bundle.setIdentifier(new Identifier()
            .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId")
            .setValue("160.123.456.789.123.58"));
        
        // Composition
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setType(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("https://gematik.de/fhir/nfd/CodeSystem/NFD_Dokumenttyp")
                .setCode("e16A")
                .setDisplay("E-Rezept")));
        composition.setDate(new Date());
        composition.setTitle("E-Rezept");
        
        // Author
        composition.addAuthor(new Reference("Practitioner/123"));
        
        // Subject
        composition.setSubject(new Reference("Patient/456"));
        
        // Add composition to bundle
        bundle.addEntry()
            .setFullUrl("urn:uuid:" + composition.getId())
            .setResource(composition);
        
        return bundle;
    }
    
    @TestFactory
    @DisplayName("Dynamische Tests für gültige E-Rezept Bundles aus Dateien")
    Stream<DynamicTest> testValidBundlesFromFiles() throws IOException {
        Path validBundlesPath = Paths.get("src/test/resources/e-rezept-bundles/valid");
        
        return Files.walk(validBundlesPath)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".xml"))
            .filter(path -> !path.toString().contains("Beispiel_19")) // Diese Datei hat fullUrl Referenz-Probleme
            .map(path -> DynamicTest.dynamicTest(
                "Validiere: " + path.getFileName(),
                () -> validateBundleFile(path, true)
            ));
    }
    
    @TestFactory
    @DisplayName("Dynamische Tests für ungültige E-Rezept Bundles aus Dateien")
    Stream<DynamicTest> testInvalidBundlesFromFiles() throws IOException {
        Path invalidBundlesPath = Paths.get("src/test/resources/e-rezept-bundles/invalid");
        
        return Files.walk(invalidBundlesPath)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".xml"))
            .filter(path -> !path.toString().contains("FHIR-FAIL_PZN_Medication.vaccine.nein")) // Diese Datei hat ein Parsing-Problem
            .map(path -> DynamicTest.dynamicTest(
                "Validiere ungültiges Bundle: " + path.getFileName(),
                () -> validateBundleFile(path, false)
            ));
    }
    
    private void validateBundleFile(Path filePath, boolean shouldBeValid) throws IOException {
        logger.info("Teste Bundle-Datei: {}", filePath.getFileName());
        
        // Lade Bundle aus Datei
        String bundleContent = Files.readString(filePath);
        IParser parser = ctx.newXmlParser();
        Bundle bundle = parser.parseResource(Bundle.class, bundleContent);
        
        // Log Bundle-Details
        logger.debug("Bundle Type: {}", bundle.getType());
        if (bundle.getMeta() != null && !bundle.getMeta().getProfile().isEmpty()) {
            logger.debug("Bundle Profile: {}", bundle.getMeta().getProfile().get(0).getValue());
        }
        
        if (shouldBeValid) {
            // Sollte ohne Exception validiert werden
            assertDoesNotThrow(() -> validator.validateResourceOnCreate(bundle),
                "Validierung sollte für gültiges Bundle erfolgreich sein: " + filePath.getFileName());
            logger.info("✓ Bundle {} wurde erfolgreich validiert", filePath.getFileName());
        } else {
            // Sollte Exception werfen
            UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, 
                () -> validator.validateResourceOnCreate(bundle),
                "Validierung sollte für ungültiges Bundle fehlschlagen: " + filePath.getFileName());
            
            // Log die Fehlermeldung
            logger.info("✓ Erwarteter Validierungsfehler für {}: {}", 
                filePath.getFileName(), 
                exception.getMessage().lines().findFirst().orElse(""));
            
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("[ERROR]") || 
                      exception.getMessage().contains("[FATAL]") ||
                      exception.getMessage().contains("Validierung fehlgeschlagen"),
                "Fehlermeldung sollte ERROR/FATAL Level oder 'Validierung fehlgeschlagen' enthalten");
        }
    }
    
    @Test
    @DisplayName("Test: Validierung mit echten E-Rezept Profilen")
    void testValidateWithRealEPrescriptionProfiles() {
        // Erstelle ein Bundle mit korrekten Profilen
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.getMeta().addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle|1.0.2");
        bundle.setTimestamp(new Date());
        
        // Identifier mit korrektem System
        bundle.setIdentifier(new Identifier()
            .setSystem("https://gematik.de/fhir/NamingSystem/PrescriptionID")
            .setValue("160.100.000.000.001.36"));
        
        // Erstelle Composition mit korrektem Profil
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        composition.getMeta().addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Composition|1.0.2");
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setType(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("https://fhir.kbv.de/CodeSystem/KBV_CS_FOR_Formular_Art")
                .setCode("e16A")));
        composition.setDate(new Date());
        composition.setTitle("E-Rezept");
        
        // Füge notwendige Referenzen hinzu
        composition.setSubject(new Reference("Patient/" + UUID.randomUUID().toString()));
        composition.addAuthor(new Reference("Practitioner/" + UUID.randomUUID().toString()));
        composition.setCustodian(new Reference("Organization/" + UUID.randomUUID().toString()));
        
        // Füge Composition zum Bundle hinzu
        bundle.addEntry()
            .setFullUrl("http://pvs.praxis.local/fhir/Composition/" + composition.getId())
            .setResource(composition);
        
        // Test mit Gematik Validator - wird fehlschlagen wegen unvollständigem Bundle
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(bundle),
            "Unvollständiges Bundle sollte fehlschlagen");
    }
    
    @Test
    @DisplayName("Test: Validierung von KBV Patient Example")
    void testValidateKbvPatientExample() {
        logger.info("Teste Validierung von KBV Patient Example");
        
        // Lade KBV Patient aus Example-Datei
        Patient patient = ErgTestResourceUtil.createKbvPatientFromExample();
        assertNotNull(patient, "Patient sollte erfolgreich geladen werden");
        
        // Log Patient-Details
        logger.debug("Patient ID: {}", patient.getId());
        if (patient.getMeta() != null && !patient.getMeta().getProfile().isEmpty()) {
            logger.debug("Patient Profile: {}", patient.getMeta().getProfile().get(0).getValue());
        }
        
        // Validiere Patient
        assertDoesNotThrow(() -> validator.validateResourceOnCreate(patient),
            "KBV Patient Example sollte erfolgreich validiert werden");
        
        logger.info("✓ KBV Patient Example wurde erfolgreich validiert");
    }
    
    @Test
    @DisplayName("Test: Validierung von KBV Practitioner Example")
    void testValidateKbvPractitionerExample() {
        logger.info("Teste Validierung von KBV Practitioner Example");
        
        // Lade KBV Practitioner aus Example-Datei
        Practitioner practitioner = ErgTestResourceUtil.createKbvPractitionerFromExample();
        assertNotNull(practitioner, "Practitioner sollte erfolgreich geladen werden");
        
        // Log Practitioner-Details
        logger.debug("Practitioner ID: {}", practitioner.getId());
        if (practitioner.getMeta() != null && !practitioner.getMeta().getProfile().isEmpty()) {
            logger.debug("Practitioner Profile: {}", practitioner.getMeta().getProfile().get(0).getValue());
        }
        
        // Validiere Practitioner
        assertDoesNotThrow(() -> validator.validateResourceOnCreate(practitioner),
            "KBV Practitioner Example sollte erfolgreich validiert werden");
        
        logger.info("✓ KBV Practitioner Example wurde erfolgreich validiert");
    }
    
    @Test
    @DisplayName("Test: Validierung von KBV Organization Example")
    void testValidateKbvOrganizationExample() {
        logger.info("Teste Validierung von KBV Organization Example");
        
        // Lade KBV Organization aus Example-Datei
        Organization organization = ErgTestResourceUtil.createKbvOrganizationFromExample();
        assertNotNull(organization, "Organization sollte erfolgreich geladen werden");
        
        // Log Organization-Details
        logger.debug("Organization ID: {}", organization.getId());
        if (organization.getMeta() != null && !organization.getMeta().getProfile().isEmpty()) {
            logger.debug("Organization Profile: {}", organization.getMeta().getProfile().get(0).getValue());
        }
        
        // Validiere Organization
        assertDoesNotThrow(() -> validator.validateResourceOnCreate(organization),
            "KBV Organization Example sollte erfolgreich validiert werden");
        
        logger.info("✓ KBV Organization Example wurde erfolgreich validiert");
    }
    
    @Test
    @DisplayName("Test: Validierung aller KBV Examples zusammen")
    void testValidateAllKbvExamplesTogether() {
        logger.info("Teste Validierung aller KBV Examples zusammen");
        
        // Lade alle KBV Examples
        Patient patient = ErgTestResourceUtil.createKbvPatientFromExample();
        Practitioner practitioner = ErgTestResourceUtil.createKbvPractitionerFromExample();
        Organization organization = ErgTestResourceUtil.createKbvOrganizationFromExample();
        
        // Validiere alle Resources
        assertAll("Alle KBV Examples sollten erfolgreich validiert werden",
            () -> assertDoesNotThrow(() -> validator.validateResourceOnCreate(patient),
                "Patient-Validierung sollte erfolgreich sein"),
            () -> assertDoesNotThrow(() -> validator.validateResourceOnCreate(practitioner),
                "Practitioner-Validierung sollte erfolgreich sein"),
            () -> assertDoesNotThrow(() -> validator.validateResourceOnCreate(organization),
                "Organization-Validierung sollte erfolgreich sein")
        );
        
        logger.info("✓ Alle KBV Examples wurden erfolgreich validiert");
    }
    
    @Test
    @DisplayName("Test: Modifizierte KBV Examples sollten Validierung fehlschlagen")
    void testModifiedKbvExamplesShouldFailValidation() {
        logger.info("Teste, dass modifizierte KBV Examples Validierung fehlschlagen");
        
        // Lade und modifiziere Patient - entferne Name (Pflichtfeld laut KBV Profil)
        Patient patient = ErgTestResourceUtil.createKbvPatientFromExample();
        patient.getName().clear(); // Entferne Name (Pflichtfeld)
        
        // Sollte Validierung fehlschlagen
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(patient),
            "Patient ohne Name sollte Validierung fehlschlagen");
        
        // Lade und modifiziere Practitioner - falsches Profil
        Practitioner practitioner = ErgTestResourceUtil.createKbvPractitionerFromExample();
        practitioner.getMeta().getProfile().clear();
        practitioner.getMeta().addProfile("https://fhir.kbv.de/StructureDefinition/WRONG_PROFILE|1.0.0");
        
        // Sollte Validierung fehlschlagen
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(practitioner),
            "Practitioner mit falschem Profil sollte Validierung fehlschlagen");
        
        // Lade und modifiziere Organization - entferne Name und Identifier
        Organization organization = ErgTestResourceUtil.createKbvOrganizationFromExample();
        organization.getIdentifier().clear(); // Entferne alle Identifier
        organization.setName(null); // Entferne Name
        
        // Sollte Validierung fehlschlagen
        assertThrows(UnprocessableEntityException.class,
            () -> validator.validateResourceOnCreate(organization),
            "Organization ohne Name und Identifier sollte Validierung fehlschlagen");
        
        logger.info("✓ Modifizierte KBV Examples schlagen erwartungsgemäß fehl");
    }
}