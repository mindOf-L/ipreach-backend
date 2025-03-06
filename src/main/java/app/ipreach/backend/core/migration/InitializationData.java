package app.ipreach.backend.core.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("migration")
public class InitializationData {

    private final JdbcTemplate remoteJdbcTemplate;
    private final JdbcTemplate localJdbcTemplate;
    private final ShutdownService shutdownService;

    public InitializationData(
        @Qualifier("localDBSource") DataSource localDataSource,
        @Qualifier("remoteDBSource") DataSource remoteDataSource,
        ShutdownService shutdownService) {
        this.localJdbcTemplate = new JdbcTemplate(localDataSource);
        this.remoteJdbcTemplate = new JdbcTemplate(remoteDataSource);
        this.shutdownService = shutdownService;
    }

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            //cloneSchema(); // testing create mode in dev
            copyData();
            log.info("Finished cloning DB from remote to local");
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
        localJdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");
        List<Map<String, Object>> tables = remoteJdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");

        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            localJdbcTemplate.execute("TRUNCATE TABLE \"" + tableName + "\" CASCADE");
            List<Map<String, Object>> rows = remoteJdbcTemplate.queryForList("SELECT * FROM \"" + tableName + "\"");

            for (Map<String, Object> row : rows) {
                StringBuilder insertQuery = new StringBuilder("INSERT INTO \"" + tableName + "\" (");
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

            System.out.println("Data copied on local db table: " + tableName);
        }
        log.info("Finished copied data");
    }


}
