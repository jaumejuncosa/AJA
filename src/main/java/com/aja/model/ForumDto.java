package com.aja.model;

/**
 * Este objeto guarda la información de cada tema o post del foro.
 */
public class ForumDto {

    private Long id;
    private String title;
    private String author;
    private String date;

    /**
     * El ID del post en el foro.
     */
    public Long getId() {
        return id;
    }

    /**
     * Guardamos el ID del post.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * El título que se le ha puesto al hilo del foro.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Guardamos el título del post.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * El nombre de usuario de la persona que escribió el post.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Guardamos quién es el autor.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Cuándo se publicó el post originalmente.
     */
    public String getDate() {
        return date;
    }

    /**
     * Guardamos la fecha de publicación.
     */
    public void setDate(String date) {
        this.date = date;
    }
}