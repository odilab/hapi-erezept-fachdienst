---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html
crawled: 2025-08-01T14:05:36.923054
---

# Server Jpa Partitioning Partition Interceptor Examples

#  7.1.1Partition Interceptor Examples
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#partition-interceptor-examples)
This page shows examples of partition interceptors.
#  7.1.2Example: Partitioning based on Tenant ID
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#example-partitioning-based-on-tenant-id)
The [RequestTenantPartitionInterceptor](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#request-tenant-partition-interceptor) uses the request tenant ID to determine the partition name. A simplified version of its source is shown below:
```
@Interceptor
public class RequestTenantPartitionInterceptor {

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_CREATE)
   public RequestPartitionId PartitionIdentifyCreate(ServletRequestDetails theRequestDetails) {
      return extractPartitionIdFromRequest(theRequestDetails);
   }

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_READ)
   public RequestPartitionId PartitionIdentifyRead(ServletRequestDetails theRequestDetails) {
      return extractPartitionIdFromRequest(theRequestDetails);
   }

   private RequestPartitionId extractPartitionIdFromRequest(ServletRequestDetails theRequestDetails) {
      // We will use the tenant ID that came from the request as the partition name
      String tenantId = theRequestDetails.getTenantId();
      return RequestPartitionId.fromPartitionName(tenantId);
   }
}

```

Copy
#  7.1.3Example: Partitioning based on headers
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#example-partitioning-based-on-headers)
If requests are coming from a trusted system, that system might be relied on to determine the partition for reads and writes.
The following example shows a simple partition interceptor that determines the partition name by looking at a custom HTTP header:
```
@Interceptor
public class CustomHeaderBasedPartitionInterceptor {

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_CREATE)
   public RequestPartitionId PartitionIdentifyCreate(ServletRequestDetails theRequestDetails) {
      String partitionName = theRequestDetails.getHeader("X-Partition-Name");
      return RequestPartitionId.fromPartitionName(partitionName);
   }

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_READ)
   public RequestPartitionId PartitionIdentifyRead(ServletRequestDetails theRequestDetails) {
      String partitionName = theRequestDetails.getHeader("X-Partition-Name");
      return RequestPartitionId.fromPartitionName(partitionName);
   }
}

```

Copy
#  7.1.4Example: Using Resource Contents
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#example-using-resource-contents)
When creating resources, the contents of the resource can also be factored into the decision on which tenant to use. The following example shows a very simple algorithm, placing resources into one of three partitions based on the resource type. Other contents in the resource could also be used instead.
```
@Interceptor
public class ResourceTypePartitionInterceptor {

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_CREATE)
   public RequestPartitionId PartitionIdentifyCreate(IBaseResource theResource) {
      if (theResource instanceof Patient) {
         return RequestPartitionId.fromPartitionName("PATIENT");
      } else if (theResource instanceof Observation) {
         return RequestPartitionId.fromPartitionName("OBSERVATION");
      } else {
         return RequestPartitionId.fromPartitionName("OTHER");
      }
   }
}

```

Copy
#  7.1.5Example: Always Read All Partitions
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#example-always-read-all-partitions)
This is an example of a simple interceptor that causes read/search/history requests to always use all partitions. This would be useful if partitioning is being used for use cases that do not involve data segregation for end users.
This interceptor only provides the [`STORAGE_PARTITION_IDENTIFY_READ`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PARTITION_IDENTIFY_READ) pointcut, so a separate interceptor would have to be added to provide the [`STORAGE_PARTITION_IDENTIFY_CREATE`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#STORAGE_PARTITION_IDENTIFY_CREATE) pointcut in order to be able to write data to the server.
```
@Interceptor
public class PartitionInterceptorReadAllPartitions {

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_READ)
   public RequestPartitionId readPartition() {
      return RequestPartitionId.allPartitions();
   }
}

```

Copy
#  7.1.6Example: Smile CDR SMART Scopes
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html#example-smile-cdr-smart-scopes)
When deploying a partitioned server in Smile CDR using SMART on FHIR security, it may be desirable to use OAuth2 scope approval as a mechanism for determining which partitions a user should have access to.
This interceptor looks for approved scopes named `partition-ABC` where "ABC" represents the tenant name that the user should have access to.
```
@Interceptor
public class PartitionInterceptorReadPartitionsBasedOnScopes {

   @Hook(Pointcut.STORAGE_PARTITION_IDENTIFY_READ)
   public RequestPartitionId readPartition(ServletRequestDetails theRequest) {

      HttpServletRequest servletRequest = theRequest.getServletRequest();
      Set<String> approvedScopes =
            (Set<String>) servletRequest.getAttribute("ca.cdr.servletattribute.session.oidc.approved_scopes");

      String partition = approvedScopes.stream()
            .filter(t -> t.startsWith("partition-"))
            .map(t -> t.substring("partition-".length()))
            .findFirst()
            .orElseThrow(() -> new InvalidRequestException("No partition scopes found in request"));
      return RequestPartitionId.fromPartitionName(partition);
   }
}

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html)
7.1 Partition Interceptor Examples 
* JPA Server: Partitioning and Multitenancy 
* [ 7.0  Partitioning and Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html)
* [ 7.1  Partition Interceptor Examples ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partition_interceptor_examples.html)
* [ 7.2  Partitioning Management Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html)
* [ 7.3  Enabling Partitioning in HAPI FHIR ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/enabling_in_hapi_fhir.html)
* [ 7.4  Database Partition Mode ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/db_partition_mode.html)
[ 7.2 Partitioning Management Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning_management_operations.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)