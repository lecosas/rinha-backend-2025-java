package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.EnqueuePaymentUseCase;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnqueuePaymentService implements EnqueuePaymentUseCase {

    @Value("${payment-backend.main-instance}")
    private Boolean isMainInstance;

    private final PaymentSummaryService paymentSummaryService;
    private final HealthCheckEngine healthCheckEngine;
    private final PaymentWorker paymentWorker;

    @Override
    public void execute(PaymentDetail paymentDetail) {
        paymentSummaryService.setFixedAmount(paymentDetail.amount());

        if (isMainInstance && !healthCheckEngine.isExecuting()) {
            healthCheckEngine.startExecuting();
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!paymentWorker.isWorking()) paymentWorker.work();

        paymentWorker.addToQueue(paymentDetail);
    }
}
