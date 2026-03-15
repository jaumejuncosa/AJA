package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.UserDto;
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
 * Cliente para consumir la API REST de usuarios.
 * Proporciona métodos para obtener información de usuarios desde el servidor backend.
 * Utiliza autenticación básica para acceder a los endpoints protegidos.
 */
public class UserApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public UserApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene la lista completa de usuarios registrados en el sistema.
     * Realiza una petición GET al endpoint /api/user con autenticación básica.
     *
     * @return Lista de objetos UserDto con la información de todos los usuarios
     * @throws Exception Si ocurre un error en la comunicación con el servidor
     *                   o en el procesamiento de la respuesta JSON
     */
    public List<UserDto> getAllUsers() throws Exception {
        String auth = ADMIN_USER + ":" + ADMIN_PASS;
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user"))
                .header("Accept", "application/json")
                .header("Authorization", basicAuth)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/user: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<UserDto>>() {}
        );
    }
}

