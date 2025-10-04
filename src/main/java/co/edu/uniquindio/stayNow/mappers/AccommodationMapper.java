package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.AccommodationDTO;
import co.edu.uniquindio.stayNow.dto.CreateAccommodationDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {

    //No se coloca el usuario pq se obtiene del token
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.AccommodationStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRate", expression = "java(0.0)")
    @Mapping(target = "address.address", source = "address" )
    @Mapping(target = "address.city", source = "city" )
    @Mapping(target = "address.location.latitude", source = "latitude" )
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "address.location.longitude", source = "longitude" )
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    @Mapping(target = "hostId", ignore = true)
    AccommodationDTO toAccommodationDTO(Accommodation accommodation);

}
