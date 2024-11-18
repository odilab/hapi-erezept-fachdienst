package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Random;

@Component
public class TaskResourceProvider implements IResourceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskResourceProvider.class);
	private static final Random RANDOM = new Random();

	@Autowired
	private DaoRegistry daoRegistry;

	@Override
	public Class<Task> getResourceType() {
		return Task.class;
	}

	@Operation(name = "$create", type = Task.class)
	public Task createTask(
		@OperationParam(name = "workflowType") Coding workflowType) {

		// Validierung des WorkflowType
		if (workflowType == null || !workflowType.getCode().equals("160")) {
			throw new UnprocessableEntityException("WorkflowType '" + workflowType.getCode() + "' ist nicht erlaubt. Nur WorkflowType '160' ist zulässig.");
		}


		LOGGER.info("Create-Operation aufgerufen mit workflowType: {}",
			workflowType.getCode());

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

		// Task über updateEntity erstellen (entspricht einem PUT)
		MethodOutcome outcome = taskDao.update(newTask);

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
}