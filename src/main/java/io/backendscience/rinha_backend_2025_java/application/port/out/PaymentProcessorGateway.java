package io.backendscience.rinha_backend_2025_java.application.port.out;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;

import java.time.OffsetDateTime;

public interface PaymentProcessorGateway {

    void sendPaymentToDefault(PaymentDetail paymentDetail, OffsetDateTime requestedAt);
    void sendPaymentToFallback(PaymentDetail paymentDetail, OffsetDateTime requestedAt);
}
