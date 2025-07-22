# Detailed NPM Migration List

## Resources to Package as `de.gematik.erg`

All files from `/src/main/resources/gematik-erg-resources(new)/`:

### StructureDefinitions (23 files):
- StructureDefinition-ERGAbrechnungsDiagnoseProzedur.json
- StructureDefinition-ERGAbrechnungsDiagnoseProzedurFreitext.json
- StructureDefinition-ERGBemaPunktsumme.json
- StructureDefinition-ERGRechnungspositionBehandlungsdatum.json
- StructureDefinition-ERGRechnungspositionZusatz.json
- StructureDefinition-ERGTeilsumme.json
- StructureDefinition-ERGTokenStornierteRechnung.json
- StructureDefinition-ERGZusatzinformationZurAbrechnungsart.json
- StructureDefinition-InvoicePeriod.json
- StructureDefinition-erg-abzug-kassenanteil.json
- StructureDefinition-erg-behandlungsart.json
- StructureDefinition-erg-docref-fachrichtung.json
- StructureDefinition-erg-docref-leistungsart.json
- StructureDefinition-erg-docref-signature.json
- StructureDefinition-erg-documentreference-gesamtbetrag.json
- StructureDefinition-erg-documentreference-markierung.json
- StructureDefinition-erg-documentreference-rechnungsdatum.json
- StructureDefinition-erg-documentreference-zahlungszieldatum.json
- StructureDefinition-erg-dokumentenmetadaten.json
- StructureDefinition-erg-fachrichtung.json
- StructureDefinition-erg-institution.json
- StructureDefinition-erg-nutzungsprotokoll.json
- StructureDefinition-erg-patient.json
- StructureDefinition-erg-person.json
- StructureDefinition-erg-rechnung.json
- StructureDefinition-erg-rechnungsdiagnose.json
- StructureDefinition-erg-rechnungsdokument.json
- StructureDefinition-erg-rechnungsposition-go-angaben.json
- StructureDefinition-erg-rechnungsposition-type.json
- StructureDefinition-erg-rechnungsposition.json
- StructureDefinition-erg-rechnungsprozedur.json
- StructureDefinition-erg-task-requestedPerformer.json
- StructureDefinition-erg-wegegeld-reiseentschaedigung.json
- StructureDefinition-erg-zahlungsziel.json
- StructureDefinition-erg-zahnregion.json

### CodeSystems (16 files):
- CodeSystem-erg-abrechnungs-diagnose-use-CS.json
- CodeSystem-erg-attachment-format-cs.json
- CodeSystem-erg-chargeitem-type-CS.json
- CodeSystem-erg-dokument-artderarchivierung-cs.json
- CodeSystem-erg-operationen-cs.json
- CodeSystem-erg-participant-role-CS.json
- CodeSystem-erg-prop-rest-interactions-cs.json
- CodeSystem-erg-rechnung-abrechnungsart-cs.json
- CodeSystem-erg-rechnung-identifier-type-cs.json
- CodeSystem-erg-rechnung-markierung-cs.json
- CodeSystem-erg-rechnung-submit-modus-cs.json
- CodeSystem-erg-rechnung-type-cs.json
- CodeSystem-erg-rechnungsart-cs.json
- CodeSystem-erg-rechnungsposition-faktor-gruende-CS.json
- CodeSystem-erg-rechnungsposition-zusatz-CS.json
- CodeSystem-erg-rechnungsstatus-cs.json
- CodeSystem-erg-total-price-component-type-cs.json

### ValueSets (17 files):
- ValueSet-ERGVerkehrsmittel.json
- ValueSet-TESTICD10GM.json
- ValueSet-erg-abrechnungs-diagnose-use-VS.json
- ValueSet-erg-audit-event-agent-type-vs.json
- ValueSet-erg-audit-event-sub-type-vs.json
- ValueSet-erg-audit-event-type-vs.json
- ValueSet-erg-chargeitem-type-VS.json
- ValueSet-erg-dokument-artderarchivierung-vs.json
- ValueSet-erg-participant-role-VS.json
- ValueSet-erg-rechnung-abrechnungsart-vs.json
- ValueSet-erg-rechnung-behandlungsart-vs.json
- ValueSet-erg-rechnung-markierung-vs.json
- ValueSet-erg-rechnung-submit-modus-vs.json
- ValueSet-erg-rechnungsart-vs.json
- ValueSet-erg-rechnungsposition-faktor-gruende-auspraegungen-VS.json
- ValueSet-erg-rechnungsposition-zusatz-VS.json
- ValueSet-erg-rechnungsstatus-vs.json
- ValueSet-erg-restricted-mime-types-vs.json
- ValueSet-erg-sonstigesdokument-type-vs.json
- ValueSet-erg-total-price-component-deduction-type-vs.json

