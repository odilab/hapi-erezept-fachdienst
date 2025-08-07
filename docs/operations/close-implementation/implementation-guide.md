# Implementierungsleitfaden für $close Operation

## Übersicht

Die `$close` Operation beendet den E-Rezept-Workflow nach erfolgter Medikamentenabgabe. Sie:
- Ändert den Task-Status von `in-progress` zu `completed`
- Erstellt eine signierte digitale Quittung (Receipt Bundle)
- Löscht alle verknüpften Communication-Ressourcen
- Speichert MedicationDispense-Informationen

## Voraussetzungen

- [x] CreateOperation ist implementiert und funktioniert
- [x] ActivateOperation ist implementiert und funktioniert  
- [x] AcceptOperation ist implementiert und funktioniert
- [ ] Signatur-Zertifikate sind konfiguriert
- [ ] CAdES-BES Signierung ist implementiert

## Schritt 1: Provider-Klasse erstellen

### 1.1 Verzeichnis erstellen
```bash
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/close
```

### 1.2 Provider kopieren und anpassen
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/close-implementation/java-implementation/CloseOperationProvider.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/close/
```

### 1.3 Kritische Anpassungen im Provider

#### loadPrescriptionBinary() implementieren (Zeile ~304)
```java
private Binary loadPrescriptionBinary(Task task) {
    // Extrahiere HealthCarePrescriptionUuid aus Task Extension
    String healthCarePrescriptionUuid = task.getExtension().stream()
        .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid".equals(ext.getUrl()))
        .map(ext -> ((StringType) ext.getValue()).getValue())
        .findFirst()
        .orElse(null);
    
    if (healthCarePrescriptionUuid == null) {
        return null;
    }
    
    IFhirResourceDao<Binary> binaryDao = daoRegistry.getResourceDao(Binary.class);
    try {
        return binaryDao.read(new IdType("Binary", healthCarePrescriptionUuid));
    } catch (ResourceNotFoundException e) {
        return null;
    }
}
```

#### getInProgressDate() implementieren (Zeile ~315)
```java
private Date getInProgressDate(Task task) {
    // Suche nach lastStatusChange Extension oder nutze lastModified
    Date lastStatusChange = task.getExtension().stream()
        .filter(ext -> "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_LastStatusChange".equals(ext.getUrl()))
        .map(ext -> ((DateTimeType) ext.getValue()).getValue())
        .findFirst()
        .orElse(null);
    
    if (lastStatusChange != null) {
        return lastStatusChange;
    }
    
    // Fallback auf lastModified
    return task.getLastModified() != null ? task.getLastModified() : task.getMeta().getLastUpdated();
}
```

## Schritt 2: Service-Klasse erstellen

### 2.1 Service kopieren
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/close-implementation/java-implementation/CloseTaskService.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/close/
```

### 2.2 CAdES-BES Signierung implementieren

**WICHTIG**: Die echte Signierung benötigt:
- Zertifikat des E-Rezept-Fachdienstes
- Privaten Schlüssel (idealerweise in HSM)
- CAdES-BES Library (z.B. Bouncy Castle)

Beispiel-Implementierung mit Bouncy Castle:
```java
private void signBundle(Bundle bundle, String deviceId) {
    try {
        // Bundle zu XML serialisieren
        String bundleXml = fhirContext.newXmlParser().encodeResourceToString(bundle);
        
        // Lade Zertifikat und privaten Schlüssel
        X509Certificate signingCert = loadSigningCertificate();
        PrivateKey privateKey = loadPrivateKey();
        
        // Erstelle CAdES-BES Signatur
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA")
            .build(privateKey);
        generator.addSignerInfoGenerator(
            new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().build())
            .build(contentSigner, signingCert));
        
        CMSTypedData msg = new CMSProcessableByteArray(bundleXml.getBytes());
        CMSSignedData sigData = generator.generate(msg, true);
        
        String base64Signature = Base64.getEncoder().encodeToString(sigData.getEncoded());
        
        // Füge Signatur zum Bundle hinzu
        Signature signature = new Signature();
        signature.setType(Collections.singletonList(new Coding()
            .setSystem("urn:iso-astm:E1762-95:2013")
            .setCode("1.2.840.113549.1.7.2")
            .setDisplay("CAdES-BES")));
        signature.setWhen(new Date());
        signature.setWho(new Reference("Device/" + deviceId));
        signature.setData(base64Signature.getBytes());
        
        bundle.getMeta().addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_Signature")
            .setValue(signature);
            
    } catch (Exception e) {
        throw new InternalErrorException("Failed to sign receipt: " + e.getMessage());
    }
}
```

