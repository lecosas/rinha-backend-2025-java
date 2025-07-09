package io.backendscience.rinha_backend_2025_java.adapter.outbound;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentProcessorGateway {

    private final Logger logger = Logger.getLogger(PaymentProcessorGateway.class.getName());

    @Qualifier("webClientDefault")
    private final WebClient webClientDefault;

    @Qualifier("webClientFallback")
    private final WebClient webClientFallback;

    public void savePaymentDefault(PaymentDetail payIn) {

        PaymentDetailToSend payOut = new PaymentDetailToSend(
                payIn.correlationId(), payIn.amount(), payIn.requestedAt().format(DateTimeFormatter.ISO_INSTANT));
        // ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));

        String retorno = webClientDefault
                .post()
                .uri("/payments")
                .bodyValue(payOut)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, success -> {
                    //logger.info("SUCCESS to send: " + success.statusCode());
                    // Handle Todo not found (404) or other errors
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is4xxClientError, error -> {
                    logger.info("Error 400 to send: " + error.statusCode());
                    // Handle Todo not found (404) or other errors
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    //logger.info("Error 500 to send: " + clientResponse.statusCode());
                    // Handle Todo not found (404) or other errors
                    return Mono.empty();
                    // Handle 5xx server errors here
                })
                .bodyToMono(String.class)
                .block();
        //                .onErrorResume(e -> {
        //                    logger.severe(
        //                            "Error to send: " + e.getMessage());
        //                    // Handle Todo not found (404) or other errors
        //                    return Mono.error(new RuntimeException("Error in Payment Processor"));
        //                });

        if (retorno == null || retorno.isBlank()) {
            String retorno2 = webClientFallback
                    .post()
                    .uri("/payments")
                    .bodyValue(payOut)
                    .retrieve()
                    .onStatus(HttpStatusCode::is2xxSuccessful, success -> {
                        logger.info("SUCCESS FALLBACK to send : " + success.statusCode());
                        // Handle Todo not found (404) or other errors
                        return Mono.empty();
                    })
                    .onStatus(HttpStatusCode::is4xxClientError, error -> {
                        logger.info("Error FALLBACK 400 to send: " + error.statusCode());
                        // Handle Todo not found (404) or other errors
                        return Mono.empty();
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        logger.info("Error FALLBACK 500 to send: " + clientResponse.statusCode());
                        // Handle Todo not found (404) or other errors
                        return Mono.empty();
                        // Handle 5xx server errors here
                    })
                    .bodyToMono(String.class)
                    .block();
            //                .onErrorResume(e -> {
            //                    logger.severe(
            //                            "Error to send: " + e.getMessage());
            //                    // Handle Todo not found (404) or other errors
            //                    return Mono.error(new RuntimeException("Error in Payment Processor"));
            //                });
        }
        // System.out.println(retorno);
    }

    private record PaymentDetailToSend(String correlationId, BigDecimal amount, String requestedAt) {}
}
