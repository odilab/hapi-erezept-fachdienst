package ca.uhn.fhir.jpa.starter.custom.interceptor.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.Signature;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;

public class BrainpoolP256r1Algorithm extends Algorithm {
    private static final Logger logger = LoggerFactory.getLogger(BrainpoolP256r1Algorithm.class);
    private final ECPublicKey publicKey;
    private final ECPrivateKey privateKey;
    
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    public BrainpoolP256r1Algorithm(ECPublicKey publicKey) {
        this(publicKey, null);
    }
    
    public BrainpoolP256r1Algorithm(ECPublicKey publicKey, ECPrivateKey privateKey) {
        super("BP256R1", "ECDSA");
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    
    private byte[] convertConcatToDER(byte[] concat) throws Exception {
        if (concat.length != 64) {
            throw new IllegalArgumentException("Ungültige Signaturlänge: " + concat.length);
        }
        
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        System.arraycopy(concat, 0, r, 0, 32);
        System.arraycopy(concat, 32, s, 0, 32);
        
        ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(new ASN1Integer(new BigInteger(1, r)));
        vector.add(new ASN1Integer(new BigInteger(1, s)));
        
        return new DERSequence(vector).getEncoded();
    }
    
    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        try {
            String headerAndPayload = String.format("%s.%s", jwt.getHeader(), jwt.getPayload());
            byte[] contentBytes = headerAndPayload.getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = convertConcatToDER(Base64.getUrlDecoder().decode(jwt.getSignature()));
            
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(publicKey);
            signature.update(contentBytes);
            
            if (!signature.verify(signatureBytes)) {
                throw new SignatureVerificationException(this);
            }
            
        } catch (IllegalArgumentException e) {
            logger.error("Fehler bei der Signatur-Dekodierung: {}", e.getMessage());
            throw new SignatureVerificationException(this, e);
        } catch (Exception e) {
            logger.error("Fehler bei der Signaturverifikation: {}", e.getMessage());
            throw new SignatureVerificationException(this, e);
        }
    }
    
    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        if (privateKey == null) {
            throw new SignatureGenerationException(this, new IllegalStateException("Private key is null"));
        }
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(contentBytes);
            return signature.sign();
        } catch (Exception e) {
            throw new SignatureGenerationException(this, e);
        }
    }
} 