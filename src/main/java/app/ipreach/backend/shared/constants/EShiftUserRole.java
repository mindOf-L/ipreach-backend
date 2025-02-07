package app.ipreach.backend.shared.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EShiftUserRole {
    OVERSEER("overseer"),
    AUXILIAR("auxiliar"),
    PARTICIPANT("participant"),;

    private final String roleName;

}
