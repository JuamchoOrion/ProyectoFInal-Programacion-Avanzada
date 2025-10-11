package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.services.implementation.UserServiceImpl;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//TODAS LAS APIS LISTAS PARA ESTA SEMANA (28 DE SEPTIEMBRE)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthService authService;

    // Inyeccion de dependencias
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> register(@Valid @RequestBody CreateUserDTO userDTO) throws Exception {
        userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenDTO>> login(@RequestBody LoginRequestDTO loginDTO) throws Exception {
        TokenDTO token = authService.login(loginDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, token));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO<String>> resetPasswordRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Code sent successfully"));
    }

    @PatchMapping("/password/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmPassword(@RequestBody EditPasswordRequestDTO editPasswordRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Password updated successfully"));
    }
}
