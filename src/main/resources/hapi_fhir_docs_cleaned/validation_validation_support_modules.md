---
source: https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html
crawled: 2025-08-01T14:05:19.777675
---

# Validation Validation Support Modules

#  13.3.1Validation Support Modules
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#validation-support-modules)
The [Instance Validator](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html) relies on an implementation of an interface called [IValidationSupport](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/support/IValidationSupport.html) to load StructureDefinitions, validate codes, etc.
By default, an implementation of this interface called [DefaultProfileValidationSupport](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/undefined/ca/uhn/fhir/context/support/DefaultProfileValidationSupport.html) is used. This implementation simply uses the built-in official FHIR definitions to validate against (and in many cases, this is good enough).
However, if you have needs beyond simply validating against the core FHIR specification, you may wish to use something more.
#  13.3.2Built-In Validation Support Classes
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#built-in-validation-support-classes)
There are a several implementations of the [IValidationSupport](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/support/IValidationSupport.html) interface built into HAPI FHIR that can be used, typically in a chain.
#  13.3.3ValidationSupportChain
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#validationsupportchain)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/ValidationSupportChain.html) / [Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/ValidationSupportChain.java)
This module can be used to combine multiple implementations together so that for every request, each support class instance in the chain is tried in sequence. Note that nearly all methods in the [IValidationSupport](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/support/IValidationSupport.html) interface are permitted to return `null` if they are not able to service a particular method call. So for example, if a call to the [`validateCode`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/support/IValidationSupport.html#validateCode\(ca.uhn.fhir.context.support.ValidationSupportContext,ca.uhn.fhir.context.support.ConceptValidationOptions,java.lang.String,java.lang.String,java.lang.String,java.lang.String\)) method is made, the validator will try each module in the chain until one of them returns a non-null response.
The following chaining logic is used:
  * Calls to `fetchAll...` methods such as `fetchAllConformanceResources()` and `fetchAllStructureDefinitions()` will call every method in the chain in order, and aggregate the results into a single list to return.
  * Calls to fetch or validate codes, such as `validateCode(...)` and `lookupCode(...)` will first test each module in the chain using the`isCodeSystemSupported(...)` or `isValueSetSupported(...)` methods (depending on whether a ValueSet URL is present in the method parameters) and will invoke any methods in the chain which return that they can handle the given CodeSystem/ValueSet URL. The first non-null value returned by a method in the chain that can support the URL will be returned to the caller.
  * All other methods will invoke the method in the chain in order, and will return immediately as soon as a non-null value is returned.


The following caching logic is used if caching is enabled using `CacheConfiguration`. You can use `CacheConfiguration.disabled()` if you want to disable caching.
  * Calls to fetch StructureDefinitions including `fetchAllStructureDefinitions()` and `fetchStructureDefinition(...)` are cached in a non-expiring cache. This is because the `FhirInstanceValidator` module makes assumptions that these objects will not change for the lifetime of the validator for performance reasons.
  * Calls to all other `fetchAll...` methods including `fetchAllConformanceResources()` and `fetchAllSearchParameters()` cache their results in an expiring cache, but will refresh that cache asynchronously.
  * Results of `generateSnapshot(...)` are not cached, as this method is generally called in contexts where the results are cached.
  * Results of all other methods are stored in an expiring cache.


Note that caching functionality used to be provided by a separate provider called {@literal CachingValidationSupport} but that functionality has been moved into this class as of HAPI FHIR 8.0.0, because it is possible to provide a more efficient chain when these functions are combined.
#  13.3.4DefaultProfileValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#defaultprofilevalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/undefined/ca/uhn/fhir/context/support/DefaultProfileValidationSupport.html) / [Source](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-base/src/main/java/ca/uhn/fhir/context/support/DefaultProfileValidationSupport.java)
This module supplies the built-in FHIR core structure definitions, including both FHIR resource definitions (StructureDefinition resources) and FHIR built-in vocabulary (ValueSet and CodeSystem resources).
#  13.3.5InMemoryTerminologyServerValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#inmemoryterminologyservervalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/InMemoryTerminologyServerValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/InMemoryTerminologyServerValidationSupport.java)
This module acts as a simple terminology service that can validate codes against ValueSet and CodeSystem resources purely in-memory (i.e. with no database). This is sufficient in many basic cases, although it is not able to validate CodeSystems with external content (i.e CodeSystems where the `CodeSystem.content` field is `external`, such as the LOINC and SNOMED CT CodeSystems).
#  13.3.6PrePopulatedValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#prepopulatedvalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/PrePopulatedValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/PrePopulatedValidationSupport.java)
This module contains a series of HashMaps that store loaded conformance resources in memory. Typically this is initialized at startup in order to add custom conformance resources into the chain.
#  13.3.7NpmPackageValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#npmpackagevalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/NpmPackageValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/NpmPackageValidationSupport.java)
This module can be used to load FHIR NPM Packages and supply the conformance resources within them to the validator. See [Validating Using Packages](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html#packages) for am example of how to use this module.
#  13.3.8SnapshotGeneratingValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#snapshotgeneratingvalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/SnapshotGeneratingValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/SnapshotGeneratingValidationSupport.java)
This module generates StructureDefinition snapshots as needed. This should be added to your chain if you are working wiith differential StructureDefinitions that do not include the snapshot view.
#  13.3.9CommonCodeSystemsTerminologyService
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#commoncodesystemsterminologyservice)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/CommonCodeSystemsTerminologyService.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/CommonCodeSystemsTerminologyService.java)
This module validates codes in CodeSystems that are not distributed with the FHIR specification because they are difficult to distribute but are commonly used in FHIR resources.
The following table lists vocabulary that is validated by this module:
Name | Canonical URLs | Validation Details  
---|---|---  
USPS State Codes |  ValueSet: [(...)/ValueSet/us-core-usps-state](http://hl7.org/fhir/us/core/ValueSet/us-core-usps-state)   
CodeSystem: <https://www.usps.com/> |  Codes are validated against a built-in list of valid state codes.   
MimeTypes (BCP-13) |  ValueSet: [(...)/ValueSet/mimetypes](http://hl7.org/fhir/ValueSet/mimetypes)   
CodeSystem: `urn:ietf:bcp:13` |  Codes are not validated, but are instead assumed to be correct. Improved validation should be added in the future, please get in touch if you would like to help.   
Languages (BCP-47) |  ValueSet: [(...)/ValueSet/languages](http://hl7.org/fhir/ValueSet/languages)   
ValueSet: [(...)/ValueSet/all-languages](http://hl7.org/fhir/ValueSet/all-languages)   
CodeSystem: `urn:ietf:bcp:47` |  Codes are validated against the respective ValueSet. Support for two different ValueSets is provided: The [languages](http://hl7.org/fhir/ValueSet/languages) ValueSet provides a collection of commonly used language codes. Only codes explicitly referenced in this ValueSet are considered valid. The [all-languages](http://hl7.org/fhir/ValueSet/languages) ValueSet accepts any valid BCP-47 code. Codes are validated using data supplied by the [Language Subtype Registry](https://github.com/mattcg/language-subtag-registry) project.   
Countries (ISO 3166) |  CodeSystem: [urn:iso:std:iso:3166](urn:iso:std:iso:3166) |  Codes are validated against a built-in list of valid ISO 3166 codes. Both Alpha-2 (two character) and Alpha-3 (three character) variants are supported.   
Unified Codes for Units of Measure (UCUM) |  ValueSet: `(...)/ValueSet/ucum-units[](http://hl7.org/fhir/ValueSet/ucum-units)`   
CodeSystem: `http://unitsofmeasure.org` |  Codes are validated using the UcumEssenceService provided by the [UCUM Java](https://github.com/FHIR/Ucum-java) library.   
#  13.3.10RemoteTerminologyServiceValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#remoteterminologyservicevalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/RemoteTerminologyServiceValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/RemoteTerminologyServiceValidationSupport.java)
This module validates codes using a remote FHIR-based terminology server.
This module will invoke the following operations on the remote terminology server:
  * **GET [base]/CodeSystem?url=[url]** – Tests whether a given CodeSystem is supported on the server
  * **GET [base]/ValueSet?url=[url]** – Tests whether a given ValueSet is supported on the server
  * **POST [base]/CodeSystem/$validate-code** – Validate codes in fields where no specific ValueSet is bound
  * **POST [base]/ValueSet/$validate-code** – Validate codes in fields where a specific ValueSet is bound


#  13.3.11UnknownCodeSystemWarningValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#unknowncodesystemwarningvalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/UnknownCodeSystemWarningValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/UnknownCodeSystemWarningValidationSupport.java)
This validation support module may be placed at the end of a ValidationSupportChain in order to configure the validator to generate a warning if a resource being validated contains an unknown code system.
Note that this module must also be activated by calling [setAllowNonExistentCodeSystem(true)](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/UnknownCodeSystemWarningValidationSupport.html#setAllowNonExistentCodeSystem\(boolean\)) in order to specify that unknown code systems should be allowed.
#  13.3.12CachingValidationSupport
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#cachingvalidationsupport)
[JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-validation/org/hl7/fhir/common/hapi/validation/support/CachingValidationSupport.html) / [Source](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-validation/src/main/java/org/hl7/fhir/common/hapi/validation/support/CachingValidationSupport.java)
This module is deprecated and no longer provides any functionality. Caching is provided by [ValidationSupportChain](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#validationsupportchain).
#  13.3.13Recipes
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#recipes)
The IValidationSupport instance passed to the FhirInstanceValidator will often resemble the chain shown in the diagram below. In this diagram:
  * DefaultProfileValidationSupport is used to supply basic built-in FHIR definitions
  * PrePopulatedValidationSupport is used to supply other custom definitions
  * InMemoryTerminologyServerValidationSupport is used to validate terminology
  * The modules above are all added to a chain via ValidationSupportChain
  * Finally, a cache is placed in front of the entire chain in order to improve performance


[![Validation Support Chain](https://hapifhir.io/hapi-fhir/docs/images/validation-support-chain.svg)  
(expand)](https://hapifhir.io/hapi-fhir/docs/images/validation-support-chain.svg)
#  13.3.14Recipe: Supplying Custom Definitions
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#recipe-supplying-custom-definitions)
The following snippet shows how to supply custom definitions to the validator.
```
FhirContext ctx = FhirContext.forR4();

// Create a chain that will hold our modules and caches the
// values they supply
ValidationSupportChain supportChain = new ValidationSupportChain();

// DefaultProfileValidationSupport supplies base FHIR definitions. This is generally required
// even if you are using custom profiles, since those profiles will derive from the base
// definitions.
DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport(ctx);
supportChain.addValidationSupport(defaultSupport);

// This module supplies several code systems that are commonly used in validation
supportChain.addValidationSupport(new CommonCodeSystemsTerminologyService(ctx));

// This module implements terminology services for in-memory code validation
supportChain.addValidationSupport(new InMemoryTerminologyServerValidationSupport(ctx));

// Create a PrePopulatedValidationSupport which can be used to load custom definitions.
// In this example we're loading two things, but in a real scenario we might
// load many StructureDefinitions, ValueSets, CodeSystems, etc.
PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport(ctx);
prePopulatedSupport.addStructureDefinition(someStructureDefnition);
prePopulatedSupport.addValueSet(someValueSet);

// Add the custom definitions to the chain
supportChain.addValidationSupport(prePopulatedSupport);

// Create a validator using the FhirInstanceValidator module. We can use this
// validator to perform validation
FhirInstanceValidator validatorModule = new FhirInstanceValidator(supportChain);
FhirValidator validator = ctx.newValidator().registerValidatorModule(validatorModule);
ValidationResult result = validator.validateWithResult(input);

```

Copy
#  13.3.15Recipe: Using a Remote Terminology Server
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html#recipe-using-a-remote-terminology-server)
The following snippet shows how to leverage a remote (FHIR-based) terminology server, by making REST calls to the external service when codes need to be validated.
```
FhirContext ctx = FhirContext.forR4();

// Create a chain that will hold our modules
ValidationSupportChain supportChain = new ValidationSupportChain();

// DefaultProfileValidationSupport supplies base FHIR definitions. This is generally required
// even if you are using custom profiles, since those profiles will derive from the base
// definitions.
DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport(ctx);
supportChain.addValidationSupport(defaultSupport);

// Create a module that uses a remote terminology service
RemoteTerminologyServiceValidationSupport remoteTermSvc = new RemoteTerminologyServiceValidationSupport(ctx);
remoteTermSvc.setBaseUrl("http://hapi.fhir.org/baseR4");
supportChain.addValidationSupport(remoteTermSvc);

// Create a validator using the FhirInstanceValidator module. We can use this
// validator to perform validation
FhirInstanceValidator validatorModule = new FhirInstanceValidator(supportChain);
FhirValidator validator = ctx.newValidator().registerValidatorModule(validatorModule);
ValidationResult result = validator.validateWithResult(input);

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
13.3 Validation Support Modules 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
[ 13.4 Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)