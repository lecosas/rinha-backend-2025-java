package io.backendscience.rinha_backend_2025_java.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding(HealthCheckStatus.class)
public record  HealthCheckStatus(
        @JsonProperty("failing") boolean failing, @JsonProperty("minResponseTime") int minResponseTime) {}
