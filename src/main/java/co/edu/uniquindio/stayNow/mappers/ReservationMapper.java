package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.CreateReservationDTO;
import co.edu.uniquindio.stayNow.dto.ReservationDTO;
import co.edu.uniquindio.stayNow.model.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping (target = "reservationStatus", expression = "java(co.edu.uniquindio.stayNow.model.enums.ReservationStatus.PENDING)")
    Reservation toEntity(CreateReservationDTO ReservationDTO);
    ReservationDTO toReservationDTO(Reservation reservation);

}
