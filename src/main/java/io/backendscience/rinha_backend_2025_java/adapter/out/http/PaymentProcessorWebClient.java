package io.backendscience.rinha_backend_2025_java.adapter.out.http;

import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentProcessorWebClient implements PaymentProcessorGateway {

    private final Logger logger = Logger.getLogger(PaymentProcessorWebClient.class.getName());

    @Qualifier("webClientDefault")
    private final WebClient webClientDefault;

    @Qualifier("webClientFallback")
    private final WebClient webClientFallback;

    public void sendPaymentToDefault(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {
        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> defaultResponse = webClientDefault
                .post()
                .uri("/payments")
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
        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> fallbackResponse = webClientFallback
                .post()
                .uri("/payments")
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


}
