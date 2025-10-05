package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Address;
import co.edu.uniquindio.stayNow.model.entity.Location;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.AccommodationServiceType;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;


import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepo;
    private final ReservationRepository reservationRepo;
    private final ReviewRepository reviewRepo;
    private final UserService userService;
    private final AccommodationMapper accommodationMapper;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final AuthService authService;
    @Override
    public AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception {


        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new Exception("User not found"));


        if (!user.getRole().equals(Role.HOST)) {
            throw new Exception("User is not a host");
        }


        Accommodation accommodation = accommodationMapper.toEntity(accommodationDTO);


        accommodation.setHost(user);


        if (accommodationDTO.services() != null && !accommodationDTO.services().isEmpty()) {
            Set<AccommodationServiceType> serviceEnums = accommodationDTO.services().stream()
                    .map(String::toUpperCase) // para evitar errores de minúsculas
                    .map(AccommodationServiceType::valueOf)
                    .collect(Collectors.toSet());

            accommodation.setAccommodationServiceTypes(serviceEnums);
        } else {
            accommodation.setAccommodationServiceTypes(Set.of()); // vacío si no hay servicios
        }


        Accommodation saved = accommodationRepo.save(accommodation);

        // 🧠 7️⃣ Convertir la entidad guardada a DTO de respuesta
        return accommodationMapper.toAccommodationDTO(saved);
    }


    @Override
    public AccommodationDTO get(Long id) throws Exception {
        return null;
    }

    @Override
    public void edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public Page<AccommodationDTO> listAll() throws Exception {
        return null;
    }
    @Override
    public Page<AccommodationDTO> search(
            String city,
            String checkIn,
            String checkOut,
            Double minPrice,
            Double maxPrice,
            List<String> services,
            Pageable pageable
    ) throws Exception {

        List<Specification<Accommodation>> filters = new ArrayList<>();

        // Filtrar por ciudad
        if (city != null && !city.isBlank()) {
            filters.add((root, query, cb) -> cb.equal(root.get("city"), city));
        }

        // Filtrar por rango de precios
        if (minPrice != null && maxPrice != null) {
            filters.add((root, query, cb) ->
                    cb.between(root.get("pricePerNight"), minPrice, maxPrice));
        } else if (minPrice != null) {
            filters.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice));
        } else if (maxPrice != null) {
            filters.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice));
        }

        // Filtrar por servicios (enum)
        if (services != null && !services.isEmpty()) {

            // Convertir los strings del request a enums
            Set<AccommodationServiceType> enumServices = services.stream()
                    .map(String::toUpperCase)
                    .map(AccommodationServiceType::valueOf)
                    .collect(Collectors.toSet());

            filters.add((root, query, cb) -> {
                Join<Accommodation, AccommodationServiceType> join = root.joinSet("services");
                query.distinct(true);
                return join.in(enumServices);
            });
        }

        // Combinar todos los filtros dinámicamente
        Specification<Accommodation> spec = Specification.allOf(filters);

        // Ejecutar la consulta con paginación
        Page<Accommodation> page = accommodationRepo.findAll(spec, pageable);

        return page.map(accommodationMapper::toAccommodationDTO);
    }


    @Override
    public List<ReservationDTO> getReservations(Long accommodationId, String startDate, String endDate, String status) throws Exception {
        return List.of();
    }

    @Override
    public void createReservation(Long accommodationId, CreateReservationDTO reservationDTO) throws Exception {

    }

    @Override
    public List<ReviewDTO> getReviews(Long accommodationId) throws Exception {
        return List.of();
    }

    @Override
    public void createReview(Long accommodationId, CreateReviewDTO reviewDTO) throws Exception {

    }

    @Override
    public void replyToReview(Long accommodationId, Long reviewId, ReplyReviewDTO replyDTO) throws Exception {

    }
}
