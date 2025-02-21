package app.ipreach.backend.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

public class SecurityProperties {

    @Data
    @Component
    @ConfigurationProperties("security")
    public static class Security { private List<String> headers, methods, exposed; }

}
