---
source: https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html
crawled: 2025-08-01T14:05:02.195021
---

# Introduction Changelog 2014

#  0.12.1Changelog: 2014
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changelog-2014)
#  0.12.2Changelog
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changelog)
#  0.12.3HAPI FHIR 0.8
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#hapi-fhir-08)
##  0.12.3.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information)
**Released:** 2014-12-17
##  0.12.3.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes)
|  |  **API CHANGE:** The "FHIR structures" for DSTU1 (the classes which model the resources and composite datatypes) have been moved out of the core JAR into their own JAR, in order to allow support for DEV resources, and DSTU2 resources when thast version is finalized. See the DSTU2 page for more information.  
---|---|---  
|  |  Remove dependency on JAXB libraries, which were used to parse and encode dates and times (even in the JSON parser). JAXB is built in to most JDKs but the version bundled with IBM's JDK is flaky and resulted in a number of problems when deploying to Websphere.  
|  |  When using Generic Client, if performing a create or update operation using a String as the resource body, the client will auto-detect the FHIR encoding style and send an appropriate Content-Type header.  
|  [#53](https://github.com/hapifhir/hapi-fhir/issues/53) |  DateDt left precision value as null in the constructor DateDt(Date) .  
|  |  **API Change** : The IResource#getResourceMetadata() method has been changed from returning `Map<ResourceMetadataKeyEnum<?>, Object>` to returning a new type called `ResourceMetadataMap`. This new type implements `Map<ResourceMetadataKeyEnum<?>, Object>` itself, so this change should not break existing code, but may require a clean build in order to run correctly.  
|  |  Add a method String IResource#getResourceName() which returns the name of the resource in question (e.g. "Patient", or "Observation"). This is intended as a convenience to users.  
|  |  Server gives a more helpful error message if multiple IResourceProvider implementations are provided for the same resource type. Thanks to wanghaisheng for the idea!  
|  [#61](https://github.com/hapifhir/hapi-fhir/issues/61) |  Bring DSTU1 resource definitions up to version 0.0.82-2929   
Bring DEV resource definitions up to 0.4.0-3775   
Thanks to crinacimpian for reporting!  
|  [#62](https://github.com/hapifhir/hapi-fhir/issues/62) |  JPA server did not correctly process _include requests if included resources were present with a non-numeric identifier. Thanks to Bill de Beaubien for reporting!  
|  [#38](https://github.com/hapifhir/hapi-fhir/issues/38) |  Profile generation on the server was not working due to IdDt being incorrectly used. Thanks to Bill de Beaubien for the pull request!  
|  [#73](https://github.com/hapifhir/hapi-fhir/issues/73) |  Add convenience methods to TokenOrListParam to test whether any of a set of tokens match the given requested list.  
|  [#86](https://github.com/hapifhir/hapi-fhir/issues/86) |  Add a protected method to RestfulServer which allows developers to implement their own method for determining which part of the request URL is the FHIR request path (useful if you are embedding the RestulServer inside of another web framework). Thanks to Harsha Kumara for the pull request!  
|  [#42](https://github.com/hapifhir/hapi-fhir/issues/42) |  Profiles did not generate correctly if a resource definition class had a defined extension which was of a composite type. Thanks to Bill de Beaubien for the pull request!  
|  [#44](https://github.com/hapifhir/hapi-fhir/issues/44) |  Remove unnecessary IOException from narrative generator API. Thanks to Petro Mykhailysyn for the pull request!  
|  [#48](https://github.com/hapifhir/hapi-fhir/issues/48) |  Introduced a new `@ProvidesResources` annotation which can be added to resource provider and servers to allow them to declare additional resource classes they are able to serve. This is useful if you have a server which can serve up multiple classes for the same resource type (e.g. a server that sometimes returns a default Patient, but sometimes uses a custom subclass). Thanks to Bill de Beaubien for the pull request!  
|  [#49](https://github.com/hapifhir/hapi-fhir/issues/49) |  Introduced a new `@Destroy` annotation which can be added to a resource provider method. This method will be called by the server when it is being closed/destroyed (e.g. when the application is being undeployed, the container is being shut down, etc.) Thanks to Bill de Beaubien for the pull request!  
|  |  Add a new method handleException to the server interceptor framework which allows interceptors to be notified of any exceptions and runtime errors within server methods. Interceptors may optionally also override the default error handling behaviour of the RestfulServer.  
|  |  Add constants to BaseResource for the "_id" search parameter which all resources should support.  
|  |  **Deprecated API Removal** : The following classes (which were deprocated previously) have now been removed: 
  * **ISecurityManager** : If you are using this class, the same functionality is available through the more general purpose server interceptor capabilities.
  * **CodingListParam** : This class was made redundant by the TokenOrListParam class, which can be used in its place.

  
|  |  DateRangeParam parameters on the server now return correct `getLowerBoundAsInstant()` and `getUpperBoundAsInstant()` values if a single unqualified value is passed in. For example, if a query containing `&birthdate=2012-10-01` is received, previously these two methods would both return the same value, but with this fix `getUpperBoundAsInstant()` now returns the instant at 23:59:59.9999.  
|  |  Resource fields with a type of "*" (or Any) sometimes failed to parse if a value type of "code" was used. Thanks to Bill de Beaubien for reporting!  
|  [#50](https://github.com/hapifhir/hapi-fhir/issues/50) |  Primitive datatypes now preserve their original string value when parsing resources, as well as containing the "parsed value". For instance, a DecimalDt field value of `1.0000` will be parsed into the corresponding decimal value, but will also retain the original value with the corresponding level of precision. This allows vadliator rules to be applied to original values as received "over the wire", such as well formatted but invalid dates, e.g. "2001-15-01". Thanks to Joe Athman for reporting and helping to come up with a fix!  
|  [#52](https://github.com/hapifhir/hapi-fhir/issues/52) |  JPA module (and public HAPI-FHIR test server) were unable to process resource types where at least one search parameter has no path specified. These now correctly save (although the server does not yet process these params, and it should). Thanks to GitHub user shvoidlee for reporting and help with analysis!  
|  |  Generic/Fluent Client "create" and "update" method requests were not setting a content type header  
|  |  RESTful server now doesn't overwrite resource IDs if they are absolute. In other words, if a server's Resource Provider returns a resource with ID "Patient/123" it will be translated to "[base url]/Patient/123" but if the RP returns ID "http://foo/Patient/123" the ID will be returned exactly as is. Thanks to Bill de Beaubien for the suggestion!  
|  [#55](https://github.com/hapifhir/hapi-fhir/issues/55) |  JPA module Transaction operation was not correctly replacing logical IDs beginning with "cid:" with server assigned IDs, as required by the specification.  
|  |  FhirTerser did not visit or find children in contained resources when searching a resource. This caused server implementations to not always return contained resources when they are included with a resource being returned.  
|  |  Do not strip version from resource references in resources returned from server search methods. Thanks to Bill de Beaubien for reporting!  
|  [#54](https://github.com/hapifhir/hapi-fhir/issues/54) |  Correct an issue with the validator where changes to the underlying OperationOutcome produced by a validation cycle cause the validation results to be incorrect.  
|  |  Client interceptors registered to an interface based client instance were applied to other client instances for the same client interface as well. (Issue did not affect generic/fluent clients)  
|  [#57](https://github.com/hapifhir/hapi-fhir/issues/57) |  DateDt, DateTimeDt and types InstantDt types now do not throw an exception if they are used to parse a value with the wrong level of precision for the given type but do throw an exception if the wrong level of precision is passed into their constructors.   
  
This means that HAPI FHIR can now successfully parse resources from external sources that have the wrong level of precision, but will generate a validation error if the resource is validated. Thanks to Alexander Kley for the suggestion!  
|  |  Encoding a Binary resource without a content type set should not result in a NullPointerException. Thanks to Alexander Kley for reporting!  
|  [#60](https://github.com/hapifhir/hapi-fhir/issues/60) |  Client requests which include a resource/bundle body (e.g. create, update, transaction) were not including a charset in the content type header, leading to servers incorrectly assuming ISO-8859/1. Thanks to shvoidlee for reporting!  
|  [#59](https://github.com/hapifhir/hapi-fhir/issues/59) |  Clean up the way that Profile resources are automatically exported by the server for custom resource profile classes. See the @ResourceDef JavaDoc for information on how this works.  
#  0.12.4HAPI FHIR 0.7
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#change0.8-0)
##  0.12.4.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-1)
**Released:** 2014-10-23
##  0.12.4.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-1)
|  [#30](https://github.com/hapifhir/hapi-fhir/issues/30) |  **API CHANGE:** The TagList class previously implemented ArrayList semantics, but this has been replaced with LinkedHashMap semantics. This means that the list of tags will no longer accept duplicate tags, but that tag order will still be preserved. Thanks to Bill de Beaubien for reporting!  
---|---|---  
|  |  Add a new client interceptor which adds HTTP Authorization Bearer Tokens (for use with OAUTH2 servers) to client requests.  
|  |  HAPI now logs a single line indicating the StAX implementation being used upon the first time an XML parser is created.  
|  |  Documentation fixes  
|  |  Documentation on contained resources contained a typo and did not actually produce contained resources. Thanks to David Hay of Orion Health for reporting!  
|  [#31](https://github.com/hapifhir/hapi-fhir/issues/31) |  Add a [Vagrant](https://www.vagrantup.com/) based environment (basically a fully built, self contained development environment) for trying out the HAPI server modules. Thanks to Preston Lee for the pull request, and for offering to maintain this!  
|  [#32](https://github.com/hapifhir/hapi-fhir/issues/32) |  Change validation API so that it uses a return type instead of exceptions to communicate validation failures. Thanks to Joe Athman for the pull request!  
|  [#35](https://github.com/hapifhir/hapi-fhir/issues/35) |  Add a client interceptor which adds an HTTP cookie to each client request. Thanks to Petro Mykhailysyn for the pull request!  
|  |  Add a collection of new methods on the generic client which support the **read** and **search** operations using an absolute URL. This allows developers to perform these operations using URLs they obtained from other sources (or external resource references within resources). In addition, the existing read/vread operations will now access absolute URL references if they are passed in. Thanks to Doug Martin of the Regenstrief Center for Biomedical Informatics for contributing this implementation!  
|  [#33](https://github.com/hapifhir/hapi-fhir/issues/33) |  Server was incorrectly including contained resources being returned as both contained resources, and as top-level resources in the returned bundle for search operations. Thanks to Bill de Beaubien for reporting! This also fixes Issue #20, thanks to lephty for reporting!  
|  |  Resources containing entities which are not valid in basic XML (e.g. §) will have those entities converted to their equivalent unicode characters when resources are encoded, since FHIR does not allow extended entities in resource instances.  
|  |  Add phloc-commons dependency explicitly, which resolves an issue building HAPI from source on some platforms. Thanks to Odysseas Pentakalos for the patch!  
|  |  Update methods on the server did not return a "content-location" header, but only a "location" header. Both are required according to the FHIR specification. Thanks to Bill de Beaubien of Systems Made Simple for reporting this!  
|  [#26](https://github.com/hapifhir/hapi-fhir/issues/26) |  Parser failed to correctly read contained Binary resources. Thanks to Alexander Kley for the patch!  
|  [#29](https://github.com/hapifhir/hapi-fhir/issues/29) |  Calling encode multiple times on a resource with contained resources caused the contained resources to be re-added (and the actual message to grow) with each encode pass. Thanks to Alexander Kley for the test case!  
|  |  JSON-encoded contained resources with the incorrect "_id" element (which should be "id", but some incorrect examples exist on the FHIR specification) now parse correctly. In other words, HAPI previously only accepted the correct "id" element, but now it also accepts the incorrect "_id" element just to be more lenient.  
|  |  Several unit tests failed on Windows (or any platform with non UTF-8 default encoding). This may have also caused resource validation to fail occasionally on these platforms as well. Thanks to Bill de Beaubien for reporting!  
|  |  toString() method on TokenParam was incorrectly showing the system as the value. Thanks to Bill de Beaubien for reporting!  
|  |  Server implementation was not correctly figuring out its own FHIR Base URL when deployed on Amazon Web Service server. Thanks to Jeffrey Ting and Bill De Beaubien of Systems Made Simple for their help in figuring out this issue!  
|  |  XML Parser failed to encode fields with both a resource reference child and a primitive type child. Thanks to Jeffrey Ting and Bill De Beaubien of Systems Made Simple for their help in figuring out this issue!  
|  |  HAPI now runs successfully on Servlet 2.5 containers (such as Tomcat 6). Thanks to Bernard Gitaadji for reporting and diagnosing the issue!  
|  |  Summary (in the bundle entry) is now encoded by the XML and JSON parsers if supplied. Thanks to David Hay of Orion Health for reporting this!  
|  [#24](https://github.com/hapifhir/hapi-fhir/issues/24) |  Conformance profiles which are automatically generated by the server were missing a few mandatory elements, which meant that the profile did not correctly validate. Thanks to Bill de Beaubien of Systems Made Simple for reporting this!  
|  |  XHTML (in narratives) containing escapable characters (e.g. < or ") will now always have those characters escaped properly in encoded messages.  
#  0.12.5HAPI FHIR 0.6
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#change0.7-0)
##  0.12.5.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-2)
**Released:** 2014-09-08
##  0.12.5.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-2)
|  |  Add server interceptor framework, and new interceptor for logging incoming requests.  
---|---|---  
|  |  Add server validation framework for validating resources against the FHIR schemas and schematrons  
|  |  Transaction server method is now allowed to return an OperationOutcome in addition to the incoming resources. The public test server now does this in order to return status information about the transaction processing.  
|  |  Update method in the server can now flag (via a field on the MethodOutcome object being returned) that the result was actually a creation, and Create method can indicate that it was actually an update. This has no effect other than to switch between the HTTP 200 and HTTP 201 status codes on the response, but this may be useful in some circumstances.  
|  |  Added narrative generator template for OperationOutcome resource  
|  |  Transaction method in server can now have parameter type Bundle instead of `List<IBaseResource>`  
|  |  HAPI parsers now use field access to get/set values instead of method accessors and mutators. This should give a small performance boost.  
|  |  SecurityEvent.Object structural element has been renamed to SecurityEvent.ObjectElement to avoid conflicting names with the java Object class. Thanks to Laurie Macdougall-Sookraj of UHN for reporting!  
|  |  SecurityEvent resource's enums now use friendly enum names instead of the unfriendly numeric code values. Thanks to Laurie MacDougall-Sookraj of UHN for the suggestion!  
|  |  Contained/included resource instances received by a client are now automatically added to any ResourceReferenceDt instancea in other resources which reference them.  
|  |  Add documentation on how to use eBay CORS Filter to support Cross Origin Resource Sharing (CORS) to server. CORS support that was built in to the server itself has been removed, as it did not work correctly (and was reinventing a wheel that others have done a great job inventing). Thanks to Peter Bernhardt of Relay Health for all the assistance in testing this!  
|  |  Annotation client search methods with a specific resource type (e.g. `List<Patient>` search()) won't return any resources that aren't of the correct type that are received in a response bundle (generally these are referenced resources, so they are populated in the reference fields instead). Thanks to Tahura Chaudhry of University Health Network for the unit test!  
|  |  Date/time types did not correctly parse values in the format "yyyymmdd" (although the FHIR-defined format is "yyyy-mm-dd" anyhow, and this is correctly handled). Thanks to Jeffrey Ting of Systems Made Simple for reporting!  
|  |  Server search method for an unnamed query gets called if the client requests a named query with the same parameter list. Thanks to Neal Acharya of University Health Network for reporting!  
|  |  Category header (for tags) is correctly read in client for "read" operation  
|  |  JSON parser encodes resource references incorrectly, using the name "resource" instead of the name "reference" for the actual reference. Thanks to Ricky Nguyen for reporting and tracking down the issue!  
|  |  Tester UI created double _format and _pretty param entries in searches. Thanks to Gered King of University Health Network for reporting!  
|  |  Rename NotImpementedException to NotImplementedException (to correct typo)  
|  |  Server setUseBrowserFriendlyContentType setting also respected for errors (e.g. OperationOutcome with 4xx/5xx status)  
|  |  Fix performance issue in date/time datatypes where pattern matchers were not static  
|  |  Server now gives a more helpful error message if a @Read method has a search parameter (which is invalid, but previously lead to a very unhelpful error message). Thanks to Tahura Chaudhry of UHN for reporting!  
|  |  Resource of type "List" failed to parse from a bundle correctly. Thanks to David Hay of Orion Health for reporting!  
|  |  QuantityParam correctly encodes approximate (~) prefix to values  
|  [#14](https://github.com/hapifhir/hapi-fhir/issues/14) |  If a server defines a method with parameter "_id", incoming search requests for that method may get delegated to the wrong method. Thanks to Neal Acharya for reporting!  
|  |  Text/narrative blocks that were created with a non-empty namespace prefix (e.g. <xhtml:div xmlns:xhtml="...">...</xhtml:div>) failed to encode correctly (prefix was missing in encoded resource)  
|  |  Resource references previously encoded their children (display and reference) in the wrong order so references with both would fail schema validation.  
|  [#4](https://github.com/hapifhir/hapi-fhir/issues/4) |  Create method was incorrectly returning an HTTP 204 on sucessful completion, but should be returning an HTTP 200 per the FHIR specification. Thanks to wanghaisheng for reporting!  
|  |  FHIR Tester UI now correctly sends UTF-8 charset in responses so that message payloads containing non US-ASCII characters will correctly display in the browser  
|  |  JSON parser was incorrectly encoding extensions on composite elements outside the element itself (as is done correctly for non-composite elements) instead of inside of them. Thanks to David Hay of Orion for reporting this!  
|  |  IResource interface did not expose the getLanguage/setLanguage methods from BaseResource, so the resource language was difficult to access.  
|  |  JSON Parser now gives a more friendly error message if it tries to parse JSON with invalid use of single quotes  
#  0.12.6HAPI FHIR 0.5
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#change0.6-0)
##  0.12.6.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-3)
**Released:** 2014-07-30
##  0.12.6.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-3)
|  |  HAPI has a number of RESTful method parameter types that have similar but not identical purposes and confusing names. A cleanup has been undertaken to clean this up. This means that a number of existing classes have been deprocated in favour of new naming schemes.   
  
All annotation-based clients and all server search method parameters are now named (type)Param, for example: StringParam, TokenParam, etc.   
  
All generic/fluent client method parameters are now named (type)ClientParam, for example: StringClientParam, TokenClientParam, etc.   
  
All renamed classes have been retained and deprocated, so this change should not cause any issues for existing applications but those applications should be refactored to use the new parameters when possible.  
---|---|---  
|  |  Allow server methods to return wildcard generic types (e.g. List<? extends IResource>)  
|  [#2](https://github.com/hapifhir/hapi-fhir/issues/2) |  Read invocations in the client now process the "Content-Location" header and use it to populate the ID of the returned resource. Thanks to Neal Acharya for the suggestion!  
|  |  Binary reads on a server not include the Content-Disposition header, to prevent HTML in binary blobs from being used for nefarious purposes. See [FHIR Tracker Bug 3298](http://gforge.hl7.org/gf/project/fhir/tracker/?action=TrackerItemEdit&tracker_id=677&tracker_item_id=3298) for more information.  
|  |  Support has been added for using an HTTP proxy for outgoing requests.  
|  |  Search parameters are not properly escaped and unescaped. E.g. for a token parameter such as "&identifier=system|codepart1|codepart2"  
|  |  Add support for OPTIONS verb (which returns the server conformance statement)  
|  |  Add support for CORS headers in server  
|  |  Bump SLF4j dependency to latest version (1.7.7)  
|  |  Add interceptor framework for clients (annotation based and generic), and add interceptors for configurable logging, capturing requests and responses, and HTTP basic auth.  
|  |  Bundle entries now support a link type of "search". Thanks to David Hay for the suggestion!  
|  [#1](https://github.com/hapifhir/hapi-fhir/issues/1) |  If a client receives a non 2xx response (e.g. HTTP 500) and the response body is a text/plain message or an OperationOutcome resource, include the message in the exception message so that it will be more conveniently displayed in logs and other places. Thanks to Neal Acharya for the suggestion!  
|  [#3](https://github.com/hapifhir/hapi-fhir/issues/3) |  Fix issue where vread invocations on server incorrectly get routed to instance history method if one is defined. Thanks to Neal Acharya from UHN for surfacing this one!  
|  |  Fix: Primitive extensions declared against custom resource types are encoded even if they have no value. Thanks to David Hay of Orion for reporting this!  
|  |  Fix: RESTful server deployed to a location where the URL to access it contained a space (e.g. a WAR file with a space in the name) failed to work correctly. Thanks to David Hay of Orion for reporting this!  
|  |  Transaction client invocations with XML encoding were using the wrong content type ("application/xml+fhir" instead of the correct "application/atom+xml"). Thanks to David Hay of Orion Health for surfacing this one!  
#  0.12.7HAPI FHIR 0.4
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#change0.5-0)
##  0.12.7.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-4)
**Released:** 2014-07-13
##  0.12.7.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-4)
|  |  **BREAKING CHANGE:** : IdDt has been modified so that it contains a partial or complete resource identity. Previously it contained only the simple alphanumeric id of the resource (the part at the end of the "read" URL for that resource) but it can now contain a complete URL or even a partial URL (e.g. "Patient/123") and can optionally contain a version (e.g. "Patient/123/_history/456"). New methods have been added to this datatype which provide just the numeric portion. See the JavaDoc for more information.  
---|---|---  
|  |  **API CHANGE:** : Most elements in the HAPI FHIR model contain a getId() and setId() method. This method is confusing because it is only actually used for IDREF elements (which are rare) but its name makes it easy to confuse with more important identifiers. For this reason, these methods have been deprecated and replaced with get/setElementSpecificId() methods. The old methods will be removed at some point. Resource types are unchanged and retain their get/setId methods.  
|  |  Add support for paging responses from RESTful servers.  
|  |  Support added for deleted-entry by/name, by/email, and comment from Tombstones spec  
|  |  Allow use of QuantityDt as a service parameter to support the "quantity" type. Previously QuantityDt did not implement IQueryParameterType so it was not valid, and there was no way to support quantity search parameters on the server (e.g. Observation.value-quantity)  
|  |  Introduce StringParameter type which can be used as a RESTful operation search parameter type. StringParameter allows ":exact" matches to be specified in clients, and handled in servers.  
|  |  Parsers (XML/JSON) now support deleted entries in bundles  
|  |  Transaction method now supported in servers  
|  |  Support for Binary resources added (in servers, clients, parsers, etc.)  
|  |  Client requests for IdentifierDt types (such as Patient.identifier) did not create the correct query string if the system is null.  
|  |  Don't fail on narrative blocks in JSON resources with only an XML declaration but no content (these are produced by the Health Intersections server)  
|  |  Server now automatically compresses responses if the client indicates support  
|  |  Server failed to support optional parameters when type is String and :exact qualifier is used  
|  |  Read method in client correctly populated resource ID in returned object  
|  |  Support for Query resources fixed (in parser)  
|  |  Nested contained resources (e.g. encoding a resource with a contained resource that itself contains a resource) now parse and encode correctly, meaning that all contained resources are placed in the "contained" element of the root resource, and the parser looks in the root resource for all container levels when stitching contained resources back together.  
|  |  Server methods with @Include parameter would sometimes fail when no _include was actually specified in query strings.  
#  0.12.8HAPI FHIR 0.3
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#change0.4-0)
##  0.12.8.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-5)
**Released:** 2014-05-12
##  0.12.8.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-5)
#  0.12.9HAPI FHIR 0.2
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#hapi-fhir-02)
##  0.12.9.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-6)
**Released:** 2014-04-23
##  0.12.9.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-6)
#  0.12.10HAPI FHIR 0.1
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#hapi-fhir-01)
##  0.12.10.1Release Information[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#release-information-7)
**Released:** 2014-04-15
##  0.12.10.2Changes[](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2014.html#changes-7)
[ ](https://hapifhir.io/hapi-fhir/docs/introduction/changelog_2015.html)
0.12 Changelog: 2014 
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
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)