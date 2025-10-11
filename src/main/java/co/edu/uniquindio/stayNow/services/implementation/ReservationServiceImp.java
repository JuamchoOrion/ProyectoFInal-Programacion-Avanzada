package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.EmailDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.EmailService;
import co.edu.uniquindio.stayNow.services.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationServiceImp implements ReservationService {
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final EmailService emailService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    @Override
    public ReservationDTO create(CreateReservationDTO dto) throws Exception {
        // Get user
        User user = userRepository.getUserById(authService.getUserID()).orElse(null);
        String email = user.getEmail();

        // Find accommodation
        Accommodation accommodation = accommodationRepository.findById(dto.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));
        User host = accommodation.getHost();
        String hostEmail = host.getEmail();

        validateAccommodation(accommodation);
        validateGuests(dto, accommodation);
        validateDates(dto);
        validateOverlaps(dto);

        // Calculate nights and total price
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        double totalPrice = nights * accommodation.getPricePerNight();

        Reservation reservation = reservationMapper.toEntity(dto);
        reservation.setGuest(user);
        reservation.setAccommodation(accommodation);
        reservation.setTotalPrice(totalPrice);

        // Save in database
        Reservation saved = reservationRepository.save(reservation);

        // Send emails
        emailService.sendMail(new EmailDTO(
                "Reservation created on " + LocalDateTime.now(),
                "Your reservation at " + accommodation.getTitle() + " was successfully created. " +
                        "Dates: " + dto.checkIn() + " - " + dto.checkOut() +
                        ". Number of guests: " + dto.guests() +
                        ". Total price: $" + totalPrice,
                email
        ));

        emailService.sendMail(new EmailDTO(
                "Reservation created on " + LocalDateTime.now(),
                "A new reservation has been made for your accommodation " + accommodation.getTitle() +
                        ". Dates: " + dto.checkIn() + " - " + dto.checkOut() +
                        ". Number of guests: " + dto.guests() +
                        ". Total price: $" + totalPrice,
                hostEmail
        ));

        return reservationMapper.toReservationDTO(saved);
    }

    @Override
    public Page<ReservationDTO> getReservationsUser(
            String status,
            LocalDateTime from,
            LocalDateTime to,
            LocalDateTime checkIn,
            LocalDateTime checkOut,
            Pageable pageable) throws Exception {

        String currentUserId = authService.getUserID();
        User currentUser = userRepository.getUserById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        // Ensure the user is a GUEST
        if (!currentUser.getRole().equals(Role.GUEST)) {
            throw new UnauthorizedActionException("Only GUEST users can view their reservations.");
        }

        // Build dynamic filter specifications
        List<Specification<Reservation>> filters = new ArrayList<>();

        // Filter by logged-in user
        filters.add((root, query, cb) -> cb.equal(root.get("guest").get("id"), currentUserId));

        // Filter by reservation status
        if (status != null && !status.isEmpty()) {
            try {
                ReservationStatus enumStatus = ReservationStatus.valueOf(status.toUpperCase());
                filters.add((root, query, cb) -> cb.equal(root.get("reservationStatus"), enumStatus));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid reservation status: " + status);
            }
        }

        // Filter by creation date range
        if (from != null) {
            filters.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }
        if (to != null) {
            filters.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }

        // Filter by check-in / check-out dates
        if (checkIn != null) {
            filters.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("checkIn"), checkIn));
        }
        if (checkOut != null) {
            filters.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("checkOut"), checkOut));
        }

        // Combine filters
        Specification<Reservation> spec = filters.stream()
                .reduce(Specification::and)
                .orElse(null);

        // Execute query
        Page<Reservation> reservationsPage = reservationRepository.findAll(spec, pageable);

        // Convert entities to DTOs
        return reservationsPage.map(reservationMapper::toReservationDTO);
    }

    @Override
    public ReservationDTO getReservationById(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        return reservationMapper.toReservationDTO(reservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        String currentUserId = authService.getUserID();
        User currentUser = userRepository.getUserById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found."));

        if (!reservation.getGuest().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You cannot cancel someone else's reservation.");
        }

        if (reservation.getReservationStatus() == ReservationStatus.CANCELED) {
            throw new ReservationCancellationNotAllowedException("This reservation is already canceled.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkIn = reservation.getCheckIn();

        if (Duration.between(now, checkIn).toHours() < 48) {
            throw new ReservationCancellationNotAllowedException("Reservations can only be canceled up to 48 hours before check-in.");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELED);
        Reservation reservationSaved = reservationRepository.save(reservation);
        return reservationMapper.toReservationDTO(reservationSaved);
    }

    // 🔹 Private validation methods
    private void validateAccommodation(Accommodation accommodation) throws Exception {
        if (accommodation == null) {
            throw new AccommodationNotFoundException("Accommodation not found");
        }
        if ("INACTIVE".equals(accommodation.getStatus())) {
            throw new AccommodationUnavailableException("Accommodation is inactive");
        }
    }

    private void validateGuests(CreateReservationDTO dto, Accommodation accommodation) throws Exception {
        if (dto.guests() > accommodation.getMaxGuests()) {
            throw new MaxGuestsExceededException("Number of guests exceeds the maximum allowed");
        }
    }

    private void validateDates(CreateReservationDTO dto) throws Exception {
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        if (nights < 1) {
            throw new BadRequestException("Reservation must be for at least one night");
        }
    }

    private void validateOverlaps(CreateReservationDTO dto) throws Exception {
        List<Reservation> overlaps = reservationRepository.findOverLappingReservations(
                dto.accommodationId(),
                dto.checkIn(),
                dto.checkOut()
        );
        if (!overlaps.isEmpty()) {
            throw new ReservationConflictException("Reservation overlaps with an existing one");
        }
    }
}
