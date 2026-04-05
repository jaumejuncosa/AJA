package com.aja.model;

/**
 * DTO para representar una categoría o comunidad (Topic) del foro.
 */
public class TopicDto {
    private Long id;
    private String title;
    private String description;

    public TopicDto() {}

    public TopicDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return title; }
}