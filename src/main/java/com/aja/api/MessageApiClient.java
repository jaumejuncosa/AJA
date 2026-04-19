package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.MessageDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class MessageApiClient extends BaseApiClient {
    
    /**
     * Preparamos el sistema para enviar y recibir mensajes.
     */
    public MessageApiClient() {
        super();    
    }

    /**
     * Pedimos la lista de todos los mensajes guardados.
     */
    public List<MessageDto> getAllMessages() throws Exception {
        return get("/api/messages", new TypeReference<ApiResponse<List<MessageDto>>>() {});
    }

    /**
     * Mandamos un mensaje nuevo al sistema.
     */
    public MessageDto createMessage(MessageDto message) throws Exception {
        return post("/api/messages", message, new TypeReference<ApiResponse<MessageDto>>() {});
    }
}