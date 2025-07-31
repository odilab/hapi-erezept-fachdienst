# Implementierungsleitfaden: FHIR Create-Operation für E-Rezept

## Übersicht

Dieser Leitfaden beschreibt die schrittweise Implementierung der FHIR Create-Operation als Custom Operation im HAPI JPA FHIR Server für das E-Rezept-System. Die Operation ermöglicht das Erstellen neuer E-Rezept-Tasks mit spezifischen Workflow-Typen.

## Grundlegende Informationen

### Operation Details
- **Name**: Create
- **Code**: `create`
- **URL**: `https://gematik.de/fhir/erp/OperationDefinition/CreateOperationDefinition`
- **Resource**: Task
- **Type**: true (Operation auf Typ-Ebene)
- **Instance**: false (keine Operation auf Instanz-Ebene)
- **System**: false

### Input Parameter
- **workflowType** (required)
  - Type: Coding
  - Binding: `https://gematik.de/fhir/erp/ValueSet/GEM_ERP_VS_FlowType`
  - Beschreibung: Definiert den Typ des E-Rezept-Workflows

### Output Parameter
- **return** (required)
  - Type: Task
  - Beschreibung: Der erstellte Task mit einer flowType-spezifischen prescriptionID

## Hauptaufgaben und Subtasks

### 1. Projekt-Setup und Abhängigkeiten

#### 1.1 HAPI FHIR Server Konfiguration überprüfen
- [ ] Aktuelle HAPI FHIR Version identifizieren
- [ ] Vorhandene Custom Operations im Projekt lokalisieren
- [ ] Server-Konfigurationsdateien überprüfen

#### 1.2 CustomValidator Integration verstehen
- [ ] CustomValidator-Klasse lokalisieren
- [ ] Validierungslogik analysieren
- [ ] Integration in HAPI Server nachvollziehen

### 2. Operation Provider erstellen

#### 2.1 Provider-Klasse anlegen
- [ ] Package-Struktur für Custom Operations identifizieren
- [ ] Neue Klasse `CreateOperationProvider` erstellen
- [ ] HAPI Provider-Annotationen hinzufügen (@Component, @Provider)

#### 2.2 Operation-Methode implementieren
- [ ] Methode mit @Operation-Annotation versehen
- [ ] Parameter korrekt definieren:
  ```java
  @Operation(name = "$create", type = Task.class)
  ```
- [ ] Input-Parameter mit @OperationParam annotieren

### 3. Input-Validierung implementieren

#### 3.1 WorkflowType-Parameter validieren
- [ ] Parameter-Extraktion aus Parameters-Resource
- [ ] Null-Check implementieren
- [ ] Coding-System validieren gegen `https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType`

#### 3.2 ValueSet-Validierung
- [ ] ValueSet `GEM_ERP_VS_FlowType` laden
- [ ] Coding gegen ValueSet validieren
- [ ] Fehlermeldungen bei ungültigen Werten

#### 3.3 CustomValidator einbinden
- [ ] CustomValidator in Operation Provider injizieren
- [ ] Validierungs-Methode aufrufen
- [ ] Validierungsergebnisse verarbeiten

### 4. Task-Erstellung implementieren

#### 4.1 Prescription ID generieren
- [ ] ID-Generierungslogik gemäß E-Rezept-Spezifikation implementieren
- [ ] Format: `XXX.XXX.XXX.XXX.XXX.XX` (160.000.000.000.000.01)
- [ ] Eindeutigkeit sicherstellen

#### 4.2 Task-Resource erstellen
- [ ] Neue Task-Instanz anlegen
- [ ] Meta-Profil setzen: `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Task|1.5`
- [ ] Extensions hinzufügen:
  - [ ] PrescriptionType Extension
  - [ ] ExpiryDate Extension
  - [ ] AcceptDate Extension

#### 4.3 Task-Status und Workflow setzen
- [ ] Initial-Status auf "draft" setzen
- [ ] WorkflowType aus Input übernehmen
- [ ] Intent auf "order" setzen
- [ ] AuthoredOn mit aktuellem Zeitstempel

### 5. Persistierung und Response

#### 5.1 Task in Datenbank speichern
- [ ] HAPI JPA Repository verwenden
- [ ] Transaktionsmanagement implementieren
- [ ] Fehlerbehandlung bei DB-Operationen

#### 5.2 Response-Parameters erstellen
- [ ] Parameters-Resource für Output erstellen
- [ ] Task als "return"-Parameter hinzufügen
- [ ] HTTP Status 201 (Created) setzen

### 6. Fehlerbehandlung

