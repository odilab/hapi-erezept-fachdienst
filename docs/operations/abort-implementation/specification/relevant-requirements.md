# Relevante Anforderungen für $abort Operation

## Anforderungs-IDs aus gemSpec_FD_eRp_V2.3.0.xml

### A_19145 - Prüfung Task Status für Apotheke
- **Beschreibung**: Task muss im Status "in-progress" sein für Apotheke
- **HTTP Status**: 403 Forbidden
- **Fehlermeldung**: "Task must be in progress for user pharmacy"

### A_19146 - Prüfung Task Status für andere Nutzer
- **Beschreibung**: Task darf NICHT im Status "in-progress" sein für Nutzer außer Apotheke
- **HTTP Status**: 403 Forbidden
- **Fehlermeldung**: "Task must not be in progress for users other than pharmacy"

### A_19224 - Secret Prüfung für Apotheke
- **Beschreibung**: Bei Apotheke muss das Secret als URL-Parameter ?secret=... übergeben und validiert werden
- **HTTP Status**: 403 Forbidden bei Mismatch
- **VAU Error Code**: brute_force

### A_20703 - VAU Error Code bei AccessCode/Secret Mismatch
- **Beschreibung**: VAU-Error-Code Header auf "brute_force" setzen bei AccessCode oder Secret Mismatch
- **HTTP Status**: 403 Forbidden

### A_19120_3 - AccessCode Prüfung für verordnende Leistungserbringer
- **Beschreibung**: Ärzte müssen gültigen AccessCode im Header X-AccessCode oder URL-Parameter ?ac=... übergeben
- **Status Anforderung**: Task muss im Status "ready" sein für Ärzte
- **HTTP Status**: 403 Forbidden bei Fehler

### A_20546_03 - KVNR Prüfung für Versicherte
- **Beschreibung**: Bei Versicherten muss die KVNR aus dem AccessToken mit der Task.for KVNR übereinstimmen
- **AuditEventId**: POST_Task_abort_insurant

### A_20547 - AccessCode Prüfung für Vertreter
- **Beschreibung**: Bei KVNR Mismatch (Vertreter) muss AccessCode validiert werden
- **AuditEventId**: POST_Task_abort_representative

### A_22102_01 - Einschränkung für Workflow 169/209
- **Beschreibung**: Versicherte dürfen Tasks mit Flowtype 169/209 nur im Status "completed" löschen
- **HTTP Status**: 403 Forbidden
- **Fehlermeldung**: "Abort for patient in workflow types 169 / 209 only allowed for completed Task"

### A_19121 - Task Status auf cancelled setzen
- **Beschreibung**: Bei erfolgreicher Löschung wird Task.status auf "cancelled" gesetzt

### A_19027_06 - Löschung personenbezogener Daten
- **Beschreibung**: Alle personenbezogenen medizinischen Daten außer KVNR in Task.for müssen gelöscht werden
- **Betrifft**: 
  - Task-bezogene Communications
  - HealthCareProviderPrescription
  - PatientConfirmation
  - Receipt
  - MedicationDispense
  - AccessCode
  - Secret
  - Owner

### A_19514 - HTTP Status Code
- **Beschreibung**: Erfolgreiche Operation liefert HTTP 204 No Content

## Berechtigungsmatrix

### Erlaubte Profession OIDs

#### Versicherte/Vertreter
- `oid_versicherter`
- Bedingungen:
  - Eigene KVNR: Direkt erlaubt (außer Status in-progress)
  - Andere KVNR: Nur mit gültigem AccessCode (Vertreter)
  - Flowtype 169/209: Nur Status completed

#### Verordnende Leistungserbringer (Ärzte)
- `oid_arzt`
- `oid_zahnarzt`
- `oid_praxis_arzt`
- `oid_zahnarztpraxis`
- `oid_praxis_psychotherapeut`
- `oid_krankenhaus`
- Bedingungen:
  - Nur Status "ready"
  - Gültiger AccessCode erforderlich

#### Abgebende Leistungserbringer (Apotheken)
- `oid_oeffentliche_apotheke`
- `oid_krankenhausapotheke`
- Bedingungen:
  - Nur Status "in-progress"
  - Gültiges Secret erforderlich

## Status-Anforderungen

### Nicht erlaubte Status
- **draft**: "Abort not expected for newly created Task" (403 Forbidden)
- **cancelled**: "Task has already been deleted" (410 Gone)

### Status-spezifische Berechtigungen
| Status | Versicherter | Vertreter | Arzt | Apotheke |
|--------|-------------|-----------|------|----------|
| draft | ❌ | ❌ | ❌ | ❌ |
| ready | ✅ | ✅ (mit AC) | ✅ (mit AC) | ❌ |
| in-progress | ❌ | ❌ | ❌ | ✅ (mit Secret) |
| completed | ✅ | ✅ (mit AC) | ❌ | ❌ |
| cancelled | ❌ | ❌ | ❌ | ❌ |

## Validierungsregeln

### 1. Task Existenz
- Task muss existieren (404 Not Found)

### 2. Task Status Grundprüfung
- Status != cancelled (410 Gone)
- Status != draft (403 Forbidden)

### 3. Flowtype-spezifische Prüfung (nur Versicherte)
- Flowtype 169/209: Status muss completed sein

### 4. Rollenbasierte Validierung

#### Apotheke
1. Status muss "in-progress" sein
2. Secret muss als URL-Parameter übergeben werden
3. Secret muss mit Task.identifier:Secret übereinstimmen

#### Versicherte (eigenes Rezept)
1. Status darf nicht "in-progress" sein
2. KVNR aus AccessToken == Task.for KVNR

#### Versicherte (Vertreter)
1. Status darf nicht "in-progress" sein
2. AccessCode muss übergeben werden (Header oder URL-Parameter)
3. AccessCode muss mit Task.identifier:AccessCode übereinstimmen

#### Ärzte
1. Status muss "ready" sein
2. AccessCode muss übergeben werden
3. AccessCode muss mit Task.identifier:AccessCode übereinstimmen

## Audit-Event IDs

- `POST_Task_abort_insurant` - Versicherter löscht eigenes Rezept
- `POST_Task_abort_representative` - Vertreter löscht Rezept
- `POST_Task_abort_doctor` - Arzt löscht Rezept
- `POST_Task_abort_pharmacy` - Apotheke löscht Rezept

## Fehlerszenarien

| Szenario | HTTP Status | Fehlermeldung |
|----------|-------------|---------------|
| Task nicht gefunden | 404 | "Task not found for prescription id" |
| Task bereits gelöscht | 410 | "Task has already been deleted" |
| Task im Status draft | 403 | "Abort not expected for newly created Task" |
| Apotheke ohne in-progress | 403 | "Task must be in progress for user pharmacy" |
| Andere mit in-progress | 403 | "Task must not be in progress for users other than pharmacy" |
| Arzt ohne ready Status | 403 | "Task must be ready for doctor" |
| AccessCode Mismatch | 403 | "AccessCode mismatch" |
| Secret Mismatch | 403 | "No or invalid secret provided for user pharmacy" |
| Flowtype 169/209 nicht completed | 403 | "Abort for patient in workflow types 169 / 209 only allowed for completed Task" |