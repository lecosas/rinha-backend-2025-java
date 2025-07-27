package io.backendscience.rinha_backend_2025_java.domain;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.math.BigDecimal;

@RegisterReflectionForBinding(PaymentDetail.class)
public record PaymentDetail(String correlationId, BigDecimal amount) {}
