package co.edu.uniquindio.stayNow.handlers;

import co.edu.uniquindio.stayNow.dto.ChatMessageDTO;
import co.edu.uniquindio.stayNow.model.entity.chat.ChatMessage;
import co.edu.uniquindio.stayNow.model.enums.MessageStatus;
import co.edu.uniquindio.stayNow.repositories.ChatMessageRepository;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthServiceImp authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Almacena las sesiones activas por usuario
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = authService.getUserID(); // obtiene usuario autenticado de la cookie
        sessions.put(userId, session);
        System.out.println("✅ Usuario conectado al WebSocket: " + userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userId = authService.getUserID();
        ChatMessageDTO dto = objectMapper.readValue(message.getPayload(), ChatMessageDTO.class);

        // Guardar el mensaje en BD
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(userId)
                .receiverId(dto.getReceiverId())
                .content(dto.getContent())
                .status(MessageStatus.SENT)
                .build();
        chatMessageRepository.save(chatMessage);

        // Enviar el mensaje al destinatario si está conectado
        WebSocketSession receiverSession = sessions.get(dto.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
        }

        System.out.println("💾 Mensaje guardado y enviado de " + userId + " a " + dto.getReceiverId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        System.out.println("🔴 Sesión cerrada: " + session.getId());
    }
}
