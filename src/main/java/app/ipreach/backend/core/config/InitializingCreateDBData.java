package app.ipreach.backend.core.config;

import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!pro")
public class InitializingCreateDBData { //implements ApplicationListener<MigrationCompletedEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Value("${DB_NAME:mydb}")
    private String dbName;

    @Value("${LOAD_INITIAL_DATA:false}")
    private boolean loadInitialData;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;


    // Execute if migration profile is active
    @Bean
    @Profile("migration-h2 | migration-pg")
    @DependsOn("migrateDBData")
    public InitializingBean createDBDataAfterMigration() {
        log.info("Starting database initialization (stage 2, after migration)...");
        return () -> createExampleData();
    }

    // Execute directly if migration profile is not active
    @Bean
    @Profile("!migration-h2 & !migration-pg")
    public InitializingBean createDBDataOnAppReady() {
        // Check if we're in a profile where migration doesn't happen
        log.info("Starting database initialization (unique stage)...");
        return () -> createExampleData();

    }

    private void createExampleData() {
        if (!loadInitialData) return;

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
            .build());

        log.info("Complete DB initialization 💿");
    }

}
