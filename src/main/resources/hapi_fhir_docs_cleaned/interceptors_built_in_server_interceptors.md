---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html
crawled: 2025-08-01T14:05:35.857334
---

# Interceptors Built In Server Interceptors

#  11.7.1Built-In Server Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#built-in-server-interceptors)
This page describes some server interceptors that are shipped with HAPI FHIR out of the box. Of course, you are also welcome to create your own.
#  11.7.2Logging: Logging Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#logging_interceptor)
The LoggingInterceptor can be used to generate a new log line (via SLF4j) for each incoming request. LoggingInterceptor provides a flexible message format that can be used to provide a customized level detail about each incoming request.
  * [LoggingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/LoggingInterceptor.html)
  * [LoggingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/LoggingInterceptor.java)


The following example shows how to register a logging interceptor within a FHIR RESTful server.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithLogging extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // ... define your resource providers here ...

      // Now register the logging interceptor
      LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
      registerInterceptor(loggingInterceptor);

      // The SLF4j logger "test.accesslog" will receive the logging events
      loggingInterceptor.setLoggerName("test.accesslog");

      // This is the format for each line. A number of substitution variables may
      // be used here. See the JavaDoc for LoggingInterceptor for information on
      // what is available.
      loggingInterceptor.setMessageFormat(
            "Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}]");
   }
}

```

Copy
This interceptor will then produce output similar to the following:
```
2014-09-04 02:37:30.030 Source[127.0.0.1] Operation[vread Patient/1667/_history/1] UA[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36] Params[?_format=json]
2014-09-04 03:30:00.443 Source[127.0.0.1] Operation[search-type Organization] UA[Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)] Params[]

```

Copy
#  11.7.3Partitioning: Multitenant Request Partition
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#request-tenant-partition-interceptor)
If the JPA server has [partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html) enabled, the RequestTenantPartitionInterceptor can be used in combination with a [Tenant Identification Strategy](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html) in order to achieve a multitenant solution. See [JPA Server Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html) for more information on partitioning.
  * [RequestTenantPartitionInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/partition/RequestTenantPartitionInterceptor.html)
  * [RequestTenantPartitionInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/partition/RequestTenantPartitionInterceptor.java)


#  11.7.4Response Customizing: Syntax Highlighting
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#response-customizing-syntax-highlighting)
The ResponseHighlighterInterceptor detects when a request is coming from a browser and returns HTML with syntax highlighted XML/JSON instead of just the raw text. In other words, if a user uses a browser to request `http://foo/Patient/1` by typing this address into their URL bar, they will get a nicely formatted HTML back with a human readable version of the content. This is particularly helpful for testers and public/development APIs where users are likely to invoke the API directly to see how it works.
  * [ResponseHighlighterInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ResponseHighlighterInterceptor.html)
  * [ResponseHighlighterInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ResponseHighlighterInterceptor.java)


To see an example of how the output of this interceptor looks, see our demo server using the following example query: <http://hapi.fhir.org/baseR4/Patient>. The HTML view you see in that page with colour and indenting is provided by ResponseHighlighterInterceptor. Without this interceptor the response will simply be raw JSON/XML (as it will also be with this interceptor if the request is not coming from a browser, or is invoked by JavaScript).
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithResponseHighlighter extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // ... define your resource providers here ...

      // Now register the interceptor
      ResponseHighlighterInterceptor interceptor = new ResponseHighlighterInterceptor();
      registerInterceptor(interceptor);
   }
}

```

Copy
#  11.7.5Response Customizing: Exception Handling
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#response-customizing-exception-handling)
The ExceptionHandlingInterceptor can be used to customize what is returned to the client and what is logged when the server throws an exception for any reason (including routine things like UnprocessableEntityExceptions thrown as a matter of normal processing in a create method, but also including unexpected exceptions thrown by client code).
  * [ExceptionHandlingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ExceptionHandlingInterceptor.html)
  * [ExceptionHandlingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ExceptionHandlingInterceptor.java)


The following example shows how to register the ExceptionHandlingInterceptor.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithExceptionHandling extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // ... define your resource providers here ...

      // Now register the interceptor
      ExceptionHandlingInterceptor interceptor = new ExceptionHandlingInterceptor();
      registerInterceptor(interceptor);

      // Return the stack trace to the client for the following exception types
      interceptor.setReturnStackTracesForExceptionTypes(InternalErrorException.class, NullPointerException.class);
   }
}

```

