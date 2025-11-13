package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.model.entity.PasswordResetCode;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import co.edu.uniquindio.stayNow.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.security.JWTUtils;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final JWTUtils jwtUtils;

    // ======================================================
    // LOGIN
    // ======================================================
    @Override
    public TokenDTO login(LoginRequestDTO loginDTO) throws Exception {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new UserNotFoundException("El usuario no existe."));

        // Solo cuentas activas pueden acceder
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("La cuenta está inactiva o suspendida.");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas.");
        }

        // Generar token JWT con claims
        String token = jwtUtils.generateToken(user.getId(), createClaims(user));
        return new TokenDTO(token);
    }

    private Map<String, String> createClaims(User user) {
        return Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "role", "ROLE_" + user.getRole().name()
        );
    }
    public String getUserIDFromToken(String token) {
        try {
            return jwtUtils.parseJwt(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }


    // ======================================================
    // OBTENER ID DE USUARIO AUTENTICADO
    // ======================================================
    @Override
    public String getUserID() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ======================================================
    // SOLICITAR RESTABLECIMIENTO DE CONTRASEÑA
    // ======================================================
    @Override
    public void resetPasswordRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) throws Exception {
        User user = userRepository.findByEmail(resetPasswordRequestDTO.email())
                .orElseThrow(() -> new UserNotFoundException("El correo no está asociado a una cuenta registrada."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("La cuenta está inactiva, no puede restablecer la contraseña.");
        }

        // Eliminar código anterior si existe
        passwordResetCodeRepository.findByUser(user).ifPresent(passwordResetCodeRepository::delete);

        // Generar un nuevo código de 6 dígitos
        int code = (int) (Math.random() * 900000) + 100000;

        PasswordResetCode passwordResetCode = new PasswordResetCode();
        passwordResetCode.setUser(user);
        passwordResetCode.setCode(String.valueOf(code));
        passwordResetCode.setCreatedAt(LocalDateTime.now());

        passwordResetCodeRepository.save(passwordResetCode);

        // Enviar correo con el código
        emailService.sendMail(new EmailDTO(
                "Código para restablecer contraseña",
                "Hola " + user.getName() + ",\n\n" +
                        "Tu código de restablecimiento de contraseña es: " + code + "\n" +
                        "Este código expirará en 30 minutos.",
                user.getEmail()
        ));
    }

    // ======================================================
    // CONFIRMAR CÓDIGO DE RESTABLECIMIENTO
    // ======================================================
    @Override
    public void confirmPassword(EditPasswordRequestDTO editPasswordRequestDTO) throws Exception {

        User user = userRepository.findByEmail(editPasswordRequestDTO.email())
                .orElseThrow(() -> new UserNotFoundException("El correo no está asociado a una cuenta registrada."));

        PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByUser(user)
                .orElseThrow(() -> new PasswordResetCodeNotFoundException("No existe un código de recuperación activo."));

        // Validar código
        if (!passwordResetCode.getCode().equals(editPasswordRequestDTO.code())) {
            throw new InvalidResetCodeException("El código ingresado no es válido.");
        }

        // Validar expiración (30 minutos)
        if (passwordResetCode.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(30))) {
            throw new ExpiredResetCodeException("El código de recuperación ha expirado.");
        }

        // Cambiar contraseña
        String encodedPassword = passwordEncoder.encode(editPasswordRequestDTO.newPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Eliminar el código usado
        passwordResetCodeRepository.delete(passwordResetCode);
    }

    // ======================================================
    // OBTENER USUARIO AUTENTICADO
    // ======================================================
    @Override
    public User getCurrentUser() throws Exception {
        String userId = getUserID();

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("La cuenta está inactiva o suspendida.");
        }

        return user;
    }
}