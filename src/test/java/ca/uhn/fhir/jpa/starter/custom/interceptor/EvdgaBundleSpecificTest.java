package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.Application;
import ca.uhn.fhir.parser.DataFormatException;
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
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific test for the actual EVDGA Bundle files
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4"
})
class EvdgaBundleSpecificTest {

    private static final Logger logger = LoggerFactory.getLogger(EvdgaBundleSpecificTest.class);

    @Autowired
    private CustomValidator validator;

    protected FhirContext ctx;

    @BeforeEach
    void setUp() {
        ctx = FhirContext.forR4();
    }

    @Test
    @DisplayName("Test actual EVDGA_Bundle.xml parsing and validation")
    void testActualEvdgaBundleFile() throws Exception {
        logger.info("=== Test: Actual EVDGA_Bundle.xml file ===");
        
        String bundlePath = "/package/evdga/EVDGA_Bundle.xml";
        
        // First, let's read the raw XML to see what we're dealing with
        String rawXml = loadResourceAsString(bundlePath);
        logger.info("First 1000 chars of raw XML:\n{}", rawXml.substring(0, Math.min(1000, rawXml.length())));
        
        // Check for BOM or other encoding issues
        byte[] bytes = rawXml.getBytes(StandardCharsets.UTF_8);
        logger.info("First 10 bytes: {}", bytesToHex(bytes, 10));
        
        // Try to parse it
        try {
            Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, rawXml);
            
            logger.info("Successfully parsed bundle");
            logger.info("Bundle id: {}", bundle.getId());
            logger.info("Bundle hasId: {}", bundle.hasId());
            logger.info("Bundle getIdElement: {}", bundle.getIdElement());
            
            if (bundle.getIdElement() != null) {
                logger.info("IdElement class: {}", bundle.getIdElement().getClass());
                logger.info("IdElement value: '{}'", bundle.getIdElement().getValue());
                logger.info("IdElement idPart: '{}'", bundle.getIdElement().getIdPart());
                logger.info("IdElement isEmpty: {}", bundle.getIdElement().isEmpty());
            }
            
            // Now validate
            logger.info("\n--- Starting validation ---");
            ValidationResult result = validator.validateAndReturnResult(bundle);
            
            // Look for Bundle.id errors
            result.getMessages().stream()
                .filter(msg -> msg.getLocationString() != null && msg.getLocationString().equals("Bundle.id"))
                .forEach(msg -> {
                    logger.error("Bundle.id error: [{}] {}", msg.getSeverity(), msg.getMessage());
                });
                
        } catch (DataFormatException e) {
            logger.error("Failed to parse bundle", e);
            fail("Failed to parse bundle: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Compare EVDGA bundle with re-created version")
    void testCompareEvdgaBundleWithRecreated() throws Exception {
        logger.info("=== Test: Compare EVDGA bundle with recreated version ===");
        
        // Load original
        String bundlePath = "/package/evdga/EVDGA_Bundle.xml";
        IBaseResource originalResource = loadResourceFromClasspath(bundlePath);
        Bundle originalBundle = (Bundle) originalResource;
        
        logger.info("Original bundle id: {}", originalBundle.getId());
        
        // Create a new bundle with the same id
        Bundle newBundle = new Bundle();
        newBundle.setId(originalBundle.getIdElement().getIdPart());
        newBundle.setMeta(originalBundle.getMeta().copy());
        newBundle.setIdentifier(originalBundle.getIdentifier().copy());
        newBundle.setType(originalBundle.getType());
        newBundle.setTimestamp(originalBundle.getTimestamp());
        
        logger.info("New bundle id: {}", newBundle.getId());
        logger.info("New bundle getIdElement: {}", newBundle.getIdElement());
        
        // Validate both
        logger.info("\n--- Validating original bundle ---");
        ValidationResult originalResult = validator.validateAndReturnResult(originalBundle);
        boolean originalHasIdError = originalResult.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        logger.info("Original has 'Objekt muss einen Inhalt haben' error: {}", originalHasIdError);
        
        logger.info("\n--- Validating new bundle ---");
        ValidationResult newResult = validator.validateAndReturnResult(newBundle);
        boolean newHasIdError = newResult.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        logger.info("New has 'Objekt muss einen Inhalt haben' error: {}", newHasIdError);
        
        if (originalHasIdError && !newHasIdError) {
            logger.error("The error only occurs with the original bundle!");
            
            // Let's compare the serialized forms
            String originalSerialized = ctx.newXmlParser().setPrettyPrint(false).encodeResourceToString(originalBundle);
            String newSerialized = ctx.newXmlParser().setPrettyPrint(false).encodeResourceToString(newBundle);
            
            logger.info("Original serialized id element: {}", 
                extractIdElement(originalSerialized));
            logger.info("New serialized id element: {}", 
                extractIdElement(newSerialized));
        }
    }

    @Test
    @DisplayName("Test EVDGA Bundle manual reconstruction")
    void testEvdgaBundleManualReconstruction() {
        logger.info("=== Test: EVDGA Bundle manual reconstruction ===");
        
        // Manually create the exact XML structure from EVDGA_Bundle.xml
        String manualXml = """
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
        
        Bundle bundle = ctx.newXmlParser().parseResource(Bundle.class, manualXml);
        
        logger.info("Manual bundle id: {}", bundle.getId());
        logger.info("Manual bundle hasId: {}", bundle.hasId());
        
        // Validate
        ValidationResult result = validator.validateAndReturnResult(bundle);
        
        // Check for Bundle.id error
        boolean hasIdError = result.getMessages().stream()
            .anyMatch(msg -> msg.getLocationString() != null && 
                           msg.getLocationString().equals("Bundle.id") &&
                           msg.getMessage().contains("Objekt muss einen Inhalt haben"));
        
        if (hasIdError) {
            logger.error("Manual reconstruction also has the error!");
            result.getMessages().stream()
                .filter(msg -> msg.getLocationString() != null && msg.getLocationString().equals("Bundle.id"))
                .forEach(msg -> logger.error("  {}: {}", msg.getLocationString(), msg.getMessage()));
        } else {
            logger.info("Manual reconstruction does NOT have the error");
        }
    }

    private String loadResourceAsString(String path) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + path);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private IBaseResource loadResourceFromClasspath(String path) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + path);
            }
            
            if (path.endsWith(".json")) {
                return ctx.newJsonParser().parseResource(inputStream);
            } else if (path.endsWith(".xml")) {
                return ctx.newXmlParser().parseResource(inputStream);
            } else {
                throw new RuntimeException("Unknown file extension for: " + path);
            }
        }
    }

    private String bytesToHex(byte[] bytes, int limit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, limit); i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }

    private String extractIdElement(String xml) {
        int start = xml.indexOf("<id ");
        if (start == -1) return "not found";
        int end = xml.indexOf("/>", start);
        if (end == -1) {
            end = xml.indexOf("</id>", start);
        }
        if (end == -1) return "not found";
        return xml.substring(start, end + 2);
    }
}