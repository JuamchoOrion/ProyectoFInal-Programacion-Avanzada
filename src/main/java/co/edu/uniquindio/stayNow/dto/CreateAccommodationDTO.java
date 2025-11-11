package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

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

        Float latitude,

        Float longitude,

        @NotNull @Positive
        Double pricePerNight,

        @NotNull @Min(1)
        Integer maxCapacity,

        @NotNull @Size(min = 1)
        List<@NotBlank String> services,

        @NotNull @Size(min = 1)
        List< MultipartFile> images// nota: cambie esto de list<string>a multipart


) {
}