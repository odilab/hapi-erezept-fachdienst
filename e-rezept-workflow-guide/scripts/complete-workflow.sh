#!/bin/bash

# E-Rezept Workflow - Kompletter Durchlauf
# ========================================
# Dieses Skript führt den kompletten E-Rezept-Workflow durch:
# 1. Create -> 2. Activate -> 3. Accept -> 4. Close

# Konfiguration
BASE_URL="http://localhost:8080/fhir"
IDP_URL="http://localhost:3000"
ERP_SERVICE_URL="http://localhost:3001"

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funktionen
print_step() {
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}$1${NC}"
    echo -e "${GREEN}========================================${NC}"
}

print_error() {
    echo -e "${RED}ERROR: $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}WARNING: $1${NC}"
}

# Prüfe Voraussetzungen
check_prerequisites() {
    print_step "Prüfe Voraussetzungen"
    
    # Prüfe ob curl installiert ist
    if ! command -v curl &> /dev/null; then
        print_error "curl ist nicht installiert"
        exit 1
    fi
    
    # Prüfe ob jq installiert ist
    if ! command -v jq &> /dev/null; then
        print_error "jq ist nicht installiert. Bitte installieren mit: brew install jq"
        exit 1
    fi
    
    # Prüfe ob FHIR Server erreichbar ist
    if ! curl -s -f "$BASE_URL/metadata" > /dev/null; then
        print_error "FHIR Server ist nicht erreichbar unter $BASE_URL"
        exit 1
    fi
    
    echo "✓ Alle Voraussetzungen erfüllt"
}

# Hole Access Token
get_access_token() {
    local role=$1
    print_step "Hole Access Token für Rolle: $role"
    
    # Hier würde normalerweise der Token vom IDP geholt werden
    # Für Tests verwenden wir einen Mock-Token
    ACCESS_TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicm9sZSI6IiRyb2xlIn0.mock"
    
    echo "✓ Access Token erhalten"
}

# Schritt 1: Create Operation
create_task() {
    print_step "Schritt 1: CREATE - Erstelle E-Rezept"
    
    get_access_token "SMCB_KRANKENHAUS"
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/Task/\$create" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/fhir+json" \
        -d @../test-data/01-create-request.json)
    
    # Extrahiere wichtige Felder
    PRESCRIPTION_ID=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.id')
    ACCESS_CODE=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.identifier[] | select(.system | contains("AccessCode")) | .value')
    STATUS=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.status')
    
    if [ "$PRESCRIPTION_ID" == "null" ]; then
        print_error "Konnte Task nicht erstellen"
        echo "$RESPONSE" | jq '.'
        exit 1
    fi
    
    echo "✓ Task erstellt"
    echo "  - Prescription ID: $PRESCRIPTION_ID"
    echo "  - Access Code: ${ACCESS_CODE:0:16}..."
    echo "  - Status: $STATUS"
    
    # Speichere für nächste Schritte
    echo "$PRESCRIPTION_ID" > .prescription_id
    echo "$ACCESS_CODE" > .access_code
}

# Schritt 2: Activate Operation
activate_task() {
    print_step "Schritt 2: ACTIVATE - Aktiviere E-Rezept"
    
    PRESCRIPTION_ID=$(cat .prescription_id)
    ACCESS_CODE=$(cat .access_code)
    
    get_access_token "SMCB_KRANKENHAUS"
    
    # Erstelle signiertes Bundle (normalerweise über Fachdiensttool)
    # Für Tests verwenden wir ein Mock-Bundle
    SIGNED_BUNDLE="MIAGCSqGSIb3DQEHAqCAMIACAQExDzANBglghkgBZQMEAgEFADCABgkqhkiG9w0BBwGggCSABIAwggQ..."
    
    # Erstelle Request
    cat > /tmp/activate-request.json <<EOF
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "ePrescription",
      "resource": {
        "resourceType": "Binary",
        "contentType": "application/pkcs7-mime",
        "data": "$SIGNED_BUNDLE"
      }
    }
  ]
}
EOF
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/Task/$PRESCRIPTION_ID/\$activate" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "X-AccessCode: $ACCESS_CODE" \
        -H "Content-Type: application/fhir+json" \
        -d @/tmp/activate-request.json)
    
    STATUS=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.status')
    KVNR=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.for.identifier.value')
    EXPIRY=$(echo "$RESPONSE" | jq -r '.parameter[0].resource.restriction.period.end')
    
    if [ "$STATUS" != "ready" ]; then
        print_error "Task konnte nicht aktiviert werden"
        echo "$RESPONSE" | jq '.'
        exit 1
    fi
    
    echo "✓ Task aktiviert"
    echo "  - Status: $STATUS"
    echo "  - Patient KVNR: $KVNR"
    echo "  - Gültig bis: $EXPIRY"
}

