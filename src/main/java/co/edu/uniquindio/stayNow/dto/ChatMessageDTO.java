package co.edu.uniquindio.stayNow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ChatMessageDTO(
        String senderId,
        String receiverId,
        String content,
        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {}
