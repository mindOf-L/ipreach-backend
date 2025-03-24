package app.ipreach.backend.core.migration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("migration-h2 | migration-pg")
public class DataSourceConfig {

    // local
    @Value("${spring.datasource.driverClassName}")
    private String localDBDriver;
    @Value("${spring.datasource.url}")
    private String localDBUrl;
    @Value("${spring.datasource.username}")
    private String localDBUsername;
    @Value("${spring.datasource.password}")
    private String localDBPassword;

    @Primary
    @Bean(name = "localDBSource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
            .driverClassName(localDBDriver)
            .url(localDBUrl)
            .username(localDBUsername)
            .password(localDBPassword)
            .build();
    }

    // remote
    @Value("${spring.remote.driverClassName}")
    private String remoteDBDriver;
    @Value("${spring.remote.url}")
    private String remoteDBUrl;
    @Value("${spring.remote.username}")
    private String remoteDBUsername;
    @Value("${spring.remote.password}")
    private String remoteDBPassword;

    @Bean(name = "remoteDBSource")
    public DataSource remoteFromDB() {
        return DataSourceBuilder.create()
            .driverClassName(remoteDBDriver)
            .url(remoteDBUrl)
            .username(remoteDBUsername)
            .password(remoteDBPassword)
            .build();
    }

}
