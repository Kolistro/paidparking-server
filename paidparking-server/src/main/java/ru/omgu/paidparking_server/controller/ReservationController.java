package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.ReservationRequestDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.dto.response.ReservationResponseDto;
import ru.omgu.paidparking_server.service.ReservationService;

import java.util.List;
@RestController
@RequestMapping("/users/{userId}/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("#userId == principal.id")
    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponseDto>> addReservation(
            @Valid @RequestBody ReservationRequestDto reservation,
            @PathVariable Long userId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<ReservationResponseDto> commonResponse =
                new CommonResponse<>(reservationService.addReservation(reservation, userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id")
    @PatchMapping("/{reservationId}/status/active")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> updateStatusToActive(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<ReservationResponseDto> commonResponse =
                new CommonResponse<>(reservationService.updateStatusToActive(reservationId, userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id")
    @PatchMapping("/{reservationId}/status/complete")
    public ResponseEntity<CommonResponse<ReservationResponseDto>> completeReservation(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<ReservationResponseDto> commonResponse =
                new CommonResponse<>(reservationService.completeReservation(reservationId, userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<ReservationResponseDto>>> getListReservationsByUserId(
            @PathVariable Long userId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<ReservationResponseDto>> commonResponse =
                new CommonResponse<>(reservationService.getListReservationsByUserId(userId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/by-car")
    public ResponseEntity<CommonResponse<List<ReservationResponseDto>>> getListReservationsByCarId(
            @RequestParam Long carId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<ReservationResponseDto>> commonResponse =
                new CommonResponse<>(reservationService.getListReservationsByCarId(carId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/by-building")
    public ResponseEntity<CommonResponse<List<ReservationResponseDto>>> getListReservationsByBuildingId(
            @RequestParam Long buildingId) {

        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<ReservationResponseDto>> commonResponse =
                new CommonResponse<>(reservationService.getListReservationsByBuildingId(buildingId), status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteAllByUserId(
            @PathVariable Long userId) {

        reservationService.deleteAllByUserId(userId);

        HttpStatus status = HttpStatus.OK;
        CommonResponse<Void> commonResponse = new CommonResponse<>(status.value());

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {

        reservationService.delete(reservationId, userId);

        HttpStatus status = HttpStatus.OK;
        CommonResponse<Void> commonResponse = new CommonResponse<>(status.value());

        return ResponseEntity.ok(commonResponse);
    }
}
