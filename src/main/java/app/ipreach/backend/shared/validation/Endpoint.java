package app.ipreach.backend.shared.validation;

import app.ipreach.backend.core.config.EndpointProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Endpoint {

    @Getter private static String[] matchEndpoints;
    @Getter private static String[] matchSwagger;
    @Getter private static String[] matchTest;
    @Getter private static String[] matchErrors;

    @Value("${spring.mvc.servlet.path}")
    private String mvcServletPath;

    private static String mvcServletPathStatic;

    @PostConstruct
    public void init() {
        Endpoint.mvcServletPathStatic = this.mvcServletPath;
    }

    public static String[] getMatchPrefixed(String[] endpoints) {
        return Arrays.stream(endpoints)
            .map(e -> String.format("%s%s", mvcServletPathStatic, e))
            .toArray(String[]::new);
    }

    private static final Map<String, List<String>> exempted = new HashMap<>();
    private static final Map<String, List<String>> nonToken = new HashMap<>();
    private static final Map<String, List<String>> replaceToken = new HashMap<>();

    private Endpoint(EndpointProperties.Matchers matchers,
                     EndpointProperties.ExemptedProperties exemptedProps,
                     EndpointProperties.NonTokenProperties nonTokenProps,
                     EndpointProperties.ReplaceTokenProperties replaceTokenProps) {

        matchEndpoints = matchers.getEndpoints().toArray(new String[0]);
        matchSwagger = matchers.getSwagger().toArray(new String[0]);
        matchTest = matchers.getTest().toArray(new String[0]);
        matchErrors = matchers.getErrors().toArray(new String[0]);

        exempted.putAll(Map.of(
                "GET", exemptedProps.getGET(),
                "POST", exemptedProps.getPOST(),
                "PUT", exemptedProps.getPUT(),
                "PATCH", exemptedProps.getPATCH(),
                "DELETE", exemptedProps.getDELETE()
        ));

        nonToken.putAll(Map.of(
                "GET", nonTokenProps.getGET(),
                "POST", nonTokenProps.getPOST(),
                "PUT", nonTokenProps.getPUT(),
                "PATCH", nonTokenProps.getPATCH(),
                "DELETE", nonTokenProps.getDELETE()
        ));

        replaceToken.putAll(Map.of(
            "GET", replaceTokenProps.getGET(),
            "POST", replaceTokenProps.getPOST(),
            "PUT", replaceTokenProps.getPUT(),
            "PATCH", replaceTokenProps.getPATCH(),
            "DELETE", replaceTokenProps.getDELETE()
        ));

    }

    public static boolean exemptedEndpoint(String method, String endpoint) {
        return exempted.get(method).contains(endpoint);
    }

    public static boolean notExemptedEndpoint(String method, String endpoint) {
        return !exemptedEndpoint(method, endpoint);
    }

    public static boolean nonTokenEndpoint(String method, String endpoint) {
        return nonToken.get(method).contains(endpoint);
    }

    public static boolean isTokenEndpoint(String method, String endpoint) {
        return !nonTokenEndpoint(method, endpoint);
    }

    public static boolean isReplaceTokenEndpoint(String method, String endpoint) {
        return replaceToken.get(method).contains(endpoint) ||
            replaceToken.get(method).stream().anyMatch(endpoint::matches);
    }

    public static boolean isNotReplaceTokenEndpoint(String method, String endpoint) {
        return !isReplaceTokenEndpoint(method, endpoint);
    }

}
