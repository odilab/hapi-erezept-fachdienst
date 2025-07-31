package ca.uhn.fhir.jpa.starter.custom.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import de.gematik.refv.ValidationModuleFactory;
import de.gematik.refv.SupportedValidationModule;
import de.gematik.refv.commons.validation.ValidationModule;
import de.gematik.refv.commons.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

@Component
@Interceptor
public class CustomValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomValidator.class);
    
    private final IParser xmlParser;
    private final Map<SupportedValidationModule, ValidationModule> validationModules;
    
    public CustomValidator(FhirContext ctx) {
        this.xmlParser = ctx.newXmlParser();
        this.validationModules = new EnumMap<>(SupportedValidationModule.class);
        logger.info("CustomValidator3 wird initialisiert mit gematik Reference Validator...");
    }
    
    @PostConstruct
    public void init() {
        try {
            ValidationModuleFactory factory = new ValidationModuleFactory();
            
            // Initialisiere alle verfügbaren Validierungsmodule
            for (SupportedValidationModule module : SupportedValidationModule.values()) {
                try {
                    ValidationModule validationModule = factory.createValidationModule(module);
                    validationModules.put(module, validationModule);
                    logger.info("Validierungsmodul {} erfolgreich geladen", module);
                } catch (Exception e) {
                    logger.warn("Konnte Validierungsmodul {} nicht laden: {}", module, e.getMessage());
                }
            }
            
            if (validationModules.isEmpty()) {
                throw new RuntimeException("Keine Validierungsmodule konnten geladen werden");
            }
            
            logger.info("CustomValidator3 erfolgreich initialisiert mit {} Modulen", validationModules.size());
        } catch (Exception e) {
            logger.error("Fehler beim Initialisieren des CustomValidator3", e);
            throw new RuntimeException("Fehler beim Initialisieren des gematik Reference Validators", e);
        }
    }
    
    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_CREATED)
    public void validateResourceOnCreate(IBaseResource theResource) {
        logger.debug("Validiere Ressource bei CREATE: {}", theResource.getClass().getSimpleName());
        validateResource(theResource);
    }
    
    @Hook(Pointcut.STORAGE_PRECOMMIT_RESOURCE_UPDATED)
    public void validateResourceOnUpdate(IBaseResource theOldResource, IBaseResource theResource) {
        logger.debug("Validiere Ressource bei UPDATE: {}", theResource.getClass().getSimpleName());
        validateResource(theResource);
    }
    
    private void validateResource(IBaseResource resource) {
        try {
            // Konvertiere die Ressource zu String (XML Format bevorzugt)
            String resourceString = xmlParser.encodeResourceToString(resource);
            
            // Bestimme das passende Validierungsmodul basierend auf der Ressource
            SupportedValidationModule moduleType = determineValidationModule(resource);
            
            if (moduleType == null) {
                logger.debug("Keine spezifische Validierung für Ressourcentyp: {}", resource.getClass().getSimpleName());
                return;
            }
            
            ValidationModule module = validationModules.get(moduleType);
            if (module == null) {
                logger.warn("Validierungsmodul {} nicht verfügbar", moduleType);
                return;
            }
            
            // Führe die Validierung durch
            ValidationResult result = module.validateString(resourceString);
            
            // Verarbeite das Ergebnis
            if (!result.isValid()) {
                StringBuilder errorMessages = new StringBuilder();
                StringBuilder warningMessages = new StringBuilder();
                
                result.getValidationMessages().forEach(msg -> {
                    String severity = msg.getSeverity().toString();
                    String message = msg.getMessage();
                    
                    if ("ERROR".equalsIgnoreCase(severity) || "FATAL".equalsIgnoreCase(severity)) {
                        errorMessages.append(String.format("[%s] %s\n", severity.toUpperCase(), message));
                    } else if ("WARNING".equalsIgnoreCase(severity)) {
                        warningMessages.append(String.format("[WARNING] %s\n", message));
                    }
                });
                
                if (errorMessages.length() > 0) {
                    logger.error("Validierungsfehler gefunden:\n{}", errorMessages.toString());
                    throw new UnprocessableEntityException("Validierung fehlgeschlagen:\n" + errorMessages.toString());
                }
                
                if (warningMessages.length() > 0) {
                    logger.warn("Validierungswarnungen:\n{}", warningMessages.toString());
                }
            }
            
            logger.debug("Ressource erfolgreich validiert");
            
        } catch (UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Fehler bei der Validierung mit gematik Reference Validator", e);
            throw new UnprocessableEntityException("Interner Fehler bei der Validierung: " + e.getMessage());
        }
    }
    
    private SupportedValidationModule determineValidationModule(IBaseResource resource) {
        String resourceType = resource.fhirType();
        
        // Prüfe auf KBV FOR Profile (Formular Profile)
        if (resource instanceof org.hl7.fhir.r4.model.DomainResource) {
            org.hl7.fhir.r4.model.DomainResource domainResource = (org.hl7.fhir.r4.model.DomainResource) resource;
            if (domainResource.hasMeta() && domainResource.getMeta().hasProfile()) {
                for (org.hl7.fhir.r4.model.CanonicalType profile : domainResource.getMeta().getProfile()) {
                    String profileUrl = profile.getValue();
                    // KBV FOR Profile werden vom CORE Modul validiert
                    if (profileUrl != null && profileUrl.contains("KBV_PR_FOR_")) {
                        logger.debug("KBV FOR Profil erkannt: {} - verwende CORE Modul", profileUrl);
                        return SupportedValidationModule.CORE;
                    }
                }
            }
        }
        
        // Prüfe Bundle-Typen für E-Rezept
        if ("Bundle".equals(resourceType)) {
            org.hl7.fhir.r4.model.Bundle bundle = (org.hl7.fhir.r4.model.Bundle) resource;
            
            // Prüfe Composition für genauere Bestimmung
            if (bundle.hasEntry()) {
                for (org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    if (entry.hasResource() && entry.getResource() instanceof org.hl7.fhir.r4.model.Composition) {
                        org.hl7.fhir.r4.model.Composition composition = (org.hl7.fhir.r4.model.Composition) entry.getResource();
                        String compositionType = composition.getType().getCodingFirstRep().getCode();
                        
                        // E-Rezept Dokumente
                        if ("e16A".equals(compositionType)) {
                            return SupportedValidationModule.ERP;
                        }
                        // EAU (Elektronische Arbeitsunfähigkeitsbescheinigung)
                        else if (compositionType != null && compositionType.startsWith("AU")) {
                            return SupportedValidationModule.EAU;
                        }
                    }
                }
            }
            
            // Fallback für E-Rezept basierend auf Bundle-Identifier
            if (bundle.hasIdentifier() && bundle.getIdentifier().hasSystem()) {
                String system = bundle.getIdentifier().getSystem();
                if (system.contains("gematik.de/fhir/erp") || system.contains("e-rezept")) {
                    return SupportedValidationModule.ERP;
                }
            }
        }
        
        // Task ist typischerweise E-Rezept
        if ("Task".equals(resourceType)) {
            return SupportedValidationModule.ERP;
        }
        
        // MedicationRequest, MedicationDispense sind E-Rezept relevant
        if ("MedicationRequest".equals(resourceType) || "MedicationDispense".equals(resourceType)) {
            return SupportedValidationModule.ERP;
        }
        
        // Default: ERP Modul für die meisten E-Rezept relevanten Ressourcen
        return SupportedValidationModule.ERP;
    }
}