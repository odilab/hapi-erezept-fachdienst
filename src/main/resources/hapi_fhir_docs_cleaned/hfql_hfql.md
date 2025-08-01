---
source: https://hapifhir.io/hapi-fhir/docs/hfql/hfql.html
crawled: 2025-08-01T14:09:23.262234
---

# Hfql Hfql

#  9.0.1HFQL Driver: SQL For FHIR Repositories
[ ](https://hapifhir.io/hapi-fhir/docs/hfql/hfql.html#hfql-driver-sql-for-fhir-repositories)
This is an [experimental module](https://smilecdr.com/docs/introduction/maturity_model.html). Use with caution. This API is likely to change. 
The HAPI FHIR JPA server can optionally be configured to support SQL-like queries against the FHIR repository. This module is intended for analytical queries. It is not optimized for performance, and may take a long time to produce results.
#  9.0.2Syntax
[ ](https://hapifhir.io/hapi-fhir/docs/hfql/hfql.html#syntax)
This module uses a proprietary flavour of SQL that is specific to HAPI FHIR. It is similar to the [Firely Query Language](https://simplifier.net/docs/fql), although it also has differences.
A simple example query is shown below:
```
SELECT
    name[0].family as family, 
    name[0].given[0] as given, 
    birthDate,
    identifier.where(system='http://hl7.org/fhir/sid/us-ssn').value as SSN
FROM
    Patient
WHERE
    active = true

```

Copy
See [SQL Syntax](https://smilecdr.com/docs/hfql/sql_syntax.html) for details on this syntax.
#  9.0.3JDBC Driver
[ ](https://hapifhir.io/hapi-fhir/docs/hfql/hfql.html#jdbc-driver)
When HFQL is enabled on the server, a JDBC-compatible driver is available. This can be used to query the FHIR server directly from a JDBC compliant database browser.
This module has been tested with [DBeaver](https://dbeaver.io/), which is a free and excellent database browser. Other JDBC compatible database tools may also work. Note that not all JDBC API methods have been implemented in the driver, so other tools may use methods that have not yet been implemented. Please let us know in the [Google Group](https://groups.google.com/g/hapi-fhir) if you encounter issues or have suggestions.
The JDBC driver can be downloaded from the [GitHub Releases site](https://github.com/hapifhir/hapi-fhir/releases). It can also be built from sources by executing the following command:
```
mvn -DskipTests -P DIST clean install -pl :hapi-fhir-jpaserver-hfql -am

```

Copy
To import this driver into your database tool, import the JDBC JAR and use the following settings:
Setting | Description  
---|---  
Class Name | ca.uhn.fhir.jpa.fql.jdbc.JdbcDriver  
URL | jdbc:hapifhirql:[server_base_url]  
Username | If provided, the username/password will be added as an HTTP Basic Authorization header on all requests to the server.  
Password  
9.0 HFQL Module 
* JPA Server: HFQL (SQL) Driver 
* [ 9.0  HFQL Module ](https://hapifhir.io/hapi-fhir/docs/hfql/hfql.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)