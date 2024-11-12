package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class}, properties = {
	"hapi.fhir.custom-bean-packages=ca.uhn.fhir.jpa.starter",
	"hapi.fhir.custom-provider-classes=ca.uhn.fhir.jpa.starter.TaskResourceProvider",
	"spring.datasource.url=jdbc:h2:mem:dbr4",
	"hapi.fhir.cr_enabled=false",
	"hapi.fhir.fhir_version=r4",
	"hapi.fhir.client_id_strategy=ANY"
})
class TaskResourceProviderTest {

	@LocalServerPort
	private int port;

	private IGenericClient client;
	private FhirContext ctx;

	@BeforeEach
	void setUp() {
		ctx = FhirContext.forR4();
		ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		ctx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
		String ourServerBase = "http://localhost:" + port + "/fhir/";
		client = ctx.newRestfulGenericClient(ourServerBase);
	}

	@Test
	void testCreateTaskOperation() {
		// Erstelle die Parameter für die Operation
		Parameters params = new Parameters();
		Coding workflowType = new Coding()
			.setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
			.setCode("160");
		params.addParameter().setName("workflowType").setValue(workflowType);

		// Führe die Operation aus
		Task createdTask = client.operation()
			.onType(Task.class)
			.named("$create")
			.withParameters(params)
			.returnResourceType(Task.class)
			.execute();

		// Überprüfe die Ergebnisse
		Assertions.assertNotNull(createdTask, "Erstellter Task sollte nicht null sein");
		Assertions.assertNotNull(createdTask.getId(), "Task sollte eine ID haben");

		// Überprüfe das ID-Format
		String taskId = createdTask.getIdElement().getIdPart();
		Assertions.assertTrue(taskId.startsWith("160."),
			"Task ID sollte mit dem workflowType (160) beginnen");
		Assertions.assertTrue(taskId.matches("160\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}"),
			"Task ID sollte dem Format 160.xxx.xxx.xxx.xxx.xxx entsprechen");

		// Überprüfe den Status
		Assertions.assertEquals(Task.TaskStatus.DRAFT, createdTask.getStatus(),
			"Task sollte im Status DRAFT sein");

		// Überprüfe den Identifier
		Assertions.assertFalse(createdTask.getIdentifier().isEmpty(),
			"Task sollte einen Identifier haben");
		Identifier identifier = createdTask.getIdentifier().get(0);
		Assertions.assertEquals(taskId, identifier.getValue(),
			"Identifier sollte den gleichen Wert wie die ID haben");
		Assertions.assertEquals("https://gematik.de/fhir/erp/NamingSystem/PrescriptionID",
			identifier.getSystem(),
			"Identifier sollte das korrekte System haben");

		// Überprüfe die Input-Parameter
		Assertions.assertFalse(createdTask.getInput().isEmpty(),
			"Task sollte Input-Parameter haben");
		Task.ParameterComponent input = createdTask.getInput().get(0);
		Type inputValue = input.getValue();
		Assertions.assertInstanceOf(Coding.class, inputValue, "Input Value sollte vom Typ Coding sein");
		Coding inputCoding = (Coding) inputValue;
		Assertions.assertEquals("160", inputCoding.getCode(),
			"Input Coding sollte den Code '160' haben");
		Assertions.assertEquals("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType",
			inputCoding.getSystem(),
			"Input Coding sollte das korrekte System haben");

		// Überprüfe, ob der Task über seine ID abrufbar ist
		Task retrievedTask = client.read()
			.resource(Task.class)
			.withId(taskId)
			.execute();

		Assertions.assertNotNull(retrievedTask,
			"Task sollte über seine ID abrufbar sein");
		Assertions.assertEquals(taskId, retrievedTask.getIdElement().getIdPart(),
			"Abgerufener Task sollte die gleiche ID haben");
	}

	@Test
	void testCreateTaskOperationWithInvalidWorkflowType() {
		// Erstelle die Parameter mit ungültigem WorkflowType
		Parameters params = new Parameters();
		Coding invalidWorkflowType = new Coding()
			.setSystem("https://gematik.de/fhir/erp/CodeSystem/GEM_ERP_CS_FlowType")
			.setCode("999"); // Ungültiger Code

		params.addParameter().setName("workflowType").setValue(invalidWorkflowType);

		// Überprüfe, ob die Operation eine Exception wirft
		Exception exception = Assertions.assertThrows(Exception.class, () -> {
			client.operation()
				.onType(Task.class)
				.named("$create")
				.withParameters(params)
				.returnResourceType(Task.class)
				.execute();
		});

		Assertions.assertTrue(exception.getMessage().contains("999"),
			"Exception sollte den ungültigen Code enthalten");
	}
}