package co.edu.uniquindio.stayNow.services.interfaces;



import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;

import java.util.List;

public interface UserService {

    void create(CreateUserDTO userDTO) throws Exception;

    UserDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UserDTO> listAll();

    void edit(String id, EditUserDTO userDTO) throws Exception;

}