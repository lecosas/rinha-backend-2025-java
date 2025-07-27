package io.backendscience.rinha_backend_2025_java.adapter.out.http;

import io.backendscience.rinha_backend_2025_java.application.port.out.HealthCheckGateway;
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
public class HealthCheckClient implements HealthCheckGateway {

    private final Logger logger = Logger.getLogger(HealthCheckClient.class.getName());

    @Qualifier("restClientDefault")
    private final RestClient restClientDefault;

    @Qualifier("restClientFallback")
    private final RestClient restClientFallback;

    private final String HEALTH_CHECK_ENDPOINT = "/payments/service-health";

    public HealthCheckStatus getHeathCheckDefault() {
        return getHealthCheck(restClientDefault);
    }

    public HealthCheckStatus getHeathCheckFallback() {
        return getHealthCheck(restClientFallback);
    }

    private HealthCheckStatus getHealthCheck(RestClient restClient) {
        ResponseEntity<HealthCheckStatus> retorno = restClient
                .get()
                .uri(HEALTH_CHECK_ENDPOINT)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    logger.severe("Error %s to get health check status." + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    logger.severe("Error %s to get health check status." + res.getStatusCode());
                })
                .toEntity(HealthCheckStatus.class);

        if (retorno.getStatusCode().is2xxSuccessful()) {
            return retorno.getBody();
        }

        throw new RuntimeException("Error to get health check status.");
    }

}
