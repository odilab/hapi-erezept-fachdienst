---
source: https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html
crawled: 2025-08-01T14:05:21.891280
---

# Getting Started Introduction

#  1.0.1Introduction to HAPI FHIR
[ ](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#introduction-to-hapi-fhir)
The HAPI FHIR library is an implementation of the [HL7 FHIR specification](http://hl7.org/fhir/) for Java. Explaining what FHIR is would be beyond the scope of this documentation, so if you have not previously worked with FHIR, the specification is a good place to start. This is often not actually the case when discussing messaging protocols, but in this case it is so: The FHIR specification is designed to be readable and implementable, and is filled with good information.
Part of the key to why FHIR is a good specification is the fact that its design is based on the design of other successful APIs (in particular, the FHIR designers often reference the Highrise API as a key influence in the design of the spec.)
HAPI FHIR is based on the same principle, but applied to the Java implementation: We have based the design of this API on the JAXB and JAX-WS APIs, which we consider to be very well thought-out, and very usable APIs. This does **not** mean that HAPI-FHIR actually uses these two APIs however, or that HAPI-FHIR is in any way compliant with JAXB ([JSR222](https://jcp.org/en/jsr/detail?id=222)) or JAX-WS ([JSR224](https://jcp.org/en/jsr/detail?id=222)), only that we have tried to emulate the easy-to-use, but flexible design of these specifications.
#  1.0.2Getting Started
[ ](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#getting-started)
To get started with HAPI FHIR, first download a copy and add it to your project. See [Downloading and Importing](https://hapifhir.io/hapi-fhir/docs/getting_started/downloading_and_importing.html) for instructions.
##  1.0.2.1A Note on FHIR Versions[](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#a-note-on-fhir-versions)
Before discussing HAPI itself, a quick word about FHIR versions. FHIR is not yet a finalized "1.0" standard. It is currently in the DSTU phase, which means that it is changing in subtle and non-subtle ways between releases. Before trying to use FHIR, you will need to determine which version of FHIR you want to support in your application. Typically this would be the latest version, but if you are looking to interact with an application which already exists, you will probably want to implement the same version implemented by that application.
##  1.0.2.2Introducing the FHIR Context[](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#introducing-the-fhir-context)
HAPI defines model classes for every resource type and datatype defined by the FHIR specification. For example, here is the [Patient](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Patient.html) resource specification. If you browse the JavaDoc you will see getters and setters for the various properties that make up a Patient resource.
We will come back to how to interact with these objects in a moment, but first we need to create a [FhirContext](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html). FhirContext is the starting point to using HAPI, and acts as a factory for most other parts of the API as well as a runtime cache of information that HAPI needs to operate. Users of the JAXB API may find this class to be similar in purpose to the [JAXBContext](http://docs.oracle.com/javaee/5/api/javax/xml/bind/JAXBContext.html) class from that API.
Creating a FhirContext is as simple as instantiating one. A FhirContext instance is specific to a given version of the FHIR specification, so it is recommended that you use one of the factory methods indicating the FHIR version you wish to support in your application, as shown in the following snippet:
```
// Create a context for DSTU2
FhirContext ctxDstu2 = FhirContext.forDstu2();

// Alternately, create a context for R4
FhirContext ctxR4 = FhirContext.forR4();

```

Copy
##  1.0.2.3Parsing a resource from a String[](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#parsing-a-resource-from-a-string)
This [Parser instance](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParser.html) can then be used to parse messages. Note that you may use the context to create as many parsers are you want.
**Performance tip:** The FhirContext is an expensive object to create, so you should try to create it once and keep it around during the life of your application. Parsers, on the other hand, are very lightweight and do not need to be reused.
```
// The following is an example Patient resource
String msgString = "<Patient xmlns=\"http://hl7.org/fhir\">"
      + "<text><status value=\"generated\" /><div xmlns=\"http://www.w3.org/1999/xhtml\">John Cardinal</div></text>"
      + "<identifier><system value=\"http://orionhealth.com/mrn\" /><value value=\"PRP1660\" /></identifier>"
      + "<name><use value=\"official\" /><family value=\"Cardinal\" /><given value=\"John\" /></name>"
      + "<gender><coding><system value=\"http://hl7.org/fhir/v3/AdministrativeGender\" /><code value=\"M\" /></coding></gender>"
      + "<address><use value=\"home\" /><line value=\"2222 Home Street\" /></address><active value=\"true\" />"
      + "</Patient>";

// The hapi context object is used to create a new XML parser
// instance. The parser can then be used to parse (or unmarshall) the
// string message into a Patient object
IParser parser = ctx.newXmlParser();
Patient patient = parser.parseResource(Patient.class, msgString);

// The patient object has accessor methods to retrieve all of the
// data which has been parsed into the instance.
String patientId = patient.getIdentifier().get(0).getValue();
String familyName = patient.getName().get(0).getFamily();
Enumerations.AdministrativeGender gender = patient.getGender();

System.out.println(patientId); // PRP1660
System.out.println(familyName); // Cardinal
System.out.println(gender.toCode()); // male

```

Copy
##  1.0.2.4Encoding a Resource to a String[](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#encoding-a-resource-to-a-string)
The parser can also be used to encode a resource (which you can populate with your own values) just as easily.
```
/**
 * FHIR model types in HAPI are simple POJOs. To create a new
 * one, invoke the default constructor and then
 * start populating values.
 */
Patient patient = new Patient();

// Add an MRN (a patient identifier)
Identifier id = patient.addIdentifier();
id.setSystem("http://example.com/fictitious-mrns");
id.setValue("MRN001");

// Add a name
HumanName name = patient.addName();
name.setUse(HumanName.NameUse.OFFICIAL);
name.setFamily("Tester");
name.addGiven("John");
name.addGiven("Q");

// We can now use a parser to encode this resource into a string.
String encoded = ctx.newXmlParser().encodeResourceToString(patient);
System.out.println(encoded);

```

Copy
This code gives the following output:
```
<Patient xmlns="http://hl7.org/fhir">
   <identifier>
      <system value="http://example.com/fictitious-mrns"/>
      <value value="MRN001"/>
   </identifier>
   <name>
      <use value="official"/>
      <family value="Tester"/>
      <given value="John"/>
      <given value="Q"/>
   </name>
</Patient>

```

Copy
##  1.0.2.5Fluent Programming[](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#fluent-programming)
Much of the HAPI FHIR API is designed using a fluent style, where method calls can be chained in a natural way. This leads to tighter and easier-to-read code.
The following snippet is functionally identical to the example above:
```
Patient patient = new Patient();
patient.addIdentifier().setSystem("http://example.com/fictitious-mrns").setValue("MRN001");
patient.addName()
      .setUse(HumanName.NameUse.OFFICIAL)
      .setFamily("Tester")
      .addGiven("John")
      .addGiven("Q");

encoded = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
System.out.println(encoded);

```

Copy
#  1.0.3JSON Encoding
[ ](https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html#json-encoding)
JSON parsing/encoding is also supported.
```
IParser jsonParser = ctx.newJsonParser();
jsonParser.setPrettyPrint(true);
encoded = jsonParser.encodeResourceToString(patient);
System.out.println(encoded);

```

Copy
This code gives the following output:
```
{
    "resourceType":"Patient",
    "identifier":[
        {
            "system":"http://example.com/fictitious-mrns",
            "value":"MRN001"
        }
    ],
    "name":[
        {
            "use":"official",
            "family":[
                "Tester"
            ],
            "given":[
                "John",
                "Q"
            ]
        }
    ]
}

```

Copy
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)