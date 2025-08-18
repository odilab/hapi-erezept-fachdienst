# Validierungsregeln aus C++ Referenz

## Alle ErpExpect/VauExpect Statements aus RejectTaskHandler.cxx

### 1. Task-Existenz Validierung
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
**Fehlercode**: 404 Not Found

---

### 2. Task Cancelled Status Validierung
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
**Fehlercode**: 410 Gone

---

### 3. Task In-Progress Status Validierung (A_19171-03)
**C++ Code**:
```cpp
ErpExpect(taskStatus == model::Task::Status::inprogress, HttpStatus::Forbidden,
          "Task not in status in progress, is: " + std::string(model::Task::StatusNames.at(taskStatus)));
```
**Java Umsetzung**:
```java
if (!task.getStatus().equals(Task.TaskStatus.INPROGRESS)) {
    throw new ForbiddenOperationException("Task not in status in progress, is: " + task.getStatus().toCode());
}
```
**Fehlercode**: 403 Forbidden

---

### 4. Secret Validierung (A_19171-03 + A_20703)
**C++ Code**:
```cpp
const auto uriSecret = session.request.getQueryParameter("secret");
VauExpect(uriSecret.has_value() && uriSecret.value() == task.secret(), HttpStatus::Forbidden,
          VauErrorCode::brute_force, "No or invalid secret");
```
**Java Umsetzung**:
```java
String providedSecret = theRequestDetails.getParameter("secret");
if (providedSecret == null || providedSecret.isEmpty()) {
    // In Java müssen wir VAU-Error-Code anders handhaben
    throw new ForbiddenOperationException("No or invalid secret");
}

String taskSecret = task.getIdentifier().stream()
    .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret".equals(id.getSystem()))
    .findFirst()
    .map(Identifier::getValue)
    .orElse(null);

if (!providedSecret.equals(taskSecret)) {
    throw new ForbiddenOperationException("No or invalid secret");
}
```
**Fehlercode**: 403 Forbidden (mit VAU-Error-Code: brute_force)

---

### 5. Task Key Validierung (für Datenbank-Update)
**C++ Code**:
```cpp
ErpExpect(taskAndKey->key.has_value(), HttpStatus::InternalServerError, "Missing key for task");
```
**Java Umsetzung**:
```java
// In Java/HAPI wird dies implizit durch die DAO-Schicht gehandhabt
// Falls explizite Prüfung nötig:
if (task.getIdElement() == null || !task.getIdElement().hasIdPart()) {
    throw new InternalErrorException("Missing key for task");
}
```
**Fehlercode**: 500 Internal Server Error

---

### 6. KVNR Validierung (für Audit)
**C++ Code**:
```cpp
const auto kvnr = task.kvnr();
Expect3(kvnr.has_value(), "Task has no KV number", std::logic_error);
```
**Java Umsetzung**:
```java
String kvnr = extractKvnr(task);
if (kvnr == null || kvnr.isEmpty()) {
    LOGGER.error("Task has no KV number for audit logging");
    // Trotzdem fortfahren, aber Audit ohne KVNR
}
```
**Hinweis**: In C++ ist dies ein logic_error, in Java sollte die Operation trotzdem durchgeführt werden

---

## Zusammenfassung der Validierungsreihenfolge

1. **Task laden** → 404 wenn nicht gefunden
2. **Cancelled prüfen** → 410 wenn cancelled
3. **Status prüfen** → 403 wenn nicht in-progress
4. **Secret prüfen** → 403 wenn fehlt oder falsch (mit brute_force)
5. **Updates durchführen**
6. **KVNR für Audit** → Warnung wenn fehlt, aber kein Fehler

## Besonderheiten

### VauExpect vs ErpExpect
- **VauExpect**: Wird für Security-relevante Prüfungen verwendet (Secret, AccessCode)
  - Setzt speziellen VAU-Error-Code Header
  - Für Brute-Force Protection
  
- **ErpExpect**: Standard Business-Logic Validierungen
  - Normale HTTP Fehlercodes
  - Keine speziellen Header

### Java-Äquivalente für Fehlerbehandlung

| C++ Exception | Java/HAPI Exception |
|--------------|-------------------|
| ErpExpect(..., HttpStatus::NotFound, ...) | ResourceNotFoundException |
| ErpExpect(..., HttpStatus::Gone, ...) | ResourceGoneException |
| ErpExpect(..., HttpStatus::Forbidden, ...) | ForbiddenOperationException |
| ErpExpect(..., HttpStatus::InternalServerError, ...) | InternalErrorException |
| VauExpect(..., HttpStatus::Forbidden, brute_force, ...) | ForbiddenOperationException + Custom Header |