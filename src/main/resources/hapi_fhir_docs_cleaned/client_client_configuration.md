---
source: https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html
crawled: 2025-08-01T14:05:17.533693
---

# Client Client Configuration

#  3.4.1Client Configuration
[ ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#client-configuration)
This page outlines ways that the client can be configured for specific behaviour.
#  3.4.2Performance
[ ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#performance)
##  3.4.2.1Server Conformance Check[](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#server-conformance-check)
By default, the client will query the server before the very first operation to download the server's conformance/metadata statement and verify that the server is appropriate for the given client. This check is only done once per server endpoint for a given FhirContext.
This check is useful to prevent bugs or unexpected behaviour when talking to servers. It may introduce unnecessary overhead however in circumstances where the client and server are known to be compatible. The following example shows how to disable this check.
```
// Create a context
FhirContext ctx = FhirContext.forR5();

// Disable server validation (don't pull the server's metadata first)
ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

// Now create a client and use it
String serverBase = "http://hapi.fhir.org/baseR5";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

```

Copy
##  3.4.2.2Deferred Model Scanning[](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#deferred-model-scanning)
By default, HAPI will scan each model type it encounters as soon as it encounters it. This scan includes a check for all fields within the type, and makes use of reflection to do this.
While this process is not particularly significant on reasonably performant machines (one benchmark showed that this takes roughly 0.6 seconds to scan all types on one developer workstation), on some devices (e.g. Android phones where every millisecond counts) it may be desirable to defer this scan.
When the scan is deferred, objects will only be scanned when they are actually accessed, meaning that only types that are actually used in an application get scanned.
The following example shows how to defer model scanning:
```
// Create a context and configure it for deferred child scanning
FhirContext ctx = FhirContext.forR5();
ctx.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);

// Now create a client and use it
String serverBase = "http://hapi.fhir.org/baseR5";
IGenericClient client = ctx.newRestfulGenericClient(serverBase);

```

Copy
#  3.4.3Configuring the HTTP Client
[ ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#configuring-the-http-client)
REST clients (both Generic and Annotation-Driven) use [Apache HTTP Client](http://hc.apache.org/httpcomponents-client-ga/) as a provider by default (except on Android, where [OkHttp](http://square.github.io/okhttp/) is the default).
The Apache HTTP Client is very powerful and extremely flexible, but can be confusing at first to configure, because of the low-level approach that the library uses.
In many cases, the default configuration should suffice. HAPI FHIR also encapsulates some of the more common configuration settings you might want to use (socket timeouts, proxy settings, etc.) so that these can be configured through HAPI's API without needing to understand the underlying HTTP Client library.
This configuration is provided by accessing the [IRestfulClientFactory](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IRestfulClientFactory.html) class from the FhirContext.
Note that individual requests and responses can be tweaked using [Client Interceptors](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html). This approach is generally useful for configuration involving tweaking the HTTP request/response, such as adding authorization headers or logging.
##  3.4.3.1Setting Socket Timeouts[](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#setting-socket-timeouts)
The following example shows how to configure low level socket timeouts for client operations.
```
FhirContext ctx = FhirContext.forR4();

// Set how long to try and establish the initial TCP connection (in ms)
ctx.getRestfulClientFactory().setConnectTimeout(20 * 1000);

// Set how long to block for individual read/write operations (in ms)
ctx.getRestfulClientFactory().setSocketTimeout(20 * 1000);

// Create the client
IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

```

Copy
##  3.4.3.2Configuring an HTTP Proxy[](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#configuring-an-http-proxy)
The following example shows how to configure the use of an HTTP proxy in the client.
```
FhirContext ctx = FhirContext.forR4();

// Set connections to access the network via the HTTP proxy at
// example.com : 8888
ctx.getRestfulClientFactory().setProxy("example.com", 8888);

// If the proxy requires authentication, use the following as well
ctx.getRestfulClientFactory().setProxyCredentials("theUsername", "thePassword");

// Create the client
IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

```

Copy
##  3.4.3.3Using OkHttp instead of Apache HttpClient[](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#using-okhttp-instead-of-apache-httpclient)
As of HAPI FHIR 2.0, an alternate client implementation is available. This client replaces the low-level Apache HttpClient implementation with the Square [OkHttp](http://square.github.io/okhttp/) library.
Changing HTTP implementations should be mostly transparent (it has no effect on the actual FHIR semantics which are transmitted over the wire) but might be useful if you have an application that uses OkHttp in other parts of the application and has specific configuration for that library.
Note that as of HAPI FHIR 2.1, OkHttp is the default provider on Android, and will be used without any configuration being required. This is done because HttpClient is deprecated on Android and has caused problems in the past.
To use OkHttp, first add the library as a dependency to your project POM:
```
<dependency>
    <groupId>ca.uhn.hapi.fhir</groupId>
    <artifactId>hapi-fhir-client-okhttp</artifactId>
    <version>${hapi_stable_version}</version>		
</dependency>

```

Copy
Then, set the client factory to use OkHttp.
```
FhirContext ctx = FhirContext.forDstu3();

// Use OkHttp
ctx.setRestfulClientFactory(new OkHttpRestfulClientFactory(ctx));

// Create the client
IGenericClient genericClient = ctx.newRestfulGenericClient("http://localhost:9999/fhir");

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
3.4 Client Configuration 
* Client 
* [ 3.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html)
* [ 3.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* [ 3.2  Generic (Fluent) Client ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
* [ 3.3  Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* [ 3.4  Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* [ 3.5  Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
[ 3.5 Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)