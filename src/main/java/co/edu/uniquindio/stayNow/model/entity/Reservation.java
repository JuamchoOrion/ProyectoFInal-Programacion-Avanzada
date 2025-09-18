package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDateTime;

import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int guestsNumber;
    private Double totalPrice;
    //Many To One
    private User guest;

    private ReservationStatus reservationStatus;
    //Many To One
    private Accommodation accommodation;
}
