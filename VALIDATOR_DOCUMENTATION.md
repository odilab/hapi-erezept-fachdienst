# HAPI FHIR eRezept Validator - Dokumentation

## Übersicht

Diese Dokumentation beschreibt die Implementierung und Konfiguration des Custom Validators für den HAPI FHIR eRezept Fachdienst. Der Validator wurde erweitert, um verschiedene eRezept-Profile zu validieren und dabei mit Versionsunterschieden und XML-Besonderheiten umzugehen.

## Implementierte Lösungen

### 1. Flexible Versionsbehandlung

#### Problem
- Bundles referenzieren Profile mit Versionen wie "1.4"
- NPM-Pakete enthalten dieselben Profile mit Versionen wie "1.4.0"
- Standard HAPI FHIR Validator erfordert exakte Versionsübereinstimmung

#### Lösung
Implementierung eines flexiblen `IValidationSupport` in `CustomValidator.java`:

```java
IValidationSupport flexibleVersionSupport = new IValidationSupport() {
    @Override
    public IBaseResource fetchStructureDefinition(String url) {
        // Wenn URL Version enthält (z.B. "http://example.com/Profile|1.4")
        // Extrahiere URL und Version
        String baseUrl = url.substring(0, url.indexOf("|"));
        String requestedVersion = url.substring(url.indexOf("|") + 1);
        
        // Suche nach kompatiblen Versionen
        // ...
    }
    
    private boolean isVersionCompatible(String requested, String available) {
        // Implementiert Semantic Versioning Kompatibilität:
        // - "1.4" akzeptiert "1.4.0", "1.4.1", etc.
        // - "1.4.0" akzeptiert auch "1.4"
        // - Major und Minor Version müssen übereinstimmen
        // - Patch-Version wird flexibel behandelt
    }
};
```

#### Ergebnis
- Profile mit verschiedenen Patch-Versionen werden korrekt aufgelöst
- Kompatibel mit Semantic Versioning Prinzipien
- Löst Versionskonflikte zwischen Bundles und NPM-Paketen

### 2. XML-Kommentar Handling

#### Problem
- EVDGA Bundles enthalten XML-Kommentare zwischen `<Bundle>` und `<id>` Element
- HAPI FHIR Validator wirft Fehler: "Bundle.id: Objekt muss einen Inhalt haben"

#### Beispiel des Problems
```xml
<Bundle xmlns="http://hl7.org/fhir">
  <!-- Beispiel-Bundle eDiGA-Verordnung -->
  <id value="evdga-bundle-gesetzliche-krankenversicherung" />
```

#### Lösung
Preprocessing von Bundle-Ressourcen vor der Validierung:

```java
public void validateAndThrowIfInvalid(IBaseResource resource) {
    // Bei Bundle-Ressourcen: XML neu parsen wenn Kommentare problematisch sein könnten
    if (resource instanceof Bundle) {
        resource = preprocessBundleForValidation(resource);
    }
    
    ValidationResult validationResult = validator.validateWithResult(resource);
    // ...
}

private String removeXmlCommentsBeforeId(String xml) {
    // Entfernt Kommentare zwischen XML-Tags
    String pattern = "(<Bundle[^>]*>)(\\s*<!--[^>]*-->\\s*)(<id[^>]*/>)";
    String cleaned = xml.replaceAll(pattern, "$1$3");
    
    // Generelle Bereinigung
    cleaned = cleaned.replaceAll(">(\\s*<!--[^>]*-->\\s*)<", "><");
    
    return cleaned;
}
```

#### Ergebnis
- EVDGA Bundles werden erfolgreich validiert
- Kommentare werden transparent entfernt ohne die Bundle-Struktur zu verändern
- Keine Auswirkung auf JSON-Bundles

### 3. NPM-Paket Integration

#### Geladene NPM-Pakete

Der Validator lädt folgende NPM-Pakete aus dem Classpath:

**Basis-Pakete:**
- `de.basisprofil.r4-1.5.3.tgz` - Deutsche Basisprofile
- `de.ihe-d.terminology-3.0.1.tgz` - IHE Deutschland Terminologie
- `dvmd.kdl.r4-2024.0.0.tgz` - DVMD Kataloge

**KBV-Pakete:**
- `kbv.basis-1.7.0.tgz` - KBV Basisprofile
- `kbv.ita.for-1.2.0.tgz` - KBV FOR (Formular) Profile
- `kbv.ita.erp-1.4.0-alpha.tgz` - KBV eRezept Profile
- `kbv.itv.evdga-1.2.1.tgz` - KBV EVDGA Profile

