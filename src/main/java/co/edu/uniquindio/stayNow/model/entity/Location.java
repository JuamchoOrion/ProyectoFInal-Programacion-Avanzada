package co.edu.uniquindio.stayNow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private float latitude;
    private float longitude;
}
