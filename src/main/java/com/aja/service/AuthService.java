package com.aja.service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aja.model.LoginResponseDto;
import com.aja.model.UserDto;
import com.aja.AppConfig;
import com.aja.api.HttpClientProvider;

/**
 * Servicio de autenticación para la aplicación AJA Desktop.
 *
 * Implementa autenticación contra el backend remoto mediante la obtención de un
 * JWT que se recibe en una cookie. El cliente HTTP comparte un CookieManager
 * para que la cookie se envíe automáticamente en peticiones posteriores.
 */
public class AuthService {

    private static final AuthService INSTANCE = new AuthService();

    public static AuthService getInstance() {
        return INSTANCE;
    }

    private final HttpClient httpClient = HttpClientProvider.getClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserDto currentUser;
    private String token;

    private AuthService() {
    }

    /**
     * Intenta autenticar al usuario contra el backend.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return LoginResponseDto con el resultado de la autenticación y mensaje
     */
    public LoginResponseDto authenticate(String username, String password) {
        if (username == null || password == null) {
            LoginResponseDto response = new LoginResponseDto();
            response.setSuccess(false);
            response.setMessage("Usuario y contraseña son requeridos");
            return response;
        }

        try {
            String loginUrl = AppConfig.getApiBaseUrl() + "/api/auth/login";

            // Construir body en formato x-www-form-urlencoded
            String body = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                          "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status: " + response.statusCode());
            System.out.println("Login response body: " + response.body());

            // Parsear respuesta JSON
            LoginResponseDto loginResponse = objectMapper.readValue(response.body(), LoginResponseDto.class);

            // Si el login fue exitoso, almacenar la información del usuario
            if (loginResponse.isSuccess() && loginResponse.getMessage() instanceof java.util.Map) {
                // Convertir el Map a UserDto
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) loginResponse.getMessage();
                currentUser = objectMapper.convertValue(userMap, UserDto.class);
            }

            return loginResponse;

        } catch (Exception e) {
            e.printStackTrace();
            LoginResponseDto response = new LoginResponseDto();
            response.setSuccess(false);
            response.setMessage("Error de conexión: " + e.getMessage());
            return response;
        }
    }

    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * @return El UserDto del usuario actual, o null si no hay usuario autenticado
     */
    public UserDto getCurrentUser() {
        return currentUser;
    }

    /**
     * Cierra la sesión del usuario actual.
     * Limpia la información del usuario y las cookies de sesión.
     */
    public void logout() {
        currentUser = null;
        // Las cookies se limpiarán automáticamente cuando se cierre la aplicación
        // o se puede implementar una llamada al servidor para invalidar la sesión
    }

    public String getToken() {
        return this.token;
    }
}