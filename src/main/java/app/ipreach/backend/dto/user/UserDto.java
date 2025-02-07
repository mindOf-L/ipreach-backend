package app.ipreach.backend.dto.user;

import app.ipreach.backend.shared.constants.ERole;
import app.ipreach.backend.shared.constants.EShiftUserRole;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(

    Long id,

    String username,
    String email,

    Long botId,
    String botName,
    String botPhone,

    String name,
    String phone,
    String phone2,
    String congregation,
    Boolean approved,

    List<ERole> userRoles,

    EShiftUserRole shiftUserRole

){ }
