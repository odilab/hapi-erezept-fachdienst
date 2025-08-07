# Anforderungen für $close Operation

## Anforderungs-IDs aus gematik Spezifikation

### A_19230-01: Rollenprüfung
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Beenden eines Tasks mittels HTTP-POST/$close-Operation sicherstellen, dass ausschließlich abgebende Institutionen in der Rolle:
- `oid_oeffentliche_apotheke`
- `oid_krankenhausapotheke`

die Operation durchführen können.

**Umsetzung**: Prüfung der professionOID aus dem ACCESS_TOKEN

### A_19231-02: Secret-Prüfung
**Beschreibung**: Der E-Rezept-Fachdienst MUSS das im URL-Parameter `?secret=...` übertragene Secret gegen das im Task gespeicherte `Task.identifier:Secret` prüfen und bei Ungleichheit mit HTTP 403 abbrechen.

**Anforderungen**:
- Task muss im Status `in-progress` sein
- Secret muss als Query-Parameter übergeben werden
- Bei Fehlen oder Ungleichheit: HTTP 403

### A_26002-01: Profilprüfung MedicationDispense (Flowtype 160/169/200/209)
**Beschreibung**: Für Flowtypes 160, 169, 200, 209 muss das MedicationDispense-Objekt dem Profil `GEM_ERP_PR_MedicationDispense` entsprechen.

**Fehlermeldung**: "Unzulässige Abgabeinformationen: Für diesen Workflow sind nur Abgabeinformationen für Arzneimittel zulässig."

### A_26003-01: Profilprüfung MedicationDispense (Flowtype 162 - DiGA)
**Beschreibung**: Für Flowtype 162 (DiGA) muss das MedicationDispense-Objekt dem Profil `GEM_ERP_PR_MedicationDispense_DiGA` entsprechen.

**Fehlermeldung**: "Unzulässige Abgabeinformationen: Für diesen Workflow sind nur Abgabeinformationen für digitale Gesundheitsanwendungen zulässig."

### A_24287-01: Aufruf ohne MedicationDispense
**Beschreibung**: Der Aufruf darf ohne MedicationDispense im Request Body erfolgen. In diesem Fall muss geprüft werden, dass bereits eine MedicationDispense für diesen Task existiert.

**Fehlerfall**: HTTP 403 mit Meldung "Abschluss des Workflows konnte nicht durchgeführt werden. Dispensierinformationen wurden nicht bereitgestellt."

### A_19248-05: Schemaprüfung MedicationDispense
**Beschreibung**: Das Parameters-Objekt muss gegen das Profil `GEM_ERP_PR_PAR_CloseOperation_Input` validiert werden, insbesondere:
- Korrektheit der Rezept-ID
- Validität des MedicationDispense-Objekts
- Konsistenz der Daten

### A_19232: Status-Update
**Beschreibung**: Der Task-Status muss auf `completed` gesetzt werden.

### A_19233-05: Receipt Bundle erstellen
**Beschreibung**: Es muss ein Receipt Bundle erstellt werden mit:
- Telematik-ID der Apotheke
- Zeitstempel von in-progress
- Aktueller Zeitstempel (completed)
- Prescription-ID
- Signatur mit CAdES-BES

### A_20513: Communication löschen
**Beschreibung**: Alle Communication-Ressourcen, die mit dem Task verknüpft sind, müssen gelöscht werden.

## Berechtigungsmatrix

| Operation | Erlaubte Rollen |
|-----------|----------------|
| $close | oid_oeffentliche_apotheke |
| | oid_krankenhausapotheke |

## Status-Anforderungen

- Task MUSS im Status `in-progress` sein
- Nach erfolgreicher Operation: Status wird zu `completed`

## Fehlerszenarien

| Szenario | HTTP-Code | Fehlermeldung |
|----------|-----------|---------------|
| Task nicht gefunden | 404 | "Task not found for prescription id" |
| Task gelöscht/cancelled | 410 | "Task has already been deleted" |
| Falscher Status | 403 | "Task has to be in progress" |
| Falsches/fehlendes Secret | 403 | "No or invalid secret provided for Task" |
| Keine MedicationDispense | 403 | "Abschluss des Workflows konnte nicht durchgeführt werden..." |
| Falsche Rolle | 403 | "Die Operation $close ist nur für Apotheken erlaubt" |