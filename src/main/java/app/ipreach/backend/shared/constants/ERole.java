package app.ipreach.backend.shared.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERole {
    DISABLED("DISABLED"),
    ROLE_MOD("MOD"),
    ROLE_USER("USER"),
    ROLE_MANAGER("MANAGER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

}
