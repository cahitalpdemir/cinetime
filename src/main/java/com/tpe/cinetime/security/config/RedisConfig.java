package com.tpe.cinetime.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "app.refresh-token.store", havingValue = "redis", matchIfMissing = true)
public class RedisConfig {

    @Value("${spring.redis.url:}")
    private String redisUrl;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.ssl:false}")
    private boolean ssl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisSettings settings = resolveRedisSettings();

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(settings.host);
        configuration.setPort(settings.port);

        if (StringUtils.hasText(settings.password)) {
            configuration.setPassword(RedisPassword.of(settings.password));
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientBuilder = LettuceClientConfiguration.builder();
        if (settings.ssl) {
            clientBuilder.useSsl();
        }

        return new LettuceConnectionFactory(configuration, clientBuilder.build());
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    private RedisSettings resolveRedisSettings() {
        if (!StringUtils.hasText(redisUrl)) {
            return new RedisSettings(host, port, password, ssl);
        }

        URI uri = URI.create(redisUrl);
        String scheme = uri.getScheme();
        boolean urlUsesSsl = "rediss".equalsIgnoreCase(scheme);
        int urlPort = uri.getPort() > 0 ? uri.getPort() : 6379;
        String urlPassword = extractPassword(uri.getUserInfo());

        return new RedisSettings(uri.getHost(), urlPort, urlPassword, urlUsesSsl || ssl);
    }

    private String extractPassword(String userInfo) {
        if (!StringUtils.hasText(userInfo)) {
            return "";
        }
        int separatorIndex = userInfo.indexOf(':');
        return separatorIndex >= 0 ? userInfo.substring(separatorIndex + 1) : userInfo;
    }

    private static final class RedisSettings {
        private final String host;
        private final int port;
        private final String password;
        private final boolean ssl;

        private RedisSettings(String host, int port, String password, boolean ssl) {
            this.host = host;
            this.port = port;
            this.password = password;
            this.ssl = ssl;
        }
    }
}
