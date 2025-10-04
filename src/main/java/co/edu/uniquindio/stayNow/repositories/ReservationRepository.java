package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByAccommodation_Id(Long accommodation_id);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.accommodation.id= :accomodationId " +
            "AND (r.checkIn < :checkOut AND r.checkOut > :checkIn)")
    List<Reservation> findOverLappingReservations(
            @Param("accomodationId") Long accomodationId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.accommodation.id = :accommodationId " +
           "AND (:from IS NULL OR r.checkIn >= :from) " +
           "AND (:to IS NULL OR r.checkOut <= :to)")
    Long countByAccommodationAndDateRange(
            @Param("accommodationId") Long accommodationId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}