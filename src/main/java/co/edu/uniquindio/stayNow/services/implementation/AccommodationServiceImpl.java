package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Address;
import co.edu.uniquindio.stayNow.model.entity.Location;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepo;
    private final ReservationRepository reservationRepo;
    private final ReviewRepository reviewRepo;
    private final UserService userService;
    private final AccommodationMapper accommodationMapper;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final AuthService authService;

    @Override
    public AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception {
            String id = authService.getUserID();
            User user = userRepository.getUserById(id).orElse(null);


            if(user == null) {
                throw new Exception("User not found");
            }

            if(!user.getRole().equals(Role.HOST)){
                throw new Exception("User is not host");
            }

            Address address = new Address();

            address.setCity(accommodationDTO.city());
            address.setLocation(new Location(accommodationDTO.latitude(), accommodationDTO.longitude()));
            address.setAddress(accommodationDTO.address());

            Accommodation accommodation = new Accommodation();

            accommodation.setTitle(accommodationDTO.title());
            accommodation.setCreatedAt(LocalDateTime.now());
            accommodation.setHost(user);
            accommodation.setCity(accommodationDTO.city());
            accommodation.setDescription(accommodationDTO.description());
            accommodation.setStatus(AccommodationStatus.ACTIVE);
            accommodation.setAverageRate(0.0);
            accommodation.setMaxGuests(accommodationDTO.maxGuests());
            accommodation.setAddress(address);
            accommodation.setPricePerNight(accommodationDTO.pricePerNight());

            Accommodation saved = accommodationRepo.save(accommodation);

            AccommodationDTO response = accommodationMapper.toAccommodationDTO(saved);
            return response;




    }

    @Override
    public AccommodationDTO get(Long id) throws Exception {
        return null;
    }

    @Override
    public void edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public List<AccommodationDTO> listAll() throws Exception {
        return List.of();
    }

    @Override
    public List<AccommodationDTO> search(String city, String checkIn, String checkOut, Double minPrice, Double maxPrice, List<String> services, int page, int size) throws Exception {
        return List.of();
    }

    @Override
    public List<ReservationDTO> getReservations(Long accommodationId, String startDate, String endDate, String status) throws Exception {
        return List.of();
    }

    @Override
    public void createReservation(Long accommodationId, CreateReservationDTO reservationDTO) throws Exception {

    }

    @Override
    public List<ReviewDTO> getReviews(Long accommodationId) throws Exception {
        return List.of();
    }

    @Override
    public void createReview(Long accommodationId, CreateReviewDTO reviewDTO) throws Exception {

    }

    @Override
    public void replyToReview(Long accommodationId, Long reviewId, ReplyReviewDTO replyDTO) throws Exception {

    }
}
