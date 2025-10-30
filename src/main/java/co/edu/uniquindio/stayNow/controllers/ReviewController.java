package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.mappers.ReplyMapper;
import co.edu.uniquindio.stayNow.mappers.ReviewMapper;
import co.edu.uniquindio.stayNow.model.entity.Reply;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final ReplyMapper replyMapper;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ResponseDTO<ReviewDTO>> createReview(@Valid @RequestBody CreateReviewDTO reviewDto) throws Exception {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO dto = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(reviewMapper.toDTO(review));
    }
//encapsular en dto
    @GetMapping("/accommodation/{id}")
    public ResponseEntity<Map<String, Object>> getReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Review> reviewPage = reviewService.getReviewsByAccommodation(id, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("items", reviewMapper.toDTOList(reviewPage.getContent()));
        response.put("page", reviewPage.getNumber());
        response.put("size", reviewPage.getSize());
        response.put("totalItems", reviewPage.getTotalElements());
        response.put("totalPages", reviewPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ReplyDTO> replyToReview(@PathVariable Long reviewId,
                                                  @Valid @RequestBody ReplyReviewDTO dto) throws Exception {
        String hostId = authService.getUserID();
        Reply reply = reviewService.replyToReview(reviewId, hostId, dto.message());
        return ResponseEntity.ok(replyMapper.toDTO(reply));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<String>> deleteReview(@PathVariable Long reviewId) throws Exception {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(new ResponseDTO<>(false,"Review deleted"));
    }
}
