# Relevante Anforderungen für $activate Operation

## Anforderungs-IDs aus gemSpec_FD_eRp_V2.3.0.xml

### A_19018 - Rollenprüfung
**Titel**: E-Rezept-Fachdienst - Rollenprüfung Verordnender aktiviert Rezept
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Aktivieren eines Tasks mittels HTTP-POST/$activate-Operation sicherstellen, dass ausschließlich verordnende Leistungserbringer in der Rolle:
- oid_praxis_arzt
- oid_zahnarztpraxis  
- oid_praxis_psychotherapeut
- oid_krankenhaus

die Operation ausführen dürfen.

### A_19568 - PrescriptionID Prüfung
**Titel**: E-Rezept-Fachdienst - Task aktivieren - Prüfung PrescriptionID
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Aufruf der Operation POST /Task/<id>/$activate prüfen, dass:
- Die PrescriptionID des Tasks mit der PrescriptionID im übergebenen QES-Datensatz übereinstimmt
- Der Präfix der PrescriptionID gleich dem Flowtype des zu aktivierenden Tasks ist
- Bei Fehler: HTTP 400 zurückgeben

### A_19127-01 - KVNR Übernahme
**Titel**: E-Rezept-Fachdienst - Task aktivieren - Übernahme der KVNR des Patienten
**Beschreibung**: Der E-Rezept-Fachdienst MUSS die KVNR des Patienten aus dem signierten Bundle extrahieren und im Task speichern.

### A_19128 - Status aktivieren
**Titel**: E-Rezept-Fachdienst - Status aktivieren
**Beschreibung**: Der E-Rezept-Fachdienst MUSS die zulässige Aktivierung eines Tasks mittels /Task/<id>/$activate-Operation im Status Task.status = ready vollziehen und bei erfolgreichem Abschluss die Ressource Task im HTTP-Body zurückgeben.

### A_19024-03 - AccessCode Prüfung
**Titel**: E-Rezept-Fachdienst - Task aktivieren - Prüfung AccessCode
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zugriff auf einen Task prüfen, dass der übermittelte AccessCode mit dem gespeicherten übereinstimmt.

### A_19020 - PKCS#7 Validierung
**Titel**: E-Rezept-Fachdienst - PKCS#7 Strukturvalidierung
**Beschreibung**: Der E-Rezept-Fachdienst MUSS den übergebenen FHIR-Operationsparameter als PKCS#7-Datei einer Enveloping CAdES-Signatur entgegennehmen und bei ungültiger ASN.1 Datenstruktur mit HTTP 400 antworten.

### A_22487 - AuthoredOn Prüfung
**Titel**: Task aktivieren - Prüfregel Ausstellungsdatum
**Beschreibung**: Das Ausstellungsdatum (authoredOn) muss mit dem Signaturdatum übereinstimmen (nur Tag wird verglichen, nicht Uhrzeit).

### A_22231 - BTM Ausschluss
**Titel**: E-Rezept-Fachdienst - BTM und Thalidomid Ausschluss
**Beschreibung**: Fehlercode 400 mit Hinweis "BTM und Thalidomid nicht zulässig" wenn Betäubungsmittel erkannt werden.

### A_22222 - Coverage Type Prüfung
**Titel**: E-Rezept-Fachdienst - Coverage Type Validierung
**Beschreibung**: Prüfung ob Coverage Type zum Workflow passt (z.B. PKV nur bei Workflow 200).

### A_19445-10 - Expiry/Accept Date
**Titel**: E-Rezept-Fachdienst - Expiry und Accept Date setzen
**Beschreibung**: 
- Task.ExpiryDate = Datum der QES-Erstellung + 3 Monate (außer bei MVO)
- Task.AcceptDate = Datum der QES-Erstellung + 28 Tage (160/169) oder 3 Monate (200/209)

## Berechtigungsmatrix

| Operation | Erlaubte Profession OIDs |
|-----------|-------------------------|
| $activate | oid_praxis_arzt, oid_zahnarztpraxis, oid_praxis_psychotherapeut, oid_krankenhaus |

## Status-Anforderungen

- Task muss im Status **draft** sein
- Nach erfolgreicher Aktivierung: Status wird zu **ready**

## Fehlerszenarien

| Fehler | HTTP Code | Meldung |
|--------|-----------|---------|
| Task nicht gefunden | 404 | Requested Task not found in DB |
| Task bereits gelöscht | 410 | Task has already been deleted |
| Falscher AccessCode | 403 | Access code does not match |
| Task nicht im Status draft | 403 | Task not in status draft |
| Ungültige PKCS#7 Struktur | 400 | Invalid PKCS#7 structure |
| PrescriptionID stimmt nicht | 400 | PrescriptionID mismatch |
| AuthoredOn != Signaturdatum | 400 | AuthoredOn does not match signing date |
| BTM/Thalidomid | 400 | BTM und Thalidomid nicht zulässig |
| Ungültige KVNR | 400 | Ungültige Versichertennummer |

## Audit-Anforderungen

Bei erfolgreicher Aktivierung muss ein AuditEvent erstellt werden mit:
- EventId: POST_Task_activate
- Action: update
- KVNR des Patienten
- PrescriptionId des Tasks