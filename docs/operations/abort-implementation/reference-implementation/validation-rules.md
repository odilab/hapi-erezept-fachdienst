# Validierungsregeln aus C++ Referenzimplementierung

## Übersicht
Diese Datei dokumentiert alle ErpExpect und VauExpect Statements aus der C++ Implementierung mit ihrer Java-Umsetzung.

## 1. Task Existenz Prüfung
**C++ Code**: 
```cpp
ErpExpect(taskAndKey.has_value(), HttpStatus::NotFound, "Task not found for prescription id");
```
**Java Umsetzung**:
```java
try {
    task = taskDao.read(theId);
} catch (ResourceNotFoundException e) {
    throw new ResourceNotFoundException("Task not found for prescription id");
}
```

## 2. Task bereits gelöscht
**C++ Code**:
```cpp
ErpExpect(taskStatus != model::Task::Status::cancelled, HttpStatus::Gone, "Task has already been deleted");
```
**Java Umsetzung**:
```java
if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
    throw new ResourceGoneException("Task has already been deleted");
}
```

## 3. Draft Status Prüfung
**C++ Code**:
```cpp
ErpExpect(taskStatus != model::Task::Status::draft, HttpStatus::Forbidden, "Abort not expected for newly created Task");
```
**Java Umsetzung**:
```java
if (task.getStatus().equals(Task.TaskStatus.DRAFT)) {
    throw new ForbiddenOperationException("Abort not expected for newly created Task");
}
```

## 4. Apotheke - Status Prüfung (A_19145)
**C++ Code**:
```cpp
ErpExpect(task.status() == model::Task::Status::inprogress, HttpStatus::Forbidden,
          "Task must be in progress for user pharmacy, is: " +
          std::string(model::Task::StatusNames.at(task.status())));
```
**Java Umsetzung**:
```java
if (!task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
    throw new ForbiddenOperationException("Task must be in progress for user pharmacy, is: " + 
        task.getStatus().toCode());
}
```

## 5. Apotheke - Secret Validierung (A_19224, A_20703)
**C++ Code**:
```cpp
const auto uriSecret = request.getQueryParameter("secret");
VauExpect(uriSecret.has_value() && uriSecret.value() == task.secret(), HttpStatus::Forbidden,
          VauErrorCode::brute_force, "No or invalid secret provided for user pharmacy");
```
**Java Umsetzung**:
```java
String providedSecret = theRequestDetails.getParameters().get("secret");
if (providedSecret == null || providedSecret.isEmpty()) {
    throw new ForbiddenOperationException("No secret provided for user pharmacy");
}

String taskSecret = task.getIdentifier().stream()
    .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
    .findFirst()
    .map(Identifier::getValue)
    .orElseThrow(() -> new ForbiddenOperationException("Task has no secret"));

if (!taskSecret.equals(providedSecret)) {
    // In Java können wir VAU Error Code nicht direkt setzen, aber Exception werfen
    throw new ForbiddenOperationException("Invalid secret provided for user pharmacy");
}
```

## 6. Nicht-Apotheke - Status Prüfung (A_19146)
**C++ Code**:
```cpp
ErpExpect(task.status() != model::Task::Status::inprogress, HttpStatus::Forbidden,
          "Task must not be in progress for users other than pharmacy, but is: " +
          std::string(model::Task::StatusNames.at(task.status())));
```
**Java Umsetzung**:
```java
if (task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
    throw new ForbiddenOperationException("Task must not be in progress for users other than pharmacy, but is: " + 
        task.getStatus().toCode());
}
```

## 7. Versicherte - KVNR Prüfung (A_20546_03)
**C++ Code**:
```cpp
const auto kvNrClaim = request.getAccessToken().stringForClaim(JWT::idNumberClaim);
const auto kvNrFromTask = task.kvnr();
Expect3(kvNrFromTask.has_value(), "Task has no KV number", std::logic_error);
if (kvNrClaim == kvNrFromTask) {
    auditDataCollector.setEventId(model::AuditEventId::POST_Task_abort_insurant);
    return;
}
```
**Java Umsetzung**:
```java
String kvnrFromToken = accessToken.getIdNumber();
String kvnrFromTask = extractKvnr(task);

if (kvnrFromTask == null) {
    throw new UnprocessableEntityException("Task has no KV number");
}

if (kvnrFromToken.equals(kvnrFromTask)) {
    // Patient löscht eigenes Rezept - kein AccessCode nötig
    auditEventId = "POST_Task_abort_insurant";
    return;
}
// Sonst ist es ein Vertreter - AccessCode wird geprüft
```

