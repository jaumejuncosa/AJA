package com.aja.api;
import com.aja.model.ApiResponse;
import com.aja.model.ForumDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
/**
 * Cliente para interactuar con los temas y posts del foro en la API.
 */
 public class ForumApiClient extends BaseApiClient {
    
    /**
     * Constructor: Preparamos la base para las peticiones del foro.
     */
    public ForumApiClient() {
        super();
    }

    /**
     * Obtenemos todos los hilos de conversación abiertos en el foro.
     */
    public List<ForumDto> getAllForumPosts() throws Exception {
        return get("/api/forum", new TypeReference<ApiResponse<List<ForumDto>>>() {});
    }

    /**
     * Publicamos un nuevo tema en el foro.
     */
    public ForumDto createForumPost(ForumDto forumPost) throws Exception {
        return post("/api/forum", forumPost, new TypeReference<ApiResponse<ForumDto>>() {});
    }
}