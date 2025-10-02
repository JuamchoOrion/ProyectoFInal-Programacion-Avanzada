package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.MetricsResponseDTO;

import java.time.LocalDate;

public interface MetricsService {
    MetricsResponseDTO getAccommodationMetrics(Long accommodationId, LocalDate from, LocalDate to);
}