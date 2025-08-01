---
source: https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html
crawled: 2025-08-01T14:05:25.084471
---

# Security Consent Interceptor

#  12.2.1Consent Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html#consent-interceptor)
HAPI FHIR 4.0.0 introduced a new interceptor, the [ConsentInterceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/consent/ConsentInterceptor.html).
The consent interceptor may be used to examine client requests to apply consent directives and create audit trail events. Like the AuthorizationInterceptor above, this interceptor is not a complete working solution, but instead is a framework designed to make it easier to implement local policies.
  * [ConsentInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/consent/ConsentInterceptor.html)
  * [ConsentInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/consent/ConsentInterceptor.java)


The consent interceptor has several primary purposes:
  * It can reject a resource from being disclosed to the user by examining it while calculating search results. This calculation is performed very early in the process of building search results, in order to ensure that in many cases the user is unaware that results have been removed.
  * It can redact results, removing specific elements before they are returned to a client.
  * It can create audit trail records (e.g. using an AuditEvent resource)
  * It can apply consent directives (e.g. by reading relevant Consent resources)
  * The consent service suppresses search the total being returned in Bundle.total for search results, even if the user explicitly requested them using the `_total=accurate` or `_summary=count` parameter.


The ConsentInterceptor requires a user-supplied instance of the [IConsentService](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/consent/IConsentService.html) interface. The following shows a simple example of an IConsentService implementation:
```
public class MyConsentService implements IConsentService {

   /**
    * Invoked once at the start of every request
    */
   @Override
   public ConsentOutcome startOperation(
         RequestDetails theRequestDetails, IConsentContextServices theContextServices) {
      // This means that all requests should flow through the consent service
      // This has performance implications - If you know that some requests
      // don't need consent checking it is a good idea to return
      // ConsentOutcome.AUTHORIZED instead for those requests.
      return ConsentOutcome.PROCEED;
   }

   /**
    * Can a given resource be returned to the user?
    */
   @Override
   public ConsentOutcome canSeeResource(
         RequestDetails theRequestDetails,
         IBaseResource theResource,
         IConsentContextServices theContextServices) {
      // In this basic example, we will filter out lab results so that they
      // are never disclosed to the user. A real interceptor might do something
      // more nuanced.
      if (theResource instanceof Observation) {
         Observation obs = (Observation) theResource;
         if (obs.getCategoryFirstRep()
               .hasCoding("http://hl7.org/fhir/codesystem-observation-category.html", "laboratory")) {
            return ConsentOutcome.REJECT;
         }
      }

      // Otherwise, allow the
      return ConsentOutcome.PROCEED;
   }

   /**
    * Modify resources that are being shown to the user
    */
   @Override
   public ConsentOutcome willSeeResource(
         RequestDetails theRequestDetails,
         IBaseResource theResource,
         IConsentContextServices theContextServices) {
      // Don't return the subject for Observation resources
      if (theResource instanceof Observation) {
         Observation obs = (Observation) theResource;
         obs.setSubject(null);
      }
      return ConsentOutcome.AUTHORIZED;
   }

   @Override
   public void completeOperationSuccess(
         RequestDetails theRequestDetails, IConsentContextServices theContextServices) {
      // We could write an audit trail entry in here
   }

   @Override
   public void completeOperationFailure(
         RequestDetails theRequestDetails,
         BaseServerResponseException theException,
         IConsentContextServices theContextServices) {
      // We could write an audit trail entry in here
   }
}

```

Copy
##  12.2.1.1Performance and Privacy[](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html#performance-and-privacy)
Filtering search results in `canSeeResource()` requires inspecting every resource during a search and editing the results. This is slower than the normal path, and will prevent the reuse of the results from the search cache. The `willSeeResource()` operation supports reusing cached search results, but removed resources may be 'visible' as holes in returned bundles. Disabling `canSeeResource()` by returning `false` from `processCanSeeResource()` will enable the search cache.
##  12.2.1.2Pre-Authorizing Requests[](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html#pre-authorizing-requests)
In some situations, it is useful to pre-authorize a request programmatically. This can be achieved by overriding the `authorizeRequest(RequestDetails theRequestDetails)` method of the `ConsentInterceptor`.
The example below shows how this method could be overridden in a system that uses user data in the `RequestDetails` to indicate that a request should be pre-authorized.
```
@Override
public void authorizeRequest(RequestDetails theRequestDetails) {
    String userDataValue = (String) theRequestDetails.getUserData().get("SOME_KEY");
    if ("SOME_VALUE".equals(userDataValue)){
        super.authorizeRequest(theRequestDetails);
    }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
12.2 Consent Interceptor 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
[ 12.3 Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)