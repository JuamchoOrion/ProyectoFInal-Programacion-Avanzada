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
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        if (!reservation.getGuest().getId().equals(userId))
            throw new UnauthorizedReviewException("La reserva no pertenece al usuario");

        if (reservation.getCheckOut().isAfter(LocalDateTime.now()))
            throw new Exception("La reserva aún no ha finalizado");

        if (reviewRepository.existsByReservation_Id(reservationId))
            throw new DuplicateReviewException("Ya existe un comentario para esta reserva");

        if (rating < 1 || rating > 5)
            throw new OperationNotAllowedException("La calificación debe estar entre 1 y 5");

        if (comment != null && comment.length() > 500)
            throw new OperationNotAllowedException("El comentario no puede superar los 500 caracteres");

        Review review = new Review();
        review.setUser(user);
        review.setReservation(reservation);
        review.setAccommodation(reservation.getAccommodation());
        review.setComment(comment);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // Obtener todas las reviews por fecha de creación más reciente.
    @Override
    public Page<Review> getReviewsByAccommodation(Long accommodationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByAccommodation_IdOrderByCreatedAtDesc(accommodationId, pageable);
    }

    // Para calcular el promedio de calificaciones del alojamiento.
    @Override
    public Double getAverageRating(Long accommodationId) {
        return reviewRepository.getAverageRatingByAccommodation(accommodationId);
    }

    // Eliminar review
    @Override
    public void deleteReview(Long reviewId, String userId) throws Exception {
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException("Review no encontrada"));

        // Solo el que hizo la review la puede borrar
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("No autorizado para eliminar este comentario");
        }

        reviewRepository.delete(review);
    }

    // Responder a la review. Solo el host del alojamiento puede responder.
    @Override
    public Reply replyToReview(Long reviewId, String hostId, String message) throws Exception {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review no encontrada"));

        var host = userRepository.findById(hostId)
                .orElseThrow(() -> new UserNotFoundException("Host no encontrado"));

        if (!review.getAccommodation().getHost().getId().equals(hostId)) {
            throw new UnauthorizedActionException("No autorizado para responder a este comentario");
        }

        if (review.getReply() != null) {
            throw new ReplyAlreadyExistsException("Ya existe una respuesta a este comentario");
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
