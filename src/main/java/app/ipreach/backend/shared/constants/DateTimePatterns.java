package app.ipreach.backend.shared.constants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DateTimePatterns {

    public static final DateTimeFormatter
        // time
        TIME_PATTERN = ofPattern("HH:mm"),
        TIME_PATTERN_SHORT = ofPattern("H:mm"),
        AM_PM_TIME_PATTERN = ofPattern("h:mm a").withLocale(Locale.US),

        // date
        YEAR_MONTH_DASHED = ofPattern("yyyy-MM"),
        DATE_PATTERN_DASHED = ofPattern("yyyy-MM-dd"),
        DATE_PATTERN_SLASHED = ofPattern("yyyy/MM/dd"),
        US_DATE_PATTERN_SHORT = ofPattern("MMM dd").withLocale(Locale.US),
        US_DATE_PATTERN_DAY = ofPattern("MMMM dd, yyyy: EEEE").withLocale(Locale.US),
        US_DATE_PATTERN_LETTER = ofPattern("MMM dd, yyyy").withLocale(Locale.US),
        DATE_LONG_PATTERN = ofPattern("EEEE, MMMM d, yyyy").withLocale(Locale.US),

        // datetime
        DATE_TIME_12H_PATTERN = ofPattern("yyyy/MM/dd h:mm a"),
        DATE_TIME_CALENDAR_PATTERN = ofPattern("yyyyMMdd'T'HHmmss'Z'").withLocale(Locale.US);

    public static final ZoneId MONROVIA = ZoneId.of("Africa/Monrovia");
}
