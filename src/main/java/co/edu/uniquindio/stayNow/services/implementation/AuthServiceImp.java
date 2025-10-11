package co.edu.uniquindio.stayNow.services.implementation;
import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.dto.TokenDTO;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.security.JWTUtils;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginRequestDTO loginDTO) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.email());

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

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