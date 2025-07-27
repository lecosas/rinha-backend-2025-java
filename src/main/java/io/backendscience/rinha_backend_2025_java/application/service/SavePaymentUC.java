package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.adapter.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SavePaymentUC {

    private final PaymentProcessorGateway paymentPort;
    private final Logger logger = Logger.getLogger(SavePaymentUC.class.getName());
    private final RedisCommands<String, String> redis;
    private final SemaphoreService semaphoreService;

    public void execute(PaymentDetail paymentDetail, PaymentProcessorType sendTo) {
        OffsetDateTime requestedAt = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        ;

        long startTime = System.nanoTime();

        if (sendTo == PaymentProcessorType.DEFAULT) {
            paymentPort.savePaymentDefault(paymentDetail, requestedAt);
        } else {
            paymentPort.savePaymentFallback(paymentDetail, requestedAt);
        }

        logger.info(String.format(
                "Time to send to Payment processor: %.3f", (System.nanoTime() - startTime) / 1_000_000.0));

        startTime = System.nanoTime();

        semaphoreService.incrementLocalSavingCounter();
        redis.zadd(sendTo.toString(), requestedAt.toInstant().toEpochMilli(), paymentDetail.correlationId());
        semaphoreService.decrementLocalSavingCounter();

        logger.info(String.format("Time to save in the Redis: %.3f", (System.nanoTime() - startTime) / 1_000_000.0));
    }
}
