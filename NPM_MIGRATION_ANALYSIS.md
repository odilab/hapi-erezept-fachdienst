# NPM Package Migration - Analyse

## Übersicht

Diese Analyse zeigt alle lokal geladenen FHIR-Ressourcen, die in NPM-Pakete migriert werden sollten.

## 1. Ressourcen die in NPM-Pakete migriert werden müssen

### 1.1 Gematik ERG Ressourcen
**Pfad**: `/gematik-erg-resources(new)/`  
**Anzahl**: 61 Ressourcen  
**Publisher**: gematik GmbH  
**Empfohlener Paketname**: `de.gematik.erg-1.0.0.tgz`

**Inhalt**:
- 18 StructureDefinitions (ERG-spezifische Profile)
- 14 CodeSystems (ERG-spezifische Codes)
- 14 ValueSets (ERG-spezifische Wertelisten)
- 15 OperationDefinitions (ERG-Operationen)

**Priorität**: HOCH - Diese sind zentral für ERG-Funktionalität

### 1.2 GKV-SV Abrechnungsdaten
**Pfad**: `/erezeptabrechnungsdaten/`  
**Anzahl**: 36 Ressourcen  
**Publisher**: GKV-Spitzenverband  
**Empfohlener Paketname**: `de.gkvsv.erezeptabrechnungsdaten-1.0.0.tgz`

**Inhalt**:
- 7 CodeSystems (Import, Leistungserbringer, Positionstyp, etc.)
- 13 Extensions (Import, Verwurf, TA7-spezifisch)
- 4 NamingSystems (Belegnummer, Dateiname, Rechnungsnummer)
- 5 StructureDefinitions (Binary, eAbrechnungsdaten, TA7-Bundle)
- 7 ValueSets

**Priorität**: HOCH - Notwendig für Abrechnungsprozesse

### 1.3 Gematik Charge Resources
**Pfad**: `/Resources/fsh-generated/resources/`  
**Anzahl**: 8 Ressourcen  
**Publisher**: gematik GmbH  
**Empfohlener Paketname**: `de.gematik.erpchrg-1.0.0.tgz`

**Inhalt**:
- 1 CodeSystem (ConsentType)
- 1 Extension (MarkingFlag)
- 5 StructureDefinitions (ChargeItem, Communications, Consent)
- 1 ValueSet (ConsentType)

**Priorität**: MITTEL - Spezifisch für Charge-Funktionalität

### 1.4 ABDA PKV Ressourcen
**Pfad**: `/erezeptabgabedatenpkv/`  
**Anzahl**: 19 Ressourcen  
**Publisher**: DAV  
**Empfohlener Paketname**: Integration in `de.abda.erezeptabgabedaten-pkv-1.5.0.tgz`

**Inhalt**:
- 4 CodeSystems (PKV-spezifisch)
- 2 Extensions (AbrechnungsTyp, Bankverbindung)
- 7 StructureDefinitions (PKV-Profile)
- 5 ValueSets (PKV-spezifisch)

**Priorität**: MITTEL - PKV-spezifische Erweiterung

## 2. Bereits als NPM-Pakete verfügbar (sollten lokal entfernt werden)

### 2.1 KBV eRezept
**Lokaler Pfad**: `/erezept/`  
**NPM-Paket**: `kbv.ita.erp-1.4.0-alpha.tgz` ✅ Bereits vorhanden

### 2.2 KBV EVDGA
**Lokaler Pfad**: `/evdga/`  
**NPM-Paket**: `kbv.itv.evdga-1.2.1.tgz` ✅ Bereits vorhanden

### 2.3 ABDA Abgabedaten
**Lokaler Pfad**: `/erezeptabgabedaten/`  
**NPM-Paket**: `de.abda.erezeptabgabedaten-1.5.0.tgz` ✅ Bereits vorhanden

## 3. Duplizierte Resource-Ordner

Die folgenden Ordner enthalten identische Ressourcen und sollten konsolidiert werden:
- `/Resources 2/` bis `/Resources 9/` - Duplikate von `/Resources/`

## 4. Empfohlene Maßnahmen

### Sofort
1. **NPM-Paket erstellen**: `de.gematik.erg-1.0.0.tgz` aus `/gematik-erg-resources(new)/`
2. **NPM-Paket erstellen**: `de.gkvsv.erezeptabrechnungsdaten-1.0.0.tgz` aus `/erezeptabrechnungsdaten/`
3. **Duplikate entfernen**: Resources 2-9 Ordner löschen

### Mittelfristig
1. **NPM-Paket erstellen**: `de.gematik.erpchrg-1.0.0.tgz` aus `/Resources/`
2. **PKV-Paket erweitern**: ABDA PKV-Ressourcen in separates Paket
3. **Lokale Kopien entfernen**: `/erezept/`, `/evdga/`, `/erezeptabgabedaten/` (nur Profile, nicht examples)

### Code-Anpassungen
```java
// In CustomValidator.java hinzufügen:
npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gematik.erg-1.0.0.tgz");
npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gkvsv.erezeptabrechnungsdaten-1.0.0.tgz");
npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gematik.erpchrg-1.0.0.tgz");

// Entfernen:
// - loadAllPackageResources() Methode
// - loadAllResources() kann bleiben bis ERG-Paket erstellt ist
```

## 5. Vorteile der Migration

1. **Versionierung**: NPM-Pakete ermöglichen klare Versionskontrolle
2. **Wartbarkeit**: Einfachere Updates und Dependency-Management
3. **Performance**: Schnelleres Laden durch optimierte Paketstruktur
4. **Konsistenz**: Einheitliche Handhabung aller Profile
5. **Wiederverwendbarkeit**: Pakete können in anderen Projekten genutzt werden

## 6. NPM-Paket Struktur

Beispielstruktur für `de.gematik.erg`:
```
package/
├── package.json
├── StructureDefinition-*.json
├── CodeSystem-*.json
├── ValueSet-*.json
├── OperationDefinition-*.json
└── examples/
    └── *.json
```

## 7. Geschätzte Aufwände

- ERG NPM-Paket erstellen: 2-3 Stunden
- GKVSV NPM-Paket erstellen: 2-3 Stunden
- Code-Refactoring: 1-2 Stunden
- Testing: 2-3 Stunden
- **Gesamt**: 1-2 Arbeitstage

---

*Erstellt am: 22.07.2025*