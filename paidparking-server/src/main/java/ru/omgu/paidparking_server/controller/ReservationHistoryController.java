package ru.omgu.paidparking_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.entity.ReservationHistoryEntity;
import ru.omgu.paidparking_server.service.ReservationHistoryService;

import java.util.List;

@RestController
@RequestMapping("users/{userId}/histories")
@RequiredArgsConstructor
public class ReservationHistoryController {
    private final ReservationHistoryService historyService;

    @PreAuthorize("hasRole('ADMIN') or @historySecurity.isOwner(#historyId, principal.id)")
    @GetMapping("/{historyId}")
    public ResponseEntity<CommonResponse<ReservationHistoryEntity>> getHistoryById(@PathVariable Long historyId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<ReservationHistoryEntity> commonResponse =
                new CommonResponse<>(historyService.getHistoryById(historyId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    public ResponseEntity<CommonResponse<List<ReservationHistoryEntity>>> getAllHistory(){
//        HttpStatus status = HttpStatus.OK;
//        CommonResponse<List<ReservationHistoryEntity>> commonResponse =
//                new CommonResponse<>(historyService.getAllHistory(), status.value());
//        return ResponseEntity.ok(commonResponse);
//    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping
    public ResponseEntity<CommonResponse<List<ReservationHistoryEntity>>> getAllHistoryByUserId(@PathVariable Long userId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<ReservationHistoryEntity>> commonResponse =
                new CommonResponse<>(historyService.getAllHistoryByUserId(userId), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{historyId}")
    public ResponseEntity<CommonResponse<Long>> delete(@PathVariable Long historyId){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(historyService.delete(historyId), status.value());
        return ResponseEntity.ok(commonResponse);
    }
}
