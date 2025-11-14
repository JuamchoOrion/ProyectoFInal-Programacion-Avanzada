package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.services.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PostMapping
    public ResponseEntity<ResponseDTO<ReservationDTO>> createReservation(@RequestBody CreateReservationDTO reservationDTO) throws Exception {
        ReservationDTO dto = reservationService.create(reservationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, dto));

    }
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsUser(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime fromDate =
                (from != null && !from.isBlank()) ? LocalDateTime.parse(from) : null;

        LocalDateTime toDate =
                (to != null && !to.isBlank()) ? LocalDateTime.parse(to) : null;

        LocalDateTime checkInDate =
                (checkIn != null && !checkIn.isBlank()) ? LocalDateTime.parse(checkIn) : null;

        LocalDateTime checkOutDate =
                (checkOut != null && !checkOut.isBlank()) ? LocalDateTime.parse(checkOut) : null;

        Page<ReservationDTO> reservationPage =
                reservationService.getReservationsUser(
                        status,
                        fromDate,
                        toDate,
                        checkInDate,
                        checkOutDate,
                        pageable
                );

        return ResponseEntity.ok(new ResponseDTO<>(false, reservationPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservationDTO>> getReservationById(@PathVariable Long id) throws Exception {
        ReservationDTO dto = reservationService.getReservationById(id);
        return ResponseEntity.ok(new ResponseDTO<>(true, dto));
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ResponseDTO<ReservationDTO>> cancelReservationById(@PathVariable Long id) throws Exception {
        ReservationDTO dto = reservationService.cancelReservation(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, dto));
    }

}
