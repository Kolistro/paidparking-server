package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.validation.annotation.ValidWorkingHours;

public class WorkingHoursValidator implements ConstraintValidator<ValidWorkingHours, BuildingRequestDto> {

    @Override
    public boolean isValid(BuildingRequestDto building, ConstraintValidatorContext context) {
        if (building.workingHoursStart() == null || building.workingHoursEnd() == null) {
            return true; // Для @NotNull можно добавить отдельные аннотации
        }

        return building.workingHoursEnd().isAfter(building.workingHoursStart());
    }
}