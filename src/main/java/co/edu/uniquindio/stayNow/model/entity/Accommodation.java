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
    @Column
    private String city;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private int maxGuests;
    @Column
    private double pricePerNight;
@Column
    private double averageRate;
@Column
    private LocalDateTime createdAt;
    //Many To One
    @Transient
    private Address address;

    @ManyToOne
    private User host;
@Column
    private AccommodationStatus status;

    //One To Many
    @Transient
    private List<Reservation> bookingList;
    @Transient
    private List<String> images;
    @Transient
    private Set<Service> services;
//ACOMODATION PUEDE EXISTIR SIN LA REVIEW
    //private List<Review> reviews;


}
