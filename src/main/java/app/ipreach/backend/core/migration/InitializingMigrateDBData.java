package app.ipreach.backend.core.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("migration-h2 | migration-pg")
public class InitializingMigrateDBData {

    private final JdbcTemplate remoteJdbcTemplate;
    private final JdbcTemplate localJdbcTemplate;
    private final ShutdownService shutdownService;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${DB_NAME:testdb}")
    private String dbName;

    @Value("${SHUTDOWN_AFTER_MIGRATE:true}")
    private boolean shutdownAfterMigrate;

    public InitializingMigrateDBData(
        @Qualifier("localDBSource") DataSource localDataSource,
        @Qualifier("remoteDBSource") DataSource remoteDataSource,
        ShutdownService shutdownService) {
        this.localJdbcTemplate = new JdbcTemplate(localDataSource);
        this.remoteJdbcTemplate = new JdbcTemplate(remoteDataSource);
        this.shutdownService = shutdownService;
    }

    @Bean
    InitializingBean migrateDBData() {
        return () -> {
            //cloneSchema(); // testing create mode in dev
            copyData();
            log.info("Finished cloning DB from remote to local");
            if(shutdownAfterMigrate)
                shutdownService.shutdown();
        };
    }

    private void cloneSchema() {
        localJdbcTemplate.execute("DROP SCHEMA public CASCADE");
        localJdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS public");

        List<Map<String, Object>> tables = remoteJdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");

        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            List<Map<String, Object>> columns = remoteJdbcTemplate.queryForList(
                "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?", tableName);

            StringBuilder createTableQuery = new StringBuilder("CREATE TABLE \"" + tableName + "\" (");
            for (Map<String, Object> column : columns) {
                createTableQuery.append(column.get("column_name"))
                    .append(" ")
                    .append(column.get("data_type"))
                    .append(", ");
            }
            createTableQuery.setLength(createTableQuery.length() - 2); // Remove last comma
            createTableQuery.append(");");

            localJdbcTemplate.execute(createTableQuery.toString());
            log.info("Created table schema in local: {}", tableName);
        }
        log.info("Finished cloning schema");

    }

    private void copyData() {
        log.info("Initializing database...💿");

        List<Map<String, Object>> tables = remoteJdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");

        if(driverClassName.equals("org.postgresql.Driver")) {
            // execute when changing database version
            localJdbcTemplate.execute("ALTER DATABASE \"%s\" REFRESH COLLATION VERSION;".formatted(dbName));
            // set accent-insensitive on searches
            localJdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");

            truncateDataPg();
            insertDataInPg(tables);

        } else if (driverClassName.equals("org.h2.Driver")) {
            truncateDataH2();
            insertDataInH2(tables);
        }

        log.info("Finished copied data");
    }

    private void truncateDataPg() {
        var tables = localJdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");

        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            localJdbcTemplate.execute("TRUNCATE TABLE \"" + tableName + "\" CASCADE");
        }
    }

    private void truncateDataH2() {
        localJdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        var tables = localJdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC'");

        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            localJdbcTemplate.execute("TRUNCATE TABLE \"" + tableName + "\"");
        }

        localJdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void insertDataInH2(List<Map<String, Object>> tables) {
        localJdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            List<Map<String, Object>> rows = remoteJdbcTemplate.queryForList("SELECT * FROM \"" + tableName + "\"");

            String localTable = (String) localJdbcTemplate
                .queryForMap("SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC' and table_name ILIKE '%s' ".formatted(tableName))
                .get("table_name");

            insertDataIntoRows(rows, localTable);

            localJdbcTemplate.execute(
                String.format("ALTER TABLE \"%s\" ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM \"%s\")", localTable, localTable));

            System.out.println("Data copied on local db table: " + tableName);
        }

        localJdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void insertDataInPg(List<Map<String, Object>> tables) {
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            localJdbcTemplate.execute(String.format("ALTER TABLE \"%s\" DISABLE TRIGGER ALL;", tableName));

            List<Map<String, Object>> rows = remoteJdbcTemplate.queryForList("SELECT * FROM \"" + tableName + "\"");

            String localTable = (String) localJdbcTemplate
                .queryForMap("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' and table_name ILIKE '%s' ".formatted(tableName))
                .get("table_name");

            insertDataIntoRows(rows, localTable);

            localJdbcTemplate.execute("""
                DO $$
                DECLARE max_id INT;
                BEGIN
                    SELECT COALESCE(MAX(id), 0) + 1 INTO max_id FROM "%s";
                    PERFORM setval(pg_get_serial_sequence('"%s"', 'id'), max_id, false);
                END $$;
                """.formatted(localTable, localTable));

            localJdbcTemplate.execute(String.format("ALTER TABLE \"%s\" ENABLE TRIGGER ALL;", tableName));

            System.out.println("Data copied on local db table: " + tableName);
        }

    }

    private void insertDataIntoRows(List<Map<String, Object>> rows, String localTable) {
        for (Map<String, Object> row : rows) {
            StringBuilder insertQuery = new StringBuilder("INSERT INTO \"" + localTable + "\" (");
            StringBuilder valuesPlaceholder = new StringBuilder(" VALUES (");
            Object[] values = new Object[row.size()];
            int i = 0;

            for (Map.Entry<String, Object> entry : row.entrySet()) {
                insertQuery.append(entry.getKey()).append(", ");
                valuesPlaceholder.append("?, ");
                values[i++] = entry.getValue();
            }

            insertQuery.setLength(insertQuery.length() - 2);
            valuesPlaceholder.setLength(valuesPlaceholder.length() - 2);
            insertQuery.append(")");
            valuesPlaceholder.append(")");

            localJdbcTemplate.update(insertQuery.toString() + valuesPlaceholder, values);
        }
    }


}
