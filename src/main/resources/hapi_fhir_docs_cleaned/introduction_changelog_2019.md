---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html
crawled: 2025-08-01T14:04:57.741964
---

# Introduction Changelog 2019

#  0.7.1Changelog: 2019
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changelog-2019)
#  0.7.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changelog)
#  0.7.3HAPI FHIR 4.1.0 (Jitterbug)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#hapi-fhir-410-jitterbug)
##  0.7.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#release-information)
**Released:** 2019-11-13
**Codename:** (Jitterbug)
##  0.7.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changes)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * SLF4j (All): 1.7.25 -> 1.7.28
  * Spring (JPA): 5.1.8.Final -> 5.2.0.Final
  * Hibernate Core (JPA): 5.4.2.Final -> 5.4.6.Final
  * Hibernate Search (JPA): 5.11.1.Final -> 5.11.3.Final
  * Jackson Databind (JPA): 2.9.9 -> 2.9.10 (CVE-2019-16335, CVE-2019-14540)
  * Commons-DBCP2 (JPA): 2.6.0 -> 2.7.0
  * Postgresql JDBC Driver (JPA): 42.2.6.jre7 -> 42.2.8
  * MSSQL JDBC Driver (JPA): 7.0.0.jre8 -> 7.4.1.jre8
  * Spring Boot (Boot): 2.1.1 -> 2.2.0
  * Phloc Schematron (Validator): 5.0.4 -> 5.2.0
  * Phloc Commons (Validator): 9.1.1 -> 9.3.8

  
