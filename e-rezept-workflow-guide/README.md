# E-Rezept Workflow - Vollständige Anleitung

Diese Anleitung beschreibt den kompletten E-Rezept-Workflow von der Erstellung bis zum Abschluss eines elektronischen Rezepts.

## Übersicht

Der E-Rezept-Workflow besteht aus vier Hauptschritten:

1. **$create** - Erstellen eines neuen E-Rezepts (Status: draft)
2. **$activate** - Aktivieren des E-Rezepts mit signiertem KBV-Bundle (Status: ready)
3. **$accept** - Annahme des E-Rezepts durch die Apotheke (Status: in-progress)
4. **$close** - Abschluss mit Dispensierinformationen (Status: completed)

## Voraussetzungen

- HAPI FHIR Server läuft auf `http://localhost:8080`
- IDP-Service und ERP-Service Container sind gestartet
- Gültige Access Tokens für verschiedene Rollen (SMCB_KRANKENHAUS, SMCB_APOTHEKE)
- Signiertes KBV-Bundle für die Aktivierung

## Detaillierter Workflow

### 1. Create Operation - E-Rezept erstellen

**Zweck:** Erstellt einen neuen Task im Status "draft" mit einer eindeutigen Prescription ID und einem Access Code.

**Endpoint:** `POST /fhir/Task/$create`

**Benötigte Rolle:** Verordnender Leistungserbringer (SMCB_KRANKENHAUS)

**Request:**
```http
POST /fhir/Task/$create
Authorization: Bearer {access_token_arzt}
Content-Type: application/fhir+json

{
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
}
```

**Unterstützte FlowTypes:**
- `160` - Muster 16 (Apothekenpflichtige Arzneimittel)
- `169` - Muster 16 (Direkte Zuweisung)
- `200` - PKV (Apothekenpflichtige Arzneimittel)
- `209` - PKV (Direkte Zuweisung)
- `210` - PKV (Abrechnung Apotheke)

**Response:**
```json
{
  "resourceType": "Parameters",
  "meta": {
    "profile": [
      "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_CreateOperation_Output|1.3"
    ]
  },
  "parameter": [
    {
      "name": "return",
      "resource": {
        "resourceType": "Task",
        "id": "160.000.000.000.000.01",
        "status": "draft",
        "intent": "order",
        "identifier": [
          {
            "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
            "value": "160.000.000.000.000.01"
          },
          {
            "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode",
            "value": "1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef"
          }
        ],
        "extension": [
          {
            "url": "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType",
            "valueCoding": {
              "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
              "code": "160"
            }
          }
        ],
        "performerType": [
          {
            "coding": [
              {
                "system": "urn:ietf:rfc:3986",
                "code": "urn:oid:1.2.276.0.76.4.54",
                "display": "Öffentliche Apotheke"
              }
            ]
          }
        ],
        "authoredOn": "2025-08-11T10:00:00+02:00",
        "lastModified": "2025-08-11T10:00:00+02:00"
      }
    }
  ]
}
```

**Wichtige Felder:**
- **Prescription ID:** Eindeutige ID des E-Rezepts (Format: `{flowType}.xxx.xxx.xxx.xxx.xx`)
- **Access Code:** 64-stelliger Hex-String für Patientenzugriff
- **Status:** `draft` - Rezept ist erstellt, aber noch nicht aktiviert

### 2. Activate Operation - E-Rezept aktivieren

**Zweck:** Aktiviert das E-Rezept durch Hinzufügen eines signierten KBV-Bundles. Status wechselt zu "ready".

**Endpoint:** `POST /fhir/Task/{prescriptionId}/$activate`

**Benötigte Rolle:** Verordnender Leistungserbringer (SMCB_KRANKENHAUS)

**Request:**
```http
POST /fhir/Task/160.000.000.000.000.01/$activate
Authorization: Bearer {access_token_arzt}
X-AccessCode: 1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef
Content-Type: application/fhir+json

{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "ePrescription",
      "resource": {
        "resourceType": "Binary",
        "contentType": "application/pkcs7-mime",
        "data": "{base64_encoded_signed_bundle}"
      }
    }
  ]
}
```

**Response:**
```json
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "return",
      "resource": {
        "resourceType": "Task",
        "id": "160.000.000.000.000.01",
        "status": "ready",
        "for": {
          "identifier": {
            "system": "http://fhir.de/sid/gkv/kvid-10",
            "value": "S040464113"
          }
        },
        "restriction": {
          "period": {
            "end": "2025-09-11T23:59:59+02:00"
          }
        },
        "extension": [
          {
            "url": "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_AcceptDate",
            "valueDate": "2025-09-09"
          }
        ]
      }
    }
  ]
}
```

**Wichtige Änderungen:**
- **Status:** Wechselt zu `ready`
- **for:** Enthält KVNR des Patienten aus dem signierten Bundle
- **ExpiryDate:** Gültigkeit des Rezepts (in restriction.period.end)
- **AcceptDate:** Bis wann das Rezept angenommen werden muss

### 3. Accept Operation - E-Rezept annehmen

**Zweck:** Apotheke nimmt das E-Rezept an. Status wechselt zu "in-progress" und ein Secret wird generiert.

**Endpoint:** `POST /fhir/Task/{prescriptionId}/$accept`

**Benötigte Rolle:** Apotheke (SMCB_APOTHEKE)

**Request (mit Query Parameter):**
```http
POST /fhir/Task/160.000.000.000.000.01/$accept?ac=1234567890abcdef...
Authorization: Bearer {access_token_apotheke}
Content-Type: application/fhir+json
```

**Alternative Request (mit Header):**
```http
POST /fhir/Task/160.000.000.000.000.01/$accept
Authorization: Bearer {access_token_apotheke}
X-AccessCode: 1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef
Content-Type: application/fhir+json
```

**Response:**
```json
{
  "resourceType": "Bundle",
  "type": "collection",
  "meta": {
    "profile": [
      "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_AcceptOperation_Output|1.3"
    ]
  },
  "entry": [
    {
      "resource": {
        "resourceType": "Task",
        "id": "160.000.000.000.000.01",
        "status": "in-progress",
        "identifier": [
          {
            "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
            "value": "160.000.000.000.000.01"
          },
          {
            "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret",
            "value": "fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321"
          }
        ],
        "owner": {
          "display": "3-SMC-B-Testkarte-883110000129084"
        }
      }
    },
    {
      "resource": {
        "resourceType": "Binary",
        "contentType": "application/pkcs7-mime",
        "data": "{base64_encoded_signed_kbv_bundle}"
      }
    }
  ]
}
```

**Wichtige Änderungen:**
- **Status:** Wechselt zu `in-progress`
- **Secret:** Neuer 64-stelliger Hex-String für Close-Operation
- **Owner:** Telematik-ID der annehmenden Apotheke
- **Bundle:** Enthält Task und signiertes KBV-Bundle

### 4. Close Operation - E-Rezept abschließen

**Zweck:** Schließt das E-Rezept ab und dokumentiert die Dispensierung. Status wechselt zu "completed".

**Endpoint:** `POST /fhir/Task/{prescriptionId}/$close`

**Benötigte Rolle:** Apotheke (SMCB_APOTHEKE)

**Request:**
```http
POST /fhir/Task/160.000.000.000.000.01/$close
Authorization: Bearer {access_token_apotheke}
Content-Type: application/fhir+json

{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "secret",
      "valueString": "fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321"
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
}
```

**Response:**
```json
{
  "resourceType": "Bundle",
  "type": "document",
  "meta": {
    "profile": [
      "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle|1.3"
    ]
  },
  "entry": [
    {
      "resource": {
        "resourceType": "Composition",
        "status": "final",
        "type": {
          "coding": [
            {
              "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_DocumentType",
              "code": "3",
              "display": "Receipt"
            }
          ]
        },
        "date": "2025-08-11T14:30:00+02:00",
        "author": [
          {
            "display": "E-Rezept Fachdienst"
          }
        ],
        "title": "Quittung",
        "section": [
          {
            "entry": [
              {
                "reference": "MedicationDispense/1"
              }
            ]
          }
        ]
      }
    },
    {
      "resource": {
        "resourceType": "MedicationDispense",
        "id": "1"
      }
    }
  ]
}
```

**Wichtige Punkte:**
- **Status:** Task wechselt zu `completed`
- **Receipt:** Bundle vom Typ "document" mit Quittung
- **MedicationDispense:** Dokumentiert die abgegebenen Medikamente

## Fehlerbehandlung

### Häufige Fehler

| Fehlercode | Ursache | Lösung |
|------------|---------|--------|
| 401 | Fehlende/ungültige Authentifizierung | Gültiges Access Token verwenden |
| 403 | Ungültiger Access Code/Secret oder falsche Rolle | Korrekten Code verwenden, richtige Rolle sicherstellen |
| 404 | Task nicht gefunden | Prescription ID prüfen |
| 409 | Ungültiger Task-Status für Operation | Task muss im richtigen Status sein |
| 422 | Validierungsfehler | Request-Daten prüfen |

### Status-Übergänge

```
draft → ready → in-progress → completed
```

Jede Operation ist nur in einem bestimmten Status möglich:
- **$create:** Erstellt neuen Task (Status: draft)
- **$activate:** Nur bei Status "draft" → "ready"
- **$accept:** Nur bei Status "ready" → "in-progress"
- **$close:** Nur bei Status "in-progress" → "completed"

## Test-Szenarien

### Erfolgreicher Workflow

1. Task erstellen mit FlowType 160
2. Task mit signiertem Bundle aktivieren
3. Task mit Access Code annehmen
4. Task mit Secret und MedicationDispense abschließen

### Fehler-Szenarien

1. **Falscher Access Code:** Accept mit ungültigem Code → 403 Forbidden
2. **Falsches Secret:** Close mit ungültigem Secret → 403 Forbidden
3. **Falscher Status:** Accept auf draft Task → 409 Conflict
4. **Falsche Rolle:** Close als Arzt statt Apotheke → 403 Forbidden
5. **Doppelte Operation:** Accept zweimal auf gleichen Task → 409 Conflict

## Testdaten

Siehe Ordner `test-data/` für:
- Beispiel KBV-Bundles
- Test-Requests für jede Operation
- Erwartete Responses

## Automatisierung

Siehe Ordner `scripts/` für:
- Shell-Skripte für den kompletten Workflow
- Postman Collection
- curl-Befehle

## Weitere Informationen

- [gematik E-Rezept Spezifikation](https://simplifier.net/erezept)
- [FHIR R4 Dokumentation](https://www.hl7.org/fhir/R4/)
- [KBV FHIR Profile](https://simplifier.net/packages/kbv.ita.erp)