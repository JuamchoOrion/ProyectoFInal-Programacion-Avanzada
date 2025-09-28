package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
//faltan los de comments
public class AccommodationController {
    //aca debemos llamar al service para buscar y lsitar los accommodation despues con esa lista mapearla a el dto por eso se devuelve un responseENtity con un dto
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccomodationDTO>>> getListOfAccomodation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String services, // lista separada por comas
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        List<AccomodationDTO> list = new ArrayList<AccomodationDTO>();
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

    @PostMapping("/{id}/reservations")
    public ResponseEntity<ResponseDTO<String>> createAccommodation(@RequestBody CreateAccommodationDTO AccomodationDTO){
        return ResponseEntity.ok(new ResponseDTO<>(false,"El alojamiento ha sido creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccomodationDTO>> getAccommodationById(@PathVariable long id){
        return ResponseEntity.ok(new ResponseDTO<>(false,new AccomodationDTO()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> editAccommodation(@PathVariable long id, @RequestBody AccomodationDTO accomodationDTO){
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