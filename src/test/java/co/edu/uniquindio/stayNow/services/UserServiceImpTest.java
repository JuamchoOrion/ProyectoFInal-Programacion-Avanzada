package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.exceptions.EmailAlreadyInUseException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.implementation.UserServiceImpl;
import co.edu.uniquindio.stayNow.mappers.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDTO createUserDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createUserDTO = new CreateUserDTO(
                "Juan Pérez",
                "3124567890",
                "juan@example.com",
                "password123",
                LocalDate.of(2000, 1, 1),
                Role.GUEST
        );

        user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Juan Pérez")
                .email("juan@example.com")
                .phone("3124567890")
                .password("encodedPass")
                .role(Role.GUEST)
                .status(UserStatus.ACTIVE)
                .dateBirth(LocalDate.of(2000, 1, 1))
                .build();
    }

    // ✅ Caso exitoso: crear usuario nuevo
    @Test
    void createUser_Success() throws Exception {
        when(userRepository.findByEmail(createUserDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        userService.create(createUserDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(createUserDTO.email(), savedUser.getEmail());
        assertEquals("encodedPass", savedUser.getPassword());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

    // ❌ Caso de error: email duplicado
    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail(createUserDTO.email())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyInUseException.class, () -> userService.create(createUserDTO));
        verify(userRepository, never()).save(any());
    }

    // ✅ Obtener usuario por id (caso exitoso)
    @Test
    void getUserById_Success() throws Exception {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(any(User.class))).thenReturn(new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhotoUrl(),
                user.getPassword(),
                user.getRole()
        ));

        UserDTO result = userService.get(user.getId());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.email());
        verify(userRepository, times(1)).findById(user.getId());
    }

    // ❌ Obtener usuario inexistente
    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById("fakeId")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.get("fakeId"));
    }

    // ✅ Eliminar usuario (caso exitoso)
    @Test
    void deleteUser_Success() throws Exception {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    // ❌ Eliminar usuario inexistente
    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById("fakeId")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete("fakeId"));
    }

    // ✅ Listar todos los usuarios
    @Test
    void listAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDTO(any(User.class))).thenReturn(new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhotoUrl(),
                user.getPassword(),
                user.getRole()
        ));

        var users = userService.listAll();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    // ✅ Verificar si un usuario es HOST
    @Test
    void isHost_True() {
        user.setRole(Role.HOST);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        boolean result = userService.isHost(1L);

        assertTrue(result);
    }

    @Test
    void isHost_False_WhenUserIsClient() {
        user.setRole(Role.GUEST);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        boolean result = userService.isHost(1L);

        assertFalse(result);
    }

    @Test
    void isHost_False_WhenUserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = userService.isHost(1L);

        assertFalse(result);
    }
}

