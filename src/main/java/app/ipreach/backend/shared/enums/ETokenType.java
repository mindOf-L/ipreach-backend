package app.ipreach.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ETokenType implements EnumMethods {

    ANONYMOUS("anonymous"),
    REGULAR("regular"),
    REFRESH("refresh"),
    RECOVERY("recovery"),
    CONFIRM("confirm"),;

    private final String name;

}
