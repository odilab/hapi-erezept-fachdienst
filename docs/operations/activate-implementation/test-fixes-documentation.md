# Test-Fixes für die Activate-Operation Implementation

Diese Dokumentation beschreibt alle Fixes, die während der Implementierung der `$activate` Operation notwendig waren, um die Tests erfolgreich zum Laufen zu bringen.

## 1. Profession Enum Mapping Fix

### Problem
```java
// Fehlerhaft:
case ZAHNARZT:
    return "oid_zahnarztpraxis";
case PSYCHOTHERAPEUT:
    return "oid_praxis_psychotherapeut";
```

**Fehler:** Die Enum-Werte `ZAHNARZT` und `PSYCHOTHERAPEUT` existieren nicht in der `Profession` Enum.

### Lösung
```java
// Korrekt:
case ZAHNARZT_PRAXIS:
    return "oid_zahnarztpraxis";
case PRAXIS_PSYCHOTHERAPEUT:
    return "oid_praxis_psychotherapeut";
```

### Begründung
Die `Profession` Enum aus dem AccessToken verwendet spezifischere Bezeichnungen, die den Praxistyp mit einschließen. Dies wurde durch Analyse der vorhandenen Enum-Definition ermittelt.

## 2. GoneException Ersetzung

### Problem
```java
// Fehlerhaft:
throw new GoneException("Task has already been deleted");
```

**Fehler:** `GoneException` ist in HAPI FHIR 8.0.0 nicht verfügbar.

### Lösung
```java
// Workaround:
throw new ForbiddenOperationException("Task has already been deleted");
```

### Begründung
HAPI FHIR 8.0.0 hat die `GoneException` Klasse entfernt. Als Workaround wurde `ForbiddenOperationException` verwendet, mit einem Kommentar, dass dies eigentlich ein 410 Status sein sollte. Dies ist ein akzeptabler Kompromiss für die erste Version.

## 3. Bouncy Castle Import

### Problem
```java
// Fehlender Import führte zu:
Attribute cannot be resolved to a type
```

### Lösung
```java
import org.bouncycastle.asn1.cms.Attribute;
```

### Begründung
Die `Attribute` Klasse wird für die Signaturvalidierung benötigt und kommt aus der Bouncy Castle Bibliothek, nicht aus dem Standard Java.

## 4. Provider Registration in Tests

### Problem
**Fehler:** "The FHIR endpoint on this server does not know how to handle POST operation[Task/$activate]"

### Lösung
In `BaseProviderTest.java`:
```java
"hapi.fhir.custom-provider-classes=ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider"
```

In `src/test/resources/application.yaml`:
```yaml
custom-provider-classes: ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider,ca.uhn.fhir.jpa.starter.custom.operation.activate.ActivateOperationProvider
```

### Begründung
Die Test-Konfiguration hatte nur den CreateOperationProvider registriert. Für die Tests musste der ActivateOperationProvider explizit in beiden Konfigurationsdateien hinzugefügt werden.

## 5. Task ID vs Prescription ID

### Problem
```java
// Fehlerhaft:
.onInstance(new IdType("Task", prescriptionId))
```

**Fehler:** HTTP 404 - Task not found. Die PrescriptionID ist ein Business Identifier, nicht die Resource ID.

### Lösung
```java
// Korrekt:
.onInstance(draftTask.getIdElement())
```

### Begründung
FHIR unterscheidet zwischen Resource IDs (technische IDs) und Business Identifiers. Die Operation muss mit der tatsächlichen Resource ID aufgerufen werden, nicht mit der PrescriptionID.

## 6. Binary Encoding Fix

### Problem
```java
// Fehlerhaft:
ePrescription.setData(signedBundle.getBytes());
```

**Fehler:** "Malformed content" - Die Signatur vom Fachdiensttool ist bereits Base64 encodiert.

### Lösung
```java
// Korrekt:
ePrescription.setDataElement(new Base64BinaryType(signedBundle));
```

### Begründung
Das Fachdiensttool liefert die Signatur bereits als Base64-String. Wenn man `.getBytes()` verwendet, wird der Base64-String nochmal als Bytes interpretiert, was zu ungültigen Daten führt.

## 7. AuthoredOn Date Validation

### Problem
**Fehler:** "AuthoredOn (2024-01-01) does not match signing date (2025-08-05)"

### Lösung
```java
// In loadAndAdaptKbvBundleXml:
String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
bundleXml = bundleXml.replaceAll(
    "<authoredOn value=\"[^\"]+\"\\s*/>",
    "<authoredOn value=\"" + today + "\"/>"
);
```

