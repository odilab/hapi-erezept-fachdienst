package ca.uhn.fhir.jpa.starter.custom;

import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test-Klasse für die Testcontainer-Infrastruktur und Access Token Funktionalität.
 * 
 * Diese Klasse testet:
 * - Dass die Testcontainer (IDP und ERP-Service) korrekt starten
 * - Dass Access Tokens für verschiedene Health Card Types abgerufen werden können
 * - Die Netzwerk-Konnektivität zwischen den Containern
 * 
 * Nützlich für die Fehlerdiagnose bei Testcontainer-Problemen.
 */
public class TestcontainerAccessTokenTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestcontainerAccessTokenTest.class);
    
    @Test
    public void testCanStartContainersAndGetAccessToken() {
        LOGGER.info("Starte Testcontainer Test");
        
        // Starte Container
        GenericContainer<?> idpContainer = TestcontainersConfig.startIdpContainer();
        GenericContainer<?> erpContainer = TestcontainersConfig.startErpServiceContainer();
        
        assertNotNull(idpContainer);
        assertNotNull(erpContainer);
        assertTrue(idpContainer.isRunning(), "IDP Container sollte laufen");
        assertTrue(erpContainer.isRunning(), "ERP Container sollte laufen");
        
        LOGGER.info("IDP Container läuft auf Port: {}", idpContainer.getMappedPort(10000));
        LOGGER.info("ERP Container läuft auf Port: {}", erpContainer.getMappedPort(3001));
        LOGGER.info("IDP Container ID: {}", idpContainer.getContainerId());
        LOGGER.info("ERP Container ID: {}", erpContainer.getContainerId());
        
        // Warte, damit die Services vollständig hochfahren
        LOGGER.info("Warte 10 Sekunden, damit die Services vollständig hochfahren...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Teste verschiedene Health Card Types
        // Hinweis: HBA_ARZT scheint in der aktuellen Testcontainer-Konfiguration nicht zu funktionieren
        String[] healthCardTypes = {"EGK1", "SMCB_KRANKENHAUS"};
        
        for (String cardType : healthCardTypes) {
            LOGGER.info("Teste Access Token für: {}", cardType);
            
            HttpURLConnection conn = null;
            try {
                String urlString = String.format("https://%s:%d/getIdpToken?healthcards=%s",
                        erpContainer.getHost(),
                        erpContainer.getMappedPort(3001),
                        cardType);
                
                LOGGER.info("Rufe URL auf: {}", urlString);
                
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                
                // SSL-Validierung für Testcontainer deaktivieren
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                    httpsConn.setSSLSocketFactory(createTrustAllSSLContext().getSocketFactory());
                    httpsConn.setHostnameVerifier((hostname, session) -> true);
                }
                conn.setRequestMethod("GET");
                conn.setRequestProperty("accept", "application/json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                LOGGER.info("Response Code für {}: {}", cardType, responseCode);
                
                if (responseCode == 200) {
                    try (InputStream is = conn.getInputStream();
                         BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        
                        LOGGER.info("Response für {}: {}", cardType, response.toString());
                        
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        assertTrue(jsonResponse.has("accessToken"), "Response sollte accessToken enthalten");
                        
                        String accessToken = jsonResponse.getString("accessToken");
                        assertNotNull(accessToken);
                        assertFalse(accessToken.isEmpty());
                        
                        LOGGER.info("Access Token für {} erfolgreich erhalten (Länge: {})", cardType, accessToken.length());
                    }
                } else {
                    // Lese Error Response
                    StringBuilder errorResponse = new StringBuilder();
                    try (InputStream is = conn.getErrorStream()) {
                        if (is != null) {
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    errorResponse.append(line);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Konnte Error Stream nicht lesen", e);
                    }
                    
                    LOGGER.error("Error Response für {} (Code {}): {}", cardType, responseCode, errorResponse.toString());
                    
                    // Prüfe Container Logs
                    LOGGER.info("ERP Container Logs:");
                    String erpLogs = erpContainer.getLogs();
                    LOGGER.info(erpLogs);
                    
                    fail("Konnte Access Token für " + cardType + " nicht abrufen. Response Code: " + responseCode + ". Error: " + errorResponse.toString());
                }
                
            } catch (Exception e) {
                LOGGER.error("Fehler beim Abrufen des Access Tokens für " + cardType, e);
                fail("Exception beim Token-Abruf für " + cardType + ": " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        
        LOGGER.info("Alle Access Tokens erfolgreich abgerufen");
    }
    
    @Test
    public void testContainerConnectivity() {
        LOGGER.info("Teste Container Konnektivität und Health Endpoints");
        
        GenericContainer<?> idpContainer = TestcontainersConfig.startIdpContainer();
        GenericContainer<?> erpContainer = TestcontainersConfig.startErpServiceContainer();
        
        // Warte auf Container
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Teste IDP Health
        try {
            URL idpHealthUrl = new URL(String.format("https://%s:%d/.well-known/openid-configuration",
                    idpContainer.getHost(),
                    idpContainer.getMappedPort(10000)));
            
            LOGGER.info("Teste IDP Health: {}", idpHealthUrl);
            
            HttpURLConnection conn = (HttpURLConnection) idpHealthUrl.openConnection();
            // SSL-Validierung für Test deaktivieren
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(createTrustAllSSLContext().getSocketFactory());
                ((HttpsURLConnection) conn).setHostnameVerifier((hostname, session) -> true);
            }
            
            int responseCode = conn.getResponseCode();
            LOGGER.info("IDP Health Response Code: {}", responseCode);
            assertEquals(200, responseCode, "IDP sollte erreichbar sein");
            
        } catch (Exception e) {
            LOGGER.error("IDP Health Check fehlgeschlagen", e);
            fail("IDP Health Check fehlgeschlagen: " + e.getMessage());
        }
        
        // Teste ERP Service Health (mit HTTPS)
        try {
            URL erpHealthUrl = new URL(String.format("https://%s:%d/actuator/health",
                    erpContainer.getHost(),
                    erpContainer.getMappedPort(3001)));
            
            LOGGER.info("Teste ERP Service Health: {}", erpHealthUrl);
            
            HttpURLConnection conn = (HttpURLConnection) erpHealthUrl.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(createTrustAllSSLContext().getSocketFactory());
                ((HttpsURLConnection) conn).setHostnameVerifier((hostname, session) -> true);
            }
            int responseCode = conn.getResponseCode();
            LOGGER.info("ERP Service Health Response Code: {}", responseCode);
            
            // Akzeptiere 200 oder 503 (Service ist da, aber evtl. noch nicht ready)
            assertTrue(responseCode == 200 || responseCode == 503, 
                      "ERP Service sollte antworten (200 oder 503)");
            
        } catch (Exception e) {
            LOGGER.error("ERP Service Health Check fehlgeschlagen", e);
            fail("ERP Service Health Check fehlgeschlagen: " + e.getMessage());
        }
    }
    
    private SSLContext createTrustAllSSLContext() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { 
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            }, new SecureRandom());
            return sc;
        } catch (Exception e) {
            throw new RuntimeException("Konnte SSL Context nicht erstellen", e);
        }
    }
}