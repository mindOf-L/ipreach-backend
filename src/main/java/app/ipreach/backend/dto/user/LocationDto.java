package app.ipreach.backend.dto.user;

import lombok.Builder;

@Builder(toBuilder = true)
public record LocationDto(

    Long id,
    String name,
    String address,
    String url,
    String details

){}
