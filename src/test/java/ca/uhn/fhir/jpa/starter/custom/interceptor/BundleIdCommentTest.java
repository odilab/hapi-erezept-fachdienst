package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.Application;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify if XML comments are causing the Bundle.id validation error
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4"
})
class BundleIdCommentTest {

    private static final Logger logger = LoggerFactory.getLogger(BundleIdCommentTest.class);

    @Autowired
    private CustomValidator validator;

    protected FhirContext ctx;

    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
    }

    @Test
    @DisplayName("Test Bundle with comment before id element")
    void testBundleWithCommentBeforeId() {
        logger.info("=== Test: Bundle with comment before id element ===");
        
        // This matches the EVDGA bundle structure with comment
        String bundleWithComment = """
            <Bundle xmlns="http://hl7.org/fhir">
              <!-- Beispiel-Bundle eDiGA-Verordnung -->
              <id value="evdga-bundle-gesetzliche-krankenversicherung" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
              <timestamp value="2024-01-01T00:00:00Z" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, bundleWithComment);
        
        logger.info("Bundle id: {}", bundle.getId());
        logger.info("Bundle hasId: {}", bundle.hasId());
        logger.info("Bundle getIdElement().getValue(): {}", bundle.getIdElement().getValue());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Check for Bundle.id error
        boolean hasIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        
        if (hasIdError) {
            logger.error("Bundle WITH comment has 'Objekt muss einen Inhalt haben' error!");
        } else {
            logger.info("Bundle WITH comment does NOT have the error");
        }
    }

    @Test
    @DisplayName("Test Bundle without comment before id element")
    void testBundleWithoutCommentBeforeId() {
        logger.info("=== Test: Bundle without comment before id element ===");
        
        // Same structure but without the comment
        String bundleWithoutComment = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="evdga-bundle-gesetzliche-krankenversicherung" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
              <timestamp value="2024-01-01T00:00:00Z" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, bundleWithoutComment);
        
        logger.info("Bundle id: {}", bundle.getId());
        logger.info("Bundle hasId: {}", bundle.hasId());
        logger.info("Bundle getIdElement().getValue(): {}", bundle.getIdElement().getValue());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Check for Bundle.id error
        boolean hasIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        
        if (hasIdError) {
            logger.error("Bundle WITHOUT comment has 'Objekt muss einen Inhalt haben' error!");
        } else {
            logger.info("Bundle WITHOUT comment does NOT have the error");
        }
    }

    @Test
    @DisplayName("Test various comment positions")
    void testVariousCommentPositions() {
        logger.info("=== Test: Various comment positions ===");
        
        String[] testCases = {
            // Comment after Bundle open tag
            """
            <Bundle xmlns="http://hl7.org/fhir"><!-- Comment here -->
              <id value="test-id-1" />
              <type value="document" />
            </Bundle>
            """,
            // Comment before id, with newline
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <!-- Comment here -->
              <id value="test-id-2" />
              <type value="document" />
            </Bundle>
            """,
            // Comment inside id element
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="test-id-3" /><!-- Comment here -->
              <type value="document" />
            </Bundle>
            """,
            // Multiple comments
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <!-- First comment -->
              <!-- Second comment -->
              <id value="test-id-4" />
              <type value="document" />
            </Bundle>
            """,
            // Comment with special characters
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <!-- Beispiel-Bundle für eDiGA-Verordnung (§ 31a SGB V) -->
              <id value="test-id-5" />
              <type value="document" />
            </Bundle>
            """
        };
        
        for (int i = 0; i < testCases.length; i++) {
            logger.info("\n--- Test case {} ---", i + 1);
            
            try {
                Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, testCases[i]);
                
                logger.info("Bundle id: {}", bundle.getId());
                
                ValidationResult result = validator.validateAndReturnResult(bundle);
                
                boolean hasIdError = result.getMessages().stream()
                    .anyMatch(msg -> msg.getLocationString() != null && 
                                   msg.getLocationString().equals("Bundle.id") &&
                                   msg.getMessage().contains("Objekt muss einen Inhalt haben"));
                
                if (hasIdError) {
                    logger.error("Test case {} HAS the error!", i + 1);
                } else {
                    logger.info("Test case {} - No error", i + 1);
                }
            } catch (Exception e) {
                logger.error("Test case {} failed to parse: {}", i + 1, e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test removing comment from EVDGA bundle")
    void testRemovingCommentFromEvdgaBundle() throws Exception {
        logger.info("=== Test: Removing comment from EVDGA bundle ===");
        
        // Load the original EVDGA bundle
        String bundlePath = "/package/evdga/EVDGA_Bundle.xml";
        String originalXml;
        try (var inputStream = getClass().getResourceAsStream(bundlePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + bundlePath);
            }
            originalXml = new String(inputStream.readAllBytes());
        }
        
        // Remove the comment
        String withoutComment = originalXml.replace("<!-- Beispiel-Bundle eDiGA-Verordnung -->", "");
        
        logger.info("Original XML has comment: {}", originalXml.contains("<!-- Beispiel-Bundle eDiGA-Verordnung -->"));
        logger.info("Modified XML has comment: {}", withoutComment.contains("<!-- Beispiel-Bundle eDiGA-Verordnung -->"));
        
        // Parse both versions
        Bundle originalBundle = ctx.newXmlParser().parseResource(Bundle.class, originalXml);
        Bundle modifiedBundle = ctx.newXmlParser().parseResource(Bundle.class, withoutComment);
        
        // Validate original
        logger.info("\n--- Validating original (with comment) ---");
        ValidationResult originalResult = validator.validateAndReturnResult(originalBundle);
        boolean originalHasError = originalResult.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        logger.info("Original has error: {}", originalHasError);
        
        // Validate modified
        logger.info("\n--- Validating modified (without comment) ---");
        ValidationResult modifiedResult = validator.validateAndReturnResult(modifiedBundle);
        boolean modifiedHasError = modifiedResult.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        logger.info("Modified has error: {}", modifiedHasError);
        
        if (originalHasError && !modifiedHasError) {
            logger.error("CONFIRMED: The comment is causing the validation error!");
        }
    }
}