package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // opcional, pero recomendado porque "user" puede ser palabra reservada en algunas BD
public class User {

    @Id
    private String id;

    private String name;
    private String phone;
    private String email;
    private String password;
    private String photoUrl;
    private LocalDate dateBirth;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // se guarda como texto ("ACTIVE", "INACTIVE")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "host")
    private List<Accommodation> accommodations;
}
