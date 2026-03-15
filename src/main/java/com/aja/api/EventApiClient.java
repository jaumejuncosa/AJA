package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.EventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Cliente para consumir la API REST de eventos.
 * Gestiona la comunicación con el servidor backend para obtener información
 * sobre eventos disponibles en la plataforma.
 */
public class EventApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public EventApiClient() {
        this.httpClient = HttpClient.newHttpClient();
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
        String auth = ADMIN_USER + ":" + ADMIN_PASS;
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/events"))
                .header("Accept", "application/json")
                .header("Authorization", basicAuth)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/events: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<EventDto>>() {}
        );
    }
}