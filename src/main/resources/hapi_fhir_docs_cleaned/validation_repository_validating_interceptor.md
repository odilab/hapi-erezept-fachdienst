---
source: https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html
crawled: 2025-08-01T14:05:26.158289
---

# Validation Repository Validating Interceptor

#  13.5.1Repository Validating Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#repository-validating-interceptor)
When using a HAPI FHIR JPA server as a FHIR Repository, it is often desirable to enforce specific rules about which specific FHIR profiles can or must be used.
For example, if an organization has created a FHIR Repository for the purposes of hosting and serving [US Core](https://www.hl7.org/fhir/us/core/) data, that organization might want to enforce rules that data being stored in the repository must actually declare conformance to US Core Profiles such as the [US Core Patient Profile](https://www.hl7.org/fhir/us/core/StructureDefinition-us-core-patient.html) and [US Core Observation Profile](https://www.hl7.org/fhir/us/core/StructureDefinition-us-core-observation.html).
In this situation, it would also be useful to require that any data being stored in the repository be validated, and rejected if it is not valid.
The **RepositoryValidatingInterceptor** interceptor can be used to easily add this kind of rule to the server.
#  13.5.2Benefits and Limitations
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#benefits-and-limitations)
For a HAPI FHIR JPA Server, the RepositoryValidatingInterceptor is a very powerful addition to the [Request and Response Validation](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#request_and_response_validation) that is also often used for validation.
##  13.5.2.1Request and Response Validation[](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#request-and-response-validation)
The _Request and Response Validation_ interceptors examine incoming HTTP payloads (e.g. FHIR creates, FHIR updates, etc.) and apply the validator to them. This approach has its limitations:
  * It may miss validating data that is added or modified through Java API calls as opposed to through the HTTP endpoint.
  * It may miss validating data that is added or modified through other interceptors
  * It may provide you with a validated resource in your Java API so that you can make certain reasonable assumptions - eg. a required field does not need a null check.
  * It is not able to validate changes coming from operations such as FHIR Patch, since the patch itself may pass validation, but may ultimately result in modifying a resource so that it is no longer valid.


##  13.5.2.2Repository Validation[](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#repository-validation)
The _Repository Validating Interceptor_ uses the direct storage pointcuts provided by the JPA Server in order to validate data exactly as it will appear in storage. In other words, no matter whether data is being written via the HTTP API or by an internal Java API call, the interceptor will catch it.
This means that:
  * Repository validation applies to patch operations and validates the results of the resource after a patch is applied (but before the actual results are saved, in case the outcome of the validation operation should roll the operation back)
  * Repository validation requires pointcuts that are thrown directly by the storage engine, meaning that it can not be used from a plain server unless the plain server code manually invokes the same pointcuts.
  * Repository validation does _NOT_ provide your custom pre-storage business logical layer with any guarantees of the profile as the resource has not been hit by the proper pointcut. This means that you cannot make reasonable profile assumptions in your pre-storage logic handling the resource.


#  13.5.3Using the Repository Validating Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#using-the-repository-validating-interceptor)
Using the repository validating interceptor is as simple as creating a new instance of [RepositoryValidatingInterceptor](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/interceptor/validation/RepositoryValidatingInterceptor.html) and registering it with the interceptor registry. The only tricky part is initializing your rules, which must be done using a [RepositoryValidatingRuleBuilder](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/interceptor/validation/RepositoryValidatingRuleBuilder.html) .
The rule builder must be obtained from the Spring context, as shown below:
```
// First you must ask the Spring Application Context for a rule builder
RepositoryValidatingRuleBuilder ruleBuilder = myAppCtx.getBean(RepositoryValidatingRuleBuilder.class);

// Add a simple rule requiring all Patient resources to declare conformance to the US Core
// Patient Profile, and to validate successfully.
ruleBuilder
      .forResourcesOfType("Patient")
      .requireAtLeastProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient")
      .and()
      .requireValidationToDeclaredProfiles();

// Build the rule list
List<IRepositoryValidatingRule> rules = ruleBuilder.build();

// Create and register the interceptor
RepositoryValidatingInterceptor interceptor = new RepositoryValidatingInterceptor(myFhirCtx, rules);
myInterceptorService.registerInterceptor(interceptor);

```

Copy
#  13.5.4Rules: Require Profile Declarations
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#rules-require-profile-declarations)
Use one of the following rule formats to require any resources submitted to the server to declare conformance to the given profiles.
```
// Require Patient resources to declare conformance to US Core patient profile
ruleBuilder
      .forResourcesOfType("Patient")
      .requireAtLeastProfile("http://www.hl7.org/fhir/us/core/StructureDefinition-us-core-patient.html");

// Require Patient resources to declare conformance to either the US Core patient profile
// or the UK Core patient profile
ruleBuilder
      .forResourcesOfType("Patient")
      .requireAtLeastOneProfileOf(
            "http://www.hl7.org/fhir/us/core/StructureDefinition-us-core-patient.html",
            "https://fhir.nhs.uk/R4/StructureDefinition/UKCore-Patient");

```

Copy
Note that this rule alone does not actually enforce validation against the specified profiles. It simply requires that any resources submitted to the server contain a declaration that they intend to conform. See the following example:
```
{
   "resourceType": "Patient",
   "id": "123",
   "meta": {
      "profile": [
         "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"
      ]
   }
}

```

Copy
#  13.5.5Rules: Require Validation to Declared Profiles
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#require-validation)
Use the following rule to require that resources of the given type be validated successfully before allowing them to be persisted. For every resource of the given type that is submitted for storage, the `Resource.meta.profile` field will be examined and the resource will be validated against any declarations found there.
This rule is generally combined with the _Require Profile Declarations_ above.
```
// Require Patient resources to validate to any declared profiles
ruleBuilder.forResourcesOfType("Patient").requireValidationToDeclaredProfiles();

```

Copy
Any resource creates or updates that do not conform to the given profile will be rejected.
##  13.5.5.1Adjusting Failure Threshold[](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#adjusting-failure-threshold)
By default, any validation messages with a severity value of _ERROR_ or _FATAL_ will result in resource creates or updates being rejected. This threshold can be adjusted however:
```
ruleBuilder
      .forResourcesOfType("Patient")
      .requireValidationToDeclaredProfiles()
      .rejectOnSeverity(ResultSeverityEnum.WARNING);

```

Copy
##  13.5.5.2Tagging Validation Failures[](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#tagging-validation-failures)
By default, resource updates/changes resulting in failing validation will cause the operation to be rolled back. You can alternately configure the rule to allow the change to proceed but add an arbitrary tag to the resource when it is saved.
```
ruleBuilder
      .forResourcesOfType("Patient")
      .requireValidationToDeclaredProfiles()
      .neverReject()
      .tagOnSeverity(ResultSeverityEnum.ERROR, "http://example.com", "validation-failure");

```

Copy
##  13.5.5.3Configuring the Validator[](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#configuring-the-validator)
The following snippet shows a number of additional optional settings that can be chained onto the validation rule.
```
ruleBuilder
      .forResourcesOfType("Patient")
      .requireValidationToDeclaredProfiles()

      // Configure the validator to reject unknown extensions
      // by default, all extensions are accepted and to undo this rejection
      // call allowAnyExtensions()
      .rejectUnknownExtensions()

      // Configure the validator to not perform terminology validation
      .disableTerminologyChecks()

      // Configure the validator to raise an error if a resource being
      // validated declares a profile, and the StructureDefinition for
      // this profile can not be found.
      .errorOnUnknownProfiles()

      // Configure the validator to suppress the information-level
      // message that is added to the validation result if a profile
      // StructureDefinition does not declare a binding for a coded
      // field.
      .suppressNoBindingMessage()

      // Configure the validator to suppress the warning-level message
      // that is added when validating a code that can't be found in a
      // ValueSet that has an extensible binding.
      .suppressWarningForExtensibleValueSetValidation();

```

Copy
#  13.5.6Rules: Disallow Specific Profiles
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#rules-disallow-specific-profiles)
Rules can declare that a specific profile is not allowed.
```
// No UK Core patients allowed!
ruleBuilder
      .forResourcesOfType("Patient")
      .disallowProfile("https://fhir.nhs.uk/R4/StructureDefinition/UKCore-Patient");

```

Copy
#  13.5.7Adding Validation Outcome to HTTP Response
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#adding-validation-outcome-to-http-response)
If you have included a [Require Validation](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html#require-validation) rule to your chain, you can add the `ValidationResultEnrichingInterceptor` to your server if you wish to have validation results added to and OperationOutcome objects that are returned by the server.
[ ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
13.5 Repository Validating Interceptor 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
[ 13.6 Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)