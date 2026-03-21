package com.aja.api;

import com.aja.config.AppConfig;
import com.aja.model.ForumDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Cliente para consumir la API REST del foro.
 * Gestiona la obtención de publicaciones y temas del foro desde el servidor backend,
 * permitiendo mostrar el contenido disponible a los usuarios.
 */
public class ForumApiClient {

    private static final String BASE_URL = AppConfig.getApiBaseUrl();

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa el cliente HTTP y el mapper de JSON.
     */
    public ForumApiClient() {
        this.httpClient = HttpClientProvider.getClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene la lista completa de publicaciones del foro.
     * Realiza una petición GET al endpoint /api/forum con autenticación básica.
     *
     * @return Lista de objetos ForumDto con la información de todas las publicaciones
     * @throws Exception Si ocurre un error en la comunicación con el servidor
     *                   o en el procesamiento de la respuesta JSON
     */
    public List<ForumDto> getAllForumPosts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/forum"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error API /api/forum: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<ForumDto>>() {}
        );
    }

    /**
     * Crea un nuevo post en el foro enviando una petición POST a /api/forum.
     *
     * @param forumPost El DTO del post a crear
     * @return El DTO del post creado (con ID asignado)
     * @throws Exception Si ocurre un error en la comunicación o parseo
     */
    public ForumDto createForumPost(ForumDto forumPost) throws Exception {
        String json = objectMapper.writeValueAsString(forumPost);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/forum"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error creando post en foro: " + response.statusCode() + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), ForumDto.class);
    }

    public void setToken(String token) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}