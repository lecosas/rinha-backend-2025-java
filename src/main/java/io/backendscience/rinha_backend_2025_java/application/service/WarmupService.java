package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
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

    private final PaymentProcessorGateway paymentProcessor;
    private final Logger logger = Logger.getLogger(WarmupService.class.getName());
    private final PurgePaymentsService purgePaymentsService;
    private final HealthCheckEngine healthCheckEngine;
    private final SemaphoreService semaphoreService;

    @Override
    public void run(String... args) throws Exception {
        paymentProcessorWarmup();
        purgePaymentsService.execute();
        healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.DEFAULT);

        logger.info("Setting PaymentWorker to working state.");
        semaphoreService.resumeWorker();
        logger.info("PaymentWorker is set to working.");

        logger.info("Resetting Local Saving Counter.");
        semaphoreService.resetLocalSavingCounter();
        logger.info("Local Saving Counter is reset.");
    }

    private void paymentProcessorWarmup() {
        logger.info("Starting PaymentProcessor warmup");

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

        logger.info("Finishing PaymentProcessor warmup");
    }
}
