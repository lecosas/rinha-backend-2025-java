package io.backendscience.rinha_backend_2025_java.application.port.in;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;

public interface EnqueuePaymentUseCase {

    void execute(PaymentDetail paymentDetail);
}
