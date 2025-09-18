package co.edu.uniquindio.stayNow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostProfile {
    private String legalDocument;
    private User user;
    private long id;
    private String aboutMe;
    private LocalDateTime createdAt;
}
