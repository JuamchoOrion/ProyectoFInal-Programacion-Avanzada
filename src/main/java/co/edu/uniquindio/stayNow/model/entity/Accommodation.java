package co.edu.uniquindio.stayNow.model.entity;

import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Service;
import lombok.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accommodation {

    private Long id;
    private String city;
    private String title;
    private String description;
    private int maxGuests;
    private double pricePerNight;
    private double averageRate;
    private LocalDateTime createdAt;
    private Address address;
    private User host;
    private AccommodationStatus status;
    private List<Reservation> bookingList;
    private List<String> images;
    private Set<Service> services;


}
