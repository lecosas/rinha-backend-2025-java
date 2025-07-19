package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentWorker {

    public final BlockingQueue<PaymentDetail> workerQueue = new LinkedBlockingQueue<>();
    //public Queue<PaymentDetail> workerQueue = new ConcurrentLinkedQueue<>();
    private final Logger logger = Logger.getLogger(PaymentWorker.class.getName());
    private final SavePaymentDefaultUC savePayUC;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    //    private final ExecutorService executorService = Executors.newFixedThreadPool(2000);

    //    public void work() {
    //
    //
    //    }

    @PostConstruct
    public void work() {
        for (int i = 1; i <= 4; i++) {
            executorService.submit(() -> {
                while (true) {
                    try {
                        PaymentDetail payment = workerQueue.take();

//                        if (payment != null) {
                            logger.info("WORKER NOVO: " + payment);

                            executorService.execute(() -> {
                                    try {
                                        savePayUC.execute(payment);
                                    } catch (Exception e) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                        workerQueue.offer(payment);
                                    }
                        });
//                        } else {
                            //Thread.sleep(25);
//                        }

                    } catch (Exception e) {
                        break;
                    }
                }
            });
        }
    }

//    @PostConstruct
//    public void work() {
//        for (int i = 1; i <= 4; i++) {
//            executorService.submit(() -> {
//                while (true) {
//                    try {
//                        PaymentDetail payment = workerQueue.poll();
//
//                        if (payment != null) {
//                            logger.info("WORKER: " + payment);
//
//                            executorService.execute(() -> savePayUC.execute(payment));
//                        } else {
//                            Thread.sleep(25);
//                        }
//
//                    } catch (Exception e) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
//                }
//            });
//        }
//    }
}
