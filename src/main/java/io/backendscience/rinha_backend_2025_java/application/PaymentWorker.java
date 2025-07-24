package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentProcessorType;
import io.lettuce.core.api.sync.RedisCommands;
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
    String PROCESSING_COUNTER_KEY = "payment-processing:counter";

    public void work() {
        isWorking = true;
        //for (int i = 1; i <= 4; i++) {
            executorService.submit(() -> {
                while (true) {
                    try {

                        while (true) {
                            String workerPause = redis.get("worker:pause");

                            if (!workerPause.equals("true"))
                                break;

                            try {
                                logger.severe("WORKER: PARADO POR GET SUMMARY ----------------------------------------------: ");
                                Thread.sleep(100); // small backoff
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        };

                        logger.info("WORKER: free.");

                        PaymentDetail payment = workerQueue.take();

                        PaymentProcessorType paymentType = healthCheckEngine.getHeathCheckStatus();

                        if (paymentType == PaymentProcessorType.STOPPED) {
                            logger.severe("WORKER: PARADO POR STOPPED ----------------------------------------------: ");
                            Thread.sleep(500);
                            workerQueue.add(payment);
                            continue;
                        } else if (paymentType == PaymentProcessorType.FALLBACK) {
                            //Thread.sleep(200);
                        }

                        logger.info("WORKER: is going to execute.");


                        executorService.execute(() -> {
                            try {
                                redis.incr(PROCESSING_COUNTER_KEY);
                                savePaymentUC.execute(payment, paymentType);
                            } catch (Exception e) {
                                try {
                                    healthCheckEngine.setHeathCheckStatus(PaymentProcessorType.STOPPED);
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                workerQueue.add(payment);
                            } finally {
                                redis.decr(PROCESSING_COUNTER_KEY);
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
