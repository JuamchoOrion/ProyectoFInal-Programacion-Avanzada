package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.*;

import java.util.List;

public interface AccommodationService {

    void create(CreateAccommodationDTO accommodationDTO) throws Exception;

    AccomodationDTO get(String id) throws Exception;

    void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception;

    void delete(String id) throws Exception;

    List<AccomodationDTO> listAll() throws Exception;

    List<AccomodationDTO> search(String city,
                                 String checkIn,
                                 String checkOut,
                                 Double minPrice,
                                 Double maxPrice,
                                 List<String> services,
                                 int page,
                                 int size) throws Exception;

    List<ReservationDTO> getReservations(String accommodationId,
                                         String startDate,
                                         String endDate,
                                         String status) throws Exception;

    void createReservation(String accommodationId,
                           CreateReservationDTO reservationDTO) throws Exception;

    List<ReviewDTO> getReviews(String accommodationId) throws Exception;

    void createReview(String accommodationId,
                      CreateReviewDTO reviewDTO) throws Exception;

    void replyToReview(String accommodationId,
                       Long reviewId,
                       ReplyReviewDTO replyDTO) throws Exception;
}