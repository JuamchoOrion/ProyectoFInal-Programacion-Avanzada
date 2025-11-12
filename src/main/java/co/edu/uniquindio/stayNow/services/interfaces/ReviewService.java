package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.CreateReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReplyDTO;
import co.edu.uniquindio.stayNow.dto.ReplyReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewService {

    ReviewDTO createReview(CreateReviewDTO dto, String userId) throws Exception;

    Page<ReviewDTO> getReviewsByAccommodation(Long accommodationId, int page, int size) throws Exception;

    Double getAverageRating(Long accommodationId);

    void deleteReview(Long reviewId, String userId) throws Exception;

    ReplyDTO replyToReview(ReplyReviewDTO replyDTO) throws Exception;
}