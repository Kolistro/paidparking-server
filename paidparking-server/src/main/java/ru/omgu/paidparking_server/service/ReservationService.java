package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.omgu.paidparking_server.dto.request.ReservationRequestDto;
import ru.omgu.paidparking_server.dto.response.ReservationResponseDto;
import ru.omgu.paidparking_server.entity.*;
import ru.omgu.paidparking_server.enums.ReservationStatus;
import ru.omgu.paidparking_server.exception.*;
import ru.omgu.paidparking_server.mapper.ReservationMapper;
import ru.omgu.paidparking_server.repository.BuildingRepo;
import ru.omgu.paidparking_server.repository.CarRepo;
import ru.omgu.paidparking_server.repository.ReservationRepo;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepo reservationRepo;
    private final UserRepo userRepo;
    private final CarRepo carRepo;
    private final BuildingRepo buildingRepo;
    private final ReservationMapper reservationMapper;
    private final ReservationHistoryService historyService;
    private final PaymentService paymentService;

    public ReservationResponseDto addReservation(ReservationRequestDto reservation, Long userId){
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        CarEntity car = carRepo.findByCarNumber(reservation.carNumber())
                .orElseThrow(() -> new CarNotFoundException("Автомобиля c номером "
                        + reservation.carNumber() + " не существует."));
        BuildingEntity building = buildingRepo.findById(reservation.buildingId())
                .orElseThrow(() -> new BuildingNotFoundException("Здания c id "
                + reservation.buildingId() + " не существует."));

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setBuilding(building);
        reservationEntity.setUser(user);
        reservationEntity.setCar(car);
        reservationEntity.setStartTime(reservation.startTime());
        reservationEntity.setEndTime(reservation.endTime());
        reservationEntity.setStatus(ReservationStatus.WAITING);

        reservationRepo.save(reservationEntity);
        return reservationMapper.toDto(reservationEntity);
    }

    // метод будет автоматически вызываться через каждые 5 минут
    // и менять статус на EXPIRED, у которых прошло 15 минут с момента создания
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredReservations() {
        List<ReservationEntity> expiredReservations = reservationRepo.findByStatusAndCreatedAtBefore(
                ReservationStatus.WAITING,
                LocalDateTime.now().minusMinutes(15));
        for (ReservationEntity reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepo.save(reservation);

            paymentService.expiredPayment(reservation);
        }
    }

    public ReservationResponseDto updateStatusToActive(Long reservationId) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Бронь не найдена"));

        if (reservation.getStatus() == ReservationStatus.WAITING) {
            reservation.setStatus(ReservationStatus.ACTIVE);  // Статус меняется на ACTIVE
            reservationRepo.save(reservation);
        } else {
            throw new ReservationInvalidStatusException("Можно активировать только бронирования со статусом WAITING");
        }
        return reservationMapper.toDto(reservation);
    }

    @Transactional
    public ReservationResponseDto completeReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Бронь не найдена"));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationInvalidStatusException("Завершить можно только бронь со статусом ACTIVE");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepo.save(reservation);
        historyService.archiveReservation(reservation);

        return reservationMapper.toDto(reservation);
    }

    public List<ReservationResponseDto> getListReservationsByUserId(Long userId){
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        List<ReservationEntity> reservations = reservationRepo.findAllByUserId(userId);
        return reservationMapper.toDto(reservations);
    }

    public List<ReservationResponseDto> getListReservationsByCarId(Long carId){
        carRepo.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Автомобиля c id " + carId + " не существует."));
        List<ReservationEntity> reservations = reservationRepo.findAllByUserId(carId);
        return reservationMapper.toDto(reservations);
    }

    public List<ReservationResponseDto> getListReservationsByBuildingId(Long buildingId){
        buildingRepo.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException("Здания c id " + buildingId + " не существует."));
        List<ReservationEntity> reservations = reservationRepo.findAllByUserId(buildingId);
        return reservationMapper.toDto(reservations);
    }

    public void deleteAllByUserId(Long userId){
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        reservationRepo.deleteAllByUserId(userId);
    }

    public void delete(Long id){
        reservationRepo.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Брони c id = " + id + " не существует."));
        reservationRepo.deleteById(id);
    }
}
