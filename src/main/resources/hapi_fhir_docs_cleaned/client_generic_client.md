---
source: https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
crawled: 2025-08-01T14:06:50.841496
---

# Client Generic Client

#  3.2.1Generic (Fluent) Client
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#generic-fluent-client)
Creating a generic client simply requires you to create an instance of `FhirContext` and use that to instantiate a client.
The following example shows how to create a client, and a few operations which can be performed.
```
// We're connecting to a DSTU1 compliant server in this example
FhirContext ctx = FhirContext.forR5();
String serverBase = "http://hapi.fhir.org/baseR5";

IGenericClient client = ctx.newRestfulGenericClient(serverBase);

// Perform a search
Bundle results = client.search()
      .forResource(Patient.class)
      .where(Patient.FAMILY.matches().value("duck"))
      .returnBundle(Bundle.class)
      .execute();

System.out.println("Found " + results.getEntry().size() + " patients named 'duck'");

```

Copy
**Performance Tip:** Note that FhirContext is an expensive object to create, so you should try to keep an instance around for the lifetime of your application. It is thread-safe so it can be passed as needed. Client instances, on the other hand, are very inexpensive to create so you can create a new one for each request if needed (although there is no requirement to do so, clients are reusable and thread-safe as well). 
#  3.2.2Fluent Calls
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#fluent-calls)
The generic client supports queries using a fluent interface which is originally inspired by the excellent [.NET FHIR API](http://firely-team.github.io/fhir-net-api/client-search.html).
The fluent interface allows you to construct powerful queries by chaining method calls together, leading to highly readable code. It also allows you to take advantage of intellisense/code completion in your favourite IDE.
Note that most fluent operations end with an `execute()` statement which actually performs the invocation. You may also invoke several configuration operations just prior to the execute() statement, such as `encodedJson()` or `encodedXml()`.
#  3.2.3Search
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search)
Searching is a very powerful part of the FHIR API specification itself, and HAPI FHIR aims to provide a complete implementation of the FHIR API search specification via the generic client API.
##  3.2.3.1Search - By Type[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-by-type)
Searching for resources is probably the most common initial scenario for client applications, so we'll start the demonstration there. The FHIR search operation generally uses a URL with a set of predefined search parameters, and returns a Bundle containing zero-or-more resources which matched the given search criteria.
Search is a very powerful mechanism, with advanced features such as paging, including linked resources, etc. See the FHIR [search specification](http://hl7.org/fhir/search.html) for more information.
The following example shows how to query using the generic client:
```
Bundle response = client.search()
      .forResource(Patient.class)
      .where(Patient.BIRTHDATE.beforeOrEquals().day("2011-01-01"))
      .and(Patient.GENERAL_PRACTITIONER.hasChainedProperty(
            Organization.NAME.matches().value("Smith")))
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.2Search - Multi-valued Parameters (ANY/OR)[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-multi-valued-parameters-anyor)
To search for a set of possible values where _ANY_ should be matched, you can provide multiple values to a parameter, as shown in the example below.
This leads to a URL resembling `http://base/Patient?family=Smith,Smyth`.
```
response = client.search()
      .forResource(Patient.class)
      .where(Patient.FAMILY.matches().values("Smith", "Smyth"))
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.3Search - Multi-valued Parameters (ALL/AND)[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-multi-valued-parameters-alland)
To search for a set of possible values where _ALL_ should be matched, you can provide multiple instances of a parameter, as shown in the example below.
This leads to a URL resembling `http://base/Patient?address=Toronto&address=Ontario&address=Canada`.
```
response = client.search()
      .forResource(Patient.class)
      .where(Patient.ADDRESS.matches().values("Toronto"))
      .and(Patient.ADDRESS.matches().values("Ontario"))
      .and(Patient.ADDRESS.matches().values("Canada"))
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.4Search - Paging[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-paging)
If the server supports paging results, the client has a page method which can be used to load subsequent pages.
```
FhirContext ctx = FhirContext.forDstu2();
IGenericClient client = ctx.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2");

// Perform a search
Bundle resultBundle = client.search()
      .forResource(Patient.class)
      .where(Patient.NAME.matches().value("Smith"))
      .returnBundle(Bundle.class)
      .execute();

if (resultBundle.getLink(Bundle.LINK_NEXT) != null) {

   // load next page
   Bundle nextPage = client.loadPage().next(resultBundle).execute();
}

```

Copy
##  3.2.3.5Search - Composite Parameters[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-composite-parameters)
If a composite parameter is being searched on, the parameter takes a "left" and "right" operand, each of which is a parameter from the resource being searched. The following example shows the syntax.
```
response = client.search()
      .forResource("Observation")
      .where(Observation.CODE_VALUE_DATE
            .withLeft(Observation.CODE.exactly().code("FOO$BAR"))
            .withRight(Observation.VALUE_DATE.exactly().day("2001-01-01")))
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.6Search - By plain URL[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-by-plain-url)
You can also perform a search using a String URL, instead of using the fluent method calls to build the URL.
This can be useful if you have a URL you retrieved from somewhere else that you want to use as a search. This can also be useful if you need to make a search that isn't presently supported by one of the existing fluent methods (e.g. reverse chaining with `_has`).
```
String searchUrl = "http://example.com/base/Patient?identifier=foo";

// Search URL can also be a relative URL in which case the client's base
// URL will be added to it
searchUrl = "Patient?identifier=foo";

response =
      client.search().byUrl(searchUrl).returnBundle(Bundle.class).execute();

```

Copy
##  3.2.3.7Search - Other Query Options[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-other-query-options)
The fluent search also has methods for sorting, limiting, specifying JSON encoding, _include, _revinclude, _lastUpdated, _tag, etc.
```
response = client.search()
      .forResource(Patient.class)
      .encodedJson()
      .where(Patient.BIRTHDATE.beforeOrEquals().day("2012-01-22"))
      .and(Patient.BIRTHDATE.after().day("2011-01-01"))
      .withTag("http://acme.org/codes", "needs-review")
      .include(Patient.INCLUDE_ORGANIZATION.asRecursive())
      .include(Patient.INCLUDE_GENERAL_PRACTITIONER.asNonRecursive())
      .revInclude(Provenance.INCLUDE_TARGET)
      .lastUpdated(new DateRangeParam("2011-01-01", null))
      .sort()
      .ascending(Patient.BIRTHDATE)
      .sort()
      .descending(Patient.NAME)
      .count(123)
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.8Search - Using HTTP POST[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-using-http-post)
The FHIR specification allows the use of an HTTP POST to transmit a search to a server instead of using an HTTP GET. With this style of search, the search parameters are included in the request body instead of the request URL, which can be useful if you need to transmit a search with a large number of parameters.
The [`usingStyle(SearchStyleEnum)`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/gclient/IQuery.html#usingStyle\(ca.uhn.fhir.rest.api.SearchStyleEnum\)) method controls which style to use. By default, GET style is used unless the client detects that the request would result in a very long URL (over 8000 chars) in which case the client automatically switches to POST.
If you wish to force the use of HTTP POST, you can do that as well.
```
response = client.search()
      .forResource("Patient")
      .where(Patient.NAME.matches().value("Tester"))
      .usingStyle(SearchStyleEnum.POST)
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.9Search - Compartments[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-compartments)
To search a [resource compartment](http://www.hl7.org/implement/standards/fhir/extras.html#compartment), simply use the [`withIdAndCompartment()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/gclient/IQuery.html#withIdAndCompartment\(java.lang.String,java.lang.String\)) method in your search.
```
response = client.search()
      .forResource(Patient.class)
      .withIdAndCompartment("123", "condition")
      .where(Patient.ADDRESS.matches().values("Toronto"))
      .returnBundle(Bundle.class)
      .execute();

```

Copy
##  3.2.3.10Search - Subsetting (_summary and _elements)[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#search-subsetting-summary-and-elements)
Sometimes you may want to only ask the server to include some parts of returned resources (instead of the whole resource). Typically this is for performance or optimization reasons, but there may also be privacy reasons for doing this.
To request that the server return only "summary" elements (those elements defined in the specification with the "Σ" flag), you can use the [`summaryMode(SummaryEnum)`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/gclient/IClientExecutable.html#summaryMode\(ca.uhn.fhir.rest.api.SummaryEnum\)) qualifier:
```
response = client.search()
      .forResource(Patient.class)
      .where(Patient.ADDRESS.matches().values("Toronto"))
      .returnBundle(Bundle.class)
      .summaryMode(SummaryEnum.TRUE)
      .execute();

```

Copy
To request that the server return only elements from a custom list provided by the client, you can use the [`elementsSubset(String...)`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/gclient/IClientExecutable.html#elementsSubset\(java.lang.String...\)) qualifier:
```
response = client.search()
      .forResource(Patient.class)
      .where(Patient.ADDRESS.matches().values("Toronto"))
      .returnBundle(Bundle.class)
      .elementsSubset("identifier", "name") // only include the identifier and name
      .execute();

```

Copy
#  3.2.4Create - Type
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#create-type)
The following example shows how to perform a create operation using the generic client:
```
Patient patient = new Patient();
// ..populate the patient object..
patient.addIdentifier().setSystem("urn:system").setValue("12345");
patient.addName().setFamily("Smith").addGiven("John");

// Invoke the server create method (and send pretty-printed JSON
// encoding to the server
// instead of the default which is non-pretty printed XML)
MethodOutcome outcome = client.create()
      .resource(patient)
      .prettyPrint()
      .encodedJson()
      .execute();

// The MethodOutcome object will contain information about the
// response from the server, including the ID of the created
// resource, the OperationOutcome response, etc. (assuming that
// any of these things were provided by the server! They may not
// always be)
IIdType id = outcome.getId();
System.out.println("Got ID: " + id.getValue());

```

Copy
##  3.2.4.1Conditional Creates[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#conditional-creates)
FHIR also specifies a type of update called "conditional create", where a set of search parameters are provided and a new resource is only created if no existing resource matches those parameters. See the FHIR specification for more information on conditional creation.
```
// One form
MethodOutcome outcome = client.create()
      .resource(patient)
      .conditionalByUrl("Patient?identifier=system%7C00001")
      .execute();

// Another form
MethodOutcome outcome2 = client.create()
      .resource(patient)
      .conditional()
      .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("system", "00001"))
      .execute();

// This will return Boolean.TRUE if the server responded with an HTTP 201 created,
// otherwise it will return null.
Boolean created = outcome.getCreated();

// The ID of the created, or the pre-existing resource
IIdType id = outcome.getId();

```

Copy
#  3.2.5Read/VRead - Instance
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#readvread-instance)
Given a resource name and ID, it is simple to retrieve the latest version of that resource (a 'read').
```
// search for patient 123
Patient patient =
      client.read().resource(Patient.class).withId("123").execute();

```

Copy
By adding a version string, it is also possible to retrieve a specific version (a 'vread').
```
// search for patient 123 (specific version 888)
Patient patient = client.read()
      .resource(Patient.class)
      .withIdAndVersion("123", "888")
      .execute();

```

Copy
It is also possible to retrieve a resource given its absolute URL (this will override the base URL set on the client).
```
// search for patient 123 on example.com
String url = "http://example.com/fhir/Patient/123";
Patient patient = client.read().resource(Patient.class).withUrl(url).execute();

```

Copy
**See Also:** See the description of [Read/VRead ETags](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#read_etags) below for information on specifying a matching version in the client request.
#  3.2.6Delete - Instance
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#delete-instance)
The following example shows how to perform a delete operation using the generic client:
```
MethodOutcome response =
      client.delete().resourceById(new IdType("Patient", "1234")).execute();

// outcome may be null if the server didn't return one
OperationOutcome outcome = (OperationOutcome) response.getOperationOutcome();
if (outcome != null) {
   System.out.println(outcome.getIssueFirstRep()
         .getDetails()
         .getCodingFirstRep()
         .getCode());
}

```

Copy
##  3.2.6.1Conditional Deletes[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#conditional-deletes)
Conditional deletions are also possible, which is a form where instead of deleting a resource using its logical ID, you specify a set of search criteria and a single resource is deleted if it matches that criteria. Note that this is not a mechanism for bulk deletion; see the FHIR specification for information on conditional deletes and how they are used.
```
client.delete()
      .resourceConditionalByUrl("Patient?identifier=system%7C00001")
      .execute();

client.delete()
      .resourceConditionalByType("Patient")
      .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("system", "00001"))
      .execute();

```

Copy
##  3.2.6.2Cascading Delete[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#cascading-delete)
The following snippet shows now to request a cascading delete. Note that this is a HAPI FHIR specific feature and is not supported on all servers.
```
client.delete()
      .resourceById(new IdType("Patient/123"))
      .cascade(DeleteCascadeModeEnum.DELETE)
      .execute();

client.delete()
      .resourceConditionalByType("Patient")
      .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("system", "00001"))
      .execute();

```

Copy
#  3.2.7Update - Instance
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#update-instance)
Updating a resource is similar to creating one, except that an ID must be supplied since you are updating a previously existing resource instance.
The following example shows how to perform an update operation using the generic client:
```
Patient patient = new Patient();
// ..populate the patient object..
patient.addIdentifier().setSystem("urn:system").setValue("12345");
patient.addName().setFamily("Smith").addGiven("John");

// To update a resource, it should have an ID set (if the resource
// object
// comes from the results of a previous read or search, it will already
// have one though)
patient.setId("Patient/123");

// Invoke the server update method
MethodOutcome outcome = client.update().resource(patient).execute();

// The MethodOutcome object will contain information about the
// response from the server, including the ID of the created
// resource, the OperationOutcome response, etc. (assuming that
// any of these things were provided by the server! They may not
// always be)
IdType id = (IdType) outcome.getId();
System.out.println("Got ID: " + id.getValue());

```

Copy
##  3.2.7.1Conditional Updates[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#conditional-updates)
FHIR also specifies a type of update called "conditional updates", where instead of using the logical ID of a resource to update, a set of search parameters is provided. If a single resource matches that set of parameters, that resource is updated. See the FHIR specification for information on how conditional updates work.
```
client.update()
      .resource(patient)
      .conditionalByUrl("Patient?identifier=system%7C00001")
      .execute();

client.update()
      .resource(patient)
      .conditional()
      .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("system", "00001"))
      .execute();

```

Copy
**See Also:** See the description of [Update ETags](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#update_etags) below for information on specifying a matching version in the client request.
#  3.2.8Patch - Instance
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#patch-instance)
The PATCH operation can be used to modify a resource in place by supplying a delta
The following example shows how to perform a patch using a [FHIR Patch](http://hl7.org/fhir/fhirpatch.html)
```
// Create a client
String serverBase = "http://hapi.fhir.org/baseR4";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

// Create a patch object
Parameters patch = new Parameters();
Parameters.ParametersParameterComponent operation = patch.addParameter();
operation.setName("operation");
operation.addPart().setName("type").setValue(new CodeType("delete"));
operation.addPart().setName("path").setValue(new StringType("Patient.identifier[0]"));

// Invoke the patch
MethodOutcome outcome =
      client.patch().withFhirPatch(patch).withId("Patient/123").execute();

// The server may provide the updated contents in the response
Patient resultingResource = (Patient) outcome.getResource();

```

Copy
The following example shows how to perform a patch using a [JSON Patch](https://tools.ietf.org/html/rfc6902.)
```
// Create a client
String serverBase = "http://hapi.fhir.org/baseR4";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

// Create a JSON patch object
String patch = "[ " + "   { "
      + "      \"op\":\"replace\", "
      + "      \"path\":\"/active\", "
      + "      \"value\":false "
      + "   } "
      + "]";

// Invoke the patch
MethodOutcome outcome =
      client.patch().withBody(patch).withId("Patient/123").execute();

// The server may provide the updated contents in the response
Patient resultingResource = (Patient) outcome.getResource();

```

Copy
#  3.2.9History - Server/Type/Instance
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#history-servertypeinstance)
To retrieve the version history of all resources, or all resources of a given type, or of a specific instance of a resource, you call the [`history()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IGenericClient.html#history\(\)) method.
```
Bundle response =
      client.history().onServer().returnBundle(Bundle.class).execute();

```

Copy
You can also optionally request that only resource versions later than a given date, and/or only up to a given count (number) of resource versions be returned starting from a specific index.
```
Bundle response = client.history()
      .onServer()
      .returnBundle(Bundle.class)
      .since(new InstantType("2012-01-01T12:22:32.038Z"))
      .offset(10)
      .count(100)
      .execute();

```

Copy
#  3.2.10Transaction - Server
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#transaction-server)
The following example shows how to execute a transaction using the generic client:
```
List<IResource> resources = new ArrayList<IResource>();
// .. populate this list - note that you can also pass in a populated
// Bundle if you want to create one manually ..

List<IBaseResource> response =
      client.transaction().withResources(resources).execute();

```

Copy
#  3.2.11Capability Statement (metadata) - Server
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#capability-statement-metadata-server)
To retrieve the server's capability statement, simply call the [`capabilities()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IGenericClient.html#capabilities\(\)) method as shown below.
```
// Retrieve the server's conformance statement and print its
// description
CapabilityStatement conf =
      client.capabilities().ofType(CapabilityStatement.class).execute();
System.out.println(conf.getDescriptionElement().getValue());

```

Copy
#  3.2.12Extended Operations
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#extended-operations)
FHIR also supports a set of _extended operations_ , which are operations beyond the basic CRUD operations defined in the specification. These operations are an RPC style of invocation, with a set of named input parameters passed to the server and a set of named output parameters returned back.
To invoke an operation using the client, you simply need to create the input [Parameters](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Parameters.html) resource, then pass that to the [`operation()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IGenericClient.html#operation\(\)) fluent method.
The example below shows a simple operation call.
```
HttpGet
      // Create a client to talk to the HeathIntersections server
      FhirContext ctx = FhirContext.forDstu2();
      IGenericClient client = ctx.newRestfulGenericClient("http://fhir-dev.healthintersections.com.au/open");
      client.registerInterceptor(new LoggingInterceptor(true));

      // Create the input parameters to pass to the server
      Parameters inParams = new Parameters();
      inParams.addParameter().setName("start").setValue(new DateType("2001-01-01"));
      inParams.addParameter().setName("end").setValue(new DateType("2015-03-01"));

      // Invoke $everything on "Patient/1"
      Parameters outParams = client.operation()
            .onInstance(new IdType("Patient", "1"))
            .named("$everything")
            .withParameters(inParams)
            .useHttpGet() // Use HTTP GET instead of POST
            .execute();

```

Copy
Note that if the operation does not require any input parameters, you may also invoke the operation using the following form. Note that the `withNoParameters` form still requires you to provide the type of the Parameters resource so that it can return the correct type in the response.
```
// Create a client to talk to the HeathIntersections server
FhirContext ctx = FhirContext.forDstu2();
IGenericClient client = ctx.newRestfulGenericClient("http://fhir-dev.healthintersections.com.au/open");
client.registerInterceptor(new LoggingInterceptor(true));

// Invoke $everything on "Patient/1"
Parameters outParams = client.operation()
      .onInstance(new IdType("Patient", "1"))
      .named("$everything")
      .withNoParameters(Parameters.class) // No input parameters
      .execute();

```

Copy
##  3.2.12.1Using the HTTP GET Form[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#using-the-http-get-form)
By default, the client will invoke operations using the HTTP POST form. The FHIR specification also allows requests to use the HTTP GET verb if the operation does not affect state and has no composite/resource parameters. Use the following form to invoke operation with HTTP GET.
```
// Create a client to talk to the HeathIntersections server
FhirContext ctx = FhirContext.forDstu2();
IGenericClient client = ctx.newRestfulGenericClient("http://fhir-dev.healthintersections.com.au/open");
client.registerInterceptor(new LoggingInterceptor(true));

// Create the input parameters to pass to the server
Parameters inParams = new Parameters();
inParams.addParameter().setName("start").setValue(new DateType("2001-01-01"));
inParams.addParameter().setName("end").setValue(new DateType("2015-03-01"));

// Invoke $everything on "Patient/1"
Parameters outParams = client.operation()
      .onInstance(new IdType("Patient", "1"))
      .named("$everything")
      .withParameters(inParams)
      .useHttpGet() // Use HTTP GET instead of POST
      .execute();

```

Copy
##  3.2.12.2Built-In Operations - Validate[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#built-in-operations-validate)
The `$validate` operation asks the server to test a given resource to see if it would be acceptable as a create/update on that server. The client has built-in support for this operation.
```
Patient patient = new Patient();
patient.addIdentifier().setSystem("http://hospital.com").setValue("123445");
patient.addName().setFamily("Smith").addGiven("John");

// Validate the resource
MethodOutcome outcome = client.validate().resource(patient).execute();

// The returned object will contain an operation outcome resource
OperationOutcome oo = (OperationOutcome) outcome.getOperationOutcome();

// If the OperationOutcome has any issues with a severity of ERROR or SEVERE,
// the validation failed.
for (OperationOutcome.OperationOutcomeIssueComponent nextIssue : oo.getIssue()) {
   if (nextIssue.getSeverity().ordinal() >= OperationOutcome.IssueSeverity.ERROR.ordinal()) {
      System.out.println("We failed validation!");
   }
}

```

Copy
#  3.2.13Built-In Operations - Process-Message
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#built-in-operations-process-message)
The `$process-message` operation asks the server to accept a FHIR message bundle for processing.
```
FhirContext ctx = FhirContext.forDstu3();

// Create the client
IGenericClient client = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

Bundle bundle = new Bundle();
// ..populate the bundle..

Bundle response = client.operation()
      .processMessage() // New operation for sending messages
      .setMessageBundle(bundle)
      .asynchronous(Bundle.class)
      .execute();

```

Copy
#  3.2.14Additional Properties
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#additional-properties)
This section contains ways of customizing the request sent by the client.
##  3.2.14.1Cache-Control[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#cache-control)
The `Cache-Control` header can be used by the client in a request to signal to the server (or any cache in front of it) that the client wants specific behaviour from the cache, or wants the cache to not act on the request altogether. Naturally, not all servers will honour this header.
To add a cache control directive in a request:
```
Bundle response = client.search()
      .forResource(Patient.class)
      .returnBundle(Bundle.class)
      .cacheControl(new CacheControlDirective().setNoCache(true)) // <-- add a directive
      .execute();

```

Copy
#  3.2.15ETags
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#etags)
ETag features are added simply by adding fluent method calls to the client method chain, as shown in the following examples.
##  3.2.15.1Read / VRead ETags[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#read_etags)
To notify the server that it should return an `HTTP 304 Not Modified` if the content has not changed, add an [`ifVersionMatches()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/gclient/IReadExecutable.html#ifVersionMatches\(java.lang.String\)) invocation.
```
// search for patient 123
Patient patient = client.read()
      .resource(Patient.class)
      .withId("123")
      .ifVersionMatches("001")
      .returnNull()
      .execute();
if (patient == null) {
   // resource has not changed
}

```

Copy
This method will add the following header to the request:
```
If-None-Match: "W/001"

```

Copy
##  3.2.15.2Update ETags[](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#update_etags)
To implement version aware updates, specify a version in the request. This will notify the server that it should only update the resource on the server if the version matches the given version. This is useful to prevent two clients from attempting to modify the resource at the same time, and having one client's updates overwrite the other's.
```
// First, let's retrieve the latest version of a resource
// from the server
Patient patient =
      client.read().resource(Patient.class).withId("123").execute();

// If the server is a version aware server, we should now know the latest version
// of the resource
System.out.println("Version ID: " + patient.getIdElement().getVersionIdPart());

// Now let's make a change to the resource
patient.setGender(Enumerations.AdministrativeGender.FEMALE);

// Invoke the server update method - Because the resource has
// a version, it will be included in the request sent to
// the server
try {
   MethodOutcome outcome = client.update().resource(patient).execute();
} catch (PreconditionFailedException e) {
   // If we get here, the latest version has changed
   // on the server so our update failed.
}

```

Copy
The following header will be added to the request as a part of this interaction:
```
If-Match: "W/001"

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
3.2 Generic (Fluent) Client 
* Client 
* [ 3.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html)
* [ 3.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* [ 3.2  Generic (Fluent) Client ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
* [ 3.3  Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* [ 3.4  Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* [ 3.5  Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
[ 3.3 Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)