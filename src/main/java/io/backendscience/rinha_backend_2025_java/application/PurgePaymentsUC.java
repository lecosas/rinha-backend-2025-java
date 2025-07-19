package io.backendscience.rinha_backend_2025_java.application;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PurgePaymentsUC {

    private final Logger logger = Logger.getLogger(PurgePaymentsUC.class.getName());
    private final RedisCommands<String, String> redis;

    public void execute() {
        try {
            logger.info("Starting purge payments successfully.");
            redis.del("payments:type:0:count", "payments:type:1:count");
            logger.info("Payments purged successfully.");
        } catch (Exception e) {
            logger.severe("Error by purging payments. Message: " + e.getMessage());
        }
    }
}
