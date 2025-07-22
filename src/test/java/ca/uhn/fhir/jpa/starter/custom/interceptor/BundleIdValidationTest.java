package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.Application;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to reproduce and understand the Bundle.id validation error:
 * "Bundle.id: Objekt muss einen Inhalt haben"
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4"
})
class BundleIdValidationTest {

    private static final Logger logger = LoggerFactory.getLogger(BundleIdValidationTest.class);

    @Autowired
    private CustomValidator validator;

    @LocalServerPort
    protected int port;

    protected FhirContext ctx;

    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
    }

    @Test
    @DisplayName("Test 1: Validate minimal Bundle with id - programmatically created")
    void testMinimalBundleWithIdProgrammatic() {
        logger.info("=== Test 1: Programmatically created Bundle with id ===");
        
        // Create a minimal Bundle with just an id
        Bundle bundle = new Bundle();
        bundle.setId("test-bundle-id");
        bundle.setType(Bundle.BundleType.DOCUMENT);
        
        logger.info("Bundle id element: {}", bundle.getId());
        logger.info("Bundle hasId: {}", bundle.hasId());
        logger.info("Bundle getIdElement: {}", bundle.getIdElement());
        logger.info("Bundle getIdElement.getValue: {}", bundle.getIdElement().getValue());
        logger.info("Bundle getIdElement.getIdPart: {}", bundle.getIdElement().getIdPart());
        
        // Validate the bundle
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Log all validation messages
        logger.info("Validation result - successful: {}", result.isSuccessful());
        logger.info("Number of messages: {}", result.getMessages().size());
        
        result.getMessages().forEach(msg -> {
            logger.info("[{}] {} - {}", 
                msg.getSeverity(), 
                msg.getLocationString(), 
                msg.getMessage()
            );
        });
        
        // Check for the specific error
        boolean hasIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getMessage().contains("Objekt muss einen Inhalt haben") && 
                           msg.getLocationString().contains("Bundle.id"));
        
        if (hasIdError) {
            logger.error("Found 'Objekt muss einen Inhalt haben' error for Bundle.id!");
        }
    }

    @Test
    @DisplayName("Test 2: Validate minimal Bundle from XML string (matching EVDGA structure)")
    void testMinimalBundleFromXml() {
        logger.info("=== Test 2: Bundle from XML string ===");
        
        String bundleXml = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="evdga-bundle-test" />
              <type value="document" />
              <timestamp value="2024-01-01T00:00:00Z" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, bundleXml);
        
        logger.info("Parsed Bundle id: {}", bundle.getId());
        logger.info("Parsed Bundle hasId: {}", bundle.hasId());
        logger.info("Parsed Bundle getIdElement: {}", bundle.getIdElement());
        logger.info("Parsed Bundle getIdElement.getValue: {}", bundle.getIdElement().getValue());
        
        // Validate the bundle
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Log all validation messages
        logger.info("Validation result - successful: {}", result.isSuccessful());
        logger.info("Number of messages: {}", result.getMessages().size());
        
        result.getMessages().forEach(msg -> {
            logger.info("[{}] {} - {}", 
                msg.getSeverity(), 
                msg.getLocationString(), 
                msg.getMessage()
            );
        });
        
        // Check for the specific error
        boolean hasIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getMessage().contains("Objekt muss einen Inhalt haben") && 
                           msg.getLocationString().contains("Bundle.id"));
        
        if (hasIdError) {
            logger.error("Found 'Objekt muss einen Inhalt haben' error for Bundle.id!");
        }
    }

    @Test
    @DisplayName("Test 3: Validate Bundle with different id formats")
    void testBundleWithDifferentIdFormats() {
        logger.info("=== Test 3: Testing different id formats ===");
        
        // Test various id formats
        String[] idFormats = {
            "simple-id",
            "123",
            "test.bundle.id",
            "test-bundle-id-with-dashes",
            "TEST_BUNDLE_ID_WITH_UNDERSCORES",
            "urn:uuid:12345678-1234-1234-1234-123456789012"
        };
        
        for (String id : idFormats) {
            logger.info("\n--- Testing id format: {} ---", id);
            
            Bundle bundle = new Bundle();
            bundle.setId(id);
            bundle.setType(Bundle.BundleType.DOCUMENT);
            bundle.setTimestamp(new java.util.Date());
            
            ValidationResult result = validator.validateAndReturnResult(bundle);
            
            boolean hasIdError = result.getMessages().stream()
                .anyMatch(msg -> msg.getMessage().contains("Objekt muss einen Inhalt haben") && 
                               msg.getLocationString().contains("Bundle.id"));
            
            if (hasIdError) {
                logger.error("Id '{}' causes 'Objekt muss einen Inhalt haben' error!", id);
            } else {
                logger.info("Id '{}' validated successfully", id);
            }
        }
    }

    @Test
    @DisplayName("Test 4: Compare Bundle.id with other resource id handling")
    void testCompareWithOtherResourceIds() {
        logger.info("=== Test 4: Comparing Bundle.id with Patient.id ===");
        
        // Test Bundle
        Bundle bundle = new Bundle();
        bundle.setId("test-id");
        bundle.setType(Bundle.BundleType.DOCUMENT);
        
        logger.info("Bundle IdElement class: {}", bundle.getIdElement().getClass().getName());
        logger.info("Bundle IdElement isEmpty: {}", bundle.getIdElement().isEmpty());
        logger.info("Bundle IdElement hasValue: {}", bundle.getIdElement().hasValue());
        
        // Test Patient for comparison
        org.hl7.fhir.r4.model.Patient patient = new org.hl7.fhir.r4.model.Patient();
        patient.setId("test-id");
        
        logger.info("Patient IdElement class: {}", patient.getIdElement().getClass().getName());
        logger.info("Patient IdElement isEmpty: {}", patient.getIdElement().isEmpty());
        logger.info("Patient IdElement hasValue: {}", patient.getIdElement().hasValue());
        
        // Validate both
        ValidationResult bundleResult = validator.validateAndReturnResult(bundle);
        ValidationResult patientResult = validator.validateAndReturnResult(patient);
        
        logger.info("Bundle validation errors: {}", 
            bundleResult.getMessages().stream()
                .filter(msg -> msg.getSeverity().ordinal() >= 3)
                .count()
        );
        
        logger.info("Patient validation errors: {}", 
            patientResult.getMessages().stream()
                .filter(msg -> msg.getSeverity().ordinal() >= 3)
                .count()
        );
    }

    @Test
    @DisplayName("Test 5: Validate actual EVDGA Bundle snippet")
    void testActualEvdgaBundleSnippet() {
        logger.info("=== Test 5: Actual EVDGA Bundle snippet ===");
        
        String evdgaSnippet = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="evdga-bundle-gesetzliche-krankenversicherung" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <identifier>
                <system value="https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId" />
                <value value="162.100.000.000.027.75" />
              </identifier>
              <type value="document" />
              <timestamp value="2026-03-26T13:12:00Z" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, evdgaSnippet);
        
        logger.info("EVDGA Bundle id: {}", bundle.getId());
        logger.info("EVDGA Bundle profile: {}", bundle.getMeta().getProfile().get(0).getValue());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        logger.info("Validation successful: {}", result.isSuccessful());
        
        // Log all messages
        result.getMessages().forEach(msg -> {
            logger.info("[{}] {} - {}", 
                msg.getSeverity(), 
                msg.getLocationString(), 
                msg.getMessage()
            );
        });
        
        // Specifically check for Bundle.id error
        result.getMessages().stream()
            .filter(msg -> msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> {
                logger.error("Bundle.id specific error: [{}] {}", msg.getSeverity(), msg.getMessage());
            });
    }

    @Test
    @DisplayName("Test 6: Test Bundle with and without namespace")
    void testBundleWithAndWithoutNamespace() {
        logger.info("=== Test 6: Bundle with and without namespace ===");
        
        // Without namespace declaration
        String bundleWithoutNs = """
            <Bundle>
              <id value="test-bundle" />
              <type value="document" />
            </Bundle>
            """;
        
        // With namespace declaration
        String bundleWithNs = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="test-bundle" />
              <type value="document" />
            </Bundle>
            """;
        
        try {
            logger.info("--- Testing Bundle WITHOUT namespace ---");
            Bundle bundle1 = ctx.newXmlParser().parseResource(Bundle.class, bundleWithoutNs);
            ValidationResult result1 = validator.validateAndReturnResult(bundle1);
            
            result1.getMessages().stream()
                .filter(msg -> msg.getLocationString().contains("Bundle.id"))
                .forEach(msg -> {
                    logger.info("Without NS - [{}] {} - {}", msg.getSeverity(), msg.getLocationString(), msg.getMessage());
                });
        } catch (Exception e) {
            logger.error("Failed to parse Bundle without namespace: {}", e.getMessage());
        }
        
        logger.info("\n--- Testing Bundle WITH namespace ---");
        Bundle bundle2 = ctx.newXmlParser().parseResource(Bundle.class, bundleWithNs);
        ValidationResult result2 = validator.validateAndReturnResult(bundle2);
        
        result2.getMessages().stream()
            .filter(msg -> msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> {
                logger.info("With NS - [{}] {} - {}", msg.getSeverity(), msg.getLocationString(), msg.getMessage());
            });
    }

    @Test
    @DisplayName("Test 7: Direct IdType manipulation")
    void testDirectIdTypeManipulation() {
        logger.info("=== Test 7: Direct IdType manipulation ===");
        
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        
        // Try different ways to set the id
        IdType idType = new IdType();
        idType.setValue("test-bundle-id");
        bundle.setIdElement(idType);
        
        logger.info("IdType value: {}", idType.getValue());
        logger.info("IdType idPart: {}", idType.getIdPart());
        logger.info("IdType hasIdPart: {}", idType.hasIdPart());
        logger.info("IdType isEmpty: {}", idType.isEmpty());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        result.getMessages().stream()
            .filter(msg -> msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> {
                logger.error("Bundle.id error: [{}] {}", msg.getSeverity(), msg.getMessage());
            });
    }
}