package io.backendscience.rinha_backend_2025_java.adapter.out;

import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthCheckRedisRepository {

    private final RedisCommands<String, String> redis;

    public PaymentProcessorType getHeathCheckStatus() {
        String status = redis.get("health-check:status");
        System.out.println("Getting health-check:status -> " + status);
        if (status.equalsIgnoreCase(PaymentProcessorType.DEFAULT.toString()))
            return PaymentProcessorType.DEFAULT;
        else if (status.equalsIgnoreCase(PaymentProcessorType.FALLBACK.toString()))
            return PaymentProcessorType.FALLBACK;
        else
            return PaymentProcessorType.NONE;
    }

    public void setHeathCheckStatus(PaymentProcessorType type) {
        System.out.println("Setting to health-check:status -> " + type);

        redis.set("health-check:status", type.toString());
    }
}
