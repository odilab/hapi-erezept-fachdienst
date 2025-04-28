package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TslCertificateItem {
    private final X509Certificate certificate;
    private final List<String> supplyPoints;
    
    public TslCertificateItem(X509Certificate certificate, List<String> supplyPoints) {
        this.certificate = certificate;
        this.supplyPoints = Collections.unmodifiableList(new ArrayList<>(supplyPoints));
    }
    
    public X509Certificate getCertificate() {
        return certificate;
    }
    
    public List<String> getSupplyPoints() {
        return supplyPoints;
    }
} 