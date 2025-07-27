package io.backendscience.rinha_backend_2025_java.adapter.out.http;

import io.backendscience.rinha_backend_2025_java.application.port.out.HealthCheckGateway;
import io.backendscience.rinha_backend_2025_java.domain.HealthCheckStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class HealthCheckClient implements HealthCheckGateway {

    private final Logger logger = Logger.getLogger(HealthCheckClient.class.getName());

    @Qualifier("webClientDefault")
    private final WebClient webClientDefault;

    @Qualifier("webClientFallback")
    private final WebClient webClientFallback;

    private final String HEALTH_CHECK_ENDPOINT = "/payments/service-health";

    public HealthCheckStatus getHeathCheckDefault() {
        return getHealthCheck(webClientDefault);
    }

    public HealthCheckStatus getHeathCheckFallback() {
        return getHealthCheck(webClientFallback);
    }

    private HealthCheckStatus getHealthCheck(WebClient webClient) {
        ResponseEntity<HealthCheckStatus> healthCheckStatusResponse = webClient
                .get()
                .uri(HEALTH_CHECK_ENDPOINT)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    logger.severe(String.format("Error %s to get health check status.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    logger.severe(String.format("Error %s to get health check status.", clientResponse.statusCode()));
                    return Mono.empty();
                })
                .toEntity(HealthCheckStatus.class)
                .block();

        if (healthCheckStatusResponse.getStatusCode().is2xxSuccessful()) {
            return healthCheckStatusResponse.getBody();
        }

        throw new RuntimeException("Error to get health check status.");
    }

}
