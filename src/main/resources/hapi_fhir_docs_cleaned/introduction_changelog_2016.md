---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html
crawled: 2025-08-01T14:04:52.198103
---

# Introduction Changelog 2016

#  0.10.1Changelog: 2016
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changelog-2016)
#  0.10.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changelog)
#  0.10.3HAPI FHIR 2.2
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#hapi-fhir-22)
##  0.10.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information)
**Released:** 2016-12-20
##  0.10.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Derby (CLI): 10.12.1.1 -> 10.13.1.1
  * Jetty (CLI): 9.3.10.v20160621 -> 9.3.14.v20161028
  * JAnsi (CLI): 1.13 -> 1.14
  * Phloc Commons (SCH Validator): 4.4.5 -> 4.4.6

  
---|---|---  
|  [#525](https://github.com/hapifhir/hapi-fhir/issues/525) |  When parsing invalid enum values in STU3, report errors through the parserErrorHandler, not by throwing an exception. Thanks to Michael Lawley for the pull request!  
|  [#516](https://github.com/hapifhir/hapi-fhir/issues/516) |  When parsing DSTU3 resources with enumerated types that contain invalid values, the parser will now invoke the parserErrorHandler. For example, when parsing `{"resourceType":"Patient", "gender":"foo"}` the previous behaviour was to throw an InvalidArgumentException. Now, the parserErrorHandler is invoked. In addition, thw LenientErrorHandler has been modified so that this one case will result in a DataFormatException. This has the effect that servers which receive an invalid enum velue will return an HTTP 400 instead of an HTTP 500. Thanks to Jim Steel for reporting!  
|  [#520](https://github.com/hapifhir/hapi-fhir/issues/520) |  DSTU3 context now pulls the FHIR version from the actual model classes. Thanks to Michael Lawley for the pull request!  
|  |  Enhancements to the tinder-plugin's generic template features of the _generate-multi-files_ and _generate-single-file_ Maven goals as well as the Ant _hapi-tinder_ task. 
  * Provides the full Tinder data model by adding composites, valuesets, and profiles to resourcesw.
  * Supports generating files for resources, composites, valuesets, and profiles
  * Supports Velocimacro files outside the tinder-plugin JAR
  * Provides filename prefix as well as suffix properties
  * Can specify any of the Velocity configuration parameters such as _macro.provide.scope.control_ which allows safe macro recursion
  * Templates can now drill down into the referenced children for a ResourceBlockCopy
  * Normalization of properties across all three generic tasks

  
|  [#510](https://github.com/hapifhir/hapi-fhir/issues/510) |  Add a docker configuration to the hapi-fhir-jpaservr-example module. Thanks to Gijsbert van den Brink for the pull request!  
|  [#507](https://github.com/hapifhir/hapi-fhir/issues/507) |  Add utility constructors to MoneyDt. Thanks to James Ren for the contribution!  
|  |  ErrorHandler is now called (resulting in a warning by default, but can also be an exception) when arsing JSON if the resource ID is not a JSON string, or an object is found where an array is expected (e.g. repeating field). Thanks to Jenni Syed of Cerner for providing a test case!  
|  [#504](https://github.com/hapifhir/hapi-fhir/issues/504) |  Custom resource types which extend Binary must not have declared extensions since this is invalid in FHIR (and HAPI would just ignore them anyhow). Thanks to Thomas S Berg for reporting!  
|  |  Add ability to JPA server for disabling stale search expiry. This is useful if you are deploying the server to a cluster.  
|  |  Standard HAPI zip/tar distributions did not include the project sources and JavaDoc JARs. Thanks to Keith Boone for pointing this out!  
|  |  Declared extensions with multiple type() options listed in the @Child annotation caused a crash on startup. Now this is supported.  
|  |  STU3 XHTML parser for narrative choked if the narrative contained an `&rsquot;` entity string.  
|  |  As the [eBay CORS interceptor](https://github.com/eBay/cors-filter) project has gone dormant, we have introduced a new HAPI server interceptor which can be used to implement CORS support instead of using the previously recommended Servlet Filter. All server examples as well as the CLI have been switched to use this new interceptor. See the CORS Documentation for more information.  
|  [#518](https://github.com/hapifhir/hapi-fhir/issues/518) |  Allow client to gracefully handle running in DSTU3 mode but with a structures JAR that does not contain a CapabilityStatement resource. Thanks to Michael Lawley for the pull request!  
|  |  Fix issue in AuthorizationIntetceptor where transactions are blocked even when they should not be  
|  |  HAPI FHIR CLI failed to delete a file when uploading example resources while running under Windows.  
|  [#521](https://github.com/hapifhir/hapi-fhir/issues/521) |  Server should reject update if the resource body does not contain an ID, or the ID does not match the request URL. Thanks to Jim Steel for reporting!  
|  [#500](https://github.com/hapifhir/hapi-fhir/issues/500) |  Web Testing UI's next and previous buttons for paging through paged results did not work after the migration to using Thymeleaf 3. Thanks to GitHub user @gsureshkumar for reporting!  
|  [#523](https://github.com/hapifhir/hapi-fhir/issues/523) |  Fix ordering of validator property handling when an element has a name that is similar to a shorter name[x] style name. Thanks to CarthageKing for the pull request!  
|  |  Fix regression in HAPI FHIR 2.1 JPA server where some search parameters on metadata resources did not appear (e.g. "StructureDefinition.url"). Thanks to David Hay for reporting!  
|  [#528](https://github.com/hapifhir/hapi-fhir/issues/528) |  AuthorizationInterceptor was failing to allow read requests to pass when a rule authorized those resources by compartment. Thanks to GitHub user @mattiuusitalo for reporting and supplying a test case!  
|  |  Correct a typo in client `IHttpRequest` class: "bufferEntitity" should be "bufferEntity".  
|  |  Fix Web Testing UI to be able to handle STU3 servers which return CapabilityStatement instead of the previously used "Conformance" resource  
|  |  CLI example uploader couldn't find STU3 examples after CI server was moved to build.fhir.org  
|  |  Fix issue in JPA subscription module that prevented purging stale subscriptions when many were present on Postgres  
|  [#532](https://github.com/hapifhir/hapi-fhir/issues/532) |  Server interceptor methods were being called twice unnecessarily by the JPA server, and the DaoConfig interceptor registration framework was not actually useful. Thanks to GitHub user @mattiuusitalo for reporting!  
|  [#503](https://github.com/hapifhir/hapi-fhir/issues/503) |  AuthorizationInterceptor on JPA server did not correctly apply rules on deleting resources in a specific compartment because the resource metadata was stripped by the JPA server before the interceptor could see it. Thanks to Eeva Turkka for reporting!  
|  [#519](https://github.com/hapifhir/hapi-fhir/issues/519) |  JPA server exported CapabilityStatement includes double entries for the _id parameter and uses the wrong type (string instead of token). Thanks to Robert Lichtenberger for reporting!  
|  |  Server AuthorizationInterceptor always rejects history operation at the type level even if rules should allow it.  
|  |  JPA server terminology service was not correctly validating or expanding codes in SNOMED CT or LOINC code systems. Thanks to David Hay for reporting!  
|  [#539](https://github.com/hapifhir/hapi-fhir/issues/539) |  Attempting to search for an invalid resource type (e.g. GET base/FooResource) should return an HTTP 404 and not a 400, per the HTTP spec. Thanks to GitHub user @CarthageKing for the pull request!  
|  [#544](https://github.com/hapifhir/hapi-fhir/issues/544) |  When parsing a Bundle containing placeholder fullUrls and references (e.g. "urn:uuid:0000-0000") the resource reference targets did not get populated with the given resources. Note that as a part of this change, `IdType` and `IdDt` have been modified so that when parsing a placeholder ID, the complete placeholder including the "urn:uuid:" or "urn:oid:" prefix will be placed into the ID part. Previously, the prefix was treated as the base URL, which led to strange behaviour like the placeholder being treated as a real IDs. Thanks to GitHub user @jodue for reporting!  
|  [#538](https://github.com/hapifhir/hapi-fhir/issues/538) |  When parsing a quantity parameter on the server with a value and units but no system (e.g. `GET [base]/Observation?value=5.4||mg` ) the unit was incorrectly treated as the system. Thanks to @CarthageKing for the pull request!  
|  [#533](https://github.com/hapifhir/hapi-fhir/issues/533) |  Correct a typo in the JPA ValueSet ResourceProvider which prevented successful operation under Spring 4.3. Thanks to Robbert van Waveren for the pull request!  
|  [#495](https://github.com/hapifhir/hapi-fhir/issues/495) |  RestfulServer with no explicitly set FhirContext fails to detect the presents of DSTU3 structures. Thanks to GitHub user @vijayt27 for reporting!  
|  [#547](https://github.com/hapifhir/hapi-fhir/issues/547) |  CapturingInterceptor did not buffer the response meaning that in many circumstances it did not actually capture the response. Thanks to Jenny Syed of Cerner for the pull request and contribution!  
|  [#480](https://github.com/hapifhir/hapi-fhir/issues/480) |  Make the parser configurable so that when parsing an invalid empty value (e.g. `{"status":""}` ) the parser will either throw a meaningful exception or log a warning depending on the configured error handler.  
|  [#276](https://github.com/hapifhir/hapi-fhir/issues/276) |  Fix issue when serializing resources that have contained resources which are referred to from multiple places. Sometimes when serializing these resources the contained resource section would contain duplicates. Thanks to Hugo Soares and Stefan Evinance for reporting and providing a test case!  
|  |  Fix a crash in JPA server when searching using an _include if _include targets are external references (and therefore can't be loaded by the server). Thanks to Hannes Ulrich for reporting!  
|  |  Deprecate the method `ICompositeElement#getAllPopulatedChildElementsOfType(Class)` as it is no longer used by HAPI and is just an annoying step in creating custom structures. Thanks to Allan Bro Hansen for pointing this out.  
#  0.10.4HAPI FHIR 2.1
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#change2.2-0)
##  0.10.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information-1)
**Released:** 2016-11-11
##  0.10.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes-1)
|  |  STU3 structure definitions have been updated to the STU3 latest definitions (1.7.0 - SVN 10129). In particular, this version supports the new CapabilityStatement resource which replaces the previous Conformance resource (in order to reduce upgrade pain, both resource types are included in this version of HAPI)  
---|---|---  
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * spring-data-orm (JPA): 1.10.2 -> 1.10.4

  
|  [#440](https://github.com/hapifhir/hapi-fhir/issues/440) |  Remove Maven dependency on Saxon library, as it is not actually used. Thanks to Lem Edmondson for the suggestion!  
|  |  STU clients now use an Accept header which indicates support for both the old MimeTypes (e.g. `application/xml+fhir` ) and the new MimeTypes (e.g. `application/fhir+xml` )  
|  |  Add a new method to the server interceptor framework which will be called after all other processing is complete (useful for performance tracking). The server LoggingInterceptor has been switched to using this method which means that log lines will be created when processing is finished, instead of when it started.  
|  |  Client, Server, and JPA server now support experimental support for HTTP PATCH using the XML Patch and JSON Patch syntax as explored during the September 2016 Baltimore Connectathon. See [this wiki page](http://wiki.hl7.org/index.php?title=201609_PATCH_Connectathon_Track_Proposal) for a description of the syntax.   
Thanks to Pater Girard for all of his help during the connectathon in implementing this feature!  
|  |  Fluent client can now return types other than Parameters when invoking operations.  
|  |  Add a new method to ReferenceClientParam which allows you to pass in a number of IDs by a collection of Strings. Thanks to Thomas Andersen for the pul request!  
|  [#469](https://github.com/hapifhir/hapi-fhir/issues/469) |  Add a new JSON library abstraction layer to the JSON parser. This contribution shouldn't have any end-user impact but does make it easier to use the JSON parser to generate custom structures for other purposes, and should allow us to support RDF more easily at some point. Thanks to Bill Denton for the pull request and the contribution!  
|  [#455](https://github.com/hapifhir/hapi-fhir/issues/455) |  DSTU1 Bundle encoder did not include the Bundle entry author in the generated bundle. Thanks to Hannes Venter for the pull request and contribution!  
|  |  Android library now uses OkHttp client by default instead of Apache HttpClient. This should lead to much simpler support for Android in the future.  
|  |  AuthorizationInterceptor is now a bit more aggressive at blocking read operations, stopping them on the way in if there is no way they will be accepted to the resource check on the way out. In addition it can now be configured to allow/deny operation invocations at the instance level on any instance of a given type  
|  |  Remove an unneccesary database flush from JPA persistence operations  
|  [#470](https://github.com/hapifhir/hapi-fhir/issues/470) |  Add method to fluent client to allow OR search across several profiles. Thanks to Thomas Andersen for the pull request!  
|  |  Both client and server now use the new STU3 mime types by default if running in STU3 mode (in other words, using an STU3 FhirContext).  
|  |  Conditional URLs in JPA server (e.g. for delete or update) did not support the `_has` parameter  
|  [#444](https://github.com/hapifhir/hapi-fhir/issues/444) |  Times before 1970 with fractional milliseconds were parsed incorrectly. Thanks to GitHub user @CarthageKing for reporting!  
|  [#448](https://github.com/hapifhir/hapi-fhir/issues/448) |  Prevent crash in parser when parsing resource with multiple profile declarations when default type for profile is used. Thanks to Filip Domazet for the pull request!  
|  [#445](https://github.com/hapifhir/hapi-fhir/issues/445) |  STU3 servers were adding the old MimeType strings to the `Conformance.format` part of the generated server conformance statement  
|  [#446](https://github.com/hapifhir/hapi-fhir/issues/446) |  When performing an update using the client on a resource that contains other resources (e.g. Bundle update), all child resources in the parent bundle were incorrectly given the ID of the parent. Thanks to Filip Domazet for reporting!  
|  |  JPA server now sends correct `HTTP 409 Version Conflict` when a DELETE fails because of constraint issues, instead of `HTTP 400 Invalid Request`  
|  |  Server history operation did not populate the Bundle.entry.request.url field, which is required in order for the bundle to pass validation. Thanks to Richard Ettema for spotting this!  
|  |  Fix a fairly significant issue in JPA Server when using the `DatabaseBackedPagingProvider` : When paging over the results of a search / $everything operation, under certain circumstances resources may be missing from the last page of results that is returned. Thanks to David Hay for reporting!  
|  |  STU3 clients were not sending the new mimetype values in the `Content-Type` header. Thanks to Claude Nanjo for pointing this out!  
|  |  JAX-RS server was not able to handle the new mime types defined in STU3  
|  |  JPA server did not handle custom types when being called programatically (I.e. not through HTTP interface). Thanks to Anthony Mei for pointing this out!  
|  |  CLI was not correctly able to upload DSTU2 examples to any server  
|  |  STU3 validator has been upgrated to include fixes made since the 1.6.0 ballot  
|  |  Prevent JPA server from creating a bunch of FhirContext objects for versions of FHIR that aren't actually being used  
|  [#443](https://github.com/hapifhir/hapi-fhir/issues/443) |  XhtmlNode.equalsDeep() contained a bug which caused resources containing a narrative to always return `false` for STU3 `Resource#equalsDeep()` . Thanks to GitHub user @XcrigX for reporting!  
|  [#441](https://github.com/hapifhir/hapi-fhir/issues/441) |  JPA server did not correctly process searches for chained parameters where the chain passed across a field that was a choice between a reference and a non-reference type (e.g. `MedicationAdministration.medication[x]` . Thanks to GitHub user @Crudelus for reporting!  
|  [#414](https://github.com/hapifhir/hapi-fhir/issues/414) |  Handle parsing an extension without a URL more gracefully. In HAPI FHIR 2.0 this caused a NullPointerException to be thrown. Now it will trigger a warning, or throw a DataFormatException if the StrictErrorHandler is configured on the parser.  
|  |  Calling a HAPI server URL with a chain on a parameter that shouldn't accept chains (e.g. `GET [base]/Patient?name.foo=smith` ) did not return an error and instead just ignored the chained part and treated the parameter as though it did not have the chain. This led to confusing and potentially unsafe behaviour. This has been corrected to return an error to the client. Thanks to Kevin Tallevi for finding this!  
|  [#411](https://github.com/hapifhir/hapi-fhir/issues/411) |  Fix #411 - Searching by `POST [base]/_search` with urlencoded parameters doesn't work correctly if interceptors are accessing the parameters and there is are also parameters on the URL. Thanks to Jim Steel for reporting!  
|  |  JPA server shouldn't report a totalCount in Bundle of "-1" when there are no results  
|  [#454](https://github.com/hapifhir/hapi-fhir/issues/454) |  JPA server was not correctly normalizing strings with non-latin characters (e.g. Chinese chars). Thanks to GitHub user @YinAqu for reporting and providing some great analysis of the issue!  
|  [#327](https://github.com/hapifhir/hapi-fhir/issues/327) |  When encoding a resource in JSON where the resource has an extension with a value where the value is a reference to a contained resource, the reference value (e.g. "#1") did not get serialized. Thanks to GitHub user @fw060 for reporting!  
|  [#464](https://github.com/hapifhir/hapi-fhir/issues/464) |  ResponseHighlighterInterceptor now pretty-prints responses by default unless the user has explicitly requested a non-pretty-printed response (ie. using `?_pretty=false` . Thanks to Allan Brohansen and Jens Villadsen for the suggestion!  
|  |  Remove unused field (myIsContained) from ResourceTable in JPA server.  
|  [#472](https://github.com/hapifhir/hapi-fhir/issues/472) |  STU3 servers were incorrectly returning the `Content-Location` header instead of the `Content` header. The former has been removed from the FHIR specification in STU3, but the latter got removed in HAPI's code base. Thanks to Jim Steel for reporting!  
|  |  Correct several documentation issues. Thanks to Vadim Peretokin for the pull requests!  
|  |  In server, when returning a list of resources, the server sometimes failed to add `_include` resources to the response bundle if they were referred to by a contained resource. Thanks to Neal Acharya for reporting!  
|  |  Fix regression in web testing UI where "prev" and "next" buttons don't work when showing a result bundle  
|  |  JPA server should not attempt to resolve built-in FHIR StructureDefinitions from the database (this causes a significant performance hit when validating)  
|  |  BanUnsupportedHttpMethodsInterceptor was erroring out when a client attempts HTTP HEAD requests  
#  0.10.5HAPI FHIR 2.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#change2.1-0)
##  0.10.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information-2)
**Released:** 2016-08-30
##  0.10.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes-2)
|  |  STU3 structure definitions have been updated to the STU3 ballot candidate versions (1.6.0 - SVN 9663)  
---|---|---  
|  [#403](https://github.com/hapifhir/hapi-fhir/issues/403) |  It is not possible to configure both the parser and the context to preserve versions in resource references (default behaviour is to strip versions from references). Thanks to GitHub user @cknaap for the suggestion!  
|  [#406](https://github.com/hapifhir/hapi-fhir/issues/406) |  Allow servers to specify the authentication realm of their choosing when throwing an AuthenticationException. Thanks to GitHub user @allanbrohansen for the suggestion!  
|  [#416](https://github.com/hapifhir/hapi-fhir/issues/416) |  Add a new client implementation which uses the [OkHttp](http://square.github.io/okhttp/) library as the HTTP client implementation (instead of Apache HttpClient). This is particularly useful for Android (where HttpClient is a pain) but could also be useful in other places too. Thanks to Matt Clarke of Orion Health for the contribution!  
|  |  Both client and server now support the new Content Types decided in [FHIR #10199](http://gforge.hl7.org/gf/project/fhir/tracker/?action=TrackerItemEdit&tracker_id=677&tracker_item_id=10199) .   
  
This means that the server now supports `application/fhir+xml` and `application/fhir+json` in addition to the older style `application/xml+fhir` and `application/json+fhir` . In order to facilitate migration by implementors, the old style remains the default for now, but the server will respond using the new style if the request contains it. The client now uses an `Accept` header value which requests both styles with a preference given to the new style when running in DSTU3 mode.   
  
As a part of this change, the server has also been enhanced so that if a request contains a Content-Type header but no Accept header, the response will prefer the encoding specified by the Content-Type header.  
|  |  hapi-fhir-cli upload-terminology command now has an argument "-b FOO" that lets you add an authorization header in the form `Authorization: Bearer FOO`  
|  |  Inprove handling of _text and _content searches in JPA server to do better matching on partial strings  
|  |  Servers in STU3 mode will now ignore any ID or VersionID found in the resource body provided by the client when processing FHIR `update` operations. This change has been made because the FHIR specification now requires servers to ignore these values. Note that as a result of this change, resources passed to `@Update` methods will always have `null` ID  
|  |  Add new methods to `AuthorizationInterceptor` which allow user code to declare support for conditional create, update, and delete.  
|  |  Add two new methods to the parser error handler that let users trap invalid contained resources with no ID, as well as references to contained resource that do not exist.  
|  |  Improve performance when parsing resources containing contained resources by eliminating a step where references were woven twice  
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Logback (used in sample projects): 1.1.5 -> 1.1.7
  * Phloc Commons (used by schematron validator): 4.4.4 -> 4.4.5
  * Commons-IO: 2.4 -> 2.5
  * Apache HTTPClient: 4.5.1 -> 4.5.2
  * Apache HTTPCore: 4.4.4 -> 4.4.5
  * Jersey (JAX-RS tests): 2.22.2 -> 2.23.1
  * Spring (JPA, Web Tester): 4.3.0 -> 4.3.1
  * Hibernate Search (JPA): 5.5.2 -> 5.5.4
  * Thymeleaf (Narrative Generator / Web Tester): 2.1.4 ->3.0.1

  
|  |  HAPI root pom shouldn't include animal-sniffer plugin, since that causes any projects which extend this to be held to Java 6 compliance.  
|  [#150](https://github.com/hapifhir/hapi-fhir/issues/150) |  Fluent client should ignore parameter values which are null instead of including them as `?foo=null`  
|  |  A new server interceptor "BanUnsupprtedHttpMethodsInterceptor" has been added which causes the server to return an HTTP 405 if an unsupported HTTP verb is received from the client  
|  |  JSON parsing in HAPI FHIR has been switched from using JSR353 (javax.json) to using Google Gson. For this reason we are bumping the major release number to 2.0. Theoretically this should not affect projects in any major way, but Gson does have subtle differences. Two differences which popped up a fair bit in our own testing: 
A space is placed after the : in keys, e.g. what was previously encoded as `"resourceType":"Patient"` is now encoded as `"resourceType": "Patient"` (this broke a number of our unit tests with hardcoded resource definitions)  Trailing content after a valid json resource is rejected by Gson (it was ignored by the Glassfish parser we were previously using even though it was invalid)  
  
|  [#404](https://github.com/hapifhir/hapi-fhir/issues/404) |  Fix an issue where resource IDs were not correctly set when using DSTU2 HL7org structures with the JAX-RS module. Thanks to Carlo Mion for the pull request!  
|  |  hapi-fhir-testpage-overlay project contained an unneccesary dependency on hapi-fhir-jpaserver-base module, which resulted in projects using the overlay having a large number of unnneded JARs included  
|  [#409](https://github.com/hapifhir/hapi-fhir/issues/409) |  `Tag#setCode(String)` did not actually set the code it was supposed to set. Thanks to Tim Tschampel for reporting!  
|  [#401](https://github.com/hapifhir/hapi-fhir/issues/401) |  JPA server's `/Bundle` endpoint cleared the `Bundle.entry.fullUrl` field on stored bundles, resulting in invalid content being saved. Thanks to Mirjam Baltus for reporting!  
|  |  JPA server now returns HTTP 200 instead of HTTP 404 for conditional deletes which did not find any matches, per FHIR-I decision.  
|  |  Client that declares explicitly that it is searching/reading/etc for a custom type did not automatically parse into that type.  
|  |  Fix a regression when parsing resources that have contained resources, where the reference in the outer resource which links to the contained resource sometimes did does not get populated with the actual target resource instance. Thanks to Neal Acharya for reporting!  
|  [#423](https://github.com/hapifhir/hapi-fhir/issues/423) |  Parser failed to successfully encode a custom resource if it contained custom fields that also used custom types. Thanks to GitHub user @sjanic for reporting!  
|  |  When encoding a resource with a reference to another resource that has a placeholder ID (e.g. urn:uuid:foo), the urn prefix was incorrectly stripped from the reference.  
|  |  Servers for STU3 (or newer) will no longer include a `Location:` header on responses for `read` operations. This header was required in earlier versions of FHIR but has been removed from the specification.  
|  [#428](https://github.com/hapifhir/hapi-fhir/issues/428) |  Fix NullPointerException when encoding an extension containing CodeableConcept with log level set to TRACE. Thanks to Bill Denton for the report!  
|  [#426](https://github.com/hapifhir/hapi-fhir/issues/426) |  Parser failed to parse resources containing an extension with a value type of "id". Thanks to Raphael MÃ¤der for reporting!  
|  |  When committing a transaction in JPA server where the transaction contained placeholder IDs for references between bundles, the placeholder IDs were not substituted with viewing resources using the _history operation  
|  |  Fix issue in DSTU1 Bundle parsing where unexpected elements in the bundle resulted in a failure to parse.  
|  |  DSTU2 QuestionnaireResponse validator failed with an exception if the QuestionnaireResponse contained certain groups with no content  
|  |  When using `_elements` parameter on server, the server was not automatically adding the `SUBSETTED` tag as it should  
|  |  JPA server should now automatically detect if Hibernate Search (Lucene) is configured to be disabled and will not attempt to use it. This prevents a crash for some operations.  
#  0.10.6HAPI FHIR 1.6
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#change2.0-1)
##  0.10.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information-3)
**Released:** 2016-07-07
##  0.10.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes-3)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Spring (JPA, Web Tester): 4.2.5 -> 4.3.0
  * Spring-Data (JPA): 1.9.2 -> 1.10.1
  * Hibernate Search (JPA): 5.5.2 -> 5.5.3
  * Jetty (CLI): 9.3.9 -> 9.3.10

  
---|---|---  
|  [#352](https://github.com/hapifhir/hapi-fhir/issues/352) |  When performing a REST Client create or update with `Prefer: return=representation` set, if the server does not honour the Prefer header, the client will automatically fetch the resource before returning. Thanks to Ewout Kramer for the idea!  
|  [#354](https://github.com/hapifhir/hapi-fhir/issues/354) |  DSTU3 structures now have `setFoo(List)` and `setGetFooFirstRep()` methods, bringing them back to parity with the HAPI DSTU2 structures. Thanks to Rahul Somasunderam and Claude Nanjo for the suggestions!  
|  |  JPA server has now been refactored to use the new FluentPath search parameter definitions for DSTU3 resources.  
|  |  RequestValidatingInterceptor and ResponseValidatingInterceptor both have new method `setIgnoreValidatorExceptions` which causes validator exceptions to be ignored, rather than causing processing to be aborted.  
|  |  LoggingInterceptor on server has a new parameter `${requestBodyFhir}` which logs the entire request body.  
|  [#355](https://github.com/hapifhir/hapi-fhir/issues/355) |  JAX-RS server module now supports DSTU3 resources (previously it only supported DSTU2). Thanks to Phillip Warner for implementing this, and providing a pull request!  
|  |  JPA server now supports composite search parameters where the type of the composite parameter is a quantity (e.g. Observation:component-code-component-value-quantity)  
|  |  Add a new option to the CLI run-server command called `--lowmem` . This option disables some features (e.g. fulltext search) in order to allow the server to start in memory-constrained environments (e.g Raspberry Pi)  
|  |  When updating a resource via an update operation on the server, if the ID of the resource is not present in the resource body but is present on the URL, this will now be treated as a warning instead of as a failure in order to be a bit more tolerant of errors. If the ID is present in the body but does not agree with the ID in the URL this remains an error.  
|  [#363](https://github.com/hapifhir/hapi-fhir/issues/363) |  JPA server can now be configured to allow external references (i.e. references that point to resources on other servers). See JPA Documentation for information on how to use this. Thanks to Naminder Soorma for the suggestion!  
|  [#367](https://github.com/hapifhir/hapi-fhir/issues/367) |  SÃ©bastien RiviÃ¨re contributed an excellent pull request which adds a number of enhancements to JAX-RS module: 
  * Enable the conditional update and delete
  * Creation of a bundle provider, and support of the @Transaction
  * Bug fix on the exceptions handling as some exceptions throw outside bean context were not intercept.
  * Add the possibility to have the stacktrace in the jaxrsException

  
|  |  Update DSTU2 InstanceValidator to latest version from upstream  
|  |  Improve error messages when the $validate operation is called but no resource is actually supplied to validate  
|  |  Deprecate fluent client search operations without an explicit declaration of the bundle type being used. This also means that in a client `.search()` operation, the `.returnBundle(Bundle.class)` needs to be the last statement before `.execute()`  
|  |  ResponseHighlightingInterceptor has been modified based on consensus on Zulip with Grahame that requests that have a parameter of `_format=json` or `_format=xml` will output raw FHIR content instead of HTML highlighting the content as they previously did. HTML content can now be forced via the (previously existing) `_format=html` or via the two newly added values `_format=html/json` and `_format=html/xml` . Because of this change, the custom `_raw=true` mode has been deprecated and will be removed at some point.  
|  |  Server now supports the _at parameter (including multiple repetitions) for history operation  
|  |  AuthorizationInterceptor can now allow or deny requests to extended operations (e.g. $everything)  
|  [#346](https://github.com/hapifhir/hapi-fhir/issues/346) |  Server now respects the parameter `_format=application/xml+fhir"` which is technically invalid since the + should be escaped, but is likely to be used. Also, a parameter of `_format=html` can now be used, which forces SyntaxHighlightingInterceptor to use HTML even if the headers wouldn't otherwise trigger it. Thanks to Jim Steel for reporting!  
|  |  JPA server now allows "forced IDs" (ids containing non-numeric, client assigned IDs) to use the same logical ID part on different resource types. E.g. A server may now have both Patient/foo and Observation/foo on the same server.   
  
Note that existing databases will need to modify index "IDX_FORCEDID" as it is no longer unique, and perform a reindexing pass.  
|  |  Performance has been improved for the initial FhirContext object creation by avoiding a lot of unnecessary reflection. HAPI FHIR 1.5 had a regression compared to previous releases and this has been corrected, but other improvements have been made so that this release is faster than previous releases too.   
  
In addition, a new "deferred scan" mode has been implemented for even faster initialization on slower environments (e.g. Android). See the performance documentation for more information.   
  
The following shows our benchmarks for context initialization across several versions of HAPI: 
  * Version 1.4: **560ms**
  * Version 1.5: **800ms**
  * Version 1.6: **340ms**
  * Version 1.6 (deferred mode): **240ms**

  
|  [#350](https://github.com/hapifhir/hapi-fhir/issues/350) |  When serializing/encoding custom types which replace exsting choice fields by fixing the choice to a single type, the parser would forget that the field was a choice and would use the wrong name (e.g. "abatement" instead of "abatementDateType"). Thanks to Yaroslav Kovbas for reporting and providing a unit test!  
|  |  JPA server transactions sometimes created an incorrect resource reference if a resource being saved contained references that had a display value but not an actual reference. Thanks to David Hay for reporting!  
|  [#356](https://github.com/hapifhir/hapi-fhir/issues/356) |  Generated conformance statements for DSTU3 servers did not properly reference their OperationDefinitions. Thanks to Phillip Warner for implementing this, and providing a pull request!  
|  [#359](https://github.com/hapifhir/hapi-fhir/issues/359) |  Properly handle null arrays when parsing JSON resources. Thanks to Subhro for fixing this and providing a pull request!  
|  |  STU3 validator failed to validate codes where the code was a child code within the code system that contained it (i.e. not a top level code). Thanks to Jon Zammit for reporting!  
|  [#361](https://github.com/hapifhir/hapi-fhir/issues/361) |  Restore the setType method in the DSTU1 Bundle class, as it was accidentally commented out. Thanks to GitHub user @Virdulys for the pull request!  
|  |  CLI tool cache feature (-c) for upload-example task sometimes failed to write cache file and exited with an exception.  
|  |  Fix error message in web testing UI when loading pages in a search result for STU3 endpoints.  
|  |  When encoding JSON resource, the parser will now always ensure that XHTML narrative content has an XHTML namespace declaration on the first DIV tag. This was preventing validation for some resources using the official validator rules.  
|  |  Server failed to invoke operations when the name was escaped (%24execute instead of $execute). Thanks to Michael Lawley for reporting!  
|  |  JPA server transactions containing a bundle that has multiple entries trying to delete the same resource caused a 500 internal error  
|  |  JPA module failed to index search parameters that mapped to a Timing datatype, e.g. CarePlan:activitydate  
|  [#345](https://github.com/hapifhir/hapi-fhir/issues/345) |  ResponseValidatingInterceptor threw an InternalErrorException (HTTP 500) for operations that do not return any content (e.g. delete). Thanks to Mohammad Jafari for reporting!  
|  |  Server / JPA server date range search params (e.g. Encounter:date) now treat a single date with no comparator (or the eq comparator) as requiring that the value be completely contained by the range specified. Thanks to Chris Moesel for the suggestion.  
|  |  In server, if a parameter was annotated with the @Count annotation, the count would not appear in the self/prev/next links and would not actually be applied to the search results by the server. Thanks to Jim Steele for letting us know!  
|  |  Conditional update on server failed to process if the conditional URL did not have any search parameters that did not start with an underscore. E.g. "Patient?_id=1" failed even though this is a valid conditional reference.  
|  [#366](https://github.com/hapifhir/hapi-fhir/issues/366) |  When posting a resource to a server that contains an invalid value in a boolean field (e.g. Patient with an active value of "1") the server should return an HTTP 400, not an HTTP 500. Thanks to Jim Steel for reporting!  
|  [#364](https://github.com/hapifhir/hapi-fhir/issues/364) |  Enable parsers to parse and serialize custom resources that contain custom datatypes. An example has been added which shows how to do this in the docs.  
|  |  JSON parser was incorrectly encoding resource language attribute in JSON as an array instead of a string. Thanks to David Hay for reporting!  
|  [#342](https://github.com/hapifhir/hapi-fhir/issues/342) |  REST server now throws an HTTP 400 instead of an HTTP 500 if an operation which takes a FHIR resource in the request body (e.g. create, update) contains invalid content that the parser is unable to parse. Thanks to Jim Steel for the suggestion!  
|  [#369](https://github.com/hapifhir/hapi-fhir/issues/369) |  FhirTerser.cloneInto method failed to clone correctly if the source had any extensions. Thanks to GitHub user @Virdulys for submitting and providing a test case!  
|  |  Web Testing UI was not able to correctly post an STU3 transaction  
|  |  DateTime parser incorrectly parsed times where more than 3 digits of precision were provided on the seconds after the decimal point  
|  [#374](https://github.com/hapifhir/hapi-fhir/issues/374) |  Create and Update operations in server did not include ETag or Last-Modified headers even though the spec says they should. Thanks to Jim Steel for reporting!  
|  [#371](https://github.com/hapifhir/hapi-fhir/issues/371) |  Update STU3 client and server to use the new sort parameter style (param1,-param2,param). Thanks to GitHub user @euz1e4r for reporting!  
|  |  QuantityClientParam#withUnit(String) put the unit into the system part of the parameter value  
|  |  Fluent client searches with date parameters were not correctly using new prefix style (e.g. gt) instead of old one (e.g. >)  
|  [#370](https://github.com/hapifhir/hapi-fhir/issues/370) |  Some built-in v3 code systems for STU3 resources were missing certain codes, which caused false failures when validating resources. Thanks to GitHub user @Xoude for reporting!  
|  [#365](https://github.com/hapifhir/hapi-fhir/issues/365) |  Some methods on DSTU2 model structures have JavaDocs that incorrectly claim that the method will not return null when in fact it can. Thanks to Rick Riemer for reporting!  
|  [#267](https://github.com/hapifhir/hapi-fhir/issues/267) |  Operation definitions (e.g. for $everything operation) in the generated server conformance statement should not include the $ prefix in the operation name or code. Thanks to Dion McMurtrie for reporting!  
|  [#378](https://github.com/hapifhir/hapi-fhir/issues/378) |  Server generated OperationDefinition resources did not validate due to some missing elements (kind, status, etc.). Thanks to Michael Lawley for reporting!  
|  [#379](https://github.com/hapifhir/hapi-fhir/issues/379) |  Operations that are defined on multiple resource provider types with the same name (e.g. "$everything") are now automatically exposed by the server as separate OperationDefinition resources per resource type. Thanks to Michael Lawley for reporting!  
|  [#380](https://github.com/hapifhir/hapi-fhir/issues/380) |  OperationDefinition resources generated automatically by the server for operations that are defined within resource/plain providers incorrectly stated that the maximum cardinality was "*" for non-collection types with no explicit maximum stated, which is not the behaviour that the JavaDoc on the @OperationParam annotation describes. Thanks to Michael Lawley for reporting!  
|  |  Server parameters annotated with `@Since` or `@Count` which are of a FHIR type such as IntegerDt or DateTimeType will now be set to null if the client's URL does not contain this parameter. Previously they would be populated with an empty instance of the FHIR type, which was inconsistent with the way other server parameters worked.  
|  |  DecimalType used BigDecimal constructor instead of valueOf method to create a BigDecimal from a double, resulting in weird floating point conversions. Thanks to Craig McClendon for reporting!  
|  [#394](https://github.com/hapifhir/hapi-fhir/issues/394) |  Remove the depdendency on a method from commons-lang3 3.3 which was causing issues on some Android phones which come with an older version of this library bundled. Thanks to Paolo Perliti for reporting!  
|  |  Parser is now better able to handle encoding fields which have been populated with a class that extends the expected class  
|  |  When declaring a child with `order=Child.REPLACE_PARENT` the serialized form still put the element at the end of the resource instead of in the correct order  
|  |  Fix STU3 JPA resource providers to allow validate operation at instance level  
|  |  Improve performance when parsing large bundles by fixing a loop over all of the entries inthe bundle to stitch together cross-references, which was happening once per entry instead of once overall. Thanks to Erick on the HAPI FHIR Google Group for noticing that this was an issue!  
|  |  Remove some clases that were deprecated over a year ago and have suitable replacements: 
  * QualifiedDateParam has been removed, but DateParam may be used instead
  * PathSpecification has been removedm but Include may be used instead

  
|  |  Remove the Remittance resource from DSTU2 structures, as it is not a real resource and was causing issues with interoperability with the .NET client.  
|  |  DSTU2+ servers no longer return the Category header, as this has been removed from the FHIR specification (and tags are now available in the resource body so the header was duplication/wasted bandwidth)  
|  |  JSON parser no longer allows the resource ID to be specified in an element called "_id" (the correct one is "id"). Previously _id was allowed because some early FHIR examples used that form, but this was never actually valid so it is now being removed.  
#  0.10.7HAPI FHIR 1.5
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#change1.6-1)
##  0.10.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information-4)
**Released:** 2016-04-20
##  0.10.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes-4)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate (JPA, Web Tester): 5.0.7 -> 5.1.0
  * Spring (JPA, Web Tester): 4.2.4 -> 4.2.5
  * SLF4j (All): 1.7.14 -> 1.7.21

  
---|---|---  
|  |  The RequestDetails and ActionRequestDetails objects which are passed to server interceptor methods and may also be used as server provider method arguments now has a new method `Map<String, String> getUserData()` which can be used to pass data and objects between interceptor methods to to providers. This can be useful, for instance, if an authorization interceptor wants to pass the logged in user's details to other parts of the server.  
|  |  Support comments when parsing and encoding both JSON and XML. Comments are retrieved and added to the newly created methods IBase#getFormatCommentsPre() and IBase#getFormatCommentsPost()  
|  [#304](https://github.com/hapifhir/hapi-fhir/issues/304) |  Improve error message if incorrect type is placed in a list field in the data model. Java uses generics to prevent this at compile time, but if someone is in an environment without generics this helps improve the error message at runtime. Thanks to Hugo Soares for suggesting.  
|  |  Per discussion on the FHIR implementer chat, the JPA server no longer includes _revinclude matches in the Bundle.total count, or the page size limit.  
|  |  JPA server now persists search results to the database in a new table where they can be temporaily preserved. This makes the JPA server much more scalable, since it no longer needs to store large lists of pages in memory between search invocations.   
  
Old searches are deleted after an hour by default, but this can be changed via a setting in the DaoConfig.  
|  |  JPA servers' resource version history mechanism has been adjusted so that the history table keeps a record of all versions including the current version. This has the very helpful side effect that history no longer needs to be paged into memory as a complete set. Previously history had a hard limit of only being able to page the most recent 20000 entries. Now it has no limit.  
|  [#293](https://github.com/hapifhir/hapi-fhir/issues/293) |  Added options to the CLI upload-examples command which allow it to cache the downloaded content file, or use an arbitrary one. Thanks to Adam Carbone for the pull request!  
|  |  JPA server applies _lastUpdated filter inline with other searches wherever possible instead of applying this filter as a second query against the results of the first query. This should improve performance when searching against large datasets.  
|  |  Parsers have new method `setDontEncodeElements` which can be used to force the parser to not encode certain elements in a resource when serializing. For example this can be used to omit sensitive data or skip the resource metadata.  
|  |  JPA server database design has been adjusted so that different tables use different sequences to generate their indexes, resulting in more sequential resource IDs being assigned by the server  
|  |  DSTU2 resources now have a `getMeta()` method which returns a modifiable view of the resource metadata for convenience. This matches the equivalent method in the DSTU3 structures.  
|  [#315](https://github.com/hapifhir/hapi-fhir/issues/315) |  Add a new method to FhirContext called `setDefaultTypeForProfile` which can be used to specify that when recources are received which declare support for specific profiles, a specific custom structures should be used instead of the default. For example, if you have created a custom Observation class for a specific profile, you could use this method to cause your custom type to be used by the parser for resources in a search bundle you receive.   
  
See the documentation page on Profiles and Extensions for more information.  
|  [#321](https://github.com/hapifhir/hapi-fhir/issues/321) |  Set up the tinder plugin to work as an ant task as well as a Maven plugin, and to use external sources. Thanks to Bill Denton for the pull request!  
|  |  Introduce a JAX-RS client provider which can be used instead of the default Apache HTTP Client provider to provide low level HTTP services to HAPI's REST client. See JAX-RS & Alternate HTTP Client Providers for more information.   
  
This is useful in cases where you have other non-FHIR REST clients using a JAX-RS provider and want to take advantage of the rest of the framework.   
  
Thanks to Peter Van Houte from Agfa for the amazing work!  
|  |  Operations methods defined using `@Operation` will now infer the maximum number of repetitions of their parameters by the type of the parameter. Previously if a default `max()` value was not specified in the `@OperationParam` annotation on a parameter, the maximum was assumed to be 1. Now, if a max value is not explicitly specified and the type of the parameter is a basic type (e.g. `StringDt`) the max will be 1. If the parameter is a collection type (e.g. `List<StringDt>`) the max will be *  
|  [#317](https://github.com/hapifhir/hapi-fhir/issues/317) |  Operation methods defined using `@Operation` may now use search parameter types, such as `TokenParam` and `TokenAndListParam` as values. Thanks to Christian Ohr for reporting!  
|  |  Add databases indexes to JPA module search index tables for the RES_ID column on each. This should help performance when searching over large datasets. Thanks to Emmanuel Duviviers for the suggestion!  
|  |  JPA server number and quantity search params now follow the rules for the use of precision in search terms outlined in the [search page](https://www.hl7.org/fhir/search.html) of the FHIR specification. For example, previously a 1% tolerance was applied for all searches (10% for approximate search). Now, a tolerance which respects the precision of the search term is used (but still 10% for approximate search).  
|  |  JPA server now supports searching for `_tag:not=[tag]` which enables finding resources that to not have a given tag/profile/security tag. Thanks to Lars Kristian Roland for the suggestion!  
|  |  Improve CLI error message if the tool can't bind to the requested port. Thanks to Claude Nanjo for the suggestion!  
|  |  RestfulServer now manually parses URL parameters instead of relying on the container's parsed parameters. This is useful because many Java servlet containers (e.g. Tomcat, Glassfish) default to ISO-8859-1 encoding for URLs insetad of the UTF-8 encoding specified by FHIR.  
|  |  ResponseHighlightingInterceptor now doesn't highlight if the request has an Origin header, since this probably denotes an AJAX request.  
|  |  JPA server now supports :above and :below qualifiers on URI search params  
|  |  Add optional support (disabled by default for now) to JPA server to support inline references containing search URLs. These URLs will be resolved when a resource is being created/updated and replaced with the single matching resource. This is being used as a part of the May 2016 Connectathon for a testing scenario.  
|  |  The server no longer adds a `WWW-Authenticate` header to the response if any resource provider code throws an `AuthenticationException` . This header is used for interactive authentication, which isn't generally appropriate for FHIR. We added code to add this header a long time ago for testing purposes and it never got removed. Please let us know if you need the ability to add this header automatically. Thanks to Lars Kristian Roland for pointing this out.  
|  [#339](https://github.com/hapifhir/hapi-fhir/issues/339) |  Security Fix: XML parser was vulnerable to XXE (XML External Entity) processing, which could result in local files on disk being disclosed. See [this page](https://www.owasp.org/index.php/XML_External_Entity_\(XXE\)_Processing) for more information. Thanks to Jim Steel for reporting!  
|  |  In the client, the create/update operations on a Binary resource (which use the raw binary's content type as opposed to the FHIR content type) were not including any request headers (Content-Type, User-Agent, etc.) Thanks to Peter Van Houte of Agfa Healthcare for reporting!  
|  |  Handling of Binary resources containing embedded FHIR resources for create/update/etc operations has been corrected per the FHIR rules outlined at [Binary Resource](http://hl7.org/fhir/binary.html) in both the client and server.   
  
Essentially, if the Binary contains something that isn't FHIR (e.g. an image with an image content-type) the client will send the raw data with the image content type to the server. The server will place the content type and raw data into a Binary resource instance and pass those to the resource provider. This part was already correct previous to 1.5.   
  
On the other hand, if the Binary contains a FHIR content type, the Binary is now sent by the client to the server as a Binary resource with a FHIR content-type, and the embedded FHIR content is contained in the appropriate fields. The server will pass this "outer" Binary resource to the resource provider code.  
|  [#297](https://github.com/hapifhir/hapi-fhir/issues/297) |  When `IServerInterceptor#incomingRequestPreHandled()` is called for a `@Validate` method, the resource was not populated in the `ActionRequestDetails` argument. Thanks to Ravi Kuchi for reporting!  
|  [#298](https://github.com/hapifhir/hapi-fhir/issues/298) |  Request to server at `[baseUrl]/metadata` with an HTTP method other than GET (e.g. POST, PUT) should result in an HTTP 405. Thanks to Michael Lawley for reporting!  
|  [#302](https://github.com/hapifhir/hapi-fhir/issues/302) |  Fix a server exception when trying to automatically add the profile tag to a resource which already has one or more profiles set. Thanks to Magnus Vinther for reporting!  
|  [#296](https://github.com/hapifhir/hapi-fhir/issues/296) |  QuantityParam parameters being used in the RESTful server were ignoring the `:missing` qualifier. Thanks to Alexander Takacs for reporting!  
|  [#299](https://github.com/hapifhir/hapi-fhir/issues/299) |  Annotation client failed with an exception if the response contained extensions on fields in the resonse Bundle (e.g. Bundle.entry.search). Thanks to GitHub user am202 for reporting!  
|  [#274](https://github.com/hapifhir/hapi-fhir/issues/274) |  Primitive elements with no value but an extension were sometimes not encoded correctly in XML, and sometimes not parsed correctly in JSON. Thanks to Bill de Beaubien for reporting!  
|  [#280](https://github.com/hapifhir/hapi-fhir/issues/280) |  The Web Testing UI has long had an issue where if you click on a button which navigates to a new page (e.g. search, read, etc) and then click the back button to return to the original page, the button you clicked remains disabled and can't be clicked again (on Firefox and Safari). This is now fixed. Unfortunately the fix means that the buttom will no longer show a "loading" spinner, but there doesn't seem to be another way of fixing this. Thanks to Mark Scrimshire for reporting!  
|  |  Extensions found while parsing an object that doesn't support extensions are now reported using the IParserErrorHandler framework in the same way that other similar errors are handled. This allows the parser to be more lenient when needed.  
|  [#308](https://github.com/hapifhir/hapi-fhir/issues/308) |  Prevent an unneeded warning when parsing a resource containing a declared extension. Thanks to Matt Blanchette for reporting!  
|  |  Web Tester UI did not invoke VRead even if a version ID was specified. Thanks to Poseidon for reporting!  
|  |  JPA server returned the wrong Bundle.type value (COLLECTION, should be SEARCHSET) for $everything operation responses. Thanks to Sonali Somase for reporting!  
|  [#305](https://github.com/hapifhir/hapi-fhir/issues/305) |  REST and JPA server should reject update requests where the resource body does not contain an ID, or contains an ID which does not match the URL. Previously these were accepted (the URL ID was trusted) which is incorrect according to the FHIR specification. Thanks to GitHub user ametke for reporting!   
  
As a part of this change, server error messages were also improved for requests where the URL does not contain an ID but needs to (e.g. for an update) or contains an ID but shouldn't (e.g. for a create)  
|  |  When fields of type BoundCodeDt (e.g. Patient.gender) are serialized and deserialized using Java's native object serialization, the enum binder was not serialized too. This meant that values for the field in the deserialized object could not be modified. Thanks to Thomas Andersen for reporting!  
|  [#313](https://github.com/hapifhir/hapi-fhir/issues/313) |  REST Server responded to HTTP OPTIONS requests with any URI as being a request for the server's Conformance statement. This is incorrect, as only a request for `OPTIONS [base url]` should be treated as such. Thanks to Michael Lawley for reporting!  
|  |  REST annotation style client was not able to handle extended operations ($foo) where the response from the server was a raw resource instead of a Parameters resource. Thanks to Andrew Michael Martin for reporting!  
|  |  Server now correctly serves up Binary resources using their native content type (instead of as a FHIR resource) if the request contains an accept header containing "application/xml" as some browsers do.  
|  [#315](https://github.com/hapifhir/hapi-fhir/issues/315) |  Parsing/Encoding a custom resource type which extends a base type sometimes caused the FhirContext to treat all future parses of the same resource as using the custom type even when this was not wanted.   
  
Custom structures may now be explicitly declared by profile using the `setDefaultTypeForProfile` method.   
  
This issue was discovered and fixed as a part of the implementation of issue #315.  
|  |  REST search parameters with a prefix/comparator had not been updated to use the DSTU2 style prefixes (gt2011-01-10) instead of the DSTU1 style prefixes (>2011-01-01). The client has been updated so that it uses the new prefixes if the client has a DSTU2+ context. The server has been updated so that it now supports both styles.   
  
As a part of this change, a new enum called ParamPrefixEnum has been introduced. This enum replaces the old QuantityCompararatorEnum which has a typo in its name and can not represent several new prefixes added since DSTU1.  
|  |  JPA server now allows searching by token parameter using a system only and no code, giving a search for any tokens which match the given token with any code. Previously the expected behaviour for this search was not clear in the spec and HAPI had different behaviour from the other reference servers.  
|  [#312](https://github.com/hapifhir/hapi-fhir/issues/312) |  Parser failed with a NPE while encoding resources if the resource contained a null extension. Thanks to steve1medix for reporting!  
|  [#320](https://github.com/hapifhir/hapi-fhir/issues/320) |  In generated model classes (DSTU1/2) don't use BoundCodeDt and BoundCodeableConceptDt for coded fields which use example bindings. Thanks to GitHub user Ricq for reporting!  
|  |  DateTimeType should fail to parse 1974-12-25+10:00 as this is not a valid time in FHIR. Thanks to Grahame Grieve for reporting!  
|  |  When parsing a Bundle resource, if the Bundle.entry.request.url contains a UUID but the resource body has no ID, the Resource.id will be populated with the ID from the Bundle.entry.request.url. This is helpful when round tripping Bundles containing UUIDs.  
|  |  When parsing a DSTU3 bundle, references between resources did not have the actual resource instance populated into the reference if the IDs matched as they did in DSTU1/2.  
|  [#326](https://github.com/hapifhir/hapi-fhir/issues/326) |  Contained resource references on DSTU3 resources were not serialized correctly when using the Json Parser. Thanks to GitHub user @fw060 for reporting and supplying a patch which corrects the issue!  
|  [#325](https://github.com/hapifhir/hapi-fhir/issues/325) |  DSTU3 model classes equalsShallow and equalsDeep both did not work correctly if a field was null in one object, but contained an empty object in the other (e.g. a StringType with no actual value in it). These two should be considered equal, since they would produce the exact same wire format.   
  
Thanks to GitHub user @ipropper for reporting and providing a test case!  
|  |  Extensions containing resource references did not get encoded correctly some of the time. Thanks to Poseidon for reporting!  
|  |  Parsers (both XML and JSON) encoded the first few elements of DSTU3 structures in the wrong order: Extensions were placed before any other content, which is incorrect (several elements come first: meta, text, etc.)  
|  |  In server implementations, the Bundle.entry.fullUrl was not getting correctly populated on Hl7OrgDstu2 servers. Thanks to Christian Ohr for reporting!  
|  [#335](https://github.com/hapifhir/hapi-fhir/issues/335) |  Ensure that element IDs within resources (i.e. IDs on elements other than the resource itself) get serialized and parsed correctly. Previously, these didn't get serialized in a bunch of circumstances. Thanks to Vadim Peretokin for reporting and providing test cases!  
|  |  Server param of `_summary=text` did not include mandatory elements in return as well as the text element, even though the FHIR specification required it.  
|  |  Remove invalid resource type "Documentation" from DSTU2 structures.  
|  [#291](https://github.com/hapifhir/hapi-fhir/issues/291) |  Fix a failure starting the REST server if a method returns an untyped List, which among other things prevented resource provider added to the server as CDI beans in a JBoss enviroment. Thanks to GitHub user fw060 (Fei) for reporting and figuring out exactly why this wasn't working!  
|  |  JPA server did not respect target types for search parameters. E.g. Appointment:patient has a path of "Appointment.participant.actor" and a target type of "Patient". The search path was being correctly handled, but the target type was being ignored.  
#  0.10.8HAPI FHIR 1.4
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#change1.5-1)
##  0.10.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#release-information-5)
**Released:** 2016-02-04
##  0.10.8.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html#changes-5)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Hibernate (JPA, Web Tester): 5.0.3 -> 5.0.7
  * Springframework (JPA, Web Tester): 4.2.2 -> 4.2.4
  * Phloc-Commons (Schematron Validator): 4.3.6 -> 4.4.4
  * Apache httpclient (Client): 4.4 -> 4.5.1
  * Apache httpcore (Client): 4.4 -> 4.4.4
  * SLF4j (All): 1.7.13 -> 1.7.14

  
---|---|---  
|  [#265](https://github.com/hapifhir/hapi-fhir/issues/265) |  Add a constraint that the Maven build will only run in JDK 8+. HAPI remains committed to supporting JDK 6+ in the compiled library, but these days it can only be built using JDK 8. Thanks to joelsch for the PR!  
|  [#240](https://github.com/hapifhir/hapi-fhir/issues/240) |  Support target parameter type in _include / _revinclude values, e.g. _include=Patient:careProvider:Organization. Thanks to Joe Portner for reporting!  
|  |  Use ResponseHighlighterInterceptor in the hapi-fhir-jpaserver-example project to provide nice syntax highlighting. Thanks to Rob Hausam for noting that this wasn't there.  
|  |  Introduce custom @CoverageIgnore annotation to hapi-fhir-base in order to remove dependency on cobertura during build and in runtime.  
|  |  JsonParser has been changed so that when serializing numbers it will use plain format (0.001) instead of scientific format (1e-3). The latter is valid JSON, and the parser will still correctly parse either format (all clients should be prepared to) but this change makes serialized resources appear more consistent between XML and JSON. As a result of this change, trailing zeros will now be preserved when serializing as well.  
|  [#278](https://github.com/hapifhir/hapi-fhir/issues/278) |  Add DSTU3 example to hapi-fhir-jpaserver-example. Thanks to Karl Davis for the Pull Request!  
|  |  RestfulServer#setUseBrowserFriendlyContentTypes has been deprecated and its functionality removed. The intention of this feature was that if it detected a request coming in from a browser, it would serve up JSON/XML using content types that caused the browsers to pretty print. But each browser has different rules for when to pretty print, and after we wrote that feature both Chrome and FF changed their rules to break it anyhow. ResponseHighlightingInterceptor provides a better implementation of this functionality and should be used instead.  
|  |  Add two new server interceptors: RequestValidatingInterceptor and ResponseValidatingInterceptor which can be used to validate incoming requests or outgoing responses using the standard FHIR validation tools. See the Server Validation Page for examples of how to use these interceptors. These interceptors have both been enabled on the [public test page](http://fhirtest.uhn.ca).  
|  [#288](https://github.com/hapifhir/hapi-fhir/issues/288) |  Add new methods to RestfulClientFactory allowing you to configure the size of the client pool used by Apache HttpClient. Thanks to Matt Blanchette for the pull request!  
|  |  Add support for new modifier types on Token search params in Server and annotation client.  
|  [#289](https://github.com/hapifhir/hapi-fhir/issues/289) |  Allow server to correctly figure out it's own address even if the container provides a Servlet Context Path which does not include the root. Thanks to Petro Mykhaylyshyn for the pull request!  
|  [#251](https://github.com/hapifhir/hapi-fhir/issues/251) |  Introduce a JAX-RS version of the REST server, which can be used to deploy the same resource provider implementations which work on the existing REST server into a JAX-RS (e.g. Jersey) environment. Thanks to Peter Van Houte from Agfa for the amazing work!  
|  |  CLI now supports writing to file:// URL for 'upload-examples' command  
|  |  GZipped content is now supported for client-to-server uploads (create, update, transaction, etc.). The server will not automatically detect compressed incoming content and decompress it (this can be disabled using a RestfulServer configuration setting). A new client interceptor has been added which compresses outgoing content from the client.  
|  |  Add a new method to the generic/fluent client for searching: `.count(int)`  
This replaces the existing ".limitTo(int)" method which has now been deprocated because it was badly named and undocumented.  
|  |  Profile validator has been configured to allow extensions even if they aren't explicitly declared in the profile.  
|  |  Remove a dependency on a Java 1.7 class (ReflectiveOperationException) in several spots in the codebase. This dependency was accidentally introduced in 1.3, and animal-sniffer-plugin failed to detect it (sigh).  
|  |  When serializing a value[x] field, if the value type was a profiled type (e.g. markdown is a profile of string) HAPI 1.3 would use the base type in the element name, e.g. valueString instead of valueMarkdown. After discussion with Grahame, this appears to be incorrect behaviour so it has been fixed.  
|  |  Server-generated conformance statements incorrectly used /Profile/ instead of /StructureDefinition/ in URL links to structures.  
|  [#283](https://github.com/hapifhir/hapi-fhir/issues/283) |  Remove dependency on Servlet-API 3.0+ by using methods available in 2.5 where possible. Note that we continue to use Servlet-API 3.0+ features in some parts of the JPA API, so running in an old serlvet container should be tested well before use. Thanks to Bill Denton for reporting!  
|  [#286](https://github.com/hapifhir/hapi-fhir/issues/286) |  Server conformance statement should include search parameter chains if the chains are explicitly defined via @Search(whitelist={....}). Thanks to lcamilo15 for reporting!  
|  |  Remove afterPropertiesSet() call in Java config for JPA server's EntityManagerFactory. This doesn't need to be called manually, the the manual call led to a warning about the EntityManager being created twice.  
|  [#259](https://github.com/hapifhir/hapi-fhir/issues/259) |  Make IBoundCodeableConcept and IValueSetEnumBinder serializable, fixing an issue when trying to serialize model classes containing bound codes. Thanks to Nick Peterson for the Pull Request!  
|  |  JPA server transaction attempted to validate resources twice each, with one of these times being before anything had been committed to the database. This meant that if a transaction contained both a Questionnaire and a QuestionnaireResponse, it would fail because the QuestionnaireResponse validator wouldn't be able to find the questionnaire. This is now corrected.  
|  |  Narrative generator framework has removed the ability to generate resource titles. This functionality was only useful for DSTU1 implementations and wasn't compatible with coming changes to that API.  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2017.html)
0.10 Changelog: 2016 
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
[ 0.11 Changelog: 2015 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)