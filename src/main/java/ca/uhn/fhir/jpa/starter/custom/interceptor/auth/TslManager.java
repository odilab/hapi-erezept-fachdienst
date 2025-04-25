package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class TslManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TslManager.class);
    private static final String TSL_NAMESPACE = "http://uri.etsi.org/02231/v2#";
    private static final String PKC_SERVICE_TYPE = "http://uri.etsi.org/TrstSvc/Svctype/CA/PKC";
    private static final String SERVICE_STATUS_ACTIVE = "http://uri.etsi.org/TrstSvc/Svcstatus/inaccord";
    private static final String EXTENSION_OID = "1.2.276.0.76.4.203";
    private static final String EXTENSION_VALUE = "oid_fd_sig";
    private static final String BC_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
    private static final String DEFAULT_TSL_RESOURCE = "/TSL_final.xml";
    
    private final Map<String, List<TslCertificateItem>> certificatesByIssuer;
    
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }
    
    public TslManager() {
        this.certificatesByIssuer = new HashMap<>();
        loadDefaultTsl();
    }
    
    private void loadDefaultTsl() {
        try {
            loadTslFromResource(DEFAULT_TSL_RESOURCE);
            logger.info("Standard-TSL erfolgreich geladen");
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Standard-TSL: {}", e.getMessage());
            throw new RuntimeException("Konnte Standard-TSL nicht laden", e);
        }
    }
    
    public void loadTslFromResource(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.error("TSL-Datei konnte nicht gefunden werden: {}", resourcePath);
                throw new IllegalArgumentException("TSL-Datei nicht gefunden: " + resourcePath);
            }
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            // Deaktiviere externe Entitäten für Sicherheit
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            
            processTslXml(doc);
            logger.info("TSL erfolgreich geladen. {} Zertifikatsaussteller gefunden.", certificatesByIssuer.size());
        } catch (Exception e) {
            logger.error("Fehler beim Laden der TSL: {}", e.getMessage(), e);
            throw new RuntimeException("Fehler beim Laden der TSL", e);
        }
    }
    
    private void processTslXml(Document doc) throws Exception {
        NodeList tspList = doc.getElementsByTagNameNS(TSL_NAMESPACE, "TrustServiceProvider");
        
        for (int i = 0; i < tspList.getLength(); i++) {
            Element tsp = (Element) tspList.item(i);
            processServiceProvider(tsp);
        }
    }
    
    private void processServiceProvider(Element tsp) throws Exception {
        NodeList services = tsp.getElementsByTagNameNS(TSL_NAMESPACE, "TSPService");
        
        for (int i = 0; i < services.getLength(); i++) {
            Element service = (Element) services.item(i);
            processService(service);
        }
    }
    
    private void processService(Element service) throws Exception {
        Element serviceInfo = (Element) service.getElementsByTagNameNS(TSL_NAMESPACE, "ServiceInformation").item(0);
        
        String type = getElementText(serviceInfo, "ServiceTypeIdentifier");
        String status = getElementText(serviceInfo, "ServiceStatus");
        
        if (!PKC_SERVICE_TYPE.equals(type) || !SERVICE_STATUS_ACTIVE.equals(status)) {
            return;
        }
        
        if (!checkServiceExtensions(serviceInfo)) {
            return;
        }
        
        NodeList digitalIds = serviceInfo.getElementsByTagNameNS(TSL_NAMESPACE, "DigitalId");
        for (int i = 0; i < digitalIds.getLength(); i++) {
            Element digitalId = (Element) digitalIds.item(i);
            processCertificate(digitalId, serviceInfo);
        }
    }
    
    private boolean checkServiceExtensions(Element serviceInfo) {
        NodeList extensions = serviceInfo.getElementsByTagNameNS(TSL_NAMESPACE, "Extension");
        boolean hasExtension = false;
        for (int i = 0; i < extensions.getLength(); i++) {
            Element extension = (Element) extensions.item(i);
            String oid = getElementText(extension, "ExtensionOID");
            String value = getElementText(extension, "ExtensionValue");
            
            if (EXTENSION_OID.equals(oid) && EXTENSION_VALUE.equals(value)) {
                hasExtension = true;
                break;
            }
        }
        return hasExtension;
    }
    
    private void processCertificate(Element digitalId, Element serviceInfo) throws Exception {
        String certB64 = getElementText(digitalId, "X509Certificate");
        if (certB64 == null || certB64.isEmpty()) {
            return;
        }
        
        byte[] certBytes = Base64.getDecoder().decode(certB64);
        X509CertificateHolder certHolder = new X509CertificateHolder(certBytes);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certHolder);
        
        List<String> supplyPoints = extractSupplyPoints(serviceInfo);
        String subjectDN = cert.getSubjectX500Principal().getName();
        TslCertificateItem item = new TslCertificateItem(cert, supplyPoints);
        
        certificatesByIssuer
            .computeIfAbsent(subjectDN, k -> new ArrayList<>())
            .add(item);
            
        logger.info("Zertifikat zur TSL hinzugefügt: {}", subjectDN);
    }
    
    private List<String> extractSupplyPoints(Element serviceInfo) {
        List<String> points = new ArrayList<>();
        try {
            NodeList supplyPoints = serviceInfo.getElementsByTagNameNS(TSL_NAMESPACE, "ServiceSupplyPoint");
            logger.debug("Gefundene ServiceSupplyPoints: {}", supplyPoints.getLength());
            
            for (int i = 0; i < supplyPoints.getLength(); i++) {
                String point = supplyPoints.item(i).getTextContent();
                if (point != null && !point.trim().isEmpty()) {
                    logger.debug("ServiceSupplyPoint gefunden: {}", point);
                    points.add(point.trim());
                }
            }
        } catch (Exception e) {
            logger.error("Fehler beim Extrahieren der ServiceSupplyPoints: {}", e.getMessage());
        }
        
        if (points.isEmpty()) {
            logger.warn("Keine ServiceSupplyPoints gefunden");
        }
        
        return points;
    }
    
    private String getElementText(Element parent, String elementName) {
        NodeList elements = parent.getElementsByTagNameNS(TSL_NAMESPACE, elementName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return null;
    }
    
    public boolean verifyCertificate(X509Certificate cert, ZonedDateTime validationTime) {
        try {
            String issuerDN = cert.getIssuerX500Principal().getName();
            List<TslCertificateItem> issuers = certificatesByIssuer.get(issuerDN);
            
            if (issuers == null || issuers.isEmpty()) {
                logger.error("Kein passender Aussteller in TSL gefunden für: {}", issuerDN);
                return false;
            }
            
            // Zeitvalidierung
            ValidationResult timeValidation = validateTime(cert, validationTime);
            if (!timeValidation.isValid()) {
                logger.error("Zeitvalidierung fehlgeschlagen: {}", timeValidation.getMessage());
                return false;
            }
            
            // Konvertiere das zu validierende Zertifikat zu BC
            org.bouncycastle.cert.X509CertificateHolder certHolder = 
                new org.bouncycastle.cert.jcajce.JcaX509CertificateHolder(cert);
            
            for (TslCertificateItem issuer : issuers) {
                X509Certificate issuerCert = issuer.getCertificate();
                
                // Prüfe auch die Gültigkeit des Aussteller-Zertifikats
                ValidationResult issuerTimeValidation = validateTime(issuerCert, validationTime);
                if (!issuerTimeValidation.isValid()) {
                    logger.info("Aussteller-Zertifikat nicht gültig: {}", issuerTimeValidation.getMessage());
                    continue;
                }
                
                // Prüfe Basic Constraints
                if (!validateBasicConstraints(issuerCert)) {
                    logger.info("Basic Constraints Validierung fehlgeschlagen für Aussteller: {}", 
                        issuerCert.getSubjectX500Principal().getName());
                    continue;
                }
                
                try {
                    // Erste Validierungsmethode: Bouncy Castle
                    if (validateWithBouncyCastle(cert, certHolder, issuerCert)) {
                        return true;
                    }
                    
                    // Zweite Validierungsmethode: Standard Java
                    if (validateWithJava(cert, issuerCert)) {
                        return true;
                    }
                    
                } catch (Exception e) {
                    logger.error("Validierung gegen Aussteller fehlgeschlagen: {} - {}", 
                        issuerDN, e.getMessage());
                }
            }
            
            logger.error("Keine erfolgreiche Validierung gegen einen der Aussteller für: {}", 
                cert.getSubjectX500Principal().getName());
            
        } catch (Exception e) {
            logger.error("Unerwarteter Fehler bei der Zertifikatsvalidierung: {}", e.getMessage());
        }
        
        return false;
    }
    
    private ValidationResult validateTime(X509Certificate cert, ZonedDateTime validationTime) {
        if (validationTime != null) {
            Date validationDate = Date.from(validationTime.toInstant());
            if (validationDate.before(cert.getNotBefore())) {
                return new ValidationResult(false, 
                    String.format("Zertifikat noch nicht gültig. Gültig ab: %s, Prüfzeitpunkt: %s", 
                        cert.getNotBefore(), validationDate));
            }
            if (validationDate.after(cert.getNotAfter())) {
                return new ValidationResult(false, 
                    String.format("Zertifikat nicht mehr gültig. Gültig bis: %s, Prüfzeitpunkt: %s", 
                        cert.getNotAfter(), validationDate));
            }
        }
        return new ValidationResult(true, "Zeitvalidierung erfolgreich");
    }
    
    private boolean validateBasicConstraints(X509Certificate cert) {
        try {
            return cert.getBasicConstraints() != -1;
        } catch (Exception e) {
            logger.error("Fehler bei der Basic Constraints Prüfung: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean validateWithBouncyCastle(X509Certificate cert, 
            org.bouncycastle.cert.X509CertificateHolder certHolder, 
            X509Certificate issuerCert) throws Exception {
        
        org.bouncycastle.cert.X509CertificateHolder issuerHolder = 
            new org.bouncycastle.cert.jcajce.JcaX509CertificateHolder(issuerCert);
        
        org.bouncycastle.operator.ContentVerifierProvider verifier = 
            new org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder()
                .setProvider(BC_PROVIDER)
                .build(issuerHolder);
        
        return certHolder.isSignatureValid(verifier);
    }
    
    private boolean validateWithJava(X509Certificate cert, X509Certificate issuerCert) {
        try {
            cert.verify(issuerCert.getPublicKey(), BC_PROVIDER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    public Map<String, List<TslCertificateItem>> getCertificatesByIssuer() {
        return Collections.unmodifiableMap(certificatesByIssuer);
    }
} 