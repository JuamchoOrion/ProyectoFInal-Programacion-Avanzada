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
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
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
    public ReviewDTO createReview(CreateReviewDTO dto, String userId) throws Exception {
        // Obtener el usuario que está creando la reseña
        User guest = userRepository.getUserById(userId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Verificar que la reserva existe
        Reservation reservation = reservationRepository.findById(dto.reservationId())
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        // Verificar que la reserva pertenece a este usuario
        if (!reservation.getGuest().getId().equals(userId)) {
            throw new Exception("No tienes permiso para calificar esta reserva");
        }
        if(!reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)){
            throw new BadRequestException("Reservation is not completed");
        }
        // Verificar que no haya otra review para la misma reserva
        boolean exists = reviewRepository.existsByReservation_Id(reservation.getId());
        if (exists) {
            throw new Exception("Ya existe una review para esta reserva");
        }

        // Crear la entidad
        Review review = reviewMapper.toEntity(dto);
        review.setUser(guest);
        review.setAccommodation(reservation.getAccommodation());
        review.setReservation(reservation); // <-- asignar la reserva

        // Guardar en BD
        Review saved = reviewRepository.save(review);

        // Devolver DTO
        return reviewMapper.toDTO(saved);
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