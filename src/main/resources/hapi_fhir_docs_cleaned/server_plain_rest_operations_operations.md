---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html
crawled: 2025-08-01T14:06:49.547457
---

# Server Plain Rest Operations Operations

#  4.6.1REST Operations: Extended Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#rest-operations-extended-operations)
The FHIR specification defines a special kind of operations that have an RPC-like functionality. These are called "Execute Operations", or simply "Operations" throughout the FHIR specification.
A good introduction to this capability can be found on the [Operations Page](http://hl7.org/fhir/operations.html) of the FHIR Specification.
FHIR extended operations are a special type of RPC-style invocation you can perform against a FHIR server, type, or resource instance. These invocations are named using the convention `$name` (i.e. the name is prefixed with $) and will generally take a [Parameters](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Parameters.html) resource as input and output. There are some cases where the input and/or output will be a different resource type however.
##  4.6.1.1Providers[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#providers)
To define an operation, a method should be placed in a [Resource Provider class](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-providers) if the operation works against a resource type/instance (e.g. `Patient/$everything`), or on a [Plain Provider class](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers) if the operation works against the server (i.e. it is global and not resource specific).
#  4.6.2Type-Level Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#type-level-operations)
To implement a type-specific operation, the method should be annotated with the [@Operation](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Operation.html) tag, and should have an [@OperationParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OperationParam.html) tag for each named parameter that the input Parameters resource may be populated with. The following example shows how to implement the [`Patient/$everything`](http://hl7.org/fhir/operation-patient-everything.html) method, defined in the FHIR specification.
```
@Operation(name = "$everything", idempotent = true)
public Bundle patientTypeOperation(
      @OperationParam(name = "start") DateDt theStart, @OperationParam(name = "end") DateDt theEnd) {

   Bundle retVal = new Bundle();
   // Populate bundle with matching resources
   return retVal;
}

```

Copy
Example URL to invoke this operation: [http://fhir.example.com/Patient/$everything](http://fhir.example.com/Patient/$everything)
#  4.6.3Instance-Level Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#instance-level-operations)
To create an instance-specific operation (an operation which takes the ID of a specific resource instance as a part of its request URL), you can add a parameter annotated with the [@IdParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IdParam.html) annotation, of type [IdType](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/IdType.html). The following example shows how to implement the `Patient/[id]/$everything` operation.
```
@Operation(name = "$everything", idempotent = true)
public Bundle patientInstanceOperation(
      @IdParam IdType thePatientId,
      @OperationParam(name = "start") DateDt theStart,
      @OperationParam(name = "end") DateDt theEnd) {

   Bundle retVal = new Bundle();
   // Populate bundle with matching resources
   return retVal;
}

```

Copy
Example URL to invoke this operation: [http://fhir.example.com/Patient/123/$everything](http://fhir.example.com/Patient/123/$everything)
#  4.6.4Server-Level Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#server-level-operations)
Server-level operations do not operate on a specific resource type or instance, but rather operate globally on the server itself. The following example shows how to implement a server-level operation. Note that the `concept` parameter in the example has a cardinality of `0..*`, so a ``List` is used as the parameter type.
```
@Operation(name = "$closure")
public ConceptMap closureOperation(
      @OperationParam(name = "name") StringDt theStart,
      @OperationParam(name = "concept") List<Coding> theEnd,
      @OperationParam(name = "version") IdType theVersion) {

   ConceptMap retVal = new ConceptMap();
   // Populate bundle with matching resources
   return retVal;
}

```

Copy
Example URL to invoke this operation (HTTP request body is Parameters resource): [http://fhir.example.com/$closure](http://fhir.example.com/$closure)
#  4.6.5Using Search Parameter Types
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#using-search-parameter-types)
FHIR allows operation parameters to be of a [Search parameter type](http://hl7.org/fhir/search.html#ptypes) (e.g. token) instead of a FHIR datatype (e.g. Coding).
To use a search parameter type, any of the search parameter types listed in [Rest Operations: Search](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html) may be used. For example, the following is a simple operation method declaration using search parameters:
```
@Operation(name = "$find-matches", idempotent = true)
public Parameters findMatchesBasic(
      @OperationParam(name = "date") DateParam theDate, @OperationParam(name = "code") TokenParam theCode) {

   Parameters retVal = new Parameters();
   // Populate bundle with matching resources
   return retVal;
}

```

Copy
Example URL to invoke this operation (HTTP request body is Parameters resource): [http://fhir.example.com/$find-matches?date=2011-01-02&code=http://system%7Cvalue](http://fhir.example.com/$find-matches?date=2011-01-02&code=http://system%7Cvalue)
It is also fine to use collection types for search parameter types if you want to be able to accept multiple values. For example, a [`List<TokenParam>`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/TokenParam.html) could be used if you want to allow multiple repetitions of a given token parameter (this is analogous to the "AND" semantics in a search).
A [`TokenOrListParam`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/TokenOrListParam.html) could be used if you want to allow multiple values within a single repetition, separated by comma (this is analogous to "OR" semantics in a search).
For example:
```
@Operation(name = "$find-matches", idempotent = true)
public Parameters findMatchesAdvanced(
      @OperationParam(name = "dateRange") DateRangeParam theDate,
      @OperationParam(name = "name") List<StringParam> theName,
      @OperationParam(name = "code") TokenAndListParam theEnd) {

   Parameters retVal = new Parameters();
   // Populate bundle with matching resources
   return retVal;
}

```

Copy
If the string value to be searched contains a space character, you should encode it with a `+` sign or with `%20`, as in the following examples with an Organization named "Acme Corporation": [http://fhir.example.com/Organization?name=Acme+Corporation](http://fhir.example.com/Organization?name=Acme+Corporation)  
[http://fhir.example.com/Organization?name=Acme%20Corporation](http://fhir.example.com/Organization?name=Acme%20Corporation)
If the string value to be searched contains a literal `+` character, you should escape it with `%2B`, as in the following example with an Organization named "H+K": [http://fhir.example.com/Organization?name=H%2BK](http://fhir.example.com/Organization?name=H%2BK)
Certain strings are automatically escaped when the FHIR server parses URLs: "|" -> "%7C"  
"=>=" -> "=%3E%3D"  
"=<=" -> "=%3C%3D"  
"=>" -> "=%3E"  
"=<" -> "=%3C"
#  4.6.6Returning Multiple OUT Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#returning-multiple-out-parameters)
In all of the Operation examples above, the return type specified for the operation is a single Resource instance. This is a common pattern in FHIR defined operations. However, it is also possible for an extended operation to be defined with multiple and/or repeating OUT parameters. In this case, you can return a [Parameters](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Parameters.html) resource directly.
#  4.6.7Accepting HTTP GET
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#accepting-http-get)
The FHIR specification allows for operations to be invoked using an HTTP GET instead of an HTTP POST **only** if the following two conditions are met:
  * All parameters have primitive datatype values
  * The operation is marked as "affectsState = false". Note that early releases of the FHIR specification referred to an operation that did not affect state as "idempotent = true". It was subsequently determined that _idempotency_ was the wrong term for the concept being expressed, but the term does persist in some HAPI FHIR documentation and code.


If you are implementing an operation which should allow HTTP GET, you should mark your operation with `idempotent=true` in the [@Operation](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Operation.html). The default value for this flag is `false`, meaning that operations will not support `HTTP GET` by default.
Note that the HTTP GET form is only supported if the operation has only primitive parameters (no complex parameters or resource parameters). If a client makes a request containing a complex parameter, the server will respond with an `HTTP 405 Method Not Supported`.
#  4.6.8Manually handing Request/Response
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#manually-handing-requestresponse)
For some operations you may wish to bypass the HAPI FHIR standard request parsing and/or response generation. In this case you may use the `manualRequest = true` and/or `manualResponse = true` attributes on the [@Operation](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Operation.html) annotation.
The following example shows an operation that parses the request and generates a response (by echoing back the request).
```
@Operation(name = "$manualInputAndOutput", manualResponse = true, manualRequest = true)
public void manualInputAndOutput(
      HttpServletRequest theServletRequest, RequestDetails theRequest, HttpServletResponse theServletResponse)
      throws IOException {
   String contentType = theServletRequest.getContentType();
   // Warning - don't use the theServletRequest.getInputStream().  It may have been consumed by the FHIR server.
   // The RequestDetails keeps a copy of the input stream for you to use.
   byte[] bytes = IOUtils.toByteArray(theRequest.getInputStream());

   ourLog.info("Received call with content type {} and {} bytes", contentType, bytes.length);

   theServletResponse.setContentType("text/plain");
   theServletResponse.getWriter().write("hello");
   theServletResponse.getWriter().close();
}

```

Copy
#  4.6.9Search Limiter Service
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html#search-limiter-service)
HAPI FHIR conforms to the specification for operations like `$everything`.
But sometimes this can expose more resource types than desired.
As such, an ISearchLimiterSvc is provided that allows filtering out specific resource types for specific operations.
To remove a resource type from being included in an `$everything` patient instance or type operations, the service can be configured as follows:
```
private void filterPatientEverythingOperation() {
   // filter out Group and List
   mySearchLimiterSvc.addOmittedResourceType("$everything", "Group");
   mySearchLimiterSvc.addOmittedResourceType("$everything", "List");
}

```

Copy
Currently, the only supported operations for filtering are `$export` and `$everything` on Patient instance or type.
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
4.6 REST Operations: Extended Operations 
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
[ 4.7 Paging Search Results ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)