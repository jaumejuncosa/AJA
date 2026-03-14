package com.aja.model;

/**
 * Representa la información de un usuario en el sistema.
 * Esta clase se utiliza para transferir datos de usuario entre la aplicación
 * de escritorio y el servidor backend.
 */
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private Integer role;
    private Boolean isactive;

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
     * @return el rol del usuario (representado como entero)
     */
    public Integer getRole() {
        return role;
    }

    /**
     * Establece el rol del usuario en el sistema.
     * @param role el rol del usuario (representado como entero)
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * Indica si el usuario está activo en el sistema.
     * @return true si el usuario está activo, false en caso contrario
     */
    public Boolean getIsactive() {
        return isactive;
    }

    /**
     * Establece el estado de actividad del usuario.
     * @param isactive true para activar el usuario, false para desactivarlo
     */
    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }
}