**ABDA-Pakete:**
- `de.abda.erezeptabgabedaten-1.4.1-rc.tgz` - ABDA Abgabedaten (RC)
- `de.abda.erezeptabgabedaten-1.5.0.tgz` - ABDA Abgabedaten

**Gematik-Pakete:**
- `de.gematik.erezept-workflow.r4-1.5.2.tgz` - Gematik eRezept Workflow

## Validierte Bundle-Typen

### 1. eRezept Bundles
- **Pfad**: `/package/erezept/`
- **Beispiele**: `Beispiel_1.xml`, `Beispiel_61_PZN_BtM.xml`
- **Status**: ✅ Validierung erfolgreich

### 2. EVDGA Bundles
- **Pfad**: `/package/evdga/`
- **Beispiele**: `EVDGA_Bundle.xml`, `EVDGA_Bundle_BG_Arbeitsunfall.xml`
- **Status**: ✅ Validierung erfolgreich (nach XML-Kommentar Fix)

### 3. ABDA Abgabedaten Bundles
- **Pfad**: `/package/erezeptabgabedaten/`
- **Status**: ❌ Fehlende Base-Profile

## Bekannte Probleme

### 1. Fehlende ABDA Base-Profile

Folgende Base-Profile werden referenziert, sind aber nicht in den NPM-Paketen enthalten:

- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-AbgabedatenBundle`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-AbgabedatenComposition`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-Abgabeinformationen`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-Abrechnungszeilen`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-Apotheke`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-ZusatzdatenEinheit`
- `http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-ZusatzdatenHerstellung`

**Lösung**: Diese Base-Profile müssen separat besorgt und als NPM-Paket hinzugefügt werden.

### 2. FHIRPath Ausdrucksfehler

Einige importierte StructureDefinitions enthalten fehlerhafte FHIRPath-Ausdrücke:
- `observation-de-ekg`: Constraint verwendet `.start` auf dateTime-Typ

**Lösung**: Dies sind Fehler in den importierten Profilen, nicht im Validator selbst.

## Test-Suite

Die `CustomValidatorTest.java` enthält umfassende Tests für:

- Laden aller StructureDefinitions
- Validierung verschiedener Bundle-Typen
- Identifizierung fehlender Profile
- Versionskompatibilität
- XML-Kommentar Handling

### Wichtige Test-Methoden

- `testValidateErezeptBundles()` - Validiert eRezept Bundles
- `testValidateEvdgaBundles()` - Validiert EVDGA Bundles
- `testValidateAbgabedatenBundles()` - Validiert ABDA Bundles
- `testValidateAllBundlesAutomatically()` - Automatische Bundle-Erkennung
- `testIdentifyMissingStructureDefinitions()` - Listet fehlende Profile

## Konfiguration

### application.yaml Einstellungen

```yaml
hapi:
  fhir:
    custom-bean-packages: ca.uhn.fhir.jpa.starter.custom.interceptor
    custom-interceptor-classes: ca.uhn.fhir.jpa.starter.custom.interceptor.CustomValidator
```

### Logging

Für detaillierte Validierungsinformationen:
```yaml
logging:
  level:
    ca.uhn.fhir.jpa.starter.custom.interceptor: DEBUG
```

## Zukünftige Erweiterungen

1. **Dynamisches NPM-Paket Laden**: Möglichkeit, NPM-Pakete zur Laufzeit zu laden
2. **Validierungsregeln Konfiguration**: Externe Konfiguration für Validierungsregeln
3. **Performance-Optimierung**: Caching von häufig verwendeten Profilen
4. **Erweiterte Fehlerbehandlung**: Detailliertere Fehlermeldungen für Endbenutzer

## Wartung

### Hinzufügen neuer NPM-Pakete

1. Paket in `/src/main/resources/package/npm packages/` ablegen
2. In `CustomValidator.java` den Ladevorgang hinzufügen:
   ```java
   npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/neues-paket.tgz");
   ```
3. Tests aktualisieren und ausführen

### Debugging von Validierungsfehlern

1. Logging auf DEBUG setzen
2. `testIdentifyMissingStructureDefinitions()` ausführen
3. Fehlende Profile in den Logs identifizieren
4. Entsprechende NPM-Pakete besorgen und hinzufügen

---

*Letzte Aktualisierung: 22.07.2025*