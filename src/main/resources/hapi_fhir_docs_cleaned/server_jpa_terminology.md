---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html
crawled: 2025-08-01T14:05:07.781160
---

# Server Jpa Terminology

#  5.12.1Terminology
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#terminology)
HAPI FHIR JPA Server includes an `IValidationSupport` class, `JpaPersistedResourceValidationSupport`, which can be used to validate terminology using CodeSystem, ValueSet and ConceptMap resources provided by the JPA Server. Terminology can be loaded into the JPA Server using standard FHIR REST APIs (PUT and POST) as well as using the hapi-fhir-cli [upload-terminology](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#upload-terminology) command.
#  5.12.2Versioning of Terminology
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#versioning-of-terminology)
Code systems can be versioned as described in the FHIR specification [here](http://hl7.org/fhir/codesystem.html#versioning). Similarly, value sets and concept maps that are defined with a versioned code system can also be versioned.
Versions of a given code system, value set or concept map are differentiated from each other by the CodeSystem.version, ValueSet.version and ConceptMap.version properties respectively. Each version of a given code system, value set and concept map will have a separate resource entity. This version should not be confused with the resource version (i.e. Resource.meta.versionId).
When queries or operations are requested with a specific CodeSystem, ValueSet, or ConceptMap url and version specified, the JPA Server will act on the resource(s) specified by or linked to the version. If the same request is submitted with no version specified, the JPA Server will instead act on the current (i.e. most recently updated) version of the resource identified by the URL.
Delta Add and Remove modes in the [upload-terminology](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#upload-terminology) command will only be applied to the most recently updated version.
#  5.12.3Terminology Schemas
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#terminology-schemas)
This page provides the schema of tables that are used to complement the CodeSystem, ValueSet and ConceptMap resources, and which map the relationships between these resources and various properties that are used or referenced by terminology operations.
##  5.12.3.1CodeSystem Tables[](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#codesystem-tables)
![Resources](https://hapifhir.io/hapi-fhir/docs/images/termcodesystem_schema.svg)
The TRM_CODESYSTEM_VER table represents a single CodeSystem resource with a specific URL and version. It is used to map terminology concepts represented by various TRM_CONCEPT_* tables to a single CodeSystem resource.
The TRM_CODESYSTEM table represents the canonical representation of a CodeSystem resource with a specific URL and maps to a single TRM_CODESYSTEM_VER row which is treated as the current version of the CodeSystem (i.e. the resource referenced if no version is specified). For example, two CodeSystem resources `CodeSystem/loinc-2.67` and `CodeSystem/loinc-2.68` might have the same CodeSystem.url, e.g. `http://loinc.org` but different CodeSystem.version values. In this case each will each have exactly one row in the TRM_CODESYSTEM_VER table, but there will be only one row in the TRM_CODESYSTEM table which will link only to the more recently updated TRM_CODESYSTEM_VER resource.
5.12.3.1.1Columns The following are the main key columns in the TRM_CODESYSTEM_VER table that are used to join to the TRM_CODESYSTEM table and TRM_CONCEPT_* tables. Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  Persistent ID of the TRM_CODESYSTEM_VER row.   
RES_ID |  | Long |  |  Persistent ID of the CodeSystem resource in the HFJ_RESOURCE table.   
CODESYSTEM_PID |  | Long | Nullable |  Persistent ID of the TRM_CODESYSTEM row for the canonical representation of this CodeSystem resource.   
CS_VERSION_ID |  | Long | Nullable |  CodeSystem.version of the CodeSystem resource.   
The TRM_CODESYSTEM_VER table will have exactly one row for each unique combination of CODESYSTEM_PID and CS_VERSION_ID.
The following are the main key columns in the TRM_CODESYSTEM table.
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  Persistent ID of the TRM_CODESYSTEM row.   
CODE_SYSTEM_URI |  | String |  |  URL of the CodeSystem resource.   
CURRENT_VERSION_PID |  | Long | Nullable |  Persistent ID of the TRM_CODESYSTEM_VER row for the most recently updated version of the CodeSystem resource for this URL.   
CS_NAME |  | String | Nullable |  CodeSystem.name value for this CodeSystem resource.   
RES_ID |  | Long | Nullable |  Persistent ID of the most recently updated version of the CodeSystem resource for this URL in the HFJ_RESOURCE table.   
The TRM_CODESYSTEM table will have exactly one row for each unique CODE_SYSTEM_URI value.
##  5.12.3.2ValueSet Tables[](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#valueset-tables)
![Resources](https://hapifhir.io/hapi-fhir/docs/images/termvalueset_schema.svg)
The TRM_VALUESET table represents a single ValueSet resource with a specific URL and version. It can be used to map terminology concepts represented by the TRM_VALUESET_CONCEPT and TRM_VALUESET_C_DESIGNATION tables to a single ValueSet resource.
5.12.3.2.1Columns The following are the main key columns in the TRM_VALUESET table that are used to join to the TRM_VALUESET_CONCEPT and TRM_VALUESET_C_DESIGNATION tables. Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  Persistent ID of the TRM_VALUESET row.   
RES_ID |  | Long | Nullable |  Persistent ID of the ValueSet resource in the HFJ_RESOURCE table.   
URL |  | String |  |  Canonical URL for the ValueSet resource.   
VER |  | String | Nullable |  ValueSet.version for this ValueSet resource.   
The TRM_VALUESET table will have exactly one row for each unique combination of URL and VER.
##  5.12.3.3ConceptMap Tables[](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html#conceptmap-tables)
![Resources](https://hapifhir.io/hapi-fhir/docs/images/termconceptmap_schema.svg)
The TRM_CONCEPTMAP table represents a single ConceptMap resource with a specific URL and version. It can be used to map terminology concepts to one another in groups.
5.12.3.3.1Columns The following are the main key columns in the TRM_CONCEPTMAP table that are used to join to the TRM_CONCEPTMAP_* tables. Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  Persistent ID of the TRM_CONCEPTMAP row.   
RES_ID |  | Long | Nullable |  Persistent ID of the ConceptMap resource in the HFJ_RESOURCE table.   
SOURCE_URL |  | String | Nullable |  URL of the source ValueSet to be mapped.   
TARGET_URL |  | String | Nullable |  URL of the target ValueSet to be mapped.   
URL |  | String |  |  Canonical URL for the ConceptMap resource.   
VER |  | String | Nullable |  ConceptMap.version for this ConceptMap resource.   
The TRM_CONCEPTMAP table will have exactly one row for each unique combination of URL and VER.
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html)
5.12 Terminology 
* JPA Server 
* [ 5.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html)
* [ 5.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html)
* [ 5.2  Architecture ](https://hapifhir.io/hapi-fhir/docs/server_jpa/architecture.html)
* [ 5.3  Database Support ](https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html)
* [ 5.4  Database Schema ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html)
* [ 5.5  Configuration ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html)
* [ 5.6  Search ](https://hapifhir.io/hapi-fhir/docs/server_jpa/search.html)
* [ 5.7  Performance ](https://hapifhir.io/hapi-fhir/docs/server_jpa/performance.html)
* [ 5.8  Upgrade Guide ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html)
* [ 5.9  Diff Operation ](https://hapifhir.io/hapi-fhir/docs/server_jpa/diff.html)
* [ 5.10  LastN Operation ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html)
* [ 5.11  Lucene/Elasticsearch Indexing ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html)
* [ 5.12  Terminology ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html)
* [ 5.13  International Patient Summary (IPS) ](https://hapifhir.io/hapi-fhir/docs/server_jpa/ips.html)
[ 5.13 International Patient Summary (IPS) ](https://hapifhir.io/hapi-fhir/docs/server_jpa/ips.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)