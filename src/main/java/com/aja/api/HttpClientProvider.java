package com.aja.api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Preparamos la conexión a internet para usarla en todo el programa. 
 * Permite recordar que estamos dentro del sistema.
 */
public class HttpClientProvider {

    private static HttpClient client;

    static {
        try {
            // Preparamos el gestor de cookies para mantener la sesión
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            // Creamos un gestor que confía en todos los certificados (para evitar el error de fecha/validez)
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            // Configuramos el contexto SSL para usar este gestor "permisivo"
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            client = HttpClient.newBuilder()
                    .sslContext(sslContext) // Le decimos al cliente que use nuestra configuración de seguridad
                    .cookieHandler(cookieManager)
                    .build();
        } catch (Exception e) {
            System.err.println("Error al configurar el cliente HTTP permisivo: " + e.getMessage());
            client = HttpClient.newBuilder().build();
        }
    }

    /**
     * Nos da la conexión lista para usarse.
     */
    public static HttpClient getClient() {
        return client;
    }
}