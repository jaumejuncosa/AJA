/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aja.model;

/**
 * Estructura estándar que nos devuelve la API. 
 * Envuelve los datos reales (message) y nos dice si la operación fue bien (success).
 */
public class ApiResponse<T> {
    // Los datos reales que devuelve la API (una lista, un objeto, un texto...)
    private T message;
    
    // Indica si la operación fue bien o hubo algún fallo en el servidor
    private boolean success;

    /**
     * Constructor vacío necesario para que Jackson pueda crear el objeto.
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