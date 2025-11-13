package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.*;

public record EditPasswordRequestDTO(
        @Email@NotNull String email,
        @NotNull String code,
        @NotNull@Size(min = 8) String newPassword
) {

}
