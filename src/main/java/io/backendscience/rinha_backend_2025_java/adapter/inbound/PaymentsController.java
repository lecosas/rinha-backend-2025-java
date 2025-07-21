package io.backendscience.rinha_backend_2025_java.adapter.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.backendscience.rinha_backend_2025_java.application.GetPaymentSummaryUC;
import io.backendscience.rinha_backend_2025_java.application.HealthCheckEngine;
import io.backendscience.rinha_backend_2025_java.application.PaymentWorker;
import io.backendscience.rinha_backend_2025_java.application.PurgePaymentsUC;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final GetPaymentSummaryUC getPaymentSummaryUC;
    private final PurgePaymentsUC purgePaymentsUC;
    private final Logger logger = Logger.getLogger(PaymentsController.class.getName());
    private final PaymentWorker paymentWorker;
    private final HealthCheckEngine healthCheckEngine;

    private BigDecimal fixedAmount = BigDecimal.ZERO;

    @PostMapping("/payments")
    public ResponseEntity<String> postPaymentsController(@RequestBody PaymentBody paymentBody) {
        long startTime = System.nanoTime();

        fixedAmount = paymentBody.amount;

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

        logger.info("FIM Controller POST_PAYMENT:  " + paymentBody );
        logger.info(String.format("FIM Controller POST_PAYMENT: takes %.3f", (System.nanoTime() - startTime) / 1_000_000.0));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentsSummary> getPaymentsSummary(
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {

        long startTime = System.nanoTime();

        logger.severe(String.format("QUEUE SIZE v2: %s", paymentWorker.workerQueue.size()));
        logger.info(String.format("Controller PAYMENT_summary: from %s to %s", from, to));

        Map<PaymentProcessorType, PaymentSummaryTotal> map = getPaymentSummaryUC.execute(from, to, fixedAmount);

        PaymentSummaryTotal countDefault = map.get(PaymentProcessorType.DEFAULT);
        PaymentSummaryTotal countFallback = map.get(PaymentProcessorType.FALLBACK);

        PaymentsSummary res = new PaymentsSummary(
                new PaymentsSummaryDetail(countDefault.requests(), countDefault.amount()),
                new PaymentsSummaryDetail(countFallback.requests(), countFallback.amount()));

        logger.info(
                String.format("Controller PAYMENT_SUMMARY: takes %.3f", (System.nanoTime() - startTime) / 1_000_000.0));

        logger.info(String.format("RETORNO summary  %s", res));

        return ResponseEntity.ok(res);
    }

    @PostMapping("/purge-payments")
    public ResponseEntity<String> postPurgePaymentsController() {
        purgePaymentsUC.execute();
        return ResponseEntity.ok().build();
    }

    public record PaymentBody(String correlationId, BigDecimal amount) {}

    public record PaymentsSummary(
            @JsonProperty("default") PaymentsSummaryDetail defaultResponse, PaymentsSummaryDetail fallback) {}

    public record PaymentsSummaryDetail(long totalRequests, BigDecimal totalAmount) {}
}
