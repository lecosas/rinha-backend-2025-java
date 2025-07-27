package io.backendscience.rinha_backend_2025_java.adapter.out;

import io.backendscience.rinha_backend_2025_java.domain.HealthCheckStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class HealthCheckGateway {

    private final Logger logger = Logger.getLogger(HealthCheckGateway.class.getName());

    @Qualifier("restClientDefault")
    private final RestClient restClientDefault;

    @Qualifier("restClientFallback")
    private final RestClient restClientFallback;

    public HealthCheckStatus getHeathCheckDefault() {
        return getHealthCheck(restClientDefault);
    }

    public HealthCheckStatus getHeathCheckFallback() {
        return getHealthCheck(restClientFallback);
    }

    private HealthCheckStatus getHealthCheck(RestClient restClient) {

        ResponseEntity<HealthCheckStatus> retorno = restClient
                .get()
                .uri("/payments/service-health")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.info("Error 400 to send: " + res.getStatusCode());
                    // Handle Todo not found (404) or other errors
                    //                    return null;
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    // logger.info("Error 500 to send: " + clientResponse.statusCode());
                    // Handle Todo not found (500) or other errors
                    // Handle 5xx server errors here
                })
                .toEntity(HealthCheckStatus.class);

        if (retorno.getStatusCode().is2xxSuccessful()) {
            return retorno.getBody();
        }

        throw new RuntimeException("erro ao obter health check");
    }

}
