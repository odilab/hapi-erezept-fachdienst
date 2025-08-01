---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html
crawled: 2025-08-01T14:06:15.017572
---

# Server Jpa Partitioning Enabling In Hapi Fhir

#  7.3.1Enabling Partitioning in HAPI FHIR
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html#enabling-partitioning-in-hapi-fhir)
Follow these steps to enable partitioning on the server:
The [PartitionSettings](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/config/PartitionSettings.html) bean contains configuration settings related to partitioning within the server. To enable partitioning, the [setPartitioningEnabled(boolean)](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/config/PartitionSettings.html#setPartitioningEnabled\(boolean\)) property should be enabled.
The following settings can be enabled:
  * **Include Partition in Search Hashes** ([JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/config/PartitionSettings.html#setIncludePartitionInSearchHashes\(boolean\))): If this feature is enabled, partition IDs will be factored into [Search Hashes](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#search-hashes). When this flag is not set (as is the default), when a search requests a specific partition, an additional SQL WHERE predicate is added to the query to explicitly request the given partition ID. When this flag is set, this additional WHERE predicate is not necessary since the partition is factored into the hash value being searched on. Setting this flag avoids the need to manually adjust indexes against the HFJ_SPIDX tables. Note that this flag should **not be used in environments where partitioning is being used for security purposes** , since it is possible for a user to reverse engineer false hash collisions. This setting should not be enabled in combination with [Index Storage Optimized](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#isIndexStorageOptimized\(\)) flag, as in this case Partition could not be included in Search Hashes.
  * **Cross-Partition Reference Mode** : ([JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/config/PartitionSettings.html#setAllowReferencesAcrossPartitions\(ca.uhn.fhir.jpa.model.config.PartitionSettings.CrossPartitionReferenceMode\))): This setting controls whether resources in one partition should be allowed to create references to resources in other partitions.


[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html)
7.3 Enabling Partitioning in HAPI FHIR 
* JPA Server: Partitioning and Multitenancy 
* [ 7.0  Partitioning and Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html)
* [ 7.1  Partition Interceptor Examples ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html)
* [ 7.2  Partitioning Management Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html)
* [ 7.3  Enabling Partitioning in HAPI FHIR ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html)
* [ 7.4  Database Partition Mode ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/db_partition_mode.html)
[ 7.4 Database Partition Mode ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/db_partition_mode.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)