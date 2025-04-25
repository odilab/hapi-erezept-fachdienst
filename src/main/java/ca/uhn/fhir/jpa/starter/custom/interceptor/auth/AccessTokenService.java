package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.security.interfaces.ECPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccessTokenService {
    
    private final PukTokenManager pukTokenManager;
    private boolean skipTimeValidation = false;
    private boolean skipSignatureValidation = false;
    private static final Logger logger = LoggerFactory.getLogger(AccessTokenService.class);
    private static final long MAX_FUTURE_SECONDS = 365 * 24 * 60 * 60L;
    
    @Autowired
    public AccessTokenService(PukTokenManager pukTokenManager) {
        this.pukTokenManager = pukTokenManager;
    }

    public void setSkipTimeValidation(boolean skip) {
        this.skipTimeValidation = skip;
    }
    
    public void setSkipSignatureValidation(boolean skip) {
        this.skipSignatureValidation = skip;
    }
    
    /**
     * Validiert einen AccessToken auf zeitliche Gültigkeit
     * @param accessToken Der zu validierende AccessToken
     * @throws AccessTokenException wenn der Token abgelaufen ist oder noch nicht gültig ist
     */
    public void validateToken(AccessToken accessToken) {
        if (!skipTimeValidation) {
            if (accessToken.isExpired()) {
                throw new AccessTokenException(AccessTokenError.EXPIRED, "Der Access Token ist abgelaufen");
            }
            
            if (accessToken.isNotValidYet()) {
                throw new AccessTokenException(AccessTokenError.NOT_VALID_YET, "Der Access Token ist noch nicht gültig");
            }
        }
    }
    
    private String extractToken(String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new AccessTokenException(AccessTokenError.INVALID_VALUE, "Ungültiges Authorization Header Format");
        }
        return authHeader.substring(7);
    }
    
    private Algorithm getAlgorithm(DecodedJWT unverifiedJwt) {
        String alg = unverifiedJwt.getAlgorithm();
        logger.info("JWT verwendet Algorithmus: {}", alg);
        
        ECPublicKey publicKey = (ECPublicKey) pukTokenManager.getCurrentPublicKey();
        if (publicKey == null) {
            throw new AccessTokenException(AccessTokenError.INVALID_VALUE, "Kein Public Key für Signaturvalidierung verfügbar");
        }
        
        if (alg.equalsIgnoreCase("BP256R1")) {
            logger.debug("Verwende Brainpool P256r1 Algorithmus");
            return new BrainpoolP256r1Algorithm(publicKey);
        } else if (alg.equalsIgnoreCase("ES256")) {
            logger.debug("Verwende NIST P-256 ECDSA Algorithmus");
            return Algorithm.ECDSA256(publicKey, null);
        }
        
        throw new AccessTokenException(AccessTokenError.INVALID_VALUE, "Nicht unterstützter JWT Algorithmus: " + alg);
    }
    
    private JWTVerifier createVerifier(Algorithm algorithm) {
        com.auth0.jwt.JWTVerifier.BaseVerification verification = 
            (com.auth0.jwt.JWTVerifier.BaseVerification) JWT.require(algorithm)
                .withIssuer("https://idp.zentral.idp.splitdns.ti-dienste.de");
        
        if (skipTimeValidation) {
            verification.acceptExpiresAt(Instant.now().getEpochSecond() + MAX_FUTURE_SECONDS)
                       .acceptIssuedAt(0)
                       .acceptNotBefore(0);
        }
        
        return verification.build();
    }
    
    private AccessToken createAccessToken(DecodedJWT jwt) {
        AccessToken accessToken = new AccessToken();
        accessToken.setIss(jwt.getIssuer());
        accessToken.setSub(jwt.getSubject());
        accessToken.setAud(jwt.getAudience().get(0));
        accessToken.setExp(jwt.getExpiresAt().toInstant());
        accessToken.setIat(jwt.getIssuedAt().toInstant());
        accessToken.setProfession(Profession.fromOID(jwt.getClaim("professionOID").asString()));
        accessToken.setIdNumber(jwt.getClaim("idNummer").asString());
        accessToken.setGivenName(jwt.getClaim("given_name").asString());
        accessToken.setFamilyName(jwt.getClaim("family_name").asString());
        accessToken.setOrganizationName(jwt.getClaim("organizationName").asString());
        accessToken.setClientId(jwt.getClaim("client_id").asString());
        accessToken.setScope(jwt.getClaim("scope").asString());
        return accessToken;
    }
    
    public AccessToken verifyAndDecode(String authHeader) {
        try {
            String token = extractToken(authHeader);
            DecodedJWT jwt;
            
            if (skipSignatureValidation) {
                jwt = JWT.decode(token);
            } else {
                DecodedJWT unverifiedJwt = JWT.decode(token);
                Algorithm algorithm = getAlgorithm(unverifiedJwt);
                JWTVerifier verifier = createVerifier(algorithm);
                jwt = verifier.verify(token);
            }
            
            AccessToken accessToken = createAccessToken(jwt);
            validateToken(accessToken);
            return accessToken;
            
        } catch (JWTDecodeException e) {
            throw new AccessTokenException(AccessTokenError.INVALID_VALUE, "Token konnte nicht decodiert werden: " + e.getMessage());
        } catch (JWTVerificationException e) {
            throw new AccessTokenException(AccessTokenError.INVALID_VALUE, "Token Signatur ungültig: " + e.getMessage());
        }
    }
} 