---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html
crawled: 2025-08-01T14:05:34.644984
---

# Server Plain Paging

#  4.7.1Paging Responses
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html#paging-responses)
The **Search** and **History** operations both return a bundle which contain zero or more resources. FHIR RESTful servers may optionally support paging responses, meaning that (for example) if a search returns 500 resources, the server can return a bundle containing only the first 20 and a link which will return the next 20, etc.
By default, RESTful servers will not page, but will rather return all resources immediately in a single bundle. However, you can take advantage of built-in paging functionality to automatically add paging links to generated Bundle resources, and to handle these links by requesting further data.
There are two complementary parts to the HAPI FHIR server paging support: paging providers, and bundle providers.
##  4.7.1.1Paging Providers[](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html#paging-providers)
To support paging, a server must have an [IPagingProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/IPagingProvider.html) implementation set. The paging provider is used to store resource return lists between incoming calls by clients.
A paging provider provides two key methods:
  * `String storeResultList(RequestDetails, IBundleProvider)`, which takes a bundle provider (see below) and stores it for later retrieval. This might be by simply keeping it in memory, but it might also store it on disk, in a database, etc. This method must return a textual ID which can be used to retrieve this list later.
  * `retrieveResultList(RequestDetails, String)`, which takes an ID obtained by a previous call to `storeResultList` and returns the corresponding result list.


Note that the IPagingProvider is intended to be simple and implementable and you are encouraged to provide your own implementations.
The following example shows a server implementation with paging support.
```
public class PagingServer extends RestfulServer {

   public PagingServer() {

      /*
       * Set the resource providers as always. Here we are using the paging
       * provider from the example below, but it is not strictly necessary
       * to use a paging resource provider as well. If a normal resource
       * provider is used (one which returns List<?> instead of IBundleProvider)
       * then the loaded resources will be stored by the IPagingProvider.
       */
      setResourceProviders(new PagingPatientProvider());

      /*
       * Set a paging provider. Here a simple in-memory implementation
       * is used, but you may create your own.
       */
      FifoMemoryPagingProvider pp = new FifoMemoryPagingProvider(10);
      pp.setDefaultPageSize(10);
      pp.setMaximumPageSize(100);
      setPagingProvider(pp);
   }
}

```

Copy
HAPI FHIR contains couple of implementations for `IPagingProvider`.
4.7.1.1.1DatabaseBackedPagingProvider When using `DatabaseBackedPagingProvider` HAPI FHIR searches may be done asynchronously. This means that the result is also cached to the database and the client may base the cached search result set. 4.7.1.1.2FifoMemoryPagingProvider When using `FifoMemoryPagingProvider` HAPI FHIR server search is persisted on the server memory and when pages are fetched the server returns the results from the cached memory (unless the cache overflowed and the old result set is no longer available).
#  4.7.2Bundle Providers
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html#bundle-providers)
If a server supports a paging provider, a further optimization is to also use a bundle provider. A bundle provider simply takes the place of the `List<IBaseResource>` return type in your provider methods. In other words, instead of returning _List_ , your search method will return [IBundleProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/api/server/IBundleProvider.html).
When using a bundle provider however, the server will only request small sublists of resources as they are actually being returned. This allows servers to optimize by not loading all resources into memory until they are actually needed.
One implementation of a bundle provider is shown below. This provider example works by only keeping the resource IDs in memory, but there are other possible implementation strategies that would work as well.
Note that the IBundleProvider is intended to be simple and implementable and you are encouraged to provide your own implementations.
```
public class PagingPatientProvider implements IResourceProvider {

   /**
    * Search for Patient resources matching a given family name
    */
   @Search
   public IBundleProvider search(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamily) {
      final InstantType searchTime = InstantType.withCurrentTime();

      /**
       * First, we'll search the database for a set of database row IDs that
       * match the given search criteria. That way we can keep just the row IDs
       * around, and load the actual resources on demand later as the client
       * pages through them.
       */
      final List<Long> matchingResourceIds = null; // <-- implement this

      /**
       * Return a bundle provider which can page through the IDs and return the
       * resources that go with them.
       */
      return new IBundleProvider() {

         @Override
         public Integer size() {
            return matchingResourceIds.size();
         }

         @Nonnull
         @Override
         public List<IBaseResource> getResources(
               int theFromIndex,
               int theToIndex,
               @Nonnull ResponsePage.ResponsePageBuilder theResponsePageBuilder) {
            int end = Math.max(theToIndex, matchingResourceIds.size() - 1);
            List<Long> idsToReturn = matchingResourceIds.subList(theFromIndex, end);
            return loadResourcesByIds(idsToReturn);
         }

         @Override
         public InstantType getPublished() {
            return searchTime;
         }

         @Override
         public Integer preferredPageSize() {
            // Typically this method just returns null
            return null;
         }

         @Override
         public String getUuid() {
            return null;
         }
      };
   }

   /**
    * Load a list of patient resources given their IDs
    */
   private List<IBaseResource> loadResourcesByIds(List<Long> theIdsToReturn) {
      // .. implement this search against the database ..
      return null;
   }

   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return Patient.class;
   }
}

```

Copy
##  4.7.2.1Using Named Pages[](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html#using-named-pages)
By default, the paging system uses parameters that are embedded into the page links for the start index and the page size. This is useful for servers that can retrieve arbitrary offsets within a search result. For example, if a given search can easily retrieve "items 5-10 from the given search", then the mechanism above works well.
Another option is to use "named pages", meaning that each page is simply assigned an ID by the server, and the next/previous page is requested using this ID.
In order to support named pages, the IPagingProvider must implement the `retrieveResultList(RequestDetails theRequestDetails, String theSearchId, String thePageId)` method.
Then, individual search/history methods may return a [BundleProviderWithNamedPages](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/BundleProviderWithNamedPages.html) or simply implement the `getPageId()` method on their own IBundleProvider implementation.
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
4.7 Paging Search Results 
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
[ 4.8 Web Testpage Overlay ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)