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

        logger.info("GET_SUMMARY: locking worker");

        redis.set("worker:pause", "true");

        String PROCESSING_COUNTER_KEY = "payment-processing:counter";
        String counterStr = "";

        int i = 1;

        // Wait until counter is zero
        while (i <= 16) {
            i++;

            counterStr = redis.get(PROCESSING_COUNTER_KEY);
            long count = counterStr != null ? Long.parseLong(counterStr) : 0;

            logger.info(String.format("GET_SUMMARY: waiting (queue size: %s)", counterStr));

            if (count == 0) {
                break; // Safe to proceed with getSummary
            }


            // Optional: add timeout or logging here
            try {
                Thread.sleep(50); // small backoff
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (i == 16) {
            logger.info(String.format("GET_SUMMARY: the queue is not empty. (queue size: %s)", counterStr));
        }

        logger.info("GET_SUMMARY: queue empty.");


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        countDefault = paymentRepository.countPaymentDefault(fromTimestamp, toTimestamp);
        countFallback = paymentRepository.countPaymentFallback(fromTimestamp, toTimestamp);

        redis.set("worker:pause", "false");

        logger.info("GET_SUMMARY: unlocked worker");

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
