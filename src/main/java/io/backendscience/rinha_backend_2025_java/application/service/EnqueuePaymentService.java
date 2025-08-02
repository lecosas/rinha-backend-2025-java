package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.EnqueuePaymentUseCase;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class EnqueuePaymentService implements EnqueuePaymentUseCase {

    @Value("${payment-backend.main-instance}")
    private Boolean isMainInstance;

    private final Logger logger = Logger.getLogger(EnqueuePaymentService.class.getName());

    private final PaymentSummaryService paymentSummaryService;
    private final PaymentWorker paymentWorker;
    private final HealthCheckEngine healthCheckEngine;
    private final PaymentService paymentService;
    private final SemaphoreService semaphoreService;

    @Override
    public void execute(PaymentDetail paymentDetail) {
        paymentSummaryService.setFixedAmount(paymentDetail.amount());

////                if (isMainInstance && !healthCheckEngine.isExecuting()) {
////                    healthCheckEngine.startExecuting();
////                }
//
//        paymentWorker.startExecution();
//        paymentWorker.addToQueue(paymentDetail);

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

//        pauseFor(10);
        paymentWorker.addToQueue(paymentDetail);
        paymentWorker.startExecution();
    }

    private void pauseFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
