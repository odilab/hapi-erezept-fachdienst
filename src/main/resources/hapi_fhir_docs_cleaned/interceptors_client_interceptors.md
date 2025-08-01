---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html
crawled: 2025-08-01T14:06:11.930400
---

# Interceptors Client Interceptors

#  11.1.1Client Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html#client-interceptors)
Client interceptors may be used to examine requests and responses before and after they are sent to the remote server.
#  11.1.2Registering Client Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html#registering-client-interceptors)
Interceptors for the client are registered against individual client instances, as shown in the example below.
```
FhirContext ctx = FhirContext.forR4();

// Create a new client instance
IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

// Register an interceptor against the client
client.registerInterceptor(new LoggingInterceptor());

// Perform client actions...
Patient pt = client.read().resource(Patient.class).withId("example").execute();

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html)
11.1 Client Interceptors 
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
[ 11.2 Filter Hook Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)