---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html
crawled: 2025-08-01T14:05:08.863300
---

# Server Jpa Introduction

#  5.0.1JPA Server Introduction
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html#jpa-server-introduction)
The HAPI FHIR [Plain Server](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html) module can be used to create a FHIR server endpoint against an arbitrary data source, which could be a database of your own design, an existing clinical system, a set of files, or anything else you come up with.
HAPI also provides a persistence module which can be used to provide a complete RESTful server implementation, backed by a database of your choosing. This module uses the [JPA 2.0](http://en.wikipedia.org/wiki/Java_Persistence_API) API to store data in a database without depending on any specific database technology.
5.0 Introduction 
* JPA Server 
* [ 5.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html)
* [ 5.1  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html)
* [ 5.2  Architecture ](https://hapifhir.io/hapi-fhir/docs/server_jpa/architecture.html)
* [ 5.3  Database Support ](https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html)
* [ 5.4  Database Schema ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html)
* [ 5.5  Configuration ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html)
* [ 5.6  Search ](https://hapifhir.io/hapi-fhir/docs/server_jpa/search.html)
* [ 5.7  Performance ](https://hapifhir.io/hapi-fhir/docs/server_jpa/performance.html)
* [ 5.8  Upgrade Guide ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html)
* [ 5.9  Diff Operation ](https://hapifhir.io/hapi-fhir/docs/server_jpa/diff.html)
* [ 5.10  LastN Operation ](https://hapifhir.io/hapi-fhir/docs/server_jpa/lastn.html)
* [ 5.11  Lucene/Elasticsearch Indexing ](https://hapifhir.io/hapi-fhir/docs/server_jpa/elastic.html)
* [ 5.12  Terminology ](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html)
* [ 5.13  International Patient Summary (IPS) ](https://hapifhir.io/hapi-fhir/docs/server_jpa/ips.html)
[ 5.1 Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)