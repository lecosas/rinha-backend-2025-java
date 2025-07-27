package io.backendscience.rinha_backend_2025_java.adapter.out.redis;

import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.Range;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisPaymentRepository implements PaymentRepository {

    private final RedisCommands<String, String> redis;

    @Override
    public long countPaymentDefault(long from, long to) {
        return countPayment(PaymentProcessorType.DEFAULT, from, to);
    }

    @Override
    public long countPaymentFallback(long from, long to) {
        return countPayment(PaymentProcessorType.FALLBACK, from, to);
    }

    @Override
    public void purgePayments() {
        redis.del(PaymentProcessorType.DEFAULT.toString(), PaymentProcessorType.FALLBACK.toString());
    }

    @Override
    public void savePaymentDefault(long timestamp, String correlationId) {
        redis.zadd(PaymentProcessorType.DEFAULT.toString(), timestamp, correlationId);
    }

    @Override
    public void savePaymentFallback(long timestamp, String correlationId) {
        redis.zadd(PaymentProcessorType.FALLBACK.toString(), timestamp, correlationId);
    }

    private long countPayment(PaymentProcessorType type, long from, long to) {
        return Optional.ofNullable(redis.zcount(
                type.toString(),
                Range.create(from, to))).orElse(0L);
    }
}
