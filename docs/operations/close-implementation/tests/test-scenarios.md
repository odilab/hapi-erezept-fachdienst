# Testszenarien für $close Operation

## Vorbedingungen

Für alle Tests gilt:
1. Task wurde mit `$create` erstellt (Status: draft)
2. Task wurde mit `$activate` aktiviert (Status: ready)
3. Task wurde mit `$accept` akzeptiert (Status: in-progress)
4. Gültiges Access Token mit Apotheken-Rolle (oid_oeffentliche_apotheke oder oid_krankenhausapotheke)

## Success Cases

### 1. Standard Close mit MedicationDispense
**Setup:**
- Task im Status `in-progress`
- Gültiges Secret im Query-Parameter
- MedicationDispense im Request Body

**Request:**
```
POST /Task/160.000.000.000.000.01/$close?secret=<secret>
Authorization: Bearer <token_apotheke>
Content-Type: application/fhir+xml

<Parameters>
  <parameter>
    <name value="rxDispensation"/>
    <part>
      <name value="medicationDispense"/>
      <resource>
        <MedicationDispense>
          <!-- Vollständige MedicationDispense -->
        </MedicationDispense>
      </resource>
    </part>
  </parameter>
</Parameters>
```

**Expected:**
- HTTP 200 OK
- Receipt Bundle mit Signatur zurückgegeben
- Task Status = completed
- Communications gelöscht

### 2. Close ohne MedicationDispense (Body leer)
**Setup:**
- Task im Status `in-progress`
- MedicationDispense wurde bereits bei vorherigem Aufruf gespeichert

**Request:**
```
POST /Task/160.000.000.000.000.01/$close?secret=<secret>
Authorization: Bearer <token_apotheke>
```

**Expected:**
- HTTP 200 OK
- Receipt Bundle zurückgegeben
- Verwendet existierende MedicationDispense

### 3. Close mit mehreren MedicationDispenses
**Setup:**
- Mehrfachverordnung
- Mehrere MedicationDispense-Objekte

**Request:**
```xml
<Parameters>
  <parameter>
    <name value="rxDispensation"/>
    <part>
      <name value="medicationDispense"/>
      <resource><!-- MedicationDispense 1 --></resource>
    </part>
  </parameter>
  <parameter>
    <name value="rxDispensation"/>
    <part>
      <name value="medicationDispense"/>
      <resource><!-- MedicationDispense 2 --></resource>
    </part>
  </parameter>
</Parameters>
```

**Expected:**
- Alle MedicationDispenses verarbeitet
- Receipt Bundle enthält alle Informationen

## Error Cases

### 1. Task nicht gefunden
**Setup:**
- Ungültige Task-ID

**Request:**
```
POST /Task/INVALID_ID/$close?secret=<secret>
```

**Expected:**
- HTTP 404 Not Found
- Message: "Task not found for prescription id"

### 2. Task bereits gelöscht (cancelled)
**Setup:**
- Task mit Status `cancelled`

**Expected:**
- HTTP 410 Gone
- Message: "Task has already been deleted"

### 3. Falscher Task-Status (nicht in-progress)
**Setup:**
- Task im Status `ready` oder `draft` oder `completed`

**Expected:**
- HTTP 403 Forbidden
- Message: "Task has to be in progress, but is: [status]"

### 4. Fehlendes Secret
**Request:**
```
POST /Task/160.000.000.000.000.01/$close
Authorization: Bearer <token_apotheke>
```

**Expected:**
- HTTP 403 Forbidden
- Message: "No secret provided"

### 5. Falsches Secret
**Request:**
```
POST /Task/160.000.000.000.000.01/$close?secret=WRONG_SECRET
```

**Expected:**
- HTTP 403 Forbidden
- Message: "Invalid secret provided for Task"

### 6. Falsche Rolle (nicht Apotheke)
**Setup:**
- Access Token mit Arzt-Rolle

**Expected:**
- HTTP 403 Forbidden
- Message: "Die Operation $close ist nur für Apotheken erlaubt"

### 7. Keine MedicationDispense vorhanden (bei leerem Body)
**Setup:**
- Leerer Request Body
- Keine existierende MedicationDispense für Task

**Expected:**
- HTTP 403 Forbidden
- Message: "Abschluss des Workflows konnte nicht durchgeführt werden. Dispensierinformationen wurden nicht bereitgestellt."

### 8. Falsches MedicationDispense-Profil für Flowtype
**Setup:**
- Flowtype 162 (DiGA) mit normalem MedicationDispense-Profil

**Expected:**
- HTTP 422 Unprocessable Entity
- Message: "Unzulässige Abgabeinformationen: Für diesen Workflow sind nur Abgabeinformationen für digitale Gesundheitsanwendungen zulässig."

### 9. MedicationDispense mit falscher Prescription-ID
**Setup:**
- MedicationDispense referenziert anderen Task

**Expected:**
- HTTP 422 Unprocessable Entity
- Message: "MedicationDispense references wrong prescription"

### 10. MedicationDispense mit falscher KVNR
**Setup:**
- MedicationDispense hat andere KVNR als Task

**Expected:**
- HTTP 422 Unprocessable Entity
- Message: "MedicationDispense has wrong KVNR"

## Edge Cases

### 1. Future whenHandedOver
**Setup:**
- MedicationDispense mit whenHandedOver in der Zukunft

**Expected:**
- Validierungsfehler oder Anpassung auf aktuelle Zeit

### 2. Sehr große MedicationDispense Bundle
**Setup:**
- Viele MedicationDispense-Objekte

**Expected:**
- Performance sollte akzeptabel bleiben
- Alle Objekte verarbeitet

### 3. Parallele Close-Aufrufe
**Setup:**
- Zwei gleichzeitige Close-Aufrufe für denselben Task

**Expected:**
- Nur einer erfolgreich
- Zweiter erhält Fehler (Status bereits completed)

## Flowtype-spezifische Tests

### Flowtype 160/169 (Muster 16)
- Standard Arzneimittel-Workflow
- MedicationDispense mit GEM_ERP_PR_MedicationDispense Profil

### Flowtype 200/209 (PKV)
- PKV-Workflow
- Consent-Handling beachten

### Flowtype 162 (DiGA)
- Digitale Gesundheitsanwendungen
- MedicationDispense mit GEM_ERP_PR_MedicationDispense_DiGA Profil
- Header für DiGA Redeem Code

## Integrationstests

### Vollständiger Workflow
1. Create Task → Status: draft
2. Activate Task → Status: ready
3. Accept Task → Status: in-progress, Secret generiert
4. Close Task → Status: completed, Receipt erstellt

### Audit-Trail
- Prüfe dass AuditEvent korrekt erstellt wird
- Actor, Action, Outcome validieren
- KVNR-Referenz prüfen

## Performance Tests

### Load Test
- 100 parallele Close-Operationen
- Response-Zeit < 2 Sekunden

### Stress Test
- Maximale Last ermitteln
- Verhalten bei Überlastung prüfen