package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dieser Interceptor prüft die Autorisierung für den Zugriff auf bestimmte Ressourcen
 * basierend auf den Scopes im Access Token.
 */
@Component
@Interceptor(order = 1) // Ausführung nach dem AuthenticationInterceptor
public class ResourceAuthorizationInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceAuthorizationInterceptor.class);
    private static final String WILDCARD_SCOPE = "openid e-rezept";
    
    /**
     * Prüft die Autorisierung für den Zugriff auf Ressourcen.
     * 
     * @param requestDetails Die Details der Anfrage
     */
    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void interceptRequest(RequestDetails requestDetails) {
        if (requestDetails == null) {
            LOGGER.error("RequestDetails ist null");
            return;
        }
        
        // Hole den Access Token aus den UserData
        Object tokenObj = requestDetails.getUserData().get("ACCESS_TOKEN");
        if (!(tokenObj instanceof AccessToken)) {
            // Wenn kein Token vorhanden ist, wurde die Anfrage bereits vom AuthenticationInterceptor abgelehnt
            // oder es handelt sich um einen Pfad auf der Whitelist
            return;
        }
        
        AccessToken accessToken = (AccessToken) tokenObj;
        String resourceName = requestDetails.getResourceName();
        RestOperationTypeEnum operationType = requestDetails.getRestOperationType();
        
        LOGGER.debug("Prüfe Autorisierung für Ressource: {}, Operation: {}", resourceName, operationType);
        
        // Prüfe Autorisierung für verschiedene Ressourcen
        if ("Patient".equals(resourceName)) {
            validatePatientAccess(accessToken, operationType);
        } else if ("DocumentReference".equals(resourceName)) {
            validateDocumentReferenceAccess(accessToken, operationType);
        } else if ("AuditEvent".equals(resourceName)) {
            validateAuditEventAccess(accessToken, operationType);
        }
        
        // Hier können weitere Ressourcen-spezifische Autorisierungsprüfungen hinzugefügt werden
    }
    
    /**
     * Prüft die Autorisierung für den Zugriff auf die Patient-Ressource.
     * 
     * @param accessToken Das Access Token
     * @param operationType Der Typ der Operation
     */
    private void validatePatientAccess(AccessToken accessToken, RestOperationTypeEnum operationType) {
        // Gemäß A_26028: Für den Zugriff auf Patient-Ressourcen wird der Scope "insurantAccount.rs" oder "openid e-rezept" benötigt
        String scope = accessToken.getScope();
        
        if (scope == null || (!scope.equals("insurantAccount.rs") && !scope.equals(WILDCARD_SCOPE))) {
            LOGGER.error("Fehlender Scope für Patient-Ressource. Erforderlich: insurantAccount.rs, Vorhanden: {}", scope);
            throw new ForbiddenOperationException("Fehlender Scope: insurantAccount.rs");
        }
        
        LOGGER.debug("Autorisierung für Patient-Ressource erfolgreich mit Scope: {}", scope);
    }
    
    /**
     * Prüft die Autorisierung für den Zugriff auf die DocumentReference-Ressource.
     * 
     * @param accessToken Das Access Token
     * @param operationType Der Typ der Operation
     */
    private void validateDocumentReferenceAccess(AccessToken accessToken, RestOperationTypeEnum operationType) {
        // Gemäß A_26033, A_26034: Für den Zugriff auf DocumentReference-Ressourcen werden verschiedene Scopes benötigt
        String scope = accessToken.getScope();
        boolean hasWildcard = scope != null && scope.equals(WILDCARD_SCOPE);
        
        // Prüfe, ob es sich um eine Suchanfrage handelt
        boolean isSearchOperation = RestOperationTypeEnum.SEARCH_TYPE.equals(operationType);
        
        if (scope == null) {
            LOGGER.error("Kein Scope im Access Token gefunden");
            throw new ForbiddenOperationException("Kein Scope im Access Token gefunden");
        }
        
        if (isSearchOperation) {
            // Für Suchanfragen wird der Scope "invoiceDoc.s" oder der Wildcard-Scope benötigt
            if (!scope.equals("invoiceDoc.s") && !hasWildcard) {
                LOGGER.error("Fehlender Scope für DocumentReference-Suche. Erforderlich: invoiceDoc.s, Vorhanden: {}", scope);
                throw new ForbiddenOperationException("Fehlender Scope: invoiceDoc.s");
            }
            LOGGER.debug("Autorisierung für DocumentReference-Suche erfolgreich mit Scope: {}", scope);
        } else {
            // Für Abruf wird der Scope "invoiceDoc.r" oder der Wildcard-Scope benötigt
            if (!scope.equals("invoiceDoc.r") && !hasWildcard) {
                LOGGER.error("Fehlender Scope für DocumentReference-Abruf. Erforderlich: invoiceDoc.r, Vorhanden: {}", scope);
                throw new ForbiddenOperationException("Fehlender Scope: invoiceDoc.r");
            }
            LOGGER.debug("Autorisierung für DocumentReference-Abruf erfolgreich mit Scope: {}", scope);
        }
    }
    
    /**
     * Prüft die Autorisierung für den Zugriff auf die AuditEvent-Ressource.
     * 
     * @param accessToken Das Access Token
     * @param operationType Der Typ der Operation
     */
    private void validateAuditEventAccess(AccessToken accessToken, RestOperationTypeEnum operationType) {
        // Gemäß A_26041: Für den Zugriff auf AuditEvent-Ressourcen wird der Scope "auditEvent.rs" oder "openid e-rezept" benötigt
        String scope = accessToken.getScope();
        
        if (scope == null || (!scope.equals("auditEvent.rs") && !scope.equals(WILDCARD_SCOPE))) {
            LOGGER.error("Fehlender Scope für AuditEvent-Ressource. Erforderlich: auditEvent.rs, Vorhanden: {}", scope);
            throw new ForbiddenOperationException("Fehlender Scope: auditEvent.rs");
        }
        
        LOGGER.debug("Autorisierung für AuditEvent-Ressource erfolgreich mit Scope: {}", scope);
    }
} 