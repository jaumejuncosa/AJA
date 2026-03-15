package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.MessageDto;
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
 * Cliente para consumir la API REST de mensajes.
 * Maneja la comunicación con el servidor para obtener mensajes del sistema,
 * incluyendo información sobre remitentes, destinatarios y estado de lectura.
 */
public class MessageApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public MessageApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene la lista completa de mensajes del sistema.
     * Realiza una petición GET al endpoint /api/messages con autenticación básica.
     *
     * @return Lista de objetos MessageDto con la información de todos los mensajes
     * @throws Exception Si ocurre un error en la comunicación con el servidor
     *                   o en el procesamiento de la respuesta JSON
     */
    public List<MessageDto> getAllMessages() throws Exception {
        String auth = ADMIN_USER + ":" + ADMIN_PASS;
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/messages"))
                .header("Accept", "application/json")
                .header("Authorization", basicAuth)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/messages: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<MessageDto>>() {}
        );
    }

    /**
     * Crea un nuevo mensaje enviando una petición POST a /api/messages.
     *
     * @param message El DTO del mensaje a crear
     * @return El DTO del mensaje creado (con ID asignado)
     * @throws Exception Si ocurre un error en la comunicación o parseo
     */
    public MessageDto createMessage(MessageDto message) throws Exception {
        String auth = ADMIN_USER + ":" + ADMIN_PASS;
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        String json = objectMapper.writeValueAsString(message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/messages"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error creando mensaje: " + response.statusCode() + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), MessageDto.class);
    }
}