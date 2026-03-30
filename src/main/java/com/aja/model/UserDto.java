package com.aja.model;

/**
 * Objeto para transportar la información de los usuarios entre la API y la aplicación.
 */
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean isActive;

    /**
     * Obtiene el identificador único del usuario.
     * @return el ID del usuario
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del usuario.
     * @param id el ID del usuario
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de usuario.
     * @return el nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     * @param username el nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la dirección de correo electrónico del usuario.
     * @return el email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece la dirección de correo electrónico del usuario.
     * @param email el email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el rol del usuario en el sistema.
     * @return el rol del usuario (como string)
     */
    public String getRole() {
        return role;
    }

    /**
     * Establece el rol del usuario en el sistema.
     * @param role el rol del usuario (como string)
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Obtiene el rol del usuario como enumeración.
     * @return el rol del usuario como objeto Role
     */
    public Role getRoleEnum() {
        return role != null ? Role.fromString(role) : null;
    }

    /**
     * Indica si el usuario está activo en el sistema.
     * @return true si el usuario está activo, false en caso contrario
     */
    public Boolean getActive() {
        return isActive;
    }

    /**
     * Establece el estado de actividad del usuario.
     * @param isActive true para activar el usuario, false para desactivarlo
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
