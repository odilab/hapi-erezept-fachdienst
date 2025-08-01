---
source: https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html
crawled: 2025-08-01T14:05:46.673567
---

# Server Jpa Mdm Mdm Rules

#  6.1.1Rules
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html#rules)
HAPI MDM rules are defined in a single json document.
Note that in all of the following configurations, valid options for `resourceType` include any supported resource, such as `Organization`, `Patient`, `Practitioner`, and `*`. Use `*` if the criteria is identical across both resource types and you would like to apply it to all resources.
Here is an example of a full HAPI MDM rules json document:
```
{
   "version": "1",
   "mdmTypes": ["Organization", "Patient", "Practitioner"],
   "candidateSearchParams": [
      {
         "resourceType": "Patient",
         "searchParams": ["phone"]
      },
      {
         "resourceType": "Patient",
         "searchParams": ["birthdate"]
      },
      {
         "resourceType": "*",
         "searchParams": ["identifier"]
      }
   ],
   "candidateFilterSearchParams": [
      {
         "resourceType": "Patient",
         "searchParam": "active",
         "fixedValue": "true"
      }
   ],
   "matchFields": [
      {
         "name": "birthday",
         "resourceType": "Patient",
         "resourcePath": "birthDate",
         "matcher": {
            "algorithm": "STRING"
         }
      },
      {
         "name": "phone",
         "resourceType": "Patient",
         "resourcePath": "telecom.value",
         "matcher": {
            "algorithm": "STRING"
         }
      },
      {
         "name": "firstname-meta",
         "resourceType": "Patient",
         "fhirPath": "name.given.first()",
         "matcher": {
            "algorithm": "METAPHONE"
         }
      },
      {
         "name": "lastname-meta",
         "resourceType": "Patient",
         "resourcePath": "name.family",
         "matcher": {
            "algorithm": "METAPHONE"
         }
      },
      {
         "name": "firstname-jaro",
         "resourceType": "Patient",
         "resourcePath": "name.given",
         "similarity": {
            "algorithm": "JARO_WINKLER",
            "matchThreshold": 0.8
         }
      },
      {
         "name": "lastname-jaro",
         "resourceType": "Patient",
         "resourcePath": "name.family",
         "similarity": {
            "algorithm": "JARO_WINKLER",
            "matchThreshold": 0.8
         }
      },
      {
         "name": "org-name",
         "resourceType": "Organization",
         "resourcePath": "name",
         "matcher": {
            "algorithm": "STRING"
         }
      }
   ],
   "matchResultMap": {
      "firstname-meta,lastname-meta,birthday": "MATCH",
      "firstname-meta,lastname-meta,phone": "MATCH",
      "firstname-jaro,lastname-jaro,birthday": "POSSIBLE_MATCH",
      "firstname-jaro,lastname-jaro,phone": "POSSIBLE_MATCH",
      "lastname-jaro,phone,birthday": "POSSIBLE_MATCH",
      "firstname-jaro,phone,birthday": "POSSIBLE_MATCH",
      "org-name": "MATCH"
   },
   "eidSystems": {
      "Organization": "https://hapifhir.org/identifier/naming/business-number",
      "Practitioner": "https://hapifhir.org/identifier/naming/license-number"
   }
}

```

