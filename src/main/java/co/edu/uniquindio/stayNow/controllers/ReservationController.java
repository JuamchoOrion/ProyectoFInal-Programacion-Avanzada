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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, // Fecha de creación desde
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,   // Fecha de creación hasta
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn, // Fecha de entrada
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut, // Fecha de salida
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        // 1️⃣ Crear el objeto de paginación
        Pageable pageable = PageRequest.of(page, size);

        // 2️⃣ Llamar al servicio con los filtros
        Page<ReservationDTO> reservationPage = reservationService.getReservationsUser(
                status,
                from,
                to,
                checkIn,
                checkOut,
                pageable
        );

        // 3️⃣ Retornar la respuesta estandarizada
        return ResponseEntity.ok(
                new ResponseDTO<>(false, reservationPage)
        );
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
