package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.PaymentEntity;

public interface PaymentRepo extends JpaRepository<PaymentEntity, Long> {
}
