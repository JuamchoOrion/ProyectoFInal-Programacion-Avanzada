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

    @Query("SELECT r FROM Reservation r " +
            "WHERE (:userId IS NULL OR r.guest.id = :userId) " +
            "AND (:hostId IS NULL OR r.accommodation.host.id = :hostId) " +

            "AND (:status IS NULL OR r.reservationStatus = :status) " +

            "AND (COALESCE(:from, r.createdAt) <= r.createdAt AND r.createdAt <= COALESCE(:to, r.createdAt)) " +

            "AND (COALESCE(:checkInTime, r.checkIn) <= r.checkIn) " +
            "AND (COALESCE(:checkOutTime, r.checkOut) >= r.checkOut) " +

            "ORDER BY r.createdAt DESC")
    Page<Reservation> findReservationsWithFilters(
            @Param("userId") String userId,
            @Param("hostId") String hostId,
            @Param("status") ReservationStatus status,
            // Aquí usamos LocalDateTime para filtrar la fecha de creación
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            // Usamos nuevos nombres para evitar conflicto con los de LocalDate
            @Param("checkInTime") LocalDateTime checkIn,
            @Param("checkOutTime") LocalDateTime checkOut,
            Pageable pageable
    );
}
