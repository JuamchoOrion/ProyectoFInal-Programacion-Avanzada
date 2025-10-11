package co.edu.uniquindio.stayNow.services.implementation;


import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.exceptions.EmailAlreadyInUseException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.mappers.UserMapper;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    //Esto se llama inyeccion de dependencias
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {

        if (isEmailDuplicated(userDTO.email())) {
            throw new EmailAlreadyInUseException("Email is already in use.");
        }

        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyInUseException("Email is already in use.");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name(userDTO.name())
                .email(userDTO.email())
                .phone(userDTO.phone())
                .role(userDTO.role())
                .dateBirth(userDTO.dateBirth())
                //.photoUrl(userDTO.photoUrl())
                .password(passwordEncoder.encode(userDTO.password())) // 🔑 encrypted
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(newUser);
    }

    @Override
    public UserDTO get(String id) throws Exception {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        return userMapper.toUserDTO(user);
    }

    @Override
    public void delete(String id) throws Exception {
        User removedUser = userRepository.findById(id).orElse(null);

        if (removedUser == null) {
            throw new UserNotFoundException("User not found.");
        }
        userRepository.delete(removedUser);
    }

    @Override
    public List<UserDTO> listAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        if (!user.getEmail().equalsIgnoreCase(userDTO.email())
                && isEmailDuplicated(userDTO.email())) {
            throw new EmailAlreadyInUseException("Email is already in use.");
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
        return userRepository.findByEmail(email).isPresent();
    }

    private String encode(String password) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    // ... dentro de UserServiceImpl ...

    @Override
    public boolean isHost(Long userId) {
        return userRepository.findById(String.valueOf(userId))
                .map(user -> user.getRole() == Role.HOST)
                .orElse(false);
    }
}