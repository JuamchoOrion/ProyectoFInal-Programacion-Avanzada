package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.dto.TokenDTO;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import co.edu.uniquindio.stayNow.security.JWTUtils;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class AuthServiceImp implements AuthService {
    private final Authorization authorization;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    @Override
    public TokenDTO login(LoginRequestDTO loginDTO) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.email());

        if(optionalUser.isEmpty()){
            throw new Exception("El usuario no existe");
        }

        User user = optionalUser.get();

        // Verificar si la contraseña es correcta usando el PasswordEncoder
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new Exception("El usuario no existe");
        }

        String token = jwtUtils.generateToken(user.getId(), createClaims(user));
        return new TokenDTO(token);
    }

    private Map<String, String> createClaims(User user){
        return Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "role", "ROLE_"+user.getRole().name()
        );
    }

    public String getUserID(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idUser = user.getId();
        return idUser;
    }
}
