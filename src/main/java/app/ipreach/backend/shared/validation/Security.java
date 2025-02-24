package app.ipreach.backend.shared.validation;

import app.ipreach.backend.shared.constants.SecurityProperties;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Security {

    @Getter private static List<String> allowedMethods;
    @Getter private static List<String> allowedHeaders;
    @Getter private static List<String> exposedHeaders;

    private Security(SecurityProperties.Security securityprops) {

        allowedMethods = securityprops.getMethods();
        allowedHeaders = securityprops.getHeaders();
        exposedHeaders = securityprops.getHeaders();

    }

}
