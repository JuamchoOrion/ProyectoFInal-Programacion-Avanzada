package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccomodationDTO {
    String id;
    String hostId;
    String city;
    String description;
    String addres;
    Double pricePerNight;
    int maxGuests;
    List<Service> services;
    List<String> images;
    String mainImage;
    AccommodationStatus status;

}
