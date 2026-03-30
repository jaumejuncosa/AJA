package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseApiClient {
    // URL base que sacamos de la configuración global
    protected static final String BASE_URL = AppConfig.getApiBaseUrl();
    
    // El cliente HTTP que reutilizaremos en todas las peticiones
    protected final HttpClient httpClient;
    
    // Mapper para convertir el JSON que nos llega de la API a objetos Java
    protected final ObjectMapper objectMapper;
    
    // Token de sesión (JWT) para las peticiones que requieren estar logueado
    protected String token;

    /**
     * Constructor base: Preparamos el cliente HTTP y el conversor de JSON.
     */
    public BaseApiClient() {
        this.httpClient = HttpClientProvider.getClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Guardamos el token para poder mandarlo en las cabeceras de las peticiones.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Método genérico para hacer un GET. 
     * Se encarga de montar la petición, añadir el token y procesar la respuesta.
     */
    protected <T> T get(String endpoint, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .GET();

        addAuthHeader(builder);
        
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return processResponse(response, typeReference);
    }

    /**
     * Método genérico para hacer un POST.
     * Mandamos un objeto en el body, lo convertimos a JSON y esperamos la respuesta.
     */
    protected <T> T post(String endpoint, Object body, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        addAuthHeader(builder);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return processResponse(response, typeReference);
    }

    /**
     * Si tenemos un token guardado, lo metemos en la cabecera 'Authorization'.
     */
    private void addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    /**
     * Comprobamos que la API no haya dado error y extraemos los datos del 'message' del JSON.
     */
    private <T> T processResponse(HttpResponse<String> response, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error en API: " + response.statusCode() + " - " + response.body());
        }
        ApiResponse<T> apiResponse = objectMapper.readValue(response.body(), typeReference);
        return apiResponse.getMessage();
    }
}