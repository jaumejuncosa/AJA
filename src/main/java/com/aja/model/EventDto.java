package com.aja.model;

/**
 * Ficha para guardar y mover los datos de las actividades.
 */
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private String date;
    private String location;

    /**
     * El número de identificación de la actividad.
     */
    public Long getId() {
        return id;
    }

    /**
     * Guardamos el número de identificación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * El nombre de la actividad.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Guardamos el nombre.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Detalles sobre qué pasará en la actividad.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Guardamos los detalles.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Cuándo se hará la actividad.
     */
    public String getDate() {
        return date;
    }

    /**
     * Guardamos la fecha.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Dónde se hará la actividad.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Guardamos el sitio.
     */
    public void setLocation(String location) {
        this.location = location;
    }
}