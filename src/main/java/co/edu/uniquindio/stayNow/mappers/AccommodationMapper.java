package co.edu.uniquindio.stayNow.mappers;

import co.edu.uniquindio.stayNow.dto.AccommodationDTO;
import co.edu.uniquindio.stayNow.dto.CreateAccommodationDTO;
import co.edu.uniquindio.stayNow.dto.EditAccommodationDTO;
import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {

    // ✅ toEntity: Asegura mapeo de mainImage e images
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.AccommodationStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRate", expression = "java(0.0)")
    @Mapping(target = "address.address", source = "address")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.location.latitude", source = "latitude")
    @Mapping(target = "address.location.longitude", source = "longitude")
    @Mapping(target = "host", ignore = true)
    // El mapeo de 'mainImage' e 'images' es implícito, pero lo haremos explícito para mayor seguridad:
    @Mapping(target = "mainImage", ignore = true) // ✅ se asigna después
    @Mapping(target = "images", ignore = true)
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    // ✅ toAccommodationDTO: Mapeo implícito (o ajusta si necesitas la URL de la imagen principal separada)
    @Mapping(target = "hostId", source = "host.id")
    @Mapping(target = "address", source = "address.address")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "latitude", source = "address.location.latitude")
    @Mapping(target = "longitude", source = "address.location.longitude")
    AccommodationDTO toAccommodationDTO(Accommodation accommodation);

    // ✅ updateEntity: El mapeo es implícito y correcto. Solo asegúrate de NO ignorar los campos de imágenes
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "averageRate", ignore = true)
    @Mapping(target = "address.address", source = "address")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.location.latitude", source = "latitude")
    @Mapping(target = "address.location.longitude", source = "longitude")
    // MapStruct mapeará mainImage e images porque no están en ignore y los nombres coinciden.
    void updateEntity(EditAccommodationDTO dto, @MappingTarget Accommodation accommodation);

}