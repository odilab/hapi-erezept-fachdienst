# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a German E-Prescription (E-Rezept) FHIR service built on HAPI FHIR JPA Server v8.0.0. It implements electronic prescription handling according to gematik (German health telematics infrastructure) standards.

## Build and Development Commands

### Build Commands
```bash
# Standard build
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Build with Spring Boot
mvn clean spring-boot:run -Pboot

# Package as WAR
mvn clean package spring-boot:repackage -DskipTests=true -Pboot

# Build Docker image
./build-docker-image.sh
```

### Test Commands
```bash
# Run all tests
mvn test

# Run integration tests
mvn verify

# Run a specific test class
mvn test -Dtest=CreateOperationIntegrationTest

# Run tests with testcontainers (requires Docker)
mvn test -Dspring.profiles.active=test
```

### Development Server
```bash
# Run with Spring Boot (default H2 database)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.config.location=src/main/resources/application.yaml
```

## Code Architecture

### Key Package Structure
```
src/main/java/ca/uhn/fhir/jpa/starter/custom/
├── interceptor/
│   ├── CustomValidator.java              # gematik profile validation
│   └── auth/
│       ├── AuthenticationInterceptor.java # JWT authentication
│       ├── ResourceAuthorizationInterceptor.java # RBAC authorization
│       ├── AccessTokenService.java       # Token management
│       ├── PukTokenManager.java         # Public key management
│       └── TslManager.java              # Trust Service List management
├── operation/
│   ├── create/
│   │   ├── CreateOperationProvider.java # $create operation endpoint
│   │   └── CreateTaskService.java      # Task creation logic
│   └── vau/
│       ├── VAUOperationProvider.java   # VAU crypto operations
│       └── VAUServerCrypto.java        # Server-side cryptography
└── service/
    ├── AuditService.java               # Audit logging
    └── AuthorizationService.java       # Authorization checks
```

### E-Rezept Specific Components

1. **$create Operation**: Creates E-Prescription tasks according to gematik specifications
   - Workflow types: 160, 169, 200, 209, 210
   - Authorization based on healthcare professional OIDs
   - Returns Task in "draft" status

2. **Authentication**: JWT-based using German health card tokens
   - Discovery URL: `https://idp-fachdienst-ref.gematik.erppre.de/`
   - Public keys fetched from TSL (Trust Service List)

3. **Validation**: Integration with gematik Reference Validator
   - Profile validation against official German FHIR profiles
   - Custom validation interceptor for E-Rezept specific rules

4. **VAU Crypto**: Verifiable Anonymous User operations
   - Server-side encryption/decryption
   - Key management and certificate handling

## Test Strategy

- **Framework**: JUnit 5 with Spring Boot Test
- **Approach**: Integration tests using Testcontainers (no mocks)
- **Test Data**: Located in `src/test/resources/e-rezept-bundles/`
- **Key Test Classes**:
  - `CreateOperationIntegrationTest` - E-Prescription creation
  - `TestcontainerAccessTokenTest` - Authentication infrastructure
  - `CustomValidatorTest` - Validation logic
  - `VAUCryptoTest` - Cryptographic operations

## Configuration

### Main Configuration Files
- `src/main/resources/application.yaml` - Primary configuration
- `src/test/resources/application.yaml` - Test configuration

### Key Configuration Properties
```yaml
hapi:
  fhir:
    # Custom interceptors and providers
    custom-bean-packages: ca.uhn.fhir.jpa.starter.custom
    custom-interceptor-classes:
      - ca.uhn.fhir.jpa.starter.custom.interceptor.auth.AuthenticationInterceptor
      - ca.uhn.fhir.jpa.starter.custom.interceptor.auth.ResourceAuthorizationInterceptor
    custom-provider-classes:
      - ca.uhn.fhir.jpa.starter.custom.operation.create.CreateOperationProvider
    
    # Authentication
    auth:
      discovery_url: https://idp-fachdienst-ref.gematik.erppre.de/...
      skip_tsl_time_validation: true  # For development with expired certs
```

## External Dependencies

### gematik Libraries (in `/libs/`)
- Reference Validator Library and dependencies
- Various validation modules (ERP, EAU, etc.)

### Security Libraries
- Bouncy Castle - Cryptographic operations
- Auth0 JWT - Token handling
- Spring Security - Authentication framework

## Common Development Tasks

### Adding a New Operation
1. Create provider class extending `@Operation` annotation
2. Register in `application.yaml` under `custom-provider-classes`
3. Add authorization rules if needed
4. Create integration tests

### Modifying Authentication
- Authentication logic: `AuthenticationInterceptor.java`
- Authorization rules: `ResourceAuthorizationInterceptor.java`
- Token validation: `AccessTokenService.java`

### Working with E-Rezept Tasks
- Task creation: `CreateTaskService.java`
- Workflow types defined in `GEM_ERP_CS_FlowType`
- Status transitions follow gematik specifications

## Important Notes

- Always run tests before committing - the project uses extensive integration testing
- TSL certificate validation can be disabled for development via `skip_tsl_time_validation`
- The project uses Testcontainers - ensure Docker is running for tests
- Follow gematik specifications strictly - reference documents in project root