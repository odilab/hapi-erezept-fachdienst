---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html
crawled: 2025-08-01T14:05:43.260037
---

# Server Jpa Mdm Mdm Details

#  6.4.1MDM Implementation Details
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#mdm-implementation-details)
This section describes details of how MDM functionality is implemented in HAPI FHIR.
#  6.4.2Automatic Linking
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#automatic-linking)
With MDM enabled, the default behavior of the MDM is to create a new Golden Record for every source record that is created such that there is a 1:1 relationship between them. Any relinking is then expected to be done manually via the [MDM Operations](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html).
In a typical configuration it is often desirable to have links created automatically using matching rules. For example, you might decide that if a Patient shares the same name, gender, and date of birth as another Patient, you have at least a little confidence that they are the same Patient.
This automatic linking is done via configurable matching rules that create links between source record and Golden Record. Based on the strength of the match configured in these rules, the link will be set to either POSSIBLE_MATCH or MATCH.
It is important to note that before a resource is processed by MDM, it is first checked to ensure that it has at least one attribute that the MDM system cares about, as defined in the `mdm-rules.json` file. If the incoming resource has no such attributes, then MDM processing does not occur on it. In this case, no Golden Resource is created for this source resource. If in the future the source resource is updated to contain attributes the MDM system does concern itself with, it will be processed at that time.
##  6.4.2.1Design[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#design)
Below are some simplifying principles HAPI MDM follows to reduce complexity and ensure data integrity.
  1. When MDM is enabled on a HAPI FHIR server, any Golden Resource in the repository that has the "hapi-mdm" tag is considered read-only by the FHIR endpoint. These Golden Resources are managed exclusively by HAPI MDM. Users can only change them via [MDM Operations](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html). In most cases, users will indirectly change them by creating and updating the corresponding source resources.
  2. Every source resource in the system has a MATCH link to at most one Golden Resource.
  3. The only source resources in the system that do not have a MATCH link are those that have the 'NO-MDM' tag or those that have POSSIBLE_MATCH links pending review.
  4. The HAPI MDM rules define a single identifier system that holds the external enterprise id ("EID"). If a source resource has an external EID, then the Golden Resource it links to always has the same EID.
  5. Two different Golden Resources cannot have the same EID.
  6. Source resources are only ever compared to Golden Resources via this EID.


##  6.4.2.2Meta Tags[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#meta-tags)
In order for MDM to work, the service adds several pieces of metadata to a given resource. This section explains what MDM does to the resources it processes. When a resource is created via MDM (a Golden Resource), the following meta tag is added to the resource:
```
{
  "resourceType": "Patient",
  "meta": {
    "tag": [
      {
        "system": "https://hapifhir.org/NamingSystem/managing-mdm-system",
        "code": "HAPI-MDM"
      }
    ]
  }
}

```

Copy
When this tag is present, the resource is considered managed by MDM. Any resource that is MDM-managed is considered read-only by the FHIR endpoint. Changes to it can only happen via MDM operations, and attribute survivorship.
There may be use cases where a resource is ingested into the system but you _don't_ want to perform MDM processing on it. Consider the example of multiple patients being admitted into a hospital as "John Doe". If MDM were performed on this patient, it's possible that hundreds of John Doe's could be linked to the same Golden Resource. This would be a very bad situation as they are likely not the same person. To prevent this, you can add the `NO-MDM` tag to the resource. This will prevent MDM from processing it.
```
{
  "resourceType": "Patient",
  "meta": {
    "tag": [
      {
        "system": "https://hapifhir.org/NamingSystem/managing-mdm-system",
        "code": "NO-MDM"
      }
    ]
  }
}

```

Copy
##  6.4.2.3Links[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#links)
  1. HAPI MDM manages mdm-link records ("links") that link a source resource to a Golden Resource. When these are created/updated by matching rules, the links are marked as AUTO. When these links are changed manually, they are marked as MANUAL.
  2. Once a link has been manually assigned as NO_MATCH or MATCH, the system will not change it.
  3. When a new source resource is created/updated it is then compared to all other source resources of the same type in the repository. The outcome of each of these comparisons is either NO_MATCH, POSSIBLE_MATCH or MATCH.
  4. HAPI MDM stores these extra link details in a table called `MPI_LINK`.

6.4.2.3.1Possible rule match outcomes: When a new source resource is compared with all other resources of the same type in the repository, there are four possible outcomes:
  * CASE 1: No MATCH and no POSSIBLE_MATCH outcomes 
  * CASE 2: All of the MATCH source resources are already linked to the same Golden Resource 
  * CASE 3: The MATCH source resources link to more than one Golden Resource 
  * CASE 4: Only POSSIBLE_MATCH outcomes 

6.4.2.3.2MDM and Resource Deletion By default, when the last source resource in a `MATCH` relationship with a golden resource is deleted, the associated golden resource is permanently (hard) deleted. This prevents orphaned golden resources that remain in the database. Note that this will also delete the respective MDM link history. Here are several scenarios and their associated behaviour, we will define SR as a source resource and GR as a golden resource:
  * There is a 1 to 1 `MATCH` relationship between SR/1 and GR/1 
  * GR/1 has a `MATCH` link with SR/1, and a `POSSIBLE_MATCH` link with SR/2 
  * GR/1 has a `MATCH` link with SR/1, a `POSSIBLE_MATCH` link with SR/2, and a `POSSIBLE_DUPLICATE` with GR/2. Additionally, GR/2 has a `MATCH` with SR/3, a `POSSIBLE_MACH` with SR/2 `POSSIBLE_DUPLICATE` link, are deleted. SR/2 maintains its `POSSIBLE_MATCH` relation with GR/2. Finally, GR/1 is deleted.


This behaviour can be changed from the default of hard deleting to soft deleting by setting [setAutoExpungeGoldenResources(boolean)](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server-mdm/ca/uhn/fhir/mdm/rules/config/MdmSettings.html#setAutoExpungeGoldenResources\(boolean\)) to false. Soft deleting the golden resource means the golden resource will continue to persist in the database, but the MDM link history for the affected link(s) will still be accessible, which may be useful for auditing.
#  6.4.3HAPI MDM Technical Details
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html#hapi-mdm-technical-details)
When MDM is enabled, the HAPI FHIR JPA Server does the following things on startup:
  1. It enables the MESSAGE subscription type and starts up the internal subscription engine.
  2. It creates MESSAGE subscriptions for each resource type prefixed with 'mdm-'. For example, if MDM supports Patient and Practitioner resource, two subscriptions, called 'mdm-patient' and 'mdm-practitioner' that match all incoming MDM managed resources and send them to an internal queue called "mdm". The JPA Server listens to this queue and links incoming resources to the appropriate Golden Resources.
  3. The [MDM Operations](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html) are registered with the server.
  4. It registers a new dao interceptor that restricts access to MDM managed Golden Resource records.


[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
6.4 MDM Technical Details 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
[ 6.5 MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)