---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html
crawled: 2025-08-01T14:05:01.114242
---

# Server Plain Rest Operations

#  4.4.1REST Operations: Overview
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#rest-operations-overview)
This page shows the operations which can be implemented on HAPI [Plain Server](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html), as well as on the [Annotation Client](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html). Most of the examples shown here show how to implement a server method, but to perform an equivalent call on an annotation client you simply put a method with the same signature in your client interface.
#  4.4.2Instance Level - Read
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_read)
The [read](http://hl7.org/fhir/http.html#read) operation retrieves a resource by ID. It is annotated with the [@Read](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Read.html) annotation, and has at least a single parameter annotated with the [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation.
```
@Read()
public Patient getResourceById(@IdParam IdType theId) {
   Patient retVal = new Patient();

   // ...populate...
   retVal.addIdentifier().setSystem("urn:mrns").setValue("12345");
   retVal.addName().setFamily("Smith").addGiven("Tester").addGiven("Q");
   // ...etc...

   // if you know the version ID of the resource, you should set it and HAPI will
   // include it in a Content-Location header
   retVal.setId(new IdType("Patient", "123", "2"));

   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient/111](http://fhir.example.com/Patient/111)
The following snippet shows how to define a client interface to handle a read method.
```
private interface IPatientClient extends IBasicClient {
   /** Read a patient from a server by ID */
   @Read
   Patient readPatient(@IdParam IdType theId);

   // Only one method is shown here, but many methods may be
   // added to the same client interface!
}

```

Copy
#  4.4.3Instance Level - VRead
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_vread)
The **[vread](http://hl7.org/implement/standards/fhir/http.html#vread)** operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your [@Read](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Read.html) annotation. This means that the read method will support both "Read" and "VRead". The IdType instance passed into your method may or may not have the version populated depending on the client's request.
```
@Read(version = true)
public Patient readOrVread(@IdParam IdType theId) {
   Patient retVal = new Patient();

   if (theId.hasVersionIdPart()) {
      // this is a vread
   } else {
      // this is a read
   }

   // ...populate...

   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient/111/_history/2](http://fhir.example.com/Patient/111/_history/2)
#  4.4.4Instance Level - Update
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_update)
The **[update](http://hl7.org/implement/standards/fhir/http.html#update)** operation updates a specific resource instance (using its ID), and optionally accepts a version ID as well (which can be used to detect version conflicts).
Update methods must be annotated with the [@Update](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Update.html) annotation, and have a parameter annotated with the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) annotation. This parameter contains the resource instance to be created. See the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) for information on the types allowed for this parameter (resource types, String, byte[]).
In addition, the method may optionally have a parameter annotated with the [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation, or they may obtain the ID of the resource being updated from the resource itself. Either way, this ID comes from the URL passed in.
Update methods must return an object of type [MethodOutcome](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/api/MethodOutcome.html). This object contains the identity of the created resource.
The following snippet shows how to define an update method on a server:
```
Etag
   @Update
   public MethodOutcome update(@IdParam IdType theId, @ResourceParam Patient thePatient) {
      String resourceId = theId.getIdPart();
      String versionId = theId.getVersionIdPart(); // this will contain the ETag

      String currentVersion = "1"; // populate this with the current version

      if (!versionId.equals(currentVersion)) {
         throw new ResourceVersionConflictException(Msg.code(632) + "Expected version " + currentVersion);
      }

      // ... perform the update ...
      return new MethodOutcome();
   }

```

Copy
Example URL to invoke this method (this would be invoked using an HTTP PUT, with the resource in the PUT body): [http://fhir.example.com/Patient](http://fhir.example.com/Patient)
The following snippet shows how the corresponding client interface would look:
```
@Update
public abstract MethodOutcome updateSomePatient(@IdParam IdType theId, @ResourceParam Patient thePatient);

```

Copy
##  4.4.4.1Conditional Updates[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#conditional-updates)
If you wish to support conditional updates, you can add a parameter tagged with a [@ConditionalUrlParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ConditionalUrlParam.html) annotation. If the request URL contains search parameters instead of a resource ID, then this parameter will be populated.
```
@Update
public MethodOutcome updatePatientConditional(
      @ResourceParam Patient thePatient, @IdParam IdType theId, @ConditionalUrlParam String theConditional) {

   // Only one of theId or theConditional will have a value and the other will be null,
   // depending on the URL passed into the server.
   if (theConditional != null) {
      // Do a conditional update. theConditional will have a value like "Patient?identifier=system%7C00001"
   } else {
      // Do a normal update. theId will have the identity of the resource to update
   }

   return new MethodOutcome(); // populate this
}

```

Copy
Example URL to invoke this method (this would be invoked using an HTTP PUT, with the resource in the PUT body): [http://fhir.example.com/Patient?identifier=system%7C00001](http://fhir.example.com/Patient?identifier=system%7C00001)
##  4.4.4.2Accessing The Raw Resource Payload[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#raw_update_access)
If you wish to have access to the raw resource payload as well as the parsed value for any reason, you may also add parameters which have been annotated with the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) of type `String` (to access the raw resource body) and/or `EncodingEnum` (to determine which encoding was used).
The following example shows how to use these additional data elements.
```
@Update
public MethodOutcome updatePatientWithRawValue(
      @ResourceParam Patient thePatient,
      @IdParam IdType theId,
      @ResourceParam String theRawBody,
      @ResourceParam EncodingEnum theEncodingEnum) {

   // Here, thePatient will have the parsed patient body, but
   // theRawBody will also have the raw text of the resource
   // being created, and theEncodingEnum will tell you which
   // encoding was used

   return new MethodOutcome(); // populate this
}

```

Copy
##  4.4.4.3Prefer Header / Returning the resource body[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#prefer)
If you want to allow clients to request that the server return the resource body as a result of the transaction, you may wish to return the updated resource in the returned MethodOutcome.
In this type of request, the client adds a header containing `Prefer: return=representation` which indicates to the server that the client would like the resource returned in the response.
In order for the server to be able to honour this request, the server method should add the updated resource to the MethodOutcome object being returned, as shown in the example below.
```
@Update
public MethodOutcome updatePatientPrefer(@ResourceParam Patient thePatient, @IdParam IdType theId) {

   // Save the patient to the database

   // Update the version and last updated time on the resource
   IdType updatedId = theId.withVersion("123");
   thePatient.setId(updatedId);
   InstantType lastUpdated = InstantType.withCurrentTime();
   thePatient.getMeta().setLastUpdatedElement(lastUpdated);

   // Add the resource to the outcome, so that it can be returned by the server
   // if the client requests it
   MethodOutcome outcome = new MethodOutcome();
   outcome.setId(updatedId);
   outcome.setResource(thePatient);
   return outcome;
}

```

Copy
##  4.4.4.4Contention Aware Updating[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#contention-aware-updating)
As of FHIR DSTU2, FHIR uses the `ETag` header to provide _contention aware updating_. Under this scheme, a client may create a request that contains an ETag specifying the version, and the server will fail if the given version is not the latest version.
Such a request is shown below. In the following example, the update will only be applied if resource "Patient/123" is currently at version "3". Otherwise, it will fail with an `HTTP 409 Conflict` error.
```
PUT [serverBase]/Patient/123
If-Match: W/"3"
Content-Type: application/fhir+json

{ ..resource body.. }

```

Copy
If a client performs a contention aware update, the ETag version will be placed in the version part of the IdDt/IdType that is passed into the method. For example:
```
@Update
public MethodOutcome update(@IdParam IdType theId, @ResourceParam Patient thePatient) {
   String resourceId = theId.getIdPart();
   String versionId = theId.getVersionIdPart(); // this will contain the ETag

   String currentVersion = "1"; // populate this with the current version

   if (!versionId.equals(currentVersion)) {
      throw new ResourceVersionConflictException(Msg.code(632) + "Expected version " + currentVersion);
   }

   // ... perform the update ...
   return new MethodOutcome();
}

```

Copy
##  4.4.4.5Update with History Rewrite[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#update-with-history-rewrite)
If you wish to update a historical version of a resource without creating a new version, this can now be done with the Update operation. While this operation is not supported by the FHIR specification, it's an enhancement added to specifically to HAPI-FHIR.
In order to use this new functionality, you must set the `setUpdateWithHistoryRewriteEnabled` setting in the `StorageSettings` to true.
The following API request shows an example of executing a PUT at the following endpoint.
The request must include the header `X-Rewrite-History`, and should be set to true. The body of the request must include the resource with the same ID and version as defined in the PUT request,
```
PUT [serverBase]/Patient/123/_history/3
Content-Type: application/fhir+json
X-Rewrite-History: true

{ 
   ..
   id: "123",
   meta: {
      versionId: "3",
      ..
   }
   ..
}

```

Copy
#  4.4.5Instance Level - Delete
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_delete)
The [delete](http://hl7.org/implement/standards/fhir/http.html#delete) operation retrieves a specific version of a resource with a given ID. It takes a single ID parameter annotated with an [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation, which supplies the ID of the resource to delete.
```
@Delete()
public void deletePatient(@IdParam IdType theId) {
   // .. Delete the patient ..
   if (couldntFindThisId) {
      throw new ResourceNotFoundException(Msg.code(634) + "Unknown version");
   }
   if (conflictHappened) {
      throw new ResourceVersionConflictException(Msg.code(635) + "Couldn't delete because [foo]");
   }
   // otherwise, delete was successful
   return; // can also return MethodOutcome
}

```

Copy
Delete methods are allowed to return the following types:
  * **void** : This method may return `void`, in which case the server will return an empty response and the client will ignore any successful response from the server (failure responses will still throw an exception)
  * **[MethodOutcome](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/api/MethodOutcome.html)** : This method may return a `MethodOutcome`, which is a wrapper for the FHIR OperationOutcome resource, which may optionally be returned by the server according to the FHIR specification.


Example URL to invoke this method (HTTP DELETE): [http://fhir.example.com/Patient/111](http://fhir.example.com/Patient/111)
##  4.4.5.1Conditional Deletes[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#conditional-deletes)
The FHIR specification also allows "conditional deletes". A conditional delete uses a search style URL instead of a read style URL, and deletes a single resource if it matches the given search parameters. The following example shows how to invoke a conditional delete.
```
@Delete()
public void deletePatientConditional(@IdParam IdType theId, @ConditionalUrlParam String theConditionalUrl) {
   // Only one of theId or theConditionalUrl will have a value depending
   // on whether the URL received was a logical ID, or a conditional
   // search string
   if (theId != null) {
      // do a normal delete
   } else {
      // do a conditional delete
   }

   // otherwise, delete was successful
   return; // can also return MethodOutcome
}

```

Copy
Example URL to perform a conditional delete (HTTP DELETE): [http://fhir.example.com/Patient?identifier=system%7C0001](http://fhir.example.com/Patient?identifier=system%7C0001)
#  4.4.6Instance Level - Patch
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance-level-patch)
HAPI FHIR includes basic support for the [patch](http://hl7.org/implement/standards/fhir/http.html#patch) operation. This support allows you to perform patches, but does not include logic to actually implement resource patching in the server framework (note that the JPA server does include a patch implementation).
The following snippet shows how to define a patch method on a server:
```
@Patch
public OperationOutcome patientPatch(
      @IdParam IdType theId, PatchTypeEnum thePatchType, @ResourceParam String theBody) {

   if (thePatchType == PatchTypeEnum.JSON_PATCH) {
      // do something
   }
   if (thePatchType == PatchTypeEnum.XML_PATCH) {
      // do something
   }

   OperationOutcome retVal = new OperationOutcome();
   retVal.getText().setDivAsString("<div>OK</div>");
   return retVal;
}

```

Copy
##  4.4.6.1Patch with History Rewrite[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#patch-with-history-rewrite)
If you wish to patch a historical version of a resource without creating a new version, this can now be done in the `Patch` operation. While this operation is not supported by the FHIR specification, it's an enhancement added to specifically to HAPI-FHIR.
In order to use this new functionality, you must set the `myUpdateWithHistoryRewriteEnabled` setting in the `StorageSettings` to true.
The request must include the header `X-Rewrite-History`, and should be set to true. The body of the request must include the desired FHIR Patch or JSON Patch. Note that transaction bundles are not yet supported.
The following API request shows an example of executing a FHIR Patch that updates a Patient's birthday without incrementing the resource version:
```
PATCH Patient/123/_history/2
Content-Type: application/fhir+json
X-Rewrite-History: true

{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "operation",
    "part": [ {
      "name": "type",
      "valueCode": "replace"
    }, {
      "name": "path",
      "valueString": "Patient.birthDate"
    }, {
      "name": "value",
      "valueDate": "1930-01-01"
    } ]
  } ]
}

```

Copy
#  4.4.7Type Level - Create
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_create)
The [create](http://hl7.org/implement/standards/fhir/http.html#create) operation saves a new resource to the server, allowing the server to give that resource an ID and version ID.
Create methods must be annotated with the [@Create](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Create.html) annotation, and have a single parameter annotated with the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) annotation. This parameter contains the resource instance to be created. See the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) for information on the types allowed for this parameter (resource types, String, byte[]).
Create methods must return an object of type [MethodOutcome](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/api/MethodOutcome.html). This object contains the identity of the created resource.
The following snippet shows how to define a server create method:
```
@Create
public MethodOutcome createPatient(@ResourceParam Patient thePatient) {

   /*
    * First we might want to do business validation. The UnprocessableEntityException
    * results in an HTTP 422, which is appropriate for business rule failure
    */
   if (thePatient.getIdentifierFirstRep().isEmpty()) {
      /* It is also possible to pass an OperationOutcome resource
       * to the UnprocessableEntityException if you want to return
       * a custom populated OperationOutcome. Otherwise, a simple one
       * is created using the string supplied below.
       */
      throw new UnprocessableEntityException(Msg.code(636) + "No identifier supplied");
   }

   // Save this patient to the database...
   savePatientToDatabase(thePatient);

   // This method returns a MethodOutcome object which contains
   // the ID (composed of the type Patient, the logical ID 3746, and the
   // version ID 1)
   MethodOutcome retVal = new MethodOutcome();
   retVal.setId(new IdType("Patient", "3746", "1"));

   // You can also add an OperationOutcome resource to return
   // This part is optional though:
   OperationOutcome outcome = new OperationOutcome();
   outcome.addIssue().setDiagnostics("One minor issue detected");
   retVal.setOperationOutcome(outcome);

   return retVal;
}

```

Copy
Example URL to invoke this method (this would be invoked using an HTTP POST, with the resource in the POST body): [http://fhir.example.com/Patient](http://fhir.example.com/Patient)
The following snippet shows how the corresponding client interface would look:
```
@Create
public abstract MethodOutcome createNewPatient(@ResourceParam Patient thePatient);

```

Copy
##  4.4.7.1Conditional Creates[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#conditional-creates)
The FHIR specification also allows "conditional creates". A conditional create has an additional header called `If-None-Exist` which the client will supply on the HTTP request. The client will populate this header with a search URL such as `Patient?identifier=foo`. See the FHIR specification for details on the semantics for correctly implementing conditional create.
When a conditional create is detected (i.e. when the create request contains a populated `If-None-Exist` header), if a method parameter annotated with the [@ConditionalUrlParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ConditionalUrlParam.html) is detected, it will be populated with the value of this header.
```
@Create
public MethodOutcome createPatientConditional(
      @ResourceParam Patient thePatient, @ConditionalUrlParam String theConditionalUrl) {

   if (theConditionalUrl != null) {
      // We are doing a conditional create

      // populate this with the ID of the existing resource which
      // matches the conditional URL
      return new MethodOutcome();
   } else {
      // We are doing a normal create

      // populate this with the ID of the newly created resource
      return new MethodOutcome();
   }
}

```

Copy
Example HTTP transaction to perform a conditional create:
```
POST http://fhir.example.com/Patient
If-None-Exist: Patient?identifier=system%7C0001
Content-Type: application/fhir+json

{ ...resource body... }

```

Copy
##  4.4.7.2Prefer Header / Returning the resource body[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#prefer-header-returning-the-resource-body)
If you wish to allow your server to honour the `Prefer` header, the same mechanism shown above for [Prefer Header for Updates](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#prefer) should be used.
##  4.4.7.3Accessing The Raw Resource Payload[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#accessing-the-raw-resource-payload)
The create operation also supports access to the raw payload, using the same semantics as raw payload access [for the update operation](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#raw_update_access).
#  4.4.8Type Level - Search
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_search)
The [search](http://hl7.org/implement/standards/fhir/http.html#search) operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
Searching is a very powerful and potentially very complicated operation to implement, with many possible parameters and combinations of parameters. See [REST Operations: Search](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html) for details on how to create search methods.
#  4.4.9Type Level - Validate
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_validate)
The [validate](http://hl7.org/implement/standards/fhir/http.html#validate) operation tests whether a resource passes business validation, and would be acceptable for saving to a server (e.g. by a create or update method).
Validate methods must be annotated with the [@Validate](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Validate.html) annotation, and have a parameter annotated with the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) annotation. This parameter contains the resource instance to be created.
Validate methods may optionally also have a parameter of type IdType annotated with the [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation. This parameter contains the resource ID (see the [FHIR specification](http://hl7.org/implement/standards/fhir/http.html#validation) for details on how this is used).
Validate methods must return normally independent of the validation outcome. The ResponseStatusCode of the MethodOutcome returned should be 200 irrespective of the validation outcome as required by the [FHIR Specification for the Resource $validate operation](https://www.hl7.org/fhir/R4/resource-operation-validate.html).
Validate methods must return either:
  * **void** – The method should throw an exception for a validation failure, or return normally.
  * An object of type [MethodOutcome](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/api/MethodOutcome.html). The MethodOutcome may optionally be populated with an OperationOutcome resource, which will be returned to the client if it exists.


The following snippet shows how to define a server validate method:
```
@Validate
public MethodOutcome validatePatient(
      @ResourceParam Patient thePatient,
      @Validate.Mode ValidationModeEnum theMode,
      @Validate.Profile String theProfile) {

   // Actually do our validation: The UnprocessableEntityException
   // results in an HTTP 422, which is appropriate for business rule failure
   if (thePatient.getIdentifierFirstRep().isEmpty()) {
      /* It is also possible to pass an OperationOutcome resource
       * to the UnprocessableEntityException if you want to return
       * a custom populated OperationOutcome. Otherwise, a simple one
       * is created using the string supplied below.
       */
      throw new UnprocessableEntityException(Msg.code(639) + "No identifier supplied");
   }

   // This method returns a MethodOutcome object
   MethodOutcome retVal = new MethodOutcome();

   // You may also add an OperationOutcome resource to return
   // This part is optional though:
   OperationOutcome outcome = new OperationOutcome();
   outcome.addIssue().setSeverity(IssueSeverity.WARNING).setDiagnostics("One minor issue detected");
   retVal.setOperationOutcome(outcome);

   return retVal;
}

```

Copy
In the example above, only the [@ResourceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/ResourceParam.html) parameter is technically required, but you may also add the following parameters:
  * **[@Validate.Mode](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Validate.Mode.html) ValidationModeEnum theMode** - This is the validation mode (see the FHIR specification for information on this)
  * **[@Validate.Profile](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Validate.Profile.html) String profile** - This is the profile to validate against (see the FHIR specification for more information on this)


Example URL to invoke this method (this would be invoked using an HTTP POST, with a Parameters resource in the POST body): [http://fhir.example.com/Patient/$validate](http://fhir.example.com/Patient/$validate)
#  4.4.10System Level - Capabilities
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#system_capabilities)
FHIR defines that a FHIR Server must be able to export a Capability Statement (formerly called a Conformance Statement), which is an instance of the [CapabilityStatement](http://hl7.org/implement/standards/fhir/CapabilityStatement.html) resource describing the server itself.
The HAPI FHIR RESTful server will automatically export such a capability statement. See the [Server Capability Statement](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#capabilities) documentation for more information.
If you wish to override this default behaviour by creating your own capability statement provider, you simply need to define a class with a method annotated using the [@Metadata](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Metadata.html) annotation.
An example provider is shown below.
```
public class CapabilityStatementProvider {

   @Metadata
   public CapabilityStatement getServerMetadata() {
      CapabilityStatement retVal = new CapabilityStatement();
      // ..populate..
      return retVal;
   }
}

```

Copy
To create a Client which can retrieve a Server's conformance statement is simple. First, define your Client Interface, using the [@Metadata](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Metadata.html) annotation:
```
public interface MetadataClient extends IRestfulClient {

   @Metadata
   CapabilityStatement getServerMetadata();

   // ....Other methods can also be added as usual....

}

```

Copy
You can then use the standard [Annotation Client](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html) mechanism for instantiating a client:
```
FhirContext ctx = FhirContext.forR4();
MetadataClient client = ctx.newRestfulClient(MetadataClient.class, "http://spark.furore.com/fhir");
CapabilityStatement metadata = client.getServerMetadata();
System.out.println(ctx.newXmlParser().encodeResourceToString(metadata));

```

Copy
#  4.4.11System Level - Transaction
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#system_transaction)
The [transaction](http://hl7.org/implement/standards/fhir/http.html#transaction) action is among the most challenging parts of the FHIR specification to implement. It allows the user to submit a bundle containing a number of resources to be created/updated/deleted as a single atomic transaction.
HAPI provides a skeleton for implementing this action, although most of the effort will depend on the underlying implementation. The following example shows how to define a _transaction_ method.
```
@Transaction
public Bundle transaction(@TransactionParam Bundle theInput) {
   for (BundleEntryComponent nextEntry : theInput.getEntry()) {
      // Process entry
   }

   Bundle retVal = new Bundle();
   // Populate return bundle
   return retVal;
}

```

Copy
Transaction methods require one parameter annotated with [@TransactionParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/TransactionParam.html), and that parameter may be of type [`List<IBaseResource>`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/org/hl7/fhir/instance/model/api/IBaseResource.html) or [`Bundle`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Bundle.html)`.
In terms of actually implementing the method, unfortunately there is only so much help HAPI will give you. One might expect HAPI to automatically delegate the individual operations in the transaction to other methods on the server but at this point it does not do that. There is a lot that transaction needs to handle (making everything atomic, replacing placeholder IDs across multiple resources which may even be circular, handling operations in the right order) and so far we have not found a way for the framework to do this in a generic way.
What it comes down to is the fact that transaction is a tricky thing to implement. For what it's worth, you could look at the HAPI FHIR JPA Server [TransactionProcessor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/ca/uhn/fhir/jpa/dao/TransactionProcessor.html) class for inspiration on how to build a transaction processor of your own (note that this class is tightly coupled with the rest of the JPA Server so it is unlikely that it can be used directly outside of that context).
Example URL to invoke this method (note that the URL is the base URL for the server, and the request body is a Bundle resource):
```
POST http://fhir.example.com/
Content-Type: application/fhir+json

{
   "resourceType": "Bundle",
   "type": "transaction",
   "entry": [ ...entries... ]
}

```

Copy
#  4.4.12System Level - Search
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#system_search)
Not yet implemented - Get in touch if you would like to help!
#  4.4.13History (Instance, Type, Server)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#history)
The [history](http://hl7.org/implement/standards/fhir/http.html#history) operation retrieves a historical collection of all versions of a single resource _(instance history)_ , all resources of a given type _(type history)_ , or all resources of any type on a server _(server history)_.
History methods are annotated with the [@History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/History.html) annotation, and will have additional requirements depending on the kind of history method intended:
  * For an **Instance History** method, the method must have a parameter annotated with the [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation, indicating the ID of the resource for which to return history. The method must either be defined in a [resource provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-providers), or must have a `type()` value in the [@History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/History.html) annotation if it is defined in a [plain provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers).
  * For a **Type History** method, the method must not have any [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) parameter. The method must either be defined in a [resource provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-providers), or must have a `type()` value in the [@History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/History.html) annotation if it is defined in a [plain provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers).
  * For a **Server History** method, the method must not have any [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) parameter, and must not have a `type()` value specified in the [@History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/History.html) annotation. The method must be defined in a [plain provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers).


The following snippet shows how to define a history method on a server. Note that the following parameters are both optional, but may be useful in implementing the history operation:
  * The [@Since](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Since.html) method argument implements the `_since` parameter and should be of type [DateTimeType](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/DateTimeType.html).
  * The [@At](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/At.html) method argument implements the `_at` parameter and may be of type [DateRangeParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/DateRangeParam.html) or [DateTimeType](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/DateTimeType.html).


```
@History()
public List<Patient> getPatientHistory(
      @IdParam IdType theId, @Since InstantType theSince, @At DateRangeParam theAt) {
   List<Patient> retVal = new ArrayList<Patient>();

   Patient patient = new Patient();

   // Set the ID and version
   patient.setId(theId.withVersion("1"));

   if (isDeleted(patient)) {

      // If the resource is deleted, it just needs to have an ID and some metadata
      ResourceMetadataKeyEnum.DELETED_AT.put(patient, InstantType.withCurrentTime());
      ResourceMetadataKeyEnum.ENTRY_TRANSACTION_METHOD.put(patient, BundleEntryTransactionMethodEnum.DELETE);

   } else {

      // If the resource is not deleted, it should have normal resource content
      patient.addName().setFamily("Smith"); // ..populate the rest
   }

   return retVal;
}

```

Copy
The following snippet shows how to define various history methods in a client.
```
public interface HistoryClient extends IBasicClient {
   /** Server level (history of ALL resources) */
   @History
   Bundle getHistoryServer();

   /** Type level (history of all resources of a given type) */
   @History(type = Patient.class)
   Bundle getHistoryPatientType();

   /** Instance level (history of a specific resource instance by type and ID) */
   @History(type = Patient.class)
   Bundle getHistoryPatientInstance(@IdParam IdType theId);

   /**
    * Either (or both) of the "since" and "count" parameters can
    * also be included in any of the methods above.
    */
   @History
   Bundle getHistoryServerWithCriteria(@Since Date theDate, @Count Integer theCount);
}

```

Copy
#  4.4.14Exceptions
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#exceptions)
When implementing a server operation, there are a number of failure conditions specified. For example, an [Instance Read](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_read) request might specify an unknown resource ID, or a [Type Create](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_create) request might contain an invalid resource which can not be created.
See [REST Exception Handling](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#exceptions) for information on available exceptions.
#  4.4.15Tags
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#tags)
FHIR RESTful servers may support a feature known as tagging. Tags are a set of named flags called which use a FHIR Coding datatype (meaning that they have a system, value, and display just like any other coded field).
Tags have very specific semantics, which may not be obvious simply by using the HAPI API. It is important to review the specification [Tags Documentation](http://hl7.org/implement/standards/fhir/http.html#tags) before attempting to implement tagging in your own applications.
##  4.4.15.1Accessing Tags in a Read / VRead / Search Method[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#accessing-tags-in-a-read-vread-search-method)
Tags are stored within a resource object, in the Resource.meta element. It is important to note that changing a resource's tags will not cause a version update to that resource.
In a server implementation, you may populate your tags into the returned resource(s) and HAPI will automatically place these tags into the response headers (for read/vread) or the bundle category tags (for search). The following example illustrates how to return tags from a server method. This example shows how to supply tags in a read method, but the same approach applies to vread and search operations as well.
```
@Read()
public Patient readPatient(@IdParam IdType theId) {
   Patient retVal = new Patient();

   // ..populate demographics, contact, or anything else you usually would..

   // Populate some tags
   retVal.getMeta().addTag("http://animals", "Dog", "Canine Patient"); // TODO: more realistic example
   retVal.getMeta().addTag("http://personality", "Friendly", "Friendly"); // TODO: more realistic example

   return retVal;
}

```

Copy
In a client operation, you simply call the read/vread/search method as you normally would (as described above), and if any tags have been returned by the server, these may be accessed from the resource metadata.
```
IPatientClient client = FhirContext.forR4().newRestfulClient(IPatientClient.class, "http://foo/fhir");
Patient patient = client.readPatient(new IdType("1234"));

// Access the tag list
List<Coding> tagList = patient.getMeta().getTag();
for (Coding next : tagList) {
   // ..process the tags somehow..
}

```

Copy
##  4.4.15.2Setting Tags in a Create/Update Method[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#setting-tags-in-a-createupdate-method)
Within a [Type Create](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_create) or [Instance Update](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_update) method, it is possible for the client to specify a set of tags to be stored along with the saved resource instance.
Note that FHIR specifies that in an update method, any tags supplied by the client are copied to the newly saved version, as well as any tags the existing version had.
To work with tags in a create/update method, the pattern used in the read examples above is simply reversed. In a server, the resource which is passed in will be populated with any tags that the client supplied:
```
@Create
public MethodOutcome createPatientResource(@ResourceParam Patient thePatient) {

   // ..save the resource..
   IdType id = new IdType("123"); // the new database primary key for this resource

   // Get the tag list
   List<Coding> tags = thePatient.getMeta().getTag();
   for (Coding tag : tags) {
      // process/save each tag somehow
   }

   return new MethodOutcome(id);
}

```

Copy
##  4.4.15.3Removing Tags[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#removing-tags)
In order to remove a tag, it does not suffice to remove it from the resource. Tags can be removed using the [Resource Operation Meta Delete](https://www.hl7.org/fhir/resource-operation-meta-delete.html), which takes a Parameter definining which tags to delete.
#  4.4.16Handling _summary and _elements
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#handling-summary-and-elements)
The `_summary` and `_elements` parameters are automatically handled by the server, so no coding is required to make this work.
However, if you wish to add parameters to manually handle these fields, the following example shows how to access these. This can be useful if you have an architecture where it is more work for the database/storage engine to load all fields.
```
@Search
public List<Patient> search(
      SummaryEnum theSummary, // will receive the summary (no annotation required)
      @Elements Set<String> theElements // (requires the @Elements annotation)
      ) {
   return null; // todo: populate
}

```

Copy
#  4.4.17Compartments
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#compartments)
FHIR defines a mechanism for logically grouping resources together called [compartments](http://www.hl7.org/implement/standards/fhir/extras.html#compartment).
To define a search by compartment, you simply need to add the `compartmentName()` attribute to the [@Search](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Search.html) annotation, and add an [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) parameter.
The following example shows a search method in a resource provider which returns a compartment. Note that you may also add [@RequiredParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/RequiredParam.html) and [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html) parameters to your compartment search method.
```
public class PatientRp implements IResourceProvider {

   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return Patient.class;
   }

   @Search(compartmentName = "Condition")
   public List<IBaseResource> searchCompartment(@IdParam IdType thePatientId) {
      List<IBaseResource> retVal = new ArrayList<IBaseResource>();

      // populate this with resources of any type that are a part of the
      // "Condition" compartment for the Patient with ID "thePatientId"

      return retVal;
   }

   // .. also include other Patient operations ..
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient/123/Condition](http://fhir.example.com/Patient/123/Condition)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html)
4.4 REST Operations: Overview 
* Plain Server 
* [ 4.0  REST Server Types ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html)
* [ 4.1  Plain Server Introduction ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html)
* [ 4.2  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
* [ 4.3  Resource Providers and Plain Providers ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html)
* [ 4.4  REST Operations: Overview ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
* [ 4.5  REST Operations: Search ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
* [ 4.6  REST Operations: Extended Operations ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
* [ 4.7  Paging Search Results ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
* [ 4.8  Web Testpage Overlay ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
* [ 4.9  Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
* [ 4.10  JAX-RS Support ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
* [ 4.11  Customizing the CapabilityStatement ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html)
* [ 4.12  OpenAPI / Swagger ](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html)
[ 4.5 REST Operations: Search ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)