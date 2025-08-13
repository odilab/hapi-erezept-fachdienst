#!/bin/bash

echo "==========================================="
echo "E-REZEPT KOMPLETTER WORKFLOW TEST"
echo "==========================================="
echo ""

# Farben für Output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. CREATE
echo -e "${GREEN}1. CREATE Operation${NC}"
TOKEN_ARZT=$(curl -s -k "https://localhost:3001/getIdpToken?healthcards=SMCB_KRANKENHAUS" | jq -r '.accessToken')
echo "   Token Arzt erhalten"

CREATE_RESPONSE=$(curl -s -X POST "http://localhost:8080/fhir/Task/\$create" \
  -H "Authorization: Bearer $TOKEN_ARZT" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Parameters",
    "parameter": [{
      "name": "workflowType",
      "valueCoding": {
        "system": "https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
        "code": "160"
      }
    }]
  }')

PRESCRIPTION_ID=$(echo "$CREATE_RESPONSE" | jq -r '.parameter[0].resource.id')
ACCESS_CODE=$(echo "$CREATE_RESPONSE" | jq -r '.parameter[0].resource.identifier[] | select(.system=="https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_AccessCode") | .value')
echo "   ✓ Task erstellt: $PRESCRIPTION_ID"
echo ""

# 2. ACTIVATE
echo -e "${GREEN}2. ACTIVATE Operation${NC}"

# Berechne aktuelle Datumswerte
CURRENT_TIMESTAMP=$(date -u +%Y-%m-%dT%H:%M:%SZ)
CURRENT_DATE=$(date +%Y-%m-%d)

