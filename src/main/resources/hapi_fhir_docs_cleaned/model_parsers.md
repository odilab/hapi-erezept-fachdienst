---
source: https://hapifhir.io/hapi-fhir/docs/model/parsers.html
crawled: 2025-08-01T14:08:03.683680
---

# Model Parsers

#  2.1.1Parsers and Serializers
[ ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#parsers-and-serializers)
HAPI FHIR has built-in support for the FHIR [JSON](http://hl7.org/fhir/json.html) and [XML](http://hl7.org/fhir/json.html) encoding formats.
A built in parser can be used to convert HAPI FHIR Java objects into a serialized form, and to parse serialized data into Java objects. Note that unlike some other frameworks, HAPI FHIR does not have separate parsers and serializers. Both of these functions are handled by a single object called the **Parser**.
#  2.1.2Parsing (aka Deserializing)
[ ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#parsing-aka-deserializing)
As with many parts of the HAPI FHIR API, parsing begins with a [FhirContext](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html) object. The FhirContext can be used to request an [IParser](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParser.html) for your chosen encoding style that is then used to parse.
```
// Create a FHIR context
FhirContext ctx = FhirContext.forR4();

// The following example is a simple serialized Patient resource to parse
String input = "{" + "\"resourceType\" : \"Patient\","
      + "  \"name\" : [{"
      + "    \"family\": \"Simpson\""
      + "  }]"
      + "}";

// Instantiate a new parser
IParser parser = ctx.newJsonParser();

// Parse it
Patient parsed = parser.parseResource(Patient.class, input);
System.out.println(parsed.getName().get(0).getFamily());

```

Copy
#  2.1.3Encoding (aka Serializing)
[ ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#encoding-aka-serializing)
As with many parts of the HAPI FHIR API, parsing begins with a [FhirContext](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html) object. The FhirContext can be used to request an [IParser](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParser.html) for your chosen encoding style that is then used to serialize.
The following example shows a JSON Parser being used to serialize a FHIR resource.
```
// Create a FHIR context
FhirContext ctx = FhirContext.forR4();

// Create a Patient resource to serialize
Patient patient = new Patient();
patient.addName().setFamily("Simpson").addGiven("James");

// Instantiate a new JSON parser
IParser parser = ctx.newJsonParser();

// Serialize it
String serialized = parser.encodeResourceToString(patient);
System.out.println(serialized);

// Using XML instead
serialized = ctx.newXmlParser().encodeResourceToString(patient);
System.out.println(serialized);

```

Copy
##  2.1.3.1Pretty Printing[](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#pretty-printing)
By default, the parser will output in condensed form, with no newlines or indenting. This is good for machine-to-machine communication since it reduces the amount of data to be transferred but it is harder to read. To enable pretty printed output:
When using the [HAPI FHIR Server](https://hapifhir.io/hapi-fhir/docs/server_plain/), pretty printing can be requested by adding the parameter `_pretty=true` to the request.
```
// Create a parser
IParser parser = ctx.newJsonParser();

// Indent the output
parser.setPrettyPrint(true);

// Serialize it
String serialized = parser.encodeResourceToString(patient);
System.out.println(serialized);

// You can also chain these statements together
ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);

```

Copy
##  2.1.3.2Encoding Configuration[](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#encoding-configuration)
There are plenty of other options too, that can be used to control the output by the parser. A few examples are shown below. See the [IParser](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParser.html) JavaDoc for more information.
```
// Create a parser
IParser parser = ctx.newJsonParser();

// Blacklist certain fields from being encoded
parser.setDontEncodeElements(Sets.newHashSet("Patient.identifier", "Patient.active"));

// Don't include resource narratives
parser.setSuppressNarratives(true);

// Use versioned references for these reference elements
parser.setDontStripVersionsFromReferencesAtPaths("Patient.organization");

// Serialize it
String serialized = parser.encodeResourceToString(patient);
System.out.println(serialized);

```

Copy
##  2.1.3.3Summary Mode[](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#summary-mode)
For each resource type, the FHIR specification defines a collection of elements which are considered "summary elements". These are marked on the individual resource views using a Sigma (Σ) symbol next to the element names. See the [Patient Resource Definition](https://hl7.org/fhir/patient.html) for an example, looking for this symbol on the page.
If the parser is configured as shown below, only the summary mode elements will be included in the encoded resource.
When using the [HAPI FHIR Server](https://hapifhir.io/hapi-fhir/docs/server_plain/), summary mode can be requested by adding the parameter `_summary=true` to the request.
```
// Create a parser
IParser parser = ctx.newJsonParser();

// Instruct the parser to only include summary elements
parser.setSummaryMode(true);

// If you need to, you can instruct the parser to override
// the default summary elements by adding and/or removing
// elements from the list of elements it will include. This
// is typically not needed, but it's shown here in case you
// need to do this:
// Include a non-summary element in the summary view.
parser.setEncodeElements("Patient.maritalStatus");
// Exclude a summary element even though it would normally
// be included.
parser.setDontEncodeElements("Patient.name");

// Serialize it
String serialized = parser.encodeResourceToString(patient);
System.out.println(serialized);

```

Copy
#  2.1.4Global Parser Configuration
[ ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html#parser-options)
It is possible to configure a number of parser settings globally for a given FhirContext, meaning that they will apply to all parsers that are created by that context. This is especially useful for [HAPI FHIR Clients](https://hapifhir.io/hapi-fhir/docs/client/) and [HAPI FHIR Servers](https://hapifhir.io/hapi-fhir/docs/server_plain/), where parsers are created by the client/server internally using the given FhirContext.
```
FhirContext ctx = FhirContext.forR4();

// Request the ParserOptions, which store global config
// settings applied to all parsers coming from the given
// context.
ParserOptions parserOptions = ctx.getParserOptions();

// Never strip resource reference versions for the following
// paths
parserOptions.setDontStripVersionsFromReferencesAtPaths(
      "AuditEvent.entity.reference", "Patient.managingOrganization");

// Never strip any resource reference versions (setting this
// to false would make the setting above redundant since this
// setting applies to all paths)
parserOptions.setStripVersionsFromReferences(false);

// Even in summary mode, always include extensions on the
// root of Patient resources.
parserOptions.setEncodeElementsForSummaryMode("Patient.extension");

// Create a parser and encode, with the global config applied.
IParser parser = ctx.newJsonParser();
String encoded = parser.encodeResourceToString(patient);

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
2.1 Parsing and Serializing 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.2 Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)