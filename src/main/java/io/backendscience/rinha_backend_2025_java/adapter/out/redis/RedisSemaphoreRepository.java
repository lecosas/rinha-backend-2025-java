package io.backendscience.rinha_backend_2025_java.adapter.out.redis;

import io.backendscience.rinha_backend_2025_java.application.port.out.SemaphoreRepository;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSemaphoreRepository implements SemaphoreRepository {

    private final RedisCommands<String, String> redis;
    private final String WORKER_STATUS_KEY = "worker:status";
    private final String LOCAL_SAVING_COUNTER_KEY = "local-saving:counter";

    public void setWorkerStatus(String workerStatus) {
        redis.set(WORKER_STATUS_KEY, workerStatus);
    }

    public String getWorkerStatus() {
        return redis.get(WORKER_STATUS_KEY);
    }

    public void incrementLocalSavingCounter() {
        redis.incr(LOCAL_SAVING_COUNTER_KEY);
    }

    public void decrementLocalSavingCounter() {
        redis.decr(LOCAL_SAVING_COUNTER_KEY);
    }

    public String getLocalSavingCounter() {
        return redis.get(LOCAL_SAVING_COUNTER_KEY);
    }

    public void resetLocalSavingCounter() {
        redis.set(LOCAL_SAVING_COUNTER_KEY, "0");
    }

}
