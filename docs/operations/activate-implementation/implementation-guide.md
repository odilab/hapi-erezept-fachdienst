# Implementierungsleitfaden für $activate Operation

## Übersicht

Die $activate Operation aktiviert einen E-Rezept Task von Status "draft" zu "ready" durch Hinzufügen eines qualifiziert signierten E-Rezept Bundles.

## Voraussetzungen

- [ ] CreateOperation ist implementiert (benötigt für Tests)
- [ ] Verstehen der Task-Workflow States (draft → ready → in-progress → completed)
- [ ] Zugriff auf Testcontainer-Setup (ERP-Service für Signaturen)
- [ ] TSL Manager für Signaturvalidierung (optional für erste Version)

## Schritt 1: Provider erstellen

1. Erstelle Verzeichnis:
   ```bash
   mkdir -p /Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/activate
   ```

2. Kopiere `ActivateOperationProvider.java` aus der Dokumentation:
   ```bash
   cp docs/operations/activate-implementation/java-implementation/ActivateOperationProvider.java \
      src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/activate/
   ```

3. Passe die TODOs im Provider an:
   - **Zeile 39-43**: ALLOWED_PROFESSION_OIDS sind bereits korrekt gesetzt
   - **Zeile 162**: mapProfessionToOid() muss ggf. erweitert werden für alle Profession-Typen

## Schritt 2: Service implementieren

1. Kopiere `ActivateTaskService.java`:
   ```bash
   cp docs/operations/activate-implementation/java-implementation/ActivateTaskService.java \
      src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/activate/
   ```

2. Wichtige Anpassungen im Service:
   - **Zeile 78-80**: TSL-Validierung ist als TODO markiert. Für erste Version kann Struktur-Validierung ausreichen
   - **Zeile 41**: ANR Validierungsmodus aus Config lesen
   - **Zeile 304**: KVNR Prüfziffer-Berechnung implementieren (oder vereinfachte Prüfung belassen)
   - **Zeile 314**: ANR Prüfziffer-Berechnung implementieren (oder vereinfachte Prüfung belassen)

## Schritt 3: Dependencies hinzufügen

In `pom.xml` sicherstellen dass Bouncy Castle vorhanden ist:
```xml
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

## Schritt 4: application.yaml anpassen

Füge den Provider zur Liste der custom-provider-classes hinzu:

```yaml
hapi:
  fhir:
    custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider
```

Füge Konfiguration für ANR-Validierung hinzu:
```yaml
erp:
  anr:
    validation:
      mode: warning  # oder "error"
```

## Schritt 5: Tests implementieren

1. Kopiere Test-Klasse:
   ```bash
   cp docs/operations/activate-implementation/java-implementation/ActivateOperationIntegrationTest.java \
      src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/activate/
   ```

2. Führe Tests aus:
   ```bash
   mvn -Dtest=ActivateOperationIntegrationTest test
   ```

## Schritt 6: Manuelle Tests

1. Erstelle einen Task:
   ```bash
   curl -X POST "http://localhost:8080/fhir/Task/$create" \
     -H "Authorization: Bearer $ACCESS_TOKEN" \
     -H "Content-Type: application/fhir+json" \
     -d '{
       "resourceType": "Parameters",
       "parameter": [{
         "name": "workflowType",
         "valueCoding": {
           "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
           "code": "160"
         }
       }]
     }'
   ```

2. Signiere ein Test-Bundle (mit Fachdiensttool)

3. Aktiviere den Task:
   ```bash
   curl -X POST "http://localhost:8080/fhir/Task/{prescriptionId}/$activate" \
     -H "Authorization: Bearer $ACCESS_TOKEN" \
     -H "X-AccessCode: $ACCESS_CODE" \
     -H "Content-Type: application/fhir+json" \
     -d '{
       "resourceType": "Parameters",
       "parameter": [{
         "name": "ePrescription",
         "resource": {
           "resourceType": "Binary",
           "contentType": "application/pkcs7-mime",
           "data": "MIIUvwYJKoZIhvcNAQ..."
         }
       }]
     }'
   ```

## Verifikation

- [ ] Alle Unit Tests laufen grün
- [ ] Integrationstests mit Testcontainern erfolgreich
- [ ] Task-Status ändert sich von "draft" zu "ready"
- [ ] KVNR wird korrekt extrahiert und gespeichert
- [ ] ExpiryDate und AcceptDate werden korrekt berechnet
- [ ] Audit-Logs werden geschrieben
- [ ] Fehlerbehandlung funktioniert für alle Szenarien

## Bekannte Einschränkungen

1. **TSL-Validierung**: Aktuell nur Struktur-Prüfung, keine vollständige Zertifikatsvalidierung
2. **Prüfziffern**: KVNR und ANR Prüfung vereinfacht implementiert
3. **MVO**: Mehrfachverordnung noch nicht vollständig getestet
4. **DiGA**: EvdgaBundle Handling noch nicht implementiert

## Troubleshooting

### Problem: "No signers found in PKCS#7 signature"
**Lösung**: Signatur wurde nicht korrekt erstellt. Nutze das Fachdiensttool zum Signieren.

### Problem: "Task not found in DB"
**Lösung**: Task-ID/PrescriptionID ist falsch oder Task existiert nicht.

### Problem: "Access code does not match"
**Lösung**: X-AccessCode Header fehlt oder ist falsch.

### Problem: Tests schlagen fehl
**Lösung**: 
1. Stelle sicher dass Testcontainer laufen
2. Prüfe ob CreateOperation korrekt funktioniert
3. Schaue in die Logs für Details

## Nächste Schritte

Nach erfolgreicher Implementierung der activate Operation:
1. Accept Operation implementieren (Apotheke akzeptiert Rezept)
2. Close/Dispense Operation (Abgabe des Medikaments)
3. Abort/Reject für Abbruch-Szenarien