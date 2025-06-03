package ru.omgu.paidparking_server.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.security.CustomUserDetails;
import ru.omgu.paidparking_server.security.JwtService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Загружаем Spring контекст
@TestPropertySource(locations = "classpath:application.properties") // Загружаем application.properties
class JwtServiceTest {

    @Value("${jwt.secret}") // Загружаем значение jwt.secret
    private String secretKey;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Устанавливаем секретный ключ через ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", secretKey);
    }


    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setPhoneNumber("+123456789");

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        String extractedPhone = jwtService.extractPhoneNumber(token);
        assertEquals("+123456789", extractedPhone);
    }

    @Test
    void extractPhoneNumber_ShouldExtractPhoneNumberFromToken() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setPhoneNumber("+723456789");

        String token = jwtService.generateToken(user);

        // Act
        String extractedPhone = jwtService.extractPhoneNumber(token);

        // Assert
        assertEquals("+723456789", extractedPhone);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setPhoneNumber("+723456789");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(user);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() throws Exception {
        // Arrange
        UserEntity user = new UserEntity();
        user.setPhoneNumber("+723456789");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Создаем просроченный токен
        String token = Jwts.builder()
                .setSubject(user.getPhoneNumber())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // Токен выдан вчера
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // Токен просрочен
                .signWith(SignatureAlgorithm.HS256, secretKey) // Используем реальный ключ
                .compact();

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid); // Токен должен быть недействительным
    }

}