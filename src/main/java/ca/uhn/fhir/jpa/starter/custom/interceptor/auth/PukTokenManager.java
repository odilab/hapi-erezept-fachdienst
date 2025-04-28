package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.security.interfaces.ECPublicKey;
import jakarta.annotation.PostConstruct;

@Component
public class PukTokenManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PukTokenManager.class);
    
    private final String discoveryUrl;
    private final long updateIntervalSeconds;
    private PublicKey currentPublicKey;
    
    @Autowired
    private TslManager tslManager;
    
    public PukTokenManager(
            @Value("${hapi.fhir.auth.discovery_url}") String discoveryUrl,
            @Value("${hapi.fhir.auth.update_interval_seconds}") long updateIntervalSeconds) {
        this.discoveryUrl = discoveryUrl;
        this.updateIntervalSeconds = updateIntervalSeconds;
    }
    
    @PostConstruct
    public void init() {
        try {
            updatePublicKey();
            logger.info("PukTokenManager wurde automatisch initialisiert und Public Key erfolgreich geladen.");
        } catch (Exception e) {
            logger.warn("Initialisierung des PukTokenManager fehlgeschlagen (z.B. Discovery URL nicht erreichbar). Public Key konnte nicht geladen werden: {}. Fortfahren ohne Public Key.", e.getMessage());
            // Die Exception wird hier abgefangen, damit die Bean-Erstellung nicht fehlschlägt.
            // Der currentPublicKey bleibt null, was an anderer Stelle behandelt werden muss, 
            // wenn der Key tatsächlich benötigt wird.
        }
    }
    
    private void configureSslContext() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            logger.error("Fehler beim Konfigurieren des SSL-Kontexts: {}", e.getMessage(), e);
            throw new RuntimeException("SSL-Konfigurationsfehler", e);
        }
    }
    
    private String fetchData(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json, */*;q=0.8");
            conn.setRequestProperty("User-Agent", "PukTokenManager/1.0");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            
            try (InputStream is = conn.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                return result.toString(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Daten von {}: {}", urlString, e.getMessage());
            throw new RuntimeException("Fehler beim Datenabruf", e);
        }
    }
    
    private X509Certificate createCertificate(byte[] certBytes) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen des Zertifikats: {}", e.getMessage());
            throw new RuntimeException("Zertifikatserstellungsfehler", e);
        }
    }
    
    private void updatePublicKey() {
        try {
            configureSslContext();
            
            // Discovery-Dokument laden und validieren
            String jwt = fetchData(discoveryUrl);
            X509Certificate discoveryDocCert = extractAndValidateCertFromJwt(jwt);
            logger.info("Discovery-Dokument Zertifikat validiert");
            
            // JWKS URL und Daten abrufen
            String jwksUrl = extractUriPukIdpSig(jwt);
            String jwksResponse = fetchData(jwksUrl);
            JSONObject jwks = new JSONObject(jwksResponse);
            
            // Zertifikat aus JWKS extrahieren und validieren
            JSONArray x5cArray = jwks.getJSONArray("x5c");
            if (x5cArray.length() == 0) {
                throw new RuntimeException("Keine Zertifikate im x5c-Array gefunden");
            }
            
            byte[] certBytes = Base64.getDecoder().decode(x5cArray.getString(0));
            X509Certificate cert = createCertificate(certBytes);
            
            if (!tslManager.verifyCertificate(cert, ZonedDateTime.now())) {
                throw new RuntimeException("JWKS-Zertifikat konnte nicht gegen TSL validiert werden");
            }
            
            // Public Key extrahieren und speichern
            PublicKey pubKey = cert.getPublicKey();
            if (pubKey instanceof ECPublicKey) {
                this.currentPublicKey = pubKey;
                logger.info("Public Key erfolgreich aktualisiert");
            } else {
                throw new RuntimeException("Extrahierter Public Key ist kein EC Public Key");
            }
            
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren des Public Keys: {}", e.getMessage());
            throw new RuntimeException("Public Key Aktualisierungsfehler", e);
        }
    }
    
    private String extractUriPukIdpSig(String jwt) {
        try {
            String[] jwtParts = jwt.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));
            return new JSONObject(payload).getString("uri_puk_idp_sig");
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Extrahieren der JWKS-URL", e);
        }
    }
    
    private X509Certificate extractAndValidateCertFromJwt(String jwt) {
        try {
            String[] jwtParts = jwt.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(jwtParts[0]));
            String x5cCert = new JSONObject(headerJson).getJSONArray("x5c").getString(0);
            
            byte[] certBytes = Base64.getDecoder().decode(x5cCert);
            X509Certificate cert = createCertificate(certBytes);
            
            if (!tslManager.verifyCertificate(cert, ZonedDateTime.now())) {
                throw new RuntimeException("Zertifikatsvalidierung fehlgeschlagen");
            }
            
            return cert;
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei der Zertifikatsextraktion", e);
        }
    }
    
    public PublicKey getCurrentPublicKey() {
        return currentPublicKey;
    }
} 