Copy
#  11.7.6Response Customizing: Evaluate FHIRPath
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#response-customizing-evaluate-fhirpath)
The FhirPathFilterInterceptor looks for a request URL parameter in the form `_fhirpath=(expression)` in all REST requests. If this parameter is found, the value is treated as a [FHIRPath](http://hl7.org/fhirpath/) expression. The response resource will be replaced with a [Parameters](http://hl7.org/fhir/parameters.html) resource containing the results of the given expression applied against the response resource.
  * [FhirPathFilterInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/FhirPathFilterInterceptor.html)
  * [FhirPathFilterInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/FhirPathFilterInterceptor.java)


The following example shows how to register the ExceptionHandlingInterceptor.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithExceptionHandling extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // ... define your resource providers here ...

      // Now register the interceptor
      ExceptionHandlingInterceptor interceptor = new ExceptionHandlingInterceptor();
      registerInterceptor(interceptor);

      // Return the stack trace to the client for the following exception types
      interceptor.setReturnStackTracesForExceptionTypes(InternalErrorException.class, NullPointerException.class);
   }
}

```

Copy
An example URL to invoke this function is shown below: [https://hapi.fhir.org/baseR4/Patient?_fhirpath=Bundle.entry.resource.as(Patient).name&_pretty=true](https://hapi.fhir.org/baseR4/Patient?_fhirpath=Bundle.entry.resource.as\(Patient\).name&_pretty=true)
A sample response to this query is shown below:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "result",
    "part": [ {
      "name": "expression",
      "valueString": "Bundle.entry.resource.as(Patient).name"
    }, {
      "name": "result",
      "valueHumanName": {
        "family": "Simpson",
        "given": [ "Homer", "Jay" ]
      }
    }, {
      "name": "result",
      "valueHumanName": {
        "family": "Simpson",
        "given": [ "Grandpa" ]
      }
    } ]
  } ]
}

```

Copy
#  11.7.7Response Customizing: Static CapabilityStatement
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#static-capabilitystatement)
By default the HAPI FHIR RestfulServer will automatically generate a CapabilityStatement that will be used when clients invoke the [Capabilities](http://hl7.org/fhir/http.html#capabilities) (ie. the `/metadata` operation).
If you wish to override this behaviour and supply a static CapabilityStatement, the **StaticCapabilityStatementInterceptor** can achieve this.
  * [StaticCapabilityStatementInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/StaticCapabilityStatementInterceptor.html)
  * [StaticCapabilityStatementInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/StaticCapabilityStatementInterceptor.java)


The following example shows how to register the StaticCapabilityStatementInterceptor.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithStaticCapabilityStatement extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // ... define your resource providers here ...

      // Create the interceptor
      StaticCapabilityStatementInterceptor interceptor = new StaticCapabilityStatementInterceptor();

      // There are two ways of supplying a CapabilityStatement to the
      // interceptor. You can use a static resource found on the classpath
      interceptor.setCapabilityStatementResource("/classpath/to/capabilitystatement.json");

      // ..or you can simply create one in code (in which case you do not
      // need to call setCapabilityStatementResource(..))
      CapabilityStatement cs = new CapabilityStatement();
      cs.setFhirVersion(Enumerations.FHIRVersion._4_0_1);
      cs.getSoftware().setName("My Acme Server");

      // Now register the interceptor
      registerInterceptor(interceptor);
   }
}

