package app.ipreach.backend.core.config;

import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitializationData {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Value("${DB_NAME:mydb}")
    private String dbName;

    @Value("${LOAD_INITIAL_DATA:false}")
    private boolean loadInitialData;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Bean
    @Profile("!pro")
    InitializingBean sendDatabase() {
        if (!loadInitialData) return null;

        return () -> {
            log.info("Initializing database...💿");
            if(driverClassName.equals("org.postgresql.Driver")) {
                // execute when changing database version
                jdbcTemplate.execute("ALTER DATABASE \"%s\" REFRESH COLLATION VERSION;".formatted(dbName));
                // set accent-insensitive on searches
                jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");
            }

            userRepository.save(User.builder()
                .name("admin")
                .email("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(List.of(ERole.ROLE_ADMIN))
                .approved(true)
                .enabled(true)
                .build());
        };
    }
}
