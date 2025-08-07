# Validierungsregeln aus C++ Referenzimplementierung

## 1. Task und Prescription abrufen
**C++ Code**: `databaseHandle->retrieveTaskAndPrescription(prescriptionId)`
**Java Umsetzung**: 
```java
IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);
Task task = taskDao.read(theId);
```
**Fehler bei nicht gefunden**: HTTP 404 "Task not found for prescription id"

## 2. Telematik-ID aus Access Token extrahieren
**C++ Code**: `session.request.getAccessToken().stringForClaim(JWT::idNumberClaim)`
**Java Umsetzung**:
```java
AccessToken accessToken = authorizationService.validateAndExtractAccessToken(theRequestDetails);
String telematikId = accessToken.getIdNumber();
```
**Fehler wenn fehlt**: HTTP 400 "Missing Telematik-ID in ACCESS_TOKEN"

## 3. Task gelöscht/cancelled prüfen (A_19149_02)
**C++ Code**: 
```cpp
if(taskAccessCode.empty() || taskStatus == model::Task::Status::cancelled) {
    ErpFail(HttpStatus::Gone, "Task meanwhile deleted for prescription id");
}
```
**Java Umsetzung**:
```java
if (!task.hasIdentifier() || task.getStatus().equals(Task.TaskStatus.CANCELLED)) {
    throw new GoneException("Task meanwhile deleted for prescription id");
}
```

## 4. AccessCode validieren (A_19167_04)
**C++ Code**: `checkAccessCodeMatches(session.request, task)`
**Java Umsetzung**:
```java
String providedAccessCode = requestDetails.getParameter("ac");
if (providedAccessCode == null) {
    providedAccessCode = requestDetails.getHeader("X-AccessCode");
}
if (providedAccessCode == null) {
    throw new ForbiddenOperationException("AccessCode missing");
}

String taskAccessCode = task.getIdentifier().stream()
    .filter(id -> "https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode".equals(id.getSystem()))
    .findFirst()
    .map(Identifier::getValue)
    .orElseThrow(() -> new UnprocessableEntityException("Task has no AccessCode"));

if (!taskAccessCode.equals(providedAccessCode)) {
    throw new ForbiddenOperationException("Access code does not match");
}
```

## 5. Task Status prüfen (A_19168_01)
**C++ Code**:
```cpp
switch (taskStatus) {
    case model::Task::Status::inprogress:
        if (task.owner() == telematikId) {
            ErpFail2(HttpStatus::Conflict, "Task has invalid status in-progress", 
                    "Task is processed by requesting institution");
        } else {
            [[fallthrough]];
        }
    case model::Task::Status::draft:
    case model::Task::Status::completed:
        ErpFail(HttpStatus::Conflict, "Task has invalid status " + statusName);
    case model::Task::Status::ready:
        break;
}
```
**Java Umsetzung**:
```java
Task.TaskStatus status = task.getStatus();
if (status.equals(Task.TaskStatus.INPROGRESS)) {
    if (task.hasOwner() && task.getOwner().getDisplay().equals(telematikId)) {
        throw new UnprocessableEntityException("Task has invalid status in-progress. Task is processed by requesting institution");
    } else {
        throw new UnprocessableEntityException("Task has invalid status in-progress");
    }
} else if (status.equals(Task.TaskStatus.DRAFT) || status.equals(Task.TaskStatus.COMPLETED)) {
    throw new UnprocessableEntityException("Task has invalid status " + status.toCode());
} else if (!status.equals(Task.TaskStatus.READY)) {
    throw new UnprocessableEntityException("Task must be in status ready");
}
```

## 6. Einlösefrist prüfen (A_23539_01)
**C++ Code**:
```cpp
const auto expiryDate = task.expiryDate();
auto validUntil = date::make_zoned(model::Timestamp::GermanTimezone, 
                                   expiryDate.toChronoTimePoint() + 24h);
validUntil = floor<date::days>(validUntil.get_local_time());
if (validUntil.get_sys_time() < now) {
    ErpFail(HttpStatus::Forbidden, "Verordnung bis " + expiryDate.toGermanDateFormat() + " einlösbar.");
}
```
**Java Umsetzung**:
```java
Date expiryDate = extractExpiryDate(task);
Calendar cal = Calendar.getInstance();
cal.setTime(expiryDate);
cal.add(Calendar.DAY_OF_MONTH, 1);
if (cal.getTime().before(new Date())) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    throw new ForbiddenOperationException("Verordnung bis " + sdf.format(expiryDate) + " einlösbar.");
}
```

