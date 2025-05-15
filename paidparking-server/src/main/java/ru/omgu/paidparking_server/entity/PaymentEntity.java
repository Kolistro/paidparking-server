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
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    // Сумма платежа (в копейках, если целое число — лучше для расчётов)
    @Column(nullable = false)
    @Positive
    private Long amount;

    // Ссылка на бронирование
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    // Статус платежа (создан, чек прикреплён, подтверждён и т.п.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.CREATED;

    // Дата создания платежа
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Дата прикрепления чека
    private LocalDateTime receiptUploadedAt;

    // Дата подтверждения вручную оператором
    private LocalDateTime confirmedAt;

    // Информация о чеке
    @Lob
    @Column(name = "receipt_file")
    private byte[] receiptFile;

    private String receiptFileName;

    private String receiptContentType;
}
