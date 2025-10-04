package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.EmailDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public ReservationDTO create(CreateReservationDTO dto) throws Exception {
        //obtener usuario
        User user = userRepository.getUserById(authService.getUserID()).orElse(null);
        String email = user.getEmail();

        // buscar alojamiento
        Accommodation accommodation = accommodationRepository.findById(dto.accommodationId())
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        // validaciones
        validateAccommodation(accommodation);
        validateGuests(dto, accommodation);
        validateDates(dto);
        validateOverlaps(dto);

        // calcular noches y precio
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        double totalPrice = nights * accommodation.getPricePerNight();

        // mapear a entidad Reservation
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setCheckIn(dto.checkIn());
        reservation.setCheckOut(dto.checkOut());
        reservation.setGuestsNumber(dto.guests());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(totalPrice);

        // guardar en BD
        Reservation saved = reservationRepository.save(reservation);

        // enviar email (antes del return)
        emailService.sendMail(new EmailDTO(
                "Reserva generada el día " + LocalDate.now(),
                "Tu reserva en " + accommodation.getTitle() + " fue creada con éxito. " +
                        "Fechas: " + dto.checkIn() + " - " + dto.checkOut() +
                        ". Número de huéspedes: " + dto.guests() +
                        ". Precio total: $" + totalPrice,
                email // <-- suponiendo que viene en tu DTO
        ));


        // mapear a DTO de respuesta
        return new ReservationDTO(
                saved.getId(),
                accommodation.getId(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getGuestsNumber(),
                reservation.getTotalPrice(),
                reservation.getReservationStatus()
        );
    }


    @Override
    public Page<Reservation> getReservations(Long userId, String status, LocalDate from, LocalDate to, Pageable pageable) {
        return null;
    }

    // 🔹 Métodos privados para validaciones
    private void validateAccommodation(Accommodation accommodation) throws Exception {
        if (accommodation == null) {
            throw new Exception("Accommodation not found");
        }
        if ("INACTIVE".equals(accommodation.getStatus())) {
            throw new Exception("Accommodation is inactive");
        }
    }

    private void validateGuests(CreateReservationDTO dto, Accommodation accommodation) throws Exception {
        if (dto.guests() > accommodation.getMaxGuests()) {
            throw new Exception("Max guests exceeded");
        }
    }

    private void validateDates(CreateReservationDTO dto) throws Exception {
        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        if (nights < 1) {
            throw new Exception("Reservation must be at least 1 night");
        }
    }

    private void validateOverlaps(CreateReservationDTO dto) throws Exception {
        List<Reservation> overlaps = reservationRepository.findOverLappingReservations(
                dto.accommodationId(),
                dto.checkIn(),
                dto.checkOut()
        );
        if (!overlaps.isEmpty()) {
            throw new Exception("Reservation overlaps with existing one");
        }
    }
}