```

Copy
#  11.7.8Validation: Request and Response Validation
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#request_and_response_validation)
HAPI FHIR provides a pair of interceptors that can be used to validate incoming requests received by the server, as well as outgoing responses generated by the server.
The RequestValidatingInterceptor and ResponseValidatingInterceptor can be used to perform validation of resources on their way into and out of the server respectively.
  * [RequestValidatingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/RequestValidatingInterceptor.html)
  * [RequestValidatingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/RequestValidatingInterceptor.java)
  * [ResponseValidatingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ResponseValidatingInterceptor.html)
  * [ResponseValidatingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ResponseValidatingInterceptor.java)


The RequestValidatingInterceptor looks at resources coming into the server (e.g. for create, update, $operations, transactions, etc.) and validates them. The ResponseValidatingInterceptor looks at resources being returned by the server (e.g. for read, search, $operations, etc.) and validates them.
These interceptors can be configured to add headers to the response, fail the response (returning an HTTP 422 and throwing an exception in the process), or to add to the OperationOutcome returned by the server.
See [Instance Validator](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html) for information on how validation works in HAPI FHIR.
The following example shows how to register this interceptor within a HAPI FHIR REST server.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class ValidatingServerWithLogging extends RestfulServer {

   @Override
   protected void initialize() {
      FhirContext ctx = FhirContext.forDstu3();
      setFhirContext(ctx);

      // ... define your resource providers here ...

      // Create an interceptor to validate incoming requests
      RequestValidatingInterceptor requestInterceptor = new RequestValidatingInterceptor();

      // Register a validator module (you could also use SchemaBaseValidator and/or SchematronBaseValidator)
      requestInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));

      requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
      requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
      requestInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
      requestInterceptor.setResponseHeaderValueNoIssues("No issues detected");

      // Now register the validating interceptor
      registerInterceptor(requestInterceptor);

      // Create an interceptor to validate responses
      // This is configured in the same way as above
      ResponseValidatingInterceptor responseInterceptor = new ResponseValidatingInterceptor();
      responseInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));
      responseInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
      responseInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
      responseInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
      responseInterceptor.setResponseHeaderValueNoIssues("No issues detected");
      registerInterceptor(responseInterceptor);
   }
}

```

Copy
**See Also:** The [Repository Validating Interceptor](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html) provides a different and potentially more powerful way of validating data when paired with a HAPI FHIR JPA Server.
#  11.7.9Search: Allow Lenient Searching
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#lenient_searching)
By default, HAPI FHIR applies strict search parameter validation. This means that FHIR search requests will fail if the search contains search parameters (any parameter that does not begin with an underscore) that are not known to the server.
  * [SearchPreferHandlingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/SearchPreferHandlingInterceptor.html)
  * [SearchPreferHandlingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/SearchPreferHandlingInterceptor.java)


The SearchPreferHandlingInterceptor looks for a header of the form `Prefer: handling=lenient` or `Prefer: handling=strict` as described in the [FHIR Search Specification](http://hl7.org/fhir/search.html#errors) and treats it appropriately. A non-strict can also optionally be set.
The following example shows how to register this interceptor within a HAPI FHIR REST server.
```
@WebServlet(
      urlPatterns = {"/fhir/*"},
      displayName = "FHIR Server")
public class RestfulServerWithPreferHandling extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {

      // Create an interceptor
      SearchPreferHandlingInterceptor interceptor = new SearchPreferHandlingInterceptor();

      // Optionally you can change the default behaviour for when the Prefer
      // header is not found in the request or does not have a handling
      // directive
      interceptor.setDefaultBehaviour(PreferHandlingEnum.LENIENT);

      // Register the interceptor
      registerInterceptor(interceptor);
   }

```

Copy
#  11.7.10Security: CORS
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-cors)
HAPI FHIR includes an interceptor which can be used to implement CORS support on your server. See [Server CORS Documentation](https://hapifhir.io/hapi-fhir/docs/security/cors.html#cors_interceptor) for information on how to use this interceptor.
#  11.7.11Security: Audit
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-audit)
HAPI FHIR provides an interceptor that can be used to automatically generate and record AuditEvent resources based on user/client actions on the server. See [BALP Interceptor](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html) for more information.
#  11.7.12Security: Authorization
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-authorization)
HAPI FHIR provides an interceptor that can be used to implement user- and system-level authorization rules that are aware of FHIR semantics. See [Authorization](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html) for more information.
#  11.7.13Security: Binary Resources
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-binary-resources)
HAPI FHIR provides an interceptor that can be used to secure access to Binary resources by using the `Binary.securityContext` element. See [Binary Security Interceptor](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html) for more information.
#  11.7.14Security: Consent
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-consent)
HAPI FHIR provides an interceptor that can be used to implement consent rules and directives. See [Consent Interceptor](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html) for more information.
#  11.7.15Security: Search Narrowing
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-search-narrowing)
HAPI FHIR provides an interceptor that can be used to implement consent rules and directives. See [Search Narrowing Interceptor](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html) for more information.
#  11.7.16Security: Rejecting Unsupported HTTP Verbs
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#security-rejecting-unsupported-http-verbs)
Some security audit tools require that servers return an HTTP 405 if an unsupported HTTP verb is received (e.g. TRACE). The BanUnsupportedHttpMethodsInterceptor can be used to accomplish this.
  * [BanUnsupportedHttpMethodsInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/BanUnsupportedHttpMethodsInterceptor.html)
  * [BanUnsupportedHttpMethodsInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/BanUnsupportedHttpMethodsInterceptor.java)


