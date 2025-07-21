package io.backendscience.rinha_backend_2025_java.application.port.outbound;

public interface PaymentRepositoryPort {

    long countPaymentDefault(long from, long to);
    long countPaymentFallback(long from, long to);
}
