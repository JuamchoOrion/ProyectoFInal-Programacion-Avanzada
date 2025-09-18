package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Review {
    User user;
    Reply reply;
    Accommodation accommodation;
    LocalDateTime createdAt;
}
