package io.backendscience.rinha_backend_2025_java.adapter.in.rest;

import io.backendscience.rinha_backend_2025_java.application.port.in.GetPaymentSummaryUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.in.EnqueuePaymentUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.in.PurgePaymentsUseCase;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final Logger logger = Logger.getLogger(PaymentsController.class.getName());

    private final GetPaymentSummaryUseCase getPaymentSummaryUC;
    private final PurgePaymentsUseCase purgePaymentsUC;
    private final EnqueuePaymentUseCase enqueuePaymentUC;

    @PostMapping("/payments")
    public void postPaymentsController(@RequestBody PaymentBody paymentBody) {
        logger.info("START: Controller postPaymentsController.");

        long startTime = System.nanoTime();

        PaymentDetail paymentDetail = new PaymentDetail(paymentBody.correlationId, paymentBody.amount);

        enqueuePaymentUC.execute(paymentDetail);

        logger.info(String.format(
                "END: Controller postPaymentsController in %.3fms", (System.nanoTime() - startTime) / 1_000_000.0));
    }

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentSummary> getPaymentsSummary(
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {

        logger.info(String.format("START: Controller getPaymentSummary. (from %s to %s)", from, to));

        long startTime = System.nanoTime();

        PaymentSummary summary = getPaymentSummaryUC.execute(from, to);

        logger.info(String.format(
                "END: Controller getPaymentSummary. (response: %s in %.3fms)",
                summary, (System.nanoTime() - startTime) / 1_000_000.0));

        return ResponseEntity.ok(summary);
    }

    @PostMapping("/purge-payments")
    public void postPurgePaymentsController() {
        logger.info("START: Controller postPurgePaymentsController.");

        long startTime = System.nanoTime();

        purgePaymentsUC.execute();

        logger.info(String.format(
                "END: Controller postPurgePaymentsController in %.3fms", (System.nanoTime() - startTime) / 1_000_000.0));
    }

    public record PaymentBody(String correlationId, BigDecimal amount) {}
}
