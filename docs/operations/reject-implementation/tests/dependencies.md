# Abhängigkeiten zu anderen Operationen

## Workflow-Abhängigkeiten

Die $reject Operation ist Teil des E-Rezept-Workflows und hat klare Abhängigkeiten zu anderen Operationen:

```
CREATE → ACTIVATE → ACCEPT → REJECT
                           ↓
                         DISPENSE → CLOSE
```

## Voraussetzungen für $reject

### 1. $create Operation (Pflicht)
- **Zweck**: Erstellt initialen Task mit Status "draft"
- **Liefert**: 
  - Task-ID (= Prescription-ID)
  - AccessCode
  - Workflow-Type
- **Status nach Operation**: draft

### 2. $activate Operation (Pflicht)
- **Zweck**: Aktiviert Task mit signiertem E-Rezept
- **Benötigt**: 
  - Task-ID von create
  - AccessCode von create
  - Signiertes E-Rezept Bundle
- **Liefert**: Task mit Status "ready"
- **Status nach Operation**: ready

### 3. $accept Operation (Pflicht)
- **Zweck**: Apotheke übernimmt Task zur Bearbeitung
- **Benötigt**:
  - Task-ID
  - AccessCode
  - Task muss im Status "ready" sein
- **Liefert**: 
  - **Secret** (256-Bit Hex-String) - KRITISCH für reject!
  - Task mit Status "in-progress"
  - Owner wird gesetzt (Telematik-ID)
- **Status nach Operation**: in-progress

### 4. $dispense Operation (Optional)
- **Zweck**: Teilweise Abgabe von Medikamenten
- **Benötigt**:
  - Task-ID
  - Secret von accept
- **Auswirkung auf reject**:
  - MedicationDispense wird erstellt
  - Diese muss bei reject gelöscht werden (A_24286-02)

## Nachfolgende Operationen

Nach erfolgreicher $reject Operation können folgende Operationen aufgerufen werden:

### 1. $accept (erneut)
- Eine andere Apotheke kann den Task wieder akzeptieren
- Neues Secret wird generiert
- Neuer Owner wird gesetzt

### 2. $abort
- Patient kann Task abbrechen
- Nur möglich wenn Status "ready"

## Kritische Abhängigkeiten

### Secret-Abhängigkeit
```
ACCEPT generiert Secret → REJECT benötigt Secret → Secret wird gelöscht
```
- **Wichtig**: Ohne gültiges Secret aus accept ist reject nicht möglich!
- **Sicherheit**: Secret dient als Nachweis, dass die Apotheke den Task besitzt

### Status-Abhängigkeit
```
draft → ready → in-progress → ready (nach reject)
                            ↓
                         completed (nach close)
```
- **Kritisch**: Reject nur aus Status "in-progress" möglich
- **Resultat**: Status wird auf "ready" zurückgesetzt

### Owner-Abhängigkeit
```
ACCEPT setzt Owner → REJECT löscht Owner
```
- Owner zeigt welche Apotheke den Task bearbeitet
- Nach reject ist Task wieder "frei"

## Test-Abhängigkeiten

### Helper-Methoden benötigt

```java
// Für Reject-Tests benötigt:
private TaskTestData createActivateAndAcceptTask() {
    // 1. Create aufrufen
    // 2. Activate aufrufen  
    // 3. Accept aufrufen
    // → Liefert Task mit Secret für Reject-Tests
}
```

### Test-Reihenfolge

1. **CreateOperationIntegrationTest** muss funktionieren
2. **ActivateOperationIntegrationTest** muss funktionieren
3. **AcceptOperationIntegrationTest** muss funktionieren
4. Erst dann können **RejectOperationIntegrationTest** laufen

### Gemeinsame Test-Infrastruktur

- `BaseProviderTest` als Basis-Klasse
- `getValidAccessToken()` für JWT-Generierung
- `createSignedBundleForTest()` für Activate
- Testcontainer für Datenbank

## Datenbank-Abhängigkeiten

### Transaktionale Integrität
- Reject muss transaktional sein
- Bei Fehler: Rollback aller Änderungen
- Lock auf Task während Operation (wie in C++)

### Zu löschende Entitäten
1. **Task.identifier** (Secret)
2. **Task.owner**
3. **MedicationDispense** (falls vorhanden)
4. **Medication** (falls von MedicationDispense referenziert)
5. **Task.extension** (lastMedicationDispense)

## Service-Abhängigkeiten

### Benötigte Services
1. **AuthorizationService**
   - Token-Validierung
   - Profession-Prüfung

2. **AuditService**
   - Audit-Log für reject
   - Event-ID: POST_Task_reject

3. **RejectTaskService**
   - Löschen von MedicationDispense
   - Geschäftslogik-Kapselung

### DAO-Abhängigkeiten
- `IFhirResourceDao<Task>` für Task-Operationen
- `IFhirResourceDao<MedicationDispense>` für Löschen
- `IFhirResourceDao<Medication>` für Löschen

## Fehlerbehandlung bei fehlenden Abhängigkeiten

| Fehlende Abhängigkeit | Fehlerverhalten |
|----------------------|-----------------|
| Task nicht gefunden | 404 Not Found |
| Kein Secret im Task | 403 Forbidden |
| Task nicht in-progress | 403 Forbidden |
| AuthorizationService fehlt | 500 Internal Server Error |
| AuditService fehlt | Operation erfolgreich, aber kein Audit |

## Konfigurationsabhängigkeiten

### application.yaml
```yaml
custom-provider-classes: 
  - ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider
  - ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider
  - ca.uhn.fhir.jpa.starter.custom.operation.accept.AcceptOperationProvider
  - ca.uhn.fhir.jpa.starter.custom.operation.reject.RejectOperationProvider  # NEU
```

### Spring Boot Komponenten
- Alle Provider mit `@Component` annotiert
- Alle Services mit `@Service` annotiert
- Dependency Injection via `@Autowired`