package app.ipreach.backend.shared.conversion;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class Convert {

    public static String decodeBase64(String endcodedString) {
        return new String(Base64.getDecoder().decode(endcodedString), StandardCharsets.UTF_8);
    }

    public static String nowToTimeStampString(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH).format(LocalDateTime.now());
    }

    public static LocalDateTime dateToLocalDateTime(Date dateToConvert) {
        return Optional.ofNullable(dateToConvert)
            .map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .orElse(null);
    }

}
