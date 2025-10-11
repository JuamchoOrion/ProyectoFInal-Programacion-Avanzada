package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.EditPasswordRequestDTO;
import co.edu.uniquindio.stayNow.dto.LoginRequestDTO;
import co.edu.uniquindio.stayNow.dto.ResetPasswordRequestDTO;
import co.edu.uniquindio.stayNow.dto.TokenDTO;
import co.edu.uniquindio.stayNow.model.entity.User;

public interface AuthService {
    TokenDTO login(LoginRequestDTO loginRequestDTO) throws Exception;
    User getCurrentUser() throws Exception;
    String getUserID();
    void resetPasswordRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) throws Exception;
    void confirmPassword(EditPasswordRequestDTO editPasswordRequestDTO) throws Exception;
}
