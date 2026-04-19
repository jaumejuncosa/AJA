/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aja.model;

/**
 * Forma en la que nos llega la información. 
 * Trae los datos y nos dice si todo ha salido bien.
 */
public class ApiResponse<T> {
    // El paquete de datos que nos llega (una lista, un texto, etc.)
    private T message;
    
    // Nos dice si ha funcionado o ha habido un problema
    private boolean success;

    /**
     * Preparación inicial del objeto de respuesta.
     */
    public ApiResponse() {
    }

    /**
     * Saca la información o el paquete de datos.
     */
    public T getMessage() {
        return message;
    }

    /**
     * Guarda los datos recibidos.
     */
    public void setMessage(T message) {
        this.message = message;
    }

    /**
     * Nos dice si la petición ha funcionado correctamente.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Guarda si la operación fue bien o mal.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}