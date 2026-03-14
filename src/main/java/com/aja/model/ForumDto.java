package com.aja.model;

/**
 * Representa una entrada o publicación en el foro del sistema.
 * Contiene la información básica necesaria para mostrar publicaciones
 * en el foro, incluyendo el autor y la fecha de publicación.
 */
public class ForumDto {

    private Long id;
    private String title;
    private String author;
    private String date;

    /**
     * Obtiene el identificador único de la publicación del foro.
     * @return el ID de la publicación
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la publicación del foro.
     * @param id el ID de la publicación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el título de la publicación del foro.
     * @return el título de la publicación
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título de la publicación del foro.
     * @param title el título de la publicación
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Obtiene el nombre del autor de la publicación.
     * @return el nombre del autor
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Establece el nombre del autor de la publicación.
     * @param author el nombre del autor
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Obtiene la fecha en que se creó la publicación.
     * @return la fecha de la publicación en formato de cadena
     */
    public String getDate() {
        return date;
    }

    /**
     * Establece la fecha en que se creó la publicación.
     * @param date la fecha de la publicación en formato de cadena
     */
    public void setDate(String date) {
        this.date = date;
    }
}