---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html
crawled: 2025-08-01T14:04:50.980529
---

# Server Plain Resource Providers

#  4.3.1Resource Providers and Plain Providers
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-providers-and-plain-providers)
There are two types of providers that can be registered against a HAPI FHIR Plain Server:
  * Resource Providers are POJO classes that implement operations for a specific resource type
  * Plain Providers are POJO classes that implement operations for multiple resource types, or for system-level operations.


#  4.3.2Resource Providers
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-providers)
A Resource provider class must implement the [IResourceProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/IResourceProvider.html) interface, and will contain one or more methods which have been annotated with special annotations indicating which RESTful operation that method supports. Below is a simple example of a resource provider which supports the FHIR [read](http://hl7.org/fhir/http.html#read) operation (i.e. retrieve a single resource by ID) as well as the FHIR [search](http://hl7.org/fhir/http.html#search) operation (i.e. find any resources matching a given criteria) for a specific search criteria.
```
/**
 * All resource providers must implement IResourceProvider
 */
public class RestfulPatientResourceProvider implements IResourceProvider {

   /**
    * The getResourceType method comes from IResourceProvider, and must
    * be overridden to indicate what type of resource this provider
    * supplies.
    */
   @Override
   public Class<Patient> getResourceType() {
      return Patient.class;
   }

   /**
    * The "@Read" annotation indicates that this method supports the
    * read operation. Read operations should return a single resource
    * instance.
    *
    * @param theId
    *    The read operation takes one parameter, which must be of type
    *    IdType and must be annotated with the "@Read.IdParam" annotation.
    * @return
    *    Returns a resource matching this identifier, or null if none exists.
    */
   @Read()
   public Patient getResourceById(@IdParam IdType theId) {
      Patient patient = new Patient();
      patient.addIdentifier();
      patient.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
      patient.getIdentifier().get(0).setValue("00002");
      patient.addName().addFamily("Test");
      patient.getName().get(0).addGiven("PatientOne");
      patient.setGender(AdministrativeGenderEnum.FEMALE);
      return patient;
   }

   /**
    * The "@Search" annotation indicates that this method supports the
    * search operation. You may have many different methods annotated with
    * this annotation, to support many different search criteria. This
    * example searches by family name.
    *
    * @param theFamilyName
    *    This operation takes one parameter which is the search criteria. It is
    *    annotated with the "@Required" annotation. This annotation takes one argument,
    *    a string containing the name of the search criteria. The datatype here
    *    is StringParam, but there are other possible parameter types depending on the
    *    specific search criteria.
    * @return
    *    This method returns a list of Patients. This list may contain multiple
    *    matching resources, or it may also be empty.
    */
   @Search()
   public List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName) {
      Patient patient = new Patient();
      patient.addIdentifier();
      patient.getIdentifier().get(0).setUse(IdentifierUseEnum.OFFICIAL);
      patient.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
      patient.getIdentifier().get(0).setValue("00001");
      patient.addName();
      patient.getName().get(0).addFamily(theFamilyName.getValue());
      patient.getName().get(0).addGiven("PatientOne");
      patient.setGender(AdministrativeGenderEnum.MALE);
      return Collections.singletonList(patient);
   }
}

```

Copy
##  4.3.2.1Adding more Methods (Search, History, Create, etc.)[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#adding-more-methods-search-history-create-etc)
You will probably wish to add more methods to your resource provider. See [REST Operations](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html) for lots more examples of how to add methods for various operations.
For now, we will move on to the next step though, which is creating the actual server to hold your resource providers and deploying that. Once you have this working, you might want to come back and start adding other operations.
##  4.3.2.2Create a Server[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#create-a-server)
Once your resource providers are created, your next step is to define a server class.
HAPI provides a class called [RestfulServer](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/RestfulServer.html), which is a specialized Java Servlet. To create a server, you simply create a class which extends RestfulServer as shown in the example below.
```
/**
 * In this example, we are using Servlet 3.0 annotations to define
 * the URL pattern for this servlet, but we could also
 * define this in a web.xml file.
 */
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class ExampleRestfulServlet extends RestfulServer {

   private static final long serialVersionUID = 1L;

   /**
    * The initialize method is automatically called when the servlet is starting up, so it can
    * be used to configure the servlet to define resource providers, or set up
    * configuration, interceptors, etc.
    */
   @Override
   protected void initialize() throws ServletException {
      /*
       * The servlet defines any number of resource providers, and
       * configures itself to use them by calling
       * setResourceProviders()
       */
      List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
      resourceProviders.add(new RestfulPatientResourceProvider());
      resourceProviders.add(new RestfulObservationResourceProvider());
      setResourceProviders(resourceProviders);
   }
}

```

Copy
#  4.3.3Plain Providers
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers)
In addition to **Resource Providers** , which are resource-type specific, a second kind of provider known as **Plain Providers**. These providers can be used both to define resource operations that apply to multiple resource types, and to define operations that operate at the server level.
##  4.3.3.1Resource Operations[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#resource-operations)
Defining one provider per resource is a good strategy to keep code readable and maintainable, but it is also possible to put methods for multiple resource types in a provider class.
Providers which do not implement the [IResourceProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/IResourceProvider.html) (and therefore are not bound to one specific resource type) are known as **Plain Providers**.
A plain provider may implement any [REST operation](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html), but will generally need to explicitly state what type of resource it applies to. If the method directly returns a resource or a collection of resources (as in an [instance read](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_read) or [type search](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#type_search) operation) the resource type will be inferred automatically. If the method returns a [Bundle Resource](http://hl7.org/fhir/bundle.html), it is necessary to explicitly specify the resource type in the method annotation. The following example shows this:
```
public class MyPlainProvider {

   /**
    * This method is a Patient search, but HAPI can not automatically
    * determine the resource type so it must be explicitly stated.
    */
   @Search(type = Patient.class)
   public Bundle searchForPatients(@RequiredParam(name = Patient.SP_NAME) StringType theName) {
      Bundle retVal = new Bundle();
      // perform search
      return retVal;
   }
}

```

Copy
In addition, some methods are not resource specific. For example, the [system history](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#history) operation returns historical versions of _all resource types_ on a server, so it needs to be defined in a plain provider.
Once you have defined your plain providers, they are passed to the server in a similar way to the resource providers.
```
public class ExampleServlet extends ca.uhn.fhir.rest.server.RestfulServer {

   /**
    * Constructor
    */
   public ExampleServlet() {
      /*
       * Plain providers are passed to the server in the same way
       * as resource providers. You may pass both resource providers
       * and plain providers to the same server if you like.
       */
      registerProvider(new MyPlainProvider());

      List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
      // ...add some resource providers...
      registerProviders(resourceProviders);
   }
}

```

Copy
#  4.3.4Common Method Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#common-method-parameters)
Different RESTful methods will have different requirements in terms of the method parameters they require, as described in the [REST Operations](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html) page.
In addition, there are several parameters you may add in order to meet specific needs of your application.
##  4.3.4.1Accessing the underlying Servlet Request/Response[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#accessing-the-underlying-servlet-requestresponse)
In some cases, it may be useful to have access to the underlying HttpServletRequest and/or HttpServletResponse objects. These may be added by simply adding one or both of these objects as method parameters.
```
@Search
public List<Patient> findPatients(
      @RequiredParam(name = "foo") StringParam theParameter,
      HttpServletRequest theRequest,
      HttpServletResponse theResponse) {
   List<Patient> retVal = new ArrayList<Patient>(); // populate this
   return retVal;
}

```

Copy
#  4.3.5REST Exception/Error Handling
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#exceptions)
Within your RESTful operations, you will generally be returning resources or bundles of resources under normal operation. During execution, you may also need to propagate errors back to the client for a variety of reasons.
##  4.3.5.1Automatic Exception Handling[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#automatic-exception-handling)
By default, HAPI generates appropriate error responses for several built-in conditions. For example, if the user makes a request for a resource type that does not exist, or tries to perform a search using an invalid parameter, HAPI will automatically generate an `HTTP 400 Invalid Request`, and provide an OperationOutcome resource as response containing details about the error.
Similarly, if your method implementation throws any exceptions (checked or unchecked) instead of returning normally, the server will usually (see below) automatically generate an `HTTP 500 Internal Error` and generate an OperationOutcome with details about the exception.
##  4.3.5.2Generating Specific HTTP Error Responses[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#generating-specific-http-error-responses)
In many cases, you will want to respond to client requests with a specific HTTP error code (and possibly your own error message too). Sometimes this is a requirement of the FHIR specification. (e.g. the "validate" operation requires a response of `HTTP 422 Unprocessable Entity` if the validation fails).
Sometimes this is simply a requirement of your specific application (e.g. you want to provide application specific HTTP status codes for certain types of errors), and other times this may be a requirement of the FHIR or HTTP specifications to respond in a specific way to certain conditions.
To customize the error that is returned by HAPI's server methods, you should throw an exception which extends HAPI's [BaseServerResponseException](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/server/exceptions/BaseServerResponseException.html) class. Various exceptions which extend this class will generate a different HTTP status code.
For example, the [ResourceNotFoundException](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/server/exceptions/ResourceNotFoundException.html) causes HAPI to return an `HTTP 404 Resource Not Found`. A complete list of available exceptions is available in the [exceptions package summary](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/server/exceptions/package-summary.html).
If you wish to return an HTTP status code for which there is no pre-defined exception, you may throw the [UnclassifiedServerFailureException](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/server/exceptions/UnclassifiedServerFailureException.html), which allows you to return any status code you wish.
##  4.3.5.3Returning an OperationOutcome for Errors[](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#returning-an-operationoutcome-for-errors)
By default, HAPI will automatically generate an OperationOutcome which contains details about the exception that was thrown. You may wish to provide your own OperationOutcome instead. In this case, you may pass one into the constructor of the exception you are throwing.
```
@Read
public Patient read(@IdParam IdType theId) {
   if (databaseIsDown) {
      OperationOutcome oo = new OperationOutcome();
      oo.addIssue().setSeverity(IssueSeverityEnum.FATAL).setDetails("Database is down");
      throw new InternalErrorException(Msg.code(641) + "Database is down", oo);
   }

   Patient patient = new Patient(); // populate this
   return patient;
}

```

Copy
#  4.3.6Server Lifecycle Methods
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#server-lifecycle-methods)
Resource providers may optionally want to be notified when the server they are registered with is being destroyed, so that they can perform cleanup. In this case, a method annotated with the [@Destroy](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Destroy.html) annotation can be added (this method should be public, return `void`, and take no parameters).
This method will be invoked once by the RestfulServer when it is shutting down.
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
4.3 Resource Providers and Plain Providers 
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
[ 4.4 REST Operations: Overview ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)