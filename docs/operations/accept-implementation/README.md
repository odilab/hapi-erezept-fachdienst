# Accept Operation Implementierung

## Übersicht

Die `$accept` Operation ermöglicht es Apotheken, ein E-Rezept zur Bearbeitung zu übernehmen. Dabei wechselt der Task-Status von "ready" zu "in-progress" und es wird ein Secret generiert, das exklusiven Zugriff gewährt.

## Schnellstart

1. **Provider implementieren**: Siehe [java-implementation/AcceptOperationProvider.java](java-implementation/AcceptOperationProvider.java)
2. **Service implementieren**: Siehe [java-implementation/AcceptTaskService.java](java-implementation/AcceptTaskService.java)
3. **Tests schreiben**: Siehe [java-implementation/AcceptOperationIntegrationTest.java](java-implementation/AcceptOperationIntegrationTest.java)
4. **Provider registrieren**: In `application.yaml` hinzufügen

## Wichtige Dokumente

### Spezifikationen
- [OperationDefinition](specification/OperationDefinition.json) - Offizielle FHIR OperationDefinition
- [Anforderungen](specification/relevant-requirements.md) - Extrahierte Anforderungen aus gemSpec
- [Validierungsregeln](reference-implementation/validation-rules.md) - Alle Validierungen aus C++

### Implementierung
- [Implementierungsleitfaden](implementation-guide.md) - Schritt-für-Schritt Anleitung
- [Provider Template](java-implementation/AcceptOperationProvider.java) - Vollständiger Provider Code
- [Service Template](java-implementation/AcceptTaskService.java) - Service Implementierung
- [Test Template](java-implementation/AcceptOperationIntegrationTest.java) - Testklasse

### Beispiele
- [Request Beispiele](examples/request/) - Valide Accept-Requests
- [Response Beispiele](examples/response/) - Erwartete Responses
- [Testdaten](examples/test-data/) - Testdaten für verschiedene Szenarien

## Checkliste

- [ ] Provider-Klasse erstellt
- [ ] Service-Klasse erstellt (falls benötigt)
- [ ] application.yaml angepasst
- [ ] Tests implementiert
- [ ] Alle Validierungen aus C++ umgesetzt
- [ ] Audit-Logging implementiert
- [ ] Secret-Generierung implementiert
- [ ] Task.owner gesetzt
- [ ] Consent für PKV berücksichtigt

## Wichtige Hinweise

- Die Operation ist eine **Instance-Operation** (Task/{id}/$accept)
- AccessCode kann als Query-Parameter `?ac=...` oder Header `X-AccessCode` übergeben werden
- Bei PKV-Rezepten (Flowtype 200/209) muss ggf. eine Consent-Ressource zurückgegeben werden
- Das generierte Secret muss 256 Bit (64 Hex-Zeichen) haben
- Task.owner muss mit der Telematik-ID aus dem Access Token gesetzt werden