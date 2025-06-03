package ru.omgu.paidparking_server;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.omgu.paidparking_server.dto.request.RegisterRequestDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
@SpringBootTest
class PaidparkingServerApplicationTests {
	private jakarta.validation.Validation Validation;
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void testInvalidPhoneNumber() {
		RegisterRequestDto request = new RegisterRequestDto(
				"John",
				"Doe",
				"8913666778", // Неверный номер телефона
				"A123BC777",
				"password123"
		);

		var violations = validator.validate(request);
		assertFalse(violations.isEmpty());
	}




	@Test
	void contextLoads() {
	}

}
