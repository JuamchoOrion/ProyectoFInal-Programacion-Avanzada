package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.model.*;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepo;
    private final ReservationRepository reservationRepo;
    private final ReviewRepository reviewRepo;
    private final UserService userService;
    private final AccommodationMapper accommodationMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Constructor para Inyección de Dependencias
    public AccommodationServiceImpl(AccommodationRepository accommodationRepo,
                                    ReservationRepository reservationRepo,
                                    ReviewRepository reviewRepo,
                                    UserService userService,
                                    AccommodationMapper accommodationMapper) {
        this.accommodationRepo = accommodationRepo;
        this.reservationRepo = reservationRepo;
        this.reviewRepo = reviewRepo;
        this.userService = userService;
        this.accommodationMapper = accommodationMapper;
    }


    // Implementación corregida en AccommodationServiceImpl

    private String getCurrentUserId() throws UnauthorizedActionException { // <-- ¡Añadir throws aquí!
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof User springUser) {
                return springUser.getName();
            }

        } catch (Exception e) {
            // La excepción original es UnauthorizedActionException
            throw new UnauthorizedActionException("Acceso denegado. Se requiere autenticación.");
        }

        // El método debe terminar lanzando la excepción si no se autenticó
        throw new UnauthorizedActionException("Acceso denegado. Se requiere autenticación.");
    }
    @Override
    public void create(CreateAccommodationDTO accommodationDTO) throws Exception {

    }

    @Override
    public AccomodationDTO get(Long id) throws Exception {
        return null;
    }

    @Override
    public void edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public List<AccomodationDTO> listAll() throws Exception {
        return List.of();
    }

    @Override
    public List<AccomodationDTO> search(String city, String checkIn, String checkOut, Double minPrice, Double maxPrice, List<String> services, int page, int size) throws Exception {
        return List.of();
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
