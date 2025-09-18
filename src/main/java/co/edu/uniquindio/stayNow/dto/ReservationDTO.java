package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


public record ReservationDTO(
//faltan atributos
        Long accommodationId,

        LocalDate checkIn,

        LocalDate checkOut,

        Integer guests


) {
}