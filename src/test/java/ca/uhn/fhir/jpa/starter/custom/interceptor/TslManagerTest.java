package ca.uhn.fhir.jpa.starter.custom.interceptor;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.TslManager;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.TslCertificateItem;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TslManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(TslManagerTest.class);
    private TslManager tslManager;
    private static final String TEST_CERT_BASE64 = "MIIEYzCCA0ugAwIBAgIBOjANBgkqhkiG9w0BAQsFADCBgTELMAkGA1UEBhMCREUxHzAdBgNVBAoMFmdlbWF0aWsgR21iSCBOT1QtVkFMSUQxNDAyBgNVBAsMK1plbnRyYWxlIFJvb3QtQ0EgZGVyIFRlbGVtYXRpa2luZnJhc3RydWt0dXIxGzAZBgNVBAMMEkdFTS5SQ0EyIFRFU1QtT05MWTAeFw0yMDA4MDUxMjUzMTdaFw0yNjExMTQxMjUzMTZaMIGaMQswCQYDVQQGEwJERTEfMB0GA1UECgwWYWNoZWxvcyBHbWJIIE5PVC1WQUxJRDFFMEMGA1UECww8RWxla3Ryb25pc2NoZSBHZXN1bmRoZWl0c2thcnRlLUNBIGRlciBUZWxlbWF0aWtpbmZyYXN0cnVrdHVyMSMwIQYDVQQDDBpBQ0hFTE9TLkVHSy1DQTIwIFRFU1QtT05MWTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKk3+G07hWgrcOpHA3SonSHiqnugSH63U2jP42h4bzcbYCybke86owJunpJXQKw81FpawmtOYoJl/0dGnbGufcHPadT6MQ7B33fj6bfv4YYrjsnhKr1E+UvOj2/YLJOqJamzhibyOf4p9D1NN2PqqimFwi0IlUGMtV4FTzh6wSA1a1fDkA4wqaIAAz0aqwSUKkkfi64Jm+k0Xr6f1MQ1B+wj6yO0fJGVjVszojWlWJ9reofQuQ9lSImojyrzE8g/dVHuKbEc7O/p6dKrjQvjlWcfX3LEvlujA4HQfpa0dbPniFygXKm8Z4udkBIRg4yodNS4oKiKnEnTKh2MN09ArX0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU4jJTMhnHRqOpkqCdPQee5Y7LRWswHwYDVR0jBBgwFoAULWkAu6H0zI4DoiWDksnSY+HZRLgwSgYIKwYBBQUHAQEEPjA8MDoGCCsGAQUFBzABhi5odHRwOi8vb2NzcC10ZXN0cmVmLnJvb3QtY2EudGktZGllbnN0ZS5kZS9vY3NwMBIGA1UdEwEB/wQIMAYBAf8CAQAwDgYDVR0PAQH/BAQDAgEGMBUGA1UdIAQOMAwwCgYIKoIUAEwEgSMwDQYJKoZIhvcNAQELBQADggEBADLw2VkdxT6WwG1UH8W1rN3NABoLtuYyGTUD0bdLVjBhFAoqs4g/C+boAIdxUqo3ErW9Mv8Ip2qm1AE+eb24/SwWMAa2/kS269vIIBPfoJkC3ujM42TxpZyeuILfhfSknSlF5UuuWAr2403VeXlyMctAQWvWxw3Iu5zOkicT3CZHTGM5Ua1GQkPSnwHKIvmfn6V4l54DQndAoTxX1UlwWFHDXEGF/SRZ5+GG3KTV+pCH0VItGnJI5cgJ5afNdaj8wqIkhIJUurSLeYbuGdJBNx9NeuBP+lFRYT+jdoHV8Y/OEYBoSp17iIsZ04nqX23SagnG63sat9K1plalm5ZBK44=";

    @BeforeEach
    void setUp() {
        tslManager = new TslManager();
    }

    @Test
    void testLoadTslFromResource() {
        // TSL laden
        tslManager.loadTslFromResource("/TSL_final.xml");

        // Überprüfen, ob Zertifikate geladen wurden
        Map<String, List<TslCertificateItem>> certificates = tslManager.getCertificatesByIssuer();
        assertFalse(certificates.isEmpty(), "Es sollten Zertifikate geladen worden sein");
    }
	 

    @Test
    void testVerifyCertificateWithExpiredValidationTime() throws Exception {
        // TSL laden
        tslManager.loadTslFromResource("/TSL_final.xml");

        // Test-Zertifikat aus Base64 laden
        byte[] certBytes = Base64.getDecoder().decode(TEST_CERT_BASE64);
        X509CertificateHolder certHolder = new X509CertificateHolder(certBytes);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certHolder);

        // Zertifikat mit abgelaufenem Datum validieren
        ZonedDateTime expiredTime = ZonedDateTime.parse("2027-01-01T00:00:00Z");
        boolean isValid = tslManager.verifyCertificate(cert, expiredTime);
        assertFalse(isValid, "Das Zertifikat sollte zu diesem Zeitpunkt ungültig sein");
    }

    @Test
    void testLoadTslAndCheckSupplyPoints() {
        // TSL laden
        tslManager.loadTslFromResource("/TSL_final.xml");

        // Zertifikate abrufen
        Map<String, List<TslCertificateItem>> certificates = tslManager.getCertificatesByIssuer();
        assertFalse(certificates.isEmpty(), "TSL sollte Zertifikate enthalten");

        // Überprüfen der Supply Points für mindestens ein Zertifikat
        boolean foundSupplyPoint = false;
        String foundUrl = null;
        
        for (List<TslCertificateItem> items : certificates.values()) {
            for (TslCertificateItem item : items) {
                List<String> supplyPoints = item.getSupplyPoints();
                if (!supplyPoints.isEmpty()) {
                    foundSupplyPoint = true;
                    foundUrl = supplyPoints.get(0);
                    logger.info("Supply Point URL gefunden: {}", foundUrl);
                    
                    // Prüfe ob die URL einem der erlaubten OCSP-Formate entspricht
                    boolean isValidOcspUrl = foundUrl.startsWith("http://ocsp.") || 
                                           foundUrl.startsWith("http://ocsp-") ||
                                           foundUrl.contains("/ocsp");
                                           
                    assertTrue(isValidOcspUrl,
                            String.format("Supply Point '%s' sollte ein gültiges OCSP-Format haben (http://ocsp. oder http://ocsp- oder /ocsp)", 
                            foundUrl));
                    break;
                }
            }
            if (foundSupplyPoint) break;
        }
        
        assertTrue(foundSupplyPoint, "Mindestens ein ServiceSupplyPoint sollte in der TSL vorhanden sein");
    }

    @Test
    void testLoadTslAndCheckCertificateCount() {
        // TSL laden
        tslManager.loadTslFromResource("/TSL_final.xml");

        // Zertifikate abrufen
        Map<String, List<TslCertificateItem>> certificates = tslManager.getCertificatesByIssuer();

        // Überprüfen, ob mindestens 3 verschiedene Aussteller vorhanden sind
        assertTrue(certificates.size() >= 3,
                "Es sollten mindestens 3 verschiedene Zertifikatsaussteller vorhanden sein");

        // Überprüfen der Gesamtanzahl der Zertifikate
        int totalCertificates = certificates.values().stream()
                .mapToInt(List::size)
                .sum();
        assertTrue(totalCertificates >= 3,
                "Es sollten insgesamt mindestens 3 Zertifikate vorhanden sein");
    }

    @Test
    void testValidateJwtSignatureFromIdp() throws Exception {
        // TSL laden
        tslManager.loadTslFromResource("/TSL_final.xml");

        // SSL-Konfiguration deaktivieren (nur für Tests!)
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        // Discovery Document abrufen
        String idpUrl = "https://localhost:10000/.well-known/openid-configuration";
        logger.info("Verbinde mit IDP: {}", idpUrl);

        try {
            URL url = new URL(idpUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json, */*;q=0.8");
            conn.setRequestProperty("User-Agent", "TslManagerTest/1.0");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoInput(true);
            conn.setUseCaches(false);

            // Response lesen
            try (InputStream is = conn.getInputStream()) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String jwt = result.toString("UTF-8");
                
                // JWT-Header extrahieren und Base64-decodieren
                String[] jwtParts = jwt.split("\\.");
                String headerJson = new String(Base64.getUrlDecoder().decode(jwtParts[0]));
                
                // x5c-Zertifikat aus Header extrahieren
                String x5cCert = headerJson.split("\"x5c\":\\[\"")[1].split("\"\\]")[0];
                
                // X509-Zertifikat aus Base64 erstellen
                byte[] certBytes = Base64.getDecoder().decode(x5cCert);
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate signerCert = (X509Certificate) certFactory.generateCertificate(
                        new ByteArrayInputStream(certBytes));

                // Zertifikat gegen TSL validieren
                boolean isValid = tslManager.verifyCertificate(signerCert, ZonedDateTime.now());
                assertTrue(isValid, "Das Signaturzertifikat sollte in der TSL vorhanden und gültig sein");
            }
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Discovery Documents: ", e);
            throw e;
        }
    }
}

// Hilfsklasse für SSL/TLS
class SSLUtils {
    public static SSLContext createTrustAllSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc;
    }
} 