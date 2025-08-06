# implement-erp-operation

Erstelle eine neue E-Rezept FHIR Operation nach den gematik Spezifikationen unter Berücksichtigung der C++ Referenzimplementierung. Dieses Command analysiert ALLE relevanten Dateien und erstellt einen vollständigen Implementierungsleitfaden, mit dem die Operation komplett umgesetzt werden kann inklusive aller Tests.

## Verwendung

```
/implement-erp-operation <operation-name>
```

Beispiel: `/implement-erp-operation activate`

Verfügbare Operationen: abort, accept, activate, close, dispense, reject

## VOLLSTÄNDIGE IMPLEMENTIERUNGSANLEITUNG

**ZIEL**: Nach Durchführung dieser Analyse musst du in der Lage sein, die Operation vollständig zu implementieren. Jeder Schritt muss EXTREM gründlich durchgeführt werden!

### SCHRITT 1: ANALYSE ALLER VORHANDENEN OPERATIONEN

**KRITISCH**: Analysiere ZUERST alle vorhandenen Operationen im Verzeichnis:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation
```

Untersuche BESONDERS:
- `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/create/CreateOperationProvider.java` - Dies ist deine HAUPT-REFERENZ!

Extrahiere und verstehe aus CreateOperationProvider:
1. **Access Token Extraktion**: Genau analysieren wie `authorizationService.validateAndExtractAccessToken(theRequestDetails)` funktioniert
2. **Profession OID Prüfung**: Verstehe das ALLOWED_PROFESSION_OIDS Pattern und die `validateProfessionOid()` Methode
3. **Service-Aufteilung**: Wie wird zwischen Provider und Service (CreateTaskService) aufgeteilt
4. **Audit-Logging Pattern**: Wie wird `auditService.createRestAuditEvent()` verwendet
5. **Output Parameter Erstellung**: Wie werden die Meta-Profile gesetzt
6. **Fehlerbehandlung**: Welche FHIR Exceptions werden verwendet

Analysiere auch die Services:
- `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/AuthorizationService.java`
- `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/AuditService.java`

### SCHRITT 2: OPERATIONDEFINITION ANALYSE

Lies die komplette OperationDefinition:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/resources/OperationDefinition-{OperationName}Operation.json
```

Extrahiere ALLE Details:
- name, code, kind, description
- instance (true/false) - WICHTIG für Task-ID in URL
- idempotent Flag
- ALLE Input Parameter mit:
  - name, use, min, max, type, documentation
  - Bei Binary: Erwarteter Content (z.B. signiertes Bundle)
- ALLE Output Parameter mit:
  - name, use, min, max, type
  - Profile URLs für Meta
- inputProfile und outputProfile URLs (WICHTIG für Meta-Profile!)

### SCHRITT 3: XML-SPEZIFIKATION TIEFENANALYSE

**WICHTIGSTE DATEI** - Durchsuche VOLLSTÄNDIG:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/gemSpec_FD_eRp_V2.3.0.xml
```

Suche nach ALLEN Erwähnungen der Operation (z.B. "$activate", "activate", "Aktivierung"):
1. **Anforderungs-IDs**: ALLE A_xxxxx und ERPF_xxxxx IDs notieren
2. **Berechtigungsmatrix**: Welche Rollen/Profession OIDs dürfen die Operation ausführen
3. **Status-Anforderungen**: In welchem Status muss der Task sein
4. **Validierungsregeln**: ALLE Prüfungen die durchgeführt werden müssen
5. **Fehlerszenarien**: Welche Fehler können auftreten mit HTTP-Codes
6. **Audit-Anforderungen**: Was muss protokolliert werden
7. **Besondere Regeln**: Z.B. KVNR-Prüfungen, AccessCode-Validierungen

### SCHRITT 4: ADOC-DOKUMENTATION ANALYSE

Lies die ergänzende Dokumentation:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/erp_bereitstellen.adoc
```

Suche nach:
- Workflow-Beschreibungen für die Operation
- Sequenzdiagramme
- Beispielszenarien
- Zusätzliche Geschäftsregeln

