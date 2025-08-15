# Implementierungsleitfaden für $abort Operation

## Übersicht

Die $abort Operation löscht einen E-Rezept Task und alle damit verbundenen personenbezogenen Daten. Die Operation ist eine instance-Operation, die auf einen spezifischen Task angewendet wird.

**Endpoint**: `POST /Task/{id}/$abort`

## Voraussetzungen

- [ ] CreateOperation ist implementiert und funktioniert
- [ ] ActivateOperation ist idealerweise implementiert (für Tests)
- [ ] AcceptOperation ist idealerweise implementiert (für Tests)
- [ ] Verstehen der Task-Workflow States
- [ ] Zugriff auf Testcontainer-Setup

## Schritt 1: Provider erstellen

### 1.1 Verzeichnis erstellen (falls nicht vorhanden)
```bash
# Das Verzeichnis existiert bereits, aber leer
cd /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/abort
```

### 1.2 Provider-Klasse kopieren
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/abort-implementation/java-implementation/provider-template.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/abort/AbortOperationProvider.java
```

### 1.3 Provider anpassen

Die wichtigsten Anpassungen im Provider:

1. **Profession OID Mapping**: Überprüfen Sie die `mapProfessionToOid()` Methode:
   ```java
   // Zeile 272-285
   // Stellen Sie sicher, dass alle Profession-Enums korrekt gemappt sind
   ```

2. **Validierungsreihenfolge** (bereits im Template korrekt):
   - Task laden (404 wenn nicht gefunden)
   - Status cancelled prüfen (410)
   - Status draft prüfen (403)
   - Flowtype 169/209 Prüfung für Versicherte
   - Rollenbasierte Validierung
   - Status Update auf cancelled
   - Daten löschen
   - 204 No Content zurückgeben

## Schritt 2: Service erstellen

### 2.1 Service-Klasse kopieren
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/abort-implementation/java-implementation/service-template.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/abort/AbortTaskService.java
```

### 2.2 Service anpassen

Der Service ist bereits vollständig implementiert. Wichtige Methoden:

- `clearPersonalDataFromTask()`: Entfernt personenbezogene Daten aus Task
- `deleteCommunicationsForTask()`: Löscht Task-bezogene Communications
- `deleteHealthCarePrescriptionBinary()`: Löscht Binary Ressource
- `deleteReceiptBundle()`: Löscht Receipt Bundle
- `deleteMedicationDispenses()`: Löscht MedicationDispense Ressourcen

## Schritt 3: Tests implementieren

### 3.1 Test-Klasse kopieren
```bash
cp /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/docs/operations/abort-implementation/java-implementation/test-template.java \
   /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/abort/AbortOperationIntegrationTest.java
```

### 3.2 Tests anpassen

**WICHTIG**: Die Hilfsmethoden müssen angepasst werden:

1. **activateTask()** - Zeile 82-86:
   ```java
   // TODO: Echte $activate Operation aufrufen wenn implementiert
   // Momentan nur Status-Änderung für Tests
   ```

2. **acceptTask()** - Zeile 91-99:
   ```java
   // TODO: Echte $accept Operation aufrufen wenn implementiert
   // Secret muss 64 Hex-Zeichen (256 Bit) sein
   ```

3. **closeTask()** - Zeile 104-108:
   ```java
   // TODO: Echte $close Operation aufrufen wenn implementiert
   ```

## Schritt 4: application.yaml anpassen

### 4.1 Provider registrieren

Öffnen Sie `/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/application.yaml`

Suchen Sie Zeile 311 mit `custom-provider-classes:` und fügen Sie den AbortOperationProvider hinzu:

```yaml
custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.close.CloseOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.abort.AbortOperationProvider
```

## Schritt 5: Build und Test

### 5.1 Projekt bauen
```bash
cd /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst
mvn clean compile
```

