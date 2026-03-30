package com.aja.api;

import com.aja.model.ApiResponse;
import com.aja.model.MessageDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class MessageApiClient extends BaseApiClient {
    
    /**
     * Constructor: Inicializa la base para los mensajes.
     */
    public MessageApiClient() {
        super();    
    }

    /**
     * Recuperamos la lista de todos los mensajes del servidor.
     */
    public List<MessageDto> getAllMessages() throws Exception {
        return get("/api/messages", new TypeReference<ApiResponse<List<MessageDto>>>() {});
    }

    /**
     * Mandamos un nuevo mensaje al sistema.
     */
    public MessageDto createMessage(MessageDto message) throws Exception {
        return post("/api/messages", message, new TypeReference<ApiResponse<MessageDto>>() {});
    }
}