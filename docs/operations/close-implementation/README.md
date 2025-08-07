# $close Operation Implementation Guide

## Übersicht
Die `$close` Operation beendet den E-Rezept-Workflow und erstellt eine digitale Quittung (Receipt Bundle). Der Task-Status wechselt von `in-progress` zu `completed`.

## Schnellstart

### 1. Provider implementieren
Kopiere das Template: `java-implementation/CloseOperationProvider.java`

### 2. Service implementieren  
Kopiere das Template: `java-implementation/CloseTaskService.java`

### 3. Test implementieren
Kopiere das Template: `java-implementation/CloseOperationIntegrationTest.java`

### 4. Provider registrieren
Füge in `application.yaml` hinzu:
```yaml
custom-provider-classes: ...,ca.uhn.fhir.jpa.starter.custom.operation.close.CloseOperationProvider
```

## Wichtige Dokumente

- [Implementierungsleitfaden](implementation-guide.md) - Schritt-für-Schritt Anleitung
- [Spezifikation](specification/relevant-requirements.md) - Alle Anforderungen aus gematik
- [Validierungsregeln](reference-implementation/validation-rules.md) - Alle Prüfungen aus C++
- [Testszenarien](tests/test-scenarios.md) - Vollständige Testabdeckung

## Checkliste

- [ ] Provider-Klasse erstellt
- [ ] Service-Klasse erstellt  
- [ ] Alle Validierungen implementiert (siehe validation-rules.md)
- [ ] Tests implementiert und grün
- [ ] Provider in application.yaml registriert
- [ ] Integration getestet (create → activate → accept → close)

## Kritische Punkte

1. **Secret-Validierung**: Muss exakt mit Task.identifier:Secret übereinstimmen
2. **Status-Prüfung**: Task MUSS im Status `in-progress` sein
3. **Rollenprüfung**: Nur Apotheken (oid_oeffentliche_apotheke, oid_krankenhausapotheke)
4. **MedicationDispense**: Optional im Body, muss aber existieren
5. **Receipt-Signierung**: Mit CAdES-BES signiert