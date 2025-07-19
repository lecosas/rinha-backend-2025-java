package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class GetPaymentSummaryUC {

    private final Logger logger = Logger.getLogger(GetPaymentSummaryUC.class.getName());
//    private final RedisTemplate redisTemplate;
    private final RedisCommands<String, String> redis;

    public Map<Integer, PaymentSummaryTotal> execute(OffsetDateTime from, OffsetDateTime to) {
        Map<Integer, PaymentSummaryTotal> map = new HashMap<>();

        long countDefault = 0;
        long countFallback = 0;

        //        List<TSElement> type0 = jedisPooled.tsRange("payments:type:0:count", rangeParams);
        //        if (type0 != null && !type0.isEmpty()) {
        //            countDefault = (long) type0.getFirst().getValue();
        //        }
        //
        //        List<TSElement> type1 = jedisPooled.tsRange("payments:type:1:count", rangeParams);
        //        if (type1 != null && !type1.isEmpty()) {
        //            countFallback = (long) type1.getFirst().getValue();
        //        }

        countDefault = redis.zcount(
                "payments:type:0:count",
                from != null ? from.toInstant().toEpochMilli() : 0L,
                to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE);

        countFallback = redis.zcount(
                "payments:type:1:count",
                from != null ? from.toInstant().toEpochMilli() : 0L,
                to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE);

//        countDefault  = redisTemplate
//                .opsForZSet()
//                .count(
//                        "payments:type:0:count",
//                        from != null ? from.toInstant().toEpochMilli() : 0L,
//                        to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE);
//
//        countFallback  = redisTemplate
//                .opsForZSet()
//                .count(
//                        "payments:type:1:count",
//                        from != null ? from.toInstant().toEpochMilli() : 0L,
//                        to != null ? to.toInstant().toEpochMilli() : Long.MAX_VALUE);

        map.put(
                0,
                new PaymentSummaryTotal(
                        countDefault, BigDecimal.valueOf(countDefault).multiply(BigDecimal.valueOf(19.9))));
        map.put(
                1,
                new PaymentSummaryTotal(
                        countFallback, BigDecimal.valueOf(countFallback).multiply(BigDecimal.valueOf(19.9))));

        return map;
    }
}
