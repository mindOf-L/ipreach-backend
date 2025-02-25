package app.ipreach.backend.users.payload.mapper;

import app.ipreach.backend.auth.db.model.Token;
import app.ipreach.backend.auth.payload.dto.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenMapper {

    TokenMapper MAPPER = Mappers.getMapper(TokenMapper.class);

    TokenDto toDto(Token token);

}
