package app.ipreach.backend.shared.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EStatus {

    APPROVED("APPROVE", "approved 👍"),
    DENIED("DENY", "denied 👎"),
    PENDING("PENDING", "pending 🕣");

    private final String actionName;
    private final String statusDesc;

    public static EStatus fromAction(String actionName) {
        for (EStatus status : EStatus.values())
            if (status.actionName.equals(actionName))
                return status;

        return null;
    }

}
