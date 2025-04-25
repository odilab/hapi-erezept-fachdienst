package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

public enum AccessTokenError {
    MISSING,
    INVALID_VALUE,
    EXPIRED,
    NOT_VALID_YET,
    NO_PUK_TOKEN,
    INVALID_PROFESSION,
    INVALID_ID
} 