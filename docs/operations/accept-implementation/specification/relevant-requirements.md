# Anforderungen für Accept Operation

## Berechtigungsmatrix

### Erlaubte Profession OIDs

Für **Flowtype 160, 169, 200, 209**:
- `oid_oeffentliche_apotheke` - Öffentliche Apotheke
- `oid_krankenhausapotheke` - Krankenhausapotheke

Für **Flowtype 162** (DiGA):
- `oid_kostentraeger` - Kostenträger/Krankenkasse

**Anforderung**: A_19166-01, A_25993

## Validierungsregeln

### 1. AccessCode Prüfung (A_19167-04)
- **Regel**: AccessCode muss im Query-Parameter `?ac=...` oder HTTP-Header `X-AccessCode` vorhanden sein
- **Prüfung**: AccessCode muss mit `Task.identifier:AccessCode` übereinstimmen
- **Fehler**: HTTP 403 "Access code does not match"

### 2. Task Status Prüfung (A_19168-01)
- **Status `draft`**: HTTP 409 "Task has invalid status draft"
- **Status `completed`**: HTTP 409 "Task has invalid status completed"  
- **Status `in-progress`**: 
  - Wenn Task.owner == telematikId: HTTP 409 "Task has invalid status in-progress" + "Task is processed by requesting institution"
  - Sonst: HTTP 409 "Task has invalid status in-progress"
- **Status `ready`**: OK - Operation kann durchgeführt werden

### 3. Task gelöscht/cancelled (A_19149-02)
- **Regel**: Wenn Task.status = `cancelled` oder kein AccessCode vorhanden
- **Fehler**: HTTP 410 "Task meanwhile deleted for prescription id"

### 4. Einlösefrist prüfen (A_23539-01)
- **Regel**: Task.ExpiryDate + 24h muss in der Zukunft liegen
- **Fehler**: HTTP 403 "Verordnung bis {ExpiryDate} einlösbar."

### 5. Mehrfachverordnung Startdatum (A_22635-02)
- **Regel**: Bei MVO muss das Startdatum erreicht sein
- **Prüfung**: `MedicationRequest.extension:Mehrfachverordnung.extension:Zeitraum.start`
- **Fehler**: HTTP 403 "Teilverordnung zur Mehrfachverordnung {MVO-ID} ist ab {Startdatum} einlösbar."

## Geschäftslogik

### Secret Generierung (A_19169-01)
- 256 Bit Zufallszahl generieren (32 Bytes)
- Hexadezimal kodieren (64 Zeichen, [0-9a-f]{64})
- Als `Task.identifier:Secret` speichern
- NamingSystem: `https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret`

### Status Update (A_19169-01)
- Task.status auf `in-progress` setzen
- Task.lastModified aktualisieren

### Telematik-ID speichern (A_24174)
- Telematik-ID aus ACCESS_TOKEN extrahieren
- In `Task.owner` speichern
- Format: Reference mit Display der Telematik-ID

### Consent für PKV (A_22110)
- Bei Flowtype 200/209:
  - Prüfen ob Consent für KVNR existiert
  - Consent.category.coding.code = "CHARGCONS"
  - Falls vorhanden: Consent in Response Bundle aufnehmen

## Response Format

### Bundle Struktur
```json
{
  "resourceType": "Bundle",
  "type": "collection",
  "link": [{
    "relation": "self",
    "url": "/Task/{id}/$accept/"
  }],
  "entry": [
    {
      "fullUrl": "/Task/{id}",
      "resource": { /* Task mit Status in-progress und Secret */ }
    },
    {
      "fullUrl": "urn:uuid:...",
      "resource": { /* Binary mit signiertem E-Rezept */ }
    },
    {
      // Optional bei PKV:
      "fullUrl": "/Consent/{id}",
      "resource": { /* Consent Resource */ }
    }
  ]
}
```

### Meta-Profile
Output muss folgendes Profile haben:
```
https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_AcceptOperation_Output|1.5
```

## Audit Requirements

### AuditEvent erstellen
- Action: Update (U)
- Subtype: "accept"
- Outcome: Success (0)
- What: Task Reference
- Who: Telematik-ID und Organisationsname aus Access Token
- Patient: KVNR aus Task.for

### Details hinzufügen
- prescription-id: Task PrescriptionId
- task-status: "in-progress"
- secret: Generiertes Secret (nur für interne Logs)
- kvnr: Versicherten-KVNR