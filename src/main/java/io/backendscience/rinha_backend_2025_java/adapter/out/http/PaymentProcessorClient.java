package io.backendscience.rinha_backend_2025_java.adapter.out.http;

import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class PaymentProcessorClient implements PaymentProcessorGateway {

    private final Logger logger = Logger.getLogger(PaymentProcessorClient.class.getName());

    @Value("${payment-processor.default-url}")
    private String paymentProcessorDefaultUrl;

    @Value("${payment-processor.fallback-url}")
    private String paymentProcessorFallbackUrl;

    private final RestClient restClient;

    public void sendPaymentToDefault(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {
        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> defaultResponse = restClient
                .post()
                .uri(paymentProcessorDefaultUrl + "/payments")
                .body(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.warning(String.format("Error %s to send DEFAULT payment.", res.getStatusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logger.warning(String.format("Error %s to send DEFAULT payment.", res.getStatusCode()));
                })
                .toBodilessEntity();

        if (!defaultResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error sending the payment to the DEFAULT");
        }
    }

    public void sendPaymentToFallback(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {
        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> fallbackResponse = restClient
                .post()
                .uri(paymentProcessorFallbackUrl + "/payments")
                .body(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.warning(String.format("Error %s to send DEFAULT payment.", res.getStatusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logger.warning(String.format("Error %s to send DEFAULT payment.", res.getStatusCode()));
                })
                .toBodilessEntity();

        if (!fallbackResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error sending the payment to the DEFAULT");
        }
    }

}
