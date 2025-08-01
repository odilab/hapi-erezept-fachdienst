---
source: https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html
crawled: 2025-08-01T14:05:27.233995
---

# Model Profiles And Extensions

#  2.3.1Profiles and Extensions
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#profiles-and-extensions)
This page describes how to extend and constrain the FHIR data model for your own purposes.
#  2.3.2Extensions
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#extensions)
Note on FHIR Versions: Because of the differences in the way the structures work between DSTU2 and DSTU3, we have provided two versions of many of the examples on this page. See the [FHIR Versions](https://hapifhir.io/hapi-fhir/docs/getting_started/versions.html) page for more information on FHIR version support in HAPI FHIR. 
Extensions are a key part of the FHIR specification, providing a standardized way of placing additional data in a resource.
The simplest way to interact with extensions (i.e. to add them to resources you are creating, or to read them from resources you are consuming) is to treat them as "undeclared extensions". Undeclared extensions can be added to any of the built in FHIR resource types that come with HAPI-FHIR.
2.3.2.0.1DSTU2 ```
// Create an example patient
Patient patient = new Patient();
patient.addIdentifier()
      .setUse(IdentifierUseEnum.OFFICIAL)
      .setSystem("urn:example")
      .setValue("7000135");

// Create an extension
ExtensionDt ext = new ExtensionDt();
ext.setModifier(false);
ext.setUrl("http://example.com/extensions#someext");
ext.setValue(new DateTimeDt("2011-01-02T11:13:15"));

// Add the extension to the resource
patient.addUndeclaredExtension(ext);

```
Copy 2.3.2.0.2DSTU3 and Later ```
// Create an example patient
Patient patient = new Patient();
patient.addIdentifier()
      .setUse(Identifier.IdentifierUse.OFFICIAL)
      .setSystem("urn:example")
      .setValue("7000135");

// Create an extension
Extension ext = new Extension();
ext.setUrl("http://example.com/extensions#someext");
ext.setValue(new DateTimeType("2011-01-02T11:13:15"));

// Add the extension to the resource
patient.addExtension(ext);

```
Copy Undeclared extensions can also be added to datatypes (composite or primitive). 2.3.2.0.3DSTU2 ```
// Continuing the example from above, we will add a name to the patient, and then
// add an extension to part of that name
HumanNameDt name = patient.addName();
name.addFamily().setValue("Shmoe");

// Add a new "given name", which is of type StringDt
StringDt given = name.addGiven();
given.setValue("Joe");

// Create an extension and add it to the StringDt
ExtensionDt givenExt = new ExtensionDt(false, "http://examples.com#moreext", new StringDt("Hello"));
given.addUndeclaredExtension(givenExt);

```
Copy 2.3.2.0.4DSTU3 and Later ```
// Continuing the example from above, we will add a name to the patient, and then
// add an extension to part of that name
HumanName name = patient.addName();
name.setFamily("Shmoe");

// Add a new "given name", which is of type String
StringType given = name.addGivenElement();
given.setValue("Joe");

// Create an extension and add it to the String
Extension givenExt = new Extension("http://examples.com#moreext", new StringType("Hello"));
given.addExtension(givenExt);

```
Copy
#  2.3.3Sub-Extensions
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#sub-extensions)
Extensions may also have child extensions as their content, instead of a datatype. This is done by adding a child undeclared extension to the parent extension.
2.3.3.0.1DSTU2 ```
Patient patient = new Patient();

// Add an extension (initially with no contents) to the resource
ExtensionDt parent = new ExtensionDt(false, "http://example.com#parent");
patient.addUndeclaredExtension(parent);

// Add two extensions as children to the parent extension
ExtensionDt child1 = new ExtensionDt(false, "http://example.com#childOne", new StringDt("value1"));
parent.addUndeclaredExtension(child1);

ExtensionDt child2 = new ExtensionDt(false, "http://example.com#childTwo", new StringDt("value1"));
parent.addUndeclaredExtension(child2);

```
Copy 2.3.3.0.2DSTU3 and Later ```
Patient patient = new Patient();

// Add an extension (initially with no contents) to the resource
Extension parent = new Extension("http://example.com#parent");
patient.addExtension(parent);

// Add two extensions as children to the parent extension
Extension child1 = new Extension("http://example.com#childOne", new StringType("value1"));
parent.addExtension(child1);

Extension child2 = new Extension("http://example.com#chilwo", new StringType("value1"));
parent.addExtension(child2);

```
Copy
#  2.3.4Retrieving Extension Values
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#retrieving-extension-values)
HAPI provides a few ways of accessing extension values in resources which are received from other sources (i.e. downloaded by a client).
2.3.4.0.1DSTU2 ```
// Get all extensions (modifier or not) for a given URL
List<ExtensionDt> resourceExts = patient.getUndeclaredExtensionsByUrl("http://fooextensions.com#exts");

// Get all non-modifier extensions regardless of URL
List<ExtensionDt> nonModExts = patient.getUndeclaredExtensions();

// Get all modifier extensions regardless of URL
List<ExtensionDt> modExts = patient.getUndeclaredModifierExtensions();

```
Copy 2.3.4.0.2DSTU3 and Later ```
// Get all extensions (modifier or not) for a given URL
List<Extension> resourceExts = patient.getExtensionsByUrl("http://fooextensions.com#exts");

// Get all non-modifier extensions regardless of URL
List<Extension> nonModExts = patient.getExtension();

// Get all modifier extensions regardless of URL
List<Extension> modExts = patient.getModifierExtension();

```
Copy
#  2.3.5Custom Resource Structures
[ ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#custom-resource-structures)
All of the examples on this page show how to work with the existing data model classes.
This is a great way to work with extensions, and most HAPI FHIR applications use the techniques described on this page. However, there is a more advanced technique available as well, involving the creation of custom Java classes that extend the built-in classes to add statically bound extensions (as opposed to the dynamically bound ones shown on this page). See [Custom Structures](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html) for more information.
[ ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
2.3 Profiles and Extensions 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.4 Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)