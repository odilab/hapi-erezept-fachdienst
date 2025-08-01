---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html
crawled: 2025-08-01T14:06:01.808348
---

# Server Jpa Schema

#  5.4.1HAPI FHIR JPA Schema
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hapi-fhir-jpa-schema)
**This page is a work in progress. It is not yet comprehensive.**
It contains a description of the tables within the HAPI FHIR JPA database. Note that columns are shown using Java datatypes as opposed to SQL datatypes, because the exact SQL datatype used will vary depending on the underlying database platform. The schema creation scripts can be used to determine the underlying column types.
#  5.4.2Background: Persistent IDs (PIDs)
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#background-persistent-ids-pids)
The HAPI FHIR JPA schema relies heavily on the concept of internal persistent IDs on tables, using a Java type of Long (8-byte integer, which translates to an _int8_ or _number(19)_ on various database platforms).
Many tables use an internal persistent ID as their primary key, allowing the flexibility for other more complex business identifiers to be changed and minimizing the amount of data consumed by foreign key relationships. These persistent ID columns are generally assigned using a dedicated database sequence on platforms which support sequences.
The persistent ID column is generally called `PID` in the database schema, although there are exceptions.
#  5.4.3HFJ_RESOURCE: Resource Master Table
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) ![Resources](https://hapifhir.io/hapi-fhir/docs/images/jpa_erd_resources.svg)
The HFJ_RESOURCE table indicates a single resource of any type in the database. For example, the resource `Patient/1` will have exactly one row in this table, representing all versions of the resource.
##  5.4.3.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PARTITION_ID |  | Integer | Nullable |  This is the optional partition ID, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
PARTITION_DATE |  | Timestamp | Nullable |  This is the optional partition date, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
RES_VER |  | Long |  |  This is the current version ID of the resource. Will contain `1` when the resource is first created, `2` the first time it is updated, etc. This column is equivalent to the **HFJ_RES_VER.RES_VER** column, although it does not have a foreign-key dependency in order to allow selective expunge of versions when necessary. Not to be confused with **RES_VERSION** below.   
RES_VERSION |  | String |  |  This column contains the FHIR version associated with this resource, using a constant drawn from [FhirVersionEnum](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirVersionEnum.html). Not to be confused with **RES_VER** above.   
RES_TYPE |  | String |  |  Contains the resource type (e.g. `Patient`)   
FHIR_ID |  | String |  |  Contains the FHIR Resource id element. Either the PID, or the client-assigned id.   
HASH_SHA256 |  | Long |  |  This column contains a SHA-256 hash of the current resource contents, exclusive of resource metadata. This is used in order to detect NO-OP writes to the resource.   
RES_PUBLISHED |  | Timestamp |  |  Contains the date that the first version of the resource was created.   
RES_UPDATED |  | Timestamp |  |  Contains the date that the most recent version of the resource was created.   
RES_DELETED_AT |  | Timestamp | Nullable |  If the most recent version of the resource is a delete, this contains the timestamp at which the resource was deleted. Otherwise, contains _NULL_.   
#  5.4.4HFJ_RES_VER: Resource Versions and Contents
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RES_VER)
The HFJ_RES_VER table contains individual versions of a resource. If the resource `Patient/1` has 3 versions, there will be 3 rows in this table.
The complete raw contents of the resource is stored in either the `RES_TEXT` or the `RES_TEXT_VC` column, using the encoding specified in the `RES_ENCODING` column.
##  5.4.4.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-1)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PARTITION_ID |  | Integer | Nullable |  This is the optional partition ID, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
PARTITION_DATE |  | Timestamp | Nullable |  This is the optional partition date, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
PID | PK | Long |  |  This is the row persistent ID.   
RES_ID | FK to [HFJ_RESOURCE](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) | Long |  |  This is the persistent ID of the resource being versioned.   
RES_VER |  | Long |  |  Contains the specific version (starting with 1) of the resource that this row corresponds to.   
RES_ENCODING |  | String |  |  Describes the encoding of the resource being used to store this resource in **RES_TEXT**. See _Encodings_ below for allowable values.   
RES_TEXT |  | byte[] (SQL LOB) |  |  Contains the actual full text of the resource being stored, stored in a binary LOB.   
RES_TEXT_VC |  | String (SQL VARCHAR2) |  |  Contains the actual full text of the resource being stored, stored in a textual VARCHAR2 column. Only one of `RES_TEXT` and `RES_TEXT_VC` will be populated for any given row. The other column in either case will be _null_.   
##  5.4.4.2Encodings[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#encodings)
Value | Description  
---|---  
JSONC |  The resource is serialized using FHIR JSON encoding, and then compressed into a byte stream using GZIP compression.   
##  5.4.4.3Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-2)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PARTITION_ID |  | Integer | Nullable |  This is the optional partition ID, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
PARTITION_DATE |  | Timestamp | Nullable |  This is the optional partition date, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html).   
PID | PK | Long |  |  This is the row persistent ID.   
RESOURCE_PID | FK to [HFJ_RESOURCE](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) | Long |  |  This is the persistent ID of the resource being versioned.   
FORCED_ID |  | String |  |  Contains the specific version (starting with 1) of the resource that this row corresponds to.   
RESOURCE_TYPE |  | String |  Contains the string specifying the type of the resource (Patient, Observation, etc).   
#  5.4.5HFJ_RES_LINK: Search Links
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RES_LINK) ![Resources](https://hapifhir.io/hapi-fhir/docs/images/jpa_erd_resource_links.svg)
When a resource is created or updated, it is indexed for searching. Any search parameters of type [Reference](http://hl7.org/fhir/search.html#reference) are resolved, and one or more rows may be created in the **HFJ_RES_LINK** table.
##  5.4.5.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-3)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PARTITION_ID |  | Integer | Nullable |  This is the optional partition ID, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html). Note that the partition indicated by the **PARTITION_ID** and **PARTITION_DATE** columns refers to the partition of the _SOURCE_ resource, and not necessarily the _TARGET_.   
PARTITION_DATE |  | Timestamp | Nullable |  This is the optional partition date, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html). Note that the partition indicated by the **PARTITION_ID** and **PARTITION_DATE** columns refers to the partition of the _SOURCE_ resource, and not necessarily the _TARGET_.   
PID |  | Long |  |  Holds the persistent ID   
SRC_PATH |  | String |  |  Contains the FHIRPath expression within the source resource containing the path to the target resource, as supplied by the SearchParameter resource that defined the link.   
SRC_RESOURCE_ID |  | Long |  |  Contains a FK reference to the resource containing the link to the target resource.   
TARGET_RESOURCE_ID |  | Long | Nullable |  Contains a FK reference to the resource that is the target resource. Will not be populated if the link contains a reference to an external resource, or a canonical reference.   
TARGET_RESOURCE_URL |  | String | Nullable |  If this row contains a reference to an external resource or a canonical reference, this column will contain the absolute URL.   
SP_UPDATED |  | Timestamp |  |  Contains the last updated timestamp for this row.   
#  5.4.6Background: Search Indexes
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#search-indexes)
The HFJ_SPIDX (Search Parameter Index) tables are used to index resources for searching. When a resource is created or updated, a set of rows in these tables will be added. These are used for finding appropriate rows to return when performing FHIR searches. There are dedicated tables for supporting each of the non-reference [FHIR Search Datatypes](http://hl7.org/fhir/search.html): Date, Number, Quantity, String, Token, and URI. Note that Reference search parameters are implemented using the [HFJ_RES_LINK](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RES_LINK) table above.
##  5.4.6.1Search Hashes[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#search-hashes)
The SPIDX tables leverage "hash columns", which contain a hash of multiple columns in order to reduce index size and improve search performance. Hashes currently use the [MurmurHash3_x64_128](https://en.wikipedia.org/wiki/MurmurHash) hash algorithm, keeping only the first 64 bits in order to produce a LongInt value.
For example, all search index tables have columns for storing the search parameter name (**SP_NAME**) and resource type (**RES_TYPE**). An additional column which hashes these two values is provided, called **HASH_IDENTITY**.
In some configurations, the partition ID is also factored into the hashes.
##  5.4.6.2Tables[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#tables)
![Search Indexes](https://hapifhir.io/hapi-fhir/docs/images/jpa_erd_search_indexes.svg)
##  5.4.6.3Common Search Index Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common)
The following columns are common to **all HFJ_SPIDX_xxx tables**.
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PARTITION_ID |  | Integer | Nullable |  This is the optional partition ID, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html). Note that the partition indicated by the **PARTITION_ID** and **PARTITION_DATE** columns refers to the partition of the _SOURCE_ resource, and not necessarily the _TARGET_.   
PARTITION_DATE |  | Timestamp | Nullable |  This is the optional partition date, if the resource is in a partition. See [Partitioning](https://hapifhir.io/hapi-fhir/docs/server_jpa_partitioning/partitioning.html). Note that the partition indicated by the **PARTITION_ID** and **PARTITION_DATE** columns refers to the partition of the _SOURCE_ resource, and not necessarily the _TARGET_.   
SP_ID |  | Long |  |  Holds the persistent ID   
RES_ID | FK to [HFJ_RESOURCE](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) | Long |  |  Contains the PID of the resource being indexed.   
SP_NAME |  | String | Nullable |  This is the name of the search parameter being indexed.   
RES_TYPE |  | String | Nullable |  This is the name of the resource being indexed.   
HASH_IDENTITY |  | Long |  |  A hash of SP_NAME and RES_TYPE. Used to narrow the table to a specific SearchParameter during sorting, and some queries.   
SP_UPDATED |  | Timestamp | Nullable |  This is the time that this row was last updated.   
SP_MISSING |  | boolean |  |  If this row represents a search parameter that is **not** populated at all in the resource being indexed, this will be populated with the value `true`. Otherwise it will be populated with `false`.   
#  5.4.7HFJ_SPIDX_DATE: Date Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-date-date-search-parameters)
For any FHIR Search Parameter of type [_date_](https://www.hl7.org/fhir/search.html#date) that generates a database index, a row in the `HFJ_SPIDX_DATE` table will be created. Range queries with Date parameters (e.g. `Observation?date=ge2020-01-01`) will query the HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL and/or SP_VALUE_HIGH_DATE_ORDINAL columns. Range queries with DateTime parameters (e.g. `Observation?date=ge2021-01-01T10:30:00`) will query the HASH_IDENTITY, SP_VALUE_LOW and/or SP_VALUE_HIGH columns. Sorting is done by the SP_VALUE_LOW column.
##  5.4.7.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-4)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
SP_VALUE_LOW |  | Timestamp | Nullable |  This is the lower bound of the date in question. 
  * For a point in time date to millisecond precision (such as an Instant with a value of `2020-05-26T15:00:00.000`) this represents the exact value.
  * For an instant value with lower precision, this represents the start of the possible range denoted by the value. For example, for a value of `2020-05-26` this represents `2020-05-26T00:00:00.000`.
  * For a Period with a lower (start) value present, this column contains that value.
  * For a Period with no lower (start) value present, this column contains a timestamp representing the "start of time".

  
SP_VALUE_HIGH |  | Timestamp | Nullable |  This is the upper bound of the date in question. 
  * For a point in time date to millisecond precision (such as an Instant with a value of `2020-05-26T15:00:00.000`) this represents the exact value.
  * For an instant value with lower precision, this represents the start of the possible range denoted by the value. For example, for a value of `2020-05-26` this represents `2020-05-26T23:59:59.999`.
  * For a Period with an upper (end) value present, this column contains that value.
  * For a Period with no upper (end) value present, this column contains a timestamp representing the "end of time".

  
SP_VALUE_LOW_DATE_ORDINAL |  | Integer | Nullable |  This column contains the same Timestamp as `SP_VALUE_LOW`, but truncated to Date precision and formatted as an integer in the format "YYYYMMDD".   
SP_VALUE_HIGH_DATE_ORDINAL |  | Integer | Nullable |  This column contains the same Timestamp as `SP_VALUE_HIGH`, but truncated to Date precision and formatted as an integer in the format "YYYYMMDD".   
#  5.4.8HFJ_SPIDX_NUMBER: Number Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-number-number-search-parameters)
FHIR Search Parameters of type [_number_](https://www.hl7.org/fhir/search.html#number) produce rows in the `HFJ_SPIDX_NUMBER` table. Range queries and sorting use the HASH_IDENTITY and SP_VALUE columns.
##  5.4.8.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-5)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
SP_VALUE |  | Double | Not nullable |  This is the value extracted by the SearchParameter expression.   
#  5.4.9HFJ_SPIDX_QUANTITY: Quantity Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-quantity-quantity-search-parameters)
FHIR Search Parameters of type [_quantity_](https://www.hl7.org/fhir/search.html#quantity) produce rows in the `HFJ_SPIDX_QUANTITY` table. Range queries (e.g. `Observation?valueQuantity=gt100`) with no units provided will query the HASH_IDENTITY and SP_VALUE columns. Range queries (e.g. `Observation?valueQuantity=gt100||mmHg`) with a unit but not unit-sytem provided will use the HASH_IDENTITY_AND_UNITS and SP_VALUE columns. Range queries (e.g. `Observation?valueQuantity=gt100|http://unitsofmeasure.org|mmHg`) with a full system and unit will use the HASH_IDENTITY_SYS_UNITS and SP_VALUE columns. Sorting is done via the HASH_IDENTITY and SP_VALUE columns.
##  5.4.9.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-6)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
HASH_IDENTITY_AND_UNITS |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_UNITS column.   
HASH_IDENTITY_SYS_UNITS |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_SYSTEM and SP_UNITS columns.   
SP_SYSTEM |  | String |  |  The system of the quantity units. e.g. "http://unitsofmeasure.org".   
SP_UNITS |  | String |  |  The units of the quantity. E.g. "mg".   
SP_VALUE |  | Double |  |  This is the value extracted by the SearchParameter expression.   
#  5.4.10HFJ_SPIDX_QUANTITY_NRML: Normalized Quantity Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-quantity-nrml-normalized-quantity-search-parameters)
Hapi Fhir supports searching by normalized units when enabled (see https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-jpaserver-model/ca/uhn/fhir/jpa/model/entity/StorageSettings.html#getNormalizedQuantitySearchLevel()). When this feature is enabled, each row stored in HFJ_SPIDX_QUANTITY to also store a row in HFJ_SPIDX_QUANTITY_NRML in canonical UCUM units. E.g. a weight recorded in an Observation as
```
"valueQuantity" : {
    "value" : 172,
    "unit" : "lb_av",
    "system" : "http://unitsofmeasure.org",
    "code" : "[lb_av]"
  },

```

would match the search `Observation?valueQuantity=172`, but would also match the search `Observation?valueQuantity=78|http://unitsofmeasure.org|kg`. The row in HFJ_SPIDX_QUANTITY would contain the value 172 pounds, while the HFJ_SPIDX_QUANTITY_NRML table would hold the equivalent 78 kg value. Only value searches that provide fully qualified units are eligible for normalized searches. Sorting only uses the HFJ_SPIDX_QUANTITY table.
##  5.4.10.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-7)
Same as HFJ_SPIDX_QUANTITY above, except the SP_VALUE, SP_SYSTEM, and SP_UNITS columns hold the converted value in canonical units instead of the value extracted by the SearchParameter. This table is only used for range queries with a unit which can be converted to canonical units.
#  5.4.11HFJ_SPIDX_STRING: String Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-string-string-search-parameters)
FHIR Search Parameters of type [_string_](https://www.hl7.org/fhir/search.html#string) produce rows in the `HFJ_SPIDX_STRING` table. The default string search matches by prefix, ignoring case or accents. This uses the HASH_IDENTITY column and a LIKE prefix clause on the SP_VALUE_NORMALIZED columns. The `:exact` string search matches exactly. This uses only the HASH_EXACT column. Sorting is done via the HASH_IDENTITY and SP_VALUE_NORMALIZED columns.
##  5.4.11.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-8)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
HASH_EXACT |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_VALUE_EXACT column.   
SP_VALUE_NORMALIZED |  | String |  |  An UPPERCASE string with accents removed.   
SP_VALUE_EXACT |  | String |  |  The extracted string unchanged.   
#  5.4.12HFJ_SPIDX_TOKEN: Token Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-token-token-search-parameters)
FHIR Search Parameters of type [_token_](https://www.hl7.org/fhir/search.html#token) extract values of type Coding, code, and others. These produce rows in the `HFJ_SPIDX_TOKEN` table. The default token search accepts three parameter formats: matching the code (e.g. `Observation?code=15074-8`), matching both system and code (e.g. `Observation?code=http://loinc.org|15074-8`), or matching a system with any code (e.g. `Observation?http://loinc.org|`). All three are exact searches and use the hashes: HASH_VALUE, HASH_SYS_AND_VALUE, and HASH_SYS respectively. Sorting is done via the HASH_IDENTITY and SP_VALUE columns.
##  5.4.12.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-9)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
HASH_VALUE |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_VALUE column.   
HASH_SYS_AND_VALUE |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_SYSTEM and SP_VALUE columns.   
HASH_SYS |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_SYSTEM column.   
SP_SYSTEM |  | String |  |  The system of the code.   
SP_VALUE |  | String |  |  This is the bare code value.   
#  5.4.13HFJ_SPIDX_URI: URI Search Parameters
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-uri-uri-search-parameters)
FHIR Search Parameters of type [_uri_](https://www.hl7.org/fhir/search.html#uri) produce rows in the `HFJ_SPIDX_URI` table. The default uri search matches the complete uri. This uses the HASH_URI column for an exact match. A uri search with the `:above` modifier will match any prefix. This also uses the HASH_URI column, but also tests hashes of every prefix of the query value. A uri search with the `:below` modifier will match any extension. This query uses the HASH_IDENTITY and a LIKE prefix match of the SP_URI column. Sorting is done via the HASH_IDENTITY and SP_URI columns.
##  5.4.13.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-10)
Note: This table has the columns listed below, but it also has all common columns listed above in [Common Search Index Columns](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_SPIDX_common).
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
HASH_URI |  | Long |  |  A hash like HASH_IDENTITY that also includes the SP_URI column.   
SP_URI |  | String |  |  The uri string extracted by the SearchParameter.   
#  5.4.14HFJ_SPIDX_IDENTITY: Search Parameter Identities
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-spidx-identity-search-parameter-identities)
The `HFJ_SPIDX_IDENTITY` table stores hash identities representing unique combinations of search parameter name and resource type of non-reference [FHIR Search Datatypes](http://hl7.org/fhir/search.html): Date, Number, Quantity, String, Token, and URI.
While not currently used in search execution, this table provides metadata to interpret hash identities in the `HFJ_SPIDX_xxx` tables. When the server is configured to omit writing `SP_NAME` and `RES_TYPE` to the `HFJ_SPIDX_xxx` tables, the `HFJ_SPIDX_IDENTITY` table can be used to reconstruct the `SP_NAME` and `RES_TYPE` values for the stored hash identities. See [Enabling Index Storage Optimization](https://hapifhir.io/hapi-fhir/docs/server_jpa/performance.html#enabling-index-storage-optimization) for more information.
##  5.4.14.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-11)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
SP_IDENTITY_ID |  | Integer |  |  This is the persistent ID of the Search Parameter Identity.   
HASH_IDENTITY |  | Long |  |  A hash of SP_NAME and RES_TYPE. Used in the HFJ_SPIDX_xxx tables to narrow results to a specific SearchParameter during sorting and certain queries.   
RES_TYPE |  | String |  |  The FHIR resource type associated with this search parameter.   
SP_NAME |  | String |  |  The name of the FHIR search parameter associated with this identity.   
#  5.4.15HFJ_IDX_CMB_TOK_NU: Combo Non-Unique Search Param
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#hfj-idx-cmb-tok-nu-combo-non-unique-search-param)
This table is used to index [Non-Unique Combo Search Parameters](https://smilecdr.com/docs/fhir_standard/fhir_search_custom_search_parameters.html#combo-search-index-parameters).
##  5.4.15.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-12)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  A unique persistent identifier for the given index row.   
RES_ID | FK to [HFJ_RESOURCE](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) | Long |  |  Contains the PID of the resource being indexed.   
IDX_STRING |  | String |  |  This column contains a FHIR search expression indicating what is being indexed. For example, if a non-unique combo search parameter is present which indexes a combination of Observation#code and Observation#status, this column might contain a value such as `Observation?code=http://loinc.org|1234-5&status=final`  
HASH_COMPLETE |  | Long |  |  This column contains a hash of the value in column `IDX_STRING`.   
#  5.4.16HFJ_IDX_CMP_STRING_UNIQ: Combo Unique Search Param
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_IDX_CMP_STRING_UNIQ)
This table is used to index [Unique Combo Search Parameters](https://smilecdr.com/docs/fhir_standard/fhir_search_custom_search_parameters.html#combo-search-index-parameters).
##  5.4.16.1Columns[](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#columns-13)
Name | Relationships | Datatype | Nullable | Description  
---|---|---|---|---  
PID |  | Long |  |  A unique persistent identifier for the given index row.   
RES_ID | FK to [HFJ_RESOURCE](https://hapifhir.io/hapi-fhir/docs/server_jpa/schema.html#HFJ_RESOURCE) | Long |  |  Contains the PID of the resource being indexed.   
IDX_STRING |  | String |  |  This column contains a FHIR search expression indicating what is being indexed. For example, if a unique combo search parameter is present which indexes a combination of Observation#code and Observation#status, this column might contain a value such as `Observation?code=http://loinc.org|1234-5&status=final`  
HASH_COMPLETE |  | Long |  |  This column contains a hash of the value in column `IDX_STRING`.   
HASH_COMPLETE_2 |  | Long |  |  This column contains an additional hash of the value in column `IDX_STRING`, using a static salt of the value prior to the hashing. This is done in order to increase the number of bits used to hash the index string from 64 to 128.   
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa/database_support.html)
5.4 Database Schema 
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
[ 5.5 Configuration ](https://hapifhir.io/hapi-fhir/docs/server_jpa/configuration.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)