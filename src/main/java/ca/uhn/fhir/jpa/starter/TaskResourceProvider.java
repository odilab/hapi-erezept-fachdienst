package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SecurityException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;

@Component
public class TaskResourceProvider implements IResourceProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskResourceProvider.class);
	private static final Random RANDOM = new Random();

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private final DaoRegistry daoRegistry;

	@Autowired
	public TaskResourceProvider(DaoRegistry daoRegistry) {
		this.daoRegistry = daoRegistry;
	}

	@Override
	public Class<Task> getResourceType() {
		return Task.class;
	}

	@Operation(name = "$create", type = Task.class)
	public Task createTask(
		RequestDetails requestDetails,  // HAPI FHIR injiziert hier automatisch die Request-Details
		@OperationParam(name = "workflowType") Coding workflowType) {

		// Access-Token aus den Request-Details auslesen
		String accessToken = requestDetails.getHeader("Authorization");
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);

			try {
				// Zertifikat als Base64-String, die ist der puk-token vom IDP
				String certB64 = "MIICsTCCAligAwIBAgIHAbssqQhqOzAKBggqhkjOPQQDAjCBhDELMAkGA1UEBhMCREUxHzAdBgNVBAoMFmdlbWF0aWsgR21iSCBOT1QtVkFMSUQxMjAwBgNVBAsMKUtvbXBvbmVudGVuLUNBIGRlciBUZWxlbWF0aWtpbmZyYXN0cnVrdHVyMSAwHgYDVQQDDBdHRU0uS09NUC1DQTEwIFRFU1QtT05MWTAeFw0yMTAxMTUwMDAwMDBaFw0yNjAxMTUyMzU5NTlaMEkxCzAJBgNVBAYTAkRFMSYwJAYDVQQKDB1nZW1hdGlrIFRFU1QtT05MWSAtIE5PVC1WQUxJRDESMBAGA1UEAwwJSURQIFNpZyAzMFowFAYHKoZIzj0CAQYJKyQDAwIIAQEHA0IABIYZnwiGAn5QYOx43Z8MwaZLD3r/bz6BTcQO5pbeum6qQzYD5dDCcriw/VNPPZCQzXQPg4StWyy5OOq9TogBEmOjge0wgeowDgYDVR0PAQH/BAQDAgeAMC0GBSskCAMDBCQwIjAgMB4wHDAaMAwMCklEUC1EaWVuc3QwCgYIKoIUAEwEggQwIQYDVR0gBBowGDAKBggqghQATASBSzAKBggqghQATASBIzAfBgNVHSMEGDAWgBQo8Pjmqch3zENF25qu1zqDrA4PqDA4BggrBgEFBQcBAQQsMCowKAYIKwYBBQUHMAGGHGh0dHA6Ly9laGNhLmdlbWF0aWsuZGUvb2NzcC8wHQYDVR0OBBYEFC94M9LgW44lNgoAbkPaomnLjS8/MAwGA1UdEwEB/wQCMAAwCgYIKoZIzj0EAwIDRwAwRAIgCg4yZDWmyBirgxzawz/S8DJnRFKtYU/YGNlRc7+kBHcCIBuzba3GspqSmoP1VwMeNNKNaLsgV8vMbDJb30aqaiX1";

				// Zertifikat laden
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				Certificate cert = cf.generateCertificate(
					new ByteArrayInputStream(Base64.getDecoder().decode(certB64))
				);
				PublicKey publicKey = cert.getPublicKey();

				// JWT-Verifikation anpassen
				Claims claims = validateJwt(accessToken, publicKey);

				String professionOID = claims.get("professionOID", String.class);
				if (!isValidProfessionOID(professionOID)) {
					throw new UnprocessableEntityException("Ungültiger professionOID: " + professionOID);
				}

				LOGGER.debug("professionOID gefunden: {}", professionOID);

			} catch (CertificateException e) {
				throw new UnprocessableEntityException("Fehler beim Laden des Zertifikats: " + e.getMessage());
			}
		}

		// Validierung des WorkflowType
		if (workflowType == null || !workflowType.getCode().equals("160")) {
			if (workflowType == null) {
				throw new UnprocessableEntityException("WorkflowType ist null.");
			}
			throw new UnprocessableEntityException("WorkflowType '" + workflowType.getCode() +
				"' ist nicht erlaubt. Nur WorkflowType '160' ist zulässig.");
		}

		LOGGER.info("Create-Operation aufgerufen mit workflowType: {}", workflowType.getCode());

		Task newTask = new Task();

		newTask.setStatus(Task.TaskStatus.DRAFT);
		newTask.addInput()
			.setValue(workflowType)
			.getType()
			.addCoding()
			.setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
			.setCode("workflowType");

		String prescriptionId = generatePrescriptionId(workflowType);

		// ID und Identifier setzen
		newTask.setId(prescriptionId);
		newTask.setIdentifier(Collections.singletonList(
			new Identifier()
				.setValue(prescriptionId)
				.setSystem("https://gematik.de/fhir/erp/NamingSystem/PrescriptionID")
		));

		LOGGER.info("Task wird erstellt mit ID: {}", prescriptionId);

		// Task-DAO aus der Registry holen
		IFhirResourceDao<Task> taskDao = daoRegistry.getResourceDao(Task.class);

		// Task über update erstellen mit den echten Request-Details
		MethodOutcome outcome = taskDao.update(newTask, requestDetails);

		Task createdTask = (Task) outcome.getResource();

		LOGGER.info("Task erfolgreich erstellt mit ID: {}", createdTask.getId());

		return createdTask;
	}

	private String generatePrescriptionId(Coding workflowType) {
		StringBuilder prescriptionId = new StringBuilder();
		prescriptionId.append(workflowType.getCode()).append(".");

		for (int i = 0; i < 5; i++) {
			if (i > 0) {
				prescriptionId.append(".");
			}
			prescriptionId.append(String.format("%03d", RANDOM.nextInt(1000)));
		}

		return prescriptionId.toString();
	}

	private boolean isValidProfessionOID(String professionOID) {
		return professionOID != null && (
			professionOID.equals("1.2.276.0.76.4.50") ||
			professionOID.equals("1.2.276.0.76.4.51") ||
			professionOID.equals("1.2.276.0.76.4.52")
		);
	}

	private Claims validateJwt(String accessToken, PublicKey publicKey) {
		try {
			// Token in seine Bestandteile zerlegen
			String[] parts = accessToken.split("\\.");
			if (parts.length != 3) {
				throw new SecurityException("Ungültiges JWT Format");
			}

			// Header und Claims dekodieren
			String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
			String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
			byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

			// Debug-Ausgaben für Header und Payload
			LOGGER.debug("JWT Header: {}", headerJson);
			LOGGER.debug("JWT Payload: {}", payloadJson);
			LOGGER.debug("Signature Length: {}", signatureBytes.length);

			// Signierte Daten rekonstruieren
			String signedContent = parts[0] + "." + parts[1];
			byte[] signedContentBytes = signedContent.getBytes(StandardCharsets.UTF_8);

			// JWT Signatur in DER Format konvertieren
			byte[] derSignature = convertJWTSignatureToDER(signatureBytes);

			// Spezifischen Algorithmus für Brainpool-Kurven verwenden
			Security.addProvider(new BouncyCastleProvider());
			Signature signature = Signature.getInstance("SHA256withECDSA", "BC");

			// Debug-Ausgaben für Schlüssel und Signatur
			LOGGER.debug("Public Key Algorithm: {}", publicKey.getAlgorithm());
			LOGGER.debug("Public Key Format: {}", publicKey.getFormat());
			LOGGER.debug("Public Key Class: {}", publicKey.getClass().getName());
			LOGGER.debug("Signed Content (Base64): {}", Base64.getEncoder().encodeToString(signedContentBytes));
			LOGGER.debug("DER Signature (Base64): {}", Base64.getEncoder().encodeToString(derSignature));

			signature.initVerify(publicKey);
			signature.update(signedContentBytes);

			boolean isValid = signature.verify(derSignature);
			
			if (!isValid) {
				LOGGER.error("Signaturverifikation fehlgeschlagen - Details:");
				LOGGER.error("Verwendeter Algorithmus: {}", signature.getAlgorithm());
				LOGGER.error("Provider: {}", signature.getProvider().getName());
				throw new SecurityException("Signatur-Verifizierung fehlgeschlagen");
			}

			// Claims manuell parsen
			ObjectMapper mapper = new ObjectMapper();
			Claims claims = mapper.readValue(payloadJson, Claims.class);
			
			LOGGER.debug("JWT erfolgreich validiert");
			return claims;

		} catch (Exception e) {
			LOGGER.error("JWT Validierung fehlgeschlagen", e);
			throw new UnprocessableEntityException("JWT Validierung fehlgeschlagen: " + e.getMessage(), e);
		}
	}

	private byte[] convertJWTSignatureToDER(byte[] jwsSignature) throws IOException {
		if (jwsSignature.length != 64) {
			LOGGER.error("Ungültige Signaturlänge: {} (erwartet: 64)", jwsSignature.length);
			throw new IllegalArgumentException("JWT Signatur muss 64 Bytes lang sein");
		}

		// R und S Komponenten extrahieren
		byte[] r = Arrays.copyOfRange(jwsSignature, 0, 32);
		byte[] s = Arrays.copyOfRange(jwsSignature, 32, 64);

		// Debug-Ausgaben für R und S
		LOGGER.debug("R component (Base64): {}", Base64.getEncoder().encodeToString(r));
		LOGGER.debug("S component (Base64): {}", Base64.getEncoder().encodeToString(s));

		// Führende Nullen entfernen
		r = removeLeadingZeros(r);
		s = removeLeadingZeros(s);

		// DER Encoding
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(new ASN1Integer(new BigInteger(1, r)));
		v.add(new ASN1Integer(new BigInteger(1, s)));
		byte[] derEncoded = new DERSequence(v).getEncoded();

		LOGGER.debug("DER encoded length: {}", derEncoded.length);
		return derEncoded;
	}

	private byte[] removeLeadingZeros(byte[] input) {
		int offset = 0;
		while (offset < input.length && input[offset] == 0) {
			offset++;
		}
		return Arrays.copyOfRange(input, offset, input.length);
	}
}