---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html
crawled: 2025-08-01T14:05:03.394835
---

# Introduction Changelog 2017

#  0.9.1Changelog: 2017
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changelog-2017)
#  0.9.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changelog)
#  0.9.3HAPI FHIR 3.1.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#hapi-fhir-310)
##  0.9.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#release-information)
**Released:** 2017-11-23
##  0.9.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changes)
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Spring (JPA): 4.3.10 -> 5.0.0
  * Jackson (JPA): 2.8.1 -> 2.9.2

  
---|---|---  
|  |  In Apache client, remove a log message at WARN level when the response does not specify a charset. This log line often showed up any time a server was not supplying a response, making client logs quite noisy  
|  |  A new configuration item has been added to the JPA server DaoConfig called `getCountSearchResultsUpTo()` . This setting governs how many search results the search coordinator should try to find before returning an initial search response to the user, which has an effect on whether the `Bundle.total` field is always populated in search responses. This has now been set to 20000 on out public server (fhirtest.uhn.ca) so most search results should now include a total.  
|  |  The DSTU2 XhtmlDt type has been modified so that it no longer uses the StAX XMLEvent type as its internal model, and instead simply uses a String. New methods called "parse" and "encode" have been added to HAPI FHIR's XmlUtil class, which can be used to convert between a String and an XML representation. This should allow HAPI FHIR to run in environments where StAX is not available, such as Android phones.  
|  [#761](https://github.com/hapifhir/hapi-fhir/issues/761) |  Restored the `org.hl7.fhir.r4.model.codesystem.*` classes (which are Java Enums for the various FHIR codesystems). These were accidentally removed in HAPI FHIR 3.0.0. Thanks to GitHub user @CarthageKing for reporting!  
|  [#711](https://github.com/hapifhir/hapi-fhir/issues/711) |  Client logic for checking the version of the connected server to ensure it is for the correct version of FHIR now includes a check for R4 servers. Thanks to Clayton Bodendein for the pull request, including a number of great tests!  
|  [#714](https://github.com/hapifhir/hapi-fhir/issues/714) |  JAX-RS client framework now supports the ability to register your own JAX-RS Component Classes against the client, as well as better documentation about thread safety. Thanks to SÃ©bastien RiviÃ¨re for the pull request!  
|  |  A performance to the JPA server has been made which reduces the number of writes to index tables when updating a resource with contents that only make minor changes to the resource content. In many cases this can noticeably improve update performance.  
|  |  Add `Prefer` and `Cache-Control` to the list of headers which are declared as being acceptable for CORS requests in CorsInterceptor, CLI, and JPA Example. Thanks to Patrick Werner for the pull request!  
|  |  Bundle resources did not have their version encoded when serializing in FHIR resource (XML/JSON) format.  
|  |  The Binary resource endpoint now supports the `X-Security-Context` header when reading or writing Binary contents using their native Content-Type (i.e exchanging the raw binary with the server, as opposed to exchanging a FHIR resource).  
|  [#743](https://github.com/hapifhir/hapi-fhir/issues/743) |  Add support for Spring Boot for initializing a number of parts of the library, as well as several examples. See the [Spring Boot samples](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-spring-boot/hapi-fhir-spring-boot-samples) for examples of how this works. Thanks to Mathieu Ouellet for the contribution!  
|  [#747](https://github.com/hapifhir/hapi-fhir/issues/747) |  JPA server now has lucene index support moved to separate classes from the entity classes in order to facilitate support for ElasticSearch. Thanks to Jiang Liang for the pull request! Note that any existing JPA projects will need to add an additional property in their Spring config called `hibernate.search.model_mapping`. See [this line](https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-example/src/main/java/ca/uhn/fhir/jpa/demo/FhirServerConfig.java#L84) in the example project.  
|  [#755](https://github.com/hapifhir/hapi-fhir/issues/755) |  A new client interceptor has been added called AdditionalRequestHeadersInterceptor, which allows a developer to add additional custom headers to a client requests. Thanks to Clayton Bodendein for the pull request!  
|  [#767](https://github.com/hapifhir/hapi-fhir/issues/767) |  JAX-RS server module was not able to generate server CapabilityStatement for some versions of FHIR (DSTU2_HL7ORG, DSTU2_1, or R4). Thanks to Clayton Bodendein for the Pull Request!  
|  [#769](https://github.com/hapifhir/hapi-fhir/issues/769) |  When a server method throws a DataFormatException, the error will now be converted into an HTTP 400 instead of an HTTP 500 when returned to the client (and a stack trace will now be returned to the client for JAX-RS server if configured to do so). Thanks to Clayton Bodendein for the pull request!  
|  [#762](https://github.com/hapifhir/hapi-fhir/issues/762) |  Prevent a crash in AuthorizationInterceptor when processing transactions if the interceptor has rules declared which allow resources to be read/written by "any ID of a given type". Thanks to GitHub user @dconlan for the pull request!  
|  |  JPA server now supports the use of the `Cache-Control` header in order to allow the client to selectively disable the search result cache. This directive can also be used to disable result paging and return results faster when only a small number of results is needed. See the JPA Page for more information.  
|  |  JPA Server search/history results now set the ID of the returned Bundle to the ID of the search, meaning that if a search returns results from the Query cache, it will reuse the ID of the previously returned Bundle  
|  |  The JPA server transaction operation (DSTU3/R4) did not correctly process the If-Match header when passed in via `Bundle.entry.request.ifMatch` value  
|  |  The Android client module has been restored to working order, and no longer requires a special classifier or an XML parser to be present in order to work. This means that the hapi-fhir-android library is much less likely to cause conflicts with other libraries imported into an Android application via Gradle.   
  
See the HAPI FHIR Android Documentation for more information. As a part of this fix, all dependencies on the StAX API have been removed in environments where StAX is not present (such as Android). The client will now detect this case, and explicitly request JSON payloads from servers, meaning that Android clients no longer need to include two parser stacks  
|  |  Remove a bunch of exceptions in the org.hl7.fhir.exception package from the hapi-fhir-base module, as they were also duplicated in the hapi-fhir-utilities module.  
|  |  The resource Profile Validator has been enhanced to not try to validate bound fields where the binding strength is "example", and a crash was resolved when validating QuestionnaireResponse answers with a type of "choice" where the choice was bound to a ValueSet.  
|  |  Remove the fake "Test" resource from DSTU2 structures. This was not a real resource type, and caused conflicts with the .NET client. Thanks to Vlad Ignatov for reporting!  
|  [#720](https://github.com/hapifhir/hapi-fhir/issues/720) |  Parsing a DSTU3/R4 custom structure which contained a field of a custom type caused a crash during parsing. Thanks to GitHub user @mosaic-hgw for reporting!  
|  [#717](https://github.com/hapifhir/hapi-fhir/issues/717) |  Processing of the If-Modified-Since header on FHIR read operations was reversed, returning a 304 when the resource had been modified recently. Thanks to Michael Lawley for the pull request!  
|  [#725](https://github.com/hapifhir/hapi-fhir/issues/725) |  DSTU2-hl7org and DSTU2.1 structures did not copy resource IDs when invoking copyValues(). Thanks to Clayton Bodendein for the pull request!  
|  [#734](https://github.com/hapifhir/hapi-fhir/issues/734) |  When encoding a Binary resource, the Binary.securityContext field was not encoded correctly. Thanks to Malcolm McRoberts for the pull request with fix and test case!  
|  |  When paging through multiple pages of search results, if the client had requested a subset of resources to be returned using the `_elements` parameter, the elements list was lost after the first page of results. In addition, elements will not remove elements from search/history Bundles (i.e. elements from the Bundle itself, as opposed to elements in the entry resources) unless the Bundle elements are explicitly listed, e.g. `_include=Bundle.total` . Thanks to @parisni for reporting!  
|  |  In FHIR DSTU3 the `ValueSet/$expand?identifier=foo` and `ValueSet/$validate-code?identifier=foo` parameters were changed to `ValueSet/$expand?url=foo` and `ValueSet/$validate-code?url=foo` respectively, but the JPA server had not caught up. The JPA DSTU3 server has been adjusted to accept either "identifier" or "url" (with "url" taking precedence), and the JPA R4 server has been changed to only accept "url". Thanks to Avinash Shanbhag for reporting!  
|  |  An issue was fixed in JPA server where extensions on primitives which are nestedt several layers deep are lost when resources are retrieved  
|  [#756](https://github.com/hapifhir/hapi-fhir/issues/756) |  Conditional deletes in JPA server were incorrectly denied by AuthorizationInterceptor if the delete was permitted via a compartment rule. Thanks to Alvin Leonard for the pull request!  
|  [#770](https://github.com/hapifhir/hapi-fhir/issues/770) |  JAX-RS server conformance provider in the example module passed in the server description, server name, and server version in the incorrect order. Thanks to Clayton Bodendein for the pull request!  
|  [#774](https://github.com/hapifhir/hapi-fhir/issues/774) |  The learn more links on the website home page had broken links. Thanks to James Daily for the pull request to fix this!  
|  [#744](https://github.com/hapifhir/hapi-fhir/issues/744) |  Fix an error in JPA server when using Derby Database, where search queries with a search URL longer than 255 characters caused a mysterious failure. Thanks to Chris Schuler and Bryn Rhodes for all of their help in reproducing this issue.  
|  |  In certain cases in the JPA server, if multiple threads all attempted to update the same resource simultaneously, the optimistic lock failure caused a "gap" in the history numbers to occur. This would then cause a mysterious failure when trying to update this resource further. This has been resolved.  
|  |  Fix a NullPointerException when validating a Bundle (in DSTU3/R4) with no `Bundle.type` value  
#  0.9.4HAPI FHIR 3.0.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#change3.1.0-0)
##  0.9.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#release-information-1)
**Released:** 2017-09-27
##  0.9.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changes-1)
|  |  Support for FHIR R4 (current working draft) has been **added** (in a new module called `hapi-fhir-structures-r4` ) and support for FHIR DSTU1 ( `hapi-fhir-structures-dstu` ) has been **removed** . Removing support for the legacy DSTU1 FHIR version was a difficult decision, but it allows us the opportunitity to clean up the codebase quite a bit, and remove some confusing legacy parts of the API (such as the legacy Atom Bundle class).   
  
A new redesigned table of HAPI FHIR versions to FHIR version support has been added to the Download Page  
---|---|---  
|  |  HAPI FHIR's modules have been restructured for more consistency and less coupling between unrelated parts of the API.   
  
A new complete list of HAPI FHIR modules has been added to the Download Page. Key changes include: 
  * HAPI FHIR's **client** codebase has been moved out of `hapi-fhir-base` and in to a new module called `hapi-fhir-client`. Client users now need to explicitly add this JAR to their project (and non-client users now no longer need to depend on it) 
  * HAPI FHIR's **server** codebase has been moved out of `hapi-fhir-base` and in to a new module called `hapi-fhir-server`. Server users now need to explicitly add this JAR to their project (and non-server users now no longer need to depend on it) 
  * As a result of the client and server changes above, we no longer need to produce a special Android JAR which contains the client, server (which added space but was not used) and structures. There is now a normal module called `hapi-fhir-android` which is added to your Android Gradle file along with whatever structures JARs you wish to add. See the [Android Integration Test](https://github.com/hapifhir/hapi-fhir-android-integration-test) to see a sample project using HAPI FHIR 3.0.0. **Note that this has been reported to work by some people but others are having issues with it!** In order to avoid delaying this release any further we are releasing now despite these issues. If you are an Android guru and want to help iron things out please get in touch. If not, it might be a good idea to stay on HAPI FHIR 2.5 until the next point release of the 3.x series. 
  * A new JAR containing FHIR utilities called `hapi-fhir-utilities` has been added. This JAR reflects the ongoing harmonization between HAPI FHIR and the FHIR RI codebases and is generally required in order to use HAPI at this point (if you are using a dependency manager such as Maven or Gradle it will be brought in to your project automatically as a dependency) 

  
|  |  IServerOperationInterceptor now has a new method `resourceUpdated(RequestDetails, IBaseResource, IBaseResource)` which replaces the previous `resourceUpdated(RequestDetails, IBaseResource)` . This allows interceptors to be notified of resource updates, but also see what the resource looked like before the update. This change was made to support the change above, but seems like a useful feature all around.  
|  |  JPA server transaction processing now honours the Prefer header and includes created and updated resource bodies in the response bundle if it is set appropriately.  
|  |  Optimize queries in JPA server remove a few redundant select columns when performing searches. This provides a slight speed increase in some cases.  
|  |  Add configuration to JPA server DaoConfig that allows a maximum number of search results to be specified. Queries will never return more than this number, which can be good for avoiding accidental performance problems in situations where large queries should not be needed  
|  |  In order to allow the reoganizations and decoupling above to happen, a number of important classes and interfaces have been moved to new packages. A sample list of these changes is listed below. When upgrading to 3.0.0 your project may well show a number of compile errors related to missing classes. In most cases this can be resolved by simply removing the HAPI imports from your classes and asking your IDE to "Organize Imports" once again. This is an annoying change we do realize, but it is neccesary in order to allow the project to continue to grow. 
  * IGenericClient moved from package ca.uhn.fhir.rest.client to package ca.uhn.fhir.rest.client.api
  * IRestfulClient moved from package ca.uhn.fhir.rest.client to package ca.uhn.fhir.rest.client.api
  * AddProfileTagEnum moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.context.api
  * IVersionSpecificBundleFactory moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.context.api
  * BundleInclusionRule moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.context.api
  * RestSearchParameterTypeEnum moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.rest.api
  * EncodingEnum moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.rest.api
  * Constants moved from package ca.uhn.fhir.rest.server to package ca.uhn.fhir.rest.api
  * IClientInterceptor moved from package ca.uhn.fhir.rest.client to package ca.uhn.fhir.rest.client.api
  * ITestingUiClientFactory moved from package ca.uhn.fhir.util to package ca.uhn.fhir.rest.server.util

  
|  |  JPA search now uses hibernate ScrollableResults instead of plain JPA List. This should improve performance over large search results.  
|  |  JPA servers with no paging provider configured, or with a paging provider other than DatabaseBackedPagingProvider will load all results in a single pass and keep them in memory. Using this setup is not a good idea unless you know for sure that you will never have very large queries since it means that all results will be loaded into memory, but there are valid reasons to need this and it will perform better than paging to the database in that case. This fix also resolves a NullPointerException when performing an $everything search. Thanks to Kamal Othman for reporting!  
|  |  Add an optional and configurable hard limit on the total number of meta items (tags, profiles, and security labels) on an individual resource. The default is 1000.  
|  |  When executing a search (HTTP GET) as a nested operation in in a transaction or batch operation, the search now returns a normal page of results with a link to the next page, like any other search would. Previously the search would return a small number of results with no paging performed, so this change brings transaction and batch processing in line with other types of search.  
|  |  JPA server no longer returns an OperationOutcome resource as the first resource in the Bundle for a response to a batch operation. This behaviour was previously present, but was not specified in the FHIR specification so it caused confusion and was inconsistent with behaviour in other servers.  
|  |  Because the Atom-based DSTU1 Bundle class has been removed from the library, users of the HAPI FHIR client must now always include a Bundle return type in search calls. For example, the following call would have worked previously:   
Bundle bundle = client.search().forResource(Patient.class)  
.where(new TokenClientParam("gender").exactly().code("unknown"))  
.prettyPrint()  
.execute();  
  
This now needs an explicit returnBundle statement, as follows:   
Bundle bundle = client.search().forResource(Patient.class)  
.where(new TokenClientParam("gender").exactly().code("unknown"))  
.prettyPrint()  
.returnBundle(Bundle.class)  
.execute();  
  
|  |  JpaConformanceProvider now has a configuration setting to enable and disable adding resource counts to the server metadata.  
|  [#651](https://github.com/hapifhir/hapi-fhir/issues/651) |  Enhancement to ResponseHighlighterInterceptor where links in the resource body are now converted to actual clickable hyperlinks. Thanks to Eugene Lubarsky for the pull request!  
|  |  BanUnsupportedHttpMethodsInterceptor has been modified so that it now allows HTTP PATCH to proceed.  
|  [#651](https://github.com/hapifhir/hapi-fhir/issues/651) |  Enhancement to ResponseHighlighterInterceptor so that it now can be configured to display the request headers and response headers, and individual lines may be highlighted.  
|  |  The version of a few dependencies have been bumped to the latest versions (dependent HAPI modules listed in brackets): 
  * Gson (JSON Parser): 2.8.0 -> 2.8.1
  * Commons-lang3 (Everywhere): 3.5 -> 3.6
  * Apache HttpClient (FHIR Client): 4.5.2 -> 4.5.3
  * Apache HttpCore (FHIR Client): 4.4.5 -> 4.4.6
  * Phloc Commons (Schematron Validator): 4.4.6 -> 4.4.11
  * Hibernate (JPA): 5.2.9 -> 5.2.10
  * Hibernate Search (JPA): 5.7.0 -> 5.7.1
  * Spring (JPA): 4.3.7 -> 4.3.10
  * Spring Data JPA (JPA): 1.10.4 -> 1.11.6
  * Guava (JPA): 22.0 -> 23.0
  * Thymeleaf (Testpage Overlay): 3.0.2 -> 3.0.7
  * OkHttp (Android): 3.4.1 -> 3.8.1

  
|  [#689](https://github.com/hapifhir/hapi-fhir/issues/689) |  Add a command line flag to the CLI tool to allow configuration of the server search result cache timeout period. Thanks to Eugene Lubarsky for the pull request!  
|  |  JPA server transaction operations now put OperationOutcome resources resulting from actions in `Bundle.entry.response.outcome` instead of the previous `Bundle.entry.resource`  
|  [#701](https://github.com/hapifhir/hapi-fhir/issues/701) |  Testing UI now has a dropdown for modifiers on token search. Thanks to GitHub user @dconlan for the pull request!  
|  [#688](https://github.com/hapifhir/hapi-fhir/issues/688) |  When parsing an incomplete ID with the form `http://my.org/Foo` into IdDt and IdType objects, the Foo portion will now be treated as the resource type. Previously my.org was treated as the resource type and Foo was treated as the ID. Thanks to GitHub user @CarthageKing for the pull request!  
|  |  JPA Subscription support has been refactored. A design contributed by Jeff Chung for the REST Hook subscription module has been ported so that Websocket subscriptions use it too. This design uses an interceptor to scan resources as they are processed to test whether they should be delivered to subscriptions, instead of using a polling design.   
  
In addition, this scanning has been reworked to happen in a separate thread from the main storage thread, which should improve performance and scalability of systems with multiple subscriptions. Thanks to Jeff for all of his work on this!  
|  |  Add a new constructor to SimpleRequestHeaderInterceptor which allows a complete header to be passed in (including name and value in one string)  
|  |  REST Hook subscriptions now honour the Subscription.channel.header field  
|  |  DSTU2 validator has been enhanced to do a better job handling ValueSets with expansions pointing to other ValueSets  
|  [#712](https://github.com/hapifhir/hapi-fhir/issues/712) |  Add appropriate import statements for logging to JPA demo code. Thanks to Rob Hausam for the pull request!  
|  [#700](https://github.com/hapifhir/hapi-fhir/issues/700) |  Add some browser performance logging to ResponseHighlightingInterceptor. Thanks to Eugene Lubarsky for the pull request, and for convincing James not to optimize something that did not need optimizing!  
|  |  A new config property has been added to the JPA seerver DaoConfig called "setAutoCreatePlaceholderReferenceTargets". This property causes references to unknown resources in created/updated resources to have a placeholder target resource automatically created.  
|  |  The server LoggingInterceptor has had a variable called `processingTimeMillis` which logs the number of milliseconds the server took to process a given request since HAPI FHIR 2.5, but this was not documented. This variable has now been documented as a part of the available features.  
|  |  A new experimental feature has been added to the JPA server which allows you to define certain search parameter combinations as being resource keys, so that a database constraint will prevent more than one resource from having a matching pair  
|  |  When using the client LoggingInterceptor in non-verbose mode, the log line showing the server's response HTTP status will now also include the returned `Location` header value as well  
|  |  A new flag has been add to the CLI upload-definitions command "-e" which allows skipping particular resources  
|  |  An issue in JPA server has been corrected where if a CodeSystem resource was deleted, it was not possible to create a new resource with the same URI as the previous one  
|  |  Subscriptions in JPA server now support "email" delivery type through the use of a new interceptor which handles that type  
|  |  JPA server can now be configured to not support `:missing` modifiers, which increases write performance since fewer indexes are written  
|  |  A new JPA configuration option has been added to the DaoConfig which allows support for the `:missing` search parameter modifier to be enabled or disabled, and sets the default to DISABLED.   
  
Support for this parameter causes many more index rows to be inserted in the database, which has a significant impact on write performance. A future HAPI update may allow these rows to be written asynchronously in order to improve this.  
|  |  JPA server is now able to handle placeholder IDs (e.g. urn:uuid:00....000) being used in Bundle.entry.request.url as a part of the conditional URL within transactions.  
|  [#667](https://github.com/hapifhir/hapi-fhir/issues/667) |  When using the AuthorizationInterceptor with the JPA server, when a client is updating a resource from A to B, the user now needs to have write permission for both A and B. This is particularly important for cases where (for example) an Observation is being updated from having a subject of Patient/A to Patient/B. If the user has write permission for Patient/B's compartment, this would previously have been allowed even if the user did not have access to write to Patient/A's compartment. Thanks to Eeva Turkka for reporting!  
|  [#604](https://github.com/hapifhir/hapi-fhir/issues/604) |  Allow DateParam (used in servers) to handle values with MINUTE precision. Thanks to Christian Ohr for the pull request!  
|  |  Fix HTTP 500 error in JPA server if a numeric search parameter was supplied with no value, e.g. `GET /Observation?value-quantity=`  
|  [#674](https://github.com/hapifhir/hapi-fhir/issues/674) |  Prevent duplicates in $everything query response in JPA server. Thanks to @vlad-ignatov for reporting!  
|  |  Fix issue in calling JPA server transactions programmatically where resources are linked by object reference and not by ID where indexes were not correctly generated. This should not affect most users.  
|  [#678](https://github.com/hapifhir/hapi-fhir/issues/678) |  Fix issue in SubscriptionInterceptor that caused interceptor to only actually notify listeners of the first 10 subscriptions. Thanks to Jeff Chung for the pull request!  
|  [#693](https://github.com/hapifhir/hapi-fhir/issues/693) |  Fix potential ConcurrentModificationException when adding subscriptions while running under heavy load. Thanks to Jeff Chung for the pull request!  
|  |  Correct an issue in JPA server on Postgres where searches with a long search URL were not able to be automatically purged from the database after they were scheduled for deletion. Thanks to Ravi Kuchi for reporting!  
|  |  Fix a regression in HAPI FHIR 2.5 JPA server where executing a search in a transaction or batch operation caused an exception. Thanks to Ravi Kuchi for reporting!  
|  |  Correct an issue when processing transactions in JPA server where updates and creates to resources with tags caused the tags to be created twice in the database. These duplicates were utomatically filtered upon read so this issue was not user-visible, but it coule occasionally lead to performance issues if a resource containing multiple tags was updated many times via transactions.  
|  |  JPA server should not allow creation of resources that have a reference to a resource ID that previously existed but is now deleted. Thanks to Artem Sopin for reporting!  
|  |  Avoid a deadlock in JPA server when the RequestValidatingInterceptor is being used and a large number of resources are being created by clients at the same time.  
|  |  Testpage Overlay's transaction method did not work if the response Bundle contained any entries that did not contain a resource (which is often the case in more recent versions of HAPI). Thanks to Sujay R for reporting!  
|  |  When the server was returning a multi-page search result where the client did not explicitly request an encoding via the _format parameter, a _format parameter was incorrectly added to the paging links in the response Bundle. This would often explicitly request XML encoding because of the browser Accept header even though this was not what the client wanted.  
|  |  AuthorizationInterceptor did not permit PATCH operations to proceed even if the user had write access for the resource being patched.  
|  [#682](https://github.com/hapifhir/hapi-fhir/issues/682) |  Fix an issue in HapiWorkerContext where structure definitions are not able to be retrieved if they are referred to by their relative or logical ID. This affects profile tooling such as StructureMapUtilities. Thanks to Travis Lukach for reporting and providing a test case!  
|  [#679](https://github.com/hapifhir/hapi-fhir/issues/679) |  Add link to DSTU3 JavaDocs from documentation index. Thanks to Vadim Peretokin for the pull request!  
|  [#680](https://github.com/hapifhir/hapi-fhir/issues/680) |  Fix a typo in the documentation. Thanks to Saren Currie for the pull request!  
|  [#683](https://github.com/hapifhir/hapi-fhir/issues/683) |  Correct an issue with the model classes for STU3 where any classes containing the @ChildOrder annotation (basically the conformance resources) will not correctly set the order if any of the elements are a choice type (i.e. named "foo[x]"). Thanks to GitHub user @CarthageKing for the pull request!  
|  |  Fix potential deadlock in stale search deleting task in JPA server, as well as potential deadlock when executing transactions containing nested searches when operating under extremely heavy load.  
|  [#696](https://github.com/hapifhir/hapi-fhir/issues/696) |  An issue was corrected where search parameters containing negative numbers were sometimes treated as positive numbers when processing the search. Thanks to Keith Boone for reporting and suggesting a fix!  
|  [#699](https://github.com/hapifhir/hapi-fhir/issues/699) |  Fix an unfortunate typo in the custom structures documentation. Thanks to Jason Owen for the PR!  
|  [#686](https://github.com/hapifhir/hapi-fhir/issues/686) |  Correct an issue in the validator (DSTU3/R4) where elements were not always correctly validated if the element contained only a profiled extension. Thanks to SÃ©bastien RiviÃ¨re for the pull request!  
|  [#695](https://github.com/hapifhir/hapi-fhir/issues/695) |  Extensions on ID datatypes were not parsed or serialized correctly. Thanks to Stephen RiviÃ¨re for the pull request!  
|  [#710](https://github.com/hapifhir/hapi-fhir/issues/710) |  Fix a bug in REST Hook Subscription interceptors which prevented subscriptions from being activated. Thanks to Jeff Chung for the pull request!  
|  [#708](https://github.com/hapifhir/hapi-fhir/issues/708) |  Fix broken links in usage pattern diagram on website. Thanks to Pascal Brandt for the pull request!  
|  [#706](https://github.com/hapifhir/hapi-fhir/issues/706) |  Fix incorrect FHIR Version Strings that were being outputted and verified in the client for some versions of FHIR. Thanks to Clayton Bodendein for the pull request!  
|  |  REST HOOK subscriptions now use HTTP PUT if there is a payload type specified, regardless of whether the source event was a create or an update  
|  |  hapi-fhir-client-okhttp project POM had dependencies on both hapi-fhir-structures-dstu2 and hapi-fhir-structures-dstu3, which meant that any project using ookhttp would import both structures JARs. This has been removed.  
|  |  When uploading a Bundle resource to the server (as a collection or document, not as a transaction) the ID was incorrectly stripped from resources being saved within the Bundle. This has been corrected.  
|  |  Schematron validator now applies invariants to resources within a Bundle, not just to the outer Bundle resource itself  
|  |  Server and Client both still included Category header for resource tags even though this feature was only present in FHIR DSTU1 and was removed from the specification in FHIR DSTU2. The presence of these headers sometimes caused parsed resource instances to contain duplicate tags  
#  0.9.5HAPI FHIR 2.5
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#change3.0.0-0)
##  0.9.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#release-information-2)
**Released:** 2017-06-08
##  0.9.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changes-2)
|  |  Server now respects the If-Modified-Since header and will return an HTTP 304 if appropriate for read operations.  
---|---|---  
|  |  JPA server now has configurable properties that allow referential integrity to be disabled for both writes and deletes. This is useful in some cases where data integrity is not wanted or not possible. It can also be useful if you want to delete large amounts of interconnected data quickly.   
  
A corresponding flag has been added to the CLI tool as well.  
|  |  Add configuration property to DSTU3 FhirInstanceValidator to allow client code to change unknown extension handling behaviour.  
|  |  Add a check in JPA server that prevents completely blank tags, profiles, and security labels from being saved to the database. These were filtered out anyhow when the result was returned back to the client but they were persisted which just wasted space.  
|  [#656](https://github.com/hapifhir/hapi-fhir/issues/656) |  Improve handling in JPA server when doing code:above and code:below searches to use a disjunction of AND and IN in order to avoid failures under certain conditions. Thanks to Michael Lawley for the pul request!  
|  |  CLI now defaults to DSTU3 mode if no FHIR version is specified  
|  |  Server and annotation-client @History annotation now allows DSTU3+ resource types in the type= property  
|  |  DaoConfig#setInterceptors() has been un-deprecated. It was previously deprecated as we thought it was not useful, but uses have been identified so it turns out this method will live after all. Interceptors registered to this method will now be treated appropriately if they implement IServerOperationInterceptor too.  
|  |  This release includes significant performance enhancements for the JPA server. Most importantly, the way that searches are performed has been re-written to allow the server to perform better when the database has a large number of results in it. The following enhancements have been made:   
  

  * Searches with multiple search parameters of different datatypes (e.g. find patients by name and date of birth) were previously joined in Java code, now the join is performed by the database which is faster 
  * Searches which returned lots of results previously has all results streamed into memory before anything was returned to the client. This is particularly slow if you do a search for (say) "get me all patients" since potentially thousands or even millions of patients' IDs were loaded into memory before anything gets returned to the client. HAPI FHIR now has a multithreaded search coordinator which returns results to the client as soon as they are available 
  * Search results will be cached and reused (so that if a client does two searches for "get me all patients matching FOO" with the same FOO in short succession, we won't query the DB again but will instead reuse the cached results). Note that this can improve performance, but does mean that searches can return slightly out of date results. Essentially what this means is that the latest version of individual resources will always be returned despite this cacheing, but newly created resources that should match may not be returned until the cache expires. By default this cache has been set to one minute, which should be acceptable for most real-world usage, but this can be changed or disabled entirely. 
  * Updates which do not actually change the contents of the resource can optionally be prevented from creating a new version of the resource in the database 

  
  
Existing users should delete the `HFJ_SEARCH`, `HFJ_SEARCH_INCLUDE`, and `HFJ_SEARCH_RESULT` tables from your database before upgrading, as the structure of these tables has changed and old search results can not be reused.  
|  [#590](https://github.com/hapifhir/hapi-fhir/issues/590) |  AuthorizationInterceptor did not correctly handle paging requests (e.g. requests for the second page of results for a search operation). Thanks to Eeva Turkka for reporting!  
|  |  Fix issue where the JSON parser sometimes did not encode DSTU3 extensions on the root of a resource which have a value of type reference.  
|  |  JPA server did not correctly process :missing qualifier on date parameters  
|  [#633](https://github.com/hapifhir/hapi-fhir/issues/633) |  AppacheHttpClient did not always respect the charset in the response Content-Type header. Thanks to Gijsbert van den Brink for the pull request!  
|  [#636](https://github.com/hapifhir/hapi-fhir/issues/636) |  Fix XhtmlParser to correctly handle hexadecimal escaped literals. Thanks to Gijsbert van den Brink for the Pull Request!  
|  |  JPA server did not correctly support searching on a custom search parameter whose path pointed to an extension, where the client used a chained value.  
|  |  Fix dependency on commons-codec 1.4 in hapi-fhir-structures-dstu3, which was preventing this library from being used on Android because Android includes an older version of commons-codec.  
|  |  JPA server failed to index search parameters on paths containing a decimal data type  
|  |  Validator incorrectly rejected references where only an identifier was populated  
|  [#649](https://github.com/hapifhir/hapi-fhir/issues/649) |  Make error handler in the client more tolerant of errors where no response has been received by the client when the error happens. Thanks to GitHub user maclema for the pull request!  
|  [#664](https://github.com/hapifhir/hapi-fhir/issues/664) |  Loading the build-in profile structures (StructureDefinition, ValueSet, etc) is now done in a synchronized block in order to prevent multiple loads happening if the server processes multiple validations in parallel threads right after startup. Previously a heavy load could cause the server to run out of memory and lock up. Thanks to Karl M Davis for analysis and help fixing this!  
|  [#652](https://github.com/hapifhir/hapi-fhir/issues/652) |  Fix bad ValueSet URL in DeviceRequest profile definition for STU3 which was preventing the CLI from uploading definitions correctly. Thanks to Joel Schneider for the Pull Request!  
|  [#660](https://github.com/hapifhir/hapi-fhir/issues/660) |  Fix an error where the JPA server sometimes failed occasional requests with a weird NullPointerException when running under very large concurrent loads. Thanks to Karl M. Davis for reporting, investigating, and ultimately finding a solution!  
|  [#630](https://github.com/hapifhir/hapi-fhir/issues/630) |  Fix concurrency issues in FhirContext that were causing issues when starting a context up on Android. Thanks to GitHub issue @Jaypeg85 for the pull request!  
|  |  Fix an issue in the JPA server if a resource has been previously saved containing vocabulary that is no longer valid. This only really happened if you were using a non-final version of FHIR (e.g. using DSTU3 before it was finalized) but if you were in this situation, upgrading HAPI could cause you to have old codes that no longer exist in your database. This fix prevents these from blocking you from accesing those resources.  
|  [#563](https://github.com/hapifhir/hapi-fhir/issues/563) |  JSON Parser gave a very unhelpful error message (Unknown attribute 'value' found during parse) when a scalar value was found in a spot where an object is expected. This has been corrected to include much more information. Thanks to GitHub user @jasminas for reporting!  
|  |  JPA server did not correctly support searching on a custom search parameter whose path pointed to an extension, where the client used a chained value.  
#  0.9.6HAPI FHIR 2.4
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#change2.5-11)
##  0.9.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#release-information-3)
**Released:** 2017-04-19
##  0.9.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changes-3)
|  |  This release brings the DSTU3 structures up to FHIR R3 (FHIR 3.0.1) definitions. Note that there are very few changes between the DSTU3 structures in HAPI FHIR 2.3 and the ones in HAPI FHIR 2.4 since the basis for the DSTU3 structures in HAPI FHIR 2.3 was the R3 QA FHIR version (1.9.0) but this is the first release of HAPI FHIR to support the final/complete R3 release.  
---|---|---  
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate (JPA): 5.2.7 -> 5.2.9
  * Hibernate Search (JPA): 5.5.7.CR1 -> 5.2.7.Final
  * Hibernate Validator (JPA): 5.3.4 -> 5.4.1
  * Spring (JPA): 4.3.6 -> 4.3.7
  * Gson (Core): 2.7 -> 2.8.0
  * Guava (JPA): 19.0 -> 21.0
  * SLF4j (Core): 1.7.21 -> 1.7.25
  * Logback (Core): 1.1.7 -> 1.2.2

  
|  |  Add a utility method to JPA server: `IFhirResourceDao#removeTag(IIdType, TagTypeEnum, String, String)` . This allows client code to remove tags from a resource without having a servlet request object in context.  
|  [#624](https://github.com/hapifhir/hapi-fhir/issues/624) |  Add an option to ParserOptions that specifies that when parsing a bundle, the ID found in the Bundle.entry.fullUrl should not override the ID found in the Resource.id field. Technically these fields must always supply the same ID in order for a server to be considered conformant, but this option allows you to deal with servers which are behaving badly. Thanks to GitHub user CarthageKing for the pul request!  
|  [#613](https://github.com/hapifhir/hapi-fhir/issues/613) |  In JAX-RS server it is now possible to change the server exception handler at runtime without a server restart. Thanks to Sebastien Riviere for the pull request!  
|  [#602](https://github.com/hapifhir/hapi-fhir/issues/602) |  hapi-fhir-jpaserver-example now includes the `Prefer` header in the list of CORS headers. Thanks to GitHub user @elnin0815 for the pull request!  
|  |  AuthorizationInterceptor can now allow make read or write authorization decisions on a resource by instance ID  
|  |  DaoConfig#setAllowInlineMatchUrlReferences() now defaults to `true` since inline conditional references are now a part of the FHIR specification. Thanks to Jan DÄdek for pointing this out!  
|  [#609](https://github.com/hapifhir/hapi-fhir/issues/609) |  hapi-fhir-jpaserver-base now exposes a `FhirInstanceValidator` bean named `"myInstanceValidatorDstu2"` for DSTU2. A similar bean for DSTU3 was previously implemented.  
|  [#453](https://github.com/hapifhir/hapi-fhir/issues/453) |  hapi-fhir-jpaserver-example project now defaults to STU3 mode instead of the previous DSTU2. Thanks to Joel Schneider for the pull request!  
|  [#534](https://github.com/hapifhir/hapi-fhir/issues/534) |  JPA server now has a setting on the DaoConfig to force it to treat certain reference URLs or reference URL patterns as logical URLs instead of literal ones, meaning that the server will not try to resolve these URLs. Thanks to Eeva Turkka for the suggestion!  
|  |  JPA server was unable to process custom search parameters where the path pointed to an extension containing a reference. Thanks to Ravi Kuchi for reporting!  
|  [#623](https://github.com/hapifhir/hapi-fhir/issues/623) |  Servers in DSTU2.1 mode were incorrectly using the legacy mimetypes instead of the new STU3 ones. Thanks to Michael Lawley for the pull request!  
|  [#617](https://github.com/hapifhir/hapi-fhir/issues/617) |  Remove unneccesary whitespace in the text areas on the testing web UI. Thanks to GitHub user @elnin0815 for the pull request!  
|  [#610](https://github.com/hapifhir/hapi-fhir/issues/610) |  Fix a potential race condition when the FhirContext is being accessed by many threads at the same time right as it is initializing. Thanks to Ben Spencer for the pull request!  
|  [#208](https://github.com/hapifhir/hapi-fhir/issues/208) |  Remove SupportingDocumentation resource from DSTU2 structures. This isn't actually a resource in FHIR DSTU2 and its inclusion causes errors on clients that don't understand what it is. Thanks to Travis Cummings and Michele Mottini for pointing this out.  
|  [#607](https://github.com/hapifhir/hapi-fhir/issues/607) |  Web testing UI displayed an error when a transaction was pasted into the UI for a DSTU2 server. Thanks to Suresh Kumar for reporting!  
#  0.9.7HAPI FHIR 2.3
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#change2.4-0)
##  0.9.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#release-information-4)
**Released:** 2017-03-18
##  0.9.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html#changes-4)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate (JPA): 5.1.0 -> 5.2.7
  * Hibernate Search (JPA): 5.5.4 -&gtp; 5.7.0.CR1
  * Hibernate Validator (JPA): 5.2.4 -&gtp; 5.3.4
  * Spring (JPA): 4.3.1 -> 4.3.6

  
---|---|---  
|  |  The JPA server now supports custom search parameters in DSTU3 mode. This allows users to create search parameters which contain custom paths, or even override and disable existing search parameters.  
|  |  Declared extensions with multiple type() options listed in the @Child annotation caused a crash on startup. Now this is supported.  
|  |  STU3 XHTML parser for narrative choked if the narrative contained an `&rsquot;` entity string.  
|  |  JPA server did not return an OperationOutcome in the response for a normal delete operation.  
|  |  A post-processing hook for subclasses of BaseValidatingInterceptor is now available.  
|  [#585](https://github.com/hapifhir/hapi-fhir/issues/585) |  AuthorizationInterceptor can now authorize (allow/deny) extended operations on instances and types by wildcard (on any type, or on any instance)  
|  [#595](https://github.com/hapifhir/hapi-fhir/issues/595) |  When RequestValidatingInterceptor is used, the validation results are now populated into the OperationOutcome produced by create and update operations  
|  [#542](https://github.com/hapifhir/hapi-fhir/issues/542) |  Add support for the $process-message operation to fluent client. Thanks to Hugo Soares for the pull request!  
|  [#543](https://github.com/hapifhir/hapi-fhir/issues/543) |  Parser can now be configured when encoding to use a specific base URL for extensions. Thanks to Sebastien Riviere for the pull request!  
|  [#575](https://github.com/hapifhir/hapi-fhir/issues/575) |  Remove an unneccesary database flush when saving large code systems to the JPA database, improving performance of this operation. Thanks to Joel Schneider for the pull request and analysis!  
|  |  A new post-processing hook for subclasses of BaseValidatingInterceptor is now available. The hook exposes the request details on validation failure prior to throwing an UnprocessableEntityException.  
|  [#504](https://github.com/hapifhir/hapi-fhir/issues/504) |  Custom resource types which extend Binary must not have declared extensions since this is invalid in FHIR (and HAPI would just ignore them anyhow). Thanks to Thomas S Berg for reporting!  
|  |  Standard HAPI zip/tar distributions did not include the project sources and JavaDoc JARs. Thanks to Keith Boone for pointing this out!  
|  |  JPA server terminology service was not correctly validating or expanding codes in SNOMED CT or LOINC code systems. Thanks to David Hay for reporting!  
|  [#539](https://github.com/hapifhir/hapi-fhir/issues/539) |  Attempting to search for an invalid resource type (e.g. GET base/FooResource) should return an HTTP 404 and not a 400, per the HTTP spec. Thanks to GitHub user @CarthageKing for the pull request!  
|  [#544](https://github.com/hapifhir/hapi-fhir/issues/544) |  When parsing a Bundle containing placeholder fullUrls and references (e.g. "urn:uuid:0000-0000") the resource reference targets did not get populated with the given resources. Note that as a part of this change, `IdType` and `IdDt` have been modified so that when parsing a placeholder ID, the complete placeholder including the "urn:uuid:" or "urn:oid:" prefix will be placed into the ID part. Previously, the prefix was treated as the base URL, which led to strange behaviour like the placeholder being treated as a real IDs. Thanks to GitHub user @jodue for reporting!  
|  [#538](https://github.com/hapifhir/hapi-fhir/issues/538) |  When parsing a quantity parameter on the server with a value and units but no system (e.g. `GET [base]/Observation?value=5.4||mg` ) the unit was incorrectly treated as the system. Thanks to @CarthageKing for the pull request!  
|  [#533](https://github.com/hapifhir/hapi-fhir/issues/533) |  Correct a typo in the JPA ValueSet ResourceProvider which prevented successful operation under Spring 4.3. Thanks to Robbert van Waveren for the pull request!  
|  [#547](https://github.com/hapifhir/hapi-fhir/issues/547) |  CapturingInterceptor did not buffer the response meaning that in many circumstances it did not actually capture the response. Thanks to Jenny Syed of Cerner for the pull request and contribution!  
|  [#548](https://github.com/hapifhir/hapi-fhir/issues/548) |  Clean up dependencies and remove Eclipse project files from git. Thanks to @sekaijin for the pull request!  
|  |  CLI example uploader couldn't find STU3 examples after CI server was moved to build.fhir.org  
|  |  When performing a conditional create in a transaction in JPA server, if a resource already existed matching the conditional expression, the server did not change the version of the resource but did update the body with the passed in body. Thanks to Artem Sopin for reporting and providing a test case for this!  
|  |  Client revincludes did not include the :recurse modifier. Thanks to Jenny Meinsma for pointing this out on Zulip!  
|  |  Fix an issue in JPA server where _history results were kept in memory instead of being spooled to the database as they should be. Note that as a part of this fix a new method was added to `IBundleProvider` called `getUuid()` . This method may return `null` in any current cases.  
|  |  Expanding a ValueSet in JPA server did not correctly apply `?filter=` parameter when the ValueSet being expanded had codes included explicitly (i.e. not by is-a relationship). Thanks to David Hay for reporting!  
|  |  JPA validator incorrectly returned an HTTP 400 instead of an HTTP 422 when the resource ID was not present and required, or vice versa. Thanks to Brian Postlethwaite for reporting!  
|  |  When using an annotation based client, a ClassCastException would occur under certain circumstances when the response contained contained resources  
|  |  JPA server interceptor methods for create/update/delete provided the wrong version ID to the interceptors  
|  |  Fix issue in JPA subscription module that prevented purging stale subscriptions when many were present on Postgres  
|  [#568](https://github.com/hapifhir/hapi-fhir/issues/568) |  Correct the resource paths for the DSTU2.1 validation resources, allowing the validator to correctly work against those structures. Thanks to Michael Lawley for the pull request!  
|  [#551](https://github.com/hapifhir/hapi-fhir/issues/551) |  XML Parser failed to parse large field values (greater than 512 Kb) on certain platforms where the StAX parser was overridden. Thanks to GitHub user @Jodue for the pull request!  
|  [#532](https://github.com/hapifhir/hapi-fhir/issues/532) |  Server interceptor methods were being called twice unnecessarily by the JPA server, and the DaoConfig interceptor registration framework was not actually useful. Thanks to GitHub user @mattiuusitalo for reporting!  
|  [#503](https://github.com/hapifhir/hapi-fhir/issues/503) |  AuthorizationInterceptor on JPA server did not correctly apply rules on deleting resources in a specific compartment because the resource metadata was stripped by the JPA server before the interceptor could see it. Thanks to Eeva Turkka for reporting!  
|  [#519](https://github.com/hapifhir/hapi-fhir/issues/519) |  JPA server exported CapabilityStatement includes double entries for the _id parameter and uses the wrong type (string instead of token). Thanks to Robert Lichtenberger for reporting!  
|  |  Server AuthorizationInterceptor always rejects history operation at the type level even if rules should allow it.  
|  |  Deprecate the method `ICompositeElement#getAllPopulatedChildElementsOfType(Class)` as it is no longer used by HAPI and is just an annoying step in creating custom structures. Thanks to Allan Bro Hansen for pointing this out.  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2018.html)
0.9 Changelog: 2017 
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
[ 0.10 Changelog: 2016 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)