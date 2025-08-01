---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html
crawled: 2025-08-01T14:05:28.279801
---

# Server Plain Introduction

#  4.1.1Creating A Plain Server
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#creating-a-plain-server)
HAPI FHIR provides a built-in mechanism for adding FHIR's RESTful Server capabilities to your applications. The HAPI RESTful Server is Servlet based, so it should be easy to deploy to any of the many compliant containers that exist.
Setup is mostly done using simple annotations, which means that it should be possible to create a FHIR compliant server quickly and easily.
#  4.1.2Defining Resource Providers
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#defining-resource-providers)
The first step in creating a FHIR RESTful Server is to define one or more resource providers. A resource provider is a class which is able to supply exactly one type of resource to be served up.
For example, if you wish to allow your server to serve up Patient, Observation and Location resources, you will need three resource providers.
See [Resource Providers](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html) for information on how to create these classes.
#  4.1.3Deploying
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#deploying)
Once you have created your resource providers and your restful server class, you can bundle these into a WAR file and you are ready to deploy to any JEE container (Tomcat, Websphere, Glassfish, etc).
Assuming that you are using a build tool such as Apache Maven to build your project, you can create your WAR file simply by building the project.
```
mvn install

```

Once the build is complete, a file called `[projectname].war` will be found in the `target/` directory. This file can be deployed to the container/application server of your choice. Deploying WAR files to an application server is beyond the scope of this page, but there are many good tutorials on how to do this available on the web.
#  4.1.4Testing Using Jetty
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#testing-using-jetty)
Many of the HAPI FHIR sample projects take advantage of the _Maven Jetty Plugin_ to provide an easy testing mechanism. This plugin can be used to automatically compile your code and deploy it to a local server (without needing to install anything additional) using a simple command.
To execute the Maven Jetty Plugin:
```
mvn jetty:run

```

#  4.1.5Server Base URL (Web Address)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#server-base-url-web-address)
The server will return data in a number of places that includes the complete "identity" of a resource. Identity in this case refers to the web address that a user can use to access the resource.
For instance, if your server is hosted at `http://foo.com/fhir`, and your resource provider returns a Patient resource with the ID `123`, the server should translate that ID to `http://foo.com/fhir/Patient/123`. This complete identity URL is used in response headers (e.g. the `Location` header) as well as in Bundle resource contents (e.g. the Bundle entry "fullUrl" property).
The server will attempt to determine what the base URL should be based on what the request it receives looks like, but if it is not getting the right address you may wish to use a different "address strategy".
##  4.1.5.1Hardcoded Address Strategy[](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#hardcoded-address-strategy)
The simplest way to do this is to configure the server to use a hardcoded base URL, which means that the server won't try to figure out the "http://foo.com/fhir" part of the URL but will instead just use a fixed value you supply.
The hardcoded address strategy is particularly useful in cases where the server is not aware of the external address used to access it, as is often the case in network architectures where a reverse proxy, API gateway, or other network equipment is present.
This strategy is shown in the following example:
```
public class MyServlet extends ca.uhn.fhir.rest.server.RestfulServer {

   /**
    * Constructor
    */
   public MyServlet() {

      String serverBaseUrl = "http://foo.com/fhir";
      setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));

      // ...add some resource providers, etc...
      List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
      setResourceProviders(resourceProviders);
   }
}

```

