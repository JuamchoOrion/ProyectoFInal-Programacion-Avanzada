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
        //obtener usuario
        User user = userRepository.getUserById(authService.getUserID()).orElse(null);
        String email = user.getEmail();

        // buscar alojamiento
        Accommodation accommodation = accommodationRepository.findById(dto.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException("Alojamiento no encontrado"));
        User host = accommodation.getHost();
        String emailHost = host.getEmail();
        validateAccommodation(accommodation);
        validateGuests(dto, accommodation);
        validateDates(dto);
        validateOverlaps(dto);

        // calcular noches y precio
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        double totalPrice = nights * accommodation.getPricePerNight();


        Reservation reservation = reservationMapper.toEntity(dto);
        reservation.setGuest(user);
        reservation.setAccommodation(accommodation);
        reservation.setTotalPrice(totalPrice);
        // guardar en BD
        Reservation saved = reservationRepository.save(reservation);
        // enviar email (antes del return)
        emailService.sendMail(new EmailDTO(
                "Reserva generada el día " + LocalDateTime.now(),
                "Tu reserva en " + accommodation.getTitle() + " fue creada con éxito. " +
                        "Fechas: " + dto.checkIn() + " - " + dto.checkOut() +
                        ". Número de huéspedes: " + dto.guests() +
                        ". Precio total: $" + totalPrice,
                email
        ));
        emailService.sendMail(new EmailDTO(
                "Reserva generada el día " + LocalDateTime.now(),
                "Nueva reserva generada en tu Alojamiento " + accommodation.getTitle()  +
                        "Fechas: " + dto.checkIn() + " - " + dto.checkOut() +
                        ". Número de huéspedes: " + dto.guests() +
                        ". Precio total: $" + totalPrice,
                emailHost
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
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));


        if (!currentUser.getRole().equals(Role.GUEST)) {
            throw new UnauthorizedActionException("Solo los usuarios GUEST pueden ver sus reservas.");
        }


        List<Specification<Reservation>> filters = new ArrayList<>();


        filters.add((root, query, cb) -> cb.equal(root.get("guest").get("id"), currentUserId));


        if (status != null && !status.isEmpty()) {
            try {
                ReservationStatus enumStatus = ReservationStatus.valueOf(status.toUpperCase());
                filters.add((root, query, cb) -> cb.equal(root.get("reservationStatus"), enumStatus));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Estado de reserva inválido: " + status);
            }
        }

        // Filtrar por rango de fechas de creación
        if (from != null) {
            filters.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }
        if (to != null) {
            filters.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }

        // Filtrar por fechas de check-in / check-out
        if (checkIn != null) {
            filters.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("checkIn"), checkIn));
        }
        if (checkOut != null) {
            filters.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("checkOut"), checkOut));
        }

        // Combinar todos los filtros
        Specification<Reservation> spec = filters.stream()
                .reduce(Specification::and)
                .orElse(null);

        // Ejecutar consulta
        Page<Reservation> reservationsPage = reservationRepository.findAll(spec, pageable);

        // Convertir entidades a DTOs
        return reservationsPage.map(reservationMapper::toReservationDTO);
    }
    //este metodo le agrege la parte 2 y 3 para porque hacia falta al probar es test de getby id unauthorized
    @Override
    public ReservationDTO getReservationById(Long reservationId) throws Exception{

        // 1. Obtener la reserva
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada"));

        // 2. Obtener el ID del usuario autenticado y su objeto completo
        // 🚨 El currentUserId es crucial para la seguridad
        String currentUserId = authService.getUserID();
        User currentUser = userRepository.getUserById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado."));

        // 3. 🚨 APLICAR REGLAS DE SEGURIDAD 🚨

        // Determinar si el usuario autenticado tiene relación con la reserva
        boolean isGuest = reservation.getGuest().getId().equals(currentUserId);
        boolean isHost = reservation.getAccommodation().getHost().getId().equals(currentUserId);

        if (!isGuest && !isHost) {
            // Si el usuario no es el huésped (u001) ni el anfitrión (u002), se deniega el acceso.
            // Esto es lo que el test 'testGetReservationByIdFailsUnauthorized' verifica.
            throw new UnauthorizedActionException("Acceso denegado. No tiene permisos para ver esta reserva.");
        }

        // 4. Mapear y retornar (Acceso concedido)
        return reservationMapper.toReservationDTO(reservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception{
        String currentUserId = authService.getUserID();
        User currentUser = userRepository.getUserById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        Reservation reservation = reservationRepository.findById(reservationId).
                orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada."));

        if(!reservation.getGuest().getId().equals(currentUser.getId())){
            throw new UnauthorizedActionException("No puede cancelar una reserva de otra persona.");
        }

        if(reservation.getReservationStatus() == ReservationStatus.CANCELED){
            throw new ReservationCancellationNotAllowedException("Esta reserva ya está cancelada.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkIn = reservation.getCheckIn();

        if(Duration.between(now, checkIn).toHours() < 48){
            throw new ReservationCancellationNotAllowedException("Solo se pueden cancelar las reservas " +
                    "hasta 48 horas antes del CheckIn");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELED);
        Reservation reservationSaved = reservationRepository.save(reservation);
        return reservationMapper.toReservationDTO(reservationSaved);
    }

    // 🔹 Métodos privados para validaciones
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
            throw new MaxGuestsExceededException("Max guests exceeded");
        }
    }

    private void validateDates(CreateReservationDTO dto) throws Exception {
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        if (nights < 1) {
            throw new BadRequestException("Reservation must be at least 1 night");
        }
    }

    private void validateOverlaps(CreateReservationDTO dto) throws Exception {
        List<Reservation> overlaps = reservationRepository.findOverLappingReservations(
                dto.accommodationId(),
                dto.checkIn(),
                dto.checkOut()
        );
        if (!overlaps.isEmpty()) {
            throw new ReservationConflictException("Reservation overlaps with existing one");
        }
    }
}
