# Test-Szenarien für $reject Operation

## Vorbedingungen

### Task-Workflow für Reject
1. **CREATE**: Task wird mit Status "draft" erstellt
2. **ACTIVATE**: Task wird mit signiertem E-Rezept aktiviert → Status "ready"
3. **ACCEPT**: Apotheke akzeptiert Task → Status "in-progress" + Secret generiert
4. **REJECT**: Apotheke gibt Task zurück → Status "ready" + Secret/Owner gelöscht

### Benötigte Test-Tokens
- Arzt/Krankenhaus-Token für CREATE und ACTIVATE
- Apotheken-Token für ACCEPT und REJECT

## Success Cases

### 1. Standard Reject Operation
**Test**: `testRejectSuccess`
**Vorbedingung**: Task im Status "in-progress" mit gültigem Secret
**Aktion**: POST /Task/{id}/$reject?secret={validSecret}
**Erwartetes Ergebnis**:
- HTTP 204 No Content
- Task-Status wird zu "ready"
- Secret wird gelöscht
- Owner wird gelöscht
- lastModified wird aktualisiert
- Audit-Log wird erstellt

### 2. Reject mit MedicationDispense
**Test**: `testRejectDeletesMedicationDispense`
**Vorbedingung**: Task im Status "in-progress" mit MedicationDispense
**Aktion**: POST /Task/{id}/$reject?secret={validSecret}
**Erwartetes Ergebnis**:
- Alle Punkte von Test 1
- MedicationDispense wird gelöscht
- Referenzierte Medication wird gelöscht
- lastMedicationDispense Extension wird entfernt

### 3. Owner wird gelöscht (A_24175)
**Test**: `testRejectOwnerDeleted`
**Vorbedingung**: Task im Status "in-progress" mit Owner (Telematik-ID)
**Aktion**: POST /Task/{id}/$reject?secret={validSecret}
**Erwartetes Ergebnis**:
- Task.owner wird auf null gesetzt
- Telematik-ID der Apotheke wird entfernt

## Error Cases

### 1. Falsches Secret (A_19171-03)
**Test**: `testRejectWithWrongSecret`
**Vorbedingung**: Task im Status "in-progress"
**Aktion**: POST /Task/{id}/$reject?secret={wrongSecret}
**Erwartetes Ergebnis**:
- HTTP 403 Forbidden
- Fehlermeldung: "No or invalid secret"
- VAU-Error-Code: brute_force (Header)
- Task bleibt unverändert

### 2. Fehlendes Secret
**Test**: `testRejectWithoutSecret`
**Vorbedingung**: Task im Status "in-progress"
**Aktion**: POST /Task/{id}/$reject (ohne secret Parameter)
**Erwartetes Ergebnis**:
- HTTP 400 Bad Request oder 403 Forbidden
- Fehlermeldung: "No or invalid secret"
- Task bleibt unverändert

### 3. Task nicht im Status in-progress
**Test**: `testRejectTaskNotInProgress`
**Vorbedingung**: Task im Status "draft", "ready" oder "completed"
**Aktion**: POST /Task/{id}/$reject?secret={anySecret}
**Erwartetes Ergebnis**:
- HTTP 403 Forbidden
- Fehlermeldung: "Task not in status in progress, is: [aktueller Status]"
- Task bleibt unverändert

### 4. Task nicht gefunden
**Test**: `testRejectNonExistentTask`
**Vorbedingung**: Nicht existierende Task-ID
**Aktion**: POST /Task/{nonExistentId}/$reject?secret={anySecret}
**Erwartetes Ergebnis**:
- HTTP 404 Not Found
- Fehlermeldung: "Task not found for prescription id"

### 5. Task ist cancelled
**Test**: `testRejectCancelledTask` (optional)
**Vorbedingung**: Task im Status "cancelled"
**Aktion**: POST /Task/{id}/$reject?secret={anySecret}
**Erwartetes Ergebnis**:
- HTTP 410 Gone
- Fehlermeldung: "Task has already been deleted"

### 6. Falsche Profession (A_19170-02)
**Test**: `testRejectWithWrongProfession`
**Vorbedingung**: Task im Status "in-progress", gültiges Secret
**Aktion**: POST /Task/{id}/$reject mit Arzt-Token statt Apotheken-Token
**Erwartetes Ergebnis**:
- HTTP 403 Forbidden
- Fehlermeldung: "Die Operation $reject ist nur für Apotheken erlaubt"
- Task bleibt unverändert

## Edge Cases

### 1. Doppeltes Reject
**Szenario**: Reject wird zweimal hintereinander aufgerufen
**Erwartetes Verhalten**:
- Erstes Reject: Erfolgreich, Task → ready
- Zweites Reject: Fehler 403, da Task nicht mehr in-progress

### 2. Reject nach Dispense
**Szenario**: Task wurde bereits teilweise dispensiert
**Erwartetes Verhalten**:
- MedicationDispense wird gelöscht
- Task wird auf ready zurückgesetzt
- Apotheke verliert alle Dispensier-Informationen

### 3. Parallele Rejects
**Szenario**: Zwei Apotheken versuchen gleichzeitig zu rejecten
**Erwartetes Verhalten**:
- Nur ein Reject erfolgreich (durch Datenbank-Lock)
- Zweites Reject erhält Fehler (403 oder 409)

## Performance-Tests

### 1. Reject mit vielen MedicationDispense
**Szenario**: Task hat mehrere MedicationDispense-Einträge
**Erwartetes Verhalten**:
- Alle MedicationDispense werden gelöscht
- Performance sollte akzeptabel bleiben (<2 Sekunden)

### 2. Bulk-Reject
**Szenario**: Viele Tasks werden nacheinander rejected
**Erwartetes Verhalten**:
- Jeder Reject erfolgreich
- Keine Memory-Leaks
- Audit-Logs vollständig

## Testdaten

### Valide Secrets (Beispiele)
```
777bea0e13cc9c42ceec14aec3ddee2263325dc2c6c699db115f58fe423607ea
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef
```

### Test-Prescription-IDs
```
160.000.000.000.001.01
169.000.000.000.002.01
200.000.000.000.003.01
```

### Test-Telematik-IDs
```
3-SMC-B-Testkarte-883110000123456  (Apotheke)
3-HBA-Testkarte-883110000123457    (Apotheker)
```

## Automatisierung

### CI/CD Integration
- Tests sollten in Maven-Build integriert sein
- Testcontainer für Datenbank-Isolation
- Mindestens 80% Code-Coverage für reject-Operation

### Test-Reihenfolge
1. Success Cases zuerst (Smoke Tests)
2. Error Cases
3. Edge Cases
4. Performance Tests (optional, nur in speziellen Test-Suites)