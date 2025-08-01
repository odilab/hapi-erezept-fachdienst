---
source: https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html
crawled: 2025-08-01T14:05:40.080228
---

# Client Annotation Client

#  3.3.1Annotation Client
[ ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html#annotation-client)
HAPI also provides a second style of client, called the _annotation-driven_ client. If you are using the [Generic (Fluent) Client](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html) do not necessarily need to read this page.
The design of the annotation-driven client is intended to be similar to that of JAX-WS, so users of that specification should be comfortable with this one. It uses a user-defined interface containing special annotated methods which HAPI binds to calls against a server.
The annotation-driven client is particularly useful if you have a server that exposes a set of specific operations (search parameter combinations, named queries, etc.) and you want to let developers have a strongly/statically typed interface to that server.
There is no difference in terms of capability between the two styles of client. There is simply a difference in programming style and complexity. It is probably safe to say that the generic client is easier to use and leads to more readable code, at the expense of not giving any visibility into the specific capabilities of the server you are interacting with.
##  3.3.1.1Defining A Restful Client Interface[](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html#defining-a-restful-client-interface)
The first step in creating an annotation-driven client is to define a restful client interface.
A restful client interface class must extend the [IRestfulClient](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IRestfulClient.html) interface, and will contain one or more methods which have been annotated with special annotations indicating which REST operation that method supports.
Below is a simple example of a resource provider which supports the [read](http://hl7.org/implement/standards/fhir/http.html#read) operation (i.e. retrieve a single resource by ID) as well as the [search](http://hl7.org/implement/standards/fhir/http.html#search) operation (i.e. find any resources matching a given criteria) for a specific search criteria.
You may notice that this interface looks a lot like the Resource Provider which is defined for use by the RESTful server. In fact, it supports all of the same annotations and is essentially identical, other than the fact that for a client you must use an interface but for a server you must use a concrete class with method implementations.
```
/**
 * All RESTful clients must be an interface which extends IBasicClient
 */
public interface IRestfulClient extends IBasicClient {

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
   Patient getResourceById(@IdParam IIdType theId);

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
   Organization getOrganizationById(@IdParam IIdType theId);

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
    *    is StringDt, but there are other possible parameter types depending on the
    *    specific search criteria.
    * @return
    *    This method returns a list of Patients. This list may contain multiple
    *    matching resources, or it may also be empty.
    */
   @Search()
   List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringType theFamilyName);
}

```

Copy
You will probably want to add more methods to your client interface.
See the [REST Operations](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html) page in the server documentation section to see examples of how these methods should look.
##  3.3.1.2Instantiating the Client[](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html#instantiating-the-client)
Once your client interface is created, all that is left is to create a FhirContext and instantiate the client and you are ready to start using it.
```
public static void main(String[] args) {
   FhirContext ctx = FhirContext.forDstu2();
   String serverBase = "http://foo.com/fhirServerBase";

   // Create the client
   IRestfulClient client = ctx.newRestfulClient(IRestfulClient.class, serverBase);

   // Try the client out! This method will invoke the server
   List<Patient> patients = client.getPatient(new StringType("SMITH"));
}

```

Copy
#  3.3.2Configuring Encoding (JSON/XML)
[ ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html#configuring-encoding-jsonxml)
Restful client interfaces that you create will also extend the interface [IRestfulClient](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/client/api/IRestfulClient.html), which comes with some helpful methods for configuring the way that the client will interact with the server.
The following snippet shows how to configure the client to explicitly request JSON or XML responses, and how to request "pretty printed" responses on servers that support this (HAPI based servers currently).
```
// Create a client
FhirContext ctx = FhirContext.forR4();
IPatientClient client = ctx.newRestfulClient(IPatientClient.class, "http://localhost:9999/");

// Request JSON encoding from the server (_format=json)
client.setEncoding(EncodingEnum.JSON);

// Request pretty printing from the server (_pretty=true)
client.setPrettyPrint(true);

```

Copy
##  3.3.2.1A Complete Example[](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html#a-complete-example)
The following is a complete example showing a RESTful client using HAPI FHIR.
```
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import java.io.IOException;
import java.util.List;

public class CompleteExampleClient {

   /**
    * This is a simple client interface. It can have many methods for various
    * searches but in this case it has only 1.
    */
   public interface ClientInterface extends IRestfulClient {

      /**
       * This is translated into a URL similar to the following:
       * http://fhir.healthintersections.com.au/open/Patient?identifier=urn:oid:1.2.36.146.595.217.0.1%7C12345
       */
      @Search
      List<Patient> findPatientsForMrn(@RequiredParam(name = Patient.SP_IDENTIFIER) Identifier theIdentifier);
   }

   /**
    * The main method here will directly call an open FHIR server and retrieve a
    * list of resources matching a given criteria, then load a linked resource.
    */
   public static void main(String[] args) throws IOException {

      // Create a client factory
      FhirContext ctx = FhirContext.forDstu2();

      // Create the client
      String serverBase = "http://fhir.healthintersections.com.au/open";
      ClientInterface client = ctx.newRestfulClient(ClientInterface.class, serverBase);

      // Invoke the client to search for patient
      Identifier identifier =
            new Identifier().setSystem("urn:oid:1.2.36.146.595.217.0.1").setValue("12345");
      List<Patient> patients = client.findPatientsForMrn(identifier);

      System.out.println("Found " + patients.size() + " patients");

      // Print a value from the loaded resource
      Patient patient = patients.get(0);
      System.out.println("Patient Last Name: " + patient.getName().get(0).getFamily());

      // Load a referenced resource
      Reference managingRef = patient.getManagingOrganization();
      Organization org = client.getOrganizationById(managingRef.getReferenceElement());

      // Print organization name
      System.out.println(org.getName());
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
3.3 Annotation Client 
* Client 
* [ 3.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/client/introduction.html)
* [ 3.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/client/get_started.html)
* [ 3.2  Generic (Fluent) Client ](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html)
* [ 3.3  Annotation Client ](https://hapifhir.io/hapi-fhir/docs/client/annotation_client.html)
* [ 3.4  Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* [ 3.5  Client Examples ](https://hapifhir.io/hapi-fhir/docs/client/examples.html)
[ 3.4 Client Configuration ](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)