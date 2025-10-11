package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.model.entity.Reply;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    @Override
    public Review createReview(Long reservationId, String userId, String comment, Integer rating) throws Exception {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (!reservation.getGuest().getId().equals(userId))
            throw new UnauthorizedReviewException("The reservation does not belong to the user");

        if (reservation.getCheckOut().isAfter(LocalDateTime.now()))
            throw new Exception("The reservation has not yet finished");

        if (reviewRepository.existsByReservation_Id(reservationId))
            throw new DuplicateReviewException("A review already exists for this reservation");

        if (rating < 1 || rating > 5)
            throw new OperationNotAllowedException("The rating must be between 1 and 5");

        if (comment != null && comment.length() > 500)
            throw new OperationNotAllowedException("The comment cannot exceed 500 characters");

        Review review = new Review();
        review.setUser(user);
        review.setReservation(reservation);
        review.setAccommodation(reservation.getAccommodation());
        review.setComment(comment);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // Get all reviews sorted by most recent creation date.
    @Override
    public Page<Review> getReviewsByAccommodation(Long accommodationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByAccommodation_IdOrderByCreatedAtDesc(accommodationId, pageable);
    }

    // Calculate the average rating for the accommodation.
    @Override
    public Double getAverageRating(Long accommodationId) {
        return reviewRepository.getAverageRatingByAccommodation(accommodationId);
    }

    // Delete a review
    @Override
    public void deleteReview(Long reviewId, String userId) throws Exception {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        // Only the user who created the review can delete it
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("Not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    // Reply to a review. Only the host of the accommodation can reply.
    @Override
    public Reply replyToReview(Long reviewId, String hostId, String message) throws Exception {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        var host = userRepository.findById(hostId)
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        if (!review.getAccommodation().getHost().getId().equals(hostId)) {
            throw new UnauthorizedActionException("Not authorized to reply to this review");
        }

        if (review.getReply() != null) {
            throw new ReplyAlreadyExistsException("A reply already exists for this review");
        }

        Reply reply = new Reply();
        reply.setMessage(message);
        reply.setRepliedAt(LocalDateTime.now());
        reply.setReview(review);

        reply = replyRepository.save(reply);

        review.setReply(reply);
        reviewRepository.save(review);

        return reply;
    }
}