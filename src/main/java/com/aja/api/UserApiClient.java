package com.aja.api;

import com.aja.AppConfig;
import com.aja.model.ApiResponse;
import com.aja.model.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Cliente para consumir la API REST de usuarios.
 * Proporciona métodos para obtener información de usuarios desde el servidor backend.
 * Utiliza autenticación básica para acceder a los endpoints protegidos.
 */
public class UserApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String token;

    
    public void setToken(String token) {
    this.token = token;
}

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public UserApiClient() {
        this.httpClient = HttpClientProvider.getClient();
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user"))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/user: " + response.statusCode());
        }

        ApiResponse<List<UserDto>> responseObj =
    objectMapper.readValue(response.body(),
        new TypeReference<ApiResponse<List<UserDto>>>() {});

return responseObj.getMessage();

    }

    /**
     * Obtiene la información de un solo usuario por su ID.
     * Hace una petición GET a /api/user/{id}.
     *
     * @param id ID del usuario a consultar
     * @return DTO del usuario
     * @throws Exception Si ocurre un error en la comunicación o parseo
     */
    public UserDto getUserById(Long id) throws Exception {
       HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/api/user/" + id)) 
            .header("Authorization", "Bearer " + token)  
            .GET()
            .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/user/" + id + ": " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), UserDto.class);
    }

    /**
     * Crea un nuevo usuario enviando una petición POST a /api/user.
     *
     * @param user El DTO del usuario a crear
     * @return El DTO del usuario creado (con ID asignado)
     * @throws Exception Si ocurre un error en la comunicación o parseo
     */
    public UserDto createUser(UserDto user) throws Exception {
        String json = objectMapper.writeValueAsString(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error creando usuario: " + response.statusCode() + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), UserDto.class);
    }
}