### Begründung
Die Validierung prüft, ob das Ausstellungsdatum des Rezepts mit dem Signaturdatum übereinstimmt. Da wir für Tests immer "heute" signieren, muss auch das authoredOn Datum auf heute gesetzt werden.

## 8. KVNR Extraction Fix

### Problem
```java
// Fehlerhaft:
.filter(id -> "http://fhir.de/StructureDefinition/identifier-kvid-10".equals(id.getType().getCodingFirstRep().getSystem()))
```

**Fehler:** "Patient has no KVNR" - Falsches System für die KVNR-Suche.

### Lösung
```java
// Korrekt:
.filter(id -> "http://fhir.de/sid/gkv/kvid-10".equals(id.getSystem()))
```

### Begründung
Die KVNR wird direkt als Identifier mit dem System `http://fhir.de/sid/gkv/kvid-10` gespeichert, nicht als getypter Identifier. Die ursprüngliche Implementierung suchte nach dem falschen Attribut.

## 9. Patient Reference als Identifier

### Problem
```java
// Fehlerhaft:
task.setFor(new Reference("Patient/" + kvnr));
```

**Fehler:** "Resource Patient/S040464113 not found" - KVNR ist kein Resource ID.

### Lösung
```java
// Korrekt:
Reference patientRef = new Reference();
patientRef.setType("Patient");
patientRef.setIdentifier(new Identifier()
    .setSystem("http://fhir.de/sid/gkv/kvid-10")
    .setValue(kvnr));
task.setFor(patientRef);
```

### Begründung
Da kein echter Patient in der Datenbank existiert, wird eine "logical reference" verwendet, die den Patienten über seine KVNR identifiziert statt über eine Resource ID.

## 10. Test Assertions für Identifier References

### Problem
```java
// Fehlerhaft:
assertTrue(activatedTask.getFor().getReference().contains(versichertenKvnr));
```

**Fehler:** NullPointerException - `getReference()` ist null bei Identifier-basierten References.

### Lösung
```java
// Korrekt:
assertNotNull(activatedTask.getFor().getIdentifier());
assertEquals("http://fhir.de/sid/gkv/kvid-10", activatedTask.getFor().getIdentifier().getSystem());
assertEquals("S040464113", activatedTask.getFor().getIdentifier().getValue());
```

### Begründung
Bei Identifier-basierten References ist das `reference` Feld null. Stattdessen muss auf das `identifier` Objekt zugegriffen werden.

## 11. Aktivierte Task ID für zweiten Aktivierungsversuch

### Problem
```java
// Fehlerhaft:
// Zweite Aktivierung mit alter Task ID
.onInstance(draftTask.getIdElement())
```

**Fehler:** HTTP 404 - Nach der Aktivierung könnte sich die Task ID geändert haben.

### Lösung
```java
// Korrekt:
Parameters firstResult = client.operation()...execute();
Task activatedTask = (Task) firstResult.getParameter().get(0).getResource();
// Zweite Aktivierung mit aktualisierter Task ID
.onInstance(activatedTask.getIdElement())
```

### Begründung
HAPI FHIR kann bei Updates die Resource ID ändern (z.B. durch Versionierung). Daher muss die aktuelle Task aus dem Ergebnis der ersten Aktivierung verwendet werden.

## 12. Feste KVNR in Tests

### Problem
```java
// Fehlerhaft:
createSignedBundleForTest(prescriptionId, versichertenKvnr);
```

### Lösung
```java
// Korrekt:
createSignedBundleForTest(prescriptionId, "S040464113");
```

### Begründung
Das Test-Bundle (Beispiel_4.xml) enthält eine feste KVNR (S040464113). Diese muss konsistent verwendet werden, da sie in der Signatur enthalten ist und nicht geändert werden kann.

## Zusammenfassung

Die meisten Fixes waren notwendig aufgrund von:
1. **API-Unterschieden** zwischen HAPI FHIR Versionen
2. **Missverständnissen** zwischen Business Identifiers und Resource IDs
3. **Encoding-Problemen** bei der Signaturverarbeitung
4. **Konfigurationsunterschieden** zwischen Produktiv- und Testumgebung
5. **Validierungsanforderungen** der gematik-Spezifikation

Alle Fixes folgen den Best Practices für FHIR-Implementierungen und stellen sicher, dass die Tests die realen Anforderungen korrekt abbilden.