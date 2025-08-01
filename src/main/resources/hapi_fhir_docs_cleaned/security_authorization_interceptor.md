---
source: https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html
crawled: 2025-08-01T14:05:12.141719
---

# Security Authorization Interceptor

#  12.1.1Authorization Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorization-interceptor)
HAPI FHIR 1.5 introduced a new interceptor: [AuthorizationInterceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/auth/AuthorizationInterceptor.html).
This interceptor can help with the complicated task of determining whether a user has the appropriate permission to perform a given task on a FHIR server. This is done by declaring a set of rules that can selectively allow (whitelist) and/or selectively block (blacklist) requests.
  * [AuthorizationInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/auth/AuthorizationInterceptor.html)
  * [AuthorizationInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/auth/AuthorizationInterceptor.java)


AuthorizationInterceptor has been well tested, but it is impossible to predict every scenario and environment in which HAPI FHIR will be used. Use with caution, and do lots of testing! We welcome feedback and suggestions on this feature. Please get in touch if you'd like to help test, have suggestions, etc. 
The AuthorizationInterceptor works by allowing you to declare permissions based on an individual request coming in. In other words, you could have code that examines an incoming request and determines that it is being made by a Patient with ID 123. You could then declare that the requesting user has access to read and write any resource in compartment "Patient/123", which corresponds to any Observation, MedicationOrder etc with a subject of "`Patient/123`". On the other hand, another request might be determined to belong to an administrator user, and could be declared to be allowed to do anything.
The AuthorizationInterceptor is used by subclassing it and then registering your subclass with the `RestfulServer`. The following example shows a subclassed interceptor implementing some basic rules:
```
public class PatientAndAdminAuthorizationInterceptor extends AuthorizationInterceptor {

   @Override
   public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {

      // Process authorization header - The following is a fake
      // implementation. Obviously we'd want something more real
      // for a production scenario.
      //
      // In this basic example we have two hardcoded bearer tokens,
      // one which is for a user that has access to one patient, and
      // another that has full access.
      IdType userIdPatientId = null;
      boolean userIsAdmin = false;
      String authHeader = theRequestDetails.getHeader("Authorization");
      if ("Bearer dfw98h38r".equals(authHeader)) {
         // This user has access only to Patient/1 resources
         userIdPatientId = new IdType("Patient", 1L);
      } else if ("Bearer 39ff939jgg".equals(authHeader)) {
         // This user has access to everything
         userIsAdmin = true;
      } else {
         // Throw an HTTP 401
         throw new AuthenticationException(Msg.code(644) + "Missing or invalid Authorization header value");
      }

      // If the user is a specific patient, we create the following rule chain:
      // Allow the user to read anything in their own patient compartment
      // Allow the user to write anything in their own patient compartment
      // If a client request doesn't pass either of the above, deny it
      if (userIdPatientId != null) {
         return new RuleBuilder()
               .allow()
               .read()
               .allResources()
               .inCompartment("Patient", userIdPatientId)
               .andThen()
               .allow()
               .write()
               .allResources()
               .inCompartment("Patient", userIdPatientId)
               .andThen()
               .denyAll()
               .build();
      }

      // If the user is an admin, allow everything
      if (userIsAdmin) {
         return new RuleBuilder().allowAll().build();
      }

      // By default, deny everything. This should never get hit, but it's
      // good to be defensive
      return new RuleBuilder().denyAll().build();
   }
}

```

Copy
The core rules support restricting access by resource type, resource instance, and compartment. The rules also support query filters expressed by FHIR queries - e.g. `code:above=http://loinc.org|55399-0` to restrict Observations to just the diabetes panel. To use query filters, you must activate the [RuleFilteringConsentService.](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/consent/RuleFilteringConsentService.html)
```
ConsentInterceptor consentInterceptor = new ConsentInterceptor();
consentInterceptor.registerConsentService(new RuleFilteringConsentService(theAuthorizationInterceptor));
restfulServer.registerInterceptor(consentInterceptor);

```

