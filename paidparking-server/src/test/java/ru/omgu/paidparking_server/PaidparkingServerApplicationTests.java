package ru.omgu.paidparking_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.RegisterRequestDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;
@SpringBootTest
class PaidparkingServerApplicationTests {
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
