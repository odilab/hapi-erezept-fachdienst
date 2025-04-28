package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Component
@Interceptor(order = 0)
public class AuthenticationInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    
    private final AccessTokenService accessTokenService;
    
    // Nur noch technische Pfade ohne Auth
    private static final Set<String> WHITELIST_PATHS = Set.of(
        "metadata",
        "$validate"
    );
    
    public AuthenticationInterceptor(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }
    
    @Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
    public void interceptRequest(RequestDetails requestDetails) {
        if (requestDetails == null) {
            LOGGER.error("RequestDetails ist null");
            throw new AuthenticationException("Interner Serverfehler: RequestDetails ist null");
        }

        String requestPath = requestDetails.getRequestPath();
        LOGGER.debug("Verarbeite Request für Pfad: {}", requestPath);
        
        // Prüfe ob der Pfad auf der Whitelist steht
        if (requestPath != null) {
            // Entferne führende und nachfolgende Slashes
            String normalizedPath = requestPath.replaceAll("^/+|/+$", "");
            
            // Prüfe nur noch die technischen Pfade
            if (WHITELIST_PATHS.contains(normalizedPath)) {
                LOGGER.debug("Pfad ist auf der Whitelist: {}", requestPath);
                return;
            }
        }

        // Hole den Authorization Header
        String authHeader = requestDetails.getHeader("Authorization");
        LOGGER.debug("Authorization Header vorhanden: {}", (authHeader != null));
        
        if (authHeader == null || authHeader.isEmpty()) {
            throw new AuthenticationException("Fehlender Authorization Header");
        }
        
        try {
            AccessToken accessToken = accessTokenService.verifyAndDecode(authHeader);
            LOGGER.debug("Token wurde validiert für Profession: {}", accessToken.getProfession());
            
            // Speichere den decodierten Token im RequestDetails für spätere Verwendung
            requestDetails.getUserData().put("ACCESS_TOKEN", accessToken);
            
            // Prüfe spezielle Berechtigungen für die Submit-Operation
            boolean isSubmitOperation = requestPath != null && 
                                      requestDetails.getOperation() != null && 
                                      requestDetails.getOperation().equals("$erechnung-submit");
            
            if (isSubmitOperation && accessToken.getProfession() == Profession.VERSICHERTER) {
                throw new AuthenticationException("Keine ausreichende Berechtigung für die Submit-Operation. Nur Leistungserbringer und Kostenträger dürfen Rechnungen einreichen.");
            }
            
        } catch (AccessTokenException e) {
            LOGGER.error("Token Validierungsfehler: {}", e.getMessage());
            throw new AuthenticationException("Token Validierungsfehler: " + e.getMessage());
        }
    }
} 