package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.*;

import java.util.List;

public interface AccommodationService {

    AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception;

    AccommodationDTO get(Long id) throws Exception;

    void edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception;

    void delete(Long id) throws Exception;

    List<AccommodationDTO> listAll() throws Exception;

    List<AccommodationDTO> search(String city,
                                  String checkIn,
                                  String checkOut,
                                  Double minPrice,
                                  Double maxPrice,
                                  List<String> services,
                                  int page,
                                  int size) throws Exception;

    List<ReservationDTO> getReservations(Long accommodationId,
                                         String startDate,
                                         String endDate,
                                         String status) throws Exception;

    void createReservation(Long accommodationId,
                           CreateReservationDTO reservationDTO) throws Exception;

    List<ReviewDTO> getReviews(Long accommodationId) throws Exception;

    void createReview(Long accommodationId,
                      CreateReviewDTO reviewDTO) throws Exception;

    void replyToReview(Long accommodationId,
                       Long reviewId,
                       ReplyReviewDTO replyDTO) throws Exception;
}