## Schritt 3: Integration Test erstellen

### 3.1 Test-Verzeichnis erstellen
```bash
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/close
```

### 3.2 Test-Klasse erstellen
```java
package ca.uhn.fhir.jpa.starter.custom.operation.close;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CloseOperationIntegrationTest extends BaseProviderTest {

    private Task testTask;
    private String secret;
    
    @BeforeEach
    void setupTask() throws Exception {
        super.setUp();
        
        // 1. Create Task
        Parameters createParams = new Parameters();
        createParams.addParameter()
            .setName("workflowType")
            .setValue(new Coding()
                .setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
                .setCode("160"));
        
        Parameters createResult = client
            .operation()
            .onType(Task.class)
            .named("$create")
            .withParameters(createParams)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
            .returnResourceType(Parameters.class)
            .execute();
        
        testTask = (Task) createResult.getParameter().get(0).getResource();
        
        // 2. Activate Task
        Binary signedBundle = createSignedPrescriptionBundle();
        
        Parameters activateParams = new Parameters();
        activateParams.addParameter()
            .setName("ePrescription")
            .setResource(signedBundle);
        
        testTask = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$activate")
            .withParameters(activateParams)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
            .withAdditionalHeader("X-AccessCode", getAccessCode(testTask))
            .returnResourceType(Task.class)
            .execute();
        
        // 3. Accept Task
        Bundle acceptResult = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$accept")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("SMCB_OEFFENTLICHE_APOTHEKE"))
            .withAdditionalHeader("X-AccessCode", getAccessCode(testTask))
            .returnResourceType(Bundle.class)
            .execute();
        
        // Extract secret
        Task acceptedTask = (Task) acceptResult.getEntry().get(0).getResource();
        secret = acceptedTask.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
            .map(Identifier::getValue)
            .findFirst()
            .orElseThrow();
    }
    
    @Test
    void testCloseWithMedicationDispense() {
        // Erstelle MedicationDispense
        MedicationDispense dispense = createMedicationDispense(testTask.getIdElement().getIdPart());
        
        Parameters closeParams = new Parameters();
        Parameters.ParametersParameterComponent rxParam = closeParams.addParameter();
        rxParam.setName("rxDispensation");
        rxParam.addPart()
            .setName("medicationDispense")
            .setResource(dispense);
        
        Bundle result = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$close")
            .withParameters(closeParams)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("SMCB_OEFFENTLICHE_APOTHEKE"))
            .withAdditionalHeader("secret", secret)
            .returnResourceType(Bundle.class)
            .execute();
        
        assertNotNull(result);
        assertEquals(Bundle.BundleType.DOCUMENT, result.getType());
        assertTrue(result.getMeta().hasProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle"));
        
        // Prüfe Composition
        Composition composition = result.getEntry().stream()
            .filter(e -> e.getResource() instanceof Composition)
            .map(e -> (Composition) e.getResource())
            .findFirst()
            .orElse(null);
        
        assertNotNull(composition);
        assertEquals(Composition.CompositionStatus.FINAL, composition.getStatus());
        
        // Prüfe Task-Status
        Task closedTask = client.read()
            .resource(Task.class)
            .withId(testTask.getIdElement())
            .execute();
        
        assertEquals(Task.TaskStatus.COMPLETED, closedTask.getStatus());
    }
    
    @Test
    void testCloseWithoutMedicationDispense_AlreadyExists() {
        // Erstelle und speichere MedicationDispense vorab
        MedicationDispense dispense = createMedicationDispense(testTask.getIdElement().getIdPart());
        client.create()
            .resource(dispense)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("SMCB_OEFFENTLICHE_APOTHEKE"))
            .execute();
        
        // Close ohne Body
        Bundle result = client
            .operation()
            .onInstance(testTask.getIdElement())
            .named("$close")
            .withNoParameters(Parameters.class)
            .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("SMCB_OEFFENTLICHE_APOTHEKE"))
            .withAdditionalHeader("secret", secret)
            .returnResourceType(Bundle.class)
            .execute();
        
        assertNotNull(result);
    }
    
    @Test
    void testCloseWithWrongSecret() {
        assertThrows(ForbiddenOperationException.class, () -> {
            client.operation()
                .onInstance(testTask.getIdElement())
                .named("$close")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("SMCB_OEFFENTLICHE_APOTHEKE"))
                .withAdditionalHeader("secret", "WRONG_SECRET")
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    @Test
    void testCloseWithWrongRole() {
        assertThrows(ForbiddenOperationException.class, () -> {
            client.operation()
                .onInstance(testTask.getIdElement())
                .named("$close")
                .withNoParameters(Parameters.class)
                .withAdditionalHeader("Authorization", "Bearer " + getValidAccessToken("HBA_ARZT"))
                .withAdditionalHeader("secret", secret)
                .returnResourceType(Bundle.class)
                .execute();
        });
    }
    
    private MedicationDispense createMedicationDispense(String taskId) {
        MedicationDispense dispense = new MedicationDispense();
        
        Meta meta = new Meta();
        meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense|1.5");
        dispense.setMeta(meta);
        
        dispense.setStatus(MedicationDispense.MedicationDispenseStatus.COMPLETED);
        dispense.addAuthorizingPrescription(new Reference("Task/" + taskId));
        dispense.setSubject(new Reference().setIdentifier(new Identifier()
            .setSystem("http://fhir.de/sid/gkv/kvid-10")
            .setValue(versichertenKvnr)));
        dispense.setWhenHandedOver(new Date());
        
        return dispense;
    }
    
    private String getAccessCode(Task task) {
        return task.getIdentifier().stream()
            .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
            .map(Identifier::getValue)
            .findFirst()
            .orElse("");
    }
    
    private Binary createSignedPrescriptionBundle() {
        // TODO: Implementiere signiertes Test-Bundle
        Binary binary = new Binary();
        binary.setContentType("application/pkcs7-mime");
        binary.setContent("TEST_SIGNED_BUNDLE".getBytes());
        return binary;
    }
}
```