#### 6.1 OperationOutcome implementieren
- [ ] Standardisierte Fehlermeldungen definieren
- [ ] OperationOutcome für verschiedene Fehlerszenarien:
  - [ ] Ungültiger workflowType
  - [ ] Fehlende Parameter
  - [ ] Validierungsfehler
  - [ ] Datenbankfehler

#### 6.2 HTTP-Statuscodes
- [ ] 400 Bad Request für Validierungsfehler
- [ ] 401 Unauthorized für Authentifizierungsfehler
- [ ] 500 Internal Server Error für Serverfehler

### 7. Sicherheit und Authentifizierung

#### 7.1 Access Token Validierung
- [ ] JWT Token aus Authorization Header extrahieren
- [ ] Token-Signatur prüfen
- [ ] Claims validieren (professionOID, idNummer, etc.)
- [ ] Authentifizierungsstärke (acr) überprüfen

#### 7.2 Berechtigungsprüfung
- [ ] Nutzerrolle aus professionOID ermitteln
- [ ] Berechtigung für Task-Erstellung prüfen
- [ ] Nur autorisierte LEI zulassen

### 8. Logging und Audit

#### 8.1 AuditEvent erstellen
- [ ] AuditEvent für jede Operation
- [ ] Nutzerinformationen aus Token extrahieren
- [ ] Zeitstempel und Details loggen

#### 8.2 Strukturiertes Logging
- [ ] SLF4J Logger einrichten
- [ ] Request/Response logging
- [ ] Performance-Metriken

### 9. Tests schreiben

#### 9.1 Unit Tests
- [ ] Provider-Methoden testen
- [ ] Validierungslogik testen
- [ ] ID-Generierung testen
- [ ] Fehlerszenarien testen

#### 9.2 Integrationstests
- [ ] End-to-End Test der Operation
- [ ] Verschiedene workflowTypes testen
- [ ] Fehlerhafte Requests testen
- [ ] Datenbank-Integration testen

### 10. Dokumentation

#### 10.1 Code-Dokumentation
- [ ] JavaDoc für alle öffentlichen Methoden
- [ ] Inline-Kommentare für komplexe Logik
- [ ] README für das Operation-Package

#### 10.2 API-Dokumentation
- [ ] OpenAPI/Swagger Annotation hinzufügen
- [ ] Beispiel-Requests dokumentieren
- [ ] Fehler-Responses dokumentieren

## Implementierungs-Checkliste

### Phase 1: Analyse (1-2 Tage)
- [ ] Bestehende Codebase verstehen
- [ ] CustomValidator analysieren
- [ ] HAPI Server-Konfiguration prüfen

### Phase 2: Basis-Implementierung (2-3 Tage)
- [ ] Provider-Klasse erstellen
- [ ] Grundlegende Operation implementieren
- [ ] Einfache Validierung

### Phase 3: Vollständige Implementierung (3-4 Tage)
- [ ] Vollständige Validierung
- [ ] Task-Erstellung
- [ ] Persistierung
- [ ] Fehlerbehandlung

### Phase 4: Sicherheit & Audit (2 Tage)
- [ ] Authentifizierung implementieren
- [ ] Autorisierung prüfen
- [ ] Audit-Logging

### Phase 5: Testing & Dokumentation (2-3 Tage)
- [ ] Unit Tests schreiben
- [ ] Integrationstests
- [ ] Dokumentation vervollständigen

## Wichtige Hinweise

1. **Prescription ID Format**: Die ID muss dem Format `XXX.XXX.XXX.XXX.XXX.XX` entsprechen, wobei der erste Block den Workflow-Typ angibt (z.B. 160 für Muster 16).

2. **Workflow Types**: Unterstützte Workflow-Typen gemäß `GEM_ERP_CS_FlowType`:
   - 160: Muster 16 (Standard-Rezept)
   - 169: Muster 16 (Direktzuweisung)
   - 200: PKV-Rezept
   - 209: PKV-Direktzuweisung

3. **Validierung**: Immer den CustomValidator verwenden, um sicherzustellen, dass alle E-Rezept-spezifischen Regeln eingehalten werden.

4. **Transaktionalität**: Alle Datenbankoperationen müssen transaktional sein, um Konsistenz zu gewährleisten.

5. **Performance**: Bei der ID-Generierung auf Thread-Sicherheit und Performance achten.

## Nächste Schritte

1. Mit der Analyse der bestehenden Codebase beginnen
2. CustomValidator-Integration verstehen
3. Einfachen Prototyp der Operation erstellen
4. Schrittweise alle Features implementieren
5. Gründlich testen und dokumentieren

Dieser Leitfaden bietet eine strukturierte Herangehensweise für Junior-Entwickler, um die Create-Operation erfolgreich zu implementieren. Bei Fragen oder Unklarheiten sollte Rücksprache mit erfahrenen Teammitgliedern gehalten werden.