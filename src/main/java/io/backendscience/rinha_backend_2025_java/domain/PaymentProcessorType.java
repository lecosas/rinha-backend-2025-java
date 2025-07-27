package io.backendscience.rinha_backend_2025_java.domain;

import lombok.Getter;

@Getter
public enum PaymentProcessorType {
    DEFAULT,
    FALLBACK,
    NONE
}
