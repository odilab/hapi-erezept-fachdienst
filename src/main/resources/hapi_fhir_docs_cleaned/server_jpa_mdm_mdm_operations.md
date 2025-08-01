---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html
crawled: 2025-08-01T14:04:49.904740
---

# Server Jpa Mdm Mdm Operations

#  6.3.1MDM Operations
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#mdm-operations)
MDM links are managed by MDM Operations. These operations are supplied by a [plain provider](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html#plain-providers) called [MdmProvider](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server-mdm/ca/uhn/fhir/mdm/provider/MdmProviderDstu3Plus.html).
In cases where the operation changes data, if a resource id parameter contains a version (e.g. `Patient/123/_history/1`), then the operation will fail with a 409 CONFLICT if that is not the latest version of that resource. This feature can be used to prevent update conflicts in an environment where multiple users are working on the same set of mdm links.
##  6.3.1.1Pagination[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#pagination)
In both the `$query-links` operation, and the `$mdm-duplicate-golden-resources` paging is supported via `_count` and `_offset` parameters. By default, if you omit page information from your query, default pagination values will be used. The response will return you the next/self/previous links as part of the parameters response. Here are examples of pagination in these MDM queries.
```
GET http://example.com/$mdm-query-links?_offset=0&_count=2

```

Copy
Or if you are making a POST request
```
POST http://example.com/$mdm-query-links

```

Copy
With request body:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "_offset",
    "valueInteger": 10
  }, {
    "name": "_count",
    "valueInteger": 10
  } ]
}

```

Copy
The returning response will contain links to the current, next, and previous pages. If there is no previous/next link, it means there is no previous/next page of data available.
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "prev",
    "valueUri": "http://example.com/$mdm-query-links?_offset=0&_count=10"
  }, {
    "name": "self",
    "valueUri": "http://example.com/$mdm-query-links?_offset=10&_count=10"
  }, {
    "name": "next",
    "valueUri": "http://example.com/$mdm-query-links?_offset=20&_count=10"
  },...(truncated) 

```

Copy
##  6.3.1.2Query links[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#query-links)
Use the `$mdm-query-links` operation to view MDM links. The results returned are based on the parameters provided. All parameters are optional. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
goldenResourceId | String | 0..1 |  The id of the Golden Resource (e.g. Golden Patient Resource).   
resourceId | String | 0..1 |  The id of the source resource (e.g. Patient resource).   
matchResult | String | 0..1 |  MATCH, POSSIBLE_MATCH or NO_MATCH.   
linkSource | String | 0..1 |  AUTO, MANUAL.   
_offset | int | 0..1 |  the offset to begin returning records at.   
_count | int | 0..1 |  The number of links to be returned in a page.   
_sort | String | 0..1 |  The sort specification (see sort note below).   
resourceType | String | 0..1 |  The resource type (e.g. Patient)   
Sort note: sort is specified by adding one or more comma-separated MdmLink property names prefixed by '-' (minus sign) to indicate descending order.
6.3.1.2.1Sort specification example
[http://example.com/$mdm-query-links?_sort=-myScore,myCreated](http://example.com/$mdm-query-links?_sort=-myScore,myCreated)
6.3.1.2.2Example
Use an HTTP GET like `http://example.com/$mdm-query-links?matchResult=POSSIBLE_MATCH` or an HTTP POST to the following URL to invoke this operation: [http://example.com/$mdm-query-links?_offset=10&_count=2](http://example.com/$mdm-query-links?_offset=10&_count=2)
The following request body could be used to find all POSSIBLE_MATCH links in the system:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
     "name": "prev",
     "valueUri": "http://example.com/$mdm-query-links?_offset=8_count=2"
  }, {
     "name": "self",
     "valueUri": "http://example.com/$mdm-query-links?_offset=10_count=2"
  }, {
     "name": "next",
     "valueUri": "http://example.com/$mdm-query-links?_offset=12_count=2"
  }, {
    "name": "matchResult",
    "valueString": "POSSIBLE_MATCH"
  } ]
}

