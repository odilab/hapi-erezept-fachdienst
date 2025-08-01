---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html
crawled: 2025-08-01T14:05:06.730485
---

# Introduction Changelog 2021

#  0.5.1Changelog: 2021
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changelog-2021)
#  0.5.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changelog)
#  0.5.3HAPI FHIR 5.6.2 (Raccoon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#hapi-fhir-562-raccoon)
##  0.5.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information)
**Released:** 2021-12-17
**Codename:** (Raccoon)
##  0.5.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes)
|  [#3170](https://github.com/hapifhir/hapi-fhir/issues/3170) |  Fixed language code validation so that it is case insensitive (eg, en-US, en-us, EN-US, EN-us should all work)  
---|---|---  
#  0.5.4HAPI FHIR 5.6.1 (Raccoon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.6.2-3170)
##  0.5.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-1)
**Released:** 2021-12-03
**Codename:** (Raccoon)
##  0.5.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-1)
|  [#3198](https://github.com/hapifhir/hapi-fhir/issues/3198) |  Fixed a regression where packages would fail to load if HAPI-FHIR was operating in DATABASE binary storage mode.  
---|---|---  
|  [#3153](https://github.com/hapifhir/hapi-fhir/issues/3153) |  Updated UnknownCodeSystemWarningValidationSupport to allow the throwing of warnings if configured to do so.  
|  [#3158](https://github.com/hapifhir/hapi-fhir/issues/3158) |  Resource links were previously not being consistently created in cases where references were versioned and pointing to recently auto-created placeholder resources.  
|  |  Fixed a serious performance issue with the `$reindex` operation.  
|  [#3215](https://github.com/hapifhir/hapi-fhir/issues/3215) |  $everything operation returns a 500 error when querying for a page when _getpagesoffset is greater than or equal to 300. This has been corrected.  
|  |  MDM was throwing a NullPointerException when upgrading a match from POSSIBLE_MATCH to MATCH. This has been corrected.  
#  0.5.5HAPI FHIR 5.6.0 (Raccoon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.6.1-3198)
##  0.5.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-2)
**Released:** 2021-11-18
**Codename:** (Raccoon)
##  0.5.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-2)
|  [#2397](https://github.com/hapifhir/hapi-fhir/issues/2397) |  Added mdm support on $everything operation for patients by adding _mdm=true query parameter. Eg: /Patient/123/$everything?_mdm=true.  
---|---|---  
|  [#2754](https://github.com/hapifhir/hapi-fhir/issues/2754) |  Suport for annotation scanning in synthetic types such as weld proxies has been added. Thanks to GitHub user @tarekmamdouh for the pull request!  
|  [#2836](https://github.com/hapifhir/hapi-fhir/issues/2836) |  FHIR bundle batch is now processed in parallel by default and is configurable by DaoConfig.  
|  [#2850](https://github.com/hapifhir/hapi-fhir/issues/2850) |  Updated handling of MDM_AFTER_PERSISTED_RESOURCE_CHECKED pointcut to include additional MDM related info.  
|  [#2851](https://github.com/hapifhir/hapi-fhir/issues/2851) |  Allows to upload not-current version of LOINC ValueSet(s).  
|  [#2905](https://github.com/hapifhir/hapi-fhir/issues/2905) |  Added displayLanguage support for CodeSystem $lookup operation to filter out designation by language.  
|  [#2933](https://github.com/hapifhir/hapi-fhir/issues/2933) |  Fixed a regression which causes transactions with multiple identical ifNoneExist clauses to create duplicate data.  
|  [#2975](https://github.com/hapifhir/hapi-fhir/issues/2975) |  Two improvements have been made to the connection to Elasticsearch. First, null username and password values are now permitted. Second, multiple hosts are now permitted via the `setHosts()` method on the ElasticHibernatePropertiesBuilder, allowing you to connect to multiple elasticsearch clusters at once. Thanks to Dušan Marković for the contribution!  
|  |  Add new RuleBuilder options which allow you to specify additional resources and search parameters which match a given compartment. More explanations of the enhancements can be found in [the documentation](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization).  
|  [#2994](https://github.com/hapifhir/hapi-fhir/issues/2994) |  When performing ValueSet expansions and ValueSet-based code validations, the HAPI FHIR terminology service will allow ValueSets to be expanded if they contain an enumeration of codes, even if the corresponding CodeSystem resource can not be found. Response messages have also been improved to give better insight into whether a precalculated expansion was used. In addition, a new operation called `$invalidate-expansion` has been added that allows for manual invalidation of previously calculated ValueSet expansions.  
|  [#2999](https://github.com/hapifhir/hapi-fhir/issues/2999) |  Lucene/Elasticsearch indexing has been extended to string, token, and reference parameters. This can be enabled by the new `setAdvancedLuceneIndexing()` property of DaoConfig.  
|  [#3005](https://github.com/hapifhir/hapi-fhir/issues/3005) |  Open up the visibility of some methods in the generation of the Open API definition files to allow extenders to add support for OIDC authorization.  
|  [#3018](https://github.com/hapifhir/hapi-fhir/issues/3018) |  Previously, when a search query explicitly includes a search parameter that is for the same resource type but a different resource instance from the one(s) specified on the authorized list, the search narrowing interceptor would include both search parameters in the final query, resulting in an empty bundle being returned to the caller. Now, such a call will result in a 403 Forbidden error, making it more clear why no resources were returned.  
|  [#3020](https://github.com/hapifhir/hapi-fhir/issues/3020) |  Added documentation for `$partition-management-read-partition`. Added `$partition-management-list-partitions` operation and documentation.  
|  [#3022](https://github.com/hapifhir/hapi-fhir/issues/3022) |  A new parser that naticaly handles NDJSON format (automatically converting to/from FHIR Bundle resources in order to represent the collection being parsed/serialized) as been added. Thanks to Ben Li-Sauerwine for the contribution!  
|  [#3020](https://github.com/hapifhir/hapi-fhir/issues/3020) |  Added `$mdm-create-link` operation and documentation.  
|  [#3047](https://github.com/hapifhir/hapi-fhir/issues/3047) |  Inline match URL searches (e.g. search URLs for conditional creates, conditional updates, etc.) are now subject to the same security and access control checks as other searches.  
|  [#3071](https://github.com/hapifhir/hapi-fhir/issues/3071) |  A new parameter, `_id` has been added to the `Patient/$everything` type-level operation. This allows you to pass in multiple patients as arguments, e.g. `[base]/Patient/$everything?_id=1,2,3`. This call would retrieve everything for patients with IDs 1,2, and 3.  
|  [#3088](https://github.com/hapifhir/hapi-fhir/issues/3088) |  Previously, chained searches were not able to traverse reference fields within contained resources. This enhancement adds the ability to traverse the reference fields of contained resources when those fields refer to discrete resources.  
|  [#3089](https://github.com/hapifhir/hapi-fhir/issues/3089) |  LOINC copyright notice is now taken from CodeSystem.copyright entry from loinc.xml input file.  
|  [#3100](https://github.com/hapifhir/hapi-fhir/issues/3100) |  Previously, only contained resources that are referenced directly by the containing resource were being indexed. This enhancement indexes the fields of contained resources that are referenced by other contained resources and uses these new indices in chained searches. Note: in order to make use of this new capability, it must be enabled via a configuration parameter and the repository must be re-indexed.  
|  [#3106](https://github.com/hapifhir/hapi-fhir/issues/3106) |  Further enhances the features added by issue 3100 to allow chained searches across any combination of discrete and contained references.  
|  [#3110](https://github.com/hapifhir/hapi-fhir/issues/3110) |  Subscription criteria in the HAPI FHIR JPA server now supports an optional alternate syntax of `[*]` (all resources of all types) and `[resourcename,resourcename,...]` (all resources of the given types. Note that no search parameters may be specitied with this syntax.  
|  [#3110](https://github.com/hapifhir/hapi-fhir/issues/3110) |  Added a functionality to deny unknown extensions.  
|  [#3131](https://github.com/hapifhir/hapi-fhir/issues/3131) |  Provided a Remote Terminology Service implementation for the $lookup Operation.  
|  [#3033](https://github.com/hapifhir/hapi-fhir/issues/3033) |  When performing a FHIR transaction using the JPA server where the transaction contains many identical inline match URLs (as is the case with recent versions of Synthea), HAPI FHIR will now avoid repeateed identical lookups while processing the transaction.  
|  [#3061](https://github.com/hapifhir/hapi-fhir/issues/3061) |  When ingesting Synthea data (or simnilarly structured transaction bundles) into a JPA server, a redundant resource ID lookup has been optimized out. This should particularly speed up larger sized transactions.  
|  [#2790](https://github.com/hapifhir/hapi-fhir/issues/2790) |  Support for the `_language` search parameter has been dropped from the JPA server. This search parameter was specified in FHIR DSTU1 but was dropped in later versions. It is rarely used in practice and imposes an indexing cost, so it has now been removed. A custom search parameter may be used in order to achieve the same functionality if needed.  
|  [#2843](https://github.com/hapifhir/hapi-fhir/issues/2843) |  HAPI-FHIR provides indexing on Canonical Types as references. However, the option to [treat absolute references as local](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html#jpa-server-configuration-options) was being ignored for those indexed canonicals. This has been corrected. Now, if you have set `getTreatBaseUrlsAsLocal()` and HAPI-FHIR detects a canonical which starts with such a url, that prefix will be stripped, and indexing will occur normally.  
|  [#2904](https://github.com/hapifhir/hapi-fhir/issues/2904) |  Change getPathItem method in OpenApiInterceptor from private to protected  
|  |  The $mdm-clear operation has been changed to use Spring Batch.  
|  |  During transactions, any resources that were PUT or POSTed with a conditional URL now receive extra validation. There is now a final storage step which ensures that the stored resource actually matches the conditional URL.  
|  [#2991](https://github.com/hapifhir/hapi-fhir/issues/2991) |  This PR eliminates the search coordinator threadpool, and executes searches synchronously on the HTTP client thread. The idea of using a separate pool was supposed to help improve server scalability, but ultimately created false bottlenecks and reduced the utility of monitoring infrastructure so it has been eliminated.  
|  |  Prevent _expunge and _cascade from being used on the same DELETE operation  
|  [#3017](https://github.com/hapifhir/hapi-fhir/issues/3017) |  Removed loinc.xml file from application, which was used as a fallback if not provided when uploading a CodeSystem. it is now required as input for LOINC CodeSystem upload.  
|  [#3063](https://github.com/hapifhir/hapi-fhir/issues/3063) |  The Saxon-HE library has been removed as a mandatory dependency from the converter library. Thanks to Jing Tang for the contribution!  
|  [#2424](https://github.com/hapifhir/hapi-fhir/issues/2424) |  This issue involves searching for a resource with a DATE parameter that is specified at only the YEAR level of precision. When searching at a higher level of precision, no results are matched. This issue is fixed now.  
|  [#2790](https://github.com/hapifhir/hapi-fhir/issues/2790) |  The SearchParameter canonical URLs exported by the JPA server have been adjusted to match the URLs specified in the FHIR specification.  
|  [#2793](https://github.com/hapifhir/hapi-fhir/issues/2793) |  Previously, when using the Expunge Everything operation, caches could retain old invalid values. This has been corrected. Thanks to [Ben Li-Sauerwine](https://github.com/theGOTOguy) for the fix!  
|  [#2799](https://github.com/hapifhir/hapi-fhir/issues/2799) |  Certain ValueSet validation/expansion operations failed with a 'no transaction' error on Postgresql. This has been corrected. Thanks to @tyfoni-systematic for reporting!  
|  [#2837](https://github.com/hapifhir/hapi-fhir/issues/2837) |  The :not modifier does not currently work for observations with multiple codes for the search. This is fixed.  
|  [#2876](https://github.com/hapifhir/hapi-fhir/issues/2876) |  Fixed a bug wherein an NPE could be thrown by the MDM module interceptor if an incoming resource had a tag with no system.  
|  [#2883](https://github.com/hapifhir/hapi-fhir/issues/2883) |  Open API docs failed to load if a custom context path is set. This has been corrected.  
|  [#2901](https://github.com/hapifhir/hapi-fhir/issues/2901) |  Processing transactions with AutoversionAtPaths set should create those resources (if AutoCreatePlaceholders is set) and use latest version as expected  
|  [#2902](https://github.com/hapifhir/hapi-fhir/issues/2902) |  Fixed a bug wherein includes were not being included on revincludes.  
|  [#2920](https://github.com/hapifhir/hapi-fhir/issues/2920) |  Previously, validation against bcp47 (urn:ietf:bcp:47) as a language would fail validation if the region was absent. This has been fixed, and the validate operation will now correctly validate simple languages, e.g. `nl` instead of requiring `nl-DE` or `nl-NL`  
|  [#2923](https://github.com/hapifhir/hapi-fhir/issues/2923) |  $lookup operation cache was based on system and code, it becomes a defect after adding displayLanguage support. Problem is now fixed.  
|  [#2935](https://github.com/hapifhir/hapi-fhir/issues/2935) |  No resource returned when search with percent sign. Problem is now fixed  
|  [#2958](https://github.com/hapifhir/hapi-fhir/issues/2958) |  Fixed issue where the processing of queries like Procedure?patient= before a cache search would cause the parameter key to be removed. Additionally, ensured that requests like Procedure?patient= cause HTTP 400 Bad Request instead of HTTP 500 Internal Error.  
|  [#2962](https://github.com/hapifhir/hapi-fhir/issues/2962) |  Added a new DaoConfig setting called `setElasticSearchIndexPrefix(String prefix)` which will cause Hibernate search to prefix all of its tables with the provided value.  
|  [#2967](https://github.com/hapifhir/hapi-fhir/issues/2967) |  Previously, the system would only traverse references to discrete resources while performing a chained search. This fix adds support for traversing references to contained resources as well, with the limitation that the reference to the contained resource must be the last reference in the chain.  
|  [#2973](https://github.com/hapifhir/hapi-fhir/issues/2973) |  CLI `smileutil help {command}` returns `Unknown command` which should return the usage of `command`. This has been corrected.  
|  |  Fixed a bug where two identical tags in parallel entries being created in a batch would fail.  
|  [#2995](https://github.com/hapifhir/hapi-fhir/issues/2995) |  CodeSystem version is copied to ValueSet.compose.include.version on loinc terminology upload to support versioned ValueSet expansion.  
|  [#3012](https://github.com/hapifhir/hapi-fhir/issues/3012) |  Fixes migration of VARBINARY columns on MS SQLServer.  
|  [#3016](https://github.com/hapifhir/hapi-fhir/issues/3016) |  A new customized Hibernate dialect is now used for HAPI FHIR JPA on Postgresql Database. This dialect uses the PG `oid` datatype instead of `text` for CLOB columns, avoiding CLOBs being seen as abandoned by the Postgresql VACUUMLO tool.  
|  [#3031](https://github.com/hapifhir/hapi-fhir/issues/3031) |  Fixes a bug that was causing a null pointer exception to be thrown during database migrations that add or drop indexes.  
|  |  Fixed a bug in processing large batch requests containing many modifying entries. PUT/POST/DELETE operations now occur sequentially, instead of in parallel.  
|  [#3046](https://github.com/hapifhir/hapi-fhir/issues/3046) |  When using deferred model scanning in highly parallelized environments, a crash could sometimes occur during parse/serialize operations.  
|  [#3097](https://github.com/hapifhir/hapi-fhir/issues/3097) |  Update documentation for $mdm-match  
|  [#3103](https://github.com/hapifhir/hapi-fhir/issues/3103) |  Fixes a regression caused by an earlier release, which would cause the `$everything` operation to throw an SQL error when run under Postgresql.  
|  [#3109](https://github.com/hapifhir/hapi-fhir/issues/3109) |  `$binary-access-write` operation returns a DocumentReference with no content. This has been corrected.  
|  [#3118](https://github.com/hapifhir/hapi-fhir/issues/3118) |  MySQL 5.7 maps BLOB columns to LONGVARBINARY and the migrator did not take this into account. This has been fixed.  
|  [#3123](https://github.com/hapifhir/hapi-fhir/issues/3123) |  When performing paging queries over large result sets in the JPA server (> 1000 results), SQL could be generated that violated the Oracle RDBMS limit of 1000 parameters. This has been resolved.  
|  [#3138](https://github.com/hapifhir/hapi-fhir/issues/3138) |  Previously, the package registry would not work correctly when externalized binary storage was enabled. This has been corrected.  
|  [#3608](https://github.com/hapifhir/hapi-fhir/issues/3608) |  Documentation on offset paging with _offset doesn't mention possible duplicate entries across different pages. The documentation has been updated, and a warning log is added to notify this behaviour as well.  
|  [#3116](https://github.com/hapifhir/hapi-fhir/issues/3116) |  The BaseResourceModifiedJson class had a redundant field called myId containing the payload ID. This has been removed.  
#  0.5.6HAPI FHIR 5.5.4 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.6.0-2397)
##  0.5.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-3)
**Released:** 2021-12-06
**Codename:** (Quasar)
##  0.5.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-3)
|  |  Fixed a serious performance issue with the `$reindex` operation.  
---|---|---  
#  0.5.7HAPI FHIR 5.5.3 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.5.4-3211)
##  0.5.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-4)
**Released:** 2021-11-23
**Codename:** (Quasar)
##  0.5.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-4)
|  [#3088](https://github.com/hapifhir/hapi-fhir/issues/3088) |  Previously, chained searches were not able to traverse reference fields within contained resources. This enhancement adds the ability to traverse the reference fields of contained resources when those fields refer to discrete resources.  
---|---|---  
|  [#3100](https://github.com/hapifhir/hapi-fhir/issues/3100) |  Previously, only contained resources that are referenced directly by the containing resource were being indexed. This enhancement indexes the fields of contained resources that are referenced by other contained resources and uses these new indices in chained searches. Note: in order to make use of this new capability, it must be enabled via a configuration parameter and the repository must be re-indexed.  
|  [#3106](https://github.com/hapifhir/hapi-fhir/issues/3106) |  Further enhances the features added by issue 3100 to allow chained searches across any combination of discrete and contained references.  
#  0.5.8HAPI FHIR 5.5.2 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.5.3-3088)
##  0.5.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-5)
**Released:** 2021-10-07
**Codename:** (Quasar)
##  0.5.8.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-5)
|  [#2935](https://github.com/hapifhir/hapi-fhir/issues/2935) |  No resource returned when search with percent sign. Problem is now fixed  
---|---|---  
|  [#2967](https://github.com/hapifhir/hapi-fhir/issues/2967) |  Previously, the system would only traverse references to discrete resources while performing a chained search. This fix adds support for traversing references to contained resources as well, with the limitation that the reference to the contained resource must be the last reference in the chain.  
#  0.5.9HAPI FHIR 5.5.1 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.5.2-2935)
##  0.5.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-6)
**Released:** 2021-08-30
**Codename:** (Quasar)
##  0.5.9.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-6)
|  [#2933](https://github.com/hapifhir/hapi-fhir/issues/2933) |  Fixed a regression which causes transactions with multiple identical ifNoneExist clauses to create duplicate data.  
---|---|---  
#  0.5.10HAPI FHIR 5.4.2 (Pangolin)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.5.1-2933)
##  0.5.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-7)
**Released:** 2021-07-06
**Codename:** (Pangolin)
##  0.5.10.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-7)
|  [#2721](https://github.com/hapifhir/hapi-fhir/issues/2721) |  Support for LOINC 2.70 has been added.  
---|---|---  
|  [#2735](https://github.com/hapifhir/hapi-fhir/issues/2735) |  The $evaluate-measure now works on a partitioned server.  
|  [#2748](https://github.com/hapifhir/hapi-fhir/issues/2748) |  A new tag storage mode called **Inline Tag Mode** tas been added. In this mode, all tags are stored directly in the serialized resource body in the database, instead of using dedicated tables. This has significant performance advantages when storing resources with many distinct tags (i.e. many tags that are unique to each resource, as opposed to being reused across multiple resources).  
|  [#2717](https://github.com/hapifhir/hapi-fhir/issues/2717) |  FHIR transactions in the JPA server that perform writes will now aggressively pre-fetch as many entities as possible at the very start of transaction processing. This can drastically reduce the number of round-trips, especially as the number of resources in a transaction gets bigger.  
|  [#2717](https://github.com/hapifhir/hapi-fhir/issues/2717) |  A new setting has been added to the DaoConfig called Tag Versioning Mode. This setting controls whether a single collection of tags/profiles/security labels is maintained across all versions of a single resource, or whether each version of the resource maintains its own independent collection. Previously each version always maintained an independent collection, which is useful sometimes, but is often not useful and can affect performance.  
|  [#2697](https://github.com/hapifhir/hapi-fhir/issues/2697) |  DELETE _expunge=true has been converted to use Spring Batch. It now simply returns the jobId of the Spring Batch job while the job continues to run in the background. A new operation called $expunge-delete has been added to provide more fine-grained control of the delete expunge operation. This operation accepts an ordered list of URLs to be delete expunged and an optional batch-size parameter that will be used to perform the delete expunge. If no batch size is specified in the operation, then the value of DaoConfig.getExpungeBatchSize() is used.  
|  [#2732](https://github.com/hapifhir/hapi-fhir/issues/2732) |  The ConceptMap.group.element.display storage size limit has been increased to 500 characters.  
|  [#2760](https://github.com/hapifhir/hapi-fhir/issues/2760) |  If two authorization compartments apply to the same targets and share the same compartment name, then instead of creating a new compartment, the rule builder now adds the new owner to the list of owners in the existing compartment.  
|  [#2773](https://github.com/hapifhir/hapi-fhir/issues/2773) |  Flyway migration used to enforce order by default. This has been changed so now the default behaviour is out of order migrations are permitted. Strict order can be enforced via the new strict-order flag if required.  
|  [#2624](https://github.com/hapifhir/hapi-fhir/issues/2624) |  ValueSet expansion did not correctly preserve the order if multiple codes were included in a single inclusion block.  
|  [#2739](https://github.com/hapifhir/hapi-fhir/issues/2739) |  Too many MDM candidates matching could result in an OutOfMemoryError. Candidate matching is now limited to the value of IMdmSettings.getCandidateSearchLimit(), default 10000.  
|  [#2741](https://github.com/hapifhir/hapi-fhir/issues/2741) |  A regression caused the JPA Server History operation to not return paging links in responses. This has been corrected.  
|  [#2747](https://github.com/hapifhir/hapi-fhir/issues/2747) |  Added null checks to MDM resource interceptor in order to avoid NPEs.  
|  [#2748](https://github.com/hapifhir/hapi-fhir/issues/2748) |  The SQL generated for the `_profile` search parameter did not use all of the columns on the tag index table, resulting on poor performance on MySQL. This has been corrected.  
|  [#2762](https://github.com/hapifhir/hapi-fhir/issues/2762) |  A regression was introduced in 2760 where a READ compartment could get collapsed into a WRITE compartment. This has been corrected.  
|  [#2764](https://github.com/hapifhir/hapi-fhir/issues/2764) |  Searches for mdm-expanded references such as Observation?subject:mdm=123 were getting denied by access rules that did not recognize the :mdm suffix. This has been corrected.  
#  0.5.11HAPI FHIR 5.4.1 (Pangolin)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.4.2-2721)
##  0.5.11.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-8)
**Released:** 2021-06-15
**Codename:** (Pangolin)
##  0.5.11.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-8)
|  [#2509](https://github.com/hapifhir/hapi-fhir/issues/2509) |  Pagination returned incorrect offset and count in the previous link of the last page when total element count was one more than multiple of page size. Problem is now fixed  
---|---|---  
|  [#2652](https://github.com/hapifhir/hapi-fhir/issues/2652) |  Settings have been added to the JPA Server DaoConfig to enable/disable various individual kinds of scheduled tasks.  
|  [#2653](https://github.com/hapifhir/hapi-fhir/issues/2653) |  When performing a conditional create operation on a JPA server, the system will now verify that the conditional URL actually matches the data supplied in the resource body, and aborts the conditional create if it does not.  
|  [#2672](https://github.com/hapifhir/hapi-fhir/issues/2672) |  Support has been added to the JPA server for `_include` and `_revinclude` where the value is a qualified star, e.g. `_include=Observation:*`.  
|  [#2675](https://github.com/hapifhir/hapi-fhir/issues/2675) |  A new interceptor ValidationMessageSuppressingInterceptor has been added. This interceptor can be used to selectively suppress specific vaLidation messages.  
|  [#2676](https://github.com/hapifhir/hapi-fhir/issues/2676) |  When performing non-query cache JPA searches (i.e. searches with `Cache-Control: no-store`) the loading of `_include` and `_revinclude` will now factor the maximum include count.  
|  [#2676](https://github.com/hapifhir/hapi-fhir/issues/2676) |  A new config option has been added to the DaoConfig that causes generated SQL statements to account for potential null values in HAPI FHIR JPA date index rows. Nulls are no longer ever used in this table after HAPI FHIR 5.3.0, but legacy data may still have nulls.  
|  [#2676](https://github.com/hapifhir/hapi-fhir/issues/2676) |  A new setting has been added to the DaoConfig that allows the maximum number of `_include` and `_revinclude` resources to be added to a single search page result. In addition, the include/revinclue processor have been redesigned to avoid accidentally overloading the server if an include/revinclude would return unexpected massive amounts of data.  
|  [#2681](https://github.com/hapifhir/hapi-fhir/issues/2681) |  A new DaoConfig setting called Mass Ingestion Mode has been added. This mode enables rapid data ingestion by skipping a number of unnecessary checks during backloading.  
|  [#2692](https://github.com/hapifhir/hapi-fhir/issues/2692) |  A new Pointcut has been added that is invoked when a new Bulk Export is initiated.  
|  [#2702](https://github.com/hapifhir/hapi-fhir/issues/2702) |  The JPA server terminology uploader now supports uploading ICD-10-CM (US Edition) using the native format for that vocabulary.  
|  [#2712](https://github.com/hapifhir/hapi-fhir/issues/2712) |  AuthorizationInterceptor can now be used to authorize bulk export requests  
|  [#2688](https://github.com/hapifhir/hapi-fhir/issues/2688) |  Conditional URL lookups in the JPA server will now explicitly specify a maximum fetch size of 2, avoiding fetching more data that won't be used inadvertently in some situations.  
|  [#2688](https://github.com/hapifhir/hapi-fhir/issues/2688) |  FHIR Transaction duplicate record checks are now performed without any database interactions or SQL statements, reducing the processing load associated with FHIR transactions by at least a small amount.  
|  [#2653](https://github.com/hapifhir/hapi-fhir/issues/2653) |  When performing a conditional create/update/delete on a JPA server, if the match URL contained a plus character, this character was interpreted as a space (per legacy URL encoding rules) even though this has proven to not be the intended behaviour in real life applications. Plus characters will now be treated literally as a plus character in these URLs.  
|  [#2695](https://github.com/hapifhir/hapi-fhir/issues/2695) |  Bulk import batch jobs are now activated in a local scheduled task, making bulk import jobs better able to take advantage of large clusters.  
|  [#2665](https://github.com/hapifhir/hapi-fhir/issues/2665) |  When performing a FHIR transaction containing a conditional create, references to that resource were inadvertently replaced with contained references."  
|  [#2672](https://github.com/hapifhir/hapi-fhir/issues/2672) |  A concurrency error was fixed when using client assigned IDs on a highly concurrent server with resource deletion disabled.  
|  [#2674](https://github.com/hapifhir/hapi-fhir/issues/2674) |  A null-pointer exception was fixed when a ResponseTerminologyDisplayInterceptor is registered and a search or read response returns a resource with code value that in turn returns a null code lookup.  
|  [#2676](https://github.com/hapifhir/hapi-fhir/issues/2676) |  Subscription notifications will no longer be triggered by default in response to changes that do not increment the resource version (e.g. `$meta-add` and `$meta-delete`). A new DaoConfig setting has been added to make this configurable.  
|  [#2674](https://github.com/hapifhir/hapi-fhir/issues/2674) |  When myDaoConfig.setDefaultTotalMode(SearchTotalModeEnum.ACCURATE) and there are zero search results on an _id search, An Index Out of Bounds error was thrown. This has been corrected.  
|  [#2682](https://github.com/hapifhir/hapi-fhir/issues/2682) |  Fixes the problem that FHIR package IDs were incorrectly treated as case sensitive when being loaded, causing loads to fail when dependencies were declared with a different case than in the package itself.  
|  [#2693](https://github.com/hapifhir/hapi-fhir/issues/2693) |  Constraint errors were not always auto-retried even when configured to do so on certain platforms (particularly Postgresql) where constraint names are auto converted to lower case. Thanks to Bruno Hedman for the pull request!  
|  [#2705](https://github.com/hapifhir/hapi-fhir/issues/2705) |  When searching by source, if deleted resources are matched, the search returned an incorrect size. This has been corrected.  
|  [#2695](https://github.com/hapifhir/hapi-fhir/issues/2695) |  The _filter search parameter was incorrectly included in the server capability statement if it was disabled on the server. This has been corrected.  
#  0.5.12HAPI FHIR 5.4.0 (Pangolin)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.4.1-2509)
##  0.5.12.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-9)
**Released:** 2021-05-20
**Codename:** (Pangolin)
##  0.5.12.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-9)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Commons-Lang3 (Core): 3.9 -> 3.12.0
  * Commons-Text (Core): 1.7 -> 1.9
  * Commons-Codec (Core): 1.14 -> 1.15
  * Commons-IO (Core): 2.6 -> 2.8.0
  * Guava (Core): 30.1-jre -> 30.1.1-jre
  * Jackson (Core): 2.12.1 -> 2.12.3
  * Woodstox (Core): 6.2.3 -> 6.2.5
  * Apache Jena (Core/RDF): 3.16.0 -> 3.17.0
  * Gson (JPA): 2.8.5 -> 2.8.6
  * Caffeine (JPA): 2.7.0 -> 3.0.1
  * Hibernate (JPA): 5.4.26.Final -> 5.4.30.Final
  * Hibernate Search (JPA): 6.0.0.Final -> 6.0.3.Final
  * Spring (JPA): 5.3.3 -> 5.3.6
  * Spring Batch (JPA): 4.2.3.RELEASE -> 4.3.2
  * Spring Data (JPA): 2.4.2 -> 2.5.0
  * Commons DBCP2 (JPA): 2.7.0 -> 2.8.0
  * ElasticSearch Client (JPA): 7.10.2 -> 7.12.1
  * Thymeleaf (Testpage Overlay): 3.0.11.RELEASE -> 3.0.12.RELEASE
  * JAnsi (CLI): 2.1.1 -> 2.3.2
  * JArchivelib (CLI): 1.0.0 -> 1.1.0

  
---|---|---  
|  [#2417](https://github.com/hapifhir/hapi-fhir/issues/2417) |  Support for the FHIR _list search parameter.  
|  [#2332](https://github.com/hapifhir/hapi-fhir/issues/2332) |  Terminology storage is now skipped for placeholder ValueSet and ConceptMap resources.  
|  [#2384](https://github.com/hapifhir/hapi-fhir/issues/2384) |  When a REST server returns a failure because no method matched the request parameters, the resulting OperationOutcome will now correctly set the issue type to `not-supported`. Thanks to Jari Maijenburg for the pull request!  
|  [#2401](https://github.com/hapifhir/hapi-fhir/issues/2401) |  Group Bulk exports are now supported. You can export all data for a Group of Patients via the `/Group/[id]/$export` endpoint for any resource type which contains a patient compartment. The _typeFilter and _since criteria are currently not supported at this level, but may eventually be  
|  [#2403](https://github.com/hapifhir/hapi-fhir/issues/2403) |  Optionally support '_contained' resource search by enabling the indexing on the contained resources in the ModelConfig.  
|  [#2407](https://github.com/hapifhir/hapi-fhir/issues/2407) |  When using the JPA server in partitioned mode with a partition interceptor, the interceptor is now called even for resource types that can not be placed in a non-default partition (e.g. SearchParameter, CodeSystem, etc.). The interceptor may return null or default in this case, but can include a non-null partition date if needed.  
|  [#2409](https://github.com/hapifhir/hapi-fhir/issues/2409) |  In MDM matching rules, support has been added for using FHIRPath expressions instead of Resource Path expressions via the `fhirPath` field in a field matcher  
|  [#2433](https://github.com/hapifhir/hapi-fhir/issues/2433) |  Support has been added for MDM expansion during Group bulk export. Calling a group export via `/Group/123/$export?_mdm=true` will cause Bulk Export to not only match group members, but also any MDM-matched patients, and their related golden record patients  
|  [#2441](https://github.com/hapifhir/hapi-fhir/issues/2441) |  Support has been added to the JPA server for indexing and searching using the `_contained` parameter, which allows searching using chained parameters that chain into contained resources. This feature is disabled by default but can be enabled via a setting on the ModelConfig object.  
|  [#2445](https://github.com/hapifhir/hapi-fhir/issues/2445) |  Support has been added for patient level Bulk export. This can be done via the `/Patient/$export` endpoint. Also, support has been added for setting Cache-Control header to no-cache for Bulk Export requests.  
|  [#2446](https://github.com/hapifhir/hapi-fhir/issues/2446) |  Auto-created placeholder reference targets now have an extension with the URL `http://hapifhir.io/fhir/StructureDefinition/resource-placeholder` and a value of `true`.  
|  [#2449](https://github.com/hapifhir/hapi-fhir/issues/2449) |  Adds interceptors for the following functionality: 
  * Data normalization (n11n) - removing unwanted characters (control, etc. as defined by the requirements) 
  * Data standardization (s13n) - normalizing data by ensuring word spacing and character cases are uniform 
  * Data validation - making sure that addresses / emails are validated 

  
|  [#2478](https://github.com/hapifhir/hapi-fhir/issues/2478) |  Added matching based on extension, when given the path to a fhir resource the matcher will take the extensions and match if the url and string value are the same  
|  [#2488](https://github.com/hapifhir/hapi-fhir/issues/2488) |  Two new server interceptors have been added that can be used to map codes and populate code display names respectively using the server terminology services.  
|  [#2499](https://github.com/hapifhir/hapi-fhir/issues/2499) |  When using the _mdm parameter during Group Bulk Export, resources written out will now contain an extension with the url `https://hapifhir.org/associated-patient-golden-resource/` identifying which golden resource the target resource belongs to.  
|  [#2506](https://github.com/hapifhir/hapi-fhir/issues/2506) |  A new server interceptor has been added that allows servers to implement lenient search mode, where unknown search parameters are ignored if an optional HTTP Prefer header is provided.  
|  [#2506](https://github.com/hapifhir/hapi-fhir/issues/2506) |  The server generated CapabilityStatment will now include supported Profile declarations for FHIR R4+.  
|  [#2520](https://github.com/hapifhir/hapi-fhir/issues/2520) |  Add support for `:mdm` search parameter qualifier on reference search parameters. Details about enabling this feature can be found [in the documentation](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html).  
|  [#2521](https://github.com/hapifhir/hapi-fhir/issues/2521) |  The server generated CapabilityStatement now reflects whether RDF/Turtle is supported by the server. In addition, the ResponseHighlightingInterceptor will now provide some TTL support.  
|  [#2521](https://github.com/hapifhir/hapi-fhir/issues/2521) |  The automatically generated CapabilityStatement for R4+ will now incude the list of supported revinclude values.  
|  [#2523](https://github.com/hapifhir/hapi-fhir/issues/2523) |  A new Validation Support Module has been added called UnknownCodeSystemWarningValidationSupport. This module allows validation to produce a warning but not an error if a code being validated references an unknown code system.  
|  [#2525](https://github.com/hapifhir/hapi-fhir/issues/2525) |  A new optional parameter has been added to the `ValueSet/$expand` operation. When provided a value of `true`, the operation will include the concept hierarchy in the expansion response.  
|  [#2534](https://github.com/hapifhir/hapi-fhir/issues/2534) |  Add new pointcut `STORAGE_TRANSACTION_PROCESSED`, which fires after all operations in a transaction have executed.  
|  [#2537](https://github.com/hapifhir/hapi-fhir/issues/2537) |  It is now possible t create narrative generator templates that apply to any custom strucures including custom extension structures.  
|  [#2547](https://github.com/hapifhir/hapi-fhir/issues/2547) |  Added new NUMERIC mdm matcher for matching phone numbers. Also added NUMERIC phonetic encoder to support adding NUMERIC encoded search parameter (e.g. if searching for matching phone numbers is required by mdm candidate searching).  
|  [#2560](https://github.com/hapifhir/hapi-fhir/issues/2560) |  A new interceptor called `OpenApiInterceptor` has been added. This interceptor can be registered against FHIR Servers to automatically add support for OpenAPI / Swagger.  
|  [#2576](https://github.com/hapifhir/hapi-fhir/issues/2576) |  A new header called `X-Upsert-Extistence-Check` can now be added to JPA server _Create with Client-Assigned ID_ operations (aka Upsert) in order to improve performance when loading data that is known to not exist by skipping the resource existence check.  
|  [#2579](https://github.com/hapifhir/hapi-fhir/issues/2579) |  When using the JPA server in offset mode, the count+offset information is now passed to the SQL query, resulting in better performance. Thanks to Tuomo Ala-Vannesluoma for the pull request!  
|  [#2585](https://github.com/hapifhir/hapi-fhir/issues/2585) |  When automatically creating a placeholder reference that is set to auto-populate identifiers, logic has been improved. If the reference does not contain an identifier, but the inline match URL does, the identifier found in the match URL will be added to the target. If both are populated, they will both be added to the target.  
|  [#2590](https://github.com/hapifhir/hapi-fhir/issues/2590) |  When resources are created using package load, the new resources will use the same IDs as were provided in with the resource definitions in the package, if they exist. If the ids are numeric, a prefix of 'npm-' will be added.  
|  [#2644](https://github.com/hapifhir/hapi-fhir/issues/2644) |  Support for validating BCP-47 (language) codes against the FHIR Languages and All-Languages ValueSets has been improved.  
|  [#2659](https://github.com/hapifhir/hapi-fhir/issues/2659) |  The MSSQL-specific index definition for the ForcedId table in the JPA server has been enhanced to include an INCLUDE() clause, which should significantly improve performance.  
|  [#2662](https://github.com/hapifhir/hapi-fhir/issues/2662) |  It is now possible to declare database migrations as being mandatory even when running in schema initialization mode.  
|  [#2457](https://github.com/hapifhir/hapi-fhir/issues/2457) |  A regression in HAPI FHIR 5.3.0 resulted in concurrent searches being executed in a sequential (and not parallel) fashion in some circumstances.  
|  [#2462](https://github.com/hapifhir/hapi-fhir/issues/2462) |  The HAPI FHIR parser will now preserve the order of contained resources across round-trip parse/serialize passes.  
|  [#2462](https://github.com/hapifhir/hapi-fhir/issues/2462) |  Several optimizations have been made to the JPA server transaction processor that should result in improved performance, particularly when processing large transaction Bundles, such as transactions containing many entries, or transactions containing very large entries.  
|  [#2576](https://github.com/hapifhir/hapi-fhir/issues/2576) |  A new PID-to-forced-ID cache, and a new optional Match-URL-to-PID cache have been added. These can improve write performance when doing large loads.  
|  [#2576](https://github.com/hapifhir/hapi-fhir/issues/2576) |  The generated search SQL statements have been optimized for simple JPA server searches containing only one parameter. In this case, an unnecessary JOIN has been removed.  
|  [#2446](https://github.com/hapifhir/hapi-fhir/issues/2446) |  DaoConfig setting for [Populate Identifier In Auto Created Placeholder Reference Targets](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-storage/ca/uhn/fhir/jpa/api/config/DaoConfig.html#setPopulateIdentifierInAutoCreatedPlaceholderReferenceTargets\(boolean\)) now defaults to `true`.  
|  [#2513](https://github.com/hapifhir/hapi-fhir/issues/2513) |  PointCutLatch now works with IPointcut instead of Pointcut.  
|  [#2634](https://github.com/hapifhir/hapi-fhir/issues/2634) |  Custom Search Parameters may now have FHIRPath expressions up to 500 characters long, up from 200.  
|  [#2648](https://github.com/hapifhir/hapi-fhir/issues/2648) |  The ConsentInterceptor no longer fully runs on calls to `/metadata` or during the `$meta` operation.  
|  [#2655](https://github.com/hapifhir/hapi-fhir/issues/2655) |  Added a new configuration option to DaoConfig, `setInternalSynchronousSearchSize()`, this controls the loadSynchronousUpTo() during internal operations such as delete with expunge, and certain CodeSystem searches.  
|  [#1644](https://github.com/hapifhir/hapi-fhir/issues/1644) |  An SQL syntax error was corrected when using the JPA server terminology services to pre-expand valuesets when using Oracle Database.  
|  [#1731](https://github.com/hapifhir/hapi-fhir/issues/1731) |  When storing resources in the JPA server, extensions in `Resource.meta` were not preserved, nor were any contents in `Bundle.entry.resource.meta`. Both of these things are now correctly persisted and returned. Thanks to Sean McIlvenna for reporting!  
|  [#1953](https://github.com/hapifhir/hapi-fhir/issues/1953) |  A crash was fixed when performing a FHIR read on a partitioned server, where the requested ID is not known. Thanks to Umberto Cappellini for reporting!  
|  [#2404](https://github.com/hapifhir/hapi-fhir/issues/2404) |  Supplementary characters in the parameters might cause errors when calculating hash values  
|  [#2417](https://github.com/hapifhir/hapi-fhir/issues/2417) |  A NullPointerException was corrected when indexing resources containing an indexed Period field that had a start but not an end defined.  
|  [#2426](https://github.com/hapifhir/hapi-fhir/issues/2426) |  When using the Generic Client, search invocations where the parameters are supplied using `.where(Map)` did not include search modifiers such as `:exact` in the search URL. Thanks to GitHub user @granadacoder for reporting this issue!  
|  [#2458](https://github.com/hapifhir/hapi-fhir/issues/2458) |  `HasParam#doGetQueryParameterQualifier()` returned a malformed modifier. For example, the modifier for `_has:Observation:patient:code=123` was returned as `Observation:code:123` when it should be `:Observation:patient:code`. This has been corrected.  
|  [#2462](https://github.com/hapifhir/hapi-fhir/issues/2462) |  References from a contained resource to the containing resource are now possible in the JPA server.  
|  [#2466](https://github.com/hapifhir/hapi-fhir/issues/2466) |  When performainfg a search using a date search parameter, invalid values (e.g. `date=foo`) resulted in a confusing error message. This has been improved.  
|  [#2468](https://github.com/hapifhir/hapi-fhir/issues/2468) |  Running a manual reindex of data failed on a partitioned server with an interceptor error. This has been corrected. Thanks to Ajay Shekar for reporting!  
|  [#2471](https://github.com/hapifhir/hapi-fhir/issues/2471) |  An NPE was fixed when performing highly concurrent system requests while using the ResourceVersionConflictResolutionStrategy interceptor.  
|  [#2479](https://github.com/hapifhir/hapi-fhir/issues/2479) |  the create-package command of the HAPI FHIR CLI was not correctly adding the `fhirVersions` section to the generated package.json. This has been fixed  
|  [#2493](https://github.com/hapifhir/hapi-fhir/issues/2493) |  A database deadlock in Postgresql was observed when uploading large terminology CodeSystems using deferred uploading. Thanks to Tyge Folke Nielsen for reporting and suggesting a fix!  
|  [#2505](https://github.com/hapifhir/hapi-fhir/issues/2505) |  An incorrect path caused the select2 library to fail to load in the HAPI FHIR testpage overlay modue. Thanks to Ari Ruotsalainen for reporting!  
|  [#2515](https://github.com/hapifhir/hapi-fhir/issues/2515) |  Fixed issues with application of survivorship rules when matching golden record to a single resource  
|  [#2526](https://github.com/hapifhir/hapi-fhir/issues/2526) |  Added Operation section to Conformance Statement.  
|  [#2528](https://github.com/hapifhir/hapi-fhir/issues/2528) |  An issue with compartment definitions in R5 models was fixed. This issue caused some authorization rules to reject valid requests. Thanks to Patrick Palacin for reporting!  
|  [#2533](https://github.com/hapifhir/hapi-fhir/issues/2533) |  When issuing a request for a specific Resource and also specifying an _include param, the referenced resource is not returned when there is only 1 version of the referenced resource available. When there are more than 1 versions available, the referenced resource is returned in the response bundle.  
|  [#2535](https://github.com/hapifhir/hapi-fhir/issues/2535) |  An issue with package installer involving logical StructureDefinition resources was fixed. Package registry will no longer attempt to generate a snapshot for logical StructureDefinition resources if one is not already provided in the resource definition.  
|  [#2543](https://github.com/hapifhir/hapi-fhir/issues/2543) |  When issuing a request for a specific Resource and also specifying an _include param, the proper historical referenced resource is not returned when there are more than 1 versions of the referenced resource available, after the reference has been changed from the original version 1 to some other version. When there are more than 1 versions available, and the referring resource had previously referred to version 1 but now refers to version 4, the resource returned in the response bundle is for version 1.  
|  [#2556](https://github.com/hapifhir/hapi-fhir/issues/2556) |  Fixed a bug which would cause Bulk Export to fail when run in a partitioned environment.  
|  [#2571](https://github.com/hapifhir/hapi-fhir/issues/2571) |  Added support for deleting resources to BundleBuilder via method `addTransactionDeleteEntry`.  
|  [#2577](https://github.com/hapifhir/hapi-fhir/issues/2577) |  When running the `$apply-codesystem-delta-add` operation, code properties were not correctly saved. Thanks to Hanan Awwad for the pull request!  
|  [#2620](https://github.com/hapifhir/hapi-fhir/issues/2620) |  In the JPA server, if a tag was defined with the exact same system and code as a security label on a different resource, the tag would be incorrectly filed as a security label (and vice versa). This has been corrected.  
|  [#2620](https://github.com/hapifhir/hapi-fhir/issues/2620) |  When creating resources in a JPA server under highly concurrent conditions, creating a new tag across multiple threads could result in a race condition leaving an invalid entry in the tag cache. This resulted in new instances of this tag being unavailable for creation.  
|  [#2623](https://github.com/hapifhir/hapi-fhir/issues/2623) |  When performing a search via GraphQL, token search parameters were not properly parsed. Thanks to Jari Maijenburg for the pull request!  
|  [#2632](https://github.com/hapifhir/hapi-fhir/issues/2632) |  Certain calls to the $evaluate-measure operation could result in a NullPointerException. This is now corrected.  
|  [#2641](https://github.com/hapifhir/hapi-fhir/issues/2641) |  A vulnerability in the FHIR History operation was resolved. When running HAPI FHIR JPA server on a large database (i.e. containing a large number of resources), if a malicious user performs a large number of concurrent FHIR History (`_history`) operations, an expensive `COUNT()` statement can consume all available database resources and ultimately trigger resource exhaustion and disable the server. A huge thanks to **Zachary Minneker at Security Innovation** who discovered and submitted a responsible disclosure of this issue.  
|  [#2643](https://github.com/hapifhir/hapi-fhir/issues/2643) |  When using Auto-Version references in the JPA server, if an auto-versioned reference within a FHIR transaction pointed to a resource that did not actually change during the transaction (e.g. an update/PUT where the resource body was unchanged from the existing version), the reference would point to an incremented version number even though none existed. This has been corrected.  
|  [#2647](https://github.com/hapifhir/hapi-fhir/issues/2647) |  The new Match URL cache suffered from potential cache poisoning if multiple threads performed a condiitonal create operation at the same time.  
|  [#2654](https://github.com/hapifhir/hapi-fhir/issues/2654) |  The id of ValueSet resources in ValueSet expansions was null. This has been corrected. The id of the expanded value set is now the same as the id of the value set that was expanded.  
|  [#2661](https://github.com/hapifhir/hapi-fhir/issues/2661) |  Fixed a bug where delete with expunge would throw a LazyInitializationException if there were delete conflicts, and the expunge batch size was smaller than the available list of resources to delete.  
|  [#2194](https://github.com/hapifhir/hapi-fhir/issues/2194) |  The Testpage Overlay now suppresses authorization headers from the output headers. Thanks to Tuomo Ala-Vannesluoma for the pull request!  
|  [#2454](https://github.com/hapifhir/hapi-fhir/issues/2454) |  Addressed the following CVE report by bumping the minor version for Jetty in the root POM: 
  * [CVE-2020-27223](https://nvd.nist.gov/vuln/detail/CVE-2020-27223)

  
#  0.5.13HAPI FHIR 5.3.3 (Odyssey)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.4.0-0)
##  0.5.13.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-10)
**Released:** 2021-04-26
**Codename:** (Odyssey)
##  0.5.13.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-10)
|  [#2417](https://github.com/hapifhir/hapi-fhir/issues/2417) |  A NullPointerException was corrected when indexing resources containing an indexed Period field that had a start but not an end defined.  
---|---|---  
#  0.5.14HAPI FHIR 5.3.2 (Odyssey)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.3.3-2417)
##  0.5.14.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-11)
**Released:** 2021-04-14
**Codename:** (Odyssey)
##  0.5.14.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-11)
|  [#2533](https://github.com/hapifhir/hapi-fhir/issues/2533) |  When issuing a request for a specific Resource and also specifying an _include param, the referenced resource is not returned when there is only 1 version of the referenced resource available. When there are more than 1 versions available, the referenced resource is returned in the response bundle.  
---|---|---  
|  [#2543](https://github.com/hapifhir/hapi-fhir/issues/2543) |  When issuing a request for a specific Resource and also specifying an _include param, the proper historical referenced resource is not returned when there are more than 1 versions of the referenced resource available, after the reference has been changed from the original version 1 to some other version. When there are more than 1 versions available, and the referring resource had previously referred to version 1 but now refers to version 4, the resource returned in the response bundle is for version 1.  
#  0.5.15HAPI FHIR 5.3.1 (Odyssey)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.3.2-2533)
##  0.5.15.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-12)
**Released:** 2021-03-11
**Codename:** (Odyssey)
##  0.5.15.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-12)
|  [#2407](https://github.com/hapifhir/hapi-fhir/issues/2407) |  When using the JPA server in partitioned mode with a partition interceptor, the interceptor is now called even for resource types that can not be placed in a non-default partition (e.g. SearchParameter, CodeSystem, etc.). The interceptor may return null or default in this case, but can include a non-null partition date if needed.  
---|---|---  
#  0.5.16HAPI FHIR 5.3.0 (Odyssey)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.3.1-2407)
##  0.5.16.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-13)
**Released:** 2021-02-18
**Codename:** (Odyssey)
##  0.5.16.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-13)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * SLF4j (All Modules): 1.7.28 -> 1.7.30
  * Jackson (All Modules): 2.11.2 -> 2.12.1
  * Woodstox (XML FHIR Parser): 4.4.1 -> 6.2.3 (Note that the Maven groupId has changed from `org.codehaus.woodstox` to `com.fasterxml.woodstox` and the Maven artifactId has changed from `woodstox-core-asl` to `woodstox-core` for this library)
  * Spring (JPA): 5.2.3.RELEASE -> 5.2.9.RELEASE
  * Datasource-Proxy (JPA): 1.5.1 -> 1.7
  * Apache Commons Collections4 (JPA): 4.3 -> 4.4
  * Jetty (JPA Starter): 9.4.30.v20200611 -> 9.4.35.v20201120
  * Guava (JP): 29.0-jre -> 30.1-jre
  * Hibernate ORM (JPA Server): 5.4.22.FINAL -> 5.4.26.FINAL
  * Spring (JPA Server): 5.2.9.RELEASE -> 5.3.3
  * Spring Data (JPA Server): 2.2.0.RELEASE -> 2.4.2
  * Hibernate Search (JPA Server): 5.11.5.FINAL -> 6.0.0.Final
  * Lucene(HAPI FHIR JPA Server): 5.5.5 -> 8.7.0
  * Spring Boot (JPA Starter): 2.2.6.RELEASE -> 2.4.1
  * JANSI (CLI): 1.18 -> 2.1.1
  * PH-Schematron (SCH Validator): 5.2.0 -> 5.6.5
  * PH-Commons (SCH Validator): 5.3.8 -> 9.5.4

  
---|---|---  
|  [#2054](https://github.com/hapifhir/hapi-fhir/issues/2054) |  Two new switches have neen added to FhirInstanceValidator to suppress optional warning messages. Thanks to Anders Havn for the pull request!  
|  [#2177](https://github.com/hapifhir/hapi-fhir/issues/2177) |  Redesigning the Enterprise Master Patient Index solution to a Master Data Management solution. The new MDM solution supports other FHIR resources where EMPI only allowed Person resource to be used. For example, if MDM is occurring on a patient, we will create a new Patient, and tag that patient as a Golden Record. This means that several things have changed: 
  * Provider methods that pointed to type of Person are now server-level operations in which you specify a resource type.
  * Link updating and querying no longer rely on Person IDs, but instead on arbitrary resource ids, depending on the resource type you are referring to in MDM.
  * Change to the EMPI config to require a list of mdmTypes.

  
Code-level changes include the following changes: 
  * hapi-fhir-server-empi and hapi-fhir-jpaserver-empi Maven projects were renamed to hapi-fhir-server-mdm and hapi-fhir-jpaserver-mdm
  * All classes were refactored to use correct terms, e.g. Golden Resource in place of Person
  * Message channel was renamed from `empi` to `mdm`
  * Subscriptions were renamed to `mdm-RESOURCE_TYPE`, where RESOURCE_TYPE is an MDM type configured in mdmTypes section of the configuration file
  * Configuration file was renamed from empi-rules.json to mdm-rules.json
  * Log file was changed from empi-troubleshooting.log to mdm-troubleshooting.log

  
|  [#2182](https://github.com/hapifhir/hapi-fhir/issues/2182) |  When a unique index SearchParameter violation is blocked, the error message will now include the ID of the relevant SearchParameter, in order to make troubleshooting easier.  
|  [#2191](https://github.com/hapifhir/hapi-fhir/issues/2191) |  Added a new IResourceChangeListenerRegistry service and modified SearchParamRegistry and SubscriptionRegistry to use it. This service contains an in-memory list of all registered {@link IResourceChangeListener} instances along with their caches and other details needed to maintain those caches. Register an {@link IResourceChangeListener} instance with this service to be notified when resources you care about are changed. This service quickly notifies listeners of changes that happened on the local process and also eventually notifies listeners of changes that were made by remote processes.  
|  [#2198](https://github.com/hapifhir/hapi-fhir/issues/2198) |  It is now possible for read operations (read/history/search/etc) in a partitioned server to read across more than one partition if the partitioning interceptor indicates multiple partitions.  
|  [#2213](https://github.com/hapifhir/hapi-fhir/issues/2213) |  Non release (i.e. SNAPSHOT) builds of HAPI FHIR will now include the Git revision hash as well as the build date in the version string that is logged on initialization, and included in the default server X-Powered-By string. Release builds are not affected by this change.  
|  [#2221](https://github.com/hapifhir/hapi-fhir/issues/2221) |  The error message returned by the transaction processor has been improved for the case where a transaction uses an unsupported/disabled resource types.  
|  [#2234](https://github.com/hapifhir/hapi-fhir/issues/2234) |  When performing a conditional create/update containing the email search parameter, any `+` characters will now be interpreted as actually being a plus symbol instead of being unescaped into a space character. This is technically a deviation from how URLs should be parsed, but allows for a sensible behaviour in a spot where no spaces are allowed.  
|  [#2237](https://github.com/hapifhir/hapi-fhir/issues/2237) |  It is now possible to use a parameter of type `IBaseResource` or `IBaseBundle` as the parameter on a @Transaction method in a plain server.  
|  [#2261](https://github.com/hapifhir/hapi-fhir/issues/2261) |  Optionally supports storage and search in canonical form of the quantity value which is defined by 'http://unitsofmeasure.org'; please check ModelConfig for the configuration. No changes were made to the existing behaviour.  
|  [#2264](https://github.com/hapifhir/hapi-fhir/issues/2264) |  The interceptor framework will now recognize and invoke `@Hook` methods that have an access level of public, protected, or default. Previously only public methods were recognized.  
|  [#2264](https://github.com/hapifhir/hapi-fhir/issues/2264) |  A new interceptor called the [Repository Validating Interceptor](https://hapifhir.io/hapi-fhir/docs/validation/repository_validating_interceptor.html) has been added. This new interceptor allows a HAPI FHIR JPA Server to declare rules about mandatory profiles that all resources stored to the server must declare conformance and/or correctly validate against.  
|  [#2282](https://github.com/hapifhir/hapi-fhir/issues/2282) |  When performing a ValueSet expansion where the valueset to be expanded includes a display name for concepts it is explicitly including, this display name will be propagated to the expansion if no other display is available. Thanks to Hanan Awwad for the contribution!  
|  [#2310](https://github.com/hapifhir/hapi-fhir/issues/2310) |  A new utility called FHIRPathResourceGeneratorR4 has been added. This class can be used to build and populate a resource model object using FHIRPath expressions. Thanks to Marcel P for the contribution!  
|  [#2323](https://github.com/hapifhir/hapi-fhir/issues/2323) |  The JPA server has a new setting on the `ModelConfig` bean called "AutoVersionReferencesAtPaths". Using this setting, the server can be configured to add the current target resource version ID to any resource references found in a resource being stored. In addition, a new setting has been added to the JPA ModelConfig bean that allows `_include` statements to respect versioned references, and actually include the correct version for the reference.  
|  |  Added support for the $evaluate-measure Operation as part of adding CQL support.  
|  [#2302](https://github.com/hapifhir/hapi-fhir/issues/2302) |  The JPA server generated SQL for date search parameters has been streamlined to avoid the use of redundant OR expressions that slow down performance in some cases.  
|  [#2190](https://github.com/hapifhir/hapi-fhir/issues/2190) |  Updates to Hibernate Search require a full reindexing of all indexed fulltext data, which is held in Lucene or Elasticsearch. Users using elasticsearch for fulltext indexing must upgrade to Elasticsearch 7.10.0.  
|  [#2197](https://github.com/hapifhir/hapi-fhir/issues/2197) |  The resource IDs for several LOINC resources were corrected. Thanks to Steven Wagers for the pull request!  
|  [#2264](https://github.com/hapifhir/hapi-fhir/issues/2264) |  The experimental `TransactionBuilder` helper class has been renamed to `BundleBuilder` as it now has utility methods for working with other types of Bundles too.  
|  [#2290](https://github.com/hapifhir/hapi-fhir/issues/2290) |  In the JPA server. the SQL datatype used to index quantities has been changed from `NUMBER(19,2)` to `double precision` (or equivalents depending on platform). This improves the query support for ssearching on very small quantities.  
|  [#2340](https://github.com/hapifhir/hapi-fhir/issues/2340) |  The `@ResourceDef` annotation has been marked as inheritable, so it does not need to be explicitly added to custom structures that extend built-in structures. Thanks to Marcelo Avancini for the pull request!  
|  [#2372](https://github.com/hapifhir/hapi-fhir/issues/2372) |  ElasticsearchHibernatePropertiesBuilder will now reject any REST url which is protocol-aware. Protocol information should be set in the protocol field of the builder.  
|  [#2175](https://github.com/hapifhir/hapi-fhir/issues/2175) |  Expanding a ValueSet using a filter now evaluates the display with left-matching by string token, case-insensitive.  
|  [#2180](https://github.com/hapifhir/hapi-fhir/issues/2180) |  Loading a package without a description was causing a null pointer exception. This has been fixed.  
|  [#2183](https://github.com/hapifhir/hapi-fhir/issues/2183) |  The `CodeSystem/$subsumes` operation inadvertantly reversed the meanings of the CodeA and CodeB parameters, resulting in `subsumes` and `subsumed-by` responses being reversed. This has been corrected. Thanks to Rob Hausam for reporting!  
|  [#2192](https://github.com/hapifhir/hapi-fhir/issues/2192) |  ApacheRestfulClientFactory now uses system properties for proxy configuration. Thanks to Vladimir Nemergut for the pull request!  
|  [#2195](https://github.com/hapifhir/hapi-fhir/issues/2195) |  When performing a ValueSet expansion with a filter a large pre-expanded ValueSet (more than 1000 codes), the filter failed to find concepts appearing after the first thousand. This has been corrected.  
|  [#2205](https://github.com/hapifhir/hapi-fhir/issues/2205) |  The new JPA SearchBuilder failed to perform FHIR Searches on Oracle DB with an invalid SQL error. This has been corrected.  
|  [#2208](https://github.com/hapifhir/hapi-fhir/issues/2208) |  When performing a JPA server search on a partitioned server, searches with only the `_id` parameter and no other parameters did not include the partition selector in the generated SQL, resulting in leakage across partitions. Thanks to GitHub user @jtheory for reporting!  
|  [#2211](https://github.com/hapifhir/hapi-fhir/issues/2211) |  The recent EMPI project accidentally caused a split package in ca.uhn.fhir.rest.server. This has been corrected. Thanks to Bill Denton for the pull request!  
|  [#2214](https://github.com/hapifhir/hapi-fhir/issues/2214) |  When using the validator from within the JPA server, validating CapabilityStatement resources failed with an error when trying to load linked SearchParameter resources. This has been corrected.  
|  [#2215](https://github.com/hapifhir/hapi-fhir/issues/2215) |  When using a partitioned JPA server, auto-create placeholder targets did not work if the partition interceptor was registered against the server (e.g. for a multitenancy configuration). This has been corrected. Thanks to Rob Whelan for reporting!  
|  [#2262](https://github.com/hapifhir/hapi-fhir/issues/2262) |  Sorting of search results was not working for MySQL, MSSQL and MariaDB due to recent changes made to handle sorting of nullable columns. This has now been fixed.  
|  [#2265](https://github.com/hapifhir/hapi-fhir/issues/2265) |  The new optimized SQL Generator introduced in HAPI FHIR 5.2.0 did not correctly bind variables for SQL Server queries, making the search functionality unusable. This has been corrected.  
|  [#2269](https://github.com/hapifhir/hapi-fhir/issues/2269) |  A database index in the JPA server was added in HAPI FHIR 5.2.0 and Smile CDR 2020.11 that exceeded the maximum index length in MySQL, preventing server upgrades on that database platform. This has been corrected.  
|  [#2271](https://github.com/hapifhir/hapi-fhir/issues/2271) |  Attempts to load IG packs when partitioning was enabled, resulted in nullpointer exceptions. This has now been fixed and IG packs and conformance resources will be loaded to the DEFAULT partition.  
|  [#2273](https://github.com/hapifhir/hapi-fhir/issues/2273) |  An incorrect HTML resource path led to a utility JavaScript library failing to load in the TestPage Overlay module. Thanks to Alejandro Medina for the pull request!  
|  [#2297](https://github.com/hapifhir/hapi-fhir/issues/2297) |  When performing a FHIR create using the HAPI FHIR client, if the payload is a Bundle resource the individual resources in the Bundle had their IDs removed by the client during payload serialization. This has been corrected.  
|  [#2298](https://github.com/hapifhir/hapi-fhir/issues/2298) |  A recent change inadvertently caused an issue with DB migration utility such that it would attempt to drop indexes even when the `dry-run` option was specified. This has been fixed.  
|  [#2299](https://github.com/hapifhir/hapi-fhir/issues/2299) |  The BulkExportDaoSvc bean was forgotten from the BaseConfig context. It has been added.  
|  [#2309](https://github.com/hapifhir/hapi-fhir/issues/2309) |  In the JPA server, HumanName.name.text was not being indexed and therefore was not searchable. This has been corrected.  
|  [#2327](https://github.com/hapifhir/hapi-fhir/issues/2327) |  The $expand filter parameter was not matching the ValueSet display value in all cases. E.g. a ValueSet with name 'abc def ghi' would match 'abc def' and 'def' but not 'def ghi'. This has been corrected so the ValueSet will match the filter if any substring of the ValueSet display value matches the $expand filter.  
|  [#2361](https://github.com/hapifhir/hapi-fhir/issues/2361) |  Unrecognized search param prefix strings were silently discarded. This is now an error.  
|  [#2365](https://github.com/hapifhir/hapi-fhir/issues/2365) |  A bug in the InMemoryResourceMatcher caused AND clauses to be treated as OR clauses. Thanks to Jari Maijenburg for the report and pull request!  
|  [#2369](https://github.com/hapifhir/hapi-fhir/issues/2369) |  When using the Consent Interceptor, the `startOperation` method was not invoked for search paging requests. This has been corrected. Thanks to Tue Toft Nørgård for reporting!  
|  [#2361](https://github.com/hapifhir/hapi-fhir/issues/2361) |  As of version 2.69, the LOINC Top2000CommonLabResultsSi.csv and Top2000CommonLabResultsUs.csv became optional. The Terminology Loader Service has been updated to reflect this change.  
|  [#2220](https://github.com/hapifhir/hapi-fhir/issues/2220) |  An important security issue with the JPA Server was solved. This issue applies only to JPA servers running in partitioned mode. When performing searches on a partitioned server, search results from previously cached searches against different partitions may be returned, potentially leaking data across partitions. This issue has been resolved.  
|  [#2229](https://github.com/hapifhir/hapi-fhir/issues/2229) |  Remove support for the `upload-igpack` command. This command is no longer supported, as the IGPack format has been withdrawn and replaced with the FHIR NPM Package specification, which is also supported by HAPI FHIR  
#  0.5.17HAPI FHIR 5.2.1 (Numbats)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#change5.3.0-0)
##  0.5.17.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#release-information-14)
**Released:** 2021-01-20
**Codename:** (Numbats)
##  0.5.17.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html#changes-14)
|  [#2298](https://github.com/hapifhir/hapi-fhir/issues/2298) |  A recent change inadvertently caused an issue with DB migration utility such that it would attempt to drop indexes even when the `dry-run` option was specified. This has been fixed.  
---|---|---  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html)
0.5 Changelog: 2021 
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
[ 0.6 Changelog: 2020 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2020.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)