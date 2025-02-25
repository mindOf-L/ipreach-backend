package app.ipreach.backend.core.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${application.version}")
    private String backendVersion;

    @Bean
    public OpenAPI ipreachBackendAPI() {
        return new OpenAPI()
            .info(new Info().title("iPreach API")
                .description("iPreach Backend")
                .version(backendVersion)
                .license(new License().name("Apache 2.0").url("https://ipreach.app")))
            .externalDocs(new ExternalDocumentation()
                .description("iPreach Wiki")
                .url("https://wiki.ipreach.app"));
    }

}
