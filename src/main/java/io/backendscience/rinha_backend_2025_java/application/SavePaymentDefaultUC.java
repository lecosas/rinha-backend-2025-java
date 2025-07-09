package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentProcessorGateway;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.PaymentEntity;
import io.backendscience.rinha_backend_2025_java.domain.PaymentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class SavePaymentDefaultUC {

    private final PaymentProcessorGateway paymentPort;
    private final PaymentRepository paymentRepository;

    public void execute(PaymentDetail paymentDetail) {

        paymentPort.savePaymentDefault(paymentDetail);

        PaymentEntity entity = new PaymentEntity();
        entity.setCorrelationId(paymentDetail.correlationId());
        entity.setAmount(paymentDetail.amount());
        entity.setRequestedAt(paymentDetail.requestedAt());
        entity.setType((int) (Math.random() * 2));

        paymentRepository.save(entity);

    }
}
