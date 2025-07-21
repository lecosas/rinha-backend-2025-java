package io.backendscience.rinha_backend_2025_java.adapter.outbound;

import io.backendscience.rinha_backend_2025_java.application.port.outbound.PaymentRepositoryPort;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.Range;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRedisRepository implements PaymentRepositoryPort {

    private final RedisCommands<String, String> redis;

    public long countPaymentDefault(long from, long to) {
        return countPayment(PaymentProcessorType.DEFAULT, from, to);
    }

    public long countPaymentFallback(long from, long to) {
        return countPayment(PaymentProcessorType.FALLBACK, from, to);
    }

    private long countPayment(PaymentProcessorType type, long from, long to) {
        return Optional.ofNullable(redis.zcount(
                type.toString(),
                Range.create(from, to))).orElse(0L);
    }
}