Copy
##  12.1.1.1Using AuthorizationInterceptor in a REST Server[](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#using-authorizationinterceptor-in-a-rest-server)
The AuthorizationInterceptor works by examining the client request in order to determine whether "write" operations are legal, and looks at the response from the server in order to determine whether "read" operations are legal.
#  12.1.2Authorizing Read Operations
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-read-operations)
When authorizing a read operation, the AuthorizationInterceptor always allows client code to execute and generate a response. It then examines the response that would be returned before actually returning it to the client, and if rules do not permit that data to be shown to the client the interceptor aborts the request.
Note that there are performance implications to this mechanism, since an unauthorized user can still cause the server to fetch data even if they won't get to see it. This mechanism should be comprehensive however, since it will prevent clients from using various features in FHIR (e.g. `_include` or `_revinclude`) to "trick" the server into showing them data they shouldn't be allowed to see.
See the following diagram for an example of how this works.
![Write Authorization](https://hapifhir.io/hapi-fhir/docs/images/hapi_authorizationinterceptor_read_normal.svg)
#  12.1.3Authorizing Write Operations
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-write-operations)
Write operations (create, update, etc.) are typically authorized by the interceptor by examining the parsed URL and making a decision about whether to authorize the operation before allowing Resource Provider code to proceed. This means that client code will not have a chance to execute and create resources that the client does not have permissions to create.
See the following diagram for an example of how this works.
![Write Authorization](https://hapifhir.io/hapi-fhir/docs/images/hapi_authorizationinterceptor_write_normal.svg)
#  12.1.4Authorizing Sub-Operations
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-sub-operations)
There are a number of situations where the REST framework doesn't actually know exactly what operation is going to be performed by the implementing server code. For example, if your server implements a `conditional update` operation, the server might not know which resource is actually being updated until the server code is executed.
Because client code is actually determining which resources are being modified, the server can not automatically apply security rules against these modifications without being provided hints from client code.
In this type of situation, it is important to manually notify the interceptor chain about the "sub-operation" being performed. The following snippet shows how to notify interceptors about a conditional create.
```
@Update()
public MethodOutcome update(
      @IdParam IdType theId,
      @ResourceParam Patient theResource,
      @ConditionalUrlParam String theConditionalUrl,
      ServletRequestDetails theRequestDetails,
      IInterceptorBroadcaster theInterceptorBroadcaster) {

   // If we're processing a conditional URL...
   if (isNotBlank(theConditionalUrl)) {

      // Pretend we've done the conditional processing. Now let's
      // notify the interceptors that an update has been performed
      // and supply the actual ID that's being updated
      IdType actual = new IdType("Patient", "1123");
   }

   // In a real server, perhaps we would process the conditional
   // request differently and follow a separate path. Either way,
   // let's pretend there is some storage code here.
   theResource.setId(theId.withVersion("2"));

   // One TransactionDetails object should be created for each FHIR operation. Interceptors
   // may use it for getting/setting details about the running transaction.
   TransactionDetails transactionDetails = new TransactionDetails();

   // Notify the interceptor framework when we're about to perform an update. This is
   // useful as the authorization interceptor will pick this event up and use it
   // to factor into a decision about whether the operation should be allowed to proceed.
   IBaseResource previousContents = theResource;
   IBaseResource newContents = theResource;
   HookParams params = new HookParams()
         .add(IBaseResource.class, previousContents)
         .add(IBaseResource.class, newContents)
         .add(RequestDetails.class, theRequestDetails)
         .add(ServletRequestDetails.class, theRequestDetails)
         .add(TransactionDetails.class, transactionDetails);
   theInterceptorBroadcaster.callHooks(Pointcut.STORAGE_PRESTORAGE_RESOURCE_UPDATED, params);

   MethodOutcome retVal = new MethodOutcome();
   retVal.setCreated(true);
   retVal.setResource(theResource);
   return retVal;
}

```

Copy
#  12.1.5Authorizing Patch Operations
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-patch-operations)
The FHIR [patch](http://hl7.org/fhir/http.html#patch) operation presents a challenge for authorization, as the incoming request often contains very little detail about what is being modified.
In order to properly enforce authorization on a server that allows the patch operation, a rule may be added that allows all patch requests, as shown below.
This should be combined with server support for [Authorizing Sub-Operations](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-sub-operations) as shown above.
```
new AuthorizationInterceptor(PolicyEnum.DENY) {
   @Override
   public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
      return new RuleBuilder()
            // Authorize patch requests
            .allow()
            .patch()
            .allRequests()
            .andThen()
            // Authorize actual writes that patch may perform
            .allow()
            .write()
            .allResources()
            .inCompartment("Patient", new IdType("Patient/123"))
            .andThen()
            .build();
   }
};

```

Copy
#  12.1.6Authorizing Multitenant Servers
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-multitenant-servers)
The AuthorizationInterceptor has the ability to direct individual rules as only applying to a single tenant in a multitenant server. The following example shows such a rule.
```
new AuthorizationInterceptor(PolicyEnum.DENY) {
   @Override
   public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
      return new RuleBuilder()
            .allow()
            .read()
            .resourcesOfType(Patient.class)
            .withAnyId()
            .forTenantIds("TENANTA")
            .andThen()
            .build();
   }
};

```

Copy
#  12.1.7Authorizing Bulk Export Operations
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#authorizing-bulk-export-operations)
AuthorizationInterceptor can be used to provide nuanced control over the kinds of Bulk Export operations that a user can initiate when using the JPA Server.
```
new AuthorizationInterceptor(PolicyEnum.DENY) {
   @Override
   public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
      return new RuleBuilder()
            .allow()
            .bulkExport()
            .systemExport()
            .withResourceTypes(Lists.newArrayList("Patient", "Encounter", "Observation"))
            .build();
   }
};

```

Copy
#  12.1.8Advanced Compartment authorization
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization)
AuthorizationInterceptor can be used to provide fine-grained control over compartment reads and writes as well. There is a strict FHIR definition of which resources and related search parameters fall into a given compartment. However, sometimes the defaults do not suffice. The following is an example of an R4 ruleset which allows `device.patient` to be considered in the Patient compartment, on top of all the standard search parameters.
```
new AuthorizationInterceptor(PolicyEnum.DENY) {
   @Override
   public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
      AdditionalCompartmentSearchParameters additionalSearchParams =
            new AdditionalCompartmentSearchParameters();
      additionalSearchParams.addSearchParameters("device:patient", "device:subject");
      return new RuleBuilder()
            .allow()
            .read()
            .allResources()
            .inCompartmentWithAdditionalSearchParams(
                  "Patient", new IdType("Patient/123"), additionalSearchParams)
            .build();
   }
};

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
12.1 Authorization Interceptor 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
[ 12.2 Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)