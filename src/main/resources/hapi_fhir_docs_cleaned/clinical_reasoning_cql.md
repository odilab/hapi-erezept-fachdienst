---
source: https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html
crawled: 2025-08-01T14:05:14.367996
---

# Clinical Reasoning Cql

#  10.1.1CQL
[ ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html#cql)
##  10.1.1.1Introduction[](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html#introduction)
Clinical Quality Language (CQL) is a high-level, domain-specific language focused on clinical quality and targeted at measure and decision support artifact authors. HAPI embeds a [CQL engine](https://github.com/cqframework/clinical_quality_language) allowing the evaluation of clinical knowledge artifacts that use CQL to describe their logic.
A more detailed description of CQL is available at the [CQL Specification Implementation Guide](https://cql.hl7.org/)
The FHIR [Clinical Reasoning module](http://www.hl7.org/fhir/clinicalreasoning-module.html) defines a set of resources, profiles, operations, etc. that can be used to work with clinical knowledge within FHIR. HAPI provides implementation for some of those operations, described in more detail below.
##  10.1.1.2Working Example[](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html#working-example)
A complete working example of HAPI CQL can be found in the [JPA Server Starter](https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html) project. You may wish to browse its source to see how it is set up.
##  10.1.1.3Clinical Reasoning Operations[](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html#clinical-reasoning-operations)
HAPI provides implementations for some operations using CQL in DSTU3 and R4:
[Measure Operations](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/measures.html)
[ ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html)
10.1 CQL 
* Clinical Reasoning 
* [ 10.0  Clinical Reasoning Overview ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html)
* [ 10.1  CQL ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html)
* [ 10.2  Care Gaps ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/caregaps.html)
* [ 10.3  Measures ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/measures.html)
* [ 10.4  ActivityDefinitions ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/activity_definitions.html)
* [ 10.5  PlanDefinitions ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/plan_definitions.html)
* [ 10.6  Questionnaires ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/questionnaires.html)
[ 10.2 Care Gaps ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/caregaps.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)