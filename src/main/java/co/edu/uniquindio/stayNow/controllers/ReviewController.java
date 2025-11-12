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
import org.springframework.http.HttpStatus;
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
        // obtener ID del usuario autenticado desde el token JWT
        String userId = authService.getUserID();

        // crear la review pasando el usuario
        ReviewDTO dto = reviewService.createReview(reviewDto, userId);

        return ResponseEntity.ok(new ResponseDTO<>(false, dto));
    }
    @GetMapping("/accommodation/{id}")
    public ResponseEntity<ResponseDTO<Page<ReviewDTO>>> getReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {

        Page<ReviewDTO> reviewPage = reviewService.getReviewsByAccommodation(id, page, size);


        return ResponseEntity.ok(new ResponseDTO<>(false, reviewPage));
    }

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ResponseDTO<ReplyDTO>> replyToReview(@PathVariable Long reviewId,
                                                  @Valid @RequestBody ReplyReviewDTO dto) throws Exception {
        String hostId = authService.getUserID();
        ReplyDTO reply = reviewService.replyToReview( dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, reply));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<String>> deleteReview(@PathVariable Long reviewId) throws Exception {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(new ResponseDTO<>(false,"Review deleted"));
    }
}
