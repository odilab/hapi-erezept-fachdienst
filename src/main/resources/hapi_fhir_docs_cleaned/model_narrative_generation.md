---
source: https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html
crawled: 2025-08-01T14:05:41.154551
---

# Model Narrative Generation

#  2.6.1Narrative Generation
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#narrative-generation)
HAPI provides several ways to add [Narrative Text](http://hl7.org/fhir/narrative.html) to your encoded messages.
The simplest way is to place the narrative text directly in the resource via the `setDivAsString()` method.
```
Patient pat = new Patient();
pat.getText().setStatus(org.hl7.fhir.r4.model.Narrative.NarrativeStatus.GENERATED);
pat.getText().setDivAsString("<div>This is the narrative text<br/>this is line 2</div>");

```

Copy
#  2.6.2Automatic Narrative Generation
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#automatic-narrative-generation)
HAPI FHIR also comes with a built-in mechanism for automatically generating narratives based on your resources.
**Warning:** This built-in capability is a work in progress, and does not cover every type of resource or even every attribute in any resource. You should test it and configure it for your particular use cases.
HAPI's built-in narrative generation uses the [Thymeleaf](http://www.thymeleaf.org/) library for templating narrative texts. Thymeleaf provides a simple XHTML-based syntax which is easy to use and meshes well with the HAPI-FHIR model objects.
##  2.6.2.1A Simple Example[](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#a-simple-example)
Activating HAPI's built-in narrative generator is as simple as calling [setNarrativeGenerator](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html#setNarrativeGenerator\(ca.uhn.fhir.narrative.INarrativeGenerator\)).
```
Patient patient = new Patient();
patient.addIdentifier().setSystem("urn:foo").setValue("7000135");
patient.addName().setFamily("Smith").addGiven("John").addGiven("Edward");
patient.addAddress()
      .addLine("742 Evergreen Terrace")
      .setCity("Springfield")
      .setState("ZZ");

FhirContext ctx = FhirContext.forDstu2();

// Use the narrative generator
ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

// Encode the output, including the narrative
String output = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(patient);
System.out.println(output);

```

Copy
...which produces the following output:
```
<Patient xmlns="http://hl7.org/fhir">
    <text>
        <status value="generated"/>
        <div xmlns="http://www.w3.org/1999/xhtml">
            <div class="hapiHeaderText"> John Edward <b>SMITH </b></div>
            <table class="hapiPropertyTable">
                <tbody>
                    <tr><td>Identifier</td><td>7000135</td></tr>
                    <tr><td>Address</td><td><span>742 Evergreen Terrace</span><br/><span>Springfield</span> <span>ZZ</span></td></tr>
                </tbody>
            </table>
        </div>
    </text>
    <!-- .... snip ..... -->
</Patient>

```

Copy
#  2.6.3Built-in Narrative Templates
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#built-in-narrative-templates)
HAPI currently only comes with built-in support for a few resource types. Our intention is that people enhance these templates and create new ones, and share these back with us so that we can continue to build out the library. To see the current template library, see the source repository [here](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-base/src/main/resources/ca/uhn/fhir/narrative).
Note that these templates expect a few specific CSS definitions to be present in your site's CSS file. See the [narrative CSS](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-base/src/main/resources/ca/uhn/fhir/narrative/hapi-narrative.css) to see these.
#  2.6.4Creating your own Templates
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#creating-your-own-templates)
To use your own templates for narrative generation, simply create one or more templates, using the Thymeleaf HTML based syntax.
```
<html>
<head>
   <link rel="stylesheet" type="text/css" href="narrative.css"/>
</head>
<body>
<!--*/-->
<div>
   <h1>Operation Outcome</h1>
   <table border="0">
      <tr th:each="issue : ${resource.issue}">
         <td th:text="${issue.severityElement.value}" style="font-weight: bold;"></td>
         <td th:text="${issue.location}"></td>
         <td th:narrative="${issue.diagnostics}"></td>
      </tr>
   </table>
</div>

<!--*/-->
</body>
</html>

```

Copy
Then create a properties file which describes your templates. In this properties file, each resource to be defined has a pair or properties.
The first (name.class) defines the class name of the resource to define a template for. The second (name.narrative) defines the path/classpath to the template file. The format of this path is `file:/path/foo.html` or `classpath:/com/classpath/foo.html`.
```
# Two property lines in the file per template. There are several forms you
# can use. This first form assigns a template type to a resource by 
# resource name
practitioner.resourceType=Practitioner
practitioner.narrative=classpath:com/example/narrative/Practitioner.html

# This second form assigns a template by class name. This can be used for
# HAPI FHIR built-in structures, or for custom structures as well.
observation.class=org.hl7.fhir.r4.model.Observation
observation.narrative=classpath:com/example/narrative/Observation.html

# You can also assign a template based on profile ID (Resource.meta.profile)
vitalsigns.profile=http://hl7.org/fhir/StructureDefinition/vitalsigns
vitalsigns.narrative=classpath:com/example/narrative/Observation_Vitals.html

# You can also assign a template based on tag ID (Resource.meta.tag). Coding
# must be represented as a code system and code delimited by a pipe character.
allergyIntolerance.tag=http://loinc.org|48765-2
allergyIntolerance.narrative=classpath:com/example/narrative/AllergyIntolerance.html

```

Copy
You may also override/define behaviour for datatypes and other structures. These datatype narrative definitions will be used as content within `th:narrative` blocks in resource templates. See the [example above](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#creating-your-own-templates).
```
# You can create a template based on a type name
quantity.dataType=Quantity
quantity.narrative=classpath:com/example/narrative/Quantity.html

string.dataType=String
string.narrative=classpath:com/example/narrative/String.html

# Or by class name, which can be useful for custom datatypes and structures
custom_extension.class=com.example.model.MyCustomExtension
custom_extension.narrative=classpath:com/example/narrative/CustomExtension.html

```

Copy
Finally, use the [CustomThymeleafNarrativeGenerator](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/narrative/CustomThymeleafNarrativeGenerator.html) and provide it to the FhirContext.
```
FhirContext ctx = FhirContext.forDstu2();
String propFile = "classpath:/com/foo/customnarrative.properties";
CustomThymeleafNarrativeGenerator gen = new CustomThymeleafNarrativeGenerator(propFile);

Patient patient = new Patient();

ctx.setNarrativeGenerator(gen);
String output = ctx.newJsonParser().encodeResourceToString(patient);
System.out.println(output);

```

Copy
#  2.6.5Fragments Expressions in Thymeleaf Templates
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#fragments-expressions-in-thymeleaf-templates)
Thymeleaf has a concept called Fragments, which allow reusable template portions that can be imported anywhere you need them. It can be helpful to put these fragment definitions in their own file. For example, the following property file declares a template and a fragment:
```
# Resource template
bundle.resourceType        = Bundle
bundle.style               = thymeleaf
bundle.narrative           = classpath:ca/uhn/fhir/narrative/narrative-with-fragment-parent.html

# Fragment template
fragment1.fragmentName     = MyFragment
fragment1.style            = thymeleaf
fragment1.narrative        = classpath:ca/uhn/fhir/narrative/narrative-with-fragment-child.html

```

Copy
The following template declares `Fragment1` and `Fragment2` as part of file `narrative-with-fragment-child.html`:
```
<html xmlns:th="http://www.thymeleaf.org">

   <div th:fragment="Fragment1(param)">
      Fragment-1-content
      [[${param}]]
   </div>

   <div th:fragment="Fragment2">
      Fragment-2-content
   </div>

</html>

```

Copy
And the following parent template (`narrative-with-fragment-parent.html`) imports `Fragment1` with parameter 'blah':
```
<html xmlns:th="http://www.thymeleaf.org">

   This is some content

   <div th:replace="MyFragment :: Fragment1('blah')"></div>

</html>

```

Copy
#  2.6.6FHIRPath Expressions in Thymeleaf Templates
[ ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html#fhirpath-expressions-in-thymeleaf-templates)
Thymeleaf templates can incorporate FHIRPath expressions using the `#fhirpath` expression object.
This object has the following methods:
  * evaluateFirst(input, pathExpression) – This method returns the first element matched on `input` by the path expression, or _null_ if nothing matches.
  * evaluate(input, pathExpression) – This method returns a Java List of elements matched on `input` by the path expression, or an empty list if nothing matches.


For example:
```
<div>
   [[${#fhirpath.evaluateFirst(resource, 'MedicationStatement.medication.text')}]]
</div>

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
2.6 Narrative Generation 
* Working With The FHIR Model 
* [ 2.0  Working With Resources ](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
* [ 2.1  Parsing and Serializing ](https://hapifhir.io/hapi-fhir/docs/model/parsers.html)
* [ 2.2  Resource References ](https://hapifhir.io/hapi-fhir/docs/model/references.html)
* [ 2.3  Profiles and Extensions ](https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html)
* [ 2.4  Version Converters ](https://hapifhir.io/hapi-fhir/docs/model/converter.html)
* [ 2.5  Custom Structures ](https://hapifhir.io/hapi-fhir/docs/model/custom_structures.html)
* [ 2.6  Narrative Generation ](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)
* [ 2.7  Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
[ 2.7 Bundle Builder ](https://hapifhir.io/hapi-fhir/docs/model/bundle_builder.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)