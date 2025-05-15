package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omgu.paidparking_server.entity.UserEntity;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phone);
}
