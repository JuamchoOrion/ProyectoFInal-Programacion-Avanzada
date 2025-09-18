package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    //Inyeccion de dependencias
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> register(@Valid @RequestBody CreateUserDTO userDTO) throws Exception{
        //userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO<String>> resetPasswordRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "Codigo enviado con exito"));
    }

    @PatchMapping("/password/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmPassword(@RequestBody EditPasswordRequestDTO editPasswordRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "Contraseña actualizada con exito"));
    }

}
