package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class WarmupService implements CommandLineRunner {

    @Value("${payment-backend.warmup.cycles}")
    private long warmupCycles;

    private final PaymentProcessorGateway paymentProcessor;
    private final Logger logger = Logger.getLogger(WarmupService.class.getName());
    private final PurgePaymentsService purgePaymentsService;
    private final HealthCheckEngine healthCheckEngine;
    private final SemaphoreService semaphoreService;
    private final PaymentWorker paymentWorker;

    @Override
    public void run(String... args) throws Exception {
        logger.severe("START: Warmup");

        long startTime = System.nanoTime();

        purgePaymentsService.execute();

        for (int i = 0; i < warmupCycles; i++) {
            paymentProcessorWarmup();

            healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.DEFAULT);

            logger.info("Setting PaymentWorker to working state.");
            semaphoreService.resumeWorker();
            logger.info("PaymentWorker is set to working.");

            logger.info("Resetting Local Saving Counter.");
            semaphoreService.resetLocalSavingCounter();
            logger.info("Local Saving Counter is reset.");
        }

        healthCheckEngine.startExecution();
        paymentWorker.startExecution();

        pauseFor(100);

        logger.severe(String.format("END: Warmup in %.3fms", (System.nanoTime() - startTime) / 1_000_000.0));
    }

    private void paymentProcessorWarmup() {
        PaymentDetail paymentDetail = new PaymentDetail("INVALID", BigDecimal.ZERO);

        try {
            paymentProcessor.sendPaymentToDefault(
                    paymentDetail, OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        } catch (Exception ex) {

        }

        try {
            paymentProcessor.sendPaymentToFallback(
                    paymentDetail, OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        } catch (Exception ex) {

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
