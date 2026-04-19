package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.UserDto;
import com.aja.model.UserNewDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

public class UserApiClient extends BaseApiClient {
    
    /**
     * Preparamos el sistema para trabajar con usuarios.
     */
    public UserApiClient() {
        super();
    }

    /**
     * Actualiza el perfil del usuario autenticado actualmente.
     * Se usa la ruta base /api/user según la documentación para usuarios no administradores.
     */
    public Object updateProfile(UserDto user) throws Exception {
        return put("/api/user", user, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Pedimos la lista de todas las personas registradas.
     */
    public List<UserDto> getAllUsers() throws Exception {
        return get("/api/user", new TypeReference<ApiResponse<List<UserDto>>>() {});
    }

    /**
     * Buscamos la ficha de una persona usando su número.
     */
    public UserDto getUserById(Long id) throws Exception {
        return get("/api/user/" + id, new TypeReference<ApiResponse<UserDto>>() {});
    }

    /**
     * Registramos a una persona nueva con sus datos.
     */
    public Object createUser(UserNewDto user) throws Exception {
        // Usamos un formato genérico por si la respuesta es corta.
        return post("/api/user", user, new TypeReference<ApiResponse<Object>>() {});
    }

    public Object updateUser(UserDto user) throws Exception {
        // El endpoint correcto para actualizar un usuario específico es /api/user/{id}
        return put("/api/user/" + user.getId(), user, new TypeReference<ApiResponse<Object>>() {});
    }

    public void deleteUser(Long id) throws Exception {
        delete("/api/user/" + id, new TypeReference<ApiResponse<String>>() {});
    }

    /**
     * Quitamos el permiso de entrada a una persona. Solo lo puede hacer el jefe.
     */
    public Object disableUser(Long id) throws Exception {
        return put("/api/user/disable/" + id, null, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Habilita a un usuario deshabilitado. Solo por admins.
     */
    public Object enableUser(Long id) throws Exception {
        return put("/api/user/enable/" + id, null, new TypeReference<ApiResponse<Object>>() {});
    }
}
