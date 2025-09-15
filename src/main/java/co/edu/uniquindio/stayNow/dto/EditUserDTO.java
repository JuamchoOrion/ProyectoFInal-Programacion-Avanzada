package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record EditUserDTO(
        @NotBlank @Length(max = 100) String name,
        @Email@NotNull String email,
        @Length(max = 10) String phone,
        @NotNull @NotBlank @Length(min = 8) String password,
        @Length(max = 300) String photoUrl,
        @NotNull @Past LocalDate dateBirth,
        @NotNull Role role
) {
}