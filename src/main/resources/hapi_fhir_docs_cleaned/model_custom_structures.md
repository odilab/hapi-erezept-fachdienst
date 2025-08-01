---
source: https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html
crawled: 2025-08-01T14:09:24.444852
---

# Model Custom Structures

#  2.5.1Custom Structures
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#custom-structures)
Typically, when working with FHIR the right way to provide your own extensions is to work with existing resource types and simply add your own extensions and/or constrain out fields you don't need.
This process is described on the [Profiles & Extensions](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html) page.
There are situations however when you might want to create an entirely custom resource type. This feature should be used only if there is no other option, since it means you are creating a resource type that will not be interoperable with other FHIR implementations.
This is an advanced features and isn't needed for most uses of HAPI FHIR. Feel free to skip this page. For a simpler way of interacting with resource extensions, see [Profiles & Extensions](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html). 
#  2.5.2Extending FHIR Resource Classes
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#extending-fhir-resource-classes)
The most elegant way of adding extensions to a resource is through the use of custom fields. The following example shows a custom type which extends the FHIR Patient resource definition through two extensions.
```
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition class for adding extensions to the built-in
 * Patient resource type.
 *
 * Note the "profile" attribute below, which indicates the URL/ID of the
 * profile implemented by this resource. You are not required to supply this,
 * but if you do it will be automatically populated in the resource meta
 * tag if the resource is returned by a server.
 */
@ResourceDef(name = "Patient", profile = "http://example.com/StructureDefinition/mypatient")
public class MyPatient extends Patient {

   private static final long serialVersionUID = 1L;

   /**
    * Each extension is defined in a field. Any valid HAPI Data Type
    * can be used for the field type. Note that the [name=""] attribute
    * in the @Child annotation needs to match the name for the bean accessor
    * and mutator methods.
    */
   @Child(name = "petName")
   @Extension(url = "http://example.com/dontuse#petname", definedLocally = false, isModifier = false)
   @Description(shortDefinition = "The name of the patient's favourite pet")
   private StringType myPetName;

   /**
    * The second example extension uses a List type to provide
    * repeatable values. Note that a [max=] value has been placed in
    * the @Child annotation.
    *
    * Note also that this extension is a modifier extension
    */
   @Child(name = "importantDates", max = Child.MAX_UNLIMITED)
   @Extension(url = "http://example.com/dontuse#importantDates", definedLocally = false, isModifier = true)
   @Description(shortDefinition = "Some dates of note for this patient")
   private List<DateTimeType> myImportantDates;

   /**
    * It is important to override the isEmpty() method, adding a check for any
    * newly added fields.
    */
   @Override
   public boolean isEmpty() {
      return super.isEmpty() && ElementUtil.isEmpty(myPetName, myImportantDates);
   }

   /********
    * Accessors and mutators follow
    *
    * IMPORTANT:
    * Each extension is required to have an getter/accessor and a setter/mutator.
    * You are highly recommended to create getters which create instances if they
    * do not already exist, since this is how the rest of the HAPI FHIR API works.
    ********/

   /** Getter for important dates */
   public List<DateTimeType> getImportantDates() {
      if (myImportantDates == null) {
         myImportantDates = new ArrayList<DateTimeType>();
      }
      return myImportantDates;
   }

   /** Getter for pet name */
   public StringType getPetName() {
      if (myPetName == null) {
         myPetName = new StringType();
      }
      return myPetName;
   }

   /** Setter for important dates */
   public void setImportantDates(List<DateTimeType> theImportantDates) {
      myImportantDates = theImportantDates;
   }

   /** Setter for pet name */
   public void setPetName(StringType thePetName) {
      myPetName = thePetName;
   }
}

```

