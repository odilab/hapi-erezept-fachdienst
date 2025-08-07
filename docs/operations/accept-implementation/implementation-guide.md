# Implementierungsleitfaden für Accept Operation

## Übersicht

Die `$accept` Operation ermöglicht es Apotheken (und Kostenträgern bei DiGA), ein E-Rezept zur Bearbeitung zu übernehmen. Dies ist eine **Instance-Operation**, die auf einem bestehenden Task aufgerufen wird.

**Endpunkt**: `POST /Task/{id}/$accept?ac={accessCode}`

## Voraussetzungen

- [x] CreateOperation ist implementiert (wird für Tests benötigt)
- [x] ActivateOperation ist implementiert (Task muss im Status "ready" sein)
- [ ] Verständnis der Task-Workflow States
- [ ] Zugriff auf Testcontainer-Setup für Integrationstests

## Schritt-für-Schritt Implementierung

### Schritt 1: Provider-Klasse erstellen

1. **Verzeichnis erstellen** (falls nicht vorhanden):
```bash
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/accept
```

2. **Provider kopieren**:
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/accept-implementation/java-implementation/AcceptOperationProvider.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/accept/
```

3. **Anpassungen vornehmen**:
   - Zeile 241-250: `loadPrescriptionBinary()` implementieren - Binary aus DB laden
   - Zeile 319-327: `addConsentIfExists()` implementieren - Consent-Suche für PKV

### Schritt 2: Service-Klasse erstellen

1. **Service kopieren**:
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/accept-implementation/java-implementation/AcceptTaskService.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/accept/
```

2. **Crypto-Implementierung ergänzen**:
   - Zeile 70-80: PKCS7/CMS Dekodierung implementieren (Bouncy Castle)
   - Zeile 180-190: Signaturvalidierung implementieren

### Schritt 3: application.yaml anpassen

**Datei**: `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/application.yaml`

Bei der Zeile mit `custom-provider-classes:` den AcceptOperationProvider hinzufügen:

```yaml
hapi:
  fhir:
    custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider
```

### Schritt 4: Tests implementieren

1. **Test-Verzeichnis erstellen**:
```bash
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/accept
```

2. **Test-Klasse kopieren**:
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/accept-implementation/java-implementation/AcceptOperationIntegrationTest.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/accept/
```

3. **Helper-Methoden anpassen**:
   - Zeile 60-75: Mock-Signatur durch echte Signatur ersetzen (siehe `FachdienstToolIntegrationTest`)

### Schritt 5: Abhängigkeiten prüfen

Stelle sicher, dass folgende Abhängigkeiten in `pom.xml` vorhanden sind:

```xml
<!-- Für Crypto-Operationen -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
    <version>1.70</version>
</dependency>

<!-- Für SecureRandom -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>
```

## Validierungen Checkliste

Die folgenden Validierungen MÜSSEN implementiert werden:

- [x] **AccessCode prüfen** (A_19167-04)
  - Query-Parameter `?ac=...` oder Header `X-AccessCode`
  - Muss mit Task.identifier:AccessCode übereinstimmen
  
- [x] **Task Status prüfen** (A_19168-01)
  - `draft` → HTTP 409
  - `completed` → HTTP 409
  - `in-progress` → HTTP 409 (mit spezieller Meldung wenn owner = telematik-id)
  - `cancelled` → HTTP 410
  - Nur `ready` ist erlaubt

- [x] **Profession OID prüfen** (A_19166-01, A_25993)
  - Apotheken für normale Rezepte
  - Kostenträger nur für DiGA (Flowtype 162)

- [x] **Einlösefrist prüfen** (A_23539-01)
  - Task.ExpiryDate + 24h muss in der Zukunft liegen

- [x] **MVO Startdatum prüfen** (A_22635-02)
  - Bei Mehrfachverordnungen muss Startdatum erreicht sein

## Geschäftslogik Checkliste

- [x] **Secret generieren** (A_19169-01)
  - 256 Bit (32 Bytes) Zufallszahl
  - Hexadezimal kodiert (64 Zeichen)
  
- [x] **Status Update**
  - Task.status auf `in-progress`
  - Task.lastModified aktualisieren
  
- [x] **Owner setzen** (A_24174)
  - Telematik-ID aus Access Token in Task.owner
  
- [x] **Response Bundle erstellen**
  - Type: collection
  - Meta-Profile setzen
  - Task + Binary (+ optional Consent)

## Tests ausführen

```bash
# Einzelnen Test ausführen
mvn test -Dtest=AcceptOperationIntegrationTest

# Alle Operation-Tests
mvn test -Dtest=*OperationIntegrationTest

# Mit Coverage
mvn clean test jacoco:report
```

## Troubleshooting

### Problem: Task nicht gefunden
**Lösung**: Stelle sicher, dass Task existiert und AccessCode korrekt ist

### Problem: Wrong status
**Lösung**: Task muss im Status "ready" sein. Erst create, dann activate, dann accept!

### Problem: Signaturvalidierung schlägt fehl
**Lösung**: Prüfe Bouncy Castle Konfiguration und TSL-Manager

### Problem: Keine Binary-Resource im Response
**Lösung**: Implementiere `loadPrescriptionBinary()` korrekt

## Offene Punkte

1. **Binary-Loading**: Die Methode `loadPrescriptionBinary()` muss an die tatsächliche DB-Struktur angepasst werden
2. **Consent-Suche**: Implementation abhängig von Consent-Repository Struktur
3. **PKCS7-Dekodierung**: Bouncy Castle Integration für Signatur-Handling
4. **MVO-Validierung**: Vollständige Implementierung wenn KBV-Bundle dekodiert werden kann

## Referenzen

- [C++ Implementierung](../reference-implementation/validation-rules.md)
- [Anforderungen](../specification/relevant-requirements.md)
- [OperationDefinition](../specification/OperationDefinition.json)
- [Test-Szenarien](../tests/test-scenarios.md)