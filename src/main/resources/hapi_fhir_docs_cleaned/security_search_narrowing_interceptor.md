---
source: https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html
crawled: 2025-08-01T14:05:32.432932
---

# Security Search Narrowing Interceptor

#  12.3.1Search Narrowing Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#search-narrowing-interceptor)
The [SearchNarrowingInterceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/auth/SearchNarrowingInterceptor.html) can be used to automatically narrow or constrain the scope of FHIR searches.
  * [SearchNarrowingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/auth/SearchNarrowingInterceptor.html)
  * [SearchNarrowingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/auth/SearchNarrowingInterceptor.java)


This interceptor is designed to be used in conjunction with the [Authorization Interceptor](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html). It uses a similar strategy where a dynamic list is built up for each request, but the purpose of this interceptor is to modify client searches that are received (after HAPI FHIR receives the HTTP request, but before the search is actually performed) to restrict the search to only search for specific resources or compartments that the user has access to.
This could be used, for example, to allow the user to perform a search for: [http://baseurl/Observation?category=laboratory](http://baseurl/Observation?category=laboratory)
...and then receive results as though they had requested: [http://baseurl/Observation?subject=Patient/123&category=laboratory](http://baseurl/Observation?subject=Patient/123&category=laboratory)
An example of this interceptor follows:
```
public class MyPatientSearchNarrowingInterceptor extends SearchNarrowingInterceptor {

   /**
    * This method must be overridden to provide the list of compartments
    * and/or resources that the current user should have access to
    */
   @Override
   protected AuthorizedList buildAuthorizedList(RequestDetails theRequestDetails) {
      // Process authorization header - The following is a fake
      // implementation. Obviously we'd want something more real
      // for a production scenario.
      //
      // In this basic example we have two hardcoded bearer tokens,
      // one which is for a user that has access to one patient, and
      // another that has full access.
      String authHeader = theRequestDetails.getHeader("Authorization");
      if ("Bearer dfw98h38r".equals(authHeader)) {

         // This user will have access to two compartments
         return new AuthorizedList().addCompartment("Patient/123").addCompartment("Patient/456");

      } else if ("Bearer 39ff939jgg".equals(authHeader)) {

         // This user has access to everything
         return new AuthorizedList();

      } else {

         throw new AuthenticationException("Unknown bearer token");
      }
   }
}

```

Copy
#  12.3.2Narrowing Conditional URLs
[ ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#narrowing-conditional-urls)
By default, this interceptor will narrow URLs for FHIR search operations only. The interceptor can also be configured to narrow URLs on conditional operations.
When this feature is enabled request URLs are also narrowed for the following FHIR operations:
  * Conditional Create (The `If-None-Exist` header is narrowed)
  * Conditional Update (The request URL is narrowed if it is a conditional URL)
  * Conditional Delete (The request URL is narrowed if it is a conditional URL)
  * Conditional Patch (The request URL is narrowed if it is a conditional URL)


The following example shows how to enable conditional URL narrowing on the interceptor.
```
SearchNarrowingInterceptor interceptor = new SearchNarrowingInterceptor();
interceptor.setNarrowConditionalUrls(true);

```

Copy
#  12.3.3Constraining by ValueSet Membership
[ ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#constraining-by-valueset-membership)
SearchNarrowingInterceptor can also be used to narrow searches by automatically appending `token:in` and `token:not-in` parameters.
In the example below, searches are narrowed as shown below:
  * Searches for http://localhost:8000/Observation become http://localhost:8000/Observation?code:in=http://hl7.org/fhir/ValueSet/observation-vitalsignresult
  * Searches for http://localhost:8000/Encounter become http://localhost:8000/Encounter?class:not-in=http://my-forbidden-encounter-classes


Important note: ValueSet Membership rules are only applied in cases where the ValueSet expansion has a code count below a configurable threshold (default is 500). To narrow searches with a larger ValueSet expansion, it is necessary to also enable [ResultSet Narrowing](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#resultset-narrowing).
```
public class MyCodeSearchNarrowingInterceptor extends SearchNarrowingInterceptor {

   /**
    * This method must be overridden to provide the list of compartments
    * and/or resources that the current user should have access to
    */
   @Override
   protected AuthorizedList buildAuthorizedList(RequestDetails theRequestDetails) {
      // Process authorization header - The following is a fake
      // implementation. Obviously we'd want something more real
      // for a production scenario.
      String authHeader = theRequestDetails.getHeader("Authorization");
      if ("Bearer dfw98h38r".equals(authHeader)) {

         return new AuthorizedList()
               // When searching for Observations, narrow the search to only include Observations
               // with a code indicating that it is a Vital Signs Observation
               .addCodeInValueSet(
                     "Observation", "code", "http://hl7.org/fhir/ValueSet/observation-vitalsignresult")
               // When searching for Encounters, narrow the search to exclude Encounters where
               // the Encounter class is in a ValueSet containing forbidden class codes
               .addCodeNotInValueSet("Encounter", "class", "http://my-forbidden-encounter-classes");

      } else {

         throw new AuthenticationException("Unknown bearer token");
      }
   }
}

```

Copy
#  12.3.4ResultSet Narrowing
[ ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#resultset-narrowing)
ResultSet narrowing currently applies only to [Constraining by ValueSet Membership](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#constraining-by-valueset-membership). ResultSet narrowing may be added for other types of search narrowing (e.g. by compartment) in a future release. 
By default, narrowing will simply modify search parameters in order to automatically constrain the results that are returned to the client. This is helpful for situations where the resource type you are trying to filter is the primary type of the search, but is less helpful when it is not.
For example suppose you wanted to narrow searches for Observations to only include Observations with a code in `http://my-value-set`. When a search is performed for `Observation?subject=Patient/123` the SearchNarrowingInterceptor will typically modify this to be performed as `Observation?subject=Patient/123&code:in=http://my-value-set`.
However this is not always possible:
  * If the ValueSet expansion is too large, it is inefficient to use it in an `:in` clause and the SearchNarrowingInterceptor will not do so.
  * If the result in question is fetched through an `_include` or `_revinclude` parameter, it is not possible to filter it by adding URL parameters.
  * If the result in question is being returned as a result of an operation (e.g. `Patient/[id]/$expand`), it is not possible to filter it by adding URL parameters.


To enable ResultSet narrowing, the SearchNarrowingInterceptor is used along with the ConsentInterceptor, and the ConsentInterceptor is configured to include a companion consent service implementation that works with search narrowing rules. This is shown in the following example:
```
SearchNarrowingInterceptor narrowingInterceptor = new SearchNarrowingInterceptor() {
   @Override
   protected AuthorizedList buildAuthorizedList(RequestDetails theRequestDetails) {
      // Your rules go here
      return new AuthorizedList()
            .addCodeInValueSet(
                  "Observation", "code", "http://hl7.org/fhir/ValueSet/observation-vitalsignresult");
   }
};
restfulServer.registerInterceptor(narrowingInterceptor);

// Create a consent service for search narrowing
IValidationSupport validationSupport = null; // This needs to be populated
FhirContext searchParamRegistry = null; // This needs to be populated
SearchNarrowingConsentService consentService =
      new SearchNarrowingConsentService(validationSupport, searchParamRegistry);

// Create a ConsentInterceptor to apply the ConsentService and register it with the server
ConsentInterceptor consentInterceptor = new ConsentInterceptor();
consentInterceptor.registerConsentService(consentService);
restfulServer.registerInterceptor(consentInterceptor);

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
12.3 Search Narrowing Interceptor 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
[ 12.4 CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)