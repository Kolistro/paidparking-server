package ru.omgu.paidparking_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<CommonResponse<RoleResponseDto>> addRole(@RequestParam Role role){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<RoleResponseDto> commonResponse =
                new CommonResponse<>(roleService.addRole(role), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<CommonResponse<List<RoleResponseDto>>> getAllRole(){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<RoleResponseDto>> commonResponse =
                new CommonResponse<>(roleService.getAllRole(), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @DeleteMapping()
    public ResponseEntity<CommonResponse<Long>> deleteRole(@RequestParam Role role){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(roleService.delete(role), status.value());
        return ResponseEntity.ok(commonResponse);
    }

}
