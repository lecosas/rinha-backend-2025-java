package io.backendscience.rinha_backend_2025_java.application;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PurgePaymentsUC {

    private final Logger logger = Logger.getLogger(PurgePaymentsUC.class.getName());
    private final Worker worker;
//    private final RedisTemplate redisTemplate;
    private final RedisCommands<String, String> redis;

    public void execute() {
        try {
            worker.workerQueue = new LinkedBlockingQueue<>();

            redis.del("payments:type:0:count", "payments:type:1:count");
//            redisTemplate.delete(List.of("payments:type:0:count", "payments:type:1:count"));


            logger.info("Payments purged successfully.");
        } catch (Exception e) {
            logger.severe("Error to purge payments. Message: " + e.getMessage());
        }
    }
}
