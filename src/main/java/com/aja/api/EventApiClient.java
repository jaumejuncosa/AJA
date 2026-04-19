package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.EventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class EventApiClient extends BaseApiClient {
    
    /**
     * Preparamos el sistema para manejar las actividades.
     */
    public EventApiClient() {
        super();
    }

    /**
     * Pedimos la lista de todas las actividades programadas.
     */
    public List<EventDto> getAllEvents() throws Exception {
        return get("/api/events", new TypeReference<ApiResponse<List<EventDto>>>() {});
    }

    /**
     * Creamos una actividad nueva.
     */
    public EventDto createEvent(EventDto event) throws Exception {
        return post("/api/events", event, new TypeReference<ApiResponse<EventDto>>() {});
    }
}