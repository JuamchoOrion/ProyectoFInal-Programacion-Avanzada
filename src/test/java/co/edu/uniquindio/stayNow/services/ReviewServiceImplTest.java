package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.CreateReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReplyDTO;
import co.edu.uniquindio.stayNow.dto.ReplyReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReviewDTO;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.mappers.ReplyMapper;
import co.edu.uniquindio.stayNow.mappers.ReviewMapper;
import co.edu.uniquindio.stayNow.model.entity.*;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.implementation.ReviewServiceImpl;

import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private ReplyRepository replyRepository;
    @Mock private AuthService authService;
    @Mock private ReviewMapper reviewMapper;
    @Mock private ReplyMapper replyMapper;

    @InjectMocks private ReviewServiceImpl reviewService;

    private User user;
    private User host;
    private Accommodation accommodation;
    private Reservation reservation;
    private Review review;

    // Common DTOs used in multiple tests
    private CreateReviewDTO validCreateReviewDTO;
    private CreateReviewDTO invalidRatingLowDTO;
    private CreateReviewDTO invalidRatingHighDTO;
    private CreateReviewDTO longCommentDTO;
    private ReplyReviewDTO validReplyDTO;
    private ReplyReviewDTO simpleReplyDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // --- Users ---
        user = new User();
        user.setId("USER-001");
        user.setName("John Doe");

        host = new User();
        host.setId("HOST-001");
        host.setName("Alice Host");

        // --- Accommodation ---
        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setTitle("Forest Cabin");
        accommodation.setHost(host);

        // --- Reservation ---
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setGuest(user);
        reservation.setAccommodation(accommodation);
        reservation.setCheckOut(LocalDateTime.now().minusDays(2)); // Checkout completed

        // --- Review ---
        review = new Review();
        review.setId(10L);
        review.setUser(user);
        review.setReservation(reservation);
        review.setAccommodation(accommodation);
        review.setRating(5);
        review.setComment("Great place!");
        review.setCreatedAt(LocalDateTime.now().minusDays(1));

        // --- Common DTOs ---
        validCreateReviewDTO = new CreateReviewDTO(reservation.getId(), 5, "Excellent stay");
        invalidRatingLowDTO = new CreateReviewDTO(reservation.getId(), 0, "Invalid rating low");
        invalidRatingHighDTO = new CreateReviewDTO(reservation.getId(), 6, "Invalid rating high");
        longCommentDTO = new CreateReviewDTO(reservation.getId(), 5, "a".repeat(501));
        validReplyDTO = new ReplyReviewDTO("Thanks for your feedback!", 10L);
        simpleReplyDTO = new ReplyReviewDTO("Hi!", 10L);
    }

    // ✅ Successful creation
    @Test
    void createReview_Success() throws Exception {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(reservation.getId())).thenReturn(false);

        when(reviewMapper.toEntity(any(CreateReviewDTO.class))).thenReturn(new Review());
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review saved = inv.getArgument(0);
            saved.setId(10L);
            saved.setUser(user);
            saved.setAccommodation(accommodation);
            saved.setRating(5);
            saved.setComment("Excellent stay");
            return saved;
        });

        when(reviewMapper.toDTO(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            return new ReviewDTO(
                    r.getId(),
                    r.getUser().getId(),
                    r.getUser().getName(),
                    r.getRating(),
                    r.getComment(),
                    r.getCreatedAt(),
                    null,
                    r.getAccommodation().getId()
            );
        });

        ReviewDTO result = reviewService.createReview(validCreateReviewDTO);

        assertNotNull(result);
        assertEquals(5, result.rating());
        assertEquals("Excellent stay", result.text());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // ❌ User not found
    @Test
    void createReview_Fails_WhenUserNotFound() {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> reviewService.createReview(validCreateReviewDTO));
    }

    // ❌ Reservation not found
    @Test
    void createReview_Fails_WhenReservationNotFound() {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> reviewService.createReview(validCreateReviewDTO));
    }

    // ❌ Reservation not owned by user
    @Test
    void createReview_Fails_WhenReservationNotOwnedByUser() {
        User otherUser = new User();
        otherUser.setId("USER-999");
        reservation.setGuest(otherUser);

        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        assertThrows(UnauthorizedReviewException.class,
                () -> reviewService.createReview(validCreateReviewDTO));
    }

    // ❌ Reservation not yet finished
    @Test
    void createReview_Fails_WhenReservationNotFinished() {
        reservation.setCheckOut(LocalDateTime.now().plusDays(1));

        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        Exception ex = assertThrows(Exception.class,
                () -> reviewService.createReview(validCreateReviewDTO));

        assertTrue(ex.getMessage().toLowerCase().contains("not yet"));
    }

    // ❌ Review already exists
    @Test
    void createReview_Fails_WhenReviewAlreadyExists() {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(reservation.getId())).thenReturn(true);

        assertThrows(DuplicateReviewException.class,
                () -> reviewService.createReview(validCreateReviewDTO));
    }

    // ❌ Rating out of bounds
    @Test
    void createReview_Fails_WhenRatingOutOfBounds() {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        assertThrows(OperationNotAllowedException.class,
                () -> reviewService.createReview(invalidRatingLowDTO));

        assertThrows(OperationNotAllowedException.class,
                () -> reviewService.createReview(invalidRatingHighDTO));
    }

    // ❌ Comment too long
    @Test
    void createReview_Fails_WhenCommentTooLong() {
        when(authService.getUserID()).thenReturn(user.getId());
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        assertThrows(OperationNotAllowedException.class,
                () -> reviewService.createReview(longCommentDTO));
    }

    // ✅ Get reviews by accommodation
    @Test
    void getReviewsByAccommodation_ReturnsPagedResult() throws Exception {
        Page<Review> page = new PageImpl<>(List.of(review));
        when(reviewRepository.findByAccommodation_IdOrderByCreatedAtDesc(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        when(reviewMapper.toDTO(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            return new ReviewDTO(
                    r.getId(),
                    r.getUser().getId(),
                    r.getUser().getName(),
                    r.getRating(),
                    r.getComment(),
                    r.getCreatedAt(),
                    null,
                    r.getAccommodation().getId()
            );
        });

        Page<ReviewDTO> result = reviewService.getReviewsByAccommodation(1L, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(reviewRepository, times(1))
                .findByAccommodation_IdOrderByCreatedAtDesc(eq(1L), any(Pageable.class));
    }

    // ✅ Get average rating
    @Test
    void getAverageRating_ReturnsValue() {
        when(reviewRepository.getAverageRatingByAccommodation(1L)).thenReturn(4.7);
        Double avg = reviewService.getAverageRating(1L);
        assertEquals(4.7, avg);
    }

    // ✅ Delete review successfully
    @Test
    void deleteReview_Success() throws Exception {
        review.setUser(user);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(10L, user.getId());

        verify(reviewRepository, times(1)).delete(review);
    }

    // ❌ Delete review not found
    @Test
    void deleteReview_Fails_WhenReviewNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.deleteReview(10L, user.getId()));
    }

    // ❌ Delete review by unauthorized user
    @Test
    void deleteReview_Fails_WhenUnauthorized() {
        User other = new User();
        other.setId("USER-999");
        review.setUser(other);

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        assertThrows(UnauthorizedActionException.class,
                () -> reviewService.deleteReview(10L, user.getId()));
    }

    // ✅ Reply successfully
    @Test
    void replyToReview_Success() throws Exception {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById(review.getUser().getId())).thenReturn(Optional.of(host));
        when(replyMapper.toEntity(any(ReplyReviewDTO.class))).thenReturn(new Reply());
        when(replyRepository.save(any(Reply.class))).thenAnswer(inv -> inv.getArgument(0));
        when(replyMapper.toDTO(any(Reply.class))).thenAnswer(inv -> {
            Reply r = inv.getArgument(0);
            return new ReplyDTO(r.getId(), r.getMessage(), r.getRepliedAt());
        });

        ReplyDTO reply = reviewService.replyToReview(validReplyDTO);

        assertNotNull(reply);
        assertEquals("Thanks for your feedback!", reply.message());
        verify(replyRepository, times(1)).save(any(Reply.class));
    }

    // ❌ Reply fails if review not found
    @Test
    void replyToReview_Fails_WhenReviewNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.replyToReview(simpleReplyDTO));
    }

    // ❌ Reply fails if host not found
    @Test
    void replyToReview_Fails_WhenHostNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById(review.getUser().getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> reviewService.replyToReview(simpleReplyDTO));
    }

    // ❌ Reply fails if already exists
    @Test
    void replyToReview_Fails_WhenReplyAlreadyExists() {
        review.setReply(new Reply());
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById(review.getUser().getId())).thenReturn(Optional.of(host));

        assertThrows(ReplyAlreadyExistsException.class,
                () -> reviewService.replyToReview(simpleReplyDTO));
    }

    // ❌ Reply fails if unauthorized host
    @Test
    void replyToReview_Fails_WhenUnauthorizedHost() {
        User anotherHost = new User();
        anotherHost.setId("HOST-999");

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById(review.getUser().getId())).thenReturn(Optional.of(anotherHost));

        assertThrows(UnauthorizedActionException.class,
                () -> reviewService.replyToReview(simpleReplyDTO));
    }
}
