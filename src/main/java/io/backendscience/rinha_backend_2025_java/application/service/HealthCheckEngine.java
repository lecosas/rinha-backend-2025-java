package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.adapter.out.redis.RedisHealthCheckRepository;
import io.backendscience.rinha_backend_2025_java.application.port.out.HealthCheckGateway;
import io.backendscience.rinha_backend_2025_java.domain.HealthCheckStatus;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class HealthCheckEngine {

    @Value("${payment-backend.main-instance}")
    private Boolean isMainInstance;

    private final Logger logger = Logger.getLogger(HealthCheckEngine.class.getName());

    private final HealthCheckGateway healthCheck;
    private final RedisHealthCheckRepository healthCheckRepository;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicBoolean isExecuting = new AtomicBoolean(false);

    public boolean isExecuting() {
        return isExecuting.get();
    }

    public void startExecution() {
        if (isMainInstance) {
            isExecuting.set(true);

            executor.submit(() -> {
                logger.info("Health Check Engine is starting.");

                while (true) {
                    logger.info("Retrieving health checks.");

                    executeHealthChecks();
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void executeHealthChecks() {
        CompletableFuture<HealthCheckStatus> taskDefault =
                CompletableFuture.supplyAsync(healthCheck::getHeathCheckDefault, executor);

        CompletableFuture<HealthCheckStatus> taskFallback =
                CompletableFuture.supplyAsync(healthCheck::getHeathCheckFallback, executor);

        try {
            HealthCheckStatus healthStatusDefault = taskDefault.get();

            logger.info("Default Health Checks retrieved: " + healthStatusDefault);

            if (!healthStatusDefault.failing() && healthStatusDefault.minResponseTime() <= 100) {
                logger.info("Health Checks set as DEFAULT");
                setHeathCheckStatus(PaymentProcessorType.DEFAULT);
                return;
            }

            HealthCheckStatus healthStatusFallback = taskFallback.get();

            if (healthStatusDefault.failing() && healthStatusFallback.failing()) {
                setHeathCheckStatus(PaymentProcessorType.NONE);
            } else if (healthStatusDefault.failing()
                    && !healthStatusFallback.failing()
                    && healthStatusFallback.minResponseTime() <= 1_000) {
                setHeathCheckStatus(PaymentProcessorType.FALLBACK);
            } else if (!healthStatusDefault.failing()) {
                setHeathCheckStatus(PaymentProcessorType.DEFAULT);
            } else {
                setHeathCheckStatus(PaymentProcessorType.NONE);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentProcessorType getHeathCheckStatus() {
        return healthCheckRepository.getHeathCheckStatus();
    }

    public void setHeathCheckStatus(PaymentProcessorType type) {
        healthCheckRepository.setHeathCheckStatus(type);
    }
}
