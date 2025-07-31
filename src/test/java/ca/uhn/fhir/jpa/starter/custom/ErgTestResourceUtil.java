package ca.uhn.fhir.jpa.starter.custom;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility-Klasse für die Erstellung von FHIR-Testressourcen gemäß ERG-Profilen
 */
public class ErgTestResourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ErgTestResourceUtil.class);

    /**
     * Erstellt einen validen ERG-Patienten gemäß dem ERG-Patient-Profil
     */
    public static Patient createTestErgPatient() {
        Patient patient = new Patient();

        // Meta mit Profil URL setzen (inkl. Version)
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erg/StructureDefinition/erg-patient|1.1.0-RC1");
        patient.setMeta(meta);

        // Narrative hinzufügen (dom-6 Constraint)
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Patientendaten</div>");
        patient.setText(narrative);

        // Identifier (KVNR) - required (min=0, but mustSupport=true)
        Identifier kvnr = patient.addIdentifier();
        // Type muss dem Pattern in identifier-kvid-10 entsprechen
        CodeableConcept kvnrType = new CodeableConcept();
        kvnrType.addCoding()
            .setSystem("http://fhir.de/CodeSystem/identifier-type-de-basis")
            .setCode("KVZ10");
        kvnr.setType(kvnrType);
        kvnr.setSystem("http://fhir.de/sid/gkv/kvid-10");
        kvnr.setValue("A123456789"); // Beispiel KVNR

        // Assigner für KVNR (IKNR der Krankenkasse) - mustSupport=true
        Identifier iknr = new Identifier();
        // Type muss dem Pattern in identifier-iknr entsprechen
        CodeableConcept iknrType = new CodeableConcept();
        iknrType.addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("XX");
        iknr.setType(iknrType);
        iknr.setSystem("http://fhir.de/sid/arge-ik/iknr");
        iknr.setValue("109519018"); // Beispiel IKNR
        kvnr.setAssigner(new Reference().setIdentifier(iknr));

        // Name - mustSupport=true
        HumanName name = patient.addName();
        name.setUse(HumanName.NameUse.OFFICIAL);
        name.setText("Frau Dr. Erika Mustermann"); // mustSupport=true
        name.setFamily("Mustermann"); // mustSupport=true
        // mustSupport Extensions für Family Name hinzufügen
        name.getFamilyElement().addExtension("http://fhir.de/StructureDefinition/humanname-namenszusatz", new StringType("geb. Beispiel")); // namenszusatz
        name.getFamilyElement().addExtension("http://hl7.org/fhir/StructureDefinition/humanname-own-name", new StringType("Mustermann")); // nachname
        name.getFamilyElement().addExtension("http://fhir.de/StructureDefinition/humanname-vorsatzwort", new StringType("von")); // vorsatzwort (Korrigierte URL)

        name.addGiven("Erika"); // mustSupport=true
        name.addPrefix("Dr."); // mustSupport=true
        // mustSupport Extension für Prefix hinzufügen
        if (!name.getPrefix().isEmpty()) {
            name.getPrefix().get(0).addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-EN-qualifier", new CodeType("AC")); // prefix-qualifier (Academic)
        }

        // BirthDate - mustSupport=true (korrigierte Initialisierung)
        Calendar cal = Calendar.getInstance();
        cal.set(1970, Calendar.JANUARY, 1);
        patient.setBirthDate(cal.getTime());

        // Address (Strassenanschrift) - mustSupport=true
        Address strassenanschrift = patient.addAddress();
        strassenanschrift.setType(Address.AddressType.BOTH); // mustSupport=true
        strassenanschrift.addLine("Musterstraße 1a Adresszusatz"); // mustSupport=true
        // mustSupport Extensions für Line hinzufügen
        if (!strassenanschrift.getLine().isEmpty()) {
            StringType lineElement = strassenanschrift.getLine().get(0);
            lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName", new StringType("Musterstraße")); // Strasse
            lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber", new StringType("1a")); // Hausnummer
            lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-additionalLocator", new StringType("Adresszusatz")); // Adresszusatz
        }

        strassenanschrift.setCity("Musterstadt"); // mustSupport=true
        strassenanschrift.setPostalCode("12345"); // mustSupport=true
        strassenanschrift.setCountry("DE"); // mustSupport=true

        return patient;
    }

    /**
     * Erstellt einen validen ERG-Practitioner gemäß dem ERG-Person-Profil
     */
    public static Practitioner createTestErgPractitioner() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId("erg-practitioner-example");
        practitioner.setMeta(new Meta().addProfile("https://gematik.de/fhir/erg/StructureDefinition/erg-person|1.1.0-RC1"));

        // Narrative hinzufügen (dom-6 Constraint)
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Practitioner Details</div>");
        practitioner.setText(narrative);

        // Identifier: Telematik-ID und USt-ID
        // Telematik-ID (korrekter Type und System)
        practitioner.addIdentifier()
            .setType(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "PRN", null)))
            .setSystem("https://gematik.de/fhir/sid/telematik-id")
            .setValue("3-8831200001234567890-31");

        // Name
        HumanName name = practitioner.addName();
        name.setUse(HumanName.NameUse.OFFICIAL);
        name.setFamily("Schneider");
        // Korrekte Extension für Namenszusatz (Adelstitel)
        name.getFamilyElement().addExtension("http://fhir.de/StructureDefinition/humanname-namenszusatz", new StringType("von"));
        // Korrekte Extension für Vorsatzwort (akademischer Titel)
        name.getFamilyElement().addExtension("http://hl7.org/fhir/StructureDefinition/humanname-own-prefix", new StringType("Prof"));

        name.addGiven("Peter");
        name.addPrefix("Dr. med.");

        // Telecom: Telefonnummer
        practitioner.addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue("+49301234567")
            .setUse(ContactPoint.ContactPointUse.WORK);

        // Qualification: Fachrichtung (mit gültigem Code aus dem ValueSet)
        Practitioner.PractitionerQualificationComponent qualification = practitioner.addQualification();
        qualification.getCode().addCoding()
            .setSystem("http://ihe-d.de/CodeSystems/AerztlicheFachrichtungen") // Korrektes System laut VS
            .setCode("ALLG") // Korrekter Code laut VS (Allgemeinmedizin)
            .setDisplay("Allgemeinmedizin"); // Optionaler Display Name

        // Address
        Address address = practitioner.addAddress();
        address.setType(Address.AddressType.BOTH);
        address.addLine("Hauptstraße 10 Hinterhaus");
        address.setCity("Berlin");
        address.setPostalCode("10115");
        address.setCountry("DE");

        return practitioner;
    }

    /**
     * Erstellt eine valide ERG-Institution gemäß dem ERG-Institution-Profil
     */
    public static Organization createTestErgInstitution() {
        Organization institution = new Organization();
        institution.setId("erg-institution-example");

        // Meta mit Profil URL setzen (inkl. Version)
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erg/StructureDefinition/erg-institution|1.1.0-RC1");
        institution.setMeta(meta);

        // Narrative hinzufügen (optional, aber gut für Lesbarkeit)
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Institutionsdetails</div>");
        institution.setText(narrative);

        // Identifier Slicing (mustSupport=true für das Slicing)
        // Telematik-ID (min=0, max=1, mustSupport=true)
        Identifier telematikId = institution.addIdentifier();
        telematikId.setType(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "PRN", null))) // Gemäß Pattern
            .setSystem("https://gematik.de/fhir/sid/telematik-id") // mustSupport=true
            .setValue("3-TelematikID123"); // mustSupport=true

        // IKNR (min=0, max=1, mustSupport=true)
        Identifier iknr = institution.addIdentifier();
        iknr.setType(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "XX", null))) // Gemäß Pattern
            .setSystem("http://fhir.de/sid/arge-ik/iknr") // mustSupport=true
            .setValue("123456789"); // mustSupport=true

        // BSNR (min=0, max=1, mustSupport=true)
        Identifier bsnr = institution.addIdentifier();
        bsnr.setType(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "BSNR", null))) // Gemäß Pattern
            .setSystem("https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR") 
            .setValue("987654321"); // mustSupport=true

        // KZVAbrechnungsnummer (min=0, max=1, mustSupport=true)
        Identifier kzva = institution.addIdentifier();
        kzva.setType(new CodeableConcept().addCoding(
                new Coding("http://fhir.de/CodeSystem/identifier-type-de-basis", "KZVA", null))) // Gemäß Pattern
            .setSystem("http://fhir.de/sid/kzbv/kzvabrechnungsnummer") 
            .setValue("112233445"); 

        // USt-ID-Nr (min=0, max=1, mustSupport=true)
        Identifier ustId = institution.addIdentifier();
        ustId.setType(new CodeableConcept().addCoding( 
                new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "TAX", null)
            ))
            .setValue("DE987654321"); // mustSupport=true

        // Type Slicing (mustSupport=true für das Slicing)
        // Fachrichtung (min=0, max=*, mustSupport=true)
        CodeableConcept fachrichtung = institution.addType();
        fachrichtung.addCoding()
            .setSystem("http://ihe-d.de/CodeSystems/AerztlicheFachrichtungen") // System aus Binding ValueSet
            .setCode("ALLG") // mustSupport=true, Code aus Binding ValueSet
            .setDisplay("Allgemeinmedizin"); // mustSupport für system und code

        // Name (min=0, max=1, mustSupport=true)
        institution.setName("Gemeinschaftspraxis Musterstadt");

        // Telecom Slicing (mustSupport=true für das Slicing)
        // Telefon (min=0, max=*, mustSupport=true)
        ContactPoint telefon = institution.addTelecom();
        telefon.setSystem(ContactPoint.ContactPointSystem.PHONE) // mustSupport=true
            .setValue("+491234567890"); // mustSupport=true

        // Address Slicing (mustSupport=true für das Slicing)
        // Strassenanschrift (min=0, max=*, mustSupport=true)
        Address strassenanschrift = institution.addAddress();
        strassenanschrift.setType(Address.AddressType.BOTH); // mustSupport=true
        StringType lineElement = strassenanschrift.addLineElement();
        lineElement.setValue("Hauptstraße 123 Adresszusatz"); // mustSupport=true
        // Extensions für Line (mustSupport=true)
        lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName", new StringType("Hauptstraße"));
        lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber", new StringType("123"));
        lineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-additionalLocator", new StringType("Adresszusatz"));

        strassenanschrift.setCity("Musterstadt"); // mustSupport=true
        strassenanschrift.setPostalCode("98765"); // mustSupport=true
        strassenanschrift.setCountry("DE"); // mustSupport=true

        // Postfach (min=0, max=*, mustSupport=true)
        Address postfach = institution.addAddress();
        postfach.setType(Address.AddressType.POSTAL); // mustSupport=true
        StringType postfachLineElement = postfach.addLineElement();
        postfachLineElement.setValue("Postfach 1234"); // mustSupport=true
        // Extension für Postfach (mustSupport=true)
        postfachLineElement.addExtension("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-postBox", new StringType("1234"));

        postfach.setCity("Musterstadt"); // mustSupport=true
        postfach.setPostalCode("98760"); // mustSupport=true
        postfach.setCountry("DE"); // mustSupport=true

        return institution;
    }

    /**
     * Konvertiert Byte-Array in Base64-String
     */
    public static byte[] encodeToBase64(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    /**
     * Lädt und erstellt einen KBV Patient aus der Example-XML-Datei
     */
    public static Patient createKbvPatientFromExample() {
        String resourcePath = "/erp-fhir-examples/KBV_PR_FOR_Patient_example.xml";
        return loadResourceFromFile(resourcePath, Patient.class);
    }

    /**
     * Lädt und erstellt einen KBV Practitioner aus der Example-XML-Datei
     */
    public static Practitioner createKbvPractitionerFromExample() {
        String resourcePath = "/erp-fhir-examples/KBV_PR_FOR_Practitioner_example.xml";
        return loadResourceFromFile(resourcePath, Practitioner.class);
    }

    /**
     * Lädt und erstellt eine KBV Organization aus der Example-XML-Datei
     */
    public static Organization createKbvOrganizationFromExample() {
        String resourcePath = "/erp-fhir-examples/KBV_PR_FOR_Organization_example.xml";
        return loadResourceFromFile(resourcePath, Organization.class);
    }

    /**
     * Generische Methode zum Laden einer FHIR-Ressource aus einer XML-Datei
     * 
     * @param resourcePath Pfad zur XML-Datei im Classpath
     * @param resourceClass Klasse der zu ladenden Ressource
     * @return Die geladene FHIR-Ressource
     */
    private static <T extends Resource> T loadResourceFromFile(String resourcePath, Class<T> resourceClass) {
        try (InputStream inputStream = ErgTestResourceUtil.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource file not found: " + resourcePath);
            }
            
            // XML-Inhalt als String lesen
            String xmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            
            // FhirContext für R4 verwenden
            FhirContext ctx = FhirContext.forR4();
            
            // XML zu FHIR-Ressource parsen
            T resource = ctx.newXmlParser().parseResource(resourceClass, xmlContent);
            
            logger.info("Successfully loaded {} from {}", resourceClass.getSimpleName(), resourcePath);
            
            return resource;
        } catch (Exception e) {
            logger.error("Error loading resource from file: " + resourcePath, e);
            throw new RuntimeException("Failed to load resource from file: " + resourcePath, e);
        }
    }

    /**
     * Lädt eine beliebige FHIR-Ressource aus einer XML-Datei im erp-fhir-examples Ordner
     * 
     * @param fileName Name der XML-Datei (z.B. "KBV_PR_FOR_Patient_example.xml")
     * @param resourceClass Klasse der zu ladenden Ressource
     * @return Die geladene FHIR-Ressource
     */
    public static <T extends Resource> T loadKbvExampleResource(String fileName, Class<T> resourceClass) {
        String resourcePath = "/erp-fhir-examples/" + fileName;
        return loadResourceFromFile(resourcePath, resourceClass);
    }
} 