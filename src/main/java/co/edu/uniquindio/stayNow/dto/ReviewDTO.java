package co.edu.uniquindio.stayNow.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        String userId,
        String userName,
        int rating,
        String text,
        LocalDateTime createdAt,
        String reply
) {}