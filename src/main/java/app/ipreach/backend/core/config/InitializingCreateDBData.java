package app.ipreach.backend.core.config;

import app.ipreach.backend.locations.db.repository.LocationRepository;
import app.ipreach.backend.locations.payload.mapper.LocationMapper;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.EShiftUserRole;
import app.ipreach.backend.shifts.db.model.ShiftAssignment;
import app.ipreach.backend.shifts.db.repository.ShiftAssigmentRepository;
import app.ipreach.backend.shifts.db.repository.ShiftRepository;
import app.ipreach.backend.shifts.payload.mapper.ShiftMapper;
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

import static app.ipreach.backend.shared.creation.FakeClass.giveMeLocation;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeShift;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!pro")
public class InitializingCreateDBData { //implements ApplicationListener<MigrationCompletedEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftAssigmentRepository shiftAssignmentRepository;

    @Value("${DB_NAME:mydb}")
    private String dbName;

    @Value("${LOAD_INITIAL_DATA:false}")
    private boolean loadInitialData;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;


    // Execute if migration profile is active
    @Bean
    @Profile("migration-h2 | migration-pg | migration-docker")
    @DependsOn("migrateDBData")
    public InitializingBean createDBDataAfterMigration() {
        log.info("Starting database initialization (stage 2, after migration)...");
        return this::createExampleData;
    }

    // Execute directly if migration profile is not active
    @Bean
    @Profile("!migration-h2 & !migration-pg & !migration-docker")
    public InitializingBean createDBDataOnAppReady() {
        // Check if we're in a profile where migration doesn't happen
        log.info("Starting database initialization (unique stage)...");

        if(driverClassName.equals("org.postgresql.Driver")) {
            // execute when changing database version
            jdbcTemplate.execute("ALTER DATABASE \"%s\" REFRESH COLLATION VERSION;".formatted(dbName));
            // set accent-insensitive on searches
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");
        }

        return this::createExampleData;
    }

    private void createExampleData() {
        if (!loadInitialData) return;

        log.info("Initializing database...💿");

        // user
        userRepository.saveAndFlush(User.builder()
            .name("admin")
            .email("admin")
            .password(passwordEncoder.encode("admin"))
            .roles(List.of(ERole.ROLE_ADMIN))
            .approved(true)
            .build());

        // locations
        var location = LocationMapper.MAPPER.toMo(giveMeLocation());
        location.setId(null);
        locationRepository.saveAndFlush(location);

        // shifts
        for (int i = 0; i < 2; i++) {
            var shift = ShiftMapper.MAPPER.toMo(giveMeShift());
            shift.setId(null);
            shift.setLocation(locationRepository.findById(1L).get());
            shiftRepository.saveAndFlush(shift);
        }

        log.warn("⚠️ This requires previously inserted data on DB ⚠️");
        // full of participants
        var fullShift = shiftRepository.findById(1L).orElseThrow(() -> new RuntimeException("Shift not found"));

        var usersFullShift = userRepository.giveMeRandomParticipants(4);

        for (int i = 0; i < usersFullShift.size(); i++) {
            fullShift.getAssignments().add(shiftAssignmentRepository.save(ShiftAssignment.builder()
                .shiftId(fullShift.getId())
                .user(usersFullShift.get(i))
                .shiftUserRole(i == 0 ? EShiftUserRole.OVERSEER :
                               i == 1 ? EShiftUserRole.AUXILIAR :
                                        EShiftUserRole.PARTICIPANT)
                .build()));
        }

        shiftRepository.saveAndFlush(fullShift);
        // partial filled with participants
        var partialShift = shiftRepository.findById(2L).orElseThrow(() -> new RuntimeException("Shift not found"));

        var usersPartialShift = userRepository.giveMeRandomParticipants(2);

        for (int i = 0; i < usersPartialShift.size(); i++) {
            fullShift.getAssignments().add(shiftAssignmentRepository.save(ShiftAssignment.builder()
                .shiftId(fullShift.getId())
                .user(usersPartialShift.get(i))
                .shiftUserRole(i == 0 ? EShiftUserRole.OVERSEER
                                      : EShiftUserRole.PARTICIPANT)
                .build()));
        }
        shiftRepository.saveAndFlush(partialShift);


        log.info("Complete DB initialization 💿");
    }

}