### OperationDefinitions (5 files):
- OperationDefinition-ERGOperationChangeStatus.json
- OperationDefinition-ERGOperationErase.json
- OperationDefinition-ERGOperationProcessFlag.json
- OperationDefinition-ERGOperationRetrieve.json
- OperationDefinition-ERGOperationSubmit.json

### Other Resources:
- CapabilityStatement-CapabilityStatementFD.json
- SearchParameter-erg-makierung.json
- Questionnaire-QuestionnaireInvoice.json

## Resources to Package as `de.gkvsv.erezeptabrechnungsdaten`

All files from `/src/main/resources/package/erezeptabrechnungsdaten/`:
- GKVSV_CS_ERP_Import.json
- GKVSV_CS_ERP_Leistungserbringer_Sitz.json
- GKVSV_CS_ERP_Leistungserbringertyp.json
- GKVSV_CS_ERP_Positionstyp.json
- GKVSV_CS_ERP_Rechnungsart.json
- GKVSV_CS_ERP_TA7.json
- GKVSV_CS_ERP_Verwurf.json
- GKVSV_CS_ERP_ZuAbschlagKey.json
- GKVSV_EX_ERP_Import.json
- GKVSV_EX_ERP_Import_PZN.json
- GKVSV_EX_ERP_Irrlaeufer.json
- GKVSV_EX_ERP_LE_Sitz.json
- GKVSV_EX_ERP_Positionstyp.json
- GKVSV_EX_ERP_TA7_Dateinummer.json
- GKVSV_EX_ERP_TA7_Rechnungsdatum.json
- GKVSV_EX_ERP_VAT_VALUE.json
- GKVSV_EX_ERP_ZusatzdatenHerstellung.json
- GKVSV_EX_TA7_Dateistatus.json
- GKVSV_EX_TA7_IK_Empfaenger.json
- GKVSV_EX_TA7_IK_Kostentraeger.json
- GKVSV_NS_Belegnummer.json
- GKVSV_NS_Dateiname.json
- GKVSV_NS_Rechnungsnummer.json
- GKVSV_PR_Binary.json
- GKVSV_PR_ERP_eAbrechnungsdaten.json
- GKVSV_PR_TA7_Rechnung_Bundle.json
- GKVSV_PR_TA7_Rechnung_Composition.json
- GKVSV_PR_TA7_Rechnung_List.json
- GKVSV_PR_TA7_RezeptBundle.json
- GKVSV_VS_ERP_Import.json
- GKVSV_VS_ERP_Leistungserbringer_Sitz.json
- GKVSV_VS_ERP_Leistungserbringertyp.json
- GKVSV_VS_ERP_Positionstyp.json
- GKVSV_VS_ERP_Rechnungsart.json
- GKVSV_VS_ERP_Verwurf.json
- GKVSV_VS_ERP_ZuAbschlagKey.json

## Resources to Package as `de.gematik.erpchrg`

From `/src/main/resources/package/Resources/fsh-generated/resources/`:
- CodeSystem-GEM-ERPCHRG-CS-ConsentType.json
- StructureDefinition-GEM-ERPCHRG-EX-MarkingFlag.json
- StructureDefinition-GEM-ERPCHRG-PR-ChargeItem.json
- StructureDefinition-GEM-ERPCHRG-PR-Communication-ChargChangeReply.json
- StructureDefinition-GEM-ERPCHRG-PR-Communication-ChargChangeReq.json
- StructureDefinition-GEM-ERPCHRG-PR-Consent.json
- StructureDefinition-GEM-ERPCHRG-PR-PAR-Patch-ChargeItem-Input.json
- ValueSet-GEM-ERPCHRG-VS-ConsentType.json

