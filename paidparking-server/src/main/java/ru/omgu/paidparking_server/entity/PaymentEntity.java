package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.omgu.paidparking_server.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @Positive
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime receiptUploadedAt;
    private LocalDateTime confirmedAt;

    // Информация о чеке
    @Lob
    @Column(name = "receipt_file")
    private byte[] receiptFile;
    private String receiptFileName;
    private String receiptContentType;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;
}
