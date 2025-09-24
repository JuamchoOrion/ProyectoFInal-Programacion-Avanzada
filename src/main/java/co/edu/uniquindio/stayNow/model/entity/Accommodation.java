package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Service;
import jakarta.persistence.*;
import lombok.*;
import java.awt.image.BufferedImage;
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
    private Long id;
    private String city;
    private String title;
    private String description;
    private int maxGuests;
    private double pricePerNight;
    private double averageRate;
    private LocalDateTime createdAt;
    @OneToOne
    private Address address;
    @ManyToOne
    private User host;
    private AccommodationStatus status;
    //private List<Reservation> bookingList;
    @ElementCollection
    @CollectionTable(name = "accommodation_images", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image")
    private List<String> images;
    @ElementCollection(targetClass = Service.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_services", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "service")
    private Set<Service> services;


}
