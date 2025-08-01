---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html
crawled: 2025-08-01T14:08:09.297509
---

# Server Plain Rest Operations Search

#  4.5.1Rest Operations: Search
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#rest-operations-search)
This page describes how to add various FHIR search features to your resource/plain providers.
#  4.5.2Search with No Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-with-no-parameters)
The following example shows a search with no parameters. This operation should return all resources of a given type (this obviously doesn't make sense in all contexts, but does for some resource types).
```
@Search
public List<Organization> getAllOrganizations() {
   List<Organization> retVal = new ArrayList<Organization>(); // populate this
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient](http://fhir.example.com/Patient)
#  4.5.3Search Parameters: String Introduction
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-string-introduction)
To allow a search using given search parameters, add one or more parameters to your search method and tag these parameters as either [@RequiredParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/RequiredParam.html) or [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html).
This annotation takes a "name" parameter which specifies the parameter's name (as it will appear in the search URL). FHIR defines standardized parameter names for each resource, and these are available as constants on the individual HAPI resource classes.
Parameters which take a string as their format should use the [StringParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/StringParam.html) type. They may also use normal java `String`, although it is not possible to use modifiers such as the `:exact` modifier in that case.
```
@Search()
public List<Patient> searchByLastName(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamily) {
   String valueToMatch = theFamily.getValue();

   if (theFamily.isExact()) {
      // Do an exact match search
   } else {
      // Do a fuzzy search if possible
   }

   // ...populate...
   Patient patient = new Patient();
   patient.addIdentifier().setSystem("urn:mrns").setValue("12345");
   patient.addName().setFamily("Smith").addGiven("Tester").addGiven("Q");
   // ...etc...

   //  Every returned resource must have its logical ID set. If the server
   //  supports versioning, that should be set too
   String logicalId = "4325";
   String versionId = "2"; // optional
   patient.setId(new IdType("Patient", logicalId, versionId));

   /*
    * This is obviously a fairly contrived example since we are always
    * just returning the same hardcoded patient, but in a real scenario
    * you could return as many resources as you wanted, and they
    * should actually match the given search criteria.
    */
   List<Patient> retVal = new ArrayList<Patient>();
   retVal.add(patient);

   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient?family=SMITH](http://fhir.example.com/Patient?family=SMITH)
#  4.5.4Search Parameters: Token/Identifier
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-tokenidentifier)
The "token" type is used for parameters which have two parts, such as an identifier (which has a system URI, as well as the actual identifier) or a code (which has a code system, as well as the actual code). For example, the search below can be used to search by identifier (e.g. search for an MRN).
```
@Search()
public List<Patient> searchByIdentifier(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam theId) {
   String identifierSystem = theId.getSystem();
   String identifier = theId.getValue();

   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient?identifier=urn:foo|7000135](http://fhir.example.com/Patient?identifier=urn:foo|7000135)
#  4.5.5Search Parameters: Date (Simple)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-date-simple)
The FHIR specification provides a syntax for specifying dates+times (but for simplicity we will just say dates here) as search criteria.
Dates may be optionally prefixed with a qualifier. For example, the string `=ge2011-01-02` means any date on or after 2011-01-02.
To accept a qualified date parameter, use the [DateParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/DateParam.html) parameter type.
```
@Search()
public List<Patient> searchByObservationNames(@RequiredParam(name = Patient.SP_BIRTHDATE) DateParam theDate) {
   ParamPrefixEnum prefix = theDate.getPrefix(); // e.g. gt, le, etc..
   Date date = theDate.getValue(); // e.g. 2011-01-02
   TemporalPrecisionEnum precision = theDate.getPrecision(); // e.g. DAY

   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Observation?birthdate=gt2011-01-02](http://fhir.example.com/Observation?birthdate=gt2011-01-02)
Invoking a client of this type involves the following syntax:
```
DateParam param = new DateParam(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, "2011-01-02");
List<Patient> response = client.getPatientByDob(param);

```

Copy
#  4.5.6Search Parameters: Date (Ranges)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#DATE_RANGES)
A common scenario in searches is to allow searching for resources with values (i.e timestamps) within a range of dates.
FHIR allows for multiple parameters with the same key, and interprets these as being an _**AND**_ set. So, for example, a range of `&date=gt2011-01-01&date=lt2011-02-01` can be interpreted as any date within January 2011.
The following snippet shows how to accept such a range, and combines it with a specific identifier, which is a common scenario. (i.e. Give me a list of observations for a specific patient within a given date range). This is accomplished using a single parameter of type [DateRangeParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/DateRangeParam.html).
```
@Search()
public List<Observation> searchByDateRange(@RequiredParam(name = Observation.SP_DATE) DateRangeParam theRange) {

   Date from = theRange.getLowerBoundAsInstant();
   Date to = theRange.getUpperBoundAsInstant();

   List<Observation> retVal = new ArrayList<Observation>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Observation?subject.identifier=7000135&date=gt2011-01-01&date=lt2011-02-01](http://fhir.example.com/Observation?subject.identifier=7000135&date=gt2011-01-01&date=lt2011-02-01)
Invoking a client of this type involves the following syntax:
```
DateParam param = new DateParam(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, "2011-01-02");
List<Patient> response = client.getPatientByDob(param);

```

Copy
##  4.5.6.1Unbounded Date Ranges[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#unbounded-date-ranges)
Note that when using a date range parameter, it is also possible for the client to request an _unbounded_ range. In other words, a range that only a start date and no end date, or vice versa.
An example of this might be the following URL, which refers to any Observation resources for the given MRN and having a date after 2011-01-01. [http://fhir.example.com/Observation?subject.identifier=7000135&date=gt2011-01-01](http://fhir.example.com/Observation?subject.identifier=7000135&date=gt2011-01-01)
When such a request is made of a server (or to make such a request from a client), the `getLowerBound()` or `getUpperBound()` property of the [DateRangeParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/DateRangeParam.html) object will be set to `null`.
#  4.5.7Search Parameters: Quantity
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-quantity)
Quantity parameters allow a number with units and a comparator.
The following snippet shows how to accept such a range, and combines it with a specific identifier, which is a common scenario. (i.e. Give me a list of observations for a specific patient within a given date range)
```
@Search()
public List<Observation> getObservationsByQuantity(
      @RequiredParam(name = Observation.SP_VALUE_QUANTITY) QuantityParam theQuantity) {

   List<Observation> retVal = new ArrayList<Observation>();

   ParamPrefixEnum prefix = theQuantity.getPrefix();
   BigDecimal value = theQuantity.getValue();
   String units = theQuantity.getUnits();
   // .. Apply these parameters ..

   // ... populate ...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Observation?value-quantity=lt123.2||mg|http://unitsofmeasure.org](http://fhir.example.com/Observation?value-quantity=lt123.2||mg|http://unitsofmeasure.org)
#  4.5.8Search Parameters: Resource Reference
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-resource-reference)
Many search parameters refer to resource references. For instance, the Patient parameter "provider" refers to the resource marked as the managing organization for patients.
Reference parameters use the [ReferenceParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/ReferenceParam.html) type. Reference parameters are, in their most basic form, just a pointer to another resource. For example, you might want to query for DiagnosticReport resources where the subject (the Patient resource that the report is about) is Patient/123. The following example shows a simple resource reference parameter in use.
```
@Search
public List<DiagnosticReport> findDiagnosticReportsWithSubjet(
      @OptionalParam(name = DiagnosticReport.SP_SUBJECT) ReferenceParam theSubject) {
   List<DiagnosticReport> retVal = new ArrayList<DiagnosticReport>();

   // If the parameter passed in includes a resource type (e.g. ?subject:Patient=123)
   // that resource type is available. Here we just check that it is either not provided
   // or set to "Patient"
   if (theSubject.hasResourceType()) {
      String resourceType = theSubject.getResourceType();
      if ("Patient".equals(resourceType) == false) {
         throw new InvalidRequestException(
               Msg.code(633) + "Invalid resource type for parameter 'subject': " + resourceType);
      }
   }

   if (theSubject != null) {
      // ReferenceParam extends IdType so all of the resource ID methods are available
      String subjectId = theSubject.getIdPart();

      // .. populate retVal with DiagnosticReport resources having
      // subject with id "subjectId" ..

   }

   return retVal;
}

```

Copy
#  4.5.9Search Parameters: Filter
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#filter)
To implement the FHIR [_filter](http://hl7.org/fhir/search_filter.html) search style, you may create a parameter of type `StringParam` (or one of the and/or derivatives), as shown below.
```
@OptionalParam(name=ca.uhn.fhir.rest.api.Constants.PARAM_FILTER)
StringAndListParam theFtFilter

```

Copy
#  4.5.10Chained Resource References
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#chained-resource-references)
References may also support a "chained" value. This is a search parameter name on the target resource. For example, you might want to search for DiagnosticReport resources by subject, but use the subject's last name instead of their resource ID. In this example, you are chaining "family" (the last name) to "subject" (the patient).
The net result in the query string would look like: [http://fhir.example.com/DiagnosticReport?subject.family=SMITH](http://fhir.example.com/DiagnosticReport?subject.family=SMITH)
What this query says is "fetch me all of the DiagnosticReport resources where the **subject** (Patient) of the report has the **family** (name) of 'SMITH'".
There are two ways of dealing with chained parameters in your methods: static chains and dynamic chains. Both are equally valid, although dynamic chains might lead to somewhat more compact and readable code.
##  4.5.10.1Dynamic Chains[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#dynamic_chains)
Chained values must be explicitly declared through the use of a whitelist (or blacklist). The following example shows how to declare a report with an allowable chained parameter:
```
@Search
public List<DiagnosticReport> findReportsWithChain(
      @RequiredParam(
                  name = DiagnosticReport.SP_SUBJECT,
                  chainWhitelist = {Patient.SP_FAMILY, Patient.SP_GENDER})
            ReferenceParam theSubject) {
   List<DiagnosticReport> retVal = new ArrayList<DiagnosticReport>();

   String chain = theSubject.getChain();
   if (Patient.SP_FAMILY.equals(chain)) {
      String familyName = theSubject.getValue();
      // .. populate with reports matching subject family name ..
   }
   if (Patient.SP_GENDER.equals(chain)) {
      String gender = theSubject.getValue();
      // .. populate with reports matching subject gender ..
   }

   return retVal;
}

```

Copy
You may also specify the whitelist value of `""` to allow an empty chain (e.g. the resource ID) and this can be combined with other values, as shown below:
```
@Search
public List<DiagnosticReport> findReportsWithChainCombo(
      @RequiredParam(
                  name = DiagnosticReport.SP_SUBJECT,
                  chainWhitelist = {"", Patient.SP_FAMILY})
            ReferenceParam theSubject) {
   List<DiagnosticReport> retVal = new ArrayList<DiagnosticReport>();

   String chain = theSubject.getChain();
   if (Patient.SP_FAMILY.equals(chain)) {
      String familyName = theSubject.getValue();
      // .. populate with reports matching subject family name ..
   }
   if ("".equals(chain)) {
      String resourceId = theSubject.getValue();
      // .. populate with reports matching subject with resource ID ..
   }

   return retVal;
}

```

Copy
If you are handling multiple types of chained parameters in a single method, you may want to convert the reference parameter type into something more convenient before using its value. The following example shows how to do that.
```
@Search()
public List<Observation> findBySubject(
      @RequiredParam(
                  name = Observation.SP_SUBJECT,
                  chainWhitelist = {"", Patient.SP_IDENTIFIER, Patient.SP_BIRTHDATE})
            ReferenceParam subject) {
   List<Observation> observations = new ArrayList<Observation>();

   String chain = subject.getChain();
   if (Patient.SP_IDENTIFIER.equals(chain)) {

      // Because the chained parameter "subject.identifier" is actually of type
      // "token", we convert the value to a token before processing it.
      TokenParam tokenSubject = subject.toTokenParam(myContext);
      String system = tokenSubject.getSystem();
      String identifier = tokenSubject.getValue();

      // TODO: populate all the observations for the identifier

   } else if (Patient.SP_BIRTHDATE.equals(chain)) {

      // Because the chained parameter "subject.birthdate" is actually of type
      // "date", we convert the value to a date before processing it.
      DateParam dateSubject = subject.toDateParam(myContext);
      DateTimeType birthDate = new DateTimeType(dateSubject.getValueAsString());

      // TODO: populate all the observations for the birthdate

   } else if ("".equals(chain)) {

      String resourceId = subject.getValue();
      // TODO: populate all the observations for the resource id

   }

   return observations;
}

```

Copy
##  4.5.10.2Static Chains[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#static-chains)
It is also possible to explicitly state a chained value right in the parameter name. This is useful if you want to only support a search by a specific given chained parameter. It has the added bonus that you can use the correct parameter type of the chained parameter (in this case a TokenParameter because the Patient.identifier parameter is a token).
```
@Search
public List<Patient> findObservations(
      @RequiredParam(name = Observation.SP_SUBJECT + '.' + Patient.SP_IDENTIFIER) TokenParam theProvider) {

   String system = theProvider.getSystem();
   String identifier = theProvider.getValue();

   // ...Do a search for all observations for the given subject...

   List<Patient> retVal = new ArrayList<Patient>(); // populate this
   return retVal;
}

```

Copy
#  4.5.11Search Parameters: Composite
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#search-parameters-composite)
Composite search parameters incorporate two parameters in a single value. Each of those parameters will themselves have a parameter type.
In the following example, Observation.name-value-date is shown. This parameter is a composite of a string and a date. Note that the composite parameter types (StringParam and DateParam) must be specified in both the annotation's `compositeTypes` field, as well as the generic types for the `CompositeParam` method parameter itself.
```
@Search()
public List<Observation> searchByComposite(
      @RequiredParam(
                  name = Observation.SP_CODE_VALUE_DATE,
                  compositeTypes = {TokenParam.class, DateParam.class})
            CompositeParam<TokenParam, DateParam> theParam) {
   // Each of the two values in the composite param are accessible separately.
   // In the case of Observation's name-value-date, the left is a string and
   // the right is a date.
   TokenParam observationName = theParam.getLeftValue();
   DateParam observationValue = theParam.getRightValue();

   List<Observation> retVal = new ArrayList<Observation>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Observation?name-value-date=PROCTIME$2001-02-02](http://fhir.example.com/Observation?name-value-date=PROCTIME$2001-02-02)
#  4.5.12Combining Multiple Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#combining-multiple-parameters)
Search methods may take multiple parameters, and these parameters may (or may not) be optional. To add a second required parameter, annotate the parameter with [@RequiredParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/RequiredParam.html). To add an optional parameter (which will be passed in as `null` if no value is supplied), annotate the method with [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html).
You may annotate a method with any combination of as many [@RequiredParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/RequiredParam.html) and as many [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html) parameters as you want. It is valid to have only [@RequiredParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/RequiredParam.html) parameters, or only [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html) parameters, or any combination of the two.
If you wish to create a server that can accept any combination of a large number of parameters, (this is how the various reference servers behave, as well as the [Public HAPI Test Server](http://hapi.fhir.org)). The easiest way to accomplish this is to simply create one method with all allowable parameters, each annotated as [@OptionalParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/OptionalParam.html).
On the other hand, if you have specific combinations of parameters you wish to support (a common scenario if you are building FHIR on top of existing data sources and only have certain indexes you can use) you could create multiple search methods, each with specific required and optional parameters matching the database indexes.
The following example shows a method with two parameters.
```
@Search()
public List<Patient> searchByNames(
      @RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName,
      @OptionalParam(name = Patient.SP_GIVEN) StringParam theGivenName) {
   String familyName = theFamilyName.getValue();
   String givenName = theGivenName != null ? theGivenName.getValue() : null;

   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
Example URLs to invoke this method: [http://fhir.example.com/Patient?family=SMITH](http://fhir.example.com/Patient?family=SMITH)  
[http://fhir.example.com/Patient?family=SMITH&given=JOHN](http://fhir.example.com/Patient?family=SMITH&given=JOHN)
#  4.5.13Multi-Valued (AND/OR) Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#multi-valued-andor-parameters)
It is possible to accept multiple values of a single parameter as well. This is useful in cases when you want to return a list of resources with criteria matching a list of possible values. See the [FHIR Specification](http://www.hl7.org/implement/standards/fhir/search.html#combining) for more information.
The FHIR specification allows two types of composite parameters:
  * Where a parameter may accept multiple comma separated values within a single value string (e.g. `?language=FR,NL`) this is treated as an _**OR**_ relationship, and the search should return elements matching either one or the other.
  * Where a parameter may accept multiple value strings for the same parameter name (e.g. `?language=FR&language=NL`) this is treated as an _**AND**_ relationship, and the search should return only elements matching both.


It is worth noting that according to the FHIR specification, you can have an AND relationship combining multiple OR relationships, but not vice-versa. In other words, it's possible to support a search like `("name" = ("joe" or "john")) AND ("age" = (11 or 12))` but not a search like `("language" = ("en" AND "fr") OR ("address" = ("Canada" AND "Quebec"))`. If you wish to support the latter, you may consider implementing the [_filter parameter](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#filter).
##  4.5.13.1OR Relationship Query Parameters[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#or-relationship-query-parameters)
To accept a composite parameter, use a parameter type which implements the [IQueryParameterOr](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/model/api/IQueryParameterOr.html) interface.
Each parameter type ([StringParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/StringParam.html), [TokenParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/TokenParam.html), etc.) has a corresponding parameter which accepts an _**OR**_ list of parameters. These types are called "[type]OrListParam", for example: [StringOrListParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/StringOrListParam.html) and [TokenOrListParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/TokenOrListParam.html).
The following example shows a search for Observation by name, where a list of names may be passed in (and the expectation is that the server will return Observations that match any of these names):
```
@Search()
public List<Observation> searchByObservationNames(
      @RequiredParam(name = Observation.SP_CODE) TokenOrListParam theCodings) {

   // The list here will contain 0..* codings, and any observations which match any of the
   // given codings should be returned
   List<TokenParam> wantedCodings = theCodings.getValuesAsQueryTokens();

   List<Observation> retVal = new ArrayList<Observation>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Observation?name=urn:fakenames|123,urn:fakenames|456](http://fhir.example.com/Observation?name=urn:fakenames|123,urn:fakenames|456)
##  4.5.13.2AND Relationship Query Parameters[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#and-relationship-query-parameters)
To accept a composite parameter, use a parameter type which implements the [IQueryParameterAnd](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/model/api/IQueryParameterAnd.html) interface (which in turn encapsulates the corresponding IQueryParameterOr types).
An example follows which shows a search for Patients by address, where multiple string lists may be supplied by the client. For example, the client might request that the address match `("Montreal" OR "Sherbrooke") AND ("Quebec" OR "QC")` using the following query: [http://fhir.example.com/Patient?address=Montreal,Sherbrooke&address=Quebec,QC](http://fhir.example.com/Patient?address=Montreal,Sherbrooke&address=Quebec,QC)
The following code shows how to receive this parameter using a [StringAndListParameter](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/StringAndListParam.html), which can handle an AND list of multiple OR lists of strings.
```
@Search()
public List<Patient> searchByPatientAddress(
      @RequiredParam(name = Patient.SP_ADDRESS) StringAndListParam theAddressParts) {

   // StringAndListParam is a container for 0..* StringOrListParam, which is in turn a
   // container for 0..* strings. It is a little bit weird to understand at first, but think of the
   // StringAndListParam to be an AND list with multiple OR lists inside it. So you will need
   // to return results which match at least one string within every OR list.
   List<StringOrListParam> wantedCodings = theAddressParts.getValuesAsQueryTokens();
   for (StringOrListParam nextOrList : wantedCodings) {
      List<StringParam> queryTokens = nextOrList.getValuesAsQueryTokens();
      // Only return results that match at least one of the tokens in the list below
      for (StringParam nextString : queryTokens) {
         // ....check for match...
      }
   }

   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
Note that AND parameters join multiple OR parameters together, but the inverse is not true. In other words, it is possible in FHIR to use AND search parameters to specify a search criteria of `(A=1 OR A=2) AND (B=1 OR B=2)` but it is not possible to specify `(A=1 AND B=1) OR (A=2 AND B=2)` (aside from in very specific cases where a composite parameter has been specifically defined).
#  4.5.14AND Relationship Query Parameters for Dates
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#and-relationship-query-parameters-for-dates)
Dates are a special case, since it is a fairly common scenario to want to match a date range (which is really just an AND query on two qualified date parameters). See the section on [date ranges](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#DATE_RANGES) for an example of a [DateRangeParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/DateRangeParam.html).
#  4.5.15Resource Includes (_include)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#resource-includes-include)
Using the `_include` FHIR parameter, clients can request that specific linked resources be embedded directly within search results. These included resources will have a search.mode of "include".
HAPI allows you to add a parameter for accepting includes if you wish to support them for specific search methods.
```
@Search()
public List<DiagnosticReport> getDiagnosticReport(
      @RequiredParam(name = DiagnosticReport.SP_IDENTIFIER) TokenParam theIdentifier,
      @IncludeParam(allow = {"DiagnosticReport:subject"}) Set<Include> theIncludes) {

   List<DiagnosticReport> retVal = new ArrayList<DiagnosticReport>();

   // Assume this method exists and loads the report from the DB
   DiagnosticReport report = loadSomeDiagnosticReportFromDatabase(theIdentifier);

   // If the client has asked for the subject to be included:
   if (theIncludes.contains(new Include("DiagnosticReport:subject"))) {

      // The resource reference should contain the ID of the patient
      IIdType subjectId = report.getSubject().getReferenceElement();

      // So load the patient ID and return it
      Patient subject = loadSomePatientFromDatabase(subjectId);
      report.getSubject().setResource(subject);
   }

   retVal.add(report);
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/DiagnosticReport?identifier=7000135&_include=DiagnosticReport.subject](http://fhir.example.com/DiagnosticReport?identifier=7000135&_include=DiagnosticReport.subject)
It is also possible to use a String type for the include parameter, which is more convenient if only a single include (or null for none) is all that is required.
```
@Search()
public List<DiagnosticReport> getDiagnosticReport(
      @RequiredParam(name = DiagnosticReport.SP_IDENTIFIER) TokenParam theIdentifier,
      @IncludeParam(allow = {"DiagnosticReport:subject"}) String theInclude) {

   List<DiagnosticReport> retVal = new ArrayList<DiagnosticReport>();

   // Assume this method exists and loads the report from the DB
   DiagnosticReport report = loadSomeDiagnosticReportFromDatabase(theIdentifier);

   // If the client has asked for the subject to be included:
   if ("DiagnosticReport:subject".equals(theInclude)) {

      // The resource reference should contain the ID of the patient
      IIdType subjectId = report.getSubject().getReferenceElement();

      // So load the patient ID and return it
      Patient subject = loadSomePatientFromDatabase(subjectId);
      report.getSubject().setResource(subject);
   }

   retVal.add(report);
   return retVal;
}

```

Copy
#  4.5.16Reverse Resource Includes (_revinclude)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#reverse-resource-includes-revinclude)
To add support for reverse includes (via the `_revinclude` parameter), use the same format as with the `_include` parameter (shown above) but add `reverse=true` to the [@IncludeParam](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/IncludeParam.html) annotation, as shown below.
```
@Search()
public List<DiagnosticReport> getDiagnosticReport(
      @RequiredParam(name = DiagnosticReport.SP_IDENTIFIER) TokenParam theIdentifier,
      @IncludeParam() Set<Include> theIncludes,
      @IncludeParam(reverse = true) Set<Include> theReverseIncludes) {

   return new ArrayList<DiagnosticReport>(); // populate this
}

```

Copy
#  4.5.17Named Queries (_query)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#named-queries-query)
FHIR supports [named queries](http://www.hl7.org/implement/standards/fhir/search.html#advanced), which may have specific behaviour defined. The following example shows how to create a Search operation with a name.
This operation can only be invoked by explicitly specifying the given query name in the request URL. Note that the query does not need to take any parameters.
```
@Search(queryName = "namedQuery1")
public List<Patient> searchByNamedQuery(@RequiredParam(name = "someparam") StringParam theSomeParam) {
   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient?_query=namedQuery1&someparam=value](http://fhir.example.com/Patient?_query=namedQuery1&someparam=value)
#  4.5.18Sorting (_sort)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#sorting-sort)
FHIR supports [sorting](http://www.hl7.org/implement/standards/fhir/search.html#sort) according to a specific set of rules.
According to the specification, sorting is requested by the client using a search param as the sort key. For example, when searching Patient resources, a sort key of "given" requests the "given" search param as the sort key. That param maps to the values in the field "Patient.name.given".
Sort specifications can be passed into handler methods by adding a parameter of type [SortSpec](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/api/SortSpec.html), which has been annotated with the [@Sort](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Sort.html) annotation, as shown in the following example:
```
@Search
public List<Patient> findPatients(
      @RequiredParam(name = Patient.SP_IDENTIFIER) StringParam theParameter, @Sort SortSpec theSort) {
   List<Patient> retVal = new ArrayList<Patient>(); // populate this

   // theSort is null unless a _sort parameter is actually provided
   if (theSort != null) {

      // The name of the param to sort by
      String param = theSort.getParamName();

      // The sort order, or null
      SortOrderEnum order = theSort.getOrder();

      // This will be populated if a second _sort was specified
      SortSpec subSort = theSort.getChain();

      // ...apply the sort...
   }

   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient?identifier=urn:foo|123&_sort=given](http://fhir.example.com/Patient?identifier=urn:foo|123&_sort=given)
#  4.5.19Limiting results (`_count`)
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#limiting-results)
FHIR supports [Page Count](http://www.hl7.org/implement/standards/fhir/search.html#count). Count specification may be passed into handler methods with [@Count](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Count.html) annotation. Count may be used to limit the number of resources fetched from the database.
```
@Search
public List<Patient> findPatients(
      @RequiredParam(name = Patient.SP_IDENTIFIER) StringParam theParameter, @Count Integer theCount) {
   List<Patient> retVal = new ArrayList<Patient>(); // populate this

   // count is null unless a _count parameter is actually provided
   if (theCount != null) {
      // ... do search with count ...
   } else {
      // ... do search without count ...
   }

   return retVal;
}

```

Copy
Example URL to invoke this method: [http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10](http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10)
#  4.5.20Paging
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#paging)
##  4.5.20.1Offset paging with `_offset`[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#offset-paging-with-offset)
**Warning:** Using `_offset` without sorting can result in duplicate entries to show up across the different pages when following the next page link provided on each page.
HAPI FHIR supports also paging. Offset specification can be passed into handler methods with [@Offset](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/annotation/Offset.html) annotation. This annotation is _not_ part of the FHIR standard.
There are two possible ways to use paging. It is possible to define `_offset` parameter in the request which means that when combined with `_count` the paging is done on the database level. This type of paging benefits from not having to return so many items from the database when paging items. It's also possible to define default page size (i.e. default `_count` if not given) and maximum page size (i.e. maximum value for the `_count` parameter). See [RestfulServer](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/ca/uhn/fhir/rest/server/RestfulServer.html) for more information.
```
@Search
public List<Patient> findPatients(
      @RequiredParam(name = Patient.SP_IDENTIFIER) StringParam theParameter,
      @Offset Integer theOffset,
      @Count Integer theCount) {
   List<Patient> retVal = new ArrayList<Patient>(); // populate this

   // offset is null unless a _offset parameter is actually provided
   if (theOffset != null) {
      // ... do search with offset ...
   } else {
      // ... do search without offset ...
   }

   return retVal;
}

```

Copy
Example URL to invoke this method for the first page: [http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10&_offset=0](http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10&_offset=0) or just[http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10](http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10)
Example URL to invoke this method for the second page: [http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10&_offset=10](http://fhir.example.com/Patient?identifier=urn:foo|123&_count=10&_offset=10)
Note that if the paging provider is configured to be database backed, `_offset=0` behaves differently than no `_offset`. This allows the user the choose if he wants offset paging or database backed paging.
##  4.5.20.2Using paging provider[](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#using-paging-provider)
It is also possible to implement own paging provider (or use implementation bundled in HAPI FHIR). See [Paging](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html) for information on how to use paging provider.
#  4.5.21Adding Descriptions
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#adding-descriptions)
It is also possible to annotate search methods and/or parameters with the [@Description](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/model/api/annotation/Description.html) annotation. This annotation allows you to add a description of the method and the individual parameters. These descriptions will be placed in the server's conformance statement, which can be helpful to anyone who is developing software against your server.
```
@Description(shortDefinition = "This search finds all patient resources matching a given name combination")
@Search()
public List<Patient> searchWithDocs(
      @Description(shortDefinition = "This is the patient's last name - Supports partial matches")
            @RequiredParam(name = Patient.SP_FAMILY)
            StringParam theFamilyName,
      @Description(shortDefinition = "This is the patient's given names") @OptionalParam(name = Patient.SP_GIVEN)
            StringParam theGivenName) {

   List<Patient> retVal = new ArrayList<Patient>();
   // ...populate...
   return retVal;
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
4.5 REST Operations: Search 
* Plain Server 
* [ 4.0  REST Server Types ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html)
* [ 4.1  Plain Server Introduction ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html)
* [ 4.2  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
* [ 4.3  Resource Providers and Plain Providers ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html)
* [ 4.4  REST Operations: Overview ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
* [ 4.5  REST Operations: Search ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
* [ 4.6  REST Operations: Extended Operations ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
* [ 4.7  Paging Search Results ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
* [ 4.8  Web Testpage Overlay ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
* [ 4.9  Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
* [ 4.10  JAX-RS Support ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
* [ 4.11  Customizing the CapabilityStatement ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html)
* [ 4.12  OpenAPI / Swagger ](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html)
[ 4.6 REST Operations: Extended Operations ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)