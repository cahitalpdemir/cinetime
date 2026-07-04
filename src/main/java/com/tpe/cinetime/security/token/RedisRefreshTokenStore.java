package com.tpe.cinetime.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Profile("!postman")
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String KEY_PREFIX = "refreshToken:user:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, refreshToken, ttl);
    }

    @Override
    public Optional<String> find(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + userId));
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
