package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    List<Reservation> findAllByAccommodation_Id(Long accommodation_id);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.accommodation.id= :accomodationId " +
            "AND (r.checkIn < :checkOut AND r.checkOut > :checkIn)")
    List<Reservation> findOverLappingReservations(
            @Param("accomodationId") Long accomodationId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);
}