package io.backendscience.rinha_backend_2025_java.domain;

import lombok.Getter;

@Getter
public enum PaymentType {
    DEFAULT(0),
    FALLBACK(1);

    private final int value;

    PaymentType(int newValue) {
        value = newValue;
    }
}
