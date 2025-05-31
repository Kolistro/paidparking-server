package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.request.PaymentRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.entity.PaymentEntity;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("user/{userId}/reservation/{reservationId}/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> addPaymentForReservation(@PathVariable Long reservationId){
        HttpStatus status = HttpStatus.OK;
        paymentService.addPaymentForReservation(reservationId);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> uploadPayment(@PathVariable Long reservationId, @Valid @RequestBody PaymentRequestDto dto){
        HttpStatus status = HttpStatus.OK;
        paymentService.uploadPayment(reservationId, dto);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> completedPayment(@PathVariable Long paymentId){
        HttpStatus status = HttpStatus.OK;
        paymentService.completedPayment(paymentId);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> failedPayment(@PathVariable Long paymentId){
        HttpStatus status = HttpStatus.OK;
        paymentService.failedPayment(paymentId);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(@PathVariable Long paymentId){
        HttpStatus status = HttpStatus.OK;
        paymentService.cancelPayment(paymentId);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<PaymentEntity>>> getPaymentsByStatus(@RequestParam PaymentStatus paymentStatus){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<PaymentEntity>> commonResponse =
                new CommonResponse<>(paymentService.getPaymentsByStatus(paymentStatus), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> deletePayment(@PathVariable Long paymentId){
        HttpStatus status = HttpStatus.OK;
        paymentService.deletePayment(paymentId);
        CommonResponse<Void> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }


}
