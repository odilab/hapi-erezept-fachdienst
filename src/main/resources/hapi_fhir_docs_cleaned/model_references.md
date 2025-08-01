---
source: https://hapifhir.io/hapi-fhir/docs/model/references.html
crawled: 2025-08-01T14:05:50.486378
---

# Model References

#  2.2.1Resource References
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#resource-references)
Resource references are a key part of the HAPI FHIR model, since almost any resource will have references to other resources within it.
The [Reference](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Reference.html) type is the datatype for references. This datatype has a number of properties which help make working with FHIR simple.
The `getReference()` method returns a String that contains the identity of the resource being referenced. This is the item which is most commonly populated when interacting with FHIR. For example, consider the following Patient resource, which contains a reference to an Organization resource:
```
{
   "resourceType": "Patient",
   "identifier": [{
      "system": "http://example.com/identifiers",
      "value": "12345"
   }],
   "managingOrganization": {
       "reference": "Organization/123"
   }
}

```

Copy
Given a Patient resource obtained by invoking a client operation, a call to `String reference = patient.getManagingOrganization().getReference();` returns a String containing `Organization/112`.
Reference also has a place for storing actual resource instances (i.e. an actual [IBaseResource](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/org/hl7/fhir/instance/model/api/IBaseResource.html) instance), and this can be very useful as shown below.
#  2.2.2References in Client Code
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#references-in-client-code)
In client code, if a resource reference refers to a resource which was received as a part of the same response, `getResource()` will be populated with the actual resource. This can happen because either the resource was received as a contained resource, or the resource was received as a separate resource in a bundle.
#  2.2.3References in Server Code
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#references-in-server-code)
In server code, you will often want to return a resource which contains a link to another resource. Generally these "linked" resources are not actually included in the response, but rather a link to the resource is included and the client may request that resource directly (by ID) if it is needed.
The following example shows a Patient resource being created which will have a link to its managing organization when encoded from a server:
```
Patient patient = new Patient();
patient.setId("Patient/1333");
patient.addIdentifier("urn:mrns", "253345");
patient.getManagingOrganization().setReference("Organization/124362");

```

