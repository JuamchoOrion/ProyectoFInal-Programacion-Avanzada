package co.edu.uniquindio.stayNow.model.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    private Long id;
    private String city;
    private String address;
    @OneToOne
    private Location location;

    @OneToOne
    private Accommodation accomodation;


}
