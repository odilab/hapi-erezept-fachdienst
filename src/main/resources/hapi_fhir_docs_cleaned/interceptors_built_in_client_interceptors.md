---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html
crawled: 2025-08-01T14:08:02.611925
---

# Interceptors Built In Client Interceptors

#  11.4.1Built-In Client Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#built-in-client-interceptors)
This page describes some client interceptors that are shipped with HAPI FHIR out of the box. Of course, you are also welcome to create your own.
#  11.4.2Logging: Logging Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#logging_interceptor)
The LoggingInterceptor logs details about each request and/or response that is performed using the client. All logging is performed using SLF4j.
  * [LoggingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/LoggingInterceptor.html)
  * [LoggingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/LoggingInterceptor.java)


LoggingInterceptor is highly configurable in terms of its output. It can be configured to log simple details about requests, or detailed output including payload bodies and header contents. The following example shows how to enable LoggingInterceptor.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// Create a logging interceptor
LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

// Optionally you may configure the interceptor (by default only
// summary info is logged)
loggingInterceptor.setLogRequestSummary(true);
loggingInterceptor.setLogRequestBody(true);

// Register the interceptor with your client (either style)
IPatientClient annotationClient = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/fhir");
annotationClient.registerInterceptor(loggingInterceptor);

IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");
genericClient.registerInterceptor(loggingInterceptor);

```

Copy
#  11.4.3Security: HTTP Basic Authorization
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#security-http-basic-authorization)
The BasicAuthInterceptor adds an `Authorization` header containing an HTTP Basic Auth (username+password) token in every outgoing request.
  * [BasicAuthInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/BasicAuthInterceptor.html)
  * [BasicAuthInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/BasicAuthInterceptor.java)


The following example shows how to configure your client to use a specific username and password in every request.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// Create an HTTP basic auth interceptor
String username = "foobar";
String password = "boobear";
IClientInterceptor authInterceptor = new BasicAuthInterceptor(username, password);

// If you're using an annotation client, use this style to
// register it
IPatientClient annotationClient = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/fhir");
annotationClient.registerInterceptor(authInterceptor);

// If you're using a generic client, use this instead
IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");
genericClient.registerInterceptor(authInterceptor);

```

Copy
#  11.4.4Security: HTTP Bearer Token Authorization
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#security-http-bearer-token-authorization)
The BearerTokenAuthInterceptor can be used to add an `Authorization` header containing a bearer token (typically used for OIDC/OAuth2/SMART security flows) to every outgoing request.
  * [BearerTokenAuthInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/BearerTokenAuthInterceptor.html)
  * [BearerTokenAuthInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/BearerTokenAuthInterceptor.java)


The following example shows how to configure your client to inject a bearer token authorization header into every request.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// In reality the token would have come from an authorization server
String token = "3w03fj.r3r3t";

BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);

// Register the interceptor with your client (either style)
IPatientClient annotationClient = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/fhir");
annotationClient.registerInterceptor(authInterceptor);

IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");
annotationClient.registerInterceptor(authInterceptor);

```

Copy
#  11.4.5Misc: Add Headers to Request
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#misc-add-headers-to-request)
The AdditionlRequestHeadersInterceptor can be used to add arbitrary headers to each request created by the client.
  * [AdditionalRequestHeadersInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/AdditionalRequestHeadersInterceptor.html)
  * [AdditionalRequestHeadersInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/AdditionalRequestHeadersInterceptor.java)


The following example shows how to configure your client to inject a bearer token authorization header into every request.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// Create a client
IGenericClient client = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

// Register an additional headers interceptor and add one header to it
AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
interceptor.addHeaderValue("X-Message", "Help I'm a Bug");
client.registerInterceptor(interceptor);

IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");
client.registerInterceptor(interceptor);

```

Copy
Note that headers can also be added to individual [Generic Client](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html) invocations inline. The example below will produce the same additional request header as the example above, although it applies only to the one request.
```
Patient p = client.read()
      .resource(Patient.class)
      .withId(123L)
      .withAdditionalHeader("X-Message", "Help I'm a Bug")
      .execute();

```

