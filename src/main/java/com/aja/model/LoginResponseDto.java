package com.aja.model;

/**
 * DTO para la respuesta del login.
 * Contiene el mensaje de respuesta y el indicador de éxito.
 */
public class LoginResponseDto {

    private Object message; // Puede ser String (error) o UserDto (éxito)
    private boolean success;

    public LoginResponseDto() {
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}