package co.edu.uniquindio.stayNow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MetricsResponseDTO(
        Long accommodationId,
        String accommodationTitle,
        Long totalReservations,
        Long completedReservations,
        Long canceledReservations,
        Double averageRating,
        Long totalReviews,
        LocalDateTime fromDate,          // fecha de inicio del filtro (opcional)
        LocalDateTime toDate             // fecha de fin del filtro (opcional)
) { }