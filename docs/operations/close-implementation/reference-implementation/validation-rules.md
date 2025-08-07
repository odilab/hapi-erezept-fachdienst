# Validierungsregeln aus C++ Referenz

## 1. Task-Existenz-Prüfung
**C++ Code**: 
```cpp
ErpExpect(taskAndKey.has_value(), HttpStatus::NotFound, "Task not found for prescription id");
```
**Java Umsetzung**:
```java
Task task = taskDao.read(theId);
// ResourceNotFoundException wird automatisch geworfen wenn nicht gefunden
```

## 2. Task Gelöscht/Cancelled Prüfung
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

## 3. Task Status in-progress Prüfung
**C++ Code**:
```cpp
ErpExpect(taskStatus == model::Task::Status::inprogress, HttpStatus::Forbidden,
          "Task has to be in progress, but is: " + std::string(model::Task::StatusNames.at(taskStatus)));
```
**Java Umsetzung**:
```java
if (!task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
    throw new ForbiddenOperationException("Task has to be in progress, but is: " + task.getStatus().toCode());
}
```

## 4. Secret-Validierung
**C++ Code**:
```cpp
const auto uriSecret = session.request.getQueryParameter("secret");
VauExpect(uriSecret.has_value() && uriSecret.value() == task.secret(), HttpStatus::Forbidden,
          VauErrorCode::brute_force, "No or invalid secret provided for Task");
```
**Java Umsetzung**:
```java
String providedSecret = theRequestDetails.getParameter("secret");
if (providedSecret == null || providedSecret.isEmpty()) {
    throw new ForbiddenOperationException("No secret provided");
}

String taskSecret = task.getIdentifier().stream()
    .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
    .findFirst()
    .map(Identifier::getValue)
    .orElseThrow(() -> new ForbiddenOperationException("Task has no secret"));
    
if (!taskSecret.equals(providedSecret)) {
    throw new ForbiddenOperationException("Invalid secret provided for Task");
}
```

## 5. Telematik-ID Extraktion
**C++ Code**:
```cpp
const auto telematikIdFromAccessToken = accessToken.stringForClaim(JWT::idNumberClaim);
ErpExpect(telematikIdFromAccessToken.has_value(), HttpStatus::BadRequest, "Telematik-ID not contained in JWT");
```
**Java Umsetzung**:
```java
String telematikId = accessToken.getIdNumber();
if (telematikId == null || telematikId.isEmpty()) {
    throw new UnprocessableEntityException("Telematik-ID not contained in JWT");
}
```

## 6. MedicationDispense Prüfung (wenn Body leer)
**C++ Code**:
```cpp
if(medicationsAndDispenses.medicationDispenses.empty()) {
    model::MedicationDispenseId medicationDispenseId(prescriptionId, 0);
    auto retrieved = databaseHandle->retrieveMedicationDispense(kvnr.value(), medicationDispenseId);
    ErpExpect(!retrieved.medicationDispenses.empty(), HttpStatus::Forbidden, "Medication dispense does not exist.");
}
```
**Java Umsetzung**:
```java
if (medicationDispenses == null || medicationDispenses.isEmpty()) {
    // Prüfe ob bereits eine MedicationDispense existiert
    SearchParameterMap params = new SearchParameterMap();
    params.add(MedicationDispense.SP_PRESCRIPTION, new ReferenceParam("Task/" + taskId));
    IBundleProvider results = medicationDispenseDao.search(params);
    
    if (results.isEmpty()) {
        throw new ForbiddenOperationException("Abschluss des Workflows konnte nicht durchgeführt werden. Dispensierinformationen wurden nicht bereitgestellt.");
    }
}
```

## 7. In-Progress Date vor Completed Date
**C++ Code**:
```cpp
ErpExpect(inProgressDate < completedTimestamp, HttpStatus::InternalServerError, 
          "in-progress date later than completed time.");
```
**Java Umsetzung**:
```java
Date inProgressDate = getInProgressDate(task);
Date completedDate = new Date();
if (inProgressDate.after(completedDate)) {
    throw new InternalErrorException("in-progress date later than completed time");
}
```

## 8. Prescription Binary Prüfung
**C++ Code**:
```cpp
ErpExpect(prescription.has_value() && prescription.value().data().has_value(), 
          ::HttpStatus::InternalServerError, "No matching prescription found.");
```
**Java Umsetzung**:
```java
Binary prescriptionBinary = loadPrescriptionBinary(task);
if (prescriptionBinary == null || prescriptionBinary.getContent() == null) {
    throw new InternalErrorException("No matching prescription found");
}
```

## Zusammenfassung der Validierungsreihenfolge

1. Task laden und Existenz prüfen
2. Task nicht cancelled prüfen
3. Task Status = in-progress prüfen
4. Secret aus Query-Parameter validieren
5. Telematik-ID aus Access Token extrahieren
6. MedicationDispense prüfen (Body oder existierend)
7. Zeitstempel-Konsistenz prüfen
8. Prescription Binary laden und prüfen
9. Task-Status auf completed setzen
10. Receipt Bundle erstellen und signieren
11. Communications löschen
12. Alles speichern und Response zurückgeben