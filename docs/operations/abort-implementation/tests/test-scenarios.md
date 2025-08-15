# Testszenarien für $abort Operation

## Vorbedingungen

Für alle Tests wird benötigt:
- Gültiges Access Token mit entsprechender Rolle
- Task in verschiedenen Status (erstellt über $create, $activate, $accept)
- AccessCode oder Secret je nach Szenario

## Success Cases

### 1. Patient löscht eigenes Rezept
- **Input**: Task im Status "ready", KVNR stimmt überein
- **Expected**: Task Status wird "cancelled", personenbezogene Daten gelöscht
- **HTTP Status**: 204 No Content
- **AuditEventId**: POST_Task_abort_insurant

### 2. Vertreter löscht Rezept mit AccessCode
- **Input**: Task im Status "ready", andere KVNR, gültiger AccessCode
- **Expected**: Task Status wird "cancelled"
- **HTTP Status**: 204 No Content
- **AuditEventId**: POST_Task_abort_representative

### 3. Arzt löscht Rezept im Status ready
- **Input**: Task im Status "ready", gültiger AccessCode
- **Expected**: Task Status wird "cancelled"
- **HTTP Status**: 204 No Content
- **AuditEventId**: POST_Task_abort_doctor

### 4. Apotheke löscht Rezept im Status in-progress
- **Input**: Task im Status "in-progress", gültiges Secret
- **Expected**: Task Status wird "cancelled"
- **HTTP Status**: 204 No Content
- **AuditEventId**: POST_Task_abort_pharmacy

### 5. Patient löscht completed Task (Flowtype 169/209)
- **Input**: Task im Status "completed", Flowtype 169 oder 209
- **Expected**: Task Status wird "cancelled"
- **HTTP Status**: 204 No Content

## Error Cases

### 1. Task nicht gefunden
- **Input**: Nicht existierende Task ID
- **Expected**: 404 Not Found
- **Message**: "Task not found for prescription id"

### 2. Task bereits gelöscht
- **Input**: Task im Status "cancelled"
- **Expected**: 410 Gone
- **Message**: "Task has already been deleted"

### 3. Task im Status draft
- **Input**: Task im Status "draft"
- **Expected**: 403 Forbidden
- **Message**: "Abort not expected for newly created Task"

### 4. Apotheke ohne Secret
- **Input**: Apotheke versucht Löschung ohne Secret-Parameter
- **Expected**: 403 Forbidden
- **Message**: "No secret provided for user pharmacy"

### 5. Apotheke mit falschem Secret
- **Input**: Apotheke mit ungültigem Secret
- **Expected**: 403 Forbidden
- **Message**: "Invalid secret provided for user pharmacy"

### 6. Apotheke mit Task nicht in-progress
- **Input**: Apotheke versucht Task in anderem Status zu löschen
- **Expected**: 403 Forbidden
- **Message**: "Task must be in progress for user pharmacy, is: {status}"

### 7. Andere Nutzer mit Task in-progress
- **Input**: Patient/Arzt versucht Task im Status "in-progress" zu löschen
- **Expected**: 403 Forbidden
- **Message**: "Task must not be in progress for users other than pharmacy"

### 8. Arzt mit Task nicht ready
- **Input**: Arzt versucht Task in Status != "ready" zu löschen
- **Expected**: 403 Forbidden
- **Message**: "Task must be ready for doctor, but is: {status}"

### 9. AccessCode fehlt
- **Input**: Vertreter/Arzt ohne AccessCode
- **Expected**: 403 Forbidden
- **Message**: "AccessCode missing"

### 10. AccessCode falsch
- **Input**: Vertreter/Arzt mit ungültigem AccessCode
- **Expected**: 403 Forbidden
- **Message**: "AccessCode mismatch"

### 11. Flowtype 169/209 nicht completed
- **Input**: Patient versucht Task mit Flowtype 169/209 in Status != "completed" zu löschen
- **Expected**: 403 Forbidden
- **Message**: "Abort for patient in workflow types 169 / 209 only allowed for completed Task"

## Datenbereinigung Tests

### 1. Löschung von Communications
- **Setup**: Task mit zugehörigen Communications erstellen
- **Action**: Task löschen
- **Expected**: Alle Communications mit basedOn = Task/{id} werden gelöscht

### 2. Entfernung personenbezogener Daten
- **Action**: Task löschen
- **Expected**: 
  - AccessCode entfernt
  - Secret entfernt
  - Owner entfernt
  - HealthCarePrescriptionUuid entfernt
  - PatientConfirmationUuid entfernt
  - ReceiptUuid entfernt
  - LastMedicationDispense entfernt
  - KVNR bleibt erhalten

### 3. Löschung von Binary/Bundle Ressourcen
- **Setup**: Task mit HealthCarePrescription Binary und Receipt Bundle
- **Action**: Task löschen
- **Expected**: Binary und Bundle Ressourcen werden gelöscht

## Audit-Logging Tests

### 1. Audit für Patient
- **Action**: Patient löscht eigenes Rezept
- **Expected**: 
  - AuditEvent.action = "D" (Delete)
  - AuditEvent.subtype = "abort"
  - Agent.who fehlt (da Patient selbst)
  - EventId = POST_Task_abort_insurant

### 2. Audit für Vertreter
- **Action**: Vertreter löscht Rezept
- **Expected**:
  - Agent.who = KVNR des Vertreters
  - Agent.name = Display Name des Vertreters
  - EventId = POST_Task_abort_representative

### 3. Audit für Arzt
- **Action**: Arzt löscht Rezept
- **Expected**:
  - Agent.who = Telematik-ID
  - Agent.name = Organisationsname
  - EventId = POST_Task_abort_doctor

### 4. Audit für Apotheke
- **Action**: Apotheke löscht Rezept
- **Expected**:
  - Agent.who = Telematik-ID
  - Agent.name = Apothekenname
  - EventId = POST_Task_abort_pharmacy

## Testmatrix nach Status und Rolle

| Status | Patient (eigenes) | Patient (Vertreter) | Arzt | Apotheke |
|--------|------------------|---------------------|------|----------|
| draft | ❌ 403 | ❌ 403 | ❌ 403 | ❌ 403 |
| ready | ✅ 204 | ✅ 204 (mit AC) | ✅ 204 (mit AC) | ❌ 403 |
| in-progress | ❌ 403 | ❌ 403 | ❌ 403 | ✅ 204 (mit Secret) |
| completed | ✅ 204 | ✅ 204 (mit AC) | ❌ 403 | ❌ 403 |
| cancelled | ❌ 410 | ❌ 410 | ❌ 410 | ❌ 410 |

## Spezialfall: Flowtype 169/209

| Status | Patient | Vertreter | Arzt | Apotheke |
|--------|---------|-----------|------|----------|
| ready | ❌ 403 | ❌ 403 | ✅ 204 | ❌ 403 |
| completed | ✅ 204 | ✅ 204 | ❌ 403 | ❌ 403 |

## Test-Reihenfolge

1. **Basis-Tests**: Task-Existenz, Status-Prüfungen
2. **Rollen-Tests**: Verschiedene Rollen mit korrekten Bedingungen
3. **Fehler-Tests**: Alle Fehlerfälle durchgehen
4. **Datenbereinigung**: Prüfen dass Daten korrekt gelöscht werden
5. **Audit-Tests**: Korrekte Audit-Einträge prüfen
6. **Spezialfälle**: Flowtype 169/209