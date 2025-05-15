package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.ReservationHistoryEntity;

import java.util.List;

public interface ReservationHistoryRepo extends JpaRepository<ReservationHistoryEntity, Long> {
    List<ReservationHistoryEntity> findAllByUserId(Long userId);
}
