---
source: https://hapifhir.io/hapi-fhir/docs/android/client.html
crawled: 2025-08-01T14:05:24.037632
---

# Android Client

#  14.0.1Android Client
[ ](https://hapifhir.io/hapi-fhir/docs/android/client.html#android-client)
HAPI now has a specially built module for use on Android. Android developers may use this JAR to take advantage of the FHIR model classes, and the FHIR client (running a FHIR server on Android is not yet supported. Get in touch if this is something you are interested in working on!)
As of HAPI FHIR 3.1.0, the `hapi-fhir-android` module has been streamlined in order to reduce its footprint. Previous versions of the library included both an XML and a JSON parser but this has been streamlined to only include JSON support in order to reduce the number of libraries required in an Android build.
When using the HAPI FHIR Android client, the client will request only JSON responses (via the HTTP `Accept` header) and will not be able to communicate with FHIR servers that support only XML encoding (few, if any, servers actually exist with this limitation that we are aware of).
The Android client also uses the `hapi-fhir-client-okhttp` module, which is an HTTP client based on the OkHttp library. This library has proven to be more powerful and less likely to cause issues on Android than the Apache HttpClient implementation which is bundled by default.
Note that the Android JAR is still new and hasn't received as much testing as other parts of the library. We would greatly appreciate feedback, testing, etc. Also note that because mobile apps run on less powerful hardware compared to desktop and server applications, it is all the more important to keep a single instance of the `FhirContext` around for good performance, since this object is expensive to create. We are hoping to improve performance of the creation of this object in a future release. If you are an Android developer and would like to help with this, please get in touch!
##  14.0.1.1Get the Android JAR[](https://hapifhir.io/hapi-fhir/docs/android/client.html#get-the-android-jar)
To add the HAPI library via Gradle, you should add the [hapi-fhir-android](https://search.maven.org/search?q=g:ca.uhn.hapi.fhir%20AND%20a:hapi-fhir-android&core=gav) library to your Gradle file, as well as a structures library for the appropriate version of FHIR that you want to support, e.g. [hapi-fhir-structures-r4](https://search.maven.org/search?q=g:ca.uhn.hapi.fhir%20AND%20a:hapi-fhir-structures-r4&core=gav).
```
dependencies {
    compile "ca.uhn.hapi.fhir:hapi-fhir-android:3.1.0-SNAPSHOT"
    compile "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2:3.1.0-SNAPSHOT"
}

```

Copy
You will also need to manually exclude the Woodstox StAX library from inclusion, as this library uses namespaces which are prohibited on Android. You should also exclude:
```
configurations {
    all*.exclude group: 'org.codehaus.woodstox'
    all*.exclude group: 'org.apache.httpcomponents'
}

```

Copy
To see a sample Gradle file for a working Android project using HAPI FHIR, see the [Android Integration Test](https://github.com/hapifhir/hapi-fhir-android-integration-test) project.
#  14.0.2Performance
[ ](https://hapifhir.io/hapi-fhir/docs/android/client.html#performance)
On mobile devices, performance problems are particularly noticeable. This is made worse by the fact that some economy Android devices have much slower performance than modern desktop computers. See the [Client Configuration Performance](https://hapifhir.io/hapi-fhir/docs/client/client_configuration.html#performance) page for some tips on how to improve client performance.
#  14.0.3Examples
[ ](https://hapifhir.io/hapi-fhir/docs/android/client.html#examples)
The following is intended to be a selection of publicly available open source Android applications which use HAPI FHIR and might be useful as a reference.
If you know of others, please let us know!
  * <https://github.com/hapifhir/hapi-fhir-android-integration-test> - hapi-fhir-android Integration Test and Reference Application is our test platform for validating new releases. Created by Thomas Andersen.
  * <https://github.com/SynappzMA/FHIR-Android> - Nice FHIR DSTU2 search app


14.0 Android Client 
* Android 
* [ 14.0  Android Client ](https://hapifhir.io/hapi-fhir/docs/android/client.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)