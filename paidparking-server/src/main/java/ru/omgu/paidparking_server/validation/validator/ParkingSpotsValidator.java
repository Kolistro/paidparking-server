package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.validation.annotation.ValidParkingSpots;

public class ParkingSpotsValidator implements ConstraintValidator<ValidParkingSpots, BuildingRequestDto> {

    @Override
    public boolean isValid(BuildingRequestDto building, ConstraintValidatorContext context) {
        if (building == null) return true;

        Long total = building.totalParkingSpots();
        Long available = building.availableParkingSpots();

        if (total == null || available == null) return true;

        return total >= 0 && available >= 0 && available <= total;
    }
}