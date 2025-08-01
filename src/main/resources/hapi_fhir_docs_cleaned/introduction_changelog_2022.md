---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html
crawled: 2025-08-01T14:05:58.445776
---

# Introduction Changelog 2022

#  0.4.1Changelog: 2022
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changelog-2022)
#  0.4.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changelog)
#  0.4.3HAPI FHIR 6.4.0 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#hapi-fhir-640-vishwa)
##  0.4.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information)
**Released:** 2022-02-18
**Codename:** (Vishwa)
##  0.4.3.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions)
##  0.4.3.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * SLF4j (All): 1.7.33 -> 2.0.3
  * Logback (All): 1.2.10 -> 1.4.4
  * Woodstox-Core (XML Parser): 6.3.1 -> 6.4.0
  * log4j-to-slf4j (JPA): 2.17.1 -> 2.19.0
  * Jetty (CLI): 9.4.48.v20220622 -> 10.0.12
  * Spring Boot: 2.7.4 -> 2.7.5
  * Graphql-Java (OpenAPI): 17.4 -> 19.2

In addition the following dependencies have been replaced with newer equivalents: 
  * javax.annotation:javax.annotation-api:1.3.2 -> jakarta.annotation:jakarta.annotation-api:1.3.3 – This change brings HAPI FHIR up to the new permanent Maven dependency name for this library. Note that this new version does not change the actual package structure of the included classes from `javax.` to `jakarta.`, it is only a change in the Maven packaging. 
  * javax.transaction:javax.transaction-api:1.3.2 -> jakarta.transaction:jakarta.transaction-api:1.3.5 – This change brings HAPI FHIR up to the new permanent Maven dependency name for this library. Note that this new version does not change the actual package structure of the included classes from `javax.` to `jakarta.`, it is only a change in the Maven packaging. 

  
