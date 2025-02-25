package app.ipreach.backend.users.payload.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record LocationDto(

    Long id,
    String name,
    String address,
    String url,
    String details

){}
