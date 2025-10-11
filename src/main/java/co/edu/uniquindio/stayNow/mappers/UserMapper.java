package co.edu.uniquindio.stayNow.mappers;
import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.EditUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.UserStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserDTO userDTO);
    UserDTO toUserDTO(User user);
    // 3. De Edición DTO a Entidad (ACTUALIZACIÓN)
    // cambio para editar el usuario
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true) // ⬅️ CRÍTICO: La contraseña se cifra en el servicio, NO aquí.
    @Mapping(target = "accommodations", ignore = true) // Ignorar colecciones de relaciones
    // photoUrl, name, email, phone, role se mapean automáticamente
    void updateEntity(EditUserDTO dto, @MappingTarget User user);
}
