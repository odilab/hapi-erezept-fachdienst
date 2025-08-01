---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html
crawled: 2025-08-01T14:05:45.478530
---

# Server Jpa Configuration

#  5.5.1JPA Server Configuration Options
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#jpa-server-configuration-options)
##  5.5.1.1External/Absolute Resource References[](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#externalabsolute-resource-references)
Clients may sometimes post resources to your server that contain absolute resource references. For example, consider the following resource:
```
<Patient xmlns="http://hl7.org/fhir">
   <id value="patient-infant-01"/>
   <name>
      <use value="official"/>
      <family value="Miller"/>
      <given value="Samuel"/>
   </name>
   <managingOrganization>
      <reference value="http://example.com/fhir/Organization/123"/>
   </managingOrganization>
</Patient>

```

Copy
By default, the server will reject this reference, as only local references are permitted by the server. This can be changed however.
If you want the server to recognize that this URL is actually a local reference (i.e. because the server will be deployed to the base URL `http://example.com/fhir/`) you can configure the server to recognize this URL via the following JpaStorageSettings setting:
```
@Bean
public JpaStorageSettings storageSettings() {
   JpaStorageSettings retVal = new JpaStorageSettings();
	// ... other config ...
	retVal.getTreatBaseUrlsAsLocal().add("http://example.com/fhir/");
	return retVal;
}

```

Copy
On the other hand, if you want the server to be configurable to allow remote references, you can set this with the configuration below. Using the `setAllowExternalReferences` means that it will be possible to search for references that refer to these external references.
```
@Bean
public JpaStorageSettings storageSettings() {
   JpaStorageSettings retVal = new JpaStorageSettings();
	// Allow external references
	retVal.setAllowExternalReferences(true);
	
	// If you are allowing external references, it is recommended to
	// also tell the server which references actually will be local
	retVal.getTreatBaseUrlsAsLocal().add("http://mydomain.com/fhir");
	return retVal;
}

```

Copy
##  5.5.1.2Logical References[](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#logical-references)
In some cases, you may have references which are _Logical References_ , which means that they act as an identifier and not necessarily as a literal web address.
A common use for logical references is in references to conformance resources, such as ValueSets, StructureDefinitions, etc. For example, you might refer to the ValueSet `http://hl7.org/fhir/ValueSet/quantity-comparator` from your own resources. In this case, you are not necessarily telling the server that this is a real address that it should resolve, but rather that this is an identifier for a ValueSet where `ValueSet.url` has the given URI/URL.
HAPI can be configured to treat certain URI/URL patterns as logical by using the JpaStorageSettings#setTreatReferencesAsLogical property ( see [JavaDoc](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#setTreatReferencesAsLogical\(java.util.Set\))) .
For example:
```
// Treat specific URL as logical
myStorageSettings.getTreatReferencesAsLogical().add("http://mysystem.com/ValueSet/cats-and-dogs");

// Treat all references with given prefix as logical
myStorageSettings.getTreatReferencesAsLogical().add("http://mysystem.com/mysystem-vs-*");

```

