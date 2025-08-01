---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html
crawled: 2025-08-01T14:09:15.050128
---

# Server Jpa Partitioning Partitioning Management Operations

#  7.2.1Partition Mapping Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#partition-mapping-operations)
Several operations exist that can be used to manage the existence of partitions. These operations are supplied by a [plain provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers) called [PartitionManagementProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/ca/uhn/fhir/jpa/partition/PartitionManagementProvider.html).
Before a partition can be used, it must be registered using these methods.
#  7.2.2Creating a Partition
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#creating-a-partition)
The `$partition-management-create-partition` operation can be used to create a new partition. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
id | Integer | 0..1 |  The numeric ID for the partition. This value can be any integer, positive or negative or zero. It must not be a value that has already been used. If omitted, a random unused integer will be selected.   
name | Code | 1..1 |  A code (string) to assign to the partition.   
description | String | 0..1 |  An optional description for the partition.   
##  7.2.2.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#example)
Note that once multitenancy is enabled, all requests to the FHIR server must contain a tenant. These operations are no exception. If you fail to include a tenant identifier in the request, an error will be returned.
An HTTP POST to the following URL would be used to invoke this operation. Notice that we use the DEFAULT partition, as it always exists by default. [http://example.com/DEFAULT/$partition-management-create-partition](http://example.com/DEFAULT/$partition-management-create-partition)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "id",
    "valueInteger": 123
  }, {
    "name": "name",
    "valueCode": "PARTITION-123"
  }, {
    "name": "description",
    "valueString": "a description"
  } ]
}

```

Copy
#  7.2.3Updating a Partition
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#updating-a-partition)
The `$partition-management-update-partition` operation can be used to update an existing partition. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
id | Integer | 1..1 |  The numeric ID for the partition to update. This ID must already exist.   
name | Code | 1..1 |  A code (string) to assign to the partition. Note that it is acceptable to change the name of a partition, but this should be done with caution since partition names may be referenced by URLs, caches, etc.   
description | String | 0..1 |  An optional description for the partition.   
##  7.2.3.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#example-1)
An HTTP POST to the following URL would be used to invoke this operation: [http://example.com/DEFAULT/$partition-management-update-partition](http://example.com/DEFAULT/$partition-management-update-partition)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "id",
    "valueInteger": 123
  }, {
    "name": "name",
    "valueCode": "PARTITION-123"
  }, {
    "name": "description",
    "valueString": "a description"
  } ]
}

```

Copy
#  7.2.4Deleting a Partition
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#deleting-a-partition)
The `$partition-management-delete-partition` operation can be used to delete an existing partition. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
id | Integer | 1..1 |  The numeric ID for the partition to update. This ID must already exist.   
##  7.2.4.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#example-2)
An HTTP POST to the following URL would be used to invoke this operation: [http://example.com/DEFAULT/$partition-management-delete-partition](http://example.com/DEFAULT/$partition-management-delete-partition)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "id",
    "valueInteger": 123
  } ]
}

```

Copy
#  7.2.5Reading a Partition
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#reading-a-partition)
The `$partition-management-read-partition` operation can be used to read an existing partition. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
id | Integer | 1..1 |  The numeric ID for the partition to update. This ID must already exist.   
##  7.2.5.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#example-3)
An HTTP POST to the following URL would be used to invoke this operation: [http://example.com/DEFAULT/$partition-management-read-partition](http://example.com/DEFAULT/$partition-management-read-partition)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "id",
    "valueInteger": 123
  } ]
}

```

Copy
#  7.2.6Listing all Partitions
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#listing-all-partitions)
The `$partition-management-list-partitions` operation can be used to list all existing partitions.
##  7.2.6.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html#example-4)
An HTTP POST to the following URL would be used to invoke this operation: [http://example.com/DEFAULT/$partition-management-list-partitions](http://example.com/DEFAULT/$partition-management-list-partitions)
This operation returns a `Parameters` resource that looks like the following:
```
{
    "resourceType": "Parameters",
    "parameter": [ {
       "name": "partition",
       "part": [ {
          "name": "id",
          "valueInteger": 1
        }, {
          "name": "name",
          "valueCode": "PARTITION-1"
        }, {
          "name": "description",
          "valueString": "a description1"
        } ]
      }, {
       "name": "partition",
       "part": [ {
          "name": "id",
          "valueInteger": 2
       }, {
          "name": "name",
          "valueCode": "PARTITION-2"
       }, {
          "name": "description",
          "valueString": "a description2"
       } ]
    } ]
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html)
7.2 Partitioning Management Operations 
* JPA Server: Partitioning and Multitenancy 
* [ 7.0  Partitioning and Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html)
* [ 7.1  Partition Interceptor Examples ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html)
* [ 7.2  Partitioning Management Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html)
* [ 7.3  Enabling Partitioning in HAPI FHIR ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html)
* [ 7.4  Database Partition Mode ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/db_partition_mode.html)
[ 7.3 Enabling Partitioning in HAPI FHIR ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)