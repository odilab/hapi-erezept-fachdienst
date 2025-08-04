# implement-erp-operation

Implementiere eine neue E-Rezept FHIR Operation nach den gematik Spezifikationen unter Berücksichtigung der C++ Referenzimplementierung und der vorhandenen Java-Patterns.

## Verwendung

```
/implement-erp-operation <operation-name>
```

Beispiel: `/implement-erp-operation activate`

Verfügbare Operationen: abort, accept, activate, close, dispense, reject

## Ausführliche Implementierungsanleitung

Wenn dieses Command aufgerufen wird, führe folgende Schritte SEHR GRÜNDLICH aus:

### 1. VOLLSTÄNDIGE ANALYSE DER OPERATIONDEFINITION

**WICHTIG**: Lies und analysiere KOMPLETT die OperationDefinition JSON-Datei:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/resources/OperationDefinition-{OperationName}Operation.json
```

Extrahiere und verstehe ALLE folgenden Details:
- Operation name, code, kind
- Instance vs. System Operation
- Idempotent Flag
- ALLE Input Parameter (name, type, min, max, documentation, profile)
- ALLE Output Parameter (name, type, min, max, documentation, profile)
- inputProfile und outputProfile URLs
- Beschreibung der Operation

**Beispiel für activate**: Die Operation nimmt einen Binary Parameter "ePrescription" mit dem signierten Bundle und gibt einen Task zurück.

### 2. TIEFGEHENDE ANALYSE DER XML-SPEZIFIKATION

**KRITISCH**: Durchsuche und analysiere die komplette Spezifikation:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/gemSpec_FD_eRp_V2.3.0.xml
```

Suche nach ALLEN Erwähnungen der Operation (z.B. "$activate", "activate", "Aktivierung") und extrahiere:
- ALLE Anforderungs-IDs (A_xxxxx, ERPF_xxxxx)
- Geschäftsregeln und Validierungen
- Erlaubte Rollen/Profession OIDs
- Status-Übergänge
- Fehlerszenarien und Fehlercodes
- Audit-Anforderungen
- Sicherheitsanforderungen

### 3. ANALYSE DER ERGÄNZENDEN DOKUMENTATION

Lies und verstehe die adoc-Datei:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/resources/E-Rezept docs/erp_bereitstellen.adoc
```

Suche nach:
- Workflow-Beschreibungen
- Sequenzdiagrammen
- Zusätzlichen Geschäftsregeln
- Beispielszenarien

### 4. DETAILLIERTE C++ REFERENZIMPLEMENTIERUNG ANALYSE

**SEHR WICHTIG**: Analysiere VOLLSTÄNDIG die C++ Implementierung als goldene Referenz:

Header-Datei:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/{OperationName}TaskHandler.hxx
```

Implementation:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/{OperationName}TaskHandler.cxx
```

Extrahiere und verstehe:
- ALLE ErpExpect Validierungen (diese MÜSSEN in Java umgesetzt werden!)
- Geschäftslogik-Ablauf
- Status-Prüfungen und -Übergänge
- Datenbank-Operationen
- Fehlerbehandlung mit spezifischen HTTP-Status-Codes
- Crypto/Signatur-Validierungen falls vorhanden
- AccessCode-Generierung/Validierung
- KVNR/TelematikID Prüfungen

**Beispiel für activate**:
- Prüfung Task Status == draft
- Validierung der Signatur
- Parsen des KBV Bundles
- Update Task zu Status "ready"
- Setzen des authoredOn Datums

### 5. C++ TEST ANALYSE

Analysiere die C++ Tests um das erwartete Verhalten zu verstehen:
```
/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/test/workflow-test/
```

Suche nach Tests für die Operation (z.B. ActivateTaskTest, A_*_Activate.cxx) und verstehe:
- Erfolgsszenarien
- Fehlerszenarien
- Edge Cases
- Testdaten

### 6. ANALYSE VORHANDENER JAVA-IMPLEMENTIERUNGEN

**KRITISCH**: Studiere GENAU die bereits implementierte CreateOperation als Vorlage:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/create/CreateOperationProvider.java
```

Verstehe und übernehme folgende Patterns:
- Dependency Injection mit @Autowired
- @Operation Annotation Konfiguration
- Access Token Extraktion und Validierung
- Profession OID Prüfung
- Service-Aufteilung (Provider vs. Service Klassen)
- Audit-Logging Pattern
- Fehlerbehandlung mit FHIR Exceptions
- Output Parameter Erstellung mit Meta-Profilen

Analysiere auch die verwendeten Services:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/AuthorizationService.java
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/AuditService.java
```

### 7. CUSTOM VALIDATOR INTEGRATION

Verstehe wie der CustomValidator für komplexe Validierungen genutzt wird:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/interceptor/CustomValidator.java
```

### 8. IMPLEMENTATION DER OPERATION

#### 8.1 Provider-Klasse erstellen

