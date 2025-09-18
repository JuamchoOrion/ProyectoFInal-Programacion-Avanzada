package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import java.util.List;

public record CreateAccommodationDTO(

        @NotBlank @Length(max = 100)
        String title,

        @NotBlank @Length(max = 1000)
        String description,

        @NotBlank @Length(max = 100)
        String city,

        @NotBlank @Length(max = 200)
        String address,

        @NotNull @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
        Double latitude,

        @NotNull @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
        Double longitude,

        @NotNull @Positive
        Double pricePerNight,

        @NotNull @Min(1)
        Integer maxGuests,

        @NotNull @Size(min = 1)
        List<@NotBlank String> services,

        @NotNull @Size(min = 1)
        List<@NotBlank String> images,

        @NotBlank
        String mainImage,

        @NotNull @Min(1)
        Integer minImage,

        @NotNull @Max(20)
        Integer maxImages
) {
}