Copy
Using this custom type is as simple as instantiating the type and working with the new fields.
```
MyPatient patient = new MyPatient();
patient.setPetName(new StringType("Fido"));
patient.getImportantDates().add(new DateTimeType("2010-01-02"));
patient.getImportantDates().add(new DateTimeType("2014-01-26T11:11:11"));

patient.addName().setFamily("Smith").addGiven("John").addGiven("Quincy").addSuffix("Jr");

IParser p = FhirContext.forDstu2().newXmlParser().setPrettyPrint(true);
String messageString = p.encodeResourceToString(patient);

System.out.println(messageString);

```

Copy
This example produces the following output:
```
<Patient xmlns="http://hl7.org/fhir">
   <modifierExtension url="http://example.com/dontuse#importantDates">
      <valueDateTime value="2010-01-02"/>
   </modifierExtension>
   <modifierExtension url="http://example.com/dontuse#importantDates">
      <valueDateTime value="2014-01-26T11:11:11"/>
   </modifierExtension>
   <extension url="http://example.com/dontuse#petname">
      <valueString value="Fido"/>
   </extension>
   <name>
      <family value="Smith"/>
      <given value="John"/>
      <given value="Quincy"/>
      <suffix value="Jr"/>
   </name>
</Patient>

```

Copy
Parsing messages using your new custom type is equally simple. These types can also be used as method return types in clients and servers.
```
IParser parser = FhirContext.forDstu2().newXmlParser();
MyPatient newPatient = parser.parseResource(MyPatient.class, messageString);

```

Copy
#  2.5.3Using Custom Types in a Client
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#using-custom-types-in-a-client)
If you are using a client and wish to use a specific custom structure, you may simply use the custom structure as you would a build in HAPI type.
```
// Create an example patient
MyPatient custPatient = new MyPatient();
custPatient.addName().setFamily("Smith").addGiven("John");
custPatient.setPetName(new StringType("Rover")); // populate the extension

// Create the resource like normal
client.create().resource(custPatient).execute();

// You can also read the resource back like normal
custPatient = client.read().resource(MyPatient.class).withId("123").execute();

```

Copy
You may also explicitly use custom types in searches and other operations which return resources.
```
// Perform the search using the custom type
Bundle bundle = client.search()
      .forResource(MyPatient.class)
      .returnBundle(Bundle.class)
      .execute();

// Entries in the return bundle will use the given type
MyPatient pat0 = (MyPatient) bundle.getEntry().get(0).getResource();

```

Copy
You can also explicitly declare a preferred response resource custom type. This is useful for some operations that do not otherwise declare their resource types in the method signature.
```
// Perform the search using the custom type
bundle = client.history()
      .onInstance(new IdType("Patient/123"))
      .andReturnBundle(Bundle.class)
      .preferResponseType(MyPatient.class)
      .execute();

// Entries in the return bundle will use the given type
MyPatient historyPatient0 = (MyPatient) bundle.getEntry().get(0).getResource();

```

Copy
##  2.5.3.1Using Multiple Custom Types in a Client[](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#using-multiple-custom-types-in-a-client)
Sometimes you may not know in advance exactly which type you will be receiving. For example, there are Patient resources which conform to several different profiles on a server and you aren't sure which profile you will get back for a specific read, you can declare the "primary" type for a given profile.
This is declared at the FhirContext level, and will apply to any clients created from this context (including clients created before the default was set).
```
FhirContext ctx = FhirContext.forDstu3();

// Instruct the context that if it receives a resource which
// claims to conform to the given profile (by URL), it should
// use the MyPatient type to parse this resource
ctx.setDefaultTypeForProfile("http://example.com/StructureDefinition/mypatient", MyPatient.class);

// You can declare as many default types as you like
ctx.setDefaultTypeForProfile("http://foo.com/anotherProfile", CustomObservation.class);

// Create a client
IGenericClient client = ctx.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3");

// You can also read the resource back like normal
Patient patient = client.read().resource(Patient.class).withId("123").execute();
if (patient instanceof MyPatient) {
   // If the server supplied a resource which declared to conform
   // to the given profile, MyPatient will have been returned so
   // process it differently..
}

```