#  11.7.17Server: OpenAPI / Swagger Support
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#server-openapi-swagger-support)
An interceptor can be registered against your server that enables support for OpenAPI (aka Swagger) automatically. See [OpenAPI](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html) for more information.
#  11.7.18Server: Interaction Blocking
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#server-interaction-blocking)
The interaction blocking interceptor selectively disables specific interactions that are provided in registered resource providers.
  * [InteractionBlockingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/InteractionBlockingInterceptor.html)
  * [InteractionBlockingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/InteractionBlockingInterceptor.java)


#  11.7.19Subscription: Subscription Debug Log Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#subscription-subscription-debug-log-interceptor)
When using Subscriptions, the debug log interceptor can be used to add a number of additional lines to the server logs showing the internals of the subscription processing pipeline.
  * [SubscriptionDebugLogInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-subscription/ca/uhn/fhir/jpa/subscription/util/SubscriptionDebugLogInterceptor.html)
  * [SubscriptionDebugLogInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-subscription/src/main/java/ca/uhn/fhir/jpa/subscription/util/SubscriptionDebugLogInterceptor.java)


#  11.7.20Subscription: Subscription Rules Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#subscription-subscription-rules-interceptor)
This interceptor can be used to enforce rules on allowable subscriptions, such as mandatory criteria patteerns, or requiring a reachable target URL for REST HOOK subscriptions.
  * [SubscriptionRulesInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-subscription/ca/uhn/fhir/jpa/subscription/util/SubscriptionRulesInterceptor.html)
  * [SubscriptionRulesInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-subscription/src/main/java/ca/uhn/fhir/jpa/subscription/util/SubscriptionRulesInterceptor.java)


#  11.7.21Request Pre-Processing: Override Meta.source
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#request-pre-processing-override-metasource)
If you wish to override the value of `Resource.meta.source` using the value supplied in an HTTP header, you can use the CaptureResourceSourceFromHeaderInterceptor to accomplish this.
  * [CaptureResourceSourceFromHeaderInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/CaptureResourceSourceFromHeaderInterceptor.html)
  * [CaptureResourceSourceFromHeaderInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/CaptureResourceSourceFromHeaderInterceptor.java)


