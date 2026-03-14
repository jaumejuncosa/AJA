package com.aja.api;

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

public class UserApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

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