Copy
##  5.5.1.3Referential Integrity[](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#referential-integrity)
Enabling referential integrity will ensure that reference values exist in the database. If the referenced entity does not exist, the server will return an error.
It is important to note that referential integrity is not enforced on database-level. The referential integrity check _only_ validates references that are indexed by a `SearchParameter`.
5.5.1.3.1Enabling Referential Integrity Referential integrity can be configured on two levels: `write` and `delete`. 5.5.1.3.1.1JPA Server ```
@Bean
public JpaStorageSettings storageSettings() {
   JpaStorageSettings retVal = new JpaStorageSettings();
	// ... other config ...
	retVal.setEnforceReferentialIntegrityOnWrite(true);
	retVal.setEnforceReferentialIntegrityOnDelete(true);
	return retVal;
}

```
Copy 5.5.1.3.1.2JPA Server Starter This can be easily enabled in the `application.yaml` file at the following paths: ```
hapi:
   fhir:
    enforce_referential_integrity_on_write: true
    enforce_referential_integrity_on_delete: true

```
Copy
#  5.5.2Search Result Caching
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#search-result-caching)
By default, search results will be cached for one minute. This means that if a client performs a search for `Patient?name=smith` and gets back 500 results, if a client performs the same search within 60000 milliseconds the previously loaded search results will be returned again. This also means that any new Patient resources named "Smith" within the last minute will not be reflected in the results.
Under many normal scenarios this is a n acceptable performance tradeoff, but in some cases it is not. If you want to disable caching, you have two options:
5.5.2.0.1Globally Disable / Change Caching Timeout You can change the global cache using the following setting: ```
myStorageSettings.setReuseCachedSearchResultsForMillis(null);

```
Copy 5.5.2.0.2Disable Cache at the Request Level Clients can selectively disable caching for an individual request using the Cache-Control header: ```
Cache-Control: no-cache

```
Copy 5.5.2.0.3Disable Paging at the Request Level If the client knows that they will only want a small number of results (for example, a UI containing 20 results is being shown and the client knows that they will never load the next page of results) the client may also use the `no-store` directive along with a HAPI FHIR extension called `max-results` in order to specify that only the given number of results should be fetched. This directive disabled paging entirely for the request and causes the request to return immediately when the given number of results is found. This can cause a noticeable performance improvement in some cases. ```
Cache-Control: no-store, max-results=20

```
Copy
#  5.5.3Additional Information
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#additional-information)
  * [This page](https://www.openhealthhub.org/t/hapi-terminology-server-uk-snomed-ct-import/592) has information on loading national editions (UK specifically) of SNOMED CT files into the database.


#  5.5.4Cascading Deletes
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#cascading-deletes)
An interceptor called `CascadingDeleteInterceptor` may be registered against the server. When this interceptor is enabled, cascading deletes may be performed using either of the following:
  * The request may include the following parameter: `_cascade=delete`
  * The request may include the following header: `X-Cascade: delete`


#  5.5.5Version Conflicts
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#retry-on-version-conflict)
If a server is serving multiple concurrent requests against the same resource, a [ResourceVersionConflictException](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/server/exceptions/ResourceVersionConflictException.html) may be thrown (resulting in an **HTTP 409 Version Conflict** being returned to the client). For example, if two client requests attempt to update the same resource at the exact same time, this exception will be thrown for one of the requests. This exception is not a bug in the server itself, but instead is a defense against client updates accidentally being lost because of concurrency issues. When this occurs, it is important to consider what the root cause might be, since concurrent writes against the same resource are often indicative of a deeper application design issue.
An interceptor called `UserRequestRetryVersionConflictsInterceptor` may be registered against the server. When this interceptor is enabled, requests may include an optional header requesting for the server to try to avoid returning an error due to concurrent writes. The server will then try to avoid version conflict errors by automatically retrying requests that would have otherwise failed due to a version conflict.
With this interceptor in place, the following header can be added to individual HTTP requests to instruct the server to avoid version conflict errors:
```
X-Retry-On-Version-Conflict: retry; max-retries=100

```

Copy
#  5.5.6Controlling Delete with Expunge size
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#controlling-delete-with-expunge-size)
Delete with expunge submits a job to delete and expunge the requested resources. This is done in batches. If the DELETE ?_expunge=true syntax is used to trigger the delete expunge, then the batch size will be determined by the value of [Expunge Batch Size](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/JpaStorageSettings.html#getExpungeBatchSize\(\)) property.
#  5.5.7Disabling Non Resource DB History
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#disabling-non-resource-db-history)
This setting controls whether MdmLink and any other non-resource (ex: Patient is a FHIR resource, MdmLink is not) DB history is enabled. Presently, this only affects the history for MDM links, but the functionality may be extended to other domains.
Clients may want to disable this setting for performance reasons as it populates a new set of database tables when enabled.
Setting this property explicitly to false disables the feature: [Non Resource DB History](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/JpaStorageSettings.html#isNonResourceDbHistoryEnabled\(\))
#  5.5.8Prevent Conditional Updates to Invalidate Match Criteria
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#prevent-conditional-updates-to-invalidate-match-criteria)
JPA Server prevents conditional updated to invalidate match criteria for first version of resources. This setting, disabled by default, allows to configure the same behaviour for later versions.
Setting this property explicitly to true enables the feature: [Prevent Conditional Updates Invalidating Match Criteria](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/JpaStorageSettings.html#isPreventInvalidatingConditionalMatchCriteria\(\))
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html)
5.5 Configuration 
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
[ 5.6 Search ](https://hapifhir.io/hapi-fhir/docs/server_jpa/search.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)