```

Copy
This operation returns a `Parameters` resource that looks like the following:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
     "name": "prev",
     "valueUri": "http://example.com$mdm-query-links?_offset=8_count=2"
    },  {
     "name": "self",
     "valueUri": "http://example.com$mdm-query-links?_offset=10_count=10"
    }, {
     "name": "next",
     "valueUri": "http://example.com$mdm-query-links?_offset=20_count=10"
    }, {
    "name": "link",
    "part": [ {
      "name": "goldenResourceId",
      "valueString": "Patient/123"
    }, {
      "name": "sourceResourceId",
      "valueString": "Patient/456"
    }, {
      "name": "matchResult",
      "valueString": "POSSIBLE_MATCH"
    }, {
      "name": "linkSource",
      "valueString": "AUTO"
    }, {
      "name": "eidMatch",
      "valueBoolean": false
    }, {
      "name": "hadToCreateNewResource",
      "valueBoolean": false
    }, {
      "name": "score",
      "valueDecimal": 1.8
    } ]
  } ]
}

```

Copy
##  6.3.1.3Link History[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#link-history)
Use the `$mdm-link-history` operation to request a list of historical entries for a given set of `goldenResourceId`s or `sourceResourceId`s. Either parameter is optional but **at least one** must be provided.
MDM link history is made possible by a back-end configuration that enables saving the historical entries to a new audit table in the database. This feature is enabled by default. Some clients may wish to leave this feature disabled in order to save disk space.
Setting this property explicitly to false disables the feature: [Non Resource DB History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/JpaStorageSettings.html#isNonResourceDbHistoryEnabled\(\))
This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
goldenResourceId | String | 0..* |  The id of the Golden Resource (e.g. Golden Patient Resource).   
resourceId | String | 0..* |  The id of the source resource (e.g. Patient resource).   
This operation returns a `Parameters` resource that looks like the following, in the example case where an MdmLink was updated from MATCH to NO_MATCH. The MDM revisions are sorted:
  * First by golden resource ID in _ascending_ order
  * Second by source resource ID in _ascending_ order
  * Third by revision timestamp in _descending_ order.


If there are any duplication between results returned by a combination of golden resource IDs and source IDs, they will be included only once. So, for example, if there is one historical MDM link for golden resource 123 and source resource 456, and both of these identifiers are in the query, only a single historical entry will be returned.
6.3.1.3.1Example Use
[http://example.com/$mdm-link-history?goldenResourceId=1553&resourceId=1552](http://example.com/$mdm-link-history?goldenResourceId=1553&resourceId=1552)
```
{
    "resourceType": "Parameters",
    "parameter": [
        {
            "name": "historical link",
            "part": [
                {
                    "name": "goldenResourceId",
                    "valueString": "Patient/1553"
                },
                {
                    "name": "revisionTimestamp",
                    "valueString": "2023-03-16 15:14:39.17"
                },
                {
                    "name": "sourceResourceId",
                    "valueString": "Patient/1552"
                },
                {
                    "name": "matchResult",
                    "valueString": "NO_MATCH"
                },
                {
                    "name": "score",
                    "valueDecimal": 1
                },
                {
                    "name": "linkSource",
                    "valueString": "MANUAL"
                },
                {
                    "name": "eidMatch",
                    "valueBoolean": false
                },
                {
                    "name": "hadToCreateNewResource",
                    "valueBoolean": true
                },
                {
                    "name": "score",
                    "valueDecimal": 1
                },
                {
                    "name": "linkCreated",
                    "valueDecimal": 1678994017461
                },
                {
                    "name": "linkUpdated",
                    "valueDecimal": 1678994079155
                }
            ]
        },
        {
            "name": "historical link",
            "part": [
                {
                    "name": "goldenResourceId",
                    "valueString": "Patient/1553"
                },
                {
                    "name": "revisionTimestamp",
                    "valueString": "2023-03-16 15:13:37.469"
                },
                {
                    "name": "sourceResourceId",
                    "valueString": "Patient/1552"
                },
                {
                    "name": "matchResult",
                    "valueString": "MATCH"
                },
                {
                    "name": "score",
                    "valueDecimal": 1
                },
                {
                    "name": "linkSource",
                    "valueString": "AUTO"
                },
                {
                    "name": "eidMatch",
                    "valueBoolean": false
                },
                {
                    "name": "hadToCreateNewResource",
                    "valueBoolean": true
                },
                {
                    "name": "score",
                    "valueDecimal": 1
                },
                {
                    "name": "linkCreated",
                    "valueDecimal": 1678994017461
                },
                {
                    "name": "linkUpdated",
                    "valueDecimal": 1678994017461
                }
            ]
        }
    ]
}

```

Copy
##  6.3.1.4Query Duplicate Golden Resources[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#query-duplicate-golden-resources)
Use the `$mdm-duplicate-golden-resources` operation to request a list of duplicate Golden Resources. This operation takes no parameters.
6.3.1.4.1Example
Use an HTTP GET to the following URL to invoke this operation: [http://example.com/$mdm-duplicate-golden-resources](http://example.com/$mdm-duplicate-golden-resources)
The following is a table of the request parameters supported by this GET operation.
Name | Type | Cardinality | Description  
---|---|---|---  
_offset | int | 0..1 |  The offset to begin returning records at.   
_count | int | 0..1 |  The number of links to be returned in a page.   
resourceType | String | 0..1 |  The resource type (e.g. Patient)   
This operation returns `Parameters` similar to `$mdm-query-links`:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "link",
    "part": [ {
      "name": "goldenResourceId",
      "valueString": "Patient/123"
    }, {
      "name": "sourceResourceId",
      "valueString": "Patient/456"
    }, {
      "name": "matchResult",
      "valueString": "POSSIBLE_DUPLICATE"
    }, {
      "name": "linkSource",
      "valueString": "AUTO"
    } ]
  } ]
}

