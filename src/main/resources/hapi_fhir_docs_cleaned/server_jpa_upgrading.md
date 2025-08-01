---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html
crawled: 2025-08-01T14:05:09.950008
---

# Server Jpa Upgrading

#  5.8.1HAPI FHIR JPA Upgrade Guide
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#hapi-fhir-jpa-upgrade-guide)
HAPI FHIR JPA is a constantly evolving product, with new features being added to each new version of the library. As a result, it is generally necessary to execute a database migration as a part of an upgrade to HAPI FHIR.
When upgrading the JPA server from one version of HAPI FHIR to a newer version, often there will be changes to the database schema. The **Migrate Database** command can be used to perform a migration from one version to the next.
Note that this feature was added in HAPI FHIR 3.5.0. It is not able to migrate from versions prior to HAPI FHIR 3.4.0. **Please make a backup of your database before running this command!**
The following example shows how to use the migrator utility to migrate to the latest version.
```
./hapi-fhir-cli migrate-database -d H2_EMBEDDED -u "jdbc:h2:directory:target/jpaserver_h2_files;create=true" -n "" -p ""

```

Copy
You may use the following command to get detailed help on the options:
```
./hapi-fhir-cli help migrate-database

```

Copy
Note the arguments:
  * `-d [dialect]` – This indicates the database dialect to use. See the detailed help for a list of options.
  * `--enable-heavyweight-migrations` – If this flag is set, additional migration tasks will be executed that are considered unnecessary to execute on a database with a significant amount of data loaded. This option is not generally necessary.


#  5.8.2Database Partition Mode
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#database-partition-mode)
If you are using [Database Partition Mode](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/db_partition_mode.html), add the flag `--flags database-partition-mode` to your command.
With this flag added to the command line, the migrator will initialize an empty database with a schema suitable for Database Partition Mode, and will appropriately migrate an existing schema that was initialized with Database Partition Mode. This flag should not be used when migrating an existing schema that was not initially created in Database Partition Mode. It can not be used to convert an existing schema into one that is suitable for this new mode.
For example:
```
./hapi-fhir-cli migrate-database --flags database-partition-mode -d POSTGRES_9_4 -u "[url]" -n "[username]" -p "[password]"

```

Copy
#  5.8.3Oracle Support
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#oracle-support)
Note that the Oracle JDBC drivers are not distributed in the Maven Central repository, so they are not included in HAPI FHIR. In order to use this command with an Oracle database, you will need to invoke the CLI as follows:
```
./hapi-fhir-cli migrate-database -d ORACLE_12C -u "[url]" -n "[username]" -p "[password]"

```

Copy
#  5.8.4Oracle and Sql Server Locking Note
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#oracle-and-sql-server-locking-note)
Some versions of Oracle and Sql Server (e.g. Oracle Standard or Sql Server Standard) do NOT support adding or removing an index without locking the underlying table. If you run migrations while these systems are running, they will have unavoidable long pauses in activity during these changes.
##  5.8.4.1Migrating 3.4.0 to 3.5.0+[](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#migrating-340-to-350)
As of HAPI FHIR 3.5.0 a new mechanism for creating the JPA index tables (HFJ_SPIDX_xxx) has been implemented. This new mechanism uses hashes in place of large multi-column indexes. This improves both lookup times as well as required storage space. This change also paves the way for future ability to provide efficient multi-tenant searches (which is not yet implemented but is planned as an incremental improvement).
This change is not a lightweight change however, as it requires a rebuild of the index tables in order to generate the hashes. This can take a long time on databases that already have a large amount of data.
As a result, in HAPI FHIR JPA 3.6.0, an efficient way of upgrading existing databases was added. Under this new scheme, columns for the hashes are added but values are not calculated initially, database indexes are not modified on the HFJ_SPIDX_xxx tables, and the previous columns are still used for searching as was the case in HAPI FHIR JPA 3.4.0.
In order to perform a migration using this functionality, the following steps should be followed:
  * Stop your running HAPI FHIR JPA instance (and remember to make a backup of your database before proceeding with any changes!)
  * Modify your `JpaStorageSettings` to specify that hash-based searches should not be used, using the following setting: `myStorageSettings.setDisableHashBasedSearches(true);`
  * Make sure that you have your JPA settings configured to not automatically create database indexes and columns using the following setting in your JPA Properties: `extraProperties.put("hibernate.hbm2ddl.auto", "none");`
  * Run the database migrator command, including the entry `-x no-migrate-350-hashes` on the command line. For example:

```
./hapi-fhir-cli migrate-database -d H2_EMBEDDED -u "jdbc:h2:directory:target/jpaserver_h2_files;create=true" -n "" -p "" -x no-migrate-350-hashes

```

  * Rebuild and start your HAPI FHIR JPA server. At this point you should have a working HAPI FHIR JPA 3.6.0 server that is is still using HAPI FHIR 3.4.0 search indexes. Search hashes will be generated for any newly created or updated data but existing data will have null hashes.
  * With the system running, request a complete reindex of the data in the database using an HTTP request such as the following: `POST /$mark-all-resources-for-reindexing`. Note that this is a custom operation built into the HAPI FHIR JPA server. It should be secured in a real deployment, so Authentication is likely required for this call.
  * You can track the reindexing process by watching your server logs, but also by using the following SQL executed directly against your database:


```
SELECT * FROM HFJ_RES_REINDEX_JOB

```

Copy
  * When this query no longer returns any rows, the reindexing process is complete.
  * At this time, HAPI FHIR should be stopped once again in order to convert it to using the hash based indexes.
  * Modify your `JpaStorageSettings` to specify that hash-based searches are used, using the following setting (this is the default setting, so it could also simply be omitted): `myStorageSettings.setDisableHashBasedSearches(false);`
  * Execute the migrator tool again, this time omitting the flag option, e.g.


```
./hapi-fhir-cli migrate-database -d H2_EMBEDDED -u "jdbc:h2:directory:target/jpaserver_h2_files;create=true" -n "" -p ""

```

Copy
  * Rebuild, and start HAPI FHIR JPA again.


#  5.8.5Database Migration
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html#database-migration)
As of version 4.2.0, HAPI FHIR JPA now uses Flyway for schema migrations. The "from" and "to" parameters are no longer used. Flyway maintains a list of completed migrations in a table called `FLY_HFJ_MIGRATION`. When you run the migration command, flyway scans the list of completed migrations in this table and compares them to the list of known migrations, and runs only the new ones.
As of version 6.2.0, HAPI FHIR JPA no longer relies on Flyway for schema migrations. HAPI FHIR still maintains a list of completed migrations in a table called `FLY_HFJ_MIGRATION`. When you run the migration command, HAPI FHIR scans the list of completed migrations in this table and compares them to the list of known migrations and executes only the new ones.
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/performance.html)
5.8 Upgrade Guide 
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
[ 5.9 Diff Operation ](https://hapifhir.io/hapi-fhir/docs/server_jpa/diff.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)