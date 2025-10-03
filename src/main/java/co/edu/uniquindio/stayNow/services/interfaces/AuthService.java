package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.dto.TokenDTO;

public interface AuthService {
    TokenDTO login(LoginRequestDTO loginRequestDTO) throws Exception;

    String getUserID();
}
