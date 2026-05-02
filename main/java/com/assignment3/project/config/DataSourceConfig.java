package com.assignment3.project.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        DbConnectionSettings settings = resolveDbSettings();

        config.setJdbcUrl(settings.jdbcUrl());
        config.setUsername(settings.username());
        config.setPassword(settings.password());
        String explicitDriver = trimToNull(env.getProperty("SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
        if (explicitDriver != null) {
            config.setDriverClassName(explicitDriver);
        } else if (settings.jdbcUrl().startsWith("jdbc:h2:")) {
            config.setDriverClassName("org.h2.Driver");
        } else {
            config.setDriverClassName("org.postgresql.Driver");
        }

        // Параметры пула с безопасными дефолтами
        config.setMaximumPoolSize(getInt("SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE", 15));
        config.setMinimumIdle(getInt("SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE", 5));
        config.setConnectionTimeout(getLong("SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT", 20000L));
        config.setIdleTimeout(getLong("SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT", 600000L));
        config.setMaxLifetime(getLong("SPRING_DATASOURCE_HIKARI_MAX_LIFETIME", 1800000L));

        return new HikariDataSource(config);
    }

    private DbConnectionSettings resolveDbSettings() {
        String springUrl = trimToNull(env.getProperty("SPRING_DATASOURCE_URL"));
        String springUsername = trimToNull(env.getProperty("SPRING_DATASOURCE_USERNAME"));
        String springPassword = trimToNull(env.getProperty("SPRING_DATASOURCE_PASSWORD"));

        if (springUrl != null) {
            return new DbConnectionSettings(
                    normalizeToJdbcUrl(springUrl),
                    firstNonBlank(springUsername, "postgres"),
                    firstNonBlank(springPassword, "postgres")
            );
        }

        // Render/Supabase часто дают DATABASE_URL в формате postgresql://...
        String databaseUrl = trimToNull(env.getProperty("DATABASE_URL"));
        String dbUsername = trimToNull(env.getProperty("DB_USERNAME"));
        String dbPassword = trimToNull(env.getProperty("DB_PASSWORD"));

        if (databaseUrl != null) {
            ParsedDatabaseUrl parsed = parseDatabaseUrl(databaseUrl);
            return new DbConnectionSettings(
                    parsed.jdbcUrl(),
                    firstNonBlank(dbUsername, parsed.username(), "postgres"),
                    firstNonBlank(dbPassword, parsed.password(), "postgres")
            );
        }

        // Локальный fallback без внешней БД:
        // file-based H2 (быстро для разработки/демо)
        return new DbConnectionSettings(
                "jdbc:h2:file:./data/azhar;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                "sa",
                ""
        );
    }

    private ParsedDatabaseUrl parseDatabaseUrl(String url) {
        String normalized = url;
        if (url.startsWith("postgres://")) {
            normalized = "postgresql://" + url.substring("postgres://".length());
        }

        URI uri = URI.create(normalized);

        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = (uri.getPath() == null || uri.getPath().isBlank()) ? "/postgres" : uri.getPath();
        String query = uri.getQuery();

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + (query == null ? "" : "?" + query);

        String username = null;
        String password = null;
        if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
            String[] parts = uri.getUserInfo().split(":", 2);
            username = parts.length > 0 ? trimToNull(parts[0]) : null;
            password = parts.length > 1 ? trimToNull(parts[1]) : null;
        }

        return new ParsedDatabaseUrl(jdbcUrl, username, password);
    }

    private String normalizeToJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return url;
        }
        return parseDatabaseUrl(url).jdbcUrl();
    }

    private int getInt(String key, int fallback) {
        String value = trimToNull(env.getProperty(key));
        if (value == null) return fallback;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private long getLong(String key, long fallback) {
        String value = trimToNull(env.getProperty(key));
        if (value == null) return fallback;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private record DbConnectionSettings(String jdbcUrl, String username, String password) {
    }

    private record ParsedDatabaseUrl(String jdbcUrl, String username, String password) {
    }
}
