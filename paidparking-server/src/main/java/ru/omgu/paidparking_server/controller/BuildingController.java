package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.service.BuildingService;

import java.util.List;

@RestController("/building")
@RequiredArgsConstructor
public class BuildingController {
    private final BuildingService buildingService;

    @PostMapping
    public ResponseEntity<CommonResponse<BuildingResponseDto>> addBuilding(@Valid @RequestBody BuildingRequestDto building){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.addBuilding(building), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<BuildingResponseDto>> editBuilding(@Valid @RequestBody BuildingRequestDto building){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.editBuilding(building), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/buildings")
    public ResponseEntity<CommonResponse<List<BuildingResponseDto>>> getListBuildings(){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<BuildingResponseDto>> commonResponse =
                new CommonResponse<>(buildingService.getListBuildings(), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<BuildingResponseDto>> getBuildingByLocationName(
            @Size(max = 100, message = "Название объекта не должно превышать 100 символов.")
            @RequestParam String locationName){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.getBuildingByLocationName(locationName), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<CommonResponse<Boolean>> isAvailableParkingSpots(@PathVariable Long id){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Boolean> commonResponse =
                new CommonResponse<>(buildingService.isAvailableParkingSpots(id), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Long>> getCountAvailableParkingSpots(@PathVariable Long id){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(buildingService.getCountAvailableParkingSpots(id), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Long>> delete(@PathVariable Long id){
        buildingService.delete(id);
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(status.value());
        return ResponseEntity.ok(commonResponse);
    }
}
