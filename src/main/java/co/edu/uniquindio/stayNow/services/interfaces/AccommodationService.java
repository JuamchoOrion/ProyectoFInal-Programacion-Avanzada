package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AccommodationService {

    AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception;

    AccommodationDTO get(Long id) throws Exception;

    AccommodationDTO edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception;

    void delete(Long id) throws Exception;

    Page<AccommodationDTO> listAll() throws Exception;

    Page<AccommodationDTO> search(String city,
                                  LocalDateTime checkIn,
                                  LocalDateTime checkOut,
                                  Double minPrice,
                                  Double maxPrice,
                                  List<String> services,
                                  Pageable pageable) throws Exception;

    Page<ReservationDTO> getReservations(Long accommodationId,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         List<String> status,
                                         Pageable pageable) throws Exception;

    List<AccommodationDTO> getAccommodationsByHostId(String hostId) throws Exception;


}