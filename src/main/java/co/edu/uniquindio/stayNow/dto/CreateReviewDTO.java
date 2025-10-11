package co.edu.uniquindio.stayNow.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewDTO(
        // The userID is extracted from the token.
        @NotNull(message = "The reservation ID is required")
        Long reservationId,

        @NotNull(message = "The rating is required")
        @Min(value = 1, message = "The minimum rating is 1")
        @Max(value = 5, message = "The maximum rating is 5")
        Integer rating,

        @NotBlank(message = "The comment cannot be empty")
        @Size(max = 500, message = "The comment cannot exceed 500 characters")
        String text
) {}