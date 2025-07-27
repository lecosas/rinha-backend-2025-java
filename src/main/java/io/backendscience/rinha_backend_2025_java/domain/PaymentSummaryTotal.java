package io.backendscience.rinha_backend_2025_java.domain;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.math.BigDecimal;

@RegisterReflectionForBinding(PaymentSummaryTotal.class)
public record PaymentSummaryTotal(long requests, BigDecimal amount) {}
