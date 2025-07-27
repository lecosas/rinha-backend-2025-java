package io.backendscience.rinha_backend_2025_java.application.port.out;

public interface PaymentRepository {

    long countPaymentDefault(long from, long to);
    long countPaymentFallback(long from, long to);
    void purgePayments();
    void savePaymentDefault(long timestamp, String correlationId);
    void savePaymentFallback(long timestamp, String correlationId);
}
