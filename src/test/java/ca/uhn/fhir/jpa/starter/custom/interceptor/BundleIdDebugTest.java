package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.Application;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Debug test specifically for the EVDGA Bundle.id validation error
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4"
})
class BundleIdDebugTest {

    private static final Logger logger = LoggerFactory.getLogger(BundleIdDebugTest.class);

    @Autowired
    private CustomValidator validator;

    protected FhirContext ctx;

    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
    }

    @Test
    @DisplayName("Debug: Load and inspect EVDGA_Bundle.xml")
    void testDebugEvdgaBundle() {
        logger.info("=== Debug: EVDGA Bundle Validation Issue ===");
        
        String bundlePath = "/package/evdga/EVDGA_Bundle.xml";
        
        try {
            // Load the bundle
            IBaseResource resource = loadResourceFromClasspath(bundlePath);
            assertNotNull(resource, "Bundle konnte nicht geladen werden");
            assertTrue(resource instanceof Bundle, "Resource ist kein Bundle");
            
            Bundle bundle = (Bundle) resource;
            
            // Inspect Bundle ID details
            logger.info("Bundle Type: {}", bundle.getType());
            logger.info("Bundle hasId(): {}", bundle.hasId());
            logger.info("Bundle getId(): {}", bundle.getId());
            logger.info("Bundle getIdElement(): {}", bundle.getIdElement());
            logger.info("Bundle getIdElement().getValue(): {}", bundle.getIdElement().getValue());
            logger.info("Bundle getIdElement().getIdPart(): {}", bundle.getIdElement().getIdPart());
            logger.info("Bundle getIdElement().hasIdPart(): {}", bundle.getIdElement().hasIdPart());
            logger.info("Bundle getIdElement().isEmpty(): {}", bundle.getIdElement().isEmpty());
            logger.info("Bundle getIdElement().hasValue(): {}", bundle.getIdElement().hasValue());
            
            // Check the raw element
            logger.info("\n--- Checking raw id element ---");
            if (bundle.getIdElement() != null) {
                logger.info("IdElement class: {}", bundle.getIdElement().getClass().getName());
                logger.info("IdElement toString: {}", bundle.getIdElement().toString());
                
                // Try to get the value in different ways
                logger.info("IdElement getValueAsString(): {}", bundle.getIdElement().getValueAsString());
                logger.info("IdElement getBaseUrl(): {}", bundle.getIdElement().getBaseUrl());
                logger.info("IdElement getResourceType(): {}", bundle.getIdElement().getResourceType());
                logger.info("IdElement getVersionIdPart(): {}", bundle.getIdElement().getVersionIdPart());
            }
            
            // Serialize and re-parse to see if something changes
            logger.info("\n--- Serialization test ---");
            String serialized = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
            logger.info("Serialized Bundle (first 500 chars):\n{}", 
                serialized.substring(0, Math.min(500, serialized.length())));
            
            // Re-parse
            Bundle reparsed = ctx.newXmlParser().parseResource(Bundle.class, serialized);
            logger.info("Reparsed Bundle getId(): {}", reparsed.getId());
            logger.info("Reparsed Bundle hasId(): {}", reparsed.hasId());
            
            // Now validate and see what happens
            logger.info("\n--- Validation ---");
            ValidationResult result = validator.validateAndReturnResult(bundle);
            
            logger.info("Validation successful: {}", result.isSuccessful());
            logger.info("Number of messages: {}", result.getMessages().size());
            
            // Look specifically for Bundle.id errors
            result.getMessages().stream()
                .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
                .forEach(msg -> {
                    logger.error("Bundle.id error found:");
                    logger.error("  Location: {}", msg.getLocationString());
                    logger.error("  Message: {}", msg.getMessage());
                    logger.error("  Severity: {}", msg.getSeverity());
                });
            
            // Also check for any errors
            result.getMessages().stream()
                .filter(msg -> msg.getSeverity().ordinal() >= 3) // ERROR or FATAL
                .forEach(msg -> {
                    logger.error("Error: [{}] {} - {}", 
                        msg.getSeverity(), 
                        msg.getLocationString(), 
                        msg.getMessage()
                    );
                });
            
        } catch (Exception e) {
            logger.error("Exception while testing EVDGA bundle", e);
            fail("Fehler beim Laden/Validieren des Bundles: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test minimal Bundle matching EVDGA structure")
    void testMinimalEvdgaLikeBundle() {
        logger.info("=== Test: Minimal EVDGA-like Bundle ===");
        
        // Create a minimal bundle with the same ID format as EVDGA
        String minimalBundle = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="evdga-bundle-gesetzliche-krankenversicherung" />
              <meta>
                <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_EVDGA_Bundle|1.2" />
              </meta>
              <type value="document" />
              <timestamp value="2024-01-01T00:00:00Z" />
            </Bundle>
            """;
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, minimalBundle);
        
        logger.info("Minimal Bundle id: {}", bundle.getId());
        logger.info("Minimal Bundle hasId: {}", bundle.hasId());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Check for Bundle.id errors
        boolean hasBundleIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().contains("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        
        if (hasBundleIdError) {
            logger.error("'Objekt muss einen Inhalt haben' error found for minimal bundle!");
            result.getMessages().stream()
                .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
                .forEach(msg -> logger.error("  {}: {}", msg.getLocationString(), msg.getMessage()));
        }
    }

    @Test
    @DisplayName("Compare parsed vs programmatic Bundle")
    void testParsedVsProgrammaticBundle() {
        logger.info("=== Test: Parsed vs Programmatic Bundle ===");
        
        // Create programmatically
        Bundle programmaticBundle = new Bundle();
        programmaticBundle.setId("evdga-bundle-test");
        programmaticBundle.setType(Bundle.BundleType.DOCUMENT);
        
        // Parse from XML
        String xmlBundle = """
            <Bundle xmlns="http://hl7.org/fhir">
              <id value="evdga-bundle-test" />
              <type value="document" />
            </Bundle>
            """;
        Bundle parsedBundle = ctx.newXmlParser().parseResource(Bundle.class, xmlBundle);
        
        // Compare IDs
        logger.info("Programmatic Bundle id: {}", programmaticBundle.getId());
        logger.info("Programmatic Bundle idElement value: {}", programmaticBundle.getIdElement().getValue());
        logger.info("Programmatic Bundle idElement idPart: {}", programmaticBundle.getIdElement().getIdPart());
        
        logger.info("Parsed Bundle id: {}", parsedBundle.getId());
        logger.info("Parsed Bundle idElement value: {}", parsedBundle.getIdElement().getValue());
        logger.info("Parsed Bundle idElement idPart: {}", parsedBundle.getIdElement().getIdPart());
        
        // Validate both
        logger.info("\n--- Validating programmatic bundle ---");
        ValidationResult progResult = validator.validateAndReturnResult(programmaticBundle);
        progResult.getMessages().stream()
            .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> logger.info("Programmatic - {}: {}", msg.getLocationString(), msg.getMessage()));
        
        logger.info("\n--- Validating parsed bundle ---");
        ValidationResult parsedResult = validator.validateAndReturnResult(parsedBundle);
        parsedResult.getMessages().stream()
            .filter(msg -> msg.getLocationString() != null && msg.getLocationString().contains("Bundle.id"))
            .forEach(msg -> logger.info("Parsed - {}: {}", msg.getLocationString(), msg.getMessage()));
    }

    private IBaseResource loadResourceFromClasspath(String path) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Ressource nicht gefunden: " + path);
            }
            
            if (path.endsWith(".json")) {
                return ctx.newJsonParser().parseResource(inputStream);
            } else if (path.endsWith(".xml")) {
                return ctx.newXmlParser().parseResource(inputStream);
            } else {
                throw new RuntimeException("Unbekannte Dateiendung für: " + path);
            }
        }
    }
}