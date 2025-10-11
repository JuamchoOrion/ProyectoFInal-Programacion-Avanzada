package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.exceptions.*;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.implementation.ReservationServiceImp;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings; // 🚨 Nueva importación
import org.mockito.quality.Strictness; // 🚨 Nueva importación

// ... (Resto de imports)

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class ReservationServiceimpTest {

    // 🚨 ARRANGE: Declaración de Mocks
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private AuthService authService;
    @Mock private UserRepository userRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private EmailService emailService;

    @InjectMocks
    private ReservationServiceImp reservationService;

    // Datos de prueba
    private final String GUEST_ID = "u001";
    private final String HOST_ID = "u002";
    private final String OTHER_USER_ID = "u003";
    private final Long ACCOMMODATION_ID = 1L;
    private final Long RESERVATION_ID = 50L;

    private User mockGuest;
    private User mockHost;
    private User mockOtherUser;
    private Accommodation mockAccommodation;
    private CreateReservationDTO validDto;

    @BeforeEach
    void setUp() {
        // 🚨 CONFIGURACIÓN: Solo se inicializan los objetos, no se configuran los 'when'
        mockGuest = new User(); mockGuest.setId(GUEST_ID); mockGuest.setEmail("guest@test.com"); mockGuest.setRole(Role.GUEST);
        mockHost = new User(); mockHost.setId(HOST_ID); mockHost.setEmail("host@test.com"); mockHost.setRole(Role.HOST);
        mockOtherUser = new User(); mockOtherUser.setId(OTHER_USER_ID); mockOtherUser.setRole(Role.GUEST);

        mockAccommodation = new Accommodation();
        mockAccommodation.setId(ACCOMMODATION_ID);
        mockAccommodation.setMaxGuests(4);
        mockAccommodation.setPricePerNight(100.0);
        mockAccommodation.setStatus(AccommodationStatus.ACTIVE);
        mockAccommodation.setHost(mockHost); // Host es u002

        validDto = new CreateReservationDTO(
                ACCOMMODATION_ID,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(8),
                2
        );
    }

    // ------------------------------------------------------------------------------------------------------------------
    // PRUEBAS PARA GET RESERVATION BY ID
    // ------------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GET BY ID SUCCESS: Huésped consulta su propia reserva")
    void testGetReservationByIdSuccessAsGuest() {
        // Arrange
        // 🚨 MOCKS ESPECÍFICOS: Configurar Autenticación para GUEST (u001)
        when(authService.getUserID()).thenReturn(GUEST_ID);
        when(userRepository.getUserById(GUEST_ID)).thenReturn(Optional.of(mockGuest));

        Reservation mockReservation = new Reservation();
        mockReservation.setGuest(mockGuest);
        mockReservation.setAccommodation(mockAccommodation);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));
        when(reservationMapper.toReservationDTO(any(Reservation.class))).thenReturn(new ReservationDTO(RESERVATION_ID, ACCOMMODATION_ID, GUEST_ID, null, null, null, null, null));

        // Act & Assert
        assertDoesNotThrow(() -> reservationService.getReservationById(RESERVATION_ID));
        verify(reservationMapper, times(1)).toReservationDTO(mockReservation);
    }

    @Test
    @DisplayName("GET BY ID SUCCESS: Anfitrión consulta reserva de su alojamiento")
    void testGetReservationByIdSuccessAsHost() {
        // Arrange
        // 🚨 MOCKS ESPECÍFICOS: Configurar Autenticación para HOST (u002)
        when(authService.getUserID()).thenReturn(HOST_ID);
        when(userRepository.getUserById(HOST_ID)).thenReturn(Optional.of(mockHost));

        Reservation mockReservation = new Reservation();
        mockReservation.setGuest(mockOtherUser); // Huésped es u003
        mockReservation.setAccommodation(mockAccommodation); // Alojamiento es de u002 (HOST)

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));
        when(reservationMapper.toReservationDTO(any(Reservation.class))).thenReturn(new ReservationDTO(RESERVATION_ID, ACCOMMODATION_ID, OTHER_USER_ID, null, null, null, null, null));

        // Act & Assert
        assertDoesNotThrow(() -> reservationService.getReservationById(RESERVATION_ID));
        verify(reservationMapper, times(1)).toReservationDTO(mockReservation);
    }
    // Inside ReservationServiceTest.java

    @Test
    @DisplayName("GET BY ID FAIL: Usuario tercero intenta consultar reserva (Método Alternativo)")
    void testGetReservationByIdFailsUnauthorizedAlternative() {

        // 🚨 ARRANGE: 1. Definir los IDs (GUEST, HOST, INTRUDER)
        final String INTRUDER_ID = "u003_intruder"; // ID del usuario autenticado
        final String RESERVATION_OWNER_ID = "u001_owner";
        final String ACCOMMODATION_HOST_ID = "u002_host";
        final Long RESERVATION_TEST_ID = 99L;

        // ARRANGE: 2. Crear los Mocks de Usuarios necesarios para la verificación

        // Usuario Autenticado (INTRUDER)
        User mockIntruder = new User();
        mockIntruder.setId(INTRUDER_ID);

        // Dueño de la Reserva (OWNER)
        User mockOwner = new User();
        mockOwner.setId(RESERVATION_OWNER_ID);

        // Host del Alojamiento (HOST)
        User mockHostOwner = new User();
        mockHostOwner.setId(ACCOMMODATION_HOST_ID);

        // ARRANGE: 3. Configurar la cadena de propiedad del objeto Reservation

        Accommodation isolatedAccommodation = new Accommodation();
        isolatedAccommodation.setHost(mockHostOwner); // Host: u002_host

        Reservation mockReservation = new Reservation();
        mockReservation.setGuest(mockOwner); // Guest: u001_owner
        mockReservation.setAccommodation(isolatedAccommodation);

        // ARRANGE: 4. Mockear las llamadas al servicio y repositorio

        // Simular que el usuario autenticado es el INTRUDER
        when(authService.getUserID()).thenReturn(INTRUDER_ID);
        when(userRepository.getUserById(INTRUDER_ID)).thenReturn(Optional.of(mockIntruder));

        // Simular que el repositorio encuentra la reserva
        when(reservationRepository.findById(RESERVATION_TEST_ID)).thenReturn(Optional.of(mockReservation));

        // ACT & ASSERT
        // Se espera que falle porque INTRUDER_ID (u003) != OWNER_ID (u001) Y != HOST_ID (u002)
        assertThrows(UnauthorizedActionException.class, () -> reservationService.getReservationById(RESERVATION_TEST_ID));
        verify(reservationMapper, never()).toReservationDTO(any());
    }
    // ------------------------------------------------------------------------------------------------------------------

    // PRUEBAS UNITARIAS PARA CANCEL RESERVATION (Foco en los casos de fallo)

    @Test
    @DisplayName("CANCEL FAIL: Reserva ya está CANCELADA")
    void testCancelFailsIfAlreadyCanceled() {
        // Arrange
        // 🚨 MOCKS ESPECÍFICOS: Configurar Autenticación para el DUEÑO (u001)
        when(authService.getUserID()).thenReturn(GUEST_ID);
        when(userRepository.getUserById(GUEST_ID)).thenReturn(Optional.of(mockGuest));

        Reservation mockReservation = new Reservation();
        mockReservation.setGuest(mockGuest);
        mockReservation.setReservationStatus(ReservationStatus.CANCELED); // Estado que causa el fallo

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));

        // Act & Assert
        assertThrows(ReservationCancellationNotAllowedException.class, () -> reservationService.cancelReservation(RESERVATION_ID));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("CANCEL FAIL: Usuario tercero intenta cancelar (Violación de seguridad)")
    void testCancelFailsWhenUnauthorizedUserTries() {
        // Arrange
        // 🚨 MOCKS ESPECÍFICOS: Configurar Autenticación para TERCERO (u003)
        when(authService.getUserID()).thenReturn(OTHER_USER_ID);
        when(userRepository.getUserById(OTHER_USER_ID)).thenReturn(Optional.of(mockOtherUser));

        Reservation mockReservation = new Reservation();
        mockReservation.setGuest(mockGuest); // La reserva es de u001 (distinto del autenticado u003)
        mockReservation.setReservationStatus(ReservationStatus.PENDING);
        mockReservation.setCheckIn(LocalDateTime.now().plusDays(10));

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));

        // Act & Assert
        assertThrows(UnauthorizedActionException.class, () -> reservationService.cancelReservation(RESERVATION_ID));
        verify(reservationRepository, never()).save(any());
    }
    // Dentro de la clase ReservationServiceTest

    @Test
    @DisplayName("CANCEL FAIL: Rechazo de cancelación si faltan MENOS de 48 horas para el Check-In")
    void testCancelFailsUnder48HoursPolicy() {
        // Arrange
        // 1. Simular autenticación (necesario para el inicio del método)
        when(authService.getUserID()).thenReturn(GUEST_ID);
        when(userRepository.getUserById(GUEST_ID)).thenReturn(Optional.of(mockGuest));

        // 2. Crear reserva con Check-In dentro de la ventana de 48 horas (ej: 24 horas)
        Reservation mockReservation = new Reservation();
        mockReservation.setId(RESERVATION_ID);
        mockReservation.setGuest(mockGuest);
        mockReservation.setReservationStatus(ReservationStatus.PENDING);
        // 🚨 Configuración crítica: CheckIn en 24 horas (NO CANCELABLE)
        mockReservation.setCheckIn(LocalDateTime.now().plusHours(24).plusSeconds(1));

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));

        // Act & Assert
        // Se espera que lance la excepción porque Duration.between(now, checkIn) < 48
        assertThrows(ReservationCancellationNotAllowedException.class, () -> reservationService.cancelReservation(RESERVATION_ID));

        // Assert final: Verificar que NO se intentó guardar (la reserva NO cambió de estado)
        verify(reservationRepository, never()).save(any());
    }
    @Test
    @DisplayName("CANCEL SUCCESS: Más de 48 horas restantes y se guarda CANCELED")
    void testCancelReservationSuccess() throws Exception {
        // Arrange
        when(authService.getUserID()).thenReturn(GUEST_ID);
        when(userRepository.getUserById(GUEST_ID)).thenReturn(Optional.of(mockGuest));

        Reservation mockReservation = new Reservation();
        mockReservation.setId(RESERVATION_ID);
        mockReservation.setGuest(mockGuest);
        mockReservation.setReservationStatus(ReservationStatus.PENDING);
        // 🚨 Configuración crítica: CheckIn futuro (72 horas > 48 horas)
        mockReservation.setCheckIn(LocalDateTime.now().plusHours(72).plusSeconds(1));

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);
        // Simular el mapeo para el retorno
        when(reservationMapper.toReservationDTO(any(Reservation.class))).thenReturn(new ReservationDTO(RESERVATION_ID, ACCOMMODATION_ID, GUEST_ID, null, null, null, null, ReservationStatus.CANCELED));

        // Act
        ReservationDTO result = assertDoesNotThrow(() -> reservationService.cancelReservation(RESERVATION_ID));

        // Assert
        assertEquals(ReservationStatus.CANCELED, mockReservation.getReservationStatus());
        verify(reservationRepository, times(1)).save(mockReservation);
        verify(emailService, times(0)).sendMail(any()); // Asumo que no envías email al cancelar (aunque es buena práctica hacerlo)
        assertEquals(ReservationStatus.CANCELED, result.reservationStatus());
    }
}