## Schritt 4: application.yaml anpassen

Füge den Provider zur Konfiguration hinzu:

```yaml
# In /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/application.yaml
hapi:
  fhir:
    custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.close.CloseOperationProvider
```

## Schritt 5: Dependencies prüfen

Stelle sicher, dass folgende Dependencies vorhanden sind:

```xml
<!-- In pom.xml -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
```

## Schritt 6: Build und Test

```bash
# Build ohne Tests
mvn clean install -DskipTests

# Nur Close-Tests ausführen
mvn test -Dtest=CloseOperationIntegrationTest

# Alle Tests
mvn test
```

## Schritt 7: Manuelle Verifikation

### 7.1 Server starten
```bash
mvn spring-boot:run
```

### 7.2 Vollständigen Workflow testen
```bash
# 1. Create
curl -X POST "http://localhost:8080/fhir/Task/\$create" \
  -H "Authorization: Bearer ${ARZT_TOKEN}" \
  -H "Content-Type: application/fhir+json" \
  -d '{"resourceType":"Parameters","parameter":[{"name":"workflowType","valueCoding":{"system":"https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType","code":"160"}}]}'

# 2. Activate (mit Task-ID und AccessCode aus Create)
# 3. Accept (mit AccessCode)
# 4. Close (mit Secret aus Accept)
curl -X POST "http://localhost:8080/fhir/Task/${TASK_ID}/\$close?secret=${SECRET}" \
  -H "Authorization: Bearer ${APOTHEKE_TOKEN}" \
  -H "Content-Type: application/fhir+json" \
  -d '{"resourceType":"Parameters","parameter":[...]}'
```

## Bekannte Probleme und Lösungen

### Problem 1: Signatur-Zertifikat fehlt
**Lösung**: Test-Zertifikat verwenden oder Mock-Signatur für Entwicklung

### Problem 2: MedicationDispense-Validierung schlägt fehl
**Lösung**: Profile in Meta korrekt setzen, Validator-Konfiguration prüfen

### Problem 3: Communications werden nicht gelöscht
**Lösung**: SearchParameter für Communication.basedOn muss konfiguriert sein

## Checkliste vor Produktion

- [ ] Echte Signatur-Zertifikate konfiguriert
- [ ] HSM-Integration für private Schlüssel
- [ ] Performance-Tests durchgeführt
- [ ] Alle Testszenarien grün
- [ ] Audit-Logging verifiziert
- [ ] Fehlerbehandlung vollständig
- [ ] Dokumentation aktualisiert