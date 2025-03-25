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
            LOGIN_CREDENTIALS_WRONG = "Username/password doesn't match",

            USER_NOT_FOUND = "User not found",
            USER_PARAMETERS_ERROR = "Error in user parameters",
            USER_PASSWORD_DONT_MATCH = "User+password doesn't match",
            USER_NOT_ENABLED = "User not enabled",

            TOKEN_INVALID = "Token invalid",
            THIS_TOKEN_INVALID = "Token invalid: %s",
            TOKEN_NOT_FOUND = "Token not found",
            TOKEN_NOT_PARSEABLE = "Token not parseable",

            ERROR_TOKEN_NOT_PROVIDED = "Please provide auth token",

            LOCATION_NOT_FOUND = "Location not found",
            SHIFT_NOT_FOUND = "Shift not found",

            ERROR_CALLING_API = "Error calling API",

            ERROR_PROCESSING_DATA = "Error processing data",

            RUNTIME_EXCEPTION = "Runtime exception"

            ;
    }

    public static class ErrorDev {

        public static final String
            HASH_ALGORITHM_ERROR = "Hash algorithm error",

            SHIFT_LIST_NULL_OR_EMPTY = "Shift list is null or empty",

            GENERIC_ERROR = "Something went wrong. Please try again later, or contact support."

            ;
    }

    public static class Params {
        public static final String

            GOOGLE_CALENDAR_TEMPLATE = "https://www.google.com/calendar/event?action=TEMPLATE&text=%s&dates=%s/%s&location=%s&details=%s",
            OUTLOOK_CALENDAR_TEMPLATE = "https://outlook.live.com/calendar/0/action/compose?allday=false&subject=%s&startdt=%s&enddt=%s&location=%s&body=%s&path=/calendar/action/compose&rru=addevent",
            APPLE_CALENDAR_TEMPLATE = "data:text/calendar;charset=utf8,BEGIN:VCALENDAR%nVERSION:2.0%nSUMMARY:%s%nBEGIN:VEVENT%nDTSTART:%s%nDTEND:%s%nDESCRIPTION:%s%nLOCATION:%s%nEND:VEVENT%nEND:VCALENDAR";
    }
}

