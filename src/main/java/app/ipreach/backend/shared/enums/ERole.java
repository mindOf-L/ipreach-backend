package app.ipreach.backend.shared.enums;

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

    public static ERole getEnumFromRoleName(String roleName) {
        for(ERole role : ERole.values())
            if(role.getRoleName().equals(roleName))
                return role;

        return null;
    }

    public static ERole getEnumFromAuthority(String authority) {
        for(ERole role : ERole.values())
            if(role.name().equalsIgnoreCase(authority))
                return role;

        return null;
    }

}
