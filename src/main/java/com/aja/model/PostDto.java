package com.aja.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.aja.model.ForumDto;
import com.aja.model.UserDto;

import java.util.List;

/**
 * DTO que representa un post o hilo del foro (PostEntity).
 * Mapea tanto el resumen para listas como el detalle completo con comentarios.
 */
public class PostDto {
    private Long id;
    private String title;
    private String content; // Cuerpo del post (devuelto por getForum)
    
    private Long topicId;

    @JsonProperty("creationDate")
    private String date;
    
    private UserDto userOwner; // El JSON trae un objeto userOwner (autor del post)
    private ForumDto forum;    // El JSON trae un objeto forum (categoría/topic al que pertenece el post)
    private List<CommentDto> comments; // Comentarios reales vinculados

    public PostDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { 
        if (userOwner == null || userOwner.getUsername() == null) return "anónimo";
        return userOwner.getUsername();
    }

    @JsonAlias("author")
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
    public Long getTopicId() { 
        if (topicId != null) return topicId;
        return forum != null ? forum.getId() : -1L; 
    }

    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getCategory() { 
        if (forum != null) return forum.getTitle(); // Aquí 'forum' actúa como la sección/topic
        return "General"; 
    }
    
    public ForumDto getForum() { return forum; }
    public void setForum(ForumDto forum) { this.forum = forum; }
    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }
}