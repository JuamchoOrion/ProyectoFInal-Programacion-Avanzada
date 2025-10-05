package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.AccommodationServiceType;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccommodationDTO {
    String id;
    String hostId;
    String city;
    String description;
    String address;
    Double pricePerNight;
    float latitude;
    float longitude;
    int maxGuests;
    List<AccommodationServiceType> accommodationServiceTypes;
    List<String> images;
    String mainImage;
    AccommodationStatus status;

}
