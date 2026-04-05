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
     * Constructor: Simplemente llamamos al de la clase base.
     */
    public UserApiClient() {
        super();
    }

    /**
     * Pedimos a la API todos los usuarios que hay en la base de datos.
     */
    public List<UserDto> getAllUsers() throws Exception {
        return get("/api/user", new TypeReference<ApiResponse<List<UserDto>>>() {});
    }

    /**
     * Buscamos los detalles de un usuario concreto usando su ID.
     */
    public UserDto getUserById(Long id) throws Exception {
        return get("/api/user/" + id, new TypeReference<ApiResponse<UserDto>>() {});
    }

    /**
     * Mandamos los datos de un nuevo usuario a la API para registrarlo.
     */
    public Object createUser(UserNewDto user) throws Exception {
        // Cambiamos a Object para que no de error si la API responde un simple mensaje de texto
        return post("/api/user", user, new TypeReference<ApiResponse<Object>>() {});
    }

    public Object updateUser(UserDto user) throws Exception {
        // Volvemos a la ruta estándar /api/user. El ID del usuario va dentro 
        // del objeto UserDto (en el cuerpo del JSON), no en la URL.
        return put("/api/user", user, new TypeReference<ApiResponse<Object>>() {});
    }

    public void deleteUser(Long id) throws Exception {
        delete("/api/user/" + id, new TypeReference<ApiResponse<String>>() {});
    }

    /**
     * Deshabilita un usuario específico por su ID.
     * Solo para administradores.
     */
    public Object disableUser(Long id) throws Exception {
        // La API espera un cuerpo para PUT, aunque esté vacío para esta operación.
        Map<String, Object> emptyBody = new HashMap<>();
        return put("/api/user/disable/" + id, emptyBody, new TypeReference<ApiResponse<Object>>() {});
    }
}
