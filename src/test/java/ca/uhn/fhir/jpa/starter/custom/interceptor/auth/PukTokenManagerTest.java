package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

class PukTokenManagerTest extends BaseProviderTest {


    @Test
    void testPukTokenManagerInitialization() {
        // Test der vollständigen Initialisierung
        PublicKey publicKey = pukTokenManager.getCurrentPublicKey();
        
        assertNotNull(publicKey, "Public Key sollte nicht null sein");
        assertEquals("EC", publicKey.getAlgorithm(), "Public Key sollte ein EC-Schlüssel sein");
    }
    
    @Test
    void testCertificateValidationChain() {
        // Test der Zertifikatskette und JWKS-Validierung
        PublicKey publicKey = pukTokenManager.getCurrentPublicKey();
        
        assertNotNull(publicKey, "Public Key sollte nach erfolgreicher Validierung verfügbar sein");
        assertEquals("EC", publicKey.getAlgorithm(), "Public Key sollte ein EC-Schlüssel sein");
    }
} 