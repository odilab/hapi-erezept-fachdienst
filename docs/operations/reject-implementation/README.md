# $reject Operation Implementation Documentation

## Übersicht
Die $reject Operation ermöglicht es Apotheken, ein akzeptiertes E-Rezept wieder zurückzugeben. Der Task wechselt vom Status "in-progress" zurück zu "ready" und kann dann von einer anderen Apotheke übernommen werden.

## Wichtige Dokumente

### Spezifikation
- [OperationDefinition](specification/OperationDefinition.json) - FHIR OperationDefinition für $reject
- [Anforderungen aus XML-Spec](specification/relevant-requirements.md) - Extrahierte Anforderungen aus gemSpec_FD_eRp_V2.3.0

### Referenz-Implementierung (C++)
- [Handler-Analyse](reference-implementation/handler-analysis.md) - Analyse des C++ RejectTaskHandler
- [Validierungsregeln](reference-implementation/validation-rules.md) - Alle ErpExpect Statements aus C++

### Java Implementierung
- [Provider Template](java-implementation/provider-template.java) - Vollständiges Java Provider Template
- [Service Template](java-implementation/service-template.java) - Service-Klasse für Geschäftslogik
- [Test Template](java-implementation/test-template.java) - Integrationstests

### Tests
- [Test-Szenarien](tests/test-scenarios.md) - Alle Testfälle dokumentiert
- [Abhängigkeiten](tests/dependencies.md) - Abhängigkeiten zu anderen Operationen

### Beispiele
- [Request-Beispiele](examples/request/) - Valide und invalide Requests
- [Response-Beispiele](examples/response/) - Success und Error Responses

## Quick-Start Guide

### 1. Vorbedingungen prüfen
- [ ] CreateOperationProvider ist implementiert
- [ ] ActivateOperationProvider ist implementiert  
- [ ] AcceptOperationProvider ist implementiert
- [ ] AuthorizationService ist verfügbar
- [ ] AuditService ist verfügbar

### 2. Provider implementieren
1. Kopiere [provider-template.java](java-implementation/provider-template.java) nach:
   ```
   src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectOperationProvider.java
   ```
2. Kopiere [service-template.java](java-implementation/service-template.java) nach:
   ```
   src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectTaskService.java
   ```

### 3. Tests implementieren
1. Kopiere [test-template.java](java-implementation/test-template.java) nach:
   ```
   src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectOperationIntegrationTest.java
   ```

### 4. Provider registrieren
In `src/main/resources/application.yaml` bei `custom-provider-classes` hinzufügen:
```yaml
,ca.uhn.fhir.jpa.starter.custom.operation.reject.RejectOperationProvider
```

### 5. Tests ausführen
```bash
mvn test -Dtest=RejectOperationIntegrationTest
```

## Checkliste

- [ ] Provider-Klasse erstellt
- [ ] Service-Klasse erstellt
- [ ] ALLOWED_PROFESSION_OIDS konfiguriert
- [ ] Secret-Validierung implementiert (A_19171-03)
- [ ] Task-Status Prüfung implementiert (A_19171-03)
- [ ] Secret löschen implementiert (A_19172-01)
- [ ] Owner löschen implementiert (A_24175)
- [ ] Status auf "ready" setzen implementiert (A_19172-01)
- [ ] MedicationDispense löschen implementiert (A_24286-02)
- [ ] HTTP 204 No Content Response implementiert (A_19514)
- [ ] Audit-Logging implementiert
- [ ] Alle Tests grün
- [ ] Provider in application.yaml registriert

## Kritische Punkte

⚠️ **Wichtig**: Task muss im Status "in-progress" sein
⚠️ **Wichtig**: Secret muss korrekt validiert werden (Brute-Force Schutz)
⚠️ **Wichtig**: Owner und Secret müssen gelöscht werden
⚠️ **Wichtig**: MedicationDispense muss gelöscht werden falls vorhanden