package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
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
        return;
    }
}