Copy
#  2.5.4Using Custom Types in a Server
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#using-custom-types-in-a-server)
If you are using a client and wish to use a specific custom structure, you may simply use the custom structure as you would a build in HAPI type.
```
// Create an example patient
MyPatient custPatient = new MyPatient();
custPatient.addName().setFamily("Smith").addGiven("John");
custPatient.setPetName(new StringType("Rover")); // populate the extension

// Create the resource like normal
client.create().resource(custPatient).execute();

// You can also read the resource back like normal
custPatient = client.read().resource(MyPatient.class).withId("123").execute();

```

Copy
#  2.5.5Custom Composite Extension Classes
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#custom-composite-extension-classes)
The following example shows a resource containing a composite extension.
```
@ResourceDef(name = "Patient")
public class CustomCompositeExtension extends Patient {

   private static final long serialVersionUID = 1L;

   /**
    * A custom extension
    */
   @Child(name = "foo")
   @Extension(url = "http://acme.org/fooParent", definedLocally = false, isModifier = false)
   protected FooParentExtension fooParentExtension;

   public FooParentExtension getFooParentExtension() {
      return fooParentExtension;
   }

   @Override
   public boolean isEmpty() {
      return super.isEmpty() && ElementUtil.isEmpty(fooParentExtension);
   }

   public void setFooParentExtension(FooParentExtension theFooParentExtension) {
      fooParentExtension = theFooParentExtension;
   }

   @Block
   public static class FooParentExtension extends BackboneElement {

      private static final long serialVersionUID = 4522090347756045145L;

      @Child(name = "childA")
      @Extension(url = "http://acme.org/fooChildA", definedLocally = false, isModifier = false)
      private StringType myChildA;

      @Child(name = "childB")
      @Extension(url = "http://acme.org/fooChildB", definedLocally = false, isModifier = false)
      private StringType myChildB;

      @Override
      public FooParentExtension copy() {
         FooParentExtension copy = new FooParentExtension();
         copy.myChildA = myChildA;
         copy.myChildB = myChildB;
         return copy;
      }

      @Override
      public boolean isEmpty() {
         return super.isEmpty() && ElementUtil.isEmpty(myChildA, myChildB);
      }

      public StringType getChildA() {
         return myChildA;
      }

      public StringType getChildB() {
         return myChildB;
      }

      public void setChildA(StringType theChildA) {
         myChildA = theChildA;
      }

      public void setChildB(StringType theChildB) {
         myChildB = theChildB;
      }
   }
}

```

Copy
This could be used to create a resource such as the following:
```
<Patient xmlns="http://hl7.org/fhir">
   <id value="123"/>
   <extension url="http://acme.org/fooParent">
      <extension url="http://acme.org/fooChildA">
         <valueString value="ValueA"/>
      </extension>
      <extension url="http://acme.org/fooChildB">
         <valueString value="ValueB"/>
      </extension>
   </extension>
</Patient>

```

Copy
#  2.5.6Custom Resource Structure
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#custom-resource-structure)
The following example shows a custom resource structure class creating an entirely new resource type as opposed to simply extending an existing one. Note that this is allowable in FHIR, but is **highly discouraged** as they are by definition not good for interoperability.
```
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example of a custom resource that also uses a custom
 * datatype.
 *
 * Note that we are extending DomainResource for an STU3
 * resource. For DSTU2 it would be BaseResource.
 */
@ResourceDef(name = "CustomResource", profile = "http://hl7.org/fhir/profiles/custom-resource")
public class CustomResource extends DomainResource {

   private static final long serialVersionUID = 1L;

   /**
    * We give the resource a field with name "television". This field has no
    * specific type, so it's a choice[x] field for any type.
    */
   @Child(name = "television", min = 1, max = Child.MAX_UNLIMITED, order = 0)
   private List<Type> myTelevision;

   /**
    * We'll give it one more field called "dogs"
    */
   @Child(name = "dogs", min = 0, max = 1, order = 1)
   private StringType myDogs;

   @Override
   public CustomResource copy() {
      CustomResource retVal = new CustomResource();
      super.copyValues(retVal);
      retVal.myTelevision = myTelevision;
      retVal.myDogs = myDogs;
      return retVal;
   }

   public List<Type> getTelevision() {
      if (myTelevision == null) {
         myTelevision = new ArrayList<Type>();
      }
      return myTelevision;
   }

   public StringType getDogs() {
      return myDogs;
   }

   @Override
   public ResourceType getResourceType() {
      return null;
   }

   @Override
   public FhirVersionEnum getStructureFhirVersionEnum() {
      return FhirVersionEnum.DSTU3;
   }

   @Override
   public boolean isEmpty() {
      return ElementUtil.isEmpty(myTelevision, myDogs);
   }

   public void setTelevision(List<Type> theValue) {
      this.myTelevision = theValue;
   }

   public void setDogs(StringType theDogs) {
      myDogs = theDogs;
   }
}

```

