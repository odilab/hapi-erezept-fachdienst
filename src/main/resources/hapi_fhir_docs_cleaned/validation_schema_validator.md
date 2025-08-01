---
source: https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html
crawled: 2025-08-01T14:05:56.977472
---

# Validation Schema Validator

#  13.4.1Schema / Schematron Validator
[ ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html#schema-schematron-validator)
FHIR resource definitions are distributed with a set of XML schema files (XSD) as well as a set of XML Schematron (SCH) files. These two sets of files are complementary to each other, meaning that in order to claim compliance to the FHIR specification, your resources must validate against both sets.
The two sets of files are included with HAPI, and it uses them to perform validation.
#  13.4.2Preparation
[ ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html#preparation)
In order to use HAPI's Schematron support, a library called [Ph-Schematron](https://github.com/phax/ph-schematron) is used, so this library must be added to your classpath (or Maven POM file, Gradle file, etc.)
Note that this library is specified as an optional dependency by HAPI FHIR so you need to explicitly include it if you want to use this functionality.
#  13.4.3Validating a Resource
[ ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html#validating-a-resource)
To validate a resource instance, a new validator instance is requested from the FHIR Context. This validator is then applied against a specific resource instance, as shown in the example below.
```
// As always, you need a context
FhirContext ctx = FhirContext.forR4();

// Create and populate a new patient object
Patient p = new Patient();
p.addName().setFamily("Smith").addGiven("John").addGiven("Q");
p.addIdentifier().setSystem("urn:foo:identifiers").setValue("12345");
p.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("416 123-4567");

// Request a validator and apply it
FhirValidator val = ctx.newValidator();

// Create the Schema/Schematron modules and register them. Note that
// you might want to consider keeping these modules around as long-term
// objects: they parse and then store schemas, which can be an expensive
// operation.
IValidatorModule module1 = new SchemaBaseValidator(ctx);
IValidatorModule module2 = new SchematronBaseValidator(ctx);
val.registerValidatorModule(module1);
val.registerValidatorModule(module2);

ValidationResult result = val.validateWithResult(p);
if (result.isSuccessful()) {

   System.out.println("Validation passed");

} else {
   // We failed validation!
   System.out.println("Validation failed");
}

// The result contains a list of "messages"
List<SingleValidationMessage> messages = result.getMessages();
for (SingleValidationMessage next : messages) {
   System.out.println("Message:");
   System.out.println(" * Location: " + next.getLocationString());
   System.out.println(" * Severity: " + next.getSeverity());
   System.out.println(" * Message : " + next.getMessage());
}

// You can also convert the results into an OperationOutcome resource
OperationOutcome oo = (OperationOutcome) result.toOperationOutcome();
String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo);
System.out.println(results);

```

Copy
##  13.4.3.1Validating a Set of Files[](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html#validating-a-set-of-files)
The following example shows how to load a set of resources from files on disk and validate each one.
```
FhirContext ctx = FhirContext.forR4();

// Create a validator and configure it
FhirValidator validator = ctx.newValidator();
validator.setValidateAgainstStandardSchema(true);
validator.setValidateAgainstStandardSchematron(true);

// Get a list of files in a given directory
String[] fileList = new File("/home/some/dir").list(new WildcardFileFilter("*.txt"));
for (String nextFile : fileList) {

   // For each file, load the contents into a string
   String nextFileContents = IOUtils.toString(new FileReader(nextFile));

   // Parse that string (this example assumes JSON encoding)
   IBaseResource resource = ctx.newJsonParser().parseResource(nextFileContents);

   // Apply the validation. This will throw an exception on the first
   // validation failure
   ValidationResult result = validator.validateWithResult(resource);
   if (result.isSuccessful() == false) {
      throw new Exception(Msg.code(640) + "We failed!");
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
13.4 Schema/Schematron Validator 
* Validation 
* [ 13.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/validation/introduction.html)
* [ 13.1  Parser Error Handler ](https://hapifhir.io/hapi-fhir/docs/validation/parser_error_handler.html)
* [ 13.2  Instance Validator ](https://hapifhir.io/hapi-fhir/docs/validation/instance_validator.html)
* [ 13.3  Validation Support Modules ](https://hapifhir.io/hapi-fhir/docs/validation/validation_support_modules.html)
* [ 13.4  Schema/Schematron Validator ](https://hapifhir.io/hapi-fhir/docs/validation/schema_validator.html)
* [ 13.5  Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* [ 13.6  Validation Examples ](https://hapifhir.io/hapi-fhir/docs/validation/examples.html)
[ 13.5 Repository Validating Interceptor ](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)