Copy
Here is a description of how each section of this document is configured.
6.1.1.0.1candidateSearchParams
These define one or more fields which must have a match before two resources are considered for matching. This is like a list of "pre-searches" that find potential candidates for matches, to avoid the expensive operation of running a match score calculation on all resources in the system. `candidateSearchParams` are capable of making searches using any SearchParameter defined in the system. For example, [phonetic SearchParameters](https://smilecdr.com/docs/fhir_repository/search_parameter_phonetic.html) can be useful here when matchFields include phonetic matchers. E.g. you may only wish to consider matching two Patients if they either share at least one identifier in common or have the same birthday or the same phone number. The HAPI FHIR server executes each of these searches separately and then takes the union of the results, so you can think of these as `OR` criteria that cast a wide net for potential candidates. In some MDM systems, these "pre-searches" are called "blocking" searches (since they identify "blocks" of candidates that will be searched for matches).
If a list of searchParams is specified in a given `candidateSearchParams` item, then these search parameters are treated as `AND` parameters. In the following `candidateSearchParams` definition, hapi-fhir will extract given name, family name and identifiers from the incoming Patient and perform two separate searches, first for all Patient resources that have the same given `AND` the same family name as the incoming Patient, and second for all Patient resources that share at least one identifier as the incoming Patient. Note that if the incoming Patient was missing any of these searchParam values, then that search would be skipped. E.g. if the incoming Patient had a given name but no family name, then only a search for matching identifiers would be performed.
```
{
   "candidateSearchParams": [
      {
         "resourceType": "Patient",
         "searchParams": ["given", "family"]
      },
      {
         "resourceType": "Patient",
         "searchParam": "identifier"
      }
   ]
}

```

Copy
6.1.1.0.2candidateFilterSearchParams When searching for match candidates, only resources that match this filter are considered. E.g. you may wish to only search for Patients for which active=true. ```
{
   "candidateFilterSearchParams": [
      {
         "resourceType": "Patient",
         "searchParam": "active",
         "fixedValue": "true"
      }
   ]
}

```
Copy For example, if the incoming patient looked like this: ```
{
   "resourceType": "Patient",
   "id": "example",
   "identifier": [
      {
         "system": "urn:oid:1.2.36.146.595.217.0.1",
         "value": "12345"
      }
   ],
   "name": [
      {
         "family": "Chalmers",
         "given": ["Peter", "James"]
      }
   ]
}

```
Copy then the above `candidateSearchParams` and `candidateFilterSearchParams` would result in the following two parallel searches for candidates:
  * `Patient?given=Peter,James&family=Chalmers&active=true`
  * `Patient?identifier=urn:oid:1.2.36.146.595.217.0.1|12345&active=true`

If you also wish to search for Practitioners for which active=true, the `resourceType` must be defined in the `candidateSearchParams` before the `candidateFilterSearchParams` can be applied. The `candidateSearchParams` should look like this: ```
{
   "candidateSearchParams": [
      {
         "resourceType": "Patient",
         "searchParams": ["given", "family"]
      },
      {
         "resourceType": "Practitioner",
         "searchParam": "email"
      }
   ]
}

```
Copy and `candidateFilterSearchParams` should look like this: ```
{
   "candidateFilterSearchParams": [
      {
         "resourceType": "Patient",
         "searchParam": "active",
         "fixedValue": "true"
      },
      {
         "resourceType": "Practitioner",
         "searchParam": "active",
         "fixedValue": "true"
      }
   ]
}

```
Copy For example, if the incoming patient looked like this: ```
{
   "resourceType": "Patient",
   "id": "example",
   "identifier": [
      {
         "system": "urn:oid:1.2.36.146.595.217.0.1",
         "value": "12345"
      }
   ],
   "name": [
      {
         "family": "Chalmers",
         "given": ["Peter", "James"]
      }
   ]
}

```
Copy and the incoming practitioner looked like this: ```
{
   "resourceType": "Practitioner",
   "id": "example",
   "identifier": [
      {
         "system": "urn:oid:1.2.36.146.595.404.0.1",
         "value": "56789"
      }
   ],
   "name": [
      {
         "family": "Smith",
         "given": ["Adam"]
      }
   ]
}

```
Copy The resulting searches would be:
  * `Patient?given=Peter,James&family=Chalmers&active=true`
  * `Patient?identifier=urn:oid:1.2.36.146.595.217.0.1|12345&active=true`
  * `Practitioner?given=Adam&family=Smith&active=true`
  * `Patient?identifier=urn:oid:urn:oid:1.2.36.146.595.404.0.1|56789&active=true`

If the practitioner `resourceType` was not defined in the `candidateSearchParams`, `active=true` would not be added to that `resourceType` and the resulting searches would be:
  * `Patient?given=Peter,James&family=Chalmers&active=true`
  * `Patient?identifier=urn:oid:1.2.36.146.595.217.0.1|12345&active=true`
  * `Practitioner?given=Adam&family=Smith`
  * `Patient?identifier=urn:oid:1.2.36.146.595.404.0.1|56789`

For instances where the `candidateFilterSearchParams` criteria is identical across both resource types and you would like to apply it to all resources, the * wildcard could be used in `resourceType`. Since multiple resource types cannot be defined at once, the following `candidateFilterSearchParams` would be incorrect: ```
{
   "candidateFilterSearchParams": [
      {
         "resourceType": ["Patient, Practitioner"],
         "searchParam": "active",
         "fixedValue": "true"
      }
   ]
}

```
Copy However if you would like to apply the `candidateFilterSearchParams` to specific resource types only, the resource types must be individually defined, like in: ```
{
   "candidateFilterSearchParams": [
      {
         "resourceType": "Patient",
         "searchParam": "active",
         "fixedValue": "true"
      },
      {
         "resourceType": "Practitioner",
         "searchParam": "active",
         "fixedValue": "true"
      }
   ]
}

```
Copy 6.1.1.0.3matchFields Once the match candidates have been found, they are then each compared to the incoming Patient resource. This comparison is made across a list of `matchField`s. Each matchField returns `true` or `false` indicating whether the candidate and the incoming Patient match on that field. There are two types of matchFields: `matcher` and `similarity`. `matcher` matchFields return a `true` or `false` directly, whereas `similarity` matchFields return a score between 0.0 (no match) and 1.0 (exact match) and this score is translated to a `true/false` via a `matchThreshold`. E.g. if a `JARO_WINKLER` matchField is configured with a `matchThreshold` of 0.8 then that matchField will only return `true` if the `JARO_WINKLER` similarity evaluates to a score >= 0.8. By default, all matchFields have `exact=false` which means that they will have all diacritical marks removed and all letters will be converted to upper case before matching. `exact=true` can be added to any matchField to compare the strings as they are originally capitalized and accented. Here is a matcher matchField that uses the SOUNDEX matcher to determine whether two family names match. ```
{
   "name": "familyname-soundex",
   "resourceType": "*",
   "resourcePath": "name.family",
   "matcher": {
      "algorithm": "SOUNDEX"
   }
}

```
Copy Here is a matcher matchField that only matches when two family names are identical. ```
{
   "name": "familyname-exact",
   "resourceType": "*",
   "resourcePath": "name.family",
   "matcher": {
      "algorithm": "STRING",
      "exact": true
   }
}

```
Copy While it is often suitable to use the `resourcePath` field to indicate the location of the data to be matched, occasionally you will need more direct control over precisely which fields are matched. When performing string matching, the matcher will indiscriminately try to match all elements of the left resource to all elements of the right resource. For example, consider the following two patients and matcher. ```
{
   "resourceType": "Patient",
   "name": [
      {
         "given": ["Frank", "John"]
      }
   ]
}

```
Copy ```
{
   "resourceType": "Patient",
   "name": [
      {
         "given": ["John", "Frank"]
      }
   ]
}

```
Copy ```
{
   "name": "firstname-meta",
   "resourceType": "Patient",
   "resourcePath": "name.given",
   "matcher": {
      "algorithm": "METAPHONE"
   }
}

```
Copy In this example, these two patients would match, as the matcher will compare all elements of `["John", "Frank"]` to all elements of `["Frank", "John"]` and find that there are matches. This is when you would want to use a FHIRPath matcher, as FHIRPath expressions give you more direct control. This following example shows a matcher that would cause these two patient's not to match to each other. ```
{
   "name": "firstname-meta-fhirpath",
   "resourceType": "Patient",
   "fhirPath": "name.given[0]",
   "matcher": {
      "algorithm": "METAPHONE"
   }
}

```
Copy Since FHIRPath expressions support indexing it is possible to directly indicate that you would only like to compare the first element of each resource. Special identifier matching is also available if you need to match on a particular identifier system: ```
{
   "name": "identifier-ssn",
   "resourceType": "*",
   "resourcePath": "identifier",
   "matcher": {
      "algorithm": "IDENTIFIER",
      "identifierSystem": "http://hl7.org/fhir/sid/us-ssn"
   }
}

```
Copy Here is a similarity matchField that matches when two given names match with a JARO_WINKLER threshold >= 0.8. ```
{
   "name": "firstname-jaro",
   "resourceType": "*",
   "resourcePath": "name.given",
   "similarity": {
      "algorithm": "JARO_WINKLER",
      "matchThreshold": 0.8
   }
}

```
Copy The following algorithms are currently supported: Algorithm | Type | Description | Example  
---|---|---|---  
CAVERPHONE1 | matcher |  [Apache Caverphone1](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/Caverphone1.html) | Gail = Gael, Gail != Gale, Thomas != Tom  
CAVERPHONE2 | matcher |  [Apache Caverphone2](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/Caverphone2.html) | Gail = Gael, Gail = Gale, Thomas != Tom  
COLOGNE | matcher |  [Apache Cologne Phonetic](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/ColognePhonetic.html) |   
DOUBLE_METAPHONE | matcher |  [Apache Double Metaphone](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/DoubleMetaphone.html) | Dury = Durie, Allsop = Allsob, Smith != Schmidt  
MATCH_RATING_APPROACH | matcher |  [Apache Match Rating Approach Encoder](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/MatchRatingApproachEncoder.html) |   
METAPHONE | matcher |  [Apache Metaphone](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/Metaphone.html) | Dury = Durie, Allsop != Allsob, Smith != Schmidt  
NYSIIS | matcher |  [Apache Nysiis](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/Nysiis.html) |   
REFINED_SOUNDEX | matcher |  [Apache Refined Soundex](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/RefinedSoundex.html) |   
SOUNDEX | matcher |  [Apache Soundex](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/language/Soundex.html) | Jon = John, Thomas != Tom  
STRING | matcher |  Match the values as strings. This matcher should be used with tokens (e.g. gender).  | MCTAVISH = McTavish when exact = false, MCTAVISH != McTavish when exact = true  
SUBSTRING | matcher |  True if one string starts with the other.  | Bill = Billy, Egbert = Bert  
DATE | matcher |  Reduce the precision of the dates to the lowest precision of the two, then compare them as strings.  | 2019-12,Month = 2019-12-19,Day  
NUMERIC | matcher |  Remove all non-numeric characters from the string before comparing.  | 4169671111 = (416) 967-1111  
NAME_ANY_ORDER | matcher |  Match names as strings in any order  | John Henry = Henry JOHN when exact = false  
NAME_FIRST_AND_LAST | matcher |  Match names as strings in any order  | John Henry = John HENRY when exact=false, John Henry != Henry John  
NICKNAME | matcher |  True if one name is a nickname of the other  | Ken = Kenneth, Kenny = Ken. Allen != Allan.  
IDENTIFIER | matcher |  Matches when the system and value of the identifier are identical.  | If an optional "identifierSystem" is provided, then the identifiers only match when they belong to that system  
EXTENSION_ANY_ORDER | matcher |  Matches extensions of resources in any order. Matches are made if both resources share at least one extensions that have the same URL and value.  |   
EMPTY_FIELD | matcher |  Matches an empty field.  |   
JARO_WINKLER | similarity |  [tdebatty Jaro Winkler](https://github.com/tdebatty/java-string-similarity#jaro-winkler) |   
COSINE | similarity |  [tdebatty Cosine Similarity](https://github.com/tdebatty/java-string-similarity#cosine-similarity) |   
JACCARD | similarity |  [tdebatty Jaccard Index](https://github.com/tdebatty/java-string-similarity#jaccard-index) |   
LEVENSCHTEIN | similarity |  [tdebatty Normalized Levenshtein](https://github.com/tdebatty/java-string-similarity#normalized-levenshtein) |   
SORENSEN_DICE | similarity |  [tdebatty Sorensen-Dice coefficient](https://github.com/tdebatty/java-string-similarity#sorensen-dice-coefficient) |   
NUMERIC_JARO_WINKLER | similarity |  Removes all non-numeric characters before applying [tdebatty Jaro Winkler](https://github.com/tdebatty/java-string-similarity#jaro-winkler) |   
NUMERIC_COSINE | similarity |  Removes all non-numeric characters before applying [tdebatty Cosine Similarity](https://github.com/tdebatty/java-string-similarity#cosine-similarity) |   
NUMERIC_JACCARD | similarity |  Removes all non-numeric characters before applying [tdebatty Jaccard Index](https://github.com/tdebatty/java-string-similarity#jaccard-index) |   
NUMERIC_LEVENSCHTEIN | similarity |  Removes all non-numeric characters before applying [tdebatty Normalized Levenshtein](https://github.com/tdebatty/java-string-similarity#normalized-levenshtein) |   
NUMERIC_SORENSEN_DICE | similarity |  Removes all non-numeric characters before applying [tdebatty Sorensen-Dice coefficient](https://github.com/tdebatty/java-string-similarity#sorensen-dice-coefficient) |   
6.1.1.0.4matchResultMap These entries convert combinations of successful matchFields into an MDM Match Result for overall matching of a given pair of resources. MATCH results are evaluated take precedence over POSSIBLE_MATCH results. If the incoming resource matches ALL of the named matchFields listed, then a new match link is created with the assigned matchResult (`MATCH` or `POSSIBLE_MATCH`). ```
{
   "matchResultMap": {
      "firstname-meta,lastname-meta,birthday": "MATCH",
      "firstname-jaro,lastname-jaro,birthday": "POSSIBLE_MATCH"
   }
}

```
Copy 6.1.1.0.5eidSystems
The external EID systems that the HAPI MDM system can expect to see on incoming resources. These are defined on a per-resource basis. Alternatively, you may use `*` to indicate that an EID is valid for all managed resource types. The values must be valid URIs, and the keys must be valid resource types, or `*`. See [MDM EID](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html) for details on how EIDs are managed by HAPI MDM.
Note that this field used to be called `eidSystem`. While that field is deprecated, it will continue to work. In the background, it effectively sets the eid for resource type `*`. 
[ ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
6.1 MDM Rules 
* JPA Server: MDM 
* [ 6.0  MDM Getting Started ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm.html)
* [ 6.1  MDM Rules ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_rules.html)
* [ 6.2  MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* [ 6.3  MDM Operations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_operations.html)
* [ 6.4  MDM Technical Details ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_details.html)
* [ 6.5  MDM Search Expansion ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_expansion.html)
* [ 6.6  MDM Customizations ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_customizations.html)
[ 6.2 MDM Enterprise Identifiers ](https://hapifhir.io/hapi-fhir/docs/server_jpa_mdm/mdm_eid.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)