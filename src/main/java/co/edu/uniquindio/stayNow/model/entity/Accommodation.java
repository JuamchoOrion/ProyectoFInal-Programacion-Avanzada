package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.AccommodationServiceType;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String title;
    private String description;
    private int maxGuests;
    private double pricePerNight;
    private double averageRate;
    private LocalDateTime createdAt;
    @Embedded
    private Address address;
    @ManyToOne
    private User host;
    @Enumerated(EnumType.STRING)
    private AccommodationStatus status;

    @ElementCollection
    @CollectionTable(name = "accommodation_images", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image")
    private List<String> images;

    @ElementCollection(targetClass = AccommodationServiceType.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "accommodation_services",
            joinColumns = @JoinColumn(name = "accommodation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "service")
    private Set<AccommodationServiceType> accommodationServiceTypes;
    //Con el mappedBy se especifica cual tabla tiene la fk en este caso una reserva tiene la fk para accommodation
    @OneToMany(mappedBy = "accommodation")
    private List<Reservation> reservations;


}
