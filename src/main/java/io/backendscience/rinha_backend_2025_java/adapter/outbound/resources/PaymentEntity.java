package io.backendscience.rinha_backend_2025_java.adapter.outbound.resources;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
            @Index(name = "idx_payments_type_requested_at", columnList = "type, requestedAt"),
            //@Index(name = "idx_correlation_id", columnList = "correlationId")
        })
@Data
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correlationId;

    private BigDecimal amount;

    private Integer type;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime requestedAt;
//    private OffsetDateTime requestedAt;
}
