package com.tpe.cinetime;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class CinetimeApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();

        Map<String, Object> envProperties = new HashMap<>();
        dotenv.entries().forEach(entry -> setProperty(envProperties, entry.getKey(), entry.getValue()));

        normalizeDatabaseUrl(envProperties);

        SpringApplication app = new SpringApplication(CinetimeApplication.class);
        app.setDefaultProperties(envProperties);
        app.run(args);
    }

    private static void normalizeDatabaseUrl(Map<String, Object> envProperties) {
        String databaseUrl = firstText(
                System.getProperty("DB_URL"),
                System.getenv("DB_URL"),
                System.getProperty("DATABASE_URL"),
                System.getenv("DATABASE_URL")
        );

        if (!StringUtils.hasText(databaseUrl)) {
            return;
        }

        PostgresUrlProperties postgresUrlProperties = toJdbcPostgresUrl(databaseUrl);
        setProperty(envProperties, "DB_URL", postgresUrlProperties.jdbcUrl);

        if (StringUtils.hasText(postgresUrlProperties.username) && !hasConfiguredValue("DB_USERNAME")) {
            setProperty(envProperties, "DB_USERNAME", postgresUrlProperties.username);
        }

        if (StringUtils.hasText(postgresUrlProperties.password) && !hasConfiguredValue("DB_PASSWORD")) {
            setProperty(envProperties, "DB_PASSWORD", postgresUrlProperties.password);
        }
    }

    private static PostgresUrlProperties toJdbcPostgresUrl(String databaseUrl) {
        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            return new PostgresUrlProperties(databaseUrl, null, null);
        }

        if (!databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("postgres://")) {
            return new PostgresUrlProperties(databaseUrl, null, null);
        }

        URI uri = URI.create(databaseUrl);
        String host = uri.getHost();
        if (!StringUtils.hasText(host)) {
            String fallbackUrl = databaseUrl.startsWith("postgresql://")
                    ? "jdbc:" + databaseUrl
                    : "jdbc:postgresql://" + databaseUrl.substring("postgres://".length());
            return new PostgresUrlProperties(fallbackUrl, null, null);
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://").append(host);
        if (uri.getPort() > 0) {
            jdbcUrl.append(':').append(uri.getPort());
        }

        if (StringUtils.hasText(uri.getRawPath())) {
            jdbcUrl.append(uri.getRawPath());
        }

        if (StringUtils.hasText(uri.getRawQuery())) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }

        Credentials credentials = extractCredentials(uri.getRawUserInfo());
        return new PostgresUrlProperties(jdbcUrl.toString(), credentials.username, credentials.password);
    }

    private static Credentials extractCredentials(String rawUserInfo) {
        if (!StringUtils.hasText(rawUserInfo)) {
            return new Credentials(null, null);
        }

        String[] parts = rawUserInfo.split(":", 2);
        String username = decode(parts[0]);
        String password = parts.length > 1 ? decode(parts[1]) : null;
        return new Credentials(username, password);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static boolean hasConfiguredValue(String key) {
        return StringUtils.hasText(System.getProperty(key)) || StringUtils.hasText(System.getenv(key));
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static void setProperty(Map<String, Object> envProperties, String key, String value) {
        envProperties.put(key, value);
        System.setProperty(key, value);
    }

    private static final class PostgresUrlProperties {
        private final String jdbcUrl;
        private final String username;
        private final String password;

        private PostgresUrlProperties(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }
    }

    private static final class Credentials {
        private final String username;
        private final String password;

        private Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
