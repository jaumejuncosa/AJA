package com.aja.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO que representa un hilo del foro (ForumEntity).
 * Mapea tanto el resumen para listas como el detalle completo con comentarios.
 */
public class ForumDto {
    private Long id;
    private String title;
    private String content; // Cuerpo del post (devuelto por getForum)
    
    @JsonProperty("creationDate")
    private String date;
    
    private UserDto userOwner; // El JSON trae un objeto userOwner
    private TopicDto forum;    // El JSON trae un objeto forum (Categoría)
    private List<CommentDto> comments; // Comentarios reales vinculados

    public ForumDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return userOwner != null ? userOwner.getUsername() : "anónimo"; }

    public void setAuthor(String author) {
        if (this.userOwner == null) {
            this.userOwner = new UserDto();
        }
        this.userOwner.setUsername(author);
    }

    public void setUserOwner(UserDto userOwner) { this.userOwner = userOwner; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    /**
     * Devuelve el ID de la comunidad (forum) asociada a este post.
     */
    public Long getForumId() { return forum != null ? forum.getId() : -1L; }

    public String getCategory() { return forum != null ? forum.getTitle() : "General"; }
    public void setForum(TopicDto forum) { this.forum = forum; }

    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }
}