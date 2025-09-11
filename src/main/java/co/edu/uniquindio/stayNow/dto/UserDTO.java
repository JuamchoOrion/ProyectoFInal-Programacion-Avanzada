package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.Role;

public record UserDTO(
        String id,
        String name,
        String email,
        String photoUrl,
        String password,
        Role role
) {
}
