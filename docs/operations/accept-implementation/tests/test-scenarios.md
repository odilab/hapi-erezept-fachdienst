# Test-Szenarien für Accept Operation

## Vorbedingungen

Für alle Accept-Tests gelten folgende Vorbedingungen:

1. **Task wurde erstellt**: Mit CREATE Operation (Status: draft)
2. **Task wurde aktiviert**: Mit ACTIVATE Operation (Status: ready)
3. **Gültiges Access Token**: Mit korrekter Profession OID
4. **AccessCode vorhanden**: Aus CREATE Operation

## Success Cases

### 1. Standard Accept - Apotheke
**Vorbedingung**: Task mit Flowtype 160 im Status "ready"
**Input**: 
- AccessCode als Query-Parameter `?ac={accessCode}`
- Authorization Header mit Apotheken-Token
**Expected**:
- HTTP 200
- Task.status = "in-progress"
- Secret generiert (64 Hex-Zeichen)
- Task.owner = Telematik-ID der Apotheke
- Bundle mit Task + Binary

### 2. Accept mit Header AccessCode
**Vorbedingung**: Task mit Flowtype 169 im Status "ready"
**Input**:
- Header `X-AccessCode: {accessCode}`
- Authorization Header mit Apotheken-Token
**Expected**:
- HTTP 200
- Gleiche Ergebnisse wie Test 1

### 3. PKV Accept mit Consent
**Vorbedingung**: 
- Task mit Flowtype 200 im Status "ready"
- Consent für KVNR vorhanden
**Input**: Standard Accept Request
**Expected**:
- HTTP 200
- Bundle enthält zusätzlich Consent-Resource
- 3 Entries: Task, Binary, Consent

### 4. DiGA Accept durch Kostenträger
**Vorbedingung**: Task mit Flowtype 162 im Status "ready"
**Input**:
- AccessCode
- Authorization Header mit Kostenträger-Token
**Expected**:
- HTTP 200
- Normaler Accept-Flow

## Error Cases

### 1. Falscher AccessCode
**Input**: Ungültiger AccessCode
**Expected**: 
- HTTP 403
- Message: "Access code does not match"

### 2. Fehlender AccessCode
**Input**: Weder Query-Parameter noch Header
**Expected**:
- HTTP 403
- Message: "AccessCode missing"

### 3. Task im falschen Status - Draft
**Vorbedingung**: Task im Status "draft"
**Expected**:
- HTTP 409
- Message: "Task has invalid status draft"

### 4. Task im falschen Status - Completed
**Vorbedingung**: Task im Status "completed"
**Expected**:
- HTTP 409
- Message: "Task has invalid status completed"

### 5. Task bereits in Bearbeitung - Gleiche Apotheke
**Vorbedingung**: 
- Task im Status "in-progress"
- Owner = anfragende Telematik-ID
**Expected**:
- HTTP 409
- Message: "Task has invalid status in-progress. Task is processed by requesting institution"

### 6. Task bereits in Bearbeitung - Andere Apotheke
**Vorbedingung**:
- Task im Status "in-progress"
- Owner ≠ anfragende Telematik-ID
**Expected**:
- HTTP 409
- Message: "Task has invalid status in-progress"

### 7. Task gelöscht/cancelled
**Vorbedingung**: Task im Status "cancelled"
**Expected**:
- HTTP 410
- Message: "Task meanwhile deleted for prescription id"

### 8. Task nicht gefunden
**Input**: Nicht existierende Task-ID
**Expected**:
- HTTP 404
- Message: "Task not found for prescription id"

### 9. Einlösefrist abgelaufen
**Vorbedingung**: Task.ExpiryDate liegt in der Vergangenheit
**Expected**:
- HTTP 403
- Message: "Verordnung bis {ExpiryDate} einlösbar."

### 10. MVO Startdatum nicht erreicht
**Vorbedingung**: 
- Mehrfachverordnung
- Startdatum liegt in der Zukunft
**Expected**:
- HTTP 403
- Message: "Teilverordnung zur Mehrfachverordnung {MVO-ID} ist ab {Startdatum} einlösbar."

### 11. Falsche Rolle - Arzt versucht Accept
**Input**: Authorization Header mit Arzt-Token
**Expected**:
- HTTP 403
- Message: "Die Operation $accept ist nur für Apotheken erlaubt"

### 12. Falsche Rolle - Apotheke bei DiGA
**Vorbedingung**: Task mit Flowtype 162
**Input**: Authorization Header mit Apotheken-Token
**Expected**:
- HTTP 403
- Message: "Die Operation $accept ist für DiGA nur für Kostenträger erlaubt"

## Edge Cases

### 1. Doppelter Accept
**Szenario**: Zwei parallele Accept-Requests
**Expected**: Nur einer erfolgreich, zweiter erhält HTTP 409

### 2. Accept nach Löschung
**Szenario**: Task wird zwischen Read und Update gelöscht
**Expected**: HTTP 410

### 3. Sehr lange AccessCodes
**Szenario**: AccessCode mit > 64 Zeichen
**Expected**: HTTP 403 (Mismatch)

## Performance Tests

### 1. Concurrent Accepts
- 10 parallele Accept-Requests für verschiedene Tasks
- Erwartung: Alle erfolgreich in < 5 Sekunden

### 2. Large Bundle Response
- Task mit sehr großem E-Rezept Bundle (> 1MB)
- Erwartung: Response in < 2 Sekunden

## Audit Requirements

Für jeden erfolgreichen Accept MUSS ein AuditEvent erstellt werden mit:
- Action: Update
- Subtype: "accept"
- Actor: Telematik-ID und Name der Apotheke
- What: Task Reference
- Patient: KVNR aus Task

## Test-Daten

### Beispiel AccessCode (64 Zeichen)
```
af3e8f9c2b4d6a1e7f5c9b3a8d2e1f4c6b9a3e7d5f2c8a1b4e6d9f3c7a2e5b8d1f
```

### Beispiel Secret (64 Zeichen)
```
d7f2e9a4c6b8e1f3a5c7b9d2e4f6a8c1b3d5e7f9a2c4e6b8d1f3a5c7e9b2d4f6a8
```

### Beispiel Telematik-IDs
- Apotheke: `3-SMC-B-Testkarte-883110000116873`
- Krankenhaus: `1-SMC-B-Testkarte-883110000116699`
- Kostenträger: `5-SMC-B-Testkarte-883110000116701`