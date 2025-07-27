package io.backendscience.rinha_backend_2025_java.adapter.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.PaymentDetailToSend;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentProcessorGateway {

    private final Logger logger = Logger.getLogger(PaymentProcessorGateway.class.getName());

    @Qualifier("restClientDefault")
    private final RestClient restClientDefault;

    @Qualifier("restClientFallback")
    private final RestClient restClientFallback;

    public void savePaymentDefault(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {

        PaymentDetailToSend payOut = new PaymentDetailToSend(
                paymentDetail.correlationId(),
                paymentDetail.amount(),
                requestedAt.format(DateTimeFormatter.ISO_INSTANT));

        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> retorno = restClientDefault
                .post()
                .uri("/payments")
                .body(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, res) -> {
                    logger.info("Success DEFAULT to send : " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.info("Error 400 to send: " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                    //                    return null;
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logger.info("Error 500 to send: " + res.getStatusCode());
                    // Handle Todo not found (500) or other errors
                    // Handle 5xx server errors here
                })
                .toBodilessEntity();

        if (!retorno.getStatusCode().is2xxSuccessful()) {
            System.out.println(retorno);
            throw new RuntimeException("deu erro");
        }
    }

    public void savePaymentFallback(PaymentDetail paymentDetail, OffsetDateTime requestedAt) {

        PaymentDetailToSend payOut = new PaymentDetailToSend(
                paymentDetail.correlationId(),
                paymentDetail.amount(),
                requestedAt.format(DateTimeFormatter.ISO_INSTANT));

        String payToSend = new StringBuilder("{")
                .append("\"correlationId\":\"").append(paymentDetail.correlationId()).append("\",")
                .append("\"amount\":").append(paymentDetail.amount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(requestedAt.format(DateTimeFormatter.ISO_INSTANT)).append("\"")
                .append("}")
                .toString();

        ResponseEntity<Void> retorno2 = restClientFallback
                .post()
                .uri("/payments")
                .body(payToSend)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, res) -> {
                    logger.info("Success FALLBACK to send : " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.info("Error FALLBACK 400 to send: " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logger.info("Error FALLBACK 500 to send: " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                    // Handle 5xx server errors here
                })
                .toBodilessEntity();

        if (!retorno2.getStatusCode().is2xxSuccessful()) {
            System.out.println(retorno2);
            throw new RuntimeException("deu erro");
        }
    }


}
