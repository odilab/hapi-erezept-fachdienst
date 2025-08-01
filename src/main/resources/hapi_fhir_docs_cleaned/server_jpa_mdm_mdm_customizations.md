---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html
crawled: 2025-08-01T14:05:18.607943
---

# Server Jpa Mdm Mdm Customizations

#  6.6.1MDM Customizations
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html#mdm-customizations)
This section describes customization supported by MDM.
#  6.6.2Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html#interceptors)
MDM allows customization through interceptors. Please refer to the [Interceptors](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html) section of the documentation for further details and implementation guidelines.
##  6.6.2.1MDM Preprocessing Pointcut[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html#mdm-preprocessing-pointcut)
MDM supports a pointcut invocation right before it starts matching an incoming source resource against defined rules. A possible use of the pointcut would be to alter a resource content with the intention of influencing the MDM matching and linking process. Any modifications to the source resource are NOT persisted to the database. Modifications performed within the pointcut will remain valid during MDM processing only.
##  6.6.2.2Example: Ignoring Matches on Patient Name[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html#example-ignoring-matches-on-patient-name)
In a scenario where a patient was given a placeholder name(John Doe), it would be desirable to ignore a 'name' matching rule but allow matching on other valid rules like matching SSN or a matching address.
This can be done with a custom interceptor, or by providing a set of BlockListRules and wiring in a IBlockListRuleProvider that provides it.
* * *
6.6.2.2.1Block List Rules MDM can be configured to block certain resources from MDM matching entirely using a set of json rules. Blocked resources will still have an associated Golden Resource created, and will still be available for future resources to match, but no matching will be done to existing resources in the system. In order to prevent MDM matching using the block rule list, an IBlockListRuleProvider must be wired in and a set of block rules provided. Blocking rules are provided in a list of rule-sets, with each one applicable to a specified resource type. Within each rule-set, a collection of fields specify the `fhirPath` and `value` (case insensitive) on which to test an input resource for blocking. If a resource matches on all blocked fields in a rule-set, MDM matching will be blocked for the entire resource. If multiple rule-sets apply to the same resource, they will be checked in sequence until one is found to be applicable. If none are, MDM matching will continue as before. Below is an example of MDM blocking rules used to prevent matching on Patients with name "John Doe" or "Jane Doe". ```
{
   "blocklist": [{
      "resourceType": "Patient",
      "fields": [{
        "fhirPath": "name.first().family",
        "value": "doe"
      }, {
         "fhirPath": "name.first().given.first()",
         "value": "john"
      }]
   }, {
      "resourceType": "Patient",
      "fields": [{
         "fhirPath": "name.first().family",
         "value": "doe"
      }, {
         "fhirPath": "name.first().given.first()",
         "value": "jane"
      }]
   }]
}

```
Copy Note that, for these rules, because the `fhirPath` specifies the `first()` name, Patient resource A below would be blocked. But Patient resource B would not be. 6.6.2.2.1.0.1Patient Resource A ```
{
   "resourceType": "Patient",
   "name": [{
      "family": "doe",
      "given": [
         "jane"
      ]
   }]
}

```
Copy 6.6.2.2.1.0.2Patient Resource B ```
{
   "resourceType": "Patient",
   "name": [{
      "family": "jetson",
      "given": [
         "jane"
      ]
   },{
      "family": "doe",
      "given": [
         "jane"
      ]
   }]
}

```
Copy
* * *
6.6.2.2.2Interceptor Blocking The following provides a full implementation of an interceptor that prevents matching on a patient name when it detects a placeholder value. ```
/**
 * This is a simple interceptor that will remove a humanName when it is found to be
 * black listed.
 */
public class PatientNameModifierMdmPreProcessingInterceptor {
   List<String> myNamesToIgnore = asList("John Doe", "Jane Doe");

   @Hook(Pointcut.MDM_BEFORE_PERSISTED_RESOURCE_CHECKED)
   public void invoke(IBaseResource theResource) {

      Patient patient = (Patient) theResource;
      List<HumanName> nameList = patient.getName();

      List<HumanName> validHumanNameList = nameList.stream()
            .filter(theHumanName -> !myNamesToIgnore.contains(theHumanName.getNameAsSingleString()))
            .collect(Collectors.toList());

      patient.setName(validHumanNameList);
   }
}

```
Copy
See the [Pointcut](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html) JavaDoc for further details on the pointcut MDM_BEFORE_PERSISTED_RESOURCE_CHECKED.
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
6.6 MDM Customizations 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)