package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.ChatMessageDTO;
import co.edu.uniquindio.stayNow.handlers.ChatSocketHandler;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.entity.chat.ChatMessage;
import co.edu.uniquindio.stayNow.model.enums.MessageStatus;
import co.edu.uniquindio.stayNow.repositories.ChatMessageRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthServiceImp authService;
    private final UserRepository userRepository;
    private final ChatSocketHandler socket;   // <--- USAMOS ESTE
    private final ObjectMapper mapper = new ObjectMapper();

    // ================================
    // Enviar mensaje
    // ================================
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestBody ChatMessageDTO mensaje,
            @CookieValue(name = "jwt", required = false) String jwtToken) throws Exception {

        String senderId = authService.getUserIDFromToken(jwtToken);
        if (senderId == null)
            return ResponseEntity.status(401).body("Token inválido");

        ChatMessage msg = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(mensaje.receiverId())
                .content(mensaje.content())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(msg);

        ChatMessageDTO dto = new ChatMessageDTO(
                senderId,
                mensaje.receiverId(),
                mensaje.content(),
                msg.getTimestamp()
        );

        // Enviar al receptor
        socket.sendToUser(mensaje.receiverId(), dto);

        // Enviar al remitente
        socket.sendToUser(senderId, dto);

        return ResponseEntity.ok("Mensaje enviado");
    }
    // ================================
    // Obtener historial entre 2 usuarios
    // ================================
    @GetMapping("/{friendId}")
    public ResponseEntity<List<ChatMessageDTO>> getChat(
            @PathVariable String friendId) throws Exception {

        String me = authService.getUserID();

        List<ChatMessage> mensajes = chatMessageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                        me, friendId,
                        me, friendId
                );

        return ResponseEntity.ok(
                mensajes.stream()
                        .map(m -> new ChatMessageDTO(
                                m.getSenderId(),
                                m.getReceiverId(),
                                m.getContent(),
                                m.getTimestamp()
                        )).toList()
        );
    }

    // ================================
    //  Lista de contactos
    // ================================
    @GetMapping("/contacts")
    public ResponseEntity<List<String>> getContacts() throws Exception {
        String me = authService.getUserID();
        return ResponseEntity.ok(chatMessageRepository.findDistinctContacts(me));
    }

    // ================================
    //  Iniciar chat NUEVO por email
    // ================================
    @PostMapping("/start/{email}")
    public ResponseEntity<String> startChat(
            @PathVariable String email,
            @RequestBody ChatMessageDTO dto) throws Exception {

        String me = authService.getUserID();

        User receiver = userRepository.findByEmail(email).orElse(null);
        if (receiver == null)
            return ResponseEntity.badRequest().body("Correo inválido");

        ChatMessage msg = ChatMessage.builder()
                .senderId(me)
                .receiverId(receiver.getId())
                .content(dto.content())
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(msg);

        // Notificar tiempo real
        ChatMessageDTO live = new ChatMessageDTO(
                me,
                receiver.getId(),
                dto.content(),
                msg.getTimestamp()
        );

        socket.sendToUser(receiver.getId(), live);
        socket.sendToUser(me, live);

        return ResponseEntity.ok("Chat iniciado");
    }
}
