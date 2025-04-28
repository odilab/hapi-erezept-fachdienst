package ca.uhn.fhir.jpa.starter.custom.operation.vau;


import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.UUID;

@Controller
public class VAUOperationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(VAUOperationProvider.class);

    @Autowired
    private VAUServerCrypto vauServerCrypto;

    @Autowired
    private ApplicationContext applicationContext;

    private final FhirContext ctx;

    @Autowired
    public VAUOperationProvider(FhirContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Endpoint zum Abrufen des VAU-Zertifikats
     */
    @GetMapping("/VAUCertificate")
    public ResponseEntity<byte[]> getVAUCertificate() throws IOException {
        try {
            // Hole den öffentlichen Schlüssel vom VAUServerCrypto
            byte[] certData = vauServerCrypto.getPublicKey().getEncoded();
            
            LOGGER.info("VAU-Zertifikat wird ausgeliefert, Größe: {} Bytes", certData.length);
            
            return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/pkix-cert"))
                .body(certData);
                
        } catch (Exception e) {
            LOGGER.error("Fehler beim Laden des VAU-Zertifikats: {}", e.getMessage());
            throw new IOException("Fehler beim Laden des VAU-Zertifikats: " + e.getMessage());
        }
    }

    /**
     * Endpoint zum Abrufen der OCSP-Response für das VAU-Zertifikat
     */
    @GetMapping("/VAUCertificateOCSPResponse")
    public ResponseEntity<byte[]> getVAUCertificateOCSPResponse() throws IOException {
        // TODO: Implementiere das Laden der OCSP-Response
        byte[] ocspData = new byte[0]; // Placeholder
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType("application/ocsp-response"))
            .body(ocspData);
    }

    /**
     * Hauptendpoint für verschlüsselte VAU-Requests
     * Format der URL ist /VAU/{userpseudonym}, wobei für den ersten Request "0" als Pseudonym verwendet wird
     */
    @PostMapping(value = "/VAU/{userpseudonym}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> handleVAURequest(
            @PathVariable("userpseudonym") String userpseudonym,
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[] encryptedData) throws IOException {
        
        try {
            // Validiere erforderliche Header
            validateHeaders(headers);

            // Entschlüssele die Anfrage
            String decryptedRequest = vauServerCrypto.decryptRequest(encryptedData);
            
            // Parse den entschlüsselten HTTP-Request
            // Format: "1 ACCESS_TOKEN REQUEST_ID RESPONSE_KEY HTTP_REQUEST"
            String[] parts = decryptedRequest.split(" ", 5);
            if (parts.length != 5 || !parts[0].equals("1")) {
                throw new UnprocessableEntityException("Ungültiges Request-Format");
            }

            String accessToken = parts[1];
            String requestId = parts[2];
            String responseKeyBase64 = parts[3];
            String innerHttpRequest = parts[4];

            // Verarbeite den inneren HTTP-Request und erhalte die Antwort
            String innerResponse = processInnerRequest(innerHttpRequest, accessToken);

            // Erstelle die innere HTTP-Response
            String vauResponse = String.format("1 %s %s", requestId, innerResponse);

            // Base64-dekodiere den Response-Key
            byte[] responseKeyBytes = java.util.Base64.getDecoder().decode(responseKeyBase64);
            SecretKeySpec responseKeySpec = new SecretKeySpec(responseKeyBytes, "AES");
            
            // Verschlüssele die Antwort mit dem Response-Key
            byte[] encryptedResponse = vauServerCrypto.encryptResponse(vauResponse, responseKeySpec);

            // Generiere ein neues Userpseudonym für Folge-Requests
            String newUserPseudonym = generateUserPseudonym();

            // Erstelle die Response mit Headers
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Userpseudonym", newUserPseudonym)
                .body(encryptedResponse);

        } catch (Exception e) {
            LOGGER.error("Fehler bei der VAU-Verarbeitung: {}", e.getMessage());
            throw new UnprocessableEntityException("Fehler bei der VAU-Verarbeitung: " + e.getMessage());
        }
    }

    private void validateHeaders(HttpHeaders headers) {
        String userType = headers.getFirst("X-erp-user");
        String resource = headers.getFirst("X-erp-resource");

        if (userType == null || (!userType.equals("l") && !userType.equals("v"))) {
            throw new UnprocessableEntityException("Ungültiger oder fehlender X-erp-user Header");
        }

        if (resource == null) {
            throw new UnprocessableEntityException("Fehlender X-erp-resource Header");
        }
    }

    private String processInnerRequest(String innerHttpRequest, String accessToken) {
        try {
            // Parse den inneren HTTP-Request
            String[] requestLines = innerHttpRequest.split("\r\n");
            if (requestLines.length < 1) {
                throw new UnprocessableEntityException("Ungültiger innerer HTTP-Request");
            }

            // Parse die Request-Line
            String[] requestLineParts = requestLines[0].split(" ");
            if (requestLineParts.length != 3) {
                throw new UnprocessableEntityException("Ungültige Request-Line im inneren HTTP-Request");
            }

            String method = requestLineParts[0];
            String path = requestLineParts[1];
            
            // Extrahiere den Body und Headers
            StringBuilder bodyBuilder = new StringBuilder();
            HttpHeaders headers = new HttpHeaders();
            boolean foundEmptyLine = false;
            
            for (int i = 1; i < requestLines.length; i++) {
                String line = requestLines[i];
                if (line.trim().isEmpty()) {
                    foundEmptyLine = true;
                    continue;
                }
                if (foundEmptyLine) {
                    bodyBuilder.append(line).append("\r\n");
                } else {
                    // Parse Header
                    int colonIndex = line.indexOf(':');
                    if (colonIndex > 0) {
                        String headerName = line.substring(0, colonIndex).trim();
                        String headerValue = line.substring(colonIndex + 1).trim();
                        headers.add(headerName, headerValue);
                    }
                }
            }

            String body = bodyBuilder.toString().trim();

            // Füge Authorization Header hinzu
            headers.setBearerAuth(accessToken);
            
            // Setze Content-Length Header neu
            headers.setContentLength(body.getBytes().length);

            // Erstelle den Request
            String baseUrl = getBaseUrl();
            RestTemplate restTemplate = new RestTemplate();
            
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + path,
                HttpMethod.valueOf(method),
                new HttpEntity<>(body, headers),
                String.class
            );

            // Erstelle die HTTP-Response
            StringBuilder responseBuilder = new StringBuilder();
            int statusCode = response.getStatusCode().value();
            String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
            responseBuilder.append("HTTP/1.1 ").append(statusCode)
                         .append(" ").append(reasonPhrase)
                         .append("\r\n");
            
            // Füge Response-Headers hinzu
            response.getHeaders().forEach((name, values) -> {
                values.forEach(value -> {
                    responseBuilder.append(name).append(": ").append(value).append("\r\n");
                });
            });
            
            // Füge Body hinzu
            responseBuilder.append("\r\n");
            if (response.getBody() != null) {
                responseBuilder.append(response.getBody());
            }

            return responseBuilder.toString();

        } catch (Exception e) {
            LOGGER.error("Fehler bei der Verarbeitung des inneren Requests: {}", e.getMessage());
            
            // Erstelle eine OperationOutcome für den Fehler
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.PROCESSING)
                .setDiagnostics(e.getMessage());

            String errorBody = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);

            return "HTTP/1.1 422 Unprocessable Entity\r\n" +
                   "Content-Type: application/fhir+json;charset=utf-8\r\n" +
                   "Content-Length: " + errorBody.length() + "\r\n" +
                   "\r\n" +
                   errorBody;
        }
    }

    private String getBaseUrl() {
        // Hole den aktuellen Request-Context
        jakarta.servlet.http.HttpServletRequest request = 
            ((org.springframework.web.context.request.ServletRequestAttributes) 
                org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes())
            .getRequest();
        
        // Baue die Base-URL aus dem Request
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        
        return String.format("%s://%s:%d", scheme, serverName, serverPort);
    }

    private String generateUserPseudonym() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
} 