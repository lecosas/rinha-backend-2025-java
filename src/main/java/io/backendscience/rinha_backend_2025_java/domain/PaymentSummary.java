package io.backendscience.rinha_backend_2025_java.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.math.BigDecimal;

@RegisterReflectionForBinding(PaymentSummary.class)
public record PaymentSummary(
        @JsonProperty("default") PaymentSummaryDetail defaultResponse,
        @JsonProperty("fallback") PaymentSummaryDetail fallback) {

    @RegisterReflectionForBinding(PaymentSummaryDetail.class)
    public record PaymentSummaryDetail(
            @JsonProperty("totalRequests") long totalRequests, @JsonProperty("totalAmount") BigDecimal totalAmount) {}
}
