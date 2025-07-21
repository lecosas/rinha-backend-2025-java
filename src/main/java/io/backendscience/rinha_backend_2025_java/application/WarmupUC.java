package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WarmupUC implements CommandLineRunner {

    private final PaymentProcessorGateway paymentPort;
    private final Logger logger = Logger.getLogger(WarmupUC.class.getName());
    private final PurgePaymentsUC purgePaymentsUC;
    private final HealthCheckEngine healthCheckEngine;

    @Override
    public void run(String... args) throws Exception {
        paymentProcessorWarmup();
        purgePaymentsUC.execute();
        healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.DEFAULT);
    }

    private void paymentProcessorWarmup() {
        logger.info("Starting PaymentProcessor warmup");

        PaymentDetail paymentDetail =
                new PaymentDetail("INVALID", BigDecimal.ZERO);

        try {
            paymentPort.savePaymentDefault(paymentDetail, OffsetDateTime.now());
        } catch (Exception ex) {

        }

        logger.info("Finishing PaymentProcessor warmup");
    }
}
