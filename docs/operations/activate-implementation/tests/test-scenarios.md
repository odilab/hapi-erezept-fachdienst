# Testszenarien für $activate Operation

## Vorbedingungen

- Task wurde mit CREATE erstellt (Status: draft)
- Gültiges Access Token mit erlaubter Profession OID
- Gültiger AccessCode vom erstellten Task
- Signiertes KBV Bundle mit passender PrescriptionID

## Success Cases

### 1. Standard Aktivierung (Workflow 160)
**Input**: 
- Task im Status draft
- Gültiges signiertes Bundle (Workflow 160)
- Korrekter AccessCode
- SMCB_KRANKENHAUS Token

**Expected**: 
- Task Status wird zu "ready"
- KVNR wird gespeichert
- ExpiryDate = Signaturdatum + 3 Monate
- AcceptDate = Signaturdatum + 28 Tage
- HTTP 200 OK

### 2. PKV Aktivierung (Workflow 200)
**Input**:
- Task im Status draft (Workflow 200)
- Signiertes Bundle mit PKV Coverage
- Korrekter AccessCode

**Expected**:
- Task Status wird zu "ready"
- AcceptDate = Signaturdatum + 3 Monate (nicht 28 Tage!)
- HTTP 200 OK

### 3. Aktivierung mit Warnung (ANR ungültig)
**Input**:
- Task im Status draft
- Bundle mit ungültiger ANR
- Config: anr.validation.mode=warning

**Expected**:
- Task wird aktiviert
- HTTP 200 mit Warning Header
- Warning: "252 erp-server \"Ungültige Arztnummer\""

## Error Cases

### 1. Task nicht gefunden
**Input**: Ungültige Task ID
**Expected**: HTTP 404 "Requested Task not found in DB"

### 2. Task bereits gelöscht
**Input**: Task im Status cancelled
**Expected**: HTTP 410 "Task has already been deleted"

### 3. Falscher AccessCode
**Input**: Ungültiger AccessCode Header
**Expected**: HTTP 403 "Access code does not match"

### 4. Task nicht im Status draft
**Input**: Task im Status ready/in-progress/completed
**Expected**: HTTP 403 "Task not in status draft but in status X"

### 5. Ungültige PKCS#7 Struktur
**Input**: Beschädigtes Base64 oder ungültige Signatur
**Expected**: HTTP 400 "Invalid PKCS#7 structure"

### 6. PrescriptionID Mismatch
**Input**: Bundle mit anderer PrescriptionID als Task
**Expected**: HTTP 400 "PrescriptionID mismatch"

### 7. AuthoredOn != Signaturdatum
**Input**: Bundle mit authoredOn 2025-08-04, signiert am 2025-08-05
**Expected**: HTTP 400 "AuthoredOn does not match signing date"

### 8. BTM/Thalidomid
**Input**: Bundle mit BTM-Medikament
**Expected**: HTTP 400 "BTM und Thalidomid nicht zulässig"

### 9. Coverage Type Mismatch
**Input**: PKV Coverage bei Workflow 160
**Expected**: HTTP 400 "PKV coverage not allowed for workflow 160"

### 10. Ungültige KVNR
**Input**: Bundle mit KVNR "B123456789" (ungültige Prüfziffer)
**Expected**: HTTP 400 "Ungültige Versichertennummer"

### 11. Unerlaubte Profession OID
**Input**: Access Token mit oid_apotheke
**Expected**: HTTP 403 "Operation nur für verordnende Leistungserbringer erlaubt"

### 12. Fehlender ePrescription Parameter
**Input**: Leere Parameters
**Expected**: HTTP 400 "ePrescription parameter is missing"

## Edge Cases

### 1. MVO (Mehrfachverordnung)
**Special**: 
- ExpiryDate und AcceptDate aus MVO Extension
- Oder bei fehlendem End-Datum: Signaturdatum + 365 Tage

### 2. DiGA (Workflow 209)
**Special**:
- Andere Bundle-Struktur (EvdgaBundle statt KbvBundle)
- Keine alternativeID in Coverage erlaubt

### 3. Entlassrezept
**Special**:
- LegalBasisCode prüfen
- Andere Gültigkeitsdauer möglich

## Abhängigkeiten

1. **CREATE muss vorher laufen**: Task muss im Status draft existieren
2. **Nach ACTIVATE**: Task kann mit ACCEPT, ABORT oder REJECT weiterverarbeitet werden
3. **Signatur-Tool**: Tests benötigen Zugriff auf /signDocumentWithTool Endpoint