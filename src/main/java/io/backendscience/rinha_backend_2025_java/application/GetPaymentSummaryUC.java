package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.application.port.outbound.PaymentRepositoryPort;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class GetPaymentSummaryUC {

    private final Logger logger = Logger.getLogger(GetPaymentSummaryUC.class.getName());
    private final PaymentRepositoryPort paymentRepository;
    private final RedisCommands<String, String> redis;

    public Map<PaymentProcessorType, PaymentSummaryTotal> execute(
            OffsetDateTime from, OffsetDateTime to, BigDecimal fixedAmount) {
        Map<PaymentProcessorType, PaymentSummaryTotal> map = new HashMap<>();

        long countDefault;
        long countFallback;

        long fromTimestamp = from != null ? from.toInstant().toEpochMilli() : 0L;
        long toTimestamp = to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE;

//        redis.set("pause:queue", "true");
//
//        try {
//            Thread.sleep(1_000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        countDefault = paymentRepository.countPaymentDefault(fromTimestamp, toTimestamp);
        countFallback = paymentRepository.countPaymentFallback(fromTimestamp, toTimestamp);

//        redis.set("pause:queue", "false");

        map.put(
                PaymentProcessorType.DEFAULT,
                new PaymentSummaryTotal(
                        countDefault, BigDecimal.valueOf(countDefault).multiply(fixedAmount)));
        map.put(
                PaymentProcessorType.FALLBACK,
                new PaymentSummaryTotal(
                        countFallback, BigDecimal.valueOf(countFallback).multiply(fixedAmount)));

        return map;
    }
}
