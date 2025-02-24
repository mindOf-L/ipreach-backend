package app.ipreach.backend.mapper.payload.user;

import app.ipreach.backend.db.model.Token;
import app.ipreach.backend.mapper.dto.auth.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenMapper {

    TokenMapper MAPPER = Mappers.getMapper(TokenMapper.class);

    TokenDto toDto(Token token);

}
