---
source: https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html
crawled: 2025-08-01T14:04:58.789815
---

# Model Bundle Builder

#  2.7.1Bundle Builder
[ ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#bundle-builder)
The BundleBuilder ([JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/util/BundleBuilder.html)) can be used to construct FHIR Bundles.
Note that this class is a work in progress! It does not yet support all transaction features. We will add more features over time, and document them here. Pull requests are welcomed.
#  2.7.2Transaction Resource Create
[ ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#transaction-resource-create)
To add an update (aka PUT) operation to a transaction bundle
```
// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Create a Patient to create
Patient patient = new Patient();
patient.setActive(true);

// Add the patient as a create (aka POST) to the Bundle
builder.addTransactionCreateEntry(patient);

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
##  2.7.2.1Conditional Create[](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#conditional-create)
If you want to perform a conditional create:
```
// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Create a Patient to create
Patient patient = new Patient();
patient.setActive(true);
patient.addIdentifier().setSystem("http://foo").setValue("bar");

// Add the patient as a create (aka POST) to the Bundle
builder.addTransactionCreateEntry(patient).conditional("Patient?identifier=http://foo|bar");

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
#  2.7.3Transaction Resource Updates
[ ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#transaction-resource-updates)
To add an update (aka PUT) operation to a transaction bundle:
```
// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Create a Patient to update
Patient patient = new Patient();
patient.setId("http://foo/Patient/123");
patient.setActive(true);

// Add the patient as an update (aka PUT) to the Bundle
builder.addTransactionUpdateEntry(patient);

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
##  2.7.3.1Conditional Update[](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#conditional-update)
If you want to perform a conditional update:
```
// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Create a Patient to update
Patient patient = new Patient();
patient.setActive(true);
patient.addIdentifier().setSystem("http://foo").setValue("bar");

// Add the patient as an update (aka PUT) to the Bundle
builder.addTransactionUpdateEntry(patient).conditional("Patient?identifier=http://foo|bar");

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
#  2.7.4Transaction Patch
[ ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#transaction-patch)
To add a PATCH operation to a transaction bundle:
```
// Create a FHIR Patch object
Parameters patch = new Parameters();
Parameters.ParametersParameterComponent op = patch.addParameter().setName("operation");
op.addPart().setName("type").setValue(new CodeType("replace"));
op.addPart().setName("path").setValue(new CodeType("Patient.active"));
op.addPart().setName("value").setValue(new BooleanType(false));

// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Create a target object (this is the ID of the resource that will be patched)
IIdType targetId = new IdType("Patient/123");

// Add the patch to the bundle
builder.addTransactionFhirPatchEntry(targetId, patch);

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
##  2.7.4.1Conditional Patch[](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#conditional-patch)
If you want to perform a conditional patch:
```
// Create a FHIR Patch object
Parameters patch = new Parameters();
Parameters.ParametersParameterComponent op = patch.addParameter().setName("operation");
op.addPart().setName("type").setValue(new CodeType("replace"));
op.addPart().setName("path").setValue(new CodeType("Patient.active"));
op.addPart().setName("value").setValue(new BooleanType(false));

// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);

// Add the patch to the bundle with a conditional URL
String conditionalUrl = "Patient?identifier=http://foo|123";
builder.addTransactionFhirPatchEntry(patch).conditional(conditionalUrl);

// Execute the transaction
IBaseBundle outcome =
      myFhirClient.transaction().withBundle(builder.getBundle()).execute();

```

Copy
#  2.7.5Customizing the Bundle
[ ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html#customizing-the-bundle)
If you want to manipulate a bundle:
```
// Create a TransactionBuilder
BundleBuilder builder = new BundleBuilder(myFhirContext);
// Set bundle type to be searchset
builder.setBundleField("type", "searchset")
      .setBundleField("id", UUID.randomUUID().toString())
      .setMetaField("lastUpdated", builder.newPrimitive("instant", new Date()));

// Create bundle entry
IBase entry = builder.addEntry();

// Create a Patient to create
Patient patient = new Patient();
patient.setActive(true);
patient.addIdentifier().setSystem("http://foo").setValue("bar");
builder.addToEntry(entry, "resource", patient);

// Add search results
IBase search = builder.addSearch(entry);
builder.setSearchField(search, "mode", "match");
builder.setSearchField(search, "score", builder.newPrimitive("decimal", BigDecimal.ONE));

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
2.7 Bundle Builder 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)