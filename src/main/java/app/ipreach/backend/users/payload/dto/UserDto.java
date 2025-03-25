package app.ipreach.backend.users.payload.dto;

import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.EShiftUserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record UserDto(

    Long id,

    String email,
    String password,

    Long botId,
    String botName,
    String botPhone,

    String name,
    String phone,
    String phone2,
    String congregation,

    List<ERole> roles,

    Boolean approved,

    EShiftUserRole shiftUserRole,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime tokenExpires,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime refreshExpires

){ }
