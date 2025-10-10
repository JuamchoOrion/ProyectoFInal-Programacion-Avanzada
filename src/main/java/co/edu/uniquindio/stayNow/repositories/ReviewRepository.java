package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByAccommodation(Accommodation accommodation);
    // Obtiene todas las reviews de un alojamiento, más recientes primero
    Page<Review> findByAccommodation_IdOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.id = :accommodationId " +
           "AND (:from IS NULL OR r.createdAt >= :from) " +
           "AND (:to IS NULL OR r.createdAt <= :to)")
    Double averageRatingByAccommodationAndDateRange(
            @Param("accommodationId") Long accommodationId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // Revisar si ya existe una review para una reserva
    boolean existsByReservation_Id(Long reservationId);

    // Todas las reviews de un usuario por su id
    List<Review> findByUser_Id(String userId);

    // Promedio de calificaciones de un alojamiento
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.id = :accommodationId")
    Double getAverageRatingByAccommodation(Long accommodationId);
}
