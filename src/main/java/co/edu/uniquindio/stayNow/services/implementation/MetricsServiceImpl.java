package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.MetricsResponseDTO;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.Review;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.ReviewRepository;
import co.edu.uniquindio.stayNow.services.interfaces.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    @Override
    public MetricsResponseDTO getAccommodationMetrics (Long accommodationId, LocalDateTime from, LocalDateTime to) throws Exception {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));


        List<Specification<Reservation>> specifications = new ArrayList<>();
        specifications.add((root, query, cb) -> cb.equal(root.get("accommodation").get("id"), accommodationId));

        if (from != null) {
            specifications.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("checkIn"), from));
        }
        if (to != null) {
            specifications.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("checkOut"), to));
        }

        Specification<Reservation> spec = Specification.allOf(specifications);
        List<Reservation> reservationList = reservationRepository.findAll(spec);


        long completedCount = reservationList.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.COMPLETED)
                .count();

        long canceledCount = reservationList.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CANCELED)
                .count();


        List<Review> reviewsByAccommodation = reviewRepository.findAllByAccommodation(accommodation);
        double averageRating = 0.0;
        if (!reviewsByAccommodation.isEmpty()) {
            averageRating = reviewsByAccommodation.stream()
                    .mapToDouble(Review::getRate)
                    .average()
                    .orElse(0.0);
        }

        return new MetricsResponseDTO(
                accommodation.getId(),
                accommodation.getTitle(),
                (long) reservationList.size(),
                completedCount,
                canceledCount,
                averageRating,
                (long) reviewsByAccommodation.size(),
                from,
                to
        );
    }

}
