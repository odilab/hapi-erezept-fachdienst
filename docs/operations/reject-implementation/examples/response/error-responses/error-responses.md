# Error Response Examples für $reject Operation

## 1. Task nicht gefunden (404)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "not-found",
      "details": {
        "text": "Task not found for prescription id"
      },
      "diagnostics": "Resource Task/160.000.000.999.999.01 is not known"
    }
  ]
}
```

## 2. Task bereits gelöscht/cancelled (410)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "deleted",
      "details": {
        "text": "Task has already been deleted"
      },
      "diagnostics": "Task with ID 160.000.000.004.714.01 has status cancelled"
    }
  ]
}
```

## 3. Task nicht im Status in-progress (403)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "forbidden",
      "details": {
        "text": "Task not in status in progress, is: ready"
      },
      "diagnostics": "Task must be in status in-progress for reject operation"
    }
  ]
}
```

## 4. Falsches oder fehlendes Secret (403)

```json
{
  "resourceType": "OperationOutcome",
  "meta": {
    "tag": [
      {
        "system": "https://gematik.de/fhir/erp/CodeSystem/VauErrorCode",
        "code": "brute_force"
      }
    ]
  },
  "issue": [
    {
      "severity": "error",
      "code": "forbidden",
      "details": {
        "text": "No or invalid secret"
      },
      "diagnostics": "The provided secret does not match the task secret"
    }
  ]
}
```

Hinweis: Bei Secret-Fehlern wird zusätzlich der VAU-Error-Code "brute_force" gesetzt.

## 5. Unberechtigte Profession (403)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "forbidden",
      "details": {
        "text": "Die Operation $reject ist nur für Apotheken erlaubt"
      },
      "diagnostics": "ProfessionOID oid_arzt is not authorized for reject operation"
    }
  ]
}
```

## 6. Fehlendes Secret Parameter (400)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "required",
      "details": {
        "text": "Parameter 'secret' ist erforderlich"
      },
      "diagnostics": "Operation $reject requires mandatory parameter 'secret'"
    }
  ]
}
```

## 7. Fehlende Telematik-ID im Token (403)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "forbidden",
      "details": {
        "text": "Missing Telematik-ID in ACCESS_TOKEN"
      },
      "diagnostics": "The access token does not contain a valid Telematik-ID"
    }
  ]
}
```

## 8. Interner Serverfehler (500)

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "exception",
      "details": {
        "text": "Interner Serverfehler"
      },
      "diagnostics": "An unexpected error occurred while processing the reject operation"
    }
  ]
}
```

## HTTP Status Codes Übersicht

| Status Code | Bedeutung | Szenario |
|------------|-----------|----------|
| 204 | No Content | Erfolgreich - kein Response Body |
| 400 | Bad Request | Fehlende/ungültige Parameter |
| 403 | Forbidden | Keine Berechtigung, falsches Secret, falscher Status |
| 404 | Not Found | Task existiert nicht |
| 410 | Gone | Task wurde gelöscht (cancelled) |
| 500 | Internal Server Error | Unerwarteter Fehler |

## VAU-Error-Codes

Bei sicherheitsrelevanten Fehlern werden spezielle VAU-Error-Codes gesetzt:

| Code | Bedeutung | Verwendung |
|------|-----------|------------|
| brute_force | Brute-Force-Angriff vermutet | Bei falschem Secret |

Diese werden im Meta-Tag der OperationOutcome gesetzt.