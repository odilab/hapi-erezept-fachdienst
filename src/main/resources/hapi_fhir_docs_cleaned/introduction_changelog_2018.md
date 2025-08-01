---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html
crawled: 2025-08-01T14:05:51.606872
---

# Introduction Changelog 2018

#  0.8.1Changelog: 2018
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changelog-2018)
#  0.8.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changelog)
#  0.8.3HAPI FHIR 3.6.0 (Food)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#hapi-fhir-360-food)
##  0.8.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#release-information)
**Released:** 2018-11-12
**Codename:** (Food)
##  0.8.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changes)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Karaf (OSGi): 4.1.4 -> 4.1.6
  * Commons-Compress (JPA): 1.14 -> 1.18
  * Jackson (JPA): 2.9.2 -> 2.9.7

  
---|---|---  
|  |  The JPA server SearchCoordinator has been refactored to make searches more efficient: When a search is performed, the SearchCoordinator loads multiple pages of results even if the user has only requested a small number. This is done in order to prevent needing to re-run the search for every page of results that is loaded. In previous versions of HAPI FHIR, when a search was made the SearchCoordinator would prefetch as many results as the user could possibly request across all pages (even if this meant prefetching thousands or millions of resources). As of this version, a new option has been added to DaoConfig that specifies how many resources to prefetch. This can have a significant impact on performance for servers with a large number of resources, where users often only want the first page of results. See `DatConfig#setSearchPreFetchThresholds()` for configuration of this feature.  
|  |  When performing a JPA server using a date parameter, if a time is not specified in the query URL, the date range is expanded slightly to include all possible timezones where the date that could apply. This makes the search slightly more inclusive, which errs on the side of caution.  
|  |  A new setting has been added to the JPA server DaoConfig that causes the server to keep certain searches "warm" in the cache. This means that the search will be performed periodically in the background in order to keep a reasonably fresh copy of the results in the query cache.  
|  |  The JPA server now automatically supplies several appropriate hibernate performance settings as long as the JPA EntityManagerFactory was created using HAPI FHIR's built-in method for creating it.   
  
