package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.GetPaymentSummaryUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummary;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class PaymentSummaryService implements GetPaymentSummaryUseCase {

    private final Logger logger = Logger.getLogger(PaymentSummaryService.class.getName());
    private final PaymentRepository paymentRepository;
    private final RedisCommands<String, String> redis;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private BigDecimal fixedAmount = BigDecimal.ZERO;

    public PaymentSummary execute(OffsetDateTime from, OffsetDateTime to) {
        long countDefault;
        long countFallback;

        long fromTimestamp = from != null ? from.toInstant().toEpochMilli() : 0L;
        long toTimestamp = to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE;

        logger.info("GET_SUMMARY: locking worker");

        redis.set("worker:pause", "true");

        //        try {
        //            Thread.sleep(20);
        //        } catch (InterruptedException e) {
        //            throw new RuntimeException(e);
        //        }

        String PROCESSING_COUNTER_KEY = "payment-processing:counter";
        String counterStr = "";

        int i = 1;

        // Wait until counter is zero
        while (i <= 100) {
            i++;

            counterStr = redis.get(PROCESSING_COUNTER_KEY);
            long count = counterStr != null ? Long.parseLong(counterStr) : 0;

            logger.info(String.format("GET_SUMMARY: waiting (queue size: %s)", counterStr));

            if (count == 0) {
                break; // Safe to proceed with getSummary
            }

            // Optional: add timeout or logging here
            try {
                Thread.sleep(10); // small backoff
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (i == 10) {
            logger.severe(String.format("GET_SUMMARY: the queue is not empty. (queue size: %s)", counterStr));
        } else {
            logger.info("GET_SUMMARY: queue empty.");
        }

        //        try {
        //            Thread.sleep(100);
        //        } catch (InterruptedException e) {
        //            throw new RuntimeException(e);
        //        }

        CompletableFuture<Long> taskCountDefault = CompletableFuture.supplyAsync(
                () -> paymentRepository.countPaymentDefault(fromTimestamp, toTimestamp), executor);

        CompletableFuture<Long> taskCountFallback = CompletableFuture.supplyAsync(
                () -> paymentRepository.countPaymentFallback(fromTimestamp, toTimestamp), executor);

        try {
            countDefault = taskCountDefault.get();
            countFallback = taskCountFallback.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        redis.set("worker:pause", "false");

        logger.info("GET_SUMMARY: unlocked worker");

        return new PaymentSummary(
                new PaymentSummary.PaymentSummaryDetail(
                        countDefault, BigDecimal.valueOf(countDefault).multiply(fixedAmount)),
                new PaymentSummary.PaymentSummaryDetail(
                        countFallback, BigDecimal.valueOf(countFallback).multiply(fixedAmount)));
    }

    @Override
    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
}
