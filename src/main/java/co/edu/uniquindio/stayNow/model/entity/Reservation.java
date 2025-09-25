package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDateTime;

import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int guestsNumber;
    private Double totalPrice;
    @ManyToOne
    private User guest;

    private ReservationStatus reservationStatus;
    @ManyToOne
    private Accommodation accommodation;
}
