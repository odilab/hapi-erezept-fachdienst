# Validierungsprobleme mit gematik Reference Validator

## Problem
Der aktuell eingebundene gematik Reference Validator (lokal: Version 2.12.0 in `/libs/`) erkennt einige E‑Rezept‑spezifische Elemente nicht korrekt und meldet dadurch Fehler bei Task‑Extensions und Profil‑Slicing:
- `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_FlowType`
- `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_HealthCarePrescriptionUuid`
- `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_AcceptDate`
- `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_EX_ExpiryDate`

Typische Meldungen (Auszug):
- „Die extension … ist nicht bekannt und hier nicht erlaubt“
- „Dieses Element stimmt mit keinem bekannten Slice … überein, Slicing ist CLOSED“
- „Slice 'Task.extension:flowType': a matching slice is required, but not found“

## Aktueller Workaround (lokale Entwicklung)
Im `CustomValidator` werden diese bekannten Meldungen aktuell zu WARNINGS herabgestuft, damit der lokale Workflow durchläuft.
- Datei: `src/main/java/ca/uhn/fhir/jpa/starter/custom/interceptor/CustomValidator.java`
- Logik: bestimmte Fehlermuster (unbekannte ERP‑Extensions / Slicing‑Fehler) → als Warnungen behandeln

Das ist bewusst nur für die lokale Entwicklungs- und Testumgebung gedacht.

## Ziel
Workarounds entfernen und eine strikte, profilkonforme Validierung ermöglichen. Dazu müssen die verwendeten Validator‑Artefakte aktualisiert bzw. ergänzt werden, sodass die ERP‑Profile und Extensions vollständig bekannt sind.

## Erforderliche Anpassungen an den Validator-Libraries

1) Aktualisierung auf eine Validator‑Version mit vollständiger ERP‑Unterstützung
- Beziehe eine neuere Version der gematik Reference Validator Libraries, die die aktuellen ERP‑Profiles/Extensions (inkl. Task‑Slicing) kennt.
- Quellen (Standards/Pakete):
  - `de.gematik.refv` (Reference Validator) – aktuelle Version verwenden
  - Offizielle ERP/KBV IG-Pakete (NPM): `de.gematik.erezept-workflow.r4`, `kbv.ita.erp` etc.
- Erwartetes Verhalten nach Update:
  - die oben genannten Extensions sind bekannt
  - Slicing‑Definitionen des Task‑Profils (z. B. `flowType`, `acceptDate`, `expiryDate`) werden korrekt aufgelöst

2) IG‑Pakete explizit bereitstellen (falls nicht durch die Validator‑Libs mitgebracht)
- Stelle sicher, dass die HAPI‑Validation Support Chain bzw. der Referenz‑Validator Zugriff auf die notwendigen `StructureDefinition`, `CodeSystem`, `ValueSet` und `SearchParameter` hat.
- Optionen:
  - IG‑Pakete als NPM-Bundles ins Projekt legen und beim Start laden
  - oder die Pakete in den Classpath/Resource-Pfad legen, wenn die Referenz‑Lib dies erwartet
- Relevante Pakete (Beispiel; Versionen passend zum Zielprofil wählen):
  - `de.gematik.erezept-workflow.r4` (ERP Workflow Profile)
  - `kbv.ita.erp` / `kbv.basis` (KBV Profile, auf die ERP-Profilkette verweist)

3) Canonical-Versionen tolerieren (versionierte vs. unversionierte Profile)
- Viele Ressourcen deklarieren Profile als Canonicals mit Versionssuffix, z. B. `…/GEM_ERP_PR_MedicationDispense|1.5`.
- Stelle sicher, dass der Validator Profile sowohl mit als auch ohne Versionsanteil korrekt matched:
  - Entweder durch korrekte IG‑Ladereihenfolge (bevorzugt)
  - oder – falls notwendig – durch tolerante Canonical‑Behandlung im eigenen Code (aktuell in `CloseTaskService` umgesetzt)

4) Terminologie‑Checks an die lokalen Bundle‑Varianten anpassen
- Für lokale Tests wird z. T. eine minimale Terminologie verwendet.
- Falls die Validator‑Libs Terminologie‑Erweiterungen erwarten, entweder:
  - die benötigten `ValueSet`/`CodeSystem` bereitstellen
  - oder Terminologie‑Prüfungen in der lokalen Umgebung abschwächen (Produktion: streng)

## Konkrete Umsetzungsschritte

1. Dependencies aktualisieren
- In `pom.xml` (oder entsprechender Build‑Konfiguration) die `de.gematik.refv`‑Artefakte auf den neuesten Stand bringen.
- Sicherstellen, dass keine veralteten JARs in `/libs/` die neuen Artefakte überdecken.

2. IG‑Pakete verfügbar machen
- Lade die relevanten NPM‑Pakete (z. B. aus `simplifier.net` oder gematik Registry) herunter.
- Variante A (HAPI): IG‑Paketlade-Mechanismus verwenden (RepositoryValidatingInterceptor, PackageCache, etc.).
- Variante B (gematik‑RefV): Falls die Lib eigene Ladepfade nutzt, die Pakete an der erwarteten Stelle bereitstellen.

3. Tests gegen die neuen Profile laufen lassen
- Integrationstests (Accept/Close) mit aktiviertem `CustomValidator` ausführen.
- Prüfen, dass die bisherigen Workarounds (Ignorierregeln) keine Errors mehr maskieren müssen.

4. Workarounds zurückbauen
- In `CustomValidator` die Heuristiken zum Ignorieren bekannter Fehlermuster entfernen oder per Konfiguration abschalten, sobald die Profile korrekt erkannt werden.
- Ziel: ERROR/FATAL vom Validator → HTTP 422; WARNING → Log‑Warnung, kein Blocker.

## Übergangslösung (bis zur Aktualisierung)
- Beibehalten der bestehenden Ignorierregeln im `CustomValidator` ausschließlich für lokale Entwicklung.
- In Produktion strikt validieren (nach Update der Libraries) und Ignorierlogik deaktivieren.

## Ergänzende Hinweise zur CLOSE‑Operation
- Die CLOSE‑Operation erwartet die Abgabeinformationen in einem `Parameters`‑Objekt unter `rxDispensation`.
- Das `Secret` kann als Query‑Parameter `secret` oder im Header `X-Secret` übergeben werden (Server unterstützt beides).
- Für `MedicationDispense`:
  - Profil: `https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense` (versioniert oder unversioniert)
  - `subject.identifier` mit KVNR (`http://fhir.de/sid/gkv/kvid-10`)
  - `status = completed`, sinnvolle `quantity` und `whenHandedOver`

## Empfehlung
- Kurzfristig: Lokale Workarounds beibehalten.
- Mittelfristig: gematik Reference Validator auf eine Version aktualisieren, die die ERP‑Profile vollständig unterstützt, IG‑Pakete bereitstellen.
- Danach: Ignorierlogik im `CustomValidator` entfernen und Validierung strikt fahren.

## Change‑Log (dieses Dokument)
- CLOSE‑Beispiel im Guide und Skript auf Parameters‑Form aktualisiert.
- Dokument um konkrete Schritte zur Aktualisierung der Validator‑Libs und IG‑Pakete erweitert.
- Klarstellung der lokalen vs. produktiven Validierungsstrategie.