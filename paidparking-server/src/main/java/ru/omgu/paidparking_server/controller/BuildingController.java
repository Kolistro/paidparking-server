package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.service.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
@Validated
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<BuildingResponseDto>> addBuilding(@Valid @RequestBody BuildingRequestDto building) {
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.addBuilding(building), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<BuildingResponseDto>> editBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingRequestDto building) {
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.editBuilding(building), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<BuildingResponseDto>>> getListBuildings() {
        CommonResponse<List<BuildingResponseDto>> commonResponse =
                new CommonResponse<>(buildingService.getListBuildings(), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<BuildingResponseDto>> getBuildingByLocationName(
            @Size(max = 100, message = "Название объекта не должно превышать 100 символов.")
            @RequestParam String locationName) {
        CommonResponse<BuildingResponseDto> commonResponse =
                new CommonResponse<>(buildingService.getBuildingByLocationName(locationName), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<CommonResponse<Boolean>> isAvailableParkingSpots(@PathVariable Long id) {
        CommonResponse<Boolean> commonResponse =
                new CommonResponse<>(buildingService.isAvailableParkingSpots(id), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/{id}/available-spots")
    public ResponseEntity<CommonResponse<Long>> getCountAvailableParkingSpots(@PathVariable Long id) {
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(buildingService.getCountAvailableParkingSpots(id), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Long>> delete(@PathVariable Long id) {
        buildingService.delete(id);
        CommonResponse<Long> commonResponse = new CommonResponse<>(HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }
}

