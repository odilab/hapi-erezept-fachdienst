---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html
crawled: 2025-08-01T14:08:05.952880
---

# Introduction Changelog 2015

#  0.11.1Changelog: 2015
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changelog-2015)
#  0.11.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changelog)
#  0.11.3HAPI FHIR 1.3
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#hapi-fhir-13)
##  0.11.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#release-information)
**Released:** 2015-11-14
##  0.11.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changes)
|  |  Bump the version of a few dependencies to the latest versions (dependent HAPI modules listed in brackets): 
  * Commons-lang3 (Core): 3.3.2 -> 3.4
  * Logback (Core): 1.1.2 -> 1.1.3
  * SLF4j (Core): 1.7.102 -> 1.7.12
  * Springframework (JPA, Web Tester): 4.1.5 -> 4.2.2
  * Hibernate (JPA, Web Tester): 4.2.17 -> 5."
  * Hibernate Validator (JPA, Web Tester): 5.2.1 -> 5.2.2
  * Derby (JPA, CLI, Public Server): 10.11.1.1 -> 10.12.1.1 
  * Jetty (JPA, CLI, Public Server): 9.2.6.v20141205 -> 9.3.4.v20151007 

  
---|---|---  
|  |  JPA and Tester Overlay now use Spring Java config files instead of the older XML config files. All example projects have been updated.  
|  [#250](https://github.com/hapifhir/hapi-fhir/issues/250) |  Clients (generic and annotation) did not populate the Accept header on outgoing requests. This is now populated to indicate that the client supports both XML and JSON unless the user has explicitly requested one or the other (in which case the appropriate type only will be send in the accept header). Thanks to Avinash Shanbhag for reporting!  
|  |  QuestionnaireResponse validator now allows responses to questions of type OPENCHOICE to be of type 'string'  
|  |  JPA server now supports searching with sort by token, quantity, number, Uri, and _lastUpdated (previously only string, date, and _id were supported)  
|  |  JPA server Patient/[id]/$everything operation now supports _lastUpdated filtering and _sort'ing of results.  
|  |  JPA server removes duplicate resource index entries before storing them (e.g. if a patient has the same name twice, only one index entry is created for that name)  
|  |  JPA server now supports $everything on Patient and Encounter types (patient and encounter instance was already supported)  
|  |  Generic client operation invocations now have an additional inline method for generating the input Parameters using chained method calls instead of by passing a Parameters resource in  
|  |  Generic/fluent client search can now be performed using a complete URL supplied by user code. Thanks to Simone Heckmann pointing out that this was needed!  
|  |  Refactor JPA $everything operations so that they perform better  
|  |  Server operation methods can now declare the ID optional, via @IdParam(optional=true) meaning that the same operation can also be invoked at the type level.  
|  |  Make JPA search queries with _lastUpdated parameter a bit more efficient  
|  [#239](https://github.com/hapifhir/hapi-fhir/issues/239) |  Clean up Android project to make it more lightweight and remove a number of unneeded dependencies. Thanks to Thomas Andersen for the pull request!  
|  |  JPA server maximumn length for a URI search parameter has been reduced from 256 to 255 in order to accomodate MySQL's indexing requirements  
|  |  Introduce IJpaServerInterceptor interceptors for JPA server which can be used for more fine grained operations.  
|  |  Add ability for a server REST resource provider @Search method to declare that it should allow even parameters it doesn't understand.  
|  |  JPA $everything operations now support new parameters _content and _text, which work the same way as the same parameters on a search. This is experimental, since it is not a part of the core FHIR specification.  
|  [#250](https://github.com/hapifhir/hapi-fhir/issues/250) |  Process "Accept: text/xml" and "Accept: text/json" headers was wanting the equivalent FHIR encoding styles. These are not correct, but the intention is clear so we will honour them just to be helpful.  
|  [#254](https://github.com/hapifhir/hapi-fhir/issues/254) |  Add the ability to wire JPA conformance providers using Spring (basically, add default constructors and setters to the conformance providers). Thanks to C. Mike Bylund for the pull request!  
|  [#225](https://github.com/hapifhir/hapi-fhir/issues/225) |  Support AND/OR on _id search parameter in JPA  
|  |  JPA server gave an unhelpful error message if $meta-add or $meta-delete were called with no meta elements in the input Parameters  
|  [#227](https://github.com/hapifhir/hapi-fhir/issues/227) |  JPA server should reject resources with a reference that points to an incorrectly typed resource (e.g. points to Patient/123 but resource 123 is actually an Observation) or points to a resource that is not valid in the location it is found in (e.g. points to Patient/123 but the field supposed to reference an Organization). Thanks to Bill de Beaubien for reporting!  
|  |  In server, if a client request is received and it has an Accept header indicating that it supports both XML and JSON with equal weight, the server's default is used instead of the first entry in the list.  
|  |  Fix issue in JPA where a search with a _lastUpdated filter which matches no results would crash if the search also had a _sort  
|  |  Fix several cases where invalid requests would cause an HTTP 500 instead of a more appropriate 400/404 in the JPA server (vread on invalid version, delete with no ID, etc.)  
|  |  Fix narrative generation for DSTU2 Medication resource  
|  |  Profile validator now works for valuesets which use v2 tables  
|  [#233](https://github.com/hapifhir/hapi-fhir/issues/233) |  Fix parser issue where profiled choice element datatypes (e.g. value[x] where one allowable type is Duration, which is a profile of Quantity) get incorrectly encoded using the profiled datatype name instead of the base datatype name as required by the FHIR spec. Thanks to Nehashri Puttu Lokesh for reporting!  
|  |  Some generated Enum types in DSTU2 HAPI structures did not have latest valueset definitions applied. Thanks to Bill de Beaubien for reporting!  
|  |  JPA server can now successfully search for tokens pointing at code values (values with no explicit system but an implied one, such as Patient.gender) even if the system is supplied in the query.  
|  [#235](https://github.com/hapifhir/hapi-fhir/issues/235) |  Correct issues with Android library. Thanks to Thomas Andersen for the submission!  
|  |  JPA server incorrectly rejected match URLs if they did not contain a question mark. Thanks to Bill de Beaubien for reporting!  
|  [#234](https://github.com/hapifhir/hapi-fhir/issues/234) |  Remove invalid entries in OSGi Manifest. Thanks to Alexander Kley for the fix!  
|  |  Parsing an XML resource where the XHTML namespace was declared before the beginning of the narrative section caused an invalid re-encoding when encoding to JSON.  
|  |  Conditional deletes in JPA did not correctly process if the condition had a chain or a qualifier, e.g. "Patient?organization.name" or "Patient.identifier:missing"  
|  |  JPA server did not correctly index search parameters of type "reference" where the path had multiple entries (i.e. "Resource.path1 | Resource.path2")  
|  |  Fix a crash when encoding a Binary resource in JSON encoding if the resource has no content-type  
|  |  JPA server now supports read/history/search in transaction entries by calling the actual implementing method in the server (previously the call was simulated, which meant that many features did not work)  
|  |  ResourceReferenceDt#loadResource(IRestfulClient) did not use the client's read functionality, so it did not handle JSON responses or use interceptors. Thanks to JT for reporting!  
|  [#242](https://github.com/hapifhir/hapi-fhir/issues/242) |  Server failed to respond correctly to compartment search operations if the same provider also contained a read operation. Thanks to GitHub user @am202 for reporting!  
|  |  JPA server _history operations (server, type, instance) not correctly set the Bundle.entry.request.method to POST or PUT for create and updates of the resource.  
|  [#245](https://github.com/hapifhir/hapi-fhir/issues/245) |  Fix issue in testpage-overlay's new Java configuration where only the first configured server actually gets used.  
|  [#241](https://github.com/hapifhir/hapi-fhir/issues/241) |  Parser (XML and JSON) shouldn't encode an ID tag in resources which are part of a bundle when the resource has a UUID/OID ID.  
|  [#247](https://github.com/hapifhir/hapi-fhir/issues/247) |  Correctly set the Bundle.type value on all pages of a search result in the server, and correcltly set the same value in JPA server $everything results.  
|  |  Generated Enum types for some ValueSets did not include all codes (specifically, ValueSets which defined concepts containing child concepts did not result in Enum values for the child concepts)  
|  [#253](https://github.com/hapifhir/hapi-fhir/issues/253) |  In the JPA server, order of transaction processing should be DELETE, POST, PUT, GET, and the order should not matter within entries with the same verb. Thanks to Bill de Beaubien for reporting!  
|  |  Constructor for DateRanfeParam which dates in two DateParam instances was ignoring comparators on the DateParam.  
|  |  In JSON parsing, finding an object where an array was expected led to an unhelpful error message. Thanks to Avinash Shanbhag for reporting!  
|  |  Narrative generator did not include OperationOutcome.issue.diagnostics in the generated narrative.  
#  0.11.4HAPI FHIR 1.2
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#change1.3-0)
##  0.11.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#release-information-1)
**Released:** 2015-09-18
##  0.11.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changes-1)
|  |  JPA server now validates QuestionnaireAnswers for conformance to their respective Questionnaire if one is declared.  
---|---|---  
|  |  SyntaxHighlightingInterceptor now also highlights OperationOutcome responses for errors/exceptions.  
|  |  Switch REST server to using HttpServletRequest#getContextPath() to get the servlet's context path. This means that the server should behave more predictably, and should work in servlet 2.4 environments. Thanks to Ken Zeisset for the suggestion!  
|  [#200](https://github.com/hapifhir/hapi-fhir/issues/200) |  Vagrant environment now has an apt recipt to ensure that package lists are up to date. Thanks to GitHub user Brian S. Corbin (@corbinbs) for thr contribution!  
|  |  JPA server and generic client now both support the _tag search parameter  
|  |  Add support for BATCH mode to JPA server transaction operation  
|  |  Create new android specialty libraries for DSTU1 and DSTU2  
|  |  JPA server now implements the $validate-code operation  
|  [#125](https://github.com/hapifhir/hapi-fhir/issues/125) |  HAPI-FHIR now has support for _summary and _elements parameters, in server, client, and JPA server.  
|  |  Resource references using resource instance objects instead of resource IDs will correctly qualify the IDs with the resource type if they aren't already qualified  
|  [#211](https://github.com/hapifhir/hapi-fhir/issues/211) |  Testpage Overlay project now properly allows a custom client factory to be used (e.g. for custom authentication, etc.) Thanks to Chin Huang (@pukkaone) for the pull request!  
|  |  _include parameters now support the new `_include:recurse=FOO` syntax that has been introduced in DSTU2 in the Client, Server, and JPA Server modules. Non-recursive behaviour is now the default (previously it was recursive) and :recurse needs to be explicitly stated in order to support recursion.  
|  |  New operations added to JPA server to force re-indexing of all resources (really only useful after indexes change or bugs are fixed)  
|  |  Server now exports operations as separate resources instead of as contained resources within Conformance  
|  |  JPA server can now store Conformance resources, per a request from David Hay  
|  |  ResponseHighlightingInterceptor now skips handling responses if it finds a URL parameter of `_raw=true` (in other words, if this parameter is found, the response won't be returned as HTML even if the request is detected as coming from a browser.  
|  |  RestfulServer now supports dynamically adding and removing resource providers at runtime. Thanks to Bill Denton for adding this.  
|  |  JPA server now correctly suppresses contents of deleted resources in history  
|  |  Add new operation $get-resource-counts which will replace the resource count extensions exported in the Conformance statement by the JPA server.  
|  |  JPA server now supports $validate operation completely, including delete mode and profile validation using the RI InstanceValidator  
|  |  Add another method to IServerInterceptor which converts an exception generated on the server into a BaseServerResponseException. This is useful so that servers using ResponseHighlighterInterceptor will highlight exceptions even if they aren't created with an OperationOutcome.  
|  |  Resources and datatypes are now serializable. This is an experimental feature which hasn't yet been extensively tested. Please test and give us your feedback!  
|  [#192](https://github.com/hapifhir/hapi-fhir/issues/192) |  Server was not correctly unescaping URL parameter values with a trailing comma or an escaped backslash. Thanks to GitHub user @SherryH for all of her help in diagnosing this issue!  
|  |  Avoid crash when parsing if an invalid child element is found in a resource reference.  
|  |  Throwing a server exception (e.g. AuthenticationException) in a server interceptor's incomingRequestPreProcessed method resulted in the server returning an HTTP 500 instead of the appropriate error code for the exception being thrown. Thanks to Nagesh Bashyam for reporting!  
|  [#207](https://github.com/hapifhir/hapi-fhir/issues/207) |  Fix issue in JSON parser where invalid contained resources (missing a resourceType element) fail to parse with a confusing NullPointerException. Thanks to GitHub user @hugosoares for reporting!  
|  [#126](https://github.com/hapifhir/hapi-fhir/issues/126) |  Model classes do not use BoundCodeableConcept for example bindings that do not actually point to any codes (e.g. Observation.interpretation). Thanks to GitHub user @steve1medix for reporting!  
|  [#209](https://github.com/hapifhir/hapi-fhir/issues/209) |  _revinclude results from JPA server should have a Bundle.entry.search.mode of "include" and not "match". Thanks to Josh Mandel for reporting!  
|  [#212](https://github.com/hapifhir/hapi-fhir/issues/212) |  JPA server should reject IDs containing invalid characters (e.g. "abc:123") but should allow client assigned IDs that contain text but do not start with text. Thanks to Josh Mandel for reporting!  
|  |  :text modifier on server and JPA server did not work correctly. Thanks to Josh Mandel for reporting!  
|  |  Fix issue in client where parameter values containing a comma were sometimes double escaped.  
|  |  JPA server did not correctly index search parameters of type "URI". Thanks to David Hay for reporting! Note that if you are using the JPA server, this change means that there are two new tables added to the database schema. Updating existing resources in the database may fail unless you set default values for the resource table by issuing a SQL command similar to the following (false may be 0 or something else, depending on the database platform in use)   
`update hfj_resource set sp_coords_present = false;  
 update hfj_resource set sp_uri_present = false;`  
|  |  FIx issue in JPA server where profile declarations, tags, and security labels were not always properly removed by an update that was trying to remove them. Also don't store duplicates.  
|  |  Instance $meta operations on JPA server did not previously return the resource version and lastUpdated time  
|  |  Server responses populate Bundle.entry.fullUrl if possible. Thanks to Bill de Beaubien for reporting!  
|  |  XML parser failed to initialize in environments where a very old Woodstox library is in use (earlier than 4.0). Thanks to Bill de Beaubien for reporting!  
|  [#216](https://github.com/hapifhir/hapi-fhir/issues/216) |  Invalid/unexpected attributes found when parsing composite elements should be logged or reported to the parser error handler  
|  [#222](https://github.com/hapifhir/hapi-fhir/issues/222) |  JPA server returned deleted resources in search results when using the _tag, _id, _profile, or _security search parameters  
|  [#223](https://github.com/hapifhir/hapi-fhir/issues/223) |  Fix issue with build on Windows. Thanks to Bryce van Dyk for the pull request!  
|  [#198](https://github.com/hapifhir/hapi-fhir/issues/198) |  JPA server sorting often returned unexpected orders when multiple indexes of the same type were found on the same resource (e.g. multiple string indexed fields). Thanks to Travis Cummings for reporting!  
|  [#158](https://github.com/hapifhir/hapi-fhir/issues/158) |  XmlParser and JsonParser in DSTU2 mode should not encode empty tags in resource. Thanks to Bill De Beaubien for reporting!  
|  |  OperationDefinitions generated by server did not properly document their return parameters or the type of their input parameters.  
|  |  Operations in server generated conformance statement should only appear once per name, since the name needs to be unique.  
#  0.11.5HAPI FHIR 1.1
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#change1.2-0)
##  0.11.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#release-information-2)
**Released:** 2015-07-13
##  0.11.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changes-2)
|  |  Add support for reference implementation structures.  
---|---|---  
|  |  LoggingInterceptor for server now supports logging DSTU2 extended operations by name  
|  [#179](https://github.com/hapifhir/hapi-fhir/issues/179) |  Add fluent client method for validate operation, and support the new DSTU2 style extended operation for $validate if the client is in DSTU2 mode. Thanks to Eric from the FHIR Skype Implementers chat for reporting.  
|  |  Server now supports complete Accept header content negotiation, including q values specifying order of preference. Previously the q value was ignored.  
|  |  Server in DSTU2 mode now indicates that whether it has support for Transaction operation or not. Thanks to Kevin Paschke for pointing out that this wasn't working!  
|  [#166](https://github.com/hapifhir/hapi-fhir/issues/166) |  Questionnaire.title now gets correctly indexed in JPA server (it has no path, so it is a special case)  
|  |  JPA server now supports ifNoneMatch in GET within a transaction request.  
|  |  DateRangeParam now supports null values in the constructor for lower or upper bounds (but still not both)  
|  |  Generic/fluent client and JPA server now both support _lastUpdated search parameter which was added in DSTU2  
|  |  Add $meta, $meta-add, and $meta-delete operations to generic client  
|  |  Introduce ResponseHighlighterInterceptor, which provides syntax highlighting on RESTful server responses if the server detects that the request is coming from a browser. This interceptor has been added to fhirtest.uhn.ca responses.  
|  [#170](https://github.com/hapifhir/hapi-fhir/issues/170) |  Add better addXXX() methods to structures, which take the datatype being added as a parameter. Thanks to Claude Nanjo for the suggestion!  
|  [#152](https://github.com/hapifhir/hapi-fhir/issues/152) |  Add a new parser validation mechanism (see the validation page for info) which can be used to validate resources as they are being parsed, and optionally fail if invalid/unexpected elements are found in resource bodies during parsing.  
|  |  Web tester UI now supports _revinclude  
|  |  Parsers did not encode the resource meta element if the resource had tags but no other meta elements. Thanks to Bill de Beaubien and Claude Nanjo for finding this.  
|  [#178](https://github.com/hapifhir/hapi-fhir/issues/178) |  Support link elements in Bundle.entry when parsing in DSTU2 mode using the old (non-resource) Bundle class. Thanks to GitHub user @joedai for reporting!  
|  |  Woodstox XML parser has a default setting to limit the maximum length of an attribute to 512kb. This caused issues handling large attachments, so this setting has been increased to 100Mb. Thanks to Nikos Kyriakoulakos for reporting!  
|  [#175](https://github.com/hapifhir/hapi-fhir/issues/175) |  Some HTML entities were not correctly converted during parsing. Thanks to Nick Kitto for reporting!  
|  |  In the JPA Server: Transactions creating resources with temporary/placeholder resource IDs and other resources with references to those placeholder IDs previously did not work if the reference did not contain the resource type (e.g. Patient/urn:oid:0.1.2.3 instead of urn:oid:0.1.2.3). The latter is actually the correct way of specifying a reference to a placeholder, but the former was the only way that worked. Both forms now work, in order to be lenient. Thanks to Bill De Beaubien for reporting!  
|  |  When parsing Bundles, if Bundle.entry.base is set to "cid:" (for DSTU1) or "urn:uuid:" / "urn:oid:" (for DSTU2) this is now correctly passed as the base in resource.getId(). Conversely, when encoding bundles, if a resource ID has a base defined, and Bundle.entry.base is empty, it will now be automatically set by the parser.  
|  [#164](https://github.com/hapifhir/hapi-fhir/issues/164) |  Correct performance issue with :missing=true search requests where the parameter is a resource link. Thanks to wanghaisheng for all his help in testing this.  
|  [#188](https://github.com/hapifhir/hapi-fhir/issues/188) |  JPA server now supports sorting on reference parameters. Thanks to Vishal Kachroo for reporting that this wasn't working!  
|  |  Prevent Last-Updated header in responses coming back to the client from overwriting the 'lastUpdated' value in the meta element in DSTU2 resources. This is important because 'lastUpdated' can have more precision than the equivalent header, but the client previously gave the header priority.  
|  |  JPA server supports _count parameter in transaction containing search URL (nested search)  
|  |  DSTU2 servers now indicate support for conditional create/update/delete in their conformance statement.  
|  |  Support for the Prefer header has been added to the server, client, and JPA modules.  
|  [#196](https://github.com/hapifhir/hapi-fhir/issues/196) |  JPA server failed to search for deep chained parameters across multiple references, e.g. "Location.partof.partof.organization". Thanks to Ismael Sarmento Jr for reporting!  
|  |  Prevent crash when encoding resources with contained resources if the contained resources contained a circular reference to each other  
|  [#149](https://github.com/hapifhir/hapi-fhir/issues/149) |  The self link in the Bundle returned by searches on the server does not respect the server's address strategy (which resulted in an internal IP being shown on fhirtest.uhn.ca)  
|  |  Performing a create operation in a client used an incorrect URL if the resource had an ID set. ID should be ignored for creates. Thanks to Peter Girard for reporting!  
|  |  IParser#parseResource(Class, String) method, which is used to parse a resource into the given structure will now throw a DataFormatException if the structure is for the wrong type of resource for the one actually found in the input String (or Reader). For example, if a Patient resource is being parsed into Organization.class this will now cause an error. Previously, the XML parser would ignore the type and the JSON parser would fail. This also caused operations to not parse correctly if they returned a resource type other than parameters with JSON encoding (e.g. the $everything operation on UHN's test server). Thanks to Avinash Shanbhag for reporting!  
#  0.11.6HAPI FHIR 1.0
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#change1.1-0)
##  0.11.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#release-information-3)
**Released:** 2015-04-08
##  0.11.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changes-3)
|  |  Bump the version of a few dependencies to the latest versions: 
  * Phloc-commons (for schematron validation) 4.3.5 -> 4.3.6
  * Apache HttpClient 4.3.6 -> 4.4
  * Woodstox 4.4.0 -> 4.4.1
  * SLF4j 1.7.9 -> 1.7.10
  * Spring (used in hapi-fhir-jpaserver-base module) 4.1.3.RELEASE -> 4.1.5.RELEASE

  
---|---|---  
|  |  Add support for "profile" and "tag" elements in the resource Meta block when parsing DSTU2 structures.  
|  [#135](https://github.com/hapifhir/hapi-fhir/issues/135) |  Remove Eclipse and IntelliJ artifacts (.project, *.iml, etc) from version control. Thanks to Doug Martin for the suggestion!  
|  |  REST server methods may now have a parameter of type NarrativeModeEnum which will be populated with the value of the _narrative URL parameter if one was supplied. Annotation client methods may also include a parameter of this type, and it will be used to populate this parameter on the request URL if it is not null. Thanks to Neal Acharya for the idea!  
|  |  Android JAR now includes servlet-API classes, as the project will not work without them. Thanks  
|  [#138](https://github.com/hapifhir/hapi-fhir/issues/138) |  Add new server address strategy "ApacheProxyAddressStrategy" which uses headers "x-forwarded-host" and "x-forwarded-proto" to determine the server's address. This is useful if you are deploying a HAPI FHIR server behind an Apache proxy (e.g. for load balancing or other reasons). Thanks to Bill de Beaubien for contributing!  
|  |  Client now supports invoking transcation using a DSTU2-style Bundle resource as the input.  
|  [#148](https://github.com/hapifhir/hapi-fhir/issues/148) |  JPA Server $everything operation now allows a _count parameter  
|  |  Add a new configuration method on the parsers, `setStripVersionsFromReferences(boolean)` which configures the parser to preserve versions in resource reference links when encoding. By default, these are removed.  
|  [#162](https://github.com/hapifhir/hapi-fhir/issues/162) |  Add a framework for the Web Tester UI to allow its internal FHIR client to be configured (e.g. to add an authorization interceptor so that it adds credentials to client requests it makes). Thanks to Harsha Kumara for the suggestion!  
|  |  Allow fluent/generic client users to execute a transaction using a raw string (containing a bundle resource) as input instead of a Bundle resource class instance.  
|  |  Add methods for setting the default encoding (XML/JSON) and oretty print behaviour in the Fluent Client. Thanks to Stackoverflow user ewall for the idea.  
|  [#167](https://github.com/hapifhir/hapi-fhir/issues/167) |  Rename the Spring Bean definition for the JPA server EntityManager from "myEntityManagerFactory" to just "entityManagerFactory" as this is the default bean name expected in other parts of the Spring framework. Thanks to Mohammad Jafari for the suggestion!  
|  [#164](https://github.com/hapifhir/hapi-fhir/issues/164) |  Improve error message when a user tries to perform a create/update with an invalid or missing Content-Type header. Thanks to wanghaisheng for reporting! (This was actually a three part bug, so the following two fixes also reference this bug number)  
|  [#164](https://github.com/hapifhir/hapi-fhir/issues/164) |  Add support for :missing qualifier in generic/fluent client.  
|  [#164](https://github.com/hapifhir/hapi-fhir/issues/164) |  Add support for :missing qualifier in JPA server.  
|  |  Add a new configuration method on the parsers, `setStripVersionsFromReferences(boolean)` which configures the parser to preserve versions in resource reference links when encoding. By default, these are removed.  
|  [#171](https://github.com/hapifhir/hapi-fhir/issues/171) |  Add an exception for RESTful clients/servers to represent the HTTP 403 Forbidden status code. Thanks to Joel Costigliola for the patch!  
|  |  Add support for `_revinclude` parameter in client, server, and JPA.  
|  |  Include constants on resources (such as `Observation.INCLUDE_VALUE_STRING` ) have been switched in the DSTU2 structures to use the new syntax required in DSTU2: [resource name]:[search param NAME] insead of the DSTU1 style [resource name].[search param PATH]  
|  [#124](https://github.com/hapifhir/hapi-fhir/issues/124) |  When encoding resources, the parser will now convert any resource references to versionless references automatically (i.e. it will omit the version part automatically if one is present in the reference) since references between resources must be versionless. Additionally, references in server responses will omit the server base URL part of the reference if the base matches the base for the server giving the response.  
|  |  Searching in JPA server with no search parameter returns deleted resources when it should exclude them.  
|  [#116](https://github.com/hapifhir/hapi-fhir/issues/116) |  Requested _include values are preserved across paging links when the server returns multiple pages. Thanks to Bill de Beaubien for reporting!  
|  [#143](https://github.com/hapifhir/hapi-fhir/issues/143) |  Resource references between separate resources found in a single bundle did not get populated with the actual resource when parsing a DSTU2 style bundle. Thanks to Nick Peterson for reporting and figuring out why none of our unit tests were actually catching the problem!  
|  [#146](https://github.com/hapifhir/hapi-fhir/issues/146) |  JSON encoder did not encode contained resources when encoding a DSTU2 style bundle. Thanks to Mohammad Jafari and baopingle for all of their help in tracking this issue down and developing useful unit tests to demonstrate it.  
|  [#147](https://github.com/hapifhir/hapi-fhir/issues/147) |  JPA Server $everything operation could sometimes include a duplicate copy of the main focus resource if it was referred to in a deep chain. Thanks to David Hay for reporting!  
|  [#113](https://github.com/hapifhir/hapi-fhir/issues/113) |  When a user manually creates the list of contained resources in a resource, the encoder fails to encode any resources that don't have a '#' at the start of their ID. This is unintuitive, so we now assume that '123' means '#123'. Thanks to myungchoi for reporting and providing a test case!  
|  [#139](https://github.com/hapifhir/hapi-fhir/issues/139) |  JPA server failed to index resources containing ContactPointDt elements with populated values (e.g. Patient.telecom). Thanks to Mohammad Jafari for reporting!  
|  [#155](https://github.com/hapifhir/hapi-fhir/issues/155) |  Terser's IModelVisitor now supplies to the path to the element. This is an API change, but I don't think there are many users of the IModelVisitor yet. Please let us know if this is a big hardship and we can find an alternate way of making this change.  
|  |  Prevent server from returning a Content-Location header for search response when using the DSTU2 bundle format  
|  |  JPA server (uhnfhirtest.uhn.ca) sometimes included an empty "text" element in Bundles being returned.  
|  [#163](https://github.com/hapifhir/hapi-fhir/issues/163) |  Fix regression in early 1.0 builds where resource type sometimes does not get populated in a resource ID when the resource is parsed. Thanks to Nick Peterson for reporting, and for providing a test case!  
|  |  Disable date validation in the web tester UI, so that it is possible to enter partial dates, or dates without times, or even test out invalid date options.  
|  [#36](https://github.com/hapifhir/hapi-fhir/issues/36) |  Make BaseElement#getUndeclaredExtensions() and BaseElement#getUndeclaredExtensions() return a mutable list so that it is possible to delete extensions from a resource instance.  
|  [#168](https://github.com/hapifhir/hapi-fhir/issues/168) |  Server conformance statement check in clients (this is the check where the first time a given FhirContext is used to access a given server base URL, it will first check the server's Conformance statement to ensure that it supports the correct version of FHIR) now uses any registered client interceptors. In addition, IGenericClient now has a method "forceConformanceCheck()" which manually triggers this check. Thanks to Doug Martin for reporting and suggesting!  
|  |  Transaction server operations incorrectly used the "Accept" header instead of the "Content-Type" header to determine the POST request encoding. Thanks to Rene Spronk for providing a test case!  
|  [#129](https://github.com/hapifhir/hapi-fhir/issues/129) |  JPA Server did not mark a resource as "no longer deleted" if it was updated after being deleted. Thanks to Elliott Lavy and Lloyd McKenzie for reporting!  
|  [#128](https://github.com/hapifhir/hapi-fhir/issues/128) |  Fix regression in 0.9 - Server responds with an HTTP 500 and a NullPointerException instead of an HTTP 400 and a useful error message if the client requests an unknown resource type  
|  [#130](https://github.com/hapifhir/hapi-fhir/issues/130) |  Narrative generator incorrectly sets the Resource.text.status to 'generated' even if the given resource type does not have a template (and therefore no narrative is actually generated). Thanks to Bill de Beaubien for reporting!  
#  0.11.7HAPI FHIR 0.9
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#change1.0-0)
##  0.11.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#release-information-4)
**Released:** 2015-03-14
##  0.11.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html#changes-4)
|  |  Support for DSTU2 features introduced: New resource definitions, Bundle resource, encoding changes (ID in resource bodt, meta tag)  
---|---|---  
|  |  RESTful Client now queries the server (only once per server base URL) to ensure that the given server corresponds to the correct version of the FHIR specification, as defined by the FhirContext. This behaviour can be disabled by setting the appropriate configuration on the RestfulClientConfig. Thanks to Grahame Grieve for the suggestion!  
|  |  JPA module now supports deleting resource via transaction  
|  |  Add new properties to RestfulServer: "DefaultResponseEncoding", which allows users to configure a default encoding (XML/JSON) to use if none is specified in the client request. Currently defaults to XML. Also "DefaultPrettyPrint", which specifies whether to pretty print responses by default. Both properties can be overridden on individual requets using the appropriate Accept header or request URL parameters.  
|  |  Library now checks if custom resource types can be instantiated on startup (e.g. because they don't have a no-argument constructor) in order to avoid failing later  
|  |  Add support for quantity search params in FHIR tester UI  
|  |  Add support for FHIR "extended operations" as defined in the FHIR DSTU2 specification, for the Generic Client, Annotation Client, and Server.  
|  [#77](https://github.com/hapifhir/hapi-fhir/issues/77) |  Server now only automatically adds _include resources which are provided as references if the client request actually requested that specific include. See RestfulServer  
|  |  Sorting is now supported in the Web Testing UI (previously a button existed for sorting, but it didn't do anything)  
|  [#111](https://github.com/hapifhir/hapi-fhir/issues/111) |  Server will no longer include stack traces in the OperationOutcome returned to the client when an exception is thrown. A new interceptor called ExceptionHandlingInterceptor has been created which adds this functionality back if it is needed (e.g. for DEV setups). See the server interceptor documentation for more information. Thanks to Andy Huang for the suggestion!  
|  |  Bump a few dependency JARs to the latest versions in Maven POM: 
  * SLF4j (in base module) - Bumped to 1.7.9
  * Apache HTTPClient (in base module) - Bumped to 4.3.6
  * Hibernate (in JPA module) - Bumped to 4.3.7

  
|  [#79](https://github.com/hapifhir/hapi-fhir/issues/79) |  JPA server module now supports `_include` value of `*` . Thanks to Bill de Beaubien for reporting!  
|  [#65](https://github.com/hapifhir/hapi-fhir/issues/65) |  Fix an issue encoding extensions on primitive types in JSON. Previously the "_value" object would be an array even if the field it was extending was not repeatable. This is not correct according to the specification, nor can HAPI's parser parse this correctly. The encoder has been corrected, and the parser has been adjusted to be able to handle resources with extensions encoded in this way. Thanks to Mohammad Jafari for reporting!  
|  [#91](https://github.com/hapifhir/hapi-fhir/issues/91) |  Custom/user defined resource definitions which contained more than one child with no order defined failed to initialize properly. Thanks to Andy Huang for reporting and figuring out where the problem was!  
|  [#97](https://github.com/hapifhir/hapi-fhir/issues/97) |  DateClientParam#second() incorrectly used DAY precision instead of SECOND precision. Thanks to Tom Wilson for the pull request!  
|  [#100](https://github.com/hapifhir/hapi-fhir/issues/100) |  Fix issue where HAPI failed to initialize correctly if Woodstox library was not on the classpath, even if StAX API was configured to use a different provider. Thanks to James Butler for reporting and figuring out where the issue was!  
|  [#101](https://github.com/hapifhir/hapi-fhir/issues/101) |  Calling BaseDateTimeDt#setValue(Date, TemporalPrecisionEnum) did not always actually respect the given precision when the value was encoded. Thanks to jacksonjesse for reporting!  
|  [#103](https://github.com/hapifhir/hapi-fhir/issues/103) |  Encoders (both XML and JSON) will no longer encode contained resources if they are not referenced anywhere in the resource via a local reference. This is just a convenience for users who have parsed a resource with contained resources and want to remove some before re-encoding. Thanks to Alexander Kley for reporting!  
|  [#110](https://github.com/hapifhir/hapi-fhir/issues/110) |  Add support for DSTU2 style security labels in the parser and encoder. Thanks to Mohammad Jafari for the contribution!  
|  |  Server requests for Binary resources where the client has explicitly requested XML or JSON responses (either with a `_format` URL parameter, or an `Accept` request header) will be responded to using the Binary FHIR resource type instead of as Binary blobs. This is in accordance with the recommended behaviour in the FHIR specification.  
|  |  Observation.applies[x] and other similar search fields with multiple allowable value types were not being correctly indexed in the JPA server.  
|  [#122](https://github.com/hapifhir/hapi-fhir/issues/122) |  DateClientParam.before() incorrectly placed "<=" instead of "<" in the request URL. Thanks to Ryan for reporting!  
|  [#120](https://github.com/hapifhir/hapi-fhir/issues/120) |  User defined resource types which contain extensions that use a bound code type (e.g. an BoundCodeDt with a custom Enum) failed to parse correctly. Thanks to baopingle for reporting and providing a test case!  
|  [#67](https://github.com/hapifhir/hapi-fhir/issues/67) |  IdDt failed to recognize local identifiers containing fragments that look like real identifiers as being local identifiers even though they started with '#'. For example, a local resource reference of "#aa/_history/aa" would be incorrectly parsed as a non-local reference. Thanks to Mohammad Jafari for reporting!  
|  |  `Last-Modified` header in server was incorrectly using FHIR date format instead of RFC-1123 format.  
|  |  Server create and update methods failed with an IllegalArgumentException if the method type was a custom resource definition type (instead of a built-in HAPI type). Thanks to Neal Acharya for the analysis.  
|  |  IdDt method withServerBase returned String (unlike all of the other "withFoo" methods on that class), and did not work correctly if the IdDt already had a server base. This has been corrected. Note that the return type for this method has been changed, so code may need to be updated.  
|  [#84](https://github.com/hapifhir/hapi-fhir/issues/84) |  In previous versions of HAPI, the XML parser encoded multiple contained resources in a single `<contained></contained>` tag, even though the FHIR specification rerquires a separate `<contained></contained>` tag for each resource. This has been corrected. Note that the parser will correctly parse either form (this has always been the case) so this change should not cause any breakage in HAPI based trading partners, but may cause issues if other applications have been coded to depend on the incorrect behaviour. Thanks to Mochaholic for reporting!  
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2016.html)
0.11 Changelog: 2015 
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
[ 0.12 Changelog: 2014 ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)