package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenServiceTest extends BaseProviderTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessTokenServiceTest.class);

    @Test
    void whenValidJWT_thenDecodeAndValidateSuccessfully() {
        // Arrange
        String validToken = getValidAccessToken("SMCB_KRANKENHAUS");
        String authHeader = "Bearer " + validToken;

        // Act
        AccessToken decodedToken = accessTokenService.verifyAndDecode(authHeader);

        // Assert
        assertNotNull(decodedToken, "Token sollte erfolgreich decodiert werden");
        assertEquals("https://idp.zentral.idp.splitdns.ti-dienste.de", decodedToken.getIss());
        assertEquals("https://erp-test.zentral.erp.splitdns.ti-dienste.de/", decodedToken.getAud());
        assertEquals(Profession.ARZT_KRANKENHAUS, decodedToken.getProfession());
        assertEquals("5-SMC-B-Testkarte-883110000129072", decodedToken.getIdNumber());
        assertEquals("Krankenhaus St. KilianTEST-ONLY", decodedToken.getOrganizationName());
        assertEquals("eRezeptApp", decodedToken.getClientId(), "Client-ID sollte korrekt extrahiert werden");
        assertEquals("openid e-rezept", decodedToken.getScope(), "Scope sollte korrekt extrahiert werden");
        logger.info("Token erfolgreich validiert und decodiert");
    }

    @Test
    void whenInvalidSignature_thenThrowException() {
        // Arrange
        String tokenWithInvalidSignature = "eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiYXQrSldUIiwia2lkIjoicHVrX2lkcF9zaWcifQ." + 
            "eyJwcm9mZXNzaW9uT0lEIjoiMS4yLjI3Ni4wLjc2LjQuNDkiLCJvcmdhbml6YXRpb25OYW1lIjoiS3Jhbmtlbmhh" +
            "dXMgU3QuIEtpbGlhblRFU1QtT05MWSIsImlkTnVtbWVyIjoiNS1TTUMtQi1UZXN0a2FydGUtODgzMTEwMDAwMTI5" +
            "MDcyIiwiYW1yIjpbIm1mYSIsInNjIiwicGluIl0sImlzcyI6Imh0dHBzOi8vaWRwLnplbnRyYWwuaWRwLnNwbGl0" +
            "ZG5zLnRpLWRpZW5zdGUuZGUiLCJnaXZlbl9uYW1lIjpudWxsLCJjbGllbnRfaWQiOiJlUmV6ZXB0QXBwIiwiYWNy" +
            "IjoiZ2VtYXRpay1oZWFsdGgtbG9hLWhpZ2giLCJhdWQiOiJodHRwczovL2VycC10ZXN0LnplbnRyYWwuZXJwLnNw" +
            "bGl0ZG5zLnRpLWRpZW5zdGUuZGUiLCJhenAiOiJlUmV6ZXB0QXBwIiwic2NvcGUiOiJvcGVuaWQgZS1yZXplcHQi" +
            "LCJhdXRoX3RpbWUiOjE3Mzc5ODMxNTQsImV4cCI6MTczNzk4MzQ1NCwiZmFtaWx5X25hbWUiOm51bGwsImlhdCI6" +
            "MTczNzk4MzE1NCwianRpIjoiYWY3ZGEwNzE2NWVlYTZjOCJ9." +
            "INVALID_SIGNATURE";

        String authHeader = "Bearer " + tokenWithInvalidSignature;

        // Act & Assert
        AccessTokenException exception = assertThrows(AccessTokenException.class, () ->
            accessTokenService.verifyAndDecode(authHeader));

        assertEquals(AccessTokenError.INVALID_VALUE, exception.getError());
        assertTrue(exception.getMessage().contains("Token Signatur ungültig"));
        logger.info("Ungültige Signatur erfolgreich erkannt");
    }

    @Test
    void whenInvalidAuthHeader_thenThrowException() {
        // Arrange
        String invalidHeader = "Basic dXNlcjpwYXNz";

        // Act & Assert
        AccessTokenException exception = assertThrows(AccessTokenException.class, () ->
            accessTokenService.verifyAndDecode(invalidHeader));

        assertEquals(AccessTokenError.INVALID_VALUE, exception.getError());
        assertTrue(exception.getMessage().contains("Ungültiges Authorization Header Format"));
    }
} 