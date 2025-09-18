package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditPasswordRequestDTO(
        @Email@NotNull String email,
        @NotNull String code,
        @NotNull@Min(8) String newPassword
) {

}