```

Copy
##  6.3.1.5Unduplicate Golden Resources[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#unduplicate-golden-resources)
Use the `$mdm-not-duplicate` operation to mark duplicate Golden Resources as not duplicates. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
goldenResourceId | String | 1..1 |  The id of the Golden Resource.   
resourceId | String | 1..1 |  The id of the source resource that has a possible duplicate link to.   
6.3.1.5.1Example
Use an HTTP POST to the following URL to invoke this operation: [http://example.com/$mdm-not-duplicate](http://example.com/$mdm-not-duplicate)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "goldenResourceId",
    "valueString": "Patient/123"
  }, {
    "name": "resourceId",
    "valueString": "Patient/456"
  } ]
}

```

Copy
When the operation is successful, it returns the following `Parameters`:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "success",
    "valueBoolean": true
  } ]
}

```

Copy
##  6.3.1.6Update Link[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#update-link)
Use the `$mdm-update-link` operation to change the `matchResult` update of an mdm link. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
goldenResourceId | String | 1..1 |  The id of the Golden Resource.   
resourceId | String | 1..1 |  The id of the target resource.   
matchResult | String | 1..1 |  Must be either MATCH or NO_MATCH.   
MDM links updated in this way will automatically have their `linkSource` set to `MANUAL`.
6.3.1.6.1Example
Use an HTTP POST to the following URL to invoke this operation: [http://example.com/$mdm-update-link](http://example.com/$mdm-update-link)
Any supported MDM type can be used. The following request body shows how to update link on the Patient resource type:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "goldenResourceId",
    "valueString": "Patient/123"
  }, {
    "name": "resourceId",
    "valueString": "Patient/456"
  }, {
    "name": "matchResult",
    "valueString": "MATCH"
  } ]
}

```

Copy
The operation returns the updated Golden Resource. For the query above `Patient` resource will be returned. Note that this is the only way to modify MDM-managed Golden Resources.
##  6.3.1.7Create Link[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#create-link)
Use the `$mdm-create-link` operation to create an MDM link from a Golden Resource to a Target Resource without the need for any pre-existing matching data within the two resources. This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
goldenResourceId | String | 1..1 |  The id of the Golden Resource.   
resourceId | String | 1..1 |  The id of the target resource.   
matchResult | String | 0..1 |  Optional matchResult. If omitted, it automatically set the default to MATCH, otherwise the value should be MATCH, POSSIBLE_MATCH or NO_MATCH.   
MDM links created in this way will automatically have their `linkSource` set to `MANUAL`.
6.3.1.7.1Example
Use an HTTP POST to the following URL to invoke this operation: [http://example.com/$mdm-create-link](http://example.com/$mdm-create-link)
Any supported MDM type can be used. The following request body shows how to update link on the Patient resource type:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "goldenResourceId",
    "valueString": "Patient/123"
  }, {
    "name": "resourceId",
    "valueString": "Patient/456"
  }, {
     "name": "matchResult",
     "valueString": "MATCH"
  } ]
}

