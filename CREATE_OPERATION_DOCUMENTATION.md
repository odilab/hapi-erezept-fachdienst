# E-Rezept Create Operation - Implementierungsdokumentation

## Übersicht

Dieses Dokument beschreibt die Implementierung der `$create` Operation für den E-Rezept Fachdienst gemäß den Spezifikationen aus:
- `gemSpec_FD_eRp_V2.3.0.xml` - Technische Spezifikation der gematik
- `erp_bereitstellen.adoc` - E-Rezept Bereitstellungsdokumentation

## 1. Anforderungen gemäß Spezifikation

### 1.1 Operation Definition (gemSpec_FD_eRp_V2.3.0.xml)

Die Create-Operation ist in der Spezifikation unter `A_21267-01` definiert:

```xml
<AFO id="A_21267-01">
  <Titel>E-Rezept-Fachdienst - Task anlegen</Titel>
  <Text>Der E-Rezept-Fachdienst MUSS die Operation $create bereitstellen. 
        Diese legt ein neues E-Rezept (Task) im Status "draft" an.</Text>
</AFO>
```

**Wichtige Parameter:**
- `workflowType` (required): Coding mit dem Workflow-Typ aus `GEM_ERP_CS_FlowType`
- Rückgabe: Task-Ressource im Status "draft"

### 1.2 Workflow-Typen (erp_bereitstellen.adoc)

Unterstützte Workflow-Typen gemäß Dokumentation:
- **160**: Muster 16 (Apothekenpflichtige Arzneimittel)
- **169**: Muster 16 (Direktzuweisung)
- **200**: PKV (Private Krankenversicherung)
- **209**: PKV (Direktzuweisung)
- **210**: TVP (Tilgungsvertrag Psychotherapie)

### 1.3 Autorisierung

Berechtigte Berufsgruppen (OIDs):
- `1.2.276.0.76.4.30`: Ärzte
- `1.2.276.0.76.4.31`: Zahnärzte  
- `1.2.276.0.76.4.32`: Apotheker (nur für bestimmte FlowTypes)
- `1.2.276.0.76.4.33`: Apotheker-PKV
- `1.2.276.0.76.4.46`: Psychotherapeuten (nur FlowType 210)
- `1.2.276.0.76.4.53`: Krankenhausapotheke
- `1.2.276.0.76.4.75`: ASV-Institutionen

## 2. Implementierung

### 2.1 Verzeichnisstruktur

```
src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/create/
├── CreateOperationProvider.java    # FHIR Operation Provider
└── CreateTaskService.java          # Business Logic Service
```

### 2.2 CreateOperationProvider.java

Der Provider implementiert die FHIR-Operation und ist für Request-Handling zuständig:

```java
@Component
public class CreateOperationProvider {
    
    @Operation(name = "$create", idempotent = false, type = Task.class)
    public Task createTaskOperation(
            @OperationParam(name = "workflowType", min = 1) Coding workflowType,
            RequestDetails theRequestDetails) {
        
        // 1. Token-Validierung
        AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
        
        // 2. Workflow-Type Validierung
        if (!isValidWorkflowType(workflowType)) {
            throw new InvalidRequestException("Ungültiger Workflow-Type");
        }
        
        // 3. Autorisierung prüfen
        if (!isAuthorized(accessToken, workflowType)) {
            throw new ForbiddenOperationException("Nicht autorisiert");
        }
        
        // 4. Task erstellen
        Task task = createTaskService.createTask(workflowType, accessToken);
        
        // 5. Audit-Event
        auditService.createRestAuditEvent(...);
        
        return task;
    }
}
```

### 2.3 CreateTaskService.java

Der Service implementiert die Geschäftslogik für die Task-Erstellung:

```java
@Service
public class CreateTaskService {
    
    public Task createTask(Coding workflowType, AccessToken accessToken) {
        Task task = new Task();
        
        // Status gemäß A_21267-01
        task.setStatus(Task.TaskStatus.DRAFT);
        task.setIntent(Task.TaskIntent.ORDER);
        
        // Prescription ID generieren (Format: [FlowType].XXX.XXX.XXX.XXX.XX.XX)
        String prescriptionId = generatePrescriptionId(workflowType.getCode());
        task.addIdentifier()
            .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId")
            .setValue(prescriptionId);
        
        // Access Code generieren (256 Bit)
        String accessCode = generateAccessCode();
        task.addIdentifier()
            .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode")
            .setValue(accessCode);
        
        // FlowType Extension
        task.addExtension()
            .setUrl("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType")
            .setValue(workflowType);
        
        // Weitere Felder setzen...
        
        // Task in Datenbank speichern
        return taskDao.create(task);
    }
}
```

### 2.4 Prescription ID Generation

Die Prescription ID folgt dem Format aus der Spezifikation:

```java
private String generatePrescriptionId(String flowType) {
    Random random = new SecureRandom();
    StringBuilder sb = new StringBuilder(flowType).append(".");
    
    // 3 Blöcke mit je 3 Ziffern
    for (int block = 0; block < 3; block++) {
        for (int i = 0; i < 3; i++) {
            sb.append(random.nextInt(10));
        }
        sb.append(".");
    }
    
    // 3 Blöcke mit je 2-3 Ziffern
    for (int block = 0; block < 3; block++) {
        int digits = (block < 2) ? 3 : 2;
        for (int i = 0; i < digits; i++) {
            sb.append(random.nextInt(10));
        }
        if (block < 2) sb.append(".");
    }
    
    return sb.toString();
}
```

