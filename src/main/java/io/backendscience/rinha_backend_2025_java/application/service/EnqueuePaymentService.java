package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.EnqueuePaymentUseCase;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class EnqueuePaymentService implements EnqueuePaymentUseCase {

    @Value("${payment-backend.enqueue.delay}")
    private long exceptionDelay;

    private final Logger logger = Logger.getLogger(EnqueuePaymentService.class.getName());

    private final PaymentSummaryService paymentSummaryService;
    private final PaymentWorker paymentWorker;
    private final HealthCheckEngine healthCheckEngine;
    private final PaymentService paymentService;
    private final SemaphoreService semaphoreService;

    @Override
    public void execute(PaymentDetail paymentDetail) {
        paymentSummaryService.setFixedAmount(paymentDetail.amount());

        paymentWorker.addToQueue(paymentDetail);
    }

    @Override
    public void tryProcessOrEnqueue(PaymentDetail paymentDetail) {
        paymentSummaryService.setFixedAmount(paymentDetail.amount());

        if (!semaphoreService.isWorkerPaused()) {
            PaymentProcessorType paymentType = healthCheckEngine.getHeathCheckStatus();

            if (paymentType == PaymentProcessorType.DEFAULT) {
                try {
                    paymentService.process(paymentDetail, paymentType);
                    return;
                } catch (Exception e) {
                    healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.NONE);
                }
            }
        }

        pauseFor(exceptionDelay);
        paymentWorker.addToQueue(paymentDetail);
    }

    private void pauseFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
