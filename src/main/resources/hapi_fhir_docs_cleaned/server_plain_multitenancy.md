---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html
crawled: 2025-08-01T14:05:52.627087
---

# Server Plain Multitenancy

#  4.9.1Multitenancy
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html#multitenancy)
If you wish to allow a single endpoint to support multiple tenants, you may supply the server with a multitenancy provider.
This means that additional logic will be performed during request parsing to determine a tenant ID, which will be supplied to resource providers. This can be useful in servers that have multiple distinct logical pools of resources hosted on the same infrastructure.
#  4.9.2URL Base Multitenancy
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html#url-base-multitenancy)
Using URL Base Multitenancy means that an additional element is added to the path of each resource between the server base URL and the resource name. For example, if your restful server is deployed to `http://acme.org:8080/baseDstu3` and a client wishes to access Patient 123 for Tenant "FOO", the resource ID (and URL to fetch that resource) would be `http://acme.org:8080/FOO/Patient/123`.
To enable this mode on your server, simply provide the [UrlBaseTenantIdentificationStrategy](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/tenant/UrlBaseTenantIdentificationStrategy.html) to the server as shown below:
```
public class MyServer extends RestfulServer {

   @Override
   protected void initialize() {

      setTenantIdentificationStrategy(new UrlBaseTenantIdentificationStrategy());

      // ... do other initialization ...
   }
}

```

Copy
Your resource providers can then use a RequestDetails parameter to determine the tenant ID:
```
public class MyPatientResourceProvider implements IResourceProvider {

   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return Patient.class;
   }

   @Read
   public Patient read(RequestDetails theRequestDetails, @IdParam IdType theId) {

      String tenantId = theRequestDetails.getTenantId();
      String resourceId = theId.getIdPart();

      // Use these two values to fetch the patient

      return new Patient();
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
4.9 Multitenancy 
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
[ 4.10 JAX-RS Support ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)