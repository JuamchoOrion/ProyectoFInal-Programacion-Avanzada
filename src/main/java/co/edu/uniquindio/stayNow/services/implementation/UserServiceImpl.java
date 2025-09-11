package co.edu.uniquindio.stayNow.services.implementation;


import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.mappers.UserMapper;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        if (isEmailDuplicated(userDTO.email())) {
            throw new Exception("El correo electrónico ya está en uso.");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name(userDTO.name())
                .email(userDTO.email())
                .phone(userDTO.phone())
                .role(userDTO.role())
                .dateBirth(userDTO.dateBirth())
                .photoUrl(userDTO.photoUrl())
                .password(encode(userDTO.password())) // 🔑 cifrado
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        userStore.put(newUser.getId(), newUser);
    }

    @Override
    public UserDTO get(String id) throws Exception {
        User user = userStore.get(id);

        if (user == null) {
            throw new Exception("Usuario no encontrado.");
        }

        return userMapper.toUserDTO(user);
    }

    @Override
    public void delete(String id) throws Exception {
        User removedUser = userStore.remove(id);

        if (removedUser == null) {
            throw new Exception("Usuario no encontrado.");
        }
    }

    @Override
    public List<UserDTO> listAll() {
        return userStore.values()
                .stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        User user = userStore.get(id);

        if (user == null) {
            throw new Exception("Usuario no encontrado.");
        }

        if (!user.getEmail().equalsIgnoreCase(userDTO.email())
                && isEmailDuplicated(userDTO.email())) {
            throw new Exception("El correo electrónico ya está en uso.");
        }

        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setEmail(userDTO.email());
        user.setPhotoUrl(userDTO.photoUrl());
        user.setRole(userDTO.role());

        if (userDTO.password() != null && !userDTO.password().isBlank()) {
            user.setPassword(encode(userDTO.password()));
        }
    }

    private boolean isEmailDuplicated(String email) {
        return userStore.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    private String encode(String password) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
