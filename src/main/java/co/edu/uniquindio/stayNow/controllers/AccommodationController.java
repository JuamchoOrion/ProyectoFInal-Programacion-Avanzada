package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
// faltan los de comments
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> getListOfAccomodation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String services, // comma-separated list
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        Pageable pageable = PageRequest.of(page, size);
        List<String> serviceList = null;
        if (services != null && !services.isBlank()) {
            serviceList = Arrays.stream(services.split(","))
                    .map(String::trim)
                    .toList();
        }
        Page<AccommodationDTO> result = accommodationService.search(
                city,
                checkIn,
                checkOut,
                minPrice,
                maxPrice,
                serviceList,
                pageable);
        return ResponseEntity.ok(new ResponseDTO<>(false, result));
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getListOfReservation(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        List<String> statusList = new ArrayList<String>();
        Pageable pageable = PageRequest.of(page, size);
        if (status != null && !status.isBlank()) {
            statusList = Arrays.stream(status.split(","))
                    .map(String::trim)
                    .toList();
        }
        Page<ReservationDTO> result = accommodationService.getReservations(
                id,
                from,
                to,
                statusList,
                pageable
        );
        return ResponseEntity.ok(new ResponseDTO<>(false, result));
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> createAccommodation(
            @ModelAttribute CreateAccommodationDTO accommodationDTO
    ) throws Exception {
        AccommodationDTO dto = accommodationService.create(accommodationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> getAccommodationById(@PathVariable long id) throws Exception {
        AccommodationDTO response = accommodationService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> editAccommodation(@PathVariable long id, @RequestBody EditAccommodationDTO accommodationDTO) throws Exception {
        return ResponseEntity.ok(new ResponseDTO<>(false, accommodationService.edit(id, accommodationDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAccommodation(@PathVariable long id) throws Exception {
        accommodationService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "The accommodation with id: " + id + " was successfully deleted"));
    }

}