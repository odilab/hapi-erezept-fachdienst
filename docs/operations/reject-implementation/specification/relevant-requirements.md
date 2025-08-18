# Relevante Anforderungen für $reject Operation

## Anforderungs-IDs aus gemSpec_FD_eRp_V2.3.0.xml

### A_19170-02: E-Rezept-Fachdienst - Task zurückweisen - Rollenprüfung
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zurückweisen eines Tasks für ein E-Rezept mittels HTTP-POST/$reject-Operation auf den in der URL referenzierten /Task/<id> sicherstellen, dass ausschließlich abgebende Institutionen in der Rolle
- oid_oeffentliche_apotheke
- oid_krankenhausapotheke

berechtigt sind.

**Implementierung**: Prüfung der professionOID im JWT Access Token

### A_19171-03: E-Rezept-Fachdienst - Task zurückweisen - Prüfung Secret
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zugriff auf einen Task mittels HTTP-POST-Operation über /Task/<id>/$reject das im URL-Parameter "?secret=..." übertragene Secret gegen das im referenzierten Task gespeicherte Secret Task.identifier:Secret prüfen.

**Implementierung**: 
- Secret aus Query-Parameter extrahieren
- Mit Task.identifier (System: GEM_ERP_NS_Secret) vergleichen
- Bei Mismatch: HTTP 403 Forbidden mit VAU-Error-Code: brute_force

### A_19172-01: E-Rezept-Fachdienst - Task zurückweisen - Secret löschen und Status setzen
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zurückweisen eines Tasks mittels HTTP-POST-Operation über /Task/<id>/$reject die externe ID in Task.identifier:Secret löschen und den Status des referenzierten Tasks auf "ready" setzen.

**Implementierung**:
- Task.identifier mit Secret-System entfernen
- Task.status auf "ready" setzen
- Task.meta.lastUpdated aktualisieren

### A_24175: E-Rezept-Fachdienst - Task zurückweisen - Telematik-ID der abgebenden LEI löschen
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zurückweisen eines Tasks mittels HTTP-POST-Operation über /Task/<id>/$reject die zum referenzierten Task in Task.owner gespeicherte Telematik-ID der abgebenden LEI löschen.

**Implementierung**: Task.owner auf null setzen

### A_24286-02: E-Rezept-Fachdienst - Task zurückweisen - Dispensierinformationen löschen
**Beschreibung**: Der E-Rezept-Fachdienst MUSS beim Zurückweisen eines Tasks für ein E-Rezept mittels POST /Task/<id>/$reject die Dispensierinformationen, falls welche vorhanden sind, löschen:
- Medication, die aus der MedicationDispense referenziert wird → löschen
- MedicationDispense → löschen

**Implementierung**: 
- Prüfen ob Task.extension mit lastMedicationDispense vorhanden
- Falls ja: MedicationDispense und referenzierte Medication löschen

### A_19514: HTTP Status für erfolgreiche POST Operation
**Beschreibung**: Erfolgreiche POST-Operationen müssen mit HTTP 204 No Content beantwortet werden.

**Implementierung**: Bei erfolgreicher Durchführung HTTP 204 ohne Body zurückgeben

### A_20703: VAU-Error-Code Header bei Brute-Force
**Beschreibung**: Set VAU-Error-Code header field to brute_force whenever AccessCode or Secret mismatches.

**Implementierung**: Bei falschem Secret spezielle Exception mit VAU-Error-Code werfen

## Berechtigungsmatrix

| Operation | Erlaubte Rollen | Validierung |
|-----------|----------------|-------------|
| $reject | oid_oeffentliche_apotheke, oid_krankenhausapotheke | Secret in URL-Parameter "secret" muss mit Task.identifier für Secret übereinstimmen |

## Status-Anforderungen

- Task MUSS im Status "in-progress" sein
- Nach Operation: Status wird auf "ready" gesetzt
- Task darf NICHT im Status "cancelled" sein (HTTP 410 Gone)
- Andere Status führen zu HTTP 403 Forbidden

## Validierungsregeln (Reihenfolge)

1. Task existiert (sonst HTTP 404)
2. Task ist nicht cancelled (sonst HTTP 410 Gone)  
3. Task ist im Status "in-progress" (sonst HTTP 403)
4. Secret ist vorhanden und korrekt (sonst HTTP 403 mit brute_force)
5. Profession OID ist berechtigt (sonst HTTP 403)

## Fehlerszenarien

| Fehler | HTTP Status | Fehlermeldung |
|--------|-------------|---------------|
| Task nicht gefunden | 404 Not Found | "Task not found for prescription id" |
| Task bereits gelöscht (cancelled) | 410 Gone | "Task has already been deleted" |
| Task nicht im Status in-progress | 403 Forbidden | "Task not in status in progress, is: [aktueller Status]" |
| Kein oder falsches Secret | 403 Forbidden | "No or invalid secret" (mit VAU-Error-Code: brute_force) |
| Unberechtigt (falsche Rolle) | 403 Forbidden | "Operation $reject ist nur für Apotheken erlaubt" |

## Audit-Anforderungen

- Event-ID: POST_Task_reject
- Action: update
- Outcome: 0 (Erfolg)
- What: Task-Referenz
- Who: Telematik-ID der Apotheke
- Patient: KVNR aus Task