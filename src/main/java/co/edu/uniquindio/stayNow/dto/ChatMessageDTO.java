package co.edu.uniquindio.stayNow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
}

