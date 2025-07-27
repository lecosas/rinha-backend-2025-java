package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentWorker {

    @Value("${payment-backend.worker.fallback-delay}")
    private long fallbackDelay;

    private final BlockingQueue<PaymentDetail> workerQueue = new LinkedBlockingQueue<>();
    private final Logger logger = Logger.getLogger(PaymentWorker.class.getName());
    private final PaymentService paymentService;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final HealthCheckEngine healthCheckEngine;
    private final AtomicBoolean isWorking = new AtomicBoolean(false);
    private final SemaphoreService semaphoreService;

    public void work() {
        isWorking.set(true);
        // for (int i = 1; i <= 4; i++) {
        executorService.submit(() -> {
            while (true) {
                if (semaphoreService.isWorkerPaused()) {
                    logger.severe("WORKER: PARADO POR GET SUMMARY ----------------------------------------------: ");
                    pauseFor(100);
                    continue;
                }

                PaymentProcessorType paymentType = healthCheckEngine.getHeathCheckStatus();

                if (paymentType == PaymentProcessorType.NONE) {
                    logger.severe("WORKER: PARADO POR STOPPED ----------------------------------------------: ");
                    pauseFor(500);
                    continue;
                } else if (paymentType == PaymentProcessorType.FALLBACK) {
                    pauseFor(fallbackDelay);
                }

                PaymentDetail payment = workerQueue.take();

                logger.info("WORKER: is going to execute.");

                executorService.execute(() -> {
                    try {
                        paymentService.process(payment, paymentType);
                    } catch (Exception e) {
                        healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.NONE);
                        pauseFor(500);
                        workerQueue.add(payment);
                    }
                });
            }
        });
        // }
    }

    private void pauseFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(PaymentDetail paymentDetail) {
        workerQueue.add(paymentDetail);
    }

    public boolean isWorking() {
        return isWorking.get();
    }

}