#  11.7.22Terminology: Map Response Terminology
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#terminology-map-response-terminology)
A common problem when implementing FHIR APIs is the challenge of how to return coded data using standard vocabularies when your source data is not modelled using these vocabularies. For example, suppose you want to implement support for an Implementation Guide that mandates the use of [LOINC](https://loinc.org) but your source data uses local/proprietary observation codes.
One solution is to simply apply mappings and add them to the FHIR data you are storing in your repository as you are storing it. This solution, often called _Mapping on the Way In_ , will work but it has potential pitfalls including:
  * All mappings must be known at the time the data is being stored.
  * If mappings change because of mistakes or new information, updating existing data is difficult.


A potentially better solution is to apply _Mapping on the Way Out_ , meaning that your mappings are stored in a central spot and applied at runtime to data as it is leaving your system. HAPI FHIR supplies an interceptor called the ResponseTerminologyTranslationInterceptor that can help with this.
  * [ResponseTerminologyTranslationInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ResponseTerminologyTranslationInterceptor.html)
  * [ResponseTerminologyTranslationInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ResponseTerminologyTranslationInterceptor.java)


This interceptor uses ConceptMap resources that are stored in your system, looking up mappings for CodeableConcept codings in your resources and adding them to the responses.
The following code snippet shows a simple example of how to create and configure this interceptor.
```
// Create an interceptor that will map from a proprietary CodeSystem to LOINC
ResponseTerminologyTranslationInterceptor interceptor = new ResponseTerminologyTranslationInterceptor(
      myValidationSupport, myResponseTerminologyTranslationSvc);
interceptor.addMappingSpecification("http://examplelabs.org", "http://loinc.org");

// Register the interceptor
registerInterceptor(interceptor);

```

Copy
##  11.7.22.1Limitations[](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#limitations)
The following limitations will hopefully be resolved in the future:
This interceptor currently only works when registered against a RestfulServer backed by the HAPI FHIR JPA server.
This interceptor only modifies responses to FHIR read/vread/search/history operations. Responses to these operations are not modified if they are found within a FHIR transaction operation.
#  11.7.23Terminology: Populate Code Display Names
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#terminology-populate-code-display-names)
The HAPI FHIR ResponseTerminologyDisplayPopulationInterceptor interceptor looks for Coding elements within responses where the `Coding.system` and `Coding.code` values are populated but the `Coding.display` is not. The interceptor will attempt to resolve the correct display using the validation support module and will add it to the Coding display value if one is found.
  * [ResponseTerminologyDisplayPopulationInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ResponseTerminologyDisplayPopulationInterceptor.html)
  * [ResponseTerminologyDisplayPopulationInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ResponseTerminologyDisplayPopulationInterceptor.java)


This interceptor uses ConceptMap resources that are stored in your system, looking up mappings for CodeableConcept codings in your resources and adding them to the responses.
##  11.7.23.1Limitations[](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#limitations-1)
The following limitation will hopefully be resolved in the future:
This interceptor only modifies responses to FHIR read/vread/search/history operations. Responses to these operations are not modified if they are found within a FHIR transaction operation.
#  11.7.24Utility: ResponseSizeCapturingInterceptor
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#utility-responsesizecapturinginterceptor)
The ResponseSizeCapturingInterceptor can be used to capture the number of characters written in each HTTP FHIR response.
  * [ResponseSizeCapturingInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/interceptor/ResponseSizeCapturingInterceptor.html)
  * [ResponseSizeCapturingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-server/src/main/java/ca/uhn/fhir/rest/server/interceptor/ResponseSizeCapturingInterceptor.java)


#  11.7.25JPA Server: Allow Cascading Deletes
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-allow-cascading-deletes)
  * [CascadingDeleteInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/ca/uhn/fhir/jpa/interceptor/CascadingDeleteInterceptor.html)
  * [CascadingDeleteInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-base/src/main/java/ca/uhn/fhir/jpa/interceptor/CascadingDeleteInterceptor.java)


The CascadingDeleteInterceptor allows clients to request deletes be cascaded to other resources that contain incoming references. See [Cascading Deletes](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#cascading-deletes) for more information.
#  11.7.26JPA Server: Disable Referential Integrity for Some Paths
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#overridepathbasedreferentialintegrityfordeletesinterceptor)
  * [OverridePathBasedReferentialIntegrityForDeletesInterceptor JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/ca/uhn/fhir/jpa/interceptor/OverridePathBasedReferentialIntegrityForDeletesInterceptor.html)
  * [OverridePathBasedReferentialIntegrityForDeletesInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-base/src/main/java/ca/uhn/fhir/jpa/interceptor/OverridePathBasedReferentialIntegrityForDeletesInterceptor.java)


The OverridePathBasedReferentialIntegrityForDeletesInterceptor can be registered and configured to allow resources to be deleted even if other resources have outgoing references to the deleted resource. While it is generally a bad idea to allow deletion of resources that are referred to from other resources, there are circumstances where it is desirable. For example, if you have Provenance or AuditEvent resources that refer to a Patient resource that was created in error, you might want to alow the Patient to be deleted while leaving the Provenance and AuditEvent resources intact (including the now-invalid outgoing references to that Patient).
This interceptor uses FHIRPath expressions to indicate the resource paths that should not have referential integrity applied to them. For example, if this interceptor is configured with a path of `AuditEvent.agent.who`, a Patient resource would be allowed to be deleted even if one or more AuditEvents had references in that path to the given Patient (unless other resources also had references to the Patient).
#  11.7.27JPA Server: Block Resource Updates Changing Patient Compartment
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-block-resource-updates-changing-patient-compartment)
  * [PatientCompartmentEnforcingInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-storage/src/main/java/ca/uhn/fhir/jpa/interceptor/PatientCompartmentEnforcingInterceptor.java)


