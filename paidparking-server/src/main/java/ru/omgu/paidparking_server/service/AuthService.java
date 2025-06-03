package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.dto.request.AuthRequestDto;
import ru.omgu.paidparking_server.dto.request.RegisterRequestDto;
import ru.omgu.paidparking_server.dto.response.AuthResponseDto;
import ru.omgu.paidparking_server.entity.CarEntity;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.exception.RoleNotFoundException;
import ru.omgu.paidparking_server.exception.UserAlreadyExistsException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.repository.CarRepo;
import ru.omgu.paidparking_server.repository.RoleRepo;
import ru.omgu.paidparking_server.repository.UserRepo;
import ru.omgu.paidparking_server.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final CarRepo carRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepo.existsByPhoneNumber(request.phoneNumber())) {
            throw new UserAlreadyExistsException("Пользователь с таким номером уже зарегистрирован");
        }

        CarEntity car = carRepo.findByCarNumber(request.carNumber())
                .orElseGet(() -> {
                    CarEntity newCar = new CarEntity();
                    newCar.setCarNumber(request.carNumber());
                    return carRepo.save(newCar);
                });

        RoleEntity userRole = roleRepo.findByRole(Role.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Роль не найдена"));


        UserEntity user = new UserEntity();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getCars().add(car);
        user.getRoles().add(userRole);

        userRepo.save(user);

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponseDto(user.getId(), jwtToken);
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        UserEntity user = userRepo.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.phoneNumber(),
                        request.password())
        );

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(user.getId(), jwtToken);
    }
}
