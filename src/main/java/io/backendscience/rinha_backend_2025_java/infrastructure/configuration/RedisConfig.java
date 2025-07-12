package io.backendscience.rinha_backend_2025_java.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

    @Value("${redis.url}")
    public String redisURL;

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(redisURL);
//        return new JedisPooled("localhost", 6379);
    }
}
