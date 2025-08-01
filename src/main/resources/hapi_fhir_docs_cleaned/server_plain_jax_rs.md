---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html
crawled: 2025-08-01T14:06:10.896593
---

# Server Plain Jax Rs

#  4.10.1JAX-RS Server
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html#jax-rs-server)
The HAPI FHIR Plain Server ([RestfulServer](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/RestfulServer.html)) is implemented as a standard JEE Servlet, meaning that it can be deployed in any compliant JEE web container.
For users who already have an existing JAX-RS infrastructure, and who would like to use that technology for their FHIR stack as well, a module exists which implements the server using [JAX-RS technology](https://jax-rs-spec.java.net/nonav/2.0/apidocs/index.html).
The JAX-RS module is a community-supported module that was not developed by the core HAPI FHIR team. Before deciding to use the HAPI FHIR JAX-RS module, please be aware that it does not have as complete of support for the full FHIR REST specification as the Plain Server. If you need a feature that is missing, please consider adding it and making a pull request! 
##  4.10.1.1Features[](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html#features)
The server currently supports:
  * Automatic [Capability Statement Generation](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#capabilities)
  * [@Read](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Read.html)
  * [@RSearch](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Search.html)
  * [@Create](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Create.html)
  * [@Update](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Update.html)
  * [@Delete](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Delete.html)
  * [@Operation](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Operation.html)


The primary intention for this project is to ensure that other web technologies (JAX-RS in this case) can be used together with the base-server functionality. An example server can be found in the Git repo [here](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-jaxrsserver-example).
#  4.10.2JAX-RS Implementation specifics
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html#jax-rs-implementation-specifics)
The set-up of a JAX-RS server goes beyond the scope of this documentation. The implementation of the server follows the same pattern as the standard server. It is required to put the correct [annotation](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html) on the methods in the [Resource Providers](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html) in order to be able to call them.
Implementing a JAX-RS Resource Provider requires some JAX-RS annotations. The [@Path](https://docs.oracle.com/javaee/6/api/javax/ws/rs/Path.html) annotation needs to define the resource path. The `@Produces[](https://docs.oracle.com/javaee/6/api/javax/ws/rs/Produces.html)` annotation needs to declare the produced formats. The constructor needs to pass the class of the object explicitly in order to avoid problems with proxy classes in a Java EE environment.
It is necessary to extend the abstract class [AbstractJaxRsResourceProvide](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jaxrsserver-base/ca/uhn/fhir/jaxrs/server/AbstractJaxRsResourceProvider.html).
```
@Path("/Patient")
@Produces({MediaType.APPLICATION_JSON, Constants.CT_FHIR_JSON, Constants.CT_FHIR_XML})
public class JaxRsPatientRestProvider extends AbstractJaxRsResourceProvider<Patient> {

   public JaxRsPatientRestProvider() {
      super(JaxRsPatientRestProvider.class);
   }

```

Copy
##  4.10.2.1Extended Operations[](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html#extended-operations)
[Extended Operations](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html) require the correct JAX-RS ( [@Path](https://docs.oracle.com/javaee/6/api/javax/ws/rs/Path.html), [@GET](https://docs.oracle.com/javaee/6/api/javax/ws/rs/GET.html) or [@POST](https://docs.oracle.com/javaee/6/api/javax/ws/rs/POST.html) annotations. The body of the method needs to call the method [AbstractJaxRsResourceProvider#customOperation](/hapi-fhir/apidocs/hapi-fhir-jaxrsserver-base/ca/uhn/fhir/jaxrs/server/AbstractJaxRsResourceProvider.html#customOperation(java.lang.String,ca.uhn.fhir.rest.api.RequestTypeEnum,java.lang.String,java.lang.String,ca.uhn.fhir.rest.api.RestOperationTypeEnum) with the correct parameters. The server will then call the method with corresponding name.
```
@GET
@Path("/{id}/$someCustomOperation")
public Response someCustomOperationUsingGet(@PathParam("id") String id, String resource) throws Exception {
   return customOperation(
         resource,
         RequestTypeEnum.GET,
         id,
         "$someCustomOperation",
         RestOperationTypeEnum.EXTENDED_OPERATION_INSTANCE);
}

@Operation(
      name = "someCustomOperation",
      idempotent = true,
      returnParameters = {@OperationParam(name = "return", type = StringDt.class)})
public Parameters someCustomOperation(@IdParam IdType myId, @OperationParam(name = "dummy") StringDt dummyInput) {
   Parameters parameters = new Parameters();
   parameters.addParameter().setName("return").setValue(new StringType("My Dummy Result"));
   return parameters;
}

```

Copy
In order to create the conformance profile, a conformance provider class needs to be deployed which exports the provider's conformance statements. These providers need to be returned as the result of the method [AbstractJaxRsConformanceProvider#getProviders](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jaxrsserver-base/ca/uhn/fhir/jaxrs/server/AbstractJaxRsConformanceProvider.html#getProviders\(\)). This method is called once, during [PostConstruct](https://docs.oracle.com/javaee/6/api/javax/annotation/PostConstruct.html).
```
@Path("")
@Stateless
@Produces({MediaType.APPLICATION_JSON, Constants.CT_FHIR_JSON, Constants.CT_FHIR_XML})
public class JaxRsConformanceProvider extends AbstractJaxRsConformanceProvider {

   @EJB
   private JaxRsPatientRestProvider provider;

   public JaxRsConformanceProvider() {
      super("My Server Description", "My Server Name", "My Server Version");
   }

   @Override
   protected ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> getProviders() {
      ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> map =
            new ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider>();
      map.put(JaxRsConformanceProvider.class, this);
      map.put(JaxRsPatientRestProvider.class, provider);
      return map;
   }
}

```

Copy
#  4.10.3A Complete Example
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html#a-complete-example)
A complete example showing how to implement a JAX-RS RESTful server can be found in our Git repo here:
  * [hapi-fhir-jaxrsserver-example](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-jaxrsserver-example)


[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
4.10 JAX-RS Support 
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
[ 4.11 Customizing the CapabilityStatement ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)