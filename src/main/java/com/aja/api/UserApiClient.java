package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;

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
    public UserDto createUser(UserDto user) throws Exception {
        return post("/api/user", user, new TypeReference<ApiResponse<UserDto>>() {});
    }
}
