package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.ApiResponse;
import com.aja.model.EventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Cliente para consumir la API REST de eventos.
 * Gestiona la comunicación con el servidor backend para obtener información
 * sobre eventos disponibles en la plataforma.
 */
public class EventApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String token;

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public EventApiClient() {
        this.httpClient = HttpClientProvider.getClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene la lista completa de eventos disponibles en el sistema.
     * Realiza una petición GET al endpoint /api/events con autenticación básica.
     *
     * @return Lista de objetos EventDto con la información de todos los eventos
     * @throws Exception Si ocurre un error en la comunicación con el servidor
     *                   o en el procesamiento de la respuesta JSON
     */
    public List<EventDto> getAllEvents() throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/events"))
                .header("Accept", "application/json")
                .GET();

        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/events: " + response.statusCode());
        }

        ApiResponse<List<EventDto>> responseObj = objectMapper.readValue(
                response.body(), 
                new TypeReference<ApiResponse<List<EventDto>>>() {});
        return responseObj.getMessage();
    }

    /**
     * Crea un nuevo evento enviando una petición POST a /api/events.
     *
     * @param event El DTO del evento a crear
     * @return El DTO del evento creado (con ID asignado)
     * @throws Exception Si ocurre un error en la comunicación o parseo
     */
    public EventDto createEvent(EventDto event) throws Exception {
        String json = objectMapper.writeValueAsString(event);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/events"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response =
                httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error creando evento: " + response.statusCode() + " - " + response.body());
        }

        ApiResponse<EventDto> responseObj = objectMapper.readValue(
                response.body(), 
                new TypeReference<ApiResponse<EventDto>>() {});
        return responseObj.getMessage();
    }

    public void setToken(String token) {
        this.token = token;
    }
}