### SCHRITT 5: C++ REFERENZIMPLEMENTIERUNG - GOLDSTANDARD

**EXTREM WICHTIG**: Die C++ Implementierung ist die REFERENZ! Analysiere VOLLSTÄNDIG:

Header-Datei:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/{OperationName}TaskHandler.hxx
```

Implementation:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/{OperationName}TaskHandler.cxx
```

Extrahiere ALLE:
1. **ErpExpect Statements**: JEDE Validierung muss in Java umgesetzt werden!
   - Notiere EXAKTE Fehlermeldungen
   - HTTP Status Codes
   - Validierungsbedingungen
2. **Geschäftslogik-Flow**: 
   - Reihenfolge der Operationen
   - Datenbank-Zugriffe
   - Status-Updates
3. **Spezielle Handler**: handleGeneric vs handleRequest Patterns
4. **Crypto/Signatur**: Falls Signaturvalidierung nötig
5. **AccessCode Handling**: Generierung oder Validierung
6. **KVNR/TelematikID**: Prüfungen und Zuordnungen

Schaue auch in:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/tools
```

### SCHRITT 6: C++ TESTS ANALYSE

**KRITISCH für Testabdeckung** - Analysiere Tests in:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/test/workflow-test/
```

Suche nach Tests für die Operation:
- `{OperationName}Test.cxx`
- `A_*_{OperationName}.cxx` 
- Tests in `ErpWorkflowTest.cxx`

Extrahiere:
1. **Erfolgsszenarien**: Alle positiven Testfälle
2. **Fehlerszenarien**: Alle negativen Testfälle mit erwarteten Fehlern
3. **Edge Cases**: Grenzfälle
4. **Testdaten**: Beispiel-Bundles, Tasks, etc.
5. **Assertions**: Was wird geprüft

### SCHRITT 7: JAVA TEST-REFERENZ ANALYSE

**WICHTIG**: Tests bauen oft aufeinander auf! Die meisten Operationen benötigen einen vorher erstellten Task, daher ist die CREATE Operation IMMER die erste die durchgeführt werden muss.

#### 7.1 Analysiere ALLE vorhandenen Integrationstests

**KRITISCH**: Schaue dir ALLE Operation-Tests an, um Abhängigkeiten zu verstehen:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/
```

Verstehe die Test-Reihenfolge:
1. Create (erstellt Task mit Status draft)
2. Activate (aktiviert Task → Status ready)
3. Accept (Apotheke akzeptiert → Status in-progress)
4. Close/Dispense (Abgabe → Status completed)
5. Abort/Reject (Abbruch-Szenarien)

#### 7.2 CreateOperationIntegrationTest als Basis

Analysiere:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/create/CreateOperationIntegrationTest.java
```

Verstehe:
- BaseProviderTest Vererbung
- Testcontainer Verwendung
- Access Token Generation mit `getValidAccessToken()`
- Client-Aufruf Pattern
- Assertions für FHIR Ressourcen

#### 7.3 Signatur und Validierungs-Tools

**SEHR WICHTIG** für Operationen die Signaturen benötigen (z.B. activate):
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/FachdienstToolIntegrationTest.java
```

Analysiere hier:
- Wie werden Bundles signiert
- Wie werden Signaturen validiert
- Helper-Methoden für Test-Signaturen
- Crypto-Tools und Zertifikate

### SCHRITT 8: CUSTOM VALIDATOR VERSTEHEN

Für FHIR-Validierungen analysiere:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/interceptor/CustomValidator.java
```

Verstehe wie komplexe FHIR-Strukturen validiert werden können.

### SCHRITT 9: IMPLEMENTIERUNG PLANEN

Basierend auf ALLEN gesammelten Informationen:

#### 9.1 Provider-Klasse

Erstelle Verzeichnis (falls nicht vorhanden):
```
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}
```

Erstelle Provider:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}OperationProvider.java
```

Provider MUSS enthalten:
```java
@Component
public class {OperationName}OperationProvider implements IResourceProvider {
    
