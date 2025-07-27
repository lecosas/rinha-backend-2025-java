package io.backendscience.rinha_backend_2025_java.adapter.in.rest;

import io.backendscience.rinha_backend_2025_java.application.port.in.GetPaymentSummaryUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.in.PurgePaymentsUseCase;
import io.backendscience.rinha_backend_2025_java.application.service.HealthCheckEngine;
import io.backendscience.rinha_backend_2025_java.application.service.PaymentWorker;
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

    private final GetPaymentSummaryUseCase getPaymentSummaryUC;
    private final PurgePaymentsUseCase purgePaymentsUC;
    private final Logger logger = Logger.getLogger(PaymentsController.class.getName());
    private final PaymentWorker paymentWorker;
    private final HealthCheckEngine healthCheckEngine;

    @PostMapping("/payments")
    public void postPaymentsController(@RequestBody PaymentBody paymentBody) {
        long startTime = System.nanoTime();

        getPaymentSummaryUC.setFixedAmount(paymentBody.amount);

        if (!healthCheckEngine.isExecuting()) {
            healthCheckEngine.startExecuting();
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!paymentWorker.isWorking()) paymentWorker.work();

        PaymentDetail paymentDetail = new PaymentDetail(paymentBody.correlationId, paymentBody.amount);

        paymentWorker.workerQueue.add(paymentDetail);

        logger.info(String.format(
                "FIM Controller POST_PAYMENT: %s takes %.3f",
                paymentBody, (System.nanoTime() - startTime) / 1_000_000.0));
        // return ResponseEntity.accepted().build();
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
    public ResponseEntity<String> postPurgePaymentsController() {
        purgePaymentsUC.execute();
        return ResponseEntity.ok().build();
    }

    public record PaymentBody(String correlationId, BigDecimal amount) {}
}
