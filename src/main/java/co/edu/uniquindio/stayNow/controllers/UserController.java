package co.edu.uniquindio.stayNow.controllers;
import java.util.*;
import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateUserDTO userDTO) throws Exception{
        userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> get(@PathVariable String id) throws Exception{
        UserDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception{
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido eliminado"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserDTO>>> listAll(){
        List<UserDTO> list = userService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) throws Exception{
        userService.edit(id, userDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido actualizado"));
    }

}
