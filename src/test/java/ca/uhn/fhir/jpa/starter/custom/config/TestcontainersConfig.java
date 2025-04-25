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
    private static final Network network = Network.newNetwork();

    public static GenericContainer<?> startIdpContainer() {
        if (idpContainer == null || !idpContainer.isRunning()) {
            idpContainer = new GenericContainer<>(
                    DockerImageName.parse("ghcr.io/odilab/ipd-server/idp-server:latest"))
                    .withExposedPorts(10000)
                    .withNetwork(network)
                    .withNetworkAliases("idp-server")
                    .withEnv("SPRING_PROFILES_ACTIVE", "ssl")
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withEntrypoint(
                                    "java", "-Dspring.profiles.active=ssl", "-jar", "/app/idp-server-19.1.0.jar"
                            ));

            idpContainer.start();
            logger.info("IDP Container gestartet auf Port: {}", idpContainer.getMappedPort(10000));
        }
        return idpContainer;
    }

    public static GenericContainer<?> startErpServiceContainer() {
        if (erpServiceContainer == null || !erpServiceContainer.isRunning()) {
            // Stelle sicher, dass IDP Container läuft
            startIdpContainer();

            erpServiceContainer = new GenericContainer<>(
                    DockerImageName.parse("ghcr.io/odilab/spring-erp-services/erp-service:latest"))
                    .withExposedPorts(3001)
                    .withNetwork(network)
                    .withNetworkAliases("erp-service")
                    .withEnv("SPRING_PROFILES_ACTIVE", "ssl")
                    .withEnv("default.string.idp.urlHttps", "https://idp-server:10000")
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withEntrypoint(
                                    "java",
                                    "-Dspring.profiles.active=ssl",
                                    "-jar",
                                    "/app/app.jar"
                            ))
                    .dependsOn(idpContainer);

            erpServiceContainer.start();
            logger.info("ERP-Service Container gestartet auf Port: {}", erpServiceContainer.getMappedPort(3001));
        }
        return erpServiceContainer;
    }

    public static class IdpInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            GenericContainer<?> idp = startIdpContainer();
            String idpUrl = String.format("https://%s:%d/.well-known/openid-configuration",
                    idp.getHost(),
                    idp.getMappedPort(10000));
            
            logger.info("Konfiguriere IDP URL: {}", idpUrl);
            
            TestPropertyValues.of(
                    "hapi.fhir.auth.discovery_url=" + idpUrl,
                    "hapi.fhir.auth.update_interval_seconds=43200"
            ).applyTo(context.getEnvironment());
        }
    }

    public static class FullStackInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            GenericContainer<?> idp = startIdpContainer();
            GenericContainer<?> erp = startErpServiceContainer();
            
            String idpUrl = String.format("https://%s:%d/.well-known/openid-configuration",
                    idp.getHost(),
                    idp.getMappedPort(10000));
            
            String erpServiceUrl = String.format("http://%s:%d",
                    erp.getHost(),
                    erp.getMappedPort(3001));
            
            logger.info("Konfiguriere IDP URL: {}", idpUrl);
            logger.info("Konfiguriere ERP-Service URL: {}", erpServiceUrl);
            
            TestPropertyValues.of(
                    "hapi.fhir.auth.discovery_url=" + idpUrl,
                    "hapi.fhir.auth.update_interval_seconds=43200",
                    "hapi.fhir.erp.service.url=" + erpServiceUrl
            ).applyTo(context.getEnvironment());
        }
    }
} 