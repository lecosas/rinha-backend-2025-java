package io.backendscience.rinha_backend_2025_java.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.timeseries.DuplicatePolicy;
import redis.clients.jedis.timeseries.TSCreateParams;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PurgePaymentsUC {

    private final Logger logger = Logger.getLogger(PurgePaymentsUC.class.getName());
    private final JedisPooled jedisPooled;
    private final Worker worker;

    public void execute() {
        try {
            worker.workerQueue = new LinkedBlockingQueue<>();

            jedisPooled.del("payments:type:0:count", "payments:type:1:count");
            jedisPooled.tsCreate("payments:type:0:count", TSCreateParams.createParams().duplicatePolicy(DuplicatePolicy.SUM));
            jedisPooled.tsCreate("payments:type:1:count", TSCreateParams.createParams().duplicatePolicy(DuplicatePolicy.SUM));
            logger.info("Payments purged successfully.");
        } catch (Exception e) {
            logger.severe("Error to purge payments. Message: " + e.getMessage());
        }
    }
}
