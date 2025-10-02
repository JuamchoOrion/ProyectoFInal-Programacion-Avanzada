package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Service;
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
    private String id;
    private String city;
    private String title;
    private String description;
    private int maxGuests;
    private double pricePerNight;
    private double averageRate;
    private LocalDateTime createdAt;

    @Embedded
    private Address address;

    @ManyToOne // This maps to the Host User entity
    private User host;

    @Enumerated(EnumType.STRING)
    private AccommodationStatus status;

    @ElementCollection
    @CollectionTable(name = "accommodation_images", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image")
    private List<String> images;

    @ElementCollection(targetClass = Service.class)
    @Enumerated(EnumType.STRING)
    // CORRECTED: Ensure table and join column names reflect the Accommodation entity
    @CollectionTable(name = "accommodation_services", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "service")
    private Set<Service> services;
}