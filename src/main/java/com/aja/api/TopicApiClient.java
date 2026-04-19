package com.aja.api;

import com.aja.api.BaseApiClient;
import com.aja.model.TopicDto;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

/**
 * Herramienta para manejar los temas del foro.
 */
public class TopicApiClient extends BaseApiClient {
    
    /**
     * Pedimos la lista de todos los temas o categorías.
     */
    public List<TopicDto> getAllTopics() throws Exception {
        return get("/api/topic", new TypeReference<ApiResponse<List<TopicDto>>>() {});
    }

    public Object addTopic(String title, Long forumId) throws Exception {
    java.util.Map<String, Object> body = new java.util.HashMap<>();
    body.put("title", title);
    body.put("forumId", forumId);
    return post("/api/topic", body, new TypeReference<ApiResponse<Object>>() {});
    }
    
    public void deleteTopic(Long id) throws Exception {
    delete("/api/topic/" + id, new TypeReference<ApiResponse<String>>() {});
    }

    public Object editTopic(Long id, String title, Long currentForumId, Long newForumId) throws Exception {
    java.util.Map<String, Object> body = new java.util.HashMap<>();
    body.put("id", id);
    body.put("title", title);
    body.put("currentForumId", currentForumId);
    body.put("newForumId", newForumId != null ? newForumId : currentForumId);
    return put("/api/topic", body, new TypeReference<ApiResponse<Object>>() {});
}

}