# Schritt 3: Accept Operation
accept_task() {
    print_step "Schritt 3: ACCEPT - Nehme E-Rezept an"
    
    PRESCRIPTION_ID=$(cat .prescription_id)
    ACCESS_CODE=$(cat .access_code)
    
    get_access_token "SMCB_APOTHEKE"
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/Task/$PRESCRIPTION_ID/\$accept?ac=$ACCESS_CODE" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/fhir+json")
    
    # Bundle Response verarbeiten
    STATUS=$(echo "$RESPONSE" | jq -r '.entry[0].resource.status')
    SECRET=$(echo "$RESPONSE" | jq -r '.entry[0].resource.identifier[] | select(.system | contains("Secret")) | .value')
    OWNER=$(echo "$RESPONSE" | jq -r '.entry[0].resource.owner.display')
    
    if [ "$STATUS" != "in-progress" ]; then
        print_error "Task konnte nicht angenommen werden"
        echo "$RESPONSE" | jq '.'
        exit 1
    fi
    
    echo "✓ Task angenommen"
    echo "  - Status: $STATUS"
    echo "  - Secret: ${SECRET:0:16}..."
    echo "  - Owner: $OWNER"
    
    # Speichere Secret für Close
    echo "$SECRET" > .secret
}

# Schritt 4: Close Operation
close_task() {
    print_step "Schritt 4: CLOSE - Schließe E-Rezept ab"
    
    PRESCRIPTION_ID=$(cat .prescription_id)
    SECRET=$(cat .secret)
    
    get_access_token "SMCB_APOTHEKE"
    
    # Erstelle Close Request mit MedicationDispense
    cat > /tmp/close-request.json <<EOF
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "secret",
      "valueString": "$SECRET"
    },
    {
      "name": "rxDispensation",
      "resource": {
        "resourceType": "Parameters",
        "parameter": [
          {
            "name": "medicationDispense",
            "resource": {
              "resourceType": "MedicationDispense",
              "meta": {
                "profile": [
                  "https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense"
                ]
              },
              "status": "completed",
              "subject": {
                "identifier": {
                  "system": "http://fhir.de/sid/gkv/kvid-10",
                  "value": "S040464113"
                }
              },
              "whenHandedOver": "$(date -u +"%Y-%m-%dT%H:%M:%S+00:00")",
              "medicationCodeableConcept": {
                "coding": [
                  {
                    "system": "http://fhir.de/CodeSystem/ifa/pzn",
                    "code": "06313728",
                    "display": "Sumatriptan-1A Pharma 100 mg Tabletten"
                  }
                ]
              },
              "quantity": {
                "value": 1,
                "unit": "St",
                "system": "http://unitsofmeasure.org",
                "code": "{tbl}"
              }
            }
          }
        ]
      }
    }
  ]
}
EOF
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/Task/$PRESCRIPTION_ID/\$close" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/fhir+json" \
        -d @/tmp/close-request.json)
    
    # Bundle Response verarbeiten
    BUNDLE_TYPE=$(echo "$RESPONSE" | jq -r '.type')
    COMPOSITION_STATUS=$(echo "$RESPONSE" | jq -r '.entry[0].resource.status')
    
    if [ "$BUNDLE_TYPE" != "document" ]; then
        print_error "Task konnte nicht geschlossen werden"
        echo "$RESPONSE" | jq '.'
        exit 1
    fi
    
    echo "✓ Task geschlossen"
    echo "  - Bundle Type: $BUNDLE_TYPE"
    echo "  - Composition Status: $COMPOSITION_STATUS"
    
    # Prüfe finalen Task-Status
    sleep 2
    FINAL_TASK=$(curl -s -X GET "$BASE_URL/Task/$PRESCRIPTION_ID" \
        -H "Authorization: Bearer $ACCESS_TOKEN")
    
    FINAL_STATUS=$(echo "$FINAL_TASK" | jq -r '.status')
    echo "  - Task Status: $FINAL_STATUS"
}

# Cleanup
cleanup() {
    rm -f .prescription_id .access_code .secret /tmp/activate-request.json /tmp/close-request.json
}

# Main
main() {
    echo -e "${GREEN}E-Rezept Workflow - Kompletter Durchlauf${NC}"
    echo ""
    
    # Prüfe Voraussetzungen
    check_prerequisites
    
    # Führe Workflow aus
    create_task
    sleep 1
    
    activate_task
    sleep 1
    
    accept_task
    sleep 1
    
    close_task
    
    # Cleanup
    cleanup
    
    echo ""
    print_step "✓ Workflow erfolgreich abgeschlossen!"
}

# Trap für Cleanup bei Fehler
trap cleanup EXIT

# Starte Hauptprogramm
main