# Erstelle KBV Bundle
cat > /tmp/bundle.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<Bundle xmlns="http://hl7.org/fhir">
  <id value="test"/>
  <meta>
    <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle|1.1.0"/>
  </meta>
  <identifier>
    <system value="https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_PrescriptionId"/>
    <value value="PRESCRIPTION_ID"/>
  </identifier>
  <type value="document"/>
  <timestamp value="$CURRENT_TIMESTAMP"/>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/Composition/1"/>
    <resource>
      <Composition>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Composition|1.1.0"/>
        </meta>
        <status value="final"/>
        <type>
          <coding>
            <system value="https://fhir.kbv.de/CodeSystem/KBV_CS_SFHIR_KBV_FORMULAR_ART"/>
            <code value="e16A"/>
          </coding>
        </type>
        <subject><reference value="Patient/1"/></subject>
        <date value="$CURRENT_DATE"/>
        <author><reference value="Practitioner/1"/></author>
        <title value="elektronische Arzneimittelverordnung"/>
        <section>
          <code>
            <coding>
              <system value="https://fhir.kbv.de/CodeSystem/KBV_CS_ERP_Section_Type"/>
              <code value="Prescription"/>
            </coding>
          </code>
          <entry><reference value="MedicationRequest/1"/></entry>
        </section>
        <section>
          <code>
            <coding>
              <system value="https://fhir.kbv.de/CodeSystem/KBV_CS_ERP_Section_Type"/>
              <code value="Coverage"/>
            </coding>
          </code>
          <entry><reference value="Coverage/1"/></entry>
        </section>
      </Composition>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/MedicationRequest/1"/>
    <resource>
      <MedicationRequest>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Prescription|1.1.0"/>
        </meta>
        <status value="active"/>
        <intent value="order"/>
        <medicationCodeableConcept>
          <coding>
            <system value="http://fhir.de/CodeSystem/ifa/pzn"/>
            <code value="06313728"/>
          </coding>
          <text value="Sumatriptan-1A Pharma 100 mg Tabletten"/>
        </medicationCodeableConcept>
        <subject><reference value="Patient/1"/></subject>
        <authoredOn value="$CURRENT_DATE"/>
        <requester><reference value="Practitioner/1"/></requester>
        <dosageInstruction><text value="1-0-0-0"/></dosageInstruction>
        <dispenseRequest>
          <quantity>
            <value value="1"/>
            <system value="http://unitsofmeasure.org"/>
            <code value="{Package}"/>
          </quantity>
        </dispenseRequest>
        <substitution><allowedBoolean value="true"/></substitution>
      </MedicationRequest>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/Patient/1"/>
    <resource>
      <Patient>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Patient|1.1.0"/>
        </meta>
        <identifier>
          <type>
            <coding>
              <system value="http://fhir.de/CodeSystem/identifier-type-de-basis"/>
              <code value="GKV"/>
            </coding>
          </type>
          <system value="http://fhir.de/sid/gkv/kvid-10"/>
          <value value="S040464113"/>
        </identifier>
        <name>
          <use value="official"/>
          <family value="Mustermann"/>
          <given value="Max"/>
        </name>
        <birthDate value="1980-01-01"/>
        <address>
          <type value="both"/>
          <line value="Musterstraße 1"/>
          <city value="Berlin"/>
          <postalCode value="10115"/>
          <country value="D"/>
        </address>
      </Patient>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/Practitioner/1"/>
    <resource>
      <Practitioner>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Practitioner|1.1.0"/>
        </meta>
        <identifier>
          <type>
            <coding>
              <system value="http://terminology.hl7.org/CodeSystem/v2-0203"/>
              <code value="LANR"/>
            </coding>
          </type>
          <system value="https://fhir.kbv.de/NamingSystem/KBV_NS_Base_ANR"/>
          <value value="123456789"/>
        </identifier>
        <name>
          <use value="official"/>
          <family value="Meier"/>
          <given value="Hans"/>
          <prefix value="Dr."/>
        </name>
        <qualification>
          <code>
            <coding>
              <system value="https://fhir.kbv.de/CodeSystem/KBV_CS_FOR_Qualification_Type"/>
              <code value="00"/>
            </coding>
          </code>
        </qualification>
        <qualification>
          <code>
            <text value="Facharzt für Allgemeinmedizin"/>
          </code>
        </qualification>
      </Practitioner>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/Organization/1"/>
    <resource>
      <Organization>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Organization|1.1.0"/>
        </meta>
        <identifier>
          <type>
            <coding>
              <system value="http://terminology.hl7.org/CodeSystem/v2-0203"/>
              <code value="BSNR"/>
            </coding>
          </type>
          <system value="https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR"/>
          <value value="031234567"/>
        </identifier>
        <name value="Hausarztpraxis Dr. Meier"/>
        <telecom>
          <system value="phone"/>
          <value value="030123456"/>
        </telecom>
        <address>
          <type value="both"/>
          <line value="Musterstraße 2"/>
          <city value="Berlin"/>
          <postalCode value="10115"/>
          <country value="D"/>
        </address>
      </Organization>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://pvs.praxis.local/fhir/Coverage/1"/>
    <resource>
      <Coverage>
        <id value="1"/>
        <meta>
          <profile value="https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Coverage|1.1.0"/>
        </meta>
        <status value="active"/>
        <type>
          <coding>
            <system value="http://fhir.de/CodeSystem/versicherungsart-de-basis"/>
            <code value="GKV"/>
          </coding>
        </type>
        <beneficiary><reference value="Patient/1"/></beneficiary>
        <payor>
          <identifier>
            <system value="http://fhir.de/sid/arge-ik/iknr"/>
            <value value="104212059"/>
          </identifier>
          <display value="AOK Rheinland/Hamburg"/>
        </payor>
      </Coverage>
    </resource>
  </entry>
</Bundle>
EOF

sed -i.bak "s/PRESCRIPTION_ID/$PRESCRIPTION_ID/g" /tmp/bundle.xml

# Signiere Bundle
SIGNED=$(curl -s -k -X POST "https://localhost:3001/signDocumentWithTool" \
  -F "kbvBundleFromFile=@/tmp/bundle.xml" \
  -F "kbvBundleAsString=")

ACTIVATE_RESPONSE=$(curl -s -X POST "http://localhost:8080/fhir/Task/$PRESCRIPTION_ID/\$activate" \
  -H "Authorization: Bearer $TOKEN_ARZT" \
  -H "X-AccessCode: $ACCESS_CODE" \
  -H "Content-Type: application/fhir+json" \
  -d "{
    \"resourceType\": \"Parameters\",
    \"parameter\": [{
      \"name\": \"ePrescription\",
      \"resource\": {
        \"resourceType\": \"Binary\",
        \"contentType\": \"application/pkcs7-mime\",
        \"data\": \"$SIGNED\"
      }
    }]
  }")

