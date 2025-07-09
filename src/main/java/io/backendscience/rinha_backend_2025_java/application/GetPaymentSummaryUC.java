package io.backendscience.rinha_backend_2025_java.application;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.PaymentRepository;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.SummaryPaymentResponse;
import io.backendscience.rinha_backend_2025_java.domain.PaymentSummaryTotal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GetPaymentSummaryUC {

    private final PaymentRepository paymentRepository;

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

        res =  paymentRepository.sumAmountBetween(fromOffset, toOffset);

        for (SummaryPaymentResponse info : res) {
            Integer type = info.type();
            BigDecimal sum = info.sum();
            Long count = info.count();
            map.put(type, new PaymentSummaryTotal(count, sum));
        }

        return map;
    }
}
