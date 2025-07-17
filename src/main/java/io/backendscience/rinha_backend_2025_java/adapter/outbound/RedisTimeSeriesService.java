package io.backendscience.rinha_backend_2025_java.adapter.outbound;

import org.springframework.stereotype.Service;

@Service
public class RedisTimeSeriesService {

//    private final StringRedisTemplate redisTemplate;
//
//    public RedisTimeSeriesService(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public void deleteKey(String key) {
//        redisTemplate.delete(key);
//    }
//
//    public void createTimeSeriesWithDuplicatePolicy(String key) {
//        redisTemplate.execute((RedisConnection connection) -> {
//            return connection.execute("TS.CREATE",
//                    key.getBytes(StandardCharsets.UTF_8),
//                    "DUPLICATE_POLICY".getBytes(StandardCharsets.UTF_8),
//                    "SUM".getBytes(StandardCharsets.UTF_8)
//            );
//        });
//    }
//
//    public void addDataPoint(String key, long timestamp, double value) {
//        redisTemplate.execute((RedisConnection connection) -> {
//            return connection.execute("TS.ADD",
//                    key.getBytes(StandardCharsets.UTF_8),
//                    String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8),
//                    String.valueOf(value).getBytes(StandardCharsets.UTF_8)
//            );
//        });
//    }
////
////    public void addTimeSeriesPoint(String key, long timestamp, double value) {
////        redisTemplate.execute((RedisConnection connection) -> {
////            String command = String.format("TS.ADD %s %d %f", key, timestamp, value);
////            return connection.execute("TS.ADD",
////                    key.getBytes(StandardCharsets.UTF_8),
////                    String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8),
////                    String.valueOf(value).getBytes(StandardCharsets.UTF_8)
////            );
////        });
////    }
//
//    public Object getRange(String key, long fromTimestamp, long toTimestamp) {
//        return redisTemplate.execute((RedisConnection connection) ->
//                connection.execute("TS.RANGE",
//                        key.getBytes(StandardCharsets.UTF_8),
//                        String.valueOf(fromTimestamp).getBytes(StandardCharsets.UTF_8),
//                        String.valueOf(toTimestamp).getBytes(StandardCharsets.UTF_8)
//                )
//        );
//    }
//
//    public Object getRangeWithAggregation(
//            String key,
//            long fromTimestamp,
//            long toTimestamp,
//            String aggregationType, // e.g., "avg", "sum", "min", "max", "count"
//            long timeBucketMillis // e.g., 60000
//    ) {
//        return redisTemplate.execute((RedisConnection connection) ->
//                connection.execute("TS.RANGE",
//                        key.getBytes(StandardCharsets.UTF_8),
//                        String.valueOf(fromTimestamp).getBytes(StandardCharsets.UTF_8),
//                        String.valueOf(toTimestamp).getBytes(StandardCharsets.UTF_8),
//                        "AGGREGATION".getBytes(StandardCharsets.UTF_8),
//                        aggregationType.getBytes(StandardCharsets.UTF_8),
//                        String.valueOf(timeBucketMillis).getBytes(StandardCharsets.UTF_8)
//                )
//        );
//    }

}
