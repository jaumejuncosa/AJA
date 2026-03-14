package com.aja.model;

/**
 * Representa la información de un mensaje en el sistema.
 * Incluye los datos necesarios para mostrar mensajes entre usuarios,
 * incluyendo remitente, destinatario y estado de lectura.
 */
public class MessageDto {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String date;
    private Boolean read;

    /**
     * Obtiene el identificador único del mensaje.
     * @return el ID del mensaje
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del mensaje.
     * @param id el ID del mensaje
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del usuario que envió el mensaje.
     * @return el nombre del remitente
     */
    public String getSender() {
        return sender;
    }

    /**
     * Establece el nombre del usuario que envió el mensaje.
     * @param sender el nombre del remitente
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Obtiene el nombre del usuario destinatario del mensaje.
     * @return el nombre del destinatario
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Establece el nombre del usuario destinatario del mensaje.
     * @param receiver el nombre del destinatario
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Obtiene el contenido textual del mensaje.
     * @return el contenido del mensaje
     */
    public String getContent() {
        return content;
    }

    /**
     * Establece el contenido textual del mensaje.
     * @param content el contenido del mensaje
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Obtiene la fecha en que se envió el mensaje.
     * @return la fecha del mensaje en formato de cadena
     */
    public String getDate() {
        return date;
    }

    /**
     * Establece la fecha en que se envió el mensaje.
     * @param date la fecha del mensaje en formato de cadena
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Indica si el mensaje ha sido leído por el destinatario.
     * @return true si el mensaje ha sido leído, false en caso contrario
     */
    public Boolean getRead() {
        return read;
    }

    /**
     * Establece el estado de lectura del mensaje.
     * @param read true si el mensaje ha sido leído, false en caso contrario
     */
    public void setRead(Boolean read) {
        this.read = read;
    }
}