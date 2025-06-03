package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.omgu.paidparking_server.dto.request.AuthRequestDto;
import ru.omgu.paidparking_server.dto.request.RegisterRequestDto;
import ru.omgu.paidparking_server.dto.response.AuthResponseDto;
import ru.omgu.paidparking_server.entity.CarEntity;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.exception.UserAlreadyExistsException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.repository.CarRepo;
import ru.omgu.paidparking_server.repository.RoleRepo;
import ru.omgu.paidparking_server.repository.UserRepo;
import ru.omgu.paidparking_server.security.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Отключаем строгую проверку
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private CarRepo carRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;


    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldRegisterUser_WhenUserDoesNotExist() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto(
                "John", "Doe", "+123456789", "ABC123", "password"
        );
        CarEntity car = new CarEntity();
        car.setCarNumber("ABC123");

        RoleEntity userRole = new RoleEntity();
        userRole.setId(1L);
        userRole.setRole(Role.ROLE_USER);

        when(userRepo.existsByPhoneNumber("+123456789")).thenReturn(false);
        when(carRepo.findByCarNumber("ABC123")).thenReturn(Optional.empty());
        when(roleRepo.findByRole(Role.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        // Act
        AuthResponseDto response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        verify(userRepo, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto(
                "John", "Doe", "+123456789", "ABC123", "password"
        );

        when(userRepo.existsByPhoneNumber("+123456789")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );
        assertEquals("Пользователь с таким номером уже зарегистрирован", exception.getMessage());
    }

    @Test
    void authenticate_ShouldAuthenticateUser_WhenCredentialsAreValid() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("+123456789", "password");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setPhoneNumber("+123456789");
        user.setPassword("encodedPassword");

        when(userRepo.findByPhoneNumber("+123456789")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Мокируем успешную аутентификацию
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new TestingAuthenticationToken(user, null, "ROLE_USER"));

        // Act
        AuthResponseDto response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("+123456789", "password");

        when(userRepo.findByPhoneNumber("+123456789")).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.authenticate(request)
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void authenticate_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("+123456789", "wrong-password");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setPhoneNumber("+123456789");
        user.setPassword("encodedPassword");

        when(userRepo.findByPhoneNumber("+123456789")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encodedPassword")).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(request)
        );
        assertEquals("Неверный пароль", exception.getMessage());
    }
}