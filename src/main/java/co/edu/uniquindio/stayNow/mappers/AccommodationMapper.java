package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.AccommodationDTO;
import co.edu.uniquindio.stayNow.dto.CreateAccommodationDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {

    // Para crear la entidad desde el DTO
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.AccommodationStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRate", expression = "java(0.0)")
    @Mapping(target = "address.address", source = "address")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.location.latitude", source = "latitude")
    @Mapping(target = "address.location.longitude", source = "longitude")
    @Mapping(target = "host", ignore = true)
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    // Para convertir la entidad en DTO de respuesta
    @Mapping(target = "hostId", source = "host.id")
    @Mapping(target = "address", source = "address.address")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "latitude", source = "address.location.latitude")
    @Mapping(target = "longitude", source = "address.location.longitude")
    AccommodationDTO toAccommodationDTO(Accommodation accommodation);
}
