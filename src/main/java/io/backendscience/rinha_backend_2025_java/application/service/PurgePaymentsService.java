package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.in.PurgePaymentsUseCase;
import io.backendscience.rinha_backend_2025_java.application.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PurgePaymentsService implements PurgePaymentsUseCase {

    private final Logger logger = Logger.getLogger(PurgePaymentsService.class.getName());
    private final PaymentRepository paymentRepository;

    public void execute() {
        try {
            logger.info("Start purging payments.");
            paymentRepository.purgePayments();
            logger.info("Payments purged successfully.");
        } catch (Exception e) {
            logger.severe("Error by purging payments. Message: " + e.getMessage());
        }
    }
}
