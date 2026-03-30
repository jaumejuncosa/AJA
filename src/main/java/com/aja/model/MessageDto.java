package com.aja.model;

/**
 * Clase para gestionar los datos de los mensajes internos del sistema.
 */
public class MessageDto {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String date;
    private Boolean read;

    /**
     * El ID único del mensaje.
     */
    public Long getId() {
        return id;
    }

    /**
     * Guardamos el ID del mensaje.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * El nombre del usuario que ha mandado el mensaje.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Guardamos quién es el que manda el mensaje.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * El nombre del usuario al que le va a llegar el mensaje.
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Guardamos para quién es el mensaje.
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * El texto o cuerpo del mensaje que se ha enviado.
     */
    public String getContent() {
        return content;
    }

    /**
     * Guardamos lo que dice el mensaje.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Cuándo se envió el mensaje (viene de la API como texto).
     */
    public String getDate() {
        return date;
    }

    /**
     * Guardamos la fecha de envío.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Un sí o no para saber si el destinatario ya ha abierto el mensaje.
     */
    public Boolean getRead() {
        return read;
    }

    /**
     * Marcamos el mensaje como leído o pendiente.
     */
    public void setRead(Boolean read) {
        this.read = read;
    }
}