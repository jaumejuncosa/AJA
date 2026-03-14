package com.aja.model;

/**
 * Representa la información de un evento en el sistema.
 * Contiene los detalles básicos necesarios para mostrar y gestionar eventos
 * en la aplicación de escritorio.
 */
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private String date;
    private String location;

    /**
     * Obtiene el identificador único del evento.
     * @return el ID del evento
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del evento.
     * @param id el ID del evento
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el título del evento.
     * @return el título del evento
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título del evento.
     * @param title el título del evento
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Obtiene la descripción detallada del evento.
     * @return la descripción del evento
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción detallada del evento.
     * @param description la descripción del evento
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene la fecha del evento.
     * @return la fecha del evento en formato de cadena
     */
    public String getDate() {
        return date;
    }

    /**
     * Establece la fecha del evento.
     * @param date la fecha del evento en formato de cadena
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Obtiene la ubicación donde se realizará el evento.
     * @return la ubicación del evento
     */
    public String getLocation() {
        return location;
    }

    /**
     * Establece la ubicación donde se realizará el evento.
     * @param location la ubicación del evento
     */
    public void setLocation(String location) {
        this.location = location;
    }
}