---
source: https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html
crawled: 2025-08-01T14:09:25.501110
---

# Security Balp Interceptor

#  12.5.1Basic Audit Log Patterns (BALP) Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#basic-audit-log-patterns-balp-interceptor)
The IHE [Basic Audit Log Patterns](https://profiles.ihe.net/ITI/BALP/) implementation guide describes a set of workflows and data models for the creation of [AuditEvent](http://hl7.org/fhir/AuditEvent.html) resources based on user/client actions.
HAPI FHIR provides an interceptor that can be registered against a server, and will observe events on that server and automatically generate AuditEvent resources which are conformant to the appropriate profiles within the BALP specification.
This interceptor implements the following profiles:
BALP Profile | Trigger | Triggering Pointcut  
---|---|---  
[Create](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.Create.html) |  Performed when a resource has been created that is not a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_CREATED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_CREATED)  
[PatientCreate](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.PatientCreate.html) |  Performed when a resource has been created that is a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_CREATED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_CREATED)  
[Read](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.Read.html) |  Performed when a resource has been read that is not a member of the Patient compartment. Note that other extended operations which expose individual resource data may also trigger the creation of an AuditEvent with this profile. For example, the `$diff` operation exposes data within a resource, so it will also trigger this event.  |  [STORAGE_PRESHOW_RESOURCES](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRESHOW_RESOURCES)  
[PatientRead](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.PatientRead.html) |  Performed when a resource has been read that is a member of the Patient compartment. Note that other extended operations which expose individual resource data may also trigger the creation of an AuditEvent with this profile. For example, the `$diff` operation exposes data within a resource, so it will also trigger this event.  |  [STORAGE_PRESHOW_RESOURCES](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRESHOW_RESOURCES)  
[Update](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.Update.html) |  Performed when a resource has been updated that is not a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_UPDATED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_UPDATED)  
[PatientUpdate](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.PatientUpdate.html) |  Performed when a resource has been updated that is a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_UPDATED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_UPDATED)  
[Delete](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.Delete.html) |  Performed when a resource has been deleted that is not a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_DELETED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_DELETED)  
[PatientDelete](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.PatientDelete.html) |  Performed when a resource has been deleted that is a member of the Patient compartment.  |  [STORAGE_PRECOMMIT_RESOURCE_DELETED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRECOMMIT_RESOURCE_DELETED)  
[Query](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.Query.html) |  Performed when a non-patient-oriented search is performed. This refers to a search that is returning data that is not in a specific patient compartment.  |  [STORAGE_PRESHOW_RESOURCES](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRESHOW_RESOURCES)  
[PatientQuery](https://profiles.ihe.net/ITI/BALP/StructureDefinition-IHE.BasicAudit.PatientQuery.html) |  Performed when a patient-oriented search is performed. This refers to a search that returns data in a specific patient compartment.  |  [STORAGE_PRESHOW_RESOURCES](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PRESHOW_RESOURCES)  
#  12.5.2Architecture
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#architecture)
The HAPI FHIR BALP infrastructure consists of the following components:
  * The [BalpAuditCaptureInterceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/BalpAuditCaptureInterceptor.html) is the primary interceptor, which you register against a HAPI FHIR [JPA Server](https://hapifhir.io/hapi-fhir/docs/server_jpa/).
  * The [IBalpAuditEventSink](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/IBalpAuditEventSink.html) is an interface which receives generated AuditEvents and processes them. Appropriate processing will depend on your use case, but could be storing them locally, transmitting them to a remote server, logging them to a syslog, or even selectively dropping them. See [Audit Event Sink](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#audit-event-sink) below.
  * The [IBalpAuditContextServices](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/IBalpAuditContextServices.html) is an interface which supplies context information for a given client action. When generating a BALP conformant AuditEvent resource, the BalpAuditCaptureInterceptor will automatically populate most of the AuditEvent with details such as the _entity_ (ie. the resource being accessed or modified) and the _server_ (the FHIR server being used to transmit or store the information). However, other information such as the agent and the user (ie. the FHIR client and the physical user) are not known to HAPI FHIR and must be supplied for each request. This interface supplies these details.


#  12.5.3Audit Event Sink
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#audit-event-sink)
The BALP [IBalpAuditEventSink](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/IBalpAuditEventSink.html) receives and handles generated audit events.
This interface is designed to support custom implementations, so you can absolutely create your own. HAPI FHIR ships with the following implementation:
  * [AsyncMemoryQueueBackedFhirClientBalpSink](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/AsyncMemoryQueueBackedFhirClientBalpSink.html) uses an HTTP/REST FHIR client to transmit AuditEvents to a FHIR server endpoint. This can be a local or a remote endpoint, and can be a server with any version of FHIR. Messages are transmitted asynchronously using an in-memory queue.


If you create an implementation of this interface that you think would be useful to others, we would welcome community contributions!
#  12.5.4Audit Context Services
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#audit-context-services)
In order to use this interceptor, you must suply an instance of [IBalpAuditContextServices](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/balp/IBalpAuditContextServices.html). This interface supplies the information about each request that the interceptor cannot determine on its own, such as the identity of the requesting user and the requesting client.
The implementation of this interface for the [public HAPI FHIR server](https://hapi.fhir.org) is available [here](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-uhnfhirtest/src/main/java/ca/uhn/fhirtest/config/FhirTestBalpAuditContextServices.java).
#  12.5.5Example
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html#example)
The following example shows a simple implementation of the Context Services:
```
public class ExampleBalpAuditContextServices implements IBalpAuditContextServices {

   /**
    * Here we are just hard-coding a simple display name. In a real implementation
    * we should use the actual identity of the requesting client.
    */
   @Nonnull
   @Override
   public Reference getAgentClientWho(RequestDetails theRequestDetails) {
      Reference client = new Reference();
      client.setDisplay("Growth Chart Application");
      client.getIdentifier().setSystem("http://example.org/clients").setValue("growth_chart");
      return client;
   }

   /**
    * Here we are just hard-coding a simple display name. In a real implementation
    * we should use the actual identity of the requesting user.
    */
   @Nonnull
   @Override
   public Reference getAgentUserWho(RequestDetails theRequestDetails) {
      Reference user = new Reference();
      user.getIdentifier().setSystem("http://example.org/users").setValue("my_username");
      return user;
   }
}

```

Copy
And the following example shows a HAPI FHIR Basic Server with the BALP interceptor wired in:
```
public class MyServer extends RestfulServer {

   /**
    * Constructor
    */
   public MyServer() {
      super(FhirContext.forR4Cached());
   }

   @Override
   protected void initialize() throws ServletException {
      // Register your resource providers and other interceptors here...

      /*
       * Create our context sservices object
       */
      IBalpAuditContextServices contextServices = new ExampleBalpAuditContextServices();

      /*
       * Create our event sink
       */
      FhirContext fhirContext = FhirContext.forR4Cached();
      String targetUrl = "http://my.fhir.server/baseR4";
      List<Object> clientInterceptors = List.of(
            // We'll register an auth interceptor against the sink FHIR client so that
            // credentials get passed to the target server. Of course in a real implementation
            // you should never hard code credentials like this.
            new BasicAuthInterceptor("username", "password"));
      IBalpAuditEventSink eventSink =
            new AsyncMemoryQueueBackedFhirClientBalpSink(fhirContext, targetUrl, clientInterceptors);
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
12.5 Basic Audit Log Pattern (BALP) 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
[ 12.6 Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)