package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.model.enums.*;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.security.JWTUtils;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private AuthServiceImp authService;

    private User user;
    private LoginRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("USER-001");
        user.setEmail("user@example.com");
        user.setName("Test User");
        user.setPassword("encodedPassword");
        user.setRole(Role.GUEST);

        loginDTO = new LoginRequestDTO("user@example.com", "password123");
    }

    // ✅ login success
    @Test
    void login_Success() throws Exception {
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.password(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(eq(user.getId()), anyMap())).thenReturn("fake-jwt-token");

        TokenDTO tokenDTO = authService.login(loginDTO);

        assertNotNull(tokenDTO);
        assertEquals("fake-jwt-token", tokenDTO.token());
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        verify(passwordEncoder, times(1)).matches(loginDTO.password(), user.getPassword());
        verify(jwtUtils, times(1)).generateToken(eq(user.getId()), anyMap());
    }

    // ❌ login fails - user not found
    @Test
    void login_Fails_UserNotFound() {
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginDTO));
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString(), anyMap());
    }

    // ❌ login fails - wrong password
    @Test
    void login_Fails_WrongPassword() {
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.password(), user.getPassword())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> authService.login(loginDTO));
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        verify(passwordEncoder, times(1)).matches(loginDTO.password(), user.getPassword());
        verify(jwtUtils, never()).generateToken(anyString(), anyMap());
    }

    // ✅ getCurrentUser success
    @Test
    void getCurrentUser_Success() throws Exception {
        // Mock SecurityContext
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        when(auth.getName()).thenReturn("USER-001");

        when(userRepository.getUserById("USER-001")).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals("USER-001", result.getId());
        verify(userRepository, times(1)).getUserById("USER-001");
    }

    // ❌ getCurrentUser fails - user not found
    @Test
    void getCurrentUser_Fails_UserNotFound() {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        when(auth.getName()).thenReturn("USER-001");

        when(userRepository.getUserById("USER-001")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.getCurrentUser());
        verify(userRepository, times(1)).getUserById("USER-001");
    }
}
