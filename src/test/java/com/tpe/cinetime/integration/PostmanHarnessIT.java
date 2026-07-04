package com.tpe.cinetime.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Keeps a real HTTP server alive while the external Newman regression runs.
 * The *IT suffix keeps this harness out of the regular unit-test suite.
 */
@ActiveProfiles("postman")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PostmanHarnessIT {

    private static final Duration MAX_RUN_TIME = Duration.ofMinutes(5);

    @DynamicPropertySource
    static void configureApplication(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> env("SERVER_PORT", "8082"));
        registry.add("spring.datasource.url", () -> requiredEnv("DB_URL"));
        registry.add("spring.datasource.username", () -> requiredEnv("DB_USERNAME"));
        registry.add("spring.datasource.password", () -> requiredEnv("DB_PASSWORD"));
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.baseline-version", () -> "1");
        registry.add("logging.file.name", () -> "");
        registry.add("app.admin.email", () -> requiredEnv("ADMIN_EMAIL"));
        registry.add("app.admin.password", () -> requiredEnv("ADMIN_PASSWORD"));
        registry.add("jwt.secret", () -> requiredEnv("JWT_SECRET"));
        registry.add("ticket.qr.secret", () -> requiredEnv("TICKET_QR_SECRET"));
    }

    @Test
    void keepServerAliveForExternalPostmanRegression() throws Exception {
        Path completionMarker = Path.of(requiredEnv("POSTMAN_MARKER_FILE"));
        Instant deadline = Instant.now().plus(MAX_RUN_TIME);

        while (Instant.now().isBefore(deadline) && Files.notExists(completionMarker)) {
            Thread.sleep(250);
        }

        assertTrue(Files.exists(completionMarker),
                "Postman regression did not signal completion within " + MAX_RUN_TIME);
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " environment variable is required");
        }
        return value;
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