Copy
##  4.1.5.2Other Strategies[](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#other-strategies)
See the [IServerAddressStrategy](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/IServerAddressStrategy.html) JavaDoc (specifically the list of "All Known Implementing Classes") to see other strategies that are available.
#  4.1.6Capability Statement / Server Metadata
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#capabilities)
The HAPI FHIR RESTful Server will automatically generate a Server [CapabilityStatement](http://hl7.org/fhir/capabilitystatement.html) resource (or a Server [Conformance](https://www.hl7.org/fhir/DSTU2/conformance.html) resource for FHIR DSTU2).
This statement is automatically generated based on the various annotated methods which are provided to the server. This behaviour may be modified by creating a new class containing a method annotated with a [@Metadata annotation](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#system_capabilities) and then passing an instance of that class to the [setServerConformanceProvider(Object)](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/RestfulServer.html#setServerConformanceProvider\(java.lang.Object\)) method on your RestfulServer instance.
##  4.1.6.1Enhancing the Generated CapabilityStatement[](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#enhancing-the-generated-capabilitystatement)
If you have a need to add your own content (special extensions, etc.) to your server's conformance statement, but still want to take advantage of HAPI's automatic CapabilityStatement generation, you may wish to extend the built-in generator class.
The generator class is version-specific, so you will need to extend the class appropriate for the version of FHIR you are implementing:
  * DSTU3: [ca.uhn.fhir.rest.server.provider.dstu2.ServerConformanceProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-dstu2/ca/uhn/fhir/rest/server/provider/dstu2/ServerConformanceProvider.html)
  * DSTU3: [org.hl7.fhir.dstu3.hapi.rest.server.ServerCapabilityStatementProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-dstu3/org/hl7/fhir/dstu3/hapi/rest/server/ServerCapabilityStatementProvider.html)
  * R4 and later: [ca.uhn.fhir.rest.server.provider.ServerCapabilityStatementProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/provider/ServerCapabilityStatementProvider.html)


In your own class extending this class, you can override the `getServerConformance()` method to obtain the built-in conformance statement and then add your own information to it.
#  4.1.7Controlling Response Contents / Encoding / Formatting
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#controlling-response-contents-encoding-formatting)
FHIR allows for a number of special behaviours where only certain portions of resources are returned, instead of the entire resource body. These behaviours are automatically supported in HAPI FHIR Plain Server and no additional effort needs to be taken.
The following behaviours are automatically supported by the HAPI server:
**Parameter** | **Description**  
---|---  
_summary=true |  Resources will be returned with any elements not marked as summary elements omitted.   
_summary=text |  Only the narrative portion of returned resources will be returned. For a read/vread operation, the narrative will be served with a content type of `text/html`. for other operations, a Bundle will be returned but resources will only include the text element.   
_summary=data |  The narrative (text) portion of the resource will be omitted.   
_summary=count |  For a search, only Bundle.count will be returned.   
_elements=[element names] |  Only the given top level elements of returned resources will be returned, e.g for a Patient search: `_elements=name,contact`  
_pretty=true |  Request that the server pretty-print the response (indent the data over multiple lines for easier human readability).   
##  4.1.7.1Extended Elements Support[](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html#extended-elements-support)
The HAPI FHIR server may be configured using the `RestfulServer#setElementsSupport` to enable extended support for the `_elements` filter.
In standard mode, elements are supported exactly as described in the [Elements Documentation](http://hl7.org/fhir/search.html#elements) in the FHIR specification.
In extended mode, HAPI FHIR provides the same behaviour as described in the FHIR specification, but also enabled the following additional options:
  * Values may be prepended using Resource names in order to apply the elements filter to multiple resources. For example, the following parameter could be used to apply elements filtering to both the DiagnosticReport and Observation resource in a search result:
```
http://base/Patient?_elements=DiagnosticReport.subject,DiagnosticReport.result,Observation.value

```

Copy
  * Values may be prepended with a wildcard star in order to apply them to all resource types. For example, the following parameter could be used to include the `subject` field in all resource types:
```
http://base/Patient?_elements=*.subject

```

Copy
  * Values may include complex paths. For example, the following parameter could be used to include only the code on a coded element:
```
http://base/Patient?_elements=Procedure.reasonCode.coding.code

```

Copy
  * Elements may be excluded using the `:exclude` modifier on the elements parameter. For example, the following parameter could be used to remove the resource metadata (meta) element from all resources in the response:
```
http://base/Patient?_elements:exclude=*.meta

```

Copy
Note that this can be used to suppress the `SUBSETTED` tag which is automatically added to resources when an `_elements` parameter is applied.


[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html)
4.1 Plain Server Introduction 
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
[ 4.2 Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)