package io.backendscience.rinha_backend_2025_java.application.service;

import io.backendscience.rinha_backend_2025_java.application.port.out.SemaphoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SemaphoreService {

    private final Logger logger = Logger.getLogger(SemaphoreService.class.getName());
    private final SemaphoreRepository semaphoreRepository;
    private final String WORKER_STATUS_PAUSED = "paused";
    private final String WORKER_STATUS_WORKING = "working";

    public void pauseWorker() {
        semaphoreRepository.setWorkerStatus(WORKER_STATUS_PAUSED);
    }

    public void resumeWorker() {
        semaphoreRepository.setWorkerStatus(WORKER_STATUS_WORKING);
    }

    public boolean isWorkerPaused() {
        return semaphoreRepository.getWorkerStatus().equalsIgnoreCase(WORKER_STATUS_PAUSED);
    }

    public void incrementLocalSavingCounter() {
        semaphoreRepository.incrementLocalSavingCounter();
    }

    public void decrementLocalSavingCounter() {
        semaphoreRepository.decrementLocalSavingCounter();
    }

    public long getLocalSavingCounter() {
        String counterStr = semaphoreRepository.getLocalSavingCounter();
        return counterStr != null ? Long.parseLong(counterStr) : 0L;
    }

    public void resetLocalSavingCounter() {
        semaphoreRepository.resetLocalSavingCounter();
    }

    public boolean isSavingLocalData() {
        long savingCounter = getLocalSavingCounter();

        if (savingCounter == 0) {
            logger.info("Complete saving local data.");
            return false;
        }

        logger.info(String.format("Still saving local data: (counter: %s)", savingCounter));
        return true;
    }
}