    // EXAKT diese Dependencies aus CreateOperationProvider
    private final FhirContext fhirContext;
    private final DaoRegistry daoRegistry;
    private final AccessTokenService accessTokenService;
    private final AuthorizationService authorizationService;
    private final {OperationName}TaskService {operation}TaskService; // Falls Service nötig
    private final AuditService auditService;
    
    // ALLOWED_PROFESSION_OIDS basierend auf Spezifikation
    private static final List<String> ALLOWED_PROFESSION_OIDS = Arrays.asList(
        // Aus XML-Spezifikation extrahierte OIDs
    );
    
    @Autowired
    public {OperationName}OperationProvider(...) {
        // Constructor
    }
    
    @Override
    public Class<Task> getResourceType() {
        return Task.class;
    }
    
    @Operation(name = "${operation}", idempotent = {aus OperationDefinition}, type = Task.class)
    public Parameters {operation}Operation(
        // EXAKTE Parameter aus OperationDefinition
        @OperationParam(name = "...", min = X) ... parameterName,
        RequestDetails theRequestDetails) {
        
        // 1. Input-Parameter Validierung (ALLE aus OperationDefinition)
        // 2. Access Token Extraktion:
        AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
        
        // 3. Profession OID Prüfung:
        validateProfessionOid(accessToken);
        
        // 4. Bei Instance-Operation: Task laden
        // String taskId = theRequestDetails.getId().getIdPart();
        
        // 5. ALLE Validierungen aus C++ ErpExpect
        
        // 6. Geschäftslogik (EXAKT wie C++)
        
        // 7. Output Parameters mit Meta-Profil aus OperationDefinition
        
        // 8. Audit-Logging
        
        return outputParameters;
    }
}
```

#### 9.2 Service-Klasse (bei komplexer Logik)

Falls die Geschäftslogik komplex ist, erstelle:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}TaskService.java
```

#### 9.3 Integration Tests

Erstelle Test-Verzeichnis:
```
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}
```

Erstelle Test:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}OperationIntegrationTest.java
```

Test MUSS:
- Von BaseProviderTest erben
- Testcontainer verwenden (wie in CreateOperationIntegrationTest)
- ALLE Szenarien aus C++ Tests abdecken
- Access Token mit `getValidAccessToken()` holen
- Abhängige Operationen berücksichtigen (z.B. erst create, dann activate)
- Bei Signatur-Tests: FachdienstToolIntegrationTest Helper nutzen

#### 9.4 Provider Registrierung in application.yaml

**WICHTIG**: Füge den Provider zur application.yaml hinzu:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/application.yaml
```

Bei der Zeile mit `custom-provider-classes:` füge deine Provider-Klasse hinzu:
```yaml
custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.{operation}.{OperationName}OperationProvider
```

### SCHRITT 10: FINALE CHECKLISTE

Bevor die Implementierung beginnt, stelle sicher dass du folgende Informationen hast:

- [ ] ALLE Parameter aus OperationDefinition mit Typen und Constraints
- [ ] ALLE erlaubten Profession OIDs aus XML-Spezifikation
- [ ] ALLE ErpExpect Validierungen aus C++ mit exakten Fehlermeldungen
- [ ] Kompletter Geschäftslogik-Flow aus C++
- [ ] ALLE Testszenarien aus C++ Tests
- [ ] Meta-Profile URLs für Output
- [ ] Audit-Log Details (was muss geloggt werden)
- [ ] HTTP Status Codes für alle Fehlerszenarien

## AUSGABE NACH ANALYSE

Erstelle einen detaillierten Implementierungsplan mit:

1. **Zusammenfassung der Operation**
   - Was macht die Operation
   - Wer darf sie ausführen
   - Input/Output Parameter

2. **Validierungsregeln** (nummeriert)
   - Jede ErpExpect aus C++
   - Jede Regel aus XML-Spec

3. **Implementierungs-Pseudocode**
   - Schritt-für-Schritt was gemacht werden muss

