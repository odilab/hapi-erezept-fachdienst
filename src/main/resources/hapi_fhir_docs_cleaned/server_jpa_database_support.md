---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html
crawled: 2025-08-01T14:09:22.208174
---

# Server Jpa Database Support

#  5.3.1Database Support
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html#database-support)
HAPI FHIR JPA Server maintains active support for several databases.
The supported databases are regularly tested for ongoing compliance and performance, and HAPI FHIR has specific performance optimizations for each platform. Make sure to use the HAPI FHIR dialect class as opposed to the default hibernate dialect class.
Database | Status | Hibernate Dialect Class | Notes  
---|---|---|---  
[MS SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-2019) | **Supported** | `ca.uhn.fhir.jpa.model.dialect.HapiFhirH2Dialect` |   
[PostgreSQL](https://www.postgresql.org/) | **Supported** | `ca.uhn.fhir.jpa.model.dialect.HapiFhirPostgresDialect` |   
[Oracle](https://www.oracle.com/ca-en/database/12c-database/) | **Supported** | `ca.uhn.fhir.jpa.model.dialect.HapiFhirOracleDialect` |   
[Cockroach DB](https://www.cockroachlabs.com/) | Experimental | `ca.uhn.fhir.jpa.model.dialect.HapiFhirCockroachDialect` | A CockroachDB dialect was contributed by a HAPI FHIR community member. This dialect is not regularly tested, use with caution.  
MySQL | Deprecated | `ca.uhn.fhir.jpa.model.dialect.HapiFhirMySQLDialect` | MySQL and MariaDB exhibit poor performance with HAPI FHIR and have therefore been deprecated. These databases should not be used.  
MariaDB | Deprecated | `ca.uhn.fhir.jpa.model.dialect.HapiFhirMariaDBDialect` | MySQL and MariaDB exhibit poor performance with HAPI FHIR and have therefore been deprecated. These databases should not be used.  
#  5.3.2Experimental Support
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html#experimental-support)
HAPI FHIR uses the Hibernate ORM to provide database abstraction. This means that HAPI FHIR could theoretically also work on other databases supported by Hibernate. For example, although we do not regularly test or validate on other platforms, community members have reported successfully running HAPI FHIR on:
  * DB2
  * Cache
  * Firebird


[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/architecture.html)
5.3 Database Support 
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
[ 5.4 Database Schema ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)