/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aja.model;

/**
 * Clase genérica que representa la estructura estándar de una respuesta de la API.
 * Sirve para encapsular los datos devueltos por el servidor junto con su estado de éxito.
 *
 * @author Usuario
 * @param <T> El tipo de dato del mensaje o payload que devuelve la API (puede ser un String, un UserDto, etc.).
 */
public class ApiResponse<T> {
    
    private T message;
    private boolean success;

    /**
     * Constructor por defecto de ApiResponse.
     */
    public ApiResponse() {
    }

    /**
     * Obtiene el mensaje o los datos devueltos por la API.
     * @return El objeto de tipo {@code T} que contiene el mensaje o payload.
     */
    public T getMessage() {
        return message;
    }

    /**
     * Establece el mensaje o los datos de la respuesta.
     * @param message El objeto de tipo {@code T} a establecer como mensaje.
     */
    public void setMessage(T message) {
        this.message = message;
    }

    /**
     * Indica si la petición a la API fue exitosa.
     * @return {@code true} si la petición tuvo éxito, {@code false} en caso de error.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Establece el estado de éxito de la petición.
     * @param success {@code true} para indicar éxito, {@code false} para indicar error.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}