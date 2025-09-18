package co.edu.uniquindio.stayNow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCode {
    private long id;
    private String code;
    private LocalDateTime createdAt;
    private User user;
}
