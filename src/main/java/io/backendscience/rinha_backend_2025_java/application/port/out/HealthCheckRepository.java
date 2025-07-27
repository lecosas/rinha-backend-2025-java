package io.backendscience.rinha_backend_2025_java.application.port.out;

import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;

public interface HealthCheckRepository {

    PaymentProcessorType getHeathCheckStatus();
    void setHeathCheckStatus(PaymentProcessorType type);
}