### 5.2 Tests ausführen
```bash
# Einzelne Test-Klasse
mvn test -Dtest=AbortOperationIntegrationTest

# Alle Tests
mvn test
```

### 5.3 Server starten
```bash
mvn spring-boot:run
```

## Schritt 6: Manuelle Tests

### 6.1 Patient löscht eigenes Rezept
```bash
curl -X POST "http://localhost:8080/fhir/Task/{taskId}/$abort" \
  -H "Authorization: Bearer {patientAccessToken}" \
  -H "Content-Type: application/fhir+json"
```

### 6.2 Arzt löscht Rezept mit AccessCode
```bash
curl -X POST "http://localhost:8080/fhir/Task/{taskId}/$abort?ac={accessCode}" \
  -H "Authorization: Bearer {doctorAccessToken}" \
  -H "Content-Type: application/fhir+json"
```

### 6.3 Apotheke löscht Rezept mit Secret
```bash
curl -X POST "http://localhost:8080/fhir/Task/{taskId}/$abort?secret={secret}" \
  -H "Authorization: Bearer {pharmacyAccessToken}" \
  -H "Content-Type: application/fhir+json"
```

## Verifikation

### Checkliste für erfolgreiche Implementierung

- [ ] Provider kompiliert ohne Fehler
- [ ] Service kompiliert ohne Fehler
- [ ] Provider ist in application.yaml registriert
- [ ] Server startet ohne Fehler
- [ ] Operation erscheint in der Capability Statement

### Test-Abdeckung prüfen

- [ ] Patient kann eigenes Rezept löschen
- [ ] Vertreter kann mit AccessCode löschen
- [ ] Arzt kann nur Status "ready" löschen
- [ ] Apotheke kann nur Status "in-progress" löschen
- [ ] Flowtype 169/209 Einschränkungen funktionieren
- [ ] Alle Fehlerfälle liefern korrekte HTTP Status Codes
- [ ] Personenbezogene Daten werden entfernt
- [ ] Communications werden gelöscht
- [ ] Audit-Events werden korrekt erstellt

### Performance-Überlegungen

- Communications-Löschung kann bei vielen Einträgen langsam sein
- Consider Batch-Löschung implementieren
- Transaktionale Sicherheit beachten

## Troubleshooting

### Problem: Provider wird nicht gefunden
**Lösung**: Prüfen Sie ob der Provider in application.yaml registriert ist und ob die Klasse das @Component Annotation hat.

### Problem: 404 bei Operation-Aufruf
**Lösung**: Prüfen Sie ob die @Operation Annotation korrekt ist:
- name = "$abort"
- type = Task.class
- instance Operation (nicht type)

### Problem: AccessCode/Secret Validierung schlägt fehl
**Lösung**: 
- AccessCode kann im Header (X-AccessCode) oder Query-Parameter (ac) sein
- Secret MUSS als Query-Parameter übergeben werden
- Beide sind case-sensitive

### Problem: Daten werden nicht gelöscht
**Lösung**: Prüfen Sie ob AbortTaskService korrekt injiziert wird und die Methoden aufgerufen werden.

## Nächste Schritte

Nach erfolgreicher Implementierung:

1. **Integration Tests erweitern**: Fügen Sie weitere Edge-Cases hinzu
2. **Performance optimieren**: Batch-Löschung für Communications
3. **Monitoring**: Logging und Metriken für die Operation
4. **Dokumentation**: OpenAPI/Swagger aktualisieren

## Referenzen

- [OperationDefinition](specification/OperationDefinition.json)
- [Anforderungen](specification/relevant-requirements.md)
- [C++ Referenz](reference-implementation/validation-rules.md)
- [Testszenarien](tests/test-scenarios.md)

## Support

Bei Fragen oder Problemen:
- Prüfen Sie die [C++ Referenzimplementierung](reference-implementation/)
- Schauen Sie in die [Testszenarien](tests/test-scenarios.md)
- Vergleichen Sie mit anderen Operations (create, accept, activate)