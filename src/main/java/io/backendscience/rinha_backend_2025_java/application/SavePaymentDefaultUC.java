package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
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

    public void execute(PaymentDetail paymentDetail) {

        PaymentType paymentType = paymentPort.savePaymentDefault(paymentDetail);

        jedisPooled.tsAdd("payments:type:" + paymentType.getValue() + ":count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);

        logger.info("Finish saving PAYMENT_DETAIL: " + paymentDetail);
    }
}
