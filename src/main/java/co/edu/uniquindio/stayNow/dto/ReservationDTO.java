package co.edu.uniquindio.stayNow.dto;

import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;


public record ReservationDTO(
//faltan atributos
        //deberia tener GuestID?= PREGUNTAR AL PROFE
        Long id,
        Long accommodationId,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Integer guests,
        Double totalPrice,
        ReservationStatus status


) {
}