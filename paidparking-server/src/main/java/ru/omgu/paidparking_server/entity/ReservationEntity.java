package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.omgu.paidparking_server.enums.ReservationStatus;
import ru.omgu.paidparking_server.validation.annotation.ValidReservationTime;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ValidReservationTime
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private BuildingEntity building;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private PaymentEntity payment;
}
