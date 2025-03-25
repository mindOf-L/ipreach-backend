package app.ipreach.backend.locations.payload.mapper;

import app.ipreach.backend.locations.db.model.Location;
import app.ipreach.backend.locations.payload.dto.LocationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationMapper {

    LocationMapper MAPPER = Mappers.getMapper(LocationMapper.class);

    LocationDto toDto(Location location);

}
