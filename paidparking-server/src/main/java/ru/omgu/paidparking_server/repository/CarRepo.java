package ru.omgu.paidparking_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.omgu.paidparking_server.entity.CarEntity;

import java.util.Optional;

public interface CarRepo extends JpaRepository<CarEntity, Long> {
    Optional<CarEntity> findByCarNumber(String number);
    @Query("SELECT c FROM CarEntity c JOIN c.users u WHERE c.carNumber = :carNumber AND u.id = :userId")
    Optional<CarEntity> findByCarNumberAndUserId(@Param("carNumber") String carNumber,
                                                 @Param("userId") Long userId);

    Optional<CarEntity> findByCarIdAndUserId(Long carId, Long userId);
}
