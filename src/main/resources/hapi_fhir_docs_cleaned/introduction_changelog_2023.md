---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html
crawled: 2025-08-01T14:06:05.338553
---

# Introduction Changelog 2023

#  0.3.1Changelog: 2023
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changelog-2023)
#  0.3.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changelog)
#  0.3.3HAPI FHIR 6.8.6 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#hapi-fhir-686-yucat%C3%A1n)
##  0.3.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information)
**Released:** 2023-12-06
**Codename:** (Yucatán)
##  0.3.3.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions)
Fixed migration related issues
##  0.3.3.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes)
|  [#5486](https://github.com/hapifhir/hapi-fhir/issues/5486) |  Previously, testing database migration with cli migrate-database command in dry-run mode would insert in the migration task table. The issue has been fixed.  
---|---|---  
|  [#5511](https://github.com/hapifhir/hapi-fhir/issues/5511) |  Previously, when creating an index as a part of a migration, if the index already existed with a different name on Oracle, the migration would fail. This has been fixed so that the create index migration task now recovers with a warning message if the index already exists with a different name.  
#  0.3.4HAPI FHIR 6.8.5 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.8.6-5486)
##  0.3.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-1)
**Released:** 2023-10-20
**Codename:** (Yucatán)
##  0.3.4.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-1)
##  0.3.4.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-1)
|  [#5377](https://github.com/hapifhir/hapi-fhir/issues/5377) |  Subscription triggering via the `$trigger-subscription` operation is now multi-threaded, which significantly improves performance for large data sets.  
---|---|---  
#  0.3.5HAPI FHIR 6.8.4 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.8.5-5377)
##  0.3.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-2)
**Released:** 2023-10-18
**Codename:** (Yucatán)
##  0.3.5.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-2)
##  0.3.5.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-2)
|  [#5353](https://github.com/hapifhir/hapi-fhir/issues/5353) |  Previously, when using revincludes and includes with iterate, while also using revincludes without iterate, the result omitted some resources that should have been included. This issue has now been fixed.  
---|---|---  
#  0.3.6HAPI FHIR 6.8.3 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.8.4-5353)
##  0.3.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-3)
**Released:** 2023-09-22
**Codename:** (Yucatán)
##  0.3.6.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-3)
This release permits you to change the severity of a coding display mismatch error during validation. This also fixes a regression which was causing Bulk Export `_exportId` to not be respected during blob prefixing.
##  0.3.6.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-3)
|  [#5271](https://github.com/hapifhir/hapi-fhir/issues/5271) |  The error messages returned in an OperationOutcome when validating terminology codes as a part of resource profile validation have been improved. Machine processable location (line/col) information is now available through a pair of dedicated extensions, and error messages such as UCUM parsing issues are now returned to the client (previously they were swallowed and a generic error message was returned).  
---|---|---  
|  [#5321](https://github.com/hapifhir/hapi-fhir/issues/5321) |  It is now possible to configure the strictness of concept display name validation using a new flag on the InMemoryTerminologyServerValidationSupport (for non-JPA validation) and JpaStorageSettings (for JPA validation). In addition, the error messages emitted by the validator when a concept display doesn't match have been improved to be much more useful.  
|  [#5333](https://github.com/hapifhir/hapi-fhir/issues/5333) |  A regression was introduced in 2023.08.R01 which caused binary storage prefixes to not be applied to exported binary blobs. This has been fixed.  
#  0.3.7HAPI FHIR 6.8.2 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.8.3-5271)
##  0.3.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-4)
**Released:** 2023-09-05
**Codename:** (Yucatán)
##  0.3.7.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-4)
Note: this release has been decommissioned, and you should not be using it. Please upgrade to a newer version.
##  0.3.7.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-4)
#  0.3.8HAPI FHIR 6.8.1 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#hapi-fhir-681-yucat%C3%A1n)
##  0.3.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-5)
**Released:** 2023-08-31
**Codename:** (Yucatán)
##  0.3.8.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-5)
Note: this release has been decommissioned, and you should not be using it. Please upgrade to a newer version.
##  0.3.8.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-5)
#  0.3.9HAPI FHIR 6.8.0 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#hapi-fhir-680-yucat%C3%A1n)
##  0.3.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-6)
**Released:** 2023-08-18
**Codename:** (Yucatán)
##  0.3.9.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-6)
Users of the `Resource.meta.source` field, as well as users of the `_source` parameter should perform a global $reindex after upgrading to this version of HAPI FHIR with the following parameters:
```
[base]/$reindex?reindexSearchParameters=false&optimizeStorage=ALL_VERSIONS

```

Copy
The previous mechanism for storing and indexing these parameters is inefficient and will be replaced in a future release of HAPI FHIR. Performing this reindex operation ensures that existing data will continue to be searchable.
##  0.3.9.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-6)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate ORM (JPA): 5.6.12.Final -> 5.6.15.Final

  
---|---|---  
|  [#4380](https://github.com/hapifhir/hapi-fhir/issues/4380) |  The `@Interceptor` annotation can now be placed at the method level. This is used only as a marker, and does not change the behaviour or interceptors in any way. Thanks to Dominique Villard for the pull request!  
|  [#4570](https://github.com/hapifhir/hapi-fhir/issues/4570) |  The batch2 framework now supports warning messages.  
|  [#4787](https://github.com/hapifhir/hapi-fhir/issues/4787) |  A new interceptor called BalpAuditCaptureInterceptor has been added. This interceptor automatically generates AuditEvent resources in a HAPI FHIR server that are conformant with the [IHE BasicAudit Log Patterns](https://profiles.ihe.net/ITI/BALP/) IG.  
|  [#4838](https://github.com/hapifhir/hapi-fhir/issues/4838) |  When invoking the instance level `$reindex` and `$reindex-dryrun` operations, the resulting status message and any warnings are now included in the response Parameters object as well as in the generated response HTML narrative.  
|  [#4838](https://github.com/hapifhir/hapi-fhir/issues/4838) |  When performing a resource reindex on a deleted resource, any search index rows will now be deleted. Deleting a resource should generally not leave any such rows behind, but they can be left if the resource is manually deleted using SQL directly against the database and in this case the reindex job will now clean up these unwanted rows.  
|  [#4911](https://github.com/hapifhir/hapi-fhir/issues/4911) |  Added a new SubscriptionTopicDispatcher service for use by java extensions that need to dispatch their own subscription topic notifications  
|  [#4913](https://github.com/hapifhir/hapi-fhir/issues/4913) |  Added support for Topic Subscription Filters. In R4B and R5, SubscriptionTopic notifications will interpret subscription filters as FHIR Search Parameters and perform an in-memory match of the focus resource against the search parameters. For R4 or user-generated event notifications, the caller can specify a custom implementation of ISubscriptionTopicFilterMatcher on the call to SubscriptionTopicDispatcher.dispatch() to provide custom filter matching logic.  
|  [#4913](https://github.com/hapifhir/hapi-fhir/issues/4913) |  Added support for Subscriptions R5 Backport to R4 as documented in http://build.fhir.org/ig/HL7/fhir-subscription-backport-ig/components.html.  
|  [#4931](https://github.com/hapifhir/hapi-fhir/issues/4931) |  The `$delete-expunge` operation has a new parameter `cascade` that can be used to request that resources with indexed references to other resources being deleted should also be deleted.  
|  [#4937](https://github.com/hapifhir/hapi-fhir/issues/4937) |  The SQL schema migrator now returns a status flag indicating whether the schema was initialized or not.  
|  [#5003](https://github.com/hapifhir/hapi-fhir/issues/5003) |  The IParser interface now has an additional pair of methods called `encodeToString(IBase)` and `encodeToWriter(IBase, Writer)` that can be used to encode fragments of resources, such as datatypes or infrastructure elements.  
|  [#5009](https://github.com/hapifhir/hapi-fhir/issues/5009) |  Previously, all revincludes would always be evaluated before all includes. Now the system checks if any revincludes are recursive (i.e. are modified :iterate) and if so, it evaluates all the includes first.  
|  [#5014](https://github.com/hapifhir/hapi-fhir/issues/5014) |  It is no longer mandatory to provide a partition ID when executing the `$partition-management-create-partition` operation. If you omit the partition ID, one will be randomly selected from the available pool of values. Note that this may incur a small performance cost when creating partitions.  
|  [#5014](https://github.com/hapifhir/hapi-fhir/issues/5014) |  There was a bug with automatic partition ID selection where it was not bound to positive integers and could return a value that was out of range. This has been fixed.  
|  |  A new configuration option, `JpaStorageSettings#setOnlyAllowInMemorySubscriptions(boolean)` can now prevent the creation of Subscriptions that would need to be matched in the database. If enabled, only Subscriptions which can be evaluated IN-MEMORY are permitted to be created.  
|  [#5052](https://github.com/hapifhir/hapi-fhir/issues/5052) |  A new interceptor called BinarySecurityInterceptor has been added. This interceptor can be used to enforce access to Binary resources by using values in the Binary.securityContext element.  
|  [#5058](https://github.com/hapifhir/hapi-fhir/issues/5058) |  Added infrastructure to allow consumers to define MDM block rules based on fhir path and specific values. To utilize this feature, an IBlockListRuleProvider must be wired up with the required rules json.  
|  [#5061](https://github.com/hapifhir/hapi-fhir/issues/5061) |  Added a utility method to the TerserUtil class to facilitate the creation of new instances of BackboneElement classes.  
|  [#5071](https://github.com/hapifhir/hapi-fhir/issues/5071) |  Previously, calls to the Fulltext Search service (Lucene/Elasticsearch) did not invoker the `JPA_PERFTRACE_INFO`. Now, they invoke this pointcut with information of which query went out to the fulltext system.  
|  [#5080](https://github.com/hapifhir/hapi-fhir/issues/5080) |  Extended the existing MDM similarity algorithms to numeric values such that the input is normalized by removing all non-numeric characters from the string before the similarity algorithm is applied. This can be useful when wanting to measure similarity between identifying numbers or phone numbers where dashes or other special separating characters may be used.  
|  [#5083](https://github.com/hapifhir/hapi-fhir/issues/5083) |  The IFhirPath evaluator interface now has an additional overload of the `evaluate` method which takes in a parsed expression returned by the `parse` method. This can be used to improve performance in cases where the same expression is being used repeatedly.  
|  [#5083](https://github.com/hapifhir/hapi-fhir/issues/5083) |  A new SQL-like evaluator called the HAPI FHIR Query Language (HFQL) has been added.  
|  [#5095](https://github.com/hapifhir/hapi-fhir/issues/5095) |  Added support for :above, :below, :contains and :missing _source search parameter modifiers.  
|  [#5115](https://github.com/hapifhir/hapi-fhir/issues/5115) |  A new experimental SQL-like query syntax called HFQL (HAPI FHIR Query Language) has been added.  
|  [#5115](https://github.com/hapifhir/hapi-fhir/issues/5115) |  OpenAPI definitions were not working for R5 JPA servers. This has been corrected.  
|  [#5115](https://github.com/hapifhir/hapi-fhir/issues/5115) |  The Generic/Fluent client can now handle arbitrary (ie. non-FHIR) responses from $operation invocation by specifying a response resource type of Binary.  
|  [#5158](https://github.com/hapifhir/hapi-fhir/issues/5158) |  Added support for Subscription matching of ':above', ':below', ':contains' and ':missing' '_source' search parameter modifiers.  
|  [#4831](https://github.com/hapifhir/hapi-fhir/issues/4831) |  Conditional deletes that delete multiple resources at once have been optimized to perform fewer SQL select statements, which should improve performance on large deletes.  
|  [#4817](https://github.com/hapifhir/hapi-fhir/issues/4817) |  Introduce IBaseResource.isDeleted() method and convert code to use it. Add subscription_topic_troubleshooting log. No longer rely on ResourceGoneException to detect deleted subscription. Instead use the new isDeleted() method. Demote unexpected exceptions in HapiTransactionService from error to debug since these exceptions are expected e.g. when checking if a resource has been deleted by catching a ResourceGoneException  
|  [#5029](https://github.com/hapifhir/hapi-fhir/issues/5029) |  Searching for token parameters has been extended to systems and values of any length. Sorting by tokens is still limited to the first 200 characters each of the system and value. valid request.  
|  [#5039](https://github.com/hapifhir/hapi-fhir/issues/5039) |  Bulk export fetch operations now properly account for the partition being searched, which should improve performance in some cases on partitioned systems.  
|  [#5039](https://github.com/hapifhir/hapi-fhir/issues/5039) |  The class `BulkDataExportOptions` has been removed and replaced with an equivalent class called `BulkExportJobParameters`. This change is a breaking change unfortunately, but should be easily remedied as the replacement class has a very similar signature. This change reduces a large amount of duplication in the bulk export codebase and should make for more maintainable code in the future.  
|  [#5039](https://github.com/hapifhir/hapi-fhir/issues/5039) |  A new interceptor pointcut called `STORAGE_BULK_EXPORT_RESOURCE_INCLUSION` has been added. This pointcut is called for every resource that will be included in a bulk export data file. Hook methods may modify the resources or signal that they should be filtered out.  
|  [#5128](https://github.com/hapifhir/hapi-fhir/issues/5128) |  Previously, when running with mdm, the server automatically changed storage settings to enable message subscriptions. This has been changed to require the user to explicitly enable message subscriptions when running with mdm. If mdm is enabled and message subscriptions are not enabled, the server will now throw an exception on startup.  
|  [#4468](https://github.com/hapifhir/hapi-fhir/issues/4468) |  When performing a search using a plain server without a cache, pagination links could contain invalid offsets in the _prev_ link. Thanks to Aleksej Parovysnik for the pull request!  
|  [#4468](https://github.com/hapifhir/hapi-fhir/issues/4468) |  When parsing a FHIR resource which has multiple value[x] choice fields, the parser does not call the IParserErrorHandler#unexpectedRepeatingElement method on the configured parser error handler. Thanks to Max Bureck for the pull request!  
|  [#4783](https://github.com/hapifhir/hapi-fhir/issues/4783) |  FHIR Patch operations on choice types in nested expressions did not work correctly. Thanks to Zach Smith for the pull request!  
|  [#4787](https://github.com/hapifhir/hapi-fhir/issues/4787) |  HashMapResourceProvider invoked the `STORAGE_PRESTORAGE_xxx` and `STORAGE_PRECOMMIT_xxx` pointcuts after storing the resource into memory. This means that if any interceptors threw an exception in an attempt to abort the transaction, this did not actually result in the resource storage being aborted. This has been corrected.  
|  [#4799](https://github.com/hapifhir/hapi-fhir/issues/4799) |  There is an issue where searching on paramName '_profile' with modifier 'missing' will not provide the correct search result. This has been fixed.  
|  [#4813](https://github.com/hapifhir/hapi-fhir/issues/4813) |  Under heavy concurrency, a bug resulted in identical tag definitions being rejected with a `NonUniqueResultException` some of the time. This has been corrected.  
|  [#4831](https://github.com/hapifhir/hapi-fhir/issues/4831) |  When performing a FHIR transaction containing both a conditional delete as well as a conditional create/update for the same resource, the resource was left in an inconsistent state. This has been corrected. Thanks to Laxman Singh for raising this issue.  
|  [#4832](https://github.com/hapifhir/hapi-fhir/issues/4832) |  When serializing resources in JSON, if resource came from an XML-parsed resource that contained comments, the JSON serializer incorrectly created an empty object. This has been corrected.  
|  [#4838](https://github.com/hapifhir/hapi-fhir/issues/4838) |  When reindexing resources, deleted resources could incorrectly fail validation rules and cause the reindex job to not complete correctly. This has been corrected.  
|  [#4838](https://github.com/hapifhir/hapi-fhir/issues/4838) |  The BALP AsyncMemoryQueueBackedFhirClientBalpSink incorrectly used a non-blocking method to add events to the blocking queue, resulting in race conditions on a heavily loaded server.  
|  [#4838](https://github.com/hapifhir/hapi-fhir/issues/4838) |  Two failures in the $delete-expunge operation were fixed: 
  * Jobs could fail if hibernate search was loaded but not enabled.
  * Jobs could fail if the criteria included a `_lastUpdated=lt[date]` clause

  
|  [#4842](https://github.com/hapifhir/hapi-fhir/issues/4842) |  There was a bug with the TerserUtil, where it would not overwrite non-empty values with empty values from a source resource. This has been corrected. Thanks to @nigtrifork for the fix!  
|  [#4878](https://github.com/hapifhir/hapi-fhir/issues/4878) |  Batch jobs occasionaly reported zero (0) record processed counts. This has been corrected.  
|  [#4881](https://github.com/hapifhir/hapi-fhir/issues/4881) |  When _Index Contained References_ is enabled in the JPA server, Bundle resources could not be stored or indexed due to an incompatibility with the default Bundle search parameters. This has been corrected.  
|  [#4888](https://github.com/hapifhir/hapi-fhir/issues/4888) |  Previously, it was possible to create composite SP with any types of SP as components. This has been fixed by limiting the component SP types to String, Token, Date, or Quantity.  
|  [#4896](https://github.com/hapifhir/hapi-fhir/issues/4896) |  The _lastUpdated query parameter is no longer applied to _include or _revinclude search results.  
|  [#4917](https://github.com/hapifhir/hapi-fhir/issues/4917) |  Matching algorithms have been refactored to allow greater flexibility in setting and defining nicknames as well as allowing bean injection into matcher classes.  
|  [#4922](https://github.com/hapifhir/hapi-fhir/issues/4922) |  R5 Subscription.filterBy.resourceType failed to deserialize because the deserializer skipped all elements named 'resourceType'. This has been changed so that only toplevel resourceType elements are skipped in the deserialization process.  
|  [#4934](https://github.com/hapifhir/hapi-fhir/issues/4934) |  Previously, installing or uninstalling an implementation guide does not refresh the validation cache. This problem has been fixed.  
|  [#4947](https://github.com/hapifhir/hapi-fhir/issues/4947) |  Previously, logging level on specific services was set too high. This has been modified.  
|  [#4952](https://github.com/hapifhir/hapi-fhir/issues/4952) |  Previously, the resource server Id strategy was not honoured when doing a create with a conditional update operation on a resource where the ID was not assigned. This is now fixed.  
|  [#4957](https://github.com/hapifhir/hapi-fhir/issues/4957) |  Previously, performing a $validate-code operation with a remote terminology service on an invalid CodeSystem or ValueSet returned 500. This problem has been fixed.  
|  [#4959](https://github.com/hapifhir/hapi-fhir/issues/4959) |  Previously, set synchronous search size to a low value will make MDM startup validation fail. This is now fixed.  
|  [#4960](https://github.com/hapifhir/hapi-fhir/issues/4960) |  Running a $delete-expunge MSSQL or Oracle with over 10,000 resources results in a error and a stack trace, even though the job ends in status COMPLETE. This has been fixed.  
|  [#4968](https://github.com/hapifhir/hapi-fhir/issues/4968) |  Previously, when performing a `$mdm-clear` followed by an update to an existing resource without doing the required `$mdm-submit` first, a non-descriptive NullPointerException was thrown. To fix this, an exception that includes a more descriptive/suggestive message will be thrown when this condition is detected.  
|  [#4971](https://github.com/hapifhir/hapi-fhir/issues/4971) |  Previously, expanding a valueSet including a codeSystem that is unknown to the server would return an internal error(500) to the caller. This problem has been fixed.  
|  [#4976](https://github.com/hapifhir/hapi-fhir/issues/4976) |  Added a test to verify that all Foreign Key constraints are explicitly indexed as well. Added indexes to a number of tables that declared foreign keys that weren't explicitly indexed. This should not only make many of these queries (including $mdm-clear operations) much faster, it will prevent deadlocks in Oracle and other databases that require foreign keys to be indexed.  
|  [#4982](https://github.com/hapifhir/hapi-fhir/issues/4982) |  $mdm-clear has been fixed to respect batch-size parameter (unless it exceeds 500). It has also been made slightly more efficient.  
|  [#4987](https://github.com/hapifhir/hapi-fhir/issues/4987) |  Search bundle entry parts were being created as `INCLUDE` unless explicitly indicated as `MATCH`. This has been fixed. Now they are created as `MATCH` unless explicitly indicated as `INCLUDE`.  
|  [#4989](https://github.com/hapifhir/hapi-fhir/issues/4989) |  Bulk export with patient ID specific user rules/permissions will fail with Parameters that include that patient ID in a _typeFilter. This has been fixed.  
|  [#5000](https://github.com/hapifhir/hapi-fhir/issues/5000) |  Previously, the JPA_PERFTRACE_RAW_SQL was not firing for raw sql that occurred during a `revinclude` or `include`. This has been corrected.  
|  [#5004](https://github.com/hapifhir/hapi-fhir/issues/5004) |  Previously when requesting `previous` or `prev` link using `BundleUtil.getLinkUrlOfType`, only the literal requested link was returned. Now when requesting either `previous` or `prev` `previous` or `prev` link is returned. If both exist, equality is validated and exception is thrown on failure.  
|  [#5005](https://github.com/hapifhir/hapi-fhir/issues/5005) |  Nickname Service Config has been moved out of search parameters as an optional config to add. Nickname service now has an interface to allow flexibility in implementation.  
|  [#5008](https://github.com/hapifhir/hapi-fhir/issues/5008) |  Previously, the GenericClient would only support the HTTP GET method for paging requests. This has been corrected by adding the possibility for paging with an HTTP POST.  
|  [#5011](https://github.com/hapifhir/hapi-fhir/issues/5011) |  Previously, if a partition was created with an ID that was already in use, it would overwrite the partition that was using that ID. Now attempting to overwrite will return a 400  
|  [#5021](https://github.com/hapifhir/hapi-fhir/issues/5021) |  Previously, running batch jobs with postgresql as the db will result in SQL error. This has now been fixed.  
|  [#5023](https://github.com/hapifhir/hapi-fhir/issues/5023) |  Jpa migration task used awssdk StringUtils rather than apache commons string utils. Thanks to @Thopap for the contribution  
|  [#5032](https://github.com/hapifhir/hapi-fhir/issues/5032) |  Fixed a condition by which a NPE was sometimes thrown when performing gateway pagination with targets with property `useHttpPostForAllSearches` configured to `true`.  
|  [#5033](https://github.com/hapifhir/hapi-fhir/issues/5033) |  Updated the query that is supposed to help detect foreign key constraints that also do not have indexes to be a) simpler (smaller) and b) find more results. Added an additional entry to the whitelist as a result.  
|  [#5037](https://github.com/hapifhir/hapi-fhir/issues/5037) |  Previously, when the last source resource with a `MATCH` link was deleted, the golden resource remained in the database, leaving it orphaned. This has now been fixed such that when there are no more `MATCH` links left associated with a golden resource, the golden resource will automatically be deleted.  
|  [#5041](https://github.com/hapifhir/hapi-fhir/issues/5041) |  Fixed an issue where reindex batch jobs failed to start and were stuck in QUEUED status after a SearchParameter update.  
|  [#5055](https://github.com/hapifhir/hapi-fhir/issues/5055) |  Delete expunge (and possibly other batch 2 jobs) will fail when given more than 2100 resources with identical timestamps when using an mssql database. This has been fixed.  
|  [#5057](https://github.com/hapifhir/hapi-fhir/issues/5057) |  Fixed recently introduced regression that when following bundle next links through to the end, in certain circumstances a next link would appear in the final bundle when in fact it was the last page.  
|  [#5075](https://github.com/hapifhir/hapi-fhir/issues/5075) |  The system was failing to recognize custom resources during transaction processing. This has been fixed.  
|  [#5084](https://github.com/hapifhir/hapi-fhir/issues/5084) |  Fixed FindCandidateByExampleSvc.findMatchGoldenResourceCandidates to not return duplicate golden resource candidates.  
|  [#5100](https://github.com/hapifhir/hapi-fhir/issues/5100) |  Fixed a bug in FindCandidateByExampleSvc that resulted in class cast exceptions for IResourcePersistentIds that are not based on Long value ids.  
|  [#5102](https://github.com/hapifhir/hapi-fhir/issues/5102) |  Consider user-assigned IDs, instead of system assigned IDs, when sorting MDM links history.  
|  [#5103](https://github.com/hapifhir/hapi-fhir/issues/5103) |  Remove + from the list of characters that are escaped automatically since + is already an escaped character (representing a space) in the query part of URLs.  
|  [#5112](https://github.com/hapifhir/hapi-fhir/issues/5112) |  Returns better diagnostics message to expunge requests that failed due to unfinished batch deletion  
|  [#5117](https://github.com/hapifhir/hapi-fhir/issues/5117) |  Previously, all MDM field scores, including `NO_MATCH`es, were included in the final total MDM score. This has now been fixed so that only `MATCH`ed fields are included in the total MDM score.  
|  [#5119](https://github.com/hapifhir/hapi-fhir/issues/5119) |  Previously, when the consent service would remove all resources to be returned, the response bundle would not provide the previous/next link(s). This has been corrected.  
|  [#5126](https://github.com/hapifhir/hapi-fhir/issues/5126) |  Previously, updating from Hapi-fhir 6.6.0 to 6.8.0 would cause migration error, it is now fixed.  
|  [#5150](https://github.com/hapifhir/hapi-fhir/issues/5150) |  When running a $delete-expunge with over 10,000 resources, only the first 10,000 resources were deleted. This is now fixed.  
|  [#5155](https://github.com/hapifhir/hapi-fhir/issues/5155) |  Previously, requesting an $expunge operation on CodeSystem resources while CS batch deletion is underway would return HTTP 500. This has been fixed to return HTTP 412 (precondition failed).  
|  [#5157](https://github.com/hapifhir/hapi-fhir/issues/5157) |  Previously, the reuse functionality did not operate correctly when dealing with POST and GET requests. This fix ensures that similar POST and GET export requests will be reused.  
|  [#5167](https://github.com/hapifhir/hapi-fhir/issues/5167) |  Fixed a dependency in the HSQL JDBC driver referencing a non-bundled class (javax.ServletOutputStream)  
|  [#5173](https://github.com/hapifhir/hapi-fhir/issues/5173) |  Fix gateway `$everything` operation to respect server configured default and maximum page sizes.  
|  [#5179](https://github.com/hapifhir/hapi-fhir/issues/5179) |  Added evaluation setting for hapi-fhir storage-cr module operations from outside. Updated provider loading from hapi-fhir instead of external server for caregaps and submitdata providers. Updated testing suite to depend on restful server for new provider loader  
|  [#5182](https://github.com/hapifhir/hapi-fhir/issues/5182) |  Previously, removing tags in a resource update with proper headers and versioning flag would not trigger a new subscription. This has been fixed.  
|  [#5183](https://github.com/hapifhir/hapi-fhir/issues/5183) |  The latest US Core IG includes two ValueSets with different contents, but the same FHIR Id and OID via two different included IGs (i.e. `2.16.840.1.113762.1.4.1010.9` via us.cdc.phinvads and us.nlm.vsac). Ingesting these duplicates in US Core failed with a 500 error. This has been resolved by logging the error and allowing the rest of the ingestion to proceed.  
|  [#5195](https://github.com/hapifhir/hapi-fhir/issues/5195) |  MAX_SUBSCRIPTION_RESULTS was set to an arbitrarily high 50000. This resulted in a failures in hapi-fhir-jpaserver-starter. Other constants, such as SearchParamRegistryImpl.MAX_MANAGED_PARAM_COUNT have a limit of 10000 as well. This aligns the MAX_SUBSCRIPTION results with that value.  
|  [#5205](https://github.com/hapifhir/hapi-fhir/issues/5205) |  Previously, uploading a large vocabulary file (like Loinc) through the upload-external-code-system command would return an error. The issue has been fixed.  
#  0.3.10HAPI FHIR 6.6.2 (Xenon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.8.0-0)
##  0.3.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-7)
**Released:** 2023-07-04
**Codename:** (Xenon)
##  0.3.10.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-7)
This release resolves several CVEs.
##  0.3.10.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-7)
|  [#5023](https://github.com/hapifhir/hapi-fhir/issues/5023) |  Jpa migration task used awssdk StringUtils rather than apache commons string utils. Thanks to @Thopap for the contribution  
---|---|---  
#  0.3.11HAPI FHIR 6.6.1 (Xenon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.6.2-5023)
##  0.3.11.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-8)
**Released:** 2023-05-28
**Codename:** (Xenon)
##  0.3.11.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-8)
This release resolves several CVEs.
##  0.3.11.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-8)
#  0.3.12HAPI FHIR 6.6.0 (Xenon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#hapi-fhir-660-xenon)
##  0.3.12.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-9)
**Released:** 2023-05-18
**Codename:** (Xenon)
##  0.3.12.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-9)
This release has breaking changes.
  * The Resource $validate operation no longer returns Precondition Failed 412 when a resource fails validation. It now returns 200 irrespective of the validation outcome as required by the [FHIR Specification for the Resource $validate operation](https://www.hl7.org/fhir/R4/resource-operation-validate.html).
  * This release changes database indexing for string, uri, and reference SearchParameters. The database migration may take several minutes. These changes will be applied automatically on first startup. To avoid this delay on first startup, run the migration manually.


Bulk export behaviour is changing in this release such that Binary resources created as part of the response will now be created in the partition that the bulk export was requested rather than in the DEFAULT partition as was being done previously.
Bulk import behaviour is changing in this release such that data imported as part of the request will now create resources in the partition that the bulk import was requested rather than in the DEFAULT partition as was being done previously.
The default statistics depth for many tables has changed for Postgres. This improves the performance of many queries. Users of Postgres may wish to ANALYZE the HFJ_SPIDX_* indexing tables to see these improvements immediately.
```
analyze hfj_spidx_coords;
analyze hfj_spidx_date;
analyze hfj_spidx_number;
analyze hfj_spidx_quantity;
analyze hfj_spidx_quantity_nrml;
analyze hfj_spidx_string;
analyze hfj_spidx_token;
analyze hfj_spidx_uri;
analyze hfj_res_link;

```

##  0.3.12.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-9)
|  [#4544](https://github.com/hapifhir/hapi-fhir/issues/4544) |  The HapiTransactionService (HAPI FHIR JPA Transaction Manager) has been improved and used in a number of new places (replacing the @Transactional annotation). This means that stack traces have much less transaction logic noise, and give a clear indication of transaction boundaries.  
---|---|---  
|  [#4544](https://github.com/hapifhir/hapi-fhir/issues/4544) |  A new API has been added to the JPA server for storing resources which are actually placeholders for externally stored payloads. This API is not yet used for any published use cases but will be rolled out for future uses.  
|  [#4562](https://github.com/hapifhir/hapi-fhir/issues/4562) |  Added support for the $care-gaps operation defined by the DaVinci DEQM IG  
|  [#4566](https://github.com/hapifhir/hapi-fhir/issues/4566) |  When creating or modifying a SearchParameter in the JPA server, the local SearchParameter cache is now immediately flushed. This should help with situations such as tests where a SearchParameter is created and then used before the scheduled cache refresh typically occurs.  
|  [#4567](https://github.com/hapifhir/hapi-fhir/issues/4567) |  The IPackageInstallerSvc interface for managing NPM packages now exposes an uninstall method. Thanks to Jens Kristian Villadsen for the pull request!  
|  [#4724](https://github.com/hapifhir/hapi-fhir/issues/4724) |  Preliminary support for R5 and R4B SubscriptionTopic matching has been added. This is not yet complete, but the simplest use cases now work. Comments in the code with prefix 'WIP STR5' indicate areas that need to be extended.  
|  [#4632](https://github.com/hapifhir/hapi-fhir/issues/4632) |  Add support for MdmLink history in the data model. MdmLink history will be stored in the new mdm_link_aud table.  
|  [#4633](https://github.com/hapifhir/hapi-fhir/issues/4633) |  A new JPA feature called Uplifed Refchains has been added. This feature allows chained search expressions to be precalculated for much better performance when executing chained searches.  
|  [#4633](https://github.com/hapifhir/hapi-fhir/issues/4633) |  JPS server _sort expressions can now include a single chained search expression, such as a search for `Encounter?_sort=patient.name`.  
|  [#4651](https://github.com/hapifhir/hapi-fhir/issues/4651) |  Introduce configuration to enable the feature that stores the history of MdmLinks. Introduce the $mdm-link-history operation.  
|  [#4650](https://github.com/hapifhir/hapi-fhir/issues/4650) |  When performing a search using the Location:near search parameter, it is now possible to include this parameter in a `_sort` expression as well. This will result in locations being sorted by their proximity to the coordinates in the parameter value. Thanks to Jens Kristian Villadsen for the suggestion and algorithm design!  
|  [#4654](https://github.com/hapifhir/hapi-fhir/issues/4654) |  Adds an Oracle embedded database to the automated migration test scaffolding. Also adds an initialization schema (version 5.1.0) with test data to the automated migration tests.  
|  [#4654](https://github.com/hapifhir/hapi-fhir/issues/4654) |  Add scaffolding for automated migration tests that use different database vendors.  
|  [#4679](https://github.com/hapifhir/hapi-fhir/issues/4679) |  When executing FHIR transactions in the JPA server where the transaction contained large numbers of inline match URLs, the transaction processor will now prefetch the match URL targets in a single optimized batch. This avoids a potentially significant number of database round trips. In addition, the SQL query used for pre-resolving resource update targets in the transaction processor has been reworked to fully leverage available database indexes. This should result in significant performance improvements for certain large FHIR transactions.  
|  [#4695](https://github.com/hapifhir/hapi-fhir/issues/4695) |  Add an implementation of the Clinical Reasoning FHIR Repository to allow calling the available CR operations in hapi-fhir.  
|  [#4697](https://github.com/hapifhir/hapi-fhir/issues/4697) |  Added DSTU3 and R4 support for the FHIR Clinical Reasoning module operations ActivityDefinition/$apply and PlanDefinition/$apply. $apply allows for general workflow processing and is used in clinical decision support, prior authorization, quality reporting, and disease surveillance use cases.  
|  [#4697](https://github.com/hapifhir/hapi-fhir/issues/4697) |  Added R4 support for Questionnaire/$prepopulate, Questionnaire/$package and PlanDefinition/$package operations. These are operations are intended to support extended DaVinci DTR and SDC uses cases.  
|  [#4697](https://github.com/hapifhir/hapi-fhir/issues/4697) |  Added DSTU3 and R4 support for the DaVinci Documentation Templates and Rules (DTR) Questionnaire/$questionnaire-package operation. This operation allows a Questionnaire to be packaged as a Bundle with all the supporting resources that may be required for its use such as ValueSets and Libraries. This operation is used in context of prior authorization.  
|  [#4697](https://github.com/hapifhir/hapi-fhir/issues/4697) |  Added DSTU3 and R4 support for the DaVinci Structured Data Capture (SDC) operations Questionnaire/$populate operation and QuestionnaireResponse/$extract. These operations are used in data capture and exchange use cases, and are used by downstream specifications such as DaVinci Documentation Templates and Rules (DTR) for prior authorization.  
|  [#4699](https://github.com/hapifhir/hapi-fhir/issues/4699) |  Two new operations have been added to the JPA server: * Instance level $reindex performs a synchronous reindex of a single resource and returns a Parameters object containing all previous and new indexes * Instance level $reindex-dryrun simulates a reindex and shows the previous and new indexes generated  
|  [#4702](https://github.com/hapifhir/hapi-fhir/issues/4702) |  The ResponseHighlightingInterceptor (which renders a stylized HTML view of resources) will now include the resource narrative in the rendered view if one is present.  
|  [#4727](https://github.com/hapifhir/hapi-fhir/issues/4727) |  The FHIR R5 definitions have been upgraded to the final FHIR R5 release version.  
|  [#4735](https://github.com/hapifhir/hapi-fhir/issues/4735) |  It is now possible to perform a conditional create and a conditional patch on the same resource (i.e. the same conditional URL) within a FHIR Transaction bundle.  
|  [#4742](https://github.com/hapifhir/hapi-fhir/issues/4742) |  The in-memory matcher used by the JPA server subscription processor has been optimized to reduce the number of FHIRPath expressions executed while processing in-memory matching.  
|  [#4742](https://github.com/hapifhir/hapi-fhir/issues/4742) |  The JPA server in-memory resource matcher, which is used to improve the efficiency of subscription processing on eligible criteria, now has support for the `_tag`, `_tag:not`, `_security`, `_security:not` and `_profile` parameters.  
|  [#4758](https://github.com/hapifhir/hapi-fhir/issues/4758) |  The JPA server REST-HOOK subscription delivery client had a hardcoded connection pool limit of 20 outgoing connections. This has been increased to 1000, since subscriptions themselves and their delivery channels already act as a rate-limiting step.  
|  [#4758](https://github.com/hapifhir/hapi-fhir/issues/4758) |  An unexpected failure to reindex a single resource during a reindex job will no longer cause the entire job to fail.  
|  [#4758](https://github.com/hapifhir/hapi-fhir/issues/4758) |  The $reindex operation now supports several new optional parameters. 'optimizeStorage' can be used to migrate data to/from inline storage mode, and `optimisticLock` can be used to control whether to guard against concurrent data modification during reindexing. `reindexSearchParameters` can be used to contron whether search parameters are reindexed.  
|  [#4772](https://github.com/hapifhir/hapi-fhir/issues/4772) |  A new optional parameter called `_typePostFetchFilterUrl` has been added to the Bulk Export `$export` operation parameters. This parameter allows filters to be specified that will be applied in-memory to the resources after they have been initially fetched by the database. This can be used to allow complex filters which only remove small numbers of resources to be efficiently applied against large datasets.  
|  [#4773](https://github.com/hapifhir/hapi-fhir/issues/4773) |  Added mdm setting allowing for resources to be matched regardless of partition, also added mdm setting for the storage of all golden resources in a single designated partition  
|  [#4774](https://github.com/hapifhir/hapi-fhir/issues/4774) |  A new Pointcut called `STORAGE_BINARY_ASSIGN_BLOB_ID_PREFIX` has been added. This pointcut is called when a binary blob is about to be stored, and allows implementers to attach a prefix to the blob ID before it is stored.  
|  [#4774](https://github.com/hapifhir/hapi-fhir/issues/4774) |  Bulk Export now supports a new `_exportId` parameter. If provided, any Binary resources generated by this export will have an extension in their `binary.meta` field which identifies this export. This can be used to correlate exported resources with the export job that generated them. In addition, the `binary.meta` field of Bulk Export-generated binaries will also contain the job ID of the export job that generated them, as well as the resource type of the data contained within the binary.  
|  [#4821](https://github.com/hapifhir/hapi-fhir/issues/4821) |  Update the clinical reasoning module version to the latest release of 3.0.0-PRE2  
|  [#4861](https://github.com/hapifhir/hapi-fhir/issues/4861) |  Add documentation for $care-gaps operation  
|  [#4545](https://github.com/hapifhir/hapi-fhir/issues/4545) |  The InterceptorService now maintains an EnumSet of all registered interceptor Pointcuts, which should improve performance when testing for the existence of specific pointcuts.  
|  [#4569](https://github.com/hapifhir/hapi-fhir/issues/4569) |  An inefficient query in the JPA Bulk Export module was optimized. This query caused exports for resources containing tags/security labels/profiles to perform a number of redundant database lookups, so this type of export should be much faster now.  
|  [#4569](https://github.com/hapifhir/hapi-fhir/issues/4569) |  A race condition in the Bulk Export module sometimes resulted in bulk export jobs producing completion reports that did not contain all generated output files. This has been corrected.  
|  [#4622](https://github.com/hapifhir/hapi-fhir/issues/4622) |  The batch system now reads less data during the maintenance pass. This avoids slowdowns on large systems.  
|  [#4629](https://github.com/hapifhir/hapi-fhir/issues/4629) |  String and URI indexing has been improved in some multi-clause queries.  
|  [#4712](https://github.com/hapifhir/hapi-fhir/issues/4712) |  Postgres indexing settings have changed. Autovacuum will run more frequently, and hashed columns will track more most-frequent items.  
|  [#4716](https://github.com/hapifhir/hapi-fhir/issues/4716) |  The SQL query used to fetch pages of search results resulted in an inefficient use of the database in cases where resources have many different versions stored. Thanks to Primož Delopst for the pull request!  
|  [#4742](https://github.com/hapifhir/hapi-fhir/issues/4742) |  The SQL generated for the JPA server `$trigger-subscription` operation has been optimized in order to drastically reduce the number of database round trips for large triggering jobs. In addition, a bug prevented the subscription retriggering from fully executing in a multithreaded way. This has been corrected.  
|  [#4758](https://github.com/hapifhir/hapi-fhir/issues/4758) |  When performing FHIR resource searches, the resource ID fetch size has been reduced. This should improve the memory impact of performing searches which return very large numbers of resources.  
|  [#4763](https://github.com/hapifhir/hapi-fhir/issues/4763) |  The SQL column type used for inline resource storage mode in the JPA server has been changed in order to avoid abitrary database size limits. We now use `text` on PostgreSQL, `long` on Oracle, and `varchar(MAX)` on MSSQL. Previously `varchar(4000)` was used in all cases, requiring manual resizing in order to support longer values.  
|  [#4769](https://github.com/hapifhir/hapi-fhir/issues/4769) |  Chained and reverse-chained searches will be faster in some scenarios. All required columns are now included in the IDX_RL_TGT_v2 index.  
|  [#4915](https://github.com/hapifhir/hapi-fhir/issues/4915) |  Includes by canonical url now use an indexed query, and are much faster.  
|  [#4920](https://github.com/hapifhir/hapi-fhir/issues/4920) |  The `$expunge` operation has been slightly optimized and should issue fewer SQL statements to the database.  
|  [#4545](https://github.com/hapifhir/hapi-fhir/issues/4545) |  The settings beans for the JPA server have been renamed to better reflect their purpose. Specifically the `ModelConfig` bean has been renamed to `StorageSettings` and the `DaoConfig` bean has been renamed to `JpaStorageSettings`.  
|  [#4603](https://github.com/hapifhir/hapi-fhir/issues/4603) |  Transaction retry will now also apply to ObjectOptimisticLockingFailureException. This enables retry of $reindex work chunks when they collide with a DELETE operation.  
|  [#4610](https://github.com/hapifhir/hapi-fhir/issues/4610) |  Bulk export operations have been enhanced to be fully partition aware.  
|  [#4618](https://github.com/hapifhir/hapi-fhir/issues/4618) |  A new interface has been created for SearchParamWithInlineReferencesExtractor with a base implementation. This allows us to encapsulate logic for resolving and replacing inline resources to the same place is also cuts down on code duplication and allow for easier sharing of the same logic  
|  [#4621](https://github.com/hapifhir/hapi-fhir/issues/4621) |  Batch2 work-chunk processing now aligns transaction boundaries with event transitions.  
|  [#4662](https://github.com/hapifhir/hapi-fhir/issues/4662) |  The Resource $validate operation no longer returns Precondition Failed 412 when a resource fails validation. It now returns 200 irrespective of the validation outcome as required by the [FHIR Spec for the Resource $validate operation](https://www.hl7.org/fhir/R4/resource-operation-validate.html).  
|  [#4685](https://github.com/hapifhir/hapi-fhir/issues/4685) |  When calling $everything on a Patient instance, the jpa server no longer traverses into other patients. Previously it was possible to pull in data from another patient for example, via a Provenance resource.  
|  [#4691](https://github.com/hapifhir/hapi-fhir/issues/4691) |  When calling $everything on a Patient instance, the jpa server no longer traverses into other patients. Previously it was possible to pull in data from another patient for example, via Provenance and Composition resources.  
|  [#4693](https://github.com/hapifhir/hapi-fhir/issues/4693) |  Bulk import operations have been enhanced to be fully partition aware.  
|  [#4710](https://github.com/hapifhir/hapi-fhir/issues/4710) |  Removed maven configuration hack for IntelliJ now that Jetbrains supports different java versions in test and main (IDEA-85478).  
|  [#4748](https://github.com/hapifhir/hapi-fhir/issues/4748) |  Previously, HAPI-FHIR converted R5 Subscriptions into R4 Subscriptions and triggered those subscriptions by resource changes in the same way R4 subscriptions are triggered. Now R5 Subscriptions are triggered based on the topic they subscribe to and the resource matching happens via the SubscriptionTopic resource. This also means that R5 Subscription endpoints are now delivered a subscription-notification Bundle as opposed to the resource as is the case with R4 Subscriptions.  
|  [#3231](https://github.com/hapifhir/hapi-fhir/issues/3231) |  Previously, conditional updates always followed the STU3 specifications. Now, conditional updates in an R4 server follow the R4 specifications.  
|  [#4204](https://github.com/hapifhir/hapi-fhir/issues/4204) |  With default configuration, Resource meta.tag properties: `userSelected` and `version`, were not stored in the database. This is now fixed.  
|  [#4736](https://github.com/hapifhir/hapi-fhir/issues/4736) |  When enabling both auto_create_placeholder_reference_targets and allow_inline_match_url_references, POSTing a bundle with a conditional create URL will fail with HAPI-0507. This has now been fixed.  
|  [#4483](https://github.com/hapifhir/hapi-fhir/issues/4483) |  The OverridePathBasedReferentialIntegrityForDeletesInterceptor now works on partitioned servers. Thanks to GitHub user @JorisHeadease for the pull request!  
|  [#4509](https://github.com/hapifhir/hapi-fhir/issues/4509) |  References to the patient/subject in IPS documents generated using the $summary operation were not replaced with the bundle-local UUID assigned to the patient. Also, some dangling references were left in the generated bundle. This has been corrected.  
|  [#4551](https://github.com/hapifhir/hapi-fhir/issues/4551) |  The HapifhirDal search method required use of TypedBundleProvider.getallresources to avoid null pointer issue on searches that are greater than the default querycount  
|  [#4565](https://github.com/hapifhir/hapi-fhir/issues/4565) |  The JPA server `Patient:address` SearchParameter did not index values in the element `Address.district`. This has been corrected.  
|  [#4575](https://github.com/hapifhir/hapi-fhir/issues/4575) |  Previously, the full set of meta tags is not present in the payload of the subscription message when UPDATE the resource. Now, this issue has been fixed.  
|  [#4580](https://github.com/hapifhir/hapi-fhir/issues/4580) |  Fix the BaseTransactionProcessor.UNQUALIFIED_MATCH_URL_START REGEX so that it correctly matches patterns that contain dashes in them.  
|  [#4582](https://github.com/hapifhir/hapi-fhir/issues/4582) |  Upgrade dependency on core to 5.6.97 including hapi-fhir code enhancements and unit test fixes.  
|  [#4582](https://github.com/hapifhir/hapi-fhir/issues/4582) |  Update IBaseCoding, Tag, and tinder Child to support new userSelected and version fields for resource tags.  
|  [#4548](https://github.com/hapifhir/hapi-fhir/issues/4548) |  Simultaneous DELETE and $reindex operations could corrupt the search index. This has been fixed.  
|  [#4597](https://github.com/hapifhir/hapi-fhir/issues/4597) |  Simultaneous conditional create or create-on-update operations no longer create duplicate matching resources.  
|  [#4601](https://github.com/hapifhir/hapi-fhir/issues/4601) |  The InteractionBlockingInterceptor did not have support for the interactions 'search' and 'history' despite them being declared in the code system. This has been fixed.  
|  [#4613](https://github.com/hapifhir/hapi-fhir/issues/4613) |  Previously, SPECIAL search parameters that were _not_ nearness parameters, such as simple string Search Parameters, could not be used in chained queries. This has been resolved, and now queries like `OrganizationAffiliation?location.some-special-param=abc` will work as intended.  
|  [#4617](https://github.com/hapifhir/hapi-fhir/issues/4617) |  Previously, Quartz jobs were scheduled after the scheduler was started which resulted in a race condition where duplicate `TRIGGER_ACCESS` entries were being added to the `QRTZ_LOCKS` table. This has been fixed.  
|  [#4630](https://github.com/hapifhir/hapi-fhir/issues/4630) |  Default values are provided for the new UPDATE_TIME columns so batch jobs started before an upgrade can complete.  
|  [#4639](https://github.com/hapifhir/hapi-fhir/issues/4639) |  When executing FHIR transactions in the JPA server with automatic retry enabled, if automatic placeholder-reference creation is enabled the system could sometimes fail to automatically retry. This has been corrected.  
|  [#4643](https://github.com/hapifhir/hapi-fhir/issues/4643) |  There was a transaction boundary issue in the Batch2 storage layer which resulted in the framework needing more open database connections than necessary. This has been corrected.  
|  [#4644](https://github.com/hapifhir/hapi-fhir/issues/4644) |  The %now value for date search parameters did not properly set the time zone of the time being compared to. This has been corrected.  
|  [#4647](https://github.com/hapifhir/hapi-fhir/issues/4647) |  Batch job state transitions are are now transitionally safe.  
|  [#4650](https://github.com/hapifhir/hapi-fhir/issues/4650) |  When processing the Location:near Search Parameter, if a distance unit was supplied in the parameter value it was ignored and the distance was always assumed to be km. This has been corrected.  
|  [#4657](https://github.com/hapifhir/hapi-fhir/issues/4657) |  Fixing inconsistency between the method behavior and naming.  
|  [#4663](https://github.com/hapifhir/hapi-fhir/issues/4663) |  Removing previous bundle provider to allow for iterableBundle provider, which will allow for paging resources instead of full consumption of patient resources at once  
|  [#4670](https://github.com/hapifhir/hapi-fhir/issues/4670) |  Previously, the $export response Content-Location header would not use a fixed base URL. Now, the URLs that are returned will respect the `Fixed Value for Endpoint Base URL`.  
|  [#4671](https://github.com/hapifhir/hapi-fhir/issues/4671) |  Previously, searching with the '_source' parameter would return matching source for all resource types. This has been corrected.  
|  [#4675](https://github.com/hapifhir/hapi-fhir/issues/4675) |  Previously, when a `_has` query was performed with a non-existent reference field, the query succeed with empty results in H2 and failed with a 500 Server Error in Postgres. Now all database vendors will throw an `InvalidRequestException` if a non-existent reference field is provided for a `_has` query.  
|  [#4680](https://github.com/hapifhir/hapi-fhir/issues/4680) |  Previously, invoking $mdm-link-history on only the golden resource ID or source ID would fail due to faulty validation. This has been corrected.  
|  [#4699](https://github.com/hapifhir/hapi-fhir/issues/4699) |  When updating resource fields targeted by a Combo Non-Unique SearchParameter, previous indexes were not deleted meaning that old search values could still find the resource. This has been corrected.  
|  [#4699](https://github.com/hapifhir/hapi-fhir/issues/4699) |  The BundleBuilder utility class did not work with DSTU2 bundles. This has been corrected.  
|  [#4700](https://github.com/hapifhir/hapi-fhir/issues/4700) |  When querying $mdm-link-history with no inputs, the error message is mislaeading. Also, $mdm-link-history cannot handle comma-delimited inputs. Both issues are now fixed.  
|  [#4721](https://github.com/hapifhir/hapi-fhir/issues/4721) |  Previously, clicking the '$diff' button on FHIRweb module would direct the user to an empty page. Now it displays the correct page.  
|  [#4725](https://github.com/hapifhir/hapi-fhir/issues/4725) |  Make small changes to stabilize the clinical reasoning code base.  
|  [#4729](https://github.com/hapifhir/hapi-fhir/issues/4729) |  When there is an EID MDM MATCH, the MATCH link has no score. This issue has been fixed.  
|  [#4734](https://github.com/hapifhir/hapi-fhir/issues/4734) |  Fixed a regression introduced by 4624 that prevented subscriptions from loading into the in-memory subscription cache on system startup.  
|  [#4738](https://github.com/hapifhir/hapi-fhir/issues/4738) |  Processing multiple transaction bundles with similar entries on different threads would lead to Constraint Violations if the entries contained similar unique values. This is an error that can be fixed with Retry Handlers now.  
|  [#4745](https://github.com/hapifhir/hapi-fhir/issues/4745) |  Fixed an issue where a golden resource could not be chosen as no-match when another golden resource was already chosen as a match.  
|  [#4746](https://github.com/hapifhir/hapi-fhir/issues/4746) |  Previously, the Lenient error handler will accept invalid extension containing value and nested extensions by default. This will lead to errors when client further attempt to read, update or delete the resources. Now, this has been fixed and a DataFormatException will be thrown instead.  
|  [#4750](https://github.com/hapifhir/hapi-fhir/issues/4750) |  Previously, an mdm-submit operation with Prefer: response-async header resulted in a NullPointerException in the error logs and a batch job status of FAILED. This has been fixed.  
|  [#4757](https://github.com/hapifhir/hapi-fhir/issues/4757) |  Previously, if a binary resource was read via GET, and the binary content had been externalized and not reinflated, a NullPointerException would be thrown. This has been fixed.  
|  [#4766](https://github.com/hapifhir/hapi-fhir/issues/4766) |  Previously, performing $mdm-clear would soft delete Golden Record resources and keep them in the database. Now, $mdm-clear will expunge the resources from the database.  
|  [#4782](https://github.com/hapifhir/hapi-fhir/issues/4782) |  Previously, a FHIRPath patch delete operation where the path resolved to an element in a collection, the element was not always being removed from the collection. This has been fixed.  
|  [#4786](https://github.com/hapifhir/hapi-fhir/issues/4786) |  With default configuration, Resource meta.tag properties: `userSelected` and `version`, were not stored in the database. This is now fixed.  
|  [#4789](https://github.com/hapifhir/hapi-fhir/issues/4789) |  Previously, there was the possibility for a race condition to happen in the initialization of the email subscription processing component that would result in email not being sent out. This issue has been fixed.  
|  [#4804](https://github.com/hapifhir/hapi-fhir/issues/4804) |  Improved performance of `mdm-clear` operation by adding index and avoiding redundant deletion.  
|  [#4814](https://github.com/hapifhir/hapi-fhir/issues/4814) |  A recent regression prevented the SQL Migrator from running on Oracle. This has been corrected.  
|  [#4812](https://github.com/hapifhir/hapi-fhir/issues/4812) |  The tag being added on golden resources does not have a version, might as well add one.  
|  [#4819](https://github.com/hapifhir/hapi-fhir/issues/4819) |  Tags no longer provide a default `false` value for `userSelected`.  
|  [#4844](https://github.com/hapifhir/hapi-fhir/issues/4844) |  /Patient/{patientid}/$everything?_type={resource types} would omit resources that were not directly related to the Patient resource (even if those resources were specified in the _type list). This was in conflict with /Patient/{patientid}/$everything operation, which did return said resources. This has been fixed so both return all related resources, even if those resources are not directly related to the Patient resource.  
|  [#4846](https://github.com/hapifhir/hapi-fhir/issues/4846) |  Job maintenance service would throw an exception if a job definition is unknown, this would run maintenance on every job instance after it. Now the maintenance will skip over unknown job definitions and display a warning log message indication a job definition is missing.  
|  [#4853](https://github.com/hapifhir/hapi-fhir/issues/4853) |  Previously, when validating resources that contain a display in a Coding/CodeableConcept different from the display defined in the CodeSystem that is used, no errors are returned in the outcome. This is now fixed.  
|  [#4860](https://github.com/hapifhir/hapi-fhir/issues/4860) |  Running an $export that completes successfully results in a progress percentage of less than 100%. This has now been fixed.  
|  [#4863](https://github.com/hapifhir/hapi-fhir/issues/4863) |  Previously the SearchParameterCanonicalizer did not correctly convert DSTU2 and DSTU3 custom resources SearchParameters into RuntimeSearchParam. This is now fixed.  
|  [#4872](https://github.com/hapifhir/hapi-fhir/issues/4872) |  POSTing a Bundle with over 100 references to the same resource will fail with HAPI-2207 'Multiple resources match'. This has been fixed.  
|  [#4873](https://github.com/hapifhir/hapi-fhir/issues/4873) |  Previously, if the fhirId in ResourceTable happened to be set to an empty string, the resourceId would be missing when trying to generate the full ID string. This has now been fixed.  
|  [#4875](https://github.com/hapifhir/hapi-fhir/issues/4875) |  Previously, `$binary-access-write` operation didn't trigger `STORAGE_BINARY_ASSIGN_BLOB_ID_PREFIX` Pointcut. This has been fixed.  
|  [#4886](https://github.com/hapifhir/hapi-fhir/issues/4886) |  Requests to start an $export of Patient or Group will now fail with 404 ResourceNotFound when the target resources do not exist. Before, the system would start a bulk export background job which would then fail.  
|  [#4891](https://github.com/hapifhir/hapi-fhir/issues/4891) |  Initiating a bulk export with a _type filter would sometimes return resource types not specified in the filter. This has been fixed.  
|  [#4893](https://github.com/hapifhir/hapi-fhir/issues/4893) |  Update the IRuleBuilder to support Patient Export rules via the new `patientExportOnPatient` method on the IRuleBuilder. Previously, it was accidentally using Group Export rules.  
|  [#4910](https://github.com/hapifhir/hapi-fhir/issues/4910) |  Remove some references to `all_constraints` table in oracle database migration tasks which were causing errors for version 19c.  
|  [#4545](https://github.com/hapifhir/hapi-fhir/issues/4545) |  The InterceptorService no longer supports ThreadLocal interceptor registrations. This feature was deprecated in 6.2.0 due to lack of use and has never been enabled by default. Please let us know on the mailing list if this affects you.  
|  [#4727](https://github.com/hapifhir/hapi-fhir/issues/4727) |  The HAPI FHIR CLI `run-server` command has been removed. This command was used to launch a HAPI FHIR JPA Server instance. The [hapi-fhir-jpaserver-starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter/) project has long since duplicated and improved on the CLI version of the launcher, and maintaining both was been a maintenance burden. Users of the CLI command are recommended to migrate to the starter project.  
#  0.3.13HAPI FHIR 6.4.6 (Wizard)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.6.0-4544)
##  0.3.13.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-10)
**Released:** 2023-06-28
**Codename:** (Wizard)
##  0.3.13.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-10)
##  0.3.13.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-10)
|  [#5008](https://github.com/hapifhir/hapi-fhir/issues/5008) |  Previously, the GenericClient would only support the HTTP GET method for paging requests. This has been corrected by adding the possibility for paging with an HTTP POST.  
---|---|---  
#  0.3.14HAPI FHIR 6.4.5 (Wizard)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.6-5008)
##  0.3.14.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-11)
**Released:** 2023-03-15
**Codename:** (Wizard)
##  0.3.14.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-11)
This release fixes an accidental behaviour that was introduced in 6.4.2. From that version up until now, if a Tag Definition was created with a null `userSelected` element, it would still be stored as `false` instead of `null`. This release fixes that behaviour, and now correctly stores the value as `null` if it is not specified. If you do not use this field, no action needs to be taken. However, if you do use this field, the `userSelected` elements stored from the installation of version 2023.02.R02 up until now are potentially suspect. The following SQL can be executed to clear the `false` value from this table and replace it with null, if desired:
```
update HFJ_TAG_DEF  set TAG_USER_SELECTED = null where TAG_USER_SELECTED = 'false'

```

Copy
This will wholesale replace all `userSelected` fields.
##  0.3.14.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-11)
|  [#4652](https://github.com/hapifhir/hapi-fhir/issues/4652) |  Fix for MSSQL migration failure related to Job instance UPDATE_TIME column default value not being set correctly  
---|---|---  
|  [#4813](https://github.com/hapifhir/hapi-fhir/issues/4813) |  Under heavy concurrency, a bug resulted in identical tag definitions being rejected with a `NonUniqueResultException` some of the time. This has been corrected.  
#  0.3.15HAPI FHIR 6.4.4 (Wizard)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.5-4652)
##  0.3.15.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-12)
**Released:** 2023-03-15
**Codename:** (Wizard)
##  0.3.15.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-12)
##  0.3.15.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-12)
|  [#4597](https://github.com/hapifhir/hapi-fhir/issues/4597) |  Simultaneous conditional create or create-on-update operations no longer create duplicate matching resources.  
---|---|---  
|  [#4652](https://github.com/hapifhir/hapi-fhir/issues/4652) |  Fix for MSSQL migration failure related to Job instance UPDATE_TIME column default value not being set correctly  
#  0.3.16HAPI FHIR 6.4.3 (Wizard)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.4-4597)
##  0.3.16.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-13)
**Released:** 2023-03-08
**Codename:** (Wizard)
##  0.3.16.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-13)
The known issue with Bulk Export in HAPI 6.4.0 has been resolved. Bulk export functionality is now more performant at large scale, and does not generate occasional incomplete file reports.
##  0.3.16.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-13)
|  [#4622](https://github.com/hapifhir/hapi-fhir/issues/4622) |  The batch system now reads less data during the maintenance pass. This avoids slowdowns on large systems.  
---|---|---  
|  [#4324](https://github.com/hapifhir/hapi-fhir/issues/4324) |  Fix issue where end time is missing for bulk exports on completion  
|  [#4630](https://github.com/hapifhir/hapi-fhir/issues/4630) |  Default values are provided for the new UPDATE_TIME columns so batch jobs started before an upgrade can complete.  
|  [#4636](https://github.com/hapifhir/hapi-fhir/issues/4636) |  Fixed Oracle syntax problem with the fix that was previously provided via Pull Request 4630.  
#  0.3.17HAPI FHIR 6.4.2 (Wizard)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.3-4622)
##  0.3.17.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-14)
**Released:** 2023-02-27
**Codename:** (Wizard)
##  0.3.17.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-14)
The known issue with Bulk Export in HAPI 6.4.0 has been resolved. Bulk export functionality is now more performant at large scale, and does not generate occasional incomplete file reports.
##  0.3.17.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-14)
|  [#4204](https://github.com/hapifhir/hapi-fhir/issues/4204) |  With default configuration, Resource meta.tag properties: `userSelected` and `version`, were not stored in the database. This is now fixed.  
---|---|---  
|  [#4548](https://github.com/hapifhir/hapi-fhir/issues/4548) |  Simultaneous DELETE and $reindex operations could corrupt the search index. This has been fixed.  
#  0.3.18HAPI FHIR 6.4.1 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.2-4204)
##  0.3.18.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-15)
**Released:** 2023-02-24
**Codename:** (Vishwa)
##  0.3.18.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-15)
This release bumps the org.hl7.fhir core dependency up to 5.6.97, and modifies IBaseCoding
##  0.3.18.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-15)
|  [#4569](https://github.com/hapifhir/hapi-fhir/issues/4569) |  An inefficient query in the JPA Bulk Export module was optimized. This query caused exports for resources containing tags/security labels/profiles to perform a number of redundant database lookups, so this type of export should be much faster now.  
---|---|---  
|  [#4569](https://github.com/hapifhir/hapi-fhir/issues/4569) |  A race condition in the Bulk Export module sometimes resulted in bulk export jobs producing completion reports that did not contain all generated output files. This has been corrected.  
|  [#4551](https://github.com/hapifhir/hapi-fhir/issues/4551) |  The HapifhirDal search method required use of TypedBundleProvider.getallresources to avoid null pointer issue on searches that are greater than the default querycount  
|  [#4582](https://github.com/hapifhir/hapi-fhir/issues/4582) |  Upgrade dependency on core to 5.6.97 including hapi-fhir code enhancements and unit test fixes.  
|  [#4582](https://github.com/hapifhir/hapi-fhir/issues/4582) |  Update IBaseCoding, Tag, and tinder Child to support new userSelected and version fields for resource tags.  
#  0.3.19HAPI FHIR 6.2.5 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.4.1-4569)
##  0.3.19.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-16)
**Released:** 2023-01-09
**Codename:** (Vishwa)
##  0.3.19.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-16)
This release fixes a problem with the batch framework which could cause jobs to hang indefinitely if multiple processes attempted to run a maintenance pass simultaneously.
##  0.3.19.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-16)
|  [#4065](https://github.com/hapifhir/hapi-fhir/issues/4065) |  A new DaoConfig configuration setting has been added called JobFastTrackingEnabled, default false. If this setting is enabled, then gated batch jobs that produce only one chunk will immediately trigger a batch maintenance job. This may be useful for testing, but is not recommended for production use. Prior to this change, fasttracking was always enabled which meant if the server was not busy, small batch jobs would be processed quickly. However this lead do instability on high-volume servers, so this feature is now disabled by default.  
---|---|---  
|  [#4400](https://github.com/hapifhir/hapi-fhir/issues/4400) |  When Batch2 work notifications are received twice (e.g. because the notification engine double delivered) an unrecoverable failure could occur. This has been corrected.  
|  [#4417](https://github.com/hapifhir/hapi-fhir/issues/4417) |  Fixed a bug with batch2 which could cause previously completed chunks to be set back to in-progress. This could cause a batch job to never complete. Now, a safeguard to ensure a job can never return to in-progress once it has completed or failed.  
|  [#4422](https://github.com/hapifhir/hapi-fhir/issues/4422) |  When a Bulk Export job runs with a large amount of data, there is a chance the reduction step can be kicked off multiple times, resulting in data loss in the final report. Jobs will now be set to in-progress before processing to prevent multiple reduction steps from being started.  
#  0.3.20HAPI FHIR 6.2.4 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.2.5-4065)
##  0.3.20.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-17)
**Released:** 2023-01-04
**Codename:** (Vishwa)
##  0.3.20.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-17)
##  0.3.20.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-17)
|  [#4388](https://github.com/hapifhir/hapi-fhir/issues/4388) |  Fixed an edge case during a Read operation where hooks could be invoked with a null resource. This could cause a NullPointerException in some cases.  
---|---|---  
#  0.3.21HAPI FHIR 6.2.3 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.2.4-4388)
##  0.3.21.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-18)
**Released:** 2023-01-05
**Codename:** (Vishwa)
##  0.3.21.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-18)
##  0.3.21.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-18)
|  |  The $mdm-clear operation sometimes failed with a constraint error when running in a heavily multithreaded environment. This has been fixed.  
---|---|---  
#  0.3.22HAPI FHIR 6.10.2 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.2.3-4398)
##  0.3.22.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-19)
**Released:** 2023-12-22
**Codename:** (Zed)
##  0.3.22.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-19)
### Major Database Change
This release fixes a migration from 6.10.1 that was ineffective for SQL Server (MSSQL) instances. This may take several minutes on a larger system (e.g. 10 minutes for 100 million resources). For zero-downtime, or for larger systems, we recommend you upgrade the schema using the CLI tools.
##  0.3.22.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-19)
#  0.3.23HAPI FHIR 6.10.1 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#hapi-fhir-6101-zed)
##  0.3.23.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-20)
**Released:** 2023-12-18
**Codename:** (Zed)
##  0.3.23.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-20)
### Major Database Change
This release contains a migration that covers every resource. This may take several minutes on a larger system (e.g. 10 minutes for 100 million resources). For zero-downtime, or for larger systems, we recommend you upgrade the schema using the CLI tools.
##  0.3.23.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-20)
|  [#5486](https://github.com/hapifhir/hapi-fhir/issues/5486) |  Previously, testing database migration with cli migrate-database command in dry-run mode would insert in the migration task table. The issue has been fixed.  
---|---|---  
|  [#5511](https://github.com/hapifhir/hapi-fhir/issues/5511) |  Previously, when creating an index as a part of a migration, if the index already existed with a different name on Oracle, the migration would fail. This has been fixed so that the create index migration task now recovers with a warning message if the index already exists with a different name.  
|  [#5546](https://github.com/hapifhir/hapi-fhir/issues/5546) |  A database migration added trailing spaces to server-assigned resource ids. This fix removes the bad migration, and adds another migration to fix the errors.  
#  0.3.24HAPI FHIR 6.10.0 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#change6.10.1-5486)
##  0.3.24.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#release-information-21)
**Released:** 2023-11-18
**Codename:** (Zed)
##  0.3.24.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#upgrade-instructions-21)
### Major Database Change
This release makes performance changes to the database definition in a way that is incompatible with releases before 6.4. Attempting to run version 6.2 or older simultaneously with this release may experience errors when saving new resources.
### Change Tracking and Subscriptions
This release introduces significant a change to the mechanism performing submission of resource modification events to the message broker. Previously, an event would be submitted as part of the synchronous transaction modifying a resource. Synchronous submission yielded responsive publishing with the caveat that events would be dropped upon submission failure.
We have replaced the synchronous mechanism with a two stage process. Events are initially stored in database upon completion of the transaction and subsequently submitted to the broker by a scheduled task. This new asynchronous submission mechanism will introduce a slight delay in event publishing. It is our view that such delay is largely compensated by the capability to retry submission upon failure which will eliminate event losses.
### Tag, Security Label, and Profile changes
There are some potentially breaking changes:
  * On resource retrieval and before storage, tags, security label and profile collections in resource meta will be sorted in lexicographical order. The order of the elements for Coding types (i.e. tags and security labels) is defined by the (security, code) pair of each element. This normally should not break any clients because these properties are sets according to the FHIR specification, and hence the order of the elements in these collections should not matter. Also with this change the following side effects can be observed: 
    * If using INLINE tag storage mode, the first update request to a resource which has tags, security labels or profiles could create a superfluous resource version if the update request does not really introduce any change to the resource. This is because the persisted tags, security labels, and profile may not be sorted in lexicographical order, and this would be interpreted as a new resource version since the tags would be sorted before storage after this change. If the update request actually changes the resource, there is no concern here. Also, subsequent updates will not create an additional version because of ordering of the meta properties anymore.
    * These meta collections are sorted in place by the storage layer before persisting the resource, so any piece of code that is calling storage layer directly should not be passing in unmodifiable collections, as it would result in an error.


##  0.3.24.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html#changes-21)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * MSSQL JDBC (JPA): 9.4.1.jre8 -> 12.2.0.jre11
  * Flexmark (All): 0.50.40 -> 0.64.8
  * Logback (All): 1.4.4 -> 1.4.7
  * H2 Database (JPA): 2.1.214 -> 2.2.220
  * Thymeleaf (Testpage Overlay): 3.0.14.RELEASE -> 3.1.2.RELEASE
  * xpp3 (All): 1.1.4c.0 -> 1.1.6
  * HtmlUnit (All): 2.67.0 -> 2.70.0
  * org.hl7.fhir.core (All): 6.0.22.2 -> 6.1.2.2

  
---|---|---  
|  [#5089](https://github.com/hapifhir/hapi-fhir/issues/5089) |  Updating storage-cr module to latest CQL 3.0.0, latest cql engine improvements, and Clinical Reasoning operations to leverage repository api pattern. This will remove several dependencies from within hapi-fhir to make future maintenance simpler and performance more robust.  
|  [#5090](https://github.com/hapifhir/hapi-fhir/issues/5090) |  Adding pointcuts for the following MDM Operations: MDM_CREATE_LINK, MDM_UPDATE_LINK, MDM_MERGE_GOLDEN_RESOURCES, MDM_LINK_HISTORY, MDM_NOT_DUPLICATE, MDM_CLEAR, MDM_SUBMIT.  
|  [#5129](https://github.com/hapifhir/hapi-fhir/issues/5129) |  Added a field that shows the total number of `POSSIBLE_DUPLICATE` links has been added to the `$mdm-duplicate-golden-resources` operation response.  
|  [#5141](https://github.com/hapifhir/hapi-fhir/issues/5141) |  Previously, updating an existing resource (resource A) to match a resource that it didn't match to before (resource B) would update only the already existing links on resource A. This behaviour has been changed. Now such an update will also add additional links, if necessary, from resource A to resource B, including adding a POSSIBLE_DUPLICATE between golden resource A and golden resource B.  
|  [#5235](https://github.com/hapifhir/hapi-fhir/issues/5235) |  Changes have been made to allow searching on multiple patient _ids when in a patient_id partitioned environment.  
|  [#5236](https://github.com/hapifhir/hapi-fhir/issues/5236) |  Previously, when updating an MDM link to NO_MATCH, the golden resource involved would maintain its previous values, as defined by survivorship service. This would result in out-of-date golden resources with data that might not be accurate anymore. Now, when a link is changed to NO_MATCH, golden resources will be rebuilt from the ground up using the MDM survivorship service, and the set of links/source resources available at the time of update.  
|  [#5238](https://github.com/hapifhir/hapi-fhir/issues/5238) |  Added an implementation of Clinical Reasoning CDS on FHIR to the CDS Hooks module that allows PlanDefinition worfklows to be processed as CDS Services using the $apply operation.  
|  [#5246](https://github.com/hapifhir/hapi-fhir/issues/5246) |  Combined the ExpandResources step and WriteBinary step in the new WriteBinary step v2 for bulk exports.  
|  [#5271](https://github.com/hapifhir/hapi-fhir/issues/5271) |  The error messages returned in an OperationOutcome when validating terminology codes as a part of resource profile validation have been improved. Machine processable location (line/col) information is now available through a pair of dedicated extensions, and error messages such as UCUM parsing issues are now returned to the client (previously they were swallowed and a generic error message was returned).  
|  [#5274](https://github.com/hapifhir/hapi-fhir/issues/5274) |  Added a service for generating metrics on mdm links and resources. This includes JPA queries and updated indices.  
|  [#5275](https://github.com/hapifhir/hapi-fhir/issues/5275) |  Added an API that allows to configure permission rules for operations with access to all resources. This permission is needed to allow a search across the entire patient's record in the scope of the $everything operation to access all resources that references input Patient, including resources outside of the patient's compartment.  
|  [#5290](https://github.com/hapifhir/hapi-fhir/issues/5290) |  Added storage property to prevent conditional updates from invalidating match criteria.  
|  [#5300](https://github.com/hapifhir/hapi-fhir/issues/5300) |  A new configuration option has been added to `StorageSettings` which enables support in the JPA server for the `_language` SearchParameter.  
|  [#5306](https://github.com/hapifhir/hapi-fhir/issues/5306) |  A new option has been added to the JPA server JpaStorageOptions which prevents the server from maintaining a version history. In this mode, when a new version of a resource is added, the previous version is automatically expunged.  
|  [#5321](https://github.com/hapifhir/hapi-fhir/issues/5321) |  It is now possible to configure the strictness of concept display name validation using a new flag on the InMemoryTerminologyServerValidationSupport (for non-JPA validation) and JpaStorageSettings (for JPA validation). In addition, the error messages emitted by the validator when a concept display doesn't match have been improved to be much more useful.  
|  [#5341](https://github.com/hapifhir/hapi-fhir/issues/5341) |  Added registries for CdsCrServices and CrDiscoveryServices in CDS Hooks to allow registration of custom services.  
|  [#5355](https://github.com/hapifhir/hapi-fhir/issues/5355) |  The generated OpenAPI documentation produced by OpenApiInterceptor will now include additional details in the individual resource type documentation, including the values of _CapabilityStatement.rest.resource.documentation_ , _CapabilityStatement.rest.resource.profile_ , and _CapabilityStatement.rest.resource.supportedProfile_.  
|  [#5366](https://github.com/hapifhir/hapi-fhir/issues/5366) |  The package installer overrides existing (built-in) SearchParameter with multiple base resources. This is happening when installing US Core package for Practitioner.given as an example. This change allows the existing SearchParameter to continue to exist with the remaining base resources.  
|  [#5375](https://github.com/hapifhir/hapi-fhir/issues/5375) |  Add settings for CDS Services using CDS on FHIR. Also removed the dependency on Spring Boot from the CR configs used by CDS Hooks.  
|  [#5407](https://github.com/hapifhir/hapi-fhir/issues/5407) |  Previously, when the payload of a subscription message exceeds the broker maximum message size, exception would be thrown and retry will be performed indefinitely until the maximum message size is adjusted. Now, the message will be successfully delivered for rest-hook and email subscriptions, while message subscriptions remains the same behavior as before.  
|  [#5428](https://github.com/hapifhir/hapi-fhir/issues/5428) |  Add support for non-standard _pid SearchParameter to the the JPA engine. This new SP provides an efficient tie-breaking sort key.  
|  [#5215](https://github.com/hapifhir/hapi-fhir/issues/5215) |  When executing an HFQL search with a FHIRPath filter on the `id` element, this will now be automatically converted into an `_id` search parameter match for better performance.  
|  [#5377](https://github.com/hapifhir/hapi-fhir/issues/5377) |  Subscription triggering via the `$trigger-subscription` operation is now multi-threaded, which significantly improves performance for large data sets.  
|  [#5387](https://github.com/hapifhir/hapi-fhir/issues/5387) |  Enable the search cache for some requests even when a consent interceptor is active. If no consent service uses canSeeResource (i.e. shouldProcessCanSeeResource() returns false); or startOperation() returns AUTHORIZED; then the search cache is enabled.  
|  [#5395](https://github.com/hapifhir/hapi-fhir/issues/5395) |  The background activity that clears stale search results now has higher throughput. Busy servers should no longer accumulate dead stale search results.  
|  [#5405](https://github.com/hapifhir/hapi-fhir/issues/5405) |  Sorting by _id now uses the FHIR_ID column on HFJ_RESOURCE and avoid joins.  
|  [#4803](https://github.com/hapifhir/hapi-fhir/issues/4803) |  Internal client-assigned ids are now resolved within the HFJ_RESOURCE table, avoiding a join to HFJ_FORCED_ID  
|  [#4834](https://github.com/hapifhir/hapi-fhir/issues/4834) |  Several enhancements were made to the IPS generator:
  * Generated IPS documents will no longer include sections that contain no contents.
  * NarrativeLink extensions now use the correct datatype (url instead of uri)
  * Section profile URLs have been updated to no longer use an unknown URL
  * Some resources added to the generated IPS did not have their FHIR server IDs replaced with a placeholder UUID.
  * Immunization manufacturer was not fetched from the server

Thanks to Rio Bennin for all of the feedback!  
|  [#5229](https://github.com/hapifhir/hapi-fhir/issues/5229) |  Previously, when using INLINE tag storage mode, a superfluous version of a resource would be created as a result of an update request which didn't have a real logical change to the resource but only changed the order of existing items in tag, security label or profile collections. This change prevents this behaviour. Also on resource retrieval, these meta collections are sorted alphabetically, based on (security, code) pair for tags and security labels.  
|  [#5262](https://github.com/hapifhir/hapi-fhir/issues/5262) |  Previously, when both resourceId and goldenResourceId are provided to the mdm link history operation, they will be treated as an OR. This is now changed to AND in order to comply with REST conventions.  
|  [#5371](https://github.com/hapifhir/hapi-fhir/issues/5371) |  Remote terminology operations that fetch ValueSets or CodeSystems now force _summary=false to allow local validation.  
|  [#3786](https://github.com/hapifhir/hapi-fhir/issues/3786) |  Previously, when executing an update on a resource that had to undergo MDM, a nullpointer could occur. This has been fixed.  
|  [#5176](https://github.com/hapifhir/hapi-fhir/issues/5176) |  Previously, the use of search parameter _lastUpdated as part of a reverse chaining search would return an error message to the client. This issue has been fixed  
|  [#5192](https://github.com/hapifhir/hapi-fhir/issues/5192) |  Fixed a bug where search Bundles with `include` entries from an _include query parameter might trigger a 'next' link to blank pages. Specifically, if _include'd resources + requested resources were greater than (or equal to) requested page size, a 'next' link would be generated, even though no additional resources are available.  
|  [#5196](https://github.com/hapifhir/hapi-fhir/issues/5196) |  Previously, type-level expunge was allowed even if expunge operation was turned off. This is now fixed.  
|  [#5198](https://github.com/hapifhir/hapi-fhir/issues/5198) |  Resolved an issue with type-everything search operations (eg, /Patient/$everything), where not all page results were being returned if _count was specified to be the same value as the maximum page size to fetch.  
|  [#5212](https://github.com/hapifhir/hapi-fhir/issues/5212) |  Previously, when a CDS hook was registered and called with a empty context, the server returned a 500. This behaviour has been fixed.  
|  [#5219](https://github.com/hapifhir/hapi-fhir/issues/5219) |  Reindex batch job threw an exception when no results matched the reindex request. This has been corrected.  
|  [#5220](https://github.com/hapifhir/hapi-fhir/issues/5220) |  Fixed a race condition in RestfulClientFactory that could cause validateInitialized() to deadlock. Fixed a race condition in FhirContext initialization that could produce a 'this.myNameToResourceType is null' NPE. Fixed a shutdown hook memory leak in BaseApp that happened when the command threw an exception; this memory leak only affects code that calls App.main repeatedly which is probably only in test code.  
|  [#5230](https://github.com/hapifhir/hapi-fhir/issues/5230) |  batch2 jobs on MS SQL Server were failing to transition to FAILED state after max retrials for the job are exhausted. This is now fixed.  
|  [#5231](https://github.com/hapifhir/hapi-fhir/issues/5231) |  Previously, the `$mdm-link-history` operation would result in a 404 response when it was executed after a `$mdm-clear`. This has now been fixed by removing link history on `$mdm-clear`.  
|  [#5242](https://github.com/hapifhir/hapi-fhir/issues/5242) |  Previously, `$mdm-clear` failed to expunge `REDIRECTED` golden resources which left them as orphans. This is now fixed.  
|  [#5254](https://github.com/hapifhir/hapi-fhir/issues/5254) |  Previously, when bulk export is enabled the resource list would be generated and would not be able to add a custom resource to that list. Now once a custom resource is added the list is rebuilt.  
|  [#5256](https://github.com/hapifhir/hapi-fhir/issues/5256) |  Added RequestDetails as part of the parameters when cleaning up possible matches to enable interceptors to access it.  
|  |  Previously, executing a Group Bulk Export without defining the `_type` parameter would accidentally omit `Patient` and `Organization` types. This has been corrected.  
|  [#5268](https://github.com/hapifhir/hapi-fhir/issues/5268) |  Previously, the response status code set in a `MethodOutcome` of a Resource provider was not respected. This has been fixed.  
|  [#5275](https://github.com/hapifhir/hapi-fhir/issues/5275) |  Previously, when calling `$everything` operation on a Patient instance, it was possible to retrieve data related to another patient via a List or Group resources. This has been fixed.  
|  [#5276](https://github.com/hapifhir/hapi-fhir/issues/5276) |  Previously, GraphQL queries will error when using base resource search parameters such as '_id' after search parameter rebuild. This has been fixed.  
|  [#5295](https://github.com/hapifhir/hapi-fhir/issues/5295) |  A regression in the HAPI FHIR 6.6.0 JPA server meant that absolute resource references which also contained an identifier were rejected even if the server was configured to allow absolute references. This has been corrected.  
|  [#5297](https://github.com/hapifhir/hapi-fhir/issues/5297) |  Several fixes have been made to the IPS generator: 
  * The display names associated with several sections have been corrected to exactly match the LOINC definitions for their codes
  * Immunizations will now be ordered from most recent to least recent
  * IPS documents containing Consent resources for Advanced Directives could result in a crash
  * IPS documents containing Procedure resources for History of Procedures with a performed date could result in a crash
  * IPS documents containing AllergyIntolerance resources containing an occurrence but not a reaction date could result in a crash
  * IPS documents containing AllergyIntolerance resources containing an onset value in string format could result in a crash
  * IPS documents containing MedicationRequest resources with no associated Medication could result in a crash

  
|  [#5298](https://github.com/hapifhir/hapi-fhir/issues/5298) |  Under some circumstances, a large `$everything` operation could enter an infinite loop and eventually timeout with a failure. This has been corrected.  
|  [#5300](https://github.com/hapifhir/hapi-fhir/issues/5300) |  A bug in DefaultProfileValidationSupport in R5 mode caused it to return duplicates in the lists returned by `fetchAllStructureDefinitions()`, `fetchAllSearchParameters()`, etc. This has been corrected.  
|  [#5307](https://github.com/hapifhir/hapi-fhir/issues/5307) |  Some search parameters include parenthetical expressions (e.g. `(MedicationRequest.medication as Reference)`). The leading `(` was causing searches using parenthetical expressions to fail where the reference target was a contained resource. This has been corrected.  
|  [#5310](https://github.com/hapifhir/hapi-fhir/issues/5310) |  Update DSTU2 tags and security labels with support for `userSelected` and `version` elements. Also fix them on security labels in JPA storage.  
|  [#5312](https://github.com/hapifhir/hapi-fhir/issues/5312) |  Previously, issuing a reindex operation for resources on a specific partition would fail. This problem has been fixed.  
|  [#5316](https://github.com/hapifhir/hapi-fhir/issues/5316) |  Previously, performing a FHIR transaction containing both a conditional delete and a conditional update on the same resource would fail. This has been fixed.  
|  [#5322](https://github.com/hapifhir/hapi-fhir/issues/5322) |  Update DSTU3 validation resources to FHIR 3.0.2 instead of 3.0.1  
|  [#5330](https://github.com/hapifhir/hapi-fhir/issues/5330) |  Previously, the capability statement returned by the server would not declare conformance to IG when a bulk data export provider is registered with the server. This issue has been fixed.  
|  [#5331](https://github.com/hapifhir/hapi-fhir/issues/5331) |  Previously, the score field returned by $mdm-query-links operation would contain imprecise decimal values. This is now fixed and rounded to 4 decimal places.  
|  [#5333](https://github.com/hapifhir/hapi-fhir/issues/5333) |  A regression was introduced in 2023.08.R01 which caused binary storage prefixes to not be applied to exported binary blobs. This has been fixed.  
|  [#5336](https://github.com/hapifhir/hapi-fhir/issues/5336) |  Previously, on PostgreSQL, the $mdm-link-history operation would fail if all ids provided to a parameter are unknown, and the error will persist for all subsequent requests. This is now fixed.  
|  [#5339](https://github.com/hapifhir/hapi-fhir/issues/5339) |  Previously, Bulk Export will error when processing a resource if the patient compartment SearchParameter of that resource is not present. This has been fixed, the new behaviour is to ignore such resources.  
|  [#5344](https://github.com/hapifhir/hapi-fhir/issues/5344) |  Previously, issuing an expunge operation for resources on a specific partition would fail. This problem has been fixed.  
|  [#5349](https://github.com/hapifhir/hapi-fhir/issues/5349) |  Removed duplicate helperSvc bean in JpaConfig (also defined in imported MdmJpaConfig) to resolve BeanDefinitionOverrideException  
|  [#5353](https://github.com/hapifhir/hapi-fhir/issues/5353) |  Previously, when using revincludes and includes with iterate, while also using revincludes without iterate, the result omitted some resources that should have been included. This issue has now been fixed.  
|  [#5356](https://github.com/hapifhir/hapi-fhir/issues/5356) |  Clinical reasoning bug that did not invalidate resources in repository api global caches for terminology and libraries when updates/deletes were made.  
|  [#5388](https://github.com/hapifhir/hapi-fhir/issues/5388) |  Previously, with partitioning enabled and `UrlBaseTenantIdentificationStrategy` used, registering `SearchNarrowingInterceptor` would cause to incorrect resolution of `entry.request.url` parameter during transaction bundle processing. This has been fixed.  
|  [#5404](https://github.com/hapifhir/hapi-fhir/issues/5404) |  Cql translating bug where FHIRHelpers library function was erroring and blocking clinical reasoning content functionality  
|  [#5412](https://github.com/hapifhir/hapi-fhir/issues/5412) |  Previously, with Partitioning enabled, submitting a bundle request would return a response with the partition name displayed twice in `response.link` property. This has been fixed.  
|  [#5415](https://github.com/hapifhir/hapi-fhir/issues/5415) |  Previously, `$mdm-clear` jobs would fail on MSSQL. This is now fixed.  
|  [#5419](https://github.com/hapifhir/hapi-fhir/issues/5419) |  Previously, when `AllowAutoInflateBinaries` was enabled in `JpaStorageSettings` and bundles with multiple resources were submitted, binaries were created on the disk only for the first resource entry of the bundle. This has been fixed.  
|  [#5431](https://github.com/hapifhir/hapi-fhir/issues/5431) |  Previously, using VersionCanonicalizer to convert a CapabilityStatement from R5 to DSTU2 would fail. This is now fixed.  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html)
0.3 Changelog: 2023 
* Welcome to HAPI FHIR 
* [ 0.0  Table of Contents ](https://hapifhir.io/hapi-fhir/docs/introduction/table_of_contents.html)
* [ 0.1  Changelog: 2025 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html)
* [ 0.2  Changelog: 2024 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html)
* [ 0.3  Changelog: 2023 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html)
* [ 0.4  Changelog: 2022 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html)
* [ 0.5  Changelog: 2021 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html)
* [ 0.6  Changelog: 2020 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2020.html)
* [ 0.7  Changelog: 2019 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html)
* [ 0.8  Changelog: 2018 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html)
* [ 0.9  Changelog: 2017 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html)
* [ 0.10  Changelog: 2016 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html)
* [ 0.11  Changelog: 2015 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html)
* [ 0.12  Changelog: 2014 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html)
[ 0.4 Changelog: 2022 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)