## 7. Mehrfachverordnung prüfen (A_22635_02)
**C++ Code**:
```cpp
if (mPExt && mPExt->isMultiplePrescription()) {
    const auto startDate = mPExt->startDateTime();
    auto validFrom = date::make_zoned(model::Timestamp::GermanTimezone, 
                                      startDate->toChronoTimePoint());
    validFrom = floor<date::days>(validFrom.get_local_time());
    if (now < validFrom.get_sys_time()) {
        ErpFail(HttpStatus::Forbidden, 
                "Teilverordnung zur Mehrfachverordnung " + mvoId + " ist ab " + 
                germanFmtTs + " einlösbar.");
    }
}
```
**Java Umsetzung**:
```java
// In Service-Klasse prüfen ob MVO und Startdatum
if (isMehrfachverordnung(prescriptionBundle)) {
    Date startDate = extractMvoStartDate(prescriptionBundle);
    if (startDate.after(new Date())) {
        String mvoId = extractMvoId(prescriptionBundle);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        throw new ForbiddenOperationException(
            "Teilverordnung zur Mehrfachverordnung " + mvoId + 
            " ist ab " + sdf.format(startDate) + " einlösbar.");
    }
}
```

## 8. Secret generieren und Status updaten (A_19169_01)
**C++ Code**:
```cpp
task.setStatus(model::Task::Status::inprogress);
const auto secret = SecureRandomGenerator::generate(32);
task.setSecret(ByteHelper::toHex(secret));
task.setOwner(*telematikId);
task.updateLastUpdate();
```
**Java Umsetzung**:
```java
// Status auf in-progress setzen
task.setStatus(Task.TaskStatus.INPROGRESS);

// Secret generieren (256 Bit = 32 Bytes)
SecureRandom random = new SecureRandom();
byte[] secretBytes = new byte[32];
random.nextBytes(secretBytes);
String secret = bytesToHex(secretBytes);

// Secret als Identifier hinzufügen
task.addIdentifier()
    .setSystem("https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret")
    .setValue(secret);

// Owner setzen
task.setOwner(new Reference().setDisplay(telematikId));

// LastModified aktualisieren
task.getMeta().setLastUpdated(new Date());
```

## 9. Consent für PKV prüfen (A_22110)
**C++ Code**:
```cpp
if (prescriptionId.type() == PrescriptionType::apothekenpflichtigeArzneimittelPkv ||
    prescriptionId.type() == PrescriptionType::direkteZuweisungPkv) {
    const auto consent = databaseHandle->retrieveConsent(kvnr.value());
    if (consent.has_value()) {
        responseBundle.addResource(makeFullUrl("/Consent/" + consentId), {}, {}, 
                                   consent->jsonDocument());
    }
}
```
**Java Umsetzung**:
```java
if (isPkvFlowType(flowType)) { // 200 oder 209
    String kvnr = extractKvnr(task);
    Consent consent = findConsentForKvnr(kvnr);
    if (consent != null) {
        bundle.addEntry()
            .setFullUrl("/Consent/" + consent.getIdElement().getIdPart())
            .setResource(consent);
    }
}
```

## 10. Response Bundle erstellen
**C++ Code**:
```cpp
model::Bundle responseBundle(model::BundleType::collection, NoProfile);
responseBundle.setLink(model::Link::Type::Self, linkBase + "/$accept/");
responseBundle.addResource(linkBase, {}, {}, task.jsonDocument());
responseBundle.addResource(uuid, {}, {}, healthCareProviderPrescription->jsonDocument());
```
**Java Umsetzung**:
```java
Bundle responseBundle = new Bundle();
responseBundle.setType(Bundle.BundleType.COLLECTION);
responseBundle.addLink()
    .setRelation("self")
    .setUrl("/Task/" + prescriptionId + "/$accept/");

// Task hinzufügen
responseBundle.addEntry()
    .setFullUrl("/Task/" + prescriptionId)
    .setResource(updatedTask);

// Binary (signiertes E-Rezept) hinzufügen
responseBundle.addEntry()
    .setFullUrl("urn:uuid:" + UUID.randomUUID())
    .setResource(prescriptionBinary);

// Meta-Profile setzen
Meta meta = new Meta();
meta.addProfile("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_PAR_AcceptOperation_Output|1.5");
responseBundle.setMeta(meta);
```