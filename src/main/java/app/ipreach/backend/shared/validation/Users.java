package app.ipreach.backend.shared.validation;

import app.ipreach.backend.app.exception.custom.RequestException;
import app.ipreach.backend.mapper.dto.user.UserDto;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import static app.ipreach.backend.shared.constants.Messages.ErrorClient.USER_PARAMETERS_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class Users {

    public static void validateUser(UserDto user) {
        try {
            Preconditions.checkArgument(ObjectUtils.allNotNull(user));
            Preconditions.checkArgument(StringUtils.isNotBlank(user.name()));
            Preconditions.checkArgument(StringUtils.isNotBlank(user.email()));
            Preconditions.checkArgument(user.approved());

        } catch (IllegalArgumentException e) {
            Object data = e.getMessage();
            throw new RequestException(BAD_REQUEST, USER_PARAMETERS_ERROR, data);
        }

    }
}
