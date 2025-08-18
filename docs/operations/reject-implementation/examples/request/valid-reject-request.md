# Valid Reject Request Example

## HTTP Request

```http
POST /Task/160.000.000.004.714.01/$reject?secret=777bea0e13cc9c42ceec14aec3ddee2263325dc2c6c699db115f58fe423607ea HTTP/1.1
Host: erp-fachdienst.example.org
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiSldUIn0...
Content-Type: application/fhir+json
Accept: application/fhir+json
```

## Alternative mit Secret im Body (FHIR Parameters)

```http
POST /Task/160.000.000.004.714.01/$reject HTTP/1.1
Host: erp-fachdienst.example.org
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiSldUIn0...
Content-Type: application/fhir+json
Accept: application/fhir+json

{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "secret",
      "valueString": "777bea0e13cc9c42ceec14aec3ddee2263325dc2c6c699db115f58fe423607ea"
    }
  ]
}
```

## Wichtige Header

- **Authorization**: Bearer Token mit JWT der Apotheke
  - Muss professionOID enthalten: `oid_oeffentliche_apotheke` oder `oid_krankenhausapotheke`
  - Muss Telematik-ID enthalten
  
- **X-Request-ID**: Optional, für Request-Tracking

## Query Parameter

- **secret** (required): 256-Bit Hex-String (64 Zeichen)
  - Wurde bei $accept generiert
  - Muss exakt mit Task.identifier:Secret übereinstimmen

## Vorbedingungen

1. Task existiert mit der angegebenen ID
2. Task ist im Status "in-progress"
3. Task hat ein Secret (aus vorheriger $accept Operation)
4. Secret stimmt überein
5. Anfragender hat die richtige Rolle (Apotheke)

## Response

```http
HTTP/1.1 204 No Content
X-Request-ID: 5d2e3790-2b4e-4b70-a3e4-c892c2607b8c
Date: Mon, 15 Aug 2025 10:23:45 GMT
```

Kein Response-Body bei erfolgreicher Operation (HTTP 204).