---|---|---  
|  |  **New Feature** : The JPA server now saves and supports searching on `Resource.meta.source` via the `_source` search parameter. The server automatically appends the Request ID as a hash value on the URI as well in order to provide request level tracking. Searches can use either the source URI, the request ID, or both.  
|  |  When using the AuthorizationInterceptor with a rule to allow all reads by resource type, the server will now reject requests for other resource types earlier in the processing cycle. Thanks to Anders Havn for the suggestion!  
|  |  **New Feature** : Support for the FHIR Bulk Data Export specification has been added to the JPA server. See the [specification](http://hl7.org/fhir/uv/bulkdata/) for information on how this works. Note that only system level export is currently supported but others will follow.  
|  |  It is now possible to submit a PATCH request as a part of a FHIR transaction in DSTU3 (previously this was only supported in R4+). This is not officially part of the DSTU3 spec, but it can now be performed by leaving the Bundle.entry.request.method blank in DSTU3 transactions and setting the request payload as a Binary resource containing a valid patch.  
|  [#1443](https://github.com/hapifhir/hapi-fhir/issues/1443) |  LOINC concepts now include multiaxial hierarchical properties (e.g. `parent` and `child` , which identify parent and child concepts.  
|  [#1445](https://github.com/hapifhir/hapi-fhir/issues/1445) |  When loading LOINC terminology, a new ValueSet is automatically created with a single include element that identifies the LOINC CodeSystem in `ValueSet.compose.include.system` . This ValueSet includes all LOINC codes.  
|  [#1461](https://github.com/hapifhir/hapi-fhir/issues/1461) |  A note has been added to the downloads page explaning the removal of the hapi-fhir-utilities module. Thanks to Andrew Fitzgerald for the PR!  
|  |  Added support for comparing resource dates to the current time via a new variable %now. E.g. Procedure?date=gt%now would match future procedures.  
|  |  **New Feature** : Support for ElasticSearch has been added to the JPA server directly (i.e. without needing a separate module) and a new class called "ElasticsearchHibernatePropertiesBuilder" has been added to facilitate the creation of relevant properties. Instructions have been added to the hapi-fhir-jpaserver-starter project to get started with Elasticsearch. It is likely we will switch our default recommendation to Elastic in the future.  
|  |  Add support for in-memory matching on date comparisons ge,gt,eq,lt,le.  
|  |  The JPA server now uses the Quartz scheduling library as a lob scheduling mechanism  
|  |  A new flag has been added to the JPA migrator tool that causes the migrator to not try to reduce the length of existing columns in the schema.  
|  [#1366](https://github.com/hapifhir/hapi-fhir/issues/1366) |  The ValueSet operation `$expand` has been optimized for large ValueSets. ValueSets are now persistence-backed by the terminology tables, which are populated by a scheduled pre-expansion process. A ValueSet previously stored in an existing FHIR repository will need to be re-created or updated to make it a candidate for pre-expansion. ValueSets that have yet to be pre-expanded will continue to be expanded in-memory.  
|  [#1431](https://github.com/hapifhir/hapi-fhir/issues/1431) |  The ValueSet operation `$validate-code` has been optimized for large ValueSets. Codes in ValueSets that have yet to be pre-expanded will continue to be validated in-memory.  
|  [#1447](https://github.com/hapifhir/hapi-fhir/issues/1447) |  LOINC filenames for terminology upload are now configurable using the `loincupload.properties` file.  
|  [#1451](https://github.com/hapifhir/hapi-fhir/issues/1451) |  Support for the LOINC `EXTERNAL_COPYRIGHT_NOTICE` property and `copyright` filter has been added.  
|  [#1453](https://github.com/hapifhir/hapi-fhir/issues/1453) |  Support for the LOINC `parent` and `child` filters has been added. Both filters can be used with either of the `=` or `in` operators.  
|  [#1454](https://github.com/hapifhir/hapi-fhir/issues/1454) |  Support for the LOINC `ancestor` and `descendant` filters has been added. The `descendant` filter can be used with either of the `=` or `in` operators. At present, the `ancestor` filter can only be used with the `=` operator.  
|  [#1512](https://github.com/hapifhir/hapi-fhir/issues/1512) |  Support for the LOINC `ancestor` filter with the `in` operator has been added.  
|  [#1517](https://github.com/hapifhir/hapi-fhir/issues/1517) |  Support for concept property values with a length exceeding 500 characters has been added in the terminology tables. In particular, this was added to facilitate the LOINC EXTERNAL_COPYRIGHT_NOTICE property, for which values can be quite long.  
|  |  The AuthorizationInterceptor has been enhanced so that a user can be authorized to perform create operations specifically, without authorizing all write operations. Also, conditional creates can now be authorized even if they are happening inside a FHIR transaction.  
|  |  A docker compose script for the hapi-fhir-jpaserver-starter project was added. Thanks to Long Nguyen for the pull request!  
|  [#1476](https://github.com/hapifhir/hapi-fhir/issues/1476) |  A number of overridden methods in the HAPI FHIR codebase did not have the @Override annotation. Thanks to Clayton Bodendein for cleaning this up!  
|  [#1373](https://github.com/hapifhir/hapi-fhir/issues/1373) |  Plain server resource providers were not correctly matching methods that had the _id search parameter if a client performed a request using a modifier such as :not or :exact. Thanks to Petro Mykhailyshyn for the pull request!  
|  |  Auto generated transaction IDs will now use both upper- and lowercase letters for more uniqueness in the same amount of space.  
|  [#1489](https://github.com/hapifhir/hapi-fhir/issues/1489) |  **Performance Improvement** : A significant performance improvement was made to the parsers (particularly the Json Parser) when serializing resources. This work yields improvements of 20-50% in raw encode speed when encoding large resources. Thanks to David Maplesden for the pull request!  
|  [#1541](https://github.com/hapifhir/hapi-fhir/issues/1541) |  The server CapabilityStatement (/metadata) endpoint now respects the Cache-Control header. Thanks to Jens Villadsen for the pull request!  
|  |  The @Metadata annotation now has an attribute that can be used to control the cache timeout  
|  [#1489](https://github.com/hapifhir/hapi-fhir/issues/1489) |  **Performance Improvement** : When running inside a JPA server, The DSTU3+ validator now performs code validations by directly testing ValueSet membership against a pre-calculated copy of the ValueSet, instead of first expanding the ValueSet and then examining the expanded contents. This can yield a significant improvement in validation speed in many cases.  
|  |  Validation errors will now include details about the line number where the issue was found  
|  |  A new built-in server interceptor called `CaptureResourceSourceFromHeaderInterceptor` has been added. This interceptor can be used to capture an incoming source system URI in an HTTP Request Header and automatically place it in `Resource.meta.source`  
|  [#1357](https://github.com/hapifhir/hapi-fhir/issues/1357) |  The email Subscription deliverer now respects the payload property of the subscription when deciding how to encode the resource being sent. Thanks to Sean McIlvenna for the pull request!  
|  [#1088](https://github.com/hapifhir/hapi-fhir/issues/1088) |  The CapabilityStatement generator will now determine supported profiles by navigating the complete hierarchy of supported resource types, instead of just using the root resource for each type. Thanks to Stig Døssing for the pull request!  
|  |  Two foreign keys have been dropped from the HFJ_SEARCH_RESULT table used by the FHIR search query cache. These constraints did not add value and caused unneccessary contention when used under high load.  
|  |  An inefficient regex expression in UrlUtil was replaced with a much more efficient hand-written checker. This regex was causing a noticable performance drop when feeding large numbers of transactions into the JPA server at the same time (i.e. when loading Synthea data).  
|  |  The HAPI FHIR CLI server now uses H2 as its database platform instead of Derby. Note that this means that data in any existing installations will need to be re-uploaded to the new database platform.  
|  |  REST servers will no longer try to guess the content type for HTTP requests where a body is provided but no Content-Type header is included. These requests are invalid, and will now result in an HTTP 400. This change corrects an error where some interceptors (notably the RequestValidatingInterceptor, but not including any HAPI FHIR security interceptors) could be bypassed if a Content Type was not included.  
|  |  The Testpage Overlay has been upgraded to use FontAwesome 5.x, and now supports being deployed to a servlet path other than "/".  
|  |  The hapi-fhir-jaxrs-server module now lists dependencies on structures JARs as optional dependencies, in order to avoid automatically importing all versions. This means that implementers of JAX-RS servers may now need to add an explicit dependency on one or more structures JARs to their own project.  
|  |  The `IValidationSupport#validateCode(...)` method has been modified to add an additional parameter (String theValueSetUrl). Most users will be unaffected by this change as HAPI FHIR provides a number of built-in implementations of this interface, but any direct user implementations of this interface will need to add the new parameter.  
|  |  The hapi-fhir-testpage-overlay project now uses WebJars for importing JavaScript dependency libraries. This reduces our Git repository size and should make it easier to stay up-to-date.  
|  |  The JPA server in DST2 mode previously automatically validated submitted QuestionnaireResponse resource against their corresponding Questionnaires and rejected non-conformant QuestionnaireResponse resources from being stored. This was in contrast to every other version where the RequestValidatingInterceptor has to be registered in order to achieve this specific behaviour. This behaviour has been removed from the JPA server, and the same interceptor must be used for QR validation in the DSTU2 JPA server as in all other versions of FHIR.  
|  |  DSTU2.1 profile validation now uses the same R5 validation as all other versions of FHIR.  
|  |  When using the _filter search parameter, string comparisons using the "eq" operator were incorrectly performing a partial match. This has been corrected. Thanks to Marc Sandberg for pointing this out!  
|  |  Reference search parameters did not work via the _filter parameter  
|  |  Transaction entries with a resource URL starting with a leading slash (e.g. `/Organization?identifier=foo` instead of `Organization?identifier=foo` did not work. These are now supported.  
|  |  SubscriptionDstu2Config incorrectly pointed to a DSTU3 configuration file. This has been corrected.  
|  |  When using the VersionedApiConverterInterceptor, GraphQL responses failed with an HTTP 500 error.  
|  |  Cascading deletes now correctly handle circular references. Previously this failed with an HTTP 500 error.  
|  |  The informational message returned in an OperationOutcome when a delete failed due to cascades not being enabled contained an incorrect example. This has been corrected.  
|  |  In some cases, deleting a CodeSystem resource would fail because the underlying codes were not correctly deleted from the terminology service tables. This is fixed.  
|  |  The FHIRPath engine used to parse search parameters in the JPA R4/R5 server is now reused across requests, as it is somewhat expensive to create and is thread safe.  
|  |  The GraphQL provider did not wrap the respone in a "data" element as described in the FHIR specification. This has been corrected.  
|  |  When using the Consent Service and denying a resource via the "Will See Resource" method, the resource ID and version were still returned to the user. This has been corrected so that no details about the resource are leaked.  
|  |  Fix a failure in FhirTerser#visit when fields in model classes being visited contain custom subclasses of the expected type.  
|  |  Updating an existing CodeSystem resource with a content mode of COMPLETE did not cause the terminology service to accurately reflect the new CodeSystem URL and/or concepts. This is now corrected.  
|  [#1495](https://github.com/hapifhir/hapi-fhir/issues/1495) |  A NullPointerException when using the AuthorizationInterceptor RuleBuilder to build a conditional rule with a custom tester has been corrected. Thanks to Tue Toft Nørgård for reporting!  
|  [#1494](https://github.com/hapifhir/hapi-fhir/issues/1494) |  The R4+ client and server modules did not recognize the new `_include:iterate` syntax that replaces the previous `_include:recurse` syntax. Both are now supported on all servers in order to avoid breaking backwards compatibility, with the new syntax now being emitted in R4+ clients.  
|  [#1482](https://github.com/hapifhir/hapi-fhir/issues/1482) |  The LOINC terminology distribution includes multiple copies of the same files. Uploading LOINC terminology resulted in some ValueSets with duplicate codes. This has been corrected by specifying a path with each filename.  
|  |  **New Feature** : A new set of operations have been added to the JPA server that allow CodeSystem deltas to be uploaded. A CodeSystem Delta consists of a set of codes and relationships that are added or removed incrementally to the live CodeSystem without requiring a downtime or a complete upload of the contents. Deltas may be specified using either a custom CSV format or a partial CodeSystem resource.   
In addition, the HAPI FHIR CLI `upload-terminology` command has been modified to support this new functionality.  
|  |  A corner case bug in the JPA server was solved: When performing a search that contained chained reference searches where the value contained slashes (e.g. `Observation?derived-from:DocumentReference.contenttype=application/vnd.mfer` ) the server could fail to load later pages in the search results.  
|  [#1483](https://github.com/hapifhir/hapi-fhir/issues/1483) |  Some resource IDs and URLs for LOINC ValueSets and ConceptMaps were inconsistently populated by the terminology uploader. This has been corrected.  
|  |  When a resource was updated with a meta.source containing a request id, the meta.source was getting appended with the new request id, resulting in an ever growing source.meta value. E.g. after the first update, it looks like "#9f0a901387128111#5f37835ee38a89e2" when it should only be "#5f37835ee38a89e2". This has been corrected.  
|  [#1421](https://github.com/hapifhir/hapi-fhir/issues/1421) |  The Plain Server method selector was incorrectly allowing client requests with _include statements to be handled by method implementations that did not have any `@IncludeParam` defined. This is now corrected. Thanks to Tuomo Ala-Vannesluoma for reporting and providing a test case!  
|  |  **New Feature** : When using Externalized Binary Storage in the JPA server, the system will now automatically externalize Binary and Attachment payloads, meaning that these will automatically not be stored in the RDBMS.  
|  |  The JPA server failed to find codes defined in not-present codesystems in some cases, and reported that the CodeSystem did not exist. This has been corrected.  
|  [#402](https://github.com/hapifhir/hapi-fhir/issues/402) |  When encoding a Composition resource in XML, the section narrative blocks were incorrectly replaced by the main resource narrative. Thanks to Mirjam Baltus for reporting!  
|  [#1473](https://github.com/hapifhir/hapi-fhir/issues/1473) |  AN issue with date pickers not working in the hapi-fhir-testpage-overlay project has been fixed. Thanks to GitHub user @jaferkhan for the pull request!  
|  |  **Model Update** : The DSTU3 structures have been upgraded to the new 3.0.2 (Technical Correction) release.  
The R4 structures have been upgraded to the new 4.0.1 (Technical Correction) release.  
The R5 structure have been upgraded to the current (October) snapshot.  
|  |  The JPA server contained a restriction on the columns used to hold a resource's type name that was too short to hold the longest name from the final R4 definitions. This has been corrected to account for names up to 40 characters long.  
|  |  The subscription triggering operation was not able to handle commas within search URLs being used to trigger resources for subscription checking. This has been corrected.  
|  |  In some cases where resources were recently expunged, null entries could be passed to JPA interceptors registered against the STORAGE_PRESHOW_RESOURCES hook.  
|  |  In issue was fixed in the JPA server where a previously failed search would be reused, immediately returning an error rather than retrying the search.  
|  |  The JPA server did not correctly process _has queries where the linked search parameter was the _id parameter.  
|  [#1529](https://github.com/hapifhir/hapi-fhir/issues/1529) |  HAPI FHIR allows transactions in DSTU3 to contain a JSON/XML Patch in a Binary resource without specifying a verb in Bundle.entry.request.method, since the valueset defined in DSTU3 for that field does not include the PATCH verb. The AuthorizationInterceptor however did not understand this and would reject these requests. This is now corrected.  
|  [#1530](https://github.com/hapifhir/hapi-fhir/issues/1530) |  A potential XXE vulnerability in the validator was corrected. The XML parser used for validating XML payloads (i.e. FHIR resources) will no longer read from DTD declarations.  
|  |  Paging requests that are incorrectly executed at the type level were interpreted by the plain server as search requests with no search parameters, leading to confusing search results. These will now result in an HTTP 400 error with a meaningful error message.  
|  [#1544](https://github.com/hapifhir/hapi-fhir/issues/1544) |  QuestionnaireResponse validation in the JPA server was not able to load Questionnaire resources that were referenced using a canonical URI instead of a local reference. Thanks to Vu Vuong for reporting!  
|  [#848](https://github.com/hapifhir/hapi-fhir/issues/848) |  When validating JSON payloads, the JSON structure was parsed by Gson instead of passing the raw JSON to the validator. This meant that the validator was unable to catch certain structural errors that are ignored by Gson. Thanks to Michael Lawley for reporting!  
|  [#1546](https://github.com/hapifhir/hapi-fhir/issues/1546) |  The JPA server exposed a number of duplicate entries in the CapabilityStatement's list of supported _include values for a given resource. Thanks to Jens Villadsen for reporting!  
|  [#1481](https://github.com/hapifhir/hapi-fhir/issues/1481) |  An unintended dependency from hapi-fhir-base on Jetty was introduced in HAPI FHIR 4.0.0. This has been removed.  
|  |  The JPA migrator tool was not able to correctly drop tables containing foreign key references in some cases. This has been corrected.  
|  [#1547](https://github.com/hapifhir/hapi-fhir/issues/1547) |  An issue was fixed where the JPA server would occasionally fail to save a resource because it contained a string containing characters that change length when normalized. Thanks to Tuomo Ala-Vannesluoma for the pull request!  
|  |  In the (fairly unlikely) circumstance that a JPA server was called with a parameter where the parameter name referenced a custom search parameter with an invalid chain attached, and the value was missing entirely (e.g. `ProcedureRequest?someCustomParameter.BAD_NAME=` , the server would ignore this parameter instead of incorrectly returning an error. This has been corrected.  
|  [#1526](https://github.com/hapifhir/hapi-fhir/issues/1526) |  Several issues with HAPI FHIR's annotation scanner that prevented use with Kotlin based resource providers have been corrected. Thanks to Jelmer ter Wal for the pull request!  
|  |  Search parameters of type URI did not work in the hapi-fhir-testpage-overlay. This has been corrected.  
|  [#1568](https://github.com/hapifhir/hapi-fhir/issues/1568) |  JPA servers accidentally stripped the type attribute from the server-exported CapabilityStatement when search parameters of type "special" were found. This has been corrected.  
|  |  When running the JPA server without Lucene indexing enabled and performing ValueSet expansion, the server would incorrectly ignore inclusion rules that specified a system but no codes (i.e. include the whole system). This has been corrected.  
|  [#1538](https://github.com/hapifhir/hapi-fhir/issues/1538) |  The hapi-fhir-testpage-overlay has been updated to support R5 endpoints. Thanks to Dazhi Jiao for the pull request!  
|  |  A NullPointerException in the XML Parser was fixed when serializing a resource containing an extension on a primitive datatype that was missing a URL declaration.  
|  |  When using the _filter search parameter in the JPA server with an untyped resource ID, the filter could bring in search results of the wrong type. Thanks to Anthony Sute for the Pull Request and Jens Villadsen for reporting!  
|  [#1300](https://github.com/hapifhir/hapi-fhir/issues/1300) |  In some cases where where a single search parameter matches the same resource many times with different distinct values (e.g. a search by Patient:name where there are hundreds of patients having hundreds of distinct names each) the Search Coordinator would end up in an infinite loop and never return all of the possible results. Thanks to @imranmoezkhan for reporting, and to Tim Shaffer for providing a reproducible test case!  
|  |  The method `IVersionSpecificBundleFactory#initializeBundleFromResourceList` has been deprecated, as it provided duplicate functionality to other methods and had an outdated argument list based on the Bundle needs in DSTU1. We are not aware of any public use of this API, please let us know if this deprecation causes any issues.  
|  |  HTTP PUT (resource update) operations will no longer allow the version to be specified in a Content-Location header. This behaviour was allowed in DSTU1 and was never removed from HAPI even though it hasn't been permitted in the spec for a very long time. Hopefully this change will not impact anyone!  
|  |  The @ProvidesResources annotation has been removed from HAPI FHIR, as it was not documented and did not do anything useful. Please get in touch if this causes any issues.  
#  0.7.4HAPI FHIR 4.0.3 (Igloo (Point Release))
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#change4.1.0-0)
##  0.7.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#release-information-1)
**Released:** 2019-09-03
**Codename:** (Igloo (Point Release))
##  0.7.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changes-1)
|  |  This release contains no new or updated functionality, but addressed a dependency version that was left incorrectly requiring a SNAPSHOT maven build of the org.hl7.fhir.utilities module. Users who are successfully using HAPI FHIR 4.0.0 do not need to upgrade, but any users who were blocked from upgrading due to snapshot dependency issues are advised to upgrade immediately.  
---|---|---  
#  0.7.5HAPI FHIR 4.0.0 (Igloo)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#change4.0.3-0)
##  0.7.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#release-information-2)
**Released:** 2019-08-14
**Codename:** (Igloo)
##  0.7.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changes-2)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Commons Codec (Core): 1.11 -> 1.12
  * Apache HTTPClient (Client): 4.5.3 -> 4.5.9
  * Apache HTTPCore (Client>: 4.4.6 -> 4.4.11
  * Spring (JPA): 5.1.6.RELEASE -> 5.1.8.RELEASE
  * Spring-Data (JPA): 2.1.6.RELEASE -> 2.1.8.RELEASE
  * JANSI (CLI): 1.17.1 -> 1.18
  * json-patch (JPA): 1.10 -> 1.15 (see changelog entry about this change)
  * Jackson-Databind (JPA): 2.9.9 -> 2.9.9.1 (due to a Jackson vulnerability CVE-2019-12384)
  * commons-collections4 (Server/JPA): 4.1 -> 4.3
  * commons-dbcp2 (JPA): 2.5.0 -> 2.6.0
  * commons-lang3 (Core): 3.8.1 -> 3.9
  * commons-text (Core): 1.6 -> 1.7
  * Guava (JPA): 27.1-jre -> 28.0-jre

  
---|---|---  
|  |  **New Feature** : A new interceptor called CascadingDeleteInterceptor has been added to the JPA project. This interceptor allows deletes to cascade when a specific URL parameter or header is added to the request. Cascading deletes can also be controlled by a new flag in the AuthorizationInterceptor RuleBuilder, in order to ensure that cascading deletes are only available to users with sufficient permission.  
|  |  Several enhancements have been made to the `AuthorizationInterceptor` : 
  * The interceptor now registers against the `STORAGE_PRESHOW_RESOURCES` interceptor hook, which allows it to successfully authorize JPA operations that don't actually return resource content, such as GraphQL responses, and resources that have been filtered using the `_elements` parameter.

  
|  |  The $expunge global everything operation has been refactored to do deletes in small batches. This change will likely reduce performance, but does allow for the operation to succeed without timing out in larger systems.  
|  |  Added some experimental version-independent model classes to ca.uhn.fhir.jpa.model.any. They permit writing code that is version independent.  
|  |  Added new subclass of HashMapResourceProvider called SearchableHashMapResourceProvider that uses the in-memory matcher to search the HashMap (using a full table scan). This allows rudimentary testing without a database.  
|  |  Added a new interceptor hook called STORAGE_PRESTORAGE_DELETE_CONFLICTS that is invoked when a resource delete operation is about to fail due to referential integrity conflicts. Hooks have access to the list of resources that have references to the resource being deleted and can delete them. The boolean return value of the hook indicates whether the server should try checking for conflicts again (true means try again).  
|  [#1336](https://github.com/hapifhir/hapi-fhir/issues/1336) |  The HAPI FHIR unit test suite has been refactored to no longer rely on PortUtil to assign a free port. This should theoretically result in fewer failed builds resulting from port conflicts. Thanks to Stig Døssing for the pull request!  
|  [#1348](https://github.com/hapifhir/hapi-fhir/issues/1348) |  JPA server now supports conditional PATCH operation (i.e. performing a patch with a syntax such as `/Patient?identifier=sys|val` )  
|  [#1347](https://github.com/hapifhir/hapi-fhir/issues/1347) |  The json-patch library used in the JPA server has been changed from [java-json-tools.json-patch](https://github.com/java-json-tools/json-patch) to a more active fork of the same project: [crate-metadata.json-patch](https://github.com/crate-metadata/json-patch). Thanks to Jens Villadsen for the suggestion and pull request!  
|  [#1343](https://github.com/hapifhir/hapi-fhir/issues/1343) |  Support has been implemented in the JPA server for the CodeSystem `$subsumes` operation.  
|  |  A new pointcut has been added to the JPA server called `JPA_PERFTRACE_RAW_SQL` that can be used to capture the raw SQL statements that are sent to the underlying database.  
|  |  The JPA server now rejects subscriptions being submitted with no value in Subscription.status (this field is mandatory, but the subscription was previously ignored if no value was provided)  
|  |  The JSON and XML parsers will now raise a warning or error with the Parser Error Handler if an extension is being encoded that is missing a URL, or has both a value and nested extensions on the same parent extension.  
|  [#1330](https://github.com/hapifhir/hapi-fhir/issues/1330) |  Support in the JPA Terminology Service terminology uploader has been added for uploading the IMGT [HLA Nomenclature](http://hla.alleles.org/nomenclature/index.html) distribution files as a FHIR CodeSystem. Thanks to Joel Schneider for the contribution!  
|  [#1354](https://github.com/hapifhir/hapi-fhir/issues/1354) |  A BOM POM has been added to the HAPI FHIR distribution, allowing users to import the HAPI FHIR library with all of its submodules automatically sharing the same version. Thanks to Stig Døssing for the pull request!  
|  |  AuthorizationInterceptor will now try to block delete operations sooner in the processing lifecycle if there is no chance they will be permitted later (i.e. because the type is not authorized at all)  
|  |  The HAPI FHIR server will now generate a random transaction ID to every request and add it to the response headers. Clients may supply the transaction header via the `X-Request-ID` header.  
|  |  When attempting to read a resource that is deleted, a Location header is now returned that includes the resource ID and the version ID for the deleted resource.  
|  |  IBundleProvider now has an isEmpty() method that can be used to check whether any results exist. A default implementation has been provided, so this is not a breaking change.  
|  |  A new server interceptor hook called PROCESSING_COMPLETED has been added. This hook is called by the server at the end of processing every request (success and failure).  
|  |  Added a new Pointcut STORAGE_PRESTORAGE_EXPUNGE_EVERYTHING that is called at the start of the expungeEverything operation.  
|  |  The JPA server now has the ability to generate snapshot profiles from differential profiles via the $snapshot operation, and will automatically generate a snapshot when needed for validation.  
|  |  Creating/updating CodeSystems now persist `CodeSystem.concept.designation` to the terminology tables.  
|  |  Expanded ValueSets now populate `ValueSet.expansion.contains.designation.language` .  
|  |  @Operation methods can now declare that they will manually process the request body and/or manually generate a response instead of letting the HAPI FHIR framework take care of these things. This is useful for situations where direct access to the low-level servlet streaming API is needed.  
|  |  @Operation methods can now declare that they are global, meaning that they will apply to all resource types (or instances of all resource types) if they are found on a plain provider.  
|  |  @Operation method parameters may now declare their type via a String name such as "code" or "Coding" in an attribute in @OperationParam. This is useful if you want to make operation methods that can operate across different versions of FHIR.  
|  |  A new resource provider for JPA servers called `BinaryAccessProvider` has been added. This provider serves two custom operations called `$binary-access-read` and `$binary-access-write` that can be used to request binary data in Attachments as raw binary content instead of as base 64 encoded content.  
|  |  **New Feature** : Support for the new R5 draft resources has been added. This support includes the client, server, and JPA server. Note that these definitions will change as the R5 standard is modified until it is released, so use with caution!  
|  |  Support for PATCH operations performed within a transaction (using a Binary resource as the resource type in order to hold a JSONPatch or XMLPatch body) has been added to the JPA server.  
|  |  A new attribute has been added to the @Operation annotation called `typeName` . This annotation can be used to specify a type for an operation declared on a plain provider without needing to use a specific version of the FHIR structures.  
|  |  The $upload-external-code-system operation and the corresponding HAPI FHIR CLI command can now be used to upload custom vocabulary that has been converted into a standard file format defined by HAPI FHIR. This is useful for uploading large organizational code systems.  
|  [#1388](https://github.com/hapifhir/hapi-fhir/issues/1388) |  When performing a read-if-newer operation on a plain server, the resource ID in Resource.meta.versionId is now used if a version isn't found in the resource ID itself. Thanks to Stig Døssing for the pull request!  
|  |  **New Feature** : A new interceptor called `ConsentInterceptor` has been added. This interceptor allows JPA based servers to make appropriate consent decisions related to resources that and operations that are being returned. See Server Security for more information.  
|  |  **New Feature** : The JPA server now supports GraphQL for DSTU3 / R4 / R5 servers.  
|  [#1220](https://github.com/hapifhir/hapi-fhir/issues/1220) |  **New Feature** : The JPA server now supports the `_filter` search parameter when configured to do so. The [filter search parameter](http://hl7.org/fhir/search_filter.html) is an extremely flexible and powerful feature, allowing for advanced grouping and order of operations on searches. It can be dangerous however, as it potentially allows users to create queries for which no database indexes exist in the default configuration so it is disabled by default. Thanks to Anthony Sute for the pull request and all of his support in what turned out to be a lengthy merge!  
|  |  **Breaking Change** : The HL7.org DSTU2 structures (and _ONLY_ the HL7.org DSTU2 structures) have been moved to a new package. Where they were previously found in `org.hl7.fhir.instance.model` they are now found in `org.hl7.fhir.dstu2.model`. This was done in order to complete the harmonization between the [HAPI FHIR](https://github.com/hapifhir/hapi-fhir) GitHub repository and the [org.hl7.fhir.core](https://github.com/hapifhir/org.hl7.fhir.core/) GitHub repository. This is the kind of change we don't make lightly, as we do know that it will be annoying for users of the existing library. It is a change however that will allow us to apply validator fixes much more quickly, and will greatly reduce the amount of effort required to keep up with R5 changes as they come out, so we're hoping it is worth it. Note that no classes are removed, they have only been moved, so it should be fairly straightforward to migrate existing code with an IDE.  
|  |  Moved in-memory matcher from Subscription module to SearchParam module and renamed the result type from SubscriptionMatchResult to InMemoryMatchResult.  
|  |  **Breaking Change** : The `IPagingProvider` interface has been modified so that the `retrieveResultList` method now takes one additional parameter of type `RequestDetails`. If you have created a custom implementation of this interface, you can add this parameter and ignore it if needed. The use of the method has not changed, so this should be an easy fix to existing code.  
|  |  **Breaking Change** : The HAPI FHIR REST client and server will now default to using JSON encoding instead of XML when the user has not explicitly configured a preference.  
|  |  **Breaking Change** : The JPA $upload-external-code-system operation has been moved from being a server level operation (i.e. called on the root of the server) to being a type level operation (i.e. called on the CodeSystem type).  
|  |  Server CapabilityStatement/Conformance repsonses from the /metadata endpoint will now be cached for 60 seconds always. This was previously a configurable setting on the ServerConformanceProvider, but it is now handled directly by the method binding so the provider now has no responsibility for caching.  
|  |  The JPA server now uses the H2 database instead of the derby database to run its unit tests. We are hoping that this cuts down on the number of false test failures we get due to mysterious derby failures.  
|  |  **Breaking Change** : The FhirValidator#validate(IResource) method has been removed. It was deprecated in HAPI FHIR 0.7 and replaced with FhirValidator#validateWithResults(IBaseResource) so it is unlikely anyone is still depending on the old method.  
|  |  The Base64Binary types for DSTU3+ now use a byte array internally to represent their content, which is more efficient than storing base 64 encoded text to represent the binary as was previously done.  
|  |  A few columns named 'CODE' in the JPA terminology services tables have been renamed to 'CODEVAL' to avoid any possibility of conflicting with reserved words in MySQL. The database migrator tool has been updated to handle this change.  
|  |  Two new operations, `$apply-codesystem-delta-add` and `$apply-codesystem-delta-remove` have been added to the terminology server. These methods allow codes to be dynamically added and removed from external (notpresent) codesystems.  
|  |  The JPA server did not correctly index Timing fields where the timing contained a period but no individual events. This has been corrected.  
|  [#1320](https://github.com/hapifhir/hapi-fhir/issues/1320) |  The HAPI FHIR CLI import-csv-to-conceptmap command was not accounting for byte order marks in CSV files (e.g. some Excel CSV files). This has been fixed.  
|  [#1241](https://github.com/hapifhir/hapi-fhir/issues/1241) |  A bug was fixed where deleting resources within a transaction did not always correctly enforce referential integrity even if referential integrity was enabled. Thanks to Tuomo Ala-Vannesluoma for reporting!  
|  |  In the JPA server, the `_total=accurate` was not always respected if a previous search already existed in the query cache that matched the same search parameters.  
|  [#1337](https://github.com/hapifhir/hapi-fhir/issues/1337) |  Improved stability of concurrency test framework. Thanks to Stig Døssing for the pull request!  
|  |  AuthorizationInterceptor sometimes failed with a 500 error when checking compartment membership on a resource that has a contained subject (Patient).  
|  |  Uploading the LOINC/RSNA Radiology Playbook would occasionally fail when evaluating part type names due to case sensitivity. This has been corrected.  
|  [#1355](https://github.com/hapifhir/hapi-fhir/issues/1355) |  Invoking the transaction or batch operation on the JPA server would fail with a NullPointerException if the Bundle passed in did not contain a resource in an entry that required a resource (e.g. a POST). Thanks to GitHub user @lytvynenko-dmitriy for reporting!  
|  [#1250](https://github.com/hapifhir/hapi-fhir/issues/1250) |  HAPI FHIR Server (plain, JPA, and JAX-RS) all populated Bundle.entry.result on search result bundles, even though the FHIR specification states that this should not be populated. This has been corrected. Thanks to GitHub user @gitrust for reporting!  
|  [#1352](https://github.com/hapifhir/hapi-fhir/issues/1352) |  Creating R4 Observation resources with a value type of SampledData failed in the JPA server because of an indexing error. Thanks to Brian Reinhold for reporting!  
|  [#1361](https://github.com/hapifhir/hapi-fhir/issues/1361) |  Fix a build failure thanks to Maven pom errors. Thanks to Gary Teichrow for the pull request!  
|  [#1362](https://github.com/hapifhir/hapi-fhir/issues/1362) |  The JPA server did not correctly process searches with a `_tag:not` expression containing more than one comma separated value.  
|  |  FHIR model classes have a method called `hasPrimitiveValue()` which previously returned true if the type was a primitive datatype (e.g. StringType). This method now only returns true if the type is a primitive datatype AND the type actually has a value.  
|  |  A number of columns in the JPA Terminology Services ConceptMap tables were not explicitly annotated with @Column, so the DB columns that were generated had Java ugly field names as their SQL column names. These have been renamed, and entries in the JPA migrator tool have been added for anyone upgrading.  
|  |  Field values with a datatype of `canonical` were indexed as though they were explicit resource references by the JPA server. This led to errors about external references not being supported when uploading various resources (e.g. Questionnaires with HL7-defined ValueSet references). This has been corrected. Note that at this time, we do not index canonical references at all (as we were previously doing it incorrectly). This will be improved soon.  
|  [#1370](https://github.com/hapifhir/hapi-fhir/issues/1370) |  The OkHttp client did not correctly apply the connection timeout and socket timeout settings to client requests. Thanks to Petro Mykhailyshyn for the pull request!  
|  |  The `_summary` element was not always respected when encoding JSON resources.  
|  [#1390](https://github.com/hapifhir/hapi-fhir/issues/1390) |  Two issues in the Thymeleaf Narrative Template which caused an error when generating a narrative on an untitled DiagnosticReport were fixed. Thanks to GitHub user @navyflower for reporting!  
|  [#1404](https://github.com/hapifhir/hapi-fhir/issues/1404) |  In the JAX-RS server, the resource type history and instance vread operations had ambiguous paths that could lead to the wrong method being called. Thanks to Seth Rylan Gainey for the pull request!  
|  [#1414](https://github.com/hapifhir/hapi-fhir/issues/1414) |  The profile validator (FhirInstanceValidator) can now be used to validate a resource using an explicit profile declaration rather than simply relying on the declared URL in the resource itself.  
|  |  When using the ResponseHighlighterInterceptor, some invalid requests that would normally generate an HTTP 400 response (e.g. an invalid _elements value) would cause an HTTP 500 crash.  
|  [#1375](https://github.com/hapifhir/hapi-fhir/issues/1375) |  An example datatype was corrected in the DSTU2 Identifier datatype StructureDefinition. Thanks to Nick Robison for the pull request!  
#  0.7.6HAPI FHIR 3.8.0 (Hippo)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#change4.0.0-0)
##  0.7.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#release-information-3)
**Released:** 2019-05-30
**Codename:** (Hippo)
##  0.7.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changes-3)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Guava (base): 25-jre -> 27.1-jre
  * Hibernate (JPA): 5.4.1 -> 5.4.2
  * Jackson (JPA): 2.9.7 -> 2.9.8
  * Spring (JPA): 5.1.3.RELEASE -> 5.1.6.RELEASE
  * Spring-Data (JPA): 2.1.3.RELEASE -> 2.1.6.RELEASE
  * Caffeine (JPA): 2.6.2 -> 2.7.0
  * JANSI (CLI): 1.16 -> 1.17.1

  
---|---|---  
|  |  ParametersUtil now has a utility method that can be used to add parameter values using the string name of the datatype (e.g. "dateTime") in order to help building Parameters resources in a version-independent way.  
|  |  In the JPA server, a much more readable error message is now returned returned when two client threads collide while trying to simultaneously create a resource with the same client-assigned ID. In addition, better error messages are now returned when conflicts such as this one are hit within a FHIR transaction operation.  
|  |  The JPA query builder has been optimized to take better advantage of SQL IN (..) expressions when performing token searches with multiple OR values.  
|  |  The JPA server transaction processor will now automatically detect if the request Bundle contains multiple entries having identical conditional create operations, and collapse these into a single operation. This is done as a convenience, since many conversion algorithms can accidentally generate such duplicates.  
|  |  A new config setting has been added to the JPA DaoConfig that disables validation of the resource type for target resources in references.  
|  |  HapiLocalizer can now handle message patterns with braces that aren't a part of a message format expression. E.g. "Here is an {example}".  
|  |  JPA searches using a Composite Unique Index will now use that index for faster searching even if the search has _includes and/or _sorts. Previously these two features caused the search builder to skip using the index.  
|  |  In Servers that are configured to support extended mode `_elements` parameters, it is now possible to use the :exclude modifier to exclude entire resource types.  
|  |  When performing a search in the JPA server where one of the parameters is a reference with multiple values (e.g. Patient?organization=A,B) the generated SQL was previously a set of OR clauses and this has been collapsed into a single IN clause for better performance.  
|  |  RequestDetails now has methods called getAttribute and setAttribute that can be used by interceptors to pass arbitrary data between requests.  
|  |  Added new configuration parameter to DaoConfig and ModelConfig to specify the websocket context path. (Before it was hardcoded to "/websocket").  
|  |  Added new IRemovableChannel interface. If a SubscriptionChannel implements this, then when a subscription channel is destroyed (because its subscription is deleted) then the remove() method will be called on that channel.  
|  |  When performing a JSON Patch in JPA server, the post-patched document is now validated to ensure that the patch was valid for the candidate resource. This means that invalid patches are caught and not just silently ignored.  
|  |  Expunges are now done in batches in multiple threads. Both the number of expunge threads and batch size are configurable in DaoConfig.  
|  |  ValidationSupportChain will now call isCodeSystemSupported() on each entry in the chain before calling fetchCodeSystem() in order to reduce the work required by chain entries. Thanks to Anders Havn for the suggestion!  
|  |  The hapi-fhir-jpaserver-starter project has been updated to use a properties file for configuration, making it much easier to get started with this project. Thanks to Sean McIlvenna for the pull request!  
|  [#1228](https://github.com/hapifhir/hapi-fhir/issues/1228) |  The InstanceValidator now supports validating QuestionnairResponses with empty items for disabled questions. Thanks to Matti Uusitalo for the pull request!  
|  [#1152](https://github.com/hapifhir/hapi-fhir/issues/1152) |  A new method has been added to the client that allows arbitrary headers to be easily added to the request. Thanks to Christian Ohr for the pull request!  
|  [#1213](https://github.com/hapifhir/hapi-fhir/issues/1213) |  VersionConverter for R2-R3 has been modified to correectly handle the renamed basedOn field. Thanks to Gary Graham for the pull request!  
|  [#1244](https://github.com/hapifhir/hapi-fhir/issues/1244) |  Add a missing @Deprecated tag. Thanks to Drew Mitchell for the pull request!  
|  [#1303](https://github.com/hapifhir/hapi-fhir/issues/1303) |  The JSON parser has removed a few unneeded super keywords that prevented overriding behaviour. Thanks to Anders Havn for the pull request!  
|  [#1179](https://github.com/hapifhir/hapi-fhir/issues/1179) |  The DSTU2/3 version converter now converts Specimen resources. Thanks to Gary Graham for the pull request!  
|  |  The JPA terminology service can now detect when Hibernate Search (Lucene) is not enabled, and will perform simple ValueSet expansions without relying on Hibenrate Search in such cases.  
|  |  The hapi-fhir-testpage-overlay project no longer includes any library JARs in the built WAR, in order to prevent duplicates and conflicts in implementing projects.  
|  |  The JSON Patch provider has been switched to use the provider from the [Java JSON Tools](https://github.com/java-json-tools/json-patch) project, as it is much more robust and fault tolerant.  
|  |  Re-use subscription channel and handlers when a subscription is updated (unless the channel type changed).  
|  [#1209](https://github.com/hapifhir/hapi-fhir/issues/1209) |  A Google Analytics script fragment was leftover in the hapi-fhir-jpaserver example and starter projects. Thanks to Patrick Werner for removing these!  
|  |  A potential security vulnerability in the hapi-fhir-testpage-overlay project was corrected: A URL parameter was not being correctly escaped, leading to a potential XSS vulnerability. A big thanks to Mudit Punia and Dushyant Garg for reporting this.  
|  |  When performing a search using the JPA server, if a search returned between 1500 and 2000 results, a query for the final page of results would timeout due to a page calculation error. This has been corrected.  
|  [#1223](https://github.com/hapifhir/hapi-fhir/issues/1223) |  Searching the JPA server with multiple instances of the same token search parameter (e.g. "Patient?identifier=&identifier=b" returned no results even if resources should have matched. Thanks to @mingdatacom for reporting!  
|  |  JPA searches using a Composite Unique Index did not return the correct results if a REFERENCE search parameter was used with arguments that consisted of unqualified resource IDs.  
|  |  A non-threadsafe use of DateFormat was cleaned up in the StopWatch class.  
|  |  When returning the results of a history operation from a HAPI FHIR server, any entries with a method of DELETE contained a stub resource in Bundle.entry.resource, even though the FHIR spec states that this field should be empty. This was corrected.  
|  |  Two expunge bug fixes: The first bug is that the expunge operation wasn't bailing once it hit its limit. This resulted in a "Page size must not be less than one!" error. The second bug is that one case wasn't properly handled: when a resourceId with no version is provided. This executed the case where only resource type is provided.  
|  |  When updating a resource in the JPA server, if the contents have not actually changed the resource version is not updated and no new version is created. In this situation, the update time was modified however. It will no longer be updated.  
|  |  When running the JPA server in Resource Client ID strategy mode of "ANY", using the `_id` search parameter could return incorrect results. This has been corrected.  
|  |  Performing a PUT or POST against a HAPI FHIR Server with no request body caused an HTTP 500 to be returned instead of a more appropriate HTTP 400. This has been corrected.  
|  [#1255](https://github.com/hapifhir/hapi-fhir/issues/1255) |  The fetchValueSet method on IValidationSupport implementation was not visible and could not be overridden. Thanks to Patrick Werner for the pull reuqest!  
|  [#1280](https://github.com/hapifhir/hapi-fhir/issues/1280) |  The JPA server failed to index R4 reources with search parameters pointing to the Money data type. Thanks to GitHub user @navyflower for reporting!  
|  |  When validating DSTU3 QuestionnaireResponses that leverage the "enableWhen" functionality available in Questionnaire resources, the validation could sometimes fail incorrectly.  
|  |  Ensure that database cursors are closed immediately after performing a FHIR search.  
|  |  Validation errors were fixed when using a Questionnaire with enableWhen on a question that contains sub-items.  
|  |  Fixed "because at least one resource has a reference to this resource" delete error message that mistakingly reported the target instead of the source with the reference.  
|  [#1299](https://github.com/hapifhir/hapi-fhir/issues/1299) |  In JPA server when updating a resource using a client assigned ID, if the resource was previously deleted (meaning that the operation is actually a create), the server will now return an HTTP 201 instead of an HTTP 200. Thanks to Mario Hyland for reporting!  
|  |  The HAPI FHIR CLI was unable to start a server in R4 mode in HAPI FHIR 3.7.0. This has been corrected.  
|  [#1311](https://github.com/hapifhir/hapi-fhir/issues/1311) |  When encoding resources, profile declarations on contained resources will now be preserved. Thanks to Anders Havn for the pull request!  
|  [#1305](https://github.com/hapifhir/hapi-fhir/issues/1305) |  Two incorrect package declarations in unit tests were corrected. Thanks to github user @zaewonyx for the PR!  
|  [#1141](https://github.com/hapifhir/hapi-fhir/issues/1141) |  The JPA database migration tool has been enhanced to support migration from HAPI FHIR 2.5. Thanks to Gary Graham for the pull request!  
|  |  The hapi-fhir-jpaserver-example did not have Subscription capabilities enabled after the refactoring of how Subscriptions are enabled that occurred in HAPI FHIR 3.7.0. Thanks to Volker Schmidt for the pull request!  
|  |  When using the `_elements` parameter on searches and reads, requesting extensions to be included caused the extensions to be included but not any values contained within. This has been corrected.  
#  0.7.7HAPI FHIR 3.7.0 (Gale)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#change3.8.0-1)
##  0.7.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#release-information-4)
**Released:** 2019-02-06
**Codename:** (Gale)
##  0.7.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2019.html#changes-4)
|  |  HAPI FHIR is now built using OpenJDK 11. Users are recommended to upgrade to this version of Java if this is feasible. We are not yet dropping support for Java 8 (aka 1.8), but users are recommended to upgrade if possible.  
---|---|---  
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Spring (JPA): 5.0.8.RELEASE -> 5.1.3.RELEASE
  * Spring-Data (JPA): 2.0.7.RELEASE -> 2.1.3.RELEASE
  * Hibernate-Core (JPA): 5.3.6.FINAL -> 5.4.1.FINAL
  * Hibernate-Search (JPA): 5.10.3.FINAL -> 5.11.1.FINAL
  * Thymeleaf (JPA): 3.0.9.RELEASE -> 3.0.11.RELEASE
  * thymeleaf-spring4 (Testpage Overlay) has been replaced with thymeleaf-spring5
  * Commons-Lang3: 3.8 -> 3.8.1
  * Commons-Text: 1.4 -> 1.4
  * Spring Boot: 1.5.6.RELEASE -> 2.1.1.RELEASE

  
|  |  FHIR Parser now has an additional overload of the `parseResource` method that accepts an InputStream instead of a Reader as the source.  
|  |  FHIR Fluent/Generic Client now has a new return option called `returnMethodOutcome` which can be used to return a raw response. This is handy for invoking operations that might return arbitrary binary content.  
|  |  Moved state and functionality out of BaseHapiFhirDao.java into new classes: LogicalReferenceHelper, ResourceIndexedSearchParams, IdHelperService, SearcchParamExtractorService, and MatchUrlService.  
|  |  Replaced explicit @Bean construction in BaseConfig.java with @ComponentScan. Beans with state are annotated with @Component and stateless beans are annotated as @Service. Also changed SearchBuilder.java and the three Subscriber classes into @Scope("protoype") so their dependencies can be @Autowired injected as opposed to constructor parameters.  
|  |  AuthorizationInterceptor now allows arbitrary FHIR $operations to be authorized, including support for either allowing the operation response to proceed unchallenged, or authorizing the contents of the response.  
|  |  JPA Migrator tool enhancements: An invalid SQL syntax issue has been fixed when running the CLI JPA Migrator tool against Oracle or SQL Server. In addition, when using the "Dry Run" option, all generated SQL statements will be logged at the end of the run. Also, a case sensitivity issue when running against some Postgres databases has been corrected.  
|  |  In the JPA server, when performing a chained reference search on a search parameter with a target type of `Reference(Any)` , the search failed with an incomprehensible error. This has been corrected to return an error message indicating that the chain must be qualified with a resource type for such a field. For example, `QuestionnaireResponse?subject:Patient.name=smith` instead of `QuestionnaireResponse?subject.name=smith` .  
|  |  Changed subscription processing, if the subscription criteria are straightforward (i.e. no chained references, qualifiers or prefixes) then attempt to match the incoming resource against the criteria in-memory. If the subscription criteria can't be matched in-memory, then the server falls back to the original subscription matching process of querying the database. The in-memory matcher can be disabled by setting isEnableInMemorySubscriptionMatching to "false" in DaoConfig (by default it is true). If isEnableInMemorySubscriptionMatching is "false", then all subscription matching will query the database as before.  
|  |  The LOINC uploader has been updated to suport the LOINC 2.65 release file format.  
|  |  The resource reindexer can now detect when a resource's current version no longer exists in the database (e.g. because it was manually expunged), and can automatically adjust the most recent version to account for this.  
|  |  When updating existing resources, the JPA server will now attempt to reuse/update rows in the index tables if one row is being removed and one row is being added (e.g. because a Patient's name is changing from "A" to "B"). This has the net effect of reducing the number  
|  |  Plain Server ResourceProvider classes are no longer required to be public classes. This limitation has always been enforced, but did not actually serve any real purpose so it has been removed.  
|  |  A new interceptor called ServeMediaResourceRawInterceptor has been added. This interceptor causes Media resources to be served as raw content if the client explicitly requests the correct content type cia the Accept header.  
|  [#917](https://github.com/hapifhir/hapi-fhir/issues/917) |  A new configuration item has been added to the FhirInstanceValidator that allows you to specify additional "known extension domains", meaning domains in which the validator will not complain about when it encounters new extensions. Thanks to Heinz-Dieter Conradi for the pull request!  
|  |  Added 3 interfaces for services required by the standalone subscription server. The standalone subscription server doesn't have access to a database and so needs to get its resources using a FhirClient. Thus for each of these interfaces, there are two implementations: a Dao implementaiton and a FhirClient implementation. The interfaces thus introduced are ISubscriptionProvider (used to load subscriptions into the SubscriptionRegistry), the IResourceProvider (used to get the latest version of a resource if the "get latest version" flag is set on the subscription) and ISearchParamProvider used to load custom search parameters.  
|  [#1051](https://github.com/hapifhir/hapi-fhir/issues/1051) |  FHIR Servers now support the HTTP HEAD method for FHIR read operations. Thanks to GitHub user Cory00 for the pull request!  
|  |  It is now possible in a plain or JPA server to specify the default return type for create/update operations when no Prefer header has been provided by the client.  
|  |  It is now possible in a JPA server to specify the _total calculation behaviour if no parameter is supplied by the client. This is done using a new setting on the DaoConfig. This can be used to force a total to always be calculated for searches, including large ones.  
|  |  AuthorizationInterceptor now rejects transactions with an invalid or unset request using an HTTP 422 response Bundle type instead of silently refusing to authorize them.  
|  |  AuthorizationInterceptor is now able to authorize DELETE operations performed via a transaction operation. Previously these were always denied.  
|  [#1065](https://github.com/hapifhir/hapi-fhir/issues/1065) |  OperationDefinitions are now created for named queries in server module. Thanks to Stig Døssing for the pull request!  
|  |  A new server interceptor has been added called "SearchNarrowingInterceptor". This interceptor can be used to automatically narrow the scope of searches performed by the user to limit them to specific resources or compartments that the user should have access to.  
|  |  In a DSTU2 server, if search parameters are expressed with chains directly in the parameter name (e.g. `@RequiredParam(name="subject.name.family")` ) the second part of the chain was lost when the chain was described in the server CapabilityStatement. This has been corrected.  
|  |  A wrapper script for Maven has been added, enabling new users to use Maven without having to install it beforehand. Thanks to Ari Ruotsalainen for the Pull Request!  
|  |  AuthorizationInterceptor can now allow a user to perform a search that is scoped to a particular resource (e.g. Patient?_id=123) if the user has read access for that specific instance.  
|  |  Changed behaviour of FHIR Server to reject subscriptions with invalid criteria. If a Subscription is submitted with invalid criteria, the server returns HTTP 422 "Unprocessable Entity" and the Subscription is not persisted.  
|  |  HAPI FHIR will now log the Git revision when it first starts up (on the ame line as the version number that it already logs).  
|  |  Added support for _id in in-memory matcher  
|  |  Add a "subscription-matching-strategy" meta tag to incoming subscriptions with value of IN_MEMORY or DATABASE indicating whether the subscription can be matched against new resources in-memory or whether a call out to the database may be required. I say "may" because subscription matches fail fast so a negative match may be performed in-memory, but a positive match will require a database call.  
|  [#1148](https://github.com/hapifhir/hapi-fhir/issues/1148) |  Support for validating enableWhen in Questionnaires has been added to the Validator. Thanks to Eeva Turkka and Matti Uutsitalo for the pull request!  
|  [#1117](https://github.com/hapifhir/hapi-fhir/issues/1117) |  A badly formatted log message when handing exceptions was cleaned up. Thanks to Magnus Watn for the pull request!  
|  |  AuthorizationInterceptor now allows the GraphQL operation to be authorized. Note that this is an all-or-nothing grant for now, it is not yet possible to specify individual resource security when using GraphQL.  
|  |  The JPA stale search deletion service now deletes cached search results in much larger batches (20000 instead of 500) in order to reduce the amount of noise in the logs.  
|  |  In example-projects/README.md and hapi-fhir-jpaserver-example/README.md, incidate that these examples projects are no longer maintained. The README.md points users to a starter project they should use for examples.  
|  |  Replaced use of BeanFactory with custom factory classes that Spring @Lookup the @Scope("prototype") beans (e.g. SearchBuilderFactory).  
|  |  Removed BaseSubscriptionInterceptor and all its subclasses (RestHook, EMail, WebSocket). These are replaced by two new interceptors: SubscriptionActivatingInterceptor that is responsible for activating subscriptions and SubscriptionMatchingInterceptor that is responsible for matching incoming resources against activated subscriptions. Call DaoConfig.addSupportedSubscriptionType(type) to configure which subscription types are supported in your environment. If you are processing subscriptions on a separate server and only want to activate subscriptions on this server, you should set DaoConfig.setSubscriptionMatchingEnabled to false. The helper method SubscriptionInterceptorLoader.registerInterceptors() will check if any subscription types are supported, and if so then load active subscriptions into the SubscriptionRegistry and register the subscription activating interceptor. This method also registers the subscription matching interceptor (that matches incoming resources and sends matches to subscription channels) only if DaoConfig.isSubscriptionMatchingEnabled is true. See https://github.com/hapifhir/hapi-fhir/wiki/Proposed-Subscription-Design-Change for more details.  
|  |  Moved e-mail from address configuration from EmailInterceptor (which doesn't exist any more) to DaoConfig.  
|  |  Separated active subscription cache from the interceptors into a new Spring component called the SubscriptionRegistry. This component maintains a cache of ActiveSubscriptions. An ActiveSubscription contains the subscription, it's delivery channel, and a list of delivery handlers.  
|  |  Introduced a new Spring factory interface ISubscribableChannelFactory that is used to create delivery channels and handlers. By default, HAPI FHIR ships with a LinkedBlockingQueue implementation of the delivery channel factory. If a different type of channel factory is required (e.g. JMS or Kafka), add it to your application context and mark it as @Primary.  
|  |  Added support for matching subscriptions in a separate server from the REST Server. To do this, run the SubscriptionActivatingInterceptor on the REST server and the SubscriptionMatchingInterceptor in the standalone server. Classes required to support running a standalone subscription server are in the ca.uhn.fhir.jpa.subscription.module.standalone package. These classes are excluded by default from the JPA ApplicationContext (that package is explicitly filtered out in the BaseConfig.java @ComponentScan).  
|  |  The ResponseHighlighterInterceptor now declines to handle Binary responses provided as a response from extended operations. In other words if the operation $foo returns a Binary resource, the ResponseHighliterInterceptor will not provide syntax highlighting on the response. This was previously the case for the /Binary endpoint, but not for other binary responses.  
|  |  A bug in the JPA resource reindexer was fixed: In many cases the reindexer would mark reindexing jobs as deleted before they had actually completed, leading to some resources not actually being reindexed.  
|  |  An issue was corrected with the JPA reindexer, where String index columns do not always get reindexed if they did not have an identity hash value in the HASH_IDENTITY column.  
|  |  Under some circumstances, when a custom search parameter was added to the JPA server resources could start reindexing before the new search parameter had been saved, meaning that it was not applied to all resources. This has been corrected.  
|  [#980](https://github.com/hapifhir/hapi-fhir/issues/980) |  When using the HL7.org DSTU2 structures, a QuestionnaireResponse with a value of type reference would fail to parse. Thanks to David Gileadi for the pull request!  
|  |  When running the JPA server on Oracle, certain search queries that return a very large number of _included resources failed with an SQL exception stating that too many parameters were used. Search include logic has been reworked to avoid this.  
|  |  JPA Subscription deliveries did not always include the accurate versionId if the Subscription module was configured to use an external queuing engine. This has been corrected.  
|  |  In the JPA server, search/read operations being performed within a transaction bundle did not pass the client request HTTP headers to the sub-request. This meant that AuthorizationInterceptor could not authorize these requests if it was depending on headers being present.  
|  |  When using a client in DSTU3/R4 mode, if the client attempted to validate the server CapabilityStatement but was not able to parse the response, the client would throw an exception with a misleading error about the Conformance resource not existing. This has been corrected. Thanks to Shayaan Munshi for reporting and providing a test case!  
|  |  It is now possible to upload a ConceptMap to the JPA server containing mappings where the source or target is a StructureDefinition canonical URI. This was previously blocked, as the system could not apply these mappings. It is now permitted to be stored, although the system will still not apply these mappings.  
|  [#1084](https://github.com/hapifhir/hapi-fhir/issues/1084) |  In JPA Server REST Hook Subscriptions, any Headers defined in the Subscription resource are now applied to the outgoing HTTP request. Thanks to Volker Schmidt for the pull request!  
|  |  When fetching a page of search results, if a page offset beyond the total number of available result was requested, a single result was still returned (e.g. requesting a page beginning at index 1000 when there are only 10 results would result in the 10th result being returned). This will now result in an empty response Bundle as would be expected.  
|  |  The casing of the base64Binary datatype was incorrect in the DSTU3 and R4 model classes. This has been corrected.  
|  |  When performing a JPA search with a chained :text modifier (e.g. MedicationStatement?medication.code:text=aspirin,tylenol) a series of unneccesary joins were introduced to the generated SQL query, harming performance. This has been fixed.  
|  |  A serialization error when performing some searches in the JPA server using data parameters has been fixed. Thanks to GitHub user @PickOneFish for reporting!  
|  [#1135](https://github.com/hapifhir/hapi-fhir/issues/1135) |  An issue with outdated syntax in the Vagrant file that prevent it from being used was corrected. Thanks to Steve Lewis for the pull requst!  
|  [#1130](https://github.com/hapifhir/hapi-fhir/issues/1130) |  The HAPI FHIR tutorial server project had outdated versions of HAPI FHIR in its pom file. Thanks to Ricardo Estevez for the pull request!  
|  |  The JPA server $expunge operation could sometimes fail to expunge if another resource linked to a resource that was being expunged. This has been corrected. In addition, the $expunge operation has been refactored to use smaller chunks of work within a single DB transaction. This improves performance and reduces contention when performing large expunge workloads.  
|  [#1114](https://github.com/hapifhir/hapi-fhir/issues/1114) |  A NullPointerException during validation was fixed. Thanks to GitHub user zilin375 for the pull request!  
|  [#944](https://github.com/hapifhir/hapi-fhir/issues/944) |  A NullPointerException has been fixed when using custom resource classes that have a @Block class as a child element. Thanks to Lars Gram Mathiasen for reporting and providing a test case!  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2020.html)
0.7 Changelog: 2019 
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
[ 0.8 Changelog: 2018 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)