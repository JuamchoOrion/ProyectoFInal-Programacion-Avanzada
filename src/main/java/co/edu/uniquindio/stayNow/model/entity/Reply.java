package co.edu.uniquindio.stayNow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    String message;
    LocalDateTime repliedAt;

}
