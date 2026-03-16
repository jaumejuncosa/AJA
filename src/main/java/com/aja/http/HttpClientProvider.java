package com.aja.http;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Proporciona un {@link HttpClient} compartido configurado con un CookieManager.
 *
 * Esto permite que las cookies (como el JWT de sesión) se almacenen y se incluyan
 * automáticamente en todas las peticiones posteriores.
 */
public final class HttpClientProvider {

    private static final HttpClient CLIENT = createHttpClient();

    private HttpClientProvider() {
        // No instanciable
    }

    private static HttpClient createHttpClient() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(cookieManager)
                .build();
    }

    /**
     * Obtiene el cliente HTTP compartido configurado con manejo de cookies.
     *
     * @return HttpClient compartido
     */
    public static HttpClient getClient() {
        return CLIENT;
    }
}
