package ca.uhn.fhir.jpa.starter.custom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class TestcontainersConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestcontainersConfig.class);
    private static GenericContainer<?> idpContainer;
    private static GenericContainer<?> erpServiceContainer;
    private static GenericContainer<?> fachdienstToolContainer;
    private static final Network network = Network.newNetwork();

    public static GenericContainer<?> startIdpContainer() {
        if (idpContainer == null || !idpContainer.isRunning()) {
            idpContainer = new GenericContainer<>(
                    DockerImageName.parse("ghcr.io/odilab/erp-services/ref-idp-server:29.3.2"))
                    .withExposedPorts(8080)
                    .withNetwork(network)
                    .withNetworkAliases("idp-server")
                    // IDP_SERVER_URL wird in discoveryDocuments kodiert
                    // Da der Service den IDP über http://idp-server:8080 erreicht
                    .withEnv("IDP_SERVER_URL", "http://idp-server:8080");

            idpContainer.start();
            logger.info("IDP Container gestartet auf Port: {}", idpContainer.getMappedPort(8080));
        }
        return idpContainer;
    }

    public static GenericContainer<?> startErpServiceContainer() {
        if (erpServiceContainer == null || !erpServiceContainer.isRunning()) {
            // Ensure IDP and Fachdienst-Tool containers are running
            startIdpContainer();
            startFachdienstToolContainer();

            erpServiceContainer = new GenericContainer<>(
                    DockerImageName.parse("ghcr.io/odilab/spring-erp-services/erp-service:latest"))
                    .withExposedPorts(3001)
                    .withNetwork(network)
                    .withNetworkAliases("erp-service")
                    .withEnv("SPRING_PROFILES_ACTIVE", "ssl,test")
                    .withEnv("default.string.idp.urlHttps", "http://idp-server:8080")
                    .withEnv("default.string.fd.urlFachdienstTools", "http://fachdienst-tool:8080")
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withEntrypoint(
                                    "java",
                                    "-Dspring.profiles.active=ssl,test",
                                    "-Ddefault.string.fd.urlFachdienstTools=http://fachdienst-tool:8080",
                                    "-jar",
                                    "/app/app.jar"
                            ))
                    .dependsOn(idpContainer);

            erpServiceContainer.start();
            logger.info("ERP-Service Container gestartet auf Port: {}", erpServiceContainer.getMappedPort(3001));
            logger.info("Container Env: {}", erpServiceContainer.getEnvMap());
        }
        return erpServiceContainer;
    }

    public static GenericContainer<?> startFachdienstToolContainer() {
        if (fachdienstToolContainer == null || !fachdienstToolContainer.isRunning()) {
            fachdienstToolContainer = new GenericContainer<>(
                    DockerImageName.parse("ghcr.io/odilab/fachdienst-tool-webservice/fachdienst_tool_webservice:latest"))
                    .withExposedPorts(8080)
                    .withNetwork(network)
                    .withNetworkAliases("fachdienst-tool")
                    .withStartupTimeout(java.time.Duration.ofSeconds(120))
                    .withFileSystemBind(
                            System.getProperty("user.dir") + "/src/main/resources/credentials/service_qes",
                            "/app/credentials",
                            org.testcontainers.containers.BindMode.READ_ONLY
                    )
                    .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forHttp("/").forPort(8080).forStatusCode(404));

            fachdienstToolContainer.start();
            logger.info("Fachdienst-Tool Container gestartet auf Port: {}", fachdienstToolContainer.getMappedPort(8080));
        }
        return fachdienstToolContainer;
    }

    public static class IdpInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            GenericContainer<?> idp = startIdpContainer();
            String idpUrl = String.format("http://%s:%d/.well-known/openid-configuration",
                    idp.getHost(),
                    idp.getMappedPort(8080));
            
            logger.info("Konfiguriere IDP URL: {}", idpUrl);
            
            TestPropertyValues.of(
                    "hapi.fhir.auth.discovery_url=" + idpUrl,
                    "hapi.fhir.auth.update_interval_seconds=43200",
                    // JWT-Validierung wieder aktivieren (skip_jwt_validation entfernt)
                    "hapi.fhir.auth.skip_jwt_validation=false"
            ).applyTo(context.getEnvironment());
        }
    }

    public static class FullStackInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            GenericContainer<?> idp = startIdpContainer();
            GenericContainer<?> erp = startErpServiceContainer();
            
            String idpUrl = String.format("http://%s:%d/.well-known/openid-configuration",
                    idp.getHost(),
                    idp.getMappedPort(8080));
            
            String erpServiceUrl = String.format("http://%s:%d",
                    erp.getHost(),
                    erp.getMappedPort(3001));
            
            logger.info("Konfiguriere IDP URL: {}", idpUrl);
            logger.info("Konfiguriere ERP-Service URL: {}", erpServiceUrl);
            
            TestPropertyValues.of(
                    "hapi.fhir.auth.discovery_url=" + idpUrl,
                    "hapi.fhir.auth.update_interval_seconds=43200",
                    // JWT-Validierung wieder aktivieren (skip_jwt_validation entfernt)
                    "hapi.fhir.auth.skip_jwt_validation=false",
                    "hapi.fhir.erp.service.url=" + erpServiceUrl
            ).applyTo(context.getEnvironment());
        }
    }
} 