package com.aja.api;

import com.aja.model.ForumDto;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

/**
 * Herramienta para manejar los grupos o comunidades del foro.
 */
public class ForumApiClient extends BaseApiClient {

    /**
     * Pedimos la lista de todas las comunidades disponibles.
     */
    public List<ForumDto> getAllForums() throws Exception {
        return get("/api/forum", new TypeReference<ApiResponse<List<ForumDto>>>() {});
    }

    /**
     * Buscamos los datos de una comunidad usando su número.
     */
    public ForumDto getForumById(Long id) throws Exception {
        return get("/api/forum/" + id, new TypeReference<ApiResponse<ForumDto>>() {});
    }
}