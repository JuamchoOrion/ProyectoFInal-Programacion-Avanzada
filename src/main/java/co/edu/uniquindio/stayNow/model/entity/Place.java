package co.edu.uniquindio.stayNow.model.entity;

import lombok.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    private String city;
    private String title;
    private String description;
    private double pricePerNight;
    private int maxCant;
    private double averageRate;
    private List<Booking> bookingList;
    private List<String> services;
    private List<BufferedImage> images;

}
