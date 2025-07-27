package io.backendscience.rinha_backend_2025_java.application.port.in;

import io.backendscience.rinha_backend_2025_java.domain.PaymentSummary;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface GetPaymentSummaryUseCase {

    PaymentSummary execute(OffsetDateTime from, OffsetDateTime to);
    void setFixedAmount(BigDecimal fixedAmount);
}