```

Copy
The operation returns the Golden Resource. For the query above, `Patient` will be returned.
##  6.3.1.8Merge Golden Resources[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#merge-golden-resources)
The `$mdm-merge-golden-resources` operation can be used to merge one Golden Resource with another. When doing this, you will need to decide which resource to merge from and which one to merge to.
After the merge is complete, `fromGoldenResourceId` will be deactivated by assigning a metadata tag `REDIRECTED`.
This operation takes the following parameters:
Name | Type | Cardinality | Description  
---|---|---|---  
fromGoldenResourceId | String | 1..1 |  The id of the Golden Resource to merge data from.   
toGoldenResourceId | String | 1..1 |  The id of the Golden Resource to merge data into.   
resource | Resource | 0..1 |  Optional manually merged Golden Resource. All values except for the metadata, PID and identifiers will be copied from this resource, if it is present. If no value is specified, all fields from the resource pointed to by "fromGoldenResourceId" will be copied instead.   
6.3.1.8.1Example
Use an HTTP POST to the following URL to invoke this operation: [http://example.com/$mdm-merge-golden-resources](http://example.com/$mdm-merge-golden-resources)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "fromGoldenResourceId",
    "valueString": "Patient/123"
  }, {
    "name": "toGoldenResourceId",
    "valueString": "Patient/128"
  } ]
}

```

