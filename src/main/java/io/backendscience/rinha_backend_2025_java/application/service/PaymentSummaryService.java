package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.GetPaymentSummaryUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class PaymentSummaryService implements GetPaymentSummaryUseCase {

    @Value("${payment-backend.summary.delay-1}")
    private long delay1;

    @Value("${payment-backend.summary.delay-2}")
    private long delay2;

    @Value("${payment-backend.summary.delay-3}")
    private long delay3;

    @Value("${payment-backend.summary.delay-4}")
    private long delay4;

    private final PaymentRepository paymentRepository;
    private final SemaphoreService semaphoreService;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private BigDecimal fixedAmount = BigDecimal.ZERO;

    public PaymentSummary execute(OffsetDateTime from, OffsetDateTime to) {
        long fromTimestamp = from != null ? from.toInstant().toEpochMilli() : 0L;
        long toTimestamp = to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE;

        semaphoreService.pauseWorker();

        pauseFor(delay1);
        waitToSaveLocalData();
        pauseFor(delay3);

        CompletableFuture<Long> taskCountDefault = CompletableFuture.supplyAsync(
                () -> paymentRepository.countPaymentDefault(fromTimestamp, toTimestamp), executor);
        CompletableFuture<Long> taskCountFallback = CompletableFuture.supplyAsync(
                () -> paymentRepository.countPaymentFallback(fromTimestamp, toTimestamp), executor);

        long countDefault;
        long countFallback;

        try {
            countDefault = taskCountDefault.get();
            countFallback = taskCountFallback.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        pauseFor(delay4);
        semaphoreService.resumeWorker();

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

    private void waitToSaveLocalData() {
        int i = 1;
        while (semaphoreService.isSavingLocalData() && i <= 100) {
            i++;
            pauseFor(delay2);
        }
    }

    private void pauseFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
