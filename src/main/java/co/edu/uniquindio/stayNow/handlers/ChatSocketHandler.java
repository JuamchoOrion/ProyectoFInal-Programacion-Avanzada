package co.edu.uniquindio.stayNow.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
@RequiredArgsConstructor
public class ChatSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        String userId = path.substring(path.lastIndexOf("/") + 1);

        System.out.println("🟢 WS conectado: " + userId);
        sessions.put(userId, session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload().toString(), Map.class);
        String receiverId = payload.get("receiverId").toString();

        sendToUser(receiverId, payload);   // 🔥 reenviar al destinatario
    }

    public void sendToUser(String userId, Object payload) {
        try {
            WebSocketSession s = sessions.get(userId);
            if (s != null && s.isOpen()) {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession s, Throwable ex) {
        System.out.println("❌ Error WS: " + ex.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession s, CloseStatus cs) {
        sessions.values().remove(s);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

