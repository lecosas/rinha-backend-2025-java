package io.backendscience.rinha_backend_2025_java.application.port.out;

import io.backendscience.rinha_backend_2025_java.domain.HealthCheckStatus;

public interface HealthCheckGateway {

    HealthCheckStatus getHeathCheckDefault();
    HealthCheckStatus getHeathCheckFallback();
}
