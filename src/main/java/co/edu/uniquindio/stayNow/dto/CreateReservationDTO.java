package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateReservationDTO(
        @NotNull Integer accommodationId,

        @NotNull @Future LocalDate checkIn,

        @NotNull @Future LocalDate checkOut,

        @NotNull @Min(1) Integer guests
) {
}