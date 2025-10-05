package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationService {
    ReservationDTO create(CreateReservationDTO reservationDTO) throws Exception;

    // Se actualiza la firma para recibir todos los filtros y delegar el manejo de ID al ServiceImp
    Page<Reservation> getReservations(
            String status,
            LocalDateTime from,
            LocalDateTime to,
            LocalDateTime checkIn,
            LocalDateTime checkOut,
            Pageable pageable) throws Exception;

    ReservationDTO getReservationById(Long id) throws Exception;

    ReservationDTO cancelReservation(Long reservationId) throws Exception;
}