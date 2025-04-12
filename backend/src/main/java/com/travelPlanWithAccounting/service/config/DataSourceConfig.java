package com.travelPlanWithAccounting.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        logger.info("Initializing HikariCP with following configuration:");
        logger.info("Driver: {}", driverClassName);
        logger.info("URL: {}", url);
        logger.info("Username: {}", username);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // HikariCP specific configurations
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(30000); // 30 seconds
        config.setMaxLifetime(1200000); // 20 minutes

        // Additional PostgreSQL specific configurations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        // SSL Configuration for Supabase
        config.addDataSourceProperty("sslmode", "require");

        try {
            HikariDataSource dataSource = new HikariDataSource(config);
            logger.info("HikariCP pool initialized successfully");
            return dataSource;
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP pool", e);
            throw e;
        }
    }
}