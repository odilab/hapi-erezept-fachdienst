---
source: https://hapifhir.io/hapi-fhir/docs/interceptors
crawled: 2025-08-01T14:09:58.657967
---

# Interceptors

#  11.0.1Interceptors: Overview
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html#interceptors-overview)
HAPI FHIR 3.8.0 introduced a new interceptor framework that is used across the entire library. In previous versions of HAPI FHIR, a "Server Interceptor" framework existed and a separate "Client Interceptor" framework existed. These have now been combined into a single unified (and much more powerful) framework.
Interceptor classes may "hook into" various points in the processing chain in both the client and the server. The interceptor framework has been designed to be flexible enough to hook into almost every part of the library. When trying to figure out "how would I make HAPI FHIR do X", the answer is very often to create an interceptor.
The following example shows a very simple interceptor example.
```
@Interceptor
public class SimpleServerLoggingInterceptor {

   private final Logger ourLog = LoggerFactory.getLogger(SimpleServerLoggingInterceptor.class);

   @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
   public void logRequests(RequestDetails theRequest) {
      ourLog.info("Request of type {} with request ID: {}", theRequest.getOperation(), theRequest.getRequestId());
   }
}

```

Copy
##  11.0.1.1Interceptors Glossary[](https://hapifhir.io/hapi-fhir/docs/interceptors#interceptors-glossary)
The HAPI FHIR interceptor framework uses a couple of key terms that are important to understand as you read the rest of this documentation:
  * **Interceptor** – A class such as the example above that has one or more _Hook_ methods on it. An optional marker annotation called [@Interceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Interceptor.html) exists and can be used, but is not required.
  * **Hook** – An individual interceptor method that is invoked in response to a specific action taking place in the HAPI FHIR framework. Hook methods must be annotated with the [@Hook](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Hook.html) annotation.
  * **Pointcut** – A pointcut is a specific point in the HAPI FHIR processing pipeline that is being intercepted. Each Hook Method must declare which pointcut it is intercepting. All available pointcuts are defined by HAPI FHIR in the [Pointcut](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html) enum.
  * **Hook Params** – Every Pointcut defines a list of parameters that may be passed to a Hook Method for a given Pointcut. For example, the definition of the [SERVER_INCOMING_REQUEST_PRE_HANDLED](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#SERVER_INCOMING_REQUEST_PRE_HANDLED) pointcut used in the example above defines 4 parameter types. A hook method for that parameter type can include any/all of these parameter types in its parameter list. The example above is only using one.


#  11.0.2Creating Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html#creating-interceptors)
Creating your own interceptors is easy. Custom interceptor classes do not need to extend any other class or implement any particular interface.
  * They must have at least one method annotated with the [@Hook](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html) annotation.
  * The method must have an appropriate return value for the chosen [Pointcut](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html).
  * The method may have any of the parameters specified for the given [Pointcut](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html).
    * A parameter of type [Pointcut](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html) may also be added, and will be passed the actual Pointcut which triggered the invocation.
  * The method must be public.


The following example shows a simple request counter interceptor.
```
@Interceptor
public class RequestCounterInterceptor {

   private int myRequestCount;

   public int getRequestCount() {
      return myRequestCount;
   }

   /**
    * Override the incomingRequestPreProcessed method, which is called
    * for each incoming request before any processing is done
    */
   @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
   public boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
      myRequestCount++;
      return true;
   }
}

```

Copy
The following example shows an exception handling interceptor which overrides the built-in exception handling by providing a custom response.
```
public class RequestExceptionInterceptor {

   @Hook(Pointcut.SERVER_HANDLE_EXCEPTION)
   public boolean handleException(
         RequestDetails theRequestDetails,
         BaseServerResponseException theException,
         HttpServletRequest theServletRequest,
         HttpServletResponse theServletResponse)
         throws IOException {

      // HAPI's server exceptions know what the appropriate HTTP status code is
      theServletResponse.setStatus(theException.getStatusCode());

      // Provide a response ourself
      theServletResponse.setContentType("text/plain");
      theServletResponse.getWriter().append("Failed to process!");
      theServletResponse.getWriter().close();

      // Since we handled this response in the interceptor, we must return false
      // to stop processing immediately
      return false;
   }
}

```

Copy
11.0 Interceptors Overview 
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
[ 11.1 Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)