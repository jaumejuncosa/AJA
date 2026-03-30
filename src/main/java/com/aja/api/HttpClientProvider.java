package com.aja.api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

/**
 * Aquí configuramos el cliente HTTP una sola vez para que lo usen todos los servicios.
 * Incluye un gestor de cookies para mantener la sesión abierta automáticamente.
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