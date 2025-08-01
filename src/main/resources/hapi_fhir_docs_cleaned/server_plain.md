---
source: https://hapifhir.io/hapi-fhir/docs/server_plain
crawled: 2025-08-01T14:09:26.537004
---

# Server Plain

#  4.0.1HAPI FHIR Server Introduction
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html#hapi-fhir-server-introduction)
HAPI FHIR provides several mechanisms for building FHIR servers. The appropriate choice depends on the specifics of what you are trying to accomplish.
##  4.0.1.1Plain Server / Facade[](https://hapifhir.io/hapi-fhir/docs/server_plain#plain-server-facade)
The HAPI FHIR Plain Server (often referred to as a Facade) is an implementation of a FHIR server against an arbitrary backend that you provide.
In this mode, you write code that handles resource storage and retrieval logic, and HAPI FHIR takes care of:
  * HTTP Processing
  * Parsing / Serialization
  * FHIR REST semantics


This module was originally created at [University Health Network](https://uhn.ca) (UHN) as a mechanism for placing a common FHIR layer on top of a series of existing data sources, including an EMR, an enterprise patient scheduling system, and a series of clinical data repositories. All of these systems existed long before FHIR was adopted at UHN and HAPI FHIR was created to make the process of adopting FHIR easier.
This module has been used by many organizations to successfully create FHIR servers in a variety of use cases, including:
  * **Hospitals:** Adding a FHIR data access layer to Existing Enterprise Data Warehouses and Clinical Data Repositories
  * **Vendors:** Integration into existing products in order to add FHIR capabilities
  * **Researchers:** Aggregate data collection and reporting platforms


To get started with the Plain Server, jump to [Plain Server Introduction](https://hapifhir.io/hapi-fhir/docs/introduction.html).
##  4.0.1.2JPA Server[](https://hapifhir.io/hapi-fhir/docs/server_plain#jpa-server)
The HAPI FHIR JPA Server is a complete implementation of a FHIR server against a relational database. Unlike the Plain Server, the JPA server provides its own database schema and handles all storage and retrieval logic without any coding being required.
The JPA server has been successfully used in many use cases, including:
  * **App Developers:** The JPA server has been used as a backend data storage layer for various mobile and web-based apps. The ease of development combined with the power of the FHIR specification makes developing clinical apps a very enjoyable experience.
  * **Government/Enterprise:** Many large architectures, including enterprise messaging systems, regional data repositories, telehealth solutions, etc. have been created using HAPI FHIR JPA server as a backend. These systems often scale to handle millions of patients and beyond.


To get started with the JPA Server, jump to [JPA Server Introduction](https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html).
##  4.0.1.3JAX-RS Server[](https://hapifhir.io/hapi-fhir/docs/server_plain#jax-rs-server)
For users in an environment where existing services using JAX-RS have been created, it is often desirable to use JAX-RS for FHIR servers as well. HAPI FHIR provides a JAX-RS FHIR server implementation for this purpose.
To get started with the JAX-RS FHIR Server, jump to [JAX-RS FHIR Server Introduction](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html).
4.0 REST Server Types 
* Plain Server 
* [ 4.0  REST Server Types ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html)
* [ 4.1  Plain Server Introduction ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html)
* [ 4.2  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
* [ 4.3  Resource Providers and Plain Providers ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html)
* [ 4.4  REST Operations: Overview ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
* [ 4.5  REST Operations: Search ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
* [ 4.6  REST Operations: Extended Operations ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
* [ 4.7  Paging Search Results ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
* [ 4.8  Web Testpage Overlay ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
* [ 4.9  Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
* [ 4.10  JAX-RS Support ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
* [ 4.11  Customizing the CapabilityStatement ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html)
* [ 4.12  OpenAPI / Swagger ](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html)
[ 4.1 Plain Server Introduction ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)