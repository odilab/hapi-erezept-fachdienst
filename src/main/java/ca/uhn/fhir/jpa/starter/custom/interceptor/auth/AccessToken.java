package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import java.time.Instant;
import java.util.Optional;

public class AccessToken {
    private String iss;
    private String sub;
    private String aud;
    private String nonce;
    private Instant exp;
    private Instant iat;
    private Instant nbf;
    private Profession profession;
    private String idNumber;
    private String givenName;
    private String familyName;
    private String organizationName;
    private String clientId;
    private String scope;

    // Getter und Setter
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Instant getExp() {
        return exp;
    }

    public void setExp(Instant exp) {
        this.exp = exp;
    }

    public Instant getIat() {
        return iat;
    }

    public void setIat(Instant iat) {
        this.iat = iat;
    }

    public Instant getNbf() {
        return nbf;
    }

    public void setNbf(Instant nbf) {
        this.nbf = nbf;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    // Business Logik
    public Optional<String> getKvnr() {
        if (profession == Profession.VERSICHERTER) {
            return Optional.of(idNumber);
        }
        return Optional.empty();
    }

    public Optional<String> getTelematikId() {
        if (profession != Profession.VERSICHERTER) {
            return Optional.of(idNumber);
        }
        return Optional.empty();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(exp);
    }

    public boolean isNotValidYet() {
        Instant effectiveNbf = nbf != null ? nbf : iat;
        return Instant.now().isBefore(effectiveNbf);
    }
} 