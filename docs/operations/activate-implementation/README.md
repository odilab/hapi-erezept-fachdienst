# Activate Operation - Implementierungsdokumentation

## Übersicht

Diese Dokumentation enthält alle notwendigen Informationen zur Implementierung der `$activate` Operation für das E-Rezept System nach gematik Spezifikation.

## Quick Start

1. **Provider erstellen**: Kopiere `java-implementation/ActivateOperationProvider.java` 
2. **Service erstellen**: Kopiere `java-implementation/ActivateTaskService.java`
3. **Tests implementieren**: Nutze `java-implementation/ActivateOperationIntegrationTest.java` als Vorlage
4. **Provider registrieren**: Füge Provider in `application.yaml` hinzu

## Dokumentationsstruktur

### 📁 specification/
- `OperationDefinition.json` - Offizielle FHIR OperationDefinition
- `relevant-requirements.md` - Alle Anforderungen aus gemSpec_FD_eRp
- `workflow-overview.md` - Ablauf der activate Operation

### 📁 examples/
- **request/** - Beispiel-Requests mit signierten Bundles
- **response/** - Erfolgs- und Fehler-Responses
- **test-data/** - Test-Bundles und Signaturen

### 📁 reference-implementation/
- `handler-analysis.md` - Analyse des C++ ActivateTaskHandler
- `validation-rules.md` - Alle ErpExpect Validierungen
- `code-snippets.md` - Wichtige C++ Code-Ausschnitte

### 📁 java-implementation/
- `ActivateOperationProvider.java` - Vollständiger Provider Code
- `ActivateTaskService.java` - Service für Geschäftslogik
- `ActivateOperationIntegrationTest.java` - Integrationstests
- `helper-methods.md` - Wiederverwendbare Hilfsmethoden

### 📁 tests/
- `test-scenarios.md` - Alle Testszenarien dokumentiert
- `dependencies.md` - Abhängigkeiten zu anderen Operationen

## Wichtigste Erkenntnisse

1. **Status-Übergang**: Task muss im Status "draft" sein und wird zu "ready"
2. **Signatur-Validierung**: PKCS#7 CAdES-BES Signatur wird validiert
3. **KVNR-Extraktion**: Patient KVNR wird aus Bundle extrahiert und im Task gespeichert
4. **Datum-Validierung**: AuthoredOn muss mit Signaturdatum übereinstimmen

## Checkliste für Implementierung

- [ ] Provider-Klasse erstellt
- [ ] Service-Klasse erstellt (falls nötig)
- [ ] Alle Validierungen implementiert
- [ ] Tests geschrieben
- [ ] Provider in application.yaml registriert
- [ ] Integrationstests laufen erfolgreich

## Nächste Schritte

1. Lies zuerst `specification/relevant-requirements.md` für alle Anforderungen
2. Schaue dir `reference-implementation/validation-rules.md` für alle Validierungen an
3. Nutze die Templates in `java-implementation/` für die Umsetzung