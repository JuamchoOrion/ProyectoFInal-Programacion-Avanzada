package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.Role;

import java.time.LocalDate;

public record RegisterRequestDTO(
        String name,
        String email,
        String password,
        Role role,
        LocalDate fechaAcimieto
                                     ) {
}
