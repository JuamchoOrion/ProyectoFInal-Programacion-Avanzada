package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.model.entity.*;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.implementation.ReviewServiceImpl;

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

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private User host;
    private Accommodation accommodation;
    private Reservation reservation;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("USER-001");
        user.setName("John Doe");

        host = new User();
        host.setId("HOST-001");
        host.setName("Alice Host");

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setTitle("Forest Cabin");
        accommodation.setHost(host);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setGuest(user);
        reservation.setAccommodation(accommodation);
        reservation.setCheckOut(LocalDateTime.now().minusDays(2)); // Checkout passed

        review = new Review();
        review.setId(10L);
        review.setUser(user);
        review.setReservation(reservation);
        review.setAccommodation(accommodation);
        review.setRating(5);
        review.setComment("Great place!");
    }

    // ✅ Successful creation
    @Test
    void createReview_Success() throws Exception {
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.createReview(1L, "USER-001", "Excellent stay", 5);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Excellent stay", result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // ❌ User not found
    @Test
    void createReview_Fails_WhenUserNotFound() {
        when(userRepository.findById("USER-001")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 4)
        );
    }

    // ❌ Reservation not found
    @Test
    void createReview_Fails_WhenReservationNotFound() {
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 4)
        );
    }

    // ❌ Reservation does not belong to user
    @Test
    void createReview_Fails_WhenReservationNotOwnedByUser() {
        User otherUser = new User();
        otherUser.setId("USER-999");
        reservation.setGuest(otherUser);

        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(UnauthorizedReviewException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 4)
        );
    }

    // ❌ Reservation not yet finished
    @Test
    void createReview_Fails_WhenReservationNotFinished() {
        reservation.setCheckOut(LocalDateTime.now().plusDays(1));

        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Exception ex = assertThrows(Exception.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 4)
        );
        assertTrue(ex.getMessage().contains("not yet finished"));
    }

    // ❌ Review already exists
    @Test
    void createReview_Fails_WhenReviewAlreadyExists() {
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation_Id(1L)).thenReturn(true);

        assertThrows(DuplicateReviewException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 4)
        );
    }

    // ❌ Rating out of bounds
    @Test
    void createReview_Fails_WhenRatingOutOfBounds() {
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(OperationNotAllowedException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 0)
        );

        assertThrows(OperationNotAllowedException.class, () ->
                reviewService.createReview(1L, "USER-001", "Text", 6)
        );
    }

    // ❌ Comment too long
    @Test
    void createReview_Fails_WhenCommentTooLong() {
        String longComment = "a".repeat(501);
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(OperationNotAllowedException.class, () ->
                reviewService.createReview(1L, "USER-001", longComment, 5)
        );
    }

    // ✅ Get reviews by accommodation
    @Test
    void getReviewsByAccommodation_ReturnsPagedResult() {
        Page<Review> page = new PageImpl<>(List.of(review));
        when(reviewRepository.findByAccommodation_IdOrderByCreatedAtDesc(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        Page<Review> result = reviewService.getReviewsByAccommodation(1L, 0, 10);

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

        reviewService.deleteReview(10L, "USER-001");

        verify(reviewRepository, times(1)).delete(review);
    }

    // ❌ Delete review not found
    @Test
    void deleteReview_Fails_WhenReviewNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
                reviewService.deleteReview(10L, "USER-001")
        );
    }

    // ❌ Delete review by different user
    @Test
    void deleteReview_Fails_WhenUnauthorized() {
        User other = new User();
        other.setId("USER-999");
        review.setUser(other);

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        assertThrows(UnauthorizedActionException.class, () ->
                reviewService.deleteReview(10L, "USER-001")
        );
    }

    // ✅ Reply to review successfully
    @Test
    void replyToReview_Success() throws Exception {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById("HOST-001")).thenReturn(Optional.of(host));
        when(replyRepository.save(any(Reply.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reply reply = reviewService.replyToReview(10L, "HOST-001", "Thanks for your feedback!");

        assertNotNull(reply);
        assertEquals("Thanks for your feedback!", reply.getMessage());
        verify(replyRepository, times(1)).save(any(Reply.class));
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // ❌ Reply fails if host not found
    @Test
    void replyToReview_Fails_WhenHostNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById("HOST-001")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reviewService.replyToReview(10L, "HOST-001", "Hi!")
        );
    }

    // ❌ Reply fails if review not found
    @Test
    void replyToReview_Fails_WhenReviewNotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
                reviewService.replyToReview(10L, "HOST-001", "Hi!")
        );
    }

    // ❌ Reply fails if already exists
    @Test
    void replyToReview_Fails_WhenReplyAlreadyExists() {
        Reply existingReply = new Reply();
        review.setReply(existingReply);

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById("HOST-001")).thenReturn(Optional.of(host));

        assertThrows(ReplyAlreadyExistsException.class, () ->
                reviewService.replyToReview(10L, "HOST-001", "Hi!")
        );
    }

    // ❌ Reply fails if wrong host
    @Test
    void replyToReview_Fails_WhenUnauthorizedHost() {
        User anotherHost = new User();
        anotherHost.setId("HOST-999");

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(userRepository.findById("HOST-999")).thenReturn(Optional.of(anotherHost));

        assertThrows(UnauthorizedActionException.class, () ->
                reviewService.replyToReview(10L, "HOST-999", "Hi!")
        );
    }
}
