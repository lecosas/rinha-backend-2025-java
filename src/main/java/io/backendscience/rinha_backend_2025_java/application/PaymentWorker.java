package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentWorker {

    public final BlockingQueue<PaymentDetail> workerQueue = new LinkedBlockingQueue<>();
    private final Logger logger = Logger.getLogger(PaymentWorker.class.getName());
    private final SavePaymentUC savePaymentUC;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final RedisCommands<String, String> redis;
    private final HealthCheckEngine healthCheckEngine;
    private boolean isWorking;

    public void work() {
        isWorking = true;
        //for (int i = 1; i <= 4; i++) {
            executorService.submit(() -> {
                while (true) {
                    try {
                        PaymentDetail payment = workerQueue.take();

                        PaymentProcessorType paymentType = healthCheckEngine.getHeathCheckStatus();

                        if (paymentType == PaymentProcessorType.NONE) {
                            logger.severe("WORKER PARADOOOOOOO: ");
                            Thread.sleep(500);
                            workerQueue.add(payment);
                            continue;
                        } else if (paymentType == PaymentProcessorType.FALLBACK) {
                            Thread.sleep(200);
                        }

                        logger.info("WORKER NOVO: " + payment);


                        executorService.execute(() -> {
                            try {
                                savePaymentUC.execute(payment, paymentType);
                            } catch (Exception e) {
                                try {
                                    healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.NONE);
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                workerQueue.add(payment);
                            }
                        });

                    } catch (Exception e) {
                        continue;
                    }
                }
            });
        //}
    }

    public boolean isWorking() {
        return isWorking;
    }
}
