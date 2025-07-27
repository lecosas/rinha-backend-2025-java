package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.adapter.out.http.PaymentProcessorClient;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProcessorGateway paymentProcessor;
    private final Logger logger = Logger.getLogger(PaymentService.class.getName());
    private final SemaphoreService semaphoreService;
    private final PaymentRepository paymentRepository;

    public void process(PaymentDetail paymentDetail, PaymentProcessorType sendTo) {
        OffsetDateTime requestedAt = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);

        long startTime = System.nanoTime();

        if (sendTo == PaymentProcessorType.DEFAULT) {
            paymentProcessor.sendPaymentToDefault(paymentDetail, requestedAt);
        } else {
            paymentProcessor.sendPaymentToFallback(paymentDetail, requestedAt);
        }

        logger.info(String.format(
                "Time to send to Payment processor: %.3f", (System.nanoTime() - startTime) / 1_000_000.0));

        startTime = System.nanoTime();

        semaphoreService.incrementLocalSavingCounter();

        if (sendTo == PaymentProcessorType.DEFAULT) {
            paymentRepository.savePaymentDefault(requestedAt.toInstant().toEpochMilli(), paymentDetail.correlationId());
        } else {
            paymentRepository.savePaymentFallback(requestedAt.toInstant().toEpochMilli(), paymentDetail.correlationId());
        }

        semaphoreService.decrementLocalSavingCounter();

        logger.info(String.format("Time to save in the Redis: %.3f", (System.nanoTime() - startTime) / 1_000_000.0));
    }

}
