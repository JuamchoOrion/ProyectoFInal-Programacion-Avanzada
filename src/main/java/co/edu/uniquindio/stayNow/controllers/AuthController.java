package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.security.JWTUtils;
import co.edu.uniquindio.stayNow.services.implementation.UserDetailsServiceImpl;
import co.edu.uniquindio.stayNow.services.implementation.UserServiceImpl;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//TODAS LAS APIS LISTAS PARA ESTA SEMANA (28 DE SEPTIEMBRE)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthService authService;
    private final JWTUtils jwtUtil;

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
                .httpOnly(false)          // No accesible desde JS
                .secure(false)           // true si usas HTTPS nota: cmabie a false para probar porqu epor ahora es http sin seguridad
                .path("/")               // accesible en toda la app
                .maxAge(24 * 60 * 60)    // 1 día
                .sameSite("lax")         // o "Strict" / "None" (si usas HTTPS + CORS)
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
    public ResponseEntity<ResponseDTO<Boolean>> validateToken(@CookieValue(value = "jwt", required = false) String jwt) {

        if (jwt == null || jwt.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(false, false, "Sin sesión activa"));
        }
        try {
            jwtUtil.parseJwt(jwt);
            return ResponseEntity.ok(new ResponseDTO<>(false, true, "Token válido"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO<>(false, false, "Token inválido o expirado"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        // 🔹 Borra la cookie JWT
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> result = new HashMap<>();
        result.put("error", false);
        result.put("content", "Sesión cerrada correctamente.");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result);
    }
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(true, Map.of("message", "No autenticado")));
        }

        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        String id = springUser.getUsername(); // tu id lo estás guardando como "username"
        String role = springUser.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN");

        Map<String, Object> userInfo = Map.of(
                "id", id,
                "role", role
        );

        return ResponseEntity.ok(new ResponseDTO<>(false, userInfo));
    }
    @GetMapping("/socket-token")
    public ResponseEntity<ResponseDTO<String>> getSocketToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return ResponseEntity.ok(new ResponseDTO<>(false, cookie.getValue()));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDTO<>(true, null, "No autenticado"));
    }



}
