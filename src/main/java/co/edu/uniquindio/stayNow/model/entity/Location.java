package co.edu.uniquindio.stayNow.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location {
    @Id
    private Long id;
    private float latitude;
    private float longitude;
    @OneToOne
    private Address address;
}