## 8. Arzt - Status Prüfung (A_19120_3)
**C++ Code**:
```cpp
ErpExpect(task.status() == model::Task::Status::ready, HttpStatus::Forbidden,
          "Task must be ready for doctor, but is: " +
          std::string(model::Task::StatusNames.at(task.status())));
```
**Java Umsetzung**:
```java
if (!task.getStatus().equals(Task.TaskStatus.READY)) {
    throw new ForbiddenOperationException("Task must be ready for doctor, but is: " + 
        task.getStatus().toCode());
}
```

## 9. AccessCode Prüfung (A_20547, A_19120_3, A_20703)
**C++ Code**:
```cpp
auto accessCode = getAccessCode(request); // Holt aus Header oder URL-Parameter
VauExpect(accessCode == task.accessCode(), HttpStatus::Forbidden, VauErrorCode::brute_force,
          "AccessCode mismatch");
```
**Java Umsetzung**:
```java
// AccessCode aus Header oder URL-Parameter holen
String providedAccessCode = theRequestDetails.getHeader("X-AccessCode");
if (providedAccessCode == null || providedAccessCode.isEmpty()) {
    providedAccessCode = theRequestDetails.getParameters().get("ac");
}

if (providedAccessCode == null || providedAccessCode.isEmpty()) {
    throw new ForbiddenOperationException("AccessCode missing");
}

String taskAccessCode = task.getIdentifier().stream()
    .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
    .findFirst()
    .map(Identifier::getValue)
    .orElseThrow(() -> new ForbiddenOperationException("Task has no AccessCode"));

if (!taskAccessCode.equals(providedAccessCode)) {
    throw new ForbiddenOperationException("AccessCode mismatch");
}
```

## 10. Flowtype 169/209 Einschränkung (A_22102_01)
**C++ Code**:
```cpp
if(professionOIDClaim.value() == profession_oid::oid_versicherter) {
    switch (task.type()) {
        case model::PrescriptionType::direkteZuweisung:
        case model::PrescriptionType::direkteZuweisungPkv:
            ErpExpect(taskStatus == model::Task::Status::completed, HttpStatus::Forbidden,
                      "Abort for patient in workflow types 169 / 209 only allowed for completed Task");
            break;
    }
}
```
**Java Umsetzung**:
```java
if ("oid_versicherter".equals(professionOid)) {
    String flowType = extractFlowType(task);
    if ("169".equals(flowType) || "209".equals(flowType)) {
        if (!task.getStatus().equals(Task.TaskStatus.COMPLETED)) {
            throw new ForbiddenOperationException(
                "Abort for patient in workflow types 169 / 209 only allowed for completed Task");
        }
    }
}
```

## 11. Task Status Update (A_19121)
**C++ Code**:
```cpp
task.setStatus(model::Task::Status::cancelled);
task.updateLastUpdate();
```
**Java Umsetzung**:
```java
task.setStatus(Task.TaskStatus.CANCELLED);
task.getMeta().setLastUpdated(new Date());
```

## 12. Daten Löschung (A_19027_06)
**C++ Code**:
```cpp
// Delete Task related Communications
databaseHandle->deleteCommunicationsForTask(task.prescriptionId());
// Update task in database and delete related HealthCareProviderPrescription, PatientConfirmation,
// Receipt, MedicationDispense, etc.:
databaseHandle->updateTaskClearPersonalData(task);
```
**Java Umsetzung**:
```java
// Communications löschen
deleteCommunicationsForTask(task.getIdElement().getIdPart());

// Personenbezogene Daten aus Task entfernen
clearPersonalDataFromTask(task);

// Task aktualisieren
taskDao.update(task, theRequestDetails);
```

## Zusammenfassung der Validierungsreihenfolge

1. **Task laden** - 404 wenn nicht gefunden
2. **Status Grundprüfung** - cancelled (410) oder draft (403)
3. **Flowtype-spezifisch** - 169/209 nur completed für Versicherte
4. **Rollenbasierte Prüfung**:
   - **Apotheke**: in-progress + Secret
   - **Versicherte**: nicht in-progress + KVNR-Check oder AccessCode
   - **Arzt**: ready + AccessCode
5. **Task Status Update** - auf cancelled setzen
6. **Daten löschen** - Communications und personenbezogene Daten
7. **Response** - 204 No Content