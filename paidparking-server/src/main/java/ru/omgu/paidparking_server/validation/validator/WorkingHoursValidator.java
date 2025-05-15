package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.entity.BuildingEntity;
import ru.omgu.paidparking_server.validation.annotation.ValidWorkingHours;

public class WorkingHoursValidator implements ConstraintValidator<ValidWorkingHours, BuildingEntity> {

    @Override
    public boolean isValid(BuildingEntity building, ConstraintValidatorContext context) {
        if (building.getWorkingHoursStart() == null || building.getWorkingHoursEnd() == null) {
            return true; // Для @NotNull можно добавить отдельные аннотации
        }

        return building.getWorkingHoursEnd().isAfter(building.getWorkingHoursStart());
    }
}