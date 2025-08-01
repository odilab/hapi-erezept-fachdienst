---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html
crawled: 2025-08-01T14:05:48.070467
---

# Introduction Changelog 2024

#  0.2.1Changelog: 2024
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changelog-2024)
#  0.2.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changelog)
#  0.2.3HAPI FHIR 7.6.1 (Despina)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#hapi-fhir-761-despina)
##  0.2.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information)
**Released:** 2024-12-18
**Codename:** (Despina)
##  0.2.3.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions)
## Device membership in Patient Compartment
As of 7.6.1, versions of FHIR below R5 now consider the `Device` resource's `patient` Search Parameter to be in the Patient Compartment. The following features are affected:
  * Patient Search with `_revInclude=*`
  * Patient instance-level `$everything` operation
  * Patient type-level `$everything` operation
  * Automatic Search Narrowing
  * Bulk Export


Previously, there were various shims in the code that permitted similar behaviour in these features. Those shims have been removed. The only remaining component is [Advanced Compartment Authorization](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization), which can still be used to add other Search Parameters into a given compartment.
##  0.2.3.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes)
|  |  The `patient` search parameter for the `Device` resource has been added to the Patient Compartment for the purposes of:
  * AuthorizationInterceptor - SearchNarrowingInterceptor - $everything Operation - Patient/Group Bulk Export This means that a search for $everything on a patient will return devices associated to this patient. This used to be possible via the [Advanced Compartment Authorization](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization), but that would only apply to authorization, and not these other use cases. This solution adds this Search Parameter to all places where compartment membership would be checked.

  
