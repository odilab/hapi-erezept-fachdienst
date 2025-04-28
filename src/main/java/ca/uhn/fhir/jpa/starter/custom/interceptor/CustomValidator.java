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


@Component
@Interceptor
public class CustomValidator {
    private static final Logger logger = LoggerFactory.getLogger(CustomValidator.class);
    private static final Pattern KVID_PATTERN = Pattern.compile("^[A-Z][0-9]{9}$");
    private static final Pattern GOAE_PATTERN = Pattern.compile("^[A-Z]?\\d{1,4}[A-Z]?$");
    private static final Pattern GOZ_PATTERN = Pattern.compile("^\\d{3,4}[a-z]?$");
    private static final String GOAE_SYSTEM = "http://fhir.de/CodeSystem/bäk/goä";
    private static final String GOZ_SYSTEM = "http://fhir.de/CodeSystem/bzäk/goz";
    private final FhirValidator validator;
    private final ValidationSupportChain validationSupportChain;
    private final PrePopulatedValidationSupport prePopulatedSupport;
    private final FhirContext ctx;

    public CustomValidator(FhirContext ctx) {
        this.ctx = ctx;
        logger.info("CustomValidator wird initialisiert...");
        try {
            // NPM Package Support erstellen und Packages laden
            NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
            npmPackageSupport.loadPackageFromClasspath("classpath:package/de.basisprofil.r4-1.5.3.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/de.ihe-d.terminology-3.0.1.tgz");
            npmPackageSupport.loadPackageFromClasspath("classpath:package/dvmd.kdl.r4-2024.0.0.tgz");

            logger.info("NPM Package Support erstellt und Packages geladen");

            // PrePopulatedValidationSupport für lokale Ressourcen erstellen und im Feld speichern
            this.prePopulatedSupport = new PrePopulatedValidationSupport(ctx);
            
            // Alle lokalen Ressourcen aus dem resources-Verzeichnis laden
            loadAllResources(this.prePopulatedSupport);
            
            // Validation Support Chain erstellen
            this.validationSupportChain = new ValidationSupportChain(
                npmPackageSupport,
                this.prePopulatedSupport,
                new DefaultProfileValidationSupport(ctx),
                new CommonCodeSystemsTerminologyService(ctx),
                new InMemoryTerminologyServerValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx)
            );
            logger.info("Validation Support Chain erstellt");

            // Validator mit Caching erstellen
            this.validator = ctx.newValidator();
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator(this.validationSupportChain);
            instanceValidator.setNoTerminologyChecks(false);
            instanceValidator.setErrorForUnknownProfiles(true);
            validator.registerValidatorModule(instanceValidator);
            logger.info("Validator erfolgreich konfiguriert");
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
    }

    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_UPDATED)
    public void validateResourceUpdate(IBaseResource resource) {
        validateAndThrowIfInvalid(resource);
    }

    public void validateAndThrowIfInvalid(IBaseResource resource) {
        logger.debug("Validiere Resource vom Typ: {}", resource.getClass().getSimpleName());
        
        // Zusätzliche KVID-Validierung für Patienten
        if (resource instanceof Patient) {
            validateKVID((Patient) resource);
        }
        
        // Zusätzliche Validierung für DocumentReference
        if (resource instanceof DocumentReference) {
            validateDocumentReference((DocumentReference) resource);
        }
        
        // Zusätzliche Validierung für Parameters
        if (resource instanceof Parameters) {
            validateParameters((Parameters) resource);
        }

        // Neue Validierung für Invoice (Gebührenordnungen)
        if (resource instanceof Invoice) {
            validateInvoiceGebOrd((Invoice) resource);
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

    private void validateKVID(Patient patient) {
        patient.getIdentifier().stream()
            .filter(id -> "http://fhir.de/sid/gkv/kvid-10".equals(id.getSystem()))
            .forEach(kvid -> {
                String value = kvid.getValue();
                if (value == null || !KVID_PATTERN.matcher(value).matches()) {
                    OperationOutcome outcome = new OperationOutcome();
                    outcome.addIssue()
                        .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                        .setCode(OperationOutcome.IssueType.INVALID)
                        .setDiagnostics("KVID muss 10-stellig sein und mit einem Großbuchstaben beginnen, gefolgt von 9 Ziffern. Gefundener Wert: " + value);
                    
                    throw new UnprocessableEntityException("Ungültiges KVID-Format", outcome);
                }
            });
    }

    private void validateDocumentReference(DocumentReference resource) {
        OperationOutcome outcome = new OperationOutcome();
        boolean hasError = false;

        // Validiere Status
        if (resource.getStatus() == null) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.REQUIRED)
                .setDiagnostics("Validierungsfehler: Pflichtfeld Status fehlt");
            hasError = true;
        }

        // Validiere Type
        if (resource.getType() == null) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.REQUIRED)
                .setDiagnostics("Validierungsfehler: Pflichtfeld Typ fehlt");
            hasError = true;
        }

        // Validiere Subject (Patient)
        if (resource.getSubject() == null || resource.getSubject().isEmpty()) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.REQUIRED)
                .setDiagnostics("Validierungsfehler: Pflichtfeld Patientenreferenz (subject) fehlt");
            hasError = true;
        }

        // Validiere Content
        if (resource.getContent().isEmpty()) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.REQUIRED)
                .setDiagnostics("Validierungsfehler: Pflichtfeld Inhalt (content) fehlt");
            hasError = true;
        } else {
            // Validiere Attachments in Content
            for (DocumentReference.DocumentReferenceContentComponent content : resource.getContent()) {
                if (content.getAttachment() == null || content.getAttachment().isEmpty()) {
                    outcome.addIssue()
                        .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                        .setCode(OperationOutcome.IssueType.REQUIRED)
                        .setDiagnostics("Validierungsfehler: Pflichtfeld Attachment fehlt im Content");
                    hasError = true;
                }
            }
        }

        if (hasError) {
            throw new UnprocessableEntityException("Validierungsfehler: Pflichtfelder fehlen", outcome);
        }
    }

    private void validateParameters(Parameters parameters) {
        OperationOutcome outcome = new OperationOutcome();
        boolean hasError = false;

        // Validiere Pflichtparameter für die Submit-Operation
        boolean hasRechnung = false;
        boolean hasModus = false;
        boolean hasAngereichertesPDF = false;

        for (Parameters.ParametersParameterComponent param : parameters.getParameter()) {
            switch (param.getName()) {
                case "rechnung":
                    hasRechnung = true;
                    if (param.getResource() instanceof DocumentReference) {
                        try {
                            validateDocumentReference((DocumentReference) param.getResource());
                        } catch (UnprocessableEntityException e) {
                            outcome.addIssue()
                                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                                .setCode(OperationOutcome.IssueType.INVALID)
                                .setDiagnostics("Fehler in der Rechnung: " + e.getMessage());
                            hasError = true;
                        }
                    }
                    break;
                case "modus":
                    hasModus = true;
                    if (param.getValue() instanceof CodeType) {
                        CodeType modus = (CodeType) param.getValue();
                        if (!"normal".equals(modus.getValue()) && !"test".equals(modus.getValue())) {
                            outcome.addIssue()
                                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                                .setCode(OperationOutcome.IssueType.INVALID)
                                .setDiagnostics("Der Modus muss 'normal' oder 'test' sein");
                            hasError = true;
                        }
                    } else {
                        outcome.addIssue()
                            .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                            .setCode(OperationOutcome.IssueType.INVALID)
                            .setDiagnostics("Der Parameter 'modus' muss vom Typ CodeType sein");
                        hasError = true;
                    }
                    break;
                case "angereichertesPDF":
                    hasAngereichertesPDF = true;
                    break;
            }
        }

        if (!hasRechnung) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.INVALID)
                .setDiagnostics("Der Parameter 'rechnung' muss angegeben werden");
            hasError = true;
        }
        if (!hasModus) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.INVALID)
                .setDiagnostics("Der Parameter 'modus' muss angegeben werden");
            hasError = true;
        }
        if (!hasAngereichertesPDF) {
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.INVALID)
                .setDiagnostics("Der Parameter 'angereichertesPDF' muss angegeben werden");
            hasError = true;
        }

        if (hasError) {
            throw new UnprocessableEntityException("Validierungsfehler in den Parametern", outcome);
        }
    }

    private void validateInvoiceGebOrd(Invoice invoice) {
        OperationOutcome outcome = new OperationOutcome();
        boolean hasError = false;

        // Prüfe Rechnungspositionen
        for (Invoice.InvoiceLineItemComponent lineItem : invoice.getLineItem()) {
            if (lineItem.hasChargeItemReference()) {
                try {
                    IBaseResource chargeItem = lineItem.getChargeItemReference().getResource();
                    if (chargeItem instanceof ChargeItem) {
                        ChargeItem charge = (ChargeItem) chargeItem;
                        
                        // Prüfe nur das Format der Gebührenordnungspositionen
                        if (charge.getCode() != null && charge.getCode().getCoding() != null) {
                            for (Coding coding : charge.getCode().getCoding()) {
                                String system = coding.getSystem();
                                String code = coding.getCode();
                                
                                if (system != null && code != null) {
                                    // Validiere Format der GOÄ Positionen
                                    if (GOAE_SYSTEM.equals(system) && !GOAE_PATTERN.matcher(code).matches()) {
                                        outcome.addIssue()
                                            .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                                            .setCode(OperationOutcome.IssueType.INVALID)
                                            .setDiagnostics("Ungültiges Format der GOÄ-Position: " + code + 
                                                ". Format muss sein: Optional Buchstabe, 1-4 Ziffern, optional Buchstabe");
                                        hasError = true;
                                    }
                                    // Validiere Format der GOZ Positionen
                                    else if (GOZ_SYSTEM.equals(system) && !GOZ_PATTERN.matcher(code).matches()) {
                                        outcome.addIssue()
                                            .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                                            .setCode(OperationOutcome.IssueType.INVALID)
                                            .setDiagnostics("Ungültiges Format der GOZ-Position: " + code + 
                                                ". Format muss sein: 3-4 Ziffern, optional Kleinbuchstabe");
                                        hasError = true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Fehler bei der Validierung der Gebührenordnungsposition: {}", e.getMessage());
                    outcome.addIssue()
                        .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                        .setCode(OperationOutcome.IssueType.INVALID)
                        .setDiagnostics("Fehler bei der Validierung der Gebührenordnungsposition: " + e.getMessage());
                    hasError = true;
                }
            }
        }

        if (hasError) {
            throw new UnprocessableEntityException("Validierungsfehler in den Gebührenordnungspositionen", outcome);
        }
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

    // Hilfsmethode zum Laden aller Ressourcen (StructureDefinitions, ValueSets, CodeSystems)
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
} 