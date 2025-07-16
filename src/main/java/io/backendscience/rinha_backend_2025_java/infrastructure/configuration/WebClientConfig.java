package io.backendscience.rinha_backend_2025_java.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {

//    @Value("${payment-processor.defaultUrl}")
//    private String paymentProcessorDefaultUrl;
//
//    @Value("${payment-processor.fallbackUrl}")
//    private String paymentProcessorFallbackUrl;
//
//    @Value("${httpclient.connection-timeout:5000}")
//    private int connectionTimeout;
//
//    @Value("${httpclient.read-timeout:10000}")
//    private int readTimeout;
//
//    @Value("${httpclient.socket-timeout:10000}")
//    private int socketTimeout;
//
//    @Value("${httpclient.max-total-connections:400}")
//    private int maxTotalConnections;
//
//    @Value("${httpclient.max-per-route-connections:400}")
//    private int maxPerRouteConnections;
//
//    private final Logger logger = Logger.getLogger(WebClientConfig.class.getName());
//
//    @Bean("webClientDefault")
//    public WebClient webClientDefault() {
//        logger.info("WebClient: connectionTimeout: " + connectionTimeout);
//        logger.info("WebClient: readTimeout: " + readTimeout);
//        logger.info("WebClient: socketTimeout: " + socketTimeout);
//        logger.info("WebClient: maxTotalConnections: " + maxTotalConnections);
//        logger.info("WebClient: maxPerRouteConnections: " + maxPerRouteConnections);
//
//        ConnectionProvider provider = ConnectionProvider.builder("ybs-pool")
//            .maxConnections(maxTotalConnections)
//            .pendingAcquireTimeout(Duration.ofMillis(0))
//            .pendingAcquireMaxCount(-1)
//            .maxIdleTime(Duration.ofMillis(8000L))
//            .maxLifeTime(Duration.ofMillis(8000L))
//            .build();
//
////        ConnectionProvider provider = ConnectionProvider.builder("custom")
////            .maxConnections(maxTotalConnections)  // Match maxTotalConnections
////            .pendingAcquireTimeout(Duration.ofMillis(connectionTimeout))  // Match connectionTimeout
////            .maxIdleTime(Duration.ofSeconds(30))  // Adjusted for better reuse
////            .maxLifeTime(Duration.ofMinutes(5))  // Similar to traditional pools
////            .evictInBackground(Duration.ofSeconds(30))  // Clean idle connections periodically
////            .build();
//
//        HttpClient httpClient = HttpClient.create(provider)
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)  // Match connectionTimeout
//            .responseTimeout(Duration.ofMillis(socketTimeout))  // Match socketTimeout
//            .doOnConnected(conn -> conn
//                .addHandlerLast(new ReadTimeoutHandler(readTimeout / 1000))  // Convert ms to s
//                .addHandlerLast(new WriteTimeoutHandler(readTimeout / 1000)));  // Convert ms to s
//
//        return WebClient.builder()
//            .clientConnector(new ReactorClientHttpConnector(httpClient))
//            .baseUrl(paymentProcessorDefaultUrl)
//            //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .build();
//    }
//
//    @Bean("webClientFallback")
//    public WebClient webClientFallback() {
//        logger.info("WebClient: connectionTimeout: " + connectionTimeout);
//        logger.info("WebClient: readTimeout: " + readTimeout);
//        logger.info("WebClient: socketTimeout: " + socketTimeout);
//        logger.info("WebClient: maxTotalConnections: " + maxTotalConnections);
//        logger.info("WebClient: maxPerRouteConnections: " + maxPerRouteConnections);
//
//        ConnectionProvider provider = ConnectionProvider.builder("ybs-pool")
//                .maxConnections(maxTotalConnections)
//                .pendingAcquireTimeout(Duration.ofMillis(0))
//                .pendingAcquireMaxCount(-1)
//                .maxIdleTime(Duration.ofMillis(8000L))
//                .maxLifeTime(Duration.ofMillis(8000L))
//                .build();
//
////        ConnectionProvider provider = ConnectionProvider.builder("custom")
////            .maxConnections(maxTotalConnections)  // Match maxTotalConnections
////            .pendingAcquireTimeout(Duration.ofMillis(connectionTimeout))  // Match connectionTimeout
////            .maxIdleTime(Duration.ofSeconds(30))  // Adjusted for better reuse
////            .maxLifeTime(Duration.ofMinutes(5))  // Similar to traditional pools
////            .evictInBackground(Duration.ofSeconds(30))  // Clean idle connections periodically
////            .build();
//
//        HttpClient httpClient = HttpClient.create(provider)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)  // Match connectionTimeout
//                .responseTimeout(Duration.ofMillis(socketTimeout))  // Match socketTimeout
//                .doOnConnected(conn -> conn
//                        .addHandlerLast(new ReadTimeoutHandler(readTimeout / 1000))  // Convert ms to s
//                        .addHandlerLast(new WriteTimeoutHandler(readTimeout / 1000)));  // Convert ms to s
//
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(paymentProcessorFallbackUrl)
//                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }
}
