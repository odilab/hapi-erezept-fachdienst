package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

public class AccessTokenException extends RuntimeException {
    private final AccessTokenError error;

    public AccessTokenException(AccessTokenError error) {
        super(error.name());
        this.error = error;
    }

    public AccessTokenException(AccessTokenError error, String message) {
        super(message);
        this.error = error;
    }

    public AccessTokenError getError() {
        return error;
    }
} 