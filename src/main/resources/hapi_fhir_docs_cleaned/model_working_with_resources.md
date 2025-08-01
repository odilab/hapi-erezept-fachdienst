---
source: https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html
crawled: 2025-08-01T14:04:59.839616
---

# Model Working With Resources

#  2.0.1Working with Resources
[ ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#working-with-resources)
Every resource type defined by FHIR has a corresponding class, which contains a number of getters and setters for the basic properties of that resource.
HAPI tries to make populating objects easier, by providing lots of convenience methods. For example, the Observation resource has an "issued" property which is of the FHIR "instant" type (a system time with either seconds or milliseconds precision). There are methods to use the actual FHIR datatype, but also convenience methods which use built-in Java types.
```
Observation obs = new Observation();

// These are all equivalent
obs.setIssuedElement(new InstantType(new Date()));
obs.setIssuedElement(new InstantType(new Date(), TemporalPrecisionEnum.MILLI));
obs.setIssued(new Date());

// The InstantType also lets you work with the instant as a Java Date
// object or as a FHIR String.
Date date = obs.getIssuedElement().getValue(); // A date object
String dateString = obs.getIssuedElement().getValueAsString(); // "2014-03-08T12:59:58.068-05:00"

```

Copy
#  2.0.2Navigating Structures
[ ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#navigating-structures)
Most HAPI structures provide getters that automatically create child objects on access. This means it is simple to navigate complex structures without needing to worry about instantiating child objects.
```
Observation observation = new Observation();

// None of these calls will not return null, but instead create their
// respective
// child elements.
List<Identifier> identifierList = observation.getIdentifier();
CodeableConcept code = observation.getCode();
StringType textElement = observation.getCode().getTextElement();

// DateTimeDt is a FHIR primitive however, so the following will return
// null
// unless a value has been placed there.
Date active = observation.addIdentifier().getPeriod().getStartElement().getValue();

```

Copy
##  2.0.2.1Coded/Enumerated Values[](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#codedenumerated-values)
There are many places in the FHIR specification where a "coded" string is used. This means that a code must be chosen from a list of allowable values.
2.0.2.1.1Closed Valuesets / Codes
The FHIR specification defines a number of "closed" ValueSets, such as the one used for [Patient.gender](http://hl7.org/fhir/valueset-administrative-gender.html). These valuesets must either be empty, or be populated with a value drawn from the list of allowable values defined by FHIR. HAPI provides special typesafe Enums to help in dealing with these fields.
```
Patient patient = new Patient();

// You can set this code using a String if you want. Note that
// for "closed" valuesets (such as the one used for Patient.gender)
// you must use one of the strings defined by the FHIR specification.
// You must not define your own.
patient.getGenderElement().setValueAsString("male");

// HAPI also provides Java enumerated types which make it easier to
// deal with coded values. This code achieves the exact same result
// as the code above.
patient.setGender(Enumerations.AdministrativeGender.MALE);

// You can also retrieve coded values the same way
String genderString = patient.getGenderElement().getValueAsString();
Enumerations.AdministrativeGender genderEnum =
      patient.getGenderElement().getValue();

```

Copy
#  2.0.3Convenience Methods
[ ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#convenience-methods)
The FHIR data model is rich enough to meet common use cases, but sometimes that richness adds complexity. For example, a Patient may have multiple names (a preferred name, a nickname, etc.) and each of those names may have multiple last names, multiple prefixes, etc.
The example below shows populating a name entry for a Patient. Note the use of the StringDt type, which encapsulates a regular String, but allows for extensions to be added.
```
Patient patient = new Patient();
HumanName name = patient.addName();
name.setFamily("Smith");
StringType firstName = name.addGivenElement();
firstName.setValue("Rob");
StringType secondName = name.addGivenElement();
secondName.setValue("Bruce");

```

Copy
HAPI also provides for simple setters that use Java primitive types and can be chained, leading to much simpler code.
```
Patient patient = new Patient();
patient.addName().setFamily("Smith").addGiven("Rob").addGiven("Bruce");

```

Copy
#  2.0.4Examples
[ ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#examples)
##  2.0.4.1Populating an Observation Resource[](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html#populating-an-observation-resource)
The following example shows how to create an observation resource containing a numeric datatype.
```
// Create an Observation instance
Observation observation = new Observation();

// Give the observation a status
observation.setStatus(Observation.ObservationStatus.FINAL);

// Give the observation a code (what kind of observation is this)
Coding coding = observation.getCode().addCoding();
coding.setCode("29463-7").setSystem("http://loinc.org").setDisplay("Body Weight");

// Create a quantity datatype
Quantity value = new Quantity();
value.setValue(83.9).setSystem("http://unitsofmeasure.org").setCode("kg");
observation.setValue(value);

// Set the reference range
SimpleQuantity low = new SimpleQuantity();
low.setValue(45).setSystem("http://unitsofmeasure.org").setCode("kg");
observation.getReferenceRangeFirstRep().setLow(low);
SimpleQuantity high = new SimpleQuantity();
low.setValue(90).setSystem("http://unitsofmeasure.org").setCode("kg");
observation.getReferenceRangeFirstRep().setHigh(high);

```

Copy
2.0 Working With Resources 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.1 Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)