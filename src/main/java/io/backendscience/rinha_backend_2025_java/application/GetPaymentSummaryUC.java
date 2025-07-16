package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.SummaryPaymentResponse;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.timeseries.AggregationType;
import redis.clients.jedis.timeseries.TSElement;
import redis.clients.jedis.timeseries.TSRangeParams;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class GetPaymentSummaryUC {

    private final Logger logger = Logger.getLogger(GetPaymentSummaryUC.class.getName());
    private final JedisPooled jedisPooled;

    public Map<Integer, PaymentSummaryTotal> execute(String from, String to) {
        Map<Integer, PaymentSummaryTotal> map = new HashMap<>();
        List<SummaryPaymentResponse> res;

        OffsetDateTime fromOffset;
        OffsetDateTime toOffset;

        if (from != null && !from.isBlank()) {
            from = from.endsWith("Z") ? from : from + "Z";
            fromOffset = OffsetDateTime.parse(from);
        } else {
            fromOffset = null;
        }

        if (to != null && !to.isBlank()) {
            to = to.endsWith("Z") ? to : to + "Z";
            toOffset = OffsetDateTime.parse(to);
        } else {
            toOffset = null;
        }

        TSRangeParams rangeParams = TSRangeParams
                .rangeParams(fromOffset != null ? fromOffset.toInstant().toEpochMilli() : 0L, toOffset != null ? toOffset.toInstant().toEpochMilli() : Long.MAX_VALUE)
                .aggregation(AggregationType.SUM, 999999999);

        long countDefault = 0;
        long countFallback = 0;

//        RedisCommands<String, String> commands = connection.sync();
//
//        String value = commands.getrange("payments:type:0:count",);

        List<TSElement> type0 = jedisPooled.tsRange("payments:type:0:count", rangeParams);
        if (type0 != null && !type0.isEmpty()) {
            countDefault = (long) type0.getFirst().getValue();
        }

        List<TSElement> type1 = jedisPooled.tsRange("payments:type:1:count", rangeParams);
        if (type1 != null && !type1.isEmpty()) {
            countFallback = (long) type1.getFirst().getValue();
        }

        map.put(0, new PaymentSummaryTotal(countDefault, BigDecimal.valueOf(countDefault).multiply(BigDecimal.valueOf(19.9))));
        map.put(1, new PaymentSummaryTotal(countFallback, BigDecimal.valueOf(countFallback).multiply(BigDecimal.valueOf(19.9))));

        return map;
    }
}
