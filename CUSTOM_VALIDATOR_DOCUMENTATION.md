# CustomValidator Dokumentation

## Überblick

Der `CustomValidator` ist ein HAPI FHIR Interceptor, der die gematik Reference Validator Library nutzt, um FHIR-Ressourcen gegen die offiziellen gematik-Profile zu validieren. Er wird automatisch bei der Erstellung und Aktualisierung von Ressourcen im HAPI FHIR Server ausgeführt.

## Technische Details

### Verwendete Bibliotheken

- **gematik Reference Validator**: Offizielle Validierungsbibliothek der gematik für E-Rezept und andere Telematikinfrastruktur-Profile
- **HAPI FHIR**: Framework für FHIR-Server-Implementierung
- **Spring Boot**: Für Dependency Injection und Komponentenverwaltung

### Hauptkomponenten

#### 1. Initialisierung

```java
@Component
@Interceptor
public class CustomValidator {
    private final Map<SupportedValidationModule, ValidationModule> validationModules;
```

Der Validator wird als Spring Component registriert und lädt beim Start alle verfügbaren Validierungsmodule:

- **ERP**: E-Rezept Validierung
- **EAU**: Elektronische Arbeitsunfähigkeitsbescheinigung
- Weitere Module je nach Verfügbarkeit

#### 2. Hook-Points

Der Validator nutzt zwei HAPI FHIR Pointcuts:

- `STORAGE_PRECOMMIT_RESOURCE_CREATED`: Validierung vor dem Speichern neuer Ressourcen
- `STORAGE_PRECOMMIT_RESOURCE_UPDATED`: Validierung vor dem Update bestehender Ressourcen

#### 3. Validierungsprozess

1. **Ressource zu XML konvertieren**: Die FHIR-Ressource wird in XML-Format serialisiert
2. **Modul-Auswahl**: Basierend auf dem Ressourcentyp wird das passende Validierungsmodul gewählt
3. **Validierung**: Die gematik-Library validiert gegen die offiziellen Profile
4. **Fehlerbehandlung**: 
   - ERROR/FATAL: Führen zu `UnprocessableEntityException` → Ressource wird nicht gespeichert
   - WARNING: Werden geloggt, blockieren aber nicht

### Modul-Zuordnung

Die Methode `determineValidationModule()` ordnet Ressourcen dem passenden Validierungsmodul zu:

| Ressourcentyp | Bedingung | Zugeordnetes Modul |
|---------------|-----------|-------------------|
| Bundle | Composition mit Code "e16A" | ERP |
| Bundle | Composition mit Code beginnend mit "AU" | EAU |
| Bundle | Identifier mit "gematik.de/fhir/erp" | ERP |
| Task | - | ERP |
| MedicationRequest | - | ERP |
| MedicationDispense | - | ERP |
| Andere | - | ERP (Default) |

## Integration im Projekt

### Automatische Aktivierung

Der Validator wird durch Spring automatisch initialisiert und registriert:

```java
@PostConstruct
public void init() {
    ValidationModuleFactory factory = new ValidationModuleFactory();
    // Lädt alle verfügbaren Module
}
```

### Fehlerbehandlung

Bei Validierungsfehlern:

1. **Strukturierte Fehlermeldungen**: Fehler werden nach Severity gruppiert
2. **Detaillierte Logs**: Alle Validierungsmeldungen werden geloggt
3. **HTTP 422**: Ungültige Ressourcen führen zu "Unprocessable Entity"

### Beispiel-Fehlermeldung

```
Validierung fehlgeschlagen:
[ERROR] Bundle.entry[0].resource.ofType(Composition): Pflichtfeld 'author' fehlt
[ERROR] Bundle.identifier: System muss 'https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId' sein
```

## Verwendung

### Automatische Validierung

Jede Ressource wird automatisch validiert bei:

- POST-Requests (neue Ressourcen)
- PUT/PATCH-Requests (Updates)

### Manuelle Tests

Die `CustomValidatorTest` Klasse zeigt verschiedene Testszenarien:

1. **Minimale Ressourcen**: Testen der Basisvalidierung
2. **Vollständige E-Rezepte**: Validierung kompletter Bundle-Dokumente
3. **Dateibasierte Tests**: Validierung von Beispieldateien aus `src/test/resources/e-rezept-bundles/`

### Test-Struktur

```java
@Test
void testValidateEPrescriptionBundle() {
    Bundle bundle = createMinimalEPrescriptionBundle();
    // Erwartet Fehler wegen unvollständigem Bundle
    assertThrows(UnprocessableEntityException.class, 
        () -> validator.validateResourceOnCreate(bundle));
}
```

## Best Practices

### 1. Profile angeben

Ressourcen sollten immer die korrekten Profile in den Meta-Daten haben:

```java
resource.getMeta().addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task|1.2");
```

### 2. Vollständige Bundles

E-Rezept Bundles müssen alle Pflichtfelder enthalten:
- Identifier mit korrektem System
- Composition mit Typ "e16A"
- Alle referenzierten Ressourcen

### 3. Fehlerbehandlung

Clients sollten auf HTTP 422 Responses vorbereitet sein und die Fehlermeldungen parsen.

## Logging

Der Validator nutzt SLF4J für strukturiertes Logging:

- **INFO**: Initialisierung, geladene Module
- **DEBUG**: Validierungsdetails, erkannte Ressourcentypen
- **WARN**: Validierungswarnungen, nicht geladene Module
- **ERROR**: Validierungsfehler, kritische Probleme

## Erweiterungsmöglichkeiten

1. **Neue Module**: Weitere gematik-Module können durch Update der Library hinzugefügt werden
2. **Konfiguration**: Severity-Level könnten konfigurierbar gemacht werden
3. **Performance**: Caching von Validierungsergebnissen für identische Ressourcen

## Bekannte Einschränkungen

1. **Strikte Validierung**: Die gematik-Validator sind sehr strikt und erfordern vollständige, profilkonforme Ressourcen
2. **XML-basiert**: Validierung erfolgt immer über XML, auch wenn JSON eingegeben wird
3. **Performance**: Validierung komplexer Bundles kann zeitintensiv sein

## Troubleshooting

### Häufige Fehler

1. **"Keine Validierungsmodule konnten geladen werden"**
   - Prüfen Sie die gematik-Library Abhängigkeiten
   - Stellen Sie sicher, dass die Profil-Pakete verfügbar sind

2. **"Validierungsmodul X nicht verfügbar"**
   - Das angeforderte Modul ist nicht installiert
   - Prüfen Sie die pom.xml für die korrekten Dependencies

3. **Unerwartete Validierungsfehler**
   - Prüfen Sie die Profile-Versionen
   - Vergleichen Sie mit offiziellen gematik-Beispielen

## Referenzen

- [gematik FHIR-Profile](https://simplifier.net/packages/de.gematik.erezept-workflow.r4)
- [HAPI FHIR Interceptors](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html)
- [gematik Reference Validator](https://github.com/gematik/app-referencevalidator)