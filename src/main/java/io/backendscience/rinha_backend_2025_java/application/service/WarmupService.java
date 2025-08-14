package io.backendscience.rinha_backend_2025_java.application.service;

import com.sun.net.httpserver.HttpServer;
import io.backendscience.rinha_backend_2025_java.application.port.in.EnqueuePaymentUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.in.GetPaymentSummaryUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarmupService implements CommandLineRunner {

    @Value("${payment-backend.warmup.cycles}")
    private long warmupCycles;

    private final PaymentProcessorGateway paymentProcessor;
    private final Logger logger = Logger.getLogger(WarmupService.class.getName());
    private final PurgePaymentsService purgePaymentsService;
    private final HealthCheckEngine healthCheckEngine;
    private final SemaphoreService semaphoreService;
    private final EnqueuePaymentUseCase enqueuePaymentUC;
    private final GetPaymentSummaryUseCase getPaymentSummaryUC;
    private final PaymentWorker paymentWorker;

    @Override
    public void run(String... args) throws Exception {
        logger.severe("START: Warmup");

        long startTime = System.nanoTime();

        purgePaymentsService.execute();

        for (int i = 0; i < warmupCycles; i++) {
            paymentProcessorWarmup();

            healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.DEFAULT);

            logger.info("Setting PaymentWorker to working state.");
            semaphoreService.resumeWorker();
            logger.info("PaymentWorker is set to working.");

            logger.info("Resetting Local Saving Counter.");
            semaphoreService.resetLocalSavingCounter();
            logger.info("Local Saving Counter is reset.");
        }

        healthCheckEngine.startExecution();

        startServer();

        paymentWorker.startExecution();
        logger.severe(String.format("END: Warmup in %.3fms", (System.nanoTime() - startTime) / 1_000_000.0));

        Thread.currentThread().join();
    }

    private void paymentProcessorWarmup() {
        PaymentDetail paymentDetail = new PaymentDetail("INVALID", BigDecimal.ZERO);

        try {
            paymentProcessor.sendPaymentToDefault(
                    paymentDetail, OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        } catch (Exception ex) {

        }

        try {
            paymentProcessor.sendPaymentToFallback(
                    paymentDetail, OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        } catch (Exception ex) {

        }
    }

    private void startServer() throws IOException {
        Executor executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());


        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10000);
        server.setExecutor(executor);

        System.out.println("🚀 Virtual Threads 2");

        // Health check endpoint
//        server.createContext("/payments", exchange -> {
//            PaymentDetail paymentDetail = new PaymentDetail(UUID.randomUUID().toString(), BigDecimal.valueOf(19.9));
//            enqueuePaymentUC.execute(paymentDetail);
//
//            exchange.sendResponseHeaders(202, -1); // 202 Accepted, no content
//            exchange.close();
//        });

        server.createContext("/payments", exchange -> {
//            long startTime = System.nanoTime();

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
//                System.out.println("START: Server Context /payments");


                String path = exchange.getRequestURI().getPath();
                // Expecting /payments/{amount}
                String[] parts = path.split("/");
                if (parts.length == 3) {
                    try {


                        exchange.sendResponseHeaders(202, -1);
                    } catch (NumberFormatException e) {
                        // If amount is not a valid number
                        exchange.sendResponseHeaders(400, -1); // Bad Request
                    } finally {
                        PaymentDetail paymentDetail =
                                new PaymentDetail(UUID.randomUUID().toString(), new BigDecimal(parts[2]));
//                        enqueuePaymentUC.tryProcessOrEnqueue(paymentDetail);
                        enqueuePaymentUC.execute(paymentDetail);

//                        System.out.println(String.format(
//                                "END: Server Context /payments in %.3fms", (System.nanoTime() - startTime) / 1_000_000.0));

                    }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        });

        // Example API endpoint
        server.createContext("/payments-summary", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                String query = exchange.getRequestURI().getQuery();
                OffsetDateTime from = null;
                OffsetDateTime to = null;

                if (query != null) {
                    Map<String, String> params = parseQuery(query);
                    if (params.containsKey("from")) {
                        from = OffsetDateTime.parse(params.get("from"));
                    }
                    if (params.containsKey("to")) {
                        to = OffsetDateTime.parse(params.get("to"));
                    }
                }

                // Get summary
                PaymentSummary summary = getPaymentSummaryUC.execute(from, to);

                // Build JSON manually
                String json = String.format(
                        "{\"default\":{\"totalRequests\":%d,\"totalAmount\":%s},\"fallback\":{\"totalRequests\":%d,\"totalAmount\":%s}}",
                        summary.defaultResponse().totalRequests(),
                        summary.defaultResponse().totalAmount(),
                        summary.fallback().totalRequests(),
                        summary.fallback().totalAmount()
                );

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        });

        server.start();
        System.out.println("🚀 HTTP server started on port 8080 (no Tomcat)");
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                        arr -> arr.length > 1 ? URLDecoder.decode(arr[1], StandardCharsets.UTF_8) : ""
                ));
    }

}
