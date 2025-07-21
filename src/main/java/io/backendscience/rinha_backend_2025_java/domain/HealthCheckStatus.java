package io.backendscience.rinha_backend_2025_java.domain;

public record HealthCheckStatus(boolean failing, int minResponseTime) {}
