package co.edu.uniquindio.stayNow.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        Long userId,
        String userName,
        int rating,
        String comment,
        LocalDateTime createdAt,
        String reply
) {}