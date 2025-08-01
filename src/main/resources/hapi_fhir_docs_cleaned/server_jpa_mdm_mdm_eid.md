---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html
crawled: 2025-08-01T14:06:00.703936
---

# Server Jpa Mdm Mdm Eid

#  6.2.1MDM Enterprise Identifiers
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html#mdm-enterprise-identifiers)
An Enterprise Identifier (EID) is a unique identifier that can be attached to source resources. Each implementation is expected to use exactly one EID system for incoming resources, defined in the MDM Rules file. If a source resource with a valid EID is submitted, that EID will be copied over to the Golden Resource that was matched.
##  6.2.1.1MDM EID Settings[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html#mdm-eid-settings)
The [MdmSettings](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server-mdm/ca/uhn/fhir/mdm/rules/config/MdmSettings.html) bean contains two EID related settings. Both are enabled by default.
  * **Prevent EID Updates** ([JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server-mdm/ca/uhn/fhir/mdm/rules/config/MdmSettings.html#setPreventEidUpdates\(boolean\))): If this is enabled, then once an EID is set on a resource, it cannot be changed. If disabled, patients may have their EID updated.
  * **Prevent multiple EIDs** : ([JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server-mdm/ca/uhn/fhir/mdm/rules/config/MdmSettings.html#setPreventMultipleEids\(boolean\))): If this is enabled, then a resource cannot have more than one EID, and incoming resources that break this rule will be rejected.


##  6.2.1.2MDM EID Scenarios[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html#mdm-eid-scenarios)
MDM EID management follows a complex set of rules to link related source records via their Enterprise Id. The following diagrams outline how EIDs are replicated from Patient resources to their linked Golden Patient resources under various scenarios according to the values of the EID Settings.
##  6.2.1.3MDM EID Create Scenarios[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html#mdm-eid-create-scenarios)
[![MDM Create 1](https://hapifhir.io/hapi-fhir/docs/images/empi-create-1.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-create-1.svg)
[![MDM Create 2](https://hapifhir.io/hapi-fhir/docs/images/empi-create-2.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-create-2.svg)
[![MDM Create 3](https://hapifhir.io/hapi-fhir/docs/images/empi-create-3.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-create-3.svg)
[![MDM Create 4](https://hapifhir.io/hapi-fhir/docs/images/empi-create-4.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-create-4.svg)
[![MDM Create 5](https://hapifhir.io/hapi-fhir/docs/images/empi-create-5.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-create-5.svg)
##  6.2.1.4MDM EID Update Scenarios[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html#mdm-eid-update-scenarios)
[![MDM Update 1](https://hapifhir.io/hapi-fhir/docs/images/empi-update-1.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-1.svg)
[![MDM Update 2](https://hapifhir.io/hapi-fhir/docs/images/empi-update-2.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-2.svg)
[![MDM Update 3](https://hapifhir.io/hapi-fhir/docs/images/empi-update-3.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-3.svg)
[![MDM Update 4](https://hapifhir.io/hapi-fhir/docs/images/empi-update-4.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-4.svg)
[![MDM Update 5](https://hapifhir.io/hapi-fhir/docs/images/empi-update-5.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-5.svg)
[![MDM Update 6](https://hapifhir.io/hapi-fhir/docs/images/empi-update-6.svg)](https://hapifhir.io/hapi-fhir/docs/images/empi-update-6.svg)
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
6.2 MDM Enterprise Identifiers 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
[ 6.3 MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)