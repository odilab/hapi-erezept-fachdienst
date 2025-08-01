---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html
crawled: 2025-08-01T14:05:42.168636
---

# Server Plain Customizing The Capabilitystatement

#  4.11.1Customizing the CapabilityStatement
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html#customizing-the-capabilitystatement)
Per the FHIR specification, any compliant FHIR REST server must support the FHIR [Capabilities](http://hl7.org/fhir/http.html#capabilities) operation. This operation is typically invoked by clients by requesting `[baseUrl]/metadata` from the server.
The Capabilities operation requires the server to return a valid [CapabilityStatement](http://hl7.org/fhir/capabilitystatement.html) resource describing the supported resources, operations, search parameters, and other capabilities of the server.
The HAPI FHIR [RestfulServer](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/RestfulServer.html) will automatically generate a CapabilityStatement which describes its abilities.
You can customize the generated CapabiliityStatement by creating a [server interceptor](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html) and registering it against the server. This interceptor should implement a hook method for the [SERVER_CAPABILITY_STATEMENT_GENERATED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/undefined/ca/uhn/fhir/interceptor/api/Pointcut.html#SERVER_CAPABILITY_STATEMENT_GENERATED) pointcut.
An example is shown below:
```
@Interceptor
public class CapabilityStatementCustomizer {

   @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
   public void customize(IBaseConformance theCapabilityStatement) {

      // Cast to the appropriate version
      CapabilityStatement cs = (CapabilityStatement) theCapabilityStatement;

      // Customize the CapabilityStatement as desired
      cs
         .getSoftware()
         .setName("Acme FHIR Server")
         .setVersion("1.0")
         .setReleaseDateElement(new DateTimeType("2021-02-06"));

   }

}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
4.11 Customizing the CapabilityStatement 
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
[ 4.12 OpenAPI / Swagger ](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)