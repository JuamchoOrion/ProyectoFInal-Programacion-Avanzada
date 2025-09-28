package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;

public interface AuthService {
    String login(LoginRequestDTO loginRequestDTO) throws Exception;
}
