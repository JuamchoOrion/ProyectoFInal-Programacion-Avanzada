package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.enums.*;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.ReviewRepository;
import co.edu.uniquindio.stayNow.services.implementation.MetricsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceImplTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private MetricsServiceImpl metricsService;

    private Accommodation accommodation;
    private Reservation completedReservation;
    private Reservation canceledReservation;
    private Review review1;
    private Review review2;

    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setTitle("Beautiful House");

        completedReservation = new Reservation();
        completedReservation.setReservationStatus(ReservationStatus.COMPLETED);
        completedReservation.setAccommodation(accommodation);
        completedReservation.setCheckIn(LocalDateTime.now().minusDays(5));
        completedReservation.setCheckOut(LocalDateTime.now().minusDays(3));

        canceledReservation = new Reservation();
        canceledReservation.setReservationStatus(ReservationStatus.CANCELED);
        canceledReservation.setAccommodation(accommodation);
        canceledReservation.setCheckIn(LocalDateTime.now().minusDays(4));
        canceledReservation.setCheckOut(LocalDateTime.now().minusDays(2));

        review1 = new Review();
        review1.setAccommodation(accommodation);
        review1.setRate(4);

        review2 = new Review();
        review2.setAccommodation(accommodation);
        review2.setRate(5);

        from = LocalDateTime.now().minusDays(10);
        to = LocalDateTime.now();
    }

    // ✅ Success: metrics with reservations and reviews
    @Test
    void getAccommodationMetrics_Success() throws Exception {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(completedReservation, canceledReservation));
        when(reviewRepository.findAllByAccommodation(accommodation))
                .thenReturn(List.of(review1, review2));

        MetricsResponseDTO metrics = metricsService.getAccommodationMetrics(1L, from, to);

        assertNotNull(metrics);
        assertEquals(1L, metrics.accommodationId());
        assertEquals("Beautiful House", metrics.accommodationTitle());
        assertEquals(2, metrics.totalReservations());
        assertEquals(1, metrics.completedReservations());
        assertEquals(1, metrics.canceledReservations());
        assertEquals(4.5, metrics.averageRating());
        assertEquals(2, metrics.totalReviews());
        assertEquals(from, metrics.fromDate());
        assertEquals(to, metrics.toDate());
    }

    // ❌ Fails if accommodation not found
    @Test
    void getAccommodationMetrics_Fails_NotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccommodationNotFoundException.class,
                     () -> metricsService.getAccommodationMetrics(1L, from, to));
    }

    // ✅ Metrics with empty reservations and reviews
    @Test
    void getAccommodationMetrics_EmptyReservationsAndReviews() throws Exception {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());
        when(reviewRepository.findAllByAccommodation(accommodation)).thenReturn(Collections.emptyList());

        MetricsResponseDTO metrics = metricsService.getAccommodationMetrics(1L, from, to);

        assertNotNull(metrics);
        assertEquals(0, metrics.totalReservations());
        assertEquals(0, metrics.completedReservations());
        assertEquals(0, metrics.canceledReservations());
        assertEquals(0.0, metrics.averageRating());
        assertEquals(0, metrics.totalReviews());
    }
}
