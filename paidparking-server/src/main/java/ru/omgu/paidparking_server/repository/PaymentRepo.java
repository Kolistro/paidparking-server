package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.PaymentEntity;
import ru.omgu.paidparking_server.entity.ReservationEntity;
import ru.omgu.paidparking_server.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByReservation(ReservationEntity reservation);

    List<PaymentEntity> findAllByStatus(PaymentStatus status);
}
