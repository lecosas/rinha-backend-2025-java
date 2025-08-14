package io.backendscience.rinha_backend_2025_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class RinhaBackend2025JavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RinhaBackend2025JavaApplication.class, args);
    }

//    @Bean
//    CommandLineRunner startServer(final EnqueuePaymentUseCase enqueuePaymentUC,
//                                  final GetPaymentSummaryUseCase getPaymentSummaryUC) {
//        return args -> {
//            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//
//            // Health check endpoint
////            server.createContext("/payment", exchange -> {
////                PaymentDetail paymentDetail = new PaymentDetail(UUID.randomUUID().toString(), BigDecimal.valueOf(19.9));
////                enqueuePaymentUC.execute(paymentDetail);
////
////                exchange.sendResponseHeaders(202, -1); // 202 Accepted, no content
////                exchange.close();
////            });
//
//            server.createContext("/payments", exchange -> {
//                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
//                    String path = exchange.getRequestURI().getPath();
//                    // Expecting /payments/{amount}
//                    String[] parts = path.split("/");
//                    if (parts.length == 3) {
//                        try {
//                            PaymentDetail paymentDetail =
//                                    new PaymentDetail(UUID.randomUUID().toString(), new BigDecimal(parts[2]));
//                            enqueuePaymentUC.execute(paymentDetail);
//
//                            exchange.sendResponseHeaders(202, -1);
//                        } catch (NumberFormatException e) {
//                            // If amount is not a valid number
//                            exchange.sendResponseHeaders(400, -1); // Bad Request
//                        }
//                    } else {
//                        exchange.sendResponseHeaders(404, -1);
//                    }
//                }
//            });
//
//            // Example API endpoint
//            server.createContext("/payments-summary", exchange -> {
//                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
//
//                    String query = exchange.getRequestURI().getQuery();
//                    OffsetDateTime from = null;
//                    OffsetDateTime to = null;
//
//                    if (query != null) {
//                        Map<String, String> params = parseQuery(query);
//                        if (params.containsKey("from")) {
//                            from = OffsetDateTime.parse(params.get("from"));
//                        }
//                        if (params.containsKey("to")) {
//                            to = OffsetDateTime.parse(params.get("to"));
//                        }
//                    }
//
//                    // Get summary
//                    PaymentSummary summary = getPaymentSummaryUC.execute(from, to);
//
//                    // Build JSON manually
//                    String json = String.format(
//                            "{\"default\":{\"totalRequests\":%d,\"totalAmount\":%s},\"fallback\":{\"totalRequests\":%d,\"totalAmount\":%s}}",
//                            summary.defaultResponse().totalRequests(),
//                            summary.defaultResponse().totalAmount(),
//                            summary.fallback().totalRequests(),
//                            summary.fallback().totalAmount()
//                    );
//
//                    exchange.getResponseHeaders().add("Content-Type", "application/json");
//                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//                    exchange.sendResponseHeaders(200, bytes.length);
//                    try (OutputStream os = exchange.getResponseBody()) {
//                        os.write(bytes);
//                    }
//                } else {
//                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
//                }
//            });
//
//            server.start();
//            System.out.println("🚀 HTTP server started on port 8080 (no Tomcat)");
//        };
//    }
//
//    private static Map<String, String> parseQuery(String query) {
//        return Arrays.stream(query.split("&"))
//                .map(s -> s.split("=", 2))
//                .collect(Collectors.toMap(
//                        arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
//                        arr -> arr.length > 1 ? URLDecoder.decode(arr[1], StandardCharsets.UTF_8) : ""
//                ));
//    }

}
