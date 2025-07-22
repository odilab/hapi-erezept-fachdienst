package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.Application;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to understand the German error message "Objekt muss einen Inhalt haben"
 * This appears to be a localized validation message
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4"
})
class BundleIdGermanErrorTest {

    private static final Logger logger = LoggerFactory.getLogger(BundleIdGermanErrorTest.class);

    @Autowired
    private CustomValidator validator;

    protected FhirContext ctx;

    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
    }

    @Test
    @DisplayName("Test Bundle without id to see the error")
    void testBundleWithoutId() {
        logger.info("=== Test: Bundle without id ===");
        
        // Create a bundle WITHOUT an id
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        
        logger.info("Bundle hasId: {}", bundle.hasId());
        logger.info("Bundle getId: {}", bundle.getId());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        logger.info("Validation successful: {}", result.isSuccessful());
        
        // Look for Bundle.id errors
        result.getMessages().stream()
            .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> {
                logger.info("Bundle.id message: [{}] {} - {}", 
                    msg.getSeverity(), 
                    msg.getLocationString(), 
                    msg.getMessage()
                );
            });
    }

    @Test
    @DisplayName("Test empty Bundle with EVDGA profile")
    void testEmptyBundleWithEvdgaProfile() {
        logger.info("=== Test: Empty Bundle with EVDGA profile ===");
        
        String bundleXml = """
            <Bundle xmlns="http://hl7.org/fhir">
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, bundleXml);
        
        logger.info("Bundle hasId: {}", bundle.hasId());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Look for all errors
        result.getMessages().stream()
            .filter(msg -> msg.getSeverity().ordinal() >= 3)
            .forEach(msg -> {
                logger.info("Error: [{}] {} - {}", 
                    msg.getSeverity(), 
                    msg.getLocationString(), 
                    msg.getMessage()
                );
            });
    }

    @Test
    @DisplayName("Test locale influence on error messages")
    void testLocaleInfluence() {
        logger.info("=== Test: Locale influence ===");
        
        // Log current locale
        logger.info("Default Locale: {}", Locale.getDefault());
        logger.info("JVM user.language: {}", System.getProperty("user.language"));
        logger.info("JVM user.country: {}", System.getProperty("user.country"));
        
        // Create a bundle that violates the EVDGA profile
        String bundleXml = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, bundleXml);
        
        logger.info("Bundle id value (empty string): '{}'", bundle.getId());
        logger.info("Bundle getIdElement().getValue(): '{}'", bundle.getIdElement().getValue());
        logger.info("Bundle getIdElement().isEmpty(): {}", bundle.getIdElement().isEmpty());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Look for Bundle.id errors
        result.getMessages().stream()
            .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> {
                logger.error("Bundle.id error: [{}] {} - {}", 
                    msg.getSeverity(), 
                    msg.getLocationString(), 
                    msg.getMessage()
                );
            });
    }

    @Test
    @DisplayName("Test various id formats with EVDGA profile")
    void testVariousIdFormatsWithProfile() {
        logger.info("=== Test: Various id formats with EVDGA profile ===");
        
        String[] testCases = {
            // Empty id value
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """,
            // Whitespace only
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="   " />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """,
            // Valid id
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="valid-id-123" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """,
            // No id element at all
            """
            <Bundle xmlns="http://hl7.org/fhir">
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
            </Bundle>
            """
        };
        
        for (int i = 0; i < testCases.length; i++) {
            logger.info("\n--- Test case {} ---", i + 1);
            Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, testCases[i]);
            
            logger.info("Bundle hasId: {}", bundle.hasId());
            logger.info("Bundle getId: '{}'", bundle.getId());
            if (bundle.getIdElement() != null) {
                logger.info("IdElement value: '{}'", bundle.getIdElement().getValue());
                logger.info("IdElement isEmpty: {}", bundle.getIdElement().isEmpty());
                logger.info("IdElement hasValue: {}", bundle.getIdElement().hasValue());
            }
            
            ValidationResult result = validator.validateAndReturnResult(bundle);
            
            // Check for Bundle.id specific errors
            boolean hasIdError = result.getMessages().stream()
                .anyMatch(msg -> msg.getLocationString() != null && 
                               msg.getLocationString().contains("Bundle.id") &&
                               msg.getMessage().contains("Objekt muss einen Inhalt haben"));
            
            if (hasIdError) {
                logger.error("Test case {} has 'Objekt muss einen Inhalt haben' error!", i + 1);
            } else {
                logger.info("Test case {} - No 'Objekt muss einen Inhalt haben' error", i + 1);
            }
        }
    }
}