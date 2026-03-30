package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.EventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class EventApiClient extends BaseApiClient {
    
    /**
     * Constructor: Preparamos el cliente para gestionar eventos.
     */
    public EventApiClient() {
        super();
    }

    /**
     * Pedimos a la API la lista de eventos programados.
     */
    public List<EventDto> getAllEvents() throws Exception {
        return get("/api/events", new TypeReference<ApiResponse<List<EventDto>>>() {});
    }

    /**
     * Registramos un nuevo evento en la base de datos.
     */
    public EventDto createEvent(EventDto event) throws Exception {
        return post("/api/events", event, new TypeReference<ApiResponse<EventDto>>() {});
    }
}