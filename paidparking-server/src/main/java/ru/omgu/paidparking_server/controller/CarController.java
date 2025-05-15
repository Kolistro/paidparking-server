package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.dto.response.CarResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.service.CarService;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;

import java.util.Set;

@RestController
@RequestMapping("user/{userId}/car")
@RequiredArgsConstructor
@Validated
public class CarController {
    private final CarService carService;

    @PostMapping
    public ResponseEntity<CommonResponse<CarResponseDto>> addCar(@RequestBody @Valid CarRequestDto car,
                                                                 @PathVariable Long userId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.addCar(car, userId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<CarResponseDto>> editCar(@RequestBody @Valid CarRequestDto car,
                                                                  @PathVariable Long userId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.editCar(car, userId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<CarResponseDto>> getCarByNumber(@ValidCarNumber @RequestParam String carNumber){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<CarResponseDto> commonResponse =
                new CommonResponse<>(carService.getCarByNumber(carNumber), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/cars")
    public ResponseEntity<CommonResponse<Set<CarResponseDto>>> getListCarByUserId(@PathVariable Long userId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Set<CarResponseDto>> commonResponse =
                new CommonResponse<>(carService.getListCarByUserId(userId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Long>> deleteByUserId(@ValidCarNumber @RequestParam String carNumber,
                                                               @PathVariable Long userId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(carService.deleteByUserId(carNumber, userId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

}
