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

    @Value("${payment-backend.worker.thread-delay}")
    private long threadDelay;

    @Value("${payment-backend.worker.paused-delay}")
    private long pausedDelay;

    @Value("${payment-backend.worker.stopped-delay}")
    private long stoppedDelay;

    @Value("${payment-backend.worker.exception-delay}")
    private long exceptionDelay;

    private final BlockingQueue<PaymentDetail> workerQueue = new LinkedBlockingQueue<>();
    private final Logger logger = Logger.getLogger(PaymentWorker.class.getName());
    private final PaymentService paymentService;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final HealthCheckEngine healthCheckEngine;
    private final AtomicBoolean isWorking = new AtomicBoolean(false);
    private final SemaphoreService semaphoreService;

    public void startExecution() {
        if (!isWorking.get()) work();
    }

    public void work() {
        isWorking.set(true);
        logger.info("Starting Payment Worker.");
        for (int i = 1; i < 200; i++) {
            executorService.submit(() -> {
                logger.info("Payment Worker started.");

                while (true) {
                    if (semaphoreService.isWorkerPaused()) {
                        pauseFor(pausedDelay);
                        continue;
                    }

                    PaymentProcessorType paymentType = healthCheckEngine.getHeathCheckStatus();

                    if (paymentType == PaymentProcessorType.NONE) {
                        pauseFor(stoppedDelay);
                        continue;
                    } else if (paymentType == PaymentProcessorType.FALLBACK) {
                        pauseFor(fallbackDelay);
                    }

                    PaymentDetail payment = workerQueue.take();

                    pauseFor(threadDelay);

//                    executorService.execute(() -> {
                        try {
                            paymentService.process(payment, paymentType);
                        } catch (Exception e) {
                            healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.NONE);
                            pauseFor(exceptionDelay);
                            workerQueue.add(payment);
                        }
//                    });
                }
            });
        }
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
}
