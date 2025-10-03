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
@Embeddable
public class Address {
    @Column(name = "address_city")
    private String city;
    private String address;
    @Embedded
    private Location location;
}
