package com.aja.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una Comunidad (ForumEntity).
 */
public class ForumDto {
    private Long id;
    private String title;

    @JsonProperty("creationDate")
    private String date;

    public ForumDto() {}

    public ForumDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() { return title; }
}