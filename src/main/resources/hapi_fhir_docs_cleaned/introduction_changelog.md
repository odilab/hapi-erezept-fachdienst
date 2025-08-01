---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html
crawled: 2025-08-01T14:05:49.388834
---

# Introduction Changelog

#  0.1.1Changelog: 2025
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changelog-2025)
#  0.1.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changelog)
#  0.1.3HAPI FHIR 8.6.0 (TBD)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#hapi-fhir-860-tbd)
##  0.1.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information)
**Released:** 2025-11-20
**Codename:** (TBD)
##  0.1.3.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions)
##  0.1.3.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes)
#  0.1.4HAPI FHIR 8.4.0 (TBD)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#hapi-fhir-840-tbd)
##  0.1.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-1)
**Released:** 2025-08-18
**Codename:** (TBD)
##  0.1.4.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions-1)
## Publishing Changes
As of `8.3.12-SNAPSHOT`, HAPI-FHIR snapshots are now published on [Maven Central](https://central.sonatype.com/namespace/ca.uhn.hapi.fhir). As of June 30th, [OSS Sonatype has been sunsetted](https://central.sonatype.org/news/20250326_ossrh_sunset/). If you need to rely on older snapshots, you must build them from source locally. If you consume snapshots, you will need to update your pom.xml with the following repository information:
```
<repositories>
    <repository>
        <name>Central Portal Snapshots</name>
        <id>central-portal-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

```

Copy
## Breaking Changes
  * FhirPath `PATCH` operations that match multiple elements will no longer replace these values, but throw an exception. This is in line with the [spec](https://www.hl7.org/fhir/R4/fhirpatch.html).


##  0.1.4.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-1)
|  [#3661](https://github.com/hapifhir/hapi-fhir/issues/3661) |  When performing a resource validation on R4+ servers, the generated OperationOutcome resource containing the validation results will now have the `OperationOutcome.issue.expression` element populated. This element contains a FHIRPath expression to the element containing the issue. The FHIR specification has deprecated the `OperationOutcome.issue.location` element. At this time, no change to the values placed in that element have been made, although it may no longer be populated in a future release of HAPI FHIR.  
---|---|---  
|  [#6152](https://github.com/hapifhir/hapi-fhir/issues/6152) |  Added the option to do HTTP HEAD requests against /metadata. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#6861](https://github.com/hapifhir/hapi-fhir/issues/6861) |  Added support for parsing additional folder resources in IG packages as utility function. This allows an easier way API-wise of adding e.g. test resources.  
|  [#6874](https://github.com/hapifhir/hapi-fhir/issues/6874) |  When _automatically create placeholder reference targets_ is enabled and a placeholder resource is created, the ID of that resource will be included in an extension on the response OperationOutcome. This extension will have the URL `http://hapifhir.io/fhir/StructureDefinition/oo-placeholder-id`.  
|  [#6889](https://github.com/hapifhir/hapi-fhir/issues/6889) |  A new table `HFJ_RESOURCE_TYPE` has been introduced. This table stores all known resource types from all releases. It is pre-populated at server start up. New or custom resource types are added to this table on demand. A new column `HFJ_RESOURCE_TYPE_ID` has been added to the following tables. 
  * HFJ_RESOURCE
  * HFJ_RE_VER
  * HFJ_RES_TAG
  * HFJ_RES_HISTORY_TAG
  * HFJ_RES_VER_LINK

This column is used to store the resource type ID which references the `HFJ_RESOURCE_TYPE` table.  
|  [#6904](https://github.com/hapifhir/hapi-fhir/issues/6904) |  The in-memory ValueSet expander, used for validation outside of JPA, and for validation of ad-hoc and not pre-expanded ValueSets, now supports filtered inclusion/exclusion with a similar scope to the JPA pre-expander. Thanks to Ibrahim Tallouzi for the contribution!  
|  [#6928](https://github.com/hapifhir/hapi-fhir/issues/6928) |  The logic to detect and resolve circular StructureDefinition dependencies when generating snapshots has been improved to be more resilient and better able to successfully generate a snapshot.  
|  [#6974](https://github.com/hapifhir/hapi-fhir/issues/6974) |  Removed all deprecated Clinical Reasoning logic.  
|  [#6983](https://github.com/hapifhir/hapi-fhir/issues/6983) |  FullText indexing via the `_content` and `_text` Search Parameters is now more configurable: Two new pointcuts have been added which can be used to customize the text which is indexed from resources. Also, it is possible to select which resource types each SearchParameter indexes by storing a custom SearchParameter with the appropriate URL. Examples of both of these approaches can be found in the documentation.  
|  [#6996](https://github.com/hapifhir/hapi-fhir/issues/6996) |  A new `ValidationMessagePostProcessingInterceptor` has been added to allow modification of validation messages severity according to rules specified using `ValidationPostProcessingRuleJson`.  
|  [#7001](https://github.com/hapifhir/hapi-fhir/issues/7001) |  FhirPath PATCH operation has been updated to be spec complaint. This means it will now allow matching/replacing fhir paths that match to primitive values (String, int, datetime, etc). It also means that fhirpath patch will only support fhirpaths that match a singular element. Ie, if multiple (or no) elements match the provided fhir path, an error will result.  
|  [#7002](https://github.com/hapifhir/hapi-fhir/issues/7002) |  Added support for reindexing deleted resources. This can be done by adding the new `_includeDeleted` parameter to the Parameters resource upon submitting the `$reindex` job. See [the reindex documentation](https://smilecdr.com/docs/fhir_repository/search_parameter_reindexing.html#reindex-batch-job) for more information.  
|  [#7003](https://github.com/hapifhir/hapi-fhir/issues/7003) |  Inject narrative generator to jpa server Fhir context to ensure narrative can be added to resources.  
|  [#7016](https://github.com/hapifhir/hapi-fhir/issues/7016) |  New interfaces and utilities have been added to convert the X-Request-Partition-IDs header into a RequestPartitionId. Also a new IDefaultPartitionSettings interface has been added to provide default partition details to assist in converting X-Request-Partition-IDs=DEFAULT into a default partition id.  
|  [#7019](https://github.com/hapifhir/hapi-fhir/issues/7019) |  Added `getNamedParameterReferences` method to `ParametersUtil`.  
|  [#7023](https://github.com/hapifhir/hapi-fhir/issues/7023) |  The HTTP Patch operation now supports the `X-Rewrite-History` header. This enables a specific version of a resource to be rewritten in-place without the resource version being incremented by using the PATCH operation. See [Patch with History Rewrite](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#patch-with-history-rewrite) for more information.  
|  [#7038](https://github.com/hapifhir/hapi-fhir/issues/7038) |  $hapi.fhir.replace-references and $merge operations now create a Provenance resource upon successful completion.  
|  [#7055](https://github.com/hapifhir/hapi-fhir/issues/7055) |  New filters have been added to allow users to filter jobs in the Batch2 search by job status, job type, job ID, and job creation date. This change applies to the class `JpaJobPersistenceImpl.java`  
|  [#7061](https://github.com/hapifhir/hapi-fhir/issues/7061) |  When invoking a FHIR extended operation such as `$validate`, where the operation is commonly invoked using an HTTP POST with the resource payload in the POST body (as opposed to being embedded in a Parameters resource), any parameters on the URL were being ignored. This has been corrected.  
|  [#7080](https://github.com/hapifhir/hapi-fhir/issues/7080) |  A new pointcut has been added which can be used to selectively enable or modify automatically created placeholder reference targets.  
|  [#7082](https://github.com/hapifhir/hapi-fhir/issues/7082) |  Add an ability to filter resources out of `$everything` and `$export` operations. This can be done via a newly created ISearchLimiterSvc. By default, no filtering is provided. But consumers may inject an ISearchLimiterSvc and specify which resources to filter on which operations. This is to allow consumers to filter out resources (for example, for security reasons), even if those resources are allowed by the spec.  
|  [#7101](https://github.com/hapifhir/hapi-fhir/issues/7101) |  A new system level operation, named `$hapi.fhir.undo-replace-references` has been added. This operation restores the resources that were updated by a `$hapi.fhir.replace-references` operation to their previous versions.  
|  [#7123](https://github.com/hapifhir/hapi-fhir/issues/7123) |  The Bulk $export batch job now supports the use of the `_until` parameter. Thanks Mads Swensson for the contribution!  
|  [#7128](https://github.com/hapifhir/hapi-fhir/issues/7128) |  Patient level Bulk export (`/Patient/$export`) now accepts the `patient` parameter of type reference to match the FHIR Bulk Data Access IG. For backwards compatibility with earlier implementations, `patient` may also be of string type, and will be interpreted as a reference.  
|  [#7133](https://github.com/hapifhir/hapi-fhir/issues/7133) |  Added CanonicalBundleEntry class to support working with Bundle entries in a FHIR version independent way.  
|  [#7148](https://github.com/hapifhir/hapi-fhir/issues/7148) |  An operation named `$hapi.fhir.undo-merge` has been added for Patient resources. This operation restores the resources that were updated by a Patient `$merge` operation to their previous versions based on the Provenance resource created by the `$merge` operation.  
|  [#7150](https://github.com/hapifhir/hapi-fhir/issues/7150) |  Add a repository loader for fhir-repository:exp-kalm-filesystem:/path/to/directory repositories used by the KALM IDE.  
|  [#7186](https://github.com/hapifhir/hapi-fhir/issues/7186) |  We now mark the match URL query span using userData on the RequestDetails so interceptors can distinguish match URL queries from other queries. This can be tested by calling MatchResourceUrlService.isDuringMatchUrlQuerySpan().  
|  [#7604](https://github.com/hapifhir/hapi-fhir/issues/7604) |  The new Repositories builder supports creating instances of IRepository by urls. We include a limited in-memory implementation of IRepository useful for unit-testing that can be created with Repositories.repositoryForUrl("fhir-repository:memory:test-repo", fhirContext). New implementations can be registered with a ServiceLoader. See IRepositoryLoader and InMemoryFhirRepositoryLoader for examples.  
|  [#7007](https://github.com/hapifhir/hapi-fhir/issues/7007) |  When processing `_include` and `_revinclude` directives on JPA server searches, the query will use a simplified SQL query which does not search for canonical URLs if the SearchParameter paths do not resolve to any elements which can potentially contain canonical references. Also, these searches now use a scrollable resultset for better efficiency.  
|  [#7156](https://github.com/hapifhir/hapi-fhir/issues/7156) |  When performing FHIR transaction operations which create a large number of resources, a CPU bottleneck in the transaction processor was resolved.  
|  [#6934](https://github.com/hapifhir/hapi-fhir/issues/6934) |  Subscription class API and logging has been improved. Also support for AutoClosable ChannelProducers has been added."  
|  [#6947](https://github.com/hapifhir/hapi-fhir/issues/6947) |  Added support for closeable producers in mdm. Also added producer suffixes to broker producer config for brokers that name publishers.  
|  [#6995](https://github.com/hapifhir/hapi-fhir/issues/6995) |  The schema initialization task run during migrations now runs the statements without a transaction. This allows some statements to run that would otherwise fail in a transaction.  
|  [#6997](https://github.com/hapifhir/hapi-fhir/issues/6997) |  Updated SpringMessagingMessageHandlerAdapter to handle receiving a GenericMessage (this can happen when receiving a message over JMS from outside the system when there is no message converter configured on the JMS Template.  
|  [#6271](https://github.com/hapifhir/hapi-fhir/issues/6271) |  context-value-type searches were returning empty bundles, even when there were hits. This has been fixed.  
|  [#6630](https://github.com/hapifhir/hapi-fhir/issues/6630) |  When reading a Binary resource from the server that contains a FHIR resource, while accepting the same FHIR content type, the server did stream the Binary content instead of returning the Binary itself, contrary to the FHIR specification. This is fixed now. Thanks to Quentin Ligier for the pull request!  
|  [#6900](https://github.com/hapifhir/hapi-fhir/issues/6900) |  If a resource was previously deleted, it was not possible to perform a FHIR transaction which restored the resource if it also contained references to other resources. Thanks to Fouad Sfarijlani for reporting the issue and providing reproduction steps, and thanks to Michal Sevcik for designing a fix!  
|  [#6937](https://github.com/hapifhir/hapi-fhir/issues/6937) |  The algorithm used to calculate geographic bounding boxes (e.g. for `Location?near=` queries) has been redesigned to ensure greater accuracy. The previous algorithm was not always accurate at large scales and could miss entries located exactly on the center point.  
|  [#6958](https://github.com/hapifhir/hapi-fhir/issues/6958) |  When attempting to patch a resource in a partitioned system with a registered `STORAGE_PARTITION_SELECTED` interceptor, an error would result. This has now been fixed.  
|  [#6965](https://github.com/hapifhir/hapi-fhir/issues/6965) |  The Patient/$match operation has been changed so it now respects the EID system definition in the MDM rules following the same algorithm as MDM resource matching: EID matching is checked first and if no matches are found, then field-based matching is used.  
|  [#6968](https://github.com/hapifhir/hapi-fhir/issues/6968) |  When validating a Message or Document bundle, the validator tries to fetch any references within resources found in the bundle to resources not found in the bundle. A bug prevented this check from working on non-R4 JPA systems. This has been fixed.  
|  [#6969](https://github.com/hapifhir/hapi-fhir/issues/6969) |  Fixed an issue where reference chaining with the _tag search parameter resulted in an error.  
|  [#6978](https://github.com/hapifhir/hapi-fhir/issues/6978) |  If the batch framework received a message for a non-existent work chunk (for example, when ingesting a replayed messages), it would result in an error and job retry. This has now been fixed.  
|  [#6987](https://github.com/hapifhir/hapi-fhir/issues/6987) |  Previously, operation $validate-code would only support Coding and error out when issued with url/code or codeableConcept. Those issues are fixed.  
|  [#6994](https://github.com/hapifhir/hapi-fhir/issues/6994) |  Fix incorrect behaviour of `ParameterUtil.unescape(String)`. The function would 'eat' multiple consecutive occurrences of backslash. Adding a test class to cover `ParameterUtil.unescape()` and `ParameterUtil.escape()`. Perviously it would have been impossible to search for an identifier with a value containing two consecutive backslashes such as: `abc\\def`. This search requires the following search parameter `identifier=abcd\\\\def`. The two consecutive escaped occurrences of `\` would have previously been inadvertently unescaped to just one occurrence.  
|  [#7037](https://github.com/hapifhir/hapi-fhir/issues/7037) |  Previously, the $hapi.fhir.replace-references operation returned a success response with an empty Bundle when the source-reference-id or target-reference-id parameter referred to a non-existent resource. Now, it returns a 404 ResourceNotFound response in these cases.  
|  [#7040](https://github.com/hapifhir/hapi-fhir/issues/7040) |  Due to a fix in Release 8.2, contained resources will no longer have their ids prepended with a '#'. This caused a regression where contained resources were referenced, not by an Id Reference, but by the actual full embedded resource, would no longer be able to be processed in a transaction. To fix this, the parser/serializer (which is responsible for creating these contained reference ids) will also create a contained resource id reference, which does contain the prepended '#', thereby allowing these transactions to work as before.  
|  [#7065](https://github.com/hapifhir/hapi-fhir/issues/7065) |  In Patient Id partitioning (PatientIdPartitionInterceptor), Group resources are now treated as if they are not part of the Patient compartment and will be created in the default partition in all cases. This avoids problems errors creating Groups in no Patient compartment, or many.  
|  [#7071](https://github.com/hapifhir/hapi-fhir/issues/7071) |  FhirPatch will now handle some error cases when the provided FhirPath is invalid (unmatching brackets, for instance).  
|  [#7088](https://github.com/hapifhir/hapi-fhir/issues/7088) |  When performing a JPA query using the `_tag`, `_profile`, and/or `_security` parameters, the search engine could include a redundant SQL JOIN in the emitted SQL statements. These extra JOINs caused a performance degradation in some cases.  
|  [#7088](https://github.com/hapifhir/hapi-fhir/issues/7088) |  A regression in HAPI FHIR 8.0.0 meant that the configured maximum export file sizes were not respected. This has been corrected.  
|  [#7095](https://github.com/hapifhir/hapi-fhir/issues/7095) |  When database partition mode was enabled and the cross-partition reference mode was set to ALLOWED_UNQUALIFIED, creating a resource with a forced ID that already existed for the same resource type in a different partition would overwrite the existing resource. This issue has now been fixed.  
|  [#7098](https://github.com/hapifhir/hapi-fhir/issues/7098) |  If the JPA schema migrator is run against a database that has already manually been initialized with the correct schema, the migrator incorrectly then tried to run all of the individual schema initialization tasks. This has been corrected.  
|  [#7125](https://github.com/hapifhir/hapi-fhir/issues/7125) |  Previously, conditional updates based on tokens were case-insensitive. As a result, a new resource would be created if the token value in the URL used a different case than the one in the request body, leading to incorrect search results when using token-based search parameters. This issue has now been resolved.  
|  [#7130](https://github.com/hapifhir/hapi-fhir/issues/7130) |  There is an API backward incompatibility issue with the hapi-fhir-storage-batch-2 library that may break some client applications. This has been fixed.  
|  [#7139](https://github.com/hapifhir/hapi-fhir/issues/7139) |  This change allows resources to be deleted using the Delete with Expunge operation when the property `enforceReferentialIntegrityOnDeleteDisableForPaths` is set to one or more FHIRPath expressions that link other resources to the target resources.  
#  0.1.5HAPI FHIR 8.2.1 (Fortification)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#change8.4.0-3661)
##  0.1.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-2)
**Released:** 2025-07-14
**Codename:** (Fortification)
##  0.1.5.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions-2)
##  0.1.5.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-2)
|  [#7040](https://github.com/hapifhir/hapi-fhir/issues/7040) |  Due to a fix in Release 8.2, contained resources will no longer have their ids prepended with a '#'. This caused a regression where contained resources were referenced, not by an Id Reference, but by the actual full embedded resource, would no longer be able to be processed in a transaction. To fix this, the parser/serializer (which is responsible for creating these contained reference ids) will also create a contained resource id reference, which does contain the prepended '#', thereby allowing these transactions to work as before.  
---|---|---  
|  [#7088](https://github.com/hapifhir/hapi-fhir/issues/7088) |  When performing a JPA query using the `_tag`, `_profile`, and/or `_security` parameters, the search engine could include a redundant SQL JOIN in the emitted SQL statements. These extra JOINs caused a performance degradation in some cases.  
|  [#7088](https://github.com/hapifhir/hapi-fhir/issues/7088) |  A regression in HAPI FHIR 8.0.0 meant that the configured maximum export file sizes were not respected. This has been corrected.  
#  0.1.6HAPI FHIR 8.2.0 (Fortification)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#change8.2.1-7040)
##  0.1.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-3)
**Released:** 2025-05-18
**Codename:** (Fortification)
##  0.1.6.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions-3)
## Breaking Changes
  * Support for Java 11 has been dropped. A minimum of Java 17 is now required for HAPI FHIR. Java 21 is also supported.
  * This also affects Android users. We now target Android Api Level 34.


## Contained Resources
When parsing or serializing resources, contained resources will no longer be given an ID starting with the `#` character, although this character is still used in references to that resource. For example, if a _Patient_ has a contained _Practitioner_ resource, the practitioner will have an ID such as `123`, and the `Patient.generalPractitioner` reference will be `#123`. In previous versions of HAPI FHIR, both of these values would be set to `#123` which was confusing and no longer validates correctly.
See [Contained Resources](https://hapifhir.io/hapi-fhir/docs/model/references.html#contained) for a more detailed example.
## The `SP_UPDATED` column in `HFJ_SPIDX_*` tables
The `SP_UPDATED` column is no longer used in the `HFJ_SPIDX_*` tables. Existing data in `SP_UPDATED` column can be safely removed manually after upgrading to version 8.2 to free up database storage space.
##  0.1.6.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-3)
|  [#5368](https://github.com/hapifhir/hapi-fhir/issues/5368) |  Several error messages reported by the `LenientErrorHandler` (the default parser error handler) did not include location information in their message. Thanks to Elliott Lavy for the contribution!  
---|---|---  
|  [#6621](https://github.com/hapifhir/hapi-fhir/issues/6621) |  A new table, `HFJ_SPIDX_IDENTITY`, has been introduced. This table stores unique hash identities along with the corresponding `sp_name` and `res_type` values from all `HFJ_SPIDX_xxx` tables. It is populated during read, write, or update operations on the `HFJ_SPIDX_xxx` tables. If the `StorageSettings#isIndexStorageOptimized()` setting is enabled, this table can be used to reconstruct `sp_name` and `res_type` values for hash identities stored in the `HFJ_SPIDX_xxx tables` for debugging purposes.  
|  [#6719](https://github.com/hapifhir/hapi-fhir/issues/6719) |  Added a troubleshooting log for request partition routing. It can be fetched by calling `Logs.getPartitionTroubleshootingLog()`  
|  [#6781](https://github.com/hapifhir/hapi-fhir/issues/6781) |  The CDS Hooks implementation now supports pagination when prefetching data for requests issued to the FHIR server for missing prefetch keys. If a prefetch request to a FHIR server returns a paginated bundle, the prefetch service now paginates through the bundle, and collects all the data into a single bundle before passing it on to the CDS Hooks service.  
|  [#6805](https://github.com/hapifhir/hapi-fhir/issues/6805) |  CDS Hooks now requires the `fhirServer` field to use HTTPS in requests when using [CDS Hooks version 2.0](https://cds-hooks.hl7.org/2.0/), as per the specification. The CDS Hooks version can be configured by providing a bean that returns a `CDSHooksVersion` enum value, introduced in this update. By default, the version is set to 1.1 to maintain compatibility with existing implementations.  
|  [#6810](https://github.com/hapifhir/hapi-fhir/issues/6810) |  The `STORAGE_PRESEARCH_REGISTERED` pointcut will now also be called before the internal FHIR searches being performed in order to resolve conditional create/update/etc URLs when processing FHIR transactions. Previously this pointcut was not called for these specific searches.  
|  [#6814](https://github.com/hapifhir/hapi-fhir/issues/6814) |  When parsing a JSON resource, the following fragment caused the parser to abort parsing with an unhelpful error message: `"extension": null`. We will now correctly detect this error and pass it to the parser error handler.  
|  [#6826](https://github.com/hapifhir/hapi-fhir/issues/6826) |  The `_offset` query parameter can now be used when constructing a history request via the IGenericClient API. Please see the [documentation](https://hapifhir.io/hapi-fhir/docs/client/generic_client.html#history-servertypeinstance) for example usage.  
|  [#6834](https://github.com/hapifhir/hapi-fhir/issues/6834) |  `RemoteTerminologyServiceValidationSupport` can now be configured with a customized `IRestfulClientFactory` which will be used to create the FHIR client for remote terminology requests.  
|  [#6836](https://github.com/hapifhir/hapi-fhir/issues/6836) |  CDS Hooks services can now specify how to handle prefetch failures using the `failureMode` property on the `CDSServicePrefetch` annotation. The failure mode can be set to `FAIL` (default), `OMIT` or `OPERATION_OUTCOME`. This applies in cases where an auto-prefetch request fails, or the CDS Client sends an OperationOutcome as a prefetch resource, which is allowed by the [CDS Hooks version 2.0](https://cds-hooks.hl7.org/2.0/) specification.  
|  [#6840](https://github.com/hapifhir/hapi-fhir/issues/6840) |  When invoking a FHIR transaction, a new optional header called `X-Transaction-Semantics` can be used to influence how the operation is processed. This header can request automatic retry on failure and other options. See [FHIR Transactions](https://smilecdr.com/docs/fhir_standard/transactions.html) for more information.  
|  [#6840](https://github.com/hapifhir/hapi-fhir/issues/6840) |  A new method called `toTransactionResponse` has been added to `BundleUtil`. This method parses a FHIR transaction/batch response bundle generated by HAPI FHIR and returns the parsed details.  
|  [#6853](https://github.com/hapifhir/hapi-fhir/issues/6853) |  A new kind of interceptor has been added called a 'Filter Hook Interceptor'. Filter hook interceptors allow implementers to run custom code around a supplied function's execution (similar to Java Servlet Filters). See the [Filter Hook Interceptors page](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html) for more details.  
|  [#6853](https://github.com/hapifhir/hapi-fhir/issues/6853) |  A `Map<String, Object>` called `userData` has been added to the `JobInstance` model class. User data that is added to the `JobInstance` via the `STORAGE_PRESTORAGE_BATCH_JOB_CREATE` pointcut will be serialized and persisted as a new attribute of the `Batch2JobInstanceEntity` called `myUserDataJson`. This data will be deserialized and available on the `JobInstance` parameter of the `BATCH2_CHUNK_PROCESS_FILTER` pointcut.  
|  [#6853](https://github.com/hapifhir/hapi-fhir/issues/6853) |  A new [filter hook pointcut](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html) named `BATCH2_CHUNK_PROCESS_FILTER` has been added, which will allow implementers to specify custom logic that will be executed around `WorkChunk` processing.  
|  [#6863](https://github.com/hapifhir/hapi-fhir/issues/6863) |  A new method has been added to the generic client (IGenericClient) which instructs the client to return the raw string response body to the caller, as opposed to trying to parse the response as a FHIR resource.  
|  [#6865](https://github.com/hapifhir/hapi-fhir/issues/6865) |  Added new NPM package management APIs to help manage duplicate resources by canonical URL across multiple packages. `IHapiPackageCacheManager` has two new methods: 
  * `List<NpmPackageAssetInfoJson> findPackageAssetInfoByUrl(FhirVersionEnum theVersion, String theCanonicalUrl)`: Shows details of the packages that are duplicated with url
  * `IBaseResource findPackageAsset(FindPackageAssetRequest theFindPackageAssetRequest)`: Allows viewing the specific resource JSON for that canonical URL, package ID and version to help determine which is the resource and package that should be kept.

  
|  [#6872](https://github.com/hapifhir/hapi-fhir/issues/6872) |  Added the IRepositoryFactory interface and HapiFhirRepository implementation from the Clinical Reasoning module to the base HAPI packages. This will allow for broader usage without having a dependency on the Clinical Reasoning module.  
|  [#6873](https://github.com/hapifhir/hapi-fhir/issues/6873) |  The server LoggingInterceptor now has a substitution variable `responseId` which contains the value of the `Location` header for write operations  
|  [#6873](https://github.com/hapifhir/hapi-fhir/issues/6873) |  A new built-in interceptor called `SubscriptionRulesInterceptor` has been added, which can enforce rules on newly created subscriptions, such as mandatory criteria patterns and ensuring that target URLs are reachable.  
|  [#6873](https://github.com/hapifhir/hapi-fhir/issues/6873) |  The Subscription registry has been made more resilient to failures when fetching Subscriptions on startup  
|  [#6818](https://github.com/hapifhir/hapi-fhir/issues/6818) |  When executing a FHIR transaction containing multiple conditional create operations, a series of SQL resource version lookups have been collapsed into a single query for better performance.  
|  [#6840](https://github.com/hapifhir/hapi-fhir/issues/6840) |  The Bulk Import ($import) operation has been reworked for much improved performance and functionality: 
  * The storage step now uses FHIR transactions to ingest chunks of data, with automatic retry and a batch failure mode. This should result in both better performance, and better job stability for large jobs. 
  * The job will now produce a report at the end of the process which details the outcome of the job. 
  * The HAPI FHIR CLI bulk-import job will now output this report at the end of execution. 

  
|  [#6870](https://github.com/hapifhir/hapi-fhir/issues/6870) |  Previously, the `VersionSpecificWorkerContextWrapper` was converting canonicalized `ValueSet` resources back to their original FHIR version every time codes were validated. Now the converted `ValueSet` is stored in the canonical `ValueSet`'s user data and used to avoid repeated conversions.  
|  [#6870](https://github.com/hapifhir/hapi-fhir/issues/6870) |  Previously, the `ValidateCodeKey` was using a reference to `ConceptValidationOptions` which was later mutated during validation causing cache misses. This has been corrected and now a copy of `ConceptValidationOptions` is used.  
|  [#6888](https://github.com/hapifhir/hapi-fhir/issues/6888) |  The `XmlUtil.encodeDocument(Node)` method contained an expensive TransformerFactory lookup which was performed on each invocation. This value is now cached for better performance.  
|  [#6658](https://github.com/hapifhir/hapi-fhir/issues/6658) |  Added `PessimisticLockException` to the list of exceptions that can be retried by the HapiTransactionService. H2 throws this exception when ensuring Primary Key uniqueness. Without a retry, concurrent transaction processing could result in deadlock while enforcing uniqueness on the `HFJ_RES_SEARCH_URL` table.  
|  [#6704](https://github.com/hapifhir/hapi-fhir/issues/6704) |  The JENA library used to provide RDF/Turtle encoding and parsing services has been upgraded from 4.9.0 to 5.3.0. Thanks to Dylan Krause for the contribution!  
|  [#6769](https://github.com/hapifhir/hapi-fhir/issues/6769) |  The org.hl7.fhir.core has been updated to the 6.5.15 version. This new version includes a number of bug fixes and improvements, as well as some changes in the validator API.  
|  [#6844](https://github.com/hapifhir/hapi-fhir/issues/6844) |  The Patient ID partitioning interceptor now has limited support for server-assigned ids when in UUID mode. This allows Synthea data to be ingested while running in Patient ID partitioning mode.  
|  [#6845](https://github.com/hapifhir/hapi-fhir/issues/6845) |  Recent versions of the HAPI FHIR corelib (containing model classes, fhirpath evaluator, validator, etc.) now enforce a rule that contained resources must not have a # character in their ID, so the # character is only used in the reference. In other words, when adding a contained resource to a containing resource, the contained resource should now have an ID set as follows: ```
Patient patient = new Patient(); patient.getContained().add(org); patient.setManagingOrganization(new Reference("#1")); ```
Existing code using the previous form of ID (e.g. #1) will continue to work, but will emit a warning and be automatically corrected by the parser. 
```
Copy  
|  [#6855](https://github.com/hapifhir/hapi-fhir/issues/6855) |  Subclasses of the `ConsentInterceptor` can now add functionality to pre-authorized a request based on `RequestDetails`. See the [Pre-Authorizing Requests section](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html#pre-authorizing-requests) for more details.  
|  [#6868](https://github.com/hapifhir/hapi-fhir/issues/6868) |  HAPI FHIR now targets Android API 34.  
|  [#6868](https://github.com/hapifhir/hapi-fhir/issues/6868) |  Support for Java 11 has been dropped. A minimum of Java 17 is now required for HAPI FHIR. Java 21 is also supported.  
|  [#7342](https://github.com/hapifhir/hapi-fhir/issues/7342) |  Fix concurrency issues in StructureDefinition snapshot generation by updating its elements collection in an atomic way.  
|  [#4129](https://github.com/hapifhir/hapi-fhir/issues/4129) |  When performing a version-aware update using the FHIR client, the `ETag` header did not specify a weak etag as required by the FHIR specification. Thanks to Berkant Karduman for the contribution!  
|  [#5834](https://github.com/hapifhir/hapi-fhir/issues/5834) |  Profiles of Device resources present in IGs would cause a failure during IG installation. This has now been fixed. Thanks to @jkiddo for the contribution!  
|  [#6564](https://github.com/hapifhir/hapi-fhir/issues/6564) |  There were many places where it was assumed the default partition id was null (when it is a configurable value). This change fixes many of them.  
|  [#6608](https://github.com/hapifhir/hapi-fhir/issues/6608) |  Previously, attempting to validate a resource using `$validate` with an invalid profile was not generating an error. This issue is fixed, and the validator will now throw an error if the profile does not exist.  
|  [#6652](https://github.com/hapifhir/hapi-fhir/issues/6652) |  A failure in the migrator has been corrected when applying the SQL migrator's drop primary key task on MSSQL. Thanks to Craig McClendon for the analysis and fix!  
|  [#6714](https://github.com/hapifhir/hapi-fhir/issues/6714) |  Previously, it was possible for a resource to change versions during a no-op update, due to a bug in tag selection. If a resource had tags, and was updated with no change, it was possible for the history version to be changed (e.g. from `Patient/1/_history/1` to `Patient/1/_history/2`) due to a bug in tag uniqueness detection. This has been fixed  
|  [#6716](https://github.com/hapifhir/hapi-fhir/issues/6716) |  Fixed an issue that was caused by batch jobs attempting to store error messages exceeding 500 characters. Now, the error message will be truncated to 500 characters if it exceeds that amount.  
|  [#6749](https://github.com/hapifhir/hapi-fhir/issues/6749) |  Previously, performing a search on a `Timing` datatype field with `DAY` precision (e.g. `ServiceRequest?occurence=ge2025-01-01`) would improperly miss results. This has now been fixed.  
|  [#6754](https://github.com/hapifhir/hapi-fhir/issues/6754) |  The IPS generator will no longer strip references to contained resources when assembling data to create an IPS document. Thanks to Xun Ma for the contribution!  
|  [#6767](https://github.com/hapifhir/hapi-fhir/issues/6767) |  Previously, when both Match URL cache and Partitioning were enabled, the Match URL cache would sometimes return the incorrect resource while resolving a conditional URL that matched to resources in multiple partitions. This has now been fixed.  
|  [#6799](https://github.com/hapifhir/hapi-fhir/issues/6799) |  Fixed an issue where bundles containing an operation outcome from a search could not be accessed without explicit read permissions given for the OperationOutcome resource. Now, if an OperationOutcome is returned as part of an otherwise authorized search or page query, the OperationOutcomes are implicitly permitted to be returned.  
|  [#6808](https://github.com/hapifhir/hapi-fhir/issues/6808) |  Previously, expanding a ValueSet could cause modifications to the underlying CodeSystem concepts. The issue was encountered when a `valueSet.compose.include.concept.display` differed from the referred `codeSystem.concept.display`. This has been fixed.  
|  [#6810](https://github.com/hapifhir/hapi-fhir/issues/6810) |  A bug in the snapshot generator prevented the automatic generation of StructureDefinition snapshots from differential profiles specifically for FHIR R5 StructureDefinitions, which could prevent successful validation when using these profiles against R5 resources. This has been corrected.  
|  [#6818](https://github.com/hapifhir/hapi-fhir/issues/6818) |  When performing a conditional create/update/etc in a FHIR transaction, if the conditional URL had parameters with a type other than `token`, the transaction processor could fail with a constraint error if the target already existed. This has been corrected. Thanks to Brian Lind for reporting!  
|  [#6821](https://github.com/hapifhir/hapi-fhir/issues/6821) |  Previously, placeholder IDs could fail to resolve when submitting a transaction bundle containing a resource with multiple references if the first reference used a placeholder ID and a subsequent reference used a literal reference. This has now been fixed.  
|  [#6841](https://github.com/hapifhir/hapi-fhir/issues/6841) |  The bulk export status reported by `$poll-export-status` now correctly shows the start-time, not the end-time. This avoids missing changes or updates in subsequent $export calls which use this as the `_since` parameter.  
|  [#6847](https://github.com/hapifhir/hapi-fhir/issues/6847) |  Previously, when performing chained searches, the exact qualifier was not honoured if the `Index Contained Resources' feature was enabled. This has now been fixed.  
|  [#6849](https://github.com/hapifhir/hapi-fhir/issues/6849) |  Previously, performing a FHIR chained search with the `_count` parameter could cause the search to time out when several specific conditions were met: 
  * the result contains multiple pages
  * the result contains a mix of resources that have a single reference and have multiple references
  * the total number of resource references exceeds internal pre-fetch thresholds

This has now been fixed. Please see the [Github issue](https://github.com/hapifhir/hapi-fhir/issues/6849) for more details on the precise conditions that could cause timeouts.  
|  [#6855](https://github.com/hapifhir/hapi-fhir/issues/6855) |  The `IConsentService#canSeeResource()` method is now executed in the `STORAGE_BULK_EXPORT_RESOURCE_INCLUSION` pointcut of the `ConsentInterceptor`. This allows Consent Services to be able to prevent resources from being exported by bulk export.  
|  [#6857](https://github.com/hapifhir/hapi-fhir/issues/6857) |  When targeting an Oracle Database, a migration task modifying column size omitted to specify the character storage size (byte/char). As a result, SQL errors could occur when inserting a string of the column maximum set size if it contained characters where the code point (character encoding) requires 2 bytes. This issue is fixed.  
|  [#6862](https://github.com/hapifhir/hapi-fhir/issues/6862) |  Modifier extensions with child modifier extensions were incorrectly serialized by the JSON Parser using the element name `modifierExtension`. This was incorrect (the child element should always be called `extension` per the [spec](https://www.hl7.org/fhir/R4B/extensibility.html#modifierExtension)). Thanks to Vytis Valentinavičius for the contribution!  
|  [#6864](https://github.com/hapifhir/hapi-fhir/issues/6864) |  Fixed an issue where performing a `$delete-expunge` operation with cascading deletes enabled would sometimes be unsuccessful due to a failure in finding large amounts of child resources.  
|  [#6882](https://github.com/hapifhir/hapi-fhir/issues/6882) |  Previously, submitting a resource for validation with an unknown CodeSystem would always result in a validation error, regardless of the defined binding strength. This has now been fixed such that, given an unknown CodeSystem: 
  * A `required` binding strength will still result in a validation error.
  * An `extensible` binding strength will now result in a validation warning.
  * A `preferred` binding strength will now result in a validation warning.
  * An `example` binding strength will result in no issues.

  
|  [#6885](https://github.com/hapifhir/hapi-fhir/issues/6885) |  When querying for an NPM resource that is not found, the error message returned was unclear. This has been fixed, and a proper HAPI error code is returned, along with an appropriate message.  
|  [#6892](https://github.com/hapifhir/hapi-fhir/issues/6892) |  Previously, attempting to PATCH an enumerated field of a resource's composite member (e.g. `Encounter.location.status`) could throw an exception based on the PATCH Parameters construction. This issue has been fixed.  
|  [#6907](https://github.com/hapifhir/hapi-fhir/issues/6907) |  Previously a request with only the `_source` parameter was not considering partitioning. This has been fixed.  
|  [#6911](https://github.com/hapifhir/hapi-fhir/issues/6911) |  MDM was previously only able to handle up to 30 sequential matching rules before throwing an exception. This value has been increased to 64.  
|  [#6939](https://github.com/hapifhir/hapi-fhir/issues/6939) |  Previously, the Subscription module could fail to boot due to a race condition happening during its initialisation sequence. The issue has been fixed.  
|  [#6942](https://github.com/hapifhir/hapi-fhir/issues/6942) |  Previously, the `FhirTerser.getValues()` method would throw a ClassCastException if the FHIRPath expression traversed a choice node and the resource instance being introspected contained a primitive data type at that node. This has been fixed.  
|  [#6945](https://github.com/hapifhir/hapi-fhir/issues/6945) |  Added index for `ResourceHistoryTable.sourceUri`, lost when moving the source column from HFJ_RES_VER_PROV to HFJ_RES_VER.  
|  [#6621](https://github.com/hapifhir/hapi-fhir/issues/6621) |  The `SP_UPDATED` column is no longer used in the `HFJ_SPIDX_*` tables. Existing data in the `SP_UPDATED` column can be safely removed manually to reclaim database storage space.  
|  [#6746](https://github.com/hapifhir/hapi-fhir/issues/6746) |  A Vagrant-based development environment has been provided in the HAPI FHIR git repository for many years. It has not been maintained, and has therefore caused a number of CVE scanner failures due to outdated dependencies. At this point it is fairly straightforward to develop HAPI FHIR without any external dependencies, so this is being removed.  
#  0.1.7HAPI FHIR 8.0.0 (Transfiguration)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#change8.2.0-5368)
##  0.1.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-4)
**Released:** 2025-02-17
**Codename:** (Transfiguration)
##  0.1.7.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions-4)
## Resource Provenance
The JPA server stores values for the field `Resource.meta.source` in dedicated columns in its database so that they can be indexes and searched for as needed, using the `_source` Search Parameter.
Prior to HAPI FHIR 6.8.0 (and Smile CDR 2023.08.R01), these values were stored in a dedicated table called `HFJ_RES_VER_PROV`. Beginning in HAPI FHIR 6.8.0 (Smile CDR 2023.08.R01), two new columns were added to the `HFJ_RES_VER` table which store the same data and make it available for searches.
As of HAPI FHIR 8.0.0, the legacy table is no longer searched by default. If you do not have Resource.meta.source data stored in HAPI FHIR that was last created/updated prior to version 6.8.0, this change will not affect you and no action needs to be taken.
If you do have such data, you should follow the following steps:
  * Enable the JpaStorageSettings setting `setAccessMetaSourceInformationFromProvenanceTable(true)` to configure the server to continue using the legacy table.
  * Perform a server resource reindex by invoking the [$reindex Operation (server)](https://smilecdr.com/docs/fhir_repository/search_parameter_reindexing.html#reindex-server) with the `optimizeStorage` parameter set to `ALL_VERSIONS`.
  * When this reindex operation has successfully completed, the setting above can be disabled. Disabling this setting avoids an extra database round-trip when loading data, so this change will have a positive performance impact on your server.


## Device membership in Patient Compartment
As of 8.0.0, versions of FHIR below R5 now consider the `Device` resource's `patient` Search Parameter to be in the Patient Compartment. The following features are affected:
  * Patient Search with `_revInclude=*`
  * Patient instance-level `$everything` operation
  * Patient type-level `$everything` operation
  * Automatic Search Narrowing
  * Bulk Export


Previously, there were various shims in the code that permitted similar behaviour in these features. Those shims have been removed. The only remaining component is [Advanced Compartment Authorization](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization), which can still be used to add other Search Parameters into a given compartment.
## Fulltext Search with _lastUpdated Filter
Fulltext searches have been updated to support `_lastUpdated` search parameter. If you are using Advanced Hibernate Search indexing and wish to use the `_lastUpdated` search parameetr with this feature, a full reindex of your repository is required.
##  0.1.7.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-4)
|  |  The version of a few dependencies have been bumped to more recent versions (dependent HAPI modules listed in brackets): 
  * org.hl7.fhir.core (Base): 6.3.25 -> 6.4.0
  * H2 (JPA): 2.2.224 -> 2.3.232
  * spring-boot-bom (Base): 3.2.6 -> 3.3.5
  * spring-retry (Base): 2.0.6 -> 2.0.10
  * spring-data-bom (Base): 2023.1.6 -> 2024.0.5
  * spring (Base): 6.1.8 -> 6.1.14
  * slf4j-api (Base): 2.0.13 -> 2.0.16
  * log4j-to-slf4j (Base): 2.19.0 -> 2.24.1
  * jackson (Base): 2.17.1 -> 2.18.1
  * jackson-databind (Base): 2.17.1 -> 2.18.1
  * opentelemetry-instrumentation-bom (Base): 2.8.0 -> 2.9.0
  * resteasy (Base): 6.2.9.Final -> 6.2.10.Final
  * logback-classic (Base): 1.4.14 -> 1.5.12
  * owasp-java-html-sanitizer (Base): 20211018.2 -> 20240325.1
  * graphql-java (Base): 21.5 -> 22.3
  * simple-java-mail (Base): 8.11.2 -> 8.12.2
  * okio-jvm (Base): 3.4.0 -> 3.9.1
  * commons-cli (Base): 1.5.0 -> 1.9.0
  * org.jetbrains.annotations (Base): 23.0.0 -> 26.0.1
  * xmlunit-core (Base): 2.4.0 -> 2.10.0
  * jboss-logging (Tinder): 3.4.2.Final -> 3.6.1.Final
  * springdoc-openapi-starter-webmvc-ui (Server): 2.2.0 -> 2.6.0
  * ace-builds (Server): 1.22.0 -> 1.36.3
  * bootstrap (Server): 4.5.2 -> 4.6.2
  * Eonasdan-bootstrap-datetimepicker (Server): 4.17.47 -> 4.17.49
  * font-awesome (Server): 5.8.2 -> 5.15.4
  * swagger-ui (Server): 4.1.3 -> 4.19.1
  * httpcore (Client): 4.4.13 -> 4.4.16
  * httpclient (Client): 4.5.13 -> 4.5.14
  * flyway (JPA): 9.4.0 -> 10.20.1
  * hibernate (JPA): 6.4.1.Final -> 6.6.2.Final
  * hibernate-search (JPA): 7.0.0.Final -> 7.2.1.Final
  * elastic-apm (JPA): 1.44.0 -> 1.52.0
  * elastic-search (JPA): 8.14.3 -> 8.15.3
  * lucene (JPA): 9.8.0 -> 9.11.1
  * postgresql (JPA): 42.7.3 -> 42.7.4
  * mysql-connector-j (JPA): 8.2.0 -> 9.1.0
  * ojdbc11 (JPA): 23.3.0.23.09 -> 23.6.0.24.10

  
---|---|---  
|  [#6107](https://github.com/hapifhir/hapi-fhir/issues/6107) |  A new extension has been created for use on SearchParameter resources in the JPA server. This extension causes a SearchParameter to be indexed, but to not be available for use in searches. This can be set when a new SP is created in order to prevent it from being used before an index has been completed. See [Introducing Search Parameters on Existing Data](https://smilecdr.com/docs/fhir_standard/fhir_search_custom_search_parameters.html) for more information.  
|  [#6398](https://github.com/hapifhir/hapi-fhir/issues/6398) |  The NPM package search module has been enhanced to support searching by the package author and the package version attributes.  
|  [#6426](https://github.com/hapifhir/hapi-fhir/issues/6426) |  Previously, it was not possible to enable Hibernate Search but not use it for fulltext indexing. This meant that you could not enable HS-based terminology services without also enabling fulltext indexing of all resources. A new setting has been added to the JpaStorageSettings bean called `HibernateSearchIndexFullText` which controls whether HS will be used for fulltext indexing. The existing property `AdvancedHSearchIndexing` has also been deprecated and a new equivalent (but better named) property called `HibernateSearchIndexSearchParams`.  
|  [#6464](https://github.com/hapifhir/hapi-fhir/issues/6464) |  A new experimental interceptor called the MdmReadVirtualizationInterceptor has been added. This interceptor rewrites results when querying an MDM-enabled JPA server in order to always include linked resources and rerwrites query results to link to the MDM golden resource. This interceptor is still being developed and should be used with caution.  
|  [#6495](https://github.com/hapifhir/hapi-fhir/issues/6495) |  A new MdmSettings mode field was added (default value `MATCH_AND_LINK`). When the MdmSettings mode is set to `MATCH_ONLY` mode, then MDM operations are disabled and no MDM processing occurs. This is useful if, for example, you want to use the Patient/$match operation without having the overhead of creating links and golden resources.  
|  [#6496](https://github.com/hapifhir/hapi-fhir/issues/6496) |  Added support for overriding message broker channel settings for MDM message processing.  
|  [#6511](https://github.com/hapifhir/hapi-fhir/issues/6511) |  Interceptors can be defined against the registry on the RestfulServer, or on the registry in the JPA repository. Because these are separate registries, the order() attribute on the Hook annotation isn't correctly processed today across the two registries. The CompositeInterceptorRegistry has been reworked so ordering will be respected across both registries.  
|  [#6520](https://github.com/hapifhir/hapi-fhir/issues/6520) |  A new FHIR client implementation based on Apache HttpClient 5.x has been added to HAPI FHIR. This implementation is optional for now, with the default remaining to use HttpClient 4.x, but this will likely become the default (and only supported version of the HttpClient library) in the future. Thanks to Ibrahim Tallouzi for the contribution!  
|  [#6534](https://github.com/hapifhir/hapi-fhir/issues/6534) |  The JpaPersistedValidationSupport module which is used to fetch conformance resources from the JPA repository for validation purposes can now support versioned URLs. Thanks to Mangala Ekanayake for the contribution!  
|  |  The `patient` search parameter for the `Device` resource has been added to the Patient Compartment for the purposes of:
  * AuthorizationInterceptor - SearchNarrowingInterceptor - $everything Operation - Patient/Group Bulk Export This means that a search for $everything on a patient will return devices associated to this patient. This used to be possible via the [Advanced Compartment Authorization](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html#advanced-compartment-authorization), but that would only apply to authorization, and not these other use cases. This solution adds this Search Parameter to all places where compartment membership would be checked.

  
|  [#6550](https://github.com/hapifhir/hapi-fhir/issues/6550) |  Upgrade the Clinical Reasoning module to the latest release of 3.15.0. CMPI now supports the delete operation  
|  [#6550](https://github.com/hapifhir/hapi-fhir/issues/6550) |  Upgrade the Clinical Reasoning module to the latest release of 3.15.0. Please review associated ticket for detailed list of changes which including changes to error handling, measure scoring and support for Organization subjects.  
|  [#6550](https://github.com/hapifhir/hapi-fhir/issues/6550) |  Upgraded the Clinical Reasoning module to the latest release of 3.15.0. Many caregaps request errors that previously resulted in HTTP status code of 500 now result in 400 instead.  
|  [#6560](https://github.com/hapifhir/hapi-fhir/issues/6560) |  Added the ability to retrieve narrative generation templates from a code system and code that is defined in the input resource's `meta.tag` property. For more information, see [documentation](https://hapifhir.io/hapi-fhir/docs/model/narrative_generation.html)."  
|  [#6580](https://github.com/hapifhir/hapi-fhir/issues/6580) |  A new `RESULT` column has been added to the database migration table to record the migration execution result. values are `NOT_APPLIED_SKIPPED` (either skipped via the `skip-versions` flag or if the migration task was stubbed), `NOT_APPLIED_NOT_FOR_THIS_DATABASE` (does not apply to that database), `NOT_APPLIED_PRECONDITION_NOT_MET` (not run based on a SQL script outcome), `NOT_APPLIED_ALLOWED_FAILURE` (the migration failed, but it is permitted to fail), `APPLIED`.  
|  [#6587](https://github.com/hapifhir/hapi-fhir/issues/6587) |  Enhanced the IPS vital signs narrative template to include code and value information for all entries in the `Observation.component` property.  
|  [#6600](https://github.com/hapifhir/hapi-fhir/issues/6600) |  When submitting a FHIR transaction, we previously did not throw an error if a resource was submitted with a resource type in the URL that did not match the actual resource type (e.g. an entry containing a Patient, with the URL `Observation/123`). This will now correctly throw an error.  
|  [#6629](https://github.com/hapifhir/hapi-fhir/issues/6629) |  Bump Clinical Reasoning to latest 3.17 versioned release, remove temporary CQL dependencies from pom  
|  [#6638](https://github.com/hapifhir/hapi-fhir/issues/6638) |  The following pointcuts for CDS Hooks Prefetch Requests have been added: `CDS_HOOK_PREFETCH_REQUEST`, `CDS_HOOK_PREFETCH_RESPONSE`, and `CDS_HOOK_PREFETCH_FAILED`.  
|  [#6644](https://github.com/hapifhir/hapi-fhir/issues/6644) |  By default, referential integrity is enforced on deletes. This change introduces support for an exceptional case such that deletion of a given resource will not be blocked if all references to that resource are versioned. If there is at least one unversioned reference to the resource, deletion will still be blocked.  
|  [#6887](https://github.com/hapifhir/hapi-fhir/issues/6887) |  A new partition interceptor that can be used to identify partitions based on a specific request header called `X-Request-Partition-IDs` has been added. The header value is expected to be a comma-separated list of partition ids. Also, a mechanism was introduced for overriding this header in a transaction bundle for individual request entries if needed. This allows writing different transaction entries to different partitions in the same transaction.  
|  [#6224](https://github.com/hapifhir/hapi-fhir/issues/6224) |  The JPA server will no longer use a separate thread and database connection to resolve tag definitions. This should improve performance in some cases, and resolves compatibility issues for some environments. Thanks to Ibrahim (Trifork A/S) for the pull request!  
|  [#6395](https://github.com/hapifhir/hapi-fhir/issues/6395) |  A new configuration option has been added to `SubsciptionSubmitterConfig` which causes Subscription resources to be submitted to the processing queue synchronously instead of asynchronously as all other resources are. This is useful for cases where subscriptions need to be activated quickly. Thanks to Michal Sevcik for the contribution!  
|  [#6409](https://github.com/hapifhir/hapi-fhir/issues/6409) |  When searching in versioned tag mode, the JPA server now avoids a redundant lookup of the un-versioned tags, avoiding an extra unnecessary database query in some cases.  
|  [#6409](https://github.com/hapifhir/hapi-fhir/issues/6409) |  The JPA server will no longer use the HFJ_RES_VER_PROV table to store and index values from the `Resource.meta.source` element. Beginning in HAPI FHIR 6.8.0 (and Smile CDR 2023.08.R01), a new pair of columns have been used to store data for this element, so this change only affects data which was stored in HAPI FHIR prior to version 6.8.0 (released August 2023). If you have FHIR resources which were stored in a JPA server prior to this version, and you use the Resource.meta.source element and/or the `_source` search parameter, you should perform a complete reindex of your server to ensure that data is not lost. See the upgrade notes for more information.  
|  [#6460](https://github.com/hapifhir/hapi-fhir/issues/6460) |  The JPA server FHIR transaction processor will now pre-fetch the target resource state for references to resources that don't also appear in the transaction bundle. This means that if you process a large FHIR transaction containing many references to other resources in the repository that are not also being updated in the same transaction, you should see a very significant improvement in performance.  
|  [#6460](https://github.com/hapifhir/hapi-fhir/issues/6460) |  The JPA server FHIR transaction processor will now more aggressively cache resource IDs for previously seen resources, reducing the number of database reads required when processing transactions. This should provide a noticeable improvement in performance when processing transactions which update pre-existing resources.  
|  [#6469](https://github.com/hapifhir/hapi-fhir/issues/6469) |  Searching for a large number of resources can use a lot of memory, due to the nature of deduplication of results in memory. We will instead push this responsibility to the db to reduce this overhead.  
|  [#6478](https://github.com/hapifhir/hapi-fhir/issues/6478) |  Transactions with multiple saved search urls will have the saved search urls deleted in a batch, instead of 1 at a time. This is a minor performance update.  
|  [#6508](https://github.com/hapifhir/hapi-fhir/issues/6508) |  The ValidationSupportChain module has been rewritten to improve validator performance. This change: * Adds new caching capabilities to ValidationSupportChain. This is an improvement over the previous separate caching module because the chain can now remember which entries in the cache responded affirmative to `isValueSetSupported()` and will therefore be more efficient about trying entries in the chain. It also makes debugging much less confusing as there is less recursion and the caches don't use loadingCache. * Importantly, the caching in ValidationSupportChain caches negative lookups (i.e. items that could not be found by URL) as well as positive lookups. This is a change from the historical caching behaviour. * Changes ValidationSupportChain to never expire StructureDefinition entries in the cache, which is needed because the validator makes assumptions about structuredefinitions never changing. Fixes #6424. * Modifies `VersionSpecificWorkerContextWrapper` so that it doesn't use a separate cache and instead relies on the caching provided by ValidationSupportChain. This class previously used a cache because it converts arbitrary versions of FHIR StructureDefinitions into the canonical version required by the validator (R5), but these converted versions are now stored in the userdata map of objects returned by and cached by ValidationSupportChain. This makes the caching more predictable since there is only one cache to track. * Adds OpenTelemetry support to ValidationSupportChain, with metrics for tracking the cache size. * Deprecates CachingValidationSupport since caching is now provided by ValidationSupportChain. CachingValidationSupport is now just a passthrough and should be removed from applications. It will be removed from the library in a future release. * Removes ConceptMap caching from TermReachSvcImpl, as this caching is both redundant and inefficient as it operates within a database transaction. These changes result in very significant performance improvements when performing validation in the JPA server. Throughput improvements of 1000% have been recorded in benchmarking use cases involving large profiles and remote terminology services enabled. Many other validation use cases should see significant improvements as well.  
|  [#6522](https://github.com/hapifhir/hapi-fhir/issues/6522) |  Several memory caches in various parts of the JPA server have been removed in an effort to consolidate caching in this system to two places: The MemoryCacheService, and ValidationSupportChain. This should make management of the system easier.  
|  [#6582](https://github.com/hapifhir/hapi-fhir/issues/6582) |  Under heavy load, a foreign key constraint in the Tag Definition table (used for Tags, Security Labels, and Profile Definitions) can cause serious slowdowns when writing large numbers of resources (particularly if many resources contain the same tags/labels, or if the resources are being written individually or in smaller batches). This has been corrected. Also, a foreign key constraint on the Resource Link table has been dropped. This will significantly improve performance when writing resource collections with many links to resources not also in the same Bundle.  
|  [#6589](https://github.com/hapifhir/hapi-fhir/issues/6589) |  When performing data loading into a JPA repository using FHIR transactions with Mass Ingestion Mode enabled, the prefetch routine has been optimized to avoid loading the current resource body/contents, since these are not actually needed in Mass Ingestion mode. This avoids a redundant select statement being issued for each transaction and should improve performance.  
|  [#6460](https://github.com/hapifhir/hapi-fhir/issues/6460) |  The HFJ_RES_LINK table with no longer store the `PARTITION_DATE` value for the indexed link target resource, as this was an unused feature which has been removed as a part of a larger performance optimization.  
|  [#6460](https://github.com/hapifhir/hapi-fhir/issues/6460) |  When performing a FHIR Transaction which deletes and then updates (or otherwise un-deletes) the same resource within a single transaction, the delete was previously not stored as a distinct version (meaning that the resource version was only incremented once, and no delete was actually stored in the resource history. This has been changed so that deletes will always appear as a distinct entry in the resource history.  
|  [#6460](https://github.com/hapifhir/hapi-fhir/issues/6460) |  If deletes are disabled in the JPA server, it is no longer possible to un-delete a resource (i.e. update a previously deleted resource to make it non-deleted).  
|  [#6689](https://github.com/hapifhir/hapi-fhir/issues/6689) |  $hapi.fhir.replace-references operation has been changed to not replace versioned references  
|  [#6258](https://github.com/hapifhir/hapi-fhir/issues/6258) |  The AuthorizationInterceptor handling for operations has been improved so that operation rules now directly test the contents of response Bundle or Parameters objects returned by the operation when configure to require explicit response authorization. This fixes a regression in 7.4.0 where operation responses could sometimes be denied even if appropriate permissions were granted to view resources in a response bundle. Thanks to Gijsbert van den Brink for reporting the issue with a sample test!  
|  [#6404](https://github.com/hapifhir/hapi-fhir/issues/6404) |  Searches using fulltext search that combined `_lastUpdated` query parameter with any other (supported) fulltext query parameter would find no matches, even if matches existed. This has been corrected.  
|  [#6407](https://github.com/hapifhir/hapi-fhir/issues/6407) |  Corrected IHE BALP AuditEvent generation, so that it records one Audit Event per resource owner. Thanks to Jens Villadsen (@jkiddo) for the contribution!  
|  [#6409](https://github.com/hapifhir/hapi-fhir/issues/6409) |  When performing a `_history` query using the `_at` parameter, the time value is now converted to a zoned-date before being passed to the database. This should avoid conflicts around date changes on some databases.  
|  [#6475](https://github.com/hapifhir/hapi-fhir/issues/6475) |  Previously, submitting a transaction bundle containing a conditional delete, a conditional create, and a resource which relied on this conditional create as a reference would lead to excessive Hibernate warnings in the logs. This has been fixed.  
|  [#6480](https://github.com/hapifhir/hapi-fhir/issues/6480) |  Updated front-end bootstrap dependency due to security vulnerabilities.  
|  [#6502](https://github.com/hapifhir/hapi-fhir/issues/6502) |  Support ReferenceParam in addition to UriParam for `_profile` in queries using the SearchParameterMap to match the change in the specification from DSTU3 to R4.  
|  [#6519](https://github.com/hapifhir/hapi-fhir/issues/6519) |  Previously, when posting a transaction bundle with versioned references, the server would strip versioned references form the resource even when configured not to do so. This has now been fixed.  
|  [#6539](https://github.com/hapifhir/hapi-fhir/issues/6539) |  Previously, deleting an MDM managed Patient could result in errors under certain conditions. Particularly, if the related Golden record has no other resources linked to it (ie, this is the final linked resource); there exists other resources referencing the to-be-deleted resource; and the `cascade delete` option is enabled (to clean up related resources). This has now been fixed.  
|  [#6574](https://github.com/hapifhir/hapi-fhir/issues/6574) |  The raw json of parsed resources will be kept in the UserData (key: `RAW_JSON`) of the resource itself. This is to allow consistency in handling validation downstream, since otherwise the FhirParser is far more lenient about what it can parse than $validate is for what it accepts.  
|  [#6578](https://github.com/hapifhir/hapi-fhir/issues/6578) |  When starting up the JPA server under heavy load, the validation support cache could perform a large number of identical parallel queries. A synchronization guard has been placed around the cache loader to avoid this.  
|  [#6578](https://github.com/hapifhir/hapi-fhir/issues/6578) |  The search parameter picker in the Testpage Overlay module has been adjusted to correct several display issues which appeared after the upgrade from Bootstrap 4 to Bootstrap 5.  
|  [#6583](https://github.com/hapifhir/hapi-fhir/issues/6583) |  When processing a batch2 job under heavy load, a race condition meant that the final reducer step would occasionally fail if it started immediately following a batch2 maintenance task.  
|  [#6599](https://github.com/hapifhir/hapi-fhir/issues/6599) |  Previously, attempting to restart the Storage module would result in a `NullPointerException` when partition selection mode was set to `PATIENT_ID`, mass ingestion was enabled, and at least one Search Parameter was disabled. This has now been fixed.  
|  [#6603](https://github.com/hapifhir/hapi-fhir/issues/6603) |  Previously, searches where processing was optimised with a combo index search parameters would skip searching the optimised indexes if a date query string was prefixed with 'eq'. Since date=2025-01-01 and date=eq2025-01-01 are equivalent search queries, we have harmonized the search behavior allowing optimised indexes inspection when the prefix is present. This issue is fixed.  
|  [#6615](https://github.com/hapifhir/hapi-fhir/issues/6615) |  SearchParameter validation was not being skipped on updates, even if requested. This has been fixed  
|  [#6618](https://github.com/hapifhir/hapi-fhir/issues/6618) |  Previously, deleting the final MDM managed resource on a partition would not result in the linked Golden Resource being deleted as well. This has now been resolved.  
|  [#6632](https://github.com/hapifhir/hapi-fhir/issues/6632) |  A race condition in the SubscriptionRegistry could cause an occasional deadlock during shutdown.  
|  [#6656](https://github.com/hapifhir/hapi-fhir/issues/6656) |  Previously, FHIR patch 'replace' would fail when trying to replace a sub-element of a high cardinality element using the the FHIR patch syntax. This has been fixed.  
|  [#6662](https://github.com/hapifhir/hapi-fhir/issues/6662) |  Fixed remote terminology lookup results showing boolean properties as strings.  
|  [#6673](https://github.com/hapifhir/hapi-fhir/issues/6673) |  When attempting to search for resources while both `AdvancedHSearchIndexing` and `StoreResourcesInHibernateSearchIndex` are enabled, returned lists could sometimes contain null entries. This has been fixed.  
|  [#6686](https://github.com/hapifhir/hapi-fhir/issues/6686) |  With partitioning selection mode set to 'PATIENT_ID', attempting to create a cross-partition subscription would fail if the default partition ID was assigned a value different than the default value(null). This issue is fixed.  
|  [#6692](https://github.com/hapifhir/hapi-fhir/issues/6692) |  Previously, when the in-memory matcher was used to match resources with a `_security` label filter and a `:not` operator (i.e. `_security:not=http://terminology.hl7.org/CodeSystem/v3-ActCode|NODSCLCD`), resources with no security labels at all were not matched. This has been fixed.  
|  [#6697](https://github.com/hapifhir/hapi-fhir/issues/6697) |  Previously, operation $apply-codesystem-delta-add issued with Hibernate Search enabled and default search params option turned off resulted in an invalid sort specification error. This has been fixed.  
|  [#6700](https://github.com/hapifhir/hapi-fhir/issues/6700) |  Previously if a non-null default partition ID was selected when partitioning is enabled, the subscription matcher would fail to find cross-partition subscriptions, causing subscriptions to appear to not be working. This has been corrected.  
|  [#6711](https://github.com/hapifhir/hapi-fhir/issues/6711) |  Introduction of Database Partitioning Mode temporarily introduced a regression in the Patient `$everything` operation on partitioned servers, in which duplicates would be returned in the bundle. This has been corrected.  
|  [#6747](https://github.com/hapifhir/hapi-fhir/issues/6747) |  With partitioning selection mode set to 'REQUEST_TENANT', attempting to create a cross-partition subscription would fail if the default partition ID was assigned a value different than the default value(null). This issue is fixed.  
|  [#6512](https://github.com/hapifhir/hapi-fhir/issues/6512) |  The methods on FhirVersionEnum which produces a FhirContext (newContext() ,and newContextCached()) have been deprecated, and will be removed.  
#  0.1.8HAPI FHIR 7.8.0 (Transfiguration)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#change8.0.0-0)
##  0.1.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-5)
**Released:** 2025-02-17
**Codename:** (Transfiguration)
##  0.1.8.2Upgrade Instructions[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#upgrade-instructions-5)
This was an interim release which was never made public, as it was decided that this release would have a major bump, to 8.0.0
##  0.1.8.3Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-5)
#  0.1.9HAPI FHIR 6.10.5 (Prerelease)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#hapi-fhir-6105-prerelease)
##  0.1.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-6)
Note: This version of HAPI FHIR is a SNAPSHOT (prerelease), meaning that it has not yet been released, but all changes and fixes listed here are available to try out as [Snapshot Builds](https://hapifhir.io/hapi-fhir/docs/getting_started/downloading_and_importing.html#snapshot).
##  0.1.9.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-6)
|  [#5589](https://github.com/hapifhir/hapi-fhir/issues/5589) |  When encoding resources using the RDF parser, placeholder IDs (i.e. resource IDs starting with `urn:`) were not omitted as they are in the XML and JSON parsers. This has been corrected.  
---|---|---  
|  [#5589](https://github.com/hapifhir/hapi-fhir/issues/5589) |  When encoding a Bundle, if resources in bundle entries had a value in `Bundle.entry.fullUrl` but no value in `Bundle.entry.resource.id`, the parser sometimes incorrectly moved these resources to be contained within other resources when serializing the bundle. This has been corrected.  
|  [#5623](https://github.com/hapifhir/hapi-fhir/issues/5623) |  Previously, searches that used more than one chained `Bundle` `SearchParameter` (i.e. `Composition`) were only adding one condition to the underlying SQL query which resulted in incorrect search results. This has been fixed.  
#  0.1.10HAPI FHIR 5.5.0 (Prerelease)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#change6.10.5-5589)
##  0.1.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#release-information-7)
Note: This version of HAPI FHIR is a SNAPSHOT (prerelease), meaning that it has not yet been released, but all changes and fixes listed here are available to try out as [Snapshot Builds](https://hapifhir.io/hapi-fhir/docs/getting_started/downloading_and_importing.html#snapshot).
##  0.1.10.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog.html#changes-7)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Spring (JPA): 5.3.6 -> 5.3.7
  * Spring Boot (JPA Starter): 2.4.4 -> 2.5.0
  * Jetty (CLI): 9.4.39.v20210325 -> 9.4.42.v20210604

  
---|---|---  
|  [#2509](https://github.com/hapifhir/hapi-fhir/issues/2509) |  Pagination returned incorrect offset and count in the previous link of the last page when total element count was one more than multiple of page size. Problem is now fixed  
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
|  [#2721](https://github.com/hapifhir/hapi-fhir/issues/2721) |  Support for LOINC 2.70 has been added.  
|  [#2828](https://github.com/hapifhir/hapi-fhir/issues/2828) |  PatientIdPartitionInterceptor now supports conditional creates of resources where the resource is in the patient compartment but the conditional URL does not contain a patietn reference.  
|  [#2735](https://github.com/hapifhir/hapi-fhir/issues/2735) |  The $evaluate-measure now works on a partitioned server.  
|  [#2743](https://github.com/hapifhir/hapi-fhir/issues/2743) |  A new interceptor has been added for JPA servers that uses semaphores to avoid multiple concurrent FHIR transactions from trying to create/update the same resource at the same time. This can improve overall performance when writing many concurrent transactions since it avoids the need for retries.  
|  [#2748](https://github.com/hapifhir/hapi-fhir/issues/2748) |  A new tag storage mode called **Inline Tag Mode** tas been added. In this mode, all tags are stored directly in the serialized resource body in the database, instead of using dedicated tables. This has significant performance advantages when storing resources with many distinct tags (i.e. many tags that are unique to each resource, as opposed to being reused across multiple resources).  
|  [#2756](https://github.com/hapifhir/hapi-fhir/issues/2756) |  A new interceptor has been addeed to the JPA server called `ForceOffsetSearchModeInterceptor`. This interceptor forces all searches to be offset searches, instead of relying on the query cache. This means that FHIR search operations will never result in any database write, which can be good for highly concurrent servers.  
|  [#2766](https://github.com/hapifhir/hapi-fhir/issues/2766) |  A new JPA partitioning interceptor `PatientIdPartitionInterceptor` has been added. This interceptor uses the ID of the patient associated with any resources in the patient compartment to generate a consistent partition ID.  
|  [#2767](https://github.com/hapifhir/hapi-fhir/issues/2767) |  `$mdm-query-links` and `$mdm-duplicate-golden-resources` now enforce paging via parameters `_offset` and `_count`. More details can be found in the [MDM Operations documentation](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html).  
|  [#2777](https://github.com/hapifhir/hapi-fhir/issues/2777) |  Support for multiple header-passthrough option using **-hp** or **--header-passthrough** parameter has been added to hapi-fhir-cli commands: **example-data-uploader** , **export-conceptmap-to-csv** , **import-csv-to-conceptmap** and **upload-terminology**  
|  [#2782](https://github.com/hapifhir/hapi-fhir/issues/2782) |  A new Validation Support Module has been added that can use NPM Packages to supply validation conformance artifacts programatcally to the validator.  
|  [#2786](https://github.com/hapifhir/hapi-fhir/issues/2786) |  Add 'loinc.codesystem.make.current' property to upload-terminology command to allow loading LOINC version without becoming current.  
|  [#2800](https://github.com/hapifhir/hapi-fhir/issues/2800) |  Allowed the optional inclusion of the LOINC Consumer Names archive in addition to the main LOINC distribution. If it is supplied, the consumer names CSV file will be scanned, and all consumer names will be added to uploaded Concepts as additional designations  
|  [#2803](https://github.com/hapifhir/hapi-fhir/issues/2803) |  Allowed the optional inclusion of the LOINC Linguistic Variants archive in addition to the main LOINC distribution. If it is supplied, all linguistic variants files will be scanned, and all translations will be added to uploaded Concepts as additional designations  
|  |  Upgrade net.java.dev.jna to run docker tests on Mac arm64 M1 machines  
|  [#2841](https://github.com/hapifhir/hapi-fhir/issues/2841) |  The [:text](https://www.hl7.org/fhir/search.html#text) Search Parameter modifier now searches by word boundary of the text content as opposed to only searching at the start of the text when using Lucene/Elasticsearch indexing. Add * to match word prefixes (e.g. weig* will match weight).  
|  [#2845](https://github.com/hapifhir/hapi-fhir/issues/2845) |  Added new `$reindex` operation with similar syntax to `$delete-expunge` that creates a spring-batch job to reindex selected resources. `$mark-all-resources-for-reindexing` and `$perform-reindexing-pass` are now deprecated, and will likely be removed in a future release.  
|  [#2852](https://github.com/hapifhir/hapi-fhir/issues/2852) |  Replace existing email implementation code with SimpleJavaMail library.  
|  [#2871](https://github.com/hapifhir/hapi-fhir/issues/2871) |  Modified the behaviour of the `:mdm` param qualifier. Previously, it used to only resolve IDs if the resource ID was a source resource. Now, MDM expansion will work if you pass it the ID of a golden resource instead.  
|  [#2688](https://github.com/hapifhir/hapi-fhir/issues/2688) |  Conditional URL lookups in the JPA server will now explicitly specify a maximum fetch size of 2, avoiding fetching more data that won't be used inadvertently in some situations.  
|  [#2688](https://github.com/hapifhir/hapi-fhir/issues/2688) |  FHIR Transaction duplicate record checks are now performed without any database interactions or SQL statements, reducing the processing load associated with FHIR transactions by at least a small amount.  
|  [#2717](https://github.com/hapifhir/hapi-fhir/issues/2717) |  FHIR transactions in the JPA server that perform writes will now aggressively pre-fetch as many entities as possible at the very start of transaction processing. This can drastically reduce the number of round-trips, especially as the number of resources in a transaction gets bigger.  
|  [#2717](https://github.com/hapifhir/hapi-fhir/issues/2717) |  A new setting has been added to the DaoConfig called Tag Versioning Mode. This setting controls whether a single collection of tags/profiles/security labels is maintained across all versions of a single resource, or whether each version of the resource maintains its own independent collection. Previously each version always maintained an independent collection, which is useful sometimes, but is often not useful and can affect performance.  
|  [#2653](https://github.com/hapifhir/hapi-fhir/issues/2653) |  When performing a conditional create/update/delete on a JPA server, if the match URL contained a plus character, this character was interpreted as a space (per legacy URL encoding rules) even though this has proven to not be the intended behaviour in real life applications. Plus characters will now be treated literally as a plus character in these URLs.  
|  [#2695](https://github.com/hapifhir/hapi-fhir/issues/2695) |  Bulk import batch jobs are now activated in a local scheduled task, making bulk import jobs better able to take advantage of large clusters.  
|  [#2697](https://github.com/hapifhir/hapi-fhir/issues/2697) |  DELETE _expunge=true has been converted to use Spring Batch. It now simply returns the jobId of the Spring Batch job while the job continues to run in the background. A new operation called $expunge-delete has been added to provide more fine-grained control of the delete expunge operation. This operation accepts an ordered list of URLs to be delete expunged and an optional batch-size parameter that will be used to perform the delete expunge. If no batch size is specified in the operation, then the value of DaoConfig.getExpungeBatchSize() is used.  
|  [#2732](https://github.com/hapifhir/hapi-fhir/issues/2732) |  The ConceptMap.group.element.display storage size limit has been increased to 500 characters.  
|  [#2751](https://github.com/hapifhir/hapi-fhir/issues/2751) |  The bulk export request length limit has been increased to 1024 characters.  
|  [#2760](https://github.com/hapifhir/hapi-fhir/issues/2760) |  If two authorization compartments apply to the same targets and share the same compartment name, then instead of creating a new compartment, the rule builder now adds the new owner to the list of owners in the existing compartment.  
|  [#2766](https://github.com/hapifhir/hapi-fhir/issues/2766) |  When operating in partitioned mode, the interceptor pointcut `STORAGE_PARTITION_IDENTIFY_CREATE` will now be invoked to determine the partition to use for create with client-assigned IDs. Previously the `STORAGE_PARTITION_IDENTIFY_READ` pointcut was invoked, which was confusing and potentially unexpected.  
|  [#2773](https://github.com/hapifhir/hapi-fhir/issues/2773) |  Flyway migration used to enforce order by default. This has been changed so now the default behaviour is out of order migrations are permitted. Strict order can be enforced via the new strict-order flag if required.  
|  [#2791](https://github.com/hapifhir/hapi-fhir/issues/2791) |  Identifier maximum length increased from 200 to 500. This specifically applies to table HFJ_IDX_CMP_STRING_UNIQ.  
|  [#2805](https://github.com/hapifhir/hapi-fhir/issues/2805) |  When the contents of a package are corrupt, the error messages now identify the corrupt element  
|  [#2665](https://github.com/hapifhir/hapi-fhir/issues/2665) |  When performing a FHIR transaction containing a conditional create, references to that resource were inadvertently replaced with contained references."  
|  [#2672](https://github.com/hapifhir/hapi-fhir/issues/2672) |  A concurrency error was fixed when using client assigned IDs on a highly concurrent server with resource deletion disabled.  
|  [#2674](https://github.com/hapifhir/hapi-fhir/issues/2674) |  A null-pointer exception was fixed when a ResponseTerminologyDisplayInterceptor is registered and a search or read response returns a resource with code value that in turn returns a null code lookup.  
|  [#2676](https://github.com/hapifhir/hapi-fhir/issues/2676) |  Subscription notifications will no longer be triggered by default in response to changes that do not increment the resource version (e.g. `$meta-add` and `$meta-delete`). A new DaoConfig setting has been added to make this configurable.  
|  [#2674](https://github.com/hapifhir/hapi-fhir/issues/2674) |  When myDaoConfig.setDefaultTotalMode(SearchTotalModeEnum.ACCURATE) and there are zero search results on an _id search, An Index Out of Bounds error was thrown. This has been corrected.  
|  [#2682](https://github.com/hapifhir/hapi-fhir/issues/2682) |  Fixes the problem that FHIR package IDs were incorrectly treated as case sensitive when being loaded, causing loads to fail when dependencies were declared with a different case than in the package itself.  
|  [#2693](https://github.com/hapifhir/hapi-fhir/issues/2693) |  Constraint errors were not always auto-retried even when configured to do so on certain platforms (particularly Postgresql) where constraint names are auto converted to lower case. Thanks to Bruno Hedman for the pull request!  
|  [#2705](https://github.com/hapifhir/hapi-fhir/issues/2705) |  When searching by source, if deleted resources are matched, the search returned an incorrect size. This has been corrected.  
|  [#2695](https://github.com/hapifhir/hapi-fhir/issues/2695) |  The _filter search parameter was incorrectly included in the server capability statement if it was disabled on the server. This has been corrected.  
|  [#2624](https://github.com/hapifhir/hapi-fhir/issues/2624) |  ValueSet expansion did not correctly preserve the order if multiple codes were included in a single inclusion block.  
|  [#2739](https://github.com/hapifhir/hapi-fhir/issues/2739) |  Too many MDM candidates matching could result in an OutOfMemoryError. Candidate matching is now limited to the value of IMdmSettings.getCandidateSearchLimit(), default 10000.  
|  [#2741](https://github.com/hapifhir/hapi-fhir/issues/2741) |  A regression caused the JPA Server History operation to not return paging links in responses. This has been corrected.  
|  [#2747](https://github.com/hapifhir/hapi-fhir/issues/2747) |  Added null checks to MDM resource interceptor in order to avoid NPEs.  
|  [#2748](https://github.com/hapifhir/hapi-fhir/issues/2748) |  The SQL generated for the `_profile` search parameter did not use all of the columns on the tag index table, resulting on poor performance on MySQL. This has been corrected.  
|  [#2758](https://github.com/hapifhir/hapi-fhir/issues/2758) |  The internal cache for tags, concepts, and others had longer or shorter expiry times than was intended. This has been corrected.  
|  [#2761](https://github.com/hapifhir/hapi-fhir/issues/2761) |  When searching for ExplanationOfBenefit?patient=123,456, the compartment authorization interceptor was treating '123,456' as a single id rather than as a list of ids. This has been corrected.  
|  [#2762](https://github.com/hapifhir/hapi-fhir/issues/2762) |  A regression was introduced in 2760 where a READ compartment could get collapsed into a WRITE compartment. This has been corrected.  
|  [#2764](https://github.com/hapifhir/hapi-fhir/issues/2764) |  Searches for mdm-expanded references such as Observation?subject:mdm=123 were getting denied by access rules that did not recognize the :mdm suffix. This has been corrected.  
|  [#2768](https://github.com/hapifhir/hapi-fhir/issues/2768) |  $mdm-submit operation was only submitting 100 resources and then stopping. It now correctly submits all requested resources.  
|  [#2794](https://github.com/hapifhir/hapi-fhir/issues/2794) |  When providing links for placeholder creation, DaoResourceLinkResolver expects just a single 'identifier=value' param, but it can be additional data, s.a. tags, extra identifiers, etc.  
|  [#2797](https://github.com/hapifhir/hapi-fhir/issues/2797) |  When initiating a FHIR bulk export, if more than one `_typeFilter` parameter was supplied only the first one was respected. This has been corrected.  
|  [#2808](https://github.com/hapifhir/hapi-fhir/issues/2808) |  Loading packages would fail when partitioning was enabled with unnamed partitions. This has been fixed.  
|  [#2810](https://github.com/hapifhir/hapi-fhir/issues/2810) |  An issue in the FHIRPath evaluator prevented Encounters from being stored when using the new PatientIdPartitionInterceptor. This has been corrected.  
|  [#2823](https://github.com/hapifhir/hapi-fhir/issues/2823) |  Mdm failed to load if any mdm subscription had been deleted, e.g. if $expunge expungeEverything had been run on the server. This has been corrected.  
|  [#2826](https://github.com/hapifhir/hapi-fhir/issues/2826) |  When Client Id Strategy is set to NOT_ALLOWED, permit system requests to create resources, e.g. SearchParameter and Subscription resources required by the system.  
|  [#2829](https://github.com/hapifhir/hapi-fhir/issues/2829) |  Add DSTU3 Support To UploadTerminologyCommand.  
|  [#2868](https://github.com/hapifhir/hapi-fhir/issues/2868) |  Fixed a bug in transaction bundle processing, specifically for bundles which contained both a conditional create, and a resource which relied on this conditional create as a reference. This would cause the referring resource to generate a contained resource instead of appropriately referencing the existing patient.  
|  [#2876](https://github.com/hapifhir/hapi-fhir/issues/2876) |  Fixed a bug wherein an NPE could be thrown by the MDM module interceptor if an incoming resource had a tag with no system.  
|  [#2887](https://github.com/hapifhir/hapi-fhir/issues/2887) |  Fixed a bug where the search results cache was ignoring the value of `_contained` parameter when assigning a cache key. This was causing queries run in a short period of time to return wrong cached results if one query used `_contained=true` and the other did not.  
|  [#2835](https://github.com/hapifhir/hapi-fhir/issues/2835) |  Addressed the following CVE report by bumping the minor version for Jetty in the root POM: 
  * [CVE-2021-34429](https://github.com/advisories/GHSA-vjv5-gp2w-65vm)

  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/table_of_contents.html)
0.1 Changelog: 2025 
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
[ 0.2 Changelog: 2024 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2024.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)