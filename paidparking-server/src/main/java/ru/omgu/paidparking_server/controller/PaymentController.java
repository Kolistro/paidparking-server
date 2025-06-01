package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.PaymentRequestDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.entity.PaymentEntity;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("users/{userId}/reservations/{reservationId}/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PostMapping
    public ResponseEntity<CommonResponse<Void>> addPaymentForReservation(@PathVariable Long userId,
                                                                         @PathVariable Long reservationId){
        paymentService.addPaymentForReservation(reservationId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> uploadPayment(@PathVariable Long userId,
                                                              @PathVariable Long reservationId,
                                                              @Valid @RequestBody PaymentRequestDto dto){
        paymentService.uploadPayment(reservationId, dto);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{paymentId}/complete")
    public ResponseEntity<CommonResponse<Void>> completedPayment(@PathVariable Long paymentId){
        paymentService.completedPayment(paymentId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{paymentId}/fail")
    public ResponseEntity<CommonResponse<Void>> failedPayment(@PathVariable Long paymentId){
        paymentService.failedPayment(paymentId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN') or @paymentSecurity.isOwner(#paymentId, principal.id)")
    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelPayment(@PathVariable Long paymentId){
        paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }

    // Получение платежей по статусу — доступно только админу
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(params = "paymentStatus")
    public ResponseEntity<CommonResponse<List<PaymentEntity>>> getPaymentsByStatus(@PathVariable Long userId,
                                                                                   @RequestParam PaymentStatus paymentStatus){
        List<PaymentEntity> payments = paymentService.getPaymentsByStatus(paymentStatus);
        return ResponseEntity.ok(new CommonResponse<>(payments, HttpStatus.OK.value()));
    }

    // Получение платежей по резервации — доступно пользователю и администратору
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping
    public ResponseEntity<CommonResponse<PaymentEntity>> getPaymentsByReservation(@PathVariable Long userId,
                                                                                  @PathVariable Long reservationId){
        PaymentEntity payment = paymentService.getPaymentByReservation(reservationId);
        return ResponseEntity.ok(new CommonResponse<>(payment, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<Void>> deletePayment(@PathVariable Long paymentId){
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK.value()));
    }
}

