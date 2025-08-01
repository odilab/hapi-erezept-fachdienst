---
source: https://hapifhir.io/hapi-fhir/docs/client/introduction.html
crawled: 2025-08-01T14:09:16.220733
---

# Client Introduction

#  3.0.1Client Introduction
[ ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html#client-introduction)
HAPI FHIR provides a built-in mechanism for connecting to FHIR REST servers.
The HAPI RESTful client is designed to be easy to set up and to allow strong compile-time type checking wherever possible.
There are two types of REST clients provided by HAPI:
  * The [Generic (Fluent) client](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html) is designed to be very flexible, yet easy to use. It allows you to build up FHIR REST invocations using a fluent API that is consistent and powerful. If you are getting started with HAPI FHIR and aren't sure which client to use, this is the client to start with.
  * The [Annotation Client](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html) client relies on static binding to specific operations to give better compile-time checking against servers with a specific set of capabilities exposed. This second model takes more effort to use, but can be useful if the person defining the specific methods to invoke is not the same person who is using those methods.


#  3.0.2HTTP Providers
[ ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html#http-providers)
The HAPI FHIR Client framework uses an underlying HTTP provider to handle the transport communication. Most of the documentation in this section describes how to perform FHIR REST operations using the client, but you may need to select a specific provider if you need to customize the transport in any way. The following example shows how to choose from several providers.
```
// Create a context and configure it for deferred child scanning
FhirContext ctx = FhirContext.forR5();

// Use Apache HttpClient 4.x client (this is the default)
ctx.setRestfulClientFactory(new ApacheRestfulClientFactory(ctx));

// Use OkHttp as the HTTP provider
ctx.setRestfulClientFactory(new OkHttpRestfulClientFactory(ctx));

// Use Apache HttpClient 5.x client
ctx.setRestfulClientFactory(new ApacheHttp5RestfulClientFactory(ctx));

// Now create a client and use it
String serverBase = "http://hapi.fhir.org/baseR5";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

```

Copy
3.0 Introduction 
* Client 
* [ 3.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html)
* [ 3.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* [ 3.2  Generic (Fluent) Client ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
* [ 3.3  Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* [ 3.4  Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* [ 3.5  Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
[ 3.1 Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)