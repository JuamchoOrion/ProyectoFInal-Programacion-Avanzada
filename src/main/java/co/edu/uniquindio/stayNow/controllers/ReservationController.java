package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.services.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping
    public ResponseEntity<ResponseDTO<ReservationDTO>> createReservation(@RequestBody CreateReservationDTO reservationDTO) throws Exception {
        ReservationDTO dto = reservationService.create(reservationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, dto));

    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> getReservationsUser(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        List<ReservationDTO> list = new ArrayList<ReservationDTO>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list ));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservationDTO>> getReservationById(@PathVariable Integer id){
        //return ResponseEntity.ok(new ResponseDTO<>(false, new ReservationDTO(0123123L, LocalDate.now(), LocalDate.now(),12)));
        return null;
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ResponseDTO<String>> cancelReservationById(@PathVariable Integer id){
        return ResponseEntity.ok(new ResponseDTO<>(false, "reserva cancelada con exito"));
    }

}
