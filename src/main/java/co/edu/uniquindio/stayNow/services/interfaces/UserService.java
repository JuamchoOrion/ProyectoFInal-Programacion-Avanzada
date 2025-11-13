package co.edu.uniquindio.stayNow.services.interfaces;



import co.edu.uniquindio.stayNow.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    void create(CreateUserDTO userDTO) throws Exception;

    boolean isHost(Long userId);

    UserProfileDTO getProfile(String id) throws Exception;
    UserProfileDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UserDTO> listAll();

    void edit(EditUserDTO userDTO, MultipartFile photo) throws Exception;

    void changePassword(ChangePasswordRequestDTO newPasswordRequest) throws Exception;
    void becomeHost() throws Exception;
    String getAuthenticatedUserId() throws Exception;
}