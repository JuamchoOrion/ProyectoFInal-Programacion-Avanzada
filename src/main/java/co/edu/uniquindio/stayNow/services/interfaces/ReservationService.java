package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationService {
    ReservationDTO create(CreateReservationDTO reservationDTO) throws Exception;
    Page<Reservation> getReservations(Long userId,
                                      String status,
                                      LocalDate from,
                                      LocalDate to,
                                      Pageable pageable);
}
