---
source: https://hapifhir.io/hapi-fhir/docs/client/examples.html
crawled: 2025-08-01T14:06:02.911208
---

# Client Examples

#  3.5.1Client Examples
[ ](https://hapifhir.io/hapi-fhir/docs/client/examples.html#client-examples)
This page contains examples of how to use the client to perform complete tasks. If you have an example you could contribute, we'd love to hear from you!
#  3.5.2Transaction With Conditional Create
[ ](https://hapifhir.io/hapi-fhir/docs/client/examples.html#transaction-with-conditional-create)
The following example demonstrates a common scenario: How to create a new piece of data for a Patient (in this case, an Observation) where the identifier of the Patient is known, but the ID is not.
In this scenario, we want to look up the Patient record and reference it from the newly created Observation. In the event that no Patient record already exists with the given identifier, a new one will be created and the Observation will reference it. This is known in FHIR as a [Conditional Create](http://hl7.org/fhir/http.html#ccreate).
**JSON** :
```
{
  "resourceType": "Bundle",
  "type": "transaction",
  "entry": [ {
    "fullUrl": "urn:uuid:3bc44de3-069d-442d-829b-f3ef68cae371",
    "resource": {
      "resourceType": "Patient",
      "identifier": [ {
        "system": "http://acme.org/mrns",
        "value": "12345"
      } ],
      "name": [ {
        "family": "Jameson",
        "given": [ "J", "Jonah" ]
      } ],
      "gender": "male"
    },
    "request": {
      "method": "POST",
      "url": "Patient",
      "ifNoneExist": "identifier=http://acme.org/mrns|12345"
    }
  }, {
    "resource": {
      "resourceType": "Observation",
      "status": "final",
      "code": {
        "coding": [ {
          "system": "http://loinc.org",
          "code": "789-8",
          "display": "Erythrocytes [#/volume] in Blood by Automated count"
        } ]
      },
      "subject": {
        "reference": "urn:uuid:3bc44de3-069d-442d-829b-f3ef68cae371"
      },
      "valueQuantity": {
        "value": 4.12,
        "unit": "10 trillion/L",
        "system": "http://unitsofmeasure.org",
        "code": "10*12/L"
      }
    },
    "request": {
      "method": "POST",
      "url": "Observation"
    }
  } ]
}

```

Copy
**XML** :
```
<Bundle xmlns="http://hl7.org/fhir">
   <type value="transaction"/>
   <entry>
      <fullUrl value="urn:uuid:47709cc7-b3ec-4abc-9d26-3df3d3d57907"/>
      <resource>
         <Patient xmlns="http://hl7.org/fhir">
            <identifier>
               <system value="http://acme.org/mrns"/>
               <value value="12345"/>
            </identifier>
            <name>
               <family value="Jameson"/>
               <given value="J"/>
               <given value="Jonah"/>
            </name>
            <gender value="male"/>
         </Patient>
      </resource>
      <request>
         <method value="POST"/>
         <url value="Patient"/>
         <ifNoneExist value="identifier=http://acme.org/mrns|12345"/>
      </request>
   </entry>
   <entry>
      <resource>
         <Observation xmlns="http://hl7.org/fhir">
            <status value="final"/>
            <code>
               <coding>
                  <system value="http://loinc.org"/>
                  <code value="789-8"/>
                  <display value="Erythrocytes [#/volume] in Blood by Automated count"/>
               </coding>
            </code>
            <subject>
               <reference value="urn:uuid:47709cc7-b3ec-4abc-9d26-3df3d3d57907"/>
            </subject>
            <valueQuantity>
               <value value="4.12"/>
               <unit value="10 trillion/L"/>
               <system value="http://unitsofmeasure.org"/>
               <code value="10*12/L"/>
            </valueQuantity>
         </Observation>
      </resource>
      <request>
         <method value="POST"/>
         <url value="Observation"/>
      </request>
   </entry>
</Bundle>

```

Copy
The server responds with the following response. Note that the ID of the already existing patient is returned, and the ID of the newly created Observation is too.
```
<Bundle xmlns="http://hl7.org/fhir">
   <id value="dd1f75b8-e472-481e-97b3-c5eebb99a5e0"/>
   <type value="transaction-response"/>
   <link>
      <relation value="self"/>
      <url value="http://fhirtest.uhn.ca/baseDstu2"/>
   </link>
   <entry>
      <response>
         <status value="200 OK"/>
         <location value="Patient/966810/_history/1"/>
         <etag value="1"/>
         <lastModified value="2015-10-29T07:25:42.465-04:00"/>
      </response>
   </entry>
   <entry>
      <response>
         <status value="201 Created"/>
         <location value="Observation/966828/_history/1"/>
         <etag value="1"/>
         <lastModified value="2015-10-29T07:33:28.047-04:00"/>
      </response>
   </entry>
</Bundle>

```

Copy
To produce this transaction in Java code:
```
// Create a patient object
Patient patient = new Patient();
patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("12345");
patient.addName().setFamily("Jameson").addGiven("J").addGiven("Jonah");
patient.setGender(Enumerations.AdministrativeGender.MALE);

// Give the patient a temporary UUID so that other resources in
// the transaction can refer to it
patient.setId(IdType.newRandomUuid());

// Create an observation object
Observation observation = new Observation();
observation.setStatus(Observation.ObservationStatus.FINAL);
observation
      .getCode()
      .addCoding()
      .setSystem("http://loinc.org")
      .setCode("789-8")
      .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
observation.setValue(new Quantity()
      .setValue(4.12)
      .setUnit("10 trillion/L")
      .setSystem("http://unitsofmeasure.org")
      .setCode("10*12/L"));

// The observation refers to the patient using the ID, which is already
// set to a temporary UUID
observation.setSubject(new Reference(patient.getIdElement().getValue()));

// Create a bundle that will be used as a transaction
Bundle bundle = new Bundle();
bundle.setType(Bundle.BundleType.TRANSACTION);

// Add the patient as an entry. This entry is a POST with an
// If-None-Exist header (conditional create) meaning that it
// will only be created if there isn't already a Patient with
// the identifier 12345
bundle.addEntry()
      .setFullUrl(patient.getIdElement().getValue())
      .setResource(patient)
      .getRequest()
      .setUrl("Patient")
      .setIfNoneExist("identifier=http://acme.org/mrns|12345")
      .setMethod(Bundle.HTTPVerb.POST);

// Add the observation. This entry is a POST with no header
// (normal create) meaning that it will be created even if
// a similar resource already exists.
bundle.addEntry()
      .setResource(observation)
      .getRequest()
      .setUrl("Observation")
      .setMethod(Bundle.HTTPVerb.POST);

// Log the request
FhirContext ctx = FhirContext.forR4();
System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));

// Create a client and post the transaction to the server
IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
Bundle resp = client.transaction().withBundle(bundle).execute();

// Log the response
System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));

```

Copy
#  3.5.3Fetch all Pages of a Bundle
[ ](https://hapifhir.io/hapi-fhir/docs/client/examples.html#fetch-all-pages-of-a-bundle)
This following example shows how to load all pages of a bundle by fetching each page one-after-the-other and then joining the results.
```
// Create a context and a client
FhirContext ctx = FhirContext.forR4();
String serverBase = "http://hapi.fhir.org/baseR4";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

// We'll populate this list
List<IBaseResource> patients = new ArrayList<>();

// We'll do a search for all Patients and extract the first page
Bundle bundle = client.search()
      .forResource(Patient.class)
      .where(Patient.NAME.matches().value("smith"))
      .returnBundle(Bundle.class)
      .execute();
patients.addAll(BundleUtil.toListOfResources(ctx, bundle));

// Load the subsequent pages
while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
   bundle = client.loadPage().next(bundle).execute();
   patients.addAll(BundleUtil.toListOfResources(ctx, bundle));
}

System.out.println("Loaded " + patients.size() + " patients!");

```

Copy
#  3.5.4Create Composition and Generate Document
[ ](https://hapifhir.io/hapi-fhir/docs/client/examples.html#create-composition-and-generate-document)
This example shows how to generate a Composition resource with two linked resources, then apply the server `$document` operation to generate a document based on this composition.
```
FhirContext ctx = FhirContext.forR4();
IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

Patient patient = new Patient();
patient.setId("PATIENT-ABC");
patient.setActive(true);
client.update().resource(patient).execute();

Observation observation = new Observation();
observation.setId("OBSERVATION-ABC");
observation.setSubject(new Reference("Patient/PATIENT-ABC"));
observation.setStatus(Observation.ObservationStatus.FINAL);
client.update().resource(observation).execute();

Composition composition = new Composition();
composition.setId("COMPOSITION-ABC");
composition.setSubject(new Reference("Patient/PATIENT-ABC"));
composition.addSection().setFocus(new Reference("Observation/OBSERVATION-ABC"));
client.update().resource(composition).execute();

Bundle document = client.operation()
      .onInstance("Composition/COMPOSITION-ABC")
      .named("$document")
      .withNoParameters(Parameters.class)
      .returnResourceType(Bundle.class)
      .execute();

ourLog.debug(
      "Document bundle: {}", ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(document));

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
3.5 Client Examples 
* Client 
* [ 3.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html)
* [ 3.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* [ 3.2  Generic (Fluent) Client ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
* [ 3.3  Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* [ 3.4  Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* [ 3.5  Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)