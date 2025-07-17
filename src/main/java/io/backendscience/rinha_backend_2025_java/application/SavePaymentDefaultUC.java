package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.RedisTimeSeriesService;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SavePaymentDefaultUC {

    private final PaymentProcessorGateway paymentPort;
    private final Logger logger = Logger.getLogger(SavePaymentDefaultUC.class.getName());
    private final JedisPooled jedisPooled;
    private final RedisTimeSeriesService redisService;
//    private final UnifiedJedis unifiedJedis;

    public void execute(PaymentDetail paymentDetail) {

        PaymentType paymentType = paymentPort.savePaymentDefault(paymentDetail);

//        String command = String.format(
//                "redis.call('TS.ADD', '%s', %d, %d, 'ON_DUPLICATE', 'SUM')",
//                "payments:type:" + paymentType.getValue() + ":count", System.currentTimeMillis(), 1
//        );
//
//        redissonClient.getScript(StringCodec.INSTANCE).eval(
//                RScript.Mode.READ_WRITE,
//                command,
//                RScript.ReturnType.VALUE,
//                List.of()
//        );


//        RTimeSeries<Long, String> ts = redissonClient.getTimeSeries("payments:type:" + paymentType.getValue() + ":count");
//        ts.add(System.currentTimeMillis(), 1L);
//        ts.add(1752792472503L, 1L);
//        ts.add(1752792472503L, 1L);

        //unifiedJedis.tsAdd("payments:type:" + paymentType.getValue() + ":count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);
//        redisService.addDataPoint("payments:type:" + paymentType.getValue() + ":count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);
        jedisPooled.tsAdd("payments:type:" + paymentType.getValue() + ":count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);
        //jedisPooled.tsAdd("payments:type:0:count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);

        logger.info("Finish saving PAYMENT_DETAIL: " + paymentDetail);
    }
}
