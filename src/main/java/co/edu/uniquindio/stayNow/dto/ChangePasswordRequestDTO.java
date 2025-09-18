package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequestDTO(
        @NotNull String currentPassword,
        @NotNull String newPassword
) {
}
