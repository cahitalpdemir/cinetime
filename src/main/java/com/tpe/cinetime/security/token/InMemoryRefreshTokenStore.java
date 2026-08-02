package com.tpe.cinetime.security.token;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@ConditionalOnProperty(name = "app.refresh-token.store", havingValue = "memory")
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private final ConcurrentMap<Long, StoredToken> tokens = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, String refreshToken, Duration ttl) {
        tokens.put(userId, new StoredToken(refreshToken, Instant.now().plus(ttl)));
    }

    @Override
    public Optional<String> find(Long userId) {
        StoredToken storedToken = tokens.get(userId);
        if (storedToken == null || storedToken.expiresAt.isBefore(Instant.now())) {
            tokens.remove(userId);
            return Optional.empty();
        }
        return Optional.of(storedToken.value);
    }

    @Override
    public void delete(Long userId) {
        tokens.remove(userId);
    }

    private static final class StoredToken {
        private final String value;
        private final Instant expiresAt;

        private StoredToken(String value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }
}
