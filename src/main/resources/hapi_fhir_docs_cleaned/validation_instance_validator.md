---
source: https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html
crawled: 2025-08-01T14:09:18.758119
---

# Validation Instance Validator

#  13.2.1Instance Validator
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#instance-validator)
HAPI provides a built-in and configurable mechanism for validating resources using FHIR's own conformance resources (StructureDefinition, ValueSet, CodeSystem, etc.). This mechanism is called the _Instance Validator_.
The resource validator is an extendible and modular system, and you can configure it in a number of ways in order to get the specific type of validation you want to achieve.
The validator can be manually invoked at any time by creating a validator and configuring it with one or more [IValidatorModule](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/validation/IValidatorModule.html) instances.
```
FhirContext ctx = FhirContext.forR4();

// Ask the context for a validator
FhirValidator validator = ctx.newValidator();

// Create a validation module and register it
IValidatorModule module = new FhirInstanceValidator(ctx);
validator.registerValidatorModule(module);

// Pass a resource instance as input to be validated
Patient resource = new Patient();
resource.addName().setFamily("Simpson").addGiven("Homer");
ValidationResult result = validator.validateWithResult(resource);

// The input can also be a raw string (this mechanism can
// potentially catch syntax issues that would have been missed
// otherwise, since the HAPI FHIR Parser is forgiving about
// its input.
String resourceText = "<Patient.....>";
ValidationResult result2 = validator.validateWithResult(resourceText);

// The result object now contains the validation results
for (SingleValidationMessage next : result.getMessages()) {
   System.out.println(next.getLocationString() + " " + next.getMessage());
}

```

