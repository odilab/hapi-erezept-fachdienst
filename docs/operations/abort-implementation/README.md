# Abort Operation Implementation Guide

## Übersicht
Die `$abort` Operation ermöglicht das Löschen eines E-Rezepts und aller damit verbundenen personenbezogenen Daten. Die Operation kann von verschiedenen Akteuren durchgeführt werden:
- **Versicherte/Patienten**: Können ihre eigenen Rezepte löschen
- **Vertreter**: Können Rezepte mit gültigem AccessCode löschen
- **Ärzte/Zahnärzte**: Können Rezepte im Status "ready" löschen
- **Apotheken**: Können Rezepte im Status "in-progress" löschen

## Quick Start

### 1. Implementierung
1. Kopiere die Templates aus `java-implementation/` in das entsprechende Verzeichnis
2. Passe die TODOs in den Templates an
3. Registriere den Provider in `application.yaml`

### 2. Tests
1. Verwende das Test-Template aus `java-implementation/test-template.java`
2. Stelle sicher, dass Testcontainer läuft
3. Führe die Tests aus

## Wichtige Dateien

### Spezifikation
- [OperationDefinition](specification/OperationDefinition.json) - FHIR OperationDefinition
- [Anforderungen](specification/relevant-requirements.md) - Extrahierte Anforderungen aus gemSpec
- [Validierungsregeln](reference-implementation/validation-rules.md) - Alle Validierungen aus C++

### Implementation Templates
- [AbortOperationProvider.java](java-implementation/provider-template.java) - Provider Template
- [AbortTaskService.java](java-implementation/service-template.java) - Service Template
- [Test Template](java-implementation/test-template.java) - Integrationstests

### Beispiele
- [Request Examples](examples/request/) - Beispiel-Requests
- [Response Examples](examples/response/) - Beispiel-Responses
- [Test Data](examples/test-data/) - Testdaten

## Checkliste

- [ ] Provider erstellt
- [ ] Service erstellt (falls nötig)
- [ ] Tests implementiert
- [ ] Provider in application.yaml registriert
- [ ] Alle Validierungen implementiert
- [ ] Audit-Logging implementiert
- [ ] Tests grün
- [ ] Code Coverage > 80%

## Verlinkungen

- [Implementierungsleitfaden](implementation-guide.md) - Detaillierte Schritt-für-Schritt Anleitung
- [Testszenarien](tests/test-scenarios.md) - Alle Testfälle
- [C++ Referenz](reference-implementation/handler-analysis.md) - Analyse der C++ Implementation