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

        if(optionalUser.isEmpty()){
            throw new UserNotFoundException("El usuario no existe");
        }

        User user = optionalUser.get();

        // Verificar si la contraseña es correcta
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new UserNotFoundException("El usuario no existe");
        }

        // Generar token con claims
        String token = jwtUtils.generateToken(user.getId(), createClaims(user));
        return new TokenDTO(token);
    }

    private Map<String, String> createClaims(User user){
        return Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "role", "ROLE_" + user.getRole().name()
        );
    }

    /**
     * Devuelve el ID del usuario autenticado
     * (el subject "sub" que se guardó en el JWT al generarlo).
     */
    public String getUserID(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Devuelve la entidad User completa del usuario autenticado
     */
    @Override
    public User getCurrentUser() throws Exception {
        String userId = getUserID();
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
