package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.model.entity.Reply;
import co.edu.uniquindio.stayNow.model.entity.Review;
import org.springframework.data.domain.Page;


public interface ReviewService {

    Review createReview(Long reservationId, String userId, String comment, Integer rating) throws Exception;

    Page<Review> getReviewsByAccommodation(Long accommodationId, int page, int size);

    Double getAverageRating(Long accommodationId);

    void deleteReview(Long reviewId, String userId) throws Exception;

    Reply replyToReview(Long reviewId, String hostId, String message) throws Exception;
}