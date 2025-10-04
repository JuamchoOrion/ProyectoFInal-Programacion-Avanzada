package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.MetricsResponseDTO;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.exceptions.OperationNotAllowedException;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.ReviewRepository;
import co.edu.uniquindio.stayNow.services.interfaces.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    public MetricsResponseDTO getAccommodationMetrics(Long accommodationId, LocalDate from, LocalDate to) throws Exception {

        // Validar que el alojamiento exista
        var accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Alojamiento no encontrado"));

        // Validar rango de fechas
        if (from != null && to != null && from.isAfter(to)) {
            throw new OperationNotAllowedException("Rango de fechas inválido");
        }

        // Contar reservas en el rango
        Long reservationCount = reservationRepository.countByAccommodationAndDateRange(accommodationId, from, to);

        // Promedio de calificaciones en el rango
        Double averageRating = reviewRepository.averageRatingByAccommodationAndDateRange(accommodationId, from, to);
        if (averageRating == null) averageRating = 0.0;

        return new MetricsResponseDTO(
                reservationCount.intValue(),
                averageRating,
                from,
                to
        );
    }
}
