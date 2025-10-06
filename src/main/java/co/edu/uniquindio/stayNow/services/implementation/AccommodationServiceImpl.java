package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.exceptions.UnauthorizedActionException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.entity.*;
import co.edu.uniquindio.stayNow.model.enums.AccommodationServiceType;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;


import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ReservationMapper reservationMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final AuthService authService;
    @Override
    public AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception {


        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!user.getRole().equals(Role.HOST)) {
            throw new UnauthorizedActionException("User is not a host");
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
    public AccommodationDTO get(Long accomodationId) throws Exception {
        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Accommodation accommodation = accommodationRepo.findById(accomodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));


        return accommodationMapper.toAccommodationDTO(accommodation);
    }

    @Override
    public AccommodationDTO edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception {

        Accommodation accommodation = accommodationRepo.findById(id).orElse(null);
        if (accommodation == null) {
            throw new Exception("Accommodation not found");
        }
        accommodationMapper.updateEntity(accommodationDTO, accommodation);
        accommodationRepo.save(accommodation);

        return accommodationMapper.toAccommodationDTO(accommodation);

    }

    @Override
    public void delete(Long id) throws Exception {
        Accommodation accommodation = accommodationRepo.findById(id)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation does not exist"));
        if(accommodation.getReservations().stream().anyMatch(r -> !r.getReservationStatus().equals(ReservationStatus.CANCELED))) {
            throw new AccommodationNotFoundException("Accommodation cannot be deleted because it has active reservations");
        }
        accommodation.setStatus(AccommodationStatus.DELETED);
        accommodationRepo.save(accommodation);

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
                Join<Accommodation, AccommodationServiceType> join = root.joinSet("accommodationServiceTypes");
                query.distinct(true);
                return join.in(enumServices);
            });
        }

        // Combinar todos los filtros dinámicamente
        Specification<Accommodation> spec = Specification.allOf(filters);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        // Ejecutar la consulta con paginación
        Page<Accommodation> page = accommodationRepo.findAll(spec, pageable);

        return page.map(accommodationMapper::toAccommodationDTO);
    }


    @Override
    public Page<ReservationDTO> getReservations(Long accommodationId, LocalDateTime from, LocalDateTime to, List<String> status, Pageable pageable) throws Exception {
        List<Specification<Reservation>> filters = new ArrayList<>();
        //SPECIFICATION ES BASICAMENTE CONSTRUIR FILTROS EN CONSULTAS SQL CON WHERE (AND)
        filters.add((root,query,cb)->cb.equal(root.get("accommodation").get("id"), accommodationId));
        if(from != null ) {
            filters.add((root,query,cb)->cb.greaterThanOrEqualTo(root.get("checkIn"), from));
        }
        if(to != null ) {
            filters.add((root, query, cb)-> cb.lessThanOrEqualTo(root.get("checkOut"), to));
        }
        if(status != null) {
            Set<ReservationStatus> enumStatus = status.stream()
                    .map(String::toUpperCase)
                    .map(ReservationStatus::valueOf)
                    .collect(Collectors.toSet());
            filters.add((root, query, cb) -> {
                Join<Reservation, ReservationStatus> join = root.joinSet("reservationStatus");
                query.distinct(true);
                return join.in(enumStatus);
            });
        }
        Specification<Reservation> spec = Specification.allOf(filters);
        Page<Reservation> page = reservationRepo.findAll(spec,pageable );
        return page.map(reservationMapper::toReservationDTO);
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
