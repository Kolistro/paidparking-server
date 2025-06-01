package ru.omgu.paidparking_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<CommonResponse<RoleResponseDto>> addRole(@RequestParam Role role) {
        CommonResponse<RoleResponseDto> commonResponse =
                new CommonResponse<>(roleService.addRole(role), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<RoleResponseDto>>> getAllRoles() {
        CommonResponse<List<RoleResponseDto>> commonResponse =
                new CommonResponse<>(roleService.getAllRole(), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping("/{role}")
    public ResponseEntity<CommonResponse<Long>> deleteRole(@PathVariable Role role) {
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(roleService.delete(role), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }
}

