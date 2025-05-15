package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.dto.response.CarResponseDto;
import ru.omgu.paidparking_server.entity.CarEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.exception.CarNotFoundException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.mapper.CarMapper;
import ru.omgu.paidparking_server.repository.CarRepo;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepo carRepo;
    private final UserRepo userRepo;
    private final CarMapper carMapper;

    public CarResponseDto addCar(CarRequestDto car, Long userId){
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));

        CarEntity carEntity = carRepo.findByCarNumber(car.carNumber())
                .orElseGet(() -> carMapper.toEntity(car));
        Set<UserEntity> users = carEntity.getUsers();
        users.add(user);
        carEntity.setUsers(users);
        carRepo.save(carEntity);
        return  carMapper.toDto(carEntity);
    }

    public CarResponseDto editCar(CarRequestDto car, Long userId){
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        CarEntity carEntity = carRepo.findByCarNumber(car.carNumber())
                .orElseThrow(() -> new CarNotFoundException("Автомобиль c номером " + car.carNumber() + " не существует."));

        carEntity.setBrand(car.brand());
        carEntity.setModel(car.model());
        carEntity.setColor(car.color());
        carRepo.save(carEntity);
        return carMapper.toDto(carEntity);
    }

    public CarResponseDto getCarByNumber(String carNumber){
        CarEntity carEntity = carRepo.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException("Автомобиль c номером " + carNumber + " не существует."));
        return carMapper.toDto(carEntity);
    }

    public Set<CarResponseDto> getListCarByUserId(Long userId){
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        Set<CarEntity> cars = user.getCars();
        return carMapper.toDto(cars);
    }

    public Long deleteByUserId(String carNumber, Long userId){
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        CarEntity carEntity = carRepo.findByCarNumberAndUserId(carNumber, userId)
                .orElseThrow(() -> new CarNotFoundException("Автомобиль c номером " + carNumber +
                        " и userId = " + userId + " не существует."));
        Set<UserEntity> users = carEntity.getUsers();
        users.remove(user);
        if(users.isEmpty()){
            carRepo.delete(carEntity);
        }else {
            carEntity.setUsers(users);
            carRepo.save(carEntity);
        }
        return carEntity.getId();
    }
}
