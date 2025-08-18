# C++ RejectTaskHandler Analyse

## Datei-Locations
- Header: `/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/RejectTaskHandler.hxx`
- Implementation: `/Users/rene/Desktop/Arbeit/ibm/erp-processing-context/src/erp/service/task/RejectTaskHandler.cxx`

## Handler-Struktur

```cpp
class RejectTaskHandler : public TaskHandlerBase
{
public:
    RejectTaskHandler(const std::initializer_list<std::string_view>& allowedProfessionOiDs);
    void handleRequest(PcSessionContext& session) override;
};
```

## Geschäftslogik-Flow (handleRequest Methode)

### 1. Prescription-ID parsen
```cpp
const auto prescriptionId = parseId(session.request, session.accessLog);
```

### 2. Task aus Datenbank laden (mit Lock für Update)
```cpp
auto* databaseHandle = session.database();
auto taskAndKey = databaseHandle->retrieveTaskForUpdate(prescriptionId);
```

### 3. Task-Existenz prüfen
```cpp
ErpExpect(taskAndKey.has_value(), HttpStatus::NotFound, "Task not found for prescription id");
```

### 4. Task-Status validieren (nicht cancelled)
```cpp
const auto taskStatus = task.status();
ErpExpect(taskStatus != model::Task::Status::cancelled, HttpStatus::Gone, "Task has already been deleted");
```

### 5. Task muss im Status "in-progress" sein (A_19171-03)
```cpp
A_19171_03.start("Check that Task is in progress");
ErpExpect(taskStatus == model::Task::Status::inprogress, HttpStatus::Forbidden,
          "Task not in status in progress, is: " + std::string(model::Task::StatusNames.at(taskStatus)));
A_19171_03.finish();
```

### 6. Secret validieren (A_19171-03)
```cpp
A_19171_03.start("Check secret");
const auto uriSecret = session.request.getQueryParameter("secret");
A_20703.start("Set VAU-Error-Code header field to brute_force whenever AccessCode or Secret mismatches");
VauExpect(uriSecret.has_value() && uriSecret.value() == task.secret(), HttpStatus::Forbidden,
          VauErrorCode::brute_force, "No or invalid secret");
A_20703.finish();
A_19171_03.finish();
```

### 7. Secret löschen (A_19172-01)
```cpp
A_19172_01.start("Delete secret from Task");
task.deleteSecret();
A_19172_01.finish();
```

### 8. Owner löschen (A_24175)
```cpp
A_24175.start("Delete owner from Task");
task.deleteOwner();
A_24175.finish();
```

### 9. Status auf "ready" setzen (A_19172-01)
```cpp
A_19172_01.start("Set Task status to ready");
task.setStatus(model::Task::Status::ready);
A_19172_01.finish();
```

### 10. LastUpdate aktualisieren
```cpp
task.updateLastUpdate();
```

### 11. Task in Datenbank aktualisieren
```cpp
ErpExpect(taskAndKey->key.has_value(), HttpStatus::InternalServerError, "Missing key for task");
databaseHandle->updateTaskStatusAndSecret(task, *taskAndKey->key);
```

### 12. MedicationDispense löschen falls vorhanden (A_24286-02)
```cpp
A_24286_02.start("Delete MedicationDispense");
if(task.lastMedicationDispense().has_value())
{
    task.deleteLastMedicationDispense();
    databaseHandle->updateTaskDeleteMedicationDispense(task);
}
A_24286_02.finish();
```

### 13. Response mit HTTP 204 No Content (A_19514)
```cpp
A_19514.start("HttpStatus 204 for successful POST");
makeResponse(session, HttpStatus::NoContent, nullptr/*body*/);
A_19514.finish();
```

### 14. Audit-Daten sammeln
```cpp
const auto kvnr = task.kvnr();
Expect3(kvnr.has_value(), "Task has no KV number", std::logic_error);
session.auditDataCollector()
    .setEventId(model::AuditEventId::POST_Task_reject)
    .setInsurantKvnr(*kvnr)
    .setAction(model::AuditEvent::Action::update)
    .setPrescriptionId(prescriptionId);
```

## Wichtige Erkenntnisse

1. **Transaktionale Sicherheit**: Task wird mit Lock für Update geladen
2. **VauExpect vs ErpExpect**: VauExpect wird für Secret-Validierung verwendet (Brute-Force Schutz)
3. **Zwei-Phasen Update**: Erst Task-Status/Secret, dann separat MedicationDispense löschen
4. **Kein Response-Body**: Operation gibt nur HTTP 204 zurück
5. **KVNR ist Pflicht**: Task muss KVNR haben für Audit-Log

## Kritische Validierungen

1. Task muss existieren
2. Task darf nicht cancelled sein
3. Task MUSS im Status "in-progress" sein
4. Secret MUSS vorhanden und korrekt sein
5. Task muss einen Key für Datenbank-Update haben

## Unterschiede zu Java-Implementierung beachten

- C++ verwendet `retrieveTaskForUpdate` mit Lock
- C++ hat separates `updateTaskDeleteMedicationDispense`
- C++ verwendet VauExpect für Brute-Force Schutz
- Java muss diese Konzepte äquivalent umsetzen