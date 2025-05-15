package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.ReservationEntity;
import ru.omgu.paidparking_server.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
    List<ReservationEntity> findByStatusAndCreatedAtBefore(ReservationStatus reservationStatus, LocalDateTime localDateTime);
}