---|---|---  
|  [#4153](https://github.com/hapifhir/hapi-fhir/issues/4153) |  Extend $member-match to store Consent resource when there is a matching patient.  
|  [#4196](https://github.com/hapifhir/hapi-fhir/issues/4196) |  In-memory caching provided by Caffeine has been refactored into using s ServiceLoader pattern in order to improve Android compatibility. Thanks to Vitor Pamplona for the pull request!  
|  [#4227](https://github.com/hapifhir/hapi-fhir/issues/4227) |  In some cases in the JPA server, CodeSystem updating failed and left the server with a hung reindex job that never completes. This has been corrected. Thanks to GitHub user @tyfoni-systematic for the bug report!  
|  [#4227](https://github.com/hapifhir/hapi-fhir/issues/4227) |  When indexing CodeSystem content in the terminology service, an unintended busy-wait cycle sometimes caused the CPU to be thrashed while waiting for a batch job to complete. This has been resolved.  
|  [#4227](https://github.com/hapifhir/hapi-fhir/issues/4227) |  The internal LinkedChannelQueue implementation used for Batch2 processing and Subscription delivery now uses automated retries if a message processing failure occurs in order to improve resiliency.  
|  [#4227](https://github.com/hapifhir/hapi-fhir/issues/4227) |  When validating terminology as a part of resource validation or by directly calling the $validate-code operations, an incorrect caching routine sometimes caused incorrect cached results to be returned in cases where display names are being validated and multiple display names were validated for the same system+code combination. This has been corrected.  
|  [#4260](https://github.com/hapifhir/hapi-fhir/issues/4260) |  The HFJ_RESOURCE table has a new column FHIR_ID which always contains the FHIR resource id, both client-assigned and server-assigned. This is parallel storage to allow gradual upgrade away from the current HFJ_FORCED_ID join table, which should improve performance in a future release.  
|  [#4266](https://github.com/hapifhir/hapi-fhir/issues/4266) |  The JPA server can now handle indexing search parameters of type `number` where the value is a Range, e.g. RiskAssessment.probability.  
|  [#4266](https://github.com/hapifhir/hapi-fhir/issues/4266) |  The JPA server can now handle indexing search parameters of type `token` and `reference` where the value is a CodeableReference (a new datatype added in R5).  
|  [#4291](https://github.com/hapifhir/hapi-fhir/issues/4291) |  The NPM package installer did not support installing on R4B repositories. Thanks to Jens Kristian Villadsen for the pull request!  
|  [#4293](https://github.com/hapifhir/hapi-fhir/issues/4293) |  The BundleBuilder now supports adding conditional DELETE operations, PATCH operations, and conditional PATCH operations to a transaction bundle.  
|  [#4293](https://github.com/hapifhir/hapi-fhir/issues/4293) |  When updating resources using a FHIR transaction in the JPA server, if the client instructs the server to include the resource body in the response, any tags that have been carried forward from previous versions of the resource are now included in the response.  
|  [#4293](https://github.com/hapifhir/hapi-fhir/issues/4293) |  When performing create/update/patch/delete operations against the JPA server, the response OperationOutcome will now include additional details about the outcome of the operation. This includes: 
  * For updates, the message will indicate the the update did not contain any changes (i.e. a No-op)
  * For conditional creates/updates/deletes, the message will indicate whether the conditional URL matched any existing resources and the outcome of the operation.
  * A new coding has been added to the `OperationOutcome.issue.details.coding` containing a machine processable equivalent to the outcome.

  
|  [#4294](https://github.com/hapifhir/hapi-fhir/issues/4294) |  The `hapi-fhir-jpaserver-cql` module has been replaced with a module called `hapi-fhir-storage-cr`. This was done to enable CQL evaluation and other clinical reasoning operations on non-JPA servers (like servers using MongoDb). Additionally, the new module supports some additional features for Measure evaluation, such as stratifiers and CQL language-level 1.5. The CQL engine and related components have also been upgraded to the latest versions.  
|  [#4311](https://github.com/hapifhir/hapi-fhir/issues/4311) |  When a REST client performs a search with an unknown search parameter, the resulting error message will now sort and deduplicate the list of allowable search parameters. Thanks to GitHub user @granadacoder for the pull request!  
|  [#4312](https://github.com/hapifhir/hapi-fhir/issues/4312) |  Previously, when $member-match operation is executed, the parameters that were returned did not include the member identifier as a parameter. This has now been added, and the consent resource is now updated with this identifier that's being returned.  
|  [#4323](https://github.com/hapifhir/hapi-fhir/issues/4323) |  Previously, the feature of Payload Search Result Mode only applied to Rest Hook subscriptions. Now, this feature also applies to Message channel type. Message delivery module will perform a search using user-defined criteria and the search results will be delivered to the user-specified endpoint channel as a bundle resource when Payload Search Result Mode is used.  
|  [#4327](https://github.com/hapifhir/hapi-fhir/issues/4327) |  Before A MDM `POSSIBLE_MATCH` link generated together with a `POSSIBLE_DUPLICATE` Golden Resource was missing the score value. this has been fixed.  
|  [#4371](https://github.com/hapifhir/hapi-fhir/issues/4371) |  Added resource type parameter to $mdm-query-links  
|  [#4385](https://github.com/hapifhir/hapi-fhir/issues/4385) |  Group bulk export async will not retrieve Device resources even if they're linked to a Patient and Group. This has been fixed by adding Device to the list of qualifying resources.  
|  [#4416](https://github.com/hapifhir/hapi-fhir/issues/4416) |  The JPA server now supports contention aware patch operations (i.e. using `If-Match`) within a FHIR Transaction. Previously the `Bundle.entry.request.ifMatch` element was incorrectly treated as a conditional URL and not a version tag.  
|  [#4426](https://github.com/hapifhir/hapi-fhir/issues/4426) |  Added sorting capability to $mdm-query-links operation.  
|  [#4432](https://github.com/hapifhir/hapi-fhir/issues/4432) |  Added resourceType as a parameter to $mdm-duplicate-golden-resources operation.  
|  [#4438](https://github.com/hapifhir/hapi-fhir/issues/4438) |  A new utility called `CompositionBuilder` has been added. This class can be used to generate Composition resources in a version-independent way, similar to the function of `BundleUtil` for Bundle resources.  
|  [#4438](https://github.com/hapifhir/hapi-fhir/issues/4438) |  A new experimental API has been added for automated generation of International Patient Summary (IPS) documents in the JPA server. This module is based on the excellent work of Rio Bennin and Panayiotis Savva of the University of Cyprus and was completed at FHIR Connectathon 34 in Henderson Nevada.  
|  [#4438](https://github.com/hapifhir/hapi-fhir/issues/4438) |  The FhirPath evaluator framework now allows for an optional evaluation context object which can be used to supply external data such as responses to the `resolve()` function.  
|  [#4438](https://github.com/hapifhir/hapi-fhir/issues/4438) |  The Thymeleaf narrative generator can now declare template fragments in separate files so that they can be reused across multiple templates.  
|  [#4456](https://github.com/hapifhir/hapi-fhir/issues/4456) |  The Testpage Overlay module now supports customizable operation buttons on search results. For example, you can configure each row of the search results page to include a `$validate` button and a `$diff` button, or choose other operations relevant to your use case. Currently only operations which do not require any parameters are possible.  
|  [#4463](https://github.com/hapifhir/hapi-fhir/issues/4463) |  Providing the capability to specify that the name of the subscription matching channel should be unqualified at creation time.  
|  [#3439](https://github.com/hapifhir/hapi-fhir/issues/3439) |  Previously, jobs were scheduled via a @PostConstruct annotated method. This has been changed to an implementation of IJobScheduler interface so the job scheduler can schedule all the jobs after it has been started.  
|  [#4065](https://github.com/hapifhir/hapi-fhir/issues/4065) |  A new DaoConfig configuration setting has been added called JobFastTrackingEnabled, default false. If this setting is enabled, then gated batch jobs that produce only one chunk will immediately trigger a batch maintenance job. This may be useful for testing, but is not recommended for production use. Prior to this change, fasttracking was always enabled which meant if the server was not busy, small batch jobs would be processed quickly. However this lead do instability on high-volume servers, so this feature is now disabled by default.  
|  [#4221](https://github.com/hapifhir/hapi-fhir/issues/4221) |  The JPA server implementation of the $process-message operation has been extracted into a standalone provider called `ProcessMessageProvider` which can be registered individually on servers that have provided an implementation of the backing operation. Because this operation can not be implemented in a generic was in HAPI FHIR (it assumes business-specific processing logic) it does not make sense to include it by default.  
|  [#4244](https://github.com/hapifhir/hapi-fhir/issues/4244) |  Replace usages of ResourcePersistentId with an interface IResourcePersistentId and parameterize specific implementations of it in classes that rely on a specific implementation.  
|  [#4363](https://github.com/hapifhir/hapi-fhir/issues/4363) |  Common `SearchParameter` functionality has been refactored. In particular, 1) Validation logic performed inside `JpaResourceDaoSearchParameter` has been moved into a new class called `SearchParameterDaoValidator` 2) Logic for extracting combo unique and combo non-unique search parameters that was inside `SearchParamWithInlineReferencesExtractor` has been moved into `BaseSearchParamExtractor` and 3) a new interface called `IResourceIndexComboSearchParameter` has been added which provides a common interface for combo unique and combo non-unique search parameters.  
|  [#4424](https://github.com/hapifhir/hapi-fhir/issues/4424) |  The `CircularQueueCaptureQueriesListener` countXXXXQueries() methods have been reworked to improve accuracy. Query totals previously counted two SQL statement executions with the same SQL but different parameters as one query, this will now be counted as two queries.  
|  [#4430](https://github.com/hapifhir/hapi-fhir/issues/4430) |  The ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor now logs request/response bodies at the DEBUG level. This is changed from INFO level.  
|  [#4430](https://github.com/hapifhir/hapi-fhir/issues/4430) |  The cli reindex-terminology command reports errors with a simpler log message.  
|  [#4440](https://github.com/hapifhir/hapi-fhir/issues/4440) |  During testing, the bulk-import command line tool logs file contents. This logging has been demoted to DEBUG level as bulk-import files may contain PHI.  
|  [#4456](https://github.com/hapifhir/hapi-fhir/issues/4456) |  The built-in narrative template for the OperationOutcome resource no longer renders the _OperationOutcome.issue.diagnostic_ inside a <pre> tag, which made long messages hard to read.  
|  [#4537](https://github.com/hapifhir/hapi-fhir/issues/4537) |  ResourceDeliveryMessage no longer includes the payload in toString(). This avoids leaking sensitive data to logs and other channels.  
|  [#3466](https://github.com/hapifhir/hapi-fhir/issues/3466) |  Several issues with the OpenAPI generator have been fixed: * FHIR Operations will always include a POST invocation option * Non-primitive parameters will be excluded from GET invocations * For an idempotent (affectsState=false) endpoint with at least one required non-primitive parameter, only support POST * Also include `_search` endpoints for the Search operation Thanks to Michael Lawley for the pull request!  
|  [#3482](https://github.com/hapifhir/hapi-fhir/issues/3482) |  Previously, persistence modules were attempting to activate subscriptions that used channel types they did not support. This has been changed, and those subscriptions will not be activated if the given channel type is not supported  
|  [#3979](https://github.com/hapifhir/hapi-fhir/issues/3979) |  Previously, the MdmStorageInterceptor was needlessly logging a warning if theOldResource of an update was null. This is now fixed.  
|  [#4090](https://github.com/hapifhir/hapi-fhir/issues/4090) |  Previously, mdm links that connected resources and golden resources that were newly created had a link score of null, this changes it to 1.0 as the golden resource should be a perfect match with the source resource it was created from.  
|  [#4156](https://github.com/hapifhir/hapi-fhir/issues/4156) |  Before multiple MDM `POSSIBLE_MATCH` links pointing to different Golden Resources could be accepted (updated to `MATCH`). This has now been fixed, allowing only one MATCH.  
|  [#4180](https://github.com/hapifhir/hapi-fhir/issues/4180) |  Before DB table joins were `LEFT OUTER` by default, which was causing some queries to skip table indexes, making them very slow. This was more noticeable in H2 database, but affected all DB types in different measures. This has been fixed by using `INNER` joins, unless specific use case requires otherwise.  
|  [#4214](https://github.com/hapifhir/hapi-fhir/issues/4214) |  Before, When exporting a group including patients referenced in observations, observations were missed from output if `Search Parameters Indexing` was enabled. This is now fixed.  
|  [#4270](https://github.com/hapifhir/hapi-fhir/issues/4270) |  When using the client LoggingInterceptor with OkHttp clients, the request URL was not correctly logged. Thanks to Roel Scholten for the pull request!  
|  [#4281](https://github.com/hapifhir/hapi-fhir/issues/4281) |  Previously, when creating a bundle, if multiple resources matched the conditional URL provided in the request, it would process the request using the last resource found as a match. An error is now thrown when multiple resources are found  
|  [#4284](https://github.com/hapifhir/hapi-fhir/issues/4284) |  Previously, an `ExecutionException` was thrown when `$expunge` operations were used with a small batch size and a low thread count due to a race condition involving the expunge limit. This has been corrected.  
|  [#4287](https://github.com/hapifhir/hapi-fhir/issues/4287) |  Fixed bug with merging 2 Golden Resources together. When merging GP1 into GP2, the result was (incorrectly) GP2 REDIRECT GP1 (instead of GP1 REDIRECT GP2).  
|  [#4287](https://github.com/hapifhir/hapi-fhir/issues/4287) |  Fixed bug with merging 2 Golden Resources together. When merging a POSSIBLE_DUPLICATE of GP1 to GP2, or GP2 to GP1, depending on the order, a dangling self-referential link would sometimes remain (eg, GP1 POSSIBLE_MATCH GP1).  
|  [#4289](https://github.com/hapifhir/hapi-fhir/issues/4289) |  The ConceptMap/$translate operation did not work for transation against a ConceptMap resource ID if the ID was non-numeric. This has been fixed. Thanks to Panayiotis Savva for the report!  
|  [#4295](https://github.com/hapifhir/hapi-fhir/issues/4295) |  A race condition in the Cache factory has been resolved. This issue could cause strange errors such as NoSuchElementException if caches are created in multiple threads.  
|  [#4309](https://github.com/hapifhir/hapi-fhir/issues/4309) |  Broke out services for loading npm packages and parsing npm package resources that will not be dependent on any the DAO layer.  
|  [#4315](https://github.com/hapifhir/hapi-fhir/issues/4315) |  Made the partitionId field of ResourceModifiedMessage also available to ResourceOperationMessage. This can be used to manually select a partition for messages of this type  
|  [#4324](https://github.com/hapifhir/hapi-fhir/issues/4324) |  Previously, an excessive amount of time was required to perform update on a large Group. This has been corrected.  
|  [#4330](https://github.com/hapifhir/hapi-fhir/issues/4330) |  When running HAPI FHIR-based applications from within IntelliJ, an arbitrary ordering of PostConstruct methods in BaseHapiFhirResourceDao could cause a crash. This has been fixed.  
|  [#4341](https://github.com/hapifhir/hapi-fhir/issues/4341) |  Fixes a regression where IndexedSearchParamExtractor returns too few links when extracting search parameters from a resource that hasn't persisted yet. E.g. when a create interceptor needs to extract search parameters from the resource before it is persisted.  
|  [#4348](https://github.com/hapifhir/hapi-fhir/issues/4348) |  Previously, if a DSTU3 Resource was updated and no changes were made, the `meta.versionId` value was incremented incorrectly. This has been fixed.  
|  [#4350](https://github.com/hapifhir/hapi-fhir/issues/4350) |  Support was accidentally removed for date `sa` and `eb` modifiers in the `date` type search parameters. This has been corrected.  
|  [#4360](https://github.com/hapifhir/hapi-fhir/issues/4360) |  Previously, Patient Bulk Export on certain resources with reference to patient only in author or performer did not get exported. This has been fixed.  
|  [#4364](https://github.com/hapifhir/hapi-fhir/issues/4364) |  Running MDM update link will fail if resources were created with a previous MDM rules version. This is now fixed with a query that retrieves and updates MDM links regardless of the MDM version of the resource links.  
|  [#4375](https://github.com/hapifhir/hapi-fhir/issues/4375) |  Depending on configuration, validation will reject resource XML that contains comments, specifically, from the JSON that's generated from the resource object. Also, for this same configuration validation will fail with an error that it can't locate resource HapiFhirStorageResponseCode.json. Both errors are now fixed.  
|  [#4387](https://github.com/hapifhir/hapi-fhir/issues/4387) |  Previously, when use group bulk export, if the group was referenced to a patient resource and the patient has not been updated after the _since parameter of the bulk export, none of their data/sub-resources comes through even if the sub-resources were qualified (By qualified, I mean the sub-resources were upadted/created after _since parameter). Now, this issue has been fixed.  
|  [#4388](https://github.com/hapifhir/hapi-fhir/issues/4388) |  Fixed an edge case during a Read operation where hooks could be invoked with a null resource. This could cause a NullPointerException in some cases.  
|  [#4395](https://github.com/hapifhir/hapi-fhir/issues/4395) |  Fixed a bug where the PackageInstallerSvcImpl was not treating FHIR version R4 as being compatible with R4B.  
|  |  The $mdm-clear operation sometimes failed with a constraint error when running in a heavily multithreaded environment. This has been fixed.  
|  |  When running RestfulServer under heavy load or a slow network, the server sometimes logged an EOFException or an IOException in the system logs despite the response completing successfully. This has been corrected.  
|  [#4400](https://github.com/hapifhir/hapi-fhir/issues/4400) |  When Batch2 work notifications are received twice (e.g. because the notification engine double delivered) an unrecoverable failure could occur. This has been corrected.  
|  [#4417](https://github.com/hapifhir/hapi-fhir/issues/4417) |  Fixed a bug with batch2 which could cause previously completed chunks to be set back to in-progress. This could cause a batch job to never complete. Now, a safeguard to ensure a job can never return to in-progress once it has completed or failed.  
|  [#4420](https://github.com/hapifhir/hapi-fhir/issues/4420) |  Fixed a bug with $meta-delete which will cause the resource, when included as part of a search result, to have all its tags be missing.  
|  [#4422](https://github.com/hapifhir/hapi-fhir/issues/4422) |  When a Bulk Export job runs with a large amount of data, there is a chance the reduction step can be kicked off multiple times, resulting in data loss in the final report. Jobs will now be set to in-progress before processing to prevent multiple reduction steps from being started.  
|  [#4427](https://github.com/hapifhir/hapi-fhir/issues/4427) |  Previously, $export-poll-status requests through POST are failing with a Null Pointer exception. Now, this issue has been fixed and POST $export-poll-status request should be able to proceed.  
|  [#4435](https://github.com/hapifhir/hapi-fhir/issues/4435) |  Java API callers of the JPA IFhirResourceDao#search() method could experience a NPE if they invoked getResources() multiple times on the same response object. This is an edge case that is very unlikely to occur, but is now corrected.  
|  [#4441](https://github.com/hapifhir/hapi-fhir/issues/4441) |  Creating a resource with an invalid embedded resource reference would not fail. Even if IsEnforceReferentialIntegrityOnWrite was enabled. This has been fixed, and invalid references will throw.  
|  [#4449](https://github.com/hapifhir/hapi-fhir/issues/4449) |  A bug prevented valueset expansion with includeHierarchy=true when lucene/elasticsearch was not enabled. This has been corrected. Thanks to GitHub user @ivagulin for the contribution!  
|  [#4456](https://github.com/hapifhir/hapi-fhir/issues/4456) |  The HashMapResourceProvider failed to display history results if the history included deleted resources. This has been corrected.  
|  [#4457](https://github.com/hapifhir/hapi-fhir/issues/4457) |  Previously, when a `ConceptMap` was created or updated using a transaction `Bundle`, the transaction failed with error code `HAPI-0550: could not execute statement`. This has been fixed.  
|  [#4461](https://github.com/hapifhir/hapi-fhir/issues/4461) |  There is inconsistent validation in the case of an incorrect communications language coding due to validation that looks for the first correct Coding. In the case of a single incorrect code there is one validation result. In the case of one correct code and one incorrect code, there is an entirely different result. This has been fixed by introducing a new boolean flag in CachingValidationSupport as well as a new constructor to leverage it.  
|  [#4467](https://github.com/hapifhir/hapi-fhir/issues/4467) |  The FhirTerser#clone method failed to clone resources when contained resources were present in the source resource. This has been fixed.  
|  [#4475](https://github.com/hapifhir/hapi-fhir/issues/4475) |  Enabling mass ingestion mode alters the resource deletion process leaving resources partially deleted. The problem has been fixed.  
|  [#4481](https://github.com/hapifhir/hapi-fhir/issues/4481) |  Previously, a reindex batch job would fail when executing on deleted resources. This issue has been fixed.  
|  [#4485](https://github.com/hapifhir/hapi-fhir/issues/4485) |  Cross-partition subscription PUT with a custom interceptor will fail on validation because the read partition ID is used. This has been fixed by skipping validation if the validator invoked during an update operation  
|  [#4486](https://github.com/hapifhir/hapi-fhir/issues/4486) |  Previously, some MDM links of type `POSSIBLE_MATCH` were saved with unnormalized score values. This has been fixed.  
|  [#4491](https://github.com/hapifhir/hapi-fhir/issues/4491) |  Batch2 Jobs in the FINALIZE state can now be cancelled.  
|  [#4491](https://github.com/hapifhir/hapi-fhir/issues/4491) |  Moved batch2 reduction step logic to the messaging queue. Before it was executed during the maintenance run directly. This resulted in bugs with multiple reduction steps kicking off for long running jobs.  
|  [#4520](https://github.com/hapifhir/hapi-fhir/issues/4520) |  Updating documentation related to narrative generation.  
|  [#4500](https://github.com/hapifhir/hapi-fhir/issues/4500) |  Schedule bulk export job and binary was not working with relational databases. This has now been fixed with a reimplementation for batch 2.  
|  [#4508](https://github.com/hapifhir/hapi-fhir/issues/4508) |  Deleting CodeSystem resources by URL then expunging would fail to expunge and a foreign key error would be thrown. This has been fixed.  
|  [#4511](https://github.com/hapifhir/hapi-fhir/issues/4511) |  Previously, bulk export jobs were getting stuck in the `FINALIZE` state when performed with many resources and a low Bulk Export File Maximum Capacity. This has been fixed.  
|  [#4516](https://github.com/hapifhir/hapi-fhir/issues/4516) |  A new command has been added to the HAPI-FHIR CLI called `clear-migration-lock`. This can be used to fix a database state which can occur if a migration is interrupted before completing.  
|  [#4523](https://github.com/hapifhir/hapi-fhir/issues/4523) |  Previously, meta source field in subscription messages was inconsistently populated regarding different requests. Now, this has been fixed and meta source will be included in all subscription messages.  
|  [#4526](https://github.com/hapifhir/hapi-fhir/issues/4526) |  Fixing an issue where a long running reduction step causes the message not to be processed fast enough, thereby allowing multiple reduction step jobs to start.  
|  [#4528](https://github.com/hapifhir/hapi-fhir/issues/4528) |  Three database columns have been changed from type TEXT to type OID when running in Postgres: BT2_JOB_INSTANCE.PARAMS_JSON_LOB, BT2_JOB_INSTANCE.REPORT, and BT2_WORK_CHUNK.CHUNK_DATA. This prevents VACUUM erasing binary objects that are still in use.  
|  [#4533](https://github.com/hapifhir/hapi-fhir/issues/4533) |  Previously, if a message with a null header was sent to a Channel Import module and failed, a NullPointerException would occur and the consumer would become unable to receive any further messages. This has now been fixed.  
#  0.4.4HAPI FHIR 6.2.2 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.4.0-0)
##  0.4.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-1)
**Released:** 2022-11-25
**Codename:** (Vishwa)
##  0.4.4.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-1)
This version fixes a bug with 6.2.0 and previous releases wherein batch jobs that created very large chunk counts could occasionally fail to submit a small proportion of chunks.
##  0.4.4.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-1)
|  [#4291](https://github.com/hapifhir/hapi-fhir/issues/4291) |  The NPM package installer did not support installing on R4B repositories. Thanks to Jens Kristian Villadsen for the pull request!  
---|---|---  
#  0.4.5HAPI FHIR 6.2.1 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.2.2-4291)
##  0.4.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-2)
**Released:** 2022-11-17
**Codename:** (Vishwa)
##  0.4.5.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-2)
This version fixes a bug with 6.2.0 and previous releases wherein batch jobs that created very large chunk counts could occasionally fail to submit a small proportion of chunks.
##  0.4.5.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-2)
#  0.4.6HAPI FHIR 6.2.0 (Vishwa)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#hapi-fhir-620-vishwa)
##  0.4.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-3)
**Released:** 2022-11-17
**Codename:** (Vishwa)
##  0.4.6.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-3)
This release has breaking changes.
  * Hibernate Search mappings for Terminology entities have been upgraded, which requires terminology freetext reindexing.


To recreate Terminology freetext indexes use CLI command: `reindex-terminology`
##  0.4.6.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-3)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Woodstox (Base): 6.2.5 -> 6.3.1
  * Spring (JPA): 5.3.20 -> 5.3.23
  * Hibernate-Core (JPA): 5.6.2 -> 5.6.12
  * Postgresql JDBC (JPA): 42.4.1 -> 42.5.0
  * H2 (JPA): 2.1.212 -> 2.1.214
  * Caffeine (JPA): 2.9.1 -> 3.1.1
  * Commons-Text (JPA and Testpage Overlay): 1.9.0 -> 1.10.0 (Addresses [CVE-2022-42889](https://nvd.nist.gov/vuln/detail/CVE-2022-42889))
  * Spring Boot (Boot): 2.6.7 -> 2.7.4
  * Jackson Databind: 2.13.2.2 -> 2.13.4.1
  * Snakeyaml : 1.31 -> 1.33
  * Graphql-Java : 17.3 -> 17.4

  
---|---|---  
|  [#3276](https://github.com/hapifhir/hapi-fhir/issues/3276) |  Added a new optional parameter to the `upload-terminology` operation of the HAPI-FHIR CLI. you can pass the `-s` or `--size` parameter to specify the maximum size that will be transmitted to the server, before a local file reference is used. This parameter can be filled in using human-readable format, for example: `upload-terminology -s "1GB"` will permit zip files up to 1 gigabyte, and anything larger than that would default to using a local file reference.  
|  [#3839](https://github.com/hapifhir/hapi-fhir/issues/3839) |  Advanced Lucene indexing now fully supports composite SearchParameters.  
|  [#3947](https://github.com/hapifhir/hapi-fhir/issues/3947) |  Previously, DELETE request type is not supported for any operations. DELETE is now supported, and is enabled for operation $export-poll-status to allow cancellation of jobs  
|  [#3989](https://github.com/hapifhir/hapi-fhir/issues/3989) |  Provided the ability to have the NPM package installer skip installing a package if it is already installed and matches the version requested. This can be controlled by the `reloadExisting` attribute in PackageInstallationSpec. It defaults to `true`, which is the existing behaviour. Thanks to Craig McClendon (@XcrigX) for the contribution!  
|  |  Added support for AWS OpenSearch to Fulltext Search. If an AWS Region is configured, HAPI-FHIR will assume you intend to connect to an AWS-managed OpenSearch instance, and will use Amazon's [DefaultAwsCredentialsProviderChain](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html) to authenticate against it. If both username and password are provided, HAPI-FHIR will attempt to use them as a static credentials provider.  
|  [#4016](https://github.com/hapifhir/hapi-fhir/issues/4016) |  When using ForcedOffsetSearchModeInterceptor, any synchronous searches initiated programmatically (i.e. through the internal java API, not the REST API) will not be modified. This prevents issues when a java call requests a synchronous search larger than the default offset search page size.  
|  [#4030](https://github.com/hapifhir/hapi-fhir/issues/4030) |  Previously, Patient Bulk Export only supported endpoint [fhir base]/Patient/$export, which exports all patients. Now, Patient Export can be done at the instance level, following this format: `[fhir base]/Patient/[id]/$export`, which will export only the records for one patient. Additionally, added support for the `patient` parameter in Patient Bulk Export, which is another way to get the records of only one patient.  
|  [#4034](https://github.com/hapifhir/hapi-fhir/issues/4034) |  Added support for the `:text` modifier on string search parameters. This corrects an issue when using Elastic/Lucene indexing enabled where prefix match was used instead.  
|  [#4052](https://github.com/hapifhir/hapi-fhir/issues/4052) |  LOINC terminology upload process was enhanced to consider 24 additional properties which were defined in loinc.csv file but not uploaded.  
|  [#4057](https://github.com/hapifhir/hapi-fhir/issues/4057) |  A new built-in server interceptor called the InteractionBlockingInterceptor has been added. This interceptor allows individual operations to be included/excluded from a RestfulServer's exported capabilities.  
|  [#4057](https://github.com/hapifhir/hapi-fhir/issues/4057) |  The OpenApi generator now allows additional CSS customization for the Swagger UI page, as well as the option to disable resource type pages.  
|  [#4080](https://github.com/hapifhir/hapi-fhir/issues/4080) |  Previously, Group Bulk Export did not support the inclusion of resources referenced in the resources in the patient compartment. This is now supported.  
|  [#4106](https://github.com/hapifhir/hapi-fhir/issues/4106) |  LOINC terminology upload process was enhanced by loading `MAP_TO` properties defined in MapTo.csv input file to TermConcept(s).  
|  [#4116](https://github.com/hapifhir/hapi-fhir/issues/4116) |  Added new attribute for the @Operation annotation to define the operation's canonical URL. This canonical URL value will populate the operation definition in the CapabilityStatement resource.  
|  [#4117](https://github.com/hapifhir/hapi-fhir/issues/4117) |  Previously, the number of resources per binary file in bulk export was a static 1000. This is now configurable by a new DaoConfig property called 'setBulkExportFileMaximumCapacity()', and the default value is 1000 resources per file.  
|  [#4125](https://github.com/hapifhir/hapi-fhir/issues/4125) |  A new interceptor pointcut `STORAGE_TRANSACTION_PROCESSING` has been added. Hooks for this pointcut can examine and modify FHIR transaction bundles being processed by the JPA server before processing starts.  
|  [#4133](https://github.com/hapifhir/hapi-fhir/issues/4133) |  Extend $member-match to validate matched patient against family name and birthdate  
|  [#4140](https://github.com/hapifhir/hapi-fhir/issues/4140) |  For SNOMED CT, upload-terminology now supports both Canadian and International edition's file names for the SCT Description File  
|  [#4150](https://github.com/hapifhir/hapi-fhir/issues/4150) |  Support has been added for FHIR R4B (4.3.0). See the [R4B Documentation](https://hapifhir.io/hapi-fhir/docs/getting_started/r4b.html) for more information on what this means.  
|  [#4154](https://github.com/hapifhir/hapi-fhir/issues/4154) |  By default, if the `$export` operation receives a request that is identical to one that has been recently processed, it will attempt to reuse the batch job from the former request. A new configuration parameter has been introduced that disables this behavior and forces a new batch job on every call.  
|  [#4182](https://github.com/hapifhir/hapi-fhir/issues/4182) |  `$mdm-submit` can now be run as a batch job, which will return a job ID, and can be polled for status. This can be accomplished by sending a `Prefer: respond-async` header with the request.  
|  [#4187](https://github.com/hapifhir/hapi-fhir/issues/4187) |  A remove method has been added to the Batch2 job registry. This will allow for dynamic job registration in the future.  
|  [#4224](https://github.com/hapifhir/hapi-fhir/issues/4224) |  Added new System Property called 'CLEAR_LOCK_TABLE_WITH_DESCRIPTION' that when set to the uuid of a lock record, will clear that lock record before attempting to insert a new one.  
|  [#3527](https://github.com/hapifhir/hapi-fhir/issues/3527) |  When using SearchNarrowingInterceptor, FHIR batch operations with a large number of conditional create/update entries exhibited very slow performance due to an unnecessary nested loop. This has been corrected.  
|  [#4007](https://github.com/hapifhir/hapi-fhir/issues/4007) |  Processing for `_include` and `_revinclude` parameters in the JPA server has been streamlined, which should improve performance on systems where includes are heavily used.  
|  |  All Spring Batch dependencies and services have been removed. Async processing has fully migrated to Batch 2.  
|  [#3907](https://github.com/hapifhir/hapi-fhir/issues/3907) |  The Partition Interceptor has been refactored out of the JPA module in order to facilitate future use in other modules. No functional changes have been made.  
|  [#4040](https://github.com/hapifhir/hapi-fhir/issues/4040) |  Removed Flyway database migration engine. The migration table still tracks successful and failed migrations to determine which migrations need to be run at startup. Database migrations no longer need to run differently when using an older database version.  
|  [#4127](https://github.com/hapifhir/hapi-fhir/issues/4127) |  Changed Minimum Size (bytes) in FHIR Binary Storage of the persistence module from an integer to a long.  
|  [#3394](https://github.com/hapifhir/hapi-fhir/issues/3394) |  Cascading deletes don't work correctly if multiple threads initiate a delete at the same time. Either the resource won't be found or there will be a collision on inserting the new version. This changes fixes the problem by better handling these conditions to either ignore an already deleted resource or to keep retrying in a new inner transaction..  
|  [#3419](https://github.com/hapifhir/hapi-fhir/issues/3419) |  With Elasticsearch configured, including terminology, an exception was raised while expanding a ValueSet with more than 10,000 concepts. This has now been fixed.  
|  [#3664](https://github.com/hapifhir/hapi-fhir/issues/3664) |  Initial page loading has been optimized to reduce the number of prefetched resources. This should improve the speed of initial search queries in many cases.  
|  [#3865](https://github.com/hapifhir/hapi-fhir/issues/3865) |  Previously, Celsius and Fahrenheit temperature quantities were not normalized. This is now fixed. This change requires reindexing of resources containing Celsius or Fahrenheit temperature quantities.  
|  [#3868](https://github.com/hapifhir/hapi-fhir/issues/3868) |  With Elastic/Lucene indexing enabled search for numbers or quantities was not always using the specified ranges, because the number of significant figures was not properly calculated. This is now fixed.  
|  [#3890](https://github.com/hapifhir/hapi-fhir/issues/3890) |  When serializing references where the reference target has been instantiated using a resource object and not a reference string, the serialization was omitted when the reference appeared in the resource metadata section. This has been corrected.  
|  [#3897](https://github.com/hapifhir/hapi-fhir/issues/3897) |  Providing a meaningful response message when deleting non-existing or already deleted resources.  
|  [#3914](https://github.com/hapifhir/hapi-fhir/issues/3914) |  Previously, unexpected response status code is not handled by the import-poll-status operation. Now, it is fixed and throwing an error message.  
|  [#3929](https://github.com/hapifhir/hapi-fhir/issues/3929) |  During bulk import, fetching resource files from a client server would fail if the server response specified a `content-type` that was not `application/fhir+json`. Validation has been loosened to accept content-type values of `text/plain` and `application/json`.  
|  [#3937](https://github.com/hapifhir/hapi-fhir/issues/3937) |  Fixing bug where searching with a target resource parameter (Coverage:payor:Patient) as value to an _include parameter would fail with a 500 response.  
|  [#3951](https://github.com/hapifhir/hapi-fhir/issues/3951) |  There are now 2 different methods of Missing Fields search. One that works if Enable Missing Fields Search is enabled, and one that works if it is not. These 2 are not compatible together.  
|  [#3956](https://github.com/hapifhir/hapi-fhir/issues/3956) |  Previously, if the Endpoint Base URL is set to something different from the default value, the URL that export-poll-status returned is incorrect. After correcting the export-poll-status URL, the binary file URL returned is also incorrect. This error has also been fixed and the URLs that are returned from $export and $export-poll-status will not contain the extra path from 'Fixed Value for Endpoint Base URL'.  
|  [#3960](https://github.com/hapifhir/hapi-fhir/issues/3960) |  Previously, when a client would provide a requestId within the source uri of a Meta.source, the provided requestId would get discarded and replaced by an id generated by the system. This has been corrected. And now it depends on configuration.  
|  [#3962](https://github.com/hapifhir/hapi-fhir/issues/3962) |  Previously, using the `import-csv-to-conceptmap` command in the CLI successfully created ConceptMap resources without a `ConceptMap.status` element, which is against the FHIR specification. This has been fixed by adding a required option for status for the command.  
|  [#3964](https://github.com/hapifhir/hapi-fhir/issues/3964) |  Fast-tracking batch jobs that produced only one chunk has been rewritten to use Quartz triggerJob. This will ensure that at most one thread is updating job status at a time. Also jobs that had FAILED, ERRORED, or been CANCELLED could be accidentally set back to IN_PROGRESS; this has been corrected.  
|  [#3987](https://github.com/hapifhir/hapi-fhir/issues/3987) |  Previously, if a FullTextSearchSvcImpl was defined, but was disabled via configuration, there could be data loss when reindexing due to transaction rollbacks. This has been corrected. Thanks to @dyoung-work for the fix!  
|  [#3972](https://github.com/hapifhir/hapi-fhir/issues/3972) |  Previously, the `:nickname` qualifier only worked with the predefined `name` and `given` SearchParameters. This has been fixed and now the `:nickname` qualifier can be used with any string SearchParameters.  
|  [#3980](https://github.com/hapifhir/hapi-fhir/issues/3980) |  A regression was introduced in 6.1.0 which caused bulk export jobs to not default to the correct output format when the `_outputFormat` parameter was omitted. This behaviour has been fixed, and if omitted, will now default to the only legal value `application/fhir+ndjson`.  
|  [#3987](https://github.com/hapifhir/hapi-fhir/issues/3987) |  Previously, bulk export for Group type with _typeFilter did not apply the filter if it was for the patients, and returned all members of the group. This has now been fixed, and the filter will apply.  
|  [#3992](https://github.com/hapifhir/hapi-fhir/issues/3992) |  Previously when ValueSets were pre-expanded after loinc terminology upload, expansion was failing with an exception for each ValueSet with more than 10,000 properties. This problem has been fixed. This fix changed some freetext mappings (definitions about how resources are freetext indexed) for terminology resources, which requires reindexing those resources. To do this use the `reindex-terminology` command.  
|  [#4003](https://github.com/hapifhir/hapi-fhir/issues/4003) |  Added support for -l parameter for providing a local validation profile in the HAPI FHIR CLI.  
|  [#4008](https://github.com/hapifhir/hapi-fhir/issues/4008) |  Previously, when executing a '[base]/_history' search, '_since' and '_at' shared the same behaviour. When a user searched for the date between the records' updated date with '_at', the record of '_at' time was not returned. This has been corrected. '_since' query parameter works as it previously did, and the '_at' query parameter returns the record of '_at' time.  
|  [#4013](https://github.com/hapifhir/hapi-fhir/issues/4013) |  In HAPI-FHIR 6.1.0, a regression was introduced into bulk export causing exports beyond the first one to fail in strange ways. This has been corrected.  
|  [#4017](https://github.com/hapifhir/hapi-fhir/issues/4017) |  Previously, creating a DSTU3 SearchParameter with an expression that does not start with a resource type would throw an error. This has been corrected.  
|  [#4018](https://github.com/hapifhir/hapi-fhir/issues/4018) |  When running HAPI FHIR inside intellij with runtime Nonnull assertions enabled, a sequencing issue in OperationMethodBinding could cause a null pointer exception. This has been corrected.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a bug in Group Bulk Export where the server would crash in oracle due to too many clauses.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a Group Bulk Export bug in which the group members would not be expanded correctly.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a bug in Group Bulk Export: If a group member was part of multiple groups , it was causing other groups to be included during Group Bulk Export, if the Group resource type was specified. Now, when doing an export on a specific group, and you elect to return Group resources, only the called Group will be returned, regardless of cross-membership.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a Group Bulk Export bug which was causing it to fail to return resources due to an incorrect search.  
|  [#4091](https://github.com/hapifhir/hapi-fhir/issues/4091) |  Previously, when the upload-terminology command was used to upload a terminology file with endpoint validation enabled, a validation error occurred due to a missing file content type. This has been fixed by specifying the file content type of the uploaded file.  
|  [#4046](https://github.com/hapifhir/hapi-fhir/issues/4046) |  Search for strings with `:text` qualifier was not performing advanced search. This has been corrected.  
|  [#4051](https://github.com/hapifhir/hapi-fhir/issues/4051) |  Fixed the `$poll-export-status` endpoint so that when a job is complete, this endpoint now correctly includes the `request` and `requiresAccessToken` attributes.  
|  [#4051](https://github.com/hapifhir/hapi-fhir/issues/4051) |  There was a bug in content-type negotiation when reading Binary resources. Previously, when a client requested a Binary resource and with an `Accept` header that matched the `contentType` of the stored resource, the server would return an XML representation of the Binary resource. This has been fixed, and a request with a matching `Accept` header will receive the stored binary data directly as the requested content type.  
|  [#4058](https://github.com/hapifhir/hapi-fhir/issues/4058) |  When enabling validation on a fhir_endpoint module, a bundle with a Measure resource referencing a Library resource or a MeasureReport resource referencing a Measure, validation will fail with an IllegalArgumentException complaining about the Library or Measure resource, respectively. The fix ensures that Library and Measure resources are handled correctly and all of validation can proceed with a report of all success and errors.  
|  [#4063](https://github.com/hapifhir/hapi-fhir/issues/4063) |  Fixed a bug where the $everything operation on Patient instances and the Patient type was not correctly propagating the transactional semantics. This was causing callers to not be in a transactional context.  
|  [#4067](https://github.com/hapifhir/hapi-fhir/issues/4067) |  Fixed a bug where /Patient/$export would fail if _type=Patient parameter was not included.  
|  [#4078](https://github.com/hapifhir/hapi-fhir/issues/4078) |  Modified BinaryAccessProvider to use a safer method of checking the contents of an input stream. Thanks to @ttntrifork for the fix!  
|  [#4082](https://github.com/hapifhir/hapi-fhir/issues/4082) |  A bug prevented the DSTU2 JPA server Conformance.implementation.description field from being populated in some cases. This has been corrected.  
|  [#4084](https://github.com/hapifhir/hapi-fhir/issues/4084) |  Previously, the `error` segment of the `$poll-export-status` operation was missing. This has now been added.  
|  [#4087](https://github.com/hapifhir/hapi-fhir/issues/4087) |  Filter by range for numeric extension does not work after update. hfj_spidx_number record deleted after PUT update on SearchParameter value for Resource  
|  [#4093](https://github.com/hapifhir/hapi-fhir/issues/4093) |  A previous fix resulted in Bulk Export files containing mixed resource types, which is not allowed in the bulk data access IG. This has been corrected.  
|  [#4094](https://github.com/hapifhir/hapi-fhir/issues/4094) |  A previous fix resulted in Bulk Export files containing duplicate resources, which is not allowed in the bulk data access IG. This has been corrected.  
|  [#4098](https://github.com/hapifhir/hapi-fhir/issues/4098) |  Fixed issue where adding a sort parameter to a query would return an incomplete result set.  
|  [#4111](https://github.com/hapifhir/hapi-fhir/issues/4111) |  MDM messages were using the resource id as a message key when it should be using the EID as a partition hash key. This could lead to duplicate golden resources on systems using Kafka as a message broker.  
|  [#4115](https://github.com/hapifhir/hapi-fhir/issues/4115) |  Previously, enabling 'indexMissingFields' and 'advancedHSearchIndexing' functionalities would cause creating a Patient resource to return HTTP 500. Upon further investigation, the same happened for creating an Observation. This has been fixed.  
|  [#4128](https://github.com/hapifhir/hapi-fhir/issues/4128) |  In the JPA server, when deleting a resource the associated tags were previously deleted even though the FHIR specification states that tags are independent of FHIR versioning. After this fix, resource tags and security labels will remain when a resource is deleted. They can be fetched using the `$meta` operation against the deleted resource, and will remain if the resource is brought back in a subsequent update.  
|  [#4128](https://github.com/hapifhir/hapi-fhir/issues/4128) |  In the JPA server, when a resource is being updated, the response will now include any tags or security labels which were not present in the request but were carried forward from the previous version of the resource.  
|  [#4130](https://github.com/hapifhir/hapi-fhir/issues/4130) |  A transaction scoping bug in Batch2 has been resolved, preventing a crash on Postgres. We have also standardized on the Spring @Transactional annotation, and removed use of the equivalent javax.transaction annotation.  
|  [#4139](https://github.com/hapifhir/hapi-fhir/issues/4139) |  Two issues with reverse chaining (i.e. the `_has` search parameter) have been addressed: * Searching for a reverse chain with a target search parameter of `_id` did not work correctly, e.g. `Patient?_has:Observation:subject:_id=Patient/123` * Searching with a combination of a forward chain and a reverse chain did not work correctly if indexing contained resources was enabled, e.g. `Observation?subject._has:Group:member:_id=Group/123`  
|  [#4142](https://github.com/hapifhir/hapi-fhir/issues/4142) |  Upon hitting a subscription delivery failure, we currently log the failing payload which could be considered PHI. Resource content is no longer written to logs on subscription failure.  
|  [#4145](https://github.com/hapifhir/hapi-fhir/issues/4145) |  Documentation was added for `reindex-terminology` command.  
|  [#4147](https://github.com/hapifhir/hapi-fhir/issues/4147) |  Previously when updating a phonetic search parameter, any existing resource will not have its search parameter String updated upon reindex if the normalized String is the same letter as under the old algorithm (ex JN to JAN). Searching on the new normalized String was failing to return results. This has been corrected.  
|  [#4162](https://github.com/hapifhir/hapi-fhir/issues/4162) |  Loading us-core IG was raising `UnprocessableEntityException: HAPI-2131: Can't process submitted SearchParameter as it is overlapping an existing one`. This problem has been fixed.  
|  [#4166](https://github.com/hapifhir/hapi-fhir/issues/4166) |  When storing a blob, the database blob binary storage service may store the blob size as being much smaller than the actual blob size. This issue has been fixed.  
|  [#4173](https://github.com/hapifhir/hapi-fhir/issues/4173) |  When triggering a batch export, the number of resources processed is returned as zero. This is now fixed to return the total number of resources processed across a single or multiple bundles.  
|  [#4175](https://github.com/hapifhir/hapi-fhir/issues/4175) |  Previously, performing $validate operation on resource update and sending the resource as a parameter in the request body would result in an error stating that the resource was missing id. This has been fixed by allowing the resource to be processed correctly when sent as a parameter.  
|  [#4177](https://github.com/hapifhir/hapi-fhir/issues/4177) |  Support has been added for clustered database upgrades in the new (non-FlyWay) migrator codebase. Only one database migration is permitted at once.  
|  [#4185](https://github.com/hapifhir/hapi-fhir/issues/4185) |  When DaoConfig UpdateWithHistoryRewriteEnabled is enabled, the package loader throws a NullPointerException. This has been corrected.  
|  [#4190](https://github.com/hapifhir/hapi-fhir/issues/4190) |  Previously, when using _offset, the queries will result in short pages, and repeats results on different pages. This has now been fixed.  
|  [#4194](https://github.com/hapifhir/hapi-fhir/issues/4194) |  Bulk Group export was failing to export Patient resources when Client ID mode was set to: ANY. This has been fixed  
|  [#4207](https://github.com/hapifhir/hapi-fhir/issues/4207) |  Previously to improve performance, if the total number of resources was less than the _getpageoffset, the results would default to last resource offset. This is especially evident when requests are consecutive resulting in one entry being displayed in some requests. This issue is now fixed.  
|  [#4210](https://github.com/hapifhir/hapi-fhir/issues/4210) |  Generating Bundle with resources was setting `entry.request.url` as absolute url when it should be relative. This has been fixed  
|  [#4218](https://github.com/hapifhir/hapi-fhir/issues/4218) |  Performing a bulk export with an _outputParam value encoded in a GET request URL that contains a '+' (ex: 'application/fhir+ndjson') will result in a 400 because the '+' is replaced with a ' '. After this fix the '+' will remain in the parameter value.  
|  [#4232](https://github.com/hapifhir/hapi-fhir/issues/4232) |  Previously during Bulk Export, if no `_type` parameter was provided, an error would be thrown. This has been changed, and if the `_type` parameter is omitted, Bulk Export will default to all registered types.  
|  [#4234](https://github.com/hapifhir/hapi-fhir/issues/4234) |  Fixed a bug which caused a failure when combining a Consent Interceptor with version conversion via the `Accept` header.  
|  [#4240](https://github.com/hapifhir/hapi-fhir/issues/4240) |  Previously, when creating a `DocumentReference` with an `Attachment` containing a URL over 254 characters an error was thrown. This has been corrected and now an `Attachment` URL can be up to 500 characters.  
|  [#4242](https://github.com/hapifhir/hapi-fhir/issues/4242) |  Batch2 jobs were incorrectly prevented from transitioning from ERRORED to COMPLETE status.  
|  [#4247](https://github.com/hapifhir/hapi-fhir/issues/4247) |  Previously, Bulk Export jobs were always reused, even if completed. Now, jobs are only reused if an identical job is already running, and has not yet completed or failed.  
|  [#4250](https://github.com/hapifhir/hapi-fhir/issues/4250) |  Previously, SearchParameters with identical codes and bases could be created. This has been corrected. If a SearchParameter is submitted which is a duplicate, it will be rejected.  
|  [#4267](https://github.com/hapifhir/hapi-fhir/issues/4267) |  Previously, if the `$reindex` operation failed with a `ResourceVersionConflictException` the related batch job would fail. This has been corrected by adding 10 retry attempts for transactions that have failed with a `ResourceVersionConflictException` during the `$reindex` operation. In addition, the `ResourceIdListStep` was submitting one more resource than expected (i.e. 1001 records processed during a `$reindex` operation if only 1000 `Resources` were in the database). This has been corrected.  
|  [#4271](https://github.com/hapifhir/hapi-fhir/issues/4271) |  Database migration steps were failing with Oracle 19C. This has been fixed by allowing the database engine to skip dropping non-existent indexes.  
|  [#3527](https://github.com/hapifhir/hapi-fhir/issues/3527) |  The `ActionRequestDetails` class has been dropped (it has been deprecated since HAPI FHIR 4.0.0). This class was used as a parameter to the `SERVER_INCOMING_REQUEST_PRE_HANDLED` interceptor pointcut, but can be replaced in any existing client code with `RequestDetails`. This change also removes an undocumented behaviour where the JPA server internally invoked the `SERVER_INCOMING_REQUEST_PRE_HANDLED` a second time from within various processing methods. This behaviour caused performance problems for some interceptors (e.g. `SearchNarrowingInterceptor`) and no longer offers any benefit so it is being removed.  
|  [#4125](https://github.com/hapifhir/hapi-fhir/issues/4125) |  The interceptor system has now deprecated the concept of ThreadLocal interceptors. This feature was added for an anticipated use case, but has never seen any real use that we are aware of and removing it should provide a minor performance improvement to the interceptor registry.  
#  0.4.7HAPI FHIR 6.1.3 (Unicorn)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.2.0-0)
##  0.4.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-4)
**Released:** 2022-10-06
**Codename:** (Unicorn)
##  0.4.7.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-4)
##  0.4.7.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-4)
|  [#4111](https://github.com/hapifhir/hapi-fhir/issues/4111) |  MDM messages were using the resource id as a message key when it should be using the EID as a partition hash key. This could lead to duplicate golden resources on systems using Kafka as a message broker.  
---|---|---  
#  0.4.8HAPI FHIR 6.1.2 (Unicorn)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.1.3-4111)
##  0.4.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-5)
**Released:** 2022-09-12
**Codename:** (Unicorn)
##  0.4.8.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-5)
##  0.4.8.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-5)
|  |  Added support for AWS OpenSearch to Fulltext Search. If an AWS Region is configured, HAPI-FHIR will assume you intend to connect to an AWS-managed OpenSearch instance, and will use Amazon's [DefaultAwsCredentialsProviderChain](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html) to authenticate against it. If both username and password are provided, HAPI-FHIR will attempt to use them as a static credentials provider.  
---|---|---  
|  [#4013](https://github.com/hapifhir/hapi-fhir/issues/4013) |  In HAPI-FHIR 6.1.0, a regression was introduced into bulk export causing exports beyond the first one to fail in strange ways. This has been corrected.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a bug in Group Bulk Export where the server would crash in oracle due to too many clauses.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a Group Bulk Export bug in which the group members would not be expanded correctly.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a bug in Group Bulk Export: If a group member was part of multiple groups , it was causing other groups to be included during Group Bulk Export, if the Group resource type was specified. Now, when doing an export on a specific group, and you elect to return Group resources, only the called Group will be returned, regardless of cross-membership.  
|  [#4022](https://github.com/hapifhir/hapi-fhir/issues/4022) |  Fixed a Group Bulk Export bug which was causing it to fail to return resources due to an incorrect search.  
#  0.4.9HAPI FHIR 6.1.1 (Unicorn)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.1.2-4010)
##  0.4.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-6)
**Released:** 2022-09-02
**Codename:** (Unicorn)
##  0.4.9.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-6)
##  0.4.9.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-6)
|  [#3980](https://github.com/hapifhir/hapi-fhir/issues/3980) |  A regression was introduced in 6.1.0 which caused bulk export jobs to not default to the correct output format when the `_outputFormat` parameter was omitted. This behaviour has been fixed, and if omitted, will now default to the only legal value `application/fhir+ndjson`.  
---|---|---  
|  [#3987](https://github.com/hapifhir/hapi-fhir/issues/3987) |  Previously, bulk export for Group type with _typeFilter did not apply the filter if it was for the patients, and returned all members of the group. This has now been fixed, and the filter will apply.  
#  0.4.10HAPI FHIR 6.1.0 (Unicorn)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.1.1-3980)
##  0.4.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-7)
**Released:** 2022-08-18
**Codename:** (Unicorn)
##  0.4.10.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-7)
When upgrading to this release, there are a few important notes to be aware of:
  * This release removes the Legacy Search Builder. If you upgrade to this release, the new Search Builder will automatically be used.
  * This release will break existing implementations which use the subscription delete feature. The place where the extension needs to be installed on the Subscription resource has now changed. While it used to be on the top-level Subscription resource, the extension should now be added to the Subscription's `channel` element.


Here is how the subscription should have looked before:
```
{
    "resourceType": "Subscription",
    "id": "1",
    "status": "active",
    "reason": "Monitor resource persistence events",
    "criteria": "Patient",
    "channel": {
        "type": "rest-hook",
        "payload": "application/json"
    },
   "extension": [
      {
         "url": "http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages",
         "valueBoolean": "true"
      }
   ]
}

```

Copy
And here is how it should now look:
```

{
    "resourceType": "Subscription",
    "id": "1",
    "status": "active",
    "reason": "Monitor resource persistence events",
    "criteria": "Patient",
    "channel": {
        "extension": [
            {
                "url": "http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages",
                "valueBoolean": "true"
            }
        ],
        "type": "rest-hook",
        "payload": "application/json"
    }
}

```

Copy
##  0.4.10.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-7)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * 9.4.44.v20210927 -> 9.4.48.v20220622 (Addresses CVE-2022-2047)

  
---|---|---  
|  [#2878](https://github.com/hapifhir/hapi-fhir/issues/2878) |  Refactored mdm classes to use ResourcePersistenceId rather than prematurely converting to longs  
|  [#3243](https://github.com/hapifhir/hapi-fhir/issues/3243) |  Previously, the `http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages` extension on REST-HOOK subscription channel element was only valid for R4. This has been expanded to support DSTU3 and DSTU2.  
|  [#3269](https://github.com/hapifhir/hapi-fhir/issues/3269) |  The SearchParameterMap query normalizer did not support `_include` and `_revinclude` parameters with the `:recurse` qualifier. This has been corrected. Thanks to Augustas Vilčinskas for the PR!  
|  [#3577](https://github.com/hapifhir/hapi-fhir/issues/3577) |  Experimental support for CockroachDB has been added to the JPA server. Thanks to Joe Shook for the contribution!  
|  [#3619](https://github.com/hapifhir/hapi-fhir/issues/3619) |  The `ValueSet/$expand` operation can now optionally support the displayLanugage parameter. Thanks to Gjergj Sheldija for the pull request!  
|  [#3620](https://github.com/hapifhir/hapi-fhir/issues/3620) |  Add support for the `:in` and `:not-in` qualifiers for use in SMART on FHIR v2 granular scope definition.  
|  [#3631](https://github.com/hapifhir/hapi-fhir/issues/3631) |  If a gated Batch job step and all prior steps produced only a single chunk of work, notify the next step immediately instead of waiting for the scheduled job maintenance to advance it.  
|  [#3640](https://github.com/hapifhir/hapi-fhir/issues/3640) |  Extended Lucene/Elasticsearch indexing of search parameters now supports _tag, _profile and _security.  
|  [#3658](https://github.com/hapifhir/hapi-fhir/issues/3658) |  Currently neither HAPI-FHIR nor the FHIR specification supports modifying historical version of a resource. This implementation adds the feature Update with History Rewrite to HAPI-FHIR. It can be accessed via the IGenericClient's `updateHistoryRewrite` method  
|  [#3662](https://github.com/hapifhir/hapi-fhir/issues/3662) |  Added support to batch2 jobs to allow for final reduction step for gated jobs.  
|  [#3674](https://github.com/hapifhir/hapi-fhir/issues/3674) |  The rule system has been extended to support optional fhir-expressions that narrow the application of a rule. A matching RuleFilteringConsentService implements post-query support for this filtering using an in-memory matcher.  
|  [#3702](https://github.com/hapifhir/hapi-fhir/issues/3702) |  Added index for table MPI_LINK on attributes MATCH_RESULT, TARGET_PID and VERSION  
|  [#3719](https://github.com/hapifhir/hapi-fhir/issues/3719) |  Added support to Bulk Export for FHIR Response Terminology Translation. It will use the same mappings as the Translation Interceptor.  
|  [#3724](https://github.com/hapifhir/hapi-fhir/issues/3724) |  Adding some test cases for CQL measures on immunization as well as some testing methods to support easier changes of CQL in unit tests.  
|  [#3728](https://github.com/hapifhir/hapi-fhir/issues/3728) |  If provided, the practitioner parameter for $evaluate-measure is now also be passed on to measures of type population (not only for type subject-list).  
|  [#3734](https://github.com/hapifhir/hapi-fhir/issues/3734) |  Added support for loading the International version of ICD-10. Thanks to kaicode for the contribution!  
|  [#3742](https://github.com/hapifhir/hapi-fhir/issues/3742) |  Added operations to batch2 api for searching job instances.  
|  [#3752](https://github.com/hapifhir/hapi-fhir/issues/3752) |  Batch2 job definitions can now optionally provide an error handler callback that will be called when a job instance fails, errors or is cancelled.  
|  [#3762](https://github.com/hapifhir/hapi-fhir/issues/3762) |  JPA server patch operation has been moved to the hapi-fhir-storage project, for reuse in other backends.  
|  [#3776](https://github.com/hapifhir/hapi-fhir/issues/3776) |  Previously, the HAPI FHIR CLI commands that made HTTP requests could only be used for HTTP endpoints. This feature adds support for HTTPS endpoints by using TLS authentication for `ExampleDataUploader`, `ExportConceptMapToCsvCommand`, `ImportCsvToConceptMapCommand`, `ReindexTerminologyCommand` and `UploadTerminologyCommand`.  
|  [#3784](https://github.com/hapifhir/hapi-fhir/issues/3784) |  The InMemoryMatcher used by Subscription matching not supports the token `:not` modifier. The `:not-in` modifier was also corrected for cases of multiple values in a `,` separated or-list.  
|  [#3792](https://github.com/hapifhir/hapi-fhir/issues/3792) |  Adding pointcut `MDM_BEFORE_PERSISTED_RESOURCE_CHECKED` allowing for the customization of source resource before MDM processing.  
|  [#3813](https://github.com/hapifhir/hapi-fhir/issues/3813) |  Updating the IJobCoordinator API to have a pageable request.  
|  [#3817](https://github.com/hapifhir/hapi-fhir/issues/3817) |  The 'SUBSCRIPTION_BEFORE_MESSAGE_DELIVERY' pointcut now supports a new parameter, `ResourceModifiedJsonMessage`. This permits interceptor implementers to modify the outgoing envelope before it is sent off.  
|  [#3820](https://github.com/hapifhir/hapi-fhir/issues/3820) |  Providing a specialized exception encapsulating the mechanism for reporting TokenParam validation failure when searching resources on parameter _tag and _security. The new exception was introduced primarily for reusability purposes.  
|  [#3840](https://github.com/hapifhir/hapi-fhir/issues/3840) |  Add Sort parameter to IJobCoordinator pageable request for fetching all jobs.  
|  [#3841](https://github.com/hapifhir/hapi-fhir/issues/3841) |  Added support for the `_type` parameter has been added when using the `$everything` operation.  
|  [#3714](https://github.com/hapifhir/hapi-fhir/issues/3714) |  Chained searches have been sped up for configurations where the `Index Contained Resources` feature is enabled.  
|  [#3671](https://github.com/hapifhir/hapi-fhir/issues/3671) |  Converted mdm clear batch job from Spring Batch to Batch2  
|  [#3755](https://github.com/hapifhir/hapi-fhir/issues/3755) |  When the Mark Resources for Reindexing after SearchParameter change configuration parameter is enabled, SMILE will use the Batch 2 framework to perform the reindexing operation.  
|  [#3756](https://github.com/hapifhir/hapi-fhir/issues/3756) |  The IAuthRuleTester api has changed and now uses a parameter object for flexibility.  
|  [#3757](https://github.com/hapifhir/hapi-fhir/issues/3757) |  The Delete Expunge operation has been moved from Spring Batch to Batch 2.  
|  [#3790](https://github.com/hapifhir/hapi-fhir/issues/3790) |  The Legacy Search Builder has been removed in favour of the new Search Builder. The DaoConfig will retain its setter for deprecation purposes, but that will also be removed after the next release.  
|  [#3379](https://github.com/hapifhir/hapi-fhir/issues/3379) |  Fixed an issue where FindCandidateByLinkSvc still uses long to store persistent ids rather than IResourcePersistentId, also fixed an issue where MdmLinkCreateSvc was not setting the resource type of MdmLinks properly  
|  [#3459](https://github.com/hapifhir/hapi-fhir/issues/3459) |  Previously, R5 Appointment resources would fail to save in JPA servers due to a path splitting problem during Search Parameter extraction. This has been corrected.  
|  [#3574](https://github.com/hapifhir/hapi-fhir/issues/3574) |  Previously, PATCH operations that were contained in a transaction bundle would not return response entries. This has been corrected.  
|  [#3612](https://github.com/hapifhir/hapi-fhir/issues/3612) |  Search with tag parameters using `:below` qualifier was not working. This is now fixed.  
|  [#3627](https://github.com/hapifhir/hapi-fhir/issues/3627) |  Previously, the `$expunge` operation at the system level was always available, regardless of configuration parameter settings. Now, the system-level `$expunge` operation requires that the `Expunge Enabled` parameter is enabled. Additionally, the `expungeEverything` option of the operation requires that the `Allow Multiple Delete Enabled` parameter is enabled.  
|  [#3628](https://github.com/hapifhir/hapi-fhir/issues/3628) |  Nickname matching only matched formal names to a nickname. This has been broadened to match nicknames to other nicknames for the same formal name.  
|  [#3642](https://github.com/hapifhir/hapi-fhir/issues/3642) |  Previously, the RuleBuilder's rules surrounding Group Bulk Export would return failures too early in the case of multiple permissions. This has been corrected, and the rule will no longer prematurely return a DENY verdict, instead opting to delegate to future rules.  
|  [#3650](https://github.com/hapifhir/hapi-fhir/issues/3650) |  Removed a strict dependency on a FullTextSVC. This was causing HAPI to fail to boot when Lucene/ElasticSearch was not enabled.  
|  [#3651](https://github.com/hapifhir/hapi-fhir/issues/3651) |  MS SQL Standard Edition does not support the ONLINE=ON feature for index creation, and failed during upgrades to 6.0. Upgrades now detect these limitations and avoid this feature when unsupported.  
|  [#3668](https://github.com/hapifhir/hapi-fhir/issues/3668) |  Fhir patch operation returning 500 when patching in a transaction with a resource query in property request.url.  
|  [#2669](https://github.com/hapifhir/hapi-fhir/issues/2669) |  Previously subscriptions in a partition with the id null will be matched against incoming resources from all partitions. Changed to subscriptions will only match against incoming resources in the partition the subscription exists in unless cross partition subscription is enabled and the subscription has the appropriate extension.  
|  [#3673](https://github.com/hapifhir/hapi-fhir/issues/3673) |  A change in H2's new version caused resources >1mb in size to fail to correctly store. This has been corrected. Thanks to Patrick Werner for the report and pull request!  
|  [#3684](https://github.com/hapifhir/hapi-fhir/issues/3684) |  Moved the `http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages` from the Subscription to the Subscription's Channel element.  
|  [#3689](https://github.com/hapifhir/hapi-fhir/issues/3689) |  hapi now populates issue.detail with the messageID from the core validator, org.hl7.fhir.core was updated to the latest version (5.6.48) Thanks to Patrick Werner for the feature request and pull request!  
|  [#3693](https://github.com/hapifhir/hapi-fhir/issues/3693) |  Previously, deleted resources with client generated ids were being included in the bundle total when searching by _id. This has been corrected by adding functionality to optionally filter out deleted resources when resolving forced ids to persistent ids.  
|  [#3700](https://github.com/hapifhir/hapi-fhir/issues/3700) |  Previously, Group Bulk Export would expand into other groups, due to the nature of `Group.member` being part of the patient search compartment. This has been fixed, and now, Group Bulk Export will only ever export the Group resource specified in the request, regardless of patient membership.  
|  [#3703](https://github.com/hapifhir/hapi-fhir/issues/3703) |  Previously, using the `http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages` extension on a subscription, along with a subscription that used a Search Expression criteria (e.g. `Patient?gender=male`) would cause a failure during an attempt to send the delete. This has been corrected.  
|  [#3706](https://github.com/hapifhir/hapi-fhir/issues/3706) |  Codes with the Seventh Character will be dynamically generated while uploading the ICD10-CM diagnosis code file, and the corresponding extended description can be extract using the code.  
|  [#3710](https://github.com/hapifhir/hapi-fhir/issues/3710) |  Changing the `Unknown resource type` error message to include only resource provider classes and exclude plain providers classes.  
|  [#3716](https://github.com/hapifhir/hapi-fhir/issues/3716) |  Server was blocking updates with Forbidden 403 that included a resource version in the url without the X-Rewrite-History header. This has been corrected.  
|  [#3722](https://github.com/hapifhir/hapi-fhir/issues/3722) |  Fixed the $expunge operation that expunge everything response will return the correct number of dropped resources.  
|  [#3729](https://github.com/hapifhir/hapi-fhir/issues/3729) |  The patient-list type was renamed to subject-list for a CQL measure in R4.  
|  [#3736](https://github.com/hapifhir/hapi-fhir/issues/3736) |  Previously, the DSTU3 Conformance provider was not including `searchInclude` or `searchRevInclude` results in the conformance response. This has been corrected.  
|  [#3745](https://github.com/hapifhir/hapi-fhir/issues/3745) |  Fixed a bug where NPE occurs while updating references for posting a bundle with duplicate resource in conditional create and another conditional verb (delete).  
|  [#3749](https://github.com/hapifhir/hapi-fhir/issues/3749) |  Fixed a regression in 6.0.0 which caused the SearchNarrowingInterceptor to occcasionally be applied to non-search operations.  
|  [#3752](https://github.com/hapifhir/hapi-fhir/issues/3752) |  It was possible under certain race conditions for the batch2 completion handler to be called twice. This has been corrected.  
|  [#3770](https://github.com/hapifhir/hapi-fhir/issues/3770) |  Batch2 will have a standard error handling that will fail the job if it fails a chunk processing for more than 3 times. Further, added better validation to reindex job to disallow bad urls.  
|  [#3773](https://github.com/hapifhir/hapi-fhir/issues/3773) |  A batch2 state change regression was introduced recently that resulted in batch2 jobs not being properly completed. This has been corrected.  
|  [#3787](https://github.com/hapifhir/hapi-fhir/issues/3787) |  FHIR queries using date sort were ignoring seconds and milliseconds when using lucene/elasticsearch SearchParameter indexing. This has been corrected.  
|  [#3788](https://github.com/hapifhir/hapi-fhir/issues/3788) |  Previously, if a FHIR patch was performed on a soft deleted resource, the patch was successfully applied and the resource was undeleted and populated with only the data contained in the patch. This has been fixed so that patch on deleted resources now issues a HTTP 410 response.  
|  [#3796](https://github.com/hapifhir/hapi-fhir/issues/3796) |  Previously, the FHIRPath PATCH operation type `add` was replacing the entire element as opposed to adding the new element to the existing element. This has been corrected.  
|  [#3801](https://github.com/hapifhir/hapi-fhir/issues/3801) |  For Lucene/Elastic previously we were using scrolled results even when performing synchronous searches. That has now been fixed.  
|  [#3804](https://github.com/hapifhir/hapi-fhir/issues/3804) |  When starting hapi-fhir from starter application, BeanDefinitionOverrideException was thrown with message: Invalid bean definition with name loadIdsStep. This issue is now fixed.  
|  [#3810](https://github.com/hapifhir/hapi-fhir/issues/3810) |  Elastic/Lucene documentation was updated to indicate that full reindex is required when enabling storing resource bodies in indexes when indexed resources exist.  
|  [#3821](https://github.com/hapifhir/hapi-fhir/issues/3821) |  Migrate existing Term Code System Delete and Term Code System Delete Version jobs to batch 2 framework  
|  [#3829](https://github.com/hapifhir/hapi-fhir/issues/3829) |  Previously when a `Subscription` was created using the package installer and with partitioning enabled, there was an error reading the partition name inside the `SubscriptionRegisteringSubscriber`. This fix checks incoming `Subscription` requests for a `RequestPartitionId` with a list of partition names containing null values and uses `RequestPartitionId#defaultPartition()` to obtain the default partition instead.  
|  [#3837](https://github.com/hapifhir/hapi-fhir/issues/3837) |  Previously, when doing a GET operation with an _include as a bundle entry could occasionally miss returning some results. This has been corrected, and queries like '/Medication?_include=Medication:ingredient' will now correctly include the relevant target resources.  
|  [#3846](https://github.com/hapifhir/hapi-fhir/issues/3846) |  Migrated Bulk Import Pull to Batch2 Framework  
|  [#3854](https://github.com/hapifhir/hapi-fhir/issues/3854) |  Previously, command prompt was not returned after initiating bulk import operation and even when the importing was completed. This has been fixed, and the command prompt will return after the uploading process finished.  
|  [#3857](https://github.com/hapifhir/hapi-fhir/issues/3857) |  Previously a lucene/elastic enabled search including offset=0 and count > 50 was returning only 50 resources this has now been fixed.  
|  [#3863](https://github.com/hapifhir/hapi-fhir/issues/3863) |  Previously a lucene/elastic enabled sorted search including offset greater than zero was not sorting results. This has now been fixed.  
|  [#3875](https://github.com/hapifhir/hapi-fhir/issues/3875) |  Previously, when modifying one of the common search parameters, if the `Mark Resources For Reindexing Upon Search Parameter Change` configuration parameter was enabled, an invalid reindex batch job request would be created. This has been fixed, and the batch job request will contain no URL constraints, causing all resources to be reindexed.  
|  [#3878](https://github.com/hapifhir/hapi-fhir/issues/3878) |  Previously, if an intermediate step of a gated batch job produced no output data, an exception would be thrown and processing of the step would abort, leaving the batch job in an unrecoverable state. This has been fixed.  
|  [#3881](https://github.com/hapifhir/hapi-fhir/issues/3881) |  Previously, if an error was thrown after the outgoing response stream had started being written to, an OperationOutcome was not being returned. Instead, an HTML servlet error was being thrown. This has been corrected.  
|  [#3884](https://github.com/hapifhir/hapi-fhir/issues/3884) |  Prevent creating overlapping SearchParameter resources which would lead to random search behaviors.  
|  [#3887](https://github.com/hapifhir/hapi-fhir/issues/3887) |  Removed a bug with subscriptions that supported sending deletes. Previously, if there was a previously-registered subscription which did not support deletes, that would shortcircuit processing and cause subsequent subscriptions not to fire. This has been corrected.  
|  [#3899](https://github.com/hapifhir/hapi-fhir/issues/3899) |  Searches using `code:in` and `code:not-in` will now expand the valueset up to the Maximum Expansion Size defined in the DaoConfig.  
|  [#3902](https://github.com/hapifhir/hapi-fhir/issues/3902) |  Fixed bug where creating a new resource in partitioned mode using a PUT operation invoked pointcut STORAGE_PARTITION_IDENTIFY_READ during validation. This caused errors because read interceptors (listing on Pointcut.STORAGE_PARTITION_IDENTIFY_READ) and write interceptors (listening on Pointcut.STORAGE_PARTITION_IDENTIFY_CREATE) could return different partitions (say, all vs default). Now, only the CREATE pointcut will be invoked, and the same partition will be used for any reads during UPDATE.  
|  [#3887](https://github.com/hapifhir/hapi-fhir/issues/3887) |  Previously, if Fulltext Search was enabled, and a `$delete-expunge` job was run, it could leave orphaned documents in the Fulltext index. This has been corrected.  
|  [#3911](https://github.com/hapifhir/hapi-fhir/issues/3911) |  Fixed a bug where repeated calls to the same Bulk Export request would not return the cached result, but would instead start a new Bulk Export job.  
|  [#3915](https://github.com/hapifhir/hapi-fhir/issues/3915) |  When multiple permissions exist for a user, granting access to the same compartment for different owners, the permissions will be collapsed into one rule. Previously, if these permissions had filters, only the the filter of the first permission in the list would be applied, but it would apply to all of the owners. This has been fixed by turning off the collapse function for permissions with filters, converting each one to a separate rule.  
|  [#3918](https://github.com/hapifhir/hapi-fhir/issues/3918) |  When offset searches are enforced on the server, not all of the search parameters were loading and this caused a NPE. Now an ERROR will be logged instead.  
|  [#3922](https://github.com/hapifhir/hapi-fhir/issues/3922) |  Fixed an issue with $delete-expunge that resulted in SQL errors on large amounts of deleted data  
#  0.4.11HAPI FHIR 6.0.5 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.1.0-0)
##  0.4.11.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-8)
**Released:** 2022-08-29
**Codename:** (Tanuki)
##  0.4.11.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-8)
##  0.4.11.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-8)
|  [#3937](https://github.com/hapifhir/hapi-fhir/issues/3937) |  Fixing bug where searching with a target resource parameter (Coverage:payor:Patient) as value to an _include parameter would fail with a 500 response.  
---|---|---  
#  0.4.12HAPI FHIR 6.0.4 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.0.5-3937)
##  0.4.12.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-9)
**Released:** 2022-07-22
**Codename:** (Tanuki)
##  0.4.12.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-9)
##  0.4.12.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-9)
|  [#3243](https://github.com/hapifhir/hapi-fhir/issues/3243) |  Previously, the `http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages` extension on REST-HOOK subscription channel element was only valid for R4. This has been expanded to support DSTU3 and DSTU2.  
---|---|---  
#  0.4.13HAPI FHIR 6.0.3 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.0.4-3243)
##  0.4.13.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-10)
**Released:** 2022-07-18
**Codename:** (Tanuki)
##  0.4.13.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-10)
##  0.4.13.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-10)
|  [#3703](https://github.com/hapifhir/hapi-fhir/issues/3703) |  Previously, using the `http://hapifhir.io/fhir/StructureDefinition/subscription-send-delete-messages` extension on a subscription, along with a subscription that used a Search Expression criteria (e.g. `Patient?gender=male`) would cause a failure during an attempt to send the delete. This has been corrected.  
---|---|---  
#  0.4.14HAPI FHIR 6.0.2 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.0.3-3703)
##  0.4.14.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-11)
**Released:** 2022-06-14
**Codename:** (Tanuki)
##  0.4.14.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-11)
##  0.4.14.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-11)
#  0.4.15HAPI FHIR 6.0.1 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#hapi-fhir-601-tanuki)
##  0.4.15.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-12)
**Released:** 2022-05-25
**Codename:** (Tanuki)
##  0.4.15.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-12)
##  0.4.15.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-12)
|  [#3642](https://github.com/hapifhir/hapi-fhir/issues/3642) |  Previously, the RuleBuilder's rules surrounding Group Bulk Export would return failures too early in the case of multiple permissions. This has been corrected, and the rule will no longer prematurely return a DENY verdict, instead opting to delegate to future rules.  
---|---|---  
|  [#3650](https://github.com/hapifhir/hapi-fhir/issues/3650) |  Removed a strict dependency on a FullTextSVC. This was causing HAPI to fail to boot when Lucene/ElasticSearch was not enabled.  
#  0.4.16HAPI FHIR 6.0.0 (Tanuki)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.0.1-3642)
##  0.4.16.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-13)
**Released:** 2022-05-18
**Codename:** (Tanuki)
##  0.4.16.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#upgrade-instructions-13)
This release has breaking changes, and some large database changes.
  * Hibernate Search mappings for Terminology entities have been upgraded, which requires a reindex of ValueSet resources.
  * Database SearchParameter indexes have been redefined. This stage of the upgrade may be slow, and can be scheduled before the main upgrade window.


## Hibernate Search Mappings Upgrade
Some freetext mappings (definitions about how resources are freetext indexed) were changed for Terminology resources. This change requires a full reindexing for any Smile CDR installations which make use of the following features:
  * Fulltext/Content search
  * Terminology Expansion


To reindex all resources call:
```
POST /$reindex Content-Type: application/fhir+json
{
  "resourceType": "Parameters",
  "parameter": [ {
    "name": "everything",
    "valueBoolean": "true"
  }, {
    "name": "batchSize",
    "valueDecimal": 1000
  } ]
}

```

Copy
## Database Indexing
The JPA SearchParameter database indexing has been redesigned in 6.0. As a result, performing the upgrade may take longer than usual. Database migrations happen automatically after server upgrade during the next restart, and the server is unavailable during this migration window. To avoid this prolonged outage during server restart, you can apply these index changes manually before upgrading the server. The server is compatible with both the old and new indexes. The syntax for index definition varies, and we include separate scripts for MS Sql, Postgres, and Oracle.
### Upgrade indexing to 6.0 manually on MS Sql Server
This script updates the indexing, and assumes Enterprise Edition. If you are running Standard Edition, edit the script and remove the `WITH (ONLINE = ON)` clauses.
**Note:** Without this feature, the database tables will be locked during the creation of each index, and you will be unable to save, update, or delete any resource until the statement completes.
```
-- Table: HFJ_SPIDX_DATE create index IDX_SP_DATE_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_HASH;
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_HASH_LOW;
create index IDX_SP_DATE_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_HASH_HIGH;
create index IDX_SP_DATE_ORD_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_ORD_HASH;
create index IDX_SP_DATE_ORD_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_ORD_HASH_LOW;
create index IDX_SP_DATE_RESID_V2 on HFJ_SPIDX_DATE(RES_ID, HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, PARTITION_ID) WITH (ONLINE = ON);
alter table HFJ_SPIDX_DATE drop constraint FK17S70OA59RM9N61K9THJQRSQM;
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_RESID;
ALTER TABLE HFJ_SPIDX_DATE DROP CONSTRAINT FK17s70oa59rm9n61k9thjqrsqm;
alter table HFJ_SPIDX_DATE add constraint FK_SP_DATE_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index HFJ_SPIDX_DATE.IDX_SP_DATE_UPDATED;
-- Table: HFJ_SPIDX_TOKEN create index IDX_SP_TOKEN_HASH_V2 on HFJ_SPIDX_TOKEN(HASH_IDENTITY, SP_SYSTEM, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_HASH;
create index IDX_SP_TOKEN_HASH_S_V2 on HFJ_SPIDX_TOKEN(HASH_SYS, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_HASH_S;
create index IDX_SP_TOKEN_HASH_SV_V2 on HFJ_SPIDX_TOKEN(HASH_SYS_AND_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_HASH_SV;
create index IDX_SP_TOKEN_HASH_V_V2 on HFJ_SPIDX_TOKEN(HASH_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_HASH_V;
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_UPDATED;
create index IDX_SP_TOKEN_RESID_V2 on HFJ_SPIDX_TOKEN(RES_ID, HASH_SYS_AND_VALUE, HASH_VALUE, HASH_SYS, HASH_IDENTITY, PARTITION_ID) WITH (ONLINE = ON);
alter table HFJ_SPIDX_TOKEN drop constraint FK7ULX3J1GG3V7MAQREJGC7YBC4;
drop index HFJ_SPIDX_TOKEN.IDX_SP_TOKEN_RESID;
ALTER TABLE HFJ_SPIDX_TOKEN DROP CONSTRAINT FK7ulx3j1gg3v7maqrejgc7ybc4;
alter table HFJ_SPIDX_TOKEN add constraint FK_SP_TOKEN_RES foreign key (RES_ID) references HFJ_RESOURCE;
-- Table: HFJ_SPIDX_NUMBER create index IDX_SP_NUMBER_HASH_VAL_V2 on HFJ_SPIDX_NUMBER(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_NUMBER.IDX_SP_NUMBER_HASH_VAL;
create index IDX_SP_NUMBER_RESID_V2 on HFJ_SPIDX_NUMBER(RES_ID, HASH_IDENTITY, SP_VALUE, PARTITION_ID) WITH (ONLINE = ON);
alter table HFJ_SPIDX_NUMBER drop constraint FKCLTIHNC5TGPRJ9BHPT7XI5OTB;
drop index HFJ_SPIDX_NUMBER.IDX_SP_NUMBER_RESID;
ALTER TABLE HFJ_SPIDX_NUMBER DROP CONSTRAINT FKcltihnc5tgprj9bhpt7xi5otb;
alter table HFJ_SPIDX_NUMBER add constraint FK_SP_NUMBER_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index HFJ_SPIDX_NUMBER.IDX_SP_NUMBER_UPDATED;
-- Table: HFJ_SPIDX_QUANTITY create index IDX_SP_QUANTITY_HASH_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY.IDX_SP_QUANTITY_HASH;
create index IDX_SP_QUANTITY_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY.IDX_SP_QUANTITY_HASH_SYSUN;
create index IDX_SP_QUANTITY_HASH_UN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY.IDX_SP_QUANTITY_HASH_UN;
create index IDX_SP_QUANTITY_RESID_V2 on HFJ_SPIDX_QUANTITY(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID) WITH (ONLINE = ON);
alter table HFJ_SPIDX_QUANTITY drop constraint FKN603WJJOI1A6ASEWXBBD78BI5;
drop index HFJ_SPIDX_QUANTITY.IDX_SP_QUANTITY_RESID;
ALTER TABLE HFJ_SPIDX_QUANTITY DROP CONSTRAINT FKn603wjjoi1a6asewxbbd78bi5;
alter table HFJ_SPIDX_QUANTITY add constraint FK_SP_QUANTITY_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index HFJ_SPIDX_QUANTITY.IDX_SP_QUANTITY_UPDATED;
-- Table: HFJ_SPIDX_QUANTITY_NRML create index IDX_SP_QNTY_NRML_HASH_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY_NRML.IDX_SP_QNTY_NRML_HASH;
create index IDX_SP_QNTY_NRML_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY_NRML.IDX_SP_QNTY_NRML_HASH_SYSUN;
create index IDX_SP_QNTY_NRML_HASH_UN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_QUANTITY_NRML.IDX_SP_QNTY_NRML_HASH_UN;
create index IDX_SP_QNTY_NRML_RESID_V2 on HFJ_SPIDX_QUANTITY_NRML(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID) WITH (ONLINE = ON);
alter table HFJ_SPIDX_QUANTITY_NRML drop constraint FKRCJOVMUH5KC0O6FVBLE319PYV;
drop index HFJ_SPIDX_QUANTITY_NRML.IDX_SP_QNTY_NRML_RESID;
ALTER TABLE HFJ_SPIDX_QUANTITY_NRML DROP CONSTRAINT FKrcjovmuh5kc0o6fvble319pyv;
alter table HFJ_SPIDX_QUANTITY_NRML add constraint FK_SP_QUANTITYNM_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index HFJ_SPIDX_QUANTITY_NRML.IDX_SP_QNTY_NRML_UPDATED;
-- Table: HFJ_RESOURCE create index IDX_RES_TYPE_DEL_UPDATED on HFJ_RESOURCE(RES_TYPE, RES_DELETED_AT, RES_UPDATED, PARTITION_ID, RES_ID) WITH (ONLINE = ON);
drop index HFJ_RESOURCE.IDX_INDEXSTATUS;
drop index HFJ_RESOURCE.IDX_RES_TYPE;
-- Table: HFJ_SPIDX_STRING create index IDX_SP_STRING_HASH_NRM_V2 on HFJ_SPIDX_STRING(HASH_NORM_PREFIX, SP_VALUE_NORMALIZED, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_STRING.IDX_SP_STRING_HASH_NRM;
create index IDX_SP_STRING_HASH_EXCT_V2 on HFJ_SPIDX_STRING(HASH_EXACT, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
drop index HFJ_SPIDX_STRING.IDX_SP_STRING_HASH_EXCT;
drop index HFJ_SPIDX_STRING.IDX_SP_STRING_UPDATED;
-- Table: HFJ_RES_TAG create index IDX_RES_TAG_RES_TAG on HFJ_RES_TAG(RES_ID, TAG_ID, PARTITION_ID) WITH (ONLINE = ON);
create index IDX_RES_TAG_TAG_RES on HFJ_RES_TAG(TAG_ID, RES_ID, PARTITION_ID) WITH (ONLINE = ON);
ALTER TABLE HFJ_RES_TAG DROP CONSTRAINT IDX_RESTAG_TAGID;
drop index IDX_RESTAG_TAGID on HFJ_RES_TAG;
ALTER TABLE HFJ_RES_TAG ADD CONSTRAINT IDX_RESTAG_TAGID UNIQUE (RES_ID, TAG_ID);
-- Table: HFJ_TAG_DEF create index IDX_TAG_DEF_TP_CD_SYS on HFJ_TAG_DEF(TAG_TYPE, TAG_CODE, TAG_SYSTEM, TAG_ID);
ALTER TABLE HFJ_TAG_DEF DROP CONSTRAINT IDX_TAGDEF_TYPESYSCODE;
drop index IDX_TAGDEF_TYPESYSCODE on HFJ_TAG_DEF;
ALTER TABLE HFJ_TAG_DEF ADD CONSTRAINT IDX_TAGDEF_TYPESYSCODE UNIQUE (TAG_TYPE, TAG_CODE, TAG_SYSTEM);

```

Copy
### Upgrade indexing to 6.0 manually on Postgres
```
-- Table: HFJ_SPIDX_DATE create index CONCURRENTLY IDX_SP_DATE_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_DATE_HASH;
drop index CONCURRENTLY IDX_SP_DATE_HASH_LOW;
create index CONCURRENTLY IDX_SP_DATE_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_DATE_HASH_HIGH;
create index CONCURRENTLY IDX_SP_DATE_ORD_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_DATE_ORD_HASH;
create index CONCURRENTLY IDX_SP_DATE_ORD_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_DATE_ORD_HASH_LOW;
create index CONCURRENTLY IDX_SP_DATE_RESID_V2 on HFJ_SPIDX_DATE(RES_ID, HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, PARTITION_ID);
alter table HFJ_SPIDX_DATE drop constraint FK17S70OA59RM9N61K9THJQRSQM;
drop index CONCURRENTLY IDX_SP_DATE_RESID;
alter table HFJ_SPIDX_DATE add constraint FK_SP_DATE_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index CONCURRENTLY IDX_SP_DATE_UPDATED;
-- Table: HFJ_SPIDX_TOKEN create index CONCURRENTLY IDX_SP_TOKEN_HASH_V2 on HFJ_SPIDX_TOKEN(HASH_IDENTITY, SP_SYSTEM, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_TOKEN_HASH;
create index CONCURRENTLY IDX_SP_TOKEN_HASH_S_V2 on HFJ_SPIDX_TOKEN(HASH_SYS, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_TOKEN_HASH_S;
create index CONCURRENTLY IDX_SP_TOKEN_HASH_SV_V2 on HFJ_SPIDX_TOKEN(HASH_SYS_AND_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_TOKEN_HASH_SV;
create index CONCURRENTLY IDX_SP_TOKEN_HASH_V_V2 on HFJ_SPIDX_TOKEN(HASH_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_TOKEN_HASH_V;
drop index CONCURRENTLY IDX_SP_TOKEN_UPDATED;
create index CONCURRENTLY IDX_SP_TOKEN_RESID_V2 on HFJ_SPIDX_TOKEN(RES_ID, HASH_SYS_AND_VALUE, HASH_VALUE, HASH_SYS, HASH_IDENTITY, PARTITION_ID);
alter table HFJ_SPIDX_TOKEN drop constraint FK7ULX3J1GG3V7MAQREJGC7YBC4;
drop index CONCURRENTLY IDX_SP_TOKEN_RESID;
alter table HFJ_SPIDX_TOKEN add constraint FK_SP_TOKEN_RES foreign key (RES_ID) references HFJ_RESOURCE;
-- Table: HFJ_SPIDX_NUMBER create index CONCURRENTLY IDX_SP_NUMBER_HASH_VAL_V2 on HFJ_SPIDX_NUMBER(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_NUMBER_HASH_VAL;
create index CONCURRENTLY IDX_SP_NUMBER_RESID_V2 on HFJ_SPIDX_NUMBER(RES_ID, HASH_IDENTITY, SP_VALUE, PARTITION_ID);
alter table HFJ_SPIDX_NUMBER drop constraint FKCLTIHNC5TGPRJ9BHPT7XI5OTB;
drop index CONCURRENTLY IDX_SP_NUMBER_RESID;
alter table HFJ_SPIDX_NUMBER add constraint FK_SP_NUMBER_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index CONCURRENTLY IDX_SP_NUMBER_UPDATED;
-- Table: HFJ_SPIDX_QUANTITY create index CONCURRENTLY IDX_SP_QUANTITY_HASH_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QUANTITY_HASH;
create index CONCURRENTLY IDX_SP_QUANTITY_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QUANTITY_HASH_SYSUN;
create index CONCURRENTLY IDX_SP_QUANTITY_HASH_UN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QUANTITY_HASH_UN;
create index CONCURRENTLY IDX_SP_QUANTITY_RESID_V2 on HFJ_SPIDX_QUANTITY(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID);
alter table HFJ_SPIDX_QUANTITY drop constraint FKN603WJJOI1A6ASEWXBBD78BI5;
drop index CONCURRENTLY IDX_SP_QUANTITY_RESID;
alter table HFJ_SPIDX_QUANTITY add constraint FK_SP_QUANTITY_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index CONCURRENTLY IDX_SP_QUANTITY_UPDATED;
-- Table: HFJ_SPIDX_QUANTITY_NRML create index CONCURRENTLY IDX_SP_QNTY_NRML_HASH_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QNTY_NRML_HASH;
create index CONCURRENTLY IDX_SP_QNTY_NRML_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QNTY_NRML_HASH_SYSUN;
create index CONCURRENTLY IDX_SP_QNTY_NRML_HASH_UN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_QNTY_NRML_HASH_UN;
create index CONCURRENTLY IDX_SP_QNTY_NRML_RESID_V2 on HFJ_SPIDX_QUANTITY_NRML(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID);
alter table HFJ_SPIDX_QUANTITY_NRML drop constraint FKRCJOVMUH5KC0O6FVBLE319PYV;
drop index CONCURRENTLY IDX_SP_QNTY_NRML_RESID;
alter table HFJ_SPIDX_QUANTITY_NRML add constraint FK_SP_QUANTITYNM_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index CONCURRENTLY IDX_SP_QNTY_NRML_UPDATED;
-- Table: HFJ_RESOURCE drop index IDX_INDEXSTATUS;
create index CONCURRENTLY IDX_RES_TYPE_DEL_UPDATED on HFJ_RESOURCE(RES_TYPE, RES_DELETED_AT, RES_UPDATED, PARTITION_ID, RES_ID);
drop index CONCURRENTLY IDX_RES_TYPE;
-- Table: HFJ_SPIDX_STRING create index CONCURRENTLY IDX_SP_STRING_HASH_NRM_V2 on HFJ_SPIDX_STRING(HASH_NORM_PREFIX, SP_VALUE_NORMALIZED, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_STRING_HASH_NRM;
create index CONCURRENTLY IDX_SP_STRING_HASH_EXCT_V2 on HFJ_SPIDX_STRING(HASH_EXACT, RES_ID, PARTITION_ID);
drop index CONCURRENTLY IDX_SP_STRING_HASH_EXCT;
drop index CONCURRENTLY IDX_SP_STRING_UPDATED;
-- Table: HFJ_RES_TAG create index CONCURRENTLY IDX_RES_TAG_RES_TAG on HFJ_RES_TAG(RES_ID, TAG_ID, PARTITION_ID);
create index CONCURRENTLY IDX_RES_TAG_TAG_RES on HFJ_RES_TAG(TAG_ID, RES_ID, PARTITION_ID);
alter table HFJ_RES_TAG drop constraint if exists IDX_RESTAG_TAGID cascade;
drop index if exists IDX_RESTAG_TAGID cascade;
ALTER TABLE HFJ_RES_TAG ADD CONSTRAINT IDX_RESTAG_TAGID UNIQUE (RES_ID, TAG_ID);
-- Table: HFJ_TAG_DEF create index IDX_TAG_DEF_TP_CD_SYS on HFJ_TAG_DEF(TAG_TYPE, TAG_CODE, TAG_SYSTEM, TAG_ID);
alter table HFJ_TAG_DEF drop constraint if exists IDX_TAGDEF_TYPESYSCODE cascade;
drop index if exists IDX_TAGDEF_TYPESYSCODE cascade;
ALTER TABLE HFJ_TAG_DEF ADD CONSTRAINT IDX_TAGDEF_TYPESYSCODE UNIQUE (TAG_TYPE, TAG_CODE, TAG_SYSTEM);

```

Copy
### Upgrade indexing to 6.0 manually on Oracle
```

-- Table: HFJ_SPIDX_DATE create index IDX_SP_DATE_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_DATE_HASH ONLINE;
drop index IDX_SP_DATE_HASH_LOW ONLINE;
create index IDX_SP_DATE_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_DATE_HASH_HIGH ONLINE;
create index IDX_SP_DATE_ORD_HASH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_DATE_ORD_HASH ONLINE;
create index IDX_SP_DATE_ORD_HASH_HIGH_V2 on HFJ_SPIDX_DATE(HASH_IDENTITY, SP_VALUE_HIGH_DATE_ORDINAL, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_DATE_ORD_HASH_LOW ONLINE;
create index IDX_SP_DATE_RESID_V2 on HFJ_SPIDX_DATE(RES_ID, HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
alter table HFJ_SPIDX_DATE drop constraint FK17S70OA59RM9N61K9THJQRSQM;
drop index IDX_SP_DATE_RESID ONLINE;
alter table HFJ_SPIDX_DATE add constraint FK_SP_DATE_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index IDX_SP_DATE_UPDATED ONLINE;
-- Table: HFJ_SPIDX_TOKEN create index IDX_SP_TOKEN_HASH_V2 on HFJ_SPIDX_TOKEN(HASH_IDENTITY, SP_SYSTEM, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_TOKEN_HASH ONLINE;
create index IDX_SP_TOKEN_HASH_S_V2 on HFJ_SPIDX_TOKEN(HASH_SYS, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_TOKEN_HASH_S ONLINE;
create index IDX_SP_TOKEN_HASH_SV_V2 on HFJ_SPIDX_TOKEN(HASH_SYS_AND_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_TOKEN_HASH_SV ONLINE;
create index IDX_SP_TOKEN_HASH_V_V2 on HFJ_SPIDX_TOKEN(HASH_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_TOKEN_HASH_V ONLINE;
drop index IDX_SP_TOKEN_UPDATED ONLINE;
create index IDX_SP_TOKEN_RESID_V2 on HFJ_SPIDX_TOKEN(RES_ID, HASH_SYS_AND_VALUE, HASH_VALUE, HASH_SYS, HASH_IDENTITY, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
alter table HFJ_SPIDX_TOKEN drop constraint FK7ULX3J1GG3V7MAQREJGC7YBC4;
drop index IDX_SP_TOKEN_RESID ONLINE;
alter table HFJ_SPIDX_TOKEN add constraint FK_SP_TOKEN_RES foreign key (RES_ID) references HFJ_RESOURCE;
-- Table: HFJ_SPIDX_NUMBER create index IDX_SP_NUMBER_HASH_VAL_V2 on HFJ_SPIDX_NUMBER(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_NUMBER_HASH_VAL ONLINE;
create index IDX_SP_NUMBER_RESID_V2 on HFJ_SPIDX_NUMBER(RES_ID, HASH_IDENTITY, SP_VALUE, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
alter table HFJ_SPIDX_NUMBER drop constraint FKCLTIHNC5TGPRJ9BHPT7XI5OTB;
drop index IDX_SP_NUMBER_RESID ONLINE;
alter table HFJ_SPIDX_NUMBER add constraint FK_SP_NUMBER_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index IDX_SP_NUMBER_UPDATED ONLINE;
-- Table: HFJ_SPIDX_QUANTITY create index IDX_SP_QUANTITY_HASH_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QUANTITY_HASH ONLINE;
create index IDX_SP_QUANTITY_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QUANTITY_HASH_SYSUN ONLINE;
create index IDX_SP_QUANTITY_HASH_UN_V2 on HFJ_SPIDX_QUANTITY(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QUANTITY_HASH_UN ONLINE;
create index IDX_SP_QUANTITY_RESID_V2 on HFJ_SPIDX_QUANTITY(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
alter table HFJ_SPIDX_QUANTITY drop constraint FKN603WJJOI1A6ASEWXBBD78BI5;
drop index IDX_SP_QUANTITY_RESID ONLINE;
alter table HFJ_SPIDX_QUANTITY add constraint FK_SP_QUANTITY_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index IDX_SP_QUANTITY_UPDATED ONLINE;
-- Table: HFJ_SPIDX_QUANTITY_NRML create index IDX_SP_QNTY_NRML_HASH_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QNTY_NRML_HASH ONLINE;
create index IDX_SP_QNTY_NRML_HASH_SYSUN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_SYS_UNITS, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QNTY_NRML_HASH_SYSUN ONLINE;
create index IDX_SP_QNTY_NRML_HASH_UN_V2 on HFJ_SPIDX_QUANTITY_NRML(HASH_IDENTITY_AND_UNITS, SP_VALUE, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_QNTY_NRML_HASH_UN ONLINE;
create index IDX_SP_QNTY_NRML_RESID_V2 on HFJ_SPIDX_QUANTITY_NRML(RES_ID, HASH_IDENTITY, HASH_IDENTITY_SYS_UNITS, HASH_IDENTITY_AND_UNITS, SP_VALUE, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
alter table HFJ_SPIDX_QUANTITY_NRML drop constraint FKRCJOVMUH5KC0O6FVBLE319PYV;
drop index IDX_SP_QNTY_NRML_RESID ONLINE;
alter table HFJ_SPIDX_QUANTITY_NRML add constraint FK_SP_QUANTITYNM_RES foreign key (RES_ID) references HFJ_RESOURCE;
drop index IDX_SP_QNTY_NRML_UPDATED ONLINE;
-- Table: HFJ_RESOURCE drop index IDX_INDEXSTATUS;
create index IDX_RES_TYPE_DEL_UPDATED on HFJ_RESOURCE(RES_TYPE, RES_DELETED_AT, RES_UPDATED, PARTITION_ID, RES_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_RES_TYPE ONLINE;
-- Table: HFJ_SPIDX_STRING create index IDX_SP_STRING_HASH_NRM_V2 on HFJ_SPIDX_STRING(HASH_NORM_PREFIX, SP_VALUE_NORMALIZED, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_STRING_HASH_NRM ONLINE;
create index IDX_SP_STRING_HASH_EXCT_V2 on HFJ_SPIDX_STRING(HASH_EXACT, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
drop index IDX_SP_STRING_HASH_EXCT ONLINE;
drop index IDX_SP_STRING_UPDATED ONLINE;
-- Table: HFJ_RES_TAG create index IDX_RES_TAG_RES_TAG on HFJ_RES_TAG(RES_ID, TAG_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
create index IDX_RES_TAG_TAG_RES on HFJ_RES_TAG(TAG_ID, RES_ID, PARTITION_ID) ONLINE DEFERRED INVALIDATION;
ALTER TABLE HFJ_RES_TAG DROP CONSTRAINT IDX_RESTAG_TAGID;
drop index IDX_RESTAG_TAGID;
ALTER TABLE HFJ_RES_TAG ADD CONSTRAINT IDX_RESTAG_TAGID UNIQUE (RES_ID, TAG_ID);
-- Table: HFJ_TAG_DEF create index IDX_TAG_DEF_TP_CD_SYS on HFJ_TAG_DEF(TAG_TYPE, TAG_CODE, TAG_SYSTEM, TAG_ID);
ALTER TABLE HFJ_TAG_DEF DROP CONSTRAINT IDX_TAGDEF_TYPESYSCODE;
drop index IDX_TAGDEF_TYPESYSCODE;
ALTER TABLE HFJ_TAG_DEF ADD CONSTRAINT IDX_TAGDEF_TYPESYSCODE UNIQUE (TAG_TYPE, TAG_CODE, TAG_SYSTEM);

```

Copy
##  0.4.16.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-13)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * FlywayDB (JPA): 8.4.4 -> 8.5.0
  * Postgresql (JPA): 42.3.2 -> 42.3.3 (Addresses WS-2022-0080)

  
---|---|---  
|  [#3348](https://github.com/hapifhir/hapi-fhir/issues/3348) |  The HAPI FHIR server GraphQL endpoints now support GraphQL introspection, making them much easier to use with GraphQL-capable IDEs.  
|  [#3360](https://github.com/hapifhir/hapi-fhir/issues/3360) |  The SearchNarrowingInterceptor can now narrow searches to require a `token:in` or `token:not-in` parameter.  
|  [#3360](https://github.com/hapifhir/hapi-fhir/issues/3360) |  Performance for JPA Server ValueSet expansion has been significantly optimized in order to minimize database lookups, especially with large expansions.  
|  [#3360](https://github.com/hapifhir/hapi-fhir/issues/3360) |  Support has been added to the JPA server for token `:not-in` queries. Similar to `:not` queries, resources will currently be considered to match if any codes in the relevant resource field are not found in the given ValueSet (as opposed to matching if _all_ codes are not in the given ValueSet).  
|  [#3360](https://github.com/hapifhir/hapi-fhir/issues/3360) |  SearchNarrowingInterceptor can now be used to automatically narrow searches to include a `code:in` or `code:not-in` expression, for mandating that results must be in a specified list of codes.  
|  [#3384](https://github.com/hapifhir/hapi-fhir/issues/3384) |  Added logs that identify the resource that failed the validation check during package installation, and describe the reason for the failure.  
|  [#3387](https://github.com/hapifhir/hapi-fhir/issues/3387) |  A new batch operation framework for executing long running background jobs has been created. This new framework is called 'Batch2', and will eventually replace Spring Batch. This framework is intended to be much more resilient to failures as well as much more paralellized than Spring Batch jobs.  
|  [#3387](https://github.com/hapifhir/hapi-fhir/issues/3387) |  Support has now (finally!) been added for the FHIR Bulk Import ($import) operation. This operation is the first operation to leverage the new Batch2 framework.  
|  [#3405](https://github.com/hapifhir/hapi-fhir/issues/3405) |  A consent service implementation that enforces search narrowing rules specified by the SearchNarrowingInterceptor has been added. This can be used to narrow results from searches in cases where this can't be done using search URL manipulation. See [ResultSet Narrowing](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html#resultset-narrowing) for more information.  
|  [#3405](https://github.com/hapifhir/hapi-fhir/issues/3405) |  When validating a code, a helpful error message is now returned if the user has supplied a ValueSet URL where a CodeSystem URL should be used, since this is a common mistake.  
|  [#3405](https://github.com/hapifhir/hapi-fhir/issues/3405) |  It is now possible to register multiple `IConsentService` implementations against a single ConsentInterceptor.  
|  [#3442](https://github.com/hapifhir/hapi-fhir/issues/3442) |  Provided a Remote Terminology Service implementation for the $translate operation.  
|  [#3450](https://github.com/hapifhir/hapi-fhir/issues/3450) |  Previously there was no way to recreate freetext indexes for terminology entities. A new CLI operation, reindex-terminology now exists for this purpose.  
|  [#3452](https://github.com/hapifhir/hapi-fhir/issues/3452) |  Improve logging so it is clear when a new search parameter has been loaded into the cache.  
|  [#3452](https://github.com/hapifhir/hapi-fhir/issues/3452) |  The JPA server terminology service can now process IS-A filters in ValueSet expansion on servers with Hibernate Search disabled.  
|  [#3470](https://github.com/hapifhir/hapi-fhir/issues/3470) |  The server now supports date searches with the NOT_EQUALS (`ne`) prefix.  
|  [#3490](https://github.com/hapifhir/hapi-fhir/issues/3490) |  When using ApacheProxyAddressStrategy as a server address strategy, the `Forward` header will now be respected. Thanks to Thomas Papke for the pull request!  
|  [#3521](https://github.com/hapifhir/hapi-fhir/issues/3521) |  Added a helper method to BundleUtil, to support the creation of a new bundle entry and to set the value for one of the fields.  
|  [#3534](https://github.com/hapifhir/hapi-fhir/issues/3534) |  Added a new multi-column index on the HFJ_RESOURCE table indexing the columns RES_TYPE, RES_DELETED_AT, RES_UPDATED, PARTITION_ID and RES_ID and removed the existing single-column index on the RES_TYPE column. This new index will improve the performance of the $reindex operation and will be useful for some other queries a well.  
|  [#3547](https://github.com/hapifhir/hapi-fhir/issues/3547) |  Added a new pointcut: `STORAGE_PRESTORAGE_CLIENT_ASSIGNED_ID` which is invoked when a user attempts to create a resource with a client-assigned ID.  
|  [#3557](https://github.com/hapifhir/hapi-fhir/issues/3557) |  Group Bulk Export (e.g. Group/123/$export) now additionally supports Organization and Practitioner as valid _type parameters. This works internally by querying using a `_has` parameter  
|  [#3563](https://github.com/hapifhir/hapi-fhir/issues/3563) |  Added search parameter modifier :nickname that can be used with 'name' or 'given' search parameters. E.g. ?Patient?given:nickname=Kenny will match a patient with the given name Kenneth. Also added MDM matching algorithm named NICKNAME that matches based this.  
|  [#3468](https://github.com/hapifhir/hapi-fhir/issues/3468) |  The resource JSON can now be stored and retrieved in the Lucene/Elasticsearch index. This enables some queries to provide results without using the database. This is enabled via `DaoConfig.setStoreResourceInLuceneIndex()`  
|  [#3478](https://github.com/hapifhir/hapi-fhir/issues/3478) |  When using JPA persistence with Hibernate Search (Lucene or Elasticsearch), simple FHIR queries that can be satisfied completely by Hibernate Search no longer query the database. Before, every search involved the database, even when not needed. This is faster when there are many results.  
|  [#3482](https://github.com/hapifhir/hapi-fhir/issues/3482) |  When updating or reindexing a resource on a JPA server where indexing search param presence is enabled (i.e. support for the `:missing` modifier), updates will try to reuse existing database rows where possible instead of deleting one row and adding another. This should cause a slight performance boost systems with this feature enabled.  
|  [#2843](https://github.com/hapifhir/hapi-fhir/issues/2843) |  Reverting the change to treat canonical references as local when setting the flag `getTreatBaseUrlsAsLocal()`. This was added to address a limitation in `_include` that did not process references by canonical URLs. The `_include` search operation now includes canonical references without special configuration.  
|  [#3460](https://github.com/hapifhir/hapi-fhir/issues/3460) |  Previously, during RepositoryValidation, we would perform validation on resources created via DaoConfig's `setAutoCreatePlaceholderReferenceTargets` property. This caused validation failures on placeholder resources as they do not conform to any profile. This has been changed, and Repository Validation will not occur on any resource containing an extension with URL http://hapifhir.io/fhir/StructureDefinition/resource-placeholder`.  
|  [#3506](https://github.com/hapifhir/hapi-fhir/issues/3506) |  Changing the MDM logging to contain scores for each applied matcher field. Deleting summary score when creating MDM link.  
|  [#3529](https://github.com/hapifhir/hapi-fhir/issues/3529) |  Several classes and interfaces related to the `$expunge` operation have moved from the hapi-fhir-jpaserver-base project to the hapi-fhir-storage project.  
|  [#3534](https://github.com/hapifhir/hapi-fhir/issues/3534) |  Ensure that migration steps do not timeout. Adding an index, and other migration steps can take exceed the default connection timeout. This has been changed - migration steps now have no timeout and will run until completion.  
|  [#3550](https://github.com/hapifhir/hapi-fhir/issues/3550) |  Method signatures on several interfaces and classes related to the `$expunge` operation have changed to support the case where the primary identifier of a resource is not necessarily a `Long`.  
|  [#3568](https://github.com/hapifhir/hapi-fhir/issues/3568) |  Modified operation method binding so that later registered custom providers take precedence over earlier registered ones. In particular, this means that custom operations can now override built-in operations.  
|  [#3569](https://github.com/hapifhir/hapi-fhir/issues/3569) |  The normalized and exact string database indexes have been changed to provide faster string searches.  
|  [#3570](https://github.com/hapifhir/hapi-fhir/issues/3570) |  The tag indexes have been changed to provide faster tag searches.  
|  [#3571](https://github.com/hapifhir/hapi-fhir/issues/3571) |  Hibernate search has been updated to 6.1.4, which adds support for Elasticsearch 7.16.X. Previous installations running on 7.10.X will continue to work normally, and this change does not require you to upgrade your elasticsearch service.  
|  [#1466](https://github.com/hapifhir/hapi-fhir/issues/1466) |  The XML and JSON Parsers are now encoding narratives of contained resources. Narratives were skipped before for contained resources. This was a rule which was only valid in STU1 (https://www.hl7.org/fhir/DSTU1/narrative.html), and was removed in DSTU2+  
|  [#2588](https://github.com/hapifhir/hapi-fhir/issues/2588) |  Include property regex operation was not working when expanding ValueSet. This is now fixed  
|  [#3065](https://github.com/hapifhir/hapi-fhir/issues/3065) |  A regression in HAPI FHIR 5.5.0 meant that very large transactions where the bundle contained over 1000 distinct client-assigned resource IDs could fail on MSSQL and Oracle due to SQL parameter count limitations.  
|  [#3256](https://github.com/hapifhir/hapi-fhir/issues/3256) |  An occasional concurrency failure in AuthorizationInterceptor has been resolved. Thanks to Martin Visser for reporting and providing a reproducible test case!  
|  [#3261](https://github.com/hapifhir/hapi-fhir/issues/3261) |  When performing a search with a `_revinclude`. the results sometimes incorrectly included resources that were reverse included by other search parameters with the same name. Thanks to GitHub user @vivektk84 for reporting and to Jean-Francois Briere for proposing a fix.  
|  [#3316](https://github.com/hapifhir/hapi-fhir/issues/3316) |  When deleting a large ValueSet operation was timing out. This issue has been fixed.  
|  [#3346](https://github.com/hapifhir/hapi-fhir/issues/3346) |  When searching for date search parameters on Postgres, ordinals could sometimes be represented as strings, causing a search failure. This has been corrected.  
|  [#3387](https://github.com/hapifhir/hapi-fhir/issues/3387) |  A race condition in the Subscription processor meant that a Subscription could fail to register right away if it was modified immediately after being created. Note that this issue only affects rapid changes, and only caused the subscription to be unregistered for a maximum of one minute after its initial creation so the impact of this issue is expected to be low.  
|  [#3392](https://github.com/hapifhir/hapi-fhir/issues/3392) |  When cross-partition reference Mode is used, the rest-hook subscriptions on a partition enabled server would cause a NPE. Cause of this is from the reloading of the subscription when the server is restarted. This issue has been fixed. Also fixed issue with revinclude for rest-hook subscription not working.  
|  [#3400](https://github.com/hapifhir/hapi-fhir/issues/3400) |  User was permitted to bulk export all groups/patients when they were unauthorized. This issue has been fixed.  
|  [#3418](https://github.com/hapifhir/hapi-fhir/issues/3418) |  `GET` resource with `_total=accurate` and `_summary=count` if consent service enabled should throw an InvalidRequestException. This issue has been fixed.  
|  [#3421](https://github.com/hapifhir/hapi-fhir/issues/3421) |  ValueSet pre-expansion was failing when number of concepts was larger than configured BooleanQuery.maxClauseCount value (default is 1024). This is now fixed.  
|  [#3423](https://github.com/hapifhir/hapi-fhir/issues/3423) |  Fixed a bug when searching for the resource persistent id in the partitioned environment. This happens when the Cross-Partition Reference Mode is set to ALLOWED_UNQUALIFIED and it is searching for the wrong partition id and resulted in resource persistent id not found.  
|  [#3418](https://github.com/hapifhir/hapi-fhir/issues/3418) |  Updated the FHIR core libs to 5.6.36 to support fix for funcReplace in FHIRPathEngine.  
|  [#3436](https://github.com/hapifhir/hapi-fhir/issues/3436) |  A regression in HAPI FHIR 5.7.0 meant that when UnknownCodeSystemWarningValidationSupport was configured for WARNING behaviour, validating a field with an implicit code system could incorrectly result in an error.  
|  [#3441](https://github.com/hapifhir/hapi-fhir/issues/3441) |  Reindexing jobs were not respected the passed in date range in SearchParams. We now take date these date ranges into account when running re-indexing jobs.  
|  [#3469](https://github.com/hapifhir/hapi-fhir/issues/3469) |  Previously, `or` expressions were not being properly validated in FHIRPath in STU3 due to a bug with expression path splitting. This has been corrected.  
|  [#3486](https://github.com/hapifhir/hapi-fhir/issues/3486) |  When doing package upload, all resources were filtered by `status=active`. That is inappropriate for some types. For Subscription, we need to check if the value is requested, for DocumentReference and Communication other values outside the expected active and not active also exist. This change adds a check for those types of resources other than the one normally done for active matching value.  
|  [#3493](https://github.com/hapifhir/hapi-fhir/issues/3493) |  Previously the Fhir parser would only set the resource type on the resource ID for resources which implemented `IDomainResource`. This caused a few resource types to be missed. This has been corrected, and resource type is now set on the id element for all `IBaseResource` instances instead.  
|  [#3504](https://github.com/hapifhir/hapi-fhir/issues/3504) |  Previously, allowing any client assigned ID by setting `ClientIdStrategyEnum` to `ANY` threw an exception when creating CodeSystem resources. This has been corrected.  
|  [#3515](https://github.com/hapifhir/hapi-fhir/issues/3515) |  Supporting expansion of ValueSet include/exclude system URI expressed in canonical format during validation.  
|  [#3524](https://github.com/hapifhir/hapi-fhir/issues/3524) |  Previously if partitioning was enabled, the URL returned from `$export-poll-status` provided a location in the wrong partition. This has been fixed and all Bulk Exports will be stored in the `DEFAULT` partition.  
|  [#3530](https://github.com/hapifhir/hapi-fhir/issues/3530) |  TerserUtil.mergeAllFields() threw an exception when cloning contained resources. This has been corrected.  
|  [#3553](https://github.com/hapifhir/hapi-fhir/issues/3553) |  Added a new setting to BinaryStorageInterceptor which allows you to disable binary de-externalization during resource reads.  
|  [#3561](https://github.com/hapifhir/hapi-fhir/issues/3561) |  Previously, Bulk export jobs automatically cleared the collection after a hardcoded 2 hour time period from start of the job. This is now configurable via a new DaoConfig property, `setBulkExportFileRetentionPeriodHours()`. If this is set to a value of 0 or below, then the collections will never be expired.  
|  [#3578](https://github.com/hapifhir/hapi-fhir/issues/3578) |  The tag index migration failed on Postres and Oracle. These have been fixed.  
|  [#3579](https://github.com/hapifhir/hapi-fhir/issues/3579) |  Mdm was not excluding NO_MATCH from golden-resource candidates in eid mode. This caused mdm to produce an error when a Patient eid is changed after that patient's link was updated to NO_MATCH. This has been corrected  
|  [#3584](https://github.com/hapifhir/hapi-fhir/issues/3584) |  Unmodified string searches and string `:contains` search were incorrectly case-sensitive when configured with recent versions of Lucene/Elasticsearch. This has been corrected.  
|  [#3586](https://github.com/hapifhir/hapi-fhir/issues/3586) |  While converting the reindexing job to the new batch framework, a regression of [#3441](https://github.com/hapifhir/hapi-fhir/issues/3441) was introduced. Reindexing jobs were not respecting the passed in `_lastUpdated` parameter. We now take date these date ranges into account when running re-indexing jobs.  
|  [#3590](https://github.com/hapifhir/hapi-fhir/issues/3590) |  When searching with the `_lastUpdated` parameter and using the `ne` prefix, search would fail with HAPI-1928 error. This has been fixed.  
|  [#3592](https://github.com/hapifhir/hapi-fhir/issues/3592) |  Command-line log output now only sends colour commands if output is being printed to a console. Otherwise, (e.g. if output is redirected to a file) the log output will not contain any special colour escape characters.  
|  [#3602](https://github.com/hapifhir/hapi-fhir/issues/3602) |  New batch job implementation (batch2) were staying on IN_PROGRESS status after being cancelled. That is now fixed. After cancellation status is changed to CANCELLED.  
|  [#3396](https://github.com/hapifhir/hapi-fhir/issues/3396) |  Previously, it was possible to update a resource with wrong `tenantID`. This issue has been fixed.  
|  [#3510](https://github.com/hapifhir/hapi-fhir/issues/3510) |  The Spring Framework library was upgraded to version 5.3.18 in order to avoid depending on a version known to be vulnerable to CVE-2022-22965, known as Spring4Shell. HAPI FHIR is not believed to be vulnerable to this issue, but the library has been bumped as a precaution.  
#  0.4.17HAPI FHIR 5.7.6 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change6.0.0-0)
##  0.4.17.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-14)
**Released:** 2022-07-07
**Codename:** (Sojourner)
##  0.4.17.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-14)
#  0.4.18HAPI FHIR 5.7.5 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#hapi-fhir-575-sojourner)
##  0.4.18.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-15)
**Released:** 2022-07-07
**Codename:** (Sojourner)
##  0.4.18.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-15)
|  [#3065](https://github.com/hapifhir/hapi-fhir/issues/3065) |  A regression in HAPI FHIR 5.5.0 meant that very large transactions where the bundle contained over 1000 distinct client-assigned resource IDs could fail on MSSQL and Oracle due to SQL parameter count limitations.  
---|---|---  
#  0.4.19HAPI FHIR 5.7.4 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.5-3065)
##  0.4.19.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-16)
**Released:** 2022-06-03
**Codename:** (Sojourner)
##  0.4.19.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-16)
|  [#2669](https://github.com/hapifhir/hapi-fhir/issues/2669) |  Previously subscriptions in a partition with the id null will be matched against incoming resources from all partitions. Changed to subscriptions will only match against incoming resources in the partition the subscription exists in unless cross partition subscription is enabled and the subscription has the appropriate extension.  
---|---|---  
#  0.4.20HAPI FHIR 5.7.3 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.4-3669)
##  0.4.20.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-17)
**Released:** 2022-05-30
**Codename:** (Sojourner)
##  0.4.20.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-17)
|  |  This version specifically modifies reindex to support moving data from the RES_TEXT to the RES_TEXT_VC column in the HFJ_RES_VER table. This is especially important for PostgreSQL users, as the RES_TEXT column only has an addressable space of about 4 billion resources. Any installation that exceeds this amount of resources stored in the RES_TEXT will experience that the software hangs on attempting to store new resources. In order to avoid this, you should use the `DaoConfig#setInlineResourceTextBelowSize` setting, and set it to a large non-zero value. This will cause PostgreSQL to not store the resource text as a LOB, but instead as a VARCHAR field. By default, this field has length 4000, but you can and should update it by following the documentation [here](https://smilecdr.com/docs/fhir_storage_relational/fhir_storage_relational_module.html#resource-body-storage).  
---|---|---  
|  [#3654](https://github.com/hapifhir/hapi-fhir/issues/3654) |  UPDATE: This change has breen removed. Previous content was: 'Modify reindexing to migrate data HFJ_RES_VER data from the RES_TEXT column to the RES_TEXT_VC if the resource's size falls inside of the configuration defined in DaoConfig's `getInlineResourceTextBelowSize` property.'  
#  0.4.21HAPI FHIR 5.7.2 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.3-0)
##  0.4.21.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-18)
**Released:** 2022-03-31
**Codename:** (Sojourner)
##  0.4.21.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-18)
|  [#3514](https://github.com/hapifhir/hapi-fhir/issues/3514) |  Bump the version of Spring Core to 5.3.18 to avoid CVE-2022-22963  
---|---|---  
#  0.4.22HAPI FHIR 5.7.1 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.2-3514)
##  0.4.22.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-19)
**Released:** 2022-03-09
**Codename:** (Sojourner)
##  0.4.22.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-19)
|  [#3460](https://github.com/hapifhir/hapi-fhir/issues/3460) |  Previously, during RepositoryValidation, we would perform validation on resources created via DaoConfig's `setAutoCreatePlaceholderReferenceTargets` property. This caused validation failures on placeholder resources as they do not conform to any profile. This has been changed, and Repository Validation will not occur on any resource containing an extension with URL http://hapifhir.io/fhir/StructureDefinition/resource-placeholder`.  
---|---|---  
#  0.4.23HAPI FHIR 5.7.0 (Sojourner)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.1-3460)
##  0.4.23.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-20)
**Released:** 2022-02-17
**Codename:** (Sojourner)
##  0.4.23.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-20)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * log4j-api (JPA): 2.11.1 -> 2.17.1 (Addresses CVE-2021-44228 - HAPI FHIR was not vulnerable to this issue but this upgrade avoids unnecessary OWASP scan notices)
  * SLF4j (All): 1.7.30 -> 1.7.33
  * Logback (All): 1.2.8 -> 1.2.10
  * Commons-IO (All): 2.8.0 -> 2.11.0
  * Jackson (All): 2.13.0 -> 2.13.1
  * Guava (All): 30.1.1-jre -> 31.0.1-jre
  * JDOM (XML Patch Support): 2.0.6 -> 2.0.6.1 (Addresses CVE-2021-33813)
  * Spring (JPA): 5.3.7 -> 5.3.15
  * Spring (JPA): 5.3.7 -> 5.3.15
  * Spring-Data (JPA): 2.5.0 -> 2.6.1
  * Hibernate ORM (JPA): 5.4.30.Final -> 5.6.2.Final
  * Flyway (JPA): 6.5.4 -> 8.4.1
  * Sqlbuilder (JPA): 3.0.1 -> 3.0.2
  * H2 (JPA): 1.4.200 -> 2.1.210 (Note that this change requires the use of the HapiFhirH2Dialect instead of the built-in Hibernate H2Dialect due to Hibernate issue [HHH-15002](https://hibernate.atlassian.net/browse/HHH-15002)
  * Commons-DBCP2 (JPA): .8.0 -> .8.0 -> 2.9.0
  * Swagger-Models (OpenAPI Support): 2.1.7 -> 2.1.12
  * Thymeleaf (Testpage Overlay): 3.0.12.RELEASE -> 3.0.14.RELEASE (Addresses CVE-2021-43466)
  * Commons-CLI (CLI): 1.4 -> 1.5.0
  * JANSI (CLI): 2.3.2 -> 2.4.0
  * Jetty Server (CLI): 9.4.43.v20210629 -> 9.4.44.v20210927
  * Spring Boot (Boot): 2.5.0 -> 2.6.2
  * Swagger UI (OpenAPI): 3.46.0 -> 4.1.3
  * Resteasy (JAX-RS): 4.0.0.Beta3 -> 5.0.2.Final
  * Postgresql (JPA): 42.3.1 -> 42.3.2
  * Spring Security Oauth2(Oauth): 2.0.2.RELEASE -> 2.0.17.RELEASE

  
---|---|---  
|  [#1892](https://github.com/hapifhir/hapi-fhir/issues/1892) |  In the JPA server, the token `:of-type` modifier is now supported. This is an optional feature and must be explicitly enabled (default is disabled).  
|  [#2480](https://github.com/hapifhir/hapi-fhir/issues/2480) |  Added partition support for subscriptions. Subscriptions will now only match resource from the same partition  
|  |  New configuration option added to validate bundle resources concurrently. Also new configuration added to skip validation of contained resources.  
|  [#2718](https://github.com/hapifhir/hapi-fhir/issues/2718) |  Added support for cross-partition subscriptions. Subscription in the default partition can now listen to resource changes from all partitions  
|  [#2838](https://github.com/hapifhir/hapi-fhir/issues/2838) |  The resource contents are optionally added to the lucene index so $lastn queries can be satisfied without database access. This is activated by the DaoConfig 'StoreResourceInLuceneIndex' property.  
|  [#3120](https://github.com/hapifhir/hapi-fhir/issues/3120) |  Added http://hapifhir.io/fhir/StructureDefinition/subscription-delivery-retry-count extension that can be provided to a subscription to define a specific retry strategy (retry retry-count number of times before giving up).  
|  [#3136](https://github.com/hapifhir/hapi-fhir/issues/3136) |  Provided a Remote Terminology Service implementation for the $validate-code Operation.  
|  [#3172](https://github.com/hapifhir/hapi-fhir/issues/3172) |  Implement support for $member-match operation by coverage-id or coverage-identifier. (Beneficiary demographic matching not supported)  
|  [#3158](https://github.com/hapifhir/hapi-fhir/issues/3158) |  Add the concept of `messageKey` to the BaseResourceMessage class. This is to support brokers which permit key-based partitioning.  
|  [#3243](https://github.com/hapifhir/hapi-fhir/issues/3243) |  Allow Rest Hook subscriptions to be configured to send delete requests.  
|  [#3335](https://github.com/hapifhir/hapi-fhir/issues/3335) |  Added ability to specify max code lengths for supported Phonetic Encoders (Metaphone, Double_Metaphone). To specify max code length, append the code length to the searchparameter-phonetic-encoder extension in brackets after the encoder type. eg: { "url": "http://hapifhir.io/fhir/StructureDefinition/searchparameter-phonetic-encoder", "valueString": "METAPHONE(5)" }  
|  [#3347](https://github.com/hapifhir/hapi-fhir/issues/3347) |  Added ability to load Implementation Guide packages from filesystem by supporting the file:/ syntax of url.  
|  [#1892](https://github.com/hapifhir/hapi-fhir/issues/1892) |  A redundant set of hash calculations in the JPA server was eliminated.  
|  [#3197](https://github.com/hapifhir/hapi-fhir/issues/3197) |  A number of minor optimizations have been added to the JsonParser serializer module as well as to the transaction processor. These optimizations lead to a significant performance improvement when processing large transaction bundles (i.e. transaction bundles containing a larger number of entries).  
|  [#3197](https://github.com/hapifhir/hapi-fhir/issues/3197) |  A new configuration option has been added to ParserOptions called `setAutoContainReferenceTargetsWithNoId`. This setting disables the automatic creation of contained resources in cases where the target/contained resource is set to a Reference instance but not actually added to the `Resource.contained` list. Disabling this automatic containing yields a minor performance improvement in systems that do not need this functionality.  
|  |  Improved validation performance by switching validation serialization from XML to JSON.  
|  [#3153](https://github.com/hapifhir/hapi-fhir/issues/3153) |  Significantly improved $delete-expunge performance by adding database indexes, and filtering needed foreign keys to delete by resource type.  
|  [#3267](https://github.com/hapifhir/hapi-fhir/issues/3267) |  A new JPA setting has been added to DaoConfig settings called **Inline Resource Text Below Size**. When this setting is set to a positive value, any resources with a total serialized length smaller than the given number will be stored as an inline VARCHAR2 text value on the HFJ_RES_VER table, instead of using an external LOB column. This improves read/write performance (often by a significant amount) at the expense of a slightly larger amount of disk usage.  
|  [#3312](https://github.com/hapifhir/hapi-fhir/issues/3312) |  Improves the performance of the query for searching by chained search parameter when the `Index Contained Resources` feature is enabled.  
|  [#3327](https://github.com/hapifhir/hapi-fhir/issues/3327) |  Improved performance of validating resources when skip contained resources is enabled.  
|  [#3158](https://github.com/hapifhir/hapi-fhir/issues/3158) |  Previously in configuring MDM, you were only allowed to set a single `eidSystem` which was global regardless of how many resource types you were performing MDM on. This has been changed. That field is now deprecated, and a new field called `eidSystems` (a json object) should be used instead. This will allow you to have granular control over which resource types use which EIDs. See the [documentation](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html) of `eidSystems` for more information.  
|  [#3198](https://github.com/hapifhir/hapi-fhir/issues/3198) |  Fixed a regression where packages would fail to load if HAPI-FHIR was operating in DATABASE binary storage mode.  
|  [#3205](https://github.com/hapifhir/hapi-fhir/issues/3205) |  These changes are related to bumping the dependency on org.hl7.fhir.core to version 5.3.0. The main updates that resulted in the majority of the changes in this PR were the addition of the interface IValidationPolicyAdvisor, and the refactoring of some of the main validation classes (for cleaning purposes). Also, many of the error messages were updated, so the resulting tests in HAPI had to be modified to reflect these message changes. In regard to the new interface IValidationPolicyAdvisor, by implementing this interface and passing it through the validation context in HAPI to the InstanceValidator in the core libraries, users can now control the behavior of the validator when validating references, contained resources, and coded content. Previously, only references were controlled in this way, and users controlled this by overriding the validation fetcher. Test cases were added in the validation test cases repository to demonstrate it's functionality. In particular, the two test cases contained-resource-bad-id, and contained-resource-bad-id-ignore. The definitions and expected outcomes of these test cases are in the manifest.json in that repository.  
|  |  Add email configuration to MailSvc constructor  
|  [#3379](https://github.com/hapifhir/hapi-fhir/issues/3379) |  Removed the following modules from the HAPI-FHIR project: `hapi-fhir-testpage-interceptor`, `hapi-fhir-structures-dstu`, `hapi-fhir-oauth2`, `hapi-fhir-narrativegenerator`.  
|  [#2377](https://github.com/hapifhir/hapi-fhir/issues/2377) |  Calling the `$document` operation previously omitted the fullUrl of the bundle entries. This has been corrected.  
|  |  When a subscription fails to activate (either on the first attempt or on start up), hapi-fhir will log the exception, set the subscription to ERROR state, and continue on. This is to prevent hanging on startup for errors that cannot be resolved by infinite retries.  
|  |  $member-match operation was allowed to be invoked by GET when it shouldn't have. That is fixed. Operation can only be invoked by POST request  
|  [#2720](https://github.com/hapifhir/hapi-fhir/issues/2720) |  Using the delivery option: Payload Search Result Mode for rest-rook subscriptions on a partition enabled server would cause a NPE. This issue has been fixed.  
|  [#2791](https://github.com/hapifhir/hapi-fhir/issues/2791) |  Users were able to access data on partitions they did not have access to by using pagination links generated by users who did have access.  
|  [#3108](https://github.com/hapifhir/hapi-fhir/issues/3108) |  Code System deletion background tasks were taking over a day to complete on very large CodeSystems for PostgreSQL, SQL Server and Oracle databases. That was improved now taking less than an hour in all three platforms  
|  [#3145](https://github.com/hapifhir/hapi-fhir/issues/3145) |  RequestValidatingInteceptor incorrectly prevented GraphQL requests from being submitted using the HTTP POST form of the GraphQL operation.  
|  [#3153](https://github.com/hapifhir/hapi-fhir/issues/3153) |  Updated UnknownCodeSystemWarningValidationSupport to allow the throwing of warnings if configured to do so.  
|  [#3156](https://github.com/hapifhir/hapi-fhir/issues/3156) |  Fixed race condition in BaseHapiFhirDao that resulted in exceptions being thrown when multiple resources in a single bundle transaction had the same tags.  
|  [#3158](https://github.com/hapifhir/hapi-fhir/issues/3158) |  Resource links were previously not being consistently created in cases where references were versioned and pointing to recently auto-created placeholder resources.  
|  [#3170](https://github.com/hapifhir/hapi-fhir/issues/3170) |  Fixed language code validation so that it is case insensitive (eg, en-US, en-us, EN-US, EN-us should all work)  
|  |  Fixed a serious performance issue with the `$reindex` operation.  
|  |  In servers with a large number of StructureDefinition resources loaded, occasionally a call for the CapabilityStatement could take a very long time to return. This has been corrected.  
|  [#3215](https://github.com/hapifhir/hapi-fhir/issues/3215) |  $everything operation returns a 500 error when querying for a page when _getpagesoffset is greater than or equal to 300. This has been corrected.  
|  |  MDM was throwing a NullPointerException when upgrading a match from POSSIBLE_MATCH to MATCH. This has been corrected.  
|  [#3242](https://github.com/hapifhir/hapi-fhir/issues/3242) |  Fixed bug that caused queries that checked Elasticsearch when setting `_total=accurate` searches to return all values when using _content or _text parameters  
|  |  Terminology reindexing job was launching the wrong process, so terminology reindexing was never launched. This has been fixed.  
|  [#3266](https://github.com/hapifhir/hapi-fhir/issues/3266) |  Concurrent validation failed with StringIndexOutOfBoundsException when the validation location did not contain a '.'. This has been corrected.  
|  [#3274](https://github.com/hapifhir/hapi-fhir/issues/3274) |  Transactions with entries that have request.url values that are fully qualified urls will now be rejected.  
|  [#3275](https://github.com/hapifhir/hapi-fhir/issues/3275) |  In rare cases where patient ID String happened to have hashCode equal to Integer.MIN_VALUE, PatientIdPartitionInterceptor would generate an invalid partition ID. This has been fixed.  
|  [#3283](https://github.com/hapifhir/hapi-fhir/issues/3283) |  Calling delete on a non-existent resource should not return a 404 (not found).  
|  [#3295](https://github.com/hapifhir/hapi-fhir/issues/3295) |  When $mdm-clear operation batch was split into multiple threads, ResourceVersionConflictExceptions were being thrown. This issue has been fixed.  
|  |  Conformance validation should happen once per client-endpoint, no matter the number of threads executing the requests, but was happening once per client-endpoint-thread. This is now fixed.  
|  [#3314](https://github.com/hapifhir/hapi-fhir/issues/3314) |  Previously, `$binary-access-read` and other operations that return neither method outcomes nor resources were causing no invocation of the `SERVER_OUTGOING_RESPONSE` pointcut. This has been corrected, and those operations will now correctly invoke `SERVER_OUTGOING_RESPONSE`.  
|  [#3322](https://github.com/hapifhir/hapi-fhir/issues/3322) |  GraphQL searches cannot be executed when number of matching results exceeds default page size. This has been corrected.  
|  [#3325](https://github.com/hapifhir/hapi-fhir/issues/3325) |  In HAPI FHIR 5.6.0, a regression meant that JPA server FHIR transactions containing two identical conditional operations (for example, a transaction with two conditional create operations where the conditional URL was identical for both) could result in resources being saved in an inconsistent state. This scenario will now be result in the server returning an HTTP 400 error instead. Note that there is no meaning defined in the FHIR specification for duplicate entries in a FHIR transaction bundle, so it has never been recommended to do this.  
|  [#3331](https://github.com/hapifhir/hapi-fhir/issues/3331) |  The JPA server will now validate `_include` and `_revinclude` statements before beginning search processing. This prevents an ugly JPA error when some invalid params are used.  
|  [#3371](https://github.com/hapifhir/hapi-fhir/issues/3371) |  Enhanced Lucene Indexing failed when indexing contained resources. Contained resources are not indexed in Lucene/Elasticsearch, but this no longer causes an exception.  
|  [#3374](https://github.com/hapifhir/hapi-fhir/issues/3374) |  Chained searches will handle common search parameters correctly when the `Index Contained Resources` configuration parameter is enabled.  
|  [#3392](https://github.com/hapifhir/hapi-fhir/issues/3392) |  When cross-partition reference Mode is used, the rest-hook subscriptions on a partition enabled server would cause a NPE. Cause of this is from the reloading of the subscription when the server is restarted. This issue has been fixed. Also fixed issue with revinclude for rest-hook subscription not working.  
|  [#3394](https://github.com/hapifhir/hapi-fhir/issues/3394) |  Bulk export permissions were over-permissive in RuleBulkExportImpl. This has been corrected.  
|  [#1892](https://github.com/hapifhir/hapi-fhir/issues/1892) |  In the JPA server, searching using the token `:text` modifier will no longer index and match values in `Identifier.type.text`. Previously we indexed this element, but the FHIR specification does not actually show this field as being indexed for this modifier, and intuitively it doesn't make sense to do so.  
#  0.4.24HAPI FHIR 5.6.4 (Raccoon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.7.0-0)
##  0.4.24.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-21)
**Released:** 2022-07-07
**Codename:** (Raccoon)
##  0.4.24.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-21)
|  [#3256](https://github.com/hapifhir/hapi-fhir/issues/3256) |  An occasional concurrency failure in AuthorizationInterceptor has been resolved. Thanks to Martin Visser for reporting and providing a reproducible test case!  
---|---|---  
#  0.4.25HAPI FHIR 5.6.3 (Raccoon)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.6.4-3256)
##  0.4.25.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-22)
**Released:** 2022-03-31
**Codename:** (Raccoon)
##  0.4.25.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-22)
|  [#3514](https://github.com/hapifhir/hapi-fhir/issues/3514) |  Bump Spring Core dependency to remove Spring4Shell vulnerability.  
---|---|---  
#  0.4.26HAPI FHIR 5.5.7 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.6.3-3514)
##  0.4.26.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-23)
**Released:** 2022-03-31
**Codename:** (Quasar)
##  0.4.26.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-23)
|  |  Bump Spring Core dependency to 5.3.18 to remove vulnerability.  
---|---|---  
#  0.4.27HAPI FHIR 5.5.6 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.5.7-3514)
##  0.4.27.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-24)
**Released:** 2022-02-11
**Codename:** (Quasar)
##  0.4.27.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-24)
|  [#3374](https://github.com/hapifhir/hapi-fhir/issues/3374) |  Chained searches will handle common search parameters correctly when the `Index Contained Resources` configuration parameter is enabled.  
---|---|---  
#  0.4.28HAPI FHIR 5.5.5 (Quasar)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#change5.5.6-3374)
##  0.4.28.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#release-information-25)
**Released:** 2022-01-22
**Codename:** (Quasar)
##  0.4.28.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2022.html#changes-25)
|  [#3312](https://github.com/hapifhir/hapi-fhir/issues/3312) |  Improves the performance of the query for searching by chained search parameter when the `Index Contained Resources` feature is enabled.  
---|---|---  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2023.html)
0.4 Changelog: 2022 
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
[ 0.5 Changelog: 2021 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2021.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)