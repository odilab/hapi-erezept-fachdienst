# Implementierungsleitfaden für $reject Operation

## Übersicht

Die $reject Operation ermöglicht es Apotheken, ein zuvor akzeptiertes E-Rezept wieder zurückzugeben. Dies ist notwendig wenn:
- Die Apotheke das Medikament nicht vorrätig hat
- Der Patient die Abholung abbricht
- Technische Probleme auftreten
- Die Apotheke aus anderen Gründen die Bearbeitung nicht fortsetzen kann

Nach erfolgreicher Reject-Operation:
- Task-Status wechselt von "in-progress" zurück zu "ready"
- Secret wird gelöscht
- Owner (Telematik-ID der Apotheke) wird gelöscht
- MedicationDispense wird gelöscht (falls vorhanden)
- Task kann von einer anderen Apotheke übernommen werden

## Schritt 1: Vorbereitung

### 1.1 Voraussetzungen prüfen
```bash
# Prüfe ob die abhängigen Operationen implementiert sind
ls src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/create/
ls src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/activate/
ls src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/accept/
```

### 1.2 Verzeichnisstruktur erstellen
```bash
mkdir -p src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/reject
mkdir -p src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/reject
```

## Schritt 2: Provider implementieren

### 2.1 Provider-Klasse kopieren
```bash
cp docs/operations/reject-implementation/java-implementation/provider-template.java \
   src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectOperationProvider.java
```

### 2.2 Anpassungen im Provider

#### Profession-Mapping anpassen
Stelle sicher, dass das Profession-Enum korrekt gemappt wird:
```java
private String mapProfessionToOid(Profession profession) {
    if (profession == null) {
        return "";
    }
    
    switch (profession) {
        case OEFFENTLICHE_APOTHEKE:
            return "oid_oeffentliche_apotheke";
        case KRANKENHAUS_APOTHEKE:
            return "oid_krankenhausapotheke";
        default:
            return profession.name().toLowerCase();
    }
}
```

#### VAU-Error-Code Handling
Für Brute-Force-Schutz bei Secret-Fehlern:
```java
private void validateSecret(StringType secretParam, Task task) {
    // Bei Fehler sollte idealerweise ein spezieller Header gesetzt werden
    // In HAPI kann dies über einen Interceptor erfolgen
    if (!secretMatches) {
        // TODO: VAU-Error-Code: brute_force setzen
        throw new ForbiddenOperationException("No or invalid secret");
    }
}
```

## Schritt 3: Service implementieren

### 3.1 Service-Klasse kopieren
```bash
cp docs/operations/reject-implementation/java-implementation/service-template.java \
   src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectTaskService.java
```

### 3.2 Service-Anpassungen

Die Service-Klasse handhabt das Löschen von MedicationDispense. Wichtige Punkte:
- Transaktionale Sicherheit beachten
- Fehler beim Löschen sollten die Operation nicht verhindern
- Logging für Audit-Trail

## Schritt 4: Tests implementieren

### 4.1 Test-Klasse kopieren
```bash
cp docs/operations/reject-implementation/java-implementation/test-template.java \
   src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/reject/RejectOperationIntegrationTest.java
```

### 4.2 Test-Helper anpassen

Die Tests benötigen Helper-Methoden aus anderen Test-Klassen:
- `getValidAccessToken()` aus BaseProviderTest
- `createSignedBundleForTest()` für Activate-Operation

### 4.3 Test-Daten vorbereiten

Stelle sicher, dass Test-Zertifikate und -Schlüssel verfügbar sind:
```
src/test/resources/certificates/
├── apotheke-test.p12
├── arzt-test.p12
└── test-ca.crt
```

## Schritt 5: Provider registrieren

### 5.1 application.yaml anpassen

In `src/main/resources/application.yaml`:
```yaml
hapi:
  fhir:
    custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.reject.RejectOperationProvider
```

**WICHTIG**: Die Provider müssen komma-separiert in EINER Zeile stehen!

### 5.2 Test-Konfiguration anpassen

In `src/test/resources/application.yaml` dieselbe Änderung durchführen.

## Schritt 6: Build und Test

### 6.1 Kompilieren
```bash
mvn clean compile
```

### 6.2 Unit-Tests ausführen
```bash
# Nur Reject-Tests
mvn test -Dtest=RejectOperationIntegrationTest

# Alle Operations-Tests
mvn test -Dtest=*OperationIntegrationTest
```

### 6.3 Integrationstests mit laufendem Server
```bash
# Server starten
mvn spring-boot:run

# In anderem Terminal: Tests gegen laufenden Server
curl -X POST http://localhost:8080/fhir/Task/160.000.000.004.714.01/\$reject?secret=...
```

## Schritt 7: Validierung

### 7.1 Funktionale Validierung

#### Test-Workflow durchführen:
1. Task erstellen (create)
2. Task aktivieren (activate)
3. Task akzeptieren (accept) → Secret erhalten
4. Task zurückweisen (reject) mit Secret
5. Prüfen: Status = ready, kein Secret, kein Owner

#### FHIR-Conformance prüfen:
```bash
curl http://localhost:8080/fhir/metadata
```
Suche nach "reject" in den OperationDefinitions.

### 7.2 Anforderungen validieren

