package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.enums.Role;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Role role);

    boolean existsByRole(Role role);
}
