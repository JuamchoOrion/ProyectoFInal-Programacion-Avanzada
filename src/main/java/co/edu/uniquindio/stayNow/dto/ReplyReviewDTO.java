package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyReviewDTO(
        @NotBlank(message = "The reply cannot be empty")
        @Size(max = 500, message = "The reply cannot exceed 500 characters")
        String message
) {}