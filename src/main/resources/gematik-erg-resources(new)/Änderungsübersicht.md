# Änderungsübersicht gematik-erg-resources

## Allgemeine Änderungen

1. **Versionsänderung**: Im neuen Ordner wurde die Version von `1.0.0-CC` auf `1.1.0-RC1` aktualisiert
2. **Datum**: Das Referenzdatum wurde von `2024-06-20` auf `2025-02-28` geändert

## Neue Dateien

### Neue ValueSets

- ValueSet-erg-total-price-component-deduction-type-vs.json
- ValueSet-erg-rechnungsart-vs.json
- ValueSet-erg-rechnungsposition-faktor-gruende-auspraegungen-VS.json
- ValueSet-erg-rechnungsposition-zusatz-VS.json
- ValueSet-erg-chargeitem-type-VS.json
- ValueSet-erg-rechnung-abrechnungsart-vs.json
- ValueSet-erg-rechnung-behandlungsart-vs.json
- ValueSet-erg-abrechnungs-diagnose-use-VS.json
- ValueSet-ERGVerkehrsmittel.json

### Neue StructureDefinitions

- StructureDefinition-erg-wegegeld-reiseentschaedigung.json
- StructureDefinition-erg-zahnregion.json
- StructureDefinition-erg-rechnungsposition-type.json
- StructureDefinition-erg-rechnungsprozedur.json
- StructureDefinition-erg-rechnungsposition-go-angaben.json
- StructureDefinition-erg-person.json
- StructureDefinition-erg-institution.json
- StructureDefinition-erg-patient.json
- StructureDefinition-erg-fachrichtung.json
- StructureDefinition-ERGTokenStornierteRechnung.json
- StructureDefinition-ERGZusatzinformationZurAbrechnungsart.json
- StructureDefinition-InvoicePeriod.json
- StructureDefinition-erg-abzug-kassenanteil.json
- StructureDefinition-ERGAbrechnungsDiagnoseProzedurFreitext.json
- StructureDefinition-ERGBemaPunktsumme.json
- StructureDefinition-ERGRechnungspositionBehandlungsdatum.json
- StructureDefinition-ERGRechnungspositionZusatz.json
- StructureDefinition-ERGTeilsumme.json
- StructureDefinition-ERGAbrechnungsDiagnoseProzedur.json

### Neue CodeSystems

- CodeSystem-erg-total-price-component-type-cs.json
- CodeSystem-erg-rechnungsart-cs.json
- CodeSystem-erg-rechnungsposition-faktor-gruende-CS.json
- CodeSystem-erg-rechnungsposition-zusatz-CS.json
- CodeSystem-erg-rechnung-abrechnungsart-cs.json
- CodeSystem-erg-rechnung-identifier-type-cs.json
- CodeSystem-erg-chargeitem-type-CS.json

## Entfernte Dateien

1. CodeSystem-erg-attachment-format-cs.json
2. StructureDefinition-erg-versicherteperson.json (ersetzt durch erg-person.json)
3. ValueSet-BehandlungsartVS.json
4. StructureDefinition-erg-pdf-repraesentation-rechnung.json
5. StructureDefinition-erg-preisdetails-rechnungsposition.json
6. StructureDefinition-erg-invoice-period.json (ersetzt durch InvoicePeriod.json)
7. StructureDefinition-erg-leistungserbringer-organisation.json (ersetzt durch erg-institution.json)
8. StructureDefinition-erg-leistungserbringer.json
9. StructureDefinition-ExtensionBehandlungsart.json
10. StructureDefinition-erg-abrechnungsrelevante-diagnose.json (ersetzt durch ERGAbrechnungsDiagnoseProzedur.json)
11. Practitioner-R2444Practitioner.json
12. Patient-R2444PKVersichertePerson.json
13. Invoice-R2444Rechnung.json
14. CodeSystem-erg-operationen-cs.json
15. CodeSystem-BehandlungsartCS.json
16. Mehrere Bundle-Beispiele wurden entfernt

## Wesentliche inhaltliche Änderungen

### StructureDefinition-erg-rechnung.json
- Deutlich größer geworden (13KB → 46KB)
- Viele neue Erweiterungen und Element-Definitionen
- Neue Behandlungszeitraum-Extension
- Neue AbrechnungsDiagnoseProzedur-Extension

### StructureDefinition-erg-rechnungsposition.json
- Wesentlich umfangreicher (3.8KB → 27KB)
- Neue Erweiterungen wie Rechnungspositionstyp, Zusatz, WegegeldReiseentschädigung

### StructureDefinition-erg-rechnungsdiagnose.json
- Erweitert von 2.2KB auf 4.4KB mit zusätzlichen Elementen

### StructureDefinition-erg-dokumentenmetadaten.json
- Leicht erweitert (20KB → 21KB)

## Zusammenfassung

Die neue Version enthält wesentliche Erweiterungen und Verfeinerungen des Datenmodells:

1. Detailliertere Abbildung von Rechnungspositionen mit spezifischen Erweiterungen für verschiedene Abrechnungsarten (GOÄ, GOZ, BEMA)
2. Neue Strukturen für Diagnosen und Prozeduren
3. Erweiterung für Wegegeld und Reiseentschädigung
4. Neue Codesysteme und ValueSets für zusätzliche strukturierte Daten
5. Umstrukturierung von Personendaten (ersetzt versicherteperson durch differenziertere Modelle)
6. Bessere Unterstützung für verschiedene Abrechnungsarten

Die Änderungen deuten auf eine umfassende Weiterentwicklung des eRechnungs-Datenmodells hin mit deutlich mehr Strukturierungsmöglichkeiten für verschiedene medizinische Abrechnungsarten. 