Existing JPA projects should consider using `super.entityManagerFactory()` as shown in [the example project](https://github.com/hapifhir/hapi-fhir-jpaserver-starter/blob/master/src/main/java/ca/uhn/fhir/jpa/demo/FhirServerConfig.java#L62) if they are not already.  
|  |  The FhirTerser `getValues(...)` methods have been overloaded. The terser can now be used to create a null-valued element where none exists. Additionally, the terser can now add a null-valued extension where one or more such extensions already exist. These changes allow better population of FHIR elements provided an arbitrary FHIR path.  
|  |  The @Operation annotation used to declare operations on the Plain Server now has a wildcard constant which may be used for the operation name. This allows you to create a server that supports operations that are not known to the server when it starts up. This is generally not advisable but can be useful for some circumstances.  
|  |  When using an @Operation method in the Plain Server, it is now possible to use a parameter annotated with @ResourceParam to receive the Parameters (or other) resource supplied by the client as the request body.  
|  |  The JPA server version migrator tool now runs in a multithreaded way, allowing it to upgrade th database faster when migration tasks require data updates.  
|  |  A new setting has been added to the JPA Server DopConfig that controls the behaviour when a client-assigned ID is encountered (i.e. the client performs an HTTP PUT to a resource ID that doesn't already exist on the server). It is now possible to disallow this action, to only allow alphanumeric IDs (the default and only option previously) or allow any IDs including alphanumeric.  
|  [#1103](https://github.com/hapifhir/hapi-fhir/issues/1103) |  It is now possible to use your own IMessageResolver instance in the narrative generator. Thanks to Ruth Alkema for the pull request!  
|  |  The REST client now allows for configurable behaviour as to whether a `_format` parameter should be included in requests.  
|  |  JPA server R4 SearchParameter custom expression validation is now done using the actual FHIRPath evaluator, meaning it is more rigorous in what it can find.  
|  |  The module which deletes stale searches has been modified so that it deletes very large searches (searches with 10000+ results in the query cache) in smaller batches, in order to avoid having very long running delete operations occupying database connections for a long time or timing out.  
|  |  A new operation has been added to the JPA server called `$trigger-subscription` . This can be used to cause a transaction to redeliver a resource that previously triggered. See [this link](https://smilecdr.com/docs/current/fhir_repository/subscription.html#manually-triggering-subscriptions) for a description of how this feature works. Note that you must add the SubscriptionRetriggeringProvider as shown in the sample project.  
|  |  When operating in R4 mode, the HAPI FHIR server will now populate Bundle.entry.response for history and search results, which is did not previously do.  
|  |  The JPA database migrator tool has been enhanced so that it now supports migrations from HAPI FHIR 3.3.0 to HAPI FHIR 3.4.0 / 3.5.0+ as well.  
|  |  A bug in the JPA migration tasks from 3.4.0 to 3.5.0 caused a failure if the HFJ_SEARCH_PARM table did not exist. This table existed in previous versions of HAPI FHIR but was dropped in 3.5.0, meaning that migrations would fail if the database was created using a snapshot version of 3.5.0.  
|  |  A bug was fixed in the JPA server $expunge operation where a database connection could sometimes be opened and not returned to the pool immediately, leading to pool starvation if the operation was called many times in a row.  
|  |  When using the testpage overlay to delete a resource, currently a crash can occur if an unqualified ID is placed in the ID text box. This has been corrected.  
|  |  AuthorizationInterceptor did not allow FHIR batch operations when the transaction() permission is granted. This has been corrected so that transaction() allows both batch and transaction requests to proceed.  
|  |  The FhirTerser `getValues(...)` methods were not properly handling modifier extensions for verions of FHIR prior to DSTU3. This has been corrected.  
|  |  When updating resources in the JPA server, a bug caused index table entries to be refreshed sometimes even though the index value hadn't changed. This issue did not cause incorrect search results but had an effect on write performance. This has been corrected.  
|  |  Automatic ID generation for contained resources (in cases where the user hasn't manually specified an ID) has been streamlined to generate more predictable IDs in some cases.  
|  |  A bug in the JPA server was fixed: When a resource was previously deleted, a transaction could not be posted that both restored the deleted resource but also contained references to the now-restored resource.  
|  |  The $expunge operation could sometimes fail to delete resources if a resource to be deleted had recently been returned in a search result. This has been corrected.  
|  [#1071](https://github.com/hapifhir/hapi-fhir/issues/1071) |  When restful reponses tried to return multiple instances of the same response header, some instances were discarded. Thanks to Volker Schmidt for the pull request!  
|  |  An issue in the HAPI FHIR CLI database migrator command has been resolved, where some database drivers did not automatically register and had to be manually added to the classpath.  
|  [#1047](https://github.com/hapifhir/hapi-fhir/issues/1047) |  A NullPointerException in DateRangeParam when a client URL conrtained a malformed date was corrected. Thanks Heinz-Dieter Conradi for the Pull Request!  
|  |  When invoking an operation using the fluent client on an instance, the operation would accidentally invoke against the server if the provided ID did not include a type. This has been corrected so that an IllegalArgumentException is now thrown.  
|  |  When using the HAPI FHIR CLI, user-prompted passwords were not correctly encoded, meaning that the "--basic-auth PROMPT" action was not usable. This has been corrected.  
#  0.8.4HAPI FHIR 3.5.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#change3.6.0-0)
##  0.8.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#release-information-1)
**Released:** 2018-09-17
##  0.8.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changes-1)
|  |  HAPI FHIR now supports JDK 9 and JDK 10, both for building HAPI FHIR as well as for use. JDK 8 remains supported and is the minimum requirement in order to build or use HAPI FHIR.  
---|---|---  
|  |  A new command has been added to the HAPI FHIR CLI tool: "migrate-database". This command performs the schema modifications required when upgrading HAPI FHIR JPA to a new version (previously this was a manual process involving running scripts and reindexing everything).   
  
See the command documentation for more information on how to use this tool. Please post in the HAPI FHIR Google Group if you run into issues, as this is a brand new framework and we still need lots of help with testing.  
|  [#1000](https://github.com/hapifhir/hapi-fhir/issues/1000) |  LOINC uploader has been updated to support the new LOINC filename scheme introduced in LOINC 2.64. Thanks to Rob Hausam for the pull request!  
|  |  In the JPA server, it is now possible for a custom search parameter to use the `resolve()` function in its path to descend into contained resources and index fields within them.  
|  |  A new IValidationSupport implementation has been added, named CachingValidationSupport. This module wraps another implementation and provides short-term caching. This can have a dramatic performance improvement on servers that are validating or executing FHIRPath repeatedly under load. This module is used by default in the JPA server.  
|  |  A new method has been added to AuthorizationInterceptor that can be used to create rules allowing FHIR patch operations. See Authorizing Patch Operations for more information.  
|  [#1018](https://github.com/hapifhir/hapi-fhir/issues/1018) |  A new view has been added to the JPA server, reducing the number of database calls required when reading resources back. This causes an improvement in performance. Thanks to Frank Tao for the pull request!  
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Gson (JSON Parser): 2.8.1 -> 2.8.5
  * Spring Framework (JPA): 5.0.3.RELEASE -> 5.0.8.RELEASE
  * Hibernate ORM (JPA): 5.2.16.Final -> 5.3.6.Final
  * Hibernate Search (JPA): 5.7.1.Final -> 5.10.3.Final
  * Jetty (CLI): 9.4.8.v20171121 -> 9.4.12.v20180830
  * Commons-Codec (All): 1.10 -> 1.11
  * Commons-Lang (All): 3.7 -> 3.8
  * Commons-IO (All): 2.5 -> 2.6
  * Spring-Data (JPA): 1.11.6.RELEASE -> 2.0.7.RELEASE

  
|  [#912](https://github.com/hapifhir/hapi-fhir/issues/912) |  The generic/fluent client now supports the :contains modifier on string search params. Thanks to Clayton Bodendein for the pull request!  
|  |  The REST Server module now allows more than one Resource Provider (i.e. more than one implementation of IResourceProvider) to be registered to the RestfulServer for the same resource type. Previous versions of HAPI FHIR have always limited support to a single resource provider, but this limitation did not serve any purpose so it has been removed.  
|  |  The HashMapResourceProvider now supports the type and instance history operations. In addition, the search method for the `_id` search parameter now has the search parameter marked as "required". This means that additional search methods can be added in subclasses without their intended searches being routed to the searchById method. Also, the resource map now uses a LinkedHashMap, so searches return a predictable order for unit tests.  
|  |  The generic client history operations (history-instance, history-type, and history-server) now support the `_at` parameter.  
|  |  A new mnandatory library depdendency has been added to hapi-fhir-base, meaning that all applications using HAPI FHIR must import ti: commons-text. This library has been added as a few utility methods used by HAPI FHIR that were formerly in the commons-lang3 project have been moved into commons-text. This library has been added as a non-optional dependency in the hapi-fhir-base POM, so Maven/Gradle users should not have to make any changes.  
|  |  In the plain server, many resource provider method parameters may now use a generic `IPrimitiveType<String>` or `IPrimitiveType<Date>` at the parameter type. This is handy if you are trying to write code that works across versions of FHIR.  
|  |  Several convenience methods have been added to the fluent/generic client interfaces. These methods allow the adding of a sort via a SortSpec object, as well as specifying search parameters via a plain Map of Strings.  
|  |  A new client interceptor called ThreadLocalCapturingInterceptor has been added. This interceptor works the same way as CapturingInterceptor in that it captures requests and responses for later processing, but it uses a ThreadLocal object to store them in order to facilitate use in multithreaded environments.  
|  |  A new constructor has been added to the client BasicAuthInterceptor allowing credentials to be specified in the form "username:password" as an alternate to specifying them as two discrete strings.  
|  |  SimpleBundleProvider has been modified to optionally allow calling code to specify a search UUID, and a field to allow the preferred page size to be configured.  
|  |  The JPA server search UUID column has been reduced in length from 40 chars to 36, in order to align with the actual length of the generated UUIDs.  
|  |  Plain servers using paging may now specify an ID/name for individual pages being returned, avoiding the need to respond to arbitrary offset/index requests from the server. In this mode, page links in search result bundles simply include the ID to the next page.  
|  |  JPA subscription delivery queues no longer store the resource body in the queue (only the ID), which should reduce the memory/disk footprint of the queue when it grows long.  
|  |  The JPA server now has a configuration item in the DaoConfig to specify which bundle types may be stored as-is on the /Bundle endpoint. By default the following types are allowed: collection, document, message.  
|  |  When performing a ConceptMap/$translate operation with reverse="true" in the arguments, the equivalency flag is now set on the response just as it is for a non-reverse lookup.  
|  |  When executing a FHIR transaction in JPA server, if the request bundle contains placeholder IDs references (i.e. "urn:uuid:*" references) that can not be resolved anywhere else in the bundle, a user friendly error is now returned. Previously, a cryptic error containing only the UUID was returned. As a part of this change, transaction processing has now been consolidated into a single codebase for DSTU3 / R4 (and future) versions of FHIR. This should greatly improve maintainability and consistency for transaction processing.  
|  |  ResponseHighlighterInterceptor now displays the total size of the output and an estimate of the transfer time at the bottom of the response.  
|  [#1022](https://github.com/hapifhir/hapi-fhir/issues/1022) |  The Prefer header is now honoured for HTTP PATCH requests. Thanks to Alin Leonard for the Pull Request!  
|  |  The `Composition` operation `$document` has been implemented. Thanks to Patrick Werner for the Pull Request!  
|  |  HAPI FHIR CLI commands that allow Basic Auth credentials or a Bearer Token may now use a value of "PROMPT" to cause the CLI to prompt the user for credentials using an interactive prompt.  
|  |  The maximum length for codes in the JPA server terminology service have been increased to 500 in order to better accomodate code systems with very long codes.  
|  |  CapabilityStatements generated by the server module will now include the server base URL in the `CapabilityStatement.implementation.url` field.  
|  |  The JPA server now performs a count query instead of a more expensive data query when searches using `_summary=count` . This means that a total will always be returned in the Bundle (this isn't always guaranteed otherwise, since the Search Controller can result in data being returned before the total number of results is known).  
|  |  The JPA server SearchCoordinator now prefetches only a smaller and configurable number of results during the initial search request, and more may be requested in subsequent page requests. This change may have a significant improvement on performance: in previous versions of HAPI FHIR, even if the user only wanted the first page of 10 results, many many more might be prefetched, consuming database resources and server time.  
|  [#974](https://github.com/hapifhir/hapi-fhir/issues/974) |  Spring-data (used by the JPA server) has been upgraded to version 2.0.7 (from version 1.11.6). Thanks to Roman Doboni for the pull request!  
|  |  AuthorizationInterceptor now examines requests more closely in order to block requests early that are not possibly going to return allowable results when compartment rules are used. For example, if an AuthorizationInterceptor is configured to allow only **read** access to compartment `Patient/123` , a search for `Observation?subject=987` will now be blocked before the method handler is called. Previously the search was performed and the results were examined in order to determine whether they were all in the appropriate compartment, but this incurs a performance cost, and means that this search would successfully return an empty Bundle if no matches were present.   
  
A new setting on AuthorizationInterceptor called `setFlags(flags)` can be used to maintain the previous behaviour.  
|  |  JPA server loading logic has been improved to enhance performance when loading a large number of results in a page, or when loading multiple search results with tags. Thanks to Frank Tao for the pull request! This change was introduced as a part of a collaboration between HAPI FHIR and the US National Institiutes for Health (NIH).  
|  [#1010](https://github.com/hapifhir/hapi-fhir/issues/1010) |  Resource loading logic for the JPA server has been optimized to reduce the number of database round trips required when loading search results where many of the entries have a "forced ID" (an alphanumeric client-assigned resource ID). Thanks to Frank Tao for the pull request! This change was introduced as a part of a collaboration between HAPI FHIR and the US National Institiutes for Health (NIH).  
|  |  An index in the JPA server on the HFJ_FORCED_ID table was incorrectly not marked as unique. This meant that under heavy load it was possible to create two resources with the same client-assigned ID.  
|  |  The JPA server `$expunge` operation deleted components of an individual resource record in separate database transactions, meaning that if an operation failed unexpectedly resources could be left in a weird state. This has been corrected.  
|  [#1015](https://github.com/hapifhir/hapi-fhir/issues/1015) |  A bug was fixed in the JPA terminology uploader, where it was possible in some cases for some ValueSets and ConceptMaps to not be saved because of a premature short circuit during deferred uploading. Thanks to Joel Schneider for the pull request!  
|  [#969](https://github.com/hapifhir/hapi-fhir/issues/969) |  A bug in the HAPI FHIR CLI was fixed, where uploading terminology for R4 could cause an error about the incorrect FHIR version. Thanks to Rob Hausam for the pull request!  
|  |  A crash was fixed when deleting a ConceptMap resource in the JPA server. This crash was a regression in HAPI FHIR 3.4.0.  
|  |  A crash in the JPA server when performing a manual reindex of a deleted resource was fixed.  
|  |  Using the generic/fluent client, it is now possible to invoke the $process-message method using a standard client.operation() call. Previously this caused a strange NullPointerException.  
|  |  The REST Server now sanitizes URL path components and query parameter names to escape several reserved characters (e.g. " and <) in order to prevent HTML injection attacks via maliciously crafted URLs.  
|  [#996](https://github.com/hapifhir/hapi-fhir/issues/996) |  The HAPI FHIR Server has been updated to correctly reflect the current FHIR specification behaviour for the Prefer header. This means that the server will no longer return an OperationOutcome by default, but that one may be requested via a Prefer header, using the newly implemented "Repreentation: OperationOutcome" value. Thanks to Ana Maria Radu for the pul request!  
|  |  Fixed a bug when creating a custom search parameter in the JPA server: if the SearchParameter resource contained an invalid expression, create/update operations for the given resource would fail with a cryptic error. SearchParameter expressions are now validated upon storage, and the SearchParameter will be rejected if the expression can not be processed.  
|  [#965](https://github.com/hapifhir/hapi-fhir/issues/965) |  An issue was fixed in BundleUtil#toListOfEntries, where sometimes a resource could be associated with the wrong entry in the response. Thanks to GitHub user @jbalbien for the pull request!  
|  [#1053](https://github.com/hapifhir/hapi-fhir/issues/1053) |  A bug was fixed in JPA server searches: When performing a search with a _lastUpdate filter, the filter was applied to any _include values, which it should not have been. Thanks to Deepak Garg for reporting!  
|  |  A crash was fixed when using the ConceptMap/$translate operation to translate a mapping where the equivalence was not specified.  
|  |  A bug in the DSTU3 validator was fixed where validation resources such as StructureDefinitions and Questionnaires were cached in a cache that never expired, leading to validations against stale versions of resources.  
|  |  In the REST server, if an incoming request has the Content-Encoding header, the server will not try to read request parameters from the content stream. This avoids an incompatibility with new versions of Jetty.  
|  [#1050](https://github.com/hapifhir/hapi-fhir/issues/1050) |  Custom profile names when not matching standard FHIR profile names, are now handled properly by the validator. Thanks to Anthony Sute for the Pull Request!  
|  |  A crash in the validator was fixed: Validating a Bundle that did not have Bundle.fullUrl populated could cause a NullPointerException.  
|  |  The experimental "dynamic mode" for search parameter registration has been removed. This mode was never published or documented and was labelled as experimental, so I am hoping it was never depended on by anyone. Please post on the HAPI FHIR mailing list if this change affects you.  
#  0.8.5HAPI FHIR 3.4.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#change3.5.0-0)
##  0.8.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#release-information-2)
**Released:** 2018-05-28
##  0.8.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changes-2)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Commons-Lang3 (All): 3.6 -> 3.7
  * Hibernate (JPA): 5.2.12.Final -> 5.2.16.Final
  * Javassist (JPA): 3.20.0-GA -> 3.22.0-GA

  
---|---|---  
|  |  Several enhancements have been made to the JPA server index tables. These enhancements consist of new colums that will be used in a future version of HAPI FHIR to significantly decrease the amount of space required for indexes on token and string index types.   
  
These new columns are not yet used in HAPI FHIR 3.4.0 but will be enabled in HAPI FHIR 3.5.0. Anyone upgrading to HAPI FHIR 3.4.0 (or above) is recommended to invoke the following SQL statement on their database in order to reindex all data in a background job:   
`update HFJ_RESOURCE set SP_INDEX_STATUS = null;`   
Note that if you do this reindex now, you will not have any downtime while you upgrade to HAPI FHIR 3.5.0. If you need to perform the reindex at the time that you upgrade to HAPI FHIR 3.5.0 some indexes may not be available.   
In addition, the following schema changes should be made while upgrading:   
`update table TRM_CODESYSTEM_VER drop column RES_VERSION_ID;`  
`alter table TRM_CODESYSTEM_VER drop constraint IDX_CSV_RESOURCEPID_AND_VER`  
  
|  |  A new operation has been added to the JPA server called "$expunge". This operation can be used to physically delete old versions of resources, logically deleted resources, or even all resources in the database.  
|  |  An experimental new feature has been added to AuthorizationInterceptor which allows user-supplied checkers to add additional checking logic to determine whether a particular rule applies. This could be used for example to restrict an auth rule to particular source IPs, or to only allow operations with specific parameter values.  
|  |  A new qualifier has been added to the AuthorizationInterceptor RuleBuilder that allows a rule on an operation to match `atAnyLevel()` , meaning that the rule applies to the operation by name whether it is at the server, type, or instance level.  
|  |  Calling `IdType#withVersion(String)` with a null/blank parameter will now return a copy of the ID with the version removed. Previously this call would deliberately cause an IllegalArgumentException.  
|  |  The JPA server CapabilityStatement generator has been tuned so that resource counts are no longer calculated synchronously as a part of building the CapabilityStatement response. With this change, counts are calculated in the background and cached which can yield significant performance improvements on hevaily loaded servers.  
|  [#903](https://github.com/hapifhir/hapi-fhir/issues/903) |  Fix a bug in the DSTU2 QuestionnaireResponseValidator which prevented validation on groups with only one question. Thanks David Gileadi for the pull request!  
|  [#709](https://github.com/hapifhir/hapi-fhir/issues/709) |  The `ConceptMap` operation `$translate` has been implemented.  
|  |  R4 structures have been updated to the latest definitions (SVN 13732)  
|  [#927](https://github.com/hapifhir/hapi-fhir/issues/927) |  HAPI-FHIR_CLI now includes two new commands: one for importing and populating a `ConceptMap` resource from a CSV; and one for exporting a `ConceptMap` resource to a CSV.  
|  |  Operation methods on a plain server may now use parameters of type String (i.e. plain Java strings), and any FHIR primitive datatype will be automatically coerced into a String.  
|  |  The HAPI FHIR CLI now supports importing an IGPack file as an import to the validation process.  
|  |  When two threads attempt to update the same resource at the same time, previously an unspecified error was thrown by the JPA server. An HTTP 409 (Conflict) with an informative error message is now thrown.  
|  |  StructureDefinitions for the FHIR standard extensions have been added to the hapi-fhir-validation-resources-XXXX modules. Thanks to Patrick Werner for the pull request! These have also been added to the list of definitions uploaded by the CLI "upload-definitions" command.  
|  [#926](https://github.com/hapifhir/hapi-fhir/issues/926) |  The HAPI FHIR CLI is now available for installation on OSX using the (really excellent) Homebrew package manager thanks to an effort by John Grimes to get it added. Thanks John!  
|  [#953](https://github.com/hapifhir/hapi-fhir/issues/953) |  When the REST Server experiences an expected error (such as a NullPointerException) in a resource provider class, a simple message of "Failed to call access method" is returned to the user. This has been enhanced to also include the message from the underlying exception.  
|  [#857](https://github.com/hapifhir/hapi-fhir/issues/857) |  DateRangeParameter was enhanced to support convenient method chanining, and the parameter validation was improved to only change state after validating that parameters were valid. Thanks to Gaetano Gallo for the pull request!  
|  [#875](https://github.com/hapifhir/hapi-fhir/issues/875) |  An issue in the narrative generator template for the CodeableConcept datatype was corrected. Thanks to @RuthAlk for the pull request!  
|  |  The JPA server automatic reindexing process has been tweaked so that it no longer runs once per minute (this was a heavy strain on large databases) but will instead run once an hour unless triggered for some reason. In addition, the number of threads allocated to reindexing may now be adjusted via a setting in the DaoConfig.  
|  [#880](https://github.com/hapifhir/hapi-fhir/issues/880) |  Several tests were added to ensure accurate validation of QuestionnaireResponse resources. Thanks to Heinz-Dieter Conradi for the pull request!  
|  [#886](https://github.com/hapifhir/hapi-fhir/issues/886) |  A NullPointerException when validating some QuestionnaireResponse reousrces was fixed in the validator. Thanks to Heinz-Dieter Conradi for the pull request!  
|  [#892](https://github.com/hapifhir/hapi-fhir/issues/892) |  QuestionnaireResponse answers of type "text" may now be validated by the FhirInstanceValidator. Thanks to Heinz-Dieter Conradi for the pull request!  
|  |  The DSTU2 validator has been refactored to use the same codebase as the DSTU3/R4 validator (which were harmonized in HAPI FHIR 3.3.0). This means that we now have a single codebase for all validators, which improves maintainability and brings a number of improvements to the accuracy of DSTU2 resource validation.  
|  |  The REST Generic Client now supports invoking an operation on a specific version of a resource instance.  
|  |  When updating resources on the JPA server, tags did not always consistently follow FHIR's recommended rules for tag retention. According to FHIR's rules, if a tag is not explicitly present on an update but was present on the previous version, it should be carried forward anyhow. Due to a bug, this happened when more than one tag was present but not when only one was present. This has been corrected. In addition, a new request header called `X-Meta-Snapshot-Mode` has been added that can be used by the client to override this behaviour.  
|  |  The JPA server's resource counts query has been optimized to give the database a bit more flexibility to optimize, which should increase performance for this query.  
|  |  Fix a significant performance regression in 3.3.0 when validating DSTU3 content using the InstanceValidator. From 3.3.0 onward, StructureDefinitions are converted to FHIR R4 content on the fly in order to reduct duplication in the codebase. These conversions happened upon every validation however, instead of only happening once which adversely affected performance. A cache has been added.  
|  |  A bug in the JPA server's DSTU2 transaction processing routine caused it to occasionally consume two database connections, which could lead to deadlocks under heavy load. This has been fixed.  
|  |  AuthorizationInterceptor sometimes incorrectly identified an operation invocation at the type level as being at the instance level if the method indicated that the IdParam parameter was optional. This has been fixed.  
|  |  A workaround for an invalid search parameter path in the R4 consent resource has been implemented. This path was preventing some Consent resources from successfully being uploaded to the JPA server. Thanks to Anthony Sute for identifying this.  
|  [#937](https://github.com/hapifhir/hapi-fhir/issues/937) |  A hard-to-understand validation message was fixed in the validator when validating against profiles that declare some elements as mustSupport but have others used but not declared as mustSupport. Thanks to Patrick Werner for the PR!  
|  [#846](https://github.com/hapifhir/hapi-fhir/issues/846) |  When calling a getter on a DSTU3/R4 structure for a choice type (e.g. Observation#getValueString()), a NullPointerException was thrown if there was no value in this field, and the NPE had no useful error message. Now this method call will simply return null. method  
|  [#836](https://github.com/hapifhir/hapi-fhir/issues/836) |  A bug in the plain server was fixed that prevented some includes from correctly causing their targets to be included in the response bundle. Thanks to GitHub user @RuthAlk for the pull request!  
|  [#867](https://github.com/hapifhir/hapi-fhir/issues/867) |  The HumanName DSTU3+ datatype had convenience methods for testing whether the name has a specific given name or not, but these methods did not work. Thanks to Jason Owen for reporting and providing a test case!  
|  [#874](https://github.com/hapifhir/hapi-fhir/issues/874) |  An issue was corrected in the validator where Questionnaire references that used contained resources caused an unexpected crash. Thanks to Heinz-Dieter Conradi for the pull request!  
|  |  AuthorizationInterceptor did not correctly grant access to resources by compartment when the reference on the target resource that pointed to the compartment owner was defined using a resource object (ResourceReference#setResource) instead of a reference (ResourceReference#setReference).  
|  |  When performing a FHIR resource update in the JPA server where the update happens within a transaction, and the resource being updated contains placeholder IDs, and the resource has not actually changed, a new version was created even though there was not actually any change. This particular combination of circumstances seems very specific and improbable, but it is quite common for some types of solutions (e.g. mapping HL7v2 data) so this fix can prevent significant wasted space in some cases.  
|  |  The REST server has been modified so that the `Location` header is no longer returned by the server on read or update responses. This header was returned in the past, but this header is actually inappropriate for any response that is not a create operation. The `Content-Location` will still be returned, and will hold the same contents.  
|  |  The Postgres sample JPA project was fixed to use the current version of HAPI FHIR (it was previously stuck on 2.2). Thanks to Kai Liu for the pull request!  
|  |  JPA server index tables did not have a column length specified on the resource type column. This caused the default of 255 to be used, which wasted a lot of space since resource names are all less than 30 chars long and a single resource can have 10-100+ index rows depending on configuration. This has now been set to a much more sensible 30.  
|  |  The LOINC uploader for the JPA Terminology Server has been significantly beefed up so that it now takes in the full set of LOINC distribution artifacts, and creates not only the LOINC CodeSystem but a complete set of concept properties, a number of LOINC ValueSets, and a number of LOINC ConceptMaps. This work was sponsored by the Regenstrief Institute. Thanks to Regenstrief for their support!  
|  |  When encoding a resource that had contained resources with user-supplied local IDs (e.g. resource.setId("#1")) as well as contained resources with no IDs (meaning HAPI should automatically assign a local ID for these resources) it was possible for HAPI to generate a local ID that already existed, making the resulting serialization invalid. This has been corrected.  
#  0.8.6HAPI FHIR 3.3.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#change3.4.0-0)
##  0.8.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#release-information-3)
**Released:** 2018-03-29
##  0.8.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changes-3)
|  |  This release corrects an inefficiency in the JPA Server, but requires a schema change in order to update. Prior to this version of HAPI FHIR, a CLOB column containing the complete resource body was stored in two tables: HFJ_RESOURCE and HFJ_RES_VER. Because the same content was stored in two places, the database consumed more space than is needed to.   
  
In order to reduce this duplication, the `RES_TEXT` and `RES_ENCODING` columns have been **dropped** from the `HFJ_RESOURCE` table, and the `RES_TEXT` and `RES_ENCODING` columns have been **made NULLABLE** on the `HFJ_RES_VER` table.   
  
The following migration script may be used to apply these changes to your database. Naturally you should back your database up prior to making this change.   
`ALTER TABLE hfj_resource DROP COLUMN res_text;`  
`ALTER TABLE hfj_resource DROP COLUMN res_encoding;`  
`ALTER TABLE hfj_res_ver ALTER COLUMN res_encoding DROP NOT NULL;`  
`ALTER TABLE hfj_res_ver ALTER COLUMN res_text DROP NOT NULL;`  
  
---|---|---  
|  |  An experimental interceptor called VersionedApiConverterInterceptor has been added, which automaticaly converts response payloads to a client-specified version according to transforms built into FHIR.  
|  |  ResponseHighlightingInterceptor now properly parses _format parameters that include additional content (e.g. `_format=html/json;fhirVersion=1.0` )  
|  |  Stale search deleting routine on JPA server has been adjusted to delete one search per transaction instead of batching 1000 searches per transaction. This should make the deletion logic more tolerant of deleting very large search result sets.  
|  |  Avoid refreshing the search parameter cache from an incoming client request thread, which caused unneccesary delays for clients.  
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate (JPA): 5.2.10.Final -> 5.2.12.Final
  * Spring (JPA): 5.0.0 -> 5.0.3
  * Thymeleaf (Web Tespage Overlay): 3.0.7.RELEASE -> 3.0.9.RELEASE

  
|  [#854](https://github.com/hapifhir/hapi-fhir/issues/854) |  JPA server now performs temporary/placeholder ID substitution processing on elements in resources which are of type "URI" in addition to the current substitution for elements of type "Reference". Thanks to GitHub user @t4deon for supplying a testcase!  
|  |  A new IResourceProvider implementation called `HashMapResourceProvider` has been added. This is a complete resource provider implementation that uses a HashMap as a backing store. This class is probably of limited use in real production systems, but it cam be useful for tests or for static servers with small amounts of data.  
|  [#868](https://github.com/hapifhir/hapi-fhir/issues/868) |  DateParam class now has equals() and hashCode() implementations. Thanks to Gaetano Gallo for the pull request!  
|  |  The client LoggingInterceptor now includes the number of milliseconds spent performing each call that is logged.  
|  [#871](https://github.com/hapifhir/hapi-fhir/issues/871) |  A number of HAPI FHIR modules have been converted so that they now work as OSGi modules. Unlike the previous OSGi module, which was a mega-JAR with all of HAPI FHIR in it, this is simply the appropriate OSGi manifest inside the existing JARs. Thanks to John Poth for the Pull Request!   
  
Note that this does not cover all modules in the project. Current support includes: 
  * HAPI-FHIR structures DSTU2, HL7ORGDSTU2, DSTU2.1, DSTU3, R4
  * HAPI-FHIR Resource validation DSTU2, HL7ORGDSTU2, DSTU2.1, DSTU3, R4
  * Apache Karaf features for all the above
  * Integration Tests

Remaining work includes: 
  * HAPI-FHIR Server support
  * HAPI-FHIR narrative support. This might be tricky as Thymeleaf doesn't support OSGi.

  
|  [#786](https://github.com/hapifhir/hapi-fhir/issues/786) |  ReferenceParam has been enhanced to properly return the resource type to user code in a server via the ReferenceType#getResourceType() method if the client has specified a reference parameter with a resource type. Thanks to @CarthageKing for the pull request!  
|  [#776](https://github.com/hapifhir/hapi-fhir/issues/776) |  An entry has been added to ResourceMetadataKeyEnum which allows extensions to be placed in the resource metadata section in DSTU2 resource (this is possible already in DSTU3+ resources as Meta is a normal model type, but the older structures worked a bit differently. Thanks to GitHub user sjanic for the contribution!  
|  [#791](https://github.com/hapifhir/hapi-fhir/issues/791) |  An example project has een contributed which shows how to use the CQL framework in a server with HAPI FHIR JPA. Thanks to Chris Schuler for the pull request!  
|  [#798](https://github.com/hapifhir/hapi-fhir/issues/798) |  A new module has been contributed called hapi-fhir-jpaserver-elasticsearch which adds support for Elasticsearch instead of raw Lucene for fulltext indexing. Testing help on this would be appreciated! Thanks to Jiajing Liang for the pull request!  
|  [#812](https://github.com/hapifhir/hapi-fhir/issues/812) |  REST HOOK subscriptions in the JPA server now support having an empty/missing Subscription.channel.payload value, which is supported according to the FHIR specification. Thanks to Jeff Chung for the pull request!  
|  [#817](https://github.com/hapifhir/hapi-fhir/issues/817) |  A new example project has been added called hapi-fhir-jpaserver-dynamic, which uses application/environment properties to configure which version of FHIR the server supports and other configuration. Thanks to Anoush Mouradian for the pull request!  
|  [#581](https://github.com/hapifhir/hapi-fhir/issues/581) |  A new example project showing the use of JAX-RS Server Side Events has been added. Thanks to Jens Kristian Villadsen for the pull request!  
|  [#819](https://github.com/hapifhir/hapi-fhir/issues/819) |  Support has been added to the JPA server for the :not modifier. Thanks to Łukasz Dywicki for the pull request!  
|  [#877](https://github.com/hapifhir/hapi-fhir/issues/877) |  Suport for the :contains string search parameter modifier has been added to the JPA server. Thanks to Anthony Sute for the pull request!  
|  |  A new method overload has been added to IServerInterceptor: `outgoingResponse(RequestDetails, ResponseDetails, HttpServletRequest, HttpServletResponse)` . This new method allows an interceptor to completely replace the resource being returned with a different resource instance, or to modify the HTTP Status Code being returned. All other "outgoingResponse" methods have been deprecated and are recommended to be migrated to the new method. This new method (with its RequestDetails and ResponseDetails parameters) should be flexible enough to accommodate future needs which means that this should be the last time we have to change it.  
|  |  The validation module has been refactored to use the R4 (currently maintained) validator even for DSTU3 validation. This is done by using an automatic converter which converts StructureDefinition/ValueSet/CodeSystem resources which are used as inputs to the validator. This change should fix a number of known issues with the validator, as they have been fixed in R4 but not in DSTU3. This also makes our validator much more maintainable since it is now one codebase.  
|  [#822](https://github.com/hapifhir/hapi-fhir/issues/822) |  Searches which were embedded in a Bundle as a transaction or batch operation did not respect any chained method parameters (e.g. MedicationRequest?medication.code=123). Thanks to @manjusampath for reporting!  
|  |  A few fixes went into the build which should now allow HAPI FHIR to build correctly on JDK 9.0. Currently building is supported on JDK 8.x and 9.x only.  
|  [#837](https://github.com/hapifhir/hapi-fhir/issues/837) |  Client requests with an `Accept` header value of `application/json` will now be served with the non-legacy content type of `application/fhir+json` instead of the legacy `application/json+fhir` . Thanks to John Grimes for reporting!  
|  |  Fixed a regression in server where a count parameter in the form `@Count IntegerType theCount` caused an exception if the client made a request with no count parameter included. Thanks to Viviana Sanz for reporting!  
|  |  A bug in the JPA server was fixed where a Subscription incorrectly created without a status or with invalid criteria would cause a crash during startup.  
|  |  An occasional crash in the JPA was fixed when using unique search parameters and updating a resource to no longer match one of these search parameters.  
|  |  Avoid an endless loop of reindexing in JPA if a SearchParameter is created which indexed the SearchParameter resource itself  
|  |  Deleting a resource from the testpage overlay resulted in an error page after clicking "delete", even though the delete succeeded.  
|  [#863](https://github.com/hapifhir/hapi-fhir/issues/863) |  JPA server now correctly indexes custom search parameters which have multiple base resource types. Previously, the indexing could cause resources of the wrong type to be returned in a search if a parameter being used also matched that type. Thanks to Dave Carlson for reporting!  
|  [#872](https://github.com/hapifhir/hapi-fhir/issues/872) |  An issue in the JPA server was corrected where searching using URI search parameters would sometimes not include the resource type in the criteria. This meant, for example, that a search for `ValueSet?url=http://foo` would also match any CodeSystem resource that happened to also have that URL as the value for the "url" search parameter. Thanks to Josh Mandel for reporting and supplying a test case!  
|  [#814](https://github.com/hapifhir/hapi-fhir/issues/814) |  Fix a bug where under certain circumstances, duplicate contained resources could be output by the parser's encode methods. Thanks to Frank Tao for supplying a test case!  
|  [#800](https://github.com/hapifhir/hapi-fhir/issues/800) |  JAX-RS server now supports R4 and DSTU2_1 FHIR versions, which were previously missing. Thanks to Clayton Bodendein for the pull request!  
|  [#806](https://github.com/hapifhir/hapi-fhir/issues/806) |  AuthorizationInterceptor did not correctly handle authorization against against a compartment where the compartment owner was specified as a list of IDs. Thanks to Jiajing Liang for the pull request!  
|  |  JPA Server Operation Interceptor create/update methods will now no longer be fired if the create/update operation being performed is a no-op (e.g. a conditional create that did not need to perform any action, or an update where the contents didn't actually change)  
|  [#879](https://github.com/hapifhir/hapi-fhir/issues/879) |  JPA server sometimes updated resources even though the client supplied an update with no actual changes in it, due to changes in the metadata section being considered content changes. Thanks to Kyle Meadows for the pull request!  
|  |  Fix a crash in the JSON parser when parsing extensions on repeatable elements (e.g. Patient.address.line) where there is an extension on the first repetition but not on subsequent repetitions of the repeatable primitive. Thanks to Igor Sirkovich for providing a test case!  
|  |  All instances of DefaultProfileValidationSupport (i.e. one for each version of FHIR) have been fixed so that they explicitly close any InputStreams they open in order to read the built-in profile resources. Leaving these open caused resource starvation in some cases under heavy load.  
|  [#832](https://github.com/hapifhir/hapi-fhir/issues/832) |  Fix an issue where the JPA server crashed while attempting to normalize string values containing Korean text. Thanks to GitHub user @JoonggeonLee for reporting!  
|  |  An issue was solved where it was possible for server interceptors to have both processingCompletedNormally and handleException called if the stream.close() method threw an exception. Thanks to Carlos Eduardo Lara Augusto for investigating!  
|  [#838](https://github.com/hapifhir/hapi-fhir/issues/838) |  The HAPI-FHIR-CLI now explicitly includes JAXB dependencies in its combined JAR file. These were not neccesary prior to Java 9, but the JDK (mercifully) does not include JAXB in the default classpath as of Java 9. This means that it is possible to perform Schematron validation on Java 9. Thanks to John Grimes for reporting and suggesting a fix!  
|  |  A number of info level log lines have been reduced to debug level in the JPA server, in order to reduce contention during heavy loads and reduce the amount of noise in log files overall. A typical server should now see far less logging coming from HAPI, at least at the INFO level.  
|  [#864](https://github.com/hapifhir/hapi-fhir/issues/864) |  An unneccesary reference to the Javassist library has been removed from the build. Thanks to Łukasz Dywicki for the pull request!  
|  [#831](https://github.com/hapifhir/hapi-fhir/issues/831) |  The `@TagListParam` annotation has been removed. This annotation had no use after DSTU1 but never got deleted and was misleading. Thanks to Angelo Kastroulis for reporting!  
#  0.8.7HAPI FHIR 3.2.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#change3.3.0-0)
##  0.8.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#release-information-4)
**Released:** 2018-01-13
##  0.8.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html#changes-4)
|  |  Support for custom search parameters has been backported in the JPA server from DSTU3 back to DSTU2. As of this release of HAPI, full support for custom search parameters exists in all supported versions of FHIR.  
---|---|---  
|  |  A new set of methods have been added to `IServerOperationInterceptor` called `resourcePreCreate` , `resourcePreUpdate` , and `resourcePreDelete` . These methods are called within the database transaction (just as the existing methods were) but are invoked prior to the contents being saved to the database. This can be useful in order to allow interceptors to change payload contents being saved.  
|  |  The HAPI FHIR Server framework now has initial support for multitenancy. At this time the support is limited to the server framework (not the client, JPA, or JAX-RS frameworks). See Server Documentation for more information.  
|  |  A new method has been added to RequestDetails called `setRequestContents()` which can be used by interceptors to modify the request body before it is parsed by the server.  
|  |  A new configuration option has been added to DaoConfig which allows newly created resources to be assigned a UUID by the server instead of a sequential ID  
|  [#810](https://github.com/hapifhir/hapi-fhir/issues/810) |  Fix an issue in JPA server where updating a resource sometimes caused date search indexes to be incorrectly deleted. Thanks to Kyle Meadows for the pull request!  
|  [#808](https://github.com/hapifhir/hapi-fhir/issues/808) |  Servers did not return an ETag if the version was provided on a DSTU3/R4 structure in the getMeta() version field instead of in the getIdElement() ID. Thanks to GitHub user @Chrisjobling for reporting!  
|  |  A bug was fixed in the JPA server when performing a validate operation with a mode of DELETE on a server with referential integrity disabled, the validate operation would delete resource reference indexes as though the delete was actually happening, which negatively affected searching for the resource being validated.  
|  |  Fix a crash in JPA server when performing a recursive `_include` which doesn't actually find any matches.  
|  [#796](https://github.com/hapifhir/hapi-fhir/issues/796) |  When encoding URL parameter values, HAPI FHIR would incorrectly escape a space (" ") as a plus ("+") insetad of as "%20" as required by RFC 3986. This affects client calls, as well as URLs generated by the server (e.g. REST HOOK calls). Thanks to James Daily for reporting!  
|  |  Searching in JPA server using a combination of _content and _id parameters failed. Thanks to Jeff Weyer for reporting!  
|  |  An unneccesary column called "MYHASHCODE" was added to the HFJ_TAG_DEF table in the JPA server schema  
|  |  A few log entries emitted by the JPA server suring every search have been reduced from INFO to DEBUG in order to reduce log noise  
|  |  A few redundant and no longer useful methods have been marked as deprecated in `IServerInterceptor` . If you have implemented custom interceptors you are recommended to migrate to the recommended methods.  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html)
0.8 Changelog: 2018 
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
[ 0.9 Changelog: 2017 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)