Copy
##  2.2.3.1Handling Includes (_include) in a Bundle[](https://hapifhir.io/hapi-fhir/docs/model/references.html#handling-includes-include-in-a-bundle)
Your server code may also wish to add additional resource to a bundle being returned (e.g. because of an _include directive in the client's request).
To do this, you can implement your server method to simply return List and then simply add your extra resources to the list. Another technique however, is to populate the reference as shown in the example below, but ensure that the referenced resource has an ID set.
In the following example, the Organization resource has an ID set, so it will not be contained but will rather appear as a distinct entry in any returned bundles. Both resources are added to a bundle, which will then have two entries:
```
@Search
private List<IBaseResource> searchForPatients() {
   // Create an organization
   Organization org = new Organization();
   org.setId("Organization/65546");
   org.setName("Test Organization");

   // Create a patient
   Patient patient = new Patient();
   patient.setId("Patient/1333");
   patient.addIdentifier().setSystem("urn:mrns").setValue("253345");
   patient.getManagingOrganization().setResource(org);

   // Here we return only the patient object, which has links to other resources
   List<IBaseResource> retVal = new ArrayList<IBaseResource>();
   retVal.add(patient);
   return retVal;
}

```

Copy
This will give the following output:
```
<Bundle xmlns="http://hl7.org/fhir">
    <id value="4e151274-2b19-4930-97f2-8427167a176c"/>
    <type value="searchset"/>
    <total value="1"/>
    <link>
        <relation value="fhir-base"/>
        <url value="http://example.com/base"/>
    </link>
    <link>
        <relation value="self"/>
        <url value="http://example.com/base/Patient"/>
    </link>
    <entry>
        <resource>
            <Patient xmlns="http://hl7.org/fhir">
                <id value="1333"/>
                <identifier>
                    <system value="urn:mrns"/>
                    <value value="253345"/>
                </identifier>
                <managingOrganization>
                    <reference value="Organization/65546"/>
                </managingOrganization>
            </Patient>
        </resource>
    </entry>
    <entry>
        <resource>
            <Organization xmlns="http://hl7.org/fhir">
                <id value="65546"/>
                <name value="Test Organization"/>
            </Organization>
        </resource>
        <search>
            <mode value="include"/>
        </search>
    </entry>
</Bundle>

```

Copy
#  2.2.4Contained Resources
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#contained)
The FHIR specification uses a feature called "containing" to nest one resource inside another resource. This is described [here](https://hl7.org/fhir/references.html#contained).
This method is useful in cases where you do not have enough information available in order to uniquely identify a referenced resource. For example, suppose you know the name of the General Practitioner for a patient you want to store, but you do not have any unique identifiers. Adding a local contained Practitioner resource allows you to store the details you do know about that practitioner, without creating a separate resource.
Containing resources should always be treated as a last resort; if you know enough information in order to create a standalone resource that is always preferable. However, contained resources are a useful tool in the right situation.
```
// Create a practitioner resource, and give it a local ID. This ID must be
// unique within the containing resource, but does not need to be otherwise
// unique.
Practitioner pract = new Practitioner();
pract.setId("my-practitioner");
pract.addName().setFamily("Smith").addGiven("Juanita");
pract.addTelecom().setValue("+1 (289) 555-1234");

// Create a patient
Patient patient = new Patient();
patient.setId("Patient/1333");
patient.addIdentifier().setSystem("http://example.com/mrns").setValue("253345");

// Set the reference, and manually add the contained resource
patient.addGeneralPractitioner(new Reference("#my-practitioner"));
patient.getContained().add(pract);

String encoded =
      FhirContext.forR4Cached().newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
System.out.println(encoded);

```

Copy
This snippet produces the following output:
```
{
  "resourceType": "Patient",
  "id": "1333",
  "contained": [ {
    "resourceType": "Practitioner",
    "id": "my-practitioner",
    "name": [ {
      "family": "Smith",
      "given": [ "Juanita" ]
    } ],
    "telecom": [ {
      "value": "+1 (289) 555-1234"
    } ]
  } ],
  "identifier": [ {
    "system": "http://example.com/mrns",
    "value": "253345"
  } ],
  "generalPractitioner": [ {
    "reference": "#my-practitioner"
  } ]
}

```

Copy
It is also possible to lew HAPI FHIR handle containing automatically, by putting the target resource directly into the reference as shown below. In this case, HAPI itself will define a local reference ID (e.g. `#1`).
Note that in this case, HAPI's parser will automatically modify the reference and the contained resource to contain a local ID. This automatic modification of the resource being serialized can be confusing.
```
// Create an organization, note that the organization does not have an ID
Organization org = new Organization();
org.getName().setValue("Contained Test Organization");

// Create a patient
Patient patient = new Patient();
patient.setId("Patient/1333");
patient.addIdentifier("urn:mrns", "253345");

// Put the organization as a reference in the patient resource
patient.getManagingOrganization().setResource(org);

String encoded = ourCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(patient);
System.out.println(encoded);

```

Copy
This will give the following output:
```
<Patient xmlns="http://hl7.org/fhir">
    <contained>
        <Organization xmlns="http://hl7.org/fhir" id="1">
            <name value="Contained Test Organization"/>
        </Organization>
    </contained>
    <identifier>
        <system value="urn:mrns"/>
        <value value="253345"/>
    </identifier>
    <managingOrganization>
        <reference value="#1"/>
    </managingOrganization>
</Patient>

```

Copy
#  2.2.5Versioned References
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#versioned-references)
By default, HAPI will strip resource versions from references between resources. For example, if you set a reference to `Patient.managingOrganization` to the value `Patient/123/_history/2`, HAPI will encode this reference as `Patient/123`.
This is because in most circumstances, references between resources should be versionless (e.g. the reference just points to the latest version, whatever version that might be).
There are valid circumstances however for wanting versioned references. If you need HAPI to emit versioned references, you have a few options:
You can force the parser to never strip versions:
```
FhirContext ctx = FhirContext.forR4();
IParser parser = ctx.newJsonParser();

// Disable the automatic stripping of versions from references on the parser
parser.setStripVersionsFromReferences(false);

```

Copy
You can also disable this behaviour entirely on the context (so that it will apply to all parsers):
```
ctx.getParserOptions().setStripVersionsFromReferences(false);

```

Copy
You can also configure HAPI to not strip versions only on certain fields. This is desirable if you want versionless references in most places but need them in some places:
```
FhirContext ctx = FhirContext.forR4();
IParser parser = ctx.newJsonParser();

// Preserve versions only on these two fields (for the given parser)
parser.setDontStripVersionsFromReferencesAtPaths(
      "AuditEvent.entity.reference", "Patient.managingOrganization");

// You can also apply this setting to the context so that it will
// flow to all parsers
ctx.getParserOptions()
      .setDontStripVersionsFromReferencesAtPaths(
            "AuditEvent.entity.reference", "Patient.managingOrganization");

```

Copy
#  2.2.6Automatically Versioned References
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html#automatically-versioned-references)
It is possible to configure HAPI to automatically version references for desired resource instances by providing the `auto-version-references-at-path` extension in the `Resource.meta` element:
```
"meta": {
   "extension":[
      {
         "url":"http://hapifhir.io/fhir/StructureDefinition/auto-version-references-at-path",
         "valueString":"focus"
      }
   ]
}

```

Copy
It is allowed to add multiple extensions with different paths. When a resource is stored, any references found at the specified paths will have the current version of the target appended, if a version is not already present.
Parser will not strip versions from references at paths provided by the `auto-version-references-at-path` extension.
[ ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
2.2 Resource References 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.3 Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)