package co.edu.uniquindio.stayNow.mappers;
import co.edu.uniquindio.stayNow.dto.CreateUserDTO;
import co.edu.uniquindio.stayNow.dto.UserDTO;
import co.edu.uniquindio.stayNow.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.stayNow.model.enums.UserStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserDTO userDTO);
    UserDTO toUserDTO(User user);
}
