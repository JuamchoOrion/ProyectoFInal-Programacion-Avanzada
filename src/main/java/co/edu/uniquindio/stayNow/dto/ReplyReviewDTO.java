package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyReviewDTO(
        @NotBlank(message = "La respuesta no puede estar vacía")
        @Size(max = 500, message = "La respuesta no puede tener más de 500 caracteres")
        String reply
) {}