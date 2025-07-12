package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.inbound.PaymentsController;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.PaymentEntity;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import io.backendscience.rinha_backend_2025_java.domain.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SavePaymentDefaultUC {

    private final PaymentProcessorGateway paymentPort;
    private final PaymentRepository paymentRepository;
    private final Logger logger = Logger.getLogger(SavePaymentDefaultUC.class.getName());
    private final JedisPooled jedisPooled;

    public void execute(PaymentDetail paymentDetail) {

        PaymentType paymentType = paymentPort.savePaymentDefault(paymentDetail);

        PaymentEntity entity = new PaymentEntity();
        entity.setCorrelationId(paymentDetail.correlationId());
        entity.setAmount(paymentDetail.amount());
        entity.setRequestedAt(paymentDetail.requestedAt());
        entity.setType(paymentType.getValue());

        paymentRepository.save(entity);

        jedisPooled.tsAdd("payments:type:" + paymentType.getValue() + ":count", paymentDetail.requestedAt().toInstant().toEpochMilli(), 1);
//        try (Jedis jedis = jedisPooled.getResource()) {
//
//        }

        logger.info("Finish saving PAYMENT_DETAIL: " + paymentDetail);
    }
}
