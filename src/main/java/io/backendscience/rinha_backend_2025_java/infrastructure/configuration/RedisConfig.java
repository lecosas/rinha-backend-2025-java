package io.backendscience.rinha_backend_2025_java.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("localhost", 6379);
    }
}
