package io.backendscience.rinha_backend_2025_java.adapter.out.redis;

import io.backendscience.rinha_backend_2025_java.application.port.out.HealthCheckRepository;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisHealthCheckRepository implements HealthCheckRepository {

    private final RedisCommands<String, String> redis;
    private final String HEALTH_CHECK_STATUS_KEY = "health-check:status";

    public PaymentProcessorType getHeathCheckStatus() {
        String status = redis.get(HEALTH_CHECK_STATUS_KEY);

        if (status.equalsIgnoreCase(PaymentProcessorType.DEFAULT.toString()))
            return PaymentProcessorType.DEFAULT;
        else if (status.equalsIgnoreCase(PaymentProcessorType.FALLBACK.toString()))
            return PaymentProcessorType.FALLBACK;
        else
            return PaymentProcessorType.NONE;
    }

    public void setHeathCheckStatus(PaymentProcessorType type) {
        redis.set(HEALTH_CHECK_STATUS_KEY, type.toString());
    }
}
