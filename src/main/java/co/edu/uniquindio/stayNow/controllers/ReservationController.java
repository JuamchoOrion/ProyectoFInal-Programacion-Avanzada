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
    //lala arreglar esto pls
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsUser(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, // Fecha de creación (desde)

            // Fechas de estadía (Check-In/Check-Out)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
/**
        // 1. Construir el objeto Pageable manualmente (igual que en Accommodation)
        Pageable pageable = PageRequest.of(page, size);

        // 3. Llamar al servicio, que devuelve Page<Reservation>
        Page<Reservation> reservationPage = reservationService.getReservations(
                status,
                from,
                to,
                checkIn,
                checkOut,
                pageable
        );

        // 4. Mapear Page<Reservation> a Page<ReservationDTO> y retornar
        Page<ReservationDTO> dtoPage = reservationMapper.toReservationDTOPage(reservationPage);
**/
        return null;
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
