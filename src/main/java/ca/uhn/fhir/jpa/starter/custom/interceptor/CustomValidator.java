package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.BeanCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.List;
import java.io.InputStream;
import java.nio.file.*;
import java.net.URI;
import java.util.Collections;
import ca.uhn.fhir.context.support.IValidationSupport;
import java.util.Arrays;


@Component
@Interceptor
public class CustomValidator {
    private static final Logger logger = LoggerFactory.getLogger(CustomValidator.class);
    private final FhirValidator validator;
    private final ValidationSupportChain validationSupportChain;
    private final PrePopulatedValidationSupport prePopulatedSupport;
    private final FhirContext ctx;

    public CustomValidator(FhirContext ctx) {
        this.ctx = ctx;
        logger.info("CustomValidator wird initialisiert...");
        try {
            // PrePopulatedValidationSupport für lokale Ressourcen erstellen und im Feld speichern
            this.prePopulatedSupport = new PrePopulatedValidationSupport(ctx);
            
            // Alle lokalen Ressourcen aus dem resources-Verzeichnis laden
            loadAllResources(this.prePopulatedSupport);
            
            // KBV Darreichungsform und DMP manuell laden (nicht als NPM verfügbar)
            loadKbvDarreichungsformResources(this.prePopulatedSupport);
            loadKbvDmpResources(this.prePopulatedSupport);
            
            // ABDA 1.4.0 manuell laden (nicht als NPM verfügbar)
            loadAbdaErezeptabgabedatenResources(this.prePopulatedSupport);
            
            // NPM Package Support erstellen und .tgz Packages laden
            NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
            
            // Bestehende Packages aus dem Hauptverzeichnis
            npmPackageSupport.loadPackageFromClasspath("classpath:package/de.basisprofil.r4-1.5.3.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/de.ihe-d.terminology-3.0.1.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/dvmd.kdl.r4-2024.0.0.tgz");
            
            // Neues Basisprofil mit EKG-Observation
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.basisprofil.r4-1.5.4.tgz");
            
            // NPM Packages aus dem npm packages Verzeichnis
            // KBV Packages
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/kbv.basis-1.7.0.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/kbv.ita.for-1.2.0.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/kbv.ita.erp-1.4.0-alpha.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/kbv.itv.evdga-1.2.1.tgz");
            
            // ABDA Packages
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.abda.erezeptabgabedaten-1.4.1-rc.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.abda.erezeptabgabedaten-1.5.0.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.abda.erezeptabgabedatenbasis-1.4.2.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.abda.erezeptabgabedatenbasis-1.5.0.tgz");
            
            // Gematik Packages
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gematik.erezept-workflow.r4-1.5.2.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gematik.erezept-patientenrechnung.r4-1.1.0.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gematik.erezept.eu-1.0.0.tgz");
            
            // GKVSV Package
            npmPackageSupport.loadPackageFromClasspath("classpath:package/npm packages/de.gkvsv.erezeptabrechnungsdaten-1.4.2 (1).tgz");
            
            logger.info("NPM Packages geladen - inklusive KBV FOR, ERP, EVDGA, ABDA und Gematik Packages");
            
            // Erstelle flexiblen ValidationSupport für Versionskompatibilität
            IValidationSupport flexibleVersionSupport = new IValidationSupport() {
                @Override
                public FhirContext getFhirContext() {
                    return ctx;
                }
                
                @Override
                public IBaseResource fetchStructureDefinition(String url) {
                    // Wenn keine Version angegeben, delegiere direkt
                    if (!url.contains("|")) {
                        return null;
                    }
                    
                    // Extrahiere URL und Version
                    String baseUrl = url.substring(0, url.indexOf("|"));
                    String requestedVersion = url.substring(url.indexOf("|") + 1);
                    
                    logger.debug("Suche nach kompatiblem Profil für: {} Version: {}", baseUrl, requestedVersion);
                    
                    // Durchsuche alle verfügbaren StructureDefinitions
                    for (IValidationSupport support : Arrays.asList(npmPackageSupport, prePopulatedSupport)) {
                        List<IBaseResource> allSds = support.fetchAllStructureDefinitions();
                        if (allSds != null) {
                            for (IBaseResource resource : allSds) {
                                if (resource instanceof StructureDefinition) {
                                    StructureDefinition sd = (StructureDefinition) resource;
                                    if (baseUrl.equals(sd.getUrl()) && sd.getVersion() != null) {
                                        if (isVersionCompatible(requestedVersion, sd.getVersion())) {
                                            logger.debug("Gefunden: {} Version {} (angefragt war {})", 
                                                sd.getUrl(), sd.getVersion(), requestedVersion);
                                            return sd;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    return null;
                }
                
                private boolean isVersionCompatible(String requested, String available) {
                    // Exakte Übereinstimmung
                    if (requested.equals(available)) {
                        return true;
                    }
                    
                    // Zerteile Versionen in Komponenten
                    String[] requestedParts = requested.split("\\.");
                    String[] availableParts = available.split("\\.");
                    
                    // Prüfe Major und Minor Version
                    if (requestedParts.length >= 2 && availableParts.length >= 2) {
                        boolean majorMatch = requestedParts[0].equals(availableParts[0]);
                        boolean minorMatch = requestedParts[1].equals(availableParts[1]);
                        
                        if (majorMatch && minorMatch) {
                            // Bei gleicher Major.Minor Version:
                            // - "1.4" akzeptiert "1.4.0", "1.4.1", etc.
                            // - "1.4.0" akzeptiert auch "1.4"
                            if (requestedParts.length == 2 && availableParts.length >= 2) {
                                // Anfrage "1.4" akzeptiert jede "1.4.x"
                                return true;
                            } else if (requestedParts.length == 3 && availableParts.length == 2) {
                                // Anfrage "1.4.0" akzeptiert auch "1.4"
                                return requestedParts[2].equals("0");
                            } else if (requestedParts.length == 3 && availableParts.length == 3) {
                                // Bei vollständigen Versionen: Patch kann unterschiedlich sein
                                // "1.4.0" akzeptiert "1.4.1" usw.
                                return true;
                            }
                        }
                    }
                    
                    // Spezialfall: "1.2" und "1.2.0" sind kompatibel
                    if (requestedParts.length == 2 && availableParts.length == 3) {
                        return requestedParts[0].equals(availableParts[0]) && 
                               requestedParts[1].equals(availableParts[1]);
                    }
                    if (requestedParts.length == 3 && availableParts.length == 2) {
                        return requestedParts[0].equals(availableParts[0]) && 
                               requestedParts[1].equals(availableParts[1]) &&
                               requestedParts[2].equals("0");
                    }
                    
                    return false;
                }
            };
            
            // Validation Support Chain erstellen (mit flexiblem Version Support zuerst)
            this.validationSupportChain = new ValidationSupportChain(
                flexibleVersionSupport,  // Zuerst flexible Versionsauflösung
                npmPackageSupport,
                this.prePopulatedSupport,
                new DefaultProfileValidationSupport(ctx),
                new CommonCodeSystemsTerminologyService(ctx),
                new InMemoryTerminologyServerValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx)
            );
            logger.info("Validation Support Chain mit NPM Packages und flexibler Versionskompatibilität erstellt");

            // Validator mit Caching erstellen
            this.validator = ctx.newValidator();
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator(this.validationSupportChain);
            instanceValidator.setNoTerminologyChecks(false);
            instanceValidator.setErrorForUnknownProfiles(true);
            // Wichtig: Deaktiviere strenge Versionsüberprüfung
            instanceValidator.setAnyExtensionsAllowed(true);
            instanceValidator.setBestPracticeWarningLevel(org.hl7.fhir.r5.utils.validation.constants.BestPracticeWarningLevel.Ignore);
            validator.registerValidatorModule(instanceValidator);
            logger.info("Validator erfolgreich konfiguriert mit flexibler Versionsbehandlung");
        } catch (IOException e) {
            logger.error("Fehler beim Laden der FHIR-Packages", e);
            throw new BeanCreationException("Fehler beim Laden der FHIR-Packages", e);
        }
    }

    @PostConstruct
    public void init() {
        logger.info("CustomValidator wurde erfolgreich initialisiert und ist bereit für Validierungen");
        logger.info("Verwendeter FHIR-Kontext: {}", ctx.getVersion().getVersion());
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_CREATED)
    public void validateResourceCreate(IBaseResource resource) {
        logger.error("====== HOOK CALLED: STORAGE_PRECOMMIT_RESOURCE_CREATED for {} ======", resource.fhirType());
        validateAndThrowIfInvalid(resource);
		  //validator.validateWithResult(resource);
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_UPDATED)
    public void validateResourceUpdate(IBaseResource resource) {
        validateAndThrowIfInvalid(resource);
    }

    public void validateAndThrowIfInvalid(IBaseResource resource) {
        logger.debug("Validiere Resource vom Typ: {}", resource.getClass().getSimpleName());
        
        // Bei Bundle-Ressourcen: XML neu parsen wenn Kommentare problematisch sein könnten
        if (resource instanceof Bundle) {
            resource = preprocessBundleForValidation(resource);
        }
        
        ValidationResult validationResult = validator.validateWithResult(resource);
        
        // Nur Nachrichten mit Severity ERROR oder FATAL sammeln
        List<SingleValidationMessage> errors = validationResult.getMessages().stream()
            .filter(m -> m.getSeverity() == ResultSeverityEnum.ERROR || 
                        m.getSeverity() == ResultSeverityEnum.FATAL)
            .collect(Collectors.toList());
            
        // Nur eine Exception werfen, wenn Fehler oder fatale Fehler vorhanden sind
        if (!errors.isEmpty()) {
            String errorMessage = errors.stream()
                .map(single -> single.getLocationString() + ": " + single.getMessage() + " [" + single.getSeverity() + "]")
                .collect(Collectors.joining("\n"));
                
            logger.error("Validierungsfehler gefunden: \n{}", errorMessage);
            
            // Erstelle OperationOutcome nur mit den Fehlern
            OperationOutcome operationOutcome = new OperationOutcome();
            errors.forEach(message -> {
                OperationOutcome.IssueSeverity severity = OperationOutcome.IssueSeverity.NULL;
                if (message.getSeverity() == ResultSeverityEnum.ERROR) {
                    severity = OperationOutcome.IssueSeverity.ERROR;
                } else if (message.getSeverity() == ResultSeverityEnum.FATAL) {
                    severity = OperationOutcome.IssueSeverity.FATAL;
                }
                operationOutcome.addIssue()
                    .setSeverity(severity)
                    .setCode(OperationOutcome.IssueType.INVALID)
                    .setDiagnostics(message.getLocationString() + ": " + message.getMessage());
            });
            
            throw new UnprocessableEntityException("Validierungsfehler: " + errorMessage, operationOutcome);
        }

        // Logge Warnungen und Informationen, wenn vorhanden
        List<SingleValidationMessage> warningsOrInfo = validationResult.getMessages().stream()
            .filter(m -> m.getSeverity() == ResultSeverityEnum.WARNING ||
                        m.getSeverity() == ResultSeverityEnum.INFORMATION)
            .collect(Collectors.toList());
        if (!warningsOrInfo.isEmpty()) {
            String warningMessage = warningsOrInfo.stream()
                .map(single -> single.getLocationString() + ": " + single.getMessage() + " [" + single.getSeverity() + "]")
                .collect(Collectors.joining("\n"));
            logger.warn("Validierungswarnungen/-informationen gefunden:\n{}", warningMessage);
        }
        
        logger.debug("Resource erfolgreich validiert (oder nur Warnungen/Informationen gefunden)");
    }

    /**
     * Führt die Validierung durch und gibt das vollständige Ergebnis zurück.
     *
     * @param resource Die zu validierende Ressource.
     * @return Das ValidationResult mit allen Meldungen (FATAL, ERROR, WARNING, INFORMATION).
     */
    public ValidationResult validateAndReturnResult(IBaseResource resource) {
        logger.debug("Führe Validierung durch und gebe Ergebnis zurück für Ressource vom Typ: {}", resource.getClass().getSimpleName());
        ValidationResult validationResult = validator.validateWithResult(resource);
        // Logging der Ergebnisse kann hier optional wiederholt oder angepasst werden
        if (validationResult.isSuccessful()) {
            logger.debug("Validierung erfolgreich (keine Fehler oder Fatals). Anzahl Meldungen: {}", validationResult.getMessages().size());
        } else {
            logger.warn("Validierung nicht erfolgreich (Fehler oder Fatals gefunden). Anzahl Meldungen: {}", validationResult.getMessages().size());
        }
        return validationResult;
    }

    public FhirValidator getValidator() {
        logger.debug("Validator wird abgerufen");
        return validator;
    }

    public ValidationSupportChain getValidationSupportChain() {
        logger.debug("Validation Support Chain wird abgerufen");
        return validationSupportChain;
    }

    public PrePopulatedValidationSupport getPrePopulatedSupport() {
        logger.debug("PrePopulatedValidationSupport wird abgerufen");
        return prePopulatedSupport;
    }

    // Hilfsmethode zum Laden von Ressourcen
    private String loadResourceAsString(String path) throws IOException {
        try (var inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Resource nicht gefunden: " + path);
            }
            return new String(inputStream.readAllBytes());
        }
    }

    // Hilfsmethode zum Laden aller Ressourcen aus dem gematik-erg-resources Verzeichnis
    private void loadAllResources(PrePopulatedValidationSupport prePopulatedSupport) throws IOException {
        try (var stream = getClass().getResourceAsStream("/gematik-erg-resources(new)")) {
            if (stream == null) {
                logger.warn("Verzeichnis /gematik-erg-resources(new) nicht gefunden");
                return;
            }
            
            var bufferedReader = new java.io.BufferedReader(new java.io.InputStreamReader(stream));
            String fileName;
            while ((fileName = bufferedReader.readLine()) != null) {
                if (fileName.endsWith(".json")) {
                    try {
                        String resourceContent = loadResourceAsString("/gematik-erg-resources(new)/" + fileName);
                        IBaseResource resource = ctx.newJsonParser().parseResource(resourceContent);
                        
                        if (resource instanceof StructureDefinition) {
                            StructureDefinition sd = (StructureDefinition) resource;
                            prePopulatedSupport.addStructureDefinition(sd);
                            logger.info("StructureDefinition '{}' aus Datei '{}' geladen", sd.getUrl(), fileName);
                        } else if (resource instanceof ValueSet) {
                            ValueSet vs = (ValueSet) resource;
                            prePopulatedSupport.addValueSet(vs);
                            logger.info("ValueSet '{}' aus Datei '{}' geladen", vs.getUrl(), fileName);
                        } else if (resource instanceof CodeSystem) {
                            CodeSystem cs = (CodeSystem) resource;
                            prePopulatedSupport.addCodeSystem(cs);
                            logger.info("CodeSystem '{}' aus Datei '{}' geladen", cs.getUrl(), fileName);
                        }
                    } catch (Exception e) {
                        logger.error("Fehler beim Laden der Datei {}: {}", fileName, e.getMessage());
                    }
                }
            }
        }
    }

    // Hilfsmethode zum Laden von KBV Darreichungsform Ressourcen
    private void loadKbvDarreichungsformResources(PrePopulatedValidationSupport prePopulatedSupport) throws IOException {
        logger.info("Lade KBV Darreichungsform Ressourcen...");
        
        // Lade CodeSystem
        String csContent = loadResourceAsString("/package/KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15/KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15.xml");
        CodeSystem cs = (CodeSystem) ctx.newXmlParser().parseResource(csContent);
        prePopulatedSupport.addCodeSystem(cs);
        logger.info("KBV Darreichungsform CodeSystem geladen: {}", cs.getUrl());
        
        // Lade ValueSet
        String vsContent = loadResourceAsString("/package/KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.15/KBV_VS_SFHIR_KBV_DARREICHUNGSFORM_V1.15.xml");
        ValueSet vs = (ValueSet) ctx.newXmlParser().parseResource(vsContent);
        prePopulatedSupport.addValueSet(vs);
        logger.info("KBV Darreichungsform ValueSet geladen: {}", vs.getUrl());
    }
    
    // Hilfsmethode zum Laden von KBV DMP Ressourcen  
    private void loadKbvDmpResources(PrePopulatedValidationSupport prePopulatedSupport) throws IOException {
        logger.info("Lade KBV DMP Ressourcen...");
        
        // Lade CodeSystem
        String csContent = loadResourceAsString("/package/KBV_CS_SFHIR_KBV_DMP_V1.06 (1)/KBV_CS_SFHIR_KBV_DMP_V1.06.xml");
        CodeSystem cs = (CodeSystem) ctx.newXmlParser().parseResource(csContent);
        prePopulatedSupport.addCodeSystem(cs);
        logger.info("KBV DMP CodeSystem geladen: {}", cs.getUrl());
        
        // Lade ValueSet
        String vsContent = loadResourceAsString("/package/KBV_CS_SFHIR_KBV_DMP_V1.06 (1)/KBV_VS_SFHIR_KBV_DMP_V1.06.xml");
        ValueSet vs = (ValueSet) ctx.newXmlParser().parseResource(vsContent);
        prePopulatedSupport.addValueSet(vs);
        logger.info("KBV DMP ValueSet geladen: {}", vs.getUrl());
    }
    
    // Hilfsmethode zum Laden von ABDA eRezeptAbgabedaten 1.4.0 Ressourcen
    private void loadAbdaErezeptabgabedatenResources(PrePopulatedValidationSupport prePopulatedSupport) throws IOException {
        logger.info("Lade ABDA eRezeptAbgabedaten 1.4.0 Ressourcen...");
        
        String basePath = "/package/npm packages/de.abda.eRezeptAbgabedaten/";
        
        // Lade alle Profile (StructureDefinitions)
        String[] profiles = {
            "Profile-DAV-PR-ERP-AbgabedatenBundle.json",
            "Profile-DAV-PR-ERP-AbgabedatenComposition.json",
            "Profile-DAV-PR-ERP-Abgabeinformationen.json",
            "Profile-DAV-PR-ERP-Abrechnungszeilen.json",
            "Profile-DAV-PR-ERP-Apotheke.json",
            "Profile-DAV-PR-ERP-ZusatzdatenEinheit.json",
            "Profile-DAV-PR-ERP-ZusatzdatenHerstellung.json"
        };
        
        for (String profileFile : profiles) {
            try {
                String content = loadResourceAsString(basePath + profileFile);
                StructureDefinition sd = (StructureDefinition) ctx.newJsonParser().parseResource(content);
                prePopulatedSupport.addStructureDefinition(sd);
                logger.info("ABDA Profil geladen: {} Version: {}", sd.getUrl(), sd.getVersion());
            } catch (Exception e) {
                logger.error("Fehler beim Laden von {}: {}", profileFile, e.getMessage());
            }
        }
        
        // Lade alle CodeSystems
        String[] codeSystems = {
            "CodeSystem-DAV-CS-ERP-ArtRezeptaenderung.json",
            "CodeSystem-DAV-CS-ERP-KostenVersicherterKategorie.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributFAMSchluesselAbgaberangfolge.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributFAMSchluesselImportFAM.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributFAMSchluesselMarkt.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributSchluesselMehrkostenuebernahme.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributSchluesselZuzahlungsstatus.json",
            "CodeSystem-DAV-CS-ERP-ZusatzattributTarifkennzeichen.json",
            "CodeSystem-DAV-CS-ERP-ZusatzdatenEinheitFaktorkennzeichen.json",
            "CodeSystem-DAV-CS-ERP-ZusatzdatenEinheitPreiskennzeichen.json",
            "CodeSystem-DAV-CS-ERP-ZusatzdatenHerstellungHerstellerSchluessel.json"
        };
        
        for (String csFile : codeSystems) {
            try {
                String content = loadResourceAsString(basePath + csFile);
                CodeSystem cs = (CodeSystem) ctx.newJsonParser().parseResource(content);
                prePopulatedSupport.addCodeSystem(cs);
                logger.info("ABDA CodeSystem geladen: {}", cs.getUrl());
            } catch (Exception e) {
                logger.error("Fehler beim Laden von {}: {}", csFile, e.getMessage());
            }
        }
        
        // Lade alle ValueSets
        String[] valueSets = {
            "ValueSet-DAV-VS-ERP-ArtRezeptaenderung.json",
            "ValueSet-DAV-VS-ERP-KostenVersicherterKategorie.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributFAMSchluesselAbgaberangfolge.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributFAMSchluesselImportFAM.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributFAMSchluesselMarkt.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributSchluesselMehrkostenuebernahme.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributSchluesselZuzahlungsstatus.json",
            "ValueSet-DAV-VS-ERP-ZusatzattributTarifkennzeichen.json",
            "ValueSet-DAV-VS-ERP-ZusatzdatenEinheitFaktorkennzeichen.json",
            "ValueSet-DAV-VS-ERP-ZusatzdatenEinheitPreiskennzeichen.json",
            "ValueSet-DAV-VS-ERP-ZusatzdatenHerstellungHerstellerSchluessel.json"
        };
        
        for (String vsFile : valueSets) {
            try {
                String content = loadResourceAsString(basePath + vsFile);
                ValueSet vs = (ValueSet) ctx.newJsonParser().parseResource(content);
                prePopulatedSupport.addValueSet(vs);
                logger.info("ABDA ValueSet geladen: {}", vs.getUrl());
            } catch (Exception e) {
                logger.error("Fehler beim Laden von {}: {}", vsFile, e.getMessage());
            }
        }
        
        // Lade Extension
        try {
            String content = loadResourceAsString(basePath + "Extension-DAV-EX-ERP-Chargenbezeichnung.json");
            StructureDefinition ext = (StructureDefinition) ctx.newJsonParser().parseResource(content);
            prePopulatedSupport.addStructureDefinition(ext);
            logger.info("ABDA Extension geladen: {}", ext.getUrl());
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Extension: {}", e.getMessage());
        }
        
        logger.info("ABDA eRezeptAbgabedaten 1.4.0 Ressourcen vollständig geladen");
    }
    
    /**
     * Vorverarbeitung von Bundle-Ressourcen zur Behebung von Validierungsproblemen
     * mit XML-Kommentaren vor dem id-Element.
     */
    private IBaseResource preprocessBundleForValidation(IBaseResource resource) {
        try {
            // Konvertiere die Ressource zu XML
            String xml = ctx.newXmlParser().encodeResourceToString(resource);
            
            // Entferne problematische Kommentare zwischen Bundle-Tag und id-Element
            xml = removeXmlCommentsBeforeId(xml);
            
            // Parse die bereinigte XML zurück zur Ressource
            return ctx.newXmlParser().parseResource(xml);
        } catch (Exception e) {
            logger.warn("Fehler bei der Vorverarbeitung der Bundle-Ressource, verwende Original: {}", e.getMessage());
            return resource;
        }
    }
    
    /**
     * Entfernt XML-Kommentare, die zwischen dem öffnenden Bundle-Tag und dem id-Element stehen.
     * Diese Kommentare verursachen den Fehler "Objekt muss einen Inhalt haben".
     */
    private String removeXmlCommentsBeforeId(String xml) {
        // Regex-Pattern für Bundle-Start bis id-Element mit Kommentaren dazwischen
        String pattern = "(<Bundle[^>]*>)(\\s*<!--[^>]*-->\\s*)(<id[^>]*/>)";
        
        // Ersetze das Pattern, behalte Bundle und id, entferne nur den Kommentar
        String cleaned = xml.replaceAll(pattern, "$1$3");
        
        // Alternative: Entferne alle Kommentare zwischen XML-Tags generell
        // Dies ist sicherer für verschiedene Ressourcentypen
        cleaned = cleaned.replaceAll(">(\\s*<!--[^>]*-->\\s*)<", "><");
        
        return cleaned;
    }
} 