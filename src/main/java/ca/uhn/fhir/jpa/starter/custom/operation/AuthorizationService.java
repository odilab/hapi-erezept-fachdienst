package ca.uhn.fhir.jpa.starter.custom.operation;

import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service für die Berechtigungsprüfung für E-Rezept FHIR-Operationen.
 * Vereinfachte Version für die Create-Operation.
 */
@Service
public class AuthorizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

    /**
     * Extrahiert und validiert den AccessToken aus den RequestDetails.
     * Wirft eine AuthenticationException, wenn kein gültiger Token gefunden wird.
     */
    public AccessToken validateAndExtractAccessToken(RequestDetails requestDetails) {
        Object tokenObj = requestDetails.getUserData().get("ACCESS_TOKEN");
        if (!(tokenObj instanceof AccessToken)) {
            LOGGER.warn("Kein AccessToken Objekt in RequestDetails gefunden.");
            throw new AuthenticationException("Kein gültiger Access Token gefunden");
        }
        AccessToken accessToken = (AccessToken) tokenObj;
        LOGGER.debug("AccessToken extrahiert für User ID: {}", accessToken.getIdNumber());
        return accessToken;
    }
}