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
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception{
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido eliminado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) throws Exception{
        userService.edit(id, userDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido actualizado"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@PathVariable String id, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña actualizada"));
    }


}