4. **Vollständiges Code-Skelett**
   - Provider-Klasse mit allen Methoden
   - Service-Klasse falls nötig
   - Test-Klasse mit allen Testfällen
   - Helper-Methoden für abhängige Operationen (z.B. createTaskForTest())

5. **application.yaml Änderung**
   - Exakte Zeile die hinzugefügt werden muss

6. **Offene Fragen**
   - Falls etwas unklar ist

### SCHRITT 11: DOKUMENTATIONS-ORDNER ERSTELLEN

**WICHTIG**: Nach der vollständigen Analyse aller Dateien, erstelle einen strukturierten Ordner mit ALLEN gesammelten Informationen:

#### 11.1 Ordnerstruktur erstellen

Erstelle folgende Verzeichnisstruktur:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/{operation}-implementation/
├── README.md                          # Hauptdokumentation mit Verlinkungen
├── specification/                      # Offizielle Spezifikationen
│   ├── OperationDefinition.json       # Kopie der OperationDefinition
│   ├── relevant-requirements.md       # Extrahierte Anforderungen aus XML
│   └── workflow-diagrams/             # Diagramme aus ADOC
├── examples/                          # Beispieldateien
│   ├── request/                       # Beispiel-Requests
│   │   ├── valid-request-1.json
│   │   ├── valid-request-2.json
│   │   └── edge-cases/
│   ├── response/                      # Beispiel-Responses
│   │   ├── success-response.json
│   │   └── error-responses/
│   └── test-data/                     # Testdaten aus C++
│       ├── test-bundles/
│       ├── test-tasks/
│       └── test-certificates/
├── reference-implementation/          # C++ Referenz
│   ├── handler-analysis.md           # Analyse des C++ Handlers
│   ├── validation-rules.md           # Alle ErpExpect Statements
│   └── code-snippets/                # Wichtige Code-Ausschnitte
├── java-implementation/              # Java Implementierung
│   ├── provider-template.java        # Vollständiges Provider Template
│   ├── service-template.java         # Service Template (falls nötig)
│   ├── test-template.java            # Test Template mit allen Szenarien
│   └── helper-methods.java           # Wiederverwendbare Hilfsmethoden
├── tests/                            # Testszenarien
│   ├── test-scenarios.md             # Alle Testfälle dokumentiert
│   ├── success-cases.md              # Positive Tests
│   ├── error-cases.md                # Negative Tests mit Fehlercodes
│   └── dependencies.md               # Abhängigkeiten zu anderen Operationen
└── implementation-guide.md           # Schritt-für-Schritt Anleitung

```

#### 11.2 Inhalte der Dateien

**README.md** - Haupteinstiegspunkt mit:
- Kurzbeschreibung der Operation
- Verlinkungen zu allen wichtigen Dateien
- Quick-Start Guide
- Checkliste für Implementierung

**specification/relevant-requirements.md** - Strukturiert nach:
- Anforderungs-IDs (A_xxxxx, ERPF_xxxxx)
- Berechtigungsmatrix
- Status-Anforderungen
- Validierungsregeln
- Fehlerszenarien mit HTTP-Codes

**examples/** - Vollständige Beispiele:
- Mindestens 3 valide Request-Beispiele
- Alle möglichen Error-Response Beispiele
- Testdaten aus C++ Tests kopiert und angepasst
- Edge-Cases dokumentiert

**reference-implementation/validation-rules.md** - ALLE ErpExpect aus C++:
```markdown
## Validierungsregeln aus C++ Referenz

### 1. Task Status Validierung
- **Regel**: Task muss im Status "ready" sein
- **C++ Code**: `ErpExpect(task.status() == Task::Status::ready, HttpStatus::Conflict, "Task must be in status ready")`
- **Java Umsetzung**: `if (!task.getStatus().equals(Task.TaskStatus.READY)) { throw new InvalidRequestException("Task must be in status ready"); }`
- **Fehlercode**: 409 Conflict

