package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.CarEntity;

import java.util.Optional;

public interface CarRepo extends JpaRepository<CarEntity, Long> {
    Optional<CarEntity> findByCarNumber(String number);
}