Copy
##  2.5.6.1Custom Datatype Structure[](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#custom-datatype-structure)
The following example shows a custom datatype structure class:
```
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.instance.model.api.ICompositeType;

/**
 * This is an example of a custom datatype.
 *
 * This is an STU3 example so it extends Type and implements ICompositeType. For
 * DSTU2 it would extend BaseIdentifiableElement and implement ICompositeDatatype.
 */
@DatatypeDef(name = "CustomDatatype")
public class CustomDatatype extends Type implements ICompositeType {

   private static final long serialVersionUID = 1L;

   @Child(name = "date", order = 0, min = 1, max = 1)
   private DateTimeType myDate;

   @Child(name = "kittens", order = 1, min = 1, max = 1)
   private StringType myKittens;

   public DateTimeType getDate() {
      if (myDate == null) myDate = new DateTimeType();
      return myDate;
   }

   public StringType getKittens() {
      return myKittens;
   }

   @Override
   public boolean isEmpty() {
      return ElementUtil.isEmpty(myDate, myKittens);
   }

   public CustomDatatype setDate(DateTimeType theValue) {
      myDate = theValue;
      return this;
   }

   public CustomDatatype setKittens(StringType theKittens) {
      myKittens = theKittens;
      return this;
   }

   @Override
   protected CustomDatatype typedCopy() {
      CustomDatatype retVal = new CustomDatatype();
      super.copyValues(retVal);
      retVal.myDate = myDate;
      return retVal;
   }
}

```

Copy
##  2.5.6.2Using the Custom Structure[](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html#using-the-custom-structure)
And now let's try the custom structure out:
```
// Create a context. Note that we declare the custom types we'll be using
// on the context before actually using them
FhirContext ctx = FhirContext.forDstu3();
ctx.registerCustomType(CustomResource.class);
ctx.registerCustomType(CustomDatatype.class);

// Now let's create an instance of our custom resource type
// and populate it with some data
CustomResource res = new CustomResource();

// Add some values, including our custom datatype
DateType value0 = new DateType("2015-01-01");
res.getTelevision().add(value0);

CustomDatatype value1 = new CustomDatatype();
value1.setDate(new DateTimeType(new Date()));
value1.setKittens(new StringType("FOO"));
res.getTelevision().add(value1);

res.setDogs(new StringType("Some Dogs"));

// Now let's serialize our instance
String output = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(res);
System.out.println(output);

```

Copy
This produces the following output (some spacing has been added for readability):
```
<CustomResource xmlns="http://hl7.org/fhir">
   <meta>
      <profile value="http://hl7.org/fhir/profiles/custom-resource"/>
   </meta>
   
   <televisionDate value="2015-01-01"/>
   <televisionCustomDatatype>
      <date value="2016-05-22T08:30:36-04:00"/>
      <kittens value="FOO"/>
   </televisionCustomDatatype>
   
   <dogs value="Some Dogs"/>
   
</CustomResource>

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
2.5 Custom Structures 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.6 Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)