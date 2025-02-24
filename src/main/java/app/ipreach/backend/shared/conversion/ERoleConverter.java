package app.ipreach.backend.shared.conversion;

import app.ipreach.backend.shared.enums.ERole;
import jakarta.persistence.Converter;

@Converter
public class ERoleConverter extends EnumListToString<ERole> {
    public ERoleConverter() {
        super(ERole.class);
    }
}