Erstelle die Provider-Klasse:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}OperationProvider.java
```

Die Klasse MUSS enthalten:
- @Component Annotation
- Implementierung von IResourceProvider
- ALLE Dependencies aus CreateOperationProvider
- @Operation Annotation mit korrekten Parametern
- Methode mit EXAKT den Parametern aus OperationDefinition
- RequestDetails Parameter für Access Token

**WICHTIGE Implementierungsschritte in der Operation-Methode**:
1. Input-Parameter Validierung (ALLE aus OperationDefinition)
2. Access Token Extraktion mit AuthorizationService
3. Profession OID Validierung (aus Spezifikation)
4. Task aus DB laden (bei Instance Operations)
5. Status-Prüfungen (aus C++ Implementierung)
6. Geschäftslogik (EXAKT wie in C++ Implementierung)
7. Task/Resource Updates
8. Output Parameter erstellen (mit korrektem Meta-Profil)
9. Audit-Logging (gemäß Spezifikation)

#### 8.2 Service-Klasse (falls komplex)

Bei komplexer Geschäftslogik erstelle:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}TaskService.java
```

### 9. UMFASSENDE TESTS IMPLEMENTIEREN

Erstelle Integrationstests basierend auf BaseProviderTest:
```
/Users/rene/Desktop/Arbeit/hapi-erezept-fachdienst/src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/{operation}/{OperationName}OperationIntegrationTest.java
```

Tests MÜSSEN abdecken:
- ALLE Erfolgsszenarien aus C++ Tests
- ALLE Fehlerszenarien mit korrekten HTTP Status Codes
- Berechtigungsprüfungen für verschiedene Rollen
- Edge Cases aus der Spezifikation
- Verwende BaseProviderTest mit Testcontainern

### 10. PROVIDER REGISTRIERUNG

Füge den Provider zur Server-Konfiguration hinzu. Finde die Stelle wo andere Provider registriert werden (z.B. JpaRestfulServer.java oder Spring Configuration).

### 11. ERLAUBTE ROLLEN/PROFESSION OIDS

Basierend auf der Spezifikationsanalyse, definiere ALLOWED_PROFESSION_OIDS Array mit allen erlaubten Rollen für diese Operation.

**Mapping der Profession OIDs** (aus CreateOperationProvider):
- oid_arzt
- oid_zahnarzt
- oid_praxis_arzt
- oid_zahnarztpraxis
- oid_praxis_psychotherapeut
- oid_krankenhaus
- oid_apotheke
- oid_versicherter

### 12. SPEZIFISCHE VALIDIERUNGEN

Implementiere ALLE Validierungen aus der C++ Implementierung, z.B.:
- Task Status Prüfungen
- KVNR/TelematikID Validierungen
- AccessCode Prüfungen
- Signatur-Validierungen
- Bundle-Struktur Validierungen

### 13. FEHLERBEHANDLUNG

Verwende die korrekten FHIR Exceptions mit den richtigen HTTP Status Codes aus der C++ Implementierung:
- UnprocessableEntityException (422)
- ForbiddenOperationException (403)
- ResourceNotFoundException (404)
- ResourceGoneException (410)
- InvalidRequestException (400)

### 14. AUDIT-LOGGING DETAILS

Implementiere Audit-Logging EXAKT nach Spezifikation mit:
- Korrektem AuditEventAction (C/R/U/D)
- Subtype für die spezifische Operation
- Actor Information
- Patient-Bezug falls vorhanden
- Zusätzliche Details mit addEntityDetail

## KRITISCHE PRÜFPUNKTE

Bevor die Implementierung als fertig gilt, stelle sicher:
- [ ] ALLE ErpExpect Validierungen aus C++ sind umgesetzt
- [ ] ALLE Parameter aus OperationDefinition sind implementiert
- [ ] ALLE Anforderungen aus der XML-Spezifikation sind erfüllt
- [ ] Profession OID Prüfung ist korrekt
- [ ] Status-Übergänge entsprechen der Spezifikation
- [ ] Fehler-Responses haben korrekte HTTP Status Codes
- [ ] Audit-Logging ist vollständig
- [ ] Tests decken alle Szenarien ab
- [ ] Provider ist registriert und erreichbar

## WICHTIGE HINWEISE

1. Die C++ Implementierung ist die GOLDENE REFERENZ - alle Validierungen und Geschäftslogik MUSS 1:1 übernommen werden
2. Verwende IMMER die vorhandenen Services (AuthorizationService, AuditService, etc.)
3. Erstelle KEINE neuen Dateien außer den genannten
4. Nutze den CustomValidator für komplexe FHIR-Validierungen
5. Orientiere dich STARK an der CreateOperationProvider Struktur

## AUSGABE

Nach Abschluss der Analyse erstelle einen detaillierten Implementierungsplan mit:
1. Zusammenfassung aller gefundenen Anforderungen
2. Liste aller notwendigen Validierungen
3. Schritt-für-Schritt Implementierungsplan
4. Code-Skelett für Provider und Tests
5. Offene Fragen falls etwas unklar ist