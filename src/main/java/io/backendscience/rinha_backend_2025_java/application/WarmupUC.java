package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WarmupUC implements CommandLineRunner {

    private final PaymentProcessorGateway paymentPort;
    private final Logger logger = Logger.getLogger(WarmupUC.class.getName());
    private final PurgePaymentsUC purgePaymentsUC;
    private final HealthCheckEngine healthCheckEngine;
    private final RedisCommands<String, String> redis;

    @Override
    public void run(String... args) throws Exception {
        String PROCESSING_COUNTER_KEY = "payment-processing:counter";

        paymentProcessorWarmup();
        purgePaymentsUC.execute();
        healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.DEFAULT);
        redis.set("worker:pause", "false");
        redis.set(PROCESSING_COUNTER_KEY, "0");
    }

    private void paymentProcessorWarmup() {
        logger.info("Starting PaymentProcessor warmup");

        PaymentDetail paymentDetail =
                new PaymentDetail("INVALID", BigDecimal.ZERO);

        try {
            paymentPort.savePaymentDefault(paymentDetail, OffsetDateTime.now(ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.MILLIS));
            paymentPort.savePaymentFallback(paymentDetail, OffsetDateTime.now(ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.MILLIS));
        } catch (Exception ex) {

        }

        try {
            paymentPort.savePaymentFallback(paymentDetail, OffsetDateTime.now());
        } catch (Exception ex) {

        }

        logger.info("Finishing PaymentProcessor warmup");
    }
}
