package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.dto.response.CarResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.service.CarService;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;

import java.util.Set;

@RestController
@RequestMapping("/users/{userId}/cars")
@RequiredArgsConstructor
@Validated
public class CarController {

    private final CarService carService;

    @PreAuthorize("#userId == principal.id")
    @PostMapping
    public ResponseEntity<CommonResponse<CarResponseDto>> addCar(
            @RequestBody @Valid CarRequestDto car,
            @PathVariable Long userId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.addCar(car, userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id")
    @PutMapping("/{carId}")
    public ResponseEntity<CommonResponse<CarResponseDto>> editCar(
            @RequestBody @Valid CarRequestDto car,
            @PathVariable Long userId,
            @PathVariable Long carId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.editCar(car, userId, carId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<CarResponseDto>> getCarByNumber(
            @ValidCarNumber @RequestParam String carNumber) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.getCarByNumber(carNumber), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<Set<CarResponseDto>>> getListCarByUserId(
            @PathVariable Long userId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<Set<CarResponseDto>> commonResponse =
                new CommonResponse<>(carService.getListCarByUserId(userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @DeleteMapping("/{carId}")
    public ResponseEntity<CommonResponse<Long>> deleteCar(
            @PathVariable Long userId,
            @PathVariable Long carId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(carService.deleteByUserId(userId, carId), status.value());

        return ResponseEntity.ok(commonResponse);
    }
}

