package io.backendscience.rinha_backend_2025_java.application.port.out;

public interface SemaphoreRepository {

    void setWorkerStatus(String workerStatus);
    String getWorkerStatus();
    void incrementLocalSavingCounter();
    void decrementLocalSavingCounter();
    String getLocalSavingCounter();
    void resetLocalSavingCounter();
}
