package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.ChatMessageDTO;
import co.edu.uniquindio.stayNow.model.entity.chat.ChatMessage;
import co.edu.uniquindio.stayNow.model.enums.MessageStatus;
import co.edu.uniquindio.stayNow.repositories.ChatMessageRepository;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthServiceImp authService;

    // Enviar mensaje por WebSocket
    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessageDTO mensaje, Principal principal) throws Exception {
        if (principal == null) throw new IllegalArgumentException("Usuario no autenticado");

        String senderId = principal.getName();

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(mensaje.getReceiverId())
                .content(mensaje.getContent())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + mensaje.getReceiverId(), mensaje);
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
                .map(m -> new ChatMessageDTO(m.getReceiverId(), m.getContent()))
                .toList();

        return ResponseEntity.ok(dto);
    }
}
