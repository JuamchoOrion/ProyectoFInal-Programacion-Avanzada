package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateReservationDTO(
        @NotNull Long accommodationId,

        @NotNull @Future LocalDateTime checkIn,

        @NotNull @Future LocalDateTime checkOut,

        @NotNull @Min(1) Integer guests
) {
}