package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.services.implementation.UserServiceImpl;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
        String tokenstr = token.token();
        ResponseCookie cookie = ResponseCookie.from("jwt", tokenstr)
                .httpOnly(true)          // No accesible desde JS
                .secure(true)           // true si usas HTTPS
                .path("/")               // accesible en toda la app
                .maxAge(24 * 60 * 60)    // 1 día
                .sameSite("None")         // o "Strict" / "None" (si usas HTTPS + CORS)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // 👈 ESTA LÍNEA FALTABA
                .body(new ResponseDTO<>(false, token));

    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO<String>> resetPasswordRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) throws Exception {
        authService.resetPasswordRequest(resetPasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Code sent successfully"));
    }

    @PatchMapping("/password/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmPassword(@RequestBody EditPasswordRequestDTO editPasswordRequestDTO) throws Exception {
            authService.confirmPassword(editPasswordRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Password sent successfully"));
    }
    @GetMapping("/validate-token")
    public ResponseEntity<ResponseDTO<Boolean>> validateToken(HttpServletRequest request) {
        // El filtro JWT ya habrá validado el token y autenticado al usuario
        return ResponseEntity.ok(new ResponseDTO<>(false, true));
    }

}
