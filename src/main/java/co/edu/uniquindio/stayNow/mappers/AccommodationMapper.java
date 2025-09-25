package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.AccomodationDTO;
import co.edu.uniquindio.stayNow.dto.CreateAccommodationDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {
    //No se coloca el usuario pq se obtiene del token
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.AccommodationStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRate", expression = "java(0.0)")
    @Mapping(target = "address", expression = "java(new co.edu.uniquindio.stayNow.model.entity.Address())")
    //Como se mapea el address, es una clase propia? con una expresion en ese caso como la creo o llamo a algo que lo busque en la bd consulta
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);
    AccomodationDTO toAccommodationDTO(Accommodation accommodation);
}
