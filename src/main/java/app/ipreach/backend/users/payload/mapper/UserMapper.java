package app.ipreach.backend.users.payload.mapper;

import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static app.ipreach.backend.shared.creation.EncryptedPassword.getEncryptedPassword;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "password", target = "password", qualifiedByName = "mapPasswordToEncryptedPassword")
    User toEntity(UserDto userDto);

    @Mapping(target = "password", ignore = true)
    UserDto toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "botId", ignore = true)
    @Mapping(target = "botName", ignore = true)
    @Mapping(target = "botPhone", ignore = true)
    @Mapping(target = "phone2", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "congregation", ignore = true)
    UserDto toDtoForAssignment(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "password", target = "password", qualifiedByName = "mapPasswordToEncryptedPassword")
    void update(UserDto userFrom, @MappingTarget User userTo);

    @Named("mapPasswordToEncryptedPassword")
    static String mapPasswordToEncryptedPassword(String password) {
        return getEncryptedPassword(password);
    }

}
