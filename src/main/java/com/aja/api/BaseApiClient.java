package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseApiClient {
    // Dirección principal de la web de donde sacamos los datos
    protected static final String BASE_URL = AppConfig.getApiBaseUrl();
    
    // Herramienta para conectarse a internet
    protected final HttpClient httpClient;
    
    // Traductor para entender los datos que nos envía el servidor
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    // Llave de seguridad para entrar a zonas privadas
    protected String token;

    /**
     * Configuración inicial: Preparamos la conexión y el traductor.
     */
    public BaseApiClient() {
        this.httpClient = HttpClientProvider.getClient();
    }

    /**
     * Guardamos la llave para usarla en cada puerta que abramos.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Pedir información: Preparamos la nota, enseñamos la llave y leemos lo que nos contestan.
     */
    protected <T> T get(String endpoint, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .GET();

        return execute(builder, typeReference);
    }

    /**
     * Enviar información nueva: Escribimos los datos, los mandamos y esperamos la confirmación.
     */
    protected <T> T post(String endpoint, Object body, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        return execute(builder, typeReference);
    }

    /**
     * Modificar información: Cambiamos algo que ya existe por datos nuevos.
     */
    protected <T> T put(String endpoint, Object body, TypeReference<ApiResponse<T>> typeReference) throws Exception {
    String json = objectMapper.writeValueAsString(body);
    
        
    HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(json));

    return execute(builder, typeReference);
}

    /**
     * Borrar información: Pedimos que quiten algo usando su número de identificación.
     */
    protected <T> T delete(String endpoint, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .DELETE();

        return execute(builder, typeReference);
    }

    /**
     * Realizar la acción: Pone la llave, hace la llamada y mira qué ha pasado.
     */
    private <T> T execute(HttpRequest.Builder builder, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        addAuthHeader(builder);

        // Depuración: Ver cookies que se enviarán en la petición
        httpClient.cookieHandler().ifPresent(handler -> {
            if (handler instanceof java.net.CookieManager cm) {
               // System.out.println("DEBUG - Cookies enviadas: " + cm.getCookieStore().getCookies());
            }
        });

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        //System.out.println("DEBUG - Response headers: " + response.headers().map()); // ← AÑADE ESTA

        int statusCode = response.statusCode();
        
        return processResponse(response, typeReference);
    }

    /**
     * Si tenemos la llave, la enseñamos antes de entrar.
     */
    private void addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    /**
     * Miramos si todo ha ido bien y sacamos el mensaje importante.
     */
    private <T> T processResponse(HttpResponse<String> response, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        String body = response.body();
        int statusCode = response.statusCode();

        // Depuración: Ver si el servidor intenta establecer nuevas cookies (como JSESSIONID)
        response.headers().allValues("Set-Cookie").forEach(v -> System.out.println("DEBUG - Recibido Set-Cookie: " + v));

        if (statusCode >= 200 && statusCode < 300) {
            ApiResponse<T> apiResponse = objectMapper.readValue(body, typeReference);
            return apiResponse.getMessage();
        } else {
            // Log de depuración para ver por qué el servidor da 403
            if (statusCode == 403) {
                System.err.println("ACCESO DENEGADO (403). Body: " + body);
            }
            // Si algo falla, intentamos leer por qué ha pasado.
            try {
                ApiResponse<?> errorResponse = objectMapper.readValue(body, new TypeReference<ApiResponse<Object>>() {});
                throw new RuntimeException("Error API (" + statusCode + "): " + errorResponse.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error de servidor: " + statusCode + " - " + body);
            }
        }
    }
}