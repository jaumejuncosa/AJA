package com.aja.model;

/**
 * Objeto para mover los datos de los eventos entre el servidor y la aplicación.
 */
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private String date;
    private String location;

    /**
     * El ID único que tiene el evento en la base de datos.
     */
    public Long getId() {
        return id;
    }

    /**
     * Guardamos el ID del evento.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * El nombre o título que le hemos dado al evento.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Guardamos el título del evento.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * El texto con todos los detalles de lo que va a pasar en el evento.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Guardamos la descripción del evento.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Cuándo se va a celebrar el evento (viene como texto desde la API).
     */
    public String getDate() {
        return date;
    }

    /**
     * Guardamos la fecha del evento.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * El sitio o lugar donde se hará el evento.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Guardamos la ubicación del evento.
     */
    public void setLocation(String location) {
        this.location = location;
    }
}