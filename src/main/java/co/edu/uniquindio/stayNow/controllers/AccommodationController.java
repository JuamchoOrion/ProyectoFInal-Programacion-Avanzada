package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
//faltan los de comments
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    //aca debemos llamar al service para buscar y lsitar los accommodation despues con esa lista mapearla a el dto por eso se devuelve un responseENtity con un dto

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> getListOfAccomodation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String services, // lista separada por comas
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        List<AccommodationDTO> list = new ArrayList<AccommodationDTO>();
        //DE HECHO DEBEMOS RETORNAR UNA LISTA DE LOS DTOS, NO UN SOLO DTO. ARREGLAR CON EL SERVICE C:
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    //lo mismo tambien debe retornar una lista de dtos
    @GetMapping("/{id}/reservations")
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> getListOfReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status
    ){
        List<ReservationDTO> list = new ArrayList<ReservationDTO>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> createAccommodation(@RequestBody CreateAccommodationDTO accommodationDTO) throws Exception {
        AccommodationDTO dto = accommodationService.create(accommodationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false,dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> getAccommodationById(@PathVariable long id){
        return ResponseEntity.ok(new ResponseDTO<>(false,new AccommodationDTO()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> editAccommodation(@PathVariable long id, @RequestBody AccommodationDTO accommodationDTO){
        return ResponseEntity.ok(new ResponseDTO<>(false, "alojamiento editado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAccommodation(@PathVariable long id){
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento fue eliminado exitosamente"));
    }
    @GetMapping("/{id}/review")
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> getReviews(@PathVariable long id){
        List<ReviewDTO> list = new ArrayList<ReviewDTO>();

        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
    @PostMapping("/{id}/review")
    public ResponseEntity<ResponseDTO<String>> createReview(@PathVariable long id, @RequestBody CreateReviewDTO reviewDTO){
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reseña creada con exito"));
    }

}