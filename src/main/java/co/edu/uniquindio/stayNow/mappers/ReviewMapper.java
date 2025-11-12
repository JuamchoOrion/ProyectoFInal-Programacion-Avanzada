package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.CreateReviewDTO;
import co.edu.uniquindio.stayNow.dto.ReviewDTO;
import co.edu.uniquindio.stayNow.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    // CreateReviewDTO a Review.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "reply", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "comment", source = "text")
    Review toEntity(CreateReviewDTO dto);

    // Review a ReviewDTO.
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "text", source = "comment")
    @Mapping(target = "reply", expression = "java(review.getReply() != null ? review.getReply().getMessage() : null)")
    @Mapping(target = "accommodationId", source = "accommodation.id")
    ReviewDTO toDTO(Review review);

    // Lista de reviews a DTOs.
    List<ReviewDTO> toDTOList(List<Review> reviews);
}