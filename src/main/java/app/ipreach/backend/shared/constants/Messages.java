package app.ipreach.backend.shared.constants;

public class Messages {

    public static class App {

        public static final String
            LOGIN_STATUS = "login";
    }

    public static class Info {

        public static final String
            USER_LOGGED = "User logged",
            USER_LOGGED_OUT = "User logged out",

            LOCATIONS_LISTED = "Found %s locations",

            SHIFT_CREATED = "Shift created",
            SHIFTS_CREATED = "%s shifts created",
            SHIFTS_LISTED = "Found %s shifts";
    }

    public static class Warning {

        public static final String

            ENDPOINT_NOT_IMPLEMENTED = "This endpoint is not implemented yet";

    }

    public static class ErrorClient {

        public static final String
            USER_NOT_FOUND = "User not found",

            ERROR_PROCESSING_DATA = "Error processing data"

            ;
    }

    public static class ErrorDev {

        public static final String
            SHIFT_LIST_NULL_OR_EMPTY = "Shift list is null or empty"

            ;
    }

    public static class Params {
        public static final String

            GOOGLE_CALENDAR_TEMPLATE = "https://www.google.com/calendar/event?action=TEMPLATE&text=%s&dates=%s/%s&location=%s&details=%s",
            OUTLOOK_CALENDAR_TEMPLATE = "https://outlook.live.com/calendar/0/action/compose?allday=false&subject=%s&startdt=%s&enddt=%s&location=%s&body=%s&path=/calendar/action/compose&rru=addevent",
            APPLE_CALENDAR_TEMPLATE = "data:text/calendar;charset=utf8,BEGIN:VCALENDAR%nVERSION:2.0%nSUMMARY:%s%nBEGIN:VEVENT%nDTSTART:%s%nDTEND:%s%nDESCRIPTION:%s%nLOCATION:%s%nEND:VEVENT%nEND:VCALENDAR";
    }
}

