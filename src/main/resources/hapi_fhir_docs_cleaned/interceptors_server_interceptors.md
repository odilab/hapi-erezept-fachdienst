---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html
crawled: 2025-08-01T14:05:15.424842
---

# Interceptors Server Interceptors

#  11.5.1Server Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html#server-interceptors)
There are many different Pointcuts available to server developers. In general, a server can be thought of as playing two roles: Server and Storage.
In the case of a Plain Server, HAPI FHIR itself performs the role of the Server and your [Resource Provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html) classes perform the role of Storage.
In the case of a JPA Server, HAPI FHIR itself performs both roles. This means that **SERVER_xxx** Pointcuts may be intercepted by interceptors on any HAPI FHIR server. However, if you want to intercept **STORAGE_xxx** Pointcuts on a plain server, you will need to trigger them yourself.
#  11.5.2Example: Clearing Tags
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html#example-clearing-tags)
The following example shows an interceptor that clears all tags, profiles, and security labels from a resource prior to storage in the JPA server.
```
/**
 * This is a simple interceptor for the JPA server that trims all tags, profiles, and security labels from
 * resources before they are saved.
 */
@Interceptor
public class TagTrimmingInterceptor {

   /** Handle creates */
   @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_CREATED)
   public void insert(IBaseResource theResource) {
      theResource.getMeta().getTag().clear();
      theResource.getMeta().getProfile().clear();
      theResource.getMeta().getSecurity().clear();
   }

   /** Handle updates */
   @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_UPDATED)
   public void update(IBaseResource theOldResource, IBaseResource theResource) {
      theResource.getMeta().getTag().clear();
      theResource.getMeta().getProfile().clear();
      theResource.getMeta().getSecurity().clear();
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html)
11.5 Server Interceptors 
* Interceptors 
* [ 11.0  Interceptors Overview ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html)
* [ 11.1  Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html)
* [ 11.2  Filter Hook Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html)
* [ 11.3  Client Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_pointcuts.html)
* [ 11.4  Built-In Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html)
* [ 11.5  Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html)
* [ 11.6  Server Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_pointcuts.html)
* [ 11.7  Built-In Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html)
* [ 11.8  7.0.0 Migration Guide ](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html)
[ 11.6 Server Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_pointcuts.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)