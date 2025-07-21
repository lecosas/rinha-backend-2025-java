package io.backendscience.rinha_backend_2025_java.domain;

import java.math.BigDecimal;

public record PaymentDetail(String correlationId, BigDecimal amount) {}
