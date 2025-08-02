package io.backendscience.rinha_backend_2025_java.adapter.out.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentProcessorWebClient implements PaymentProcessorGateway {

    private final Logger logger = Logger.getLogger(PaymentProcessorWebClient.class.getName());

    @Value("${payment-processor.default-url}")
    private String paymentProcessorDefaultUrl;

    @Value("${payment-processor.fallback-url}")
    private String paymentProcessorFallbackUrl;

    private final WebClient webClient;

    public void sendPaymentToDefault(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {
        //        String payToSend = new StringBuilder("{")
        //                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
        //                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
        //
        // .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
        //                .append("}")
        //                .toString();

        PaymentDetailToSend payToSend = new PaymentDetailToSend(
                paymentDetail.correlationId(),
                paymentDetail.amount(),
                requestedAt.format(DateTimeFormatter.ISO_INSTANT));

        ResponseEntity<Void> defaultResponse = webClient
                .post()
                .uri(paymentProcessorDefaultUrl + "/payments")
                .bodyValue(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    logger.severe(String.format("Error %s to send DEFAULT payment.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    logger.severe(String.format("Error %s to send DEFAULT payment.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .toBodilessEntity()
                .block();

        if (!defaultResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error sending the payment to the FALLBACK");
        }
    }

    public void sendPaymentToFallback(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {
        //        String payToSend = new StringBuilder("{")
        //                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
        //                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
        //
        // .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
        //                .append("}")
        //                .toString();

        PaymentDetailToSend payToSend = new PaymentDetailToSend(
                paymentDetail.correlationId(),
                paymentDetail.amount(),
                requestedAt.format(DateTimeFormatter.ISO_INSTANT));

        ResponseEntity<Void> fallbackResponse = webClient
                .post()
                .uri(paymentProcessorFallbackUrl + "/payments")
                .bodyValue(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    logger.severe(String.format("Error %s to send FALLBACK payment.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    logger.severe(String.format("Error %s to send FALLBACK payment.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .toBodilessEntity()
                .block();

        if (!fallbackResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error sending the payment to the FALLBACK");
        }
    }

    private record PaymentDetailToSend(
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("requestedAt") String requestedAt) {}
}
