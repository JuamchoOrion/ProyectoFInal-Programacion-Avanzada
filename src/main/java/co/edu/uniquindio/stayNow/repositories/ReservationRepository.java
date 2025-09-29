package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
}
