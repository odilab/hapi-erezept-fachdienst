---
source: https://hapifhir.io/hapi-fhir/docs/getting_started/modules.html
crawled: 2025-08-01T14:06:56.270732
---

# Getting Started Modules

#  1.2.1HAPI FHIR Modules
[ ](https://hapifhir.io/hapi-fhir/docs/getting_started/modules.html#hapi-fhir-modules)
The following table shows the modules that make up the HAPI FHIR library.
Core Libraries   
---  
hapi-fhir-base |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/) [Sources ›](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-base) |  This is the core HAPI FHIR library and is always required in order to use the framework. It contains the context, parsers, and other support classes.   
Structures   
hapi-fhir-structures-dstu2 |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-dstu2/) |  This module contains FHIR DSTU2 model classes.   
hapi-fhir-structures-hl7org-dstu2 |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-hl7org-dstu2/) |  This module contains alternate FHIR DSTU2 model classes. The HAPI FHIR and FHIR "Java Reference Implementation" libraries were merged in 2015, and at the time there were two parallel sets of DSTU2 model classes. This set more closely resembles the model classes for DSTU3+ where the other set more closely resembles the original DSTU1 model classes. The two DSTU2 model JARs are functionally identital, but the various utility methods on the classes are somewhat different.   
hapi-fhir-structures-dstu3 |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-dstu3/) |  This module contains FHIR DSTU3 model classes.   
hapi-fhir-structures-r4 |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/) |  This module contains FHIR R4 model classes.   
hapi-fhir-structures-r5 |  [JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r5/) |  This module contains FHIR R5 model classes.   
hapi-fhir-converter |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/model/converter.html)  
[JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-converter/) |  This module contains converters for converting between FHIR versions.   
_hapi-fhir-structures-dstu (retired)_ |  |  This module contained FHIR DSTU1 model classes. It was retired in HAPI FHIR 3.0.0.   
Client Framework   
hapi-fhir-client |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/client/)  
[JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-client/) [Sources ›](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-client) |  This module contains the core FHIR client framework, including an HTTP implementation based on [Apache HttpClient](https://hc.apache.org/). It is required in order to use client functionality in HAPI.   
hapi-fhir-client-okhttp |  [Sources ›](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-client-okhttp) |  This module contains an alternate HTTP implementation based on [OKHTTP](http://square.github.io/okhttp/).   
hapi-fhir-android |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/android/) [Sources ›](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-android/) |  This module contains the Android HAPI FHIR framework, which is a FHIR client framework which has been tailored specifically to run on Android.   
Validation   
hapi-fhir-validation |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/validation/) |  This module contains the FHIR Profile Validator, which is used to validate resource instances against FHIR Profiles (StructureDefinitions, ValueSets, CodeSystems, etc.).   
hapi-fhir-validation-resources-dstu2 |  |  This module contains the StructureDefinitions, ValueSets, CodeSystems, Schemas, and Schematrons for FHIR DSTU2   
hapi-fhir-validation-resources-dstu2.1 |  |  This module contains the StructureDefinitions, ValueSets, CodeSystems, Schemas, and Schematrons for FHIR DSTU2.1   
hapi-fhir-validation-resources-dstu3 |  |  This module contains the StructureDefinitions, ValueSets, CodeSystems, Schemas, and Schematrons for FHIR DSTU3   
hapi-fhir-validation-resources-r4 |  |  This module contains the StructureDefinitions, ValueSets, CodeSystems, Schemas, and Schematrons for FHIR R4   
hapi-fhir-validation-resources-r5 |  |  This module contains the StructureDefinitions, ValueSets, CodeSystems for R5. As of FHIR R5, Schema and Schematron files are no longer distributed with HAPI FHIR.   
Server  
hapi-fhir-server |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/server_plain/)  
[JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-server/) [Sources ›](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-server/) |  This module contains the HAPI FHIR Server framework, which can be used to develop FHIR compliant servers against your own data storage layer.   
hapi-fhir-jpaserver-base |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/server_jpa/)  
[JavaDoc ›](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-base/) [Sources ›](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-jpaserver-base/) |  This module contains the HAPI FHIR "JPA Server", which is a complete FHIR server solution including a database and implementations of many advanced FHIR server features.   
hapi-fhir-testpage-overlay |  [Documentation ›](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)  
[Sources ›](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-testpage-overlay/) |  This module contains the web based "testpage overlay", which is the UI that powers our [Public Demo Server](http://fhirtest.uhn.ca) and can also be added to your applications.   
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)