ACTIVATE_STATUS=$(echo "$ACTIVATE_RESPONSE" | jq -r '.parameter[0].resource.status' 2>/dev/null)
if [ "$ACTIVATE_STATUS" = "ready" ]; then
  echo "   ✓ Task aktiviert - Status: ready"
else
  echo -e "   ${RED}✗ Fehler bei ACTIVATE${NC}"
  exit 1
fi
echo ""

# 3. ACCEPT
echo -e "${GREEN}3. ACCEPT Operation${NC}"
TOKEN_APOTHEKE=$(curl -s -k "https://localhost:3001/getIdpToken?healthcards=SMCB_APOTHEKE" | jq -r '.accessToken')
echo "   Token Apotheke erhalten"

ACCEPT_RESPONSE=$(curl -s -X POST "http://localhost:8080/fhir/Task/$PRESCRIPTION_ID/\$accept" \
  -H "Authorization: Bearer $TOKEN_APOTHEKE" \
  -H "X-AccessCode: $ACCESS_CODE")

if echo "$ACCEPT_RESPONSE" | jq -e '.resourceType == "Bundle"' > /dev/null 2>&1; then
  SECRET=$(echo "$ACCEPT_RESPONSE" | jq -r '.entry[0].resource.identifier[] | select(.system=="https://gematik.de/fhir/erp/NamingSystem/GEM_ERP_NS_Secret") | .value' 2>/dev/null)
  echo "   ✓ Task akzeptiert - Status: in-progress"
  echo "   Secret: ${SECRET:0:20}..."
else
  echo -e "   ${RED}✗ Fehler bei ACCEPT${NC}"
  echo "$ACCEPT_RESPONSE" | jq .
  exit 1
fi
echo ""

# 4. CLOSE
echo -e "${GREEN}4. CLOSE Operation${NC}"

# Baue Parameters-Payload für rxDispensation als verschachtelte Parameters-Struktur
CLOSE_PARAMS=$(cat <<EOF
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "rxDispensation",
      "resource": {
        "resourceType": "Parameters",
        "parameter": [
          {
            "name": "medicationDispense",
            "resource": {
              "resourceType": "MedicationDispense",
              "status": "completed",
              "medicationCodeableConcept": {
                "coding": [{
                  "system": "http://fhir.de/CodeSystem/ifa/pzn",
                  "code": "06313728"
                }],
                "text": "Sumatriptan-1A Pharma 100 mg Tabletten"
              },
              "subject": {
                "identifier": {
                  "system": "http://fhir.de/sid/gkv/kvid-10",
                  "value": "S040464113"
                }
              },
              "quantity": {
                "value": 1,
                "system": "http://unitsofmeasure.org",
                "code": "{Package}"
              },
              "whenHandedOver": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
            }
          }
        ]
      }
    }
  ]
}
EOF
)

CLOSE_RESPONSE=$(curl -s -X POST "http://localhost:8080/fhir/Task/$PRESCRIPTION_ID/\$close" \
  -H "Authorization: Bearer $TOKEN_APOTHEKE" \
  -H "X-Secret: $SECRET" \
  -H "Content-Type: application/fhir+json" \
  -d "$CLOSE_PARAMS")

if echo "$CLOSE_RESPONSE" | jq -e '.resourceType == "Bundle" and .type == "document"' > /dev/null 2>&1; then
  echo "   ✓ Task abgeschlossen - Receipt erstellt"
else
  echo -e "   ${RED}✗ Fehler bei CLOSE${NC}"
  echo "$CLOSE_RESPONSE" | jq .
  exit 1
fi

echo ""
echo -e "${GREEN}==========================================="
echo "WORKFLOW ERFOLGREICH ABGESCHLOSSEN!"
echo "==========================================="
echo ""
echo "Zusammenfassung:"
echo "  Prescription ID: $PRESCRIPTION_ID"
echo "  CREATE  → Status: draft"
echo "  ACTIVATE → Status: ready"
echo "  ACCEPT  → Status: in-progress"
echo "  CLOSE   → Status: completed"
echo -e "===========================================${NC}"