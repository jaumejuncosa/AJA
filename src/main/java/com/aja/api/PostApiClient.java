package com.aja.api;

import com.aja.model.PostDto;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.aja.model.PostMessageDto;
import java.util.List;

public class PostApiClient extends BaseApiClient {

    /**
     * Pedimos la lista de todos los mensajes escritos en el foro.
     */
    public List<PostDto> getAllPosts() throws Exception {
    return get("/api/topic", new TypeReference<ApiResponse<List<PostDto>>>() {});
    }


    /**
     * Buscamos un mensaje concreto usando su número.
     */
    public PostDto getPostById(Long id) throws Exception {
    return get("/api/topic/" + id, new TypeReference<ApiResponse<PostDto>>() {});
    }

    /**
     * Añadimos un mensaje nuevo al foro.
     */
    public Object addPost(PostDto postDto) throws Exception {
        return post("/api/post", postDto, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Cambiamos el contenido de un mensaje que ya existe.
     */
    public Object editPost(PostDto postDto) throws Exception {
        return put("/api/post/" + postDto.getId(), postDto, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Borramos un mensaje del foro.
     */
    public Object delPost(Long id) throws Exception {
        return delete("/api/post/" + id, new TypeReference<ApiResponse<Object>>() {});
    }

    public Object addComment(String text, Long topicId) throws Exception {
    java.util.Map<String, Object> body = new java.util.HashMap<>();
    body.put("text", text);
    body.put("topicId", topicId);
    return post("/api/post", body, new TypeReference<ApiResponse<Object>>() {});
    }

    public List<PostMessageDto> getCommentsByTopicId(Long topicId) throws Exception {
    List<PostMessageDto> all = get("/api/post", new TypeReference<ApiResponse<List<PostMessageDto>>>() {});
    if (all == null) return new java.util.ArrayList<>();
    return all.stream()
        .filter(p -> p.getTopic() != null && topicId.equals(p.getTopic().getId()))
        .collect(java.util.stream.Collectors.toList());
    }

    public Object editComment(Long id, String text) throws Exception {
    java.util.Map<String, Object> body = new java.util.HashMap<>();
    body.put("id", id);
    body.put("text", text);
    return put("/api/post", body, new TypeReference<ApiResponse<Object>>() {});
    }

    public Object deleteComment(Long id) throws Exception {
    return delete("/api/post/" + id, new TypeReference<ApiResponse<Object>>() {});
    }

}