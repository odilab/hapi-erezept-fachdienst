package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationInterceptorTest extends BaseProviderTest {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptorTest.class);

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    void whenNoAuthHeader_thenThrowAuthenticationException() {
        // Arrange
        RequestDetails requestDetails = Mockito.mock(RequestDetails.class);
        Mockito.when(requestDetails.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> 
            authenticationInterceptor.interceptRequest(requestDetails));
    }

    @Test
    void whenValidToken_thenStoreInRequestDetails() {
        // Arrange
        RequestDetails requestDetails = Mockito.mock(RequestDetails.class);
        String validToken = getValidAccessToken("SMCB_KRANKENHAUS");
        String authHeader = "Bearer " + validToken;
        Mockito.when(requestDetails.getHeader("Authorization")).thenReturn(authHeader);

        Map<String, Object> userData = new HashMap<>();
        Mockito.when(requestDetails.getUserData()).thenReturn((Map)userData);

        // Act
        authenticationInterceptor.interceptRequest(requestDetails);

        // Assert
        AccessToken storedToken = (AccessToken) userData.get("ACCESS_TOKEN");
        assertNotNull(storedToken, "Token sollte im RequestDetails gespeichert sein");
        assertEquals("https://idp.zentral.idp.splitdns.ti-dienste.de", storedToken.getIss());
        assertEquals("https://erp-test.zentral.erp.splitdns.ti-dienste.de/", storedToken.getAud());
        assertEquals("Krankenhaus St. KilianTEST-ONLY", storedToken.getOrganizationName());
        assertEquals("5-SMC-B-Testkarte-883110000129072", storedToken.getIdNumber());
    }

    @Test
    void whenInvalidToken_thenThrowAuthenticationException() {
        // Arrange
        RequestDetails requestDetails = Mockito.mock(RequestDetails.class);
        String invalidToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature";
        String authHeader = "Bearer " + invalidToken;
        Mockito.when(requestDetails.getHeader("Authorization")).thenReturn(authHeader);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> 
            authenticationInterceptor.interceptRequest(requestDetails));
        
        assertTrue(exception.getMessage().contains("Token Validierungsfehler"));
    }

    @Test
    void whenMalformedToken_thenThrowAuthenticationException() {
        // Arrange
        RequestDetails requestDetails = Mockito.mock(RequestDetails.class);
        String malformedToken = "not.a.jwt.token";
        String authHeader = "Bearer " + malformedToken;
        Mockito.when(requestDetails.getHeader("Authorization")).thenReturn(authHeader);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> 
            authenticationInterceptor.interceptRequest(requestDetails));
        
        assertTrue(exception.getMessage().contains("Token Validierungsfehler"));
    }
} 