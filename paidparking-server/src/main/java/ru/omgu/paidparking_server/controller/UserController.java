package ru.omgu.paidparking_server.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.omgu.paidparking_server.dto.request.UserRequestDto;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.dto.response.UserResponseDto;
import ru.omgu.paidparking_server.service.UserService;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponseDto>> editUser(@Valid @RequestBody UserRequestDto user, @PathVariable Long id){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<UserResponseDto> commonResponse =
                new CommonResponse<>(userService.editUser(user, id), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<UserResponseDto>> getUserByPhoneNumber(@ValidPhoneNumber @RequestParam String phoneNumber){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<UserResponseDto> commonResponse =
                new CommonResponse<>(userService.getUserByPhoneNumber(phoneNumber), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUserById(@PathVariable Long id){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<UserResponseDto> commonResponse =
                new CommonResponse<>(userService.getUserById(id), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("/users")
    public ResponseEntity<CommonResponse<List<UserResponseDto>>> getAllUsers(){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<List<UserResponseDto>> commonResponse =
                new CommonResponse<>(userService.getAllUsers(), status.value());
        return ResponseEntity.ok(commonResponse);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Long>> deleteUser(@PathVariable Long id){
        HttpStatus status = HttpStatus.OK;
        CommonResponse<Long> commonResponse =
                new CommonResponse<>(userService.deleteUser(id), status.value());
        return ResponseEntity.ok(commonResponse);
    }


}