## Resources to Add to `de.abda.erezeptabgabedaten.pkv`

All files from `/src/main/resources/package/erezeptabgabedatenpkv/`:
- CodeSystem-DAV-PKV-CS-ERP-AbrechnungsTyp.json
- CodeSystem-DAV-PKV-CS-ERP-ArtRezeptaenderung.json
- CodeSystem-DAV-PKV-CS-ERP-KostenVersicherterKategorie.json
- CodeSystem-DAV-PKV-CS-ERP-ZusatzattributSchluesselAutidemAustausch.json
- CodeSystem-DAV-PKV-CS-ERP-ZusatzdatenEinheitFaktorkennzeichen.json
- Extension-DAV-PKV-EX-ERP-AbrechnungsTyp.xml
- Extension-DAV-PKV-EX-ERP-Bankverbindung.xml
- Profile-DAV-PKV-PR-ERP-AbgabedatenBundle.xml
- Profile-DAV-PKV-PR-ERP-AbgabedatenComposition.xml
- Profile-DAV-PKV-PR-ERP-Abgabeinformationen.xml
- Profile-DAV-PKV-PR-ERP-Abrechnungszeilen.xml
- Profile-DAV-PKV-PR-ERP-Apotheke.xml
- Profile-DAV-PKV-PR-ERP-ZusatzdatenEinheit.xml
- Profile-DAV-PKV-PR-ERP-ZusatzdatenHerstellung.xml
- ValueSet-DAV-PKV-VS-ERP-AbrechnungsTyp.json
- ValueSet-DAV-PKV-VS-ERP-ArtRezeptaenderung.json
- ValueSet-DAV-PKV-VS-ERP-KostenVersicherterKategorie.json
- ValueSet-DAV-PKV-VS-ERP-ZusatzattributSchluesselAutidemAustausch.json
- ValueSet-DAV-PKV-VS-ERP-ZusatzdatenEinheitFaktorkennzeichen.json

## Resources to Add to `kbv.basis` or Create Separate Package

### KBV Darreichungsform:
From `/src/main/resources/package/KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15/`:
- KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15.xml
- KBV_VS_SFHIR_KBV_DARREICHUNGSFORM_V1.15.xml

### KBV DMP:
From `/src/main/resources/package/KBV_CS_SFHIR_KBV_DMP_V1.06 (1)/`:
- KBV_CS_SFHIR_KBV_DMP_V1.06.xml
- KBV_VS_SFHIR_KBV_DMP_V1.06.xml

## Local Resources to Remove (Already in NPM Packages)

1. **Delete** `/src/main/resources/package/erezept/` - Already in `kbv.ita.erp-1.7.0.tgz`
2. **Delete** `/src/main/resources/package/evdga/` - Already in `kbv.itv.evdga-1.2.1.tgz`
3. **Delete** `/src/main/resources/package/erezeptabgabedaten/` - Already in `de.abda.erezeptabgabedaten-1.5.0.tgz`
4. **Delete** duplicate Resource folders:
   - `/src/main/resources/package/Resources 2/`
   - `/src/main/resources/package/Resources 3/`
   - `/src/main/resources/package/Resources 4/`
   - `/src/main/resources/package/Resources 5/`
   - `/src/main/resources/package/Resources 6/`
   - `/src/main/resources/package/Resources 7/`
   - `/src/main/resources/package/Resources 8/`
   - `/src/main/resources/package/Resources 9/`
5. **Delete** all `.zip` files - use only `.tgz` NPM packages

## CustomValidator.java Changes Required

1. Remove the `loadAllPackageResources()` method call (line 55)
2. Add NPM package loading for new packages:
   ```java
   npmPackageSupport.loadPackageFromClasspath("classpath:package/de.gematik.erg-1.0.0.tgz");
   npmPackageSupport.loadPackageFromClasspath("classpath:package/de.gkvsv.erezeptabrechnungsdaten-1.0.0.tgz");
   npmPackageSupport.loadPackageFromClasspath("classpath:package/de.gematik.erpchrg-1.1.0.tgz");
   ```
3. Remove duplicate package loads (keep only latest versions)
4. Consider removing `loadAllResources()` if ERG resources are packaged