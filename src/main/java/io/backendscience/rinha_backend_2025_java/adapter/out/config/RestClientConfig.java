package io.backendscience.rinha_backend_2025_java.adapter.out.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.logging.Logger;

@Configuration
public class RestClientConfig {

    @Value("${payment-processor.default-url}")
    private String paymentProcessorDefaultUrl;

    @Value("${payment-processor.fallback-url}")
    private String paymentProcessorFallbackUrl;

    @Value("${httpclient.connection-timeout}")
    private int connectionTimeout;

    @Value("${httpclient.read-timeout}")
    private int readTimeout;

    @Value("${httpclient.socket-timeout}")
    private int socketTimeout;

    @Value("${httpclient.max-total-connections}")
    private int maxTotalConnections;

    @Value("${httpclient.max-per-route-connections}")
    private int maxPerRouteConnections;

    private final Logger logger = Logger.getLogger(RestClientConfig.class.getName());

    @Bean("restClientDefault")
    public RestClient restClientDefault() {
        return RestClient.builder()
                .baseUrl(paymentProcessorDefaultUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(this.clientHttpRequestFactory())
                .build();
    }

    @Bean("restClientFallback")
    public RestClient restClientFallback() {
        return RestClient.builder()
                .baseUrl(paymentProcessorFallbackUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(this.clientHttpRequestFactory())
                .build();
    }

    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        logger.info(String.format(
                "Configuring RestClient properties: connectionTimeout: %s | readTimeout: %s | socketTimeout: %s | maxTotalConnections: %s | maxPerRouteConnections: %s.",
                connectionTimeout, readTimeout, socketTimeout, maxTotalConnections, maxPerRouteConnections));

        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(maxTotalConnections);
        poolingConnManager.setDefaultMaxPerRoute(maxPerRouteConnections);

        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofMilliseconds(socketTimeout))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolingConnManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(Duration.ofMillis(connectionTimeout));
        factory.setReadTimeout(Duration.ofMillis(readTimeout));

        return factory;
    }
}
