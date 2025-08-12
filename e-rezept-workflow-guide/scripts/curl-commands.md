# E-Rezept Workflow - curl Befehle

Diese Datei enthält alle curl-Befehle für den manuellen Test des E-Rezept-Workflows.

## Voraussetzungen

- HAPI FHIR Server läuft auf `http://localhost:8080`
- Gültige Access Tokens für die verschiedenen Rollen
- jq für JSON-Verarbeitung (optional aber empfohlen)

## 1. Create Operation

Erstellt einen neuen Task im Status "draft":

```bash
# Erstelle E-Rezept mit FlowType 160
curl -X POST "http://localhost:8080/fhir/Task/\$create" \
  -H "Authorization: Bearer {ACCESS_TOKEN_ARZT}" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Parameters",
    "parameter": [
      {
        "name": "workflowType",
        "valueCoding": {
          "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
          "code": "160",
          "display": "Muster 16 (Apothekenpflichtige Arzneimittel)"
        }
      }
    ]
  }' | jq '.'
```

**Wichtige Werte aus der Response speichern:**
- Prescription ID: `.parameter[0].resource.id`
- Access Code: `.parameter[0].resource.identifier[] | select(.system | contains("AccessCode")) | .value`

## 2. Activate Operation

Aktiviert den Task mit einem signierten KBV-Bundle:

```bash
# Aktiviere Task (ersetze PRESCRIPTION_ID und ACCESS_CODE)
curl -X POST "http://localhost:8080/fhir/Task/{PRESCRIPTION_ID}/\$activate" \
  -H "Authorization: Bearer {ACCESS_TOKEN_ARZT}" \
  -H "X-AccessCode: {ACCESS_CODE}" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Parameters",
    "parameter": [
      {
        "name": "ePrescription",
        "resource": {
          "resourceType": "Binary",
          "contentType": "application/pkcs7-mime",
          "data": "{BASE64_ENCODED_SIGNED_BUNDLE}"
        }
      }
    ]
  }' | jq '.'
```

**Status sollte zu "ready" wechseln**

## 3. Accept Operation

Apotheke nimmt das E-Rezept an:

### Option A: Access Code als Query-Parameter

```bash
# Accept mit Query-Parameter
curl -X POST "http://localhost:8080/fhir/Task/{PRESCRIPTION_ID}/\$accept?ac={ACCESS_CODE}" \
  -H "Authorization: Bearer {ACCESS_TOKEN_APOTHEKE}" \
  -H "Content-Type: application/fhir+json" | jq '.'
```

### Option B: Access Code als Header

```bash
# Accept mit Header
curl -X POST "http://localhost:8080/fhir/Task/{PRESCRIPTION_ID}/\$accept" \
  -H "Authorization: Bearer {ACCESS_TOKEN_APOTHEKE}" \
  -H "X-AccessCode: {ACCESS_CODE}" \
  -H "Content-Type: application/fhir+json" | jq '.'
```

**Wichtige Werte aus der Response speichern:**
- Secret: `.entry[0].resource.identifier[] | select(.system | contains("Secret")) | .value`

## 4. Close Operation

Schließt das E-Rezept mit Dispensierinformationen ab:

```bash
# Close mit MedicationDispense (ersetze SECRET)
curl -X POST "http://localhost:8080/fhir/Task/{PRESCRIPTION_ID}/\$close" \
  -H "Authorization: Bearer {ACCESS_TOKEN_APOTHEKE}" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Parameters",
    "parameter": [
      {
        "name": "secret",
        "valueString": "{SECRET}"
      },
      {
        "name": "rxDispensation",
        "resource": {
          "resourceType": "Parameters",
          "parameter": [
            {
              "name": "medicationDispense",
              "resource": {
                "resourceType": "MedicationDispense",
                "meta": {
                  "profile": [
                    "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense"
                  ]
                },
                "status": "completed",
                "subject": {
                  "identifier": {
                    "system": "http://fhir.de/sid/gkv/kvid-10",
                    "value": "S040464113"
                  }
                },
                "whenHandedOver": "2025-08-11T14:30:00+02:00",
                "medicationCodeableConcept": {
                  "coding": [
                    {
                      "system": "http://fhir.de/CodeSystem/ifa/pzn",
                      "code": "06313728",
                      "display": "Sumatriptan-1A Pharma 100 mg Tabletten"
                    }
                  ]
                },
                "quantity": {
                  "value": 1,
                  "unit": "St",
                  "system": "http://unitsofmeasure.org",
                  "code": "{tbl}"
                }
              }
            }
          ]
        }
      }
    ]
  }' | jq '.'
```

## Hilfsbefehle

### Task abrufen

```bash
# Task mit ID abrufen
curl -X GET "http://localhost:8080/fhir/Task/{PRESCRIPTION_ID}" \
  -H "Authorization: Bearer {ACCESS_TOKEN}" | jq '.'
```

### Task suchen

```bash
# Suche nach Prescription ID
curl -X GET "http://localhost:8080/fhir/Task?identifier=https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId|{PRESCRIPTION_ID}" \
  -H "Authorization: Bearer {ACCESS_TOKEN}" | jq '.'
```

### Server-Metadaten

```bash
# Capability Statement abrufen
curl -X GET "http://localhost:8080/fhir/metadata" | jq '.'
```

## Tipps

1. **jq verwenden:** Pipe die Ausgabe durch `jq '.'` für formatiertes JSON
2. **Werte extrahieren:** Nutze jq zum Extrahieren spezifischer Werte:
   ```bash
   # Prescription ID extrahieren
   curl ... | jq -r '.parameter[0].resource.id'
   ```
3. **Variablen nutzen:** Speichere wichtige Werte in Shell-Variablen:
   ```bash
   PRESCRIPTION_ID=$(curl ... | jq -r '.parameter[0].resource.id')
   ```
4. **Fehler debuggen:** Bei Fehlern die Response genau prüfen:
   ```bash
   curl -v ... 2>&1 | less
   ```

## Fehlerbehandlung

Bei Fehlern prüfen:
- HTTP Status Code
- OperationOutcome in der Response
- Authorization Header korrekt?
- Access Code/Secret korrekt?
- Task im richtigen Status?