| Anforderung | Implementiert | Test |
|------------|---------------|------|
| A_19170-02: Rollenprüfung | ✓ | testRejectWithWrongProfession |
| A_19171-03: Secret-Validierung | ✓ | testRejectWithWrongSecret |
| A_19172-01: Secret löschen, Status ready | ✓ | testRejectSuccess |
| A_24175: Owner löschen | ✓ | testRejectOwnerDeleted |
| A_24286-02: MedicationDispense löschen | ✓ | testRejectDeletesMedicationDispense |
| A_19514: HTTP 204 | ✓ | Automatisch durch void-Return |

### 7.3 Security-Validierung

- [ ] JWT-Token wird validiert
- [ ] Profession OID wird geprüft
- [ ] Secret wird sicher verglichen (timing-safe)
- [ ] Brute-Force-Schutz aktiv
- [ ] Audit-Logs werden geschrieben

## Schritt 8: Fehlerbehandlung

### Häufige Probleme und Lösungen

#### Problem: "Provider not found"
```
Lösung: Provider in application.yaml registrieren
```

#### Problem: "No qualifying bean of type RejectTaskService"
```
Lösung: @Service Annotation prüfen, Component-Scan konfigurieren
```

#### Problem: "Task not found" in Tests
```
Lösung: Test-Datenbank prüfen, Testcontainer neu starten
```

#### Problem: "Invalid JWT"
```
Lösung: Test-Zertifikate aktualisieren, JWT-Builder prüfen
```

## Schritt 9: Dokumentation

### 9.1 JavaDoc ergänzen
```java
/**
 * Provider für die $reject Operation gemäß E-Rezept-Spezifikation.
 * 
 * @see <a href="https://gematik.de/fhir/erp/OperationDefinition/RejectOperationDefinition">
 *      OperationDefinition</a>
 * @since 1.5.0
 * @author Ihr Name
 */
```

### 9.2 OpenAPI/Swagger
Die Operation wird automatisch in der OpenAPI-Dokumentation erscheinen unter:
```
http://localhost:8080/fhir/swagger-ui/
```

## Schritt 10: Deployment

### 10.1 Pre-Deployment Checklist

- [ ] Alle Tests grün
- [ ] Code-Review durchgeführt
- [ ] Dokumentation aktualisiert
- [ ] Performance-Tests durchgeführt
- [ ] Security-Review abgeschlossen
- [ ] Rollback-Plan erstellt

### 10.2 Deployment-Schritte

1. Feature-Branch mergen
2. CI/CD Pipeline durchläuft
3. Deployment auf Test-Umgebung
4. Smoke-Tests durchführen
5. Deployment auf Produktion
6. Monitoring aktivieren

### 10.3 Post-Deployment

- Audit-Logs überwachen
- Performance-Metriken prüfen
- Fehlerrate monitoren
- User-Feedback sammeln

## Anhang A: Vollständige Validierungsreihenfolge

```java
1. JWT-Token validieren
2. Telematik-ID extrahieren
3. Profession OID prüfen (nur Apotheken)
4. Task laden
5. Task nicht cancelled? (sonst 410)
6. Task im Status in-progress? (sonst 403)
7. Secret vorhanden und korrekt? (sonst 403 + brute_force)
8. Secret löschen
9. Owner löschen
10. Status auf ready setzen
11. MedicationDispense löschen (falls vorhanden)
12. Task speichern
13. Audit-Log schreiben
14. HTTP 204 zurückgeben
```

## Anhang B: Test-Kommandos

```bash
# Vollständiger Test-Zyklus
mvn clean test -Dtest=CreateOperationIntegrationTest
mvn test -Dtest=ActivateOperationIntegrationTest
mvn test -Dtest=AcceptOperationIntegrationTest
mvn test -Dtest=RejectOperationIntegrationTest

# Mit Coverage
mvn clean test jacoco:report
# Report unter: target/site/jacoco/index.html

# Mit Testcontainers Debug-Output
mvn test -Dtest=RejectOperationIntegrationTest -Dtestcontainers.reuse.enable=true
```

## Anhang C: cURL-Beispiele

### Erfolgreicher Reject
```bash
curl -X POST \
  'http://localhost:8080/fhir/Task/160.000.000.004.714.01/$reject?secret=777bea0e13cc9c42ceec14aec3ddee2263325dc2c6c699db115f58fe423607ea' \
  -H 'Authorization: Bearer eyJhbGc...' \
  -H 'Accept: application/fhir+json' \
  -v
```

### Mit Parameters im Body
```bash
curl -X POST \
  'http://localhost:8080/fhir/Task/160.000.000.004.714.01/$reject' \
  -H 'Authorization: Bearer eyJhbGc...' \
  -H 'Content-Type: application/fhir+json' \
  -d '{
    "resourceType": "Parameters",
    "parameter": [{
      "name": "secret",
      "valueString": "777bea0e13cc9c42ceec14aec3ddee2263325dc2c6c699db115f58fe423607ea"
    }]
  }'
```

## Support und Hilfe

Bei Problemen:
1. Logs prüfen: `target/logs/application.log`
2. Debug-Modus aktivieren: `-Dlogging.level.ca.uhn.fhir=DEBUG`
3. Issue erstellen: [GitHub Issues]
4. Team kontaktieren: [Team Chat]

---
Stand: August 2025 | Version: 1.0