Copy
This operation returns the merged Golden Resource (`toGoldenResourceId`).
#  6.3.2Querying The MDM
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#querying-the-mdm)
##  6.3.2.1Querying the Patient Resource[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#querying-the-patient-resource)
When MDM is enabled, the [$match operation](http://hl7.org/fhir/patient-operation-match.html) will be enabled on the JPA Server for Patient resources.
This operation allows a Patient or Practitioner resource to be submitted to the endpoint, and the system will attempt to find and return any Patient resources that match it according to the matching rules. The response includes a search score field that is calculated by averaging the number of matched rules against total rules checked for the Patient resource. Appropriate match grade extension is also included.
For example, the following request may be submitted:
```
POST /Patient/$match
Content-Type: application/fhir+json; charset=UTF-8

{
    "resourceType":"Parameters",
    "parameter": [
        {
            "name":"resource",
            "resource": {
                "resourceType":"Patient",
                "name": [
                   { "family":"foo" }
                ]
            }
        }
    ]
}

```

Copy
Sample response for the Patient match is included below:
```
{
  "resourceType": "Bundle",
  "id": "0e712adc-6979-4875-bbe9-70b883a955b8",
  "meta": {
    "lastUpdated": "2019-06-06T22:46:43.809+03:30"
  },
  "type": "searchset",
  "entry": [
    {
      "resource": {
        "resourceType": "Patient",
        "id": "3",
        "meta": {
          "versionId": "1",
          "lastUpdated": "2019-06-06T22:46:43.339+03:30"
        },
        "name": [
          {
            "family": "foo",
            "given": [
              "bar"
            ]
          }
        ],
        "birthDate": "2000-01-01"
      },
      "search": {
        "extension": [{
          "url": "http://hl7.org/fhir/StructureDefinition/match-grade",
          "valueCode": "certain"
        }],
        "mode": "match",
        "score": 0.9
      }
    }
  ]
}

```

Copy
##  6.3.2.2Querying the Other Supported MDM Resources via `/$mdm-match`[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#querying-the-other-supported-mdm-resources-via-mdm-match)
Query operations on any other supported MDM type are also allowed via the server-level operation `/$mdm-match`. This operation will find resources that match the provided parameters according to the matching rules. The response includes a search score field that is calculated by averaging the number of matched rules against total rules checked for the Patient resource. Appropriate match grade extension is also included in the response.
The request below may be submitted to search for `Organization` in case it defined as a supported MDM type:
```
POST /$mdm-match 
Content-Type: application/fhir+json; charset=UTF-8

{
    "resourceType":"Parameters",
    "parameter": [
        {
            "name":"resource",
            "resource": {
                "resourceType":"Organization",
                "name": "McMaster Family Practice"
            }
        },
        {
            "name":"resourceType",
            "valueString": "Organization"
        }
    ]
}

```

Copy
MDM will respond with the appropriate resource bundle.
Note that the request goes to the root of the FHIR server, and not the `Organization` endpoint. Since this is not in the FHIR spec directly, it was decided that this would be a separate operation from the Patient/Practitioner `/$match` operation.
##  6.3.2.3Clearing MDM Links[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#clearing-mdm-links)
The `$mdm-clear` operation is used to batch-delete MDM links and related Golden Resources from the database. This operation is intended to be used during the rules-tuning phase of the MDM implementation so that you can quickly test your ruleset. It permits the user to reset the state of their MDM system without manual deletion of all related links and Golden Resources.
After the operation is complete, all targeted MDM links are removed from the system, and their related Golden Resources are deleted and expunged from the server. Additionally, the link history for targeted links and their related golden resources will also be expunged.
This operation takes two optional Parameters.
Name | Type | Cardinality | Description  
---|---|---|---  
resourceType | String | 0..* |  The Source resource types you would like to clear. If omitted, all resource types will be cleared.   
batchSize | Integer | 0..1 |  The number of links that should be deleted at a time. If omitted, then the batch size will be determined by the value of [Reindex Batch Size](/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/StorageConfig.html#getReindexBatchSize()) property.   
6.3.2.3.1Example Use an HTTP POST to the following URL to invoke this operation: ```
POST /$mdm-clear
Content-Type: application/fhir+json

{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "resourceType",
    "valueString": "Patient"
  }, {
    "name": "resourceType",
    "valueString": "Practitioner"
  }, {
    "name": "batchSize",
    "valueDecimal": 1000
  } ]
}

```
Copy This operation returns the job execution id of the Spring Batch job that will be run to remove all the links and their golden resources.
##  6.3.2.4Batch-creating MDM Links[](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html#batch-creating-mdm-links)
Call the `$mdm-submit` operation to submit patients and practitioners for MDM processing. In the rules-tuning phase of your setup, you can use `$mdm-submit` to apply MDM rules across multiple Resources. An important thing to note is that this operation only submits the resources for processing. Actual MDM processing is run asynchronously, and depending on the size of the affected bundle of resources, may take some time to complete.
After the operation is complete, all resources that matched the criteria will now have at least one MDM link attached to them.
This operation takes a single optional criteria parameter unless it is called on a specific instance.
Note that this operation can take a long time on large data sets. In order to support large data sets, the operation can be run asynchronously. This can be done by sending the `Prefer: respond-async` header with the request. This will cause HAPI-FHIR to execute the request as a batch job. The response will contain a `jobId` parameter that can be used to poll the status of the operation. Note that completion of the job indicates completion of loading all the resources onto the broker, not necessarily the completion of the actual underlying MDM process.
Name | Type | Cardinality | Description  
---|---|---|---  
criteria | String | 0..1 |  The search criteria used to filter resources. An empty criteria will submit all resources.   
6.3.2.4.1Example
This operation can be executed at the Server level, Resource level, or Instance level. Use an HTTP POST to the following URL to invoke this operation with matching criteria: [http://example.com/$mdm-submit](http://example.com/$mdm-submit)  
[http://example.com/Patient/$mdm-submit](http://example.com/Patient/$mdm-submit)  
[http://example.com/Practitioner/$mdm-submit](http://example.com/Practitioner/$mdm-submit)
The following request body could be used:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "criteria",
    "valueString": "birthDate=2020-07-28"
  } ]
}

```

Copy
This operation returns the number of resources that were submitted for MDM processing. The following is a sample response:
```
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "submitted",
    "valueDecimal": 5
  } ]
}

```

Copy
This operation can also be done at the Instance level. When this is the case, the operations accepts no parameters. The following are examples of Instance level POSTs, which require no parameters. [http://example.com/Patient/123/$mdm-submit](http://example.com/Patient/123/$mdm-submit)  
[http://example.com/Practitioner/456/$mdm-submit](http://example.com/Practitioner/456/$mdm-submit)
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
6.3 MDM Operations 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
[ 6.4 MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)