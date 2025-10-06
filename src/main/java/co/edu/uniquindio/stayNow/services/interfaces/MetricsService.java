package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.MetricsResponseDTO;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MetricsService {
    MetricsResponseDTO getAccommodationMetrics(Long accommodationId, LocalDateTime from, LocalDateTime to) throws Exception;
}