The PatientCompartmentEnforcingInterceptor can be registered to block resource updates which would change the resource's patient compartment. This interceptor requires FHIR version R4 or later.
If the JPA server has [partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html) enabled, and [Tenant Identification Strategy](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html) is PATIENT_ID, the PatientCompartmentEnforcingInterceptor can be used to block resources to change partition as a consequence of updating a patient reference.
#  11.7.28JPA Server: Retry on Version Conflicts
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-retry-on-version-conflicts)
The UserRequestRetryVersionConflictsInterceptor allows clients to request that the server avoid version conflicts (HTTP 409) when two concurrent client requests attempt to modify the same resource. See [Version Conflicts](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#retry-on-version-conflict) for more information.
#  11.7.29JPA Server: Selective Auto-Create Placeholder Reference Targets
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-selective-auto-create-placeholder-reference-targets)
The `AutoCreatePlaceholderReferenceEnabledByTypeInterceptor` can be used in combination with [Auto-Create Placeholder Reference Targets](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#setAutoCreatePlaceholderReferenceTargets\(boolean\)) in order to specify that only reference targets with specific resource types should be created.
  * [AutoCreatePlaceholderReferenceEnabledByTypeInterceptor Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-storage/src/main/java/ca/uhn/fhir/storage/interceptor/AutoCreatePlaceholderReferenceEnabledByTypeInterceptor.java)


#  11.7.30JPA Server: Validate Data Being Stored
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-validate-data-being-stored)
The RepositoryValidatingInterceptor can be used to enforce validation rules on data stored in a HAPI FHIR JPA Repository. See [Repository Validating Interceptor](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html) for more information.
#  11.7.31Data Standardization
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#data-standardization)
`StandardizingInterceptor` handles data standardization (s13n) requirements. This interceptor applies standardization rules on all FHIR primitives as configured in the `s13n.json` file that should be made available on the classpath. This file contains FHIRPath definitions together with the standardizers that should be applied to that path. Currently, there are six pre-built standardizers: NAME_FAMILY, NAME_GIVEN, EMAIL, TITLE, PHONE and TEXT. Custom standardizers can be developed by implementing `ca.uhn.fhir.rest.server.interceptor.s13n.standardizers.IStandardizer` interface and providing class name in the configuration.
A sample configuration file can be found below:
```
{
	"Person" : {
		"Person.name.family" : "NAME_FAMILY",
		"Person.name.given" : "NAME_GIVEN",
		"Person.telecom.where(system='phone').value" : "PHONE"
	},
	"Patient" : {
		"name.family" : "NAME_FAMILY",
		"name.given" : "NAME_GIVEN",
		"telecom.where(system='phone').value" : "PHONE"
	},
	"*" : {
		"telecom.where(system='email').value" : "org.example.s13n.MyCustomStandardizer"
	}
}

```

Copy
Standardization can be disabled for a given request by providing `HAPI-Standardization-Disabled: *` request header. Header value can be any string, it is the presence of the header that disables the s13n.
#  11.7.32Validation: Address Validation
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#validation-address-validation)
`AddressValidatingInterceptor` validates addresses on all incoming resources through a 3rd party address validation service. This interceptor invokes address validation service, updates the address with the validated results and adds a validation extension with `http://hapifhir.org/StructureDefinition/ext-validation-address-has-error` URL.
This interceptor is configured in `address-validation.properties` file that should be made available on the classpath. This file must contain `validator.class` property, which defines a fully qualified class implementing `ca.uhn.fhir.rest.server.interceptor.validation.address.IAddressValidator` interface. The specified implementation must provide service-specific logic for validating an Address instance. An example implementation can be found in `ca.uhn.fhir.rest.server.interceptor.validation.address.impl.LoquateAddressValidator` class which validates addresses by using Loquate Data Cleanse service.
Address validation can be disabled for a given request by providing `HAPI-Address-Validation-Disabled: *` request header. Header value can be any string, it is the presence of the header that disables the validation.
#  11.7.33Validation: Field-Level Validation
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#validation-field-level-validation)
`FieldValidatingInterceptor` enables validation of primitive values on various FHIR resources. It expects validation rules to be provided via `field-validation-rules.json` file that should be available on the classpath. JSON in this file defines a mapping of FHIRPath expressions to validators that should be applied to those fields. Custom validators that implement `ca.uhn.fhir.rest.server.interceptor.validation.fields.IValidator` interface can be provided.
```
{
	"telecom.where(system='email')" : "EMAIL",
   "telecom.where(system='phone')" : "org.example.validation.MyCustomValidator"
}

```

Copy
Field validation can be disabled for a given request by providing `HAPI-Field-Validation-Disabled: *` request header. Header value can be any string, it is the presence of the header that disables the validation.
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_pointcuts.html)
11.7 Built-In Server Interceptors 
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
[ 11.8 7.0.0 Migration Guide ](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)