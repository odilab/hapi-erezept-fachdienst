# Validierungsregeln aus C++ Referenzimplementierung

## 1. Task Existenz-Prüfung
**C++ Code**: `ErpExpect(taskAndKey.has_value(), HttpStatus::NotFound, "Requested Task not found in DB");`
**Java Umsetzung**: 
```java
Task task = taskDao.read(new IdType("Task", taskId));
if (task == null) {
    throw new ResourceNotFoundException("Requested Task not found in DB");
}
```
**Fehlercode**: 404 Not Found

## 2. Task Status - Nicht cancelled
**C++ Code**: `ErpExpect(taskStatus != model::Task::Status::cancelled, HttpStatus::Gone, "Task has already been deleted");`
**Java Umsetzung**:
```java
if (task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
    throw new GoneException("Task has already been deleted");
}
```
**Fehlercode**: 410 Gone

## 3. AccessCode Validierung
**C++ Code**: `checkAccessCodeMatches(session.request, task);`
**Java Umsetzung**:
```java
String providedAccessCode = theRequestDetails.getHeader("X-AccessCode");
String taskAccessCode = extractAccessCode(task);
if (!taskAccessCode.equals(providedAccessCode)) {
    throw new ForbiddenOperationException("Access code does not match");
}
```
**Fehlercode**: 403 Forbidden

## 4. Task Status - Muss draft sein
**C++ Code**: `ErpExpect(taskStatus == model::Task::Status::draft, HttpStatus::Forbidden, "Task not in status draft but in status " + std::string(model::Task::StatusNames.at(taskStatus)));`
**Java Umsetzung**:
```java
if (!task.getStatus().equals(Task.TaskStatus.DRAFT)) {
    throw new ForbiddenOperationException("Task not in status draft but in status " + task.getStatus().toCode());
}
```
**Fehlercode**: 403 Forbidden

## 5. PKCS#7 Struktur-Validierung
**C++ Code**: `SignedPrescription::fromBin(cadesBesSignatureFile, session.serviceContext.getTslManager(), allowedProfessionOidsForQesSignature(task.type()));`
**Java Umsetzung**:
```java
try {
    CadesBesSignature signature = CadesBesSignature.fromBase64(binaryData);
    signature.validate(tslManager);
} catch (Exception e) {
    throw new UnprocessableEntityException("Invalid PKCS#7 structure: " + e.getMessage());
}
```
**Fehlercode**: 400 Bad Request

## 6. PrescriptionID Übereinstimmung
**C++ Code**: `checkBundlePrescriptionId(task, kbvOrEvdgaBundle);`
**Java Umsetzung**:
```java
String taskPrescriptionId = task.getIdentifierFirstRep().getValue();
String bundlePrescriptionId = extractPrescriptionIdFromBundle(kbvBundle);
if (!taskPrescriptionId.equals(bundlePrescriptionId)) {
    throw new UnprocessableEntityException("PrescriptionID mismatch: Task has " + taskPrescriptionId + ", Bundle has " + bundlePrescriptionId);
}
// Präfix-Prüfung
String flowType = extractFlowType(task);
if (!bundlePrescriptionId.startsWith(flowType + ".")) {
    throw new UnprocessableEntityException("PrescriptionID prefix does not match flowType");
}
```
**Fehlercode**: 400 Bad Request

## 7. AuthoredOn == Signaturdatum
**C++ Code**: `checkAuthoredOnEqualsSigningDate(kbvOrEvdgaBundle, *signingTime);`
**Java Umsetzung**:
```java
LocalDate authoredOn = extractAuthoredOn(kbvBundle).toLocalDate();
LocalDate signingDate = signature.getSigningTime().toLocalDate();
if (!authoredOn.equals(signingDate)) {
    throw new UnprocessableEntityException("AuthoredOn (" + authoredOn + ") does not match signing date (" + signingDate + ")");
}
```
**Fehlercode**: 400 Bad Request

## 8. BTM/Thalidomid Prüfung
**C++ Code**: `ErpExpect(! mr.isNarcotics(), HttpStatus::BadRequest, "BTM und Thalidomid nicht zulässig");`
**Java Umsetzung**:
```java
for (MedicationRequest mr : extractMedicationRequests(kbvBundle)) {
    if (isNarcotics(mr)) {
        throw new UnprocessableEntityException("BTM und Thalidomid nicht zulässig");
    }
}
```
**Fehlercode**: 400 Bad Request

## 9. Coverage Type Validierung
**C++ Code**: `checkValidCoverage(kbvOrEvdgaBundle, task.type());`
**Java Umsetzung**:
```java
String coverageType = extractCoverageType(kbvBundle);
String flowType = extractFlowType(task);
// PKV Coverage nur bei 200er Workflows
if ("PKV".equals(coverageType) && !flowType.startsWith("20")) {
    throw new UnprocessableEntityException("PKV coverage not allowed for workflow " + flowType);
}
// GKV Coverage nicht bei 200er Workflows
if ("GKV".equals(coverageType) && flowType.startsWith("20")) {
    throw new UnprocessableEntityException("GKV coverage not allowed for workflow " + flowType);
}
```
**Fehlercode**: 400 Bad Request

## 10. KVNR Validierung
**C++ Code**: Implizit beim Extrahieren der KVNR
**Java Umsetzung**:
```java
String kvnr = extractKvnrFromBundle(kbvBundle);
if (!isValidKvnr(kvnr)) {
    throw new UnprocessableEntityException("Ungültige Versichertennummer (KVNR): Die übergebene Versichertennummer entspricht nicht den Prüfziffer-Validierungsregeln.");
}
```
**Fehlercode**: 400 Bad Request

## 11. ANR/LANR/ZANR Prüfung (Optional)
**C++ Code**: `checkPractitioner(kbvOrEvdgaBundle, session);`
**Java Umsetzung**:
```java
String anr = extractAnrFromPractitioner(kbvBundle);
if (!isValidAnr(anr)) {
    if (config.isAnrValidationError()) {
        throw new UnprocessableEntityException("Ungültige Arztnummer (LANR oder ZANR)");
    } else {
        response.addHeader("Warning", "252 erp-server \"Ungültige Arztnummer\"");
    }
}
```
**Fehlercode**: 400 Bad Request oder Warning