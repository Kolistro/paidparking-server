package ru.omgu.paidparking_server.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.UserRequestDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.dto.response.UserResponseDto;
import ru.omgu.paidparking_server.security.CustomUserDetails;
import ru.omgu.paidparking_server.service.UserService;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<UserResponseDto>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        CommonResponse<UserResponseDto> response =
                new CommonResponse<>(userService.getUserById(userId), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<UserResponseDto>> editCurrentUser(
            @Valid @RequestBody UserRequestDto userDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        CommonResponse<UserResponseDto> response =
                new CommonResponse<>(userService.editUser(userDto, userId), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<CommonResponse<UserResponseDto>> editUser(@Valid @RequestBody UserRequestDto userDto, @PathVariable Long id) {
        CommonResponse<UserResponseDto> response =
                new CommonResponse<>(userService.editUser(userDto, id), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        CommonResponse<UserResponseDto> response =
                new CommonResponse<>(userService.getUserById(id), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUserByPhoneNumber(@ValidPhoneNumber @RequestParam String phoneNumber) {
        CommonResponse<UserResponseDto> response =
                new CommonResponse<>(userService.getUserByPhoneNumber(phoneNumber), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<List<UserResponseDto>>> getAllUsers() {
        CommonResponse<List<UserResponseDto>> response =
                new CommonResponse<>(userService.getAllUsers(), HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<CommonResponse<Long>> deleteUser(@PathVariable Long id) {
        Long deletedId = userService.deleteUser(id);
        CommonResponse<Long> response = new CommonResponse<>(deletedId, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}

