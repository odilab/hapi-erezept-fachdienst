---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html
crawled: 2025-08-01T14:06:52.975779
---

# Interceptors Filter Hook Interceptors

#  11.2.1Filter Hook Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html#filter-hook-interceptors)
A filter hook is a hook that wraps a supplied function (i.e. Supplier). Filter hooks allow implementers to run custom code around the supplied function's execution (similar to Java Servlet Filters). Implementers can specify logic that should be executed:
  1. Before the supplied function call is made
  2. When the supplied function call throws an exception
  3. After the supplied function call is made


The example below shows how a Filter Hook Interceptor can be implemented with the `BATCH2_CHUNK_PROCESS_FILTER` pointcut:
```
import ca.uhn.fhir.batch2.model.JobInstance;
import ca.uhn.fhir.batch2.model.WorkChunk;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.IBaseInterceptorBroadcaster.IInterceptorFilterHook;
import ca.uhn.fhir.interceptor.api.Pointcut;

public class WorkChunkProcessingInterceptor {

   @Hook(Pointcut.BATCH2_CHUNK_PROCESS_FILTER)
   public IInterceptorFilterHook batch2ProcessFilter(JobInstance theJobInstance, WorkChunk theWorkChunk) {
      return theContinuation -> {
         try {
            // Perform pre-processing logic before the work chunk is processed

            // Process the work chunk (Note: If the continuation is not ran, an IllegalStateException will be
            // thrown)
            theContinuation.run();
         } catch (Exception e) {
            // Handle any exceptions that occur during work chunk processing

            // rethrow the exception
            throw e;
         } finally {
            // Perform any necessary cleanup or final operations
         }
      };
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html)
11.2 Filter Hook Interceptors 
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
[ 11.3 Client Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_pointcuts.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)