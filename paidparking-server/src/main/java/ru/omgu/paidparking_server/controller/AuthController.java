package ru.omgu.paidparking_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.AuthRequestDto;
import ru.omgu.paidparking_server.dto.request.RegisterRequestDto;
import ru.omgu.paidparking_server.dto.response.AuthResponseDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        CommonResponse<AuthResponseDto> commonResponse =
                new CommonResponse<>(authService.register(request), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthResponseDto>> authenticate(@Valid @RequestBody AuthRequestDto request) {
        CommonResponse<AuthResponseDto> commonResponse =
                new CommonResponse<>(authService.authenticate(request), HttpStatus.OK.value());
        return ResponseEntity.ok(commonResponse);
    }
}
