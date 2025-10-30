package co.edu.uniquindio.stayNow.services.implementation;
import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.PasswordResetCodeNotFoundException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.PasswordResetCode;
import co.edu.uniquindio.stayNow.model.entity.User;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginRequestDTO loginDTO) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.email());

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if(!user.getStatus().equals("ACTIVE")){
            throw new UserNotFoundException("User is not active");
        }

        // Verify if the password is correct
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new UserNotFoundException("User does not exist");
        }

        // Generate token with claims
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

    /**
     * Returns the ID of the authenticated user
     * (the "sub" subject stored in the JWT when it was generated).
     */
    public String getUserID() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public void resetPasswordRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) throws Exception {
        User user = userRepository.findByEmail(resetPasswordRequestDTO.email())
                .orElseThrow(() -> new UserNotFoundException("The given email does not have a registered user"));
        PasswordResetCode passwordResetCode = new PasswordResetCode();
        passwordResetCode.setUser(user);

        // Genera un código aleatorio de 6 dígitos
        int code = (int) (Math.random() * 900000) + 100000;
        passwordResetCode.setCode(String.valueOf(code));
        passwordResetCode.setCreatedAt(LocalDateTime.now());
        passwordResetCodeRepository.save(passwordResetCode);
        // Envía correo con el código
        emailService.sendMail(new EmailDTO(
                "Password Reset Code",
                "To change your password, please enter this code on the website: " + code,
                user.getEmail()
        ));
    }

    @Override
    public void confirmPassword(EditPasswordRequestDTO editPasswordRequestDTO) throws Exception {

        User user = userRepository.findByEmail(editPasswordRequestDTO.email()).orElseThrow(()-> new UserNotFoundException("The given email does not have a registered user"));
        PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByUser(user).orElseThrow(() -> new PasswordResetCodeNotFoundException("The given Password Reset code does not exist "));

        if (!passwordResetCode.getCode().equals(editPasswordRequestDTO.code())) {
            throw new Exception("Invalid reset code");
        }

        if (passwordResetCode.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(30))) {
            throw new Exception("Reset code has expired");
        }

        String encodedPassword = passwordEncoder.encode(editPasswordRequestDTO.newPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        passwordResetCodeRepository.delete(passwordResetCode);
    }

    /**
     * Returns the complete User entity of the authenticated user
     */
    @Override
    public User getCurrentUser() throws Exception {
        String userId = getUserID();

        return userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}