---|---|---  
|  [#6538](https://github.com/hapifhir/hapi-fhir/issues/6538) |  In HAPI FHIR 8.0.0, transaction processing has been significantly improved thanks to ticket [#6460](https://github.com/hapifhir/hapi-fhir/pull/6460). This enhancement has been partially backported to the 7.6.x release line in order to provide partial improvement prior to the release of HAPI FHIR 8.0.0.  
|  [#6538](https://github.com/hapifhir/hapi-fhir/issues/6538) |  In HAPI FHIR 8.0.0, validation processing has been significantly improved thanks to ticket [#6508](https://github.com/hapifhir/hapi-fhir/pull/6508). This enhancement has been partially backported to the 7.6.x release line in order to provide partial improvement prior to the release of HAPI FHIR 8.0.0.  
|  [#6424](https://github.com/hapifhir/hapi-fhir/issues/6424) |  Changed VersionSpecificWorkerContextWrapper to never expire StructureDefinition entries in the cache, which is needed because the validator makes assumptions about StructureDefinitions never changing.  
|  [#6502](https://github.com/hapifhir/hapi-fhir/issues/6502) |  Support ReferenceParam in addition to UriParam for `_profile` in queries using the SearchParameterMap to match the change in the specification from DSTU3 to R4.  
#  0.2.4HAPI FHIR 7.6.0 (Despina)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.6.1-6536)
##  0.2.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-1)
**Released:** 2024-11-15
**Codename:** (Despina)
##  0.2.4.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-1)
# Measures and Care Gaps
## TimeZone Header
DQM `$care-gaps` and `$evaluate-measure` will convert parameters `periodStart` and `periodEnd` according to a timezone supplied by the client, not the server timezone as it was previously. Clients can leverage this functionality by passing in a new `Timezone` header (ex: `America/Denver`). If nothing is supplied, it will default to UTC.
## CareGaps Operation Parameters
### Parameters removed
Certain `$care-gaps` operation parameters have been dropped, because they are not used or likely to be implemented
  * `topic`
  * `practitioner` is now callable via `subject` parameter
  * `organization`
  * `program`


### Parameters added:
  * `measureIdentifier` now is available to resolve measure resources for evaluation
  * `nonDocument` is a new optional parameter that defaults to `false` which returns standard `document` bundle for `$care-gaps`. If `true`, this will return summarized subject bundle with only detectedIssue.


# SDC $populate operation
The `subject` parameter of the `Questionnaire/$populate` operation has been changed to expect a `Reference` as specified in the SDC IG.
##  0.2.4.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-1)
|  |  The version of a few dependencies have been bumped to more recent versions (dependent HAPI modules listed in brackets): 
  * org.hl7.fhir.core (Base): 6.3.18 -> 6.4.0
  * Bower/Moment.js (hapi-fhir-testpage-overlay): 2.27.0 -> 2.29.4
  * htmlunit (Base): 3.9.0 -> 3.11.0
  * Elasticsearch (Base): 8.11.1 -> 8.14.3

  
---|---|---  
|  [#6182](https://github.com/hapifhir/hapi-fhir/issues/6182) |  A new Pointcut called `MDM_SUBMIT_PRE_MESSAGE_DELIVERY` has been added. If you wish to customize the `ResourceModifiedJsonMessage` sent to the broker, you can do so by implementing this Pointcut, and returning `ResourceModifiedJsonMessage`.  
|  [#6184](https://github.com/hapifhir/hapi-fhir/issues/6184) |  Added a configuration setting for the TTL of HTTP connections to IRestfulClientFactory. The following implementations have been updated to respect this new setting: 1. ApacheRestfulClientFactory 2. OkHttpRestfulClientFactory 3. HapiFhirCliRestfulClientFactory Thanks to Alex Kopp and Alex Cote for the contribution!  
|  [#6210](https://github.com/hapifhir/hapi-fhir/issues/6210) |  Batch instance ID and chunk ID have been added to the logging context so that they can be automatically added to batch-related messages in the log.  
|  [#6263](https://github.com/hapifhir/hapi-fhir/issues/6263) |  The method `FhirTerser.cloneInto()` has been enhanced to copy the `resource` field of a `Reference`, in addition to the fields that are accessible via the structure definition.  
|  [#6313](https://github.com/hapifhir/hapi-fhir/issues/6313) |  The `STORAGE_PARTITION_DELETED` pointcut has been added and will be called upon deleting a partition using the `$partition-management-delete-partition` operation.  
|  [#6325](https://github.com/hapifhir/hapi-fhir/issues/6325) |  A new configuration option, `PartitionSettings#setPartitionIdsInPrimaryKeys(boolean)` configures the query engine to include the partitioning column in search query joins.  
|  [#6357](https://github.com/hapifhir/hapi-fhir/issues/6357) |  Upgrade the Clinical Reasoning module to the latest release of 3.13.0. This update comes with several changes and feature enhancements to CPG and dQM clinical-reasoning operations. Please review associated ticket and upgrade.md for detailed list of changes.  
|  [#6359](https://github.com/hapifhir/hapi-fhir/issues/6359) |  Remote Terminology validation has been enhanced to support output parameter `issues` for the $validate-code operation.  
|  [#6366](https://github.com/hapifhir/hapi-fhir/issues/6366) |  Add plumbing for combining IConsentServices with different vote tally strategies  
|  [#6370](https://github.com/hapifhir/hapi-fhir/issues/6370) |  When using the FHIR `TerserUtil` to merge two resource, if one resource has real data in a particular field, and the other resource has a `data-absent-reason` extension in the same field, the real data will be given precedence in the merged resource, and the extension will be ignored.  
|  [#6375](https://github.com/hapifhir/hapi-fhir/issues/6375) |  A new experimental JPA setting has been added to JpaStorageSettings which causes searches for token SearchParameters to include a predicate on the HASH_IDENTITY column even if it is not needed because other hashes are in use.  
|  [#6445](https://github.com/hapifhir/hapi-fhir/issues/6445) |  Add Multimap versions of the search() methods to Repository to support queries like `Patient?_tag=a&_tag=b`  
|  [#6253](https://github.com/hapifhir/hapi-fhir/issues/6253) |  A cache has been added to the validation services layer which results in improved validation performance. Thanks to Max Bureck for the contribution!  
|  [#6323](https://github.com/hapifhir/hapi-fhir/issues/6323) |  A synchronization choke point was removed from the model object initialization code, reducing the risk of multi-thread contention.  
|  [#6345](https://github.com/hapifhir/hapi-fhir/issues/6345) |  Date searches using equality would perform badly as the query planner does not know that our LOW_VALUE columns are always < HIGH_VALUE columns, and HIGH_VALUE is always > LOW_VALUE columns. These queries have been fixed to account for this.  
|  [#6489](https://github.com/hapifhir/hapi-fhir/issues/6489) |  Change the migrator to avoid table locks when adding an index. This allows systems to continue running during upgrade.  
|  [#6261](https://github.com/hapifhir/hapi-fhir/issues/6261) |  Upgrading to Jakarta had caused a problem with the version of java-simple-mail that was in use. This has been updated to be conformant with the Jakarta Mail APIs. Thanks to Thomas Papke(@thopap) for the contribution!  
|  [#6341](https://github.com/hapifhir/hapi-fhir/issues/6341) |  The CachingValidationSupport cache for concept translations will now keep up to 500000 translations instead of the previous 5000. This will be made configurable in a future release.  
|  |  Contained resources which arrive without assigned IDs are now assigned GUIDs, as opposed to monotonically increasing numeric IDs. This avoids a whole class of issues related to processing order and collisions.  
|  [#6203](https://github.com/hapifhir/hapi-fhir/issues/6203) |  Previously, the SubscriptionValidatingInterceptor would allow the creation/update of a REST hook subscription where the endpoint URL property is not prefixed with http[s]. This issue is fixed.  
|  [#6206](https://github.com/hapifhir/hapi-fhir/issues/6206) |  A resource leak during database migration on Oracle could cause a failure `ORA-01000 maximum open cursors for session`. This has been corrected. Thanks to Jonas Beyer for the contribution!  
|  [#6216](https://github.com/hapifhir/hapi-fhir/issues/6216) |  Previously, searches combining the `_text` query parameter (using Lucene/Elasticsearch) with query parameters using the database (e.g. `identifier` or `date`) could miss matches when more than 500 results match the `_text` query parameter. This has been fixed, but may be slow if many results match the `_text` query and must be checked against the database parameters.  
|  [#6218](https://github.com/hapifhir/hapi-fhir/issues/6218) |  Previously, when disabling the 'Enforce Referential Integrity on Write' setting, referential integrity was still partially enforced when posting a resource with an invalid reference. This has now been fixed.  
|  [#6231](https://github.com/hapifhir/hapi-fhir/issues/6231) |  The PatientIdPartitionInterceptor could on rare occasion select the incorrect partition for a resource. This has been corrected. In order for the wrong partition to be selected, the following three things need to be true: 1) there are multiple values of a patient compartment for a resource (see https://hl7.org/fhir/R4/compartmentdefinition-patient.html) 2) a patient compartment value is a non-Patient reference 3) the search parameter of the incorrect value needs to come alphabetically before the search parameter of the correct value. For example, if a QuestionnaireResponse has subject Patient/123 and author Organization/456, then since 'author' appears ahead of 'subject' alphabetically it would incorrectly determine the partition. The fix changed the partition selection so that it now only matches on Patient references.  
|  [#6262](https://github.com/hapifhir/hapi-fhir/issues/6262) |  Previously, when a `extension` was passed in as a part of the CDS hooks request, it would result in a `400 service not found`. This behaviour has now been fixed.  
|  [#6285](https://github.com/hapifhir/hapi-fhir/issues/6285) |  Updated the Reindex Batch2 job to allow for an additional step that will check to ensure that no pending 'reindex' work is needed. This was done to prevent a bug in which value set expansion would not return all the existing CodeSystem Concepts after a reindex call, due to some of the concepts being deferred to future job runs. As such, `$reindex` operations on CodeSystems will no longer result in incorrect value set expansion when such an expansion is called 'too soon' after a $reindex operation.  
|  [#6290](https://github.com/hapifhir/hapi-fhir/issues/6290) |  Previously, a specific migration task was using the `TRIM()` function, which does not exist in MSSQL 2012. This was causing migrations targeting MSSQL 2012 to fail. This has been corrected and replaced with usage of a combination of LTRIM() and RTRIM(). Thanks to Primož Delopst at Better for the contribution!  
|  [#6305](https://github.com/hapifhir/hapi-fhir/issues/6305) |  Previously, when having StorageSettings#getIndexMissingFields() == IndexEnabledEnum.DISABLED (default value) and attempting to search with the missing qualifier against a resource type with multiple search parameters of type reference, the returned results would be incorrect. This has been fixed.  
|  [#6317](https://github.com/hapifhir/hapi-fhir/issues/6317) |  Previously, defining a unique combo Search Parameter with the DateTime component and submitting multiple resources with the same dateTime element (e.g. Observation.effectiveDateTime) resulted in duplicate resource creation. This has been fixed.  
|  [#6339](https://github.com/hapifhir/hapi-fhir/issues/6339) |  Fixed a bug in migrations when using Postgres when using the non-default schema. If a migration attempted to drop a primary key, the generated SQL would only ever target the `public` schema. This has been corrected, and the current schema is now used, with `public` as a fallback. Thanks to Adrienne Sox for the contribution!  
|  [#6359](https://github.com/hapifhir/hapi-fhir/issues/6359) |  After upgrading org.hl7.fhir.core from 6.1.2.2 to 6.3.11, the $validate-code operation stopped returning an error for invalid codes using remote terminology. This has been fixed.  
|  [#6365](https://github.com/hapifhir/hapi-fhir/issues/6365) |  A crash while executing a search with named `_include` parameters on MSSQL has been fixed. Thanks to Craig McClendon for the pull request!  
|  [#6372](https://github.com/hapifhir/hapi-fhir/issues/6372) |  Searches that combined full-text searching (i.e. `_text` or `_content`) with other search parameters could fail to return all results if we encountered 1600 matches against the full-text index where none of them match the rest of the query. This has now been fixed.  
|  |  Fixed a rare bug in the JSON Parser, wherein client-assigned contained resource IDs could collide with server-assigned contained IDs. For example if a resource had a client-assigned contained ID of `#2`, and a contained resource with no ID, then depending on the processing order, the parser could occasionally provide duplicate contained resource IDs, leading to non-deterministic behaviour.  
|  [#6419](https://github.com/hapifhir/hapi-fhir/issues/6419) |  Previously, on Postgres, the `$reindex` operation with `optimizeStorage` set to `ALL_VERSIONS` would process only a subset of versions if there were more than 100 versions to be processed for a resource. This has been fixed so that all versions of the resource are now processed.  
|  [#6420](https://github.com/hapifhir/hapi-fhir/issues/6420) |  Previously, when the `$reindex` operation is run for a single FHIR resource with `optimizeStorage` set to `ALL_VERSIONS`, none of the versions of the resource were processed in `hfj_res_ver` table. This has been fixed.  
|  [#6422](https://github.com/hapifhir/hapi-fhir/issues/6422) |  Previously, since 7.4.4 the validation issue detail codes were not translated correctly for Remote Terminology validateCode calls. The detail code used was `invalid-code` for all use-cases which resulted in profile binding strength not being applied to the issue severity as expected when validating resources against a profile. This has been fixed and issue detail codes are translated correctly.  
|  [#6440](https://github.com/hapifhir/hapi-fhir/issues/6440) |  Previously, if an `IInterceptorBroadcaster` was set in a `RequestDetails` object, `STORAGE_PRECHECK_FOR_CACHED_SEARCH` hooks that were registered to that `IInterceptorBroadcaster` were not called. Also, if an `IInterceptorBroadcaster` was set in the `RequestDetails` object, the boolean return value of the hooks registered to that `IInterceptorBroadcaster` were not taken into account. This second issue existed for all pointcuts that returned a boolean type, not just for `STORAGE_PRECHECK_FOR_CACHED_SEARCH`. These issues have now been fixed.  
|  [#6451](https://github.com/hapifhir/hapi-fhir/issues/6451) |  Previously, activating `BulkDataImport` job would not change jobs status to `RUNNING`, causing it to be processed multiple times instead of single time. This has been fixed.  
|  [#6467](https://github.com/hapifhir/hapi-fhir/issues/6467) |  Fixed an incompatibility between Hibernate Search and Lucene versions that caused ValueSet expansion to fail when Hibernate Search was configured to use Lucene.  
|  [#6283](https://github.com/hapifhir/hapi-fhir/issues/6283) |  Hibernate Search Fulltext fields which were unused have been removed from indexing. This will reduce storage usage in Lucene and Elasticsearch. The fields that were removed are: `myNarrativeTextEdgeNGram`, `myNarrativeTextNGram`, `myNarrativeTextPhonetic`, `myContentTextEdgeNGram`, `myContentTextNGram`, `myContentTextPhonetic`.  
#  0.2.5HAPI FHIR 7.4.5 (Copernicus)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.6.0-0)
##  0.2.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-2)
**Released:** 2024-10-21
**Codename:** (Copernicus)
##  0.2.5.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-2)
##  0.2.5.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-2)
|  [#6372](https://github.com/hapifhir/hapi-fhir/issues/6372) |  Searches that combined full-text searching (i.e. `_text` or `_content`) with other search parameters could fail to return all results if we encountered 1600 matches against the full-text index where none of them match the rest of the query. This has now been fixed.  
---|---|---  
#  0.2.6HAPI FHIR 7.4.4 (Copernicus)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.4.5-6372)
##  0.2.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-3)
**Released:** 2024-10-17
**Codename:** (Copernicus)
##  0.2.6.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-3)
##  0.2.6.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-3)
|  [#6359](https://github.com/hapifhir/hapi-fhir/issues/6359) |  Remote Terminology validation has been enhanced to support output parameter `issues` for the $validate-code operation.  
---|---|---  
|  [#6359](https://github.com/hapifhir/hapi-fhir/issues/6359) |  After upgrading org.hl7.fhir.core from 6.1.2.2 to 6.3.11, the $validate-code operation stopped returning an error for invalid codes using remote terminology. This has been fixed.  
|  [#6363](https://github.com/hapifhir/hapi-fhir/issues/6363) |  This release updates the org.hl7.fhir core dependency up to 6.3.23, in order to patch [CVE-2024-45294](https://nvd.nist.gov/vuln/detail/CVE-2024-45294).  
#  0.2.7HAPI FHIR 7.4.3 (Copernicus)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.4.4-6359)
##  0.2.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-4)
**Released:** 2024-09-30
**Codename:** (Copernicus)
##  0.2.7.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-4)
##  0.2.7.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-4)
|  [#6317](https://github.com/hapifhir/hapi-fhir/issues/6317) |  Previously, defining a unique combo Search Parameter with the DateTime component and submitting multiple resources with the same dateTime element (e.g. Observation.effectiveDateTime) resulted in duplicate resource creation. This has been fixed.  
---|---|---  
#  0.2.8HAPI FHIR 7.4.2 (Copernicus)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.4.3-6317)
##  0.2.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-5)
**Released:** 2024-09-20
**Codename:** (Copernicus)
##  0.2.8.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-5)
##  0.2.8.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-5)
|  [#6216](https://github.com/hapifhir/hapi-fhir/issues/6216) |  Previously, searches combining the `_text` query parameter (using Lucene/Elasticsearch) with query parameters using the database (e.g. `identifier` or `date`) could miss matches when more than 500 results match the `_text` query parameter. This has been fixed, but may be slow if many results match the `_text` query and must be checked against the database parameters.  
---|---|---  
#  0.2.9HAPI FHIR 7.4.0 (Copernicus)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.4.2-6216)
##  0.2.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-6)
**Released:** 2024-08-15
**Codename:** (Copernicus)
##  0.2.9.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-6)
## Derby JARs removed from HAPI-FHIR
As of Derby 17, in order to support JDK17, the jars must be built from source, as the default package supports JDK21. Due to a [high severity vulnerability](https://github.com/hapifhir/hapi-fhir/issues/5471) in older versions, and a lack of appetite to host a forked packaged version of Derby, the Derby JARs have been removed from the HAPI-FHIR distribution. For those who wish to continue to use Derby, you may still do so, but the following jars must be manually added to your classpath:
  * [derby](https://mvnrepository.com/artifact/org.apache.derby/derby)
  * [derbyclient](https://mvnrepository.com/artifact/org.apache.derby/derbyclient)
  * [derbyshared](https://mvnrepository.com/artifact/org.apache.derby/derbyshared)
  * [derbynet](https://mvnrepository.com/artifact/org.apache.derby/derbynet)
  * [derbytools](https://mvnrepository.com/artifact/org.apache.derby/derbytools)


## Possible migration errors on SQL Server (MSSQL)
  * This affects only clients running SQL Server (MSSQL) who have custom indexes on `HFJ_SPIDX` tables, which include `sp_name` or `res_type` columns.
  * For those clients, migration of `sp_name` and `res_type` columns to nullable on `HFJ_SPIDX` tables may be completed with errors, as changing a column to nullable when a column is a part of an index can lead to errors on SQL Server (MSSQL).
  * If client wants to use existing indexes and settings, these errors can be ignored. However, if client wants to enable both [Index Storage Optimized](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#setIndexStorageOptimized\(boolean\)) and [Index Missing Fields](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#getIndexMissingFields\(\)) settings, manual steps are required to change `sp_name` and `res_type` nullability.


To update columns to nullable in such a scenario, execute steps below:
  1. Indexes that include `sp_name` or `res_type` columns should be dropped:


```
DROP INDEX IDX_SP_TOKEN_REST_TYPE_SP_NAME ON HFJ_SPIDX_TOKEN;

```

Copy
  1. The nullability of `sp_name` and `res_type` columns should be updated:


```
ALTER TABLE HFJ_SPIDX_TOKEN ALTER COLUMN RES_TYPE varchar(100) NULL;
ALTER TABLE HFJ_SPIDX_TOKEN ALTER COLUMN SP_NAME varchar(100) NULL;

```

Copy
  1. Additionally, the following index may need to be added to improve the search performance:


```
CREATE INDEX IDX_SP_TOKEN_MISSING_OPTIMIZED ON HFJ_SPIDX_TOKEN (HASH_IDENTITY, SP_MISSING, RES_ID, PARTITION_ID);

```

Copy
##  0.2.9.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-6)
|  [#5871](https://github.com/hapifhir/hapi-fhir/issues/5871) |  When encoding resources in summary mode, it is now possible to override the built-in list of summary elements, by adding additional elements and/or by removing elements from the default list. This can be done for an individual parser instance, or globally using the ParserOptions object available from the FhirContext.  
---|---|---  
|  [#5914](https://github.com/hapifhir/hapi-fhir/issues/5914) |  Update Clinical Reasoning version to v3.6.0  
|  [#5919](https://github.com/hapifhir/hapi-fhir/issues/5919) |  Added the entire WorkChunk to the StepExecutionDetails of a Batch Job.  
|  [#5935](https://github.com/hapifhir/hapi-fhir/issues/5935) |  Remote Terminology Service can now return subproperty fields for a CodeSystem lookup operation. This can be done in DSTU3 and R4. R5 is not yet implemented.  
|  [#5938](https://github.com/hapifhir/hapi-fhir/issues/5938) |  Generated IPS documents will now include a bundle profile declaration.  
|  [#5945](https://github.com/hapifhir/hapi-fhir/issues/5945) |  A new utility method has been added to `BundleUtil` which converts a FHIR Bundle containing resources (e.g. a search result bundle) into a FHIR transaction bundle which could be used to upload those resources to a server.  
|  [#5952](https://github.com/hapifhir/hapi-fhir/issues/5952) |  Previously, since hapi-fhir 7.0, when retrieving the consecutive pages of a Bundle resource search operation using a client with read permissions for Bundle resources, the request would fail with a 403 Forbidden error. This has been fixed.  
|  [#5968](https://github.com/hapifhir/hapi-fhir/issues/5968) |  Added support for filtering on CodeSystem defined properties when using database (hibernate) filtering. The following ValueSet filters can be used to filter on CodeSystem defined properties: EQUAL, EXISTS, IN, NOTIN  
|  [#5980](https://github.com/hapifhir/hapi-fhir/issues/5980) |  When processing a batch job, OpenTelemetry spans named `hapifhir.batch_job.execute` are now generated by the worker threads. These spans have the following span attributes related to the batch job: `hapifhir.batch_job.definition_id`, `hapifhir.batch_job.definition_version`, `hapifhir.batch_job.instance_id`, `hapifhir.batch_job.step_id`, `hapifhir.batch_job.chunk_id`.  
|  [#6003](https://github.com/hapifhir/hapi-fhir/issues/6003) |  When performing a search for a resource which uses versioned canonical references to another resource (e.g. a QuestionnaireResponse with a questionnaire reference of `http://example.org/my-questionnaire|1.0`), the server failed to process `_include` directives which should bring in these references. Includes will now fetch all versions of a canonical reference, which is still not perfect but is an improvement over the current behaviour. A future update may add more targeted behaviour to address including versioned reference targets, but this will require a significant re-architecture of the loading module.  
|  [#6008](https://github.com/hapifhir/hapi-fhir/issues/6008) |  Previously, when partitioning is enabled, the reindex job could only be run against one partition. The reindex job parameters can now accept a list of partitions or all partitions (list of RequestPartitionId or RequestPartitionId.allPartitions). In the future, the functionality can be extended to other bulk operations run via batch2 jobs (e.g. $delete-expunge, $export).  
|  [#6010](https://github.com/hapifhir/hapi-fhir/issues/6010) |  When populated, the search score field will now be included in the entries of a response Bundle.  
|  [#6014](https://github.com/hapifhir/hapi-fhir/issues/6014) |  When uploading an invalid CodeSystem to the JPA server containing duplicate codes, the server responded with an unhelpful error message referring to a database constraint error. This has been fixed so that a more informative error message is returned.  
|  [#6031](https://github.com/hapifhir/hapi-fhir/issues/6031) |  Subscriptions now support the evaluation use of FhirPath criteria and the use of the variables %current and %previous. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#6038](https://github.com/hapifhir/hapi-fhir/issues/6038) |  Allow overriding RestfulServer's contextPath determination by overriding IServerAddressStrategy. Thanks to Alex Kopp (@alexrkopp) for the contribution!  
|  [#6046](https://github.com/hapifhir/hapi-fhir/issues/6046) |  Added support for `:contains` parameter qualifier on the `_text` and `_content` Search Parameters. When using Hibernate Search, this will cause the search to perform an substring match on the provided value. Documentation can be found [here](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#performing-fulltext-search-in-luceneelasticsearch).  
|  [#6060](https://github.com/hapifhir/hapi-fhir/issues/6060) |  The server-generated CapabilityStatement will now use the server name defined by `RestfulServer#setServerName(..)` instead of the hardcoded string `HAPI FHIR`. Thanks to Renaud Subiger for the pull request!  
|  [#6070](https://github.com/hapifhir/hapi-fhir/issues/6070) |  Added paging support for `$everything` operation in synchronous search mode.  
|  [#6073](https://github.com/hapifhir/hapi-fhir/issues/6073) |  Previously, if a unique or non-unique combo SeaerchParameter was defined, it would not be used by searches if any parameter contained multiple OR clauses (e.g. `Patient?family=simpson&given=homer,marge`). Such searches will now use the combo index table, which should result in much more performant searches in some cases.  
|  [#6079](https://github.com/hapifhir/hapi-fhir/issues/6079) |  It is now possible to save TermConceptDesignations with values over 2,000 characters.  
|  [#6148](https://github.com/hapifhir/hapi-fhir/issues/6148) |  Added the target resource partitionId and partitionDate to the resourceLink table.  
|  [#5658](https://github.com/hapifhir/hapi-fhir/issues/5658) |  The MDM candidate search will now be skipped if data for required search parameters is not present. Thanks to Adrienne Sox for the pull request!  
|  [#5885](https://github.com/hapifhir/hapi-fhir/issues/5885) |  Indexing for unique combo Search Parameters has been modified so that a hash value is now stored. This hash value is not yet used in searching or enforcing uniqueness, but will be in the future in order to reduce the space required to store the indexes and the current size limitation on unique indexes.  
|  [#5885](https://github.com/hapifhir/hapi-fhir/issues/5885) |  Indexing for non-unique combo Search Parameters has been improved, using a new hash-based index that should perform significantly better in many circumstances.  
|  [#5885](https://github.com/hapifhir/hapi-fhir/issues/5885) |  When unique and non-unique combo parameters are in use on a server, FHIR Transaction and Reindex Job performance has been optimized by pre-fetching all existing combo index rows for a large batch of resources in a single database operation. This should yield a meaningful performance improvement on such systems.  
|  [#5937](https://github.com/hapifhir/hapi-fhir/issues/5937) |  A new configuration option, `StorageSettings#setIndexStorageOptimized(boolean)` has been added. If enabled, the server will not write data to the `SP_NAME`, `RES_TYPE`, `SP_UPDATED` columns for all `HFJ_SPIDX_xxx` tables. This can help reduce the overall storage size on servers where HFJ_SPIDX tables are expected to have a large amount of data.  
|  [#5999](https://github.com/hapifhir/hapi-fhir/issues/5999) |  The in-memory search matcher module (used for Subscriptions and other places where a resource needs to be evaluated against an existing pool of search parameter expressions) has been optimized so that it doesn't evaluate unnecessary search parameters while analyzing the resource. Thanks to Ahmet Melih Aydoğdu for the pull request!  
|  [#6099](https://github.com/hapifhir/hapi-fhir/issues/6099) |  Database migrations that add or drop an index no longer lock tables when running on Azure Sql Server.  
|  [#5887](https://github.com/hapifhir/hapi-fhir/issues/5887) |  Extract Subscription Settings from Storage Settings so they can be managed independently.  
|  [#5939](https://github.com/hapifhir/hapi-fhir/issues/5939) |  This brings the org.hl7.fhir.core dependency to version 6.3.11.  
|  [#6023](https://github.com/hapifhir/hapi-fhir/issues/6023) |  Previously, CDS hook extensions needed to be encoded as strings. This has been changed so that extensions are now properly provided as inline JSON.  
|  [#6140](https://github.com/hapifhir/hapi-fhir/issues/6140) |  An prototype interface to abstract data access across different types of FHIR repositories (e.g. remote REST, local JPA) has been added to the `hapi-fhir-base` project. Implementations of this interface will follow in future HAPI releases, and it will continue to evolve as it's validated through implementation.  
|  [#6179](https://github.com/hapifhir/hapi-fhir/issues/6179) |  The $reindex operation could potentially initiate a reindex job without any urls provided in the parameters. We now internally generate a list of urls out of all the supported resource types and attempt to reindex found resources of each type separately. As a result, each reindex (batch2) job chunk will be always associated with a url.  
|  [#3986](https://github.com/hapifhir/hapi-fhir/issues/3986) |  Removed the validation error of the `max` query parameter on the $lastn operation. Now, the $lastn operation can be invoked with the `max` query parameter. Contribution by Gijs Groenewegen (@thetrueoneshots).  
|  [#4556](https://github.com/hapifhir/hapi-fhir/issues/4556) |  The CSS file used by the OpenApiInterceptor to serve up the Swagger UI component inadvertently blocked the authorization button evem when it was wanted. This has been fixed. Thanks Jesse Bonzo for the contribution!  
|  [#4837](https://github.com/hapifhir/hapi-fhir/issues/4837) |  In the case where a resource was serialized, deserialized, copied and reserialized it resulted in duplication of contained resources. This has been corrected.  
|  [#5559](https://github.com/hapifhir/hapi-fhir/issues/5559) |  The IPS narrative generator previously created narratives for each `Composition.section.text` entry, but also combined these section narratives into a single concatenated narrative for `Composition.text`. This caused validation issues as the narratives then contained duplicate IDs, and also wasted space. This has been removed.  
|  [#5867](https://github.com/hapifhir/hapi-fhir/issues/5867) |  Previously, when adding multiple resources and defining a golden resource using MDM, the golden resource's tags were removed. This has been fixed  
|  [#5877](https://github.com/hapifhir/hapi-fhir/issues/5877) |  Previously, updating a tokenParam with a value greater than 200 characters would raise a SQLException. This issue has been fixed.  
|  [#5900](https://github.com/hapifhir/hapi-fhir/issues/5900) |  Remove unnecessary call to deleteAllSearchParams when $expunge is called on an already-deleted resource.  
|  [#5913](https://github.com/hapifhir/hapi-fhir/issues/5913) |  Bulk export was failing with a HAPI-2222 error with the persistence module configured for R5. This has been fixed.  
|  [#5925](https://github.com/hapifhir/hapi-fhir/issues/5925) |  An UnsupportedOperationException occurred when validating R5 MHD bundles using the HAPI FHIR validator. Thanks to Renaud Subiger for contributing a fix!  
|  [#5926](https://github.com/hapifhir/hapi-fhir/issues/5926) |  A number of columns in the JPA schema use primitive types (and therefore can never have a null value) but aren't marked as non-null.  
|  [#5926](https://github.com/hapifhir/hapi-fhir/issues/5926) |  A regression in HAPI FHIR 6.4.0 meant that ther JPA server schema migrator ran all tasks even when the database was initially empty and the schema was being initialized by script. This did not produce any incorrect results, but did impact the amount of time taken to initialize an empty database. This has been corrected.  
|  [#5942](https://github.com/hapifhir/hapi-fhir/issues/5942) |  Delete expunge targeted to resources (not everything) that were created with IfNoneExists URL followed by recreation of said resources was failing with HAPI-0550 due to the fact that the corresponding search url records were not being deleted. This has been fixed.  
|  [#5956](https://github.com/hapifhir/hapi-fhir/issues/5956) |  Fixed a regression that prevented rebuilding golden resources with mdm survivorship rules when resources have non-numeric fhir ids in a partitioned system.  
|  [#6077](https://github.com/hapifhir/hapi-fhir/issues/6077) |  Previously, when a MDM rule tried to perform a phonetic match by HumanName (eg. Patient.name), a `ClassCastException` was thrown. This has now been fixed.  
|  [#5959](https://github.com/hapifhir/hapi-fhir/issues/5959) |  An overly verbose log in the JPA server about deleting stale searches has been reduced to DEBUG level.  
|  [#5960](https://github.com/hapifhir/hapi-fhir/issues/5960) |  Previously, queries with chained would fail to sort correctly with lucene and full text searches enabled. This has been fixed.  
|  [#5962](https://github.com/hapifhir/hapi-fhir/issues/5962) |  The default use of the Accept-Charset header has been removed as its use is deprecated. Thanks to Jens Villadsen for the suggestion and pull request!  
|  [#5971](https://github.com/hapifhir/hapi-fhir/issues/5971) |  The JSON Parser failed to parse alternate names (e.g. `_family`) containing extensions if the corresponding regular named element (e.g. `family`) was not present. Thanks to Stefan Lindström for the pull request!  
|  [#5973](https://github.com/hapifhir/hapi-fhir/issues/5973) |  Previously, subscription criteria on R4 backport subscriptions were not validated by the SubscriptionValidatingInterceptor. This has been corrected.  
|  [#5981](https://github.com/hapifhir/hapi-fhir/issues/5981) |  Batch2WorkChunkEntity poll_attempt count is a nullable field, but was incorrectly declared as a primitive. This has been fixed.  
|  [#5985](https://github.com/hapifhir/hapi-fhir/issues/5985) |  Fixed an issue where MDM json links were unserializable due to using the IResourcePersistenceId objects. This has been fixed, and IResourcePersistenceId objects will not be serialized or returned to users.  
|  [#5988](https://github.com/hapifhir/hapi-fhir/issues/5988) |  Previously, when creating a custom SearchParameter for a document Bundle that references an entry resource through the composition (e.g. Bundle.entry[0].resource.as(Composition).subject.resolve().as(Patient).identifier), the search using that parameter would not return the matching document Bundle resources, despite the fullUrl of the entry matching the composite reference. This problem does not exist if the reference match can be done against the entry id. However an id is not required so the match should be done against the fullUrl. This issue has been fixed.  
|  [#5933](https://github.com/hapifhir/hapi-fhir/issues/5933) |  Bulk export was failing with a HAPI-1775 error with the persistence module configured for DSTU3. This has been fixed.  
|  [#5993](https://github.com/hapifhir/hapi-fhir/issues/5993) |  Corrected a regression in valueset expansion. If a valueset with a non-standard `property` in the filter used the `is-a` operation, the caller would receive `HAPI-2526`. This has been corrected.  
|  [#5994](https://github.com/hapifhir/hapi-fhir/issues/5994) |  Fixed a bug in the MdmSurvivorshipSvcImpl that caused resourceType/id (as opposed to just id) to be passed on to the IIdHelperService.  
|  [#5006](https://github.com/hapifhir/hapi-fhir/issues/5006) |  Expanding a ValueSet on a property using a filter operator that is not supported (ISA, NOTISA, etc) would throw an exception and fail expansion. This has been fixed.  
|  [#6000](https://github.com/hapifhir/hapi-fhir/issues/6000) |  In an MDM enabled system with multi-delete enabled, deleting both the final source resource and it's linked golden resource at the same time results in an error being thrown. This has been fixed.  
|  [#6007](https://github.com/hapifhir/hapi-fhir/issues/6007) |  Previously, HAPI-FHIR schedulers were automatically suffixing the Quartz scheduler's `org.quartz.scheduler.instanceName` value to a unique autogenerated value based on the provided instance name. This has been corrected, and now the instance name provided to the BaseHapiFhirScheduler is used directly, with no unique suffix added. This is in line with the [Quartz Documentation](https://www.quartz-scheduler.org/documentation/quartz-2.1.7/configuration/ConfigMain.html) related to defining the scheduler instance name for those who run schedulers in a cluster.  
|  [#6021](https://github.com/hapifhir/hapi-fhir/issues/6021) |  The updated Spring Boot was not compatible with jetty version 12.0.3. Jetty has been updated to 12.0.9, fixing this issue.  
|  [#6024](https://github.com/hapifhir/hapi-fhir/issues/6024) |  Fixed a bug in search where requesting a count with HSearch indexing and FilterParameter enabled and using the _filter parameter would result in inaccurate results being returned. This happened because the count query would use an incorrect set of parameters to find the count, and the regular search when then try and ensure its results matched the count query (which it couldn't because it had different parameters).  
|  [#6033](https://github.com/hapifhir/hapi-fhir/issues/6033) |  Previously, attempting to store resources with common identifies but different partitions would. This has been fixed by adding a new configuration key defaulting to false to allow storing resources with duplicate identifiers across partitions. This new feature can be activated by calling PartitionSettings.setConditionalCreateDuplicateIdentifiersEnabled()  
|  [#6034](https://github.com/hapifhir/hapi-fhir/issues/6034) |  Two indexes introduced in HAPI-FHIR 6.6.0, `IDX_SP_URI_HASH_IDENTITY_V2` and `IDX_SP_URI_HASH_URI_V2` were previously created as unique indexes. This has caused issues on SQL Server due to the way that a filtered index is created. The unique clause was not necessary to this index, and has been removed.  
|  [#6036](https://github.com/hapifhir/hapi-fhir/issues/6036) |  The maximum length of `SP_VALUE_EXACT` and `SP_VALUE_NORMALIZED` in the `HFJ_SPIDX_STRING` table has been increased from 200 to 768. This allows for longer `:contains` searches. If you have previously stored values longer than 200 characters in String search parameters and want to perform a `:contains` search on the new longer maximum size, a reindex of the affected data is required.  
|  [#6040](https://github.com/hapifhir/hapi-fhir/issues/6040) |  The `meta.profile` element on resources was not being respected as canonical (ie, allowing for a version to be appended, `http://example.com/StructureDefinition/abc|1.0.0`), and was thus being ignored during validation. This has been fixed.  
|  [#6044](https://github.com/hapifhir/hapi-fhir/issues/6044) |  Fixed an issue where doing a cache refresh with advanced Hibernate Search enabled would result in an infinite loop of cache refresh -> search for StructureDefinition -> cache refresh, etc  
|  [#6046](https://github.com/hapifhir/hapi-fhir/issues/6046) |  Previously, using `_text` and `_content` searches in Hibernate Search in R5 was not supported. This issue has been fixed.  
|  [#6049](https://github.com/hapifhir/hapi-fhir/issues/6049) |  Fixed a bug in SchemaMigrator that would considered skippable migration tasks as 'failed' migrations.  
|  [#6049](https://github.com/hapifhir/hapi-fhir/issues/6049) |  Rolled back org.apache.derby dependency to one that supports JREs 11 - 17.  
|  [#6058](https://github.com/hapifhir/hapi-fhir/issues/6058) |  Added `PATCH` operation to `BundleEntryTransactionMethodEnumTest` in order to fully support transaction bundle operations.  
|  [#6061](https://github.com/hapifhir/hapi-fhir/issues/6061) |  The latest migration for HFJ_RES_SEARCH_URL introduced in [6033](https://github.com/hapifhir/hapi-fhir/issues/6033) does not fully respect dry-run. This has been fixed.  
|  [#6063](https://github.com/hapifhir/hapi-fhir/issues/6063) |  Removing deprecated thymeleaf syntax from existing templates, so as to avoid deprecation warnings in logs.  
|  [#6074](https://github.com/hapifhir/hapi-fhir/issues/6074) |  Before being processed, subscriptions would be read out of the database all at once. This lead to massive memory consumption if there were a lot of them. This has now been changed to use batching as a means of mitigating this problem.  
|  [#6083](https://github.com/hapifhir/hapi-fhir/issues/6083) |  Address care-gaps bug for handling missing improvement notation validation, and group level measure scoring definition. Bug fix for evaluate-measure subject-list where contained List resource was setting invalid references. Bumping to latest version of clinical reasoning 3.8.  
|  [#6094](https://github.com/hapifhir/hapi-fhir/issues/6094) |  Fix for regression of searches for canonical uris using a version (eg: http://example.com|1.2.3).  
|  [#6097](https://github.com/hapifhir/hapi-fhir/issues/6097) |  Fixed a bug where booting the JPA Server Starter would get a duplicate bean error. This has been corrected. Thanks to [@subigre](https://github.com/subigre) for the fix!  
|  [#6111](https://github.com/hapifhir/hapi-fhir/issues/6111) |  Previously, the package installer wouldn't create a composite SearchParameter resource if the SearchParameter resource didn't have an expression element at the root level. This has now been fixed by making SearchParameter validation in package installer consistent with the DAO level validations.  
|  [#6094](https://github.com/hapifhir/hapi-fhir/issues/6094) |  Searching or conditional creating/updating with a timestamp with an offset containing '+' fails with HAPI-1883. For example: 'Observation?date=2024-07-08T20:47:12.123+03:30' This has been fixed.  
|  [#6122](https://github.com/hapifhir/hapi-fhir/issues/6122) |  Previously, executing the '$validate' operation on a resource instance could result in an HTTP 400 Bad Request instead of an HTTP 200 OK response with a list of validation issues. This has been fixed.  
|  [#6123](https://github.com/hapifhir/hapi-fhir/issues/6123) |  `IAnyResource` `_id` search parameter was missing `path` property value, which resulted in extractor not working when standard search parameters were instantiated from defined context. This has been fixed, and also `_LastUpdated`, `_tag`, `_profile`, and `_security` parameter definitions were added to the class.  
|  [#6124](https://github.com/hapifhir/hapi-fhir/issues/6124) |  Previously, when retrieving a resource which may contain other resources, such as a document Bundle, if a ConsentService's willSeeResource returned AUTHORIZED or REJECT on this parent resource, the willSeeResource was still being called for the child resources. This has now been fixed so that if a consent service returns AUTHORIZED or REJECT for a parent resource, willSeeResource is not called for the child resources.  
|  [#6083](https://github.com/hapifhir/hapi-fhir/issues/6083) |  A bug with $everything operation was discovered when trying to search using hibernate search, this change makes all $everything operation rely on database search until hibernate search fully supports the operation.  
|  [#6134](https://github.com/hapifhir/hapi-fhir/issues/6134) |  Fixed a regression in 7.2.0 which caused systems using `FILESYSTEM` binary storage mode to be unable to read metadata documents that had been previously stored on disk.  
|  [#6142](https://github.com/hapifhir/hapi-fhir/issues/6142) |  Previously, if you upgraded from any older HAPI version to 6.6.0 or later, the `SEARCH_UUID` column length still showed as 36 despite it being updated to have a length of 48. This has now been fixed.  
|  [#6146](https://github.com/hapifhir/hapi-fhir/issues/6146) |  Previously, on MSSQL, two resources with IDs that are identical except for case (ex: Patient1 vs. patient1) would be considered to have the same ID because the database collation is case insensitive (SQL_Latin1_General_CP1_CI_AS). Among other things, this would manifest itself when trying to delete and re-create one of the resources. This has been fixed with a migration step that makes the collation on the resource ID case sensitive (SQL_Latin1_General_CP1_CS_AS).  
|  [#6150](https://github.com/hapifhir/hapi-fhir/issues/6150) |  Previously, the resource $validate operation would return a 404 when the associated profile uses a ValueSet that has multiple includes referencing Remote Terminology CodeSystem resources. This has been fixed to return a 200 with issues instead.  
|  [#6153](https://github.com/hapifhir/hapi-fhir/issues/6153) |  Previously, if you created a resource with some conditional url, but then submitted a transaction bundle that a) updated the resource to not match the condition anymore and b) create a resource with the (same) condition a unique index violation would result. This has been fixed.  
|  [#6156](https://github.com/hapifhir/hapi-fhir/issues/6156) |  Index IDX_IDXCMBTOKNU_HASHC on table HFJ_IDX_CMB_TOK_NU's migration is now marked as online (concurrent).  
|  [#6159](https://github.com/hapifhir/hapi-fhir/issues/6159) |  Previously, `$apply-codesystem-delta-add` and `$apply-codesystem-delta-remove` operations were failing with a 500 Server Error when invoked with a CodeSystem Resource payload that had a concept without a `display` element. This has now been fixed so that concepts without display field is accepted, as `display` element is not required.  
|  |  When JPA servers are configured to always require a new database transaction when switching partitions, the server will now correctly identify the correct partition for FHIR transaction operations, and fail the operation if multiple partitions would be required.  
|  [#6179](https://github.com/hapifhir/hapi-fhir/issues/6179) |  Previously, the $reindex operation would fail when using a custom partitioning interceptor which decides the partition based on the resource type in the request. This has been fixed, such that we avoid retrieving the resource type from the request, rather we use the urls provided as parameters to the operation to determine the partitions.  
|  [#6188](https://github.com/hapifhir/hapi-fhir/issues/6188) |  Previously, a Subscription not marked as a cross-partition subscription could listen to incoming resources from other partitions. This issue is fixed.  
|  [#6208](https://github.com/hapifhir/hapi-fhir/issues/6208) |  A regression was temporarily introduced which caused searches by `_lastUpdated` to fail with a NullPointerException when using Lucene as the backing search engine. This has been corrected  
|  [#6056](https://github.com/hapifhir/hapi-fhir/issues/6056) |  The Derby JARs have been removed from this release. The older versions have multiple high-severity vulnerabilities against them, and the newer versions force usage of Java 21, which HAPI-FHIR does not yet support. If you wish to continue to use derby, you will have to provide the `derby`, `derbyclient`,`derbynet`, `derbyshared`, and `derbytools` jars on your classpath.  
#  0.2.10HAPI FHIR 7.2.3 (Borealis)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.4.0-5871)
##  0.2.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-7)
**Released:** 2024-08-25
**Codename:** (Borealis)
##  0.2.10.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-7)
##  0.2.10.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-7)
|  [#6070](https://github.com/hapifhir/hapi-fhir/issues/6070) |  Added paging support for `$everything` operation in synchronous search mode.  
---|---|---  
|  [#6216](https://github.com/hapifhir/hapi-fhir/issues/6216) |  Previously, searches combining the `_text` query parameter (using Lucene/Elasticsearch) with query parameters using the database (e.g. `identifier` or `date`) could miss matches when more than 500 results match the `_text` query parameter. This has been fixed, but may be slow if many results match the `_text` query and must be checked against the database parameters.  
#  0.2.11HAPI FHIR 7.2.2 (Borealis)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.2.3-6070)
##  0.2.11.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-8)
**Released:** 2024-07-19
**Codename:** (Borealis)
##  0.2.11.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-8)
##  0.2.11.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-8)
|  [#6046](https://github.com/hapifhir/hapi-fhir/issues/6046) |  Added support for `:contains` parameter qualifier on the `_text` and `_content` Search Parameters. When using Hibernate Search, this will cause the search to perform an substring match on the provided value. Documentation can be found [here](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html#performing-fulltext-search-in-luceneelasticsearch).  
---|---|---  
|  [#6099](https://github.com/hapifhir/hapi-fhir/issues/6099) |  Database migrations that add or drop an index no longer lock tables when running on Azure Sql Server.  
|  [#6024](https://github.com/hapifhir/hapi-fhir/issues/6024) |  Fixed a bug in search where requesting a count with HSearch indexing and FilterParameter enabled and using the _filter parameter would result in inaccurate results being returned. This happened because the count query would use an incorrect set of parameters to find the count, and the regular search when then try and ensure its results matched the count query (which it couldn't because it had different parameters).  
|  [#6044](https://github.com/hapifhir/hapi-fhir/issues/6044) |  Fixed an issue where doing a cache refresh with advanced Hibernate Search enabled would result in an infinite loop of cache refresh -> search for StructureDefinition -> cache refresh, etc  
|  [#6046](https://github.com/hapifhir/hapi-fhir/issues/6046) |  Previously, using `_text` and `_content` searches in Hibernate Search in R5 was not supported. This issue has been fixed.  
|  [#6083](https://github.com/hapifhir/hapi-fhir/issues/6083) |  A bug with $everything operation was discovered when trying to search using hibernate search, this change makes all $everything operation rely on database search until hibernate search fully supports the operation.  
|  [#6134](https://github.com/hapifhir/hapi-fhir/issues/6134) |  Fixed a regression in 7.2.0 which caused systems using `FILESYSTEM` binary storage mode to be unable to read metadata documents that had been previously stored on disk.  
#  0.2.12HAPI FHIR 7.2.1 (Borealis)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.2.2-6046)
##  0.2.12.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-9)
**Released:** 2024-05-30
**Codename:** (Borealis)
##  0.2.12.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-9)
##  0.2.12.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-9)
|  [#5960](https://github.com/hapifhir/hapi-fhir/issues/5960) |  Previously, queries with chained would fail to sort correctly with lucene and full text searches enabled. This has been fixed.  
---|---|---  
#  0.2.13HAPI FHIR 7.2.0 (Borealis)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.2.1-5960)
##  0.2.13.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-10)
**Released:** 2024-05-18
**Codename:** (Borealis)
##  0.2.13.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-10)
## HFJ_FORCED_ID Table
The HFJ_FORCED_ID table is no longer used. Users may delete it after upgrading to 7.2 to free database storage space.
##  0.2.13.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-10)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Apache Commons Compress (Base): 1.21 -> 1.26.0

  
---|---|---  
|  [#5610](https://github.com/hapifhir/hapi-fhir/issues/5610) |  Before, message broker always used `JacksonMessageConverter`. Added a configuration option that allows to disable it so other converters can be configured depending on the message type.  
|  [#5682](https://github.com/hapifhir/hapi-fhir/issues/5682) |  Several enhancements have been made to the International Patient Summary generator based on feedback from implementers: 
  * New methods have been added to the `IIpsGenerationStrategy` allowing resources for any or all sections to be fetched from a source other than the FHIR repository. 
  * The `IpsSectionEnum` class has been removed and replaced in any user-facing APIs with references to `SectionRegistry.Section`. This makes it much easier to extend or replace the section registry with custom sections not defined in the universal IPS implementation guide. 
  * Captions have been removed from narrative section tables, and replaced with H5 tags directly above the table. This results in an easier to read display since the table title will appear above the table instead of below it. 
  * The IPS narrative generator built in templates will now omit tables when the template specified multiple tables and the specific table would have no resources. 

  
|  [#5692](https://github.com/hapifhir/hapi-fhir/issues/5692) |  The JPA WebSocket delivery mechanism now supports the `content` delivery mode. Thanks to Artiom Darie for the contribution!  
|  [#5712](https://github.com/hapifhir/hapi-fhir/issues/5712) |  The SearchNarrowingInterceptor can now optionally be configured to also apply URL narrowing to conditional URLs used by conditional create/update/delete/patch operations, both as raw HTTP transactions as well as within FHIR transaction Bundles.  
|  [#5745](https://github.com/hapifhir/hapi-fhir/issues/5745) |  Added another state to the Batch2 work chunk state machine: `READY`. This work chunk state will be the initial state on creation. Once queued for delivery, they will transition to `QUEUED`. The exception is for ReductionStep chunks (because reduction steps are not read off of the queue, but executed by the maintenance job inline.  
|  [#5750](https://github.com/hapifhir/hapi-fhir/issues/5750) |  Update to the 3.2.0 release of the Clinical Reasoning Module. This includes the following changes: 
  * Updated the Clinical Reasoning documentation.
  * Added support for additional parameters on operations.
  * Added StructureDefinition/$questionnaire operation.
  * Add ability to generate PlanDefinition/$apply results with unique ids.
  * Resolved issues with Questionnaire item generation during PlanDefinition/$apply.
  * Resolved issues with some request resources not generated correctly from ActivityDefinition/$apply

  
|  [#5767](https://github.com/hapifhir/hapi-fhir/issues/5767) |  Added new `POLL_WAITING` state for WorkChunks in batch jobs. Also added RetryChunkLaterException for jobs that have steps that need to be retried at a later time (can be provided optionally to exception). If a step throws this new exception, it will be set with the new `POLL_WAITING` status and retried at a later time.  
|  [#5777](https://github.com/hapifhir/hapi-fhir/issues/5777) |  Change the implementation of CDS on FHIR to use the Auto Prefetch functionality and to no longer pass the fhirServer from the request into the dataEndpoint parameter of $apply.  
|  [#5784](https://github.com/hapifhir/hapi-fhir/issues/5784) |  Add support to _sort for chained `composition` Bundle SearchParameters  
|  [#5800](https://github.com/hapifhir/hapi-fhir/issues/5800) |  A new setting in JpaStorageSettings enforces a maximum file size for Bulk Export output files, as well as work chunks creating during processing. This setting has a default value of 100 MB.  
|  [#5816](https://github.com/hapifhir/hapi-fhir/issues/5816) |  The ConceptMap/$translate operation will include targets with an equivalence code of `unmatched` in the response regardless of whether the target has a code.  
|  [#5818](https://github.com/hapifhir/hapi-fhir/issues/5818) |  Added another state to the Batch2 work chunk state machine: `GATE_WAITING`. This work chunk state will be the initial state on creation for gated jobs. Once all chunks are completed for the previous step, they will transition to `READY`.  
|  [#5845](https://github.com/hapifhir/hapi-fhir/issues/5845) |  Added a new pointcut: STORAGE_PRE_INITIATE_BULK_EXPORT. This pointcut is meant to be called explicitly before STORAGE_INITIATE_BULK_EXPORT, so that parameter manipulation that needs to be done before permission checks (currently done using STORAGE_INITIATE_BULK_EXPORT) can safely be done without risk of affecting permission checks/ authorization.  
|  [#5850](https://github.com/hapifhir/hapi-fhir/issues/5850) |  Add additional Clinical Reasoning operations $collect-data and $data-requirements to HAPI-FHIR to expand capability for clinical reasoning module users. These operations will assist users in identifying data requirements for evaluating a measure, and what data was used for a measure evaluation  
|  [#5855](https://github.com/hapifhir/hapi-fhir/issues/5855) |  If using an OpenTelemetry agent, a span is now generated for each interceptor method call. The span is named as 'hapifhir.interceptor' and it has the following attributes about the interceptor: 'hapifhir.interceptor.pointcut_name','hapifhir.interceptor.class_name', 'hapifhir.interceptor.method_name'  
|  [#5861](https://github.com/hapifhir/hapi-fhir/issues/5861) |  Enhance RuleBuilder code to support multiple instance IDs.  
|  [#5890](https://github.com/hapifhir/hapi-fhir/issues/5890) |  As part of the migration from LOB, provided the capability to force persisting data to LOB columns. The default behavior is to not persist in lob columns.  
|  [#5899](https://github.com/hapifhir/hapi-fhir/issues/5899) |  The `MDM_POST_MERGE_GOLDEN_RESOURCES` now supports an additional parameter, of type `ca.uhn.fhir.mdm.model.MdmTransactionContext`. Thanks to Jens Villadsen for the contribution.  
|  [#5748](https://github.com/hapifhir/hapi-fhir/issues/5748) |  In the JPA server, several database columns related to Batch2 jobs and searching have been reworked so that they no will longer use LOB datatypes going forward. This is a significant advantage on Postgresql databases as it removes a significant use of the inefficient `pg_largeobject` table, and should yield performance boosts for MSSQL as well.  
|  [#5838](https://github.com/hapifhir/hapi-fhir/issues/5838) |  Migration of remaining database columns still using the LOB datatypes. This change effectively cuts all ties with the inefficient `pg_largeobject` table.  
|  [#5682](https://github.com/hapifhir/hapi-fhir/issues/5682) |  The IPS $summary generation API has been overhauled to make it more flexible for future use cases. Specifically, the section registry has been removed and folded into the generation strategy, and support has been added for non-JPA sources of data. This is a breaking change to the API, and implementers will need to update their code. This updated API incorporates community feedback, and should now be considered a stable API for IPS generation.  
|  [#5814](https://github.com/hapifhir/hapi-fhir/issues/5814) |  Extracted methods out of ResourceProviderFactory into ObservableSupplierSet so that functionality can be used by other services. Unit tests revealed a cleanup bug in MdmProviderLoader that is fixed in this MR.  
|  [#5817](https://github.com/hapifhir/hapi-fhir/issues/5817) |  The HFJ_FORCED_ID table is no longer used.  
|  [#3761](https://github.com/hapifhir/hapi-fhir/issues/3761) |  Fixed behaviour of the `_list` query parameter. Now it returns exclusively resources that are members of the given list. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#5088](https://github.com/hapifhir/hapi-fhir/issues/5088) |  Previously, the fullUrl for resources in _history bundles was not generated correctly when using a client provided id. The same problem started to happen for the resources with server generated ids more recently (after 6.9.10). This has now been fixed  
|  [#5110](https://github.com/hapifhir/hapi-fhir/issues/5110) |  When processing a FHIR transaction in the JPA server, an identifier containing a system that has no value but has an extension present could cause a NullPointerException. This has been corrected.  
|  [#5667](https://github.com/hapifhir/hapi-fhir/issues/5667) |  Previously, creating an XML encoded FHIR resource with a decimal element that has a leading plus sign value would result in `JsonParseException` during the read operation from the database. Thus, making it impossible to retrieve or modify such resources. This has been fixed.  
|  [#5668](https://github.com/hapifhir/hapi-fhir/issues/5668) |  Added support for sorting on a chained `location.near` search. This allows you to sort location by nearness via a chained search. Thanks to Nicolai Gjøderum (@nigtrifork) for the contribution!  
|  [#5671](https://github.com/hapifhir/hapi-fhir/issues/5671) |  Avoid lock contention by refreshing SearchParameter cache in a new transaction.  
|  [#5672](https://github.com/hapifhir/hapi-fhir/issues/5672) |  Previously, when performing a FHIR search using a non-chained relative reference (returns entire resource) with a server assigned id, it ignores the invalid resourceType in the parameter value and proceeds with the id based lookup. e.g. GET `/MedicationAdministration?context=abc/1352` returns `Encounter/1352`. This has been fixed.  
|  [#5682](https://github.com/hapifhir/hapi-fhir/issues/5682) |  The BundleBuilder utility class will no longer include the `/_version/xxx` portion of the resource ID in the `Bundle.entry.fullUrl` it generates, as the FHIR specification states that this should be omitted.  
|  [#5682](https://github.com/hapifhir/hapi-fhir/issues/5682) |  The IPS Generator will no longer replace resource IDs with placeholder IDs in the resulting bundle by default, although this can be overridden in the generation strategy object.  
|  [#5690](https://github.com/hapifhir/hapi-fhir/issues/5690) |  Previously, a DELETE on a specific URL search string would always attempt to delete no matter the number of resolved resources. This has been fixed by adding a storage setting to enforce a threshold for resolved resources, above which the DELETE operation will fail to execute with HAPI-2496.  
|  [#5701](https://github.com/hapifhir/hapi-fhir/issues/5701) |  Previously, invoking search URLs containing ':identifier' would result in a HAPI-1250 error complaining about an invalid resource type. This has been fixed by returning a clearer error message for this specific condition: HAPI-2498.  
|  [#5707](https://github.com/hapifhir/hapi-fhir/issues/5707) |  Previously, with validation active, when a user POSTed a resource with a meta profile with a non-existent StructureDefinition URL, then POSTed the StructureDefinition, POSTing the same or another patient with that same meta profile URL would still fail with a VALIDATION_VAL_PROFILE_UNKNOWN_NOT_POLICY validation error. This has been fixed.  
|  [#5771](https://github.com/hapifhir/hapi-fhir/issues/5771) |  Previously, a Patch operation would fail when adding a complex extension, i.e. an extension comprised of another extension. This issue has been fixed.  
|  [#5720](https://github.com/hapifhir/hapi-fhir/issues/5720) |  System-level and Type-level History operations on the JPA server (i.e. `_history`) could sometimes contain duplicates or miss entries when a large number of matching resources on the server had identical update timestamps. This has been corrected.  
|  [#5722](https://github.com/hapifhir/hapi-fhir/issues/5722) |  An incorrect migration script caused a failure when upgrading to HAPI FHIR 7.0.0 on PostgreSQL if the database was not in the `public` schema. Thanks to GitHub user @pano-smals for the contribution!  
|  [#5725](https://github.com/hapifhir/hapi-fhir/issues/5725) |  The recommended constructor was not present on hibernate dialects provided by HAPI FHIR, leading to warnings during startup, and failures in some cases. This has been corrected. Thanks to GitHub user @pano-smals for the contribution!  
|  [#5730](https://github.com/hapifhir/hapi-fhir/issues/5730) |  Previously, using the FhirTerser to process an Extension with an Enumeration would fail. This has been fixed.  
|  [#5731](https://github.com/hapifhir/hapi-fhir/issues/5731) |  Previously, transactions that had entries with `Bundle` requests failed with `HAPI-0339: Can not handle transaction with nested resource of type Bundle`. This has been fixed and now transactions are permitted that contain entries with Bundle requests that have a specified url (i.e. `POST` to `/Bundle`), but are rejected if no url is specified (i.e. nested transactions or batches).  
|  [#5734](https://github.com/hapifhir/hapi-fhir/issues/5734) |  A scheduled job to clean up the Search URL table used to enforce uniqueness of conditional create/update jobs was created as a local job and not a clustered job. This has been fixed.  
|  [#5735](https://github.com/hapifhir/hapi-fhir/issues/5735) |  Previously, complex _has then chain then _has searches would fail to return results when expected (ex: 'Practitioner?_has:ExplanationOfBenefit:care-team:coverage.payor._has:List:item:_id=list1') This has been fixed.  
|  [#5742](https://github.com/hapifhir/hapi-fhir/issues/5742) |  Fixed behaviour of the _language query parameter. Now it is picked up as search parameter in the resource provider and filters accordingly. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#5746](https://github.com/hapifhir/hapi-fhir/issues/5746) |  Batch2 jobs with reduction steps didn't fire the completion handler. This has been fixed.  
|  [#5758](https://github.com/hapifhir/hapi-fhir/issues/5758) |  Batch2 jobs with reduction steps didn't fire the completion handler with the up-to-date job status. This has been fixed.  
|  [#5773](https://github.com/hapifhir/hapi-fhir/issues/5773) |  Subscriptions with null content caused NullPointerExceptions. This condition is now checked and handled.  
|  [#5780](https://github.com/hapifhir/hapi-fhir/issues/5780) |  SearchBundleEntryParts now correctly respects `OUTCOME` and `null` search modes in a bundle entry. In the public space, this means `BundleUtil#getSearchBundleEntryParts()` no longer incorrectly infers information about the entry mode  
|  [#5788](https://github.com/hapifhir/hapi-fhir/issues/5788) |  Previously, the casing of the X-Request-ID header key was not retained in the corresponding response. This has been fixed.  
|  [#5801](https://github.com/hapifhir/hapi-fhir/issues/5801) |  Support for the _language parameter was added but it was not able to be used by clients of a JPA server because the _language parameter was not added to the resource providers. Additionally, no error message was returned when language support was disabled and a search with _language was performed. This has been fixed.  
|  [#5802](https://github.com/hapifhir/hapi-fhir/issues/5802) |  Previously, using the ':mdm' qualifier with the '_id' search parameter would not included expanded resources in search result. This issue has been fixed.  
|  [#5812](https://github.com/hapifhir/hapi-fhir/issues/5812) |  Previously, the 'planDefinition' resource parameter for the Dstu3 version of PlanDefinition/$apply was incorrectly defined as an R4 resource. This issue has been fixed.  
|  [#5820](https://github.com/hapifhir/hapi-fhir/issues/5820) |  Unifying the code paths for Patient type export and Patient instance export. These paths should be the same, since type is defined by spec, but instance is just 'syntactic sugar' on top of that spec (and so should be the same).  
|  [#5824](https://github.com/hapifhir/hapi-fhir/issues/5824) |  We now avoid a query during reindex and transaction processing that was very slow on Sql Server.  
|  [#5828](https://github.com/hapifhir/hapi-fhir/issues/5828) |  When batch 2 jobs with Reduction steps fail in the final part of the reduction step, this would often leave the job stuck in the FINALIZE state. This has been fixed; the job will now FAIL.  
|  [#5842](https://github.com/hapifhir/hapi-fhir/issues/5842) |  Fixed a bug where an NPE was being thrown when trying to serialize a FHIR fragment (e.g., backboneElement , compound datatype) to a string representation if the fragment contains a `Reference`.  
|  [#5856](https://github.com/hapifhir/hapi-fhir/issues/5856) |  Previously, it was possible to execute the `$delete-expunge` operation on a resource even if the user did not have delete permissions for the given resource type. This has been fixed.  
|  [#5865](https://github.com/hapifhir/hapi-fhir/issues/5865) |  Moving the Hibernate.Search annotation for text indexing from the lob column to the column added as part of the PostgreSql LOB migration.  
|  [#5874](https://github.com/hapifhir/hapi-fhir/issues/5874) |  Fixed a bug where 'List' would be incorrectly shown as 'ListResource' in the error response for a GET for an invalid resource.  
|  [#5877](https://github.com/hapifhir/hapi-fhir/issues/5877) |  Previously, updating a tokenParam with a value greater than 200 characters would raise a SQLException. This issue has been fixed.  
|  [#5886](https://github.com/hapifhir/hapi-fhir/issues/5886) |  Previously, either updating links on, or deleting one of two patients with non-numeric IDs linked to a golden patient would result in a HAPI-0389 if there were survivorship rules. This issue has been fixed for both the update links and delete cases.  
|  [#5888](https://github.com/hapifhir/hapi-fhir/issues/5888) |  Updated documentation on binary_security_interceptor to specify using `STORAGE_PRE_INITIATE_BULK_EXPORT` not `STORAGE_INITIATE_BULK_EXPORT` pointcut to change bulk export parameters.  
|  [#5893](https://github.com/hapifhir/hapi-fhir/issues/5893) |  Previously, hapi-fhir-cli: upload-terminology failed with a HAPI-0862 error when uploading LOINC. This has been fixed.  
|  [#5898](https://github.com/hapifhir/hapi-fhir/issues/5898) |  Previously, triggering a `$meta` via GET on a new patient with Megascale configured resulted in error HAPI-0389. This has been corrected This has been fixed.  
|  [#5904](https://github.com/hapifhir/hapi-fhir/issues/5904) |  Chained sort would exclude results that did not have resources matching the sort chain. These are now included, and sorted at the end.  
|  [#5915](https://github.com/hapifhir/hapi-fhir/issues/5915) |  Previously, in some edge case scenarios the Bulk Export Rule Applier could accidentally permit a Patient type level bulk export request, even if the calling user only had permissions to a subset of patients. This has been corrected.  
|  [#5917](https://github.com/hapifhir/hapi-fhir/issues/5917) |  Fix chained sorts on strings when using MS Sql  
|  [#5960](https://github.com/hapifhir/hapi-fhir/issues/5960) |  Previously, queries with chained would fail to sort correctly with lucene and full text searches enabled. This has been fixed.  
|  [#5717](https://github.com/hapifhir/hapi-fhir/issues/5717) |  Fixed a potential XSS vulnerability in the HAPI FHIR Testpage Overlay module.  
#  0.2.14HAPI FHIR 7.0.3 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.2.0-0)
##  0.2.14.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-11)
**Released:** 2024-08-24
**Codename:** (Zed)
##  0.2.14.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-11)
##  0.2.14.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-11)
|  [#6099](https://github.com/hapifhir/hapi-fhir/issues/6099) |  Database migrations that add or drop an index no longer lock tables when running on Azure Sql Server.  
---|---|---  
#  0.2.15HAPI FHIR 7.0.2 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.0.3-6099)
##  0.2.15.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-12)
**Released:** 2024-03-20
**Codename:** (Zed)
##  0.2.15.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-12)
##  0.2.15.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-12)
#  0.2.16HAPI FHIR 7.0.1 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#hapi-fhir-701-zed)
##  0.2.16.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-13)
**Released:** 2024-03-01
**Codename:** (Zed)
##  0.2.16.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-13)
##  0.2.16.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-13)
|  [#5671](https://github.com/hapifhir/hapi-fhir/issues/5671) |  Avoid lock contention by refreshing SearchParameter cache in a new transaction.  
---|---|---  
|  [#5722](https://github.com/hapifhir/hapi-fhir/issues/5722) |  An incorrect migration script caused a failure when upgrading to HAPI FHIR 7.0.0 on PostgreSQL if the database was not in the `public` schema. Thanks to GitHub user @pano-smals for the contribution!  
|  [#5742](https://github.com/hapifhir/hapi-fhir/issues/5742) |  Fixed behaviour of the _language query parameter. Now it is picked up as search parameter in the resource provider and filters accordingly. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#5717](https://github.com/hapifhir/hapi-fhir/issues/5717) |  Fixed a potential XSS vulnerability in the HAPI FHIR Testpage Overlay module.  
#  0.2.17HAPI FHIR 7.0.0 (Apollo)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.0.1-5671)
##  0.2.17.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-14)
**Released:** 2024-02-18
**Codename:** (Apollo)
##  0.2.17.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-14)
This release contains a large breaking change for authors of interceptors. Internally, HAPI-FHIR has swapped from using `javax.*` to `jakarta.*` packages. Please see [the migration guide](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html) for more information. Without manual intervention, the majority of interceptors will fail at runtime unless they are upgraded.
## Possible New Indexes on PostgresSQL
  * This affects only clients running PostgreSQL who have a locale/collation that is NOT 'C'
  * For those clients, the migration will detect this condition and add new indexes to: 
    * hfj_spidx_string
    * hfj_spidx_uri
  * This is meant to address performance issues for these clients on GET queries whose resulting SQL uses "LIKE" clauses


These are the new indexes that will be created:
```
CREATE INDEX idx_sp_string_hash_nrm_pattern_ops ON public.hfj_spidx_string USING btree (hash_norm_prefix, sp_value_normalized varchar_pattern_ops, res_id, partition_id);

```

Copy
```
CREATE UNIQUE INDEX idx_sp_uri_hash_identity_pattern_ops ON public.hfj_spidx_uri USING btree (hash_identity, sp_uri varchar_pattern_ops, res_id, partition_id);

```

Copy
##  0.2.17.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-14)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Jackson (Base): 2.15.3 -> 2.16.1
  * SLF4j (Base): 2.0.3 -> 2.0.9
  * Logback (Base): 1.4.7 -> 1.4.14
  * Caffeine (Base): 3.1.1 -> 3.1.8
  * Spring Framework (JPA): 5.3.27 -> 6.1.1
  * Spring Boot (JPA-Starter): 5.3.27 -> 6.2.0
  * Spring Data BOM (JPA): 2021.2.2 -> 2023.1.0
  * Hibernate (JPA): 5.6.15.Final -> 6.4.1.Final
  * Hibernate Validator (JPA): 6.1.5.Final -> 8.0.0.Final
  * Hibernate Search (JPA): 6.1.6.Final -> 7.0.0.Final
  * Commons-DBCP2 (JPA): 2.9.0 -> 2.11.0
  * DataSource-Proxy (JPA): 1.9 -> 1.10
  * Spring Boot (Boot+Starter): 2.7.12 -> 3.1.4
  * Jetty (CLI): 10.0.14 -> 12.0.3
  * Jansi (CLI): 2.4.0 -> 2.4.1
  * Derby (CLI): 10.14.2.0 -> 10.17.1.0
  * Commons-Lang3 (CLI): 3.12.0 -> 3.14.0
  * Commons-CSV (CLI): 1.8 -> 1.10.0
  * Phloc Schematron (Schematron Validator): 5.6.5 -> 7.1.2
  * RestEasy (JAX-RS Server): 5.0.2.Final -> 6.2.5.Final

  
---|---|---  
|  [#5081](https://github.com/hapifhir/hapi-fhir/issues/5081) |  Added MDM support for FHIR R5.  
|  [#5138](https://github.com/hapifhir/hapi-fhir/issues/5138) |  A match result map field has been added to the `$mdm-link-history` operation. This new field shows the rules that evaluated true during matching and the corresponding initial match result when the link was created.  
|  [#5271](https://github.com/hapifhir/hapi-fhir/issues/5271) |  The error messages returned in an OperationOutcome when validating terminology codes as a part of resource profile validation have been improved. Machine processable location (line/col) information is now available through a pair of dedicated extensions, and error messages such as UCUM parsing issues are now returned to the client (previously they were swallowed and a generic error message was returned).  
|  [#5321](https://github.com/hapifhir/hapi-fhir/issues/5321) |  It is now possible to configure the strictness of concept display name validation using a new flag on the InMemoryTerminologyServerValidationSupport (for non-JPA validation) and JpaStorageSettings (for JPA validation). In addition, the error messages emitted by the validator when a concept display doesn't match have been improved to be much more useful.  
|  [#5401](https://github.com/hapifhir/hapi-fhir/issues/5401) |  Previously, it was only possible to clear a top-level field on a resource using TerserUtil. A new method has been added to TerserUtil to support clearing a value by FhirPath.  
|  [#5426](https://github.com/hapifhir/hapi-fhir/issues/5426) |  Added a new method `getResourceByReferenceAndResourceType()` in `BundleUtil.java` to find a specific `Resource` from `Bundle` using `Reference`.  
|  [#5436](https://github.com/hapifhir/hapi-fhir/issues/5436) |  Moving clinical reasoning to non-javax dependency for alignment with incoming hapi-fhir changes, bumping CR version and updating impacted classes and services  
|  [#5439](https://github.com/hapifhir/hapi-fhir/issues/5439) |  Added Terminology Troubleshooting Log to support troubleshooting terminology issues.  
|  [#5442](https://github.com/hapifhir/hapi-fhir/issues/5442) |  The ValidatorResourceFetcher will now resolve canonical URL references as well as simple local references.  
|  [#5469](https://github.com/hapifhir/hapi-fhir/issues/5469) |  There is a programmatic filter enabled which skips installation for certain resources based on their status in PackageInstallerSvcImpl. The filter can now be controlled via StorageSettings.  
|  [#5476](https://github.com/hapifhir/hapi-fhir/issues/5476) |  A new method on the IValidationSupport interface called lookupCode(LookupCodeRequest) has been added. This method will replace the existing lookupCode methods, which are now deprecated.  
|  [#5480](https://github.com/hapifhir/hapi-fhir/issues/5480) |  Updated $member-match operation signature to match the latest specification.  
|  [#5480](https://github.com/hapifhir/hapi-fhir/issues/5480) |  Updated Consent storage in $member-match operation.  
|  [#5498](https://github.com/hapifhir/hapi-fhir/issues/5498) |  Added support for `id-only` and `empty` payload content types for notifications triggered by R5, R4B, and R4 back-ported topic subscriptions.  
|  [#5502](https://github.com/hapifhir/hapi-fhir/issues/5502) |  It is now possible to mutate an HTTP response from the CLIENT_RESPONSE Pointcut, and pass this mutated response to downstream processing.  
|  [#5527](https://github.com/hapifhir/hapi-fhir/issues/5527) |  Added a map of `additionalData` to BulkExport job params. This will allow consumers of BulkExport to add additional data to be accessed at later steps by using various pointcuts in the system. Updated ConsentService so that BulkExport operations will call the willSeeResource method for each exported resource.  
|  [#5536](https://github.com/hapifhir/hapi-fhir/issues/5536) |  In code: Support lowercase for SQL columns and overridden column type/driver type SQL type string rules  
|  [#5558](https://github.com/hapifhir/hapi-fhir/issues/5558) |  An interceptor was added to block resource updates which would cause the resource to change Patient compartment. Please see [JPA Server: Block Resource Updates Changing Patient Compartment](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html#jpa-server-block-resource-updates-changing-patient-compartment) for more information.  
|  [#5569](https://github.com/hapifhir/hapi-fhir/issues/5569) |  Upgrade Clinical Reasoning version for latest bug fixes and available operations. Add $evaluate library operation.  
|  [#5588](https://github.com/hapifhir/hapi-fhir/issues/5588) |  Added `auto-version-references-at-path` extension that allows to enable auto versioning references at specified paths of resource instances.  
|  [#5593](https://github.com/hapifhir/hapi-fhir/issues/5593) |  Hibernate SQL log filtering capability was added. See [Hibernate SQL Log Filtering](https://hapifhir.io/hapi-fhir/docs/appendix/logging.html#hibernate-sql-log-filtering).  
|  [#5377](https://github.com/hapifhir/hapi-fhir/issues/5377) |  Subscription triggering via the `$trigger-subscription` operation is now multi-threaded, which significantly improves performance for large data sets.  
|  [#5555](https://github.com/hapifhir/hapi-fhir/issues/5555) |  Previously, resource body content went into one of 2 columns on the HFJ_RES_VER table: RES_TEXT if the size was above a configurable threshold, or RES_TEXT_VC if it was below that threshold. Performance testing has shown that the latter is always faster, and that on Postgres the use of the latter is particularly problematic since it maps to the largeobject table which isn't the recommended way of storing high frequency objects. The configurable threshold is now ignored, and the latter column is always used. Any legacy data in the former column will still be read however.  
|  |  HAPI FHIR JPA now requires PostgreSQL 10+. Previously Postgres 9.4 was supported but this version has been dropped.  
|  [#5444](https://github.com/hapifhir/hapi-fhir/issues/5444) |  The reindexing and mdm-clear batch jobs now stream results internally for more reliable operation.  
|  [#5612](https://github.com/hapifhir/hapi-fhir/issues/5612) |  The resource dao interface now supports searching for IIdType or full resources.  
|  [#5452](https://github.com/hapifhir/hapi-fhir/issues/5452) |  Swapped from using `javax.*` to `jakarta.*` packages. This is a breaking change for a large majority of people who write custom code against HAPI-FHIR. Please see [the migration guide](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html) for more information.  
|  [#4634](https://github.com/hapifhir/hapi-fhir/issues/4634) |  Previously, rule builder could not effectively handle Patient Type-Level Exports. It would over-permit requests in certain scenarios. This fix allows for accumulation of ids on a Patient Type-Level Bulk export to enable us to properly match the requested Patient IDs against the users permitted Patient IDs.  
|  [#5192](https://github.com/hapifhir/hapi-fhir/issues/5192) |  Fixed a bug where search Bundles with `include` entries from an _include query parameter might trigger a 'next' link to blank pages when no more results `match` results are available.  
|  [#5333](https://github.com/hapifhir/hapi-fhir/issues/5333) |  A regression was introduced in 2023.08.R01 which caused binary storage prefixes to not be applied to exported binary blobs. This has been fixed.  
|  [#5340](https://github.com/hapifhir/hapi-fhir/issues/5340) |  Updated documentation specifying the correct status (CANCELLED) to set the job status to if a job is cancelled.  
|  [#5353](https://github.com/hapifhir/hapi-fhir/issues/5353) |  Previously, when using revincludes and includes with iterate, while also using revincludes without iterate, the result omitted some resources that should have been included. This issue has now been fixed.  
|  [#5445](https://github.com/hapifhir/hapi-fhir/issues/5445) |  Added warnings when MDM candidate search parameters are not defined and candidate search limit is exceeded.  
|  [#5452](https://github.com/hapifhir/hapi-fhir/issues/5452) |  Previously, the $mdm-query-link operation would return values of field linkCreated and linkUpdated in scientific notation when the last digits are 0. This is now fixed and always returns in standard notation.  
|  [#5454](https://github.com/hapifhir/hapi-fhir/issues/5454) |  Previously, searching with parameter '_total' could influence chunked query resultsets and subsequently, paged results. This is now fixed.  
|  [#5460](https://github.com/hapifhir/hapi-fhir/issues/5460) |  Previously, expanding a ValueSet using hierarchical CodeSystem would fail in different scenarios with a constraint violation exception when codes (term concepts) were being persisted. This is now fixed.  
|  [#5465](https://github.com/hapifhir/hapi-fhir/issues/5465) |  Several fixes to the HAPI FHIR generated OpenAPI schema have been implemented. This means that the spec now validates cleanly. Thanks to Primož Delopst for the contribution!  
|  |  The hapi-fhir-validation module inadvertently included a mandatory dependency on the Caffeine caching library instead of leaving it to implementors to choose which module to pick. This has been corrected.  
|  [#5475](https://github.com/hapifhir/hapi-fhir/issues/5475) |  Ensure batch2 jpa persistence always targets the default partition.  
|  [#5486](https://github.com/hapifhir/hapi-fhir/issues/5486) |  Previously, testing database migration with cli migrate-database command in dry-run mode would insert in the migration task table. The issue has been fixed.  
|  |  Previously, it was impossible to find all resources from different partitions for $everything operation with partitioning.cross_partition_reference_mode=ALLOWED_UNQUALIFIED and dao_config.client_id_mode=ANY. It's fixed now  
|  [#5496](https://github.com/hapifhir/hapi-fhir/issues/5496) |  Ensure batch jobs target the default partition for non-gated steps.  
|  |  The HFQL/SQL engine incorrectly parsed expressions containing a `>=` or `<=` comparator in a WHERE clause. This has been corrected. Additionally, the execution engine has been optimized to apply clauses against the `meta.lastUpdated` path more efficiently by using the equivalent search parameter automatically.  
|  [#5511](https://github.com/hapifhir/hapi-fhir/issues/5511) |  Previously, when creating an index as a part of a migration, if the index already existed with a different name on Oracle, the migration would fail. This has been fixed so that the create index migration task now recovers with a warning message if the index already exists with a different name.  
|  [#5511](https://github.com/hapifhir/hapi-fhir/issues/5511) |  Previously, CodeSystem `$lookup` with Remote Terminology Service enabled would throw NullPointerException when the CodeSystem included designations with no language value. Also, there was an inconsistency between input and output type `string` vs. `code` for property parameters. These issues have been fixed.  
|  [#5523](https://github.com/hapifhir/hapi-fhir/issues/5523) |  Previously, it was possible to store NPM Packages where the package name's case did not match the package ID's case, e.g. `my-package` was different than `MY-PACKAGE`. Names are now normalized to lower case before queries occur.  
|  [#5529](https://github.com/hapifhir/hapi-fhir/issues/5529) |  When using a chained SearchParameter to search within a Bundle as [described here](https://smilecdr.com/docs/fhir_storage_relational/chained_searches_and_sorts.html#document-and-message-search-parameters), if the `Bundle.entry.fullUrl` was fully qualified but the reference was not, the search did not work. This has been corrected.  
|  [#5537](https://github.com/hapifhir/hapi-fhir/issues/5537) |  Calling the method getOrCreateContentType in AttachmentUtil on an attachment with no content type would throw exception because contentType is a code not a string. This fixes the function to create an empty code as expected  
|  [#5546](https://github.com/hapifhir/hapi-fhir/issues/5546) |  A database migration added trailing spaces to server-assigned resource ids. This fix removes the bad migration, and adds another migration to fix the errors.  
|  [#5547](https://github.com/hapifhir/hapi-fhir/issues/5547) |  Previously LIKE queries against resources would perform poorly on PostgreSQL if the database locale/collation was not 'C'. This has been resolved by checking hfj_spidx_string.sp_value_normalized and hfj_spidx_uri.sp_uri column collations during migration and if either or both are non C, create a new btree varchar_pattern_ops on the hash values. If both column collations are 'C', do not create any new indexes.  
|  [#5547](https://github.com/hapifhir/hapi-fhir/issues/5547) |  The addition of the indexes `idx_sp_uri_hash_identity_pattern_ops` and `idx_sp_string_hash_nrm_pattern_ops` could occasionally timeout during migration in Postgresql on large databases, leaving the migration table in a failed state, and Smile CDR unable to boot. Now existence of the index is checked before attempting to add it again.  
|  [#5553](https://github.com/hapifhir/hapi-fhir/issues/5553) |  mdm-clear jobs are prone to failing because of deadlocks when running on SQL Server. Such job failures have been mitigated to some extent by increasing the retries on deadlocks.  
|  [#5563](https://github.com/hapifhir/hapi-fhir/issues/5563) |  Previously, certain mdm configuration could lead to duplicate eid identifier entries in golden resources. This has been corrected  
|  [#5564](https://github.com/hapifhir/hapi-fhir/issues/5564) |  Previously, FHIRPath expression evaluation when using the `_fhirpath` parameter would not work on chained use of 'resolve()'. This was most notable when using `_fhirpath` with FHIR Documents (i.e. 'Bundle' of type 'document' where 'entry[0]' is a 'Composition'). This has now been fixed.  
|  [#5567](https://github.com/hapifhir/hapi-fhir/issues/5567) |  When updating or reindexing a resource in the JPA server, if duplicate rows are present in the indexing tables, the duplicate rows may fail to be removed. Duplicates are not typically present in these tables, but can happen if an indexing job fails in some circumstances. The impact of these rows not being cleaned up is that resources may appear in search results that they should no longer appear in.  
|  [#5589](https://github.com/hapifhir/hapi-fhir/issues/5589) |  When encoding resources using the RDF parser, placeholder IDs (i.e. resource IDs starting with `urn:`) were not omitted as they are in the XML and JSON parsers. This has been corrected.  
|  [#5589](https://github.com/hapifhir/hapi-fhir/issues/5589) |  When encoding a Bundle, if resources in bundle entries had a value in `Bundle.entry.fullUrl` but no value in `Bundle.entry.resource.id`, the parser sometimes incorrectly moved these resources to be contained within other resources when serializing the bundle. This has been corrected.  
|  |  $everything queries with MDM expansion were bypassing the partition boundary by return resources outside the partition. Also, POST (as opposed to GET) $everything queries with MDM expansion. The first issue was fixed by reversing the previous changes in [5493](https://github.com/hapifhir/hapi-fhir/issues/5493) and by filtering source and golden resources by partition ID. The second issue was fixed by correctly capturing the MDM expansion flag in POST $everything queries.  
|  [#5602](https://github.com/hapifhir/hapi-fhir/issues/5602) |  Previously, instance-level Patient Bulk Export batch job would fail if the `_type` parameter was empty. This is now fixed and `_type` will default to all resource types in the Patient Compartment plus Device.  
|  [#5603](https://github.com/hapifhir/hapi-fhir/issues/5603) |  Previously, the semantics of `is-a` were incorrect in Valueset Expansion. The implementation previously used the behaviour of `descendent-of`, which means that `A is-a A` was not being considered as true. This has been corrected. In addition, `descendent-of` is now supported, which compares for strict descendency, and does not include itself. Thanks to Ole Hedegaard (@ohetrifork) for the fix.  
|  [#5606](https://github.com/hapifhir/hapi-fhir/issues/5606) |  Fixed an issue where executing $trigger-subscription with a search URL criteria on a partitioned Subscription resource would result in the failure to deliver the affected resources. This issue has now been resolved.  
|  [#5617](https://github.com/hapifhir/hapi-fhir/issues/5617) |  Resource UserData RESOURCE_PARTITION_ID was incorrectly being set to null for the default partition. This has been corrected to use RequestPartitionId.defaultPartition()  
|  [#5619](https://github.com/hapifhir/hapi-fhir/issues/5619) |  Previously, when a transaction was posted with a resource that had placeholder references and auto versioning references enabled for that path, if the target resource was included in the Bundle but not modified, the reference was saved with a version number that didn't exist. This has been fixed.  
|  [#5621](https://github.com/hapifhir/hapi-fhir/issues/5621) |  Fixed a deadlock in resource conditional create.  
|  [#5623](https://github.com/hapifhir/hapi-fhir/issues/5623) |  Previously, searches that used more than one chained `Bundle` `SearchParameter` (i.e. `Composition`) were only adding one condition to the underlying SQL query which resulted in incorrect search results. This has been fixed.  
|  [#5626](https://github.com/hapifhir/hapi-fhir/issues/5626) |  Previously, an exception could be thrown by the container when executing a contextClosedEvent on the Scheduler Service. This issue has been fixed.  
|  [#5632](https://github.com/hapifhir/hapi-fhir/issues/5632) |  Previously bulk export operation was returning an empty response when no resources matched the request, which didn't comply with [HL7 HAPI IG](https://hl7.org/fhir/uv/bulkdata/export/index.html#response---complete-status). This has been corrected.  
|  [#5633](https://github.com/hapifhir/hapi-fhir/issues/5633) |  Smile failed to save resources running on Oracle when installed from 2023-02 or earlier. This has been fixed.  
|  [#5634](https://github.com/hapifhir/hapi-fhir/issues/5634) |  Previously, expanding a 'ValueSet' with no concepts based on system `urn:ietf:bcp:13` would fail with `ExpansionCouldNotBeCompletedInternallyException`. This has been fixed.  
|  [#5636](https://github.com/hapifhir/hapi-fhir/issues/5636) |  Previously, the number of threads allocated to the $expunge operation in certain cases could be more than configured, this would cause hundreds of threads to be created and all available database connections to be consumed. This has been fixed.  
|  [#5640](https://github.com/hapifhir/hapi-fhir/issues/5640) |  Clinical reasoning version bump to address reported 'null pointer' error that is encountered when running $evaluate-measure against a measure with an omitted measure.group.population.id  
|  [#5642](https://github.com/hapifhir/hapi-fhir/issues/5642) |  A non-superuser with correct permissions encounters HAPI-0339 when POSTING a transaction Bundle with a PATCH. This has been fixed.  
|  [#5644](https://github.com/hapifhir/hapi-fhir/issues/5644) |  Previously, searching for `Bundle` resources with read all `Bundle` resources permissions, returned an HTTP 403 Forbidden error. This was because the `AuthorizationInterceptor` applied permissions to the resources inside the `Bundle`, instead of the `Bundle` itself. This has been fixed and permissions are no longer applied to the resources inside a `Bundle` of type `document`, `message`, or `collection` for `Bundle` requests.  
|  [#5649](https://github.com/hapifhir/hapi-fhir/issues/5649) |  Change database upgrade script to avoid holding locks while adding indices.  
|  [#5651](https://github.com/hapifhir/hapi-fhir/issues/5651) |  Previously, conditional creates would fail with HAPI-0929 errors if there was no preceding '?'. This has been fixed.  
|  [#5654](https://github.com/hapifhir/hapi-fhir/issues/5654) |  Fixed a MeasureReport measureScoring bug impacting any measures currently using denominator-exception population will incorrectly calculate the score without following specification. This bug adds an extension to MeasureReport Groups to capture calculated denominator and numerator to bring transparency to the measureScore calculation and act as a dataSource of measureScore instead of behind the scenes calculations.  
|  |  Previously, the Bulk Import (`$import`) job was ignoring the `httpBasicCredentials` section of the incoming parameters object, causing the job to fail with a 403 error. This has been corrected.  
|  [#5659](https://github.com/hapifhir/hapi-fhir/issues/5659) |  Previously, after registering built-in interceptor `PatientIdPartitionInterceptor`, the system bulk export (with no filters) operation would fail with a NullPointerException. This has been fixed.  
|  [#5680](https://github.com/hapifhir/hapi-fhir/issues/5680) |  Previously, after registering built-in interceptor `PatientIdPartitionInterceptor`, while performing an async system bulk export, the `$poll-export-status` operation would fail with a `NullPointerException`. This has been fixed.  
|  [#5700](https://github.com/hapifhir/hapi-fhir/issues/5700) |  Previously (this release cycle), when you call `RemoteTerminologyServiceValidationSupport` method `lookupCode` with a `CodeSystem` that has properties that are not `string` or `Coding`, the method would throw an exception. It should instead accept any type and convert any unsupported type to `string`. This has been fixed.  
|  |  The legacy `$lastn` support has been removed, and only the redesigned 'advanced' version is now supported. The advanced version requires Elasticsearch.  
|  [#5505](https://github.com/hapifhir/hapi-fhir/issues/5505) |  Removed an incorrect implementation of $member-match.  
#  0.2.18HAPI FHIR 6.8.7 (Yucatán)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change7.0.0-0)
##  0.2.18.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-15)
**Released:** 2024-03-01
**Codename:** (Yucatán)
##  0.2.18.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-15)
This release strictly attempts to remove as much usage of the LOB table as possible in postgres. Specifically, in the JPA server, several database columns related to Batch2 jobs and searching have been reworked so that they no will longer use LOB datatypes going forward. This is a significant advantage on Postgresql databases as it removes a significant use of the inefficient `pg_largeobject` table, and should yield performance boosts for MSSQL as well.
##  0.2.18.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-15)
|  [#5748](https://github.com/hapifhir/hapi-fhir/issues/5748) |  In the JPA server, several database columns related to Batch2 jobs and searching have been reworked so that they no will longer use LOB datatypes going forward. This is a significant advantage on Postgresql databases as it removes a significant use of the inefficient `pg_largeobject` table, and should yield performance boosts for MSSQL as well.  
---|---|---  
#  0.2.19HAPI FHIR 6.10.4 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change6.8.7-5748)
##  0.2.19.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-16)
**Released:** 2024-01-31
**Codename:** (Zed)
##  0.2.19.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-16)
##  0.2.19.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-16)
|  [#5621](https://github.com/hapifhir/hapi-fhir/issues/5621) |  Fixed a deadlock in resource conditional create.  
---|---|---  
#  0.2.20HAPI FHIR 6.10.3 (Zed)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#change6.10.4-5621)
##  0.2.20.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#release-information-17)
**Released:** 2024-01-17
**Codename:** (Zed)
##  0.2.20.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#upgrade-instructions-17)
##  0.2.20.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html#changes-17)
|  [#5563](https://github.com/hapifhir/hapi-fhir/issues/5563) |  Previously, certain mdm configuration could lead to duplicate eid identifier entries in golden resources. This has been corrected  
---|---|---  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html)
0.2 Changelog: 2024 
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
[ 0.3 Changelog: 2023 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)