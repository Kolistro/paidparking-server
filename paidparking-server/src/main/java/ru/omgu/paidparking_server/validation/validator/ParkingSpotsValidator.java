package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.entity.BuildingEntity;
import ru.omgu.paidparking_server.validation.annotation.ValidParkingSpots;

public class ParkingSpotsValidator implements ConstraintValidator<ValidParkingSpots, BuildingEntity> {

    @Override
    public boolean isValid(BuildingEntity building, ConstraintValidatorContext context) {
        if (building == null) return true;

        Long total = building.getTotalParkingSpots();
        Long available = building.getAvailableParkingSpots();

        if (total == null || available == null) return true;

        return total >= 0 && available >= 0 && available <= total;
    }
}