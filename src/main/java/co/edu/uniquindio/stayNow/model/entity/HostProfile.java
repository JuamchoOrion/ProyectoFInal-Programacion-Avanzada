package co.edu.uniquindio.stayNow.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HostProfile {
    @Id
    private long id;
    private String legalDocument;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private String aboutMe;
    private LocalDateTime createdAt;
}
