---
source: https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html
crawled: 2025-08-01T14:04:53.277025
---

# Validation Parser Error Handler

#  13.1.1Parser Error Handler
[ ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html#parser-error-handler)
Parser Error Handler validation is enabled by calling [IParser#setParserErrorHandler(IParserErrorHandler)](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParser.html#setParserErrorHandler\(ca.uhn.fhir.parser.IParserErrorHandler\)) on either the FhirContext or on individual parser instances. This method takes an [IParserErrorHandler](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/IParserErrorHandler.html), which is a callback that will be invoked any time a parse issue is detected.
There are two implementations of IParserErrorHandler that come built into HAPI FHIR. You can also supply your own implementation if you want.
  * [**LenientErrorHandler**](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/LenientErrorHandler.html) logs any errors but does not abort parsing. By default this handler is used, and it logs errors at "warning" level. It can also be configured to silently ignore issues. LenientErrorHandler is the default.
  * [**StrictErrorHandler**](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/StrictErrorHandler.html) throws a [DataFormatException](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/DataFormatException.html) if any errors are detected.


The following example shows how to configure a parser to use strict validation.
```
FhirContext ctx = FhirContext.forR4();

// Create a parser and configure it to use the strict error handler
IParser parser = ctx.newXmlParser();
parser.setParserErrorHandler(new StrictErrorHandler());

// This example resource is invalid, as Patient.active can not repeat
String input = "<Patient><active value=\"true\"/><active value=\"false\"/></Patient>";

// The following will throw a DataFormatException because of the StrictErrorHandler
parser.parseResource(Patient.class, input);

```

Copy
You can also configure the error handler at the FhirContext level, which is useful for clients.
```
FhirContext ctx = FhirContext.forR4();

ctx.setParserErrorHandler(new StrictErrorHandler());

// This client will have strict parser validation enabled
IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

```

Copy
FhirContext level validators can also be useful on servers.
```
public class MyRestfulServer extends RestfulServer {

   @Override
   protected void initialize() throws ServletException {
      // ...Configure resource providers, etc...

      // Create a context, set the error handler and instruct
      // the server to use it
      FhirContext ctx = FhirContext.forR4();
      ctx.setParserErrorHandler(new StrictErrorHandler());
      setFhirContext(ctx);
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
13.1 Parser Error Handler 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
[ 13.2 Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)