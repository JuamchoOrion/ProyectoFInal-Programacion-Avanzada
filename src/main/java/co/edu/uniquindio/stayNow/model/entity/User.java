package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String photoUrl;
    private LocalDate dateBirth;
    private LocalDateTime createdAt;
    private Role role;
    private UserStatus status;
}