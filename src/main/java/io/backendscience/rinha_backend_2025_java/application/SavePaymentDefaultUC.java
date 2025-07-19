package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SavePaymentDefaultUC {

    private final PaymentProcessorGateway paymentPort;
    private final Logger logger = Logger.getLogger(SavePaymentDefaultUC.class.getName());
//    private final RedisTemplate redisTemplate;
    private final RedisCommands<String, String> redis;

    public void execute(PaymentDetail paymentDetail) {

        PaymentType paymentType = paymentPort.savePaymentDefault(paymentDetail);

        redis.zadd("payments:type:" + paymentType.getValue() + ":count",
                paymentDetail.requestedAt().toInstant().toEpochMilli(),
                UUID.randomUUID().toString());

//        redisTemplate
//                .opsForZSet()
//                .add(
//                        "payments:type:" + paymentType.getValue() + ":count",
//                        UUID.randomUUID().toString(),
//                        paymentDetail.requestedAt().toInstant().toEpochMilli());

        logger.info("Finish saving PAYMENT_DETAIL: " + paymentDetail);
    }
}
