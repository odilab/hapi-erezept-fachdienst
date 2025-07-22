# NPM Package Migration Analysis

## Overview
This document analyzes all locally loaded FHIR resources in the HAPI E-Rezept Fachdienst and provides recommendations for NPM package migration.

## 1. Resources from `/gematik-erg-resources(new)`

### Publisher: gematik GmbH
All resources in this directory are published by gematik GmbH and appear to be part of the E-Rechnungs-Gateway (ERG) specification.

**Resource Types:**
- **StructureDefinitions (23)**: ERG profiles for invoicing, documentation, and patient data
- **CodeSystems (16)**: ERG-specific code systems for invoice types, statuses, etc.
- **ValueSets (17)**: Value sets corresponding to the code systems
- **OperationDefinitions (5)**: ERG operations (Submit, Retrieve, ChangeStatus, etc.)
- **Examples**: Various example resources

**Status**: These are NOT in any NPM package and should be packaged as `de.gematik.erg`

## 2. Resources from `/package/` Directories (excluding NPM packages)

### 2.1 KBV E-Rezept Resources (`/erezept/`)
**Publisher**: Kassenärztliche Bundesvereinigung (KBV)  
**Version**: 1.4.0  
**Content**:
- StructureDefinitions for prescriptions, medications, dosages
- CodeSystems and ValueSets for prescription-specific codes
- Extension definitions
- Example resources

**Status**: Already available as NPM package `kbv.ita.erp-1.7.0.tgz` (newer version)

### 2.2 ABDA E-Rezept Abgabedaten (`/erezeptabgabedaten/`)
**Publisher**: Deutscher Apothekerverband e.V. (DAV)  
**Version**: 1.5.0  
**Content**:
- Profiles for pharmacy dispensing data
- Extensions for charge information

**Status**: Already available as NPM package `de.abda.erezeptabgabedaten-1.5.0.tgz`

### 2.3 ABDA E-Rezept Abgabedaten PKV (`/erezeptabgabedatenpkv/`)
**Publisher**: Deutscher Apothekerverband e.V. (DAV)  
**Content**:
- PKV-specific profiles and code systems
- Banking and billing extensions

**Status**: NOT in NPM package, should be part of ABDA package

### 2.4 GKVSV E-Rezept Abrechnungsdaten (`/erezeptabrechnungsdaten/`)
**Publisher**: GKV-Spitzenverband (inferred)  
**Content**:
- TA7 billing profiles
- Import and export specifications
- Billing-specific code systems

**Status**: NOT in NPM package, needs packaging as `de.gkvsv.erezeptabrechnungsdaten`

### 2.5 KBV EVDGA (`/evdga/`)
**Publisher**: Kassenärztliche Bundesvereinigung (KBV)  
**Version**: 1.2.0  
**Content**:
- EVDGA Bundle and Composition profiles
- Health app request profiles

**Status**: Already available as NPM package `kbv.itv.evdga-1.2.1.tgz` (newer version)

### 2.6 Gematik Charge Resources (`/Resources*/`)
Multiple copies of the same resources in different "Resources" folders:
**Publisher**: gematik GmbH  
**Version**: 1.1.0  
**Content**:
- ChargeItem profiles
- Communication profiles for charge changes
- Consent profiles

**Status**: NOT in NPM package, should be packaged as `de.gematik.erpchrg`

### 2.7 Gematik ERP Resources (`/Resources*/`)
**Publisher**: gematik GmbH  
**Content**:
- Task, MedicationDispense, Communication profiles
- EU-specific profiles
- Operation definitions

**Status**: Partially available in `de.gematik.erezept-workflow.r4` packages

### 2.8 KBV Darreichungsform (`/KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15*/`)
**Publisher**: Kassenärztliche Bundesvereinigung (KBV)  
**Version**: 1.15  
**Content**:
- CodeSystem and ValueSet for dosage forms

**Status**: Should be part of KBV Basis package

### 2.9 KBV DMP (`/KBV_CS_SFHIR_KBV_DMP_V1.06 (1)/`)
**Publisher**: Kassenärztliche Bundesvereinigung (KBV)  
**Version**: 1.06  
**Content**:
- DMP-related code systems and value sets

**Status**: Should be part of KBV Basis package

## 3. NPM Packages Already Loaded

### Currently loaded as NPM packages:
1. `de.basisprofil.r4-1.5.3.tgz` - German base profiles
2. `de.ihe-d.terminology-3.0.1.tgz` - IHE Germany terminology
3. `dvmd.kdl.r4-2024.0.0.tgz` - DVMD catalog
4. `kbv.ita.erp-1.7.0.tgz` - KBV E-Rezept (newer version)
5. `kbv.basis-1.7.0.tgz` - KBV base profiles
6. `kbv.ita.for-1.2.0.tgz` - KBV Formular
7. `kbv.itv.evdga-1.2.1.tgz` - KBV EVDGA
8. `de.abda.erezeptabgabedaten-1.5.0.tgz` - ABDA dispensing data
9. `de.gematik.erezept-workflow.r4-1.5.2.tgz` - Gematik workflow

## 4. Recommendations for NPM Package Creation

### High Priority (Not in any NPM package):
1. **`de.gematik.erg`** - All gematik ERG resources from `/gematik-erg-resources(new)`
2. **`de.gkvsv.erezeptabrechnungsdaten`** - GKVSV billing data from `/erezeptabrechnungsdaten/`
3. **`de.gematik.erpchrg`** - Gematik charge resources from `/Resources*/`

### Medium Priority (Should be consolidated):
1. **ABDA PKV resources** - Should be added to existing ABDA package or created as separate `de.abda.erezeptabgabedaten.pkv`
2. **KBV Darreichungsform & DMP** - Should be added to `kbv.basis` package

### Low Priority (Already in NPM but locally duplicated):
1. Remove local `/erezept/` folder - use `kbv.ita.erp-1.7.0.tgz`
2. Remove local `/evdga/` folder - use `kbv.itv.evdga-1.2.1.tgz`
3. Remove local `/erezeptabgabedaten/` folder - use `de.abda.erezeptabgabedaten-1.5.0.tgz`
4. Clean up duplicate Resources folders (Resources 2-9)

## 5. Action Items

### Immediate Actions:
1. Package gematik ERG resources as NPM package
2. Package GKVSV billing resources as NPM package
3. Package gematik charge resources as NPM package
4. Remove duplicate local copies of resources already in NPM packages

### Code Changes Required:
1. Update `CustomValidator.java` to remove `loadAllPackageResources()` method
2. Replace with NPM package loading for new packages
3. Remove duplicate NPM package loads (e.g., multiple versions of same package)

### Cleanup Required:
1. Delete duplicate "Resources" folders (keep only one)
2. Remove .zip files (use only .tgz NPM packages)
3. Organize remaining local resources that cannot be packaged