Copy
Note that in earlier releases of HAPI FHIR it was common to register different kinds of validator modules (such as [Schema/Schematron](./schema_validator.html)) because the FHIR Instance Validator module described below was not mature. This is no longer the case, and it is generally recommended to use the FHIR Instance Validator. 
#  13.2.2FHIR Conformance Concepts
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#fhir-conformance-concepts)
There are a few key concepts worth explaining before getting into how validation is performed in HAPI FHIR.
Conformance Resources:
  * [StructureDefinition](http://hl7.org/fhir/structuredefinition.html) – Contains definitions of the valid fields in a given resource, including details about their datatypes, min/max cardinalities, valid values, and other rules about what content is valid and what is not. StructureDefinition resources are also used to express derivative profiles (e.g. a description of a constraint on a FHIR resource for a specfic purpose) as well as to describe extensions.
  * [CodeSystem](http://hl7.org/fhir/codesystem.html) – Contains definiitions of codes and vocabularies that can be used in FHIR resources, or even outside of FHIR resources.
  * [ValueSet](http://hl7.org/fhir/valueset.html) – Contains lists of codes drawn from one or more CodeSystems that are suitable for use in a specific field in a FHIR resource.


#  13.2.3FHIR Instance Validator
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#fhir-instance-validator)
**Note on HAPI FHIR 5.0.0+:** Many of the classes described here have changed in HAPI FHIR 5.0.0 and existing users of HAPI FHIR may need to migrate existing validation code in order to successfully use the validator in HAPI FHIR 5.0.0 and beyond. See [Migrating to 5.x](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#migrating-to-5x) for information. 
HAPI has very complete support for validation against FHIR conformance resources.
This functionality is proviided by the HAPI FHIR "reference validator", which is able to check a resource for conformance to FHIR profiles.
The FHIR instance validator is very powerful. It will use terminology services to validate codes, StructureDefinitions to validate semantics, and uses a customized XML/JSON parser in order to provide descriptive error messages.
It is always worth considering the performance implications of using the Instance Validator at runtime in a production system. While efforts are made to keep the Instance Validator and its supporting infrastructure performant, the act of performing deep semantic validation is never going to be without some performance cost.
The FHIR instance validator can be used to validate a resource against the official structure definitions (produced by HL7) as well as against custom definitions provided either by HL7 or by the user.
#  13.2.4Running the Validator
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#running-the-validator)
To execute the validator, you create a [validation support chain](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html) and pass this to an instance of [FhirInstanceValidator](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/validator/FhirInstanceValidator.html). The FhirInstanceValidator is then used as a module for the HAPI FHIR validation framework.
Note that the example below uses the official FHIR StructureDefintions and ValueSets to validate the resource. It will not work unless you include the **hapi-fhir-validation-resources-[version].jar** module/JAR on your classpath.
```
FhirContext ctx = FhirContext.forR4();

// Create a validation support chain
ValidationSupportChain validationSupportChain = new ValidationSupportChain(
      new DefaultProfileValidationSupport(ctx),
      new InMemoryTerminologyServerValidationSupport(ctx),
      new CommonCodeSystemsTerminologyService(ctx));

// Create a FhirInstanceValidator and register it to a validator
FhirValidator validator = ctx.newValidator();
FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);
validator.registerValidatorModule(instanceValidator);

/*
 * If you want, you can configure settings on the validator to adjust
 * its behaviour during validation
 */
instanceValidator.setAnyExtensionsAllowed(true);

/*
 * Let's create a resource to validate. This Observation has some fields
 * populated, but it is missing Observation.status, which is mandatory.
 */
Observation obs = new Observation();
obs.getCode().addCoding().setSystem("http://loinc.org").setCode("12345-6");
obs.setValue(new StringType("This is a value"));

// Validate
ValidationResult result = validator.validateWithResult(obs);

/*
 * Note: You can also explicitly declare a profile to validate against
 * using the block below.
 */
// ValidationResult result = validator.validateWithResult(obs, new
// ValidationOptions().addProfile("http://myprofile.com"));

// Do we have any errors or fatal errors?
System.out.println(result.isSuccessful()); // false

// Show the issues
for (SingleValidationMessage next : result.getMessages()) {
   System.out.println(
         " Next issue " + next.getSeverity() + " - " + next.getLocationString() + " - " + next.getMessage());
}
// Prints:
// Next issue ERROR - /f:Observation - Element '/f:Observation.status': minimum required = 1, but only found 0
// Next issue WARNING - /f:Observation/f:code - Unable to validate code "12345-6" in code system
// "http://loinc.org"

// You can also convert the result into an operation outcome if you
// need to return one from a server
OperationOutcome oo = (OperationOutcome) result.toOperationOutcome();

```

Copy
#  13.2.5Validating Using Packages
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#packages)
HAPI FHIR supports the use of FHIR NPM Packages for supplying validation artifacts.
When using the HAPI FHIR [JPA Server](https://hapifhir.io/hapi-fhir/docs/server_jpa/) you can simply upload your packages into the JPA Server package registry and the contents will be made available to the validator.
If you are using the validator as a standalone service (i.e. you are invoking it via a Java call) you will need to explicitly make your packages available to the validation support chain.
The following example shows the use of [NpmPackageValidationSupport](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#npmpackagevalidationsupport) to load a package and use it to validate a resource.
```
// Create an NPM Package Support module and load one package in from
// the classpath
FhirContext ctx = FhirContext.forR4();
NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
npmPackageSupport.loadPackageFromClasspath("classpath:package/UK.Core.r4-1.1.0.tgz");

// Create a support chain including the NPM Package Support
ValidationSupportChain validationSupportChain = new ValidationSupportChain(
      npmPackageSupport,
      new DefaultProfileValidationSupport(ctx),
      new CommonCodeSystemsTerminologyService(ctx),
      new InMemoryTerminologyServerValidationSupport(ctx),
      new SnapshotGeneratingValidationSupport(ctx));

// Create a validator. Note that for good performance you can create as many validator objects
// as you like, but you should reuse the same validation support object in all of the,.
FhirValidator validator = ctx.newValidator();
FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);
validator.registerValidatorModule(instanceValidator);

// Create a test patient to validate
Patient patient = new Patient();
patient.getMeta().addProfile("https://fhir.nhs.uk/R4/StructureDefinition/UKCore-Patient");
// System but not value set for NHS identifier (this should generate an error)
patient.addIdentifier().setSystem("https://fhir.nhs.uk/Id/nhs-number");

// Perform the validation
ValidationResult outcome = validator.validateWithResult(patient);

```

Copy
#  13.2.6Migrating to HAPI FHIR 5.x
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#migrating-to-hapi-fhir-5x)
HAPI FHIR 5.x contained a significant rewrite of the IValidationSupport interface and the entire validation support module infrastructure.
Users wishing to upgrade an existing application from HAPI FHIR 4.2 or earlier may need to consider the following points (note that the HAPI FHIR JPA server has already been adapted to use the new infrastructure, so most users of the JPA server will not need to make any changes in this regard).
  * The `IContextValidationSupport` interface has been renamed to `IValidationSupport`. Previous versions of HAPI had a number of sub-interfaces of IContextValidationSupport that were all named IValidationSupport (but were FHIR version-specific and were located in distinct packages). These previous interfaces named IValidationSupport have all been removed.
  * The `IValidationSupport` interface has been reworked significantly in order to simplify extension (these points apply only to users who have created custom implementations of this interface):
    * A method called `getFhirContext()` has been added, meaning that all validation support modules must be able to report their FhirContext object. This is used to ensure that all modules in the chain are consistent with each other in terms of supported FHIR version, etc.
    * All other methods in the interface now have a default implementation which returns a null response. This means that custom implementations of this interface only need to implement the methods they care about.
    * The `validateCode(...)` methods previously passed in a special constant to `theCodeSystem` (the code system URL) parameter in cases where the system URL was implied and not explicit (e.g. when validating the `Patient.gender` field, where the resource body does not contain the code system URL). This constant was confusing to implementors and has been replaced with a new parameter of type [ConceptValidationOptions](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/support/ConceptValidationOptions.html) that supplies details about the validation.
  * Many classes were previously duplicated across different FHIR versions. For example, there were previously 5 classes named `DefaultProfileValidationSupport` spanning the different versions of FHIR that were supported by the validator. As of HAPI FHIR 5.x, a single [DefaultProfileValidationSupport](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/undefined/ca/uhn/fhir/context/support/DefaultProfileValidationSupport.html) class exists. Users if this class (and several other implementations of the IValidationSupport interface may need to change their package declarations.
  * The DefaultProfileValidationSupport module previously contained code to perform terminology/code validation based on the CodeSystems it contained. This functionality has been relocated to a new module called InMemoryTerminologyServerValidationSupport(/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/InMemoryTerminologyServerValidationSupport.html), so this module should generally be added to the chain.


[ ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
13.2 Instance Validator 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
[ 13.3 Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)