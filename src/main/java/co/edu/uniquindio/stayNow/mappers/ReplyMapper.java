package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.ReplyDTO;
import co.edu.uniquindio.stayNow.dto.ReplyReviewDTO;
import co.edu.uniquindio.stayNow.model.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReplyMapper {

    // ReplyReviewDTO a Reply
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "repliedAt", expression = "java(java.time.LocalDateTime.now())")
    Reply toEntity(ReplyReviewDTO dto);

    // Reply a ReplyDTO
    ReplyDTO toDTO(Reply reply);
}
