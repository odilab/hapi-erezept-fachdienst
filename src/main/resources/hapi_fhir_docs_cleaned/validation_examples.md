---
source: https://hapifhir.io/hapi-fhir/docs/validation/examples.html
crawled: 2025-08-01T14:06:51.890116
---

# Validation Examples

#  13.6.1Validation Examples
[ ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html#validation-examples)
##  13.6.1.1Generate a Snapshot profile from a Differential[](https://hapifhir.io/hapi-fhir/docs/validation/examples.html#generate-a-snapshot-profile-from-a-differential)
The following code can be used to generate a Snapshot Profile (StructureDefinition) when all you have is a differential.
```
// Create a validation support chain that includes default validation support 
// and a snapshot generator
DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport();
SnapshotGeneratingValidationSupport snapshotGenerator = new SnapshotGeneratingValidationSupport(myFhirCtx, defaultSupport);
ValidationSupportChain chain = new ValidationSupportChain(defaultSupport, snapshotGenerator);

// Generate the snapshot
StructureDefinition snapshot = chain.generateSnapshot(differential, "http://foo", null, "THE BEST PROFILE");

```

Copy
##  13.6.1.2Validate a Resource with Cross Version Extensions[](https://hapifhir.io/hapi-fhir/docs/validation/examples.html#validate-a-resource-with-cross-version-extensions)
The following code can be used to validate a resource using FHIR [Cross Version Extensions](http://hl7.org/fhir/versions.html#extensions).
Note that you must have the [hl7.fhir.xver-extensions-0.0.11.tgz](http://fhir.org/packages/hl7.fhir.xver-extensions/0.0.11/package.tgz) package available in your classpath.
```
// Create a validation support chain that includes default validation support
// and support from the hl7.fhir.xver-extensions NPM pacakage. 
NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(myFhirCtx);
npmPackageSupport.loadPackageFromClasspath("classpath:package/hl7.fhir.xver-extensions-0.0.11.tgz");

myFhirCtx.setValidationSupport(new ValidationSupportChain(
        new DefaultProfileValidationSupport(myFhirCtx),
        npmPackageSupport
   ));
    
FhirInstanceValidator instanceValidator = new FhirInstanceValidator(myFhirCtx);
	
FhirValidator validator = myFhirCtx.newValidator();
validator.registerValidatorModule(instanceValidator);

// Validate theResource
ValidationResult validationResult = validator.validateWithResult(theResource);

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
13.6 Validation Examples 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)