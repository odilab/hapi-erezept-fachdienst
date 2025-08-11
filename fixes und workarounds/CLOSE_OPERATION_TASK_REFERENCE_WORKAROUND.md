## Workaround: MedicationDispense.authorizingPrescription mit Task-Referenz in HAPI 8.0.0

### Problem
- Gematik fordert: `MedicationDispense.authorizingPrescription` MUSS auf eine `Task` verweisen (z. B. `Task/{PrescriptionId}`).
- HAPI FHIR (R4) validiert jedoch strikt gegen den Standard, der hier nur `MedicationRequest` zulässt. Ergebnis: HTTP 422 (HAPI-0931) beim Persistieren.
- Die HAPI-Core-Validierung greift VOR Custom-Validatoren, daher kommt unser gematik-Validator nicht zum Zug.

### Ursache
- Reihenfolge in HAPI:
  1) Core Type Checking (scheitert bei `Task` an `authorizingPrescription`)
  2) Referential Integrity
  3) Custom Interceptors/Validatoren (gematik-Validierung)

### Ziel
- Task-Referenz gematik-konform verarbeiten, ohne HAPI zu forken.
- Weiterhin die gematik-Validierung ermöglichen.

### Umgesetzte Lösung (Workaround)
- Eingehende `MedicationDispense` dürfen eine echte `Task/{id}`-Referenz in `authorizingPrescription` enthalten.
- Vor dem Persistieren wird diese Referenz in eine Identifier-Referenz umgeschrieben, um den HAPI-Core-Type-Check zu umgehen:
  - `Reference.identifier.system = https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId`
  - `Reference.identifier.value = {PrescriptionId}`
- Die Validierung akzeptiert und prüft beide Formen:
  - `Reference.reference == "Task/{PrescriptionId}"` ODER
  - `Reference.identifier` mit obigem System/Value.

Damit bleibt die Semantik erhalten (klare Bindung zur Task/PrescriptionId), ohne gegen HAPI-Core-Typregeln zu verstoßen.

### Implementierungsdetails
- Geänderte Klassen/Funktionen:
  - `src/main/java/ca/uhn/fhir/jpa/starter/custom/operation/close/CloseTaskService.java`
    - `validateMedicationDispenses(...)`: Akzeptiert nun beide Referenzformen und validiert entsprechend.
    - `saveMedicationDispenses(...)`: Ruft `normalizeAuthorizingPrescriptionForHapi(...)` auf.
    - `normalizeAuthorizingPrescriptionForHapi(...)`: Wandelt `Task/{id}` → Identifier-Referenz (siehe oben) vor dem DAO-Write.

- Test-Anpassung (stabilisiert Status-Prüfung):
  - `src/test/java/ca/uhn/fhir/jpa/starter/custom/operation/close/CloseOperationIntegrationTest.java`
    - Nach `$close` wird der `Task` über die versionlose ID gelesen (`toVersionless()`), um nicht versehentlich eine alte Revision zu erwischen.

### Auswirkungen
- Alle Close-Integrationstests laufen grün.
- Persistierte `MedicationDispense` enthält intern eine Identifier-Referenz statt einer harten `Task/{id}`-Referenz. Für Clients ist die Verknüpfung über System/Value eindeutig herstellbar.
- Optional: Ein Response-Interceptor könnte beim Ausliefern die Identifier-Referenz wieder als `Task/{id}`-Referenz darstellen (kosmetisch, kein Muss).

### Verworfen/ohne Wirkung
- `enforce_referential_integrity_on_write: false`: Beeinflusst nur Existenz-Prüfung, nicht den Core-Type-Check.
- Reine CustomValidator-Lösung: Greift zu spät (nach Core-Type-Check).

### Hinweise
- Lösung ist HAPI-8.0.0-kompatibel und erfordert keinen Fork.
- Akzept/Activate-Flows bleiben unverändert; der Workaround ist auf `$close`/`MedicationDispense` fokussiert.


