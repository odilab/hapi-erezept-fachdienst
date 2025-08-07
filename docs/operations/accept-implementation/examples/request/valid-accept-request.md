# Beispiel Accept Requests

## 1. Accept mit Query-Parameter

```http
POST /fhir/Task/160.000.033.491.280.78/$accept?ac=af3e8f9c2b4d6a1e7f5c9b3a8d2e1f4c6b9a3e7d5f2c8a1b4e6d9f3c7a2e5b8d1f
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiYXQrSldUIiwia2lkIjoicHVrX2lkcF9zaWcifQ...
Accept: application/fhir+json
```

## 2. Accept mit Header

```http
POST /fhir/Task/160.000.033.491.280.78/$accept
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiYXQrSldUIiwia2lkIjoicHVrX2lkcF9zaWcifQ...
X-AccessCode: af3e8f9c2b4d6a1e7f5c9b3a8d2e1f4c6b9a3e7d5f2c8a1b4e6d9f3c7a2e5b8d1f
Accept: application/fhir+json
```

## 3. Accept für PKV (Flowtype 200)

```http
POST /fhir/Task/200.000.033.491.280.78/$accept?ac=bf4e9f0c3b5d7a2e8f6c0b4a9d3e2f5c7b0a4e8d6f3c9a2b5e7d0f4c8a3e6b9d2f
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiYXQrSldUIiwia2lkIjoicHVrX2lkcF9zaWcifQ...
Accept: application/fhir+json
```

## 4. Accept für DiGA (Flowtype 162)

```http
POST /fhir/Task/162.000.033.491.280.78/$accept?ac=cf5e0f1c4b6d8a3e9f7c1b5a0d4e3f6c8b1a5e9d7f4c0a3b6e8d1f5c9a4e7b0d3f
Authorization: Bearer eyJhbGciOiJCUDI1NlIxIiwidHlwIjoiYXQrSldUIiwia2lkIjoicHVrX2lkcF9zaWcifQ...
Accept: application/fhir+json
```

Hinweis: Der Authorization Bearer Token muss ein gültiges JWT mit korrekter Profession OID sein:
- Apotheke: `oid_oeffentliche_apotheke` oder `oid_krankenhausapotheke`
- Kostenträger (nur für DiGA): `oid_kostentraeger`