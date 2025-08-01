---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html
crawled: 2025-08-01T14:05:16.492777
---

# Server Jpa Lastn

#  5.10.1LastN Operation
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html#lastn-operation)
HAPI FHIR 5.1.0 introduced preliminary support for the `$lastn` operation described [here](http://hl7.org/fhir/observation-operation-lastn.html).
This implementation of the `$lastn` operation requires an external Elasticsearch server implementation which is used to implement the indexes required by this operation. The following sections describe the current functionality supported by this operation and the configuration needed to enable this operation.
#  5.10.2Functional Overview and Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html#functional-overview-and-parameters)
As described in the [FHIR specification](http://hl7.org/fhir/observation-operation-lastn.html), the `$lastn` can be used to retrieve the most recent or last n=number of observations for one or more subjects. This implementation supports the following search parameters:
  * `subject=` or `patient=`: Identifier(s) of patient(s) to return Observation resources for. If not specified, returns most recent observations for all patients.
  * `category=`: One or more category code search parameters used to filter Observations.
  * `Observation.code=`: One or more `Observation.code` search parameters use to filter and group observations. If not specified, returns most recent observations for all `Observation.code` values.
  * `date=`: Date search parameters used to filter Observations by `Observation.effectiveDtm`.
  * `max=`: The maximum number of observations to return for each `Observation.code`. If not specified, returns only the most recent observation in each group.


#  5.10.3Limitations
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html#limitations)
Currently only Elasticsearch version 7.10.0 is officially supported.
Search parameters other than those listed above are currently not supported.
The grouping of Observation resources by `Observation.code` means that the `$lastn` operation will not work in cases where `Observation.code` has more than one coding.
#  5.10.4Deployment and Configuration
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html#deployment-and-configuration)
The `$lastn` operation is disabled by default. The operation can be enabled by setting the JpaStorageSettings#setLastNEnabled property ( see [JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/JpaStorageSettings.html#setLastNEnabled\(boolean\))) .
In addition, the Elasticsearch client service, `ElasticsearchSvcImpl` will need to be instantiated with parameters specifying how to connect to the Elasticsearch server, for e.g.:
```
  @Bean()
  public ElasticsearchSvcImpl elasticsearchSvc() {
     String elasticsearchHost = "localhost:9200";
     String elasticsearchUsername = "elastic";
     String elasticsearchPassword = "changeme";

     return new ElasticsearchSvcImpl(elasticsearchHost, elasticsearchUsername, elasticsearchPassword);
  }

```

Copy
The Elasticsearch client service requires that security be enabled in the Elasticsearch clusters, and that an Elasticsearch user be available with permissions to create an index and to index, update and delete documents as needed.
See the [JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/ca/uhn/fhir/jpa/search/lastn/IElasticsearchSvc.html) for more information regarding the Elasticsearch client service.
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/diff.html)
5.10 LastN Operation 
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
[ 5.11 Lucene/Elasticsearch Indexing ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)