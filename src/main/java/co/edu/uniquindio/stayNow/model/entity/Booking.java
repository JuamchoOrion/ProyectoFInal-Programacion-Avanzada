package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private LocalDate creationDate;
    private LocalTime checkIn;
    private LocalDate checkOut;
    private int guestsNumber;
    private BookingStatus bookingStatus;
}
