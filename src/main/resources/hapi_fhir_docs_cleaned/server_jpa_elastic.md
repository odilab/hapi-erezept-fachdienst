---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html
crawled: 2025-08-01T14:05:13.332660
---

# Server Jpa Elastic

#  5.11.1HAPI FHIR JPA Lucene/Elasticsearch Indexing
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#hapi-fhir-jpa-luceneelasticsearch-indexing)
HAPI FHIR supports additional indexing when an appropriate indexer is configured. There are two FullText indexers supported: Lucene, and ElasticSearch. These systems do not replace a relational database (RDBMS), which is always required in order to use HAPI FHIR JPA. Instead, they augment the relational database by enabling additional search functionality.
With FullText Indexing enabled, the following Search Parameters may be used:
  * `_content` – Supports Full-Text searching across the textual components in a FHIR resource (i.e. the values of any **String** datatypes found within the resource).
  * `_text` – Supports Full-Text searching across the HTML narrative of the results.


#  5.11.2Performing Fulltext Search in Lucene/Elasticsearch
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#performing-fulltext-search-in-luceneelasticsearch)
When enabled, searches for `_text` and `_content` are forwarded to the underlying Hibernate Search engine, which can be backed by either Elasticsearch or Lucene. By default, search is supported in the way indicated in the [FHIR Specification on _text/_content Search](https://www.hl7.org/fhir/search.html#_text). This means that queries like the following can be evaluated:
```
GET [base]/Observation?_content=cancer OR metastases OR tumor

```

Copy
To understand how this works, look at the following example. During ingestion, the fields required for `_content` and `_text` searches are stored in the backing engine, after undergoing normalization and analysis. For example consider this Observation:
```
{
  "resourceType" : "Observation",
  "code" : {
    "coding" : [{
      "system" : "http://loinc.org",
      "code" : "15074-8",
      "display" : "Glucose [Moles/volume] in Blood Found during patient's visit!"
    }]
  }
  "valueQuantity" : {
    "value" : 6.3,
    "unit" : "mmol/l",
    "system" : "http://unitsofmeasure.org",
    "code" : "mmol/L"
  }
}

```

Copy
In the display section, once parsed and analyzed, will result in the followings tokens being generated to be able to be searched on:
```
["glucose", "mole", "volume", "blood", "found", "during", "patient", "visit"]

```

Copy
You will notice that plurality is removed, and the text has been normalized, and special characters removed. When searched for, the search terms will be normalized in the same fashion.
However, the default implementation will not allow you to search for an exact match over a long string that contains special characters or other characters which could be broken apart during tokenization. E.g. an exact match for `_content=[Moles/volume]` would not return this result.
In order to perform such an exact string match in Lucene/Elasticsearch, you should modify the `_text` or `_content` Search Parameter with the `:contains` modifier, as follows:
```
GET [base]/Observation?_content:contains=[Moles/volume]

```

Copy
Using `:contains` on the `_text` or `_content` modifies the search engine to perform a direct substring match anywhere within the field.
#  5.11.3Customizing Content and Text Indexing
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#customizing-fulltext)
By default, HAPI FHIR JPA will index the two FullText parameters using the following algorithm.
  * For all resources, the `_content` parameter is indexed by extracting all of the **string** content (i.e. the values of all string datatypes within the resource) and aggregating them into a single document which is passed to the indexer.
  * For all domain resources (all resources with a narrative), the `_text` parameter is indexed by aggregating all non-tag text in the resource narrative into a document which is passed to the indexer.


#  5.11.4Selectively Enabling Content and Text Indexing
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#selective-enabling)
You can selectively enable indexing and allowing the `_content` and/or `_text` parameters at the resource level by manually uploading a SearchParameter which explicitly specifies the resources which should be indexed.
Note that for this approach to work correctly:
  * The canonical URL (SearchParameter.url) value must match the examples below exactly.
  * The status (SearchParameter.status) value must be set to `active`.


##  5.11.4.1Example: Selective Content Indexing[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#ex_selective_content_indexing)
The following example shows a SearchParameter which limits the `_content` SearchParameter to indexing only specific resource types:
```
{
  "resourceType": "SearchParameter",
  "id": "Resource-content",
  "url": "http://hl7.org/fhir/SearchParameter/Resource-content",
  "name": "_content",
  "status": "active",
  "code": "_content",
  "base": [ "Observation", "Patient" ],
  "type": "string",
  "processingMode": "normal"
}

```

Copy
##  5.11.4.2Example: Selective Text Indexing[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#ex_selective_text_indexing)
The following example shows a SearchParameter which limits the `_text` SearchParameter to indexing only specific resource types:
```
{
  "resourceType": "SearchParameter",
  "id": "Resource-text",
  "url": "http://hl7.org/fhir/SearchParameter/DomainResource-text",
  "name": "_text",
  "status": "active",
  "code": "_text",
  "base": [ "Observation", "Patient" ],
  "type": "string",
  "processingMode": "normal"
}

```

Copy
##  5.11.4.3Example: Completely Disable Parameter[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#ex_completely_disable_individual)
If you want to completely disable Content indexing (i.e. indexing the `_content` Search Parameter), you can set the status to `retired`:
```
{
  "resourceType": "SearchParameter",
  "id": "Resource-content",
  "url": "http://hl7.org/fhir/SearchParameter/Resource-content",
  "name": "_content",
  "status": "retired",
  "code": "_content",
  "base": [ "Resource" ],
  "type": "string",
  "processingMode": "normal"
}

```

Copy
If you want to completely disable Text indexing (i.e. indexing the `_text` Search Parameter) you can set the status to `retired`:
```
{
    "resourceType": "SearchParameter",
    "id": "Resource-text",
    "url": "http://hl7.org/fhir/SearchParameter/DomainResource-text",
    "name": "_text",
    "status": "retired",
    "code": "_text",
    "base": [ "Resource" ],
    "type": "string",
    "processingMode": "normal"
}

```

Copy
#  5.11.5Customizing Content and Text Indexing by Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#fulltext-interceptor)
Using an interceptor with the [JPA_INDEX_EXTRACT_FULLTEXT_CONTENT](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#JPA_INDEX_EXTRACT_FULLTEXT_CONTENT) and/or [JPA_INDEX_EXTRACT_FULLTEXT_TEXT](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/interceptor/api/Pointcut.html#JPA_INDEX_EXTRACT_FULLTEXT_TEXT) pointcuts, it is possible to customize the indexed documents, or to selectively disable indexing entirely.
These pointcuts use an object of type [FullTextExtractionRequest](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-searchparam/ca/uhn/fhir/jpa/searchparam/fulltext/FullTextExtractionRequest.html) as input, and an object of type [FullTextExtractionResponse](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-searchparam/ca/uhn/fhir/jpa/searchparam/fulltext/FullTextExtractionResponse.html) as output. The _JPA_INDEX_EXTRACT_FULLTEXT_CONTENT_ pointcut customizes indexing for the `_content` SearchParameter, and the _JPA_INDEX_EXTRACT_FULLTEXT_TEXT_ pointcut customizes indexing for the `_text` SearchParameter.
##  5.11.5.1Example[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#example)
The following example shows an interceptor which enables full-text indexing only for specific resources, and controls which parts of resources are indexed:
```
/**
 * This interceptor demonstrates how to customize full-text indexing. It
 * implements a fairly contrived set of rules, intended to demonstrate
 * possibilities:
 * <ul>
 * <li>
 *     Observations with an <code>Observation.category</code> code of
 *     "vital-signs" will not be full-text indexed.
 * </li>
 * <li>
 *     Observations with an <code>Observation.value</code> type of
 *     "string" will only have the string value indexed, and no other
 *     strings in the resource will be indexed.
 * </li>
 * <li>
 *     All other resource types are indexed normally.
 * </li>
 * </ul>
 */
@Interceptor
public class FullTextSelectiveIndexingInterceptor {

   /**
    * Override the default behaviour for the <code>_content</code>
    * SearchParameter indexing.
    */
   @Hook(Pointcut.JPA_INDEX_EXTRACT_FULLTEXT_CONTENT)
   public FullTextExtractionResponse indexPayload(FullTextExtractionRequest theRequest) {
      IBaseResource resource = theRequest.getResource();
      if (resource instanceof Observation observation) {

         // Do not full-text index vital signs
         if (isVitalSignsObservation(observation)) {
            return FullTextExtractionResponse.doNotIndex();
         }

         // If Observation.value[x] has a string datatype, we'll index only
         // the value of that string and ignore any other text in the resource
         if (observation.hasValueStringType()) {
            String stringValue = observation.getValueStringType().getValue();
            return FullTextExtractionResponse.indexPayload(stringValue);
         }
      }

      // For all other resources, index normally
      return FullTextExtractionResponse.indexNormally();
   }

   /**
    * Returns {@literal true} if the Observation has the vital-signs category
    */
   private boolean isVitalSignsObservation(Observation theObservation) {
      for (var category : theObservation.getCategory()) {
         for (var coding : category.getCoding()) {
            if ("http://hl7.org/fhir/codesystem/Observation-category".equals(coding.getSystem())) {
               if ("vital-signs".equals(coding.getCode())) {
                  return true;
               }
            }
         }
      }
      return false;
   }
}

```

Copy
#  5.11.6Experimental Extended Lucene/Elasticsearch Indexing
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#experimental-extended-luceneelasticsearch-indexing)
Additional indexing is implemented for simple search parameters of type token, string, and reference. These implement the basic search, as well as several modifiers: This **experimental** feature is enabled via the `setAdvancedHSearchIndexing()` property of JpaStorageSettings.
##  5.11.6.1Search Parameter Support[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#search-parameter-support)
Extended Lucene Indexing supports all of the [core search parameter types](https://www.hl7.org/fhir/search.html). These include:
  * Number
  * Date/DateTime
  * String
  * Token
  * Reference
  * Composite
  * Quantity
  * URI


##  5.11.6.2Date search[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#date-search)
We support date searches using the eq, ne, lt, gt, ge, and le comparisons.  
See https://www.hl7.org/fhir/search.html#date.
##  5.11.6.3String search[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#string-search)
The Extended Lucene string search indexing supports the default search, as well as `:contains`, `:exact`, and `:text` modifiers.
  * The default (unmodified) string search matches by prefix, insensitive to case or accents.
  * `:exact` matches the entire string, matching case and accents.
  * `:contains` match any substring of the text, ignoring case and accents.
  * `:text` provides a rich search syntax as using a [modified Simple Query Syntax](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#modified-simple-query-syntax).


See https://www.hl7.org/fhir/search.html#string.
##  5.11.6.4Modified Simple Query Syntax[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#modified-simple-query-syntax)
The `:text` modifier for token and string uses a modified version of the Simple Query Syntax provided by [Lucene](https://lucene.apache.org/core/8_10_1/queryparser/org/apache/lucene/queryparser/simple/SimpleQueryParser.html) and [Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html#simple-query-string-syntax). Terms are delimited by whitespace, or query punctuation `"'()|+`.  
Literal uses of these characters must be escaped by `&#92;`. If the query contains any SQS query punctuation, the query is treated as a normal SQS query. But when the query only contains one or more bare terms, and does not use any query punctuation, a modified syntax is used. In modified syntax, each search term is converted to a prefix search to match standard FHIR string searching behaviour. When multiple terms are present, they must all match (i.e. `AND`). For `OR` behaviour use the `|` operator between terms. To match only whole words, but not match by prefix, quote bare terms with the `"` or `'` characters.
Examples:
Fhir Query String | Executed Query | Matches | No Match | Note  
---|---|---|---|---  
Smit | Smit* | John Smith | John Smi |   
Jo Smit | Jo* Smit* | John Smith | John Frank | Multiple bare terms are `AND`  
frank | john | frank | john | Frank Smith | Franklin Smith | SQS characters disable prefix wildcard  
'frank' | 'frank' | Frank Smith | Franklin Smith | Quoted terms are exact match  
##  5.11.6.5Token search[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#token-search)
The Extended Lucene Indexing supports the default token search by code, system, or system+code, as well as with the `:text` modifier. The `:text` modifier provides the same [modified Simple Query Syntax](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#modified-simple-query-syntax) used by string `:text` searches.  
See https://www.hl7.org/fhir/search.html#token.
##  5.11.6.6Supported Common and Special Search Parameters[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#supported-common-and-special-search-parameters)
Parameter | Supported | type  
---|---|---  
_id | no |   
_lastUpdated | yes | date  
_tag | yes | token  
_profile | yes | URI  
_security | yes | token  
_text | yes | string(R4) special(R5)  
_content | yes | string(R4) special(R5)  
_list | no |   
_has | no |   
_type | no |   
_source | yes | URI  
##  5.11.6.7ValueSet autocomplete extension[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#valueset-autocomplete-extension)
The Extended Lucene Indexing supports an extension of the `$expand` operation on ValueSet with a new `contextDirection` value of `existing`. In this mode, the `context` parameter is interpreted as a SearchParameter reference (by resource type and code), and the `filter` is interpreted as a query token. The expansion will contain the most frequent `Coding` values matching the filter. E.g. the query
```
GET /ValueSet/$expand?contextDirection=existing&context=Observation.code:text&filter=press

```

will return a ValueSet containing the most common values indexed under `Observation.code` whose display text contains a word starting with "press", such as `http://loinc.org|8478-0` - "Mean blood pressure". This extension is only valid at the type level, and requires that Extended Lucene Indexing be enabled.
##  5.11.6.8Resource Storage[](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#resource-storage)
As an experimental feature with the extended indexing, the full resource can be stored in the search index. This allows some queries to return results without using the relational database. Note: This does not support the $meta-add or $meta-delete operations. Full reindexing is required when this option is enabled after resources have been indexed.
This **experimental** feature is enabled via the `setStoreResourceInHSearchIndex()` option of JpaStorageSettings.
#  5.11.7Synchronous Writes
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#synchronous-writes)
ElasticSearch writes are asynchronous by default. This means that when writing to an ElasticSearch instance (independent of HAPI FHIR), the data you write will not be available to subsequent reads for a short period of time while the indexes synchronize.
ElasticSearch states that this behaviour leads to better overall performance and throughput on the system.
This can cause issues, particularly in unit tests where data is being examined shortly after it is written.
You can force synchronous writing to them in HAPI FHIR JPA by setting the Hibernate Search [synchronization strategy](https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#mapper-orm-indexing-automatic-synchronization). This setting is internally setting the ElasticSearch [refresh=wait_for](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-refresh.html) option. Be warned that this will have a negative impact on overall performance. THE HAPI FHIR TEAM has not tried to quantify this impact but the ElasticSearch docs seem to make a fairly big deal about it.
#  5.11.8Sorting
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#sorting)
It is possible to sort with Lucene indexing and full text searching enabled. For example, this will work: `Practitioner?_sort=family`.
Also, chained sorts will work: `PractitionerRole?_sort=practitioner.family`.
However, chained sorting _combined_ with full text searches will fail. For example, this query will fail with an error: `PractitionerRole?_text=blah&_sort=practitioner.family`
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html)
5.11 Lucene/Elasticsearch Indexing 
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
[ 5.12 Terminology ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)