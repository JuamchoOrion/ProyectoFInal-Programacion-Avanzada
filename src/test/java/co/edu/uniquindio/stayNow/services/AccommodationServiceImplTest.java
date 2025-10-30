package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.exceptions.UnauthorizedActionException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import co.edu.uniquindio.stayNow.model.enums.*;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.repositories.AccommodationRepository;
import co.edu.uniquindio.stayNow.repositories.ReservationRepository;
import co.edu.uniquindio.stayNow.repositories.ReviewRepository;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.implementation.AccommodationServiceImpl;

import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.ImageService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceImplTest {

    @Mock
    private AccommodationRepository accommodationRepo;
    @Mock
    private ReservationRepository reservationRepo;
    @Mock
    private ReviewRepository reviewRepo;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private AuthService authService;
    @Mock
    private ImageService imageService;
    // <-- este faltaba
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private User host;
    private User guest;
    private Accommodation accommodation;
    private Reservation reservation;
    private CreateAccommodationDTO createDTO;
    private EditAccommodationDTO editDTO;
    private AccommodationDTO accommodationDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        host = new User();
        host.setId("HOST-001");
        host.setEmail("host@example.com");
        host.setRole(Role.HOST);

        guest = new User();
        guest.setId("GUEST-001");
        guest.setRole(Role.GUEST);

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setHost(host);
        accommodation.setCity("Medellin");
        accommodation.setPricePerNight(100.0);
        accommodation.setMaxGuests(4);
        accommodation.setStatus(AccommodationStatus.ACTIVE);
        accommodation.setReservations(new ArrayList<>());
        accommodation.setAccommodationServiceTypes(Set.of(AccommodationServiceType.WIFI));
        accommodation.setImages(List.of("img1.png"));
        accommodation.setTitle("Beautiful House");
        accommodation.setDescription("Very nice");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setGuest(guest);
        reservation.setAccommodation(accommodation);
        reservation.setReservationStatus(ReservationStatus.CANCELED);
        reservation.setCheckIn(LocalDateTime.now().minusDays(1));
        reservation.setCheckOut(LocalDateTime.now());

        createDTO = new CreateAccommodationDTO(
                "Beautiful House",
                "Very nice",
                "Medellin",
                "123 Street",
                6.2f,
                -75.5f,
                100.0,
                4,
                List.of("WIFI", "POOL"),
                List.of("img1.png"),
                "img1.png",
                1,
                5
        );

        editDTO = new EditAccommodationDTO(
                "Edited House",
                "Edited description",
                "Bogota",
                "45 Avenue",
                4.5,
                -74.0,
                150.0,
                5,
                List.of("WIFI"),
                List.of("img2.png"),
                "img2.png"
        );

        accommodationDTO = new AccommodationDTO();
        accommodationDTO.setId("1");
        accommodationDTO.setHostId("HOST-001");
        accommodationDTO.setCity("Medellin");
        accommodationDTO.setPricePerNight(100.0);

        pageable = PageRequest.of(0, 10);
    }

    // ✅ create() success
    @Test
    void createAccommodation_Success() throws Exception {
        when(authService.getUserID()).thenReturn("HOST-001");
        when(userRepository.getUserById("HOST-001")).thenReturn(Optional.of(host));
        when(accommodationMapper.toEntity(createDTO)).thenReturn(accommodation);
        when(accommodationRepo.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toAccommodationDTO(accommodation)).thenReturn(accommodationDTO);

        AccommodationDTO result = accommodationService.create(createDTO);

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(accommodationRepo, times(1)).save(accommodation);
    }

    // ❌ create() fails when user not found
    @Test
    void createAccommodation_Fails_UserNotFound() {
        when(authService.getUserID()).thenReturn("HOST-001");
        when(userRepository.getUserById("HOST-001")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> accommodationService.create(createDTO));
    }

    // ❌ create() fails when user not host
    @Test
    void createAccommodation_Fails_UserNotHost() {
        when(authService.getUserID()).thenReturn("GUEST-001");
        when(userRepository.getUserById("GUEST-001")).thenReturn(Optional.of(guest));

        assertThrows(UnauthorizedActionException.class, () -> accommodationService.create(createDTO));
    }

    // ✅ get() success
    @Test
    void getAccommodation_Success() throws Exception {
        when(authService.getUserID()).thenReturn("HOST-001");
        when(userRepository.getUserById("HOST-001")).thenReturn(Optional.of(host));
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toAccommodationDTO(accommodation)).thenReturn(accommodationDTO);

        AccommodationDTO result = accommodationService.get(1L);

        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    // ❌ get() fails when accommodation not found
    @Test
    void getAccommodation_Fails_NotFound() {
        when(authService.getUserID()).thenReturn("HOST-001");
        when(userRepository.getUserById("HOST-001")).thenReturn(Optional.of(host));
        when(accommodationRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccommodationNotFoundException.class, () -> accommodationService.get(1L));
    }

    // ✅ edit() success
    @Test
    void editAccommodation_Success() throws Exception {
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationMapper).updateEntity(editDTO, accommodation);
        when(accommodationMapper.toAccommodationDTO(accommodation)).thenReturn(accommodationDTO);

        AccommodationDTO result = accommodationService.edit(1L, editDTO);

        assertNotNull(result);
        verify(accommodationMapper, times(1)).updateEntity(editDTO, accommodation);
        verify(accommodationRepo, times(1)).save(accommodation);
    }

    // ❌ edit() fails when accommodation not found
    @Test
    void editAccommodation_Fails_NotFound() {
        when(accommodationRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> accommodationService.edit(1L, editDTO));
    }

    // ✅ delete() success
    @Test
    void deleteAccommodation_Success() throws Exception {
        accommodation.setReservations(List.of(reservation));
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(accommodation));

        accommodationService.delete(1L);

        assertEquals(AccommodationStatus.DELETED, accommodation.getStatus());
        verify(accommodationRepo, times(1)).save(accommodation);
    }

    // ❌ delete() fails due to active reservations
    @Test
    void deleteAccommodation_Fails_ActiveReservations() {
        Reservation activeRes = new Reservation();
        activeRes.setReservationStatus(ReservationStatus.CONFIRMED);
        accommodation.setReservations(List.of(activeRes));

        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(accommodation));

        assertThrows(AccommodationNotFoundException.class, () -> accommodationService.delete(1L));
    }

    // ❌ delete() fails when accommodation not found
    @Test
    void deleteAccommodation_Fails_NotFound() {
        when(accommodationRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccommodationNotFoundException.class, () -> accommodationService.delete(1L));
    }

    // ✅ search() success
    @Test
    void searchAccommodation_Success() throws Exception {
        Page<Accommodation> page = new PageImpl<>(List.of(accommodation));
        when(accommodationRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(accommodationMapper.toAccommodationDTO(accommodation)).thenReturn(accommodationDTO);

        Page<AccommodationDTO> result = accommodationService.search(
                "Medellin", null, null, null, null, List.of("WIFI"), pageable
        );

        assertEquals(1, result.getContent().size());
    }

    // ✅ getReservations() success
    @Test
    void getReservations_Success() throws Exception {
        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        when(reservationRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(reservationMapper.toReservationDTO(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return new ReservationDTO(
                    r.getId(),
                    r.getAccommodation().getId(),
                    r.getGuest().getId(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    2,
                    100.0,
                    r.getReservationStatus()
            );
        });

        Page<ReservationDTO> result = accommodationService.getReservations(
                1L, null, null, null, pageable
        );

        assertEquals(1, result.getContent().size());
        assertEquals("GUEST-001", result.getContent().get(0).guestId());
    }
}

