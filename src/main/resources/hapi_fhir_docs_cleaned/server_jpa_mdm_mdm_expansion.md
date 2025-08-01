---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html
crawled: 2025-08-01T14:06:14.014420
---

# Server Jpa Mdm Mdm Expansion

#  6.5.1MDM Expansion
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html#mdm-expansion)
Once you have MDM enabled, and you have many linked resources, it can be useful to search across all linked resources. Let's say you have the following MDM links in your database:
```
Patient/1 --> Patient/3
Patient/2 --> Patient/3

```

Copy
This indicates that both Patient/1 and Patient/2 are MDM-matched to the same golden resource (Patient/3). What if you want to get all observations from Patient/1, but also include any observations from all of their linked resources. You could do this by first querying the [$mdm-query-links](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html) endpoint, and then making a subsequent call like the following
```
GET http://example.com:8000/Observation?subject=Patient/1,Patient/2,Patient/3

```

Copy
But HAPI-FHIR allows a shorthand for this, by means of a Search Parameter qualifier, as follows:
```
GET http://example.com:8000/Observation?subject:mdm=Patient/1

```

Copy
This `:mdm` parameter qualifier instructs an interceptor in HAPI fhir to expand the set of resources included in the search by their MDM-matched resources. The two above HTTP requests will return the same result.
This behaviour is also supported on the `$everything` operation, via a slightly different mechanism. If you call the operation with `_mdm=true`, then MDM expansion will occur on the base Patient instance. For example:
```
GET http://example.com:8000/Patient/1/$everything?_mdm=true

```

Copy
This will first lookup all Patients linked to Patient/1, and then perform an `$everything` including all resources for these patients.
One important caveat is that chaining is currently not supported when using this prefix. 
##  6.5.1.1Enabling MDM Expansion[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html#enabling-mdm-expansion)
On top of needing to instantiate an MDM module, you must enable this feature in the [StorageSettings](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html) bean, using the [Allow MDM Expansion](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#setAllowMdmExpansion\(boolean\)) property.
It is important to note that enabling this functionality can lead to incorrect data being returned by a request, if your MDM links are incorrect. Use with caution. 
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
6.5 MDM Search Expansion 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
[ 6.6 MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)