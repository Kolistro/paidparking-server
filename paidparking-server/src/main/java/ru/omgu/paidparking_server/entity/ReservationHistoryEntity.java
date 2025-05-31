package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReservationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Данные пользователя
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userPhoneNumber;

    // Данные автомобиля
    private Long carId;
    private String carNumber;

    // Данные здания/парковки
    private Long buildingId;
    private String buildingAddress;

    // Интервалы бронирования
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Статус брони
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    // Информация об оплате (снэпшот)
    private Long paymentAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentCreatedAt = LocalDateTime.now();
    private LocalDateTime paymentReceiptUploadedAt;
    private LocalDateTime paymentConfirmedAt;
    @Lob
    @Column(name = "receipt_file")
    private byte[] receiptFile;
    private String receiptFileName;
    private String receiptContentType;

    // Когда была создана запись в истории
    private LocalDateTime archivedAt = LocalDateTime.now();
}
