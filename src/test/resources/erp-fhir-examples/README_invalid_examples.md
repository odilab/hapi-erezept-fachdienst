# Ungültige KBV FOR Beispiele

Diese Dateien enthalten absichtlich fehlerhafte KBV FOR Ressourcen, die für Validierungstests verwendet werden können.

## Fehlerhafte Dateien:

### 1. KBV_PR_FOR_Patient_example_invalid_no_name.xml
- **Fehler**: Patient ohne Name-Element
- **Erwartung**: Validierungsfehler, da `name` ein Pflichtfeld im KBV_PR_FOR_Patient Profil ist

### 2. KBV_PR_FOR_Patient_example_invalid_no_birthdate.xml
- **Fehler**: Patient ohne Geburtsdatum
- **Erwartung**: Validierungsfehler, da `birthDate` ein Pflichtfeld im KBV_PR_FOR_Patient Profil ist

### 3. KBV_PR_FOR_Patient_example_invalid_wrong_identifier_system.xml
- **Fehler**: Patient mit falschem System für die Krankenversichertennummer
- **Erwartung**: Validierungsfehler wegen ungültigem identifier.system

### 4. KBV_PR_FOR_Practitioner_example_invalid_wrong_profile.xml
- **Fehler**: Practitioner mit falschem/nicht existierendem Profil
- **Erwartung**: Validierungsfehler, da das Profil "WRONG_PROFILE" nicht existiert

### 5. KBV_PR_FOR_Organization_example_invalid_no_name_identifier.xml
- **Fehler**: Organization ohne Name und ohne Identifier
- **Erwartung**: Validierungsfehler, da sowohl `name` als auch `identifier` Pflichtfelder sind

## Test mit Reference Validator

Diese Dateien können direkt mit dem Gematik Reference Validator getestet werden:

```bash
# Beispiel für Patient ohne Name
java -jar referencevalidator-cli.jar core KBV_PR_FOR_Patient_example_invalid_no_name.xml

# Beispiel für Practitioner mit falschem Profil
java -jar referencevalidator-cli.jar core KBV_PR_FOR_Practitioner_example_invalid_wrong_profile.xml

# Beispiel für Organization ohne Name und Identifier
java -jar referencevalidator-cli.jar core KBV_PR_FOR_Organization_example_invalid_no_name_identifier.xml
```

## Hinweis

Das CORE Modul des Gematik Reference Validators ist möglicherweise toleranter als erwartet. Einige dieser Fehler werden eventuell nur als Warnungen und nicht als Fehler gemeldet.