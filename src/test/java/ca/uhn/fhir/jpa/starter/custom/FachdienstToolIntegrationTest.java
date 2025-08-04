package ca.uhn.fhir.jpa.starter.custom;

import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import ca.uhn.fhir.jpa.starter.custom.util.TestSslUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersConfig.FullStackInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FachdienstToolIntegrationTest {

    private RestTemplate restTemplate;
    private String erpServiceUrl;
    private GenericContainer<?> erpServiceContainer;

    @BeforeAll
    public void setup() throws Exception {
        // Create RestTemplate that accepts self-signed certificates (for test purposes only)
        restTemplate = TestSslUtils.createTrustAllRestTemplate();
        
        // Start ERP service container and get URL
        erpServiceContainer = TestcontainersConfig.startErpServiceContainer();
        erpServiceUrl = String.format("https://localhost:%d", erpServiceContainer.getMappedPort(3001));
    }

    @Test
    public void testSignDocumentWithTool() throws IOException {
        // Prepare the multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Load the test XML file
        ClassPathResource fileResource = new ClassPathResource("erp-fhir-examples/KBV_PR_FOR_Patient_example.xml");
        body.add("kbvBundleFromFile", fileResource);
        body.add("kbvBundleAsString", "");

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("accept", "*/*");

        // Create the request
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // Make the request
        String signUrl = erpServiceUrl + "/signDocumentWithTool";
        ResponseEntity<String> response = restTemplate.postForEntity(signUrl, request, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().startsWith("MII"), "Response should be a base64 encoded signature");
        
        // Store the signature for verification test
        String signature = response.getBody();
        
        // Now test verification
        testVerifyDocumentWithTool(signature);
    }

    private void testVerifyDocumentWithTool(String signature) {
        // Prepare the verification request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "*/*");

        HttpEntity<String> request = new HttpEntity<>(signature, headers);

        // Make the request
        String verifyUrl = erpServiceUrl + "/verifyDocumentWithTool";
        ResponseEntity<String> response = restTemplate.postForEntity(verifyUrl, request, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        // The response should contain the original XML content
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("Patient"), "Response should contain the original Patient XML");
        assertTrue(responseBody.contains("93866fdc-3e50-4902-a7e9-891b54737b5e"), "Response should contain the patient ID");
        assertTrue(responseBody.contains("Königsstein"), "Response should contain the patient name");
    }

    @Test
    public void testSignAndVerifyLargerDocument() throws IOException {
        // Test with a larger FHIR bundle if available
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Create a simple Bundle with the Patient resource
        String bundleXml = """
            <Bundle xmlns="http://hl7.org/fhir">
                <id value="test-bundle-001"/>
                <type value="document"/>
                <entry>
                    <resource>
                        %s
                    </resource>
                </entry>
            </Bundle>
            """.formatted(Files.readString(Path.of("src/test/resources/erp-fhir-examples/KBV_PR_FOR_Patient_example.xml")));
        
        // Create a temporary file with the bundle content
        Path tempFile = Files.createTempFile("test-bundle", ".xml");
        Files.writeString(tempFile, bundleXml);
        
        body.add("kbvBundleFromFile", new org.springframework.core.io.FileSystemResource(tempFile.toFile()));
        body.add("kbvBundleAsString", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("accept", "*/*");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // Sign the document
        String signUrl = erpServiceUrl + "/signDocumentWithTool";
        ResponseEntity<String> signResponse = restTemplate.postForEntity(signUrl, request, String.class);
        
        assertNotNull(signResponse);
        assertEquals(200, signResponse.getStatusCodeValue());
        String signature = signResponse.getBody();
        assertNotNull(signature);

        // Verify the document
        HttpHeaders verifyHeaders = new HttpHeaders();
        verifyHeaders.setContentType(MediaType.APPLICATION_JSON);
        verifyHeaders.set("accept", "*/*");

        HttpEntity<String> verifyRequest = new HttpEntity<>(signature, verifyHeaders);
        String verifyUrl = erpServiceUrl + "/verifyDocumentWithTool";
        ResponseEntity<String> verifyResponse = restTemplate.postForEntity(verifyUrl, verifyRequest, String.class);

        assertNotNull(verifyResponse);
        assertEquals(200, verifyResponse.getStatusCodeValue());
        String verifiedContent = verifyResponse.getBody();
        assertTrue(verifiedContent.contains("Bundle"), "Verified content should contain Bundle");
        assertTrue(verifiedContent.contains("test-bundle-001"), "Verified content should contain bundle ID");
        
        // Clean up
        Files.deleteIfExists(tempFile);
    }
}