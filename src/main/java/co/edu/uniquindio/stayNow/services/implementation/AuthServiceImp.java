package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;

@AllArgsConstructor
public class AuthServiceImp implements AuthService {
    Authorization authorization;
    UserRepository userRepository;
    @Override
    public String login(LoginRequestDTO loginRequestDTO) throws Exception {
        User user  = userRepository.findByEmail(loginRequestDTO.email()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado");
        }
        return "";
    }

    public String getUserID(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idUser = user.getUsername();
        return idUser;
    }
}
