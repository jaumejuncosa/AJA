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
import com.aja.config.AppConfig;
import com.aja.api.HttpClientProvider;

/**
 * Servicio que maneja la sesión y el login. 
 * Se comunica con la API para validar las credenciales y guarda el usuario actual.
 */
public class AuthService {
    // Singleton: Solo queremos una instancia de este servicio en toda la app
    private static final AuthService INSTANCE = new AuthService();

    public static AuthService getInstance() {
        return INSTANCE;
    }

    // Cliente y herramientas para el login
    private final HttpClient httpClient = HttpClientProvider.getClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Datos del usuario logueado actualmente
    private UserDto currentUser;
    private String token;

    private AuthService() {
    }

    /**
     * Preparamos la petición POST, mandamos las credenciales y, si el servidor 
     * nos da el OK, guardamos el token y el usuario en memoria para el resto de la app.
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
            //response.headers().allValues("Set-Cookie").forEach(v -> 
            //System.out.println("DEBUG AUTH Cookie: " + v));

            // Parsear respuesta JSON
            LoginResponseDto loginResponse;
            if (response.statusCode() == 200) {
                loginResponse = objectMapper.readValue(response.body(), LoginResponseDto.class);
            } else {
                try {
                    loginResponse = objectMapper.readValue(response.body(), LoginResponseDto.class);
                } catch (Exception e) {
                    loginResponse = new LoginResponseDto();
                    loginResponse.setSuccess(false);
                    loginResponse.setMessage("Error " + response.statusCode() + ": Acceso denegado.");
                }
                return loginResponse;
            }

            // Si el login fue exitoso, almacenar la información del usuario
            if (loginResponse.isSuccess() && loginResponse.getMessage() instanceof java.util.Map) {
                // Solo actualizamos el token si el servidor nos proporciona uno nuevo
                if (loginResponse.getToken() != null && !loginResponse.getToken().isBlank()) {
                    this.token = loginResponse.getToken();
                }
                
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
     * Devuelve el usuario que ha iniciado sesión.
     */
    public UserDto getCurrentUser() {
        return currentUser;
    }

    /**
     * "Limpiamos" la sesión local.
     */
    public void logout() {
        currentUser = null;
        // Las cookies se limpiarán automáticamente cuando se cierre la aplicación
        // o se puede implementar una llamada al servidor para invalidar la sesión
    }

    /**
     * Devuelve el token JWT almacenado.
     */
    public String getToken() {
        return this.token;
    }
}