package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.NotNull;

public record TokenDTO(
        @NotNull String token
) {
}
