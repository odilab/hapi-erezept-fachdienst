package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import ca.uhn.fhir.jpa.starter.custom.BaseProviderTest;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceAuthorizationInterceptorTest extends BaseProviderTest {

    @Autowired
    private ResourceAuthorizationInterceptor resourceAuthorizationInterceptor;

    private IGenericClient clientWithToken;
    private String serverBaseUrl;

    @BeforeEach
    void setUpTest() {
        // Stelle sicher, dass der ResourceAuthorizationInterceptor registriert ist
        assertNotNull(resourceAuthorizationInterceptor, "ResourceAuthorizationInterceptor sollte nicht null sein");
        
        // Erstelle die Server-URL
        serverBaseUrl = "http://localhost:" + port + "/fhir/";
    }

    @Test
    void testPatientAccessWithCorrectScope() {
        // Erstelle einen Client mit dem korrekten Scope "insurantAccount.rs" oder "openid e-rezept"
        clientWithToken = ctx.newRestfulGenericClient(serverBaseUrl);
        clientWithToken.registerInterceptor(new BearerTokenAuthInterceptor(getValidAccessToken("EGK1")));
        
        // Sollte erfolgreich sein, da der EGK1-Token den Scope "insurantAccount.rs" oder "openid e-rezept" hat
        Patient patient = clientWithToken.read().resource(Patient.class).withId(testPatient.getIdElement().getIdPart()).execute();
        
        assertNotNull(patient, "Patient sollte erfolgreich abgerufen werden");
    }

    @Test
    void testDocumentReferenceReadWithCorrectScope() {
        // Erstelle einen Client mit dem korrekten Scope "invoiceDoc.r" oder "openid e-rezept"
        clientWithToken = ctx.newRestfulGenericClient(serverBaseUrl);
        clientWithToken.registerInterceptor(new BearerTokenAuthInterceptor(getValidAccessToken("EGK1")));
        
        // Erstelle ein DocumentReference zum Testen
        DocumentReference docRef = createTestDocumentReference();
        DocumentReference createdDocRef = (DocumentReference) clientWithToken.create().resource(docRef).execute().getResource();
        
        // Sollte erfolgreich sein, da der EGK1-Token den Scope "invoiceDoc.r" oder "openid e-rezept" hat
        DocumentReference retrievedDocRef = clientWithToken.read().resource(DocumentReference.class)
                .withId(createdDocRef.getIdElement().getIdPart()).execute();
        
        assertNotNull(retrievedDocRef, "DocumentReference sollte erfolgreich abgerufen werden");
    }
    
    @Test
    void testDocumentReferenceSearchWithCorrectScope() {
        // Erstelle einen Client mit dem korrekten Scope "invoiceDoc.s" oder "openid e-rezept"
        clientWithToken = ctx.newRestfulGenericClient(serverBaseUrl);
        clientWithToken.registerInterceptor(new BearerTokenAuthInterceptor(getValidAccessToken("EGK1")));
        
        // Erstelle ein DocumentReference zum Testen
        DocumentReference docRef = createTestDocumentReference();
        clientWithToken.create().resource(docRef).execute();
        
        // Sollte erfolgreich sein, da der EGK1-Token den Scope "invoiceDoc.s" oder "openid e-rezept" hat
        Bundle results = clientWithToken.search().forResource(DocumentReference.class)
                .where(DocumentReference.SUBJECT.hasId(testPatient.getIdElement().getIdPart()))
                .returnBundle(Bundle.class)
                .execute();
        
        assertNotNull(results, "DocumentReference-Suche sollte erfolgreich sein");
    }
    
    @Test
    void testAuditEventAccessWithCorrectScope() {
        // Erstelle einen Client mit dem korrekten Scope "auditEvent.rs" oder "openid e-rezept"
        clientWithToken = ctx.newRestfulGenericClient(serverBaseUrl);
        clientWithToken.registerInterceptor(new BearerTokenAuthInterceptor(getValidAccessToken("EGK1")));
        
        // Erstelle ein AuditEvent zum Testen
        AuditEvent auditEvent = createTestAuditEvent();
        AuditEvent createdAuditEvent = (AuditEvent) clientWithToken.create().resource(auditEvent).execute().getResource();
        
        // Sollte erfolgreich sein, da der EGK1-Token den Scope "auditEvent.rs" oder "openid e-rezept" hat
        AuditEvent retrievedAuditEvent = clientWithToken.read().resource(AuditEvent.class)
                .withId(createdAuditEvent.getIdElement().getIdPart()).execute();
        
        assertNotNull(retrievedAuditEvent, "AuditEvent sollte erfolgreich abgerufen werden");
    }
    
    /**
     * Erstellt ein Test-DocumentReference.
     */
    private DocumentReference createTestDocumentReference() {
        DocumentReference docRef = new DocumentReference();
        docRef.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
        docRef.getSubject().setReference("Patient/" + testPatient.getIdElement().getIdPart());
        docRef.addAuthor().setReference("Practitioner/" + testPractitioner.getIdElement().getIdPart());
        docRef.setDate(new java.util.Date());
        
        DocumentReference.DocumentReferenceContentComponent content = docRef.addContent();
        content.getAttachment().setContentType("application/pdf");
        content.getAttachment().setData("Test PDF data".getBytes());
        
        return docRef;
    }
    
    /**
     * Erstellt ein Test-AuditEvent.
     */
    private AuditEvent createTestAuditEvent() {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setAction(AuditEvent.AuditEventAction.R);
        auditEvent.setRecorded(new java.util.Date());
        auditEvent.setOutcome(AuditEvent.AuditEventOutcome._0);
        
        AuditEvent.AuditEventAgentComponent agent = auditEvent.addAgent();
        agent.setRequestor(true);
        agent.getWho().setReference("Patient/" + testPatient.getIdElement().getIdPart());
        
        AuditEvent.AuditEventEntityComponent entity = auditEvent.addEntity();
        entity.getWhat().setReference("Patient/" + testPatient.getIdElement().getIdPart());
        
        return auditEvent;
    }
} 