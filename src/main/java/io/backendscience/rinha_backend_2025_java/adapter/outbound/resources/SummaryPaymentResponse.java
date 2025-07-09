package io.backendscience.rinha_backend_2025_java.adapter.outbound.resources;

import java.math.BigDecimal;

public record SummaryPaymentResponse(Integer type, BigDecimal sum, Long count) {}
