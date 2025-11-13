package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.ChatMessageDTO;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.entity.chat.ChatMessage;
import co.edu.uniquindio.stayNow.model.enums.MessageStatus;
import co.edu.uniquindio.stayNow.repositories.ChatMessageRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthServiceImp authService;
    private final UserRepository userRepository;

    // Enviar mensaje por WebSocket
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestBody ChatMessageDTO mensaje,
            @CookieValue(name = "jwt", required = false) String jwtToken) throws Exception {

        if (jwtToken == null || jwtToken.isBlank()) {
            return ResponseEntity.status(401).body("❌ No se encontró token JWT en la cookie");
        }

        // 🔐 Obtener ID del usuario desde el JWT
        String senderId = authService.getUserIDFromToken(jwtToken);
        if (senderId == null) {
            return ResponseEntity.status(401).body("❌ Token inválido o expirado");
        }

        // 💾 Guardar mensaje en BD
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(mensaje.getReceiverId())
                .content(mensaje.getContent())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(chatMessage);

        // 📡 Notificar al receptor por WebSocket (si está conectado)
        messagingTemplate.convertAndSend("/topic/chat/" + mensaje.getReceiverId(), mensaje);

        return ResponseEntity.ok("✅ Mensaje enviado correctamente");
    }

    // Consultar todos los mensajes con un amigo
    @GetMapping("/{friendId}")
    public ResponseEntity<List<ChatMessageDTO>> getChat(@PathVariable String friendId) throws Exception {
        String currentUserId = authService.getUserID();

        List<ChatMessage> mensajes = chatMessageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                        currentUserId, friendId,
                        currentUserId, friendId
                );

        List<ChatMessageDTO> dto = mensajes.stream()
                .map(m -> new ChatMessageDTO(m.getReceiverId(), m.getContent(),m.getSenderId(), m.getTimestamp()))
                .toList();

        return ResponseEntity.ok(dto);
    }
    @GetMapping("/contacts")
    public ResponseEntity<List<String>> getUserChats() throws Exception {
        String currentUserId = authService.getUserID();

        List<String> contacts = chatMessageRepository.findDistinctContacts(currentUserId);

        return ResponseEntity.ok(contacts);
    }

    @PostMapping("/start/{email}")
    public ResponseEntity<String> startChat(@PathVariable String email, @RequestBody ChatMessageDTO dto) throws Exception {
        String currentUserId = authService.getUserID();

        // ✅ Buscar usuario destino por correo

        User receiver = userRepository.findByEmail(email).get();
        if (receiver == null) {
            return ResponseEntity.badRequest().body("❌ No existe ningún usuario con ese correo");
        }

        // ✅ Crear mensaje inicial
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(currentUserId)
                .receiverId(receiver.getId())
                .content(dto.getContent())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(chatMessage);

        // ✅ Notificar por WebSocket (si está conectado)
        messagingTemplate.convertAndSend("/topic/chat/" + receiver.getId(), dto);

        return ResponseEntity.ok("✅ Chat iniciado correctamente");
    }


}
