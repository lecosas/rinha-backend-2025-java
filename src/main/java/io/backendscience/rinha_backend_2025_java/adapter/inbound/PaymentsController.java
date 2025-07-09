package io.backendscience.rinha_backend_2025_java.adapter.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.backendscience.rinha_backend_2025_java.application.GetPaymentSummaryUC;
import io.backendscience.rinha_backend_2025_java.application.SavePaymentDefaultUC;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import io.backendscience.rinha_backend_2025_java.domain.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final SavePaymentDefaultUC savePayUC;
    private final GetPaymentSummaryUC getPaySumUC;
    private final Logger logger = Logger.getLogger(PaymentsController.class.getName());

    @PostMapping("/payments")
    public ResponseEntity<String> postPaymentsController(@RequestBody PaymentBody paymentBody) {
        logger.info("Controller POST_PAYMENT: " + paymentBody);

        PaymentDetail paymentDetail =
                new PaymentDetail(paymentBody.correlationId, paymentBody.amount, OffsetDateTime.now(ZoneOffset.UTC));

        savePayUC.execute(paymentDetail);

        return ResponseEntity.ok(paymentDetail.correlationId());
    }

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentsSummary> getPaymentsSummary(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {

        logger.info(String.format("Controller PAYMENT_SUMMARY: from %s to %s", from, to));

        Map<Integer, PaymentSummaryTotal> map = getPaySumUC.execute(from, to);

        PaymentSummaryTotal countDefault =
                map.getOrDefault(PaymentType.DEFAULT.getValue(), new PaymentSummaryTotal(0, BigDecimal.ZERO));
        PaymentSummaryTotal countFallback =
                map.getOrDefault(PaymentType.FALLBACK.getValue(), new PaymentSummaryTotal(0, BigDecimal.ZERO));

        PaymentsSummary res = new PaymentsSummary(
                new PaymentsSummaryDetail(countDefault.requests(), countDefault.amount()),
                new PaymentsSummaryDetail(countFallback.requests(), countFallback.amount()));

        return ResponseEntity.ok(res);
    }

    public record PaymentBody(String correlationId, BigDecimal amount) {}

    public record PaymentsSummary(
            @JsonProperty("default") PaymentsSummaryDetail defaultResponse, PaymentsSummaryDetail fallback) {}

    public record PaymentsSummaryDetail(long totalRequests, BigDecimal totalAmount) {}
}
