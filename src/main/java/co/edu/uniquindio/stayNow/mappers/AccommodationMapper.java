package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.AccomodationDTO;
import co.edu.uniquindio.stayNow.dto.CreateAccommodationDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.entity.Address;
import co.edu.uniquindio.stayNow.model.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {

    //No se coloca el usuario pq se obtiene del token
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.AccommodationStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRate", expression = "java(0.0)")
    @Mapping(target = "address.address", source = "address" )
    @Mapping(target = "address.city", source = "city" )
    @Mapping(target = "address.location.latitude", source = "latitude" )
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "address.location.longitude", source = "longitude" )
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    @Mapping(target = "host", ignore = true)
    AccomodationDTO toAccommodationDTO(Accommodation accommodation);

}
