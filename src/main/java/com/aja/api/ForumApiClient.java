package com.aja.api;

import com.aja.model.ForumDto;
import com.aja.model.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class ForumApiClient extends BaseApiClient {

    public List<ForumDto> getAllForumPosts() throws Exception {
        return get("/api/topic", new TypeReference<ApiResponse<List<ForumDto>>>() {});
    }

    public ForumDto getForumById(Long id) throws Exception {
        return get("/api/topic/" + id, new TypeReference<ApiResponse<ForumDto>>() {});
    }

    public String createForumPost(ForumDto post) throws Exception {
        return post("/api/topic", post, new TypeReference<ApiResponse<String>>() {});
    }

    public String updateForumPost(ForumDto post) throws Exception {
        return put("/api/topic", post, new TypeReference<ApiResponse<String>>() {});
    }

    public String deleteForumPost(Long id) throws Exception {
        return delete("/api/topic/" + id, new TypeReference<ApiResponse<String>>() {});
    }
}