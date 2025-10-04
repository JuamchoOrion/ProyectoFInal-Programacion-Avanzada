package co.edu.uniquindio.stayNow.services;


import co.edu.uniquindio.stayNow.dto.CreateReviewDTO;
import co.edu.uniquindio.stayNow.exceptions.DuplicateReviewException;
import co.edu.uniquindio.stayNow.model.entity.*;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.ReviewRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.services.implementation.ReviewServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private Accommodation accommodation;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simular usuario y alojamiento
        user = new User();
        user.setId("USER-001");
        user.setName("Juan Esteban");

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setTitle("Cabaña en el bosque");

        // Simular reserva completada
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setGuest(user);
        reservation.setAccommodation(accommodation);
        reservation.setCheckOut(LocalDateTime.now().minusDays(2)); // Ya pasó el checkout
    }

    @Test
    void crearReview_exitosa() throws Exception {
        // Arrange
        CreateReviewDTO dto = new CreateReviewDTO(1L, 5, "Excelente");

        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Review review = reviewService.createReview(dto.reservationId(), "USER-001", dto.text(), dto.rating());

        // Assert
        assertNotNull(review);
        assertEquals(5, review.getRating());
        assertEquals("Excelente", review.getComment());
        assertEquals(user, review.getUser());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void crearReview_falla_siYaExisteUnaReseña() {
        // Arrange
        CreateReviewDTO dto = new CreateReviewDTO(1L, 4, "Muy Buena");
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateReviewException.class, () ->
                reviewService.createReview(dto.reservationId(), "USER-001", dto.text(), dto.rating())
        );
    }

    @Test
    void crearReview_falla_siLaReservaNoHaTerminado() {
        // Arrange
        reservation.setCheckOut(LocalDateTime.now().plusDays(1)); // Aún no termina
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        Exception ex = assertThrows(Exception.class, () ->
                reviewService.createReview(1L, "USER-001", "Comentario", 4)
        );
        assertTrue(ex.getMessage().contains("aún no ha finalizado") || ex.getMessage().contains("no ha sido completada"));
    }
}
