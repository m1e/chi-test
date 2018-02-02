package com.chisw.microservices.nodes.config;


import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;

import static de.flapdoodle.embed.process.runtime.Network.getFreeServerPort;

/**
 * This configuration class starts postgresql server v9.6.6 for tests
 * and configures corresponding data source
 */
@Order(1)
@Configuration
public class PostgresSQLTestDbConfiguration {

    final private PostgresConfig config;

    public PostgresSQLTestDbConfiguration() throws IOException {

        config = new PostgresConfig(
                Version.V9_6_6,
                new AbstractPostgresConfig.Net("localhost", getFreeServerPort()),
                new AbstractPostgresConfig.Storage("test"),
                new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("user", "password")
        );
    }

    @Bean(destroyMethod = "stop", name = "postgresProcess")
    PostgresProcess postgresProcess() throws IOException {

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        PostgresExecutable exec = runtime.prepare(config);
        return exec.start();
    }

    @DependsOn("postgresProcess")
    @Primary
    @Bean
    DataSource dataSource() {

        PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setUser(config.credentials().username());
        ds.setPassword(config.credentials().password());
        ds.setPortNumber(config.net().port());
        ds.setServerName(config.net().host());
        ds.setDatabaseName(config.storage().dbName());
        return ds;
    }
}