Copy
#  11.4.6Misc: Add Cookies to Request
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#misc-add-cookies-to-request)
The CookieInterceptor can be used to add an HTTP Cookie header to each request created by the client.
  * [CookieInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/CookieInterceptor.html)
  * [CookieInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/CookieInterceptor.java)


The following example shows how to configure your client to inject a bearer token authorization header into every request.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// Create a cookie interceptor. This cookie will have the name "mycookie" and
// the value "Chips Ahoy"
CookieInterceptor interceptor = new CookieInterceptor("mycookie=Chips Ahoy");

// Register the interceptor with your client (either style)
IPatientClient annotationClient = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/fhir");
annotationClient.registerInterceptor(interceptor);

IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");
annotationClient.registerInterceptor(interceptor);

```

Copy
#  11.4.7Multitenancy: Add tenant ID to path
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#multitenancy-add-tenant-id-to-path)
When communicating with a server that supports [URL Base Multitenancy](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html#url-base-multitenancy), an extra element needs to be added to the request path. This can be done by simply appending the path to the base URL supplied to the client, but it can also be dynamically appended using this interceptor.
  * [UrlTenantSelectionInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/UrlTenantSelectionInterceptor.html)
  * [UrlTenantSelectionInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/UrlTenantSelectionInterceptor.java)


```
FhirContext ctx = FhirContext.forR4();

// Create the client
IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

// Register the interceptor
UrlTenantSelectionInterceptor tenantSelection = new UrlTenantSelectionInterceptor();
genericClient.registerInterceptor(tenantSelection);

// Read from tenant A
tenantSelection.setTenantId("TENANT-A");
Patient patientA =
      genericClient.read().resource(Patient.class).withId("123").execute();

// Read from tenant B
tenantSelection.setTenantId("TENANT-B");
Patient patientB =
      genericClient.read().resource(Patient.class).withId("456").execute();

```

Copy
#  11.4.8Performance: GZip Outgoing Request Bodies
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#performance-gzip-outgoing-request-bodies)
The GZipContentInterceptor compresses outgoing contents. With this interceptor, if the client is transmitting resources to the server (e.g. for a create, update, transaction, etc.) the content will be GZipped before transmission to the server.
  * [GZipContentInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/apache/GZipContentInterceptor.html)
  * [GZipContentInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/apache/GZipContentInterceptor.java)


The following example shows how to enable the GZipContentInterceptor.
```
// Create a context and get the client factory so it can be configured
FhirContext ctx = FhirContext.forR4();
IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

// Register the interceptor with your client (either style)
IPatientClient annotationClient = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/fhir");
annotationClient.registerInterceptor(new GZipContentInterceptor());

```

Copy
#  11.4.9Capture: Programmatically Capturing Request/Response Details
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html#capture-programmatically-capturing-requestresponse-details)
The CapturingInterceptor can be used to capture the details of the last request that was sent by the client, as well as the corresponding response that was received.
  * [CapturingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/CapturingInterceptor.html)
  * [CapturingInterceptor Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/CapturingInterceptor.java)


A separate but related interceptor called ThreadLocalCapturingInterceptor also captures request/response pairs but stores these in a Java ThreadLocal so it is suitable for use in multithreaded environments.
  * [ThreadLocalCapturingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/ca/uhn/fhir/rest/client/interceptor/ThreadLocalCapturingInterceptor.html)
  * [ThreadLocalCapturingInterceptor Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/interceptor/ThreadLocalCapturingInterceptor.java)


[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_pointcuts.html)
11.4 Built-In Client Interceptors 
* Interceptors 
* [ 11.0  Interceptors Overview ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html)
* [ 11.1  Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html)
* [ 11.2  Filter Hook Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html)
* [ 11.3  Client Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_pointcuts.html)
* [ 11.4  Built-In Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html)
* [ 11.5  Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html)
* [ 11.6  Server Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_pointcuts.html)
* [ 11.7  Built-In Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html)
* [ 11.8  7.0.0 Migration Guide ](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html)
[ 11.5 Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)