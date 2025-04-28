package ca.uhn.fhir.jpa.starter.custom.operation.vau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;

class VAUCryptoTest {

    private VAUClientCrypto clientCrypto;
    private VAUServerCrypto serverCrypto;

    @BeforeEach
    void setUp() throws Exception {
        clientCrypto = new VAUClientCrypto();
        serverCrypto = new VAUServerCrypto();
    }

    @Test
    void testEncryptionDecryption() throws Exception {
        // Test message
        String originalMessage = "Test message for VAU encryption";
        
        // Client encrypts message
        byte[] encryptedMessage = clientCrypto.encrypt(
            serverCrypto.getPublicKey(), 
            originalMessage
        );
        
        // Server decrypts message
        String decryptedMessage = serverCrypto.decryptRequest(encryptedMessage);
        
        // Verify decrypted message matches original
        assertEquals(originalMessage, decryptedMessage);
    }

    @Test
    void testResponseEncryptionDecryption() throws Exception {
        // Test response
        String originalResponse = "Test response from server";
        
        // Generate client keys for response encryption
        String requestId = clientCrypto.generateRequestId();
        SecretKeySpec responseKey = clientCrypto.generateResponseKey();
        
        // Server encrypts response
        byte[] encryptedResponse = serverCrypto.encryptResponse(
            originalResponse,
            responseKey
        );
        
        // Client decrypts response
        String decryptedResponse = clientCrypto.decrypt(
            responseKey,
            encryptedResponse
        );
        
        // Verify decrypted response matches original
        assertEquals(originalResponse, decryptedResponse);
    }
} 