### 2. AccessCode Validierung
...
```

**java-implementation/provider-template.java** - Vollständiges Template mit:
- Allen Imports
- Korrekten Annotations
- Vollständiger Methodensignatur
- Kommentaren wo spezifische Logik eingefügt werden muss
- Verweis auf relevante C++ Stellen

**tests/test-scenarios.md** - Strukturierte Testfälle:
```markdown
## Testszenarien für {Operation}

### Vorbedingungen
- Task wurde mit CREATE erstellt (siehe `CreateOperationIntegrationTest`)
- Task ist im Status X
- Gültiges Access Token mit Profession OID Y

### Success Cases
1. **Standard {Operation}**
   - Input: [Verweis auf examples/request/valid-request-1.json]
   - Expected: Task Status wird zu Z
   - Assertions: Status, AuditLog, Output Parameter

### Error Cases
1. **Falscher Task Status**
   - Input: Task im Status "draft"
   - Expected: 409 Conflict
   - Message: "Task must be in status ready"
```

**implementation-guide.md** - Detaillierte Schritt-für-Schritt Anleitung:
```markdown
# Implementierungsleitfaden für {Operation}

## Übersicht
Diese Operation [Beschreibung] ...

## Voraussetzungen
- [ ] CreateOperation ist implementiert (benötigt für Tests)
- [ ] Verstehen der Task-Workflow States
- [ ] Zugriff auf Testcontainer-Setup

## Schritt 1: Provider erstellen
1. Kopiere `java-implementation/provider-template.java` nach:
   `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}OperationProvider.java`

2. Passe die TODOs im Template an:
   - Zeile 45: Füge ALLOWED_PROFESSION_OIDS ein (siehe `specification/relevant-requirements.md#berechtigungsmatrix`)
   - Zeile 67: Implementiere Validierung 1 (siehe `reference-implementation/validation-rules.md#1`)

## Schritt 2: Tests implementieren
1. Verwende `java-implementation/test-template.java`
2. Testdaten findest du in `examples/test-data/`
3. Für Signatur-Tests siehe `examples/test-data/test-certificates/`

## Schritt 3: application.yaml anpassen
Füge in Zeile X hinzu:
```yaml
custom-provider-classes: ...,ca.uhn.fhir.jpa.starter.custom.operation.{operation}.{OperationName}OperationProvider
```

## Verifikation
- [ ] Alle Tests aus `tests/test-scenarios.md` sind grün
- [ ] Code Coverage > 80%
- [ ] Audit-Logs werden korrekt geschrieben
```

#### 11.3 Automatisches Befüllen

Der Befehl soll:
1. Alle relevanten Dateien analysieren
2. Automatisch Beispiele extrahieren
3. Code-Snippets mit Kontext speichern
4. Querverweise zwischen Dateien erstellen
5. TODOs an Stellen einfügen wo manuelle Anpassung nötig ist

### SCHRITT 12: FINALE AUSGABE

Nach Abschluss aller Analysen und Erstellung des Dokumentationsordners:

1. **Zusammenfassung erstellen**
   - Kurze Übersicht was analysiert wurde
   - Pfad zum erstellten Dokumentationsordner
   - Wichtigste Erkenntnisse

2. **Nächste Schritte**
   - Verweis auf `implementation-guide.md` für die Umsetzung
   - Hinweis auf kritische Punkte die Aufmerksamkeit benötigen
   - Empfehlung welche Dateien zuerst gelesen werden sollten

3. **Verifikation**
   - Bestätige dass alle Dateien erstellt wurden
   - Prüfe dass alle Beispiele vollständig sind
   - Stelle sicher dass alle Querverweise funktionieren

**REMEMBER**: Das Ziel ist eine VOLLSTÄNDIGE Implementierung. Jedes Detail muss erfasst werden!

*** CRITICAL AFTER YOU ARE DONE RESEARCHING AND EXPLORING THE CODEBASE BEFORE YOU START WRITING THE IMPLEMENTIERUNGSLEITFADEN ***

*** ULTRATHINK ABOUT THE IMPLEMENTIERUNGSLEITFADEN AND PLAN YOUR APPROACH THEN START WRITING THE IMPLEMENTIERUNGSLEITFADEN ***

