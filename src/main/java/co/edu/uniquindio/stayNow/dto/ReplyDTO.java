package co.edu.uniquindio.stayNow.dto;

import java.time.LocalDateTime;

public record ReplyDTO(
        Long id,
        String message,
        LocalDateTime repliedAt
) {}
