package app.ipreach.backend.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

public class EndpointProperties {

    @Data
    @Component
    @ConfigurationProperties("matchers")
    public static class Matchers { private List<String> endpoints, swagger, test, errors; }

    @Data
    @Component
    @ConfigurationProperties("exempted")
    public static class ExemptedProperties { private List<String> GET, POST, PUT, PATCH, DELETE; }

    @Data
    @Component
    @ConfigurationProperties("non-token")
    public static class NonTokenProperties { private List<String> GET, POST, PUT, PATCH, DELETE; }

    @Data
    @Component
    @ConfigurationProperties("replace-token")
    public static class ReplaceTokenProperties { private List<String> GET, POST, PUT, PATCH, DELETE; }

}
