package io.backendscience.rinha_backend_2025_java.adapter.outbound;

import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.PaymentEntity;
import io.backendscience.rinha_backend_2025_java.adapter.outbound.resources.SummaryPaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    @Query(
            value =
                    """
                        SELECT p.type, SUM(p.amount), COUNT(p.amount)
                        FROM payments p
                        WHERE (p.requested_at >= :from OR :from IS NULL) AND
                        (p.requested_at <= :to OR :to IS NULL)
                        GROUP BY p.type ORDER BY p.type
                    """,
            nativeQuery = true)
    List<SummaryPaymentResponse> sumAmountBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}
