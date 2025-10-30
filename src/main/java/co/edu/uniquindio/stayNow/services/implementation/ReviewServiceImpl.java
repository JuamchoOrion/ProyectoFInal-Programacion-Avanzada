package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.CreateReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReplyDTO;
import co.edu.uniquindio.stayNow.dto.ReplyReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReviewDTO;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.mappers.ReplyMapper;
import co.edu.uniquindio.stayNow.mappers.ReviewMapper;
import co.edu.uniquindio.stayNow.model.entity.Reply;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
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
    private final AuthService authService;
    private final ReviewMapper reviewMapper;
    private final ReplyMapper replyMapper;

    @Override
    public ReviewDTO createReview(CreateReviewDTO createReviewDTO) throws Exception {
        User user = userRepository.getUserById(authService.getUserID())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var reservation = reservationRepository.findById(createReviewDTO.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (!reservation.getGuest().getId().equals(user.getId()))
            throw new UnauthorizedReviewException("The reservation does not belong to the user");

        if (reservation.getCheckOut().isAfter(LocalDateTime.now()))
            throw new Exception("The reservation has not yet finished");

        if (reviewRepository.existsByReservation_Id(createReviewDTO.reservationId()))
            throw new DuplicateReviewException("A review already exists for this reservation");

        if (createReviewDTO.rating() < 1 || createReviewDTO.rating() > 5)
            throw new OperationNotAllowedException("The rating must be between 1 and 5");

        if (createReviewDTO.text() != null && createReviewDTO.text().length() > 500)
            throw new OperationNotAllowedException("The comment cannot exceed 500 characters");

        Review review = reviewMapper.toEntity(createReviewDTO);
        review.setUser(user);
        review.setReservation(reservation);
        review.setAccommodation(reservation.getAccommodation());
        review.setComment(createReviewDTO.text());
        review.setRating(createReviewDTO.rating());
        review.setCreatedAt(LocalDateTime.now());
        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    // Get all reviews sorted by most recent creation date.
    @Override
    public Page<ReviewDTO> getReviewsByAccommodation(Long accommodationId, int page, int size) throws Exception {
        // Find the accommodation and throw an exception if it doesn't exist
        var accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));

        // Create pageable object with descending order by creation date
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Retrieve paginated reviews for the given accommodation
        Page<Review> reviewsPage = reviewRepository.findByAccommodation_IdOrderByCreatedAtDesc(accommodationId, pageable);

        // Convert Review entities to ReviewDTO using the mapper
        return reviewsPage.map(reviewMapper::toDTO);
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
    public ReplyDTO replyToReview(ReplyReviewDTO dto) throws Exception {
        var review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        var host = userRepository.findById(review.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        if (!review.getAccommodation().getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("Not authorized to reply to this review");
        }

        if (review.getReply() != null) {
            throw new ReplyAlreadyExistsException("A reply already exists for this review");
        }

        Reply reply = replyMapper.toEntity(dto);
        reply.setMessage(dto.message());
        reply.setRepliedAt(LocalDateTime.now());
        reply.setReview(review);
        review.setReply(reply);
        return replyMapper.toDTO(replyRepository.save(reply));
    }
}