package co.edu.uniquindio.stayNow.controllers;
import java.util.*;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserProfileDTO>> get(@PathVariable String id) throws Exception{
        UserProfileDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "The user has been deleted"));
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<String>> edit(
            @Valid @RequestPart("user") EditUserDTO userDTO,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws Exception {

        userService.edit(userDTO, photo);

        return ResponseEntity.ok(new ResponseDTO<>(false, "The user has been updated"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@RequestBody ChangePasswordRequestDTO newPasswordRequest) throws Exception {
        userService.changePassword(newPasswordRequest);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Password updated"));
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<UserProfileDTO>> getProfile() throws Exception {
        // Obtiene el ID directamente desde el token (ya autenticado por el filtro JWT)
        String id = userService.getAuthenticatedUserId();

        UserProfileDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }


}
