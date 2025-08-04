package ca.uhn.fhir.jpa.starter.custom.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

/**
 * Utility class for creating RestTemplate instances that accept self-signed certificates.
 * This is only for test purposes and should not be used in production.
 */
public class TestSslUtils {
    
    /**
     * Creates a RestTemplate that accepts all SSL certificates including self-signed ones.
     * WARNING: This should only be used in test environments!
     * 
     * @return RestTemplate configured to accept self-signed certificates
     * @throws Exception if SSL context creation fails
     */
    public static RestTemplate createTrustAllRestTemplate() throws Exception {
        // Trust strategy that accepts all certificates
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        
        // Create SSL context with the trust strategy
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        
        // Register socket factories for both HTTP and HTTPS
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
                
        // Create connection manager with the socket factory registry
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        
        // Create HTTP client with the connection manager
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
                
        // Create request factory with the HTTP client
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        
        // Return RestTemplate with the configured request factory
        return new RestTemplate(requestFactory);
    }
}