package co.edu.uniquindio.stayNow.dto;

import java.time.LocalDate;

public record MetricsResponseDTO(
        int reservationCount,
        double averageRating,
        LocalDate from,
        LocalDate to
) {}
