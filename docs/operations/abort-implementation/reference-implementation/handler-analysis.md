# C++ AbortTaskHandler Analyse

## Klassenstruktur

```cpp
class AbortTaskHandler : public TaskHandlerBase
{
    // Konstruktor mit erlaubten Profession OIDs
    AbortTaskHandler(const std::initializer_list<std::string_view>& allowedProfessionOiDs);
    
    // Hauptmethode für Request-Verarbeitung
    void handleRequest(PcSessionContext& session);
    
    // Validierungsmethoden
    void checkAccessValidityOutsidePharmacy(...);
    void checkAccessValidity(...);
    void checkAccessValidityPharmacy(...);
};
```

## Hauptlogik (handleRequest)

### 1. Request Parsing
```cpp
const auto prescriptionId = parseId(session.request, session.accessLog);
```

### 2. Profession OID Extraktion
```cpp
const auto professionOIDClaim = session.request.getAccessToken().stringForClaim(JWT::professionOIDClaim);
```

### 3. Task Laden mit Lock
```cpp
auto taskAndKey = databaseHandle->retrieveTaskForUpdate(prescriptionId);
```
**WICHTIG**: `retrieveTaskForUpdate` sperrt den Task für Updates (Transactional Lock)

### 4. Status Validierungen
```cpp
// Drei Hauptprüfungen:
ErpExpect(taskAndKey.has_value(), HttpStatus::NotFound, "Task not found");
ErpExpect(taskStatus != Status::cancelled, HttpStatus::Gone, "Already deleted");
ErpExpect(taskStatus != Status::draft, HttpStatus::Forbidden, "Not for newly created");
```

### 5. Flowtype 169/209 Spezialfall (A_22102_01)
```cpp
if(professionOIDClaim == oid_versicherter) {
    switch (task.type()) {
        case PrescriptionType::direkteZuweisung:
        case PrescriptionType::direkteZuweisungPkv:
            ErpExpect(taskStatus == Status::completed, HttpStatus::Forbidden, ...);
    }
}
```

### 6. Rollenbasierte Validierung
```cpp
checkAccessValidity(auditDataCollector, professionOIDClaim, task, request);
```

### 7. Task Status Update
```cpp
task.setStatus(model::Task::Status::cancelled);
task.updateLastUpdate();
```

### 8. Datenbereinigung
```cpp
// Communications löschen
databaseHandle->deleteCommunicationsForTask(task.prescriptionId());

// Personenbezogene Daten löschen
databaseHandle->updateTaskClearPersonalData(task);
```

### 9. Response
```cpp
makeResponse(session, HttpStatus::NoContent, nullptr);
```

## Validierungsmethoden im Detail

### checkAccessValidityPharmacy (A_19145, A_19224)

**Anforderungen**:
- Task MUSS im Status "in-progress" sein
- Secret MUSS als URL-Parameter übergeben werden
- Secret MUSS mit Task.secret übereinstimmen

```cpp
void checkAccessValidityPharmacy(const Task& task, const ServerRequest& request) {
    // Status prüfen
    ErpExpect(task.status() == Status::inprogress, HttpStatus::Forbidden,
              "Task must be in progress for user pharmacy");
    
    // Secret aus URL-Parameter
    const auto uriSecret = request.getQueryParameter("secret");
    
    // Secret validieren mit VAU Error Code
    VauExpect(uriSecret.has_value() && uriSecret.value() == task.secret(), 
              HttpStatus::Forbidden, VauErrorCode::brute_force,
              "No or invalid secret provided");
}
```

### checkAccessValidityOutsidePharmacy (A_19146, A_20546_03, A_20547)

**Anforderungen**:
- Task darf NICHT im Status "in-progress" sein
- Versicherte: KVNR-Check oder AccessCode
- Ärzte: Status muss "ready" sein + AccessCode

```cpp
void checkAccessValidityOutsidePharmacy(...) {
    // Status prüfen
    ErpExpect(task.status() != Status::inprogress, HttpStatus::Forbidden,
              "Task must not be in progress for users other than pharmacy");
    
    if(professionOIDClaim == oid_versicherter) {
        // KVNR Vergleich
        const auto kvNrClaim = request.getAccessToken().stringForClaim(JWT::idNumberClaim);
        const auto kvNrFromTask = task.kvnr();
        
        if (kvNrClaim == kvNrFromTask) {
            // Eigenes Rezept
            auditDataCollector.setEventId(POST_Task_abort_insurant);
            return;
        }
        // Vertreter - AccessCode prüfen
    } else {
        // Arzt - Status muss ready sein
        ErpExpect(task.status() == Status::ready, HttpStatus::Forbidden,
                  "Task must be ready for doctor");
    }
    
    // AccessCode prüfen
    checkAccessCodeMatches(request, task);
}
```

## Datenbank-Operationen

### deleteCommunicationsForTask
- Löscht alle Communications mit basedOn = Task/{id}
- Keine Fehlerbehandlung - Fehler brechen Operation nicht ab

### updateTaskClearPersonalData
- Entfernt alle personenbezogenen Daten aus Task
- KVNR bleibt erhalten
- Folgende Felder werden gelöscht:
  - AccessCode
  - Secret
  - Owner
  - HealthCarePrescriptionUuid
  - PatientConfirmationUuid
  - ReceiptUuid
  - LastMedicationDispense
  - Performer
  - Input/Output

## Audit-Logging

### EventIds nach Rolle
- `POST_Task_abort_insurant` - Patient löscht eigenes Rezept
- `POST_Task_abort_representative` - Vertreter mit AccessCode
- `POST_Task_abort_doctor` - Arzt mit AccessCode
- `POST_Task_abort_pharmacy` - Apotheke mit Secret

### Audit-Daten
```cpp
session.auditDataCollector()
    .setInsurantKvnr(*kvnr)
    .setPrescriptionId(prescriptionId)
    .setAction(model::AuditEvent::Action::del);
```

## Wichtige Unterschiede zu Java

### 1. Transactional Lock
C++ verwendet `retrieveTaskForUpdate` für pessimistic locking.
In Java/JPA: Verwende `@Lock(LockModeType.PESSIMISTIC_WRITE)`

### 2. VAU Error Codes
C++ kann spezielle VAU Error Codes setzen (brute_force).
In Java: Nur Exception werfen möglich

### 3. Datenbank-Batch-Operationen
C++ hat spezielle DB-Methoden für Batch-Löschung.
In Java: Einzelne DAO-Operationen verwenden

### 4. Error Handling
C++ verwendet `ErpExpect` und `VauExpect` Makros.
In Java: Explizite Exception-Würfe

## Performance-Überlegungen

1. **Lock Duration**: Task wird für gesamte Operation gesperrt
2. **Communications**: Können viele sein - Batch-Löschung wichtig
3. **Transactional Boundary**: Gesamte Operation sollte transaktional sein

## Sicherheitsaspekte

1. **Brute-Force Protection**: VAU Error Code bei falschem Secret/AccessCode
2. **KVNR Retention**: Bleibt für Audit-Trail erhalten
3. **Complete Deletion**: Alle zugehörigen Ressourcen werden gelöscht