### 2.5 Access Code Generation

Der Access Code ist ein 256-Bit Zufallswert, hex-codiert:

```java
private String generateAccessCode() {
    SecureRandom random = new SecureRandom();
    byte[] accessCodeBytes = new byte[32]; // 256 Bit = 32 Byte
    random.nextBytes(accessCodeBytes);
    return DatatypeConverter.printHexBinary(accessCodeBytes).toLowerCase();
}
```

## 3. Autorisierungs-Matrix

Die Autorisierung erfolgt basierend auf der Kombination von Profession OID und FlowType:

| FlowType | Beschreibung | Berechtigte Professionen |
|----------|--------------|-------------------------|
| 160 | Muster 16 | Ärzte, Zahnärzte, Krankenhausapotheke, ASV |
| 169 | Muster 16 (Direktzuweisung) | Ärzte, Zahnärzte, Krankenhausapotheke |
| 200 | PKV | Ärzte, Zahnärzte, Krankenhausapotheke |
| 209 | PKV (Direktzuweisung) | Apotheker-PKV |
| 210 | TVP | Psychotherapeuten |

## 4. Task-Struktur

Ein erstellter Task enthält folgende Elemente:

```json
{
  "resourceType": "Task",
  "id": "160.123.456.789.123.45.67",
  "meta": {
    "profile": ["https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task"]
  },
  "identifier": [
    {
      "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId",
      "value": "160.123.456.789.123.45.67"
    },
    {
      "system": "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode",
      "value": "a1b2c3d4e5f6789012345678901234567890123456789012345678901234abcd"
    }
  ],
  "status": "draft",
  "intent": "order",
  "extension": [
    {
      "url": "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType",
      "valueCoding": {
        "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
        "code": "160",
        "display": "Muster 16 (Apothekenpflichtige Arzneimittel)"
      }
    }
  ],
  "authoredOn": "2025-07-31T13:00:00+00:00",
  "performerType": [
    {
      "coding": [{
        "system": "urn:ietf:rfc:3986",
        "code": "urn:oid:1.2.276.0.76.4.54",
        "display": "Öffentliche Apotheke"
      }]
    }
  ]
}
```

## 5. Fehlerbehandlung

Die Operation wirft spezifische Exceptions für verschiedene Fehlerfälle:

| Fehlerfall | HTTP Status | Exception |
|------------|-------------|-----------|
| Fehlende Authentifizierung | 401 | AuthenticationException |
| Keine Berechtigung | 403 | ForbiddenOperationException |
| Ungültiger WorkflowType | 400 | InvalidRequestException |
| Fehlende Parameter | 400 | InvalidRequestException |
| Interner Fehler | 500 | InternalErrorException |

## 6. Testing

### 6.1 Test-Strategie

- **Keine Mocks**: Alle Tests laufen gegen echte Services
- **Testcontainer**: Verwendung von Docker-Containern für IDP und ERP-Service
- **Integration Tests**: Vollständige End-to-End Tests der Operation

### 6.2 Test-Klassen

1. **CreateOperationIntegrationTest.java**
   - 6 Tests für verschiedene Szenarien
   - Testet alle FlowTypes
   - Prüft Fehlerbehandlung
   - Validiert eindeutige IDs

2. **TestcontainerAccessTokenTest.java**
   - Testet Testcontainer-Infrastruktur
   - Prüft Token-Abruf
   - Validiert Container-Konnektivität

### 6.3 Test-Abdeckung

- ✅ Task-Erstellung mit allen FlowTypes (160, 169, 200, 209, 210)
- ✅ Authentifizierung und Autorisierung
- ✅ Fehlerbehandlung (401, 403, 400)
- ✅ Eindeutigkeit von Prescription IDs und Access Codes
- ✅ Korrekte Task-Status und Metadaten

## 7. Konfiguration

Die Operation wird über Spring-Properties konfiguriert:

```properties
# Custom Provider registrieren
hapi.fhir.custom-provider-classes=ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider

# Interceptoren für Authentifizierung
hapi.fhir.custom-interceptor-classes=ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AuthenticationInterceptor
```

## 8. Deployment

Die Create-Operation ist Teil des HAPI FHIR Servers und wird automatisch über die Provider-Registrierung verfügbar gemacht. Der Endpunkt ist:

```
POST /fhir/Task/$create
```

## 9. Sicherheitsaspekte

1. **Access Token Validierung**: Jeder Request wird authentifiziert
2. **Autorisierungsprüfung**: Nur berechtigte Professionen können Tasks erstellen
3. **Sichere ID-Generation**: Verwendung von SecureRandom
4. **Audit-Logging**: Alle Operationen werden geloggt
5. **HTTPS-Only**: Kommunikation nur über verschlüsselte Verbindungen

## 10. Referenzen

- [gemSpec_FD_eRp_V2.3.0.xml](../src/main/resources/E-Rezept%20docs/gemSpec_FD_eRp_V2.3.0.xml)
- [erp_bereitstellen.adoc](../src/main/resources/E-Rezept%20docs/erp_bereitstellen.adoc)
- [FHIR Task Resource](http://hl7.org/fhir/R4/task.html)
- [E-Rezept Implementierungsleitfaden](https://simplifier.net/erezept)

---

*Erstellt am: 31.07.2025*  
*Version: 1.0*  
*Autor: Development Team*