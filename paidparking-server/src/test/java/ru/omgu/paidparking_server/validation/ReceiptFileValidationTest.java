package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.dto.request.PaymentRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptFileValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenFileIsValid() {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
                "receiptFile",
                "example.pdf",
                "application/pdf",
                new byte[1024 * 1024]
        );
        PaymentRequestDto dto = new PaymentRequestDto(validFile);

        // Act
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного файла.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenFileHasInvalidContentType() {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
                "receiptFile",
                "example.txt",
                "text/plain",
                new byte[1024 * 1024]
        );
        PaymentRequestDto dto = new PaymentRequestDto(invalidFile);

        // Act
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для файла с недопустимым типом контента.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимый файл чека. Разрешены только JPG, PNG и PDF, размер до 5 МБ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenFileIsTooLarge() {
        // Arrange
        MockMultipartFile largeFile = new MockMultipartFile(
                "receiptFile",
                "example.pdf",
                "application/pdf",
                new byte[6 * 1024 * 1024]
        );
        PaymentRequestDto dto = new PaymentRequestDto(largeFile);

        // Act
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для слишком большого файла.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимый файл чека. Разрешены только JPG, PNG и PDF, размер до 5 МБ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenFileIsEmpty() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "receiptFile",
                "example.pdf",
                "application/pdf",
                new byte[0]
        );
        PaymentRequestDto dto = new PaymentRequestDto(emptyFile);

        // Act
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пустого файла.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимый файл чека. Разрешены только JPG, PNG и PDF, размер до 5 МБ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenFileIsNull() {
        // Arrange
        MultipartFile nullFile = null;
        PaymentRequestDto dto = new PaymentRequestDto(nullFile);

        // Act
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для null-значения.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимый файл чека. Разрешены только JPG, PNG и PDF, размер до 5 МБ.", violationMessage);
    }
}