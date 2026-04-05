package com.aja.api;

import com.aja.model.TopicDto;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

/**
 * Cliente API para gestionar las categorías (Topics) del foro.
 */
public class TopicApiClient extends BaseApiClient {
    
    /**
     * Obtiene la lista de comunidades/categorías desde /api/topic.
     */
    public List<TopicDto> getAllTopics() throws Exception {
        return get("/api/forum", new TypeReference<ApiResponse<List<TopicDto>>>() {});
    }
}