package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import org.apache.tomcat.util.http.parser.Authorization;


public class AuthServiceImp implements AuthService {
    Authorization authorization;
    JwtUtils jwtUtils;
    @Override
    public String login(LoginRequestDTO loginRequestDTO) throws Exception {
    return "";
    }
}
