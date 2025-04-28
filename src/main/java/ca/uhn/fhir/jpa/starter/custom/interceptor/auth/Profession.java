package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

public enum Profession {
    VERSICHERTER("1.2.276.0.76.4.49"),
    LEISTUNGSERBRINGER("1.2.276.0.76.4.30"),
    KOSTENTRAEGER("1.2.276.0.76.4.31"),
    PRAXIS_ARZT("1.2.276.0.76.4.30"),
    ZAHNARZT_PRAXIS("1.2.276.0.76.4.30"),
    PRAXIS_PSYCHOTHERAPEUT("1.2.276.0.76.4.30"),
    KRANKENHAUS("1.2.276.0.76.4.30"),
    OEFFENTLICHE_APOTHEKE("1.2.276.0.76.4.30"),
    KRANKENHAUS_APOTHEKE("1.2.276.0.76.4.30"),
    ARZT_KRANKENHAUS("1.2.276.0.76.4.53");
    
    private final String oid;
    
    Profession(String oid) {
        this.oid = oid;
    }
    
    public String getOid() {
        return oid;
    }
    
    public static Profession fromOID(String oid) {
        if (oid == null) {
            throw new AccessTokenException(AccessTokenError.INVALID_PROFESSION, "ProfessionOID fehlt");
        }
        
        for (Profession profession : values()) {
            if (profession.getOid().equals(oid)) {
                return profession;
            }
        }
        
        throw new AccessTokenException(AccessTokenError.INVALID_PROFESSION, 
            "Ungültige ProfessionOID: " + oid);
    }
} 