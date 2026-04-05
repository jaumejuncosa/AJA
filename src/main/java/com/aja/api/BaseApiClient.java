package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

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
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    // Token de sesión (JWT) para las peticiones que requieren estar logueado
    protected String token;

    /**
     * Constructor base: Preparamos el cliente HTTP y el conversor de JSON.
     */
    public BaseApiClient() {
        this.httpClient = HttpClientProvider.getClient();
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
        System.out.println("DEBUG GET Request: " + BASE_URL + endpoint);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .GET();

        return execute(builder, typeReference);
    }

    /**
     * Método genérico para hacer un POST.
     * Mandamos un objeto en el body, lo convertimos a JSON y esperamos la respuesta.
     */
    protected <T> T post(String endpoint, Object body, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        System.out.println("DEBUG POST [" + endpoint + "]: " + json);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        return execute(builder, typeReference);
    }

    /**
     * Método genérico para hacer un PUT.
     * Se utiliza para actualizar recursos existentes.
     */
    protected <T> T put(String endpoint, Object body, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        System.out.println("DEBUG PUT [" + endpoint + "]: " + json);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));

        return execute(builder, typeReference);
    }

    /**
     * Método genérico para hacer un DELETE.
     * Se utiliza para eliminar recursos por su ID.
     */
    protected <T> T delete(String endpoint, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .DELETE();

        return execute(builder, typeReference);
    }

    /**
     * Ejecuta la petición, añade el token y procesa el resultado.
     */
    private <T> T execute(HttpRequest.Builder builder, TypeReference<ApiResponse<T>> typeReference) throws Exception {
        addAuthHeader(builder);
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        int statusCode = response.statusCode();
        String body = response.body();
        
        System.out.println("DEBUG Response Status: " + statusCode);
        // Intentamos formatear el JSON para que sea fácil de leer en consola
        Object json = objectMapper.readValue(body, Object.class);
        System.out.println("DEBUG Response Body:\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        
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
        String body = response.body();
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            ApiResponse<T> apiResponse = objectMapper.readValue(body, typeReference);
            return apiResponse.getMessage();
        } else {
            // Intentamos parsear el error si viene en formato ApiResponse para obtener el mensaje del servidor
            try {
                ApiResponse<?> errorResponse = objectMapper.readValue(body, new TypeReference<ApiResponse<Object>>() {});
                throw new RuntimeException("Error API (" + statusCode + "): " + errorResponse.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error de servidor: " + statusCode + " - " + body);
            }
        }
    }
}