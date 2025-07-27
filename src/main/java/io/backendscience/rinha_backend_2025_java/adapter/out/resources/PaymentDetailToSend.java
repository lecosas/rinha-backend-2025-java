package io.backendscience.rinha_backend_2025_java.adapter.out.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.math.BigDecimal;

@RegisterReflectionForBinding(PaymentDetailToSend.class)
public class PaymentDetailToSend {
    @JsonProperty("correlationId") String correlationId;
    @JsonProperty("amount") BigDecimal amount;
    @JsonProperty("requestedAt") String requestedAt;

    public PaymentDetailToSend(String correlationId, BigDecimal amount, String requestedAt) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = requestedAt;
    }
}
