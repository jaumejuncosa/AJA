package com.aja.api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

/**
 * Proveedor de cliente HTTP compartido para todas las llamadas a la API.
 * Configura un CookieManager para manejar automáticamente las cookies de sesión,
 * incluyendo el JWT recibido durante el login.
 */
public class HttpClientProvider {

    private static HttpClient client;

    static {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    /**
     * Obtiene el cliente HTTP compartido con gestión de cookies.
     *
     * @return El cliente HTTP configurado
     */
    public static HttpClient getClient() {
        return client;
    }
}