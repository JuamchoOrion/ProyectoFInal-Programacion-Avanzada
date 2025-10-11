package co.edu.uniquindio.stayNow.controllers;
import java.util.*;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> get(@PathVariable String id) throws Exception{
        UserDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "The user has been deleted"));
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<String>> edit( @Valid @RequestBody EditUserDTO userDTO) throws Exception {
        userService.edit( userDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "The user has been updated"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@PathVariable String id, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Password updated"));
    }


}
