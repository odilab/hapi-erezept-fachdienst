---
source: https://hapifhir.io/hapi-fhir/docs/model/converter.html
crawled: 2025-08-01T14:07:28.379847
---

# Model Converter

#  2.4.1HL7 FHIR Converter
[ ](https://hapifhir.io/hapi-fhir/docs/model/converter.html#hl7-fhir-converter)
Beginning in HAPI FHIR 2.3, a new module called `hapi-fhir-converter` has been added to the project. This is an **experimental feature** so use it with caution!
This feature allows automated conversion from earlier versions of the FHIR structures to a later version.
The following page shows some basic examples. Please get in touch if you are able to contribute better examples!
##  2.4.1.1Importing the Module[](https://hapifhir.io/hapi-fhir/docs/model/converter.html#importing-the-module)
To use the `hapi-fhir-converter` module, import the following dependency into your project pom.xml (or equivalent)
```
<dependency>
	<groupId>ca.uhn.hapi.fhir</groupId>
	<artifactId>hapi-fhir-converter</artifactId>
	<version>${project.version}</version>
</dependency>

```

Copy
##  2.4.1.2Converting from DSTU2 to DSTU3[](https://hapifhir.io/hapi-fhir/docs/model/converter.html#converting-from-dstu2-to-dstu3)
The following example shows a conversion from a `hapi-fhir-structures-hl7org-dstu2` structure to a `hapi-fhir-structures-dstu3` structure.
```
// Create an input resource to convert
org.hl7.fhir.dstu2.model.Observation input = new org.hl7.fhir.dstu2.model.Observation();
input.setEncounter(new org.hl7.fhir.dstu2.model.Reference("Encounter/123"));

// Convert the resource
org.hl7.fhir.dstu3.model.Observation output =
      (Observation) VersionConvertorFactory_10_30.convertResource(input);
String context = output.getContext().getReference();

```

Copy
##  2.4.1.3Converting from DSTU2.1 to DSTU3[](https://hapifhir.io/hapi-fhir/docs/model/converter.html#converting-from-dstu21-to-dstu3)
The following example shows a conversion from a `hapi-fhir-structures-dstu2.1` structure to a `hapi-fhir-structures-dstu3` structure.
```
// Create a resource to convert
org.hl7.fhir.dstu2016may.model.Questionnaire input = new org.hl7.fhir.dstu2016may.model.Questionnaire();
input.setTitle("My title");

// Convert the resource
org.hl7.fhir.dstu3.model.Questionnaire output =
      (Questionnaire) VersionConvertorFactory_14_30.convertResource(input);
String context = output.getTitle();

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
2.4 Version Converters 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.5 Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)