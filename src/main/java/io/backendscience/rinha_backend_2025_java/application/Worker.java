package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.inbound.PaymentsController;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class Worker implements CommandLineRunner {

    public BlockingQueue<PaymentDetail> workerQueue = new LinkedBlockingQueue<>();
    private final Logger logger = Logger.getLogger(Worker.class.getName());
    private final SavePaymentDefaultUC savePayUC;
//    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2000);

//    public void work() {
//
//
//    }

    @Override
    public void run(String... args) throws Exception {
//        while (true) {
//            PaymentDetail payment = workerQueue.take();
//
//            logger.info("WORKER: " + payment);
//
//            executorService.execute(() -> savePayUC.execute(payment));
//        }
    }
}
