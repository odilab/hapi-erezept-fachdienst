package ca.uhn.fhir.jpa.starter.custom;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessToken;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AccessTokenService;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.PukTokenManager;
import ca.uhn.fhir.jpa.starter.custom.interceptor.auth.TslManager;
import ca.uhn.fhir.jpa.starter.custom.config.TestcontainersConfig;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.json.JSONObject;
import ca.uhn.fhir.jpa.starter.Application;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
    Application.class, 
}, properties = {
    "hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter.custom.interceptor,ca.uhn.fhir.jpa.starter.custom.operation",
    "hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AuthenticationInterceptor,ca.uhn.fhir.jpa.starter.custom.interceptor.auth.ResourceAuthorizationInterceptor",
    "hapi.fhir.custom-provider-classes=ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider",
    "spring.datasource.url=jdbc:h2:mem:dbr4",
    "hapi.fhir.cr_enabled=false",
    "hapi.fhir.fhir_version=r4",
    "hapi.fhir.client_id_strategy=ANY"
})
@ContextConfiguration(initializers = {TestcontainersConfig.FullStackInitializer.class})
public abstract class BaseProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProviderTest.class);

    @LocalServerPort
    protected int port;

    protected IGenericClient client;
    protected FhirContext ctx;
    protected Patient testPatient;
    protected Practitioner testPractitioner;

    @Autowired
    protected AccessTokenService accessTokenService;

    @Autowired
    protected TslManager tslManager;
    
    @Autowired
    protected PukTokenManager pukTokenManager;

    // Speichere die KVNR aus dem EGK1-Token
    protected String versichertenKvnr;

    @BeforeEach
    void setUp() throws Exception {
        ctx = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ctx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
        String ourServerBase = "http://localhost:" + port + "/fhir/";
        client = ctx.newRestfulGenericClient(ourServerBase);

        // SSL-Validierung für Tests deaktivieren
        disableSSLValidation();
        
        // Für Tests: Zeit- und Signaturvalidierung deaktivieren
        // Die Test-Tokens kommen vom Testcontainer und haben keine gültigen Signaturen
        accessTokenService.setSkipTimeValidation(true);
        accessTokenService.setSkipSignatureValidation(true);

        // Extrahiere die KVNR aus dem EGK1-Token
        versichertenKvnr = extractKvnrFromEgk1Token();

        // Erstelle und speichere den Testpatienten mit EGK1 Token
//        testPatient = createTestPatient();
//        testPatient = (Patient) client.create()
//            .resource(testPatient)
//            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("EGK1"))
//            .execute()
//            .getResource();
//
//        // Erstelle und speichere den Testpractitioner mit HBA_ARZT Token
//        testPractitioner = createTestPractitioner();
//        testPractitioner = (Practitioner) client.create()
//            .resource(testPractitioner)
//            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
//            .execute()
//            .getResource();
    }

    /**
     * Deaktiviert die SSL-Validierung für Testzwecke
     */
    protected void disableSSLValidation() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            LOGGER.info("SSL-Validierung deaktiviert");
        } catch (Exception e) {
            fail("Konnte SSL-Validierung nicht deaktivieren: " + e.getMessage());
        }
    }

    /**
     * Holt ein gültiges Access Token vom IDP Server
     * @param healthCardType Der Typ der Gesundheitskarte (z.B. "SMCB_KRANKENHAUS")
     * @return Das Access Token als String
     */
    protected String getValidAccessToken(String healthCardType) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(String.format("https://%s:%d/getIdpToken?healthcards=%s",
                    TestcontainersConfig.startErpServiceContainer().getHost(),
                    TestcontainersConfig.startErpServiceContainer().getMappedPort(3001),
                    healthCardType));
            conn = (HttpURLConnection) url.openConnection();
            
            // SSL-Validierung für Testcontainer deaktivieren
            if (conn instanceof javax.net.ssl.HttpsURLConnection) {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[] { new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }}, new SecureRandom());
                ((javax.net.ssl.HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
                ((javax.net.ssl.HttpsURLConnection) conn).setHostnameVerifier((hostname, session) -> true);
            }
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            
            try (InputStream is = conn.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("accessToken");
            }
        } catch (Exception e) {
            LOGGER.error("Fehler beim Abrufen des Access Tokens: " + e.getMessage());
            throw new RuntimeException("Konnte Access Token nicht abrufen", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Extrahiert die KVNR aus einem EGK1-Token
     * @return Die KVNR des Versicherten
     */
    protected String extractKvnrFromEgk1Token() {
        String token = getValidAccessToken("EGK1");
        AccessToken accessToken = accessTokenService.verifyAndDecode("Bearer " + token);
        String kvnr = accessToken.getKvnr().orElse("A123456789");
        LOGGER.info("Extrahierte KVNR aus EGK1-Token: {}", kvnr);
        return kvnr;
    }

    protected Patient createTestPatient() {
        Patient patient = new Patient();
        
        // Meta mit Profil URL setzen
        patient.getMeta().addProfile("https://gematik.de/fhir/erg/StructureDefinition/erg-versicherteperson");
        
        // Narrative hinzufügen (dom-6 Constraint)
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Patient Max Testpatient</div>");
        patient.setText(narrative);
        
        // Active Status setzen (required laut Profil)
        patient.setActive(true);
        
        // Identifier (KVID) mit korrektem Slicing und der KVNR aus dem Token
        Identifier kvid = patient.addIdentifier();
        kvid.setSystem("http://fhir.de/sid/gkv/kvid-10")
            .setValue(versichertenKvnr);
        
        // Type mit beiden Codings (HL7 Standard + DE Basis)
        CodeableConcept identifierType = new CodeableConcept();
        identifierType.addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("SS");  // Social Security Number
        identifierType.addCoding()
            .setSystem("http://fhir.de/CodeSystem/identifier-type-de-basis")
            .setCode("KVZ10");
        kvid.setType(identifierType);
        
        // Name (required laut Profil)
        HumanName name = patient.addName();
        name.setFamily("Testpatient") // family ist required
            .addGiven("Max");         // given ist required
        name.setText("Max Testpatient"); // text ist required
        
        // Geburtsdatum (required laut Profil)
        patient.setBirthDate(new Date());
        
        // Adresse gemäß deutschem Basis-Profil
        Address address = patient.addAddress();
        
        // Straße und Hausnummer als separate Extensions in der line
        Extension strasseExtension = new Extension();
        strasseExtension.setUrl("http://fhir.de/StructureDefinition/address-de-basis/strasse");
        strasseExtension.setValue(new StringType("Teststraße"));
        
        Extension hausnummerExtension = new Extension();
        hausnummerExtension.setUrl("http://fhir.de/StructureDefinition/address-de-basis/hausnummer");
        hausnummerExtension.setValue(new StringType("123"));
        
        StringType addressLine = new StringType("Teststraße 123");
        addressLine.addExtension(strasseExtension);
        addressLine.addExtension(hausnummerExtension);
        address.addLine(addressLine.getValue());
        
        // Basis-Adressfelder
        address.setCity("Berlin")
            .setPostalCode("12345")
            .setCountry("DE");
        
        return patient;
    }

    protected Practitioner createTestPractitioner() {
        Practitioner practitioner = new Practitioner();
        
        // Meta mit Profil setzen
        practitioner.getMeta().addProfile("https://gematik.de/fhir/erg/StructureDefinition/erg-leistungserbringer");
        
        // Narrative hinzufügen (dom-6 Constraint)
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Dr. med. Olivia Orthoschmerz</div>");
        practitioner.setText(narrative);
        
        // Telematik-ID
        Identifier telematikId = practitioner.addIdentifier();
        CodeableConcept identifierType = new CodeableConcept();
        identifierType.addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("PRN");
        telematikId.setType(identifierType)
            .setSystem("https://gematik.de/fhir/sid/telematik-id")
            .setValue("11223344");
        
        // Name gemäß deutschem Basisprofil
        HumanName name = practitioner.addName();
        name.setUse(HumanName.NameUse.OFFICIAL);
        name.setFamily("Orthoschmerz");
        name.addGiven("Olivia");
        name.addPrefix("Dr. med.");
        
        // Extensions für den Namen
        Extension prefixQualifier = new Extension();
        prefixQualifier.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-EN-qualifier");
        prefixQualifier.setValue(new CodeType("AC")); // Academic
        name.getPrefix().get(0).addExtension(prefixQualifier);
        
        // Adresse gemäß deutschem Basisprofil
        Address address = practitioner.addAddress();
        address.setType(Address.AddressType.BOTH);
        
        // Straße und Hausnummer als separate Extensions
        StringType addressLine = new StringType("Teststraße 123");
        Extension strasseExtension = new Extension();
        strasseExtension.setUrl("http://fhir.de/StructureDefinition/address-de-basis/strasse");
        strasseExtension.setValue(new StringType("Teststraße"));
        addressLine.addExtension(strasseExtension);
        
        Extension hausnummerExtension = new Extension();
        hausnummerExtension.setUrl("http://fhir.de/StructureDefinition/address-de-basis/hausnummer");
        hausnummerExtension.setValue(new StringType("123"));
        addressLine.addExtension(hausnummerExtension);
        
        address.addLine(addressLine.getValue());
        address.setCity("Berlin")
            .setPostalCode("12345")
            .setCountry("DE");
        
        // Gender
        practitioner.setGender(Enumerations.AdministrativeGender.FEMALE);
        Extension genderExt = new Extension();
        genderExt.setUrl("http://fhir.de/StructureDefinition/gender-amtlich-de");
        genderExt.setValue(new CodeType("W")); // Weiblich
        practitioner.getGenderElement().addExtension(genderExt);
        
        // Qualifikation (KBV-Fachgruppe)
        CodeableConcept qualCode = new CodeableConcept();
        qualCode.addCoding()
            .setSystem("https://fhir.kbv.de/CodeSystem/KBV_CS_SFHIR_BAR2_ARZTNRFACHGRUPPE")
            .setCode("00")  // Allgemeinmedizin
            .setDisplay("Allgemeinmedizin");
        practitioner.addQualification().setCode(qualCode);
        
        return practitioner;
    }


    protected byte[] encodeToBase64(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    protected HttpHeaders createVAUHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-erp-user", "l"); // Leistungserbringer
        headers.set("X-erp-resource", "Task");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }

} 