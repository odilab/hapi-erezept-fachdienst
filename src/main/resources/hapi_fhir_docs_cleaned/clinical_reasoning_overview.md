---
source: https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html
crawled: 2025-08-01T14:08:01.545100
---

# Clinical Reasoning Overview

#  10.0.1Clinical Reasoning
[ ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html#clinical-reasoning)
##  10.0.1.1Overview[](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html#overview)
Clinical Reasoning (CR) is ability to represent, encode, and evaluate clinical knowledge so that it can be integrated into clinical systems. In other words, clinical reasoning is the ability to store and run business logic that is relevant to clinical settings. This may be as simple as controlling whether a particular section of an order set appears based on the conditions that a patient has, or it may be as complex as representing the care pathway for a patient with multiple conditions.
The FHIR [Clinical Reasoning module](http://www.hl7.org/fhir/clinicalreasoning-module.html) specifies a foundational set of FHIR resources and associated operations that allow a FHIR repository to perform clinical reasoning on clinical data. Some use cases include:
  * Prospective/Retrospective Analytics 
    * Quality Measures
    * Gaps in Care
  * Clinical Decision Support
  * Payer/Provider Data Exchange
  * Prior Authorization


There are additional IGs outside the FHIR CR module that define further requirements and behavior for other Clinical Reasoning use cases. Some examples include:
  * [Structured Data Capture IG](https://build.fhir.org/ig/HL7/sdc/)
  * [Clinical Guidelines IG](https://hl7.org/fhir/uv/cpg/)
  * [Quality Measures IG](http://hl7.org/fhir/us/cqfmeasures/)
  * [Canonical Resource Management Infrastructure IG](https://build.fhir.org/ig/HL7/crmi-ig/index.html)


##  10.0.1.2HAPI FHIR[](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html#hapi-fhir)
The HAPI FHIR server includes support for storing all the Clinical Reasoning resources defined in the FHIR CR module, including `Measure`, `PlanDefinition`, `ActivityDefinition` and so on. Additionally, HAPI includes an embedded [CQL](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html) engine that allows it to process clinical logic encoded in a standard representation.
HAPI also includes a [Quality Measure](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/measures.html) engine that can evaluate clinical quality measures.
See the [CQL](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html) and [Measure](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/measures.html) documentation for further details.
10.0 Clinical Reasoning Overview 
* Clinical Reasoning 
* [ 10.0  Clinical Reasoning Overview ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/overview.html)
* [ 10.1  CQL ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html)
* [ 10.2  Care Gaps ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/caregaps.html)
* [ 10.3  Measures ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/measures.html)
* [ 10.4  ActivityDefinitions ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/activity_definitions.html)
* [ 10.5  PlanDefinitions ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/plan_definitions.html)
* [ 10.6  Questionnaires ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/questionnaires.html)
[ 10.1 CQL ](https://hapifhir.io/hapi-fhir/docs/clinical_reasoning/cql.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)