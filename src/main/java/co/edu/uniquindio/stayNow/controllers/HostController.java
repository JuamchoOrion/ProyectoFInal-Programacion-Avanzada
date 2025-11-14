package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.AccommodationDTO;
import co.edu.uniquindio.stayNow.dto.UserProfileDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import co.edu.uniquindio.stayNow.services.implementation.UserServiceImpl;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hosts")
@RequiredArgsConstructor
public class HostController {

    private final UserService userService;
    private final AccommodationService accommodationService;

    /** Devuelve datos completos del host logeado */
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<UserProfileDTO>> getCurrentHost() {
        try {
            String hostId = ((UserServiceImpl) userService).getAuthenticatedUserId();
            UserProfileDTO hostData = userService.getProfile(hostId);
            return ResponseEntity.ok(new ResponseDTO<>(false, hostData));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ResponseDTO<>(true, null, e.getMessage()));
        }
    }

    /** Devuelve datos de un host por id */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserProfileDTO>> getHostById(@PathVariable String id) {
        try {
            UserProfileDTO hostData = userService.get(id);
            return ResponseEntity.ok(new ResponseDTO<>(false, hostData));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>(true, null, e.getMessage()));
        }
    }

    /** Devuelve alojamientos de un host */
    @GetMapping("/{id}/accommodations")
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> getHostAccommodations(@PathVariable String id) {
        try {
            List<AccommodationDTO> accommodations = accommodationService.getAccommodationsByHostId(id);
            return ResponseEntity.ok(new ResponseDTO<>(false, accommodations));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>(true, null, e.getMessage()));
        }
    }
}
