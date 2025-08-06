# execute-erp-operation

Führe die Implementierung einer E-Rezept FHIR Operation basierend auf dem strukturierten Dokumentationsordner aus, der vom `implement-erp-operation` Command erstellt wurde.

## Verwendung

```
/execute-erp-operation <operation-name>
```

Beispiel: `/execute-erp-operation activate`

## VORAUSSETZUNGEN

**WICHTIG**: Dieses Command erwartet, dass zuvor `/implement-erp-operation <operation-name>` ausgeführt wurde und der Dokumentationsordner unter `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/{operation}-implementation/` existiert.

## IMPLEMENTIERUNGS-AUSFÜHRUNG

### SCHRITT 1: DOKUMENTATIONSORDNER VALIDIEREN

Prüfe zuerst, ob der Dokumentationsordner existiert und vollständig ist:

```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/{operation}-implementation/
```

Verifiziere dass folgende Dateien vorhanden sind:
- `README.md`
- `implementation-guide.md`
- `java-implementation/provider-template.java`
- `java-implementation/test-template.java`
- `specification/relevant-requirements.md`
- `reference-implementation/validation-rules.md`

Falls der Ordner nicht existiert, führe zuerst aus:
```
/implement-erp-operation {operation}
```

### SCHRITT 2: IMPLEMENTATION GUIDE LADEN

Lies und folge den Anweisungen aus:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/{operation}-implementation/implementation-guide.md
```

### SCHRITT 3: VERZEICHNISSTRUKTUR ERSTELLEN

Erstelle die notwendigen Verzeichnisse basierend auf dem Guide:

```bash
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}
mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}
```

### SCHRITT 4: PROVIDER-KLASSE IMPLEMENTIEREN

1. **Template kopieren**:
   - Kopiere `docs/operations/{operation}-implementation/java-implementation/provider-template.java`
   - Nach: `src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{Operation}OperationProvider.java`

2. **TODOs im Template bearbeiten**:
   - Suche nach allen `// TODO:` Kommentaren
   - Jeder TODO verweist auf eine spezifische Stelle in der Dokumentation
   - Beispiel: `// TODO: Insert ALLOWED_PROFESSION_OIDS from specification/relevant-requirements.md#berechtigungsmatrix`

3. **Validierungen implementieren**:
   - Nutze `reference-implementation/validation-rules.md`
   - Jede Regel hat:
     - C++ Code
     - Java Übersetzung
     - Fehlercode
   - Implementiere in der Reihenfolge wie dokumentiert

4. **Geschäftslogik**:
   - Folge dem Flow aus `reference-implementation/handler-analysis.md`
   - Bei Unklarheiten: Schaue in die referenzierten C++ Code-Snippets

### SCHRITT 5: SERVICE-KLASSE (FALLS VORHANDEN)

Wenn `java-implementation/service-template.java` existiert:

1. Kopiere das Template nach:
   ```
   src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{Operation}TaskService.java
   ```

2. Implementiere die Service-Methoden gemäß Template

### SCHRITT 6: INTEGRATIONSTESTS IMPLEMENTIEREN

1. **Test-Template kopieren**:
   - Von: `docs/operations/{operation}-implementation/java-implementation/test-template.java`
   - Nach: `src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{Operation}OperationIntegrationTest.java`

2. **Testdaten einbinden**:
   - Nutze Beispiele aus `examples/request/`
   - Verwende Testdaten aus `examples/test-data/`
   - Für Fehlerszenarien: `examples/response/error-responses/`

3. **Test-Szenarien implementieren**:
   - Folge `tests/test-scenarios.md`
   - Jedes Szenario hat:
     - Vorbedingungen
     - Input (mit Verweis auf Beispieldatei)
     - Expected Result
     - Assertions

4. **Abhängigkeiten beachten**:
   - Prüfe `tests/dependencies.md`
   - Implementiere Helper-Methoden für abhängige Operationen
   - Nutze `java-implementation/helper-methods.java` falls vorhanden

### SCHRITT 7: PROVIDER REGISTRIEREN

1. **application.yaml anpassen**:
   - Öffne `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/application.yaml`
   - Finde `custom-provider-classes:` (ca. Zeile 311)
   - Füge hinzu wie in `implementation-guide.md` beschrieben

### SCHRITT 8: VALIDIERUNG DER IMPLEMENTIERUNG

Führe die Validierungsschritte aus:

1. **Kompilierung**:
   ```bash
   mvn clean compile
   ```

2. **Unit Tests**:
   ```bash
   mvn test -Dtest={Operation}OperationIntegrationTest
   ```

3. **Checkliste aus implementation-guide.md**:
   - [ ] Alle Validierungen implementiert
   - [ ] Alle Test-Szenarien grün
   - [ ] Audit-Logging funktioniert
   - [ ] HTTP Status Codes korrekt

### SCHRITT 9: CODE REVIEW

Prüfe gegen die Referenzen:

1. **Validierungen vollständig?**
   - Vergleiche mit `reference-implementation/validation-rules.md`
   - Alle ErpExpect Statements umgesetzt?

2. **Tests vollständig?**
   - Alle Szenarien aus `tests/test-scenarios.md` abgedeckt?
   - Success und Error Cases implementiert?

3. **Meta-Profile korrekt?**
   - Output Profile URLs aus `specification/relevant-requirements.md` verwendet?

### SCHRITT 10: INTEGRATION TESTS MIT BEISPIELDATEN

Teste mit den bereitgestellten Beispielen:

```bash
# Führe Tests mit verschiedenen Beispiel-Requests aus
mvn test -Dtest={Operation}OperationIntegrationTest#testWithExample1
mvn test -Dtest={Operation}OperationIntegrationTest#testWithExample2
```

## FEHLERBEHANDLUNG

Bei Problemen nutze die Referenzen im Dokumentationsordner:

1. **Validierungsfehler**: 
   - Prüfe `reference-implementation/validation-rules.md`
   - Vergleiche mit C++ Code in `code-snippets/`

2. **Test-Fehler**:
   - Prüfe `tests/error-cases.md` für erwartete Fehler
   - Vergleiche mit `examples/response/error-responses/`

3. **Unklare Anforderungen**:
   - Konsultiere `specification/relevant-requirements.md`
   - Prüfe Original-Dateien die dort referenziert sind

## AUSGABE

Nach erfolgreicher Implementierung:

1. **Zusammenfassung erstellen**:
   ```markdown
   ## Implementierung {Operation} Operation abgeschlossen
   
   ### Erstellte Dateien:
   - Provider: {Operation}OperationProvider.java
   - Service: {Operation}TaskService.java (falls erstellt)
   - Test: {Operation}OperationIntegrationTest.java
   - application.yaml aktualisiert
   
   ### Implementierte Features:
   - {X} Validierungsregeln
   - {Y} Test-Szenarien
   - Profession OIDs: {Liste}
   
   ### Test-Ergebnisse:
   - Alle {Z} Tests erfolgreich
   - Code Coverage: {%}
   ```

2. **Dokumentation aktualisieren**:
   - Markiere in `implementation-guide.md` alle erledigten Schritte
   - Füge ggf. Notizen zu Besonderheiten hinzu

3. **Nächste Schritte**:
   - Integrationstests mit anderen Operationen
   - Performance-Tests bei Bedarf
   - Code-Review durch Team

**REMEMBER**: 
- Folge EXAKT dem strukturierten Dokumentationsordner
- Jeder TODO im Template hat einen Verweis auf die entsprechende Dokumentation
- Bei Unklarheiten sind alle Informationen im Dokumentationsordner zu finden