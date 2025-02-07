package app.ipreach.backend.shared.conversion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Converter
@RequiredArgsConstructor
public class EnumListToString<E extends Enum<E>> implements AttributeConverter<List<E>, String> {

    private final Class<E> enumClass;

    private static final String DELIMITER = ",";

    public String convertToDatabaseColumn(List<E> dtoAttribute) {
        if (dtoAttribute == null || dtoAttribute.isEmpty()) {
            return "";
        }
        return dtoAttribute.stream()
            .map(Enum::name)
            .collect(Collectors.joining(DELIMITER));
    }

    public List<E> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(DELIMITER))
            .map(name -> Enum.valueOf(enumClass, name))
            .collect(Collectors.toList());
    }

}
