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
    // Get all reviews for an accommodation
    List<Review> findAllByAccommodation(Accommodation accommodation);

    // Get all reviews for an accommodation, newest first
    Page<Review> findByAccommodation_IdOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.id = :accommodationId " +
           "AND (:from IS NULL OR r.createdAt >= :from) " +
           "AND (:to IS NULL OR r.createdAt <= :to)")
    Double averageRatingByAccommodationAndDateRange(
            @Param("accommodationId") Long accommodationId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // Check if a review already exists for a reservation
    boolean existsByReservation_Id(Long reservationId);

    // Get all reviews by user ID
    List<Review> findByUser_Id(String userId);

    // Get average rating of an accommodation
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.id = :accommodationId")
    Double getAverageRatingByAccommodation(Long accommodationId);
}
