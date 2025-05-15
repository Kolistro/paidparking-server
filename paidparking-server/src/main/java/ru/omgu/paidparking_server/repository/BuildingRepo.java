package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.BuildingEntity;

import java.util.Optional;

public interface BuildingRepo extends JpaRepository<BuildingEntity, Long> {
    boolean existsByLocationName(String s);

    Optional